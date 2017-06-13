package Grass::gprops_reader;

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

Grass::gprops_reader.pm - Module to read gprops config file


=head1 B<DESCRIPTION>

Reading all gprops values into the global variables of GRASS.pm.


=for comment
    Debug Levels checked!


=cut

use Exporter;
use lib "$ENV{GRASS_HOME}/src/perl";

use Getopt::Long;
use GRASS;

@ISA    = ('Exporter');
@EXPORT = qw(ident_gprops_reader
  read_gprops
  gen_transfer_info
  gen_management_info
  print_gprops
  GRASSprint_gprops
  get_GRASS_HOME
  get_global_email_ref
  get_global_email_support
  get_global_email_deploy
  get_core_debug
  get_tracker_debug
  get_business_name
  get_display_name
  get_sector_name
  get_category_name
  get_primary
  get_secondary
  get_tracker_type
  get_expected_completion
  get_expiry_count
  get_run_offset
  get_vaulted
  get_max_match_ref
  get_max_break_ref
  get_max_log
  get_transfer_names
  get_transfer_source
  get_transfer_script
  get_no_key
  get_match_duplicates
  get_one_sided_tolerance
  get_primary_consolidation_types
  get_secondary_consolidation_types
  get_manipulation_scripts
  get_tolerance_values
  get_format_types
  get_format_precision
  get_keys
  get_matches
  get_primary_info
  get_secondary_info
  get_consolidate
);

sub ident_gprops_reader {

    if ( $GRASS::DEBUG > 2 ) {
        ### GRASSprint is only possible after initialisation
        print("-------------------------------\n");
        print(" Module: gprops_reader.pm\n");
        print("   Version: 1.16.000\n");
        print("   Date:    20.08.2004 \n\n");
        print("-------------------------------\n");
    }

}

=head1 B<MAIN FUNCTION>

=head2 B<read_gprops>



=cut


