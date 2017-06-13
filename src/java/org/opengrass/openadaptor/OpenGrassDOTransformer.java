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
*** Transforms DataObjects.
*** <p>Holds a Vector of predicates and a Vector of Actions set up during initialisation.
*** Takes dataobjects and tests them against the predicates. If all the predicates evaluate to true it then
*** applies the actions to the data object transforming the data object.
*** <p>Used by both.OpenGrassPipe and.OpenGrassFilter (and im sure other pipes in the future)
*** <p>Intialisation in Adaptor Property file
*** <br>prefix.PredicateN.OpenGrassAttPredicate = className
*** <br>prefix.PredicateN.Att  = AttributeName
*** <br>prefix.PredicateN.Criteria = Criteria
*** <br>prefix.ActionN.OpenGrassDOAction = className
*** <br>prefix.ActionN.Action  = Action
*** <br>prefix.ActionN.Attribute = AttributeName
*** <p>e.g. If the dataObject Type attribute equals "Payment", multiply the Amount attribute by -1
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.OpenGrassAttPredicate = org.opengrass.openadaptor.opengrassPredicates.StringPredicate
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Att      = Type
*** <br>Adaptor.OpenGrassPipe.Type1.Predicate1.Criteria = [Payment]
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.OpenGrassDOAction = org.opengrass.openadaptor.opengrassActions.MathsAction
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Action      = *
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att1    = Amount
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Att2    = {-1}
*** <br>Adaptor.OpenGrassPipe.Type1.Action1.Dest    = Amount
*** @date 25th June 2002
*** @version 1.00
***
**/

import java.util.*;
import org.opengrass.exceptions.*;
import org.openadaptor.dataobjects.DataObject;
import org.opengrass.openadaptor.opengrassPredicates.*;
import org.opengrass.openadaptor.opengrassActions.*;



public class OpenGrassDOTransformer {
  /** Type of DataObject to apply the predicate to */
  private String doName;
  /** property prefix */
  private String prefix;
  /** Vector of.OpenGrassAttPredicates to apply to dataObject*/
  private Vector attPredicates;
  /** Vector of.OpenGrassDOPredicates to apply to dataObject*/
  private Vector doPredicates;
  /** Vector of.OpenGrassActions to apply to dataObject*/
  private Vector actions;

  /** Sets up a DataObjectTransformer creating a Vector of predicates and a Vector of actions from the properties supplied. See property format above **/
  public void init(Properties props, String pfix) throws OpenGrassException {
    prefix = pfix;
    attPredicates = new Vector();
    doPredicates = new Vector();
    actions = new Vector();
    try {
      // get DO type
      if (props.getProperty(prefix + "." + "DOType") != null) {
        doName = props.getProperty(prefix + "." + "DOType").toString();
      }
      int i = 1;
      while (  props.get(prefix + ".Predicate" +  i + ".OpenGrassDOPredicate") != null
            || props.get(prefix + ".Predicate" +  i + ".OpenGrassAttPredicate") != null ) {
        String pf = prefix + ".Predicate" +  i;
        if (props.get(prefix + ".Predicate" +  i + ".OpenGrassDOPredicate") != null ) {
          // Create the predicate
         OpenGrassDOPredicate attPredicate = getDOPredicate(props, pf);
          doPredicates.add(attPredicate);
        }
        else {
         OpenGrassAttPredicate attPredicate = getAttPredicate(props, pf);
          attPredicates.add(attPredicate);
        }
        i++;
      }
      // Get Actions
      for (int j = 1; props.get(prefix + ".Action" +  j + ".OpenGrassDOAction") != null; j++) {
        String pf = prefix + ".Action" +  j;

       OpenGrassDOAction opengrassAction = getAction(props, pf);
        // Add the do action to the vector of actions
        actions.add(opengrassAction);
      }
    } catch (Exception e) {
      throw new OpenGrassException(e.getMessage());
    }
  }

  /**
  *** Transforms the DataObject. Compares the dataObject toTransform to a vector of predicates. If these all evaluate to true
  *** the vector of Attribute transformers are applied to the data object.
  *** The altered dataObject is then returned
  **/
  public DataObject transformDataObject (DataObject toTransform) throws OpenGrassException{
    System.err.println("TRACE: " + prefix);
    System.err.println("TRACE: Apply Predicates to DataObject");
    // If the data object matches the predicates
    if (this.matches(toTransform)) {
      System.err.println("TRACE: DataObject matches - apply actions");
      // Apply each of the actions in turn
      for (int i = 0; i < actions.size(); i++) {
       OpenGrassDOAction ga = (OpenGrassDOAction) actions.get(i);
        toTransform = ga.transformOpenGrassAttributes(toTransform);
      }
    }
    return toTransform;
  }

