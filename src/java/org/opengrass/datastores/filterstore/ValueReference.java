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


/** Hold reference info about a user added value
 **
 ** @version 1.00
 **/

import org.opengrass.utils.DateStrings;
import org.opengrass.utils.XMLiser;


public class ValueReference {

  /** Check Authorisation */
  private boolean _auth;
  /** Authorisation id */
  private String _auth_id;
  /** Predicate value */
  private String _value;
  /** Value to change to */
  private String _new_value;
  /** Comment */
  private String _text;
  /** Who assigned this value */
  private String _user_id;
  /** Date value added */
  private String _date;

  /** Constructor. Filter date will automatically be set to todays date.
  **
  ** @param value of predicate
  ** @param new_value value to change to
  ** @param text business logic
  ** @param user_id who created/added this ValueReference
  ** @param auth if ValueReference is authorised
  ** @param auth_id the authorizer of the ValueReference
  **
  */
  public ValueReference(String value, String new_value, String text, String user_id, boolean auth, String auth_id) {
    _value = value;
    _new_value = new_value;
    _text = text;
    _user_id = user_id;
    _auth = auth;
    _auth_id = auth_id;
    setCurrentDate();
  }

  /** Constructor
  **
  ** @param value of predicate
  ** @param new_value value to change to
  ** @param text business logic
  ** @param user_id who created/decommisioned this ValueReference
  ** @param date date ValueReference created/decommissioned
  ** @param auth if ValueReference is authorised
  ** @param auth_id the authorizer of the ValueReference
  */
  public ValueReference(String value, String new_value, String text, String user_id, String date,  boolean auth, String auth_id) {
    _value = value;
    _new_value = new_value;
    _text = text;
    _user_id = user_id;
    _date = date;
    _auth = auth;
    _auth_id = auth_id;
  }


  /** Returns escaped value of predicate
   ** @return escaped value of predicate
   **
   */
  public String getKeyValue() {
    return(XMLiser.escapeCharsForKey(_value));
  }

  /** Returns value of predicate
   ** @return value of predicate
   **
   */
  public String getValue() {
    return(_value);
  }

  /** Returns value to change to
   **
   ** @return changed value String
   */
  public String getNewValue() {
    return(_new_value);
  }

  /** Returns business logic text
   **
   ** @return business logic text
   */
  public String getText() {
    return(_text);
  }

  /** Returns ref of person who created this ValueRef
   **
   ** @return name of person who created this ValueRef
   */
  public String getUserId() {
    return(_user_id);
  }

  /** Returns date ValueRef created
   **
   ** @return date ValueRef created
   */
  public String getDate() {
    return(_date);
  }

  /** Returns if this ValueRef should be authorized
   **
   ** @return boolean authorized
   */
  public boolean getAuth() {
    return(_auth);
  }

  /** Returns the name of the authoriser
   **
   ** @return boolean authorizer
   */
  public String getAuthId() {
    return(_auth_id);
  }

  /** Sets the authorisation
   **
   ** @param boolean authorized
   ** @param String authorizer
   */
  public void setAuth(boolean auth, String auth_id) {
    _auth = auth;
    _auth_id = auth_id;
  }

  /** Sets the ValueRef date to the current date */
  private void setCurrentDate() {
    _date = DateStrings.getCurrentDate();
  }

  /**
   ** Decommissions the ValueRef. The user_id and date are reset.
   ** The String "(decommissioned)" is added to the text
   **
   ** @param user_id id of decommissioner
   */
  protected void decommission (String user_id) {
    _user_id =  user_id;
    setCurrentDate();
    _text = "(decommissioned)" + _text;
  }

  /** Returns String representation of filter
   **
   ** @return String representation of filter
   */
  public String toString() {
    return ("Xref: " + getValue() + " -> " + getNewValue() + " - Text: " + getText() +
            " - User ID: " + getUserId() + " - Date: " + getDate() +
            " - Auth: " + Boolean.valueOf(getAuth()).toString() +
            " - Auth ID: " + getAuthId() + "\n");
  }

  /** Returns XML Representation of filter
   **
   ** @return XML Representation of filter
   */
  public String toXMLString() {
    return ("  <value_reference auth='" + XMLiser.escapeChars(Boolean.valueOf(_auth).toString())
           +"' auth_id='" + XMLiser.escapeChars(_auth_id)+ "'>\n"
           +"    <value>" + XMLiser.escapeChars(_value) + "</value>\n"
           +"    <new_value>" + XMLiser.escapeChars(_new_value) + "</new_value>\n"
           +"    <user_id>" +  XMLiser.escapeChars(_user_id) + "</user_id>\n"
           +"    <text>" + XMLiser.escapeChars(_text) + "</text>\n"
           +"    <date>" + XMLiser.escapeChars(_date) + "</date>\n"
           +"  </value_reference>\n");
  }
}