sub read_gprops {

    $business_name = shift;

    if ( length $business_name == 0 ) {
        die("ERROR: Gprops reader module must be passed a business name\n");
    }

    $GRASS_HOME = $ENV{GRASS_HOME};
    if ( length $GRASS_HOME == 0 ) {
        die("ERROR: GRASS_HOME has zero length in Gprops reader module\n");
    }
    $GRASS_HOME =~ s/\\/\//g;

    $GRASS::consolidate = 0;
    $GRASS::no_key      = 0;

    # initialise the info variables
    my $gprops_info = '';

    # set gprops and ggprops file name
    my $gprops_file = "$GRASS::BA_HOME/conf/$business_name.gprops";
    my $opengprops_file = "$GRASS_HOME/OpenGprops.xml";

    #Get the gerneral GRASS parameters
    open( OGPROP, "<$opengprops_file" )
      || die("ERROR: OpenGprops reader module failed to open properties file $opengprops_file: $!\n");
    while (<OGPROP>) { $ogprops_info .= $_ }
    close(OGPROP);

    # remove any comments
    $ogprops_info =~ s/.*<!--.*->//g;

    # get the field sections from field_info
    foreach $name ( 'global_email_ref', 'global_email_support', 'global_email_deploy' ) {
        if ( $ogprops_info =~ /\<$name\>(.*)\<\/$name\>/gs ) {
            ${"GRASS::$name"} = $1;
        }
        else {
            die("ERROR: Global refernce tag <$name> is not correctly specified in $opengprops_file\n");
        }
    }

    # open the gprops file
    open( GPROPS, "<$gprops_file" )
      || die("ERROR: Gprops reader module failed to open properties file $gprops_file: $!\n");

    # read in the whole file
    while (<GPROPS>) { $gprops_info .= $_ }
    close(GPROPS);

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

    # get the field sections from field_info
    foreach $name ( 'keys', 'matches', 'info' ) {
        $field_info =~ /\<($name)\>(.*)\<\/($name)\>/gs;
        if ( $1 eq $name && $1 eq $3 ) {
            ${$name} = $2;
        }
        else {
            die("ERROR: The tag <$name> is not properly formed in $gprops_file\n"
            );
        }
    }

    # get setup info variables
    foreach $name (
        'business_name', 'display_name', 'sector_name', 'category_name',
        'primary',       'secondary'
      )
    {
        if ( $setup_info =~ /\<$name\>(.*)\<\/$name\>/m ) {
            ${"GRASS::$name"} = $1;
        }
        else {
            die("ERROR: setup_value tag <$name> is not correctly specified in $gprops_file\n"
            );
        }

        # check everything has a value
        if ( length $1 < 1 ) {
            die("ERROR: Tag <$name> must contain a value in $gprops_file\n");
        }
    }

    # get property info variables
    foreach $name (
        'tracker_type',        'vaulted',
        'expected_completion', 'expiry_count',
        'run_offset',          'core_debug',
        'tracker_debug',       'max_match_ref',
        'max_break_ref',       'max_log',
        'match_duplicates',    'one_sided_tolerance'
      )
    {
        if ( $property_info =~ /\<$name\>(.*)\<\/$name\>/m ) {
            ${"GRASS::$name"} = $1;
        }
        else {
            die("ERROR: property_info tag <$name> is not correctly specified in $gprops_file\n"
            );
        }

        # check everything has a value
        if ( length $1 < 1 ) {
            die("ERROR: Tag <$name> must contain a value in $gprops_file\n");
        }
    }

    # get management info variables
    foreach $name (
        'cost_centre',        'cost_band',
        'cost_signatory',     'cost_justification',
        'deployment_sponsor', 'deployment_reason',
        'deployment_date',    'access_authority',
        'backup_authority'
      )
    {
        if ( $management_info =~ /\<$name\>(.*)\<\/$name\>/m ) {
            ${"GRASS::$name"} = $1;
        }
        else {
            die("ERROR: property_info tag <$name> is not correctly specified in $gprops_file\n"
            );
        }

        # check everything has a value
        if ( length $1 < 1 ) {
            die("ERROR: Tag <$name> must contain a value in $gprops_file\n");
        }
    }

    # get management info variables
    foreach $name (
        'business_signoff', 'qa_signoff',
        'deployer_signoff', 'support_signoff',
        'dev_signoff'
      )
    {
        if ( $management_info =~ /\<$name\>(.*)\<\/$name\>/m ) {
            if ( defined $1 ) {
                ${"GRASS::$name"} = $1;
            }
        }
        else {
            ${"GRASS::$name"} = "";
            print("WARN: management_info tag <$name> is not or not correctly specified in $gprops_file\n"
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

            # get the transfer_source
            $transfer_info =~
              /<(transfer_source)>(.*?)\<\/(transfer_source)\>/gs;
            if ( $1 eq 'transfer_source' && $1 eq $3 ) {
                my $transfer_source = $2;
                foreach $name ( 'type', 'login', 'password', 'machine', 'path',
                    'filename' )
                {
                    if ( $transfer_source =~ /\<$name\>(.*)\<\/$name\>/m ) {

                        # add as an array to the transfer_source hash
                        push( @{ $GRASS::transfer_source{$transfer_name} },
                            $1 );
                    }
                    else {
                        die("ERROR: transfer_source tag <$name> is not correctly specified in $gprops_file\n"
                        );
                    }
                }
            }
            else {
                die("ERROR: The tag <transfer_source> is not properly formed in $gprops_file\n"
                );
            }

            # check everything has a value
            if ( length $1 < 1 ) {
                die("ERROR: Tag <$name> must contain a value in $gprops_file\n"
                );
            }

            # get any scripts
            $transfer_info =~ /<transfer_script>(.*?)\<\/transfer_script\>/m;
            if ( length $1 > 0 ) {
                $GRASS::transfer_script{$transfer_name} = $1;
            }
            else { $GRASS::transfer_script{$transfer_name} = "" }

            # make sure the format is right and add filenames

            $transfer_info =~ /<(contacts)>(.*?)\<\/(contacts)\>/gs;
            if ( $1 eq 'contacts' && $1 eq $3 ) {
                my $transfer_contacts = $2;
                foreach $name ( 'contact', 'cover' ) {
                    if ( $transfer_contacts =~ /\<$name\>(.*)\<\/$name\>/gs ) {

                        # add as an array to the transfer_source hash
                        my $transfer_c = $1;
                        if ( $name eq 'contact' ) {
                            foreach $name1 ( 'name', 'number', 'email' ) {
                                if ( $transfer_c =~
                                    /\<$name1\>(.*)\<\/$name1\>/m )
                                {

                                   # add as an array to the transfer_source hash
                                    push(
                                        @{
                                            $GRASS::transfer_contact{
                                                $transfer_name}
                                          },
                                        $1
                                    );
                                }
                                else {
                                    die("ERROR: contact tag <$name1> is not correctly specified in $gprops_file\n"
                                    );
                                }
                            }
                        }
                        if ( $name eq 'cover' ) {
                            foreach $name2 ( 'name', 'number', 'email' ) {
                                if ( $transfer_c =~
                                    /\<$name2\>(.*)\<\/$name2\>/m )
                                {

                                   # add as an array to the transfer_source hash
                                    push(
                                        @{
                                            $GRASS::transfer_cover{
                                                $transfer_name}
                                          },
                                        $1
                                    );
                                }
                                else {
                                    die("ERROR: cover tag <$name2> is not correctly specified in $gprops_file\n"
                                    );
                                }
                            }
                        }
                    }

                }
            }
            else {
                die("ERROR: The tag <contacts> is not properly formed in $gprops_file\n"
                );
            }

            # get additional transfer info
            foreach $name ( 'system_name', 'delivery_schedule', 'notes' ) {
                if ( $transfer_info =~ /\<$name\>(.*)\<\/$name\>/m ) {

                    # add as an array to the transfer_source hash
                    push( @{ $GRASS::transfer_addinfo{$transfer_name} }, $1 );
                }
                else {
                    die("ERROR: transfer_addinfo tag <$name> is not correctly specified in $gprops_file\n"
                    );
                }

            }

        }
        else {
            die("ERROR: The tag <transfer_info> is not properly formed in $gprops_file\n"
            );
        }
    }

    # get key fields
    while ( $keys =~ /(<field .*>.*?<\/field\>)/gm ) {
        push( @a_keys, $1 );
    }
    foreach $field (@a_keys) {

        if ( $field =~/<field (p_c)=\"([^"]*?)\" (s_c)=\"([^"]*?)\" (format)=\"([^"]*?)\">(.*?)<\/field\>/
          )
        {
            $field =~/<field (p_c)=\"([^"]*?)\" (s_c)=\"([^"]*?)\" (format)=\"([^"]*?)\">(.*?)<\/field\>/;

            if (   ( defined $1 && defined $3 && defined $5 )
                && ( $1 eq 'p_c' && $3 eq 's_c' && $5 eq 'format' ) )
            {
                push( @GRASS::keys, $7 );
                if ( defined $2 && length $2 > 0 ) {
                    $GRASS::primary_consolidation_types{$7} = $2;
                }
                else {
                    $GRASS::primary_consolidation_types{$7} = 'non_tolerate';
                }
                if ( defined $4 && length $4 > 0 ) {
                    $GRASS::secondary_consolidation_types{$7} = $4;
                }
                else {
                    $GRASS::secondary_consolidation_types{$7} = 'non_tolerate';
                }
                my $format = $6;
                my $field  = $7;
                if ( defined $6 && length $6 > 0 ) {
                    if ( $format =~ m/numeric,(\d)/ ) {
                        $GRASS::format_types{$field}     = "numeric";
                        $GRASS::format_precision{$field} = $1;
                    }
                    else {
                        $GRASS::format_types{$field}     = $format;
                        $GRASS::format_precision{$field} = 'default';
                    }
                }
                else {
                    $GRASS::format_types{$field}     = 'default';
                    $GRASS::format_precision{$field} = 'default';
                }
            }
            else {
                die("ERROR: The tag <key> is not properly formed in $gprops_file (missing p_c|s_c|format option)\nLine: $field\n"
                );
            }
        }
        else {
            die("ERROR: The tag <key> is not properly formed in $gprops_file (missing p_c|s_c|format option)\nLine: $field\n"
            );
        }

    }

    # get match fields

    while ( $matches =~ /(<field .*>.*?<\/field\>)/gm ) {
        push( @a_matches, $1 );
    }

    foreach $field (@a_matches) {

        if ( $field =~/<field (p_c)=\"([^"]*?)\" (s_c)=\"([^"]*?)\" (format)=\"([^"]*?)\" (tolerance)=\"([^"]*?)\">(.*?)<\/field\>/
          )
        {

            $field =~/<field (p_c)=\"([^"]*?)\" (s_c)=\"([^"]*?)\" (format)=\"([^"]*?)\" (tolerance)=\"([^"]*?)\">(.*?)<\/field\>/;

            if (
                ( defined $1 && defined $3 && defined $5 && defined $7 )
                && (   $1 eq 'p_c'
                    && $3 eq 's_c'
                    && $5 eq 'format'
                    && $7 eq 'tolerance' )
              )
            {
                push( @GRASS::matches, $9 );

                if ( defined $2 && length $2 > 0 ) {
                    $GRASS::primary_consolidation_types{$9} = $2;
                }
                else {
                    $GRASS::primary_consolidation_types{$9} = 'non_tolerate';
                }
                if ( defined $4 && length $4 > 0 ) {
                    $GRASS::secondary_consolidation_types{$9} = $4;
                }
                else {
                    $GRASS::secondary_consolidation_types{$9} = 'non_tolerate';
                }
                my $tol    = $8;
                my $format = $6;
                my $field  = $9;
                if ( defined $6 && length $6 > 0 ) {
                    if ( $format =~ m/numeric,(\d)/ ) {
                        $GRASS::format_types{$field}     = "numeric";
                        $GRASS::format_precision{$field} = $1;
                    }
                    else {
                        $GRASS::format_types{$field}     = $format;
                        $GRASS::format_precision{$field} = 'default';
                    }
                }
                else {
                    $GRASS::format_types{$field}     = 'default';
                    $GRASS::format_precision{$field} = 'default';
                }
                if ( defined $tol && length $tol > 0 ) {
                    $GRASS::tolerance_values{$field} = $tol;
                }
                else { $GRASS::tolerance_values{$field} = '' }
            }
            else {
                die("ERROR: The tag <matches> is not properly formed in $gprops_file (missing p_c|s_c|format or tolerance option)\nLine: $field\n"
                );
            }
        }
        else {
            die("ERROR: The tag <matches> is not properly formed in $gprops_file (missing p_c|s_c|format or tolerance option)\nLine: $field\n"
            );
        }
    }

    # get info fields
    while ( $info =~ /(<field .*>.*?<\/field\>)/gm ) {
        push( @a_info, $1 );
    }
    foreach $field (@a_info) {

        if ( $field =~/<field (p_c)=\"([^"]*?)\" (s_c)=\"([^"]*?)\" (format)=\"([^"]*?)\">(.*?)<\/field\>/
          )
        {
            $field =~/<field (p_c)=\"([^"]*?)\" (s_c)=\"([^"]*?)\" (format)=\"([^"]*?)\">(.*?)<\/field\>/;

            if (   ( defined $1 && defined $3 && defined $5 )
                && ( $1 eq 'p_c' && $3 eq 's_c' && $5 eq 'format' ) )
            {
                push( @GRASS::infos, $7 );

                if ( defined $2 && length $2 > 0 ) {
                    push( @GRASS::primary_info, $7 );
                    $GRASS::primary_consolidation_types{$7} = $2;
                }
                if ( defined $4 && length $4 > 0 ) {
                    push( @GRASS::secondary_info, $7 );
                    $GRASS::secondary_consolidation_types{$7} = $4;
                }
                my $format = $6;
                my $field  = $7;
                if ( defined $6 && length $6 > 0 ) {
                    if ( $format =~ m/numeric,(\d)/ ) {
                        $GRASS::format_types{$field}     = "numeric";
                        $GRASS::format_precision{$field} = $1;
                    }
                    else {
                        $GRASS::format_types{$field}     = $format;
                        $GRASS::format_precision{$field} = 'default';
                    }
                }
                else {
                    $GRASS::format_types{$field}     = 'default';
                    $GRASS::format_precision{$field} = 'default';
                }
            }
            else {
                die("ERROR: The tag <info> is not properly formed in $gprops_file (missing p_c|s_c|format option)\nLine: $field\n"
                );
            }
        }
        else {
            die("ERROR: The tag <info> is not properly formed in $gprops_file (missing p_c|s_c|format option)\nLine: $field\n"
            );
        }
    }
    # Before we go on lets make sure our fields are unique
    my %test_lookup;
    my @fields_lookup;
    my $field_count;
    @test_lookup{@GRASS::keys} = ();
    @test_lookup{@GRASS::matches} = ();
    @test_lookup{@GRASS::infos} = ();
    @fields_lookup = keys %test_lookup;
    $field_count = scalar @GRASS::keys + scalar @GRASS::matches + scalar @GRASS::infos - scalar @fields_lookup;
    if( $field_count ) {
        die("ERROR: There is duplicate field names in $gprops_file \n");
    }

    if ( !scalar @GRASS::keys ) { $GRASS::no_key = 1 }

    foreach $type (
        values %GRASS::primary_consolidation_types,
        values %GRASS::secondary_consolidation_types
      )
    {
        if ( $type ne 'non_tolerate' ) {
            $GRASS::consolidate = 1;
        }
    }

    $GRASS::DEBUG = $GRASS::core_debug;
    if ( $GRASS::core_debug > 4 ) { print_gprops() }
    ident_gprops_reader();

}

