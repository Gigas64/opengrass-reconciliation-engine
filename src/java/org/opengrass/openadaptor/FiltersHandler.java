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

package org.opengrass.openadaptor;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

/**
  FiltersHandler is an implementation of an event handler
  for a SAX parser ( here from Apache - xerces ). It has been
  written for the.opengrass project ( under Clive Ryder ).
  It's constructed with an xml filename which it then parses
  and constructs an array list of filters stored in xml file.

  eg here's filter file.

  <?xml version="1.0" encoding="UTF-8" ?>

  <filters>
  <filter>singer</filter>
  <filter>diver</filter>
  </filters>

  FiltersHandler when constructed with the name of this file
  will generate an ArrayList containing the strings "singer"
  and "diver".

  This ArrayList can be obtained using the method:
  getFilters().
*/

public class FiltersHandler extends DefaultHandler
{

  // Holds the filters
  private ArrayList filters;

  // Flag to determine if the filter is to be added
  private boolean isAdd;

  // Flag to determine if callback characters are inside a filter tag
  private boolean filterFlag;

  // Holds characters which make up a filter as they are added
  private StringBuffer buildsUpFilter;

  // The name of the xml file to be parsed
  private String filtersFile;

  // SAX parser associated with this handler
  private XMLReader xr;

  public FiltersHandler()
  {
    super();
    filters = new ArrayList();
    filterFlag = false;
  }

  public FiltersHandler( String filename )
  {
    super();

    filters = new ArrayList();
    filterFlag = false;
    filtersFile = filename;

    // Sets the SAX parser
    xr = new org.apache.xerces.parsers.SAXParser();
    System.err.println( "TRACE: SAX Parser created" );

    // Tell the parser where to send the callbacks
    xr.setContentHandler( this );
    xr.setErrorHandler( this );

    // Builds up the ArrayList of filters from the xml file
    buildFilterListFromXMLFile();

  }

  private void buildFilterListFromXMLFile()
  {
    try
    {
      // Parse the xml file
      FileReader fr = new FileReader( filtersFile );
      System.err.println( "TRACE: XML file " + filtersFile + " found" );

      xr.parse( new InputSource( fr ) );
      System.err.println( "TRACE: XML file " + filtersFile + " parsed" );

    }
    catch ( FileNotFoundException e )
    {
      System.err.println( "ERROR: XML File not found"
                      + e.getMessage() );
    }
    catch ( IOException e )
    {
      System.err.println(
        "ERROR: Error creating InputSource from xml file"
                      + e.getMessage() );
    }
    catch ( SAXException e )
    {
      System.err.println( "ERROR: Error parsing xml file"
                      + e.getMessage() );
    }
  }

  public static void main (String args [])
    throws Exception
  {
          if (args.length < 1)
    {
            System.err.println ("Add a filename, dummy");
            System.exit (1);
          }

    // Create an instance of our SAX Application
      FiltersHandler handler = new FiltersHandler ( args[0] );

    handler.displayFilters();

  }

  public void startDocument ()
  {
    // empty
  }

  public void endDocument ()
  {
      // empty
  }


  public void startElement( String uri, String name,
          String qName, Attributes atts )
  {
    // check to see if we are to add this
    if(name.equals("value_reference")) {

      if(atts.getValue("apply")  != null && atts.getValue("apply").equals("false"))
        isAdd = false;
      else
        isAdd = true;
    }

    // at the beginning of a new filter tag
    // set the flag to true and create a new StringBuffer object
    if ( name.equals( "value" ) && isAdd )
    {
      filterFlag = true;
      buildsUpFilter = new StringBuffer();
    }
  }

  public void endElement( String uri, String name, String qName )
  {
    // at the end of filter tag add the contents of the StringBuffer
    // buildsUpFilter to the filters ArrayList
    // then set the StringBuffer to null ready for garbage collection
    // and the flag to false

    if ( name.equals( "value" ) && isAdd )
    {
      if ( buildsUpFilter.length() != 0 )
      {
        filters.add( buildsUpFilter.toString() );
      }
      buildsUpFilter = null;
      filterFlag = false;
    }
  }

  public void characters ( char[] ch, int start, int length )
  {
    // if inside filter flag add characters to String buffer
    // called buildsUpFilter
    if ( filterFlag == true )
    {
      for(int i=start;i<length+start;i++)
      {
        buildsUpFilter.append( ch[i] );
      }
    }
  }

  public ArrayList getFilters()
  {
    return filters;
  }

  public void displayFilters()
  {
    System.err.println( "TRACE: Displaying filters" );

    for ( int i=0; i < filters.size(); i++ )
    {
      System.err.println( "TRACE: " + filters.get(i) );
    }
  }

} // End of Class
