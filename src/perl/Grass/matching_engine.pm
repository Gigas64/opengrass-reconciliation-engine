package Grass::matching_engine;

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


Grass::new_matching.pm - Module to match the data


=head1 B<DESCRIPTION>

Gets data using primary and secondary xml files. Matches data and prints breaks.

=head1 B<GENERAL FLOW>

The matching engine generates XML files under
ertba/<business_name>/data/3_matched. It was planned to remove the code for the XML files.
At the moment there is the advantage that the xml files are easier to read in the case of occuring problems.



=head2 Matching SystemA against SystemB

=head3 Match Status




=for comment
    Debug Levels checked!


=cut


use Exporter;
use lib "$ENV{GRASS_HOME}/src/perl";

use Getopt::Long;

use Time::localtime;
use locale;

use GRASS;

@ISA    = ('Exporter');
@EXPORT = qw(ident_matching_engine do_matching);




#######################################################################################################################

my $mod = "matching_engine";
my $module="   Module : matching_engine.pm";
my $version="   Version: 1.00.001";
my $date="   Date:    29.08.2005";

sub ident_matching_engine {

    if ( $GRASS::DEBUG > 2 ) {
        ### GRASSprint is only possible after initialisation
        print_version ($module, $version, $date);

    }

}
#######################################################################################################################

=head1 B<MAIN FUNCTION>

=head2 B<do_matching>

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

