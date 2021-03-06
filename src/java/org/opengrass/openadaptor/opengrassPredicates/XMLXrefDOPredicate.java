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

import java.util.*;
import org.opengrass.exceptions.*;
import org.openadaptor.dataobjects.DataObject;
import org.opengrass.datastores.filterstore.ValueXMLParser;


public class XMLXrefDOPredicate extends AbstractDOPredicate {
  /** Hash of strings **/
  private TreeMap strings;
  /** Handler for reading data from the file **/
  private ValueXMLParser xmlXrefHandler;

  protected void setupOperator (String o) throws OpenGrassException {
    if (! o.equals("==") && ! o.equals("!=")) {
      throw new OpenGrassException("ERROR: Operator must be either [==] or [!=]");
    }
    operator = o;
  }

  /** parse array notation in to an array of strings **/
  protected void setupCriteria( String c) throws OpenGrassException {
    strings = new TreeMap();
    criteria = c;
    xmlXrefHandler = new ValueXMLParser(criteria, 0);
    try {
      xmlXrefHandler.parse();
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Unable to parse predicate xml file: " + criteria
                               + "\n" + e.getMessage());
    }
    strings = xmlXrefHandler.getAuthData();
    predicateClass = "XMLXrefPredicate";
  }

  protected void setupAttribute(String a) {
    attribute = a;
  }

  protected boolean test(String atValue, DataObject dataObj) throws OpenGrassException {
    boolean matches = false;
    if (atValue == null || atValue.length() < 1) {
    atValue = "";
    }
    if (strings.containsKey(atValue)) {
      matches = true;
    }
    // toggle
    if (operator.equals("!=")) {
      if (matches) { matches = false;}
      else { matches = true; }
    }
    return matches;
  }
}


