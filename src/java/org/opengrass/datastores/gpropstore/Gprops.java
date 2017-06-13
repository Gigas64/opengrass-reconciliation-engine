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

import java.util.*;

public class Gprops {

    protected int debug;

    // setup info
    protected String business_name;
    protected String display_name;
    protected String sector_name;
    protected String category_name;
    protected String primary;
    protected String secondary;
    // property info
    protected String tracker_type;
    protected String vaulted;
    protected String expected_completion;
    protected int expiry_count;
    protected String run_offset;
    protected int core_debug;
    protected int tracker_debug;
    protected int max_match_ref;
    protected int max_break_ref;
    protected int max_log;
    protected String match_duplicates;
    protected String one_sided_tolerance;
    // file info
    protected Vector files;
    // field info
    protected Vector keys;      // Vector of FieldInfo Objects
    protected Vector matches;   // Vector of FieldInfo Objects
    protected Vector info;      // Vector of FieldInfo Objects
    protected Vector p_info;    // Vector of FieldInfo Objects
    protected Vector s_info;    // Vector of FieldInfo Objects
    protected Vector numeric;   // Vector of Strings
    protected Vector precision; // Vector of Strings
    protected Vector diff;      // Vector of Strings
    protected Vector ignorecase;     // Vector of Strings
    // management info
    protected String cost_centre;
    protected String cost_band;
    protected String cost_signatory;
    protected String cost_justification;
    protected String deployment_sponsor;
    protected String deployment_reason;
    protected String deployment_date;
    protected String access_authority;
    protected String backup_authority;
    protected String business_signoff;
    protected String qa_signoff;
    protected String deployer_signoff;
    protected String support_signoff;
    protected String dev_signoff;

    //***************************************************************************

    /** Creates a new instance of Gprops */
    public Gprops(int local_debug) {
        debug = local_debug;
        if (debug > 2) System.err.println("INFO: Initialising Gprops");
        files = new Vector();
        keys = new Vector();
        matches = new Vector();
        info = new Vector();
        p_info = new Vector();
        s_info = new Vector();
        numeric = new Vector();
        precision = new Vector();
        diff = new Vector();
        ignorecase = new Vector();
    }

    /**
     *
     */
    // get all the variables
    public String getRefName() { return business_name; }

    public String getBusinessName() { return business_name; }

    public String getDisplayName() { return display_name; }

    public String getSectorName() { return sector_name; }

    public String getCategoryName() { return category_name; }

    public String getPrimary() { return primary; }

    public String getSecondary() { return secondary; }

    public int getCoreDebug() { return core_debug; }

    public int getTrackerDebug() { return tracker_debug; }

    public int getDebug() { return tracker_debug; }

    public String getTrackerType() { return tracker_type; }

    public String getVaulted() { return vaulted; }

    public String getExpectedCompletion() { return expected_completion; }

    public int getExpiryCount() { return expiry_count; }

    public String getRunOffset() { return run_offset; }

    public int getMaxBreak() { return max_break_ref; }

    public int getMaxMatch() { return max_match_ref; }

    public int getMaxLog() { return max_log; }

    public String getMatchDuplicates() { return match_duplicates; }

    public String getOneSidedTolerance() { return one_sided_tolerance; }

    // Vector of fileInfo Objects
    public Vector getFiles() { return files; }

    // Vector of FieldInfo Objects
    public Vector getKeys() { return keys; }

    // Vector of FieldInfo Objects
    public Vector getMatches() { return matches; }

    // Vector of FieldInfo Objects
    public Vector getInfos() { return info; }

    // Vector of FieldInfo Objects
    public Vector getPInfos() { return p_info; }

    // Vector of FieldInfo Objects
    public Vector getSInfos() { return s_info; }

    // Vector of Strings
    public Vector getNumericFields() { return numeric; }

    // Vector of Strings
    public Vector getPrecisionFields() { return precision; }

    // Vector of Strings
    public Vector getDiffFields() { return diff; }

    // Vector of Strings
    public Vector getIgnoreCaseFields() { return ignorecase; }

    public String getCostCentre() { return cost_centre; }

    public String getCostBand() { return cost_band; }

    public String getCostSignatory() { return cost_signatory; }

