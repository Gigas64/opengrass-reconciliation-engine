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
import java.text.SimpleDateFormat;
import org.opengrass.exceptions.*;
import org.openadaptor.dataobjects.DataObject;
import org.apache.regexp.RE;


public class DateDOPredicate extends AbstractDOPredicate {

  private Hashtable attDateFormat;
  /** Comparison date **/
  private Date predDate;
  private String att1Criteria;

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
      setDateCriteria(criteria.substring(1, criteria.length() - 1));
      comparisonAtt = null;
    }
    else {
      comparisonAtt = getAttName(criteria);
      attDateFormat.put(comparisonAtt, getDateFormat(criteria));
    }
    predicateClass = "DatePredicate";
  }

  protected void setupAttribute(String a) throws OpenGrassException {
    attDateFormat = new Hashtable();
    attribute = a;
    att1Criteria = attribute;
    try {
      attribute = getAttName(att1Criteria);
      attDateFormat.put(attribute, getDateFormat(att1Criteria));
    }
    catch (Exception e) {
      throw new OpenGrassException("Incorrect format for att [ " + att1Criteria + "]: Format must match [AttName DateFormat] e.g PaymentDate ddMMyyy \n" + e.getMessage());
    }
  }

  /** Returns true if the attValue matches the predicate **/
  public boolean test (String atValue, DataObject dataObj) throws OpenGrassException {
    Date testDate1;
    Date testDate2 = predDate;
    SimpleDateFormat sdf1 = (SimpleDateFormat) attDateFormat.get(attribute);
    SimpleDateFormat sdf2 = (SimpleDateFormat) attDateFormat.get(attribute);;
    boolean match = false;
    try {
      testDate1 = sdf1.parse(atValue);
    }
    catch (Exception e) {
      System.err.println("WARN: Cant Convert " + attribute + " [" + atValue + "] to Date with SimpleDateFormat " + sdf1.toPattern());
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
        sdf2 = (SimpleDateFormat) attDateFormat.get(comparisonAtt);
        testDate2 = sdf2.parse(atValue2);
      }
      catch (Exception e) {
        System.err.println("WARN: Cant Convert " + comparisonAtt + " [" + atValue2 + "] to Date with SimpleDateFormat " + sdf2.toPattern());
        return false;
      }
    }
    System.err.println("TRACE: (DatePredicate) " + sdf1.format(testDate1) + " " + operator + " " + sdf2.format(testDate2));
    if ((operator.equals("=") && testDate1.compareTo(testDate2) == 0)
      || (operator.equals("!=") && testDate1.compareTo(testDate2) != 0)
      || (operator.equals("<") && testDate1.compareTo(testDate2) < 0)
      || (operator.equals(">") && testDate1.compareTo(testDate2) > 0)
      || (operator.equals("<=") && testDate1.compareTo(testDate2) <= 0)
      || (operator.equals(">=") && testDate1.compareTo(testDate2) >= 0)) {
      match = true;
    }
    return match;
  }


  private String getAttName (String s) throws OpenGrassException {
    String att;
    StringTokenizer st = new StringTokenizer(s);
    if (st.countTokens() != 2) {
      throw new OpenGrassException("");
    }
    att = st.nextToken();
    return att;
  }

  private SimpleDateFormat getDateFormat (String s) throws OpenGrassException {
    SimpleDateFormat df;
    StringTokenizer st = new StringTokenizer(s);
    if (st.countTokens() != 2) {
      throw new OpenGrassException("");
    }
    //String tmp = st.nextToken();
    df = new SimpleDateFormat(st.nextToken());
    return df;
  }

  private void setDateCriteria (String _date) throws OpenGrassException {
    GregorianCalendar gc = new GregorianCalendar();
    RE regExp;
    SimpleDateFormat sdf = (SimpleDateFormat) attDateFormat.get(attribute);
    try {
      regExp = new RE("RunDate([+-]?)([\\d]*)");
      if (regExp.match(_date)) {
        Date x1 = new Date();
        String x2 = sdf.format(x1);
        x1 = sdf.parse(x2);
        gc.setTime(x1);
        if (regExp.getParen(1).equals("+")) {
          gc.add(Calendar.DATE, Integer.parseInt(regExp.getParen(2)));
        }
        else if (regExp.getParen(1).equals("-")) {
          gc.add(Calendar.DATE, Integer.parseInt(regExp.getParen(1)+ regExp.getParen(2)));
        }
        else if (!regExp.getParen(1).equals("") && !regExp.getParen(1).equals("")) {
          throw new OpenGrassException("");
        }
        predDate = gc.getTime();
      }
      else {
        try {
          predDate =  sdf.parse(_date);
        }
        catch (Exception e) {
          throw new OpenGrassException("");
        }
      }
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Cant parse date criteria {" + _date + "} Must follow date format {" + sdf.toPattern() + "} or {RunDate[+/-]integer}\n" + e.getMessage());
    }
  }
}
