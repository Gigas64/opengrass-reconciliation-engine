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
*** Filter pipe allows you to filter the dataobjects based on their type
*** and attribute values. You can use this in an adaptor to discard dataobjects
*** that you are not interested in.
*** It can explicitly pass through dataobjects
*** that match defined criteria and can explicitly "block" (discard) dataobjects
*** that match defined criteria.
*** <p>The Filter can be configured  using the following properties...
*** <p>Adaptor.OpenGrassPipe.ClassName = org.opengrass.openadaptor.OpenGrassFilter
*** <br>Add OpenGrass Predicates here
*** <br>Adaptor.OpenGrassPipe.TypeN.Action = pass/block
*** <p>e.g.
*** <p>Adaptor.OpenGrassPipe.ClassName = org.opengrass.openadaptor.OpenGrassFilter
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptorngrassPredicate.StringPredicate
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Att = Book
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Criteria = TST
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptorngrassPredicate.DoublePredicate
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Att = Strike
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Criteria = !0
*** <br>Adaptor.OpenGrassPipe.Type1.Action = block
*** <br>Adaptor.OpenGrassPipe.Type2.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptorngrassPredicate.StringPredicate
*** <br>Adaptor.OpenGrassPipe.Type2.Predicate1.Att = Location
*** <br>Adaptor.OpenGrassPipe.Type2.Predicate1.Criteria = LDN
*** <br>Adaptor.OpenGrassPipe.Type2.Action = pass
***
*** @version: 1.00
***
**/

import org.openadaptor.adaptor.*;
import org.openadaptor.dataobjects.*;
import org.opengrass.exceptions.*;

import java.util.Properties;
import java.util.Vector;



public class OpenGrassFilter extends AbstractSimplePipe {

 /** vector of predicates **/
 protected Vector doTransformers;
 protected Vector passOrBlock;

 /**
 *** Initialise from properties object. See class comment for property values
 ***
 *** @param props  Properties object
 *** @param prefix  Prefix string to search for properties
 ***
 *** @exception IbafException Thrown if initialisation fails, mandatory properties are missing or properties cannot be parsed.
 **/

  public void init(String name, Properties props, String prefix, Controller controller)
     throws IbafException {

    super.init(name, props, prefix, controller);
    doTransformers = new Vector();
    passOrBlock = new Vector();
    // For each filter type
    for (int i = 1; props.get(prefix + ".Type" + (i) + ".Action") != null; i++) {
      String filter_prefix = prefix + ".Type" + (i);
      System.err.println("INFO: Initialising " + filter_prefix);
      String type = props.get(prefix + ".Type" + (i) + ".Action").toString();
      try {
        // get filter type (pass/block) + add to filter type vector
        if (type.equals("pass") || type.equals("block")) {
          passOrBlock.addElement(type);
        }
        else {
          throw new OpenGrassException("ERROR: "+ filter_prefix + ".Action  must be \"pass\" or \"block\"");
        }
        // set up.opengrassTransformer that holds the predicates
        OpenGrassDOTransformer doTransformer = new OpenGrassDOTransformer();
        doTransformer.init(props, filter_prefix);
        System.err.println("INFO: Action = " + type);
        // Add transformer to vector
        doTransformers.addElement(doTransformer);
      }
      catch (Exception e) {
        throw new IbafException("\nERROR: " + filter_prefix + " + init failed\n " + e.getMessage());
      }
    }
  }

  /**
  *** Takes each dataObject + applies each filter in turn. If any of the filters block the dataobject
  *** it is removed from the dataObject array.
  **/
  protected DataObject[] transformDataObjects (DataObject[] inDobs)
     throws PipelineException {
    Vector dobs = new Vector();
    if (inDobs == null || inDobs.length == 0) {
      throw new PipelineException("received null or empty dataobject array",
          PipelineException._HOSPITAL);
    }
    // looping through each incoming DataObject
    for (int i = 0; i< inDobs.length; i++ ) {
      boolean passDataObject = true;
      // looping through each Transformer
      for (int j=0; j<doTransformers.size(); j++ ) {
        boolean matches;
        // get the filter type (pass/block)
        String filterType = (String) passOrBlock.get(j);
        System.err.println("TRACE: Applying Filter Type " + filterType);
        try {
          // check in data object matches the predicates in the transformer
          matches = ((OpenGrassDOTransformer) doTransformers.get(j)).matches(inDobs[i]);
        }
        catch ( OpenGrassException e ) {
          throw new PipelineException ( e.getMessage(), PipelineException._HOSPITAL);
        }
        if ( (matches && filterType.equals("block") )
           ||(!matches && filterType.equals("pass") )) {
          // if we dont want to pass the data object break out of the loop and set pass to false
          passDataObject = false;
          break;
        }
      }
      if (passDataObject) {
        System.err.println("TRACE: OpenGrassFilter passing DataObject");
        dobs.addElement(inDobs[i]);
      }
      else {
        System.err.println("TRACE: OpenGrassFilter blocking DataObject");
      }
    }
    if (dobs.size() != inDobs.length) {
      inDobs = new DataObject [dobs.size()];
      dobs.copyInto(inDobs);
    }
    return inDobs;
  }
}