###############################################################################
# FUNCTIONS #

=head1 B<FUNCTIONS>

=head2 B<print_gprops>

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

sub print_gprops {

    print "\nGRASS PROPERTIES\n"
      . "----------------------------------------------------------------\n"
      . " \nSETUP INFO\n"
      . "  GRASS HOME            = $GRASS::GRASS_HOME\n"
      . "  Business Name         = $GRASS::business_name\n"
      . "  Display Name          = $GRASS::display_name\n"
      . "  Sector Name           = $GRASS::sector_name\n"
      . "  Category Name         = $GRASS::category_name\n"
      . "  Primary               = $GRASS::primary\n"
      . "  Secondary             = $GRASS::secondary\n"
      . "\n----------------------------------------------------------------\n"
      . " \nPROPERTY INFO\n"
      . "  Tracker Type          = $GRASS::tracker_type\n"
      . "  Vaulted               = $GRASS::vaulted\n"
      . "  Expected Completion   = $GRASS::expected_completion\n"
      . "  Expiry Count          = $GRASS::expiry_count\n"
      . "  Run Offset            = $GRASS::run_offset\n"
      . "  Core Debug            = $GRASS::core_debug\n"
      . "  Tracker Debug         = $GRASS::tracker_debug\n"
      . "  Max Match Ref         = $GRASS::max_match_ref\n"
      . "  Max Break Ref         = $GRASS::max_break_ref\n"
      . "  Max Log               = $GRASS::max_log\n"
      . "  Match Duplicates      = $GRASS::match_duplicates\n"
      . "  One-Sided Tolerance   = $GRASS::one_sided_tolerance\n"
      . "  No Key                = $GRASS::no_key\n"
      . "  Consolidate           = $GRASS::consolidate\n"
      . "\n----------------------------------------------------------------\n"
      . " \nMANAGEMENT INFO\n"
      . "  Cost Centre           = $GRASS::cost_centre\n"
      . "  Cost Band             = $GRASS::cost_band\n"
      . "  Cost Signatory        = $GRASS::cost_signatory\n"
      . "  Cost Justification    = $GRASS::cost_justification\n"
      . "  Deployment Sponsor    = $GRASS::deployment_sponsor\n"
      . "  Deployment Reason     = $GRASS::deployment_reason\n"
      . "  Deployment Date       = $GRASS::deployment_date\n"
      . "  Access Authority      = $GRASS::access_authority\n"
      . "  Backup Authority      = $GRASS::backup_authority\n"
      . "  Business Signoff      = $GRASS::business_signoff\n"
      . "  QA Signoff            = $GRASS::qa_signoff\n"
      . "  Deployer Signoff      = $GRASS::deployer_signoff\n"
      . "  Support Signoff       = $GRASS::support_signoff\n"
      . "  Dev Signoff           = $GRASS::dev_signoff\n"
      . "\n----------------------------------------------------------------\n"
      . " \nGRASS GLOBALS\n"
      . "  Global Ref Email      = $GRASS::global_email_ref\n"
      . "  Global Support Email  = $GRASS::global_email_support\n"
      . "  Global Deploy Email   = $GRASS::global_email_deploy\n"
      . "\n----------------------------------------------------------------\n";

    print " \nFILE INFO\n";

    foreach $transfer_name (@GRASS::transfer_names) {
        print "\n  Transfer name $transfer_name\n";
        print "      transfer_source\n";
        print
          "        Type     = @{$GRASS::transfer_source{$transfer_name}}[0]\n";
        print
          "        Login    = @{$GRASS::transfer_source{$transfer_name}}[1]\n";
        print
          "        Password = @{$GRASS::transfer_source{$transfer_name}}[2]\n";
        print
          "        Machine  = @{$GRASS::transfer_source{$transfer_name}}[3]\n";
        print
          "        Path     = @{$GRASS::transfer_source{$transfer_name}}[4]\n";
        print
          "        Filename = @{$GRASS::transfer_source{$transfer_name}}[5]\n";
        print
          "      transfer_script = $GRASS::transfer_script{$transfer_name}\n";
        print "      transfer_contact\n";
        print
          "        Name     = @{$GRASS::transfer_contact{$transfer_name}}[0]\n";
        print
          "        Number   = @{$GRASS::transfer_contact{$transfer_name}}[1]\n";
        print
          "        Email    = @{$GRASS::transfer_contact{$transfer_name}}[2]\n";
        print "      transfer_cover\n";
        print
          "        Name     = @{$GRASS::transfer_cover{$transfer_name}}[0]\n";
        print
          "        Number   = @{$GRASS::transfer_cover{$transfer_name}}[1]\n";
        print
          "        Email    = @{$GRASS::transfer_cover{$transfer_name}}[2]\n";
        print "      transfer_addinfo\n";
        print
          "        SystemName       = @{$GRASS::transfer_addinfo{$transfer_name}}[0]\n";
        print
          "        DeliverySchedule = @{$GRASS::transfer_addinfo{$transfer_name}}[1]\n";
        print
          "        Notes            = @{$GRASS::transfer_addinfo{$transfer_name}}[2]\n";
        print "\n";
    }

    print
      "\n----------------------------------------------------------------\n";

    print "\nFIELD INFO\n";
    print "\n  KEYS\n";
    foreach $field (@GRASS::keys) {
        print "\n    $field\n";
        print "      consolidate\n";
        print
          "        primary   = $GRASS::primary_consolidation_types{$field}\n";
        print
          "        secondary = $GRASS::secondary_consolidation_types{$field}\n";
        print "      format      = $GRASS::format_types{$field}\n";
        print "      precision   = $GRASS::format_precision{$field}\n";
        print "\n";
    }
    print "\n  MATCHES\n";
    foreach $field (@GRASS::matches) {
        print "\n    $field\n";
        print "      consolidate\n";
        print
          "        primary   = $GRASS::primary_consolidation_types{$field}\n";
        print
          "        secondary = $GRASS::secondary_consolidation_types{$field}\n";
        print "      tolerance   = $GRASS::tolerance_values{$field}\n";
        print "      format      = $GRASS::format_types{$field}\n";
        print "      precision   = $GRASS::format_precision{$field}\n";
        print "\n";
    }

    print "\n  INFO\n";
    foreach $field (@GRASS::primary_info) {
        print "\n    $field\n";
        print "      side        = primary\n";
        print
          "      consolidate = $GRASS::primary_consolidation_types{$field}\n";
        print "      format      = $GRASS::format_types{$field}\n";
        print "      precision   = $GRASS::format_precision{$field}\n";
        print "\n";
    }
    foreach $field (@GRASS::secondary_info) {
        print "\n    $field\n";
        print "      side        = secondary\n";
        print
          "      consolidate = $GRASS::secondary_consolidation_types{$field}\n";
        print "      format      = $GRASS::format_types{$field}\n";
        print "      precision   = $GRASS::format_precision{$field}\n";
        print "\n";
    }

    print
      "\n----------------------------------------------------------------\n";
}