    public String getCostJustification() { return cost_justification; }

    public String getDeploymentSponsor() { return deployment_sponsor; }

    public String getDeploymentReason() { return deployment_reason; }

    public String getDeploymentDate() { return deployment_date; }

    public String getAccessAuthority() { return access_authority; }

    public String getBackupAuthority() { return backup_authority; }

    public String getBusinessSignoff() { return business_signoff; }

    public String getQaSignoff() { return qa_signoff; }

    public String getDeployerSignoff() { return deployer_signoff; }

    public String getSupportSignoff() { return support_signoff; }

    public String getDevSignoff() { return dev_signoff; }

    public String getSignoffState() {
        if(business_signoff.length() > 1 && deployer_signoff.length() > 1
        && support_signoff.length() > 1
        && qa_signoff.length() > 1) {
            return "true";
        }
        return "false";
    }


    // Set all the variables

    public void setRefName(String s) {
        if (debug > 3) { System.err.println("DEBUG: ref_name set to [" + s + "]"); }
        business_name = s;
    }

    public void setBusinessName(String s) {
        if (debug > 3) { System.err.println("DEBUG: business_name set to [" + s + "]"); }
        business_name = s;
    }

    public void setDisplayName(String s) {
        if (debug > 3) { System.err.println("DEBUG: display_name set to [" + s + "]"); }
        display_name = s;
    }

    public void setSectorName(String s) {
        if (debug > 3) { System.err.println("DEBUG: sector_name set to [" + s + "]"); }
        sector_name = s;
    }

    public void setCategoryName(String s) {
        if (debug > 3) { System.err.println("DEBUG: category_name set to [" + s + "]"); }
        category_name = s;
    }

    public void setPrimary(String s) {
        if (debug > 3) { System.err.println("DEBUG: primary set to [" + s + "]"); }
        primary = s;
    }

    public void setSecondary(String s) {
        if (debug > 3) { System.err.println("DEBUG: secondary set to [" + s + "]"); }
        secondary = s;
    }

    public void setTrackerType(String s) {
        if (debug > 3) { System.err.println("DEBUG: tracker_type set to [" + s + "]"); }
        tracker_type = s;
    }

    public void setVaulted(String s) {
        if (debug > 3) { System.err.println("DEBUG: vaulted set to [" + s + "]"); }
        vaulted = s;
    }

    public void setExpectedCompletion(String s) {
        if (debug > 3) { System.err.println("DEBUG: expected_completion set to [" + s + "]"); }
        expected_completion = s;
    }

    public void setExpiryCount(int i) {
        if (debug > 3) { System.err.println("DEBUG: expiry_count set to [" + Integer.toString(i) + "]"); }
        expiry_count = i;
    }

    public void setRunOffset(String s) {
        if (debug > 3) { System.err.println("DEBUG: run_offset set to [" + s + "]"); }
        run_offset = s;
    }

    public void setCoreDebug(int i) {
        if (debug > 3) { System.err.println("DEBUG: core_debug set to [" + Integer.toString(i) + "]"); }
        core_debug = i;
    }

    public void setTrackerDebug(int i) {
        if (debug > 3) { System.err.println("DEBUG: tracker_debug set to [" + Integer.toString(i) + "]"); }
        tracker_debug = i;
    }

    public void setMaxBreak(int i) {
        if (debug > 3) { System.err.println("DEBUG: max_break_ref set to [" + Integer.toString(i) + "]"); }
        max_break_ref = i;
    }

    public void setMaxMatch(int i) {
        if (debug > 3) { System.err.println("DEBUG: max_match_ref set to [" + Integer.toString(i) + "]"); }
        max_match_ref = i;
    }

    public void setMaxLog(int i) {
        if (debug > 3) { System.err.println("DEBUG: max_log set to [" + Integer.toString(i) + "]"); }
        max_log = i;
    }

    public void setMatchDuplicates(String s) {
        if (debug > 3) { System.err.println("DEBUG: match_duplicates set to [" + s + "]"); }
        match_duplicates = s;
    }

    public void setOneSidedTolerance(String s) {
        if (debug > 3) { System.err.println("DEBUG: one_sided_tolerance set to [" + s + "]"); }
        one_sided_tolerance = s;
    }