  /** Returns true if the DataObject matches all of the predicates in the.OpenGrassDOTransformer */
  protected boolean matches (DataObject dob) throws OpenGrassException {
    boolean matches = true;
    System.err.println("TRACE: " + prefix);
    // if the data object type does not match that of the predicate return false
    if (doName == null || doName.equals(dob.getType().getName())) {
      // run through each of the attribute predicates in order
      for (int i = 0; i < doPredicates.size(); i++) {
        // Get attribute name for predicate
        OpenGrassDOPredicate attPredicate = (OpenGrassDOPredicate) doPredicates.get(i);
        if (! attPredicate.matches(dob)) {
          matches = false;
          break;
        }
      }
      if (matches) {
        for (int i = 0; i < attPredicates.size(); i++) {
          // Get attribute name for predicate
         OpenGrassAttPredicate attPredicate = (OpenGrassAttPredicate) attPredicates.get(i);
          String attName = attPredicate.getAttName();
          try {
            // Get attribute value from DataObject
            String attValue = "";
            if (dob.getAttributeValue(attName) != null) {
              attValue = dob.getAttributeValue(attName).toString();
            }
            // If the attribute predicates does not match the whole predicate cannot match
            // so break out of loop and return false overall
            if (! attPredicate.matches(attValue)) {
              matches = false;
              break;
            }
          }
          catch (Exception e) {
              throw new OpenGrassException("ERROR: " +  prefix + ": No attribute " + attPredicate.getAttName() + " in incomming DataObject");
          }
        }
      }
    }
    else {
      matches = false;
    }
    return matches;
  }

  /** Creates and initialises an attPredicate of the specifed class for the given attribute using the criteria*/
  private OpenGrassDOPredicate getDOPredicate(Properties p, String pref) throws OpenGrassException {
    String className;
    OpenGrassDOPredicate ap = null;
    try {
      className = p.get(pref + ".OpenGrassDOPredicate").toString();
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Can't get " + pref + ".OpenGrassDOPredicate");
    }
    try {
      Class c = Class.forName(className);
      Object o = c.newInstance();
      if (o instanceof OpenGrassDOPredicate) {
        ap = (OpenGrassDOPredicate) o;
        ap.init(p, pref);
      }
      else {
        throw new OpenGrassException("ERROR: " + className + " does not implement.OpenGrassDOPredicate");
      }
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Failed to create instance of " + className + "\n" + e.getMessage());
    }
    return ap;
  }

  /** Creates and initialises an attPredicate of the specifed class for the given attribute using the criteria*/
  private OpenGrassAttPredicate getAttPredicate(Properties props, String pf) throws OpenGrassException {
    OpenGrassAttPredicate ap = null;
    String attName = null;
    String className = null;
    String criteria = null;
    try {
      attName = props.get(pf + ".Att").toString();
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Can't get " + pf + ".Att");
    }
    try {
     className = props.get(pf + ".OpenGrassAttPredicate").toString();
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Can't get " + pf + ".OpenGrassAttPredicate");
    }
    try {
      criteria = props.get(pf + ".Criteria").toString();
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Can't get " + pf + ".Criteria");
    }
    try {
      Class c = Class.forName(className);
      Object o = c.newInstance();
      if (o instanceof OpenGrassAttPredicate) {
        ap = (OpenGrassAttPredicate) o;
        ap.init(attName, criteria);
      }
      else {
        throw new OpenGrassException("ERROR: " + className + " does not implement.OpenGrassAttPredicate");
      }
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Failed to create instance of " + className + "\n" + e.getMessage());
    }
    return ap;
  }

  /** Creates and initialises a.OpenGrassDOAction of the specified class from the given properties **/
  private OpenGrassDOAction getAction(Properties p, String pref) throws OpenGrassException {
    OpenGrassDOAction ap = null;
    // Create DOAction of specified class
    String className = p.get(pref + ".OpenGrassDOAction").toString();
    try {
      Class c = Class.forName(className);
      Object o = c.newInstance();
      if (o instanceof OpenGrassDOAction) {
        ap = (OpenGrassDOAction) o;
        ap.init(p,pref);
      }
      else {
        throw new OpenGrassException("ERROR: " + className + " does not implement.OpenGrassDOAction");
      }
    }
    catch (Exception e) {
      throw new OpenGrassException("ERROR: Failed to create instance of " + className + "\n" + e.getMessage());
    }
    return ap;
  }


}
