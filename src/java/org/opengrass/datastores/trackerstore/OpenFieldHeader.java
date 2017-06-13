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

/** Class Overview:
 **
 **
 **
 ** @version 1.00 - 11/10/2004
 ** @author  Darryl Oatridge
 **
 */
public class OpenFieldHeader extends OpenField implements Comparable {

    /** a position value */
    private int _position;

    /** a precision value
     * Only set if _numeric is true
     */
    private int _precision;

    /** width setting associated with the field */
    private int _width;

    /** If the field is to be visible */
    private boolean _visible;

    /** if the field is numeric */
    private boolean _numeric;

    /**
     * Constructor Class constructor
     *
     * @param name the name of the field header
     * @param family the family type of the field header (_key_,_match_,etc)
     * @param side primary secondary or non as P,S or T
     * @param text the text to be stored in this field
     * @param pos the position of the header field
     * @param prec the precision if numberic
     * @param width the width of the display column
     * @param visible true id field should be visible
     * @param numeric boolean if is numeric
     */
    public OpenFieldHeader(String name, String family, char side, String text,
                           int pos, int prec, int width, boolean visible,
                           boolean numeric) {
        super(name, family, side, text);
        _position = pos;
        _precision = prec;
        _width = width;
        _visible = visible;
        _numeric = numeric;
    }

    // All the get methods
    /* getPosition returns the position int */
    public int getPosition() {
        return _position;
    }

    /* getPrecision returns the precision int */
    public int getPrecision() {
        return _precision;
    }

    /* getWidth returns the width int */
    public int getWidth() {
        return _width;
    }

    /* isVisible returns the visible boolean */
    public boolean isVisible() {
        return _visible;
    }

    /* isNumeric returns true if numeric */
    public boolean isNumeric() {
        return _numeric;
    }

    // All the set methods
    /* setPosition sets the position int */
    public void setPosition(int i) {
        _position = i;
    }

    /* setPrecision sets the precision int */
    public void setPrecision(int i) {
        _precision = i;
    }

    /* setWidth sets the width int */
    public void setWidth(int i) {
        _width = i;
    }

    /* setVisible sets the boolean value of visible */
    public void setVisible(boolean b) {
        _visible = b;
    }

    /* setNumeric sets the numeric boolean */
    public void setNumeric(boolean b) {
        _numeric = b;
    }

    /**
     * compareTo compares the position of the header to another header the return
     * is:
     * -1 if this header is less than passed header
     * 1 if this header is greater than passed header
     * 0 otherwise
     *
     * @param otherObject other OpenFieldHeader.
     * @return int -1 < Object < 1 (0 if same)
     */
    public int compareTo(Object otherObject) {
        OpenFieldHeader other = (OpenFieldHeader) otherObject;
        if (this._position < other._position) {
            return -1;
        }
        if (this._position > other._position) {
            return 1;
        }
        return 0;
    }

    /** toString
     * returns the full Header content.
     *
     * @return String
     */
    public String toString() {
        return ("OpenFieldHeader:" +
                "Name = " + this.getName() + " : " +
                "Side = " + this.getSide() + " : " +
                "Family = " + this.getFamily() + " : " +
                "Text = " + this.getText() + " : " +
                "Numeric = " + Boolean.toString(this._numeric) + " : " +
                "Position = " + Integer.toString(this._position) + " : " +
                "Width = " + Integer.toString(this._width) + " : " +
                "Precision = " + Integer.toString(this._precision) + " : " +
                "Visible = " + Boolean.toString(this._visible));
    }

}
