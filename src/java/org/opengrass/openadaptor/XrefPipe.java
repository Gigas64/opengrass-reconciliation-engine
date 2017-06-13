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


/**
***   Here is how to use XrefPipe in an adaptor
***
*** eg:
***   A.C2.ClassName         = org.opengrass.openadaptor.XrefPipe
***   A.C2.Attribute1            = Book
***   A.C2.File1           = xrefBook.xml
***   A.C2.Attribute2            = Trade
***   A.C2.File2           = xrefTrade.xml
***
**/

package org.opengrass.openadaptor;

// OpenAdaptor classes
import org.openadaptor.dataobjects.*;
import org.openadaptor.adaptor.*;

// Collections classes
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Properties;

//.OpenGrass helper classes
import org.opengrass.openadaptor.XrefHandler;


public class XrefPipe extends AbstractSimplePipe
{
  public HashMap holdsAllXRefs;
  public ArrayList attributes;

  /**
  *** Initialise from properties object.
  ***
  *** @param props    Properties object
  *** @param prefix   Prefix string to search for properties
  ***
  *** @exception IbafException
  **/

  public void init(String name, Properties props, String prefix, Controller controller)
      throws IbafException
  {
    super.init(name, props, prefix, controller);

    holdsAllXRefs = new HashMap();
    HashMap mapping = null;
    XrefHandler handler = null;

    // this loop obtains all attributes and their associated xml files from the props object
    // then it parses the xml into a HashMap.
    // finally it puts each HashMap into a big HashMap with the attribute as its key
    // if an attribute has no file with it, it's ignored

    settingXrefs: for (int i = 0; props.get(prefix + "." + "Attribute" + (i+1)) != null; i++)
    {
      String attribute = props.getProperty(prefix + "." + "Attribute" + (i+1));
      String file = props.getProperty(prefix + "." + "File" + (i+1));

      if ( file == null )
      {
        System.err.println( "ERROR: XrefPipe: init(): Attribute: " +
                  attribute +
                  " has no associated file in Properties file." );
        continue settingXrefs;
      }

      handler = new XrefHandler( file );
      mapping = handler.getXrefs();

      holdsAllXRefs.put( attribute, mapping );
    }

    if ( holdsAllXRefs.isEmpty() )
    {
      System.err.println( "WARN: XrefPipe init(): X-referencing empty." );
    }
    else
    {
      displayXrefs();
    }
  }

    /**
     * Takes an array of DataObjects and calls doXref() on each
     *
     * @param inDobs a OpenAdaptor DataObject array
     * @exception PipelineException
     * @return The amended DataObject array
     */

  protected DataObject[] transformDataObjects (DataObject[] inDobs)
    throws PipelineException
  {
    if (inDobs == null || inDobs.length == 0)
    {
      throw new PipelineException("received null or empty dataobject array",
                    PipelineException._HOSPITAL);
    }

    DataObject[] outDobs = inDobs;

    dataObjectTransformation: for ( int i=0; i < inDobs.length ; i++ )
    {
      if ( inDobs[i] == null )
      {
        continue dataObjectTransformation;
      }

      outDobs[i] = doXref ( inDobs[i] );
    }

    return outDobs;
  }


    /**
     * Takes a DataObject and performs x-referencing
     * for all attributes contained in holdsAllXRefs
     *
     * @param dataObject a OpenAdaptor DataObject
     * @exception InvalidParameterException
     * @return The amended DataObject
     */

  private DataObject doXref( DataObject dataObject )
  {
    if ( holdsAllXRefs == null )
    {
      System.err.println( "ERROR: XrefPipe: doXref() holdsAllXRefs is null" );
      return dataObject;
    }
    if ( holdsAllXRefs.isEmpty() )
    {
      System.err.println( "ERROR: XrefPipe: doXref() holdsAllXRefs is empty" );
      return dataObject;
    }

    Iterator attributes = holdsAllXRefs.keySet().iterator();

    HashMap mapping = null;
    String attribute = null;
    String potentialFrom = null;
    String to = null;

    // this loops through all the attributes held in holdsAllXRefs HashMap
    // if the value in the dataobject of that attribute matches a x-referencing
    // held for that attribute in the holdsAllXRefs HashMap a swap is made
    // the Data Object is amended

    xreferencing: while ( attributes.hasNext() )
    {
      attribute = ( String ) attributes.next();
      mapping = ( HashMap ) holdsAllXRefs.get( attribute );
      try
      {
        potentialFrom = ( String ) dataObject.getAttributeValue ( attribute );
      }
      catch ( InvalidParameterException e )
      {
        System.err.println( "INFO: XrefPipe: doXref(): Incoming DataObject does not contain attribute:"  + attribute );
        System.err.println( e.getMessage() );
        continue xreferencing;

      }
      if ( mapping.containsKey( potentialFrom ) )
      {
        to = ( String ) mapping.get( potentialFrom );
        try
        {
          dataObject.setAttributeValue( attribute, to );
        }
        catch ( InvalidParameterException e )
        {
          System.err.println( "INFO: XrefPipe" );
          System.err.println( "INFO: Incoming DataObject does not contain attribute:" );
          System.err.println( "INFO: " + attribute );
          System.err.println( e.getMessage() );
          continue xreferencing;
        }
      }

    }

    // the amended data object is returned
    return dataObject;
  }

  private void displayXrefs()
  {
      System.err.println( "TRACE: XrefPipe: displayXrefs(): X-Referencing as follows:" );
      Iterator iterator = holdsAllXRefs.keySet().iterator();

      while ( iterator.hasNext() )
      {
        String att = ( String ) iterator.next();
        System.err.println( "TRACE: Attribute: " + att );
        System.err.println( "TRACE: From\tTo" );

        HashMap mapping = (HashMap)holdsAllXRefs.get( att );
        Iterator it = mapping.keySet().iterator();

        while ( it.hasNext() )
        {
          String from = (String) it.next();
          System.err.println( "TRACE: " + from + "\t" + mapping.get ( from ) );
        }

      }
  }

}