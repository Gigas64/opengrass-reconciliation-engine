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
*** Predicate for a Date type attribute.
*** <p>Intialisation in Adaptor Property file
*** <br>prefix.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.DatePredicate
*** <br>prefix.Att  = AttributeName dateFormat
*** <br>prefix.Criteria = dateCriteria
*** <p>Date format should follow SimpleDateFormat syntax
*** <br>prefix.Att = paymentDate ddMMyyyy
*** <p>Example date criteria
*** <br>prefix.Criteria = 27071974         # Match 27071974
*** <br>prefix.Criteria = !27071974        # Match any data except 27071974
*** <br>prefix.Criteria = >=27071974       # Match later or equal to 27071974
*** <br>prefix.Criteria = [RunDate]        # Match if present date
*** <br>prefix.Criteria = <[RunDate + 14]  # Match any date less than 2 weeks from present date
*** <p>e.g. Returns true if PaymentDate is 27071974
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.DatePredicate
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Att      = PaymentDate ddMMyyyy
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Criteria = 27071974
*** @date 25th June 2002
*** @version 1.00
*** @see.OpenGrassAttPredicate
***
**/


import java.util.*;
import java.text.SimpleDateFormat;
import org.opengrass.exceptions.*;

import org.apache.regexp.RE;



public class DatePredicate implements OpenGrassAttPredicate {
  /** Attribute to apply the predicate to **/
  private String attribute;
  /** Format of date **/
  private String dateFormatString;
  /** matching criteria (e.g. <,>,=,!) **/
  private String criteria;
  /** Comparison date **/
  private Date predDate;
  /** Date formater **/
  private SimpleDateFormat df;

  /** Initialiser<br>prefix.Att = attNameAndFormat <br>prefix.Criteria = _criteria **/
  public void init(String attNameAndFormat,String _criteria) throws OpenGrassException {
    try {
      StringTokenizer st = new StringTokenizer(attNameAndFormat);
      if (st.countTokens() != 2) {
        throw new OpenGrassException("");
      }
      attribute = st.nextToken();
      dateFormatString = st.nextToken();
      df = new SimpleDateFormat(dateFormatString);
    }
    catch (Exception e) {
      throw new OpenGrassException("Incorrect format for Date attribute: [AttName DateFormat] e.g PaymentDate ddMMyyy \n" + e.getMessage());
    }
    if (_criteria.startsWith("<=") || _criteria.startsWith(">=")) {
      setDateCriteria(_criteria.substring(2));
      criteria = _criteria.substring(0,2);
    }
    else if (_criteria.startsWith("!") || _criteria.startsWith(">") || _criteria.startsWith("<")  ) {
      setDateCriteria(_criteria.substring(1));
      criteria = _criteria.substring(0,1);
    }
    else {
      criteria = "=";
      setDateCriteria(_criteria);
    }
    System.err.println("INFO: Date Predicate " + dateFormatString + " " +attribute + " " + criteria + " " + df.format(predDate));
  }
  /** Returns attribute Name **/
  public String getAttName() {
    return attribute;
  }
  /** Returns true if the attValue matches the predicate **/
  public boolean matches (String attValue) {
    Date doDate;
    boolean match = false;
    try {
      doDate = df.parse(attValue);
      if ((criteria.equals("=") && doDate.compareTo(predDate) == 0)
        || (criteria.equals("!=") && doDate.compareTo(predDate) != 0)
        || (criteria.equals("<") && doDate.compareTo(predDate) < 0)
        || (criteria.equals(">") && doDate.compareTo(predDate) > 0)
        || (criteria.equals("<=") && doDate.compareTo(predDate) <= 0)
        || (criteria.equals(">=") && doDate.compareTo(predDate) >= 0)) {
        match = true;
      }
      System.err.println("TRACE: (Date) " + attribute + " [ " + df.format(doDate) + " " + criteria + " " + df.format(predDate) + "] = " + match);
    }
    catch (Exception e) {
      System.err.println("WARN: Cant parse date [ " + attValue + "] Format must be [" + dateFormatString + "] ");
    }
    return match;
  }
  private void setDateCriteria (String _date) throws OpenGrassException {
    GregorianCalendar gc = new GregorianCalendar();
    RE regExp;
    try {
      regExp = new RE("RunDate([+-]?)([\\d]*)");
      if (regExp.match(_date)) {
        Date x1 = new Date();
        String x2 = df.format(x1);
        x1 = df.parse(x2);
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
          predDate =  df.parse(_date);
        }
        catch (Exception e) {
          throw new OpenGrassException("");
        }
      }
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Cant parse date criteria [" + _date + "] Format must be [" + dateFormatString + "] or [RunDate[+/-]integer]\n" + e.getMessage());
    }
  }
}
