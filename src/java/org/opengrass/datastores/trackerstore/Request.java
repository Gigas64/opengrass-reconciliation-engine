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
 ** @version  : 1.21.000
 ** Date:  22/07/2003
 */

import org.apache.regexp.*;
import java.util.*;

import org.opengrass.exceptions.OpenGrassException;
import org.opengrass.utils.XMLiser;

/** Class for the requests to the trackerstore.
 * Interface between GUI and Trackerstore.
 */
public class Request {

    /** Values could be
     *  pass  = False -> Don't use the filter
     *  filter = True -> Use the filter
     */
    private boolean filter_;

    /** NameValue
     * Fieldname for the filter
     * Filter Value
     * - e.g. show me all with that substring
     * use Regular Experessions
     */
    private Vector filters_;

    /** Start at key or at SOL
     *  - If it don't exist throw GRASS exception
     */
    private String start_key_;
    private String end_key_;


    /** All keys of a selection
     *  eg. give me all data of the defined keys
     */
    private Vector all_keys_;

    /** like next/prev/current 'N' / 'P' / 'C'
     *  - e.g. give me the next 20 keys
     * out of SOL to EOL
     */
    private char select_type_;

    /** Select n records - 0 = all */
    private int select_number_;

    /** Sort Vector of fields
     */
    private Vector sort_;

    /** Values could be
     *  Asc  = True -> Sort in Ascending order
     *  Desc = False -> Sort in descending order
     */
    private boolean sort_type_;

    /** Give me only the fields selected by User
     *  Values could be ... a Vector of headers
     */
    private Vector header_vec_;

    /** Display Type is for predefined queries
     *  Values can be:
     *  Excluded - only records with match_type 'Excluded' will be set from frontend
     *  Default - all records without excludeds
     *  All - everything
     *  Forced - only forced records
     *  OneSided - all onesideds
     */
    private String display_type_;


    // ***********************************************************

    /**
     * Creates a new instance of Request
     */
    public Request() {
        filter_ = false;
        filters_ = new Vector();
        start_key_ = "SOL";
        all_keys_ = new Vector();
        select_type_ = 'C';
        select_number_ = 0;
        sort_ = new Vector();
        sort_type_ = true;
        header_vec_ = new Vector();
        display_type_ = "Default";
    }


    /** Get value of the filter switch
     * @return
     *  pass  = False -> Don't use the filter
     *  filter = True -> use the filter
     */
    public boolean getFilter() {

        return filter_;
    }

    /** Filter switch
     * @param filter
     * pass  = False -> Don't use the filter
     *  filter = True -> use the filter
     */

    public void setFilter(boolean filter) {
        filter_ = filter;
    }

    /** Get the complete Vector of filters
     * @return
     */
    public Vector getFilters() {
        return filters_;
    }

    /** Set a complete Vector of filters
     * @param fn
     */
    public void setFilters(Vector fn) {
        filters_ = fn;
    }

    /** Get an iterator for the filter vector
     * @return
     */
    public Iterator getFiltersIterator() {
        return filters_.iterator();
    }

    /** Set one given (by idx) filter element in the filter vector to the given values
     * @param idx
     * @param name
     * @param value
     * @param operator
     */
    public void set1Filter(int idx, String name, String value,
                           String filteroperator, String filtertype) throws
            OpenGrassException {

        String field_type = "";
        String keyside = "";
        String field_name = "";

        try {
            // Split the field into tokens
            RE sep = new RE(":");
            String tokens[] = sep.split(name);

            field_type = tokens[0];
            keyside = tokens[1];
            field_name = tokens[2];

        } catch (Exception e) {
            throw new OpenGrassException(
                    "Error: Problems splitting the fields of the given string!");
        }

        Filter element = new Filter(name, field_name, value, keyside,
                                    field_type, filteroperator, filtertype);

        filters_.add(idx, element);
    }

    /** Add a filter element with the given values to the filter vector
     * @param name
     * @param value
     */
    public void add1Filter(String name, String value, String filteroperator,
                           String filtertype) throws OpenGrassException {

        String field_type = "";
        String keyside = "";
        String field_name = "";

        try {
            // Split the field into tokens
            RE sep = new RE(":");
            String tokens[] = sep.split(name);

            field_type = tokens[0];
            keyside = tokens[1];
            field_name = tokens[2];

        } catch (Exception e) {
            throw new OpenGrassException(
                    "Error: Problems splitting the fields of the given string!");
        }
        /*StringTokenizer stx = new StringTokenizer (name, ":");
               field_type = stx.nextToken();
               keyside = stx.nextToken();
               field_name = stx.nextToken(); */

        Filter element = new Filter(name, field_name, value, keyside,
                                    field_type, filteroperator, filtertype);

        filters_.add(element);
    }

