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
*** Predicate for a String type attribute.
*** <p>Intialisation in Adaptor Property file
*** <br>prefix.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.XMLStringPredicate
*** <br>prefix.Att  = Attribute name
*** <br>prefix.Criteria = FileName
*** <p>e.g. Returns true if the Book attribute value matches any values in the imagine_book.xml file
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.XMLStringPredicate
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Att      = Book
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Criteria = ${FILTER_PATH}/imagine_book.xml
*** @date 13th July 2002
*** @version 1.01
*** @see.OpenGrassAttPredicate
***
**/

import java.util.*;
import org.opengrass.exceptions.OpenGrassException;
import org.opengrass.datastores.filterstore.ValueXMLParser;



public class XMLStringPredicate implements OpenGrassAttPredicate {
  /** Attribute to apply the predicate to **/
  private String attribute;
  /** Name of file containing strings **/
  private String fileName;
  /** Hash of strings **/
  private TreeMap strings;
  /** Handler for reading data from the file **/
  private ValueXMLParser valueParser;

  /** Initialiser <br>prefix.Att = att <br>prefix.Criteria = file **/
  public void init(String att, String file) throws OpenGrassException {
    attribute = att;
    fileName = file;
    strings = new TreeMap();
    valueParser = new ValueXMLParser(fileName, 0);
    try {
      valueParser.parse();
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Unable to parse predicate xml file: " + fileName
                               + "\n" + e.getMessage());
    }
    strings = valueParser.getAuthData();
    System.err.println("INFO: XMLStringFile Predicate " + attribute + " = " + fileName);
  }
  /** Returns attribute Name **/
  public String getAttName() {
   return attribute;
  }
  /** Returns true if the attValue matches any of the strings in the file **/
  public boolean matches (String attValue) {
    boolean matchStatus = false;
    if (strings.containsKey(attValue)) {
      matchStatus = true;
    }
    System.err.println("TRACE: (XMLStringFile) " + attribute + " [" + attValue + "] found in [" + fileName + "] " + matchStatus);
    return matchStatus;
  }
}