=head2 B<GRASSprint_gprops>

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

sub GRASSprint_gprops {

    GRASSprint( "\nGRASS PROPERTIES\n"
          . "----------------------------------------------------------------\n"
          . " \nSETUP INFO\n"
          . "  GRASS HOME            = $GRASS::GRASS_HOME\n"
          . "  Business Name         = $GRASS::business_name\n"
          . "  Display Name          = $GRASS::display_name\n"
          . "  Sector Name           = $GRASS::sector_name\n"
          . "  Category Name         = $GRASS::category_name\n"
          . "  Primary               = $GRASS::primary\n"
          . "  Secondary             = $GRASS::secondary\n"
          . "\n----------------------------------------------------------------\n"
          . " \nPROPERTY INFO\n"
          . "  Tracker Type          = $GRASS::tracker_type\n"
          . "  Vaulted               = $GRASS::vaulted\n"
          . "  Expected Completion   = $GRASS::expected_completion\n"
          . "  Expiry Count          = $GRASS::expiry_count\n"
          . "  Run Offset            = $GRASS::run_offset\n"
          . "  Core Debug            = $GRASS::core_debug\n"
          . "  Tracker Debug         = $GRASS::tracker_debug\n"
          . "  Max Match Ref         = $GRASS::max_match_ref\n"
          . "  Max Break Ref         = $GRASS::max_break_ref\n"
          . "  Max Log               = $GRASS::max_log\n"
          . "  Match Duplicates      = $GRASS::match_duplicates\n"
          . "  One-Sided Tolerance   = $GRASS::one_sided_tolerance\n"
          . "  No Key                = $GRASS::no_key\n"
          . "  Consolidate           = $GRASS::consolidate\n"
          . "\n----------------------------------------------------------------\n"
          . " \nMANAGEMENT INFO\n"
          . "  Cost Centre           = $GRASS::cost_centre\n"
          . "  Cost Band             = $GRASS::cost_band\n"
          . "  Cost Signatory        = $GRASS::cost_signatory\n"
          . "  Cost Justification    = $GRASS::cost_justification\n"
          . "  Deployment Sponsor    = $GRASS::deployment_sponsor\n"
          . "  Deployment Reason     = $GRASS::deployment_reason\n"
          . "  Deployment Date       = $GRASS::deployment_date\n"
          . "  Access Authority      = $GRASS::access_authority\n"
          . "  Backup Authority      = $GRASS::backup_authority\n"
          . "\n----------------------------------------------------------------\n"
          . " \nGRASS GLOBALS\n"
          . "  Global Ref Email      = $GRASS::global_email_ref\n"
          . "  Global Support Email  = $GRASS::global_email_support\n"
          . "  Global Deploy Email   = $GRASS::global_email_deploy\n"
          . "\n----------------------------------------------------------------\n"
    );

    GRASSprint(" \nFILE INFO\n");

    foreach $transfer_name (@GRASS::transfer_names) {
        GRASSprint("\n  Transfer name $transfer_name\n");
        GRASSprint("      transfer_source\n");
        GRASSprint(
            "        Type     = @{$GRASS::transfer_source{$transfer_name}}[0]\n"
        );
        GRASSprint(
            "        Login    = @{$GRASS::transfer_source{$transfer_name}}[1]\n"
        );
        GRASSprint(
            "        Password = @{$GRASS::transfer_source{$transfer_name}}[2]\n"
        );
        GRASSprint(
            "        Machine  = @{$GRASS::transfer_source{$transfer_name}}[3]\n"
        );
        GRASSprint(
            "        Path     = @{$GRASS::transfer_source{$transfer_name}}[4]\n"
        );
        GRASSprint(
            "        Filename = @{$GRASS::transfer_source{$transfer_name}}[5]\n"
        );
        GRASSprint(
            "      transfer_script = $GRASS::transfer_script{$transfer_name}\n"
        );
        GRASSprint("      transfer_contact\n");
        GRASSprint(
            "        Name     = @{$GRASS::transfer_contact{$transfer_name}}[0]\n"
        );
        GRASSprint(
            "        Number   = @{$GRASS::transfer_contact{$transfer_name}}[1]\n"
        );
        GRASSprint(
            "        Email    = @{$GRASS::transfer_contact{$transfer_name}}[2]\n"
        );
        GRASSprint("      transfer_cover\n");
        GRASSprint(
            "        Name     = @{$GRASS::transfer_cover{$transfer_name}}[0]\n"
        );
        GRASSprint(
            "        Number   = @{$GRASS::transfer_cover{$transfer_name}}[1]\n"
        );
        GRASSprint(
            "        Email    = @{$GRASS::transfer_cover{$transfer_name}}[2]\n"
        );
        GRASSprint("      transfer_addinfo\n");
        GRASSprint(
            "        SystemName       = @{$GRASS::transfer_addinfo{$transfer_name}}[0]\n"
        );
        GRASSprint(
            "        DeliverySchedule = @{$GRASS::transfer_addinfo{$transfer_name}}[1]\n"
        );
        GRASSprint(
            "        Notes            = @{$GRASS::transfer_addinfo{$transfer_name}}[2]\n"
        );
        GRASSprint("\n");
    }

    GRASSprint(
        "\n----------------------------------------------------------------\n");

    GRASSprint("\nFIELD INFO\n");
    GRASSprint("\n  KEYS\n");
    foreach $field (@GRASS::keys) {
        GRASSprint("\n    $field\n");
        GRASSprint("      consolidate\n");
        GRASSprint(
            "        primary   = $GRASS::primary_consolidation_types{$field}\n"
        );
        GRASSprint(
            "        secondary = $GRASS::secondary_consolidation_types{$field}\n"
        );
        GRASSprint("      format      = $GRASS::format_types{$field}\n");
        GRASSprint("      precision   = $GRASS::format_precision{$field}\n");
        GRASSprint("\n");
    }

    GRASSprint("\n  MATCHES\n");
    foreach $field (@GRASS::matches) {
        GRASSprint("\n    $field\n");
        GRASSprint("      consolidate\n");
        GRASSprint(
            "        primary   = $GRASS::primary_consolidation_types{$field}\n"
        );
        GRASSprint(
            "        secondary = $GRASS::secondary_consolidation_types{$field}\n"
        );
        GRASSprint("      tolerance   = $GRASS::tolerance_values{$field}\n");
        GRASSprint("      format      = $GRASS::format_types{$field}\n");
        GRASSprint("      precision   = $GRASS::format_precision{$field}\n");
        GRASSprint("\n");
    }

    GRASSprint("\n  INFO\n");
    foreach $field (@GRASS::primary_info) {
        GRASSprint("\n    $field\n");
        GRASSprint("      side        = primary\n");
        GRASSprint(
            "      consolidate = $GRASS::primary_consolidation_types{$field}\n"
        );
        GRASSprint("      format      = $GRASS::format_types{$field}\n");
        GRASSprint("      precision   = $GRASS::format_precision{$field}\n");
        GRASSprint("\n");
    }
    foreach $field (@GRASS::secondary_info) {
        GRASSprint("\n    $field\n");
        GRASSprint("      side        = secondary\n");
        GRASSprint(
            "      consolidate = $GRASS::secondary_consolidation_types{$field}\n"
        );
        GRASSprint("      format      = $GRASS::format_types{$field}\n");
        GRASSprint("      precision   = $GRASS::format_precision{$field}\n");
        GRASSprint("\n");
    }

    GRASSprint(
        "\n----------------------------------------------------------------\n");
}

