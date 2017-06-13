package org.opengrass.datastores.gpropstore;

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
 ** Version: 1.01.000
 ** Date:  30/07/2003
 */

public class FieldInfo {

    protected String field_name;
    protected String primary_consolidation;
    protected String secondary_consolidation;
    protected String tolerance;
    protected String format;
    protected String precision;

    public String getFieldName() { return field_name; }
    public String getPrimaryConsolidation() { return primary_consolidation; }
    public String getSecondaryConsolidation() { return secondary_consolidation; }
    public String getTolerance() { return tolerance; }
    public String getFormat() { return format; }
    public String getPrecision() { return precision; }

    public void setFieldName(String s) { field_name = s; }
    public void setPrimaryConsolidation(String s) { primary_consolidation = s; }
    public void setSecondaryConsolidation(String s) { secondary_consolidation = s; }
    public void setTolerance(String s) { tolerance = s; }
    public void setFormat(String s) { format = s; }
    public void setPrecision(String s) { precision = s; }

    protected String FieldNametoString() {
        return(field_name);
    }

    protected String toXML() {
        String formated="";
        formated = format;

        if (!precision.equals("") && !precision.equals("default")) {
            formated += ","+precision;
        }
        return(
        "      <field p_c=\"" + primary_consolidation + "\" s_c=\"" + secondary_consolidation +
        "\" format=\"" + formated + "\">" + field_name + "</field>\n");
    }

    protected String toAllXML() {
        String formated="";
        formated = format;

        if (!precision.equals("") && !precision.equals("default")) {
            formated += ","+precision;
        }
        return(
        "      <field p_c=\"" + primary_consolidation + "\" s_c=\"" + secondary_consolidation +
        "\" format=\"" + formated + "\" tolerance=\"" + tolerance + "\">" + field_name + "</field>\n");
    }
}
