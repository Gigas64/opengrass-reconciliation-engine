#!/opt/DKBperl/bin/perl -w

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


#
# Perl Script to convert from xml to csv
#
################################################

die "Usage: $0 <Source File> <Dest File> <Element Tag>\n" if ( 0 + @ARGV != 3 );
( $infile, $outfile, $tag ) = @ARGV;

$start_tag = 0;
$tag_count = 0;
$line      = "";
$head      = "";

open( IN,  "<$infile" )  || die "Source Failed $!\n";
open( OUT, ">$outfile" ) || die "Dest Failed $!\n";
while (<IN>) {
    chomp($_);
    if ( $_ =~ /^\s*\<$tag\>/ ) {
        $start_tag = 1;
        $tag_count++;
        $line = "";
        $head = "";
    }
    elsif ( $_ =~ /^\s*\<\/$tag\>/ ) {
        $start_tag = 0;
        chop($head);
        chop($line);
        if ( $tag_count == 1 ) {
            print OUT "$head\n";
        }
        print OUT "$line\n";
    }
    elsif ( $start_tag == 1 ) {
        $_ =~ /^\s*\<([^\>]*)\>([^\<]*)\</;
        $head .= "$1,";
        $line .= "$2,";
    }
}

close(IN);
close(OUT);

