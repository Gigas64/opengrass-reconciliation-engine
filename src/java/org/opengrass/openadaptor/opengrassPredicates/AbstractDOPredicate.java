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
import java.util.Properties;
import org.openadaptor.dataobjects.DataObject;
import org.openadaptor.dataobjects.InvalidParameterException;

abstract class AbstractDOPredicate implements OpenGrassDOPredicate {

  /** Attribute name */
  protected String attribute;
  /** String representation of the regular expression */
  protected String criteria;
  /** Operator (! or =) */
  protected String operator;
  /** Predicate Type */
  protected String predicateClass;
  /** True = att comparison, False = set criteria */
  protected String comparisonAtt;

  public void init(Properties props, String prefix) throws OpenGrassException {
	String a;
	String c;
	String o;
    if ( props.get(prefix + ".Att") != null) {
      a = (String) props.get(prefix + ".Att");
    }
    else {
      throw new OpenGrassException("ERROR: " + prefix + ".Att not specified");
    }
    if (  props.get(prefix + ".Criteria") != null) {
      c = (String) props.get(prefix + ".Criteria");
    }
    else {
      throw new OpenGrassException("ERROR: " + prefix + ".Criteria not specified");
    }
    if (  props.get(prefix + ".Operator") != null) {
      o = (String) props.get(prefix + ".Operator");
    }
    else {
      throw new OpenGrassException("ERROR: " + prefix + ".Operator not specified");
    }
    predicateClass = (String) props.get(prefix + ".ClassName");
    this.setupOperator(o);
    this.setupAttribute(a);
    this.setupCriteria(c);
    System.err.println( "INFO: (" + predicateClass + ") " + attribute + " " + operator + " " + criteria);
  }

  abstract void setupCriteria(String criteria) throws OpenGrassException;
  abstract void setupAttribute(String criteria) throws OpenGrassException;
  abstract void setupOperator (String operator) throws OpenGrassException;

  public boolean matches (DataObject dObj) throws OpenGrassException {
    String attValue = null;
    try {
      attValue = (String) dObj.getAttributeValue( attribute );
    }
    catch (InvalidParameterException e) {
      throw new OpenGrassException("ERROR: No attribute " + attribute + " in incomming DataObject");
    }
    boolean matches = this.test(attValue, dObj);
    System.err.println("TRACE: (" + predicateClass + ") " + attribute + " [" + attValue + "] " + operator + " " + criteria + " " + matches);
    return matches;
  }

  abstract boolean test(String attName, DataObject testObj) throws OpenGrassException;

}


