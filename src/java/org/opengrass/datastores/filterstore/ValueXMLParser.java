package org.opengrass.datastores.filterstore;

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
 * Title:               ValueXMLParser
 * Date:                23rd May 2002
 * @version             1.00.00
 */


import org.opengrass.exceptions.OpenGrassException;
import java.util.*;
import java.io.*;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

/** Reads and writes data from a specified xml file */
public class ValueXMLParser extends DefaultHandler {

  /** SAX parser associated with this handler */
  private XMLReader _reader;
  /** Name of XML File */
  private String _file_name;
  /** XML file object */
  private File _file;
  /** Debug level */
  protected int _debug;
  /** Holds all the data */
  protected TreeMap _all_data;
  /** Holds authorised data */
  protected TreeMap _auth_data;

  /** Holds xml element content */
  protected StringBuffer sb;
  protected boolean current_auth;
  protected String current_auth_id;
  protected String current_value;
  protected String current_new_value;
  protected String current_text;
  protected String current_user_id;
  protected String current_date;


  /** Constructor */
  public ValueXMLParser( String file_name, int debug ){
    _file_name = file_name;
    _debug = debug;
    if (_debug > 0) System.err.println("INFO: Initialising XMLReaderWriter");
  }

  /** Parses the specified file */
  public synchronized void parse()  throws OpenGrassException {
    _all_data = new TreeMap();
    _auth_data = new TreeMap();
    if (_debug > 1) { System.err.println("INFO: XMLReaderWriter: PARSING " + _file_name ); }
    try {
        _file = new File(_file_name);
        if (_file.canRead()) {
            _reader = new SAXParser();
            _reader.setContentHandler( this );
            _reader.setErrorHandler( this );
            FileReader fr = new FileReader( _file );
            _reader.parse( new InputSource(fr) );
            fr.close();
        }
        else {
      write("<value_references>\n</value_references>");
      if (_debug > 0) { System.err.println("INFO: No file exists"); }
    }
    }catch (Exception e) {
      throw new OpenGrassException("ERROR: " + e.getMessage());
    }
  }

  /** Used in Sax Parsing */
  public void startDocument () {
    if (_debug > 3) { System.err.println("PARSING: Start of document"); }
  }

  /** Used in Sax Parsing */
  public void endDocument () {
    if (_debug > 3) { System.err.println("PARSING: End of document"); }
  }

  /** Used in Sax Parsing: Should be overridden in impelementing class */
  public void startElement( String uri, String name, String qName, Attributes atts ) {
    sb = new StringBuffer();

    // load the attribute 'auth' and 'auth_id'
    if(name.equals("value_reference")) {

      if(atts.getValue("auth")  != null && atts.getValue("auth").equals("false"))
        current_auth = false;
      else
        current_auth = true;
      current_auth_id = atts.getValue("auth_id");
    }
  }

  /** Used in Sax Parsing: Should be overriden in implementing class */
  public void endElement( String uri, String name, String qName ) throws SAXException {
    if (name.equals("value_reference")) {
      _all_data.put(current_value, new ValueReference(current_value, current_new_value, current_text,
                                         current_user_id, current_date, current_auth,current_auth_id));
      if(current_auth) {
        _auth_data.put(current_value, new ValueReference(current_value, current_new_value, current_text,
                                         current_user_id, current_date, current_auth,current_auth_id));
      }
      current_auth = true;
      current_auth_id = null;
      current_value = null;
      current_new_value = null;
      current_text = null;
      current_user_id = null;
      current_date = null;
    }
    else if (name.equals("value")) {  current_value = sb.toString(); }
    else if (name.equals("new_value")) {  current_new_value = sb.toString(); }

    else if (name.equals("date")) {  current_date = sb.toString(); }
    else if (name.equals("user_id")) {  current_user_id = sb.toString(); }
    else if (name.equals("text")) {  current_text = sb.toString(); }
  }

  /** Used in Sax Parsing: Should be overriden in implimenting class */
  public void characters ( char[] ch, int start, int length ) {
    int endMarker = start+length;
    for (int i=start; i<endMarker; i++) {
      sb.append( ch[i] );
    }
  }

  /** Returns a hash of the data from the given XML file **/
  public TreeMap getData() {
      return _all_data;
  }

  /** Returns a hash of the data from the given XML file **/
  public TreeMap getAuthData() {
      return _auth_data;
  }

  /** Writes the data to file in xml format */
  public synchronized void write (String s) throws OpenGrassException {
  if (_debug > 0) System.err.println("Writing Filters to " + _file.getName());
    try {
      FileWriter writer = new FileWriter(_file);
      writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      writer.write(s);
      writer.flush();
      writer.close();
    } catch (Exception e) {
      throw new OpenGrassException(e.getMessage());
    }
  }
}
