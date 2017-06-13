package org.opengrass.datastores.gpropstore;

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
 ** Version: 1.01.000
 ** Date:  08/04/2004
 **
 */

import org.opengrass.exceptions.OpenGrassException;
import java.util.*;
import java.io.*;

/**
 * Sets and manipulates list of Gpropses available for a specific reconcilliation.
 * Information about these can be accessed via various methods.
 */

public class GpropsStore extends File {

    /** Hash of Gprops objects */
    private Hashtable gprops_store;
    /** Data handler responsible for reading / writing to resource that holds Gprops info */
    private GpropsXMLReaderWriter gprops_handler;
    /** Debug level */
    private int debug;

    private String business_name;
    
    private File gpropsFile_;

    private long gpropsLastModified_ ;

    /**
     * Creates a new GpropsStore object and sets the debug level to the default level (0)
     */
    /**public GpropsStore(String opengrass_path, String bn) throws OpenGrassException {
    	this.debug = 0;
        this.business_name = bn;
        this.og_path = opengrass_path;
    }*/

    /**
     * Creates a new GpropsStore object and sets debug to the specified
     */
    public GpropsStore(String opengrass_path, String bn, int local_debug) throws OpenGrassException {
        super(opengrass_path, opengrass_path + separator + "ertba" + separator + bn +separator);
        if (local_debug >= 1) System.err.println("INFO: Initialising GpropsStore");
        debug = local_debug;
        business_name = bn;

        validatePath();
        if (debug >= 3) { System.err.println("DEBUG: Validated Gprops file path " + gpropsFile_.getAbsolutePath()); }
        
        if (debug >= 3) { System.err.println("DEBUG: Getting Gprops handler"); }
        gprops_handler = new GpropsXMLReaderWriter(gpropsFile_.getAbsolutePath(), debug);
        if (debug >= 2) { System.err.println("DEBUG: Initialising GpropsStore complete"); }

    }

    private void validatePath() throws OpenGrassException {
        // check this the path given in constructor and is a directory
        if ( !( exists() && isDirectory() ) ) {
            throw new OpenGrassException( " No directory called: [" + getAbsolutePath() + "]." );
        }

        // now check that there is a data directory under the current path
        File confDir_ = new File( this, "conf" );
        if ( !( confDir_.exists() && confDir_.isDirectory() ) ) {
            throw new OpenGrassException( "[" + getAbsolutePath() + "] has no conf directory" );
        }


        gpropsFile_ = new File( confDir_, getName() + ".gprops" );
        if ( !gpropsFile_.exists() ) {
            throw new OpenGrassException( "[" + gpropsFile_.getAbsolutePath() + "] doesn't exist. " );
        }
    }

    /** Creates a data store of Gprops from a specified handler */
    private void parse()  throws OpenGrassException {
        gprops_store = new Hashtable();
        gprops_handler.parse();
        gprops_store = gprops_handler.getData();
        gpropsLastModified_ = gpropsFile_.lastModified();
    }

    /**
     * Checks if the tracker file has been updated
     *
     * @return     true or false
     */
    public boolean gpropsModified() {

        boolean out = false;
        if ( gpropsFile_ == null ) {
            out = true;
        }
        else if ( (gpropsFile_.lastModified()) != gpropsLastModified_ ) {
            out = true;
        }
        return out;
    }

    /** Writes the data store back to a specified handler */
    private void write() throws OpenGrassException {
        gprops_handler.write(gprops_store);
    }

    /**
     * Returns a Vector containing a list of available gprops_store
     */
    public Vector getGpropsNames()  throws OpenGrassException {
        // check we don't have to update
        if (gpropsModified() == true) {
            this.parse();
        }

        Vector Gprops_list = new Vector();
        for (Enumeration e = gprops_store.keys(); e.hasMoreElements();) {
            Gprops_list.add((String) e.nextElement());
        }
        return Gprops_list;
    }

    /**
     * Returns a Gprops object or null if it doesn't exist
     */
    public Gprops getGprops(String ref)  throws OpenGrassException {
        // check we don't have to update;
        if (gpropsModified() == true) {
            this.parse();
        }

        return ((Gprops) gprops_store.get(ref));
    }

    /**
     * Returns a Gprops object or null if it doesn't exist
     */
    public Gprops getGprops()  throws OpenGrassException {
        // check we don't have to update;
        if (gpropsModified() == true) {
            this.parse();
        }
        return ((Gprops) gprops_store.get(business_name));
    }

    /**
     * Removes the specified Gprops
     */
    public void removeGprops(String name) throws OpenGrassException {
        // check we don't have to update;
        this.parse();
        gprops_store.remove(name);
        this.write();
    }

    /**
     * Creates a new Gprops
     */
    public void setGprops(Gprops b)  throws OpenGrassException {
        if (debug > 0) { System.err.println("INFO: Setting Gprops"); }
        // check we don't have to update;
        this.parse();
        gprops_store.put(b.getRefName(), b);
        this.write();
    }


    public void setGprops(String ref_name, String display_name) throws OpenGrassException {
        if (debug > 0) { System.err.println("INFO: Setting Gprops"); }
        // check we don't have to update;
        this.parse();
        Gprops tmp = new Gprops(debug);
        tmp.setRefName(ref_name);
        tmp.setDisplayName(display_name);
        gprops_store.put(ref_name, tmp);
        this.write();
    }

    // main method to act as test harness
    public static void main(String args []) throws Exception {
        System.err.println("GpropsStore startet!");

        GpropsStore gs = new GpropsStore(args[0],"TestRec", 10);
        Gprops gp = gs.getGprops();

        System.err.println("GpropsStore");
        System.err.println(gp.toString());

    }

}