=head2 B<gen_transfer_info>

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

sub gen_transfer_info {

    my $info_file = "$GRASS::BA_HOME/conf/transfer_info.html";

    open( INFO, ">$info_file" )
      || die("ERROR: Generate Transfer Info function failed to open info file $info_file: $!\n"
      );

    foreach $transfer_name (@GRASS::transfer_names) {

        print INFO "<tr>\n";
        print INFO "  <td class=\"Cell\">$transfer_name</td>\n";
        print INFO "  <td class=\"Cell\">@{$GRASS::transfer_addinfo{$transfer_name}}[0]</td>\n";
        print INFO "  <td class=\"Cell\">@{$GRASS::transfer_source{$transfer_name}}[0]</td>\n";
        print INFO "  <td class=\"Cell\">@{$GRASS::transfer_source{$transfer_name}}[3]</td>\n";
        print INFO "  <td class=\"Cell\">@{$GRASS::transfer_source{$transfer_name}}[4]</td>\n";
        print INFO "  <td class=\"Cell\">@{$GRASS::transfer_source{$transfer_name}}[5]</td>\n";
        print INFO "  <td class=\"Cell\"><a href=\"mailto:@{$GRASS::transfer_contact{$transfer_name}}[2]?subject=GRASS Query for $GRASS::business_name - File name: @{$GRASS::transfer_source{$transfer_name}}[5]\">@{$GRASS::transfer_contact{$transfer_name}}[0]</a></td>\n";
        print INFO "  <td class=\"Cell\"><a href=\"mailto:@{$GRASS::transfer_cover{$transfer_name}}[2]?subject=GRASS Query for $GRASS::business_name - File name: @{$GRASS::transfer_source{$transfer_name}}[5]\">@{$GRASS::transfer_cover{$transfer_name}}[0]</a></td>\n";
        print INFO "<tr>\n";

    }

    close(INFO);
}

