package org.opengrass.datastores.filterstore;

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

/** Holds ValueReferences. Uses ValueXMLParser to read and write ValueReferences
 ** to a file and store deleted ValueRefs in another file.
 **
 ** @version 1.00
 **/


import java.util.*;
/** Holds ValueReferences. Uses ValueXMLParser to read and write ValueReferences
 ** to a file and store deleted ValueRefs in another file.
 **
 ** @author Maili Buckingham
 ** @version 1.00
 **/

public class ValueReferenceStore {

  /** Holds active ValueReferences */
  private TreeMap _active_value_refs;
  /** Holds deleted ValueReferences */
  private TreeMap _inactive_value_refs;

  /** Name of file containing active ValueReferences */
  private String _active_values_file;
  /** Name of file containing deleted ValueReferences */
  private String _inactive_values_file;

  /** Parses the file of active ValueReferences */
  private ValueXMLParser _active_file_reader;
  /** Parses the file of inactive ValueReferences */
  private ValueXMLParser _inactive_file_reader;

   /** Constructor
  **
  ** @param business name of business area
  ** @param type ValueReferenceType - must be "filter" or "xref"
  ** @param file_name name of file holding active ValueReferences
  ** @param debug debug level
  */
  public  ValueReferenceStore (String business, String file_name, int debug) {
   
    _active_value_refs = new TreeMap();
    _inactive_value_refs = new TreeMap();
    // Create file names
    String OPENGRASS_HOME = System.getProperty("OPENGRASS_HOME");
    _active_values_file = OPENGRASS_HOME + "/ertba/" + business + "/data/adaptor/" + file_name;
    _inactive_values_file = OPENGRASS_HOME + "/ertba/" + business + "/data/adaptor/" + file_name + ".deleted";
  }

  /** Constructor. Creates ValueReferenceStore will debug level of zero.
  **
  ** @param business name of business area
  ** @param type ValueReferenceType - must be "filter" or "xref"
  ** @param file_name name of file holding active ValueReferences
  ** @param debug debug level
  */
  public  ValueReferenceStore (String business, String fileName) {
    this(business, fileName, 0);
  }

  /** Gets ValueReferences from the files.
   ** <br>OPENGRASS_HOME/ertba/business/data/type/file_name
   ** <br>OPENGRASS_HOME/ertba/business/data/type/file_name.deleted
   */
  public void parse() {
  // Create parsers
    _active_file_reader = new ValueXMLParser(_active_values_file, 5);
    _inactive_file_reader = new ValueXMLParser(_inactive_values_file, 5);
    // parse the Files
    try {
      _active_file_reader.parse();
    }
    catch (Exception e) {
    System.err.println("ERROR: Problem parsing _active_values_file. " + e.getMessage());
  }
    try {
      _inactive_file_reader.parse();
    }
    catch (Exception e) {
    System.err.println("ERROR: Problem parsing _inactive_values_file. " + e.getMessage());
  }
  // get the Values refs from the files
    _active_value_refs = _active_file_reader.getData();
    _inactive_value_refs = _inactive_file_reader.getData();
  }

  /** Returns all ValueReferences
   **
   ** @param the name of the field to sort by
   ** @returns all active ValueReferences
   */
  public Vector getValueRefs(String sortField) {
    Vector recList = new Vector();
    TreeMap sortedRecords = new TreeMap();
    Iterator i =  _active_value_refs.values().iterator();
    while (i.hasNext()) {
      ValueReference vr = (ValueReference) i.next();
      String sortValue;
      if (sortField.equals("New Value")) {
        sortValue = vr.getNewValue() + vr.getValue();
      }
      else if (sortField.equals("Comment")) {
        sortValue = vr.getText() + vr.getValue();
      }
      else if (sortField.equals("Ref")) {
        sortValue = vr.getUserId() + vr.getValue();
      }
      else if (sortField.equals("Date")) {
        sortValue = vr.getDate() + vr.getValue();
      }
      else if (sortField.equals("Auth")) {
        sortValue = Boolean.toString(vr.getAuth()) + vr.getAuthId() + vr.getValue();
      }
      else {
        sortValue = vr.getValue();
      }
      if (! sortedRecords.containsKey(sortValue)) {
        sortedRecords.put(sortValue, new TreeMap());
      }
      ((TreeMap) sortedRecords.get(sortValue)).put(vr.getValue(), vr);
    }
    Iterator pKey = sortedRecords.values().iterator();
    while (pKey.hasNext()) {
      TreeMap t = (TreeMap) pKey.next();
      Iterator sKey = t.values().iterator();
      while (sKey.hasNext()) {
        recList.add(sKey.next());
      }
    }

    return recList;
  }


