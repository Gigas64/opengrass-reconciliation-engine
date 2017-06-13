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
*** Predicate for a Double type attribute.
*** <p>Intialisation in Adaptor Property file
*** <br>prefix.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.DoublePredicate
*** <br>prefix.Att  = Attribute name
*** <br>prefix.Criteria = Value
*** <p>Value can be any of the following
*** <br>prefix.Criteria = 0          #Match 0
*** <br>prefix.Criteria = <2000      #Match if less than 2000
*** <br>prefix.Criteria = !40        #Match if not 40
*** <br>prefix.Criteria = >=0        #Match if larger than or equal to 0
*** <p>e.g. Returns true if Strike value is equal to zero
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.DoublePredicate
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Att      = Strike
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Criteria = 0
*** @date 29th July 2002
*** @version 1.01
*** @see.OpenGrassAttPredicate
***
**/


import org.opengrass.exceptions.*;

import org.apache.regexp.RE;


public class DoublePredicate implements OpenGrassAttPredicate {
  /** Attribute to apply the predicate to **/
  private String attribute;
  /** matching criteria (<,>,!,<=,>= **/
  private String criteria;
  /** Double value to match against **/
  private double value;

  /** Initialiser<br>prefix.Att = att<br>prefix.Criteria = _criteria **/
  public void init(String att, String _criteria) throws OpenGrassException {
    attribute = att;
    try {
      RE reg_exp = new RE("(!|>|<|(<=)|(>=))?.*");
      if (reg_exp.match(_criteria)) {
        if (_criteria.startsWith("<=") || _criteria.startsWith(">=")) {
          value = Double.parseDouble(_criteria.substring(2));
          criteria = _criteria.substring(0,2);
        }
        else if (_criteria.startsWith("!") || _criteria.startsWith(">") || _criteria.startsWith("<")  ) {
         value = Double.parseDouble(_criteria.substring(1));
          criteria = _criteria.substring(0,1);
        }
        else {
          value = Double.parseDouble(_criteria);
          criteria = "=";
        }
      }
      else {
        throw new OpenGrassException("");
      }
    }
    catch (Exception e) {
      throw new OpenGrassException( "ERROR:  Predicate type [" + _criteria + "] not valid\n"
                              + "Criteria must match the following format: (!|>|<|(<=)|(>=))number" );
    }
    System.err.println("INFO: Double Predicate " + attribute + " = " + _criteria);
  }
  /** Returns attribute Name **/
  public String getAttName() {
    return attribute;
  }
  /** Returns true if the attValue matches the predicate **/
  public boolean matches (String attValue) {
    double d = Double.parseDouble(attValue);
    boolean matches = false;
    if ( (criteria.equals("=") && d == value)
       ||(criteria.equals("!") && d != value)
       ||(criteria.equals("<") && d < value)
       ||(criteria.equals(">") && d > value)
       ||(criteria.equals(">=") && d >= value)
       ||(criteria.equals("<=") && d <= value)) {
      matches = true;
    }
    System.err.println("TRACE: (Double) " + attribute + " [" + d + " " + criteria + " " + value + "] = " + matches);
    return matches;
  }

  public String toString() {
			 return attribute + " " + criteria + " " + value;

		}
}

