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
 ** @ersion  : 1.00.000
 ** Date     : 26/08/2004
 */

package org.opengrass.datastores.layoutstore;

import org.opengrass.exceptions.OpenGrassException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* Holds information about a reconcillition layout.
**/
public class Layout {

  /** Layout Name **/
  private String name;
  /** Title for top of report page */
  private String title;
  /** Type e.g One - Sided, Breaks, Duplicates */
  private String typeFilter;
  /** Number of days required between run date and display date for report (-1 = yesterday) */
  private int dateOffset;
  /** The font size */
  private int fontSize;
  /** The row count */
  private int rowCount;
  /** True if the layout is required to be stacked */
  private boolean stacked;
  /** List of match, info (and comment?) fields to be displayed */
  private Vector fields;
  /** Hash of name - email pairs */
  private Hashtable distributionList;
  /** Debug level */
  private int DEBUG;

  /**
  * Constructs a Layout object with name set to the specified string and a DEBUG of zero
  **/
  public Layout (String string) {
    this(string, 0);
  }
  /**
  * Constructs a Layout object with specified name and DEBUG
  **/

  public Layout (String string, int i) {
    fields = new Vector();
    distributionList = new Hashtable();
    name = string;
    DEBUG = i;
    if (DEBUG > 2) {
      System.err.println("\nINFO: Creating new layout\n\tSetting name: " + name );
    }
  }

  /**
  * This returns the name of the layout
  **/
  public String getName () {
    return name;
  }

  /**
  * This returns title of the layout
  **/
  public String getTitle () {
    return title;
  }

  /** returns the font Size */
  public int getFontSize () {
    return fontSize;
  }

  /** Sets the font size */
  public void setFontSize(int i) {
    fontSize = i;
  }

  /** returns the row count */
  public int getRowCount () {
    return rowCount;
  }

  /** Sets the row count */
  public void setRowCount(int i) {
    rowCount = i;
  }

  /** Is true if the layout is stacked */
  public boolean isStacked () {
    return stacked;
  }

  public void setStacked(boolean b) {
    stacked = b;
  }

  /**
  *
  **/
  public String getOffsetFromDate(String date) throws OpenGrassException {
    SimpleDateFormat recdf = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat layoutdf = new SimpleDateFormat("EEE MMM d yyyy");
    Calendar g = Calendar.getInstance();
    Date recDate;
    Date layoutDate;

    try {
      recDate =  (recdf.parse(date));
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Cant parse run date " + date + "\nFormat must be [EEE MMM d hh:mm:ss yyyy]");
    }
    g.setTime(recDate);
    if (dateOffset < 0) {
      for( int i = 0; i > dateOffset; i--) {
        if (g.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
          g.add(Calendar.DATE, -2);
        }
        g.add(Calendar.DATE, -1);
      }
    }
    else if (dateOffset > 0) {
      for( int i = 0; i < dateOffset; i++) {
        if (g.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
          g.add(Calendar.DATE, 2);
        }
        g.add(Calendar.DATE, 1);
      }
    }
    layoutDate = g.getTime();
    return layoutdf.format(layoutDate);
  }

  /**
  * This returns the date dateOffset of the layout
  **/
  public int getDateOffset () {
      return dateOffset;
  }

  /**
  * This returns the layout type (e.g One-Sided, Break, etc)
  **/
  public String getTypeFilter () {
      return typeFilter;
  }

  /**
  * This returns the fields of the layout
  **/
  public Vector getFields () {
      return fields;
  }

  /**
  * This returns the distribution list of the layout
  **/
  public Hashtable getDistributionList () {
      return distributionList;
  }

  /**
  * Sets layout title to specified string
  **/
  public void setTitle ( String string )  {
    title = string;
    if (DEBUG > 2) {
      System.err.println("\tSetting title: " + title);
    }
  }

  /**
  * Sets layout type to specified string
  **/
  public void setTypeFilter ( String string )  {
    typeFilter = string;
    if (DEBUG > 2) {
      System.err.println("\tSetting Type Filter: " + typeFilter);
    }
  }

  /**
  * Sets layout offset
  **/
  public void setDateOffset ( int i )  {
    dateOffset = i;
    if (DEBUG > 2) {
      System.err.println("\tSetting dateOffset: " + dateOffset);
    }
  }

  /**
  * Replaces current vector of fields with that specified
  **/
  public void setFields ( Vector fvector ) {
    resetFields();
    fields = fvector;
    if (DEBUG > 2) {
      System.err.println("\tSetting fields: " + fvector);
    }
  }

  /**
  * Removes all the current fields vector
  **/
  public void resetFields () {
    fields.clear();
  }

  /**
  * Replaces current distribution list with that specified
  **/
  public void setDistributionList ( Hashtable hash ) {
    distributionList.clear();
    distributionList = hash;
    if (DEBUG > 2) {
      System.err.println("\tSetting distribution list: " + distributionList);
    }
  }

  /**
  * removes all current distribution list
  **/
  public void resetDistributionList () {
    distributionList.clear();
  }

  /**
  * Appends the specified field name to the list of fields
  **/
  public void addAllFields (Vector f) {
    fields.addAll(f);
    if (DEBUG > 2) {
      System.err.println("\tAdding fields: " + f);
    }
  }

  /**
  * Appends the specified field name to the list of fields
  **/
  public void addField (String string) {
    fields.add(string);
    if (DEBUG > 2) {
      System.err.println("\tAdding field: " + string);
    }
  }

  /**
  * Adds the name and email to the distribution list
  **/
  public void addPerson (String name, String email) {
    distributionList.put(name,email);
    if (DEBUG > 2) {
      System.err.println("\tAdding person: " + name + " " + email);
    }
  }

  /**
  * remove the name and email to the distribution list
  **/
  public void removePerson (String name) {
    distributionList.remove(name);
    if (DEBUG > 2) {
      System.err.println("\tRemoving person: " + name);
    }
  }

  /** Returns a string representation of the layout */
  public String toString ()  {
    return "\n*****LAYOUT******\nName: " + name
             + "\nTitle: " + title
             + "\nFontSize: " + fontSize
             + "\nRowCount: " + rowCount
             + "\nIsStacked: " + stacked
             + "\nDateOffset: " + dateOffset
             + "\nFields: " + fields
             + "\nDistributionList: " + distributionList;
  }

  /** Returns an XML representation of the Layout */
  public String toXMLString () {
    StringBuffer sb = new StringBuffer(
                   "  <layout name=\"" + name + "\" stacked=\"" + stacked + "\">\n"
                 + "    <title>" + title + "</title>\n"
                 + "    <font_size>" + fontSize + "</font_size>\n"
                 + "    <row_count>" + rowCount + "</row_count>\n"
                 + "    <date_offset>" + dateOffset + "</date_offset>\n"
                 + "    <type_filter>" + typeFilter + "</type_filter>\n"
                 + "    <distribution_list>\n");
    for (Enumeration j = distributionList.keys(); j.hasMoreElements();) {
        String n = (String) j.nextElement();
        sb.append("      <person>\n"
                + "        <name>" + n + "</name>\n"
                + "        <email>" + (String) distributionList.get(n) + "</email>\n"
                + "      </person>\n");
    }
    sb.append("    </distribution_list>\n" + "    <fields>\n");
    for (int i = 0; i < fields.size(); i++) {
        sb.append("      <field>" + fields.get(i) + "</field>\n");
    }
    sb.append("    </fields>\n" + "  </layout>\n");
    return sb.toString();
  }

}