=head2 B<gen_management_info>

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

sub gen_management_info {

    my $info_file = "$GRASS::BA_HOME/conf/management_info.html";

    open( INFO, ">$info_file" )
      || die("ERROR: Generate Transfer Info function failed to open info file $info_file: $!\n"
      );

    print INFO "<tr>\n";
    print INFO "  <td width=\"150\" class=\"BlueSubHeader\">Cost Centre</td>\n";
    print INFO "  <td class=\"Text\">$GRASS::cost_centre</td>\n";
    print INFO "  <td width=\"150\" class=\"BlueSubHeader\">Cost Band</td>\n";
    print INFO "  <td class=\"Text\">$GRASS::cost_band</td>\n";
    print INFO
      "  <td width=\"150\" class=\"BlueSubHeader\">Cost Signatory</td>\n";
    print INFO "  <td class=\"Text\">$GRASS::cost_signatory</td>\n";
    print INFO "</tr>\n";
    print INFO "<tr>\n";
    print INFO
      "  <td width=\"150\" class=\"BlueSubHeader\">Deployment Date</td>\n";
    print INFO "  <td class=\"Text\">$GRASS::deployment_date</td>\n";
    print INFO
      "  <td width=\"150\" class=\"BlueSubHeader\">Cost Justification</td>\n";
    print INFO
      "  <td class=\"Text\" colspan=\"3\">$GRASS::cost_justification</td>\n";
    print INFO "</tr>\n";
    print INFO "<tr>\n";
    print INFO
      "  <td width=\"150\" class=\"BlueSubHeader\">Deployment Sponsor</td>\n";
    print INFO "  <td class=\"Text\">$GRASS::deployment_sponsor</td>\n";
    print INFO
      "  <td width=\"150\" class=\"BlueSubHeader\">Deployment Reason</td>\n";
    print INFO
      "  <td class=\"Text\" colspan=\"3\">$GRASS::deployment_reason</td>\n";
    print INFO "</tr>\n";
    print INFO "<tr>\n";
    print INFO
      "  <td width=\"150\" class=\"BlueSubHeader\">Access Authority</td>\n";
    print INFO "  <td class=\"Text\">$GRASS::access_authority</td>\n";
    print INFO
      "  <td width=\"150\" class=\"BlueSubHeader\">Backup Authority</td>\n";
    print INFO "  <td class=\"Text\">$GRASS::backup_authority</td>\n";
    print INFO "  <td width=\"150\" class=\"Text\"></td>\n";
    print INFO "  <td class=\"Text\"></td>\n";
    print INFO "</tr>\n";

    close(INFO);
}

