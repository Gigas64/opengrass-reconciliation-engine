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

import java.util.Properties;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
*** ---------------------------------------------------------
*** The necessary classes to obtain the filters from an xml
*** file using FiltersHandler.
*** ---------------------------------------------------------
*/

import java.util.ArrayList;
import java.util.Iterator;

import org.opengrass.openadaptor.FiltersHandler;

import org.openadaptor.dataobjects.DataObjectException;
import org.openadaptor.dataobjects.DataObject;
import org.openadaptor.dataobjects.DOType;

/**
*** @data 3rd May 2001
***
*/

/**
  XMLDOPredicate is a modification of DOPredicate.
  It retrieves the the name of an xml file from the
  Properties object which is passed into its constructor.
  It creates a FiltersHandler object with the filename.
  This in turn parses the xml into an ArrayList which is
  then turned into a long string. The string is loaded into
  a DOAttPredicate and this is stored in the Vector
  _att_predicates.
*/

public class XMLDOPredicate
{
  /**
  *** type name
  **/

  protected String _type_name;

  /**
  *** if this is true and type condition equalates to true and
  *** all of the attribute predicate return true, then predicate
  *** will evaluate to true. If this is false then the opposite
  *** applies.
  **/

  protected boolean _equals = true;

  /**
  *** collection of attribute predicates on which this predicate
  *** is based
  **/

  protected Vector _att_predicates;

  /**
  *** ---------------------------------------------------------
  *** Initializing the FiltersHandler
  *** ---------------------------------------------------------
  */

  private FiltersHandler handler;

  /**
  *** initialise the predicate from a properties object and
  *** a prefix for the property names.
  **/

  public XMLDOPredicate(Properties props, String prefix)
    throws DataObjectException
  {
    _att_predicates = new Vector();

    //
    // read type value
    //

    if (props.get(prefix + "." + "Type") != null)
    {
      _type_name = props.get(prefix + "." + "Type").toString();

      if (_type_name.startsWith("!"))
      {
        _type_name = _type_name.substring(1);
        _equals = false;
      }
    }

    //
    // read and add attribute predicates
    //

    for (int i = 0; props.get(prefix + "." + "AttName" + (i+1)) != null; i++)
    {
      System.err.println("found type in predicate");

      String att_name = null;
      String att_value = null;
      String filename = null;

      boolean equals = true;

      att_name = props.get(prefix + "." + "AttName" + (i+1)).toString();

      if (props.get(prefix + "." + "AttValue" + (i+1)) != null)
      {
        att_value = props.get(prefix + "." + "AttValue" + (i+1)).toString();

        if (att_value.startsWith("!"))
        {
          att_value = att_value.substring(1);
          equals = false;
        }
      }

      /**
      *** --------------------------------------------------------------------
      *** The name of the xml file is taken from the Properties object.
      *** At this point an FiltersHandler is created to parse the file
      *** --------------------------------------------------------------------
      ***
      */

      if (props.get(prefix + "." + "AttValuesFile" + (i+1)) != null)
      {

        //
        System.err.println("TRACE: ");

        filename = ( String ) props.get(prefix + "." + "AttValuesFile" + (i+1));
        System.err.println("TRACE: XML Filename is: " + filename );

        handler = new FiltersHandler ( filename );
        System.err.println("TRACE: " + filename + " found and parsed." );
        handler.displayFilters();

        ArrayList filters = handler.getFilters();
        Iterator filtersIterator = filters.iterator();
        StringBuffer sb = new StringBuffer();
        sb.append( "[" );
        while ( filtersIterator.hasNext() )
        {
          sb.append( filtersIterator.next() + "," );
        }
        sb.setCharAt( sb.length() - 1, ']' );
        att_value = sb.toString();

      }


      addAttPredicate(att_name, att_value, equals, false);
    }

  }

  public XMLDOPredicate(String type_name, boolean equals)
  {
    _type_name = type_name;
    _equals = equals;
  }

  public void addAttPredicate(String att_name, String att_value, boolean equals, boolean reg_exp)
    throws DataObjectException
  {
    DOAttPredicate p = new DOAttPredicate(att_name, att_value, equals, false);

    _att_predicates.addElement(p);
  }

  public boolean isTrue(DataObject dob)
  {
    DOType type = dob.getType();
    String type_name = type.getName();

    if (  (_type_name == null)
      ||(_equals && type_name.equals(_type_name))
      ||(!_equals && !type_name.equals(_type_name)))
    {
      Enumeration e = _att_predicates.elements();

      while (e.hasMoreElements())
      {
        DOAttPredicate p = (DOAttPredicate)e.nextElement();

        if (!p.isTrue(dob))
        {
          return false;
        }
      }

      return true;
    }
    else
    {
      return false;
    }
  }

  public class DOAttPredicate
  {
    protected String _att_name = null;
    protected String[] _att_values = null;
    protected boolean _equals = true;

    public DOAttPredicate(String att_name, String att_value, boolean equals, boolean reg_exp)
      throws DataObjectException
    {
      _att_name = att_name;
      _equals = equals;


      if (att_value.startsWith("[") && att_value.endsWith("]"))
      {
        _att_values = parseStringArray(att_value);

        if (_att_values == null && _att_values.length == 0)
        {
          throw new DataObjectException("Predicate list of values [" + att_value + "] is invalid");
        }
      }
      else
      {
        _att_values = new String[] {att_value};
      }

    }

    public boolean isTrue(DataObject dob)
    {
      Object value = null;

      //
      // get attribute value, if this throws and exception
      // then return false
      //

      try
      {
        value = dob.getAttributeValue(_att_name);
      }
      catch (Exception e)
      {
        return false;
      }


      //
      // if value is NULL and predicate value is null
      // return predicate condition (true | false)
      // otherwsie if value is NULL and predicate value
      // is non null return the inverse
      //

      if ((value == null && _att_values[0] == null))
      {
        return _equals;
      }
      else if (value == null)
      {
        return !_equals;
      }

      //
      // loop thor the array of acceptable values until
      // one matches
      //

      String att_value;

      for (int i = 0; i < _att_values.length; i++)
      {
        att_value = _att_values[i];

        //
        // convert the predicate value to same type
        // as the attribute value and then back to a string
        // this should cope with floats, doubles and bools
        //

        if (value instanceof Float)
        {
          try
          {
            att_value = (new Float(att_value)).toString();
          }
          catch (Exception e)
          {
            return false;
          }
        }
        if (value instanceof Double)
        {
          try
          {
            att_value = (new Float(att_value)).toString();
          }
          catch (Exception e)
          {
            return false;
          }
        }
        else if (value instanceof Boolean)
        {
          try
          {
            att_value = (new Boolean(att_value)).toString();
          }
          catch (Exception e)
          {
            return false;
          }
        }

        if (att_value.equals(value.toString()))
        {
          return _equals;
        }
      }

      //
      // if all of the above conditions failed to match
      // then return the inverse of the predicate condition
      //

      return !_equals;
    }


    /**
    *** parse array notation in to an array of strings
    **/

    String[] parseStringArray(String s)
    {
      Vector v = new Vector();

      String array_str = s.substring(1, s.length() - 1);

      StringTokenizer tokenizer = new StringTokenizer(array_str, ",");

      while (tokenizer.hasMoreTokens())
      {
        v.addElement(tokenizer.nextToken());
      }

      String[] array = new String[v.size()];
      v.copyInto(array);

      return array;
    }
  }
}