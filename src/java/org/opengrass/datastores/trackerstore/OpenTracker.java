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

import org.opengrass.exceptions.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

/** Class Overview:
 **
 **
 **
 ** @version 1.00 - 11/10/2004
 ** @author  Darryl Oatridge
 **
 */
public class OpenTracker {
    public static String Break = "Break";
    public static String PrimaryOneSided = "PrimaryOneSided";
    public static String SecondaryOneSided = "SecondaryOneSided";
    public static String PrimaryExcluded = "PrimaryExcluded";
    public static String SecondaryExcluded = "SecondaryExcluded";
    public static String ForcedMatch = "ForcedMatch";
    public static String ForcedBreak = "ForcedBreak";


    /** The Tracker key */
    private String t_key;
    /** The Tracker type (Break etc..) */
    private String t_type;
    /** All the fields that make up the tracker */
    private Hashtable tracker_fields;
    /** duplicate count for this tracker */
    public short _dup_count;
    /** the match status for the matched fields */
    public int _match_status;


    /** Class constructor
     *
     * @param key for the OpenTracker Object
     * @param type of the OpenTracker Object
     */
    public OpenTracker(String key, String type) {
        tracker_fields = new Hashtable();
        t_key = key;
        t_type = type;
        _dup_count = 0;
        _match_status = 0;
    }

    /** getFieldCount
     * Returns the number of fields currently in the field hashtable
     *
     * @return int relating the the hash count
     */
    public int getFieldCount() {
        return (tracker_fields.size());
    }

    /** getField
     * Returns the OpenField object from the field Hashtable referenced
     * by the name parameter passed.
     *
     * @param name The hashtable key used to retrieve the OpenField Object
     * @return OpenField Object or null if not found
     * @see OpenField
     */
    public OpenField getField(String name) {
        return ((OpenField) tracker_fields.get(name));
    }

    /**
     * addField adds the OpenField object to the field hashtable. The OpenField
     * refernce is used as the key.
     *
     * @param field The hashtable key used to retrieve the OpenField Object
     * @see OpenField
     */
    public void addField(OpenField field) {
        // add the field if it is unique
        if (!tracker_fields.containsKey(field.getFieldRef())) {
            tracker_fields.put(field.getFieldRef(), field);
        }
    }

    /** getTrackerKey
     * Returns the t_key String
     *
     * @return t_key String
     */
    public String getTrackerKey() {
        return (t_key);
    }

    /** getTrackerType
     * Returns the t_type String
     *
     * @return t_type String
     */
    public String getTrackerType() {
        return (t_type);
    }

    /**
     * applyTrackerAction A tracker action can be applied to this Open Tracker
     * Object, only allows replacement of text
     *
     * @param ta TrackerAction
     * @throws TrackerActionNotFoundException,ClassCastException,NoSuchElementException
     */
    public void applyTrackerAction(TrackerAction ta) throws Exception {

        if(ta.getAction() != TrackerAction.TEXT_REPLACE && ta.getAction() != TrackerAction.VALUE_RESET) {
            throw new TrackerActionNotFoundException("WARNING: Tracker action '"
                                                     + TrackerAction.toString(ta.getAction()) +
                                                     "' cannot be applied in OpenTracker.applyTrackerAction");
        }
        OpenFieldEdit ofe;
        try {
            ofe = (OpenFieldEdit) tracker_fields.get(OpenFieldEdit.genFieldRef(ta.getField()));
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Tracker Field '"
                                         + ta.getField() +
                                         "' is not editable in Tracker "
                                         + this.getTrackerKey());
        }
        if (ofe == null) {
            throw new NoSuchElementException("Tracker Field '"
                                             + ta.getField() +
                                             "' was no found in Tracker "
                                             + this.getTrackerKey());
        }
        ofe.setText(ta.getValue(), ta.getEditBy(), ta.getEditDate());
        if(tracker_fields.containsKey(OpenField.genFieldRef(OpenField.TRACKER,OpenField.NEUTRAL,"LastEdit"))) {
            ((OpenField)tracker_fields.get(OpenField.genFieldRef(OpenField.TRACKER,OpenField.NEUTRAL,"LastEdit"))).setText("0");
        }
    }

    /** toString
     * returns the full Tracker content.
     *
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("Tracker key " + t_key + " [" +
                                           t_type + "] :\n");

        for (Enumeration e = tracker_fields.elements(); e.hasMoreElements(); ) {
            Object o = e.nextElement();
            if (o instanceof OpenFieldHeader) {
                sb.append(((OpenFieldHeader) o).toString() + "\n");
            } else if (o instanceof OpenFieldEdit) {
                sb.append(((OpenFieldEdit) o).toString() + "\n");
            } else {
                sb.append(((OpenField) o).toString() + "\n");
            }
        }

        return (sb.toString());
    }

}
