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
 ** Version: 1.03.000
 ** Date:  30/07/2004
 */

import org.opengrass.templates.*;
import java.util.*;
import java.io.*;
import org.xml.sax.*;

/**
 * Reads and writes info about business objects from the specified file.
 */

public class GpropsXMLReaderWriter extends XMLReaderWriter implements DataHandler {

    /** Debug level */
    private int debug;

    private File gpropsFile_;
    //Objects for SAX parsing
    private Gprops current_Gprops;

    // Flags
    private boolean file_info = false;
    private boolean transfer_info = false;
    private boolean transfer_contact = false;
    private boolean transfer_cover = false;
    private boolean field_info = false;
    private boolean keys = false;
    private boolean matches = false;
    private boolean info = false;
    private boolean p_info = false;
    private boolean s_info = false;

    // numeric att
    private String _numeric = "text";

    // precision att
    private String _precision = "default";

    // format att
    private String _format = "";

    // tolerance att
    private String _tolerance = "";

    // p_cons att
    private String _p_cons = "";

    // s_cons att
    private String _s_cons = "";

    // for transfer info
    private FileTransfer current_trans;

    /**
     * Creates a new Business object from the specifed file and sets the debug level to the default level (0)
     */
  /*public GpropsXMLReaderWriter(String f) {
    this(f,0);

  }
   */
    /**
     * Creates a new Business object from the specifed file and sets debug to the specified
     */
    public GpropsXMLReaderWriter(String f, int local_debug) {
        super(f, local_debug);
        debug = local_debug;
        if (debug > 1) System.err.println("INFO: Initialising GpropsXMLReaderWriter");
        current_Gprops = new Gprops(debug);
        if (debug > 2) System.err.println("DEBUG: Initialising GpropsXMLReaderWriter complete");

    }


    /**
     * Used in Sax Parsing
     */
    public void startElement( String uri, String name, String qName, Attributes atts ) {
        sb = null;
        sb = new StringBuffer();
        if (debug > 4) {
            System.err.println("PARSING: Start of element: " + name);
            System.err.println("PARSING: QName           : " + qName);
            System.err.println("PARSING: Attr            : " + atts.toString());
        }
        if (name.equals("gprops")) {

        }

        else if (name.equals("file_info")) { file_info=true; }
        else if (name.equals("transfer_info")) {
            transfer_info=true;
            current_trans = new FileTransfer();
        }
        else if (name.equals("contact") && transfer_info) { transfer_contact=true; }
        else if (name.equals("cover") && transfer_info) { transfer_cover=true; }
        else if (name.equals("field_info")) { field_info=true; }
        else if (name.equals("keys")) { keys=true; }
        else if (name.equals("matches")) { matches=true; }
        else if (name.equals("info")) { info=true; }
        else if (name.equals("field") && keys) {
            if(atts.getValue("p_c") != null && atts.getValue("p_c").length() > 0) {
                _p_cons = atts.getValue("p_c");
            }else{
                _p_cons = "";
            }
            if(atts.getValue("s_c") != null && atts.getValue("s_c").length() > 0) {
                _s_cons = atts.getValue("s_c");
            }else{
                _s_cons = "";
            }
            if(atts.getValue("format") != null && atts.getValue("format").length() > 0) {
                _format = atts.getValue("format");
            }else{
                _format = "";
            }
        }
        else if (name.equals("field") && matches) {
            if(atts.getValue("p_c") != null && atts.getValue("p_c").length() > 0) {
                _p_cons = atts.getValue("p_c");
            }else{
                _p_cons = "";
            }
            if(atts.getValue("s_c") != null && atts.getValue("s_c").length() > 0) {
                _s_cons = atts.getValue("s_c");
            }else{
                _s_cons = "";
            }
            if(atts.getValue("tolerance") != null && atts.getValue("tolerance").length() > 0) {
                _tolerance = atts.getValue("tolerance");
            }else{
                _tolerance = "";
            }
        }
        else if (name.equals("field") && info) {
            if(atts.getValue("p_c") != null && atts.getValue("p_c").length() > 0) {
                p_info = true;
                _p_cons = atts.getValue("p_c");
            }else{
                _p_cons = "";
            }
            if(atts.getValue("s_c") != null && atts.getValue("s_c").length() > 0) {
                s_info = true;
                _s_cons = atts.getValue("s_c");
            }else{
                _s_cons = "";
            }
        }

        if(matches || info) {
            _numeric = atts.getValue("format");

            if (_numeric != null && !_numeric.equals("text") ) {
                String[] x = _numeric.split(",");
                _numeric = x[0];
                if ( x.length != 2 ) {
                    _precision = "default";
                }else{
                    _precision = x[1];
                }
            }

            if (_numeric == null || (!_numeric.equals("numeric") && !_numeric.equals("ignore_case"))) {
                _numeric = "text";
                _format = _numeric;
            }else{

                _format = _numeric;
            }
        }

        if (debug > 4) { System.err.println("PARSING: Start of element: " + name + " Keys: " + keys + " Field_Info: " + field_info); }
    }