    public void setFiles(FileTransfer s) {
        if (debug > 3) { System.err.println("DEBUG: Adding key [" + s.toString() + "]"); }
        files.addElement(s);
    }

    public void setKeys(FieldInfo s) {
        if (debug > 3) { System.err.println("DEBUG: Adding key [" + s.toString() + "]"); }
        keys.addElement(s);
    }

    public void setMatches(FieldInfo s) {
        if (debug > 3) { System.err.println("DEBUG: Adding match [" + s.toString() + "]"); }
        matches.addElement(s);
    }

    public void setInfos(FieldInfo s) {
        if (debug > 3) { System.err.println("DEBUG: Adding info [" + s.toString() + "]"); }
        info.addElement(s);
    }

    public void setPInfos(FieldInfo s) {
        if (debug > 3) { System.err.println("DEBUG: Adding p_info [" + s.toString() + "]"); }
        p_info.addElement(s);
    }

    public void setSInfos(FieldInfo s) {
        if (debug > 3) { System.err.println("DEBUG: Adding s_info [" + s.toString() + "]"); }
        s_info.addElement(s);
    }

    public void setNumericFields(String s) {
        if (debug > 3) { System.err.println("DEBUG: Adding numeric [" + s + "]"); }
        numeric.addElement(s);
    }

    public void setPrecisionFields(String s) {
        if (debug > 3) { System.err.println("DEBUG: Adding precision [" + s + "]"); }
        precision.addElement(s);
    }

    public void setDiffFields(String s) {
        if (debug > 3) { System.err.println("DEBUG: Adding diff [" + s + "]"); }
        diff.addElement(s);
    }


    public void setIgnoreCaseFields(String s) {
        if (debug > 3) { System.err.println("DEBUG: Adding ignorecase [" + s + "]"); }
        ignorecase.addElement(s);
    }

    public void setCostCentre(String s) {
        if (debug > 3) { System.err.println("DEBUG: cost_centre set to [" + s + "]"); }
        cost_centre = s;
    }

    public void setCostBand(String s) {
        if (debug > 3) { System.err.println("DEBUG: cost_band set to [" + s + "]"); }
        cost_band = s;
    }

    public void setCostJustification(String s) {
        if (debug > 3) { System.err.println("DEBUG: cost_justification set to [" + s + "]"); }
        cost_justification = s;
    }

    public void setCostSignatory(String s) {
        if (debug > 3) { System.err.println("DEBUG: cost_signatory set to [" + s + "]"); }
        cost_signatory = s;
    }

    public void setDeploymentSponsor(String s) {
        if (debug > 3) { System.err.println("DEBUG: deployment_sponsor set to [" + s + "]"); }
        deployment_sponsor = s;
    }

    public void setDeploymentReason(String s) {
        if (debug > 3) { System.err.println("DEBUG: deployment_reason set to [" + s + "]"); }
        deployment_reason = s;
    }

    public void setDeploymentDate(String s) {
        if (debug > 3) { System.err.println("DEBUG: deployment_date set to [" + s + "]"); }
        deployment_date = s;
    }

    public void setAccessAuthority(String s) {
        if (debug > 3) { System.err.println("DEBUG: access_authority set to [" + s + "]"); }
        access_authority = s;
    }

    public void setBackupAuthority(String s) {
        if (debug > 3) { System.err.println("DEBUG: backup_authority set to [" + s + "]"); }
        backup_authority = s;
    }

    public void setBusinessSignoff(String s) {
        if (debug > 3) { System.err.println("DEBUG: business_signoff set to [" + s + "]"); }
        business_signoff = s;
    }

    public void setQaSignoff(String s) {
        if (debug > 3) { System.err.println("DEBUG: qa_signoff set to [" + s + "]"); }
        qa_signoff = s;
    }

    public void setDeployerSignoff(String s) {
        if (debug > 3) { System.err.println("DEBUG: deployer_signoff set to [" + s + "]"); }
        deployer_signoff = s;
    }

    public void setSupportSignoff(String s) {
        if (debug > 3) { System.err.println("DEBUG: support_signoff set to [" + s + "]"); }
        support_signoff = s;
    }