sub do_matching {

    ident_matching_engine();


    %match_values = ();
    $sep          = "\x1E";

##############################################################################
    # SET FILES AND DIRECTORIES

    # data directories
    $data_dir  = "${GRASS::GRASS_HOME}/ertba/$GRASS::business_name/data";
    $src_dir   = "$data_dir/2_normalised";
    $dest_dir1 = "$data_dir/3_matched";
    $dest_dir2 = "$data_dir/4_tracked";

    $match_file     = "$dest_dir1/${GRASS::business_name}_matches.xml";
    $breaks_file    = "$dest_dir1/${GRASS::business_name}_breaks.xml";
    $totals_file    = "$dest_dir1/${GRASS::business_name}_totals.xml";
    $np_pri_file    = "$dest_dir1/${GRASS::business_name}_np_pri.xml";
    $np_sec_file    = "$dest_dir1/${GRASS::business_name}_np_sec.xml";


    if ( !-e $dest_dir1 ) {
        if ( $GRASS::DEBUG > 3 ) {
            GRASSprint("DEBUG: Creating Directory $dest_dir1\n");
        }
        mkdir( $dest_dir1, 0755 )
          or GRASSdie( "ERROR: Couldn't mkdir $dest_dir1: $!", $mod );
    }
    if ( !-e $dest_dir2 ) {
        if ( $GRASS::DEBUG > 3 ) {
            GRASSprint("DEBUG: Creating Directory $dest_dir2\n");
        }
        mkdir( $dest_dir2, 0755 )
          or GRASSdie( "ERROR: Couldn't mkdir $dest_dir2: $!", $mod );
    }

    $GRASS::primary_src   = "$src_dir/$GRASS::primary.xml";
    $GRASS::secondary_src = "$src_dir/$GRASS::secondary.xml";

    $GRASS::primary_src_crc   = 0;
    $GRASS::secondary_src_crc = 0;

###############################################################################
    # INITIALISE VARIABLES

    # remember - gprops must somehow indicate ordering of keys !!!!!!
    # what about duplicate breaks - do we apply tolerance?
    if ( $GRASS::DEBUG > 3 ) { GRASSprint("INFO: Initialising variables\n") }

    %field_positions = ();

    %key_positions   = ();
    $key_count       = 0;
    $field_count     = 0;
    $pexcluded_count = 0;
    $sexcluded_count = 0;

    # totals stuff
    $total_break_count   = $match_count       = $p_count    = $s_count         =
      $p_processed_count = $s_processed_count = $true_count = $pos_count       =
      $sos_count         = $pdup_count        = $sdup_count = $pexcluded_count =
      $sexcluded_count = $bexcluded_count = $forced_count = 0;
      $np_pri_count = $np_sec_count = 0;
    $true_break_count  = ();

    %default_breaks  = ();
    %default_matches = ();
    @dummy_record    = ();

    # for XML data
    foreach $field ( @GRASS::keys, @GRASS::matches, @GRASS::primary_info,
        @GRASS::secondary_info )
    {

        if (! exists $field_positions{$field}) {
            $field_positions{$field} = $field_count;
        }
        $dummy_record[$field_count] = '';
        $field_count++;
    }


    if ($GRASS::no_key) {
        foreach $field (@GRASS::matches) {
            $key_positions{$field} = $key_count;
        }
    }
    else {
        foreach $field (@GRASS::keys) {
            $key_positions{$field} = $key_count;
            $key_count++;
        }
    }

    foreach $field (@GRASS::matches) {
        $default_breaks{$field}   = 'Break';
        $default_matches{$field}  = 'Match';
        $true_break_count{$field} = 0;
    }

    # for CSV data
    %p_field_positions = ();
    %s_field_positions = ();
    $field_count       = 0;

    foreach $field ( @GRASS::keys, @GRASS::matches ) {

        $p_field_positions{$field} = $s_field_positions{$field} = $field_count;
        $dummy_record[$field_count] = '';
        $field_count++;
    }
    $help = $field_count;

    foreach $field (@GRASS::primary_info) {

        $p_field_positions{$field} = $field_count;
        $dummy_record[$field_count] = '';
        $field_count++;
    }

    foreach $field (@GRASS::secondary_info) {

        $s_field_positions{$field} = $help;
        $dummy_record[$field_count] = '';
        $field_count++;
        $help++;
    }

    # Read first_seen into first_seen hash
    # for the XML Output only!
    if ( $GRASS::DEBUG > 2 ) { GRASSprint("INFO: GETTING FIRST SEEN DATES\n") }
    $dir = "$GRASS::GRASS_HOME/ertba/$GRASS::business_name";


###############################################################################
    # Get a timestamp
    $tm = localtime;
    ( $day, $month, $year ) = ( $tm->mday, $tm->mon, $tm->year );
    $display_date = sprintf( "%02d/%02d/%04d", $day, $month + 1, $year + 1900 );

##############################################################################
#    # load trackerstore
#    %h_forced_tracker = read_in_trackerfile();

##############################################################################
    # READ DATA AND FIRST SEEN DATES INTO HASHES -  COMPOSITE KEY = HASH KEY

    if ( $GRASS::DEBUG > 3 ) { GRASSprint("INFO: Read Data\n") }

    %p_hash = fill_hash($GRASS::primary_src);
    %s_hash = fill_hash($GRASS::secondary_src);

    while ( ( $k, $recs ) = each %p_hash ) {
        $all_keys{$k} += scalar @{$recs};
        $p_processed_count++;
    }
    while ( ( $k, $recs ) = each %s_hash ) {
        $all_keys{$k} += scalar @{$recs};
        $s_processed_count++;
    }

    # Open and set up files
    open( MATCH, ">$match_file" )
      || GRASSdie( "ERROR: Can't open XML Matches file [$match_file]: $!\n",
        $mod );
    open( BREAK, ">$breaks_file" )
      || GRASSdie( "ERROR: Can't open XML Breaks file [$breaks_file]: $!\n",
        $mod );
    open( TOTAL, ">$totals_file" )
      || GRASSdie( "ERROR: Can't open XML Totals file [$totals_file]: $!\n",
        $mod );

    open( NP_PRI, ">$np_pri_file" )
      || GRASSdie( "ERROR: Can't open XML not processed primary file [$np_pri_file]: $!\n",
        $mod );
    open( NP_SEC, ">$np_sec_file" )
      || GRASSdie( "ERROR: Can't open XML not processed secondary file [$np_sec_file]: $!\n",
        $mod );



        # print headers in files
    print TOTAL "<?xml version='1.0' encoding='ISO-8859-1'?>\n";
    print TOTAL
      "<totals business=\"$GRASS::business_name\" run_date=\"$display_date\" ";
    print TOTAL
"primary=\"$GRASS::primary\" secondary=\"$GRASS::secondary\" content=\"totals\">\n";
    print BREAK "<?xml version='1.0' encoding='ISO-8859-1'?>\n";
    print BREAK
      "<breaks business=\"$GRASS::business_name\" run_date=\"$display_date\" ";
    print BREAK
"primary=\"$GRASS::primary\" secondary=\"$GRASS::secondary\" content=\"breaks\">\n
";
    print MATCH "<?xml version='1.0' encoding='ISO-8859-1'?>\n";
    print MATCH
      "<matches business=\"$GRASS::business_name\" run_date=\"$display_date\" ";
    print MATCH
"primary=\"$GRASS::primary\" secondary=\"$GRASS::secondary\" content=\"matches\">\n
";

    print NP_PRI "<?xml version='1.0' encoding='ISO-8859-1'?>\n";
    print NP_PRI
      "<notprocessed_pri business=\"$GRASS::business_name\" run_date=\"$display_date\" ";
    print NP_PRI
"primary=\"$GRASS::primary\"  content=\"NotProcessed\">\n
";


    print NP_SEC "<?xml version='1.0' encoding='ISO-8859-1'?>\n";
    print NP_SEC
      "<notprocessed_sec business=\"$GRASS::business_name\" run_date=\"$display_date\" ";
    print NP_SEC
"secondary=\"$GRASS::secondary\" content=\"NotProcessed\">\n
";


##############################################################################
    # Match

    if ( $GRASS::DEBUG > 2 ) { GRASSprint("INFO: Start Matching process\n") }

    single_key_match();

    not_processed();

    print_totals();

    print MATCH "</matches>\n";
    print BREAK "</breaks>\n";
    print TOTAL "</totals>\n";
    print NP_PRI "</notprocessed_pri>\n";
    print NP_SEC "</notprocessed_sec>\n";


    close(MATCH);
    close(TOTAL);
    close(BREAK);
    close(NP_PRI);
    close(NP_SEC);


    if ( $GRASS::DEBUG > 2 ) { GRASSprint("INFO: End Matching process\n") }

    if ( $GRASS::DEBUG >= 0 ) {
        GRASSprint("\n");
        GRASSprint("TOTALS\n");
        GRASSprint("===========================\n");
        GRASSprint("Total Breaks\t\t$total_break_count\n");
        GRASSprint("Total Matches\t\t$match_count\n");

        GRASSprint("$GRASS::primary not processed \t$np_pri_count\n");
        GRASSprint("$GRASS::secondary not processed \t$np_sec_count\n");

        #GRASSprint("Break Exclusions \t$bexcluded_count\n");
        #GRASSprint("$GRASS::primary Exclusions \t$pexcluded_count\n");
        #GRASSprint("$GRASS::secondary Exclusions \t$sexcluded_count\n");
        GRASSprint("$GRASS::primary \t\t\t$p_processed_count\n");
        GRASSprint("$GRASS::secondary\t \t\t$s_processed_count\n\n");
        GRASSprint("True Matches \t\t$match_count\n");
        GRASSprint("P/S Breaks \t\t$true_count\n");
        #GRASSprint("$GRASS::primary One Sideds \t$pos_count\n");
        #GRASSprint("$GRASS::secondary One Sideds \t$sos_count\n");
        #GRASSprint("$GRASS::primary Duplicate \t$pdup_count\n");
        #GRASSprint("$GRASS::secondary Duplicate \t$sdup_count\n\n");

        foreach $m (@GRASS::matches) {
            GRASSprint("$m\t\t\t$true_break_count{$m}\n");
        }
    }

    GRASSprint("INFO: matching_engine complete\n");

}

