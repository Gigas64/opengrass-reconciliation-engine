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
 * Title:         Info
 * Description:   Represents the data held in one xml info tag in breaks.xml file from matching engine
 * Date:          23rd October 2001
 * @version 1.0
 */

public class Info
{
  protected String field_;
  protected String system_;
  protected String value_;
  protected String type_;
  public Info()
  {
  }
  public Info( String field, String system, String value, String type )
  {
    field_ = field;
    system_ = system;
    value_ = value;
    type_ = type;
  }
  public String getField()
  {
    return field_;
  }
  public void setField( String string )
  {
    field_ = string;
  }
  public String getSystem()
  {
    return system_;
  }
  public void setSystem( String string )
  {
    system_ = string;
  }
  public String getType()
  {
    return type_;
  }
  public void setType( String string )
  {
    type_ = string;
  }
  public String getValue()
  {
    return value_;
  }
  public void setValue( String string )
  {
    value_ = string;
  }
  public static Info example()
  {
    return new Info( "Currency", "Bon", "CHF", "" );
  }
  public Info copy()
  {
    return new Info( this.getField(), this.getSystem(), this.getValue(), this.getType() );
  }
  public String toString()
  {
    return  "\nfield: [" + this.getField() + "]\n" +
            "system: [" + this.getSystem() + "]\n" +
            "value: [" + this.getValue() + "]\n" +
            "type: [" + this.getType() + "]\n";

  }
  public void display()
  {
    System.err.println( "\n  info:" );
    System.err.println( "    field: [" + getField() + "]" );
    System.err.println( "    system: [" + getSystem() + "]" );
    System.err.println( "    value: [" + getValue() + "]" );
    System.err.println( "    type: [" + getType() + "]" );
  }
  public static void main(String[] args)
  {
    System.out.println( Info.example().copy() );
  }
}