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


import org.opengrass.exceptions.OpenGrassException;
import org.apache.regexp.*;
import java.util.*;
import java.io.*;


/**
 * Reads and writes info about business objects from the specified file.
 */

public class OpenTrackerLogReaderWriter extends File {
    private final static char sep1 = '\u001E';
    private static String SEP = "" + sep1;
    private int debug;
    private Hashtable data;

    /** Class Constructor
     *
     * @param filename of the file being read
     * @param debug_level for information reporting
     */
    public OpenTrackerLogReaderWriter(String filename, int debug_level) {
        super(filename);
        debug = debug_level;
        data = new Hashtable();
        if (debug > 1) {
            System.err.println("INFO: Initialising OpenTrackerLogReaderWriter");
        }
    }

    /**
     * parse
     *
     * @param sync_key String
     * @throws OpenGrassException
     */
    public synchronized void parse(String sync_key) throws OpenGrassException {
        if (!this.exists()) {
            return;
        }
        try {
            String line;
            RE sync_line = new RE("### SyncKey=(.*)");

            BufferedReader in = new BufferedReader(new FileReader(this));
            while ((line = in.readLine()) != null) {
                if (!line.equals("")) {
                    if (sync_line.match(line)) {
                        // get only the version value out of the line
                        String value = sync_line.getParen(1);
                        if (!value.equals(sync_key)) {
                            //this is a dfferent sync number
                            data.clear();
                            throw new OpenGrassException(
                                    "WARNING: The Sync number '" + sync_key +
                                    "' does not match the file Sync number '" +
                                    value + "'");
                        }
                        continue;
                    }

                    // Split the line into tokens
                    RE data_sep = new RE(SEP);
                    String tokens[] = data_sep.split(line);
                    // Put the tokens into a TrackRec Object
                    TrackerAction ta = new TrackerAction(tokens[0],
                            TrackerAction.toValue(tokens[1]),
                            tokens[2], tokens[3], tokens[4], tokens[5]);

                    // add this to the Hashtable
                    data.put(ta.getTrackerKey(), ta);
                }
            }
            in.close();
        } catch (Exception e) {
            throw new OpenGrassException(
                    "ERROR: Problems reading from tracklog.csv file.");
        }
    }

    /**
     * Returns a hash of the data from the given XML file *
     *
     * @return Hashtable
     */
    public Hashtable getData() {
        return data;
    }

    /**
     * Writes the data to file in xml format
     *
     * @param tracker_actions Vector
     * @param sync_key String
     * @throws OpenGrassException
     */
    public synchronized void write(Vector tracker_actions, String sync_key) throws OpenGrassException {
        FileWriter x;
        String sync_header = "";
        // writing to tracker file
        try {
            // check if this is a new file
            if (!this.exists()) {
                sync_header = "### SyncKey=" + sync_key + "\n";
            }
            // ... put the line to the end of the file
            BufferedWriter tout = new BufferedWriter(new FileWriter(this, true));
            // just in case this is a new file
            tout.write(sync_header);
            // run through the hash and add the TrackerAction(s)
            for (Enumeration e = tracker_actions.elements(); e.hasMoreElements(); ) {
                TrackerAction ta = (TrackerAction) e.nextElement();

                tout.write(ta.getTrackerKey() + SEP +
                           TrackerAction.toString(ta.getAction()) + SEP +
                           ta.getField() + SEP + ta.getValue() + SEP +
                           ta.getEditBy() + SEP + ta.getEditDate());

                tout.newLine();
                tout.flush();
            }
            tout.close();

        } catch (Exception e) {
            throw new OpenGrassException(
                    "ERROR - Problems writing to tracklog.csv file. ");

        }
    }
}