=head2 B<single_key_match>

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

sub single_key_match {
    my $test = 0;

    start_message();

    while ( ( $key, $no_recs ) = each %all_keys ) {

        if ( $no_recs == 2 && defined $p_hash{$key} && defined $s_hash{$key} ) {
            if ( $GRASS::DEBUG > 3 ) {
                GRASSprint("INFO: SINGLE KEY MATCHING #$key#\n");
            }

            %breaks       = get_breaks(${ $p_hash{$key} }[0],${ $s_hash{$key} }[0],$key,$key );

            #$test = change_forceds_back($key);
            if ( $test == 1 ) {
                if ( $GRASS::DEBUG > 4 ) {
                    print "DEBUG: This was a forced! $key\n";
                }

                my $MchecksumA_old;
                my $MchecksumB_old;
                my $MchecksumA;
                my $MchecksumB;

                # the trade input data are identical
                my @key_values = ();
                my $side_keyB  = "";
                ( $side_keyB, @key_values ) = @{ $h_forced{$key} };
                $match_values{"$key$side_keyB"} = $match_values{"$key$key"};

                if ( $match_values{"$key$key"} > 0 ) {

                    $breaks{'_Overall_Grass_Match_Value_'} = "ForcedBreak";
                    if ( $GRASS::DEBUG > 3 ) {
                        GRASSprint(
"INFO: FORCED MATCHING $key $side_keyB - its a break\n"
                        );
                    }
                    $total_break_count++;

                    print_xml_result(
                        $key,
                        $breaks{'_Overall_Grass_Match_Value_'},
                        ${ $p_hash{$key} }[0],
                        ${ $s_hash{$side_keyB} }[0], \%breaks
                    );


                }
                else {
                    $breaks{'_Overall_Grass_Match_Value_'} = "ForcedMatch";
                    if ( $GRASS::DEBUG > 3 ) {
                        GRASSprint(
"INFO: FORCED MATCHING $key $side_keyB - its a match\n"
                        );
                    }
                    $match_count++;
                    print_xml_result(
                        $key,
                        $breaks{'_Overall_Grass_Match_Value_'},
                        ${ $p_hash{$key} }[0],
                        ${ $s_hash{$side_keyB} }[0], \%breaks
                    );


                }

                delete $all_keys{$key};
                delete $p_hash{$key};
                delete $s_hash{$side_keyB};

                $test = 0;
            }
            else {
                print_xml_result(
                    $key,
                    $breaks{'_Overall_Grass_Match_Value_'},
                    ${ $p_hash{$key} }[0],
                    ${ $s_hash{$key} }[0], \%breaks
                );

                if ( $GRASS::DEBUG > 3 && $match_values{"$key$key"} > 0 ) {
                    GRASSprint(
                        "INFO: SINGLE KEY MATCHING $key - its a break\n");
                }


                delete $all_keys{$key};
                delete $p_hash{$key};
                delete $s_hash{$key};

                if ( $breaks{'_Overall_Grass_Match_Value_'} eq "Match" ) {
                    $match_count++;
                }
                else {
                    $true_count++;
                    $total_break_count++;
                    increment_field_breaks( \%breaks );
                }
            }
        }
    }
    complete_message();
}


