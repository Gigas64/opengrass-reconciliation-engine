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

package org.opengrass.templates;

import org.xml.sax.helpers.DefaultHandler;

/**
 * Title:               CommentsHandler
 * Description:         Provides sax callbacks and builds up a hashtable holding comments structure
 * Date:                23rd May 2002
 * @version             1.01.01
 */

import org.opengrass.exceptions.OpenGrassException;
import java.util.*;
import java.io.*;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.*;

/** Reads and writes data from a specified xml file */
public abstract class XMLReaderWriter extends DefaultHandler {

  /** SAX parser associated with this handler */
  private XMLReader reader;
  /** Name of XML File */
  private String fileName;
  /** XML file object */
  private File file;
  /** Debug level */
  protected int debug;
  /** Holds xml element content */
  protected StringBuffer sb;
  /** Holds the data */
  protected Hashtable data;

  /** Constructor */
  public XMLReaderWriter( String fileName_, int debug_ ){
    fileName = fileName_;
    debug = debug_;
    data = new Hashtable();
    if (debug > 0) System.err.println("INFO: Initialising XMLReaderWriter");
  }

  /** Parses the specified file */
  public synchronized void parse()  throws OpenGrassException {
       data = new Hashtable();
    if (debug > 1) { System.err.println("INFO: XMLReaderWriter: PARSING " + fileName ); }
    try {
        file = new File(fileName);
        if (file.canRead()) {
            if (debug > 2) { System.err.println("DEBUG: Opening XML reader" ); }
            reader = new SAXParser();
            reader.setContentHandler( this );
            reader.setErrorHandler( this );
            FileReader fr = new FileReader( file );
            if (debug > 2) { System.err.println("DEBUG: Start parsing XMLReader" ); }
            reader.parse( new InputSource(fr) );
            if (debug > 2) { System.err.println("DEBUG: End parsing XMLReader" ); }
            fr.close();
            if (debug > 2) { System.err.println("DEBUG: Closing XMLReader" ); }
        }
        else if (debug > 0) { System.err.println("INFO: No file exists"); }
    }catch (Exception e) {
      throw new OpenGrassException(e.getMessage());
    }
    if (debug > 3) { System.err.println(this.toString()); }
  }

  /** Used in Sax Parsing */
  public void startDocument () {
    if (debug > 3) { System.err.println("PARSING: Start of document"); }
  }

  /** Used in Sax Parsing */
  public void endDocument () {
    if (debug > 3) { System.err.println("PARSING: End of document"); }
  }

  /** Used in Sax Parsing: Should be overridden in impelementing class */
  public void startElement( String uri, String name, String qName, Attributes atts ) {
  }

  /** Used in Sax Parsing: Should be overriden in implementing class */
  public void endElement( String uri, String name, String qName ) throws SAXException {
  }

  /** Used in Sax Parsing: Should be overriden in implimenting class */
  public void characters ( char[] ch, int start, int length ) {
  }

  /** Returns a hash of the data from the given XML file **/
  public Hashtable getData() {
      return data;
  }

  /** Writes the data to file in xml format */
  public synchronized void write (Hashtable d) throws OpenGrassException {
     if (debug > 0) System.err.println("Writing Layout to " + file.getName());
     data = d;
     try {
        FileWriter writer = new FileWriter(file);
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write(this.toString());
        writer.flush();
        writer.close();
    } catch (Exception e) {
        throw new OpenGrassException(e.getMessage());
    }
  }
}
