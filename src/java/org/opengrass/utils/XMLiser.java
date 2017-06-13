package org.opengrass.utils;

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
** Provides various static methods usefull for xml-isation of data
** Version: 2.00.0
** Date: 5th August 2002
*/


public class XMLiser {

  /** Excapes characters in a string that are illegal in XML */
  public static String escapeChars (String s) {
    char oldString [] = s.toCharArray();
    StringBuffer escapedString = new StringBuffer();
    for (int i=0; i<s.length(); i++) {
      if (oldString[i] == '&') { escapedString.append("&amp;"); }
      else if (oldString[i] == '\"') { escapedString.append("&quot;"); }
      else if (oldString[i] == '\'') { escapedString.append("&apos;"); }
      else if (oldString[i] == '<') { escapedString.append("&lt;"); }
      else if (oldString[i] == '>') { escapedString.append("&gt;"); }
      else { escapedString.append(oldString[i]); }
    }
    return escapedString.toString();
  }

  /** Excapes characters in a string that are illegal in XML */
  public static String escapeCharsForKey (String s) {
    char oldString [] = s.toCharArray();
    StringBuffer escapedString = new StringBuffer();
    for (int i=0; i<s.length(); i++) {
      if (oldString[i] == '&') { escapedString.append("&amp;"); }
      else if (oldString[i] == '\"') { escapedString.append("&quot;"); }
      else if (oldString[i] == '<') { escapedString.append("&lt;"); }
      else if (oldString[i] == '>') { escapedString.append("&gt;"); }
      else { escapedString.append(oldString[i]); }
    }
    return escapedString.toString();
  }
}
