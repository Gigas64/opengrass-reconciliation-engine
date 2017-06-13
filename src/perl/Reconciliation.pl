#!/usr/bin/perl -w

# ******************************************************************************
# ** Copyright (C) 2001 - 2006 - 2005 The Software Conservancy as Trustee. All rights
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


#
#   Reconciliation Engine     - controls the process
#
###############################################################################

=head1 B<NAME>B<Reconciliation.pl>
=head1 B<DESCRIPTION>
The Reconciliation is the central application which will start all the sub-processes of a OpenGrass Rec.
The script starts the Rec process straight through.

=for comment

=cut

use Getopt::Long;
use Time::localtime;

use File::stat;
use File::Basename;

use lib "$ENV{GRASS_HOME}/src/perl";

use GRASS;
use Grass::gprops_reader;
use Grass::run_adaptor;
use Grass::matching_engine;
#use Grass::alignment_engine;

######################################################################################################################
# Global Vars
$| = 1;

my $business_name = "";
my $help_option   = "";
my $option        = "";

#######################################################################################################################
# Get the command line options
$result = GetOptions(
    "bn=s" => \$business_name,
    "h"    => \$help_option,
    "o=s"  => \$option
);

if ($help_option) {
  print STDERR "\nUsage: $0 [-o=<Single Component>] -bn=<Business Name>\n";
  print STDERR "\n\tOptions:\n";
  print STDERR "\t\toa - run open adaptor only\n";
  print STDERR "\t\tme - run matching engine only\n";
  print STDERR "\t\tae - run the alignment engine only\n";
  print STDERR "\n\tExample:\n";
  print STDERR "\t\t$0 -o=me -bn=TestRec\n\n";

  exit;
}

if ( !$result || length $business_name == 0 ) {
  die("Usage: $0 { [ -o=<option>] -bn=Business Name } or { -h for help } \n");
}

# initialise global Variables
init_GRASS($business_name);
$business_name = $GRASS::business_name;

if ( length $option > 0) {
  if($option ne "oa" && $option ne "me" && $option ne "ae") {
    GRASSdie("ERROR: '$option' is not a recognised option, please use -h for help.","reconciliation");
  }
}
else {
  $option = "all";
}

#######################################################################################################################
# Version number
if($GRASS::DEBUG) {
  print("INFO: -------------------------------\n");
  print("INFO: SCRIPT Reconciliation.pl\n");
  print("INFO:    Version: 1.00.000\n");
  print("INFO:    Date:    24.08.2005\n");
  print("INFO: -------------------------------\n");
}

## MAIN PROGRAM ###############################################################

if($GRASS::DEBUG >=1) {
  GRASSprint("INFO: Reconciliation started\n");
}

###############################################################################
# RUN ADAPTORS #
if ( $option eq "all" || $option eq "oa" ){
   do_run_adaptor();
}

###############################################################################
# MATCHING ENGINE #
if ( $option eq "all" || $option eq "me" ){
  do_matching();
}


if($GRASS::DEBUG >=1) {
  GRASSprint("INFO: Reconciliation complete\n");
}
exit;
