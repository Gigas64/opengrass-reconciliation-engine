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

import java.io.File;
import java.util.*;

import org.opengrass.datastores.gpropstore.Gprops;
import org.opengrass.datastores.gpropstore.GpropsStore;
import org.opengrass.exceptions.OpenGrassException;

/** Class Overview:
 ** This provides the store for all the tracker objects and manages their
 ** manipulation. It provides the main methods and functions required by
 ** an interfacing component.
 **
 ** @version 1.00 - 11/10/2004
 ** @author  Darryl Oatridge
 **
 */

public class OpenTrackerStore {

    /** Debug level */
    private int debug;
    //** the sync_key for the tracker_file */
    private String sync_key;

    /** All the fields that make up the tracker */
    private Hashtable _tracker_store;

    /** the breaks file */
    private String tracker_file_path;
    /** the breaks file */
    private File _tracker_log;
    /** the date associated with the breaks file */
    private long _tracker_modified;
    /** the handler for reading and writing to to tracker log */
    private OpenTrackerLogReaderWriter log_handler;

    /** Class constructor
     *
     * @param.opengrass_path the path of the GRASS home root
     * @param gprops provides all the Gprops values
     * @param debug_level primative int debug level setting
     */
    public OpenTrackerStore(String opengrass_path, Gprops gprops, int debug_level) {
        debug = debug_level;
        tracker_file_path = opengrass_path + File.separator + "ertba" +
                            File.separator + gprops.getBusinessName() +
                            File.separator + "data" + File.separator + "4_tracked" +
                            File.separator + gprops.getBusinessName();
    }

    /**
     * parse Loads all the files and prepares the tracker store
     *
     * @throws OpenGrassException
     */
    private void parse() throws OpenGrassException {
        if (_tracker_store == null) {
            _tracker_store = new Hashtable();
        } else {
            _tracker_store.clear();
        }
        // make this a File object so we can check on changes
        File _tracker_file = new File(tracker_file_path + "_tracked.xml");
        // Take out all the current tracker items
        OpenTrackerXMLReaderWriter xml_file_handler =
            new OpenTrackerXMLReaderWriter(_tracker_file.getAbsolutePath(), debug);
        xml_file_handler.parse();
        _tracker_store = xml_file_handler.getData();
        sync_key = xml_file_handler.getSyncKey();
        _tracker_modified = _tracker_file.lastModified();

        // Get any action that might need applying
        log_handler = new OpenTrackerLogReaderWriter(
            tracker_file_path + "_tracklog.csv", debug);
        log_handler.parse(sync_key);
        // now run through the logs and change the data
        for (Enumeration e = log_handler.getData().elements(); e.hasMoreElements(); ) {
            TrackerAction ta = (TrackerAction) e.nextElement();
            try {
                OpenTracker t = (OpenTracker) _tracker_store.get(ta.getTrackerKey());
                t.applyTrackerAction(ta);
            } catch (Exception x) {
                    throw new OpenGrassException("Exception while parsing the trackerlog: " + x.getMessage());
            }
        }
        return;
    }

    /**
     * getTrackers
     *
     * @param user_request Request
     * @return Vector
     * @todo
     */
    public Vector getTracker(Request user_request) {
        Vector return_vector= new Vector();
        return(return_vector);
    }


    /**
     * applyAction
     *
     * @param tracker_actions Vector
     */
    public void applyAction(Vector tracker_actions) {
        try {
            log_handler.write(tracker_actions, sync_key);
        } catch (Exception e) {

        }
    }


    /** getTrackerCount
     * Returns the number of OpenTracker currently in the Tracker Store
     *
     * @return int relating the the hash count
     */
    public int getTrackerCount() {
        return (_tracker_store.size());
    }

    /** getTracker
     * Returns the OpenTracker object from the internal Hashtable referenced
     * by the tracker key parameter passed.
     *
     * @param tracker_key The tracker_key used to retrieve the OpenTracker Object
     * @return OpenTracker Object or null if not found
     * @see OpenTracker
     */
    public OpenTracker getTracker(String tracker_key) {
        return ((OpenTracker) _tracker_store.get(tracker_key));
    }

    /**
     * setTracker adds the OpenTracker object to the Tracker Store. The
     * tracker_key is used as the reference.
     *
     * @param tracker The tracker_key used to retrieve the OpenTracker Object
     * @return the previous OpenTracker of the specified key, or null if it did
     *   not have one.
     * @see OpenTracker
     */
    public OpenTracker addTracker(OpenTracker tracker) {
        return ((OpenTracker) _tracker_store.put(tracker.getTrackerKey(),
                                                 tracker));
    }

    private SortedMap getDefinedSort( Request r, Vector sort, boolean sort_type ) throws OpenGrassException   {

       SortedMap st;

       if ( r.is_DescSortType() ) {
           st = Collections.synchronizedSortedMap( new TreeMap(Collections.reverseOrder()) );
       }else{
           st = Collections.synchronizedSortedMap( new TreeMap() );
       }
       return(st);
   }

   /** toString
     * returns the full Header content.
     *
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("Tracker Store:\n");

        for (Enumeration e = _tracker_store.elements(); e.hasMoreElements(); ) {
            OpenTracker t = (OpenTracker) e.nextElement();
            sb.append(t.toString() + "\n");
        }

        return (sb.toString());
    }

    /**
     * Test Harness for the tracker store interface and its classes
     *
     * @param args String[]
     */
    public static void main(String args[]) {
        GpropsStore gstore = null;
        Gprops gprops = null;
        //Create an instance of the Gprops
        try {
            gstore = new GpropsStore(args[0], "TestRec",0);
        } catch (Exception e) {
            System.err.println("FATAL: Unable to initialise Gprops " +
                               e.getMessage());
        }

        try {
            gprops = gstore.getGprops();
        } catch (OpenGrassException e) {
            System.err.println("FATAL: Unable to get Gprops from GpropsStore");
        }

        OpenTrackerStore ts = new OpenTrackerStore(args[0], gprops, 3);
        try {
            ts.parse();
        } catch (OpenGrassException e) {
            System.err.println("FATAL: Unable to parse Tracker Store");
        }
        System.out.println(ts.toString());
        //Vector v = new Vector();
        //v.add(new TrackerAction("10000001",TrackerAction.TEXT_REPLACE,"Comment","This is a test","Darryl Oatridge","12/01/2004"));
        //v.add(new TrackerAction("10000001",TrackerAction.VALUE_RESET,"Reviewer","David James","Darryl Oatridge","12/01/2004"));
        //ts.applyAction(v);
    }

}