    /** Remove a filter element from the filter vector
     * @param idx
     */
    public void remove1Filter(int idx) {
        filters_.remove(idx);
    }

    /**
     * @param idx
     * @return
     */
    public Filter get1Filter(int idx) {
        Filter element;

        element = (Filter) filters_.get(idx);
        return element;
    }

    /**
     * @return
     */
    public String getStartKey() {

        return start_key_;
    }

    /**
     * @return
     */
    public String getEndKey() {

        return end_key_;
    }

    /**
     * @param sk
     */
    public void setStartKey(String sk) {
        start_key_ = XMLiser.escapeChars(sk);
    }

    /**
     * @param sk
     */
    public void setEndKey(String sk) {
        end_key_ = XMLiser.escapeChars(sk);
    }

    /**
     * @return
     */
    public Vector getAllKeys() {

        return all_keys_;
    }

    /**
     * @param ak
     */
    public void setAllKeys(Vector ak) {

        for (int i = 0; i < ak.size(); i++) {
            String key = (String) ak.get(i);
            key = XMLiser.escapeChars(key);
            ak.setElementAt(key, i);
        }

        all_keys_ = ak;
    }

    /**
     * @return
     */
    public char getSelectType() {

        return select_type_;
    }

    /**
     * @param st
     */
    public void setSelectType(char st) {
        select_type_ = st;
    }

    /**
     * @return
     */
    public int getSelectNumber() {

        return select_number_;
    }

    /**
     * @param sel
     */
    public void setSelectNumber(int sel) {
        select_number_ = sel;
    }

    /** Get the Vector of Sort Fields
     * @return
     */
    public Vector getSort() {

        return sort_;
    }

    /** Set a Vector of Sort-Fields
     * @param s
     */
    public void setSort(Vector s) {
        sort_ = s;
    }

    /** Set one given (by idx) sort element in the sort vector to the given value
     * @param idx
     * @param name
     *
     */
    public void set1Sort(int idx, String name) {

        sort_.add(idx, name);
    }

    /** Add a sort element with the given value to the sort vector
     * @param name
     */
    public void add1Sort(String name) {

        sort_.add(name);
    }

    /** Remove a sort element from the sort vector
     * @param idx
     */
    public void remove1Sort(int idx) {

        sort_.remove(idx);
    }

    /** Get the SortType
     * Values could be
     *  Asc  = True -> Sort in Ascending order
     *  Desc = False -> Sort in descending order
     * @return
     */
    public boolean getSortType() {

        return sort_type_;
    }

    /** Set a SortType
     * Values could be
     *  Asc  = True -> Sort in Ascending order
     *  Desc = False -> Sort in descending order
     * @param s
     */
    public void setSortType(boolean st) {

        sort_type_ = st;
    }

    /** check if it is a ascending sort order
     * @param s
     */
    public boolean is_AscSortType() {

        if (sort_type_ == true) {
            return true;
        }
        ;
        return false;
    }

    /** check if it is a ascending sort order
     * @param s
     */
    public boolean is_DescSortType() {

        if (sort_type_ == false) {
            return true;
        }
        ;
        return false;
    }

    /**
     * @return
     */
    public Vector getHeaderVec() {

        return header_vec_;
    }

    /**
     * @param hs
     */
    public void setHeaderVec(Vector hs) {

        header_vec_ = hs;
    }

    /**
     * @return
     */
    public String getDisplayType() {

        return display_type_;
    }

    /**
     * @param hs
     */
    public void setDisplayType(String dt) {

        display_type_ = dt;
    }

    // ****************************** end of get/set *******************

    /**
     * Filter Vector equals
     **/
    public boolean is_equal_filter_vector(Vector that, Vector other) {

        int i = 0;
        Filter outerdata;
        Filter innerdata;

        if (that == other) {
            return true;
        }
        if (that == null && other == null) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (other == null) {
            return false;
        }
        if (that.isEmpty() && other.isEmpty()) {
            return true;
        }
        if (that.isEmpty()) {
            return false;
        }
        if (other.isEmpty()) {
            return false;
        }
        if (that.getClass() != other.getClass()) {
            return false;
        }

        while (i < that.size() && i < other.size()) {
            outerdata = (Filter) that.get(i);
            innerdata = (Filter) other.get(i);
            // check if nameValues are equal
            if (!outerdata.equals(innerdata)) {
                return false;
            }
            i++;
        }

        if (i == that.size() && i == other.size()) {
            // one vector has an element more than the other
            return true;
        }

        return false;

    }

