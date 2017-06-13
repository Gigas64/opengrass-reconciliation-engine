package Grass::alignment_engine;

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


Grass::alignment_engine.pm - Alignment of tracker data, tracker log and results data


=head1 B<DESCRIPTION>

Apply the tracker log to the origional tracker data to align the tracker.xml then add
in additional breaks from the results files.



=head1

=head2

=head3

=for comment
    Debug Levels checked!


=cut


use Exporter;
use lib "$ENV{GRASS_HOME}/src/perl";

use Getopt::Long;

use Time::Local;
use locale;

use GRASS;

@ISA    = ('Exporter');
@EXPORT = qw(ident_alignment_engine do_alignment);




#######################################################################################################################

my $mod = "alignment_engine";
my $module="   Module : alignment_engine.pm";
my $version="   Version: 1.00.000";
my $date="   Date:    25.08.2005";

sub ident_alignment_engine {

    if ( $GRASS::DEBUG > 2 ) {
        ### GRASSprint is only possible after initialisation
        print_version ($module, $version, $date);

    }

}
#######################################################################################################################

=head1 B<MAIN FUNCTION>

=head2 B<do_alignment>

=over

=item INPUT: -

=back

=over

=item OUTPUT: -

=back

=over

=item RETURN-VALUE: -


=back

=cut

sub do_alignment {

    ident_alignment_engine();

    # Get a timestamp
    ( $sec, $min, $hours, $day, $month, $year, $wday, $yday, $isdst ) = localtime;

    my $display_date = sprintf("%04d%02d%02d_%02d%02d%02d",($year + 1900),$month + 1, $day, $hours, $min, $sec);


    # data directories
    my $data_dir  = "${GRASS::GRASS_HOME}/ertba/$GRASS::business_name/data";
    my $dest_dir1 = "$data_dir/3_matched";
    my $dest_dir2 = "$data_dir/4_tracked";
    my $dest_dir3 = "$data_dir/5_archive";

    my $breaks_file       = "$dest_dir1/${GRASS::business_name}_breaks.xml";
    my $tracked_file      = "$dest_dir2/${GRASS::business_name}_tracked.xml";
    my $tracklog_file     = "$dest_dir2/${GRASS::business_name}_tracklog.csv";
    my $arc_tracked_file  = "$dest_dir3/${GRASS::business_name}_tracked_$display_date.xml";
    my $arc_tracklog_file = "$dest_dir3/${GRASS::business_name}_tracklog_$display_date.csv";

    if ( !-e $dest_dir1 ||  !-e $dest_dir2 ) {
      GRASSdie( "ERROR: One of the reference directories does not exist: $!", $mod );
    }


    ###########
    ## ARCHIVING
    ## Archive the current files and work from the archive to produce the
    ## new aligned tracker file.
    ##
    if ( !-e $dest_dir3 ) {
        if ( $GRASS::DEBUG >= 2 ) { GRASSprint("DEBUG: Creating Directory $dest_dir3\n"); }
        mkdir( $dest_dir3, 0755 )
          or GRASSdie( "ERROR: Couldn't mkdir $dest_dir3: $!", $mod );
    }

    rename($tracked_file,$arc_tracked_file);
    rename($tracklog_file,$arc_tracklog_file);


    ###########
    ## LOAD TRACKER DATA
    ## Open and read in the tracker data for processing.
    ##

    # section variables
    my is_root = 0;
    my is_rec = 0

    # Loading the archive tracker file
    if ( $GRASS::DEBUG >= 1 ) { GRASSprint("DEBUG: Loading $arc_tracked_file\n"); }
    open( ARCTRACK, "<$arc_tracked_file" )
      || GRASSdie( "ERROR: Can't open XML Archive Tracker file [$arc_tracked_file]: $!\n",$mod );

    # read in the whole file
    while (<ARCTRACK>) {
      chomp();
      if($is_root) {
        $is_root=0;
        next;
      }
      if($is_rec) {
        # load the record into a string

        $is_rec=0;
        next;
      }


      $gprops_info .= $_ }
    close(ARCTRACK);










    # remove any comments
    $gprops_info =~ s/.*<!--.*->//g;

    # extract all the _info sections and check their form.
    foreach $name (
        'setup_info', 'property_info',
        'file_info',  'field_info',
        'management_info'
      )
    {
        $gprops_info =~ /\<($name)\>(.*)\<\/($name)\>/gs;
        if ( $1 eq $name && $1 eq $3 ) {
            ${$name} = $2;
        }
        else {
            die("ERROR: The tag <$name> is not properly formed in $gprops_file\n"
            );
        }
    }
    # get transfer info variables
    while ( $file_info =~ /<(transfer_info)>(.*?)\<\/(transfer_info)\>/gs ) {
        if ( $1 eq 'transfer_info' && $1 eq $3 ) {
            my $transfer_info = $2;
            if ( length $2 < 1 ) {
                die("ERROR: There has been no transfer_info found in $gprops_file\n"
                );
            }

            # get the transfer_name
            $transfer_info =~ /<transfer_name>(.*?)\<\/transfer_name\>/m;
            if ( length $1 < 1 ) {
                die("ERROR: transfer_name must contain a value in $gprops_file\n"
                );
            }
            my $transfer_name = $1;
            push( @GRASS::transfer_names, $1 );












    ###########
    ## LOG ALIGNMENT
    ## Run through the tracker log and align the tracker data.
    ##



    # Loading the archive tracker log
    if ( $GRASS::DEBUG >= 1 ) { GRASSprint("DEBUG: Applying the tracker log $arc_tracklog_file\n"); }
    open( ARCLOG, "<$arc_tracklog_file" )
      || GRASSdie( "ERROR: Can't open CSV Archive Tracker Log file [$arc_tracklog_file]: $!\n",$mod );

    close(ARCLOG);

    # Opening the new tracker file
    open( TRACKER, ">$tracked_file" )
      || GRASSdie( "ERROR: Can't create XML Tracker file [$tracked_file]: $!\n",$mod );


    # print headers in files
    print TRACKER "<?xml version='1.0' encoding='ISO-8859-1'?>\n";



    close(TRACKER);


    ###########
    ## BREAKS ALIGNMENT
    ## Open the Breaks file and align the new breaks with the new tracker file
    ##

    open( BREAK, "<$breaks_file" )
      || GRASSdie( "ERROR: Can't open XML Breaks file [$breaks_file]: $!\n",$mod );
    close(BREAK);



}

1;

