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
 ** Version: 1.00.02
 ** Date:  27/05/2003
 */

public class FileTransfer {

    protected String transfer_name;
    protected String type;
    protected String login;
    protected String password;
    protected String machine;
    protected String path;
    protected String filename;
    protected String transfer_script;
    protected String system_name;
    protected String delivery_schedule;
    protected String contact_name;
    protected String contact_number;
    protected String contact_email;
    protected String cover_name;
    protected String cover_number;
    protected String cover_email;
    protected String notes;

    public String getTransferName() { return(transfer_name); }
    public String getType() { return(type); }
    public String getLogin() { return(login); }
    public String getPassword() { return(password); }
    public String getMachine() { return(machine); }
    public String getPath() { return(path); }
    public String getFilename() { return(filename); }
    public String getTransferScript() { return(transfer_script); }
    public String getSystemName() { return(system_name); }
    public String getDeliverySchedule() { return(delivery_schedule); }
    public String getContactName() { return(contact_name); }
    public String getContactNumber() { return(contact_number); }
    public String getContactEmail() { return(contact_email); }
    public String getCoverName() { return(cover_name); }
    public String getCoverNumber() { return(cover_number); }
    public String getCoverEmail() { return(cover_email); }
    public String getNotes() { return(notes); }

    public void setTransferName(String s) { transfer_name = s; }
    public void setType(String s) { type = s; }
    public void setLogin(String s) { login = s; }
    public void setPassword(String s) { password = s; }
    public void setMachine(String s) { machine = s; }
    public void setPath(String s) { path = s; }
    public void setFilename(String s) { filename = s; }
    public void setTransferScript(String s) { transfer_script = s; }
    public void setSystemName(String s) { system_name = s; }
    public void setDeliverySchedule(String s) { delivery_schedule = s; }
    public void setContactName(String s) { contact_name = s; }
    public void setContactNumber(String s) { contact_number = s; }
    public void setContactEmail(String s) { contact_email = s; }
    public void setCoverName(String s) { cover_name = s; }
    public void setCoverNumber(String s) { cover_number = s; }
    public void setCoverEmail(String s) { cover_email = s; }
    public void setNotes(String s) { notes = s; }

    protected String toXML() {
        return(
        "\n" +
        "    <!-- single/multiple file transfer information -->\n" +
        "    <transfer_info>\n" +
        "      <!-- unique name identifier for the transfer -->\n" +
        "      <transfer_name>" + transfer_name + "</transfer_name>\n" +
        "      <!-- transfer source information -->\n" +
        "      <transfer_source>\n" +
        "        <!-- transfer type (SSH,SSH_NONDATA,FTP,FTP_NONDATA or Script) -->\n" +
        "        <type>" + type + "</type>\n" +
        "        <!-- transfer information -->\n" +
        "        <login>" + login + "</login>\n" +
        "        <password>" + password + "</password>\n" +
        "        <machine>" + machine + "</machine>\n" +
        "        <path>" + path + "</path>\n" +
        "        <filename>" + filename + "</filename>\n" +
        "      </transfer_source>\n" +
        "      <!-- Script to run on transfer -->\n" +
        "      <transfer_script>" + transfer_script + "</transfer_script>\n" +
        "      <!-- System associated with this transfer -->\n" +
        "      <system_name>" + system_name + "</system_name>\n" +
        "      <!-- Type and time of delivery (type:time) -->\n" +
        "      <delivery_schedule>" + delivery_schedule + "</delivery_schedule>\n" +
        "      <!-- contact information for file queries -->\n" +
        "      <contacts>\n" +
        "        <contact>\n" +
        "          <name>" + contact_name + "</name>\n" +
        "          <number>" + contact_number + "</number>\n" +
        "          <email>" + contact_email + "</email>\n" +
        "        </contact>\n" +
        "        <cover>\n" +
        "          <name>" + cover_name + "</name>\n" +
        "          <number>" + cover_number + "</number>\n" +
        "          <email>" + cover_email + "</email>\n" +
        "        </cover>\n" +
        "      </contacts>\n" +
        "      <!-- additional information of use -->\n" +
        "      <notes>" + notes + "</notes>\n" +
        "    </transfer_info>\n");
    }
}