=head2 B<remainder_break>

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

sub not_processed {

    start_message();

    while ( ( $key, $no_recs ) = each %all_keys ) {
        foreach $p_array ( @{ $p_hash{$key} } ) {
            if ( $GRASS::DEBUG > 3 ) {
                GRASSprint("INFO: NotProcessed PrimarySide $key\n");
            }
            print_xml_result( $key, "NotProcessed Primary", $p_array, \@dummy_record,
                \%default_breaks );

            #$pos_count++;
            $np_pri_count++;
        }
        foreach $s_array ( @{ $s_hash{$key} } ) {
            if ( $GRASS::DEBUG > 3 ) {
                GRASSprint("INFO: NotProcessed SecondarySide $key\n");
            }
            print_xml_result( $key, "NotProcessed Secondary", \@dummy_record,
                $s_array, \%default_breaks );

            #$sos_count++;
            $np_sec_count++;
        }
        delete $all_keys{$key};
        delete $p_hash{$key};
        delete $s_hash{$key};
    }
    complete_message();
}


###

=head2 B<fill_hash>

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

sub fill_hash {

    my $file        = $_[0];
    my %hash        = ();
    my $root        = 'reco';
    my $whole_reco  = '';
    my $inside_reco = 0;
    my $next_reco   = '';

    start_message();

    if ( $GRASS::DEBUG > 2 ) { GRASSprint("INFO: Getting data from $file\n") }

    open( XML, "<$file" )
      || GRASSdie( "ERROR: failed to open XML file $file: $!\n", $mod );

    while (<XML>) {
        chomp $_;

        if ( $_ =~ /^\<$root\>/ && !$inside_reco ) {
            $inside_reco = 1;
            $whole_reco  = $_;
        }
        elsif ( $_ !~ /\<\/$root\>/ && $inside_reco ) {
            $whole_reco .= $_;
        }
        elsif ($inside_reco) {
            $inside_reco = 0;
            $whole_reco .= $_;
            push(
                @{ $hash{ get_key_from_xml($whole_reco) } },
                get_fields_from_xml($whole_reco)
            );
        }

    }

    complete_message();

    return %hash;
}

=head2 B<get_fields_from_xml>

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

sub get_fields_from_xml {
    my $xml_line     = $_[0];
    my @field_values = ();

    while ( ( $field, $pos ) = each %field_positions ) {
        if ( $xml_line =~ /\<$field\>(.*)\<\/$field\>/ ) {
            $field_values[$pos] = $1;
        }
        else { $field_values[$pos] = '' }
        if ( $GRASS::DEBUG > 4 ) {
            GRASSprint("DEBUG:      pos  = $pos\n");
            GRASSprint("DEBUG:      $field  = [$field_values[$pos]]\n");
        }
        if ( $GRASS::format_types{$field} eq 'numeric'
            && !isa_number( $field_values[$pos] ) )
        {
            GRASSprint(
                "WARNING: $field value [$field_values[$pos]] is not a number\n");
        }
    }

    return \@field_values;
}

=head2 B<get_key_from_xml>

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

sub get_key_from_xml {
    my $xml_line      = $_[0];
    my @key_values    = ();
    my $composite_key = '';

    while ( ( $field, $pos ) = each %key_positions ) {

        if ( $xml_line =~ /\<$field\>(.*)\<\/$field\>/ ) {

            $key_values[$pos] = $1;
        }
        else { $key_values[$pos] = '' }
    }

    # create key

    foreach $k (@key_values) {
        $composite_key .= "$k,";
    }
    chop($composite_key);
    if ( $GRASS::DEBUG > 4 ) {
        GRASSprint("DEBUG: Composite Key = [$composite_key]\n");
    }

    return $composite_key;
}

