package org.opengrass.openadaptor.opengrassActions;

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
***
*** Perfoms a mathemetical operation on the specified Dataobject Attribute values.
*** Reads in the two source Attributes from the given Dataobject,
*** applies the operation specified, then puts the results in the
*** Attribute specified by Dest.
*** <p>Intialisation in Adaptor Property file
*** <br>prefix.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.MathsAction
*** <br>prefix.Action  = +,-,/,*
*** <br>prefix.Att1 = Attribute Name
*** <br>prefix.Att2 = Attribute Name
*** <br>prefix.Dest = Attribute Name
*** <p>e.g. Adds the values in Sale1 and Sale2 and puts the result in Amount
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.MathsAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Action      = +
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att1    = Sale1
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att2    = Sale2
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Dest    = Amount
*** <p>e.g. Multiplies Total by 10, putting the result back in Total
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.MathsAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Action      = *
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att1    = Total
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att2    = {10}
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Dest    = Total
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Precision    = 0
*** <p>e.g. Rounds Cost to 2dp
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.MathsAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Dest    = Cost
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Precision    = 2
*** @date 30th July 2002
*** @version 1.01
*** @see.OpenGrassDOAction
***
**/


import org.opengrass.exceptions.OpenGrassException;
import java.util.Properties;
import java.math.BigDecimal;
import org.apache.regexp.RE;
import org.openadaptor.dataobjects.InvalidParameterException;
import org.openadaptor.dataobjects.DataObject;


public class MathsAction implements OpenGrassDOAction {

  /** Property prefix **/
  private String prefix;
  /** Attribute that holds value1 **/
  private String att1;
  /** Value 1 **/
  private BigDecimal value1;
  /** Attribute that holds value2 **/
  private String att2;
  /** Value 2 **/
  private BigDecimal value2;
  /** Action to perform on the values **/
  private String action;
  /** Attribute that will hold the result created when the action is performed on the two values **/
  private String dest;
  /** Result to put in destination **/
  private BigDecimal destValue;
  /** No decimal places precision **/
  private int precision;
 /**
 *** Initialise from properties object. See class comment for property values
 ***
 *** @param props  Properties object
 *** @param prefix  Prefix string to search for properties
 ***
 *** @exception OpenGrassException Thrown if initialisation fails, mandatory properties are missing or properties cannot be parsed.
 **/

  public void init(Properties props, String pfix) throws OpenGrassException {
    prefix = pfix;
    RE regExp;
    String tmp1;
    String tmp2;
    try   {
      regExp = new RE("\\{(.*)\\}");
      // get names of the attributes used in the calculation
      // If no attribute name is specified get the default value
      dest = props.getProperty( prefix + ".Dest").toString();
      if (  props.getProperty( prefix + ".Att1") != null
          && props.getProperty( prefix + ".Att2") != null
          && props.getProperty( prefix + ".Action") != null ) {
        tmp1 = props.getProperty( prefix + ".Att1");
        if (regExp.match(tmp1)) {
          value1 = new BigDecimal(regExp.getParen(1));
          att1 = "undefined";
        }
        else {
          att1 = tmp1;
        }
        tmp2 = props.getProperty( prefix + ".Att2");
        if (regExp.match(tmp2)) {
          value2 = new BigDecimal(regExp.getParen(1));
          att2 = "undefined";
        }
        else {
          att2 = tmp2;
        }
        // Get the operation to be performed
        action = props.getProperty( prefix + ".Action").toString();
        if (!(action.equals("*") || action.equals("/")
           || action.equals("+") || action.equals("-"))) {
         throw new OpenGrassException( "ERROR: Math operand not recognised: " + action);

        }
        System.err.println("INFO: Maths Action " + att1 + " (" + value1 + ") " + action + " " + att2 + " (" + value2 + ") -> " + dest);
      }
      else {
        att1 = "undefined";
        att2 = "undefined";
        action = "undefined";
      }
      if (props.getProperty( prefix + ".Precision") != null) {
        precision = Integer.parseInt(props.getProperty( prefix + ".Precision"));
        System.err.println("INFO: Maths Action: " + dest + " attribute rounded to " + precision + " decimal places");
      }
      else {
        precision = 1000000;
      }
    }
    catch ( Exception e ) {
      throw new OpenGrassException( "ERROR: Incorrect setup for " + prefix + "\n" + e.getMessage());
    }
  }

