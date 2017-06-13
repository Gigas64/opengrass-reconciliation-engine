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
 ** @version  : 1.02.00
 ** Date:  17/03/2004
 */


import org.opengrass.utils.NameValue;


public class Filter extends NameValue{

    // Name holds the fieldname, as given from frontend
    // Value holds the filter values
    private String field_keyside_;  // is P/S
    private String field_type_;     // _key_/_match_/_info_/_tracker_
    private String field_name_;     // only the name
    private String filter_operator_; // '=='/'!='
    private String filter_type_;     // AND / OR

    /**
     * Get the Field Keyside
     **/
    public String getKeySide() {
        return field_keyside_;
    }

    /**
     * Set the Field Keyside
     **/
    public void setKeySide(String fks) {
        field_keyside_  = fks;
    }

    /**
     * Get the Field Type
     **/
    public String getFieldType() {
        return field_type_;
    }

    /**
     * Set the Field Type
     **/
    public void setFieldType(String ft) {
        field_type_  = ft;
    }

    /**
     * Get the Field Name
     **/
    public String getFieldName() {
        return field_name_;
    }

    /**
     * Set the Field Type
     **/
    public void setFieldName(String fn) {
        field_name_  = fn;
    }

    /**
     * Get the operator
     **/
    public String getOperator() {
        return filter_operator_;
    }

    /**
     * Set the operator
     **/
    public void setOperator(String o) {
        filter_operator_  = o;
    }

    /**
     * Get the FilterType e.g. AND/OR
     **/
    public String getType() {
        return filter_type_;
    }

    /**
     * Set the FilterType e.g. AND/OR
     **/
    public void setType(String t) {
        filter_type_  = t;
    }

    /**
     * reset the values
     **/
    public void reset() {
        this.reset();
        filter_operator_ = "";
        filter_type_ = "";
    }


    /** Creates a new instance of Filter*/
    public Filter(String FrontendName, String FilterFieldName, String FilterValue, String FilterKeySide, String FilterFieldType, String FilterOperator, String FilterType) {
        this.setName(FrontendName);
        this.setValue(FilterValue);
        this.setKeySide(FilterKeySide);
        this.setFieldType(FilterFieldType);
        this.setFieldName(FilterFieldName);
        this.setOperator(FilterOperator);
        this.setType(FilterType);
    }

    public String toString() {
        return  "\nFilter:\n" +
        "\tName       : [" + this.getName() + "]\n" +
        "\tValue      : [" + this.getValue() + "]\n" +
        "\tKeySide    : [" + this.getKeySide() + "]\n" +
        "\tFieldType  : [" + this.getFieldType() + "]\n" +
        "\tFieldName  : [" + this.getFieldName() + "]\n" +
        "\tOperator   : [" + this.getOperator() + "]\n" +
        "\tType       : [" + this.getType() + "]\n";
    }

}