    /**
     * Header Vector equals
     **/
    public boolean is_equal_header_vector(Vector that, Vector other) {

        int i = 0;
        OpenFieldHeader outerdata;
        OpenFieldHeader innerdata;

        if (that == other) {
            return true;
        }
        if (that == null && other == null) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (other == null) {
            return false;
        }
        if (that.isEmpty() && other.isEmpty()) {
            return true;
        }
        if (that.isEmpty()) {
            return false;
        }
        if (other.isEmpty()) {
            return false;
        }
        if (that.getClass() != other.getClass()) {
            return false;
        }

        while (i < that.size() && i < other.size()) {
            outerdata = (OpenFieldHeader) that.get(i);
            innerdata = (OpenFieldHeader) other.get(i);
            // check if nameValues are equal
            if (!outerdata.equals(innerdata)) {
                return false;
            }
            i++;
        }

        if (i == that.size() && i == other.size()) {
            // one vector has an element more than the other
            return true;
        }

        return false;

    }


    /** check for
     * select_type
     */
    public boolean is_equalSelectType(Request a, Request r) {
        // a quick test to see if the objects are identical
        if (a == r) {
            return true;
        }

        // must return false if explicit parameter is null
        if (r == null) {
            return false;
        }

        // must return false if explicit parameter is null
        if (a == null) {
            return false;
        }

        // test wether the fields have identical values
        return a.select_type_ == r.select_type_;
    }

    /** check for
     * select_type
     */
    public boolean is_equalSelectType(Request r) {
        // a quick test to see if the objects are identical
        if (this == r) {
            return true;
        }

        // must return false if explicit parameter is null
        if (r == null) {
            return false;
        }

        // test wether the fields have identical values
        return select_type_ == r.select_type_;
    }


    /** check for
     * filter, filter_type, name, value,
     */
    public boolean is_equal(Request r) {
        boolean filter_equal = false;
        boolean header_equal = false;

        // a quick test to see if the objects are identical
        if (this == r) {
            return true;
        }

        // must return false if explicit parameter is null
        if (r == null) {
            return false;
        }

        filter_equal = is_equal_filter_vector(this.filters_, r.getFilters());
        header_equal = is_equal_header_vector(this.header_vec_, r.getHeaderVec());

        // test wether the fields have identical values
        return this.filter_ == r.filter_ &&
                filter_equal &&

                this.display_type_.equals(r.display_type_) &&
                this.start_key_.equals(r.start_key_) &&
                this.select_number_ == r.select_number_ &&
                this.all_keys_.equals(r.all_keys_) &&
                header_equal;
    }


    /**
     * @param headerh
     * @return
     */
    public TreeMap getSortedHeaderOrder(Hashtable headerh) {
        TreeMap ia = new TreeMap();

        for (Enumeration e = headerh.elements(); e.hasMoreElements(); ) {
            OpenFieldHeader he = (OpenFieldHeader) e.nextElement();

            ia.put(new Integer(he.getPosition()), he);
        }
        return ia;

    }

    /**
     * @return
     */
    public String toString() {
        return "\nRequest:\n" +
                "\tFilter        : [" + this.getFilter() + "]\n" +
                "\tFilters       : [" + this.getFilters().toString() + "]\n" +
                "\tStart-Key     : [" + this.getStartKey() + "]\n" +
                "\tEnd-Key       : [" + this.getEndKey() + "]\n" +
                "\tAll Keys      : [" + (this.getAllKeys()).toString() + "]\n" +
                "\tSelect Type   : [" + this.getSelectType() + "]\n" +
                "\tSelect Number : [" + this.getSelectNumber() + "]\n" +
                "\tSort          : [" + this.getSort().toString() + "]\n" +
                "\tSort Type     : [" + Boolean.toString(this.getSortType()) +
                "]\n" +
                "\tDisplay Type  : [" + this.getDisplayType() + "]\n";

        //"\tHeader Vector : [" + this.getHeaderVec().toString() + "]\n";
    }

}
