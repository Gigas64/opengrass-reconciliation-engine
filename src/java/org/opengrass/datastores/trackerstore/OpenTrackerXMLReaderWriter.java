package org.opengrass.datastores.trackerstore;

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
 ** Version: 1.03.000
 ** Date:  30/07/2004
 */


import org.opengrass.templates.DataHandler;
import org.opengrass.templates.XMLReaderWriter;
import java.util.*;
import org.xml.sax.*;
import org.opengrass.exceptions.OpenGrassException;


/**
 * Reads and writes info about business objects from the specified file.
 */

public class OpenTrackerXMLReaderWriter extends XMLReaderWriter implements DataHandler {

    // Debug level
    private int debug;

    //the Sync Reference key
    String sync_key;

    // Flags for elements
    private boolean rec_flag;
    private boolean keys_flag;
    private boolean matches_flag;
    private boolean infos_flag;
    private boolean trackers_flag;
    private boolean refs_flag;
    private boolean key_flag;
    private boolean match_flag;

    // Values from attributes
    private String _field;
    private String _status;
    private String _diff;
    private String _system;
    private String _edit_by;
    private String _edit_date;

    // the current reconciliation Item
    private OpenTracker _current_tracker;

    /** Class Constructor
     *
     * @param filename of the file being read
     * @param debug_level for information reporting
     */
    public OpenTrackerXMLReaderWriter(String filename, int debug_level) {
      super(filename, debug_level);
      debug = debug_level;
      if (debug > 1) System.err.println("INFO: Initialising OpenTrackerXMLReaderWriter");
      // for consitency default all the flags
      rec_flag = false;
      keys_flag = false;
      matches_flag = false;
      infos_flag = false;
      trackers_flag = false;
      refs_flag = false;
      key_flag = false;
      match_flag = false;
    }


    /**
     * Identifies the Start Element used in SAX parsing
     *
     * @param uri String
     * @param name String
     * @param qName String
     * @param atts Attributes
     */
    public void startElement( String uri, String name, String qName, Attributes atts ) {
      sb = null;
      sb = new StringBuffer();
      if (debug > 3) {
          System.err.println("PARSING: Start of element: " + name);
          System.err.println("PARSING: QName           : " + qName);
          System.err.println("PARSING: Attr            : " + atts.toString());
      }
      // set the sync_key
      if(name.equals("breaks_tracker")) {
          if(atts.getValue("sync_key") != null && atts.getValue("sync_key").length() > 0) {
              sync_key = atts.getValue("sync_key");
          }
          else {
              sync_key = "";
          }
      }
      if (name.equals("track")) {
        rec_flag=true;
        _current_tracker = new OpenTracker(atts.getValue("key"),atts.getValue("type"));
      }
      else if (name.equals("keys")) { keys_flag=true; }
      else if (name.equals("matches")) { matches_flag=true; }
      else if (name.equals("infos")) { infos_flag=true; }
      else if (name.equals("trackers")) { trackers_flag=true; }
      else if (name.equals("references")) { refs_flag=true; }


      else if (rec_flag && ( keys_flag || matches_flag || infos_flag || trackers_flag || refs_flag)) {
        if(name.equals("key") || name.equals("match") || name.equals("info") || name.equals("tracker") || name.equals("reference")) {
          key_flag = true;
          _field = "";
          if(atts.getValue("field") != null && atts.getValue("field").length() > 0) {
            _field = atts.getValue("field");
          }
          else { _field = ""; }
        }
        if(name.equals("match")) {
          // set the match flag
          match_flag = true;
          if(atts.getValue("status") != null && atts.getValue("status").length() > 0) {
            _status = atts.getValue("status");
          }
          else { _status = ""; }
          if(atts.getValue("difference") != null && atts.getValue("difference").length() > 0) {
            _diff = atts.getValue("difference");
          }
          else { _diff = ""; }
        }
        if(name.equals("info")) {
          if(atts.getValue("system") != null && atts.getValue("system").length() > 0) {
            _system = atts.getValue("system");
          }
          else { _system = ""; }
        }
        if(name.equals("reference")) {
          if(atts.getValue("edit_by") != null && atts.getValue("edit_by").length() > 0) {
            _edit_by = atts.getValue("edit_by");
          }
          else { _edit_by = ""; }
          if(atts.getValue("edit_date") != null && atts.getValue("edit_date").length() > 0) {
            _edit_date = atts.getValue("edit_date");
          }
          else { _edit_date = ""; }
        }
      }
    }

