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

import org.opengrass.exceptions.*;
import org.openadaptor.dataobjects.DataObject;



public class DoubleDOPredicate extends AbstractDOPredicate {
  /** Double value to match against **/
  private double value;

  protected void setupOperator (String o) throws OpenGrassException {
    if (! o.equals("==") && ! o.equals("!=") && ! o.equals("<=")
     && ! o.equals(">=") && ! o.equals("<") && ! o.equals(">")) {
      throw new OpenGrassException("ERROR: Operator must be either [==], [!=], [<=], [>=], [<] or  [>]");
    }
    operator = o;
  }

  protected void setupCriteria(String c) throws OpenGrassException {
    criteria = c;
    if (criteria.startsWith("{") && criteria.endsWith("}")) {
      try {
        value = Double.parseDouble(criteria.substring(1, criteria.length() - 1));
      }
      catch (NumberFormatException e) {
        throw new OpenGrassException("ERROR: Cant Convert " + criteria + " to number format");
      }
      comparisonAtt = null;
    }
    else {
      comparisonAtt = criteria;
    }
    predicateClass = "DoublePredicate";
  }

  private double getValidDouble(String s) throws OpenGrassException {
    double d;
    if (s != null && s.length() > 0) {
      try {
        d = Double.parseDouble(s);
      }
      catch (NumberFormatException e) {
        throw new OpenGrassException("");
      }
    }
    else {
      throw new OpenGrassException("");
    }
    return d;
  }

  protected void setupAttribute (String a) throws OpenGrassException {
    attribute = a;
  }

  protected boolean test(String atValue, DataObject dataObj) throws OpenGrassException {
    boolean matches = false;
    double testValue1 = 0;
    double testValue2 = value;
    try {
      testValue1 = getValidDouble(atValue);
    }
    catch (OpenGrassException e) {
      System.err.println("WARN: Cant Convert " + attribute + " [" + atValue + "] to number format");
      return false;
    }
    if (comparisonAtt != null) {
      String atValue2;
      try {
        atValue2 = (String) dataObj.getAttributeValue( comparisonAtt );
      }
      catch (Exception e) {
        throw new OpenGrassException("ERROR: No attribute " + comparisonAtt + " in incomming DataObject");
      }
      try {
        testValue2 = getValidDouble(atValue2);
      }
      catch (OpenGrassException e) {
        System.err.println("WARN: Cant Convert " + comparisonAtt + " [" + atValue2 + "] to number format");
        return false;
      }
      System.err.println("TRACE: (" + predicateClass + ") " + attribute + " [" + testValue1 + "] " + operator + " " + comparisonAtt + " [" + testValue2 + "] ");
    }
    if ( (operator.equals("==") && testValue1 == testValue2)
       ||(operator.equals("!=") && testValue1 != testValue2)
       ||(operator.equals("<") && testValue1 < testValue2)
       ||(operator.equals(">") && testValue1 > testValue2)
       ||(operator.equals(">=") && testValue1 >= testValue2)
       ||(operator.equals("<=") && testValue1 <= testValue2)) {
      matches = true;
    }
    return matches;
  }

}