=head2 B<gen_xxxxx>

A bunch of functions for getting global values.

=over

=item get_GRASS_HOME

=item get_core_debug

=item get_tracker_debug

=item get_business_name

=item get_display_name

=item get_sector_name

=item get_category_name

=item get_primary

=item get_secondary

=item get_tracker_type

=item get_vaulted

=item get_expected_completion

=item get_expiry_count

=item get_run_offset

=item get_max_match_ref

=item get_max_break_ref

=item get_max_log

=item get_transfer_names

=item get_transfer_source

=item get_transfer_script

=item get_one_sided_tolerance

=item get_match_duplicates

=item get_no_key

=item get_manipulation_scripts

=item get_primary_consolidation_types

=item get_secondary_consolidation_types

=item get_tolerance_values

=item get_format_types

=item get_format_precision

=item get_keys

=item get_matches

=item get_infos

=item get_primary_info

=item get_secondary_info

=item get_consolidate

=back

=cut

sub get_GRASS_HOME {
    return $GRASS::GRASS_HOME;
}

sub get_global_email_ref {
    return $GRASS::global_email_ref;
}

sub get_global_email_support {
    return $GRASS::global_email_support;
}

sub get_global_email_deploy {
    return $GRASS::global_email_deploy;
}

sub get_core_debug {
    return $GRASS::core_debug;
}

