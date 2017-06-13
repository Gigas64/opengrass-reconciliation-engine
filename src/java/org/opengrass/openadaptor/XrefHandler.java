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

// io classes to find the file
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

// collections classes
import java.util.Iterator;
import java.util.HashMap;

// the sax parser
import org.apache.xerces.parsers.SAXParser;

// sax classes written by David Megginson and which are
// published open source at www.megginson.com
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

/**

*** @data 3rd May 2001
***
*/

/**
  XrefHandler is an implementation of an event handler
  for a SAX parser ( here from Apache - xerces ). It has been
  written for the.opengrass project ( under Clive Ryder ).
  It's constructed with an xml filename which it then parses
  and constructs a HashMap.

  eg here's a xref file.

  <?xml version="1.0">
  <x-refs>
    <x-ref>
      <value>Book1</value>
      <new_value>Book</new_value>
    </xref>
    <x-ref>
      <value>Book2</value>
      <new_value>Book</new_value>
    </xref>
    <x-ref>
      <value>Dollar</value>
      <new_value>FXTrade</new_value>
    </xref>
  </x-refs>

  The HashMap will be a follows:
  Key       Value
  Book1     Book
  Book2     Book
  Dollar      FXTrade

  This HashMap can be obtained using the method:
  getXrefs().
*/
/**
*** This class is written as a helper class for org.opengrass.openadaptor.XrefPipe
***
*** @date 24th May 2001
**/

public class XrefHandler extends DefaultHandler
{

  // Holds the xrefs
  private HashMap xrefs;

  // Flag to determine if callback characters are inside a to or value tags
  private boolean isFrom;
  private boolean isTo;

  // Strings to hold value and to strings ready for adding to hashmap
  private String value;
  private String new_value;

  // Holds characters which make up a filter as they are added
  private StringBuffer buffer;

  // The xml file to be parsed
  private String xrefFile;

  // SAX parser associated with this handler
  private XMLReader xr = null;

  public XrefHandler( String filename )
  {
    super();

    xrefs     = new HashMap();
    xrefFile    = filename;

    isFrom      = false;
    isTo      = false;

    // Sets the SAX parser
    xr        = new SAXParser();
    if ( xr != null )
    {
      System.err.println( "TRACE: SAX Parser created" );
    }

    // Tell the parser where to send the callbacks
    xr.setContentHandler( this );
    xr.setErrorHandler( this );

    // Builds up the HashMap of xrefs from the xml file
    buildXrefs();

  }

  private void buildXrefs()
  {
    try
    {
      // Parse the xml file
      FileReader fr = new FileReader( xrefFile );
      System.err.println( "TRACE: XML file " + xrefFile + " found" );

      xr.parse( new InputSource( fr ) );
      System.err.println( "TRACE: XML file " + xrefFile + " parsed" );

    }
    catch ( FileNotFoundException e )
    {
      System.err.println( "ERROR: XrefHandler: buildXrefs(): " );
      System.err.println( "ERROR: " + xrefFile + " not found.");
      System.err.println( "ERROR: " + e.getMessage() );
    }
    catch ( IOException e )
    {
      System.err.println( "ERROR: XrefHandler: buildXrefs():" );
      System.err.println( "ERROR: Problem creating InputSource from " + xrefFile );
      System.err.println( "ERROR: " + e.getMessage() );
    }
    catch ( SAXException e )
    {
      System.err.println( "ERROR: XrefHandler: buildXrefs():" );
      System.err.println( "ERROR: Problem parsing " + xrefFile );
      System.err.println( "ERROR: " + e.getMessage() );
    }
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
    // at the beginning of a new value tag
    // set the isFrom flag to true and create a new StringBuffer object

    if ( name.equals( "value" ) )
    {
      isFrom = true;
      buffer = new StringBuffer();
    }

    if ( name.equals( "new_value" ) )
    {
      isTo = true;
      buffer = new StringBuffer();
    }
  }

  public void endElement( String uri, String name, String qName )
  {
    // at the end of value tag add the contents of the StringBuffer
    // to currentFrom String then set the StringBuffer to null
    // ready for garbage collection and the flag to false

      if ( name.equals( "value" ) )
    {
      if ( buffer.length() != 0 )
      {
        value = buffer.toString();
      }
      buffer = null;
      isFrom = false;
    }

      if ( name.equals( "new_value" ) )
    {
      if ( buffer.length() != 0 )
      {
        new_value = buffer.toString();
      }
      buffer = null;
      isTo = false;
    }

    if ( name.equals( "value_reference" ) )
    {
      if ( value == null || new_value == null )
      {
        System.err.println( "ERROR: XrefHandler: endElement(): " );
        System.err.println( "ERROR: Null Value found in mapping:" );
        System.err.println( "ERROR: From: [" + value + "]" );
        System.err.println( "ERROR: To: [" + new_value + "]" );
        System.err.println( "ERROR: Check that " + xrefFile + " is correctly formatted" );
      }
      else
      {
        xrefs.put( value, new_value );
      }

      value = null;
      new_value = null;
    }
  }

  public void characters ( char[] ch, int start, int length )
  {
    // if inside value tag add characters to buffer
    if ( isFrom || isTo )
    {
      for(int i=start;i<length+start;i++)
      {
        buffer.append( ch[i] );
      }
    }
  }

  public HashMap getXrefs()
  {
    return xrefs;
  }

  public void displayXrefs()
  {
    System.err.println( "TRACE: Displaying Xrefs" );
    System.err.println( "TRACE: From\t\tTo" );

    Iterator iterator = xrefs.keySet().iterator();
    while ( iterator.hasNext() )
    {
      String value = ( String ) iterator.next();
      String new_value = ( String ) xrefs.get( value );
      System.err.println( "TRACE: " + value + "\t\t" + new_value );
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
      XrefHandler handler = new XrefHandler ( args[0] );

    handler.displayXrefs();

  }


} // End of Class
