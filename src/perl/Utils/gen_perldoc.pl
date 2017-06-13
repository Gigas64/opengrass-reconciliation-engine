#!/usr/bin/perl -w

# ******************************************************************************
# ** Copyright (C) 2001 - 2006 The Software Conservancy as Trustee. All rights
# ** reserved.
# **
# ** Permission is hereby granted, free of charge, to any person obtaining a
# ** copy of this software and associated documentation files (the
# ** "Software"), to deal in the Software without restriction, including
# ** without limitation the rights to use, copy, modify, merge, publish,
# ** distribute, sublicense, and/or sell copies of the Software, and to
# ** permit persons to whom the Software is furnished to do so, subject to
# ** the following conditions:
# **
# ** The above copyright notice and this permission notice shall be included
# ** in all copies or substantial portions of the Software.
# **
# ** THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
# ** OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# ** MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# ** NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
# ** LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
# ** OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
# ** WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
# **
# ** Nothing in this notice shall be deemed to grant any rights to
# ** trademarks, copyrights, patents, trade secrets or any other intellectual
# ** property of the licensor or any contributor except as expressly stated
# ** herein. No patent license is granted separate from the Software, for
# ** code that you delete from the Software, or for combinations of the
# ** Software with other software or hardware.
# **
# *****************************************************************************

my $GRASS_HOME    = $ENV{GRASS_HOME};
my $perldoc_dir =   $GRASS_HOME."/doc/programmer/pod"; 

###############################################################################


print STDERR "\nSCRIPT: gen_perldoc.pl\n";
print STDERR "   Version: 1.00.000\n";
print STDERR "   Date:    24.08.2005 \n\n";



## ##########################################

# list of perl modules

@modules = qw(
   ..\/Reconciliation.pl
   "..\/Grass\/run_adaptor.pm"
   "..\/Grass\/matching_engine.pm"
   "..\/Grass\/version.pm"
   "..\/Grass\/debug.pm"
   "..\/Grass\/error.pm"
   "..\/Grass\/logging.pm"
   );


# Generate perldoc dir under doc if not existent
# check log directory exists - create if it doesnt
    if ( !-e $perldoc_dir ) {
         print("INFO: Creating Directory $perldoc_dir\n");
        
        mkdir( $perldoc_dir, 0755 )
          or die( "ERROR: Couldn't mkdir $perldoc_dir: $!");
    }


# foreach perl module -> generate a pod file  

foreach $module (@modules) {
   `perldoc $module > $perldoc_dir/$module\.pm`;

}

print ("\nGeneration of perldoc finished\n\n");
