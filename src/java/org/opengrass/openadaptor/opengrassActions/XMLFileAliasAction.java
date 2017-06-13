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
*** Takes a DataObject alias one of the fields depending on the current value
*** <p>Intialisation in Adaptor Property file
*** <br>prefix.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.ValueAliasAction
*** <br>prefix.Att  = AttributeName
*** <br>prefix.FileName = FileName
*** <p>e.g. Changes values in BuySell
*** <p>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.XMLFileAliasAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att     = BuySell
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.FileName  = ${XREF_PATH}/imagine_book.xml
*** <p> If you want to Alias to or from null or an empty string use "" in the xml file
*** @date 25th June 2002
*** @version 1.01
*** @see.OpenGrassDOAction
***
**/


import org.opengrass.exceptions.OpenGrassException;
import org.opengrass.datastores.filterstore.ValueXMLParser;
import org.opengrass.datastores.filterstore.ValueReference;
import java.util.*;
import org.openadaptor.dataobjects.InvalidParameterException;
import org.openadaptor.dataobjects.DataObject;




public class XMLFileAliasAction implements OpenGrassDOAction {

  /** Property prefix **/
  private String prefix;
  /** Hash of from - to values **/
  private TreeMap fromTo;
  /** Attribute name **/
  private String attribute;
  /** File name **/
  private String fileName;
  /** Handler for reading data from the file **/
  private ValueXMLParser valueParser;

 /**
 *** Initialise from properties object. See class comment for property values
 ***
 *** @param props  Properties object
 *** @param prefix  Prefix string to search for properties
 ***
 *** @exception OpenGrassException Thrown if initialisation fails, mandatory properties are missing or properties cannot be parsed.
 **/

  public void init(Properties props, String pfix) throws OpenGrassException {
    fromTo = new TreeMap();
    prefix = pfix;
    if ((String) props.get(prefix + ".Att") != null) {
      attribute = (String) props.get(prefix + ".Att");
    }
    else {
      throw new OpenGrassException("ERROR: No attribute specified for " + prefix + ".Att");
    }
    if ((String) props.get(prefix + ".FileName") != null) {
      fileName = (String) props.get(prefix + ".FileName");
    }
    else {
      throw new OpenGrassException("ERROR: No file name specified for " + prefix + ".FileName");
    }
    valueParser = new ValueXMLParser(fileName, 0);
    try {
      valueParser.parse();
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Unable to parse xml file: " + fileName
                               + "\n" + e.getMessage());
    }
    fromTo = valueParser.getAuthData();
    System.err.println("INFO: Value Alias Action [" + attribute + "] " + fromTo.toString());
  }

  /** Aliases the attribute values in the DataObject **/
  public DataObject transformOpenGrassAttributes(DataObject dObject) throws OpenGrassException {
    try {
      String origValue = (String) dObject.getAttributeValue(attribute);
      if (origValue == null) {
        origValue = "";
      }
      if (fromTo.containsKey(origValue)) {
        String newValue = ((ValueReference) fromTo.get(origValue)).getNewValue();
        System.err.println("TRACE: Aliasing attribute [" + attribute + "] " + origValue + " -> " + newValue);

        if (newValue.equals("")) {
          newValue = null;
        }
        dObject.setAttributeValue(attribute, newValue);
      }
    }
    catch (InvalidParameterException e) {
      throw new OpenGrassException("ERROR: Can't get Attribute [" + attribute + "]");
    }
    return dObject;
  }
}