    public void setDevSignoff(String s) {
        if (debug > 3) { System.err.println("DEBUG: dev_signoff set to [" + s + "]"); }
        dev_signoff = s;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        FieldInfo x;

        sb.append("<gprops>\n\n");
        sb.append("  <!-- SETUP INFOMATION SECTION -->\n");
        sb.append("  <setup_info>\n");
        sb.append("    <!-- Unique name identifier for this deployment (single word) -->\n");
        sb.append("    <business_name>" + business_name + "</business_name>\n");
        sb.append("    <!-- Visual displayed name for the deployment -->\n");
        sb.append("    <display_name>" + display_name + "</display_name>\n");
        sb.append("    <!-- The sector name for this deployment (single word) -->\n");
        sb.append("    <sector_name>" + sector_name + "</sector_name>\n");
        sb.append("    <!-- The category name for this deployment (single word) -->\n");
        sb.append("    <category_name>" + category_name + "</category_name>\n");
        sb.append("    <!-- Primary system identifier (single word) -->\n");
        sb.append("    <primary>" + primary + "</primary>\n");
        sb.append("    <!-- Secondary system identifier (single word) -->\n");
        sb.append("    <secondary>" + secondary + "</secondary>\n");
        sb.append("  </setup_info>\n\n");

        sb.append("  <!-- PROPERTY INFORMATION SECTION -->\n");
        sb.append("  <property_info>\n");
        sb.append("    <!-- The behaviour of the reconciliation before the data -->\n");
        sb.append("    <!-- is passed through the matching engine -->\n");
        sb.append("    <!-- Options: -->\n");
        sb.append("    <!-- 1) Standard - Standard reconciliation behavior -->\n");
        sb.append("    <!-- 2) Merge - Merge previous results before matching -->\n");
        sb.append("    <tracker_type>" + tracker_type + "</tracker_type>\n");
        sb.append("    <!-- 'true' or 'false' depending if has passed QA -->\n");
        sb.append("    <vaulted>" + vaulted + "</vaulted>\n");
        sb.append("    <!-- type and time customer expectation of completion -->\n");
        sb.append("    <expected_completion>" + expected_completion + "</expected_completion>\n");
        sb.append("    <!-- If > 0 then delete record if inactive after expiry_count -->\n");
        sb.append("    <expiry_count>" + Integer.toString(expiry_count) + "</expiry_count>\n");
        sb.append("    <!-- offset the first seen date as the true data date -->\n");
        sb.append("    <!-- Options: -->\n");
        sb.append("    <!-- 1) none - no offset -->\n");
        sb.append("    <!-- 2) short - an offset of -1 for 5 day week -->\n");
        sb.append("    <!-- 3) long - an offset of -1 for 7 day week -->\n");
        sb.append("    <run_offset>" + run_offset + "</run_offset>\n");
        sb.append("    <!-- Debug level for Core Engine (0-5) -->\n");
        sb.append("    <core_debug>" + Integer.toString(core_debug) + "</core_debug>\n");
        sb.append("    <!-- Debug level for Tracker Engine (0-5) -->\n");
        sb.append("    <tracker_debug>" + Integer.toString(tracker_debug) + "</tracker_debug>\n");
        sb.append("    <!-- Number of days to span match reference archive -->\n");
        sb.append("    <max_match_ref>" + Integer.toString(max_match_ref) + "</max_match_ref>\n");
        sb.append("    <!-- Number of days to span break reference archive -->\n");
        sb.append("    <max_break_ref>" + Integer.toString(max_break_ref) + "</max_break_ref>\n");
        sb.append("    <!-- Auto maintenance log archive run count -->\n");
        sb.append("    <max_log>" + Integer.toString(max_log) + "</max_log>\n");
        sb.append("    <!-- Matching of duplicate entries -->\n");
        sb.append("    <match_duplicates>" + match_duplicates + "</match_duplicates>\n");
        sb.append("    <!-- Pre Matching tolerance for one-sided entries -->\n");
        sb.append("    <one_sided_tolerance>" + one_sided_tolerance + "</one_sided_tolerance>\n");
        sb.append("  </property_info>\n\n");

        sb.append("  <!-- FILE INFORMATION SECTION -->\n");
        sb.append("  <!-- By convention use 'PrimaryTransfer' and 'SecondaryTransfer' -->\n");
        sb.append("  <!-- as the transfer_names when a standard transfer model is used -->\n");
        sb.append("  <file_info>\n");
        for(int i=0;i<files.size();i++) {
            sb.append(((FileTransfer)files.get(i)).toXML());
        }
        sb.append("  </file_info>\n\n");

        sb.append("  <!-- FIELD INFORMATION SECTION -->\n");
        sb.append("  <!-- Field information including consolidation with the setting -->\n");
        sb.append("  <!--   aggregate           - Add values together and display total-->\n");
        sb.append("  <!--   tolerate            - One value displayed others are ignored -->\n");
        sb.append("  <!--   concatenate_unique  - Every unique value is displayed as a list -->\n");
        sb.append("  <!--   concatenate_verbose - Every value is displayed as a list -->\n");
        sb.append("  <!--   non_tolerate        - Every value must be identical -->\n");
        sb.append("  <!-- If all consolidation fields are set to 'non_tolerate' then consolidation -->\n");
        sb.append("  <!-- is bypassed and a one to one reconciliation is performed. -->\n");
        sb.append("  <field_info>\n");

        sb.append("    <!-- Unique key fields with consolidation attributes -->\n");
        sb.append("    <!-- both consolidation attributes should always be 'non_tolerate' -->\n");
        sb.append("    <!-- the 'format' attribute is currently not used -->\n");
        sb.append("    <keys>\n");
        for(int i=0;i<keys.size();i++) {
            x = (FieldInfo) keys.get(i);
            sb.append(x.toXML());
        }
        sb.append("    </keys>\n");

        sb.append("    <!-- Matching fields with consolidation, tolerance and format attributes -->\n");
        sb.append("    <!-- Available 'format' options are: -->\n");
        sb.append("    <!--   text        - Default setting -->\n");
        sb.append("    <!--   numeric     - Present this field in numeric format -->\n");
        sb.append("    <!--   numeric,3   - Present this field in numeric format with precision 3 -->\n");
        sb.append("    <!--   ignore_case - When matching, case is ignored. -->\n");
        sb.append("    <!-- If 'format' is left blank it assumes 'text' -->\n");
        sb.append("    <!-- If 'tolerance' is left blank it assumes no tolerance -->\n");
        sb.append("    <matches>\n");
        for(int i=0;i<matches.size();i++) {
            sb.append(((FieldInfo)matches.get(i)).toAllXML());
        }
        sb.append("    </matches>\n");

        sb.append("    <!-- Infomration fields with system, consolidation and format attributes -->\n");
        sb.append("    <!-- Fill in 'p_c' and/or 's_c' to indicate the side of the field -->\n");
        sb.append("    <!-- the 'format' attribute is currently not used -->\n");
        sb.append("    <info>\n");
        for(int i=0;i<info.size();i++) {
            sb.append(((FieldInfo)info.get(i)).toXML());
        }
        sb.append("    </info>\n");
        sb.append("  </field_info>\n\n");

        sb.append("  <!-- MANAGEMENT INFORMATION SECTION -->\n");
        sb.append("  <management_info>\n");
        sb.append("    <!-- The cost centre to be charged for this deployment -->\n");
        sb.append("    <cost_centre>" + cost_centre + "</cost_centre>\n");
        sb.append("    <!-- The charge band for this reconciliation (1,2,3,5,7,11) -->\n");
        sb.append("    <cost_band>" + cost_band + "</cost_band>\n");
        sb.append("    <!-- The signoff against the cost centre -->\n");
        sb.append("    <cost_signatory>" + cost_signatory + "</cost_signatory>\n");
        sb.append("    <!-- Cost justification text for 7 and importantly 11 band -->\n");
        sb.append("    <cost_justification>" + cost_justification + "</cost_justification>\n");
        sb.append("    <!-- The name of the sign off for the charged cost centre -->\n");
        sb.append("    <deployment_sponsor>" + deployment_sponsor + "</deployment_sponsor>\n");
        sb.append("    <!-- The reason and resoning behind this deployment -->\n");
        sb.append("    <deployment_reason>" + deployment_reason + "</deployment_reason>\n");
        sb.append("    <!-- Date of deployment -->\n");
        sb.append("    <deployment_date>" + deployment_date + "</deployment_date>\n");
        sb.append("    <!-- The name of the person to authorise access -->\n");
        sb.append("    <access_authority>" + access_authority + "</access_authority>\n");
        sb.append("    <!-- The name of the person to backup the authoriser -->\n");
        sb.append("    <backup_authority>" + backup_authority + "</backup_authority>\n");
        sb.append("    <!-- Business Signoff -->\n");
        sb.append("    <business_signoff>" + business_signoff + "</business_signoff>\n");
        sb.append("    <!-- QA Signoff -->\n");
        sb.append("    <qa_signoff>" + qa_signoff + "</qa_signoff>\n");
        sb.append("    <!-- Deployer Signoff -->\n");
        sb.append("    <deployer_signoff>" + deployer_signoff + "</deployer_signoff>\n");
        sb.append("    <!-- Support Signoff -->\n");
        sb.append("    <support_signoff>" + support_signoff + "</support_signoff>\n");
        sb.append("    <!-- Development Signoff (optional) -->\n");
        sb.append("    <dev_signoff>" + dev_signoff + "</dev_signoff>\n");
        sb.append("  </management_info>\n");
        sb.append("</gprops>\n");

        return sb.toString();
    }