sub get_tracker_debug {
    return $GRASS::tracker_debug;
}

sub get_business_name {
    return $GRASS::business_name;
}

sub get_display_name {
    return $GRASS::display_name;
}

sub get_sector_name {
    return $GRASS::sector_name;
}

sub get_category_name {
    return $GRASS::category_name;
}

sub get_primary {
    return $GRASS::primary;
}

sub get_secondary {
    return $GRASS::secondary;
}

sub get_tracker_type {
    return $GRASS::tracker_type;
}

sub get_vaulted {
    return $GRASS::vaulted;
}

sub get_expected_completion {
    return $GRASS::expected_completion;
}

sub get_expiry_count {
    return $GRASS::expiry_count;
}

sub get_run_offset {
    return $GRASS::run_offset;
}

sub get_max_match_ref {
    return $GRASS::max_match_ref;
}

sub get_max_break_ref {
    return $GRASS::max_break_ref;
}

sub get_max_log {
    return $GRASS::max_log;
}

sub get_transfer_names {
    return @GRASS::transfer_names;
}

sub get_transfer_source {
    return %GRASS::transfer_source;
}

sub get_transfer_script {
    return %GRASS::transfer_script;
}

sub get_one_sided_tolerance {
    return $GRASS::one_sided_tolerance;
}

sub get_match_duplicates {
    return $GRASS::match_duplicates;
}

sub get_no_key {
    return $GRASS::no_key;
}

sub get_manipulation_scripts {
    return @GRASS::manipulation_scripts;
}

sub get_primary_consolidation_types {
    return %GRASS::primary_consolidation_types;
}

sub get_secondary_consolidation_types {
    return %GRASS::secondary_consolidation_types;
}

sub get_tolerance_values {
    return %GRASS::tolerance_values;
}

sub get_format_types {
    return %GRASS::format_types;
}

sub get_format_precision {
    return %GRASS::format_precision;
}

sub get_keys {
    return @GRASS::keys;
}

sub get_matches {
    return @GRASS::matches;
}

sub get_infos {
    return @GRASS::infos;
}

sub get_primary_info {
    return @GRASS::primary_info;
}

sub get_secondary_info {
    return @GRASS::secondary_info;
}

sub get_consolidate {
    return $GRASS::consolidate;
}


=head2 B<test_parameters>

NOT implemented at the moment!!!

the idea was to check that everything is setup correctly

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


sub test_parameters {

 # if consolidation on
 # fill in all blank types with non-tolerate
 # check if valid type
 #  keys = non_tolerate/aggregate (not conatenate or tolerate)
 # matches = non_tolerate/aggregate/concatinate_unique (not verbose or tolerate)
 # info= any
 # if no key some fields must be non_tolerate

}


1;
