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

import org.opengrass.templates.DataHandler;
import org.opengrass.exceptions.OpenGrassException;
import org.opengrass.datastores.gpropstore.*;
import java.util.*;

/**
* Sets and manipulates list of layouts available for a specific reconcilliation. Information about these can be accessed via various methods.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       */

public class LayoutStore {

  /** Name of business area */
  private String business;
  
  /** Hash of layout objects */
  private Hashtable layouts;
  /** Data handler responsible for reading / writing to resource that holds layout info */
  private DataHandler layoutHandler;
  /** Debug level */
  private int DEBUG;

  /**
  * Creates a new LayoutStore object
  */
  public LayoutStore (Gprops configuration) {
    DEBUG = configuration.getDebug();
    business = configuration.getBusinessName();

    if (DEBUG > 0) System.err.println("INFO: Initialising LayoutBean");
    String OPENGRASS_HOME = System.getProperty("OPENGRASS_HOME");
    String fileName = OPENGRASS_HOME + "/ertba/" + business + "/conf/" + business + ".layout";
    if (DEBUG > 1) { System.err.println("INFO: Getting Layouts from " + fileName); }
    layoutHandler = new LayoutXMLReaderWriter(fileName, DEBUG);
  }

  /** Gets the data from the resource set via the layout handler */
  public void parse()  throws OpenGrassException {
    layoutHandler.parse();
    layouts = layoutHandler.getData();
  }

  /** Writes data  to the resource set in setLayoutSource via the layout handler */
  private void write() throws OpenGrassException {
    layoutHandler.write(layouts);
  }

/**
* Returns a Vector containing a list of available layouts
*/
  public Vector getLayoutNames () {
    Vector layoutList = new Vector();
    for (Enumeration e = layouts.keys(); e.hasMoreElements();) {
      layoutList.add((String) e.nextElement());
    }
    return layoutList;
  }

/**
* Returns a Layout Object of the specified Name
*/
  public Layout getLayout (String name) {
    if (layouts.containsKey(name)) { return (Layout) layouts.get(name); }
    else { return null; }
  }

/**
* Removes the specified layout
*/
  public void removeLayout (String name) throws OpenGrassException {
      layouts.remove(name);
      this.write();
  }

/**
* Creates a new layout passed a layout objst
*/
  public void setLayout(Layout l) throws OpenGrassException {
    if (DEBUG > 1) { System.err.println("INFO: Setting layout '" + l.getName() + "'"); }
    layouts.put(l.getName(), l);
    write();
 }

/**
* Creates a new layout
*/
  public void setLayout(String name, int fontSize, int rowCount, boolean isStacked, int dateOffset, String title, String typeFilter, Vector fields, Hashtable distlist) throws OpenGrassException {
    if (DEBUG > 1) { System.err.println("INFO: Setting layout '" + name + "'"); }
    Layout tmp = new Layout(name, DEBUG);
    tmp.setFontSize(fontSize);
    tmp.setRowCount(rowCount);
    tmp.setStacked(isStacked);
    tmp.setDateOffset(dateOffset);
    tmp.setTypeFilter(typeFilter);
    tmp.setFields(fields);
    tmp.setTitle(title);
    tmp.setDistributionList(distlist);
    layouts.put(name, tmp);
    write();
 }
}