  /** Performs the mathematical function on the attributes in the dataObject **/
  public DataObject transformOpenGrassAttributes(DataObject dObject) throws OpenGrassException {

    if (!att1.equals("undefined")) {
      String tmp;

      try {
        tmp =  (String) dObject.getAttributeValue(att1);
      }
      catch (InvalidParameterException e) {
        throw new OpenGrassException("ERROR: No value specified for " + att1);
      }
      if (tmp == null  || tmp.length() < 1) {
        tmp = "0";
        System.err.println("WARN: No value specified for " + att1
                            + ": Setting to zero");
      }
      value1 = new BigDecimal(tmp);
    }
    if (!att2.equals("undefined")) {
      String tmp;
      try {
        tmp =  (String) dObject.getAttributeValue(att2);
      }
      catch (InvalidParameterException e) {
        throw new OpenGrassException("ERROR: No value specified for " + att2);
      }
      if (tmp == null  || tmp.length() < 1) {
        tmp = "0";
        System.err.println("WARN: No value specified for " + att2
                                + ": Setting to zero");
      }
      value2 = new BigDecimal(tmp);
    }
    if (att2.equals("undefined") && att1.equals("undefined")) {
      String tmp;
      try {
        tmp =  (String) dObject.getAttributeValue(dest);
      }
      catch (InvalidParameterException e) {
        throw new OpenGrassException("ERROR: No value specified for " + dest);
      }
      if (tmp == null  || tmp.length() < 1) {
        tmp = "0";
        System.err.println("WARN: No value specified for " + dest
                                + ": Setting to zero");
      }
      destValue = new BigDecimal(tmp);
    }
    // Apply the MathsFunction and write the answer back into the DataObject
    try {
      dObject.setAttributeValue( dest , this.applyFunction());
    }
    catch (InvalidParameterException e) {
      throw new OpenGrassException("ERROR: Cannot set attribute [" + dest + "] to [" + this.applyFunction() + "]");
    }
    return dObject ;
  }

  /**
  * This returns the value created when the operator is applied to value1 and value2
  **/
  private String applyFunction() throws OpenGrassException {
    BigDecimal result = null;
    try {
      if (action.equals("*")) {
        result = value1.multiply(value2);
      }
      else if (action.equals("/")) {
        if (value2.compareTo(new BigDecimal("0")) == 0)  {
         System.err.println( "WARN: Can't divide by zero");
        }
        else {
         result = value1.divide(value2, 20, BigDecimal.ROUND_HALF_UP);
        }
      }
      else if (action.equals("+")) {
        result = value1.add(value2);
      }
      else if (action.equals("-")) {
        result = value1.subtract(value2);
      }
      else if (action.equals("undefined")) {
        result = destValue;
      }
      else {
       throw new OpenGrassException( "ERROR: Operator type not recognised" + action);
      }
      if (precision != 1000000 && result != null) {
        result = result.setScale(precision, BigDecimal.ROUND_HALF_UP);
      }
    }
    catch (Exception e) {
      throw new OpenGrassException( "ERROR: Error when applying maths function\n" + e.getMessage());
			 }
			 System.err.println("TRACE: (" + att1 + ") " + value1 + " " + action + " (" + att2 + ") " +  value2 + " = (" + dest + ") " +  result +  " to " + precision + " dp");
    if (result == null) {
					 return "NaN";
				}
				else {
      return result.toString();
			 }
  }
}

