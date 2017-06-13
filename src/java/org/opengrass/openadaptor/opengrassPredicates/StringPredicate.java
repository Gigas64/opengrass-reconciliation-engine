package org.opengrass.openadaptor.opengrassPredicates;

/*
 **
 ** Copyright (C) 2001 - 2006 The Software Conservancy as Trustee. All rights
 ** reserved.
 **
 ** Permission is hereby granted, free of charge, to any person obtaining a
 ** copy of this software and associated documentation files (the
 ** "Software"), to deal in the Software without restriction, including
 ** without limitation the rights to use, copy, modify, merge, publish,
 ** distribute, sublicense, and/or sell copies of the Software, and to
 ** permit persons to whom the Software is furnished to do so, subject to
 ** the following conditions:
 **
 ** The above copyright notice and this permission notice shall be included
 ** in all copies or substantial portions of the Software.
 **
 ** THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 ** OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 ** MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 ** NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 ** LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 ** OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 ** WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **
 ** Nothing in this notice shall be deemed to grant any rights to
 ** trademarks, copyrights, patents, trade secrets or any other intellectual
 ** property of the licensor or any contributor except as expressly stated
 ** herein. No patent license is granted separate from the Software, for
 ** code that you delete from the Software, or for combinations of the
 ** Software with other software or hardware.
 **
 */


/**
*** Predicate for a String type attribute.
*** <p>Intialisation in Adaptor Property file
*** <br>prefix.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.StringPredicate
*** <br>prefix.Att  = AttributeName
*** <br>prefix.Criteria = Value
*** <p>Example criteria
*** <br>prefix.Criteria = [TFG]      #Match TTG
*** <br>prefix.Criteria = [FTG,TFG]  #Match FTG or TFG (note - no space after comma)
*** <br>prefix.Criteria = ![next time]     #Match any value except "next time"
*** <br>prefix.Criteria = ![this year,next year] #Match any value except "this year" or "next year"
*** <p>e.g. Returns true if Location attribute value is not EKR
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.StringPredicate
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Att      = Location
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Criteria = ![EKR]
*** <p>e.g. Returns true if Location attribute value is an empty string
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.StringPredicate
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Att      = Location
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Criteria = NULL
*** <p>e.g. Returns true if Location attribute value is the string "null"
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.StringPredicate
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Att      = Location
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Criteria = [NULL]
*** @date 30th July 2002
*** @version 1.01
*** @see.OpenGrassAttPredicate
***
**/

import java.util.*;


public class StringPredicate implements OpenGrassAttPredicate {
  /** Attribute to apply the predicate to **/
  private String attribute;
  /** If false predicated will match if attribute value is not found in 'strings' **/
  private boolean notflag = false;
  /** Vector of strings to test the attribute value against **/
  private Vector strings;
  /** matching criteria **/
  private String criteria;

  /** Initialiser: <br>prefix.Att = att <br>prefix.Criteria = values **/
  public void init(String att, String values) {
    attribute = att;
    criteria = values;
    strings = new Vector();
    if (values.startsWith("!")) {
      values = values.substring(1);
      notflag = true;
    }
    if (values.equals("NULL")) {
					 strings.add("");
				}
    else if (values.startsWith("[") && values.endsWith("]")) {
      strings = parseStringArray(values);
    }
    else { strings.add(values); }
    System.err.println("INFO: String Predicate " + attribute + " = " + criteria);
  }
  /** Returns attribute Name **/
  public String getAttName() {
   return attribute;
  }
  /** Returns true if the attValue matches the predicate **/
  public boolean matches (String attValue) {
    String s = "No values specified in props file";
    boolean matchStatus;
    if (notflag) {
      matchStatus = true;
      for (int i=0; i<strings.size(); i++) {
        s = (String) strings.get(i);
        if (s.equals(attValue)) {
          matchStatus = false;
          break;
        }
      }
    }
    else {
      matchStatus = false;
      for (int i=0; i<strings.size(); i++) {
        s = (String) strings.get(i);
        if (s.equals(attValue)) {
          matchStatus = true;
          break;
        }
      }
    }
    System.err.println("TRACE: (String) " + attribute + " [" + attValue + "] = " + criteria + " " + matchStatus);
    return matchStatus;
  }
  /** parse array notation in to an array of strings **/
  private Vector parseStringArray(String s) {
    Vector v = new Vector();
    String array_str = s.substring(1, s.length() - 1);
    StringTokenizer tokenizer = new StringTokenizer(array_str, ",");
    while (tokenizer.hasMoreTokens()) {  v.addElement(tokenizer.nextToken());  }
    return v;
  }
}