=head2 B<os_tol>

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

sub os_tol {
    my @array  = @{ $_[0] };
    my $result = 'Match';

    start_message();

    while ( ( $name, $tol ) = each %GRASS::tolerance_values ) {
        my $val = $array[ $field_positions{$name} ];
        if ( !isa_number($val) ) { $result = 'Break' }
        elsif ( ( $val >= 0 ? $val : $val * -1 ) > $tol ) { $result = 'Break' }
        if ( $GRASS::DEBUG > 3 ) {
            GRASSprint("DEBUG: $name [$val] == 0: +/- $tol (tol) $result\n");
        }
    }
    complete_message();
    return $result;
}

=head2 B<get_breaks>

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

sub get_breaks {
    my @p_array       = @{ $_[0] };
    my @s_array       = @{ $_[1] };
    my $keyA          = $_[2];
    my $keyB          = $_[3];
    my %match_results = ();
    my $matchstatus   = 0;

    start_message();

    $match_results{'_Overall_Grass_Match_Value_'} = 'Match';
    my $match_index = 0;
    foreach $field (@GRASS::matches) {
        my $index   = $field_positions{$field};
        my $p_value = $p_array[$index];
        my $s_value = $s_array[$index];
        my $test    = 0;

        if ( $GRASS::format_types{$field} eq 'numeric' ) {

            if ( isa_number($p_value) && isa_number($s_value) ) {

                if ( defined $GRASS::tolerance_values{$field}
                    && $GRASS::tolerance_values{$field} ne "" )
                {
                    if ( $GRASS::DEBUG > 3 ) {
                        GRASSprint(
"DEBUG: \t$field [$p_value] <=> [$s_value]: Tolerance check\n"
                        );
                    }
                    %match_results = check_tolerance_breaks(
                        $p_value, $s_value,      \%match_results,
                        $keyA,    $keyB,         $match_index,
                        $field,   \$matchstatus, \%match_values
                    );
#SD 05/11/2004#
                    $match_values{"$keyA$keyB"} = $matchstatus;
                }
                elsif ( $p_value == $s_value ) {
                    $match_results{$field} = 'Match';

                    $matchstatus &= ~( 2**$match_index );
                    $match_values{"$keyA$keyB"} = $matchstatus;
                    if ( $GRASS::DEBUG > 3 ) {
                        GRASSprint(
                            "DEBUG: \t$field [$p_value] == [$s_value]: NumericMatch\n"
                        );
                    }
                }
                else {

                    $match_results{'_Overall_Grass_Match_Value_'} = 'Break';
                    $match_results{$field} = 'Break';

                    $matchstatus |= 2**$match_index;
                    $match_values{"$keyA$keyB"} = $matchstatus;
                    if ( $GRASS::DEBUG > 3 ) {
                        GRASSprint(
                            "DEBUG: \t$field [$p_value] != [$s_value]: Numeric Break\n"
                        );
                    }
                }

            }
            else {
                GRASSprint(
"WARNING: Non-numeric value found when matching on match field [$field]. [$p_value] != [$s_value] Break\n"
                );
                $match_results{'_Overall_Grass_Match_Value_'} = 'Break';
                $match_results{$field} = 'Non-NumericBreak';

                $matchstatus |= 2**$match_index;
                $match_values{"$keyA$keyB"} = $matchstatus;
            }
        }
        elsif ($GRASS::format_types{$field} eq 'ignore_case'
            || $GRASS::format_types{$field} eq 'text'
            || $GRASS::format_types{$field} eq 'default'
            || $GRASS::format_types{$field} eq "" )
        {

            if ( $GRASS::format_types{$field} eq 'ignore_case' ) {
                $p_value = lc($p_value);
                $s_value = lc($s_value);
                if ( $GRASS::DEBUG > 3 ) {
                    GRASSprint(
"DEBUG: \t$field [$p_value] <=> [$s_value]: converted to lowercase\n"
                    );
                }
            }
            if ( $p_value eq $s_value ) {
                $match_results{$field} = 'Match';

                $matchstatus &= ~( 2**$match_index );
                $match_values{"$keyA$keyB"} = $matchstatus;
                if ( $GRASS::DEBUG > 3 ) {
                    GRASSprint("DEBUG: \t$field [$p_value] == [$s_value]: Match\n");
                }
            }
            else {

                $match_results{'_Overall_Grass_Match_Value_'} = 'Break';
                $match_results{$field} = 'Break';

                $matchstatus |= 2**$match_index;
                $match_values{"$keyA$keyB"} = $matchstatus;
                if ( $GRASS::DEBUG > 3 ) {
                    GRASSprint("DEBUG: \t$field [$p_value] != [$s_value]: Break\n");
                }
            }
        }
        else {

            $match_results{'_Overall_Grass_Match_Value_'} = 'Break';
            $match_results{$field} = 'Break';

            $matchstatus |= 2**$match_index;
            $match_values{"$keyA$keyB"} = $matchstatus;
            if ( $GRASS::DEBUG > 3 ) {
                GRASSprint(
"DEBUG: \t$field [$p_value] != [$s_value]: Break (no text and no number)\n"
                );
            }
        }
        if ( $GRASS::DEBUG > 4 ) {
            GRASSprint("DEBUG: Field: $field - MatchStatus: $matchstatus\n");
        }
        $match_index++;
    }

    complete_message();

    return %match_results;
}