  /** Creates a new active ValueReference. Creates new ValueReference with specified
   ** parameters and adds to list of active ValueReferences - also adds to the file
   ** containing active ValueReferences.
   **
   ** @param value value of new ValueReference
   ** @param new_value value to change to
   ** @param text text for new ValueReference
   ** @param user_id creator of the ValueReference
   */
  public void addValueRef(String value, String new_value, String text, String userId) {
    // Add new valueReference
    _active_value_refs.put((value), new ValueReference(value, new_value, text, userId, false,""));
    // If there is a deleted one with the same value - delete it
    _inactive_value_refs.remove(value);
     // Write changes to file
    write();
  }

  /** Deletes the active ValueReference with the given value.
   **
   ** @param auth true if ValueReference is authorised
   ** @param auth_id authoriser of the ValueReference
   */
  public void setAuthValue(String value, boolean auth, String auth_id) {
    // Get the valueReference
    if (_active_value_refs.containsKey(value)) {
      ValueReference tmp = (ValueReference) _active_value_refs.get(value);
      tmp.setAuth(auth,auth_id);
      _active_value_refs.remove(value);
      _active_value_refs.put((value),tmp);
      // Write changes to file
      write();
		}
  }

  /** Deletes the active ValueReference with the given value.
   **
   ** @param value value of new ValueReference
   ** @param user_id requestor of removal of the ValueReference
   */
  public void removeValueRef(String value, String user_id) {
    // Get the valueReference
    if (_active_value_refs.containsKey(value)) {
      ValueReference tmp = (ValueReference) _active_value_refs.get(value);
      // Deactivate the ValueReference and move to inactive TreeMap
      tmp.decommission(user_id);
      _inactive_value_refs.put(value, tmp);
      // Removed from the active TreeMap
      _active_value_refs.remove(value);
      // Write changes to file
      write();
    }
  }

  /** Returns a string representation of this ValueReferenceStore. The string
   ** representation consists of XML representations of first the active ValueReferences
   ** and then the inactive (deleted) ValueReferences.
   **
   ** @returns a string representation of the ValueReferenceStore
   */
  public String toString() {
    StringBuffer sb = new StringBuffer("ACTIVE VALUES\n ------------------\n");
    sb.append( activeToXML());
    sb.append("INACTIVE VALUES\n ------------------\n");
    sb.append( inactiveToXML());
    return sb.toString();
  }


  /** Returns an XML string representation of the active ValueReferences. This format is
   ** defined in toXMLString().
   **
   ** @returns an xml representation of the active ValueReferences
   */
  private String activeToXML() {
    return this.toXMLString(_active_value_refs);
  }

  /** Returns an XML string representation of the active ValueReferences. This format is
   ** defined in toXMLString().
   **
   ** @returns an xml representation of the active ValueReferences
   */
  private String inactiveToXML() {
    return this.toXMLString(_inactive_value_refs);
  }

  /** Returns an XML string representation of the ValueReferences.
   **
   ** @returns an xml representation of the active ValueReferences
   */
  public String toXMLString(TreeMap tm) {
    StringBuffer sb = new StringBuffer("<value_references>\n");
    Iterator i =  tm.values().iterator();
    while (i.hasNext()) {
      ValueReference vr = (ValueReference) i.next();
      sb.append(vr.toXMLString());
    }
    sb.append("</value_references>\n");
    return sb.toString();
  }

  /** Writes the stored ValueReferences to a file.
   ** <br>OPENGRASS_HOME/ertba/business/data/type/file_name
   ** <br>OPENGRASS_HOME/ertba/business/data/type/file_name.deleted
   */
  private void write () {
    try {
      _active_file_reader.write(this.activeToXML());
      _inactive_file_reader.write(this.inactiveToXML());
    }
    catch (Exception e) {
      System.err.println("ERROR: " + e.getMessage());
    }
  }

  public static void main (String[] args){
    ValueReferenceStore fb = new ValueReferenceStore("TestRec", "SystemB_Currency.xml");
    fb.parse();
    try {
    System.err.println("Key" + fb.getValueRefs("Datewe"));
    } catch (Exception e) {
      e.printStackTrace(); }
 //   System.err.println(fb.toString());
 //   fb. addValueRef("D\"%", "D", " 1 to a", "Dbuckinm");
//    fb. addValueRef("2", "b", " 2 to b", "Dbuckinm");
//   System.err.println(fb.toString());
//    fb. addValueRef("3", "c", " 3 to 3", "Dbuckinm");
//    fb. removeValueRef("D\"%", "Doatrid");
//    fb. removeValueRef("3", "Dbuckinm");
//    fb. addValueRef("3", "c", " 3 to 3", "Dkanjib");
//   System.err.println(fb.toString());


  }

}

