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
*** Takes a DataObject and trims one of the attribute values.
*** <p>Initialisation in Adaptor Property file
*** <br>prefix.OpenGrassDOAction  = org.opengrass.openadaptor.opengrassActions.StringTrimAction
*** <br>prefix.Att  = NameOfAttribute eg FirstName
*** <br>prefix.Action  = NameOfAction  eg TrimStart
*** <br>prefix.Index   = Index  eg 3
*** <p>Each attribute has its own action as above.
*** Each action requires an index ( an integer ).
*** This index represents a position in the incoming string.
*** The first character has position 0, the second 1 and so on.
*** The string is then split into two, the first part all characters before the index
*** and the last all characters including and after that at the index position.
*** eg if the index is 4 and the string 'maddening'
*** the first part is 'madd' and the second 'ening'.
*** If the index is negative the index is set that number of characters to the left of the end.
*** eg if index is -3 and the string is 'maddening'
*** the index is set at the 'i'. The first part will be 'madden' and the last 'ing'
*** The actions TrimStart and SaveEnd both discard the first part and keep the last.
*** TrimEnd and SaveStart do the opposite.
*** <p>e.g. Trims the first 3 characters off the value in the Book Attribute
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.StringTrimAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att        = Book
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Action     = TrimStart
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Index     = 3
*** @date 25th June 2002
*** @version 1.00
*** @see.OpenGrassDOAction
***
**/

import java.util.Properties;
import org.opengrass.exceptions.OpenGrassException;
import org.openadaptor.dataobjects.InvalidParameterException;
import org.openadaptor.dataobjects.DataObject;

public class StringTrimAction implements OpenGrassDOAction {
  /** Part of string before index **/
  private String prefix;
  /** Part of string after index **/
  private String attName;
  /** SaveEnd/SaveStart/TrimEnd/TrimStart **/
  private String action;
  /** Index **/
  private int index;

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
      if ( ! (action.equals("TrimStart")
         || action.equals("TrimEnd")
         || action.equals("SaveStart")
         || action.equals("SaveEnd"))) {
        throw new OpenGrassException("ERROR: " + prefix + " must be TrimStart, TrimEnd, SaveStart or SaveEnd");
      }
    }
    else {
      throw new OpenGrassException("ERROR: No attribute specified for " + prefix + ".Action");
    }
    if (props.get(prefix + ".Index") != null) {
      index = Integer.parseInt((String) props.get(prefix + ".Index"));
    }
    else {
      throw new OpenGrassException("ERROR: No attribute specified for " + prefix + ".Index");
    }
    System.err.println("INFO: String Trim Action [" + attName + "] " + action + " with index " + index);

  }

  /** Trims the attribute value in the DataObject */
  public DataObject transformOpenGrassAttributes(DataObject dObject) throws OpenGrassException {

    try {
      String toBeTrimmed = (String) dObject.getAttributeValue( attName );
      String trimmed = trimString ( toBeTrimmed );
      dObject.setAttributeValue( attName, trimmed );
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: When Trimming " + attName + "\n" + e.getMessage());
    }
    return dObject;
  }

  private String trimString( String toBeTrimmed ) throws InvalidParameterException {
    String trimmed="";
    String preIndex = "";
    String postIndex = "";
    int idxPoint = 0;

    if( toBeTrimmed != null) {
      if (index < 1) {
        idxPoint = toBeTrimmed.length() + index;
      }
      else {
        idxPoint = index;
      }

      if (idxPoint >= toBeTrimmed.length()) {
             System.err.println("WARN: Trim index [" + idxPoint
         + "] is greater than the string length ["
         + toBeTrimmed.length() + "]");
         preIndex = toBeTrimmed;
      }
      else if (idxPoint >= toBeTrimmed.length() || idxPoint < 1) {
             System.err.println("WARN: Trim index [" + idxPoint
         + "] is less than the string length ["
         + toBeTrimmed.length() + "]");
         postIndex = toBeTrimmed;
      }
      else {
        // The incoming string is split into two parts
        preIndex  =  toBeTrimmed.substring(0,idxPoint);
        postIndex  =  toBeTrimmed.substring(idxPoint);
      }

      // if the action accompanying the string is one of these keep the last part
      if (action.equals("SaveEnd") || action.equals("TrimStart")) {
        trimmed = postIndex;
      }
      // if these two keep the first part
      else if(action.equals("TrimEnd") || action.equals("SaveStart")) {
        trimmed = preIndex;
      }
      else {
        trimmed = toBeTrimmed;
        System.err.println("ERROR: Action for String trim not recognised" + action);
      }
    }
    System.err.println("TRACE: Value Trim: [" + toBeTrimmed + "] -> [" + trimmed + "]");
    return trimmed;
  }

}