    protected String toXMLTemplate() {
        return(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<gprops>\n" +
        "\n" +
        "  <!-- SETUP INFOMATION SECTION -->\n" +
        "  <setup_info>\n" +
        "    <!-- Unique name identifier for this deployment (single word) -->\n" +
        "    <business_name></business_name>\n" +
        "    <!-- Visual displayed name for the deployment -->\n" +
        "    <display_name></display_name>\n" +
        "    <!-- The sector name for this deployment (single word) -->\n" +
        "    <sector_name></sector_name>\n" +
        "    <!-- The category name for this deployment (single word) -->\n"+
        "    <category_name></category_name>\n" +
        "    <!-- Primary system identifier (single word) -->\n" +
        "    <primary></primary>\n" +
        "    <!-- Secondary system identifier (single word) -->\n" +
        "    <secondary></secondary>\n" +
        "  </setup_info>\n" +
        "\n" +
        "  <!-- PROPERTY INFORMATION SECTION -->\n" +
        "  <property_info>\n" +
        "    <!-- The behaviour of the reconciliation before the data -->\n" +
        "    <!-- is passed through the matching engine -->\n" +
        "    <!-- Options: -->\n" +
        "    <!-- 1) Standard - Standard reconciliation behavior -->\n" +
        "    <!-- 2) Merge - Merge previous results before matching -->\n" +
        "    <tracker_type>Standard</tracker_type>\n" +
        "    <!-- 'true' or 'false' depending if has passed QA -->\n" +
        "    <vaulted>false</vaulted>\n" +
        "    <!-- type and time customer expectation of completion -->\n" +
        "    <expected_completion></expected_completion>\n" +
        "    <!-- If > 0 then delete record if inactive after expiry_count -->\n" +
        "    <expiry_count>1</expiry_count>\n" +
        "    <!-- offset the first seen date as the true data date -->\n" +
        "    <!-- Options: -->\n" +
        "    <!-- 1) none - no offset -->\n" +
        "    <!-- 2) short - an offset of -1 for 5 day week -->\n" +
        "    <!-- 3) long - an offset of -1 for 7 day week -->\n" +
        "    <run_offset>none</run_offset>\n" +
        "    <!-- Debug level for Core Engine (0-5) -->\n" +
        "    <core_debug>0</core_debug>\n" +
        "    <!-- Debug level for Tracker Engine (0-5) -->\n" +
        "    <tracker_debug>0</tracker_debug>\n" +
        "    <!-- Number of days to span match reference archive -->\n" +
        "    <max_match_ref>7</max_match_ref>\n" +
        "    <!-- Number of days to span break reference archive -->\n" +
        "    <max_break_ref>28</max_break_ref>\n" +
        "    <!-- Auto maintenance log archive run count -->\n" +
        "    <max_log>1</max_log>\n" +
        "    <!-- Matching of duplicate entries -->\n" +
        "    <match_duplicates>false</match_duplicates>\n" +
        "    <!-- Pre Matching tolerance for one-sided entries -->\n" +
        "    <one_sided_tolerance>false</one_sided_tolerance>\n" +
        "  </property_info>\n" +
        "\n" +
        "  <!-- FILE INFORMATION SECTION -->\n" +
        "  <!-- By convention use 'PrimaryTransfer' and 'SecondaryTransfer' -->\n" +
        "  <!-- as the transfer_names when a standard transfer model is used -->\n" +
        "  <file_info>\n" +
        "    <!-- single/multiple file transfer information -->\n" +
        "    <transfer_info>\n" +
        "      <!-- unique name identifier for the transfer -->\n" +
        "      <transfer_name></transfer_name>\n" +
        "      <!-- transfer source information -->\n" +
        "      <transfer_source>\n" +
        "        <!-- transfer type (SSH,SSH_NONDATA,FTP,FTP_NONDATA or Script) -->\n" +
        "        <type></type>\n" +
        "        <!-- transfer information -->\n" +
        "        <login></login>\n" +
        "        <password></password>\n" +
        "        <machine></machine>\n" +
        "        <path></path>\n" +
        "        <filename></filename>\n" +
        "      </transfer_source>\n" +
        "      <!-- Script to run on transfer -->\n" +
        "      <transfer_script></transfer_script>\n" +
        "      <!-- System associated with this transfer -->\n" +
        "      <system_name></system_name>\n" +
        "      <!-- Type an time of delivery (type:time) -->\n" +
        "      <delivery_schedule></delivery_schedule>\n" +
        "      <!-- contact information for file queries -->\n" +
        "      <contacts>\n" +
        "        <contact>\n" +
        "          <name></name>\n" +
        "          <number></number>\n" +
        "          <email></email>\n" +
        "        </contact>\n" +
        "        <cover>\n" +
        "          <name></name>\n" +
        "          <number></number>\n" +
        "          <email></email>\n" +
        "        </cover>\n" +
        "      </contacts>\n" +
        "      <!-- additional information of use -->\n" +
        "      <notes></notes>\n" +
        "    </transfer_info>\n" +
        "  </file_info>\n" +
        "\n" +
        "  <!-- FIELD INFORMATION SECTION -->\n" +
        "  <!-- Field information including consolidation with the setting -->\n" +
        "  <!--   aggregate           - Add values together and display total-->\n" +
        "  <!--   tolerate            - One value displayed others are ignored -->\n" +
        "  <!--   concatenate_unique  - Every unique value is displayed as a list -->\n" +
        "  <!--   concatenate_verbose - Every value is displayed as a list -->\n" +
        "  <!--   non_tolerate        - Every value must be identical -->\n" +
        "  <!-- If all consolidation fields are set to 'non_tolerate' then consolidation -->\n" +
        "  <!-- is bypassed and a one to one reconciliation is performed. -->\n" +
        "  <field_info>\n" +
        "    <!-- Unique key fields with consolidation attributes -->\n" +
        "    <!-- both consolidation attributes should always be 'non_tolerate' -->\n" +
        "    <!-- the 'format' attribute is currently not used -->\n" +
        "    <keys>\n" +
        "      <field p_c=\"non_tolerate\" s_c=\"non_tolerate\" format=\"text\"></field>\n" +
        "    </keys>\n" +
        "    <!-- Matching fields with consolidation, tolerance and format attributes -->\n" +
        "    <!-- Available 'format' options are: -->\n" +
        "    <!--   text        - Default setting -->\n" +
        "    <!--   numeric     - Present this field in numeric format -->\n" +
        "    <!--   numeric,3   - Present this field in numeric format with precision 3-->\n" +
        "    <!--   ignore_case - When matching, case is ignored. -->\n" +
        "    <!-- If 'format' is left blank it assumes 'text' -->\n" +
        "    <!-- If 'tolerance' is left blank it assumes no tolerance -->\n" +
        "    <matches>\n" +
        "      <field p_c=\"\" s_c=\"\" format=\"text\" tolerance=\"0\"></field>\n" +
        "    </matches>\n" +
        "    <!-- Infomration fields with system, consolidation and format attributes -->\n" +
        "    <!-- Fill in 'p_c' and/or 's_c' to indicate the side of the field -->\n" +
        "    <!-- the 'format' attribute is currently not used -->\n" +
        "    <info>\n" +
        "       <field p_c=\"\" s_c=\"\" format=\"text\"></field>\n" +
        "     </info>\n" +
        "  </field_info>\n" +
        "\n" +
        "  <!-- MANAGEMENT INFORMATION SECTION -->\n" +
        "  <management_info>\n" +
        "    <!-- The cost centre to be charged for this deployment -->\n" +
        "    <cost_centre></cost_centre>\n" +
        "    <!-- The charge band for this reconciliation (1,2,3,5,7,11) -->\n" +
        "    <cost_band></cost_band>\n" +
        "    <!-- The signoff against the cost centre -->\n" +
        "    <cost_signatory></cost_signatory>\n" +
        "    <!-- Cost justification text for 7 and importantly 11 band -->\n" +
        "    <cost_justification></cost_justification>\n" +
        "    <!-- The name of the sign off for the charged cost centre -->\n" +
        "    <deployment_sponsor></deployment_sponsor>\n" +
        "    <!-- The reason and resoning behind this deployment -->\n" +
        "    <deployment_reason></deployment_reason>\n" +
        "    <!-- Date of deployment -->\n" +
        "    <deployment_date></deployment_date>\n" +
        "    <!-- The name of the person to authorise access -->\n" +
        "    <access_authority></access_authority>\n" +
        "    <!-- The name of the person to backup the authoriser -->\n" +
        "    <backup_authority></backup_authority>\n" +
        "    <!-- Business Signoff -->\n" +
        "    <business_signoff></business_signoff>\n" +
        "    <!-- QA Signoff -->\n" +
        "    <qa_signoff></qa_signoff>\n" +
        "    <!-- Deployer Signoff -->\n" +
        "    <deployer_signoff></deployer_signoff>\n" +
        "    <!-- Support Signoff -->\n" +
        "    <support_signoff></support_signoff>\n" +
        "    <!-- Development Signoff (optional) -->\n" +
        "    <dev_signoff></dev_signoff>\n" +
        "  </management_info>\n" +
        "\n" +
        "</gprops>\n");
    }