    /**
     * Identifies the closing tag used in Sax Parsing
     *
     * @param uri String
     * @param name String
     * @param qName String
     * @throws SAXException
     */
    public void endElement( String uri, String name, String qName ) throws SAXException {

      if (debug > 4) { System.err.println("PARSING: End of element: " + name); }

      if (name.equals("track")) {
        data.put(_current_tracker.getTrackerKey(), _current_tracker);
        if (debug > 2) { System.err.println("Adding new tracker [" + _current_tracker.getTrackerKey() + "]"); }
        _current_tracker = null;
        rec_flag = false;
      }
      else if(rec_flag) {
        if(keys_flag && key_flag && name.equals("primary")) {
          _current_tracker.addField(new OpenField(_field,OpenField.KEY,OpenField.PRIMARY,sb.toString()));
        }
        else if(keys_flag && key_flag && name.equals("secondary") && (_current_tracker.getTrackerType()).equals("")) {
          _current_tracker.addField(new OpenField(_field,OpenField.KEY,OpenField.SECONDARY,sb.toString()));
          // cancel the key flag
          key_flag = false;
        }
        else if(matches_flag && match_flag && name.equals("primary")) {
          _current_tracker.addField(new OpenField(_field,OpenField.MATCH,OpenField.PRIMARY,sb.toString()));
        }
        else if(matches_flag && match_flag && name.equals("secondary")) {
          _current_tracker.addField(new OpenField(_field,OpenField.MATCH,OpenField.SECONDARY,sb.toString()));
          // cancel the match flag
          match_flag = false;
        }
        else if(infos_flag && name.equals("info")) {
          _current_tracker.addField(new OpenField(_field,OpenField.INFO,_system.charAt(0),sb.toString()));
        }
        else if(trackers_flag && name.equals("tracker")) {
          _current_tracker.addField(new OpenField(_field,OpenField.TRACKER,OpenField.NEUTRAL,sb.toString()));
        }
        else if(refs_flag && name.equals("reference")) {
          _current_tracker.addField(new OpenFieldEdit(_field,OpenField.REF,OpenField.NEUTRAL,sb.toString(),_edit_by,_edit_date));
        }
        else if (name.equals("keys")) { keys_flag=false; }
        else if (name.equals("matches")) { matches_flag=false; }
        else if (name.equals("infos")) { infos_flag=false; }
        else if (name.equals("trackers")) { trackers_flag=false; }
        else if (name.equals("references")) { refs_flag=false; }
      }
    }

    /**
     * Used in Sax Parsing to obtain the content of a particular tag
     *
     * @param ch char[]
     * @param start int
     * @param length int
     */
    public void characters( char[] ch, int start, int length ) {
        int endMarker = start+length;
        for (int i=start; i<endMarker; i++) {
            sb.append( ch[i] );
        }
    }

    /**
     * getSyncKey
     */
    public String getSyncKey() {
        return sync_key;
    }

    /**
     * write
     *
     * @param d Hashtable
     * @throws OpenGrassException
     */
    public void write (Hashtable d) throws OpenGrassException {
        // Do nothing as we don't ever want to write to the Tracker file
    }

    /**
     * Returns an String representation of the data element
     *
     * @return String
     */
    public String toString() {
        StringBuffer jb = new StringBuffer();
        for (Enumeration e = data.elements(); e.hasMoreElements();) {
            _current_tracker = (OpenTracker) e.nextElement();
            jb.append(_current_tracker.toString());
        }
        return jb.toString();
    }

}
