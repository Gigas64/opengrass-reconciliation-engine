package GRASS;

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

=head1 B<NAME>

=head2 B<GRASS.pm>

GRASS.pm - Central Module


=head1 B<DESCRIPTION>

Definition of global variables and central functions like
log messages, central exit function.

Global (exported) variables are:
  $GRASS_HOME
  $business_name
  $display_name
  $sector_name
  $category_name
  $primary
  $secondary
  $DEBUG
  $tracker_type
  $vaulted
  $expected_completion
  $expiry_count
  $run_offset
  $core_debug
  $tracker_debug
  $max_match_ref
  $max_break_ref
  $max_log
  # @manipulation_scripts
  $no_key
  # $match_duplicates
  # $consolidate
  @transfer_names
  %transfer_source
  %transfer_script
  # %primary_consolidation_types
  # %secondary_consolidation_types
  %tolerance_values
  %format_types
  %format_precision
  # $one_sided_tolerance
  @keys
  @matches
  @infos
  @primary_info
  @secondary_info
  $cost_centre
  $cost_band
  $cost_signatory
  $cost_justification
  $deployment_sponsor
  $deployment_reason
  $deployment_date
  $access_authority
  $backup_authority
  $business_signoff
  $qa_signoff
  $deployer_signoff
  $support_signoff
  $dev_signoff



=for comment
    Debug Levels checked!


=cut

use Exporter;
use Getopt::Long;
use File::Copy;
use File::stat;
use Grass::gprops_reader;

@ISA = ('Exporter');

@EXPORT = qw(
  init_GRASS
  GRASSdie
  GRASSprint
  start_message
  complete_message
);

@EXPORT_OK = qw(

  $GRASS_HOME
  $business_name
  $display_name
  $sector_name
  $category_name
  $primary
  $secondary
  $DEBUG
  $tracker_type
  $vaulted
  $expected_completion
  $expiry_count
  $run_offset
  $core_debug
  $tracker_debug
  $max_match_ref
  $max_break_ref
  $max_log
  $no_key
  @transfer_names
  %transfer_source
  %transfer_script
  %tolerance_values
  %format_types
  %format_precision
  @keys
  @matches
  @infos
  @primary_info
  @secondary_info
  $cost_centre
  $cost_band
  $cost_signatory
  $cost_justification
  $deployment_sponsor
  $deployment_reason
  $deployment_date
  $access_authority
  $backup_authority
  $business_signoff
  $qa_signoff
  $deployer_signoff
  $support_signoff
  $dev_signoff
);

###############################################################################
# Author: Georg Riker/Darryl Oatridge
###############################################################################
sub ident_GRASS {

    print STDERR "-------------------------------\n";
    print STDERR " Module: GRASS.pm\n";
    print STDERR "   Version: 1.00.001\n";
    print STDERR "   Date:    29.08.2005 \n";
    print STDERR "-------------------------------\n";

}

################################################################################
# Consts
################################################################################

################################################################################
# Variables
################################################################################

# Strings
my $GRASS_HOME    = $ENV{GRASS_HOME};
my $business_name = 'DefaultRec';
my $display_name  = 'Default Reconciliation';
my $sector_name   = '';
my $category_name = '';
my $primary       = '';
my $secondary     = '';

# Integers
my $DEBUG = 0;
my $tracker_type;
my $vaulted;
my $expected_completion;
my $expiry_count;
my $run_offset;
my $core_debug    = 0;
my $tracker_debug = 0;
my $max_match_ref = 0;
my $max_break_ref = 0;
my $max_log       = 0;

# Array of extra file transfer scripts
my @manipulation_scripts = ();

# Booleans for match types
my $no_key              = 0;
# my $match_duplicates    = 0;
# my $consolidate         = 0;
# my $one_sided_tolerance = 0;

# transfer information
my @transfer_names   = ();
my %transfer_source  = ();
my %transfer_script  = ();
my %transfer_contact = ();
my %transfer_cover   = ();
my %transfer_addinfo = ();

# field -> consolidation type mappings
# (aggregate,concatenate_unique,concatenate_verbose,tolerate,non_tolerate)
# my %primary_consolidation_types  = ();
# my %secondary_consolidationtypes = ();
my %tolerance_values             = ();
my %format_types                 = ();
my %format_precisions            = ();

# field type arrays
my @keys           = ();
my @matches        = ();
my @infos          = ();
my @primary_info   = ();
my @secondary_info = ();

# managment info
my $cost_centre        = "";
my $cost_band          = "";
my $cost_signatory     = "";
my $cost_justification = "";
my $deployment_sponsor = "";
my $deployment_reason  = "";
my $deployment_date    = "";
my $access_authority   = "";
my $backup_authority   = "";
my $business_signoff   = "";
my $qa_signoff         = "";
my $deployer_signoff   = "";
my $support_signoff    = "";
my $dev_signoff        = "";

# directories for general use (BA = business area)
my $BA_HOME = "";

####################################################################################

################################################################################
################################################################################

=head2 B<init_grass>

=over
=item Description:
Set paths of business area, set the logfile path
an read the gprops of the reconciliation.
=back

=over
=item INPUT: - Businessname - e.g. TestRec
=back

=over
=item RETURN-VALUE:
No direct Return Value.
(Global) Variables from the GRASS.pm module are initialized.
=back

=cut


sub init_GRASS {
    my $business_name = shift;

    $GRASS::GRASS_HOME = $ENV{GRASS_HOME};
    $GRASS::BA_HOME    = "$GRASS::GRASS_HOME/ertba/$business_name/";

    $GRASS::business_name = $business_name;

    $GRASS::log_dir = "$GRASS::GRASS_HOME/ertba/$GRASS::business_name/logs";

## GET PROPERTIES FROM GPROPS FILE #
    read_gprops($business_name);

}

=head2 B<GRASSprint>

=over
=item Description:
  Printing to the logfile of the open filehandle.
  Add a time stamp at the beginning of each line.
=back

=over
=item INPUT: - Message to print to the logfile.
=back

=cut

sub GRASSprint {
  my $err_msg = shift;

  # Get a timestamp
  ( $sec, $min, $hours, $day, $month, $year, $wday, $yday, $isdst ) = localtime;
  my $display_date = sprintf("%04d-%02d-%02d %02d:%02d:%02d",
                              $year + 1900,$month + 1, $day, $hours, $min, $sec);

  # print to STDERR
  print STDERR "[" . $display_date . "] " . $err_msg;

}



=head2 B<GRASSdie>

=over
=item Description:
Central exit function which ensures to close the log file.

=item INPUT:
- Error Message
- Module which created the error message

=item RETURN-VALUE:
STOPS the whole process. Displays the last given error message
and the module which cuased the system to die.
=back

=cut

sub GRASSdie {
  my $err_msg = shift;
  my $module  = shift;

  # remove end of lines just in case
  chomp($err_msg);

  GRASSprint($err_msg . " - [" . $module . "]\n");

  exit;
}

=head2 B<start_message>

=over
=item Description:
- used for function starts
=back

=cut

sub start_message {

    if ( $GRASS::DEBUG >= 2 ) {
        my $this_function = (caller(1))[3];
        GRASSprint("INFO: $this_function started\n");
    }

}

=head2 B<complete_message>

=over
=item Description:
- used for function completion when debug tracking
=back

=cut

sub complete_message {
    if ( $GRASS::DEBUG >= 2 ) {
        my $this_function = (caller(1))[3];
        GRASSprint("INFO: $this_function completed\n");
    }
}

1;