=head2 B<check_tolerance_breaks>

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

# Matching with tolerances
sub check_tolerance_breaks {
    my $p_value        = $_[0];
    my $s_value        = $_[1];
    my %b_hash         = %{ $_[2] };
    my $keyA           = $_[3];
    my $keyB           = $_[4];
    my $match_index    = $_[5];
    my $field          = $_[6];
    my $matchstatus    = $_[7];
    my $r_match_values = $_[8];
    my %match_values   = %$r_match_values;

    start_message();

    $b_hash{$field} = 'Break';

    if ( defined $GRASS::tolerance_values{$field} ) {
        if ( $GRASS::DEBUG > 3 ) {
            GRASSprint(
"DEBUG: \t$field [$p_value] <=> [$s_value]: +/- $GRASS::tolerance_values{$field} (tol) \n"
            );
        }
        if (   !defined $p_value
            || !defined $s_value
            || !isa_number($p_value)
            || !isa_number($s_value) )
        {
            GRASSprint(
"WARNING: Non-numeric value found when applying tolerance to match field [$field]. [$p_value] != [$s_value] Break\n"
            );
            $b_hash{$field} = 'Break';

            ${$matchstatus} |= 2**$match_index;
#            $match_values{"$keyA$keyB"} = ${$matchstatus};
        }
        elsif (
            ( $p_value > $s_value ? $p_value - $s_value : $s_value - $p_value )
            <= ( $GRASS::tolerance_values{$field} ) )
        {

            if ( $GRASS::DEBUG > 3 ) {
                GRASSprint(
                    "DEBUG: \t$field [$p_value] == [$s_value]: Tolerance Match\n");
            }

            $b_hash{$field} = 'Match';

            ${$matchstatus} &= ~( 2**$match_index );
#            $match_values{"$keyA$keyB"} = ${$matchstatus};
            if ( $GRASS::DEBUG > 4 ) {
                GRASSprint("DEBUG: $keyA, $keyB\n");
                GRASSprint(
"DEBUG: Changed Matchstatus ${$matchstatus} - caused by tolerance\n"
                );
                GRASSprint("DEBUG: $p_value == $s_value - with tolerance\n");
            }
        }
        else {
            if ( $GRASS::DEBUG > 3 ) {
                GRASSprint(
                    "DEBUG: \t$field [$p_value] != [$s_value]: Tolerance Break\n");
            }
            $b_hash{$field} = 'Break';

            ${$matchstatus} |= 2**$match_index;
#            $match_values{"$keyA$keyB"} = ${$matchstatus};
        }

    }
    else {
        if ( $GRASS::DEBUG > 3 ) {
            GRASSprint(
"DEBUG: \t$field [$p_value] != [$s_value]: Tolerance Value not defined - Break\n"
            );
        }
        $b_hash{$field} = 'Break';

        ${$matchstatus} |= 2**$match_index;
#        $match_values{"$keyA$keyB"} = ${$matchstatus};
    }

    if ( $b_hash{$field} eq 'Break' ) {
        $b_hash{'_Overall_Grass_Match_Value_'} = 'Break';
    }

#    $r_match_values = \%match_values;

    complete_message();

    return %b_hash;
}

=head2 B<print_xml_result>

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

