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
*** Takes a DataObject and alters the format of date fields.
*** <p>Initialisation in Adaptor Property file
*** <br>prefix.OpenGrassDOAction  = org.opengrass.openadaptor.opengrassActions.ConcatinateAction
*** <br>prefix.oldFormat  = DateFormat
*** <br>prefix.newFormat  = DateFormat
*** <br>prefix.AttN  = AttributeName
*** <p>Date format should follow SimpleDateFormat syntax
*** <p>e.g. Changes the Date Format from
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.DateFormatAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.oldFormat = ddMMyyyy
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.newFormat = ddMMyy
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att1       = PaymentDate
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att2       = ResetDate
*** @date 31th July 2002
*** @version 1.00
*** @see.OpenGrassDOAction
***
**/

import java.util.*;
import org.opengrass.exceptions.OpenGrassException;
import org.openadaptor.dataobjects.DataObject;
import java.text.SimpleDateFormat;


public class DateFormatAction implements OpenGrassDOAction {
  /** Part of string before index **/
  private String prefix;
  /** List of attributes **/
  private Vector attNames;
  /** String representation of old format **/
  private String oldFormatString;
  /** String representation of new format **/
  private String newFormatString;
  /** Old date format **/
  private SimpleDateFormat oldFormat;
  /** New date format **/
  private SimpleDateFormat newFormat;

  /**
  *** Initialise from properties object. See class comment for property values
  ***
  *** @param props  Properties object
  *** @param prefix  Prefix string to search for properties
  ***
  *** @exception OpenGrassException Thrown if initialisation fails, mandatory properties are missing or properties cannot be parsed.
  **/

  public void init(Properties props, String pfix) throws OpenGrassException {
    int i = 1;
    prefix = pfix;

    attNames = new Vector();
    try {
      try {
        oldFormatString = props.get(prefix + ".oldFormat").toString();
      }
      catch (Exception e) {
       throw new OpenGrassException("ERROR: oldFormat not specified");
      }
      oldFormat = new SimpleDateFormat(oldFormatString);
      try {
        newFormatString = props.get(prefix + ".newFormat").toString();
      }
      catch (Exception e) {
        throw new OpenGrassException("ERROR: newFormat not specified");
      }
      newFormat = new SimpleDateFormat(newFormatString);
      while(props.get(prefix + ".Att" + i) != null) {
        attNames.add(props.get(prefix + ".Att" + i));
        i++;
      }
    }
    catch ( Exception e ) {
      throw new OpenGrassException( "ERROR: Incorrect setup for " + prefix + "\n" + e.getMessage());
    }
    System.err.println("INFO: DateFormatAction converting " + attNames + " from " + oldFormat.toPattern() + " to " + newFormat.toPattern());
  }

  /** Transforms the format of the specified date fields */
  public DataObject transformOpenGrassAttributes(DataObject dObject) throws OpenGrassException {
   for(int i=0;i<attNames.size();i++) {
     String att = (String) attNames.get(i);
     String oldDate;
     String newDate;
     try {
       oldDate = (String) dObject.getAttributeValue(att);
       newDate = this.reformatDate(oldDate, att);
       dObject.setAttributeValue(att, newDate);
     }
     catch (Exception e) {
       throw new OpenGrassException("ERROR: Error altering date format for attribute " + att + "\n" + e.getMessage());
     }
     System.err.println("TRACE: Reformatting " + att + " from [" + oldDate + "] to [" + newDate + "]");
   }
   return dObject;
  }

  private String reformatDate(String d, String att) throws OpenGrassException {
    Date oldDate;
    String newDate;
    try {
      oldDate = oldFormat.parse(d);
      newDate = newFormat.format(oldDate);
    }
    catch (Exception e) {
      System.err.println("WARN: Can't reformat attribute " + att + " [" + d + "] from [" + oldFormat.toPattern() + "] to [" + newFormat.toPattern() + "]\n" + e.toString());
      newDate = d;
    }
    return newDate;
  }
}