    public String toDebug() {
        FieldInfo field_elem;
        StringBuffer sk = new StringBuffer("[");
        StringBuffer sm = new StringBuffer("[");
        StringBuffer si = new StringBuffer("[");

        for(int i=0;i< getKeys().size();i++) {
            field_elem = (FieldInfo) keys.get(i);
            sk.append(field_elem.FieldNametoString()+ " ");
        }
        sk.append("]");

        for(int i=0;i< getMatches().size();i++) {
            field_elem = (FieldInfo) matches.get(i);
            sm.append(field_elem.FieldNametoString()+ " ");
        }
        sm.append("]");

        for(int i=0;i< getInfos().size();i++) {
            field_elem = (FieldInfo) info.get(i);
            si.append(field_elem.FieldNametoString()+ " ");
        }
        si.append("]");

        return  "\nGprops:\n" +
        "\tReference Name: [" + this.business_name + "]\n" +
        "\tBusiness Name : [" + this.business_name + "]\n" +
        "\tDisplay Name  : [" + this.display_name + "]\n" +
        "\tSector Name   : [" + this.sector_name + "]\n" +
        "\tCategory Name : [" + this.category_name + "]\n" +
        "\tPrimary       : [" + this.primary + "]\n" +
        "\tSecondary     : [" + this.secondary + "]\n" +
        "\tDebug Level   : [" + this.tracker_debug + "]\n" +
        "\tIs Vaulted    : [" + this.getVaulted() + "]\n" +
        "\tKey Fields    : " + sk + "\n" +
        "\tMatch Fields  : " + sm + "\n" +
        "\tInfo Fields   : " + si + "\n" +
        "\tNumeric Fields: " + (this.getNumericFields()).toString() +
        //"\tPrecision Fields: " + (this.getPrecisionFields()).toString() +
        "\tIgnore Case Fields: " + (this.getIgnoreCaseFields()).toString() + "\n";
    }

}
