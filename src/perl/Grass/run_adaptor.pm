package Grass::run_adaptor;

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

=head1 B<NAME>Grass::run_adaptor.pm - Module to start OpenAdaptor processes
=head1 B<DESCRIPTION>

=head2 OpenAdaptor
Reads file source data, adapts file source in accordance with appropriate property
files (both primary and secondary props). Produces primary and secondary xml files.

=head2 run_adaptors
The perl module starts the java process data preparation of the source files for
the matching engine. Normally the File Input is a comma separated list. The output
is a XML file.

=for comment

=cut

use Exporter;
use lib "$ENV{GRASS_HOME}/src/perl";

use Getopt::Long;

use GRASS;

@ISA    = ('Exporter');
@EXPORT = qw( do_run_adaptor );

#######################################################################################################################

=head1 B<MAIN FUNCTION>
=head2 B<do_run_adaptors>

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

sub do_run_adaptor {

  # Version number
  if($GRASS::DEBUG) {
    print("INFO: -------------------------------\n");
    print("INFO: MODULE run_adaptor.pl\n");
    print("INFO:    Version: 1.00.000\n");
    print("INFO:    Date:    24.08.2005\n");
    print("INFO: -------------------------------\n");
  }


# SET CLASSPATH #############################################################

  #sort out operating system for classpath
  $sep = ':';
  if ( $^O eq 'Windows_NT' or $^O eq 'MSWin32' or $^O eq 'dos' ) {
    $sep = ';';
  }

  ($JAVA_HOME) = $ENV{JAVA_HOME};
  if (length $JAVA_HOME == 0) {
      GRASSdie ("ERROR: JAVA_HOME not found in your environment\n","run_adaptor"); }

  ($OA_HOME) = $ENV{OA_HOME};
  if (length $OA_HOME == 0) {
      GRASSdie ("ERROR: OA_HOME not found in your environment\n","run_adaptor"); }

  ($GRASS_HOME)=$ENV{GRASS_HOME};
  if (length $GRASS_HOME == 0) {
      GRASSdie ("ERROR: GRASS_HOME not found in your environment\n","run_adaptor"); }
  $GRASS_HOME =~ s/\\/\//g;

  #Add in any Jar files from our lib directory
  $class_add="";
  opendir(DIR,"${GRASS_HOME}/lib/") or
              GRASSdie("ERROR: Unable to open ${GRASS_HOME}/lib: $!","run_adaptor");
  while (defined($file = readdir(DIR))) {
      next if $file =~ /^\.\.?$/;
    $class_add .= "${GRASS_HOME}/lib/${file}$sep";
  }
  opendir(DIR,"${OA_HOME}/classes/") or
              GRASSdie("ERROR: Unable to open ${OA_HOME}/classes/: $!","run_adaptor");
  while (defined($file = readdir(DIR))) {
      next if $file =~ /^\.\.?$/;
    $class_add .= "${OA_HOME}/classes/${file}$sep";
  }

  my $CLASSPATH="$JAVA_HOME/lib/tools.jar${sep}${class_add}$JAVA_HOME/lib/dev.jar";
  if ( $GRASS::DEBUG >= 3 ) { print STDERR "CLASSPATH = $CLASSPATH\n\n"; }

## SET FILES AND DIRECTORIES ##################################################

    $adaptor_dir    = "$GRASS::BA_HOME/data/1_raw_data";
    $match_dir      = "$GRASS::BA_HOME/data/2_normalised";

    foreach $d ( $adaptor_dir, $match_dir ) {
      if ( !-e $d ) {
        if ( $GRASS::DEBUG >= 2 ) { print STDERR "INFO: Creating Directory $d\n"; }
        mkdir( $d, 0755 )
          or GRASSdie( "ERROR: Couldn't mkdir $d: $!", $mod );
      }
    }

## MAIN PROGRAM #####################################################


    my $p_pid = start_adaptor($GRASS::primary,$CLASSPATH);
    my $s_pid = start_adaptor($GRASS::secondary,$CLASSPATH);

    waitpid( $p_pid, 0 );
    waitpid( $s_pid, 0 );

    if($GRASS::DEBUG >=1) {
      GRASSprint("INFO: Run Adaptor complete\n");
    }

}


=head2 B<start_adaptor>

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


sub start_adaptor {
    my $system = shift;
    my $CLASSPATH = shift;
    my $props  = '';
    my $input  = '';
    my $output = '';

    start_message();

    $props = "$GRASS::BA_HOME/conf/$system.props";
    $input = "$GRASS::BA_HOME/data/1_raw_data/";
    $output = "$GRASS::BA_HOME/data/2_normalised/$system.xml";

    if ( $GRASS::DEBUG >= 3 ) {
        GRASSprint ("DEBUG: Working with the following directories:\n");
        GRASSprint ("DEBUG: Input dir  - $input\n");
        GRASSprint ("DEBUG: Output dir - $output\n");
    }

    my $pid = 0;

    # get all the transfer_names
    my $transfer_file_options = "";
    foreach $transfer_name (@GRASS::transfer_names) {
        $transfer_file_options .= "-D$transfer_name=${input}@{$GRASS::transfer_source{$transfer_name}}[5] ";
    }

    # create options
    my $options = ""
      . "-classpath $CLASSPATH "
      . "-DDATA_PATH=$input "
      . "$transfer_file_options "
      . "-DADAPTOR_PATH=$adaptor_dir "
      . "-DOUTPUT=$output ";

    if ( $GRASS::DEBUG >= 4 ) {
        GRASSprint("DEBUG: running adaptors with options $options\n");
    }

    # create bootstrap
    my $bootstrap = "org.openadaptor.adaptor.RunAdaptor";

    foreach $f ( $input, $props ) {
        GRASSdie( "ERROR: '$f' does not exist: $!\n", $mod ) if ( !-e $f );
    }

    if ( $GRASS::DEBUG >= 3 ) {
        GRASSprint("RUNNING: $bootstrap $props Adaptor\n");
    }

    if ( !defined( $pid = fork() ) ) {
        GRASSdie( "ERROR: Unable to fork system: $!", $mod );
    }
    elsif ( !$pid ) {    #child
       exec "$JAVA_HOME/bin/java $options $bootstrap $props Adaptor > $GRASS::log_dir/${system}.log 2>&1";
    }

    complete_message();

    return $pid;
}

1;
