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
 * Title:        Match
 * Description:  Represents the data held in one xml match tag in breaks.xml file from matching engine
 * Date:         23rd October 2001
 * @version 1.0
 */

public class Match
{
  protected String field_;
  protected String status_;
  protected String primary_;
  protected String secondary_;
  protected String difference_;
  public Match()
  {
  }
  public Match( String field, String status, String primary, String secondary, String difference )
  {
    field_ = field;
    status_ = status;
    primary_ = primary;
    secondary_ = secondary;
    difference_ = difference;
  }
  public String getField()
  {
    return field_;
  }
  public void setField( String string )
  {
    field_ = string;
  }
  public String getStatus()
  {
    return status_;
  }
  public void setStatus( String string )
  {
    status_ = string;
  }
  public String getPrimary()
  {
    return primary_;
  }
  public void setPrimary( String string )
  {
    primary_ = string;
  }
  public String getSecondary()
  {
    return secondary_;
  }
  public void setSecondary( String string )
  {
    secondary_ = string;
  }
  public String getDifference()
  {
    return difference_;
  }
  public void setDifference( String string )
  {
    difference_ = string;
  }
  public static Match example()
  {
    return new Match( "Balance", "Break", "Cedel", "Bon", "0.25" );
  }
  public Match copy()
  {
    return new Match( this.getField(), this.getStatus(), this.getPrimary(), this.getSecondary(), this.getDifference() );
  }
  public String toString()
  {
    return  "\nfield: [" + this.getField() + "]\n" +
            "status: [" + this.getStatus() + "]\n" +
            "primary: [" + this.getPrimary() + "]\n" +
            "secondary: [" + this.getSecondary() + "]\n" +
            "type: [" + this.getDifference() + "]\n";
  }
  public void display()
  {
    System.err.println( "\n  match:" );
    System.err.println( "    field: [" + getField() + "]" );
    System.err.println( "    status: [" + getStatus() + "]" );
    System.err.println( "    primary: [" + getPrimary() + "]" );
    System.err.println( "    secondary: [" + getSecondary() + "]" );
    System.err.println( "    difference: [" + getDifference() + "]" );
  }
  public static void main(String[] args)
  {
    System.out.println( Match.example().copy() );
  }
}