    /**
     * Used in Sax Parsing
     */
    public void endElement( String uri, String name, String qName ) throws SAXException {

        if (debug > 4) { System.err.println("PARSING: End of element: " + name + " Keys: " + keys + " Field_Info: " + field_info); }
        if(current_Gprops != null) {
            if (name.equals("business_name")) { current_Gprops.setBusinessName(sb.toString());
            current_Gprops.setRefName(sb.toString());
            if (debug > 4) { System.err.println("RefName: "+current_Gprops.getRefName()); }
            }
            else if (name.equals("display_name")) { current_Gprops.setDisplayName(sb.toString()); }
            else if (name.equals("sector_name")) { current_Gprops.setSectorName(sb.toString()); }
            else if (name.equals("category_name")) { current_Gprops.setCategoryName(sb.toString()); }
            else if (name.equals("primary")) { current_Gprops.setPrimary(sb.toString()); }
            else if (name.equals("secondary")) { current_Gprops.setSecondary(sb.toString()); }

            else if (name.equals("tracker_debug")) { current_Gprops.setTrackerDebug(Integer.parseInt(sb.toString())); }
            else if (name.equals("tracker_type")) { current_Gprops.setTrackerType(sb.toString()); }
            else if (name.equals("vaulted")) { current_Gprops.setVaulted(sb.toString()); }
            else if (name.equals("expected_completion")) { current_Gprops.setExpectedCompletion(sb.toString()); }
            else if (name.equals("expiry_count")) { current_Gprops.setExpiryCount(Integer.parseInt(sb.toString())); }
            else if (name.equals("run_offset")) { current_Gprops.setRunOffset(sb.toString()); }
            else if (name.equals("max_match_ref")) { current_Gprops.setMaxMatch(Integer.parseInt(sb.toString())); }
            else if (name.equals("max_break_ref")) { current_Gprops.setMaxBreak(Integer.parseInt(sb.toString())); }
            else if (name.equals("max_log")) { current_Gprops.setMaxLog(Integer.parseInt(sb.toString())); }
            else if (name.equals("match_duplicates")) { current_Gprops.setMatchDuplicates(sb.toString()); }
            else if (name.equals("one_sided_tolerance")) { current_Gprops.setOneSidedTolerance(sb.toString()); }

            else if (name.equals("cost_centre")) { current_Gprops.setCostCentre(sb.toString()); }
            else if (name.equals("cost_band")) { current_Gprops.setCostBand(sb.toString()); }
            else if (name.equals("cost_signatory")) { current_Gprops.setCostSignatory(sb.toString()); }
            else if (name.equals("cost_justification")) { current_Gprops.setCostJustification(sb.toString()); }
            else if (name.equals("deployment_sponsor")) { current_Gprops.setDeploymentSponsor(sb.toString()); }
            else if (name.equals("deployment_reason")) { current_Gprops.setDeploymentReason(sb.toString()); }
            else if (name.equals("deployment_date")) { current_Gprops.setDeploymentDate(sb.toString()); }
            else if (name.equals("access_authority")) { current_Gprops.setAccessAuthority(sb.toString()); }
            else if (name.equals("backup_authority")) { current_Gprops.setBackupAuthority(sb.toString()); }
            else if (name.equals("business_signoff")) { current_Gprops.setBusinessSignoff(sb.toString()); }
            else if (name.equals("qa_signoff")) { current_Gprops.setQaSignoff(sb.toString()); }
            else if (name.equals("deployer_signoff")) { current_Gprops.setDeployerSignoff(sb.toString()); }
            else if (name.equals("support_signoff")) { current_Gprops.setSupportSignoff(sb.toString()); }
            else if (name.equals("dev_signoff")) { current_Gprops.setDevSignoff(sb.toString()); }

            else if (name.equals("transfer_name") && transfer_info) { current_trans.setTransferName(sb.toString()); }
            else if (name.equals("type") && transfer_info) { current_trans.setType(sb.toString()); }
            else if (name.equals("login") && transfer_info) { current_trans.setLogin(sb.toString()); }
            else if (name.equals("password") && transfer_info) { current_trans.setPassword(sb.toString()); }
            else if (name.equals("machine") && transfer_info) { current_trans.setMachine(sb.toString()); }
            else if (name.equals("path") && transfer_info) { current_trans.setPath(sb.toString()); }
            else if (name.equals("filename") && transfer_info) { current_trans.setFilename(sb.toString()); }
            else if (name.equals("transfer_script") && transfer_info) { current_trans.setTransferScript(sb.toString()); }
            else if (name.equals("system_name") && transfer_info) { current_trans.setSystemName(sb.toString()); }
            else if (name.equals("delivery_schedule") && transfer_info) { current_trans.setDeliverySchedule(sb.toString()); }
            else if (name.equals("name") && transfer_info && transfer_contact) { current_trans.setContactName(sb.toString()); }
            else if (name.equals("number") && transfer_info && transfer_contact) { current_trans.setContactNumber(sb.toString()); }
            else if (name.equals("email") && transfer_info && transfer_contact) { current_trans.setContactEmail(sb.toString()); }
            else if (name.equals("name") && transfer_info && transfer_cover) { current_trans.setCoverName(sb.toString()); }
            else if (name.equals("number") && transfer_info && transfer_cover) { current_trans.setCoverNumber(sb.toString()); }
            else if (name.equals("email") && transfer_info && transfer_cover) { current_trans.setCoverEmail(sb.toString()); }
            else if (name.equals("notes") && transfer_info) { current_trans.setNotes(sb.toString()); }


            else if (name.equals("file_info")) { file_info=false; }
            else if (name.equals("transfer_info")) {
                transfer_info=false;
                current_Gprops.setFiles(current_trans);
            }
            else if (name.equals("contact") && transfer_info) { transfer_contact=false; }
            else if (name.equals("cover") && transfer_info) { transfer_contact=false; }

            else if (name.equals("field_info")) { field_info=false; }
            else if (name.equals("keys")) { keys=false; }
            else if (name.equals("matches")) { matches=false; }
            else if (name.equals("info")) { p_info=false; s_info=false; }
            else if (field_info && keys && name.equals("field")) {
                FieldInfo elem = new FieldInfo();
                elem.setFieldName(sb.toString());
                elem.setPrimaryConsolidation(_p_cons);
                elem.setSecondaryConsolidation(_s_cons);
                elem.setFormat(_format);
                elem.setPrecision(_precision);
                current_Gprops.setKeys(elem);
            }
            else if (field_info && matches && name.equals("field")) {
                FieldInfo elem = new FieldInfo();
                elem.setFieldName(sb.toString());
                elem.setPrimaryConsolidation(_p_cons);
                elem.setSecondaryConsolidation(_s_cons);
                elem.setFormat(_format);
                elem.setPrecision(_precision);
                elem.setTolerance(_tolerance);
                current_Gprops.setMatches(elem);

                if(_numeric.equals("numeric")) {
                    current_Gprops.setNumericFields(sb.toString());
                }
                if(_numeric.equals("numeric") && matches) {
                    current_Gprops.setDiffFields(sb.toString());
                }
                if(_numeric.equals("ignore_case")) {
                    current_Gprops.setIgnoreCaseFields(sb.toString());
                }
            }
            else if (name.equals("field") && field_info && (p_info || s_info)) {
                FieldInfo elem = new FieldInfo();
                elem.setFieldName(sb.toString());
                elem.setPrimaryConsolidation(_p_cons);
                elem.setSecondaryConsolidation(_s_cons);
                elem.setFormat(_format);
                elem.setPrecision(_precision);
                current_Gprops.setInfos(elem);

                if(p_info) {
                    current_Gprops.setPInfos(elem);
                }
                if(s_info) {
                    current_Gprops.setSInfos(elem);
                }
                if(_numeric.equals("numeric")) {
                    current_Gprops.setNumericFields(sb.toString());
                }
                if(_numeric.equals("numeric") && matches) {
                    current_Gprops.setDiffFields(sb.toString());
                }
                if(_numeric.equals("ignore_case")) {
                    current_Gprops.setIgnoreCaseFields(sb.toString());
                }
                p_info = false;
                s_info = false;
            }
            else if (name.equals("gprops")) {
                data.put(current_Gprops.getRefName(), current_Gprops);
                if (debug > 2) { System.err.println(current_Gprops.toString()); }
                current_Gprops = null;

            }
        }
    }

    /** Used in Sax Parsing - can get it to parse if this method is in XMLReaderWriter only???**/
    public void characters( char[] ch, int start, int length ) {
        int endMarker = start+length;
        for (int i=start; i<endMarker; i++) {
            sb.append( ch[i] );
        }
    }

    /** Returns an String representation of the gprops */
    public String toString() {
        StringBuffer jb = new StringBuffer();
        for (Enumeration e = data.elements(); e.hasMoreElements();) {
            current_Gprops = (Gprops) e.nextElement();
            jb.append(current_Gprops.toString());
        }
        return jb.toString();
    }



    public Gprops getCurrentGprops() {
        return current_Gprops;
    }

    // main method to act as test harness
    public static void main(String args []) throws Exception {
        System.err.println("Gprops startet!");


        // Tests should be done with GpropsStore

        System.err.println("Gprops finished!");

    }

}