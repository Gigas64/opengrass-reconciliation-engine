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
*** Takes a DataObject and re-ailias one of the fields.
*** <p>Intialisation in Adaptor Property file
*** <br>prefix.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.FieldAliasAction
*** <br>prefix.Source  = AttributeName
*** <br>prefix.Dest = Attribute Name
*** <p>e.g. Overwrites the value in Strike with that in Strike amount
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.FieldAliasAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Source   = StrikeAmount
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Dest     = Strike
*** <p>e.g. Overwrites the value in the Attribute Strike with "0"
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.FieldAliasAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Source   = {0}
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Dest     = Strike
*** <p>Note: NULL in source attribute will change destination attribute value to null
*** @date 25th June 2002
*** @version 1.00
*** @see.OpenGrassDOAction
***
**/

import org.opengrass.exceptions.OpenGrassException;
import java.util.*;
import org.apache.regexp.RE;
import org.openadaptor.dataobjects.InvalidParameterException;
import org.openadaptor.dataobjects.DataObject;

public class FieldAliasAction implements OpenGrassDOAction {

  /** Property prefix **/
  private String prefix;
  /** Source Attribute name **/
  private String sourceAtt;
  /** Source value **/
  private String sourceValue;
  /** Dest Attribute **/
  private String destAtt;

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
    if (props.get(prefix + ".Dest") != null) {
      destAtt = (String) props.get(prefix + ".Dest");
    }
    else {
      throw new OpenGrassException("ERROR: No attribute specified for " + prefix + ".Dest");
    }
    try {
      RE regExp = new RE("\\{(.*)\\}");
      String tmp = (String) props.get(prefix + ".Source");
      if (regExp.match(tmp)) {
        if (regExp.getParen(1).equals("NULL")) {
           sourceValue = "_NULL_VALUE";
        }
        else {
          sourceValue = regExp.getParen(1);
        }
        System.err.println("INFO: Value Alias Action [" + destAtt + "] -> [" + sourceValue + "]");

      }
      else {
        sourceAtt = tmp;
        System.err.println("INFO: Value Alias Action [" + destAtt + "] -> [" + sourceAtt + "]");

      }
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: In description of source attribute: " + prefix + ".Dest = attName/{attValue}");
    }
  }

  /** Performs the aliasing on the attributes in the dataObject **/
  public DataObject transformOpenGrassAttributes(DataObject dObject) throws OpenGrassException {
    String newValue;
    if (sourceAtt == null) {
      if (sourceValue.equals("_NULL_VALUE")) {
        newValue = null;
      }
      else {
        newValue = sourceValue;
      }
    }
    else {
      try {
        newValue = (String) dObject.getAttributeValue(sourceAtt);
      }
      catch (InvalidParameterException e) {
        throw new OpenGrassException("Cannot get source attribute " + sourceAtt);
      }
    }
    try {
      dObject.setAttributeValue( destAtt , newValue);
    }
    catch (InvalidParameterException e) {
      throw new OpenGrassException("Cannot set destination attribute " + destAtt + " to " + sourceValue);
    }
    System.err.println("TRACE: Setting Attribute [" + destAtt + "] -> [ " + sourceAtt + " = " + newValue + " ]");
    return dObject;
  }
}






