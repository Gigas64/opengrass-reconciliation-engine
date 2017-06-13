package org.opengrass.openadaptor.opengrassActions;

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
*** Takes a DataObject and concatinates the attribute values.
*** <p>Initialisation in Adaptor Property file
*** <br>prefix.OpenGrassDOAction  = org.opengrass.openadaptor.opengrassActions.ConcatinateAction
*** <br>prefix.Att1  = NameOfAttribute or {Value}
*** <br>prefix.Att2  = NameOfAttribute or {Value}
*** <br>prefix.Dest  = TargetAttribute
*** <p>Each attribute is added to the one below in the order specified.
*** <p>e.g. Concatinate two attributes preceded by the text 'Colt'
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.ConcatinateAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att1          = {Colt}
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att2          = Book
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att3          = PositionId
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Dest          = RecId
*** @date 30th July 2002
*** @version 1.00
*** @see.OpenGrassDOAction
***
**/

import java.util.Properties;
import java.util.Vector;
import org.opengrass.exceptions.OpenGrassException;
import org.apache.regexp.RE;
import org.openadaptor.dataobjects.DataObject;

public class ConcatinateAction implements OpenGrassDOAction {
  /** Part of string before index **/
  private String prefix;
  /** List of attributes **/
  private Vector attNames;
  /** Destination Attributes **/
  private String attDest;

 /**
 *** Initialise from properties object. See class comment for property values
 ***
 *** @param props  Properties object
 *** @param prefix  Prefix string to search for properties
 ***
 *** @exception OpenGrassException Thrown if initialisation fails, mandatory properties are missing or properties cannot be parsed.
 **/

  public void init(Properties props, String pfix) throws OpenGrassException {
    int i = 1;
	prefix = pfix;

	attNames = new Vector();

	try {
		while(props.get(prefix + ".Att" + i) != null) {
		  attNames.add(props.get(prefix + ".Att" + i));
		  i++;
		}
		  // Get the name of the Attribute that will hold the result
		  attDest = props.getProperty( prefix + ".Dest").toString();
	}
    catch ( Exception e ) {
    	throw new OpenGrassException( "ERROR: Incorrect setup for " + prefix + "\n" + e.getMessage());
    }
    System.err.println("INFO: Concatinate Action attributes set");

  }

  /** Trims the attribute value in the DataObject */
  public DataObject transformOpenGrassAttributes(DataObject dObject) throws OpenGrassException {
	StringBuffer result = new StringBuffer();
    RE regExp;

    try {
		for(int i=0;i<attNames.size();i++) {
			regExp = new RE("\\{(.*)\\}");
			if (regExp.match((String)attNames.get(i))) {
				result.append(regExp.getParen(1));
			}
			else {
				result.append((String) dObject.getAttributeValue( (String) attNames.get(i) ));
			}
		}
      	dObject.setAttributeValue( attDest, result.toString() );
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: When Concatinating\n" + e.getMessage());
    }
    System.err.println("TRACE: Concatinate Action created [" + result.toString() + "]");
    return dObject;
  }
}
