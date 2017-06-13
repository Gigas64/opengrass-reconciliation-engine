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
 ** OpenField is a data store for a single field entry. Though
 ** the key is usually the name a construct of the name, side
 ** and family can be used as a more unique reference.
 **
 ** @version 1.00 - 11/10/2004
 ** @author  Darryl Oatridge
 **
 */
public class OpenField {

    // family statics
    public static String KEY     = "_key_";
    public static String MATCH   = "_match_";
    public static String INFO    = "_info_";
    public static String TRACKER = "_tracker_";
    public static String REF     = "_ref_";

    // side statics
    public static char PRIMARY   = 'P';
    public static char SECONDARY = 'S';
    public static char NEUTRAL   = 'N';



    /** name of the field */
    private String _name;

    /** the side type of the field (options are P,S or T)*/
    private char _side;

    /** family the field belongs to
     * Possible values:
     * _key_
     * _match_
     * _info_
     * _tracker_
     * _internal_
     */
    private String _family;

    /** The text associated with this field */
    private String _text;


    /** Constructor
     * Class constructor
     * @param name the name of the field header
     * @param family the family type of the field header (_key_,_match_,etc)
     * @param side primary secondary or non as P,S or T
     * @param text the text to be stored in this field
     */
    public OpenField(String name, String family, char side, String text) {
        _name = name;
        _family = family;
        _side = side;
        _text = text;
    }

    // All the get methods
    /* getName returns the name String */
    public String getName() {
        return _name;
    }

    /* getFamily returns the family String */
    public String getFamily() {
        return _family;
    }

    /* getSide returns the side char */
    public char getSide() {
        return _side;
    }

    /* getText returns the text String */
    public String getText() {
        return _text;
    }

    // All the set methods
    /* setText sets the text String */
    public void setText(String s) {
        _text = s;
    }

    /**
     * genFieldRef
     *
     * @param family String
     * @param side String
     * @param name String
     */
    public static String genFieldRef(String family, char side, String name) {
        return (family + ":" + side + ":" + name);
    }

    /** GetFieldRef
     * Used to obtain a compound reference for the field where
     * the name alone might not be unique enough
     *
     * @return String in the format 'family:side:name'
     */
    public String getFieldRef() {
        return (_family + ":" + _side + ":" + _name);
    }

    /** toString
     * returns the full Field content.
     *
     * @return String
     */
    public String toString() {
        return ("OpenField:" +
                "Name = " + this._name + " : " +
                "Side = " + this._side + " : " +
                "Family = " + this._family + " : " +
                "Text = " + this._text);
    }
}