sub print_xml_result {
    my $key     = $_[0];
    my $type    = $_[1];
    my @p_array = @{ $_[2] };
    my @s_array = @{ $_[3] };
    my %results = %{ $_[4] };
    my $FILE;
    my $pos;
    my $fs;

    my $key_array = \@p_array;

    start_message();

    if ( $type =~ /Secondary/ ) { $key_array = \@s_array }
    else { $key_array = \@p_array }

    if ( $type =~ 'Match' && $type ne 'ForcedMatch' ) { $FILE = MATCH }
    else { $FILE = BREAK }

    if ( $type =~ 'NotProcessed Primary') { $FILE = NP_PRI }

    if ( $type =~ 'NotProcessed Secondary') { $FILE = NP_SEC }

    if ( exists $first_seen{$key} ) { $fs = $first_seen{$key} }
    else { $fs = $display_date }

    if ( $GRASS::DEBUG > 3 ) {
        GRASSprint("INFO: Record [$key] is a $type : Printing to $FILE file\n");
    }
    print $FILE "<rec key=\"$key\" type=\"$type\" first_seen=\"$fs\">\n";
    print $FILE " <keys>\n";

    foreach $field (@GRASS::keys) {
        $pos = $key_positions{$field};

        if ( $type =~ /Secondary/ ) {
            print $FILE "  <key field=\"$field\">$s_array[$pos]</key>\n";
        }
        else {
            print $FILE "  <key field=\"$field\">$p_array[$pos]</key>\n";
        }
    }
    print $FILE " </keys>\n";

    print $FILE " <matches>\n";
    foreach $field (@GRASS::matches) {
        $pos = $field_positions{$field};
        $dif = " ";
        if (! defined $results{$field}) { $results{$field} = " " }

        if (   $GRASS::format_types{$field} eq 'numeric'
            && isa_number( $p_array[$pos] )
            && isa_number( $s_array[$pos] ) )
        {
            $dif = ( $p_array[$pos] || 0 ) - ( $s_array[$pos] || 0 );
        $dif = print_format($dif,$field);
        }
        print $FILE
"  <match field=\"$field\" status=\"$results{$field}\" difference=\"$dif\">\n";
        print $FILE "   <primary>$p_array[$pos]</primary>\n";
        print $FILE "   <secondary>$s_array[$pos]</secondary>\n";
        print $FILE "  </match>\n";
    }
    print $FILE " </matches>\n";

    print $FILE " <reference>\n";

    foreach $field (@GRASS::primary_info) {
        $pos = $field_positions{$field};
        print $FILE
"  <info field=\"$field\" system=\"$GRASS::primary\" >$p_array[$pos]</info>\n";
    }
    foreach $field (@GRASS::secondary_info) {
        $pos = $field_positions{$field};
        print $FILE
"  <info field=\"$field\" system=\"$GRASS::secondary\" >$s_array[$pos]</info>\n";
    }

    print $FILE " </reference>\n";
    print $FILE "</rec>\n";

    complete_message();

}


=head2 B<print_totals>

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

sub print_totals {

    start_message();

    $total_break_count =
      $total_break_count - $bexcluded_count - $pexcluded_count -
      $sexcluded_count;
    if ( $total_break_count < 0 ) { $total_break_count = 0 }

    print TOTAL
      "<sub_totals type=\"Total Breaks\">$total_break_count</sub_totals>\n";
    print TOTAL
      "<sub_totals type=\"Total Matches\">$match_count</sub_totals>\n";
    print TOTAL
    "<sub_totals type=\"$GRASS::primary not processed\">$np_pri_count</sub_totals>\n";
    print TOTAL
    "<sub_totals type=\"$GRASS::secondary not processed\">$np_sec_count</sub_totals>\n";

#    print TOTAL
#      "<sub_totals type=\"Break Exclusions\">$bexcluded_count</sub_totals>\n";
#    print TOTAL
#"<sub_totals type=\"$GRASS::primary Exclusions\">$pexcluded_count</sub_totals>\n";
#    print TOTAL
#"<sub_totals type=\"$GRASS::secondary Exclusions\">$sexcluded_count</sub_totals>\n";
    print TOTAL
"<sub_totals type=\"$GRASS::primary Processed\">$p_processed_count</sub_totals>\n";
    print TOTAL
"<sub_totals type=\"$GRASS::secondary Processed\">$s_processed_count</sub_totals>\n";
    print TOTAL
"<sub_totals type=\"$GRASS::primary/$GRASS::secondary Breaks\">$true_count</sub_totals>\n";
#print TOTAL
#"<sub_totals type=\"$GRASS::primary One Sided Breaks\">$pos_count</sub_totals>\n";
#    print TOTAL
#"<sub_totals type=\"$GRASS::secondary One Sided Breaks\">$sos_count</sub_totals>\n";
#    print TOTAL
#"<sub_totals type=\"$GRASS::primary Duplicate\">$pdup_count</sub_totals>\n";
#    print TOTAL
#"<sub_totals type=\"$GRASS::secondary Duplicate\">$sdup_count</sub_totals>\n";

    foreach $m (@GRASS::matches) {
        print TOTAL
          "<sub_breaks field=\"$m\">$true_break_count{$m}</sub_breaks>\n";
    }

    complete_message();

}

