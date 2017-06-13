package org.opengrass.datastores.referencestore;

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
 * Title:         Rec
 * Description:   Represents the data held in one xml rec tag in breaks.xml file from matching engine
 * Date:          23rd October 2001
 * @version 1.0
 */

import org.opengrass.exceptions.OpenGrassException;
import java.util.Hashtable;
import java.util.Iterator;

public class Rec
{
  protected String key_;
  protected String type_;
  protected String firstSeen_;
  protected Hashtable keys_;
  protected Hashtable matches_;
  protected Hashtable infos_;
  /**
    * Class constructor with no arguments. All attributes set
    */
  public Rec()
  {
    keys_ = new Hashtable(); // maps keys to the keys field values
    matches_ = new Hashtable(); // maps matches to match field values
    infos_ = new Hashtable(); // maps infos to info field and system values
  }
  /**
    * Class constructor with three arguments
    *
    * @param
    * @return
    * @exception
    */
  public Rec( String key, String type, String firstSeen )
  {
    this();
    key_ = key;
    type_ = type;
    firstSeen_ = firstSeen;
  }
  /**
    *
    *
    * @param
    * @return
    * @exception
    */
  public Rec( String key, String type, String firstSeen, Hashtable keys, Hashtable matches, Hashtable infos )
  {
    this( key, type, firstSeen );
    keys_ = keys;
    matches_ = matches;
    infos_ = infos;
  }

  protected void addKey( Key key )
  {
    keys_.put( key.getField(), key );
  }
  protected void addMatch( Match match )
  {
    matches_.put( match.getField(), match );
  }
  protected void addInfo( Info info )
  {
    infos_.put( info.getSystem() + info.getField(), info );
  }

  public String getKey()
  {
    return key_;
  }
  protected void setKey( String string )
  {
    key_ = string;
  }

  public String getFirstSeen()
  {
    return firstSeen_;
  }
  protected void setFirstSeen( String string )
  {
    firstSeen_ = string;
  }

  public String getType()
  {
    return type_;
  }
  protected void setType( String string )
  {
    type_ = string;
  }

  public Hashtable getKeys()
  {
    return keys_;
  }
  public Hashtable getMatches()
  {
    return matches_;
  }
  public Hashtable getInfos()
  {
    return infos_;
  }
  public static Rec example()
  {
    Rec example = new Rec( "11", "Secondary-OneSided", "09/10/2001" );
    Key key = Key.example();
    Match match = Match.example();
    Info info = Info.example();
    example.addKey( key );
    example.addMatch( match );
    example.addInfo( info );
    return example;
  }
  public Rec copy()
  {
    return new Rec( this.getKey(), this.getType(), this.getFirstSeen(), this.getKeys(), this.getMatches(), this.getInfos() );
  }
  public String toString()
  {
    return  "\nRec\n" +
            "\tkey: [" + this.getKey() + "]\n" +
            "\ttype: [" + this.getType() + "]\n" +
            "\tfirstSeen: [" + this.getFirstSeen() + "]\n" +
            "\tkeys: [" + this.getKeys().elements() + "]\n" +
            "\tmatches: [" + this.getMatches() + "]\n" +
            "\tinfos: [" + this.getInfos() + "]\n";
  }
  public void display()
  {
    System.err.println( "Rec:" );
    System.err.println( "  key: [" + getKey() + "]" );
    System.err.println( "  type: [" + getType() + "]" );
    System.err.println( "  firstSeen: [" + getFirstSeen() + "]" );
    System.err.println( "  keys:" );
    Iterator it = getKeys().keySet().iterator();
    while ( it.hasNext() )
    {
      String key = (String)(it.next());
      ((Key)(getKeys().get(key))).display();
    }
    System.err.println( "  matches:" );
    it = getMatches().keySet().iterator();
    while ( it.hasNext() )
    {
      String key = (String)(it.next());
      ((Match)(getMatches().get(key))).display();
    }
    System.err.println( "  infos" );
    it = getInfos().keySet().iterator();
    while ( it.hasNext() )
    {
      String key = (String)(it.next());
      ((Info)(getInfos().get(key))).display();
    }

  }
  public static void main(String[] args) throws OpenGrassException
  {

  }
}