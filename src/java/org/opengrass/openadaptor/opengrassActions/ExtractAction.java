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
*** Takes a DataObject and extracts part of the specifed field
*** <p>Initialisation in Adaptor Property file
*** <br>prefix.OpenGrassDOAction  = org.opengrass.openadaptor.opengrassActions.ExtractAction
*** <br>prefix.RegExp  = Regular Expression with parentheses around bit you want to extract
*** <br>prefix.Source  = AttributeName
*** <br>prefix.Dest  = AttributeName
*** <p>Regular Expression format should follow java RE class syntax. If the incomming attribute does
*** not match the regular expression the dest attribute will be set to the content of the src att.
*** <p>e.g.
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.ExtractAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.RegExp = ([A-Z]*)[\\d]*\$
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Source       = ResetDate
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Dest       = ResetDate
*** @date 31th July 2002
*** @version 1.00
*** @see.OpenGrassDOAction
***
**/


import java.util.*;
import org.opengrass.exceptions.OpenGrassException;
import org.openadaptor.dataobjects.DataObject;
import org.apache.regexp.RE;



public class ExtractAction implements OpenGrassDOAction {
  /** Part of string before index **/
  private String prefix;
  /** Source Attribute Name **/
  private String sourceAtt;
  /** Destination Attribute Name **/
  private String destAtt;
  /** String representation of Regular Expression **/
  private String regExpString;
  /** The regular Expression **/
  private RE regExp;

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
    RE regExpFormat;
    try {
      regExpFormat = new RE(".*[(].*[)].*");
      sourceAtt = props.get(prefix + ".Source").toString();
      destAtt = props.get(prefix + ".Dest").toString();
      regExpString = props.get(prefix + ".RegExp").toString();
      if (regExpFormat.match(regExpString)) {
        regExp = new RE(regExpString);
      }
      else {
        throw new OpenGrassException("RegExp incorrectly specifed [" + regExpString + "] - Place parentheses around the string you wish to extract\n");
      }
    }
    catch ( Exception e ) {
      throw new OpenGrassException( "ERROR: Incorrect setup for " + prefix + "\n" + e.getMessage());
    }
    System.err.println("INFO: ExtractAction extracting from field " + sourceAtt + " to field " + destAtt + " using the pattern " + regExpString);
  }

  /** Extracts part of the source attribute value and places in the destination attribute */
  public DataObject transformOpenGrassAttributes(DataObject dObject) throws OpenGrassException {
    String sourceString = "";
    String extractedString = "";
    try {
      sourceString = (String) dObject.getAttributeValue(sourceAtt);
      if (sourceString != null && regExp.match(sourceString)) {
        extractedString =  regExp.getParen(1);
        System.err.println("TRACE: Extracting " + extractedString + " from " + sourceAtt + " [" + sourceString + "] to [" + destAtt + "]");
      }
      else {
        extractedString = sourceString;
        System.err.println("WARN:" +  prefix + ": ExtractAction " + sourceAtt + " value [" + sourceString + "] does not match extraction regular expression " + regExpString + ". Setting " + destAtt + " to " + sourceString);
      }
      dObject.setAttributeValue(destAtt, extractedString);
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Error extracting string from " + sourceAtt + " and placing in " + destAtt + "\n" + e.getMessage());
    }
    return dObject;
  }
}


