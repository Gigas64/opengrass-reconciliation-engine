package org.opengrass.openadaptor;

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
***
*** This pipe allows you to transform the dataobjects according to their type
*** and attribute values.
*** <p>The Filter can be configured  using the following properties...
*** <p>Adaptor.OpenGrassPipe.ClassName = org.opengrass.openadaptor.OpenGrassPipe
*** <br>Add.OpenGrass Predicates here
*** <br>Add.OpenGrass Actions here
*** <p>e.g. If the Book attribute of a reco type dataObject is "TFT",
*** add together the values in the Sale1 and Sale2 attributes and set the
*** Attribute Value for Amount to the result.
*** <p>Adaptor.OpenGrassPipe.ClassName = org.opengrass.openadaptor.OpenGrassPipe
*** <br>Adaptor.OpenGrassPipe.Type1.DoType = reco
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptorngrassPredicate.StringPredicate
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Att = Book
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Criteria = TFT
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.MathsAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Action      = +
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att1    = Sale1
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att2    = Sale2
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Dest    = Amount
***
*** @date 1st August 2002
*** @version: 1.01
***
**/

import org.openadaptor.adaptor.*;
import org.openadaptor.dataobjects.*;
import org.opengrass.exceptions.*;

import java.util.Properties;
import java.util.Vector;




public class OpenGrassPipe extends AbstractSimplePipe {

 /** vector of predicates **/
 protected Vector doTransformers;

 /**
 *** Initialise from properties object. See class comment for property values
 ***
 *** @param props  Properties object
 *** @param prefix  Prefix string to search for properties
 ***
 *** @exception DOStringException Thrown if initialisation fails, mandatory properties are missing or properties cannot be parsed.
 **/

  public void init(String name, Properties props, String prefix, Controller controller)
     throws IbafException {

    super.init(name, props, prefix, controller);
    doTransformers = new Vector();
    // for each type create at.OpenGrassDOTransformer from the given properties
    for (int i = 1; props.get(prefix + ".Type" + (i) + ".Action1.OpenGrassDOAction") != null; i++) {
      String filter_prefix = prefix + ".Type" + (i);
      System.err.println("INFO: Initialising " + filter_prefix);
     OpenGrassDOTransformer doTransformer = new OpenGrassDOTransformer();
      try {
        doTransformer.init(props, filter_prefix);
      }
      catch (Exception e) {
        throw new IbafException("\nERROR: " + filter_prefix + " + init failed\n " + e.getMessage());
      }
      // add the.OpenGrassDOTransformer to the vector of transformers
      doTransformers.addElement(doTransformer);
    }
  }

 /** Passes the data objects to the transformers which alter them accordingly **/
  protected DataObject[] transformDataObjects (DataObject[] inDobs)
     throws PipelineException {
    Vector dobs = new Vector();
    DataObject tmp = null;
    if (inDobs == null || inDobs.length == 0) {
      throw new PipelineException("received null or empty dataobject array",
          PipelineException._HOSPITAL);
    }
    // looping through each incoming DataObject
    for (int i = 0; i< inDobs.length; i++ ) {
      // looping through each Transformer
      tmp = (DataObject) inDobs[i];
      for (int j=0; j<doTransformers.size(); j++ ) {
        try {
          tmp = ((OpenGrassDOTransformer) doTransformers.get(j)).transformDataObject(tmp);
        }
        catch ( OpenGrassException e ) {
          throw new PipelineException ( e.getMessage(), PipelineException._HOSPITAL);
        }

      }
      if (tmp != null) {
        dobs.addElement(tmp);
      }
    }
    if (dobs.size() != inDobs.length) {
      inDobs = new DataObject [dobs.size()];
      dobs.copyInto(inDobs);
    }
    return inDobs;
  }
}