=head2 B<isa_number>

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

sub isa_number {
    my $potential_number = shift;
    my $isnumber         = 0;

    if ( $potential_number =~
        /^([+-]?)(?=\d|\.\d)\d*(\.\d*)?([Ee]([+-]?\d+))?$/ )
    {
        $isnumber = 1;
    }

    return $isnumber;
}

=head2 B<increment_field_breaks>

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

sub increment_field_breaks {
    my %break_array = %{ $_[0] };

    foreach $f (@GRASS::matches) {
        if ( $break_array{$f} eq 'Break' ) { $true_break_count{$f}++ }
    }

}

=head2 B<setOnes>

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

sub setOnes {
    my $matchstatus;
    my $f;
    my $match_index = 0;

    foreach $f (@GRASS::matches) {

        $matchstatus |= 2**$match_index;
        ++$match_index;
    }

    return $matchstatus;
}

=head2 B<get_record_string>

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

sub get_record_string {
    my ( $system, @x_array ) = @_;
    my $pos;
    my $record_string = ();

    start_message();

    #print ">>>> @x_array\n";
    foreach $field (@GRASS::keys) {
        $pos = $field_positions{$field};

        if ( defined $x_array[$pos] ) {
            $record_string .= " $x_array[$pos]";
        }
    }

    foreach $field (@GRASS::matches) {
        $pos = $field_positions{$field};

        if ( defined $x_array[$pos] ) {
            $record_string .= " $x_array[$pos]";
        }
    }

    if ( $system eq $GRASS::primary ) {
        foreach $field (@GRASS::primary_info) {
            $pos = $field_positions{$field};
            if ( defined $x_array[$pos] ) {
                $record_string .= " $x_array[$pos]";
            }
        }
    }
    elsif ( $system eq $GRASS::secondary ) {    #

        foreach $field (@GRASS::secondary_info) {
            $pos = $field_positions{$field};
            if ( defined $x_array[$pos] ) {
                $record_string .= " $x_array[$pos]";
            }
        }
    }

    if ( $GRASS::DEBUG > 4 ) { GRASSprint "DEBUG: Record-String: $record_string\n"; }

    complete_message();

    return $record_string;
}

=head2 B<get_matchfield_string>

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

sub get_matchfield_string {
    my ( $system, @x_array ) = @_;
    my $pos;
    my $matchfields_string = ();

    start_message();

    #print ">>>> @x_array\n";
    foreach $field (@GRASS::keys) {
        $pos = $field_positions{$field};

        if ( defined $x_array[$pos] ) {
            $matchfields_string .= " $x_array[$pos]";
        }
    }

    # Work with the checksum of only Matchfields - without Key-fields

    foreach $field (@GRASS::matches) {
        $pos = $field_positions{$field};

        if ( defined $x_array[$pos] ) {
            $matchfields_string .= " $x_array[$pos]";
        }
    }

    if ( $GRASS::DEBUG > 4 ) {
        GRASSprint "DEBUG: Match-Field String: $matchfields_string\n";
    }

    complete_message();

    return $matchfields_string;
}


### functions for xml diffs

=head2 B<round>

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

sub round {
  my $prec = $_[0];
  my $value = $_[1];

  if ($value > 0) {  $value = (int($value*10**$prec + 0.5))/10**$prec; }
  elsif ($value < 0) {  $value = (int($value*10**$prec - 0.5))/10**$prec; }

  return $value;
}

=head2 B<print_format>

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

sub print_format {

  my $value = $_[0];
  my $field = $_[1];
  my $prec = 0;

  start_message();

  if ( $GRASS::format_precision{$field} eq 'default') {
      $prec = 2;
  }
  else{
      $prec = $GRASS::format_precision{$field};
  }

  if (isa_number($value)) {
      $value = round($prec,$value);
      if ($prec > 0) {
        # Add a decimal place if none found
      if ($value=~/^-?\d*$/) {   $value.= '.'; }
      # if required number print_format buffer to correct no of zeros
        if ($value=~/^(.*\.)(\d*)$/) {
        # add some zeros
        $value.= '0' x $prec;
        $value=~/^(.*\.)(\d*)$/;
          # cut decimal places to no required
        $dp = substr($2, 0,$prec);
        $value="$1$dp";
        }
      }
  }

  complete_message();

  return $value;
}



