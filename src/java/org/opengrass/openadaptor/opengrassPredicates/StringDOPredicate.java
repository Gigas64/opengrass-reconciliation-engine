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

import java.util.*;
import org.opengrass.exceptions.*;
import org.openadaptor.dataobjects.DataObject;


public class StringDOPredicate extends AbstractDOPredicate {

  /** Vector of comparison strings */
  private Vector strings;

  protected void setupOperator (String o) throws OpenGrassException {
    if (! o.equals("==") && ! o.equals("!=")) {
      throw new OpenGrassException("ERROR: Operator must be either [==] or [!=]");
    }
    operator = o;
  }

  /** parse array notation in to an array of strings **/
  protected void setupCriteria(String c) {
    criteria = c;
    if ((criteria.startsWith("{") && criteria.endsWith("}")) || criteria.equals("NULL")) {
      strings = new Vector();
      comparisonAtt = null;
      if (criteria.compareToIgnoreCase("null") == 0) {
        strings.add("");
      }
      else {
        String array_str = criteria.substring(1, criteria.length() - 1);
        StringTokenizer tokenizer = new StringTokenizer(array_str, ",");
        while (tokenizer.hasMoreTokens()) {
          strings.addElement(tokenizer.nextToken());
        }
      }
    }
    else {
      comparisonAtt = criteria;
      strings = null;
    }
    predicateClass = "StringPredicate";
  }

  protected void setupAttribute(String a) {
	attribute = a;
  }

  protected boolean test(String atValue, DataObject dataObj) throws OpenGrassException {
    boolean matches = false;
    if (atValue == null || atValue.length() < 1) {
	  atValue = "";
    }
    if (strings != null) {
      for (int i =0; i<strings.size(); i++) {
        String s = (String) strings.get(i);
        if (atValue.equals(s)) {
          matches = true;
          break;
        }
      }
    }
    else if (comparisonAtt != null) {
      String attValue2;
      try {
        attValue2 = (String) dataObj.getAttributeValue( comparisonAtt );
      }
      catch (Exception e) {
        throw new OpenGrassException("ERROR: No attribute " + comparisonAtt + " in incomming DataObject");
      }
      if (atValue.equals(attValue2)) {
        System.err.println("TRACE: (" + predicateClass + ") " + attribute + " [" + atValue + "] = " + comparisonAtt + " [" + attValue2 + "] ");
        matches = true;
      }
      else {
		System.err.println("TRACE: (" + predicateClass + ") " + attribute + " [" + atValue + "] != " + comparisonAtt + " [" + attValue2 + "] ");
        matches = false;
      }
    }
    // toggle
    if (operator.equals("!=")) {
      if (matches) { matches = false;}
      else { matches = true; }
    }
    return matches;
  }
}


