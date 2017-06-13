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

import java.lang.reflect.Array;

public class TrackerAction {

    // the allowed actions
    public static final short NOT_FOUND      = 0;
    public static final short TEXT_REPLACE   = 1;
    public static final short VALUE_RESET    = 2;
    public static final short FORCE_WITH     = 3;
    private static String[] action_string =
            { "NO_ACTION","REPLACE","RESET","FORCE" };


    private String t_key;
    private short action;
    private String field;
    private String value;
    private String edit_by;
    private String edit_date;


    /**
     * TrackerAction Used to define actions to be applied to the Tracker Store
     *
     * @param t_key String
     */
    public TrackerAction(String t_key) {
        this.t_key=t_key;
    }

    public TrackerAction(String t_key, short action, String field, String value, String editby, String editdate) {
        this.t_key=t_key;
        this.setAction(action);
        this.setField(field);
        this.setValue(value);
        this.setEditBy(editby);
        this.setEditDate(editdate);
    }

    public static String toString(short action) {
        return(action_string[action]);
    }
    public static short toValue(String s) {
        for(short i=0;i<Array.getLength(action_string);i++) {
            if(action_string[i].equals(s)) {
                return(i);
            }
        }
        return(0);
    }

    public String getTrackerKey() {
        return t_key;
    }

    public short getAction() {
        return action;
    }
    public String getActionString() {
        return(action_string[this.action]);
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public String getEditBy() {
        return edit_by;
    }

    public String getEditDate() {
        return edit_date;
    }

    public void setAction(short action) {
        this.action = action;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setEditBy(String edit_by) {
        this.edit_by = edit_by;
    }

    public void setEditDate(String edit_date) {
        this.edit_date = edit_date;
    }

    /**
     * toString
     *
     * @return String
     */
    public String toString() {
        return("Tracker Tracker Key [" + this.t_key +
               "] Action '" + TrackerAction.toString(this.action) +
               "' on Field '" + this.field + " with Value " + this.value +
               "' by '" + this.edit_by + "' on '" + this.edit_date + "'");
    }


}

