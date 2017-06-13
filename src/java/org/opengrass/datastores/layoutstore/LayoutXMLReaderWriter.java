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
 **
 ** @ersion  : 1.00.000
 ** Date     : 26/08/2004
 */

package org.opengrass.datastores.layoutstore;

import org.opengrass.templates.*;
import java.lang.System;
import java.util.*;
import org.xml.sax.*;

/**
* Reads and writes info about layout objects from the specified file.
*/

public class LayoutXMLReaderWriter extends XMLReaderWriter implements DataHandler {


  //Objects for SAX parsing
  private Layout currentLayout;
  private String currentLayoutName;
  private String currentPerson;
  private String currentEmail;

/**
* Creates a new Layout object from the specifed file and sets the debug level to the default level (0)
*/
  public LayoutXMLReaderWriter (String f) {
    this(f,0);
  }

/**
* Creates a new Layout object from the specifed file and sets debug to the specified
*/
  public LayoutXMLReaderWriter (String f, int debug_level) {
    super(f, debug_level);
    if (debug > 0) System.err.println("INFO: Initialising LayoutXMLReaderWriter");
  }


/**
* Used in Sax Parsing
*/
  public void startElement( String uri, String name, String qName, Attributes atts ) {
    sb = null;
    sb = new StringBuffer();
    if (debug > 4) { System.err.println("PARSING: Start of element: " + name); }
    if (name.equals("layout")) {
     currentLayoutName = atts.getValue("name");
     currentLayout = new Layout(currentLayoutName, debug);
     currentLayout.setStacked(Boolean.valueOf(atts.getValue("stacked")).booleanValue());
    }
  }

/**
* Used in Sax Parsing
*/
  public void endElement( String uri, String name, String qName ) throws SAXException {

    if (debug > 4) { System.err.println("PARSING: End of element: " + name ); }
    if (name.equals("title")) { currentLayout.setTitle(sb.toString()); }
    else if (name.equals("type_filter")) { currentLayout.setTypeFilter(sb.toString()); }
    else if (name.equals("date_offset")) { currentLayout.setDateOffset(Integer.parseInt(sb.toString())); }
    else if (name.equals("font_size")) { currentLayout.setFontSize(Integer.parseInt(sb.toString())); }
    else if (name.equals("row_count")) { currentLayout.setRowCount(Integer.parseInt(sb.toString())); }
    else if (name.equals("name")) { currentPerson = sb.toString(); }
    else if (name.equals("email")) { currentEmail = sb.toString(); }
    else if (name.equals("person")) { currentLayout.addPerson( currentPerson, currentEmail); }
    else if (name.equals("field")) { currentLayout.addField(sb.toString()); }
    else if (name.equals("layout")) {
     data.put(currentLayoutName, currentLayout);
     if (debug > 2) { System.err.println(currentLayout.toString()); }
    }
  }

/** Returns an xml representation of the layouts */
  public String toXMLString () {
    StringBuffer jb = new StringBuffer("<layouts>\n");
    for (Enumeration e = data.elements(); e.hasMoreElements();) {
      currentLayout = (Layout) e.nextElement();
      jb.append(currentLayout.toXMLString());
    }
    jb.append("</layouts>\n");
    return jb.toString();
  }

  public String toString() {
    return this.toXMLString();
  }

  /** Used in Sax Parsing - can get it to parse if this method is in XMLReaderWriter only???**/
  public void characters ( char[] ch, int start, int length ) {
    int endMarker = start+length;
    for (int i=start; i<endMarker; i++) {
      sb.append( ch[i] );
    }
  }
}