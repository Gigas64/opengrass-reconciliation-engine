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
*** Predicate for a RegExp type attribute.
*** <p>Intialisation in Adaptor Property file
*** <br>prefix.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.RegExpPredicate
*** <br>prefix.Att  = AttributeName
*** <br>prefix.Criteria = RegularExpression
*** <p>For info on how to define criteria see java class RE
*** <p>e.g. Returns true if Book attribute value starts with TEST_
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.RegExpPredicate
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Att      = Book
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Criteria = ^TEST_.*
*** @date 25th June 2002
*** @version 1.00
*** @see.OpenGrassAttPredicate
***
**/


import org.opengrass.exceptions.*;

import org.apache.regexp.RE;



public class RegExpPredicate implements OpenGrassAttPredicate {
  /** Attribute to apply the predicate to **/
  private String attribute;
  /** Criteria **/
  private String regExpString;
  /** Regular expression created from the criteria **/
  private RE reg_exp;

  /** Initialiser: <br>prefix.Att = att <br>prefix.Criteria = _criteria **/
  public void init(String att, String _criteria) throws OpenGrassException {
    regExpString = _criteria;
    attribute = att;
    try {
      reg_exp = new RE(_criteria);
    }
    catch (Exception e) {
      throw new OpenGrassException("Predicate regular expression [" + _criteria + "] is invalid\n" + e.getMessage());
    }
    System.err.println( "INFO: RegExp Predicate " + attribute + " = " + regExpString);
  }
  /** Returns attribute Name **/
  public String getAttName() {
    return attribute;
  }
  /** Returns true if the attValue matches the predicate **/
  public boolean matches (String attValue) {
    boolean matches = false;
    if (reg_exp.match(attValue)) { matches = true; }
    System.err.println("TRACE: (RegExp) " + attribute + " [" + attValue + "] = [" + regExpString + "] " + matches);
    return matches;
  }
}

