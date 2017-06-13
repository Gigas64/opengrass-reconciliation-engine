package org.opengrass.datastores.referencestore;

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
*
* @Date:         10th Jan 2003
* @Version       1.00.02
*/

import org.opengrass.exceptions.OpenGrassException;
import java.util.*;
import java.io.*;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLRecFinder {

  /** Name of business area */
  private String business;

  /** Title of business  */
  private String business_title;

  /** Name of Primary  */
  private String primary_name;

  /** Name of Secondary  */
  private String secondary_name;

  /** List of allKeys found in rec */
  private TreeSet allKeys;

  /** Current search key */
  private String currentSearchKey;

  /** Vector of rec objects that relate to the search key */
  private Vector recs;

  /** Vector containing Vectors of records for previous search keys*/
  private Vector previousFoundRecs;

  /** Max no of rec Vectors to store in the previousFoundRecs Vector */
  private static int noPreviousFoundRecs = 15;

  /** Name of the match file */
  private String matchFileName;

  /** Name of the breaks file */
  private String breaksFileName;

  /** Represents the match file */
  private File matchFile;

  /** Represents the breaks file */
  private File breaksFile;

  /** Date the match file was last modfied */
  private long matchFileModDate;

  /** Date the breaks file was last modfied */
  private long breaksFileModDate;

  /** Debug level */
  private int debug;

  /** Object for parseing the rec files */
  private RecParser recParser;

  /** Constructor */
  public XMLRecFinder(String business_area, int debug_level){
    debug = debug_level;
    business = business_area;
    allKeys = new TreeSet();
    recs = new Vector();
    previousFoundRecs = new Vector();
    if (debug > 0) System.err.println("INFO: Initialising XMLRecFinder");
  }

  /** Sets up the parser and parses the rec files for the first time */
  public void parse() throws OpenGrassException {
    // Create file paths
    String OPENGRASS_HOME = System.getProperty("OPENGRASS_HOME");
    matchFileName = OPENGRASS_HOME + "/ertba/" + business + "/data/ert_results/" + business + "_matches.xml";
    breaksFileName = OPENGRASS_HOME + "/ertba/" + business + "/data/ert_results/" + business + "_breaks.xml";
    try {
      // Create File objects
      breaksFile = new File(breaksFileName);
      matchFile = new File(matchFileName);
      // Set up the RecParser
      recParser = new RecParser();
      recParser.setUp();
      recParser.parseFiles();
    }
    catch (Exception e ){
      throw new OpenGrassException(e.getMessage());
    }
  }

  /** Returns a the title of the business */
  public String getBusinessTitle() { return business_title; }

  /** Returns a the name of the primary */
  public String getPrimary() { return primary_name; }

  /** Returns a the name of the secondary */
  public String getSecondary() { return secondary_name; }

  /** Returns a sorted list of all the keys found in the rec files */
  public TreeSet getKeys()  throws OpenGrassException {
    if (debug > 0) System.err.println("Info: Getting Keys");
    // If either of the files have been modified reparse them
    if ( breaksFileModDate != breaksFile.lastModified()
      || matchFileModDate != matchFile.lastModified()) {
      recParser.parseFiles();
    }
    if (debug > 2) System.err.println(allKeys);
    return allKeys;
  }

  /** Returns a Vector of Rec objects found in the rec files that have the specifed key */
  public Vector getRecs(String key)  throws OpenGrassException {
    if (debug > 0) System.err.println("Info: Searching for " + key);
    currentSearchKey = key;
    recs.removeAllElements();
    boolean alreadyFound = false;
    // If either of the recs files have been modified
    if ( breaksFileModDate != breaksFile.lastModified()
      || matchFileModDate != matchFile.lastModified()) {
      //If either of the rec files have be modified reset the Vector of previous search results
      previousFoundRecs.removeAllElements();
    }
    // Were any of the previous searches for the current search key
    for (int i=0; i<previousFoundRecs.size(); i++) {
      if (((Vector) previousFoundRecs.get(i)).size() > 0) {
        Rec tmp = (Rec) ((Vector) previousFoundRecs.get(i)).firstElement();
        if (tmp.getKey().equals(currentSearchKey)) {
          //Previous search matches
          recs = (Vector) previousFoundRecs.get(i);
          alreadyFound = true;
          break;
        }
      }
    }
    // If there is no previous search for the current search key
    if (! alreadyFound) {
      // Parse the files to find key matching recs
      recParser.parseFiles();
      // Add the recs the the Vector of previous search results (FIFO with max no 15)
      if (previousFoundRecs.size() <= noPreviousFoundRecs) {
        previousFoundRecs.add(recs);
      }
      else {
        previousFoundRecs.remove(0);
        previousFoundRecs.add(recs);
      }
    }
    if (debug > 2) System.err.println(recs);
    currentSearchKey = null;
    return recs;
  }

  /** Returns a Vector of Rec objects found in the rec files that have the key immediately after to the specified one*/
  public Vector getNext(String key) throws OpenGrassException {
  String nextKey;
  if (key.equals(allKeys.last())) {
    nextKey = (String) allKeys.first();
    }
    else {
      nextKey = (String) allKeys.tailSet(key+"\0").first();
    }
  return this.getRecs(nextKey);

  }
  /** Returns a Vector of Rec objects found in the rec files that have the key immediately previous to the specified one*/


  public Vector getPrev(String key) throws OpenGrassException {
  String previousKey;
  if (key.equals(allKeys.first())) {
    previousKey = (String) allKeys.last();
    }
    else {
      previousKey = (String) allKeys.headSet(key).last();
    }
    return this.getRecs(previousKey);
  }

  public static void main(String[] args) throws OpenGrassException {
    XMLRecFinder rf = new XMLRecFinder("TestRec", 3);
    rf.parse();
    rf.getKeys();
    rf.getRecs("151660");
  }

  /** Internal class for parsing the recs files */
  private class RecParser extends DefaultHandler {

    /** SAX parser associated with this handler */
    private XMLReader reader;
    /** Used in SAX parsing */
    /** Used in SAX parsing */
    private Rec currentRec;
    /** Used in SAX parsing */
    private String currentRecKey;
    /** Used in SAX parsing */
    private Key currentKey;
    /** Used in SAX parsing */
    private Match currentMatch;
    /** Used in SAX parsing */
    private Info currentInfo;
    /** Used in SAX parsing */
    private StringBuffer sb;
    /** Used in SAX parsing */
    private boolean searchKeyFound;

    /** Sets up the RecParser ready to parse the rec files */
    private void setUp () {
      reader = new SAXParser();
      reader.setContentHandler( this );
      reader.setErrorHandler( this );
    }

    /** Parses the rec files */
    private void parseFiles () throws OpenGrassException {
      allKeys = new TreeSet();
      breaksFileModDate = breaksFile.lastModified();
      matchFileModDate = matchFile.lastModified();
      try {
        FileReader fr = new FileReader( breaksFile );
        reader.parse( new InputSource(fr) );
        fr.close();
        fr = new FileReader( matchFile );
        reader.parse( new InputSource(fr) );
        fr.close();
      }catch (Exception e) {
        throw new OpenGrassException(e.getMessage());
      }
    }

    /** Used in Sax Parsing */
    public void startDocument () {
      if (debug > 3) { System.err.println("PARSING: Start of document"); }
    }

    /** Used in Sax Parsing */
    public void endDocument () {
      if (debug > 3) { System.err.println("PARSING: End of document"); }
    }

    /** Used in Sax Parsing */
    public void startElement( String uri, String name, String qName, Attributes atts ) {
      sb = new StringBuffer();
      if (debug > 4) { System.err.println("PARSING: Start of element: " + name); }
      if (name.equals("breaks")) {
    business_title = atts.getValue("business");
        primary_name = atts.getValue("primary");
        secondary_name = atts.getValue("secondary");
    }
      if (name.equals("rec")) {
       allKeys.add(atts.getValue("key"));
      }
      if (currentSearchKey != null) {
        if (name.equals("rec")) {
          currentRecKey = atts.getValue("key");
          if (currentRecKey.equals( currentSearchKey)) {
            searchKeyFound = true;
            currentRec = new Rec (currentRecKey, atts.getValue("type"), atts.getValue("first_seen"));
          }
          else {
            searchKeyFound = false;
          }
        }
        if (searchKeyFound ) {
          if (name.equals("key")) {
            currentKey = new Key();
            String f = atts.getValue("field");
            currentKey.setField(f);
          }
          if (name.equals("match")) {
            currentMatch = new Match();
            String f = atts.getValue("field");
            currentMatch.setField(f);
            currentMatch.setStatus(atts.getValue("status"));
            String s = atts.getValue("difference");
            currentMatch.setDifference(s);
          }
          if (name.equals("info")) {
            currentInfo = new Info ();
            String f = atts.getValue("field");
            String s = atts.getValue("system");
            currentInfo.setSystem(s);
            currentInfo.setField(f);
          }
        }
      }

    }

    /** Used in Sax Parsing */
    public void endElement( String uri, String name, String qName ) throws SAXException {
      if (debug > 4) { System.err.println("PARSING: End of element: " + name); }
      if (searchKeyFound) {
        if (name.equals("rec")) {
          recs.add(currentRec);
          currentRec = null;
          searchKeyFound = false;
        }
        if (name.equals("key")) {
          currentKey.setValue(sb.toString());
          currentRec.addKey(currentKey);
          currentKey = null;
        }
        if (name.equals("primary")) {
          currentMatch.setPrimary(sb.toString());
        }
        if (name.equals("secondary")) {
          currentMatch.setSecondary(sb.toString());
        }
        if (name.equals("match")) {
          currentRec.addMatch(currentMatch);
        }
        if (name.equals("info")) {
          currentInfo.setValue(sb.toString());
          currentRec.addInfo(currentInfo);
          currentInfo = null;
        }
      }
    }

    /** Used in Sax Parsing **/
    public void characters ( char[] ch, int start, int length ) {
      int endMarker = start+length;
      for (int i=start; i<endMarker; i++) {
        sb.append( ch[i] );
      }
    }
  }
}
