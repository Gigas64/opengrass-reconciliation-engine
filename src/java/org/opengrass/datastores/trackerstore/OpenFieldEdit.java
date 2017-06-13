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

import org.opengrass.utils.DateStrings;

/** Class Overview:
 **
 **
 **
 ** @version 1.00 - 11/10/2004
 ** @author  Darryl Oatridge
 **
 */
public class OpenFieldEdit extends OpenField {

    /** Who set the value */
    private String _edit_by;

    /** What time it was set */
    private String _edit_date;

    /** Constructor
     * Class constructor
     * @param name the name of the field header
     * @param family the family type of the field header (_key_,_match_,etc)
     * @param side primary secondary or non as P,S or T
     * @param text the text to be stored in this field
     * @param editby the person that edited the text
     * @param editdate the date the edit happened
     */
    public OpenFieldEdit(String name, String family, char side, String text,
                         String editby, String editdate) {
        super(name, family, side, text);
        _edit_by = editby;
        _edit_date = editdate;
    }

    /**
     * genFieldRef
     *
     * @param name String
     * @return String
     */
    public static String genFieldRef(String name) {
        return ("_ref_:N:" + name);
    }

    // All the get methods
    /* getSetBy returns the _edit_by String */
    public String getEditBy() {
        return _edit_by;
    }

    /* getSetTime returns the _edit_date as a String */
    public String getEditDate() {
        return _edit_date;
    }

    /* setText sets the text String with edit_by */
    public void setText(String s, String cb) {
        this.setText(s,cb,DateStrings.getCurrentDate());
    }
    /* setText sets the text String with edit_by and edit_date*/
    public void setText(String s, String cb, String cd) {
        this.setText(s);
        _edit_by = cb;
        _edit_date = cd;
    }

    /** toString
     * returns the full Edit content.
     *
     * @return String
     */
    public String toString() {
        return ("OpenFieldEdit:" +
                "Name = " + this.getName() + " : " +
                "Side = " + this.getSide() + " : " +
                "Family = " + this.getFamily() + " : " +
                "Text = " + this.getText() + " : " +
                "Edited By = " + this._edit_by + " : " +
                "Edit Date = " + this._edit_date);
    }

}
