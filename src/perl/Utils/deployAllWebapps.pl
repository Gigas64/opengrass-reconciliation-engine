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

#
# 
#
#########################################################
$GRASS_HOME = $ENV{GRASS_HOME};

@errors = ();

# Deploy each of the business areas
opendir( DIR, "$GRASS_HOME/ertba" );
while ( defined( $bn = readdir DIR ) ) {
    next if $bn =~ /^(\.\.?)|(index)|(CoreManager)$/;    # skip '.' and '..'
    next
      if ( !-e "$GRASS_HOME/ertba/$bn/conf/$bn.gprops" )
      ;    # make sure there are no stray files

    print "\n\n####\n####  Deploying Webapp for '$bn' ####\n####\n";

    # make sure we are in GRASS_HOME directory
    chdir("$GRASS_HOME") || die "cannot cd to $GRASS_HOME ($!)";

    # now deploy the business area
    system("perl $GRASS_HOME/build.pl -t=deploy-webapp -bn=$bn")
      && push( @errors,
        "ERROR: Business Area $bn has not been deployed properly: $!\n" );

}

close(DIR);

print "\n\n####\n####  Deploying Webapp for 'ROOT' ####\n####\n";

# make sure we are in GRASS_HOME directory
chdir("$GRASS_HOME") || die "cannot cd to $GRASS_HOME ($!)";

# now deploy the root
system("perl $GRASS_HOME/build.pl -t=deploy-root")
  && die "ERROR: Can't execute [perl $GRASS_HOME/build.pl -t=deploy-root] $!\n";

print "\nRun Report:\n";
if ( @errors == 0 ) {
    print "All Business Areas were successfully deployed\n\n";
}
else { print "@errors\n"; }

exit;
