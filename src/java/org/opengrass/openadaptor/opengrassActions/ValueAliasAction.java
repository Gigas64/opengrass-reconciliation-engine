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
*** <br>prefix.AliasN = fromValue toValue
*** <p>e.g. Changes values in BuySell
*** <p>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.ValueAliasAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att     = BuySell
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Alias1  = 2 B
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Alias2  = 1 "S 1"
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Alias3  = 0 S
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Alias4  = NULL "NULL"
*** <p>Note: Alias4 converts a null to the string "NULL"
*** @date 25th June 2002
*** @version 1.00
*** @see.OpenGrassDOAction
***
**/


import org.opengrass.exceptions.OpenGrassException;
import java.util.*;
import org.openadaptor.dataobjects.InvalidParameterException;
import org.openadaptor.dataobjects.DataObject;




public class ValueAliasAction implements OpenGrassDOAction {

  /** Property prefix **/
  private String prefix;
  /** Hash of from - to values **/
  private Hashtable fromTo;
  /** Attribute name **/
  private String attribute;

 /**
 *** Initialise from properties object. See class comment for property values
 ***
 *** @param props  Properties object
 *** @param prefix  Prefix string to search for properties
 ***
 *** @exception OpenGrassException Thrown if initialisation fails, mandatory properties are missing or properties cannot be parsed.
 **/

  public void init(Properties props, String pfix) throws OpenGrassException {
    fromTo = new Hashtable();
    prefix = pfix;
    if ((String) props.get(prefix + ".Att") != null) {
      attribute = (String) props.get(prefix + ".Att");
    }
    else {
      throw new OpenGrassException("ERROR: No attribute specified for " + prefix + ".Att");
    }
    for (int i = 1; props.get(prefix + ".Alias" + i) != null; i++) {
      String  value_str = (String) props.get(prefix + ".Alias" + i);
      AliasParser p = new AliasParser(value_str);
      String  fromValue = p.getNextToken();
      String  toValue = p.getNextToken();
      if (fromValue != null || toValue != null) {
        fromTo.put(fromValue, toValue);
      }
      else {
        throw new OpenGrassException("ERROR:failed to parse property value for "
            + prefix + ".Alias" + i);
      }
    }
    System.err.println("INFO: Value Alias Action [" + attribute + "] " + fromTo.toString());
  }

  /** Aliases the attribute values in the DataObject **/
  public DataObject transformOpenGrassAttributes(DataObject dObject) throws OpenGrassException {
    try {
      String origValue = (String) dObject.getAttributeValue(attribute);
      if (origValue == null) {
        origValue = "_NULL_VALUE";
      }
      if (fromTo.containsKey(origValue)) {
        String newValue = (String) fromTo.get(origValue);
        System.err.println("TRACE: Aliasing attribute [" + attribute + "] " + origValue + " -> " + newValue);

        if (newValue.equals("_NULL_VALUE")) {
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

   /**
   *** simple class to parse alias value properties
   *** Swiped from openAdaptor
   *** org.openadaptor.adaptor.standard.ValueAlias
   *** Thanks guys
   **/

  protected class AliasParser {
    private char[]    buf = null;
    private int     offset = -1;
    private int     len = 0;

    protected AliasParser(String s) {
      buf = s.toCharArray();
      len = s.length();
      offset = 0;
    }
    protected String getNextToken()  {
      StringBuffer sbuf = new StringBuffer();
      boolean   inBraces = false;
      boolean   noBraces = true;
      boolean   foundString = false;
      if (offset == -1) {
        return null;
      }

      // chuck away whitespace
      while (offset < len && (buf[offset] == 9 || buf[offset] == ' ')) {
        offset++;
      }

      // read next token
      while (offset < len) {
        if (!inBraces && buf[offset] == '"')  {
          inBraces = true;
          noBraces = false;
          foundString = true;
        }
        else if (inBraces && buf[offset] == '"')  {
          inBraces = false;
        }
        else if (!inBraces  && (buf[offset] == 9 || buf[offset] == ' '))  {
          break;
        }
        else {
          sbuf.append(buf[offset]);
          foundString = true;
        }
        offset++;
      }
      if (foundString &&!inBraces) {
        String  s = sbuf.toString();
        if (s.equals("NULL") && noBraces) {
          return "_NULL_VALUE";
        }
        else {
          return s;
        }
      }
      else {
        offset = -1;
        return null;
      }
    }
  }
}






