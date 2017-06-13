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
*** Takes a DataObject and applies Case to one of the attribute values.
*** <p>Initialisation in Adaptor Property file
*** <br>prefix.OpenGrassDOAction  = org.opengrass.openadaptor.opengrassActions.StringCaseAction
*** <br>prefix.Att  = NameOfAttribute eg FirstName
*** <br>prefix.Action  = NameOfAction  eg ToUpper
*** <p>Each attribute has its own action as above.
*** The actions are ToUpper to convert all characters to
*** upper case, and ToLower for the opposite.
*** <p>e.g. Convert to upper case all characters in the Book Attribute
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.StringCaseAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att        = Book
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Action     = ToUpper
*** @date 16/08/2004
*** @version 1.00
*** @see.OpenGrassDOAction
***
**/

import java.util.Properties;

import org.opengrass.exceptions.OpenGrassException;
import org.openadaptor.dataobjects.DataObject;

public class StringCaseAction implements OpenGrassDOAction {
  /** Part of string before index **/
  private String prefix;
  /** Part of string after index **/
  private String attName;
  /** ToUpper/ToLower **/
  private String action;

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
    if (props.get(prefix + ".Att") != null) {
      attName = (String) props.get(prefix + ".Att");
    }
    else {
      throw new OpenGrassException("ERROR: No attribute specified for " + prefix + ".Att");
    }
    if (props.get(prefix + ".Action") != null) {
      action = (String) props.get(prefix + ".Action");
      if ( ! (action.equals("ToUpper") || action.equals("ToLower"))) {
        throw new OpenGrassException("ERROR: " + prefix + " must be ToUpper or ToLower");
      }
    }
    else {
      throw new OpenGrassException("ERROR: No attribute specified for " + prefix + ".Action");
    }
    System.err.println("INFO: String Case Action [" + attName + "] " + action);

  }

  /** Trims the attribute value in the DataObject */
  public DataObject transformOpenGrassAttributes(DataObject dObject) throws OpenGrassException {

    try {
      String old_string = (String) dObject.getAttributeValue( attName );
      String new_string;
      if(action.equals("ToUpper")) {
        new_string = old_string.toUpperCase();
      }
      else if(action.equals("ToLower")) {
        new_string = old_string.toLowerCase();
      }
      else {
        new_string = old_string;
      }
      dObject.setAttributeValue( attName, new_string );
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: When Changing Case on " + attName + "\n" + e.getMessage());
    }
    return dObject;
  }
}
