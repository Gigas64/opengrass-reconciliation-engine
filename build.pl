#!/usr/bin/perl -w
#
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
#########################################################
#sort out opperating system for classpath
$sep = ':';
$OS='UNIX';
if ($^O eq 'Windows_NT' or $^O eq 'MSWin32' or $^O eq 'dos') { $sep = ';'; $OS = 'WIN' }

use lib "$ENV{GRASS_HOME}/src/perl";

use Getopt::Long;

print("-------------------------------\n");
print("SCRIPT build.pl\n");
print("   Version: 1.00.000\n");
print("   Date:    01.09.2005\n\n");
print("-------------------------------\n");

($JAVA_HOME)=$ENV{JAVA_HOME};
if (length $JAVA_HOME == 0) {
    die "ERROR: JAVA_HOME not found in your environment\n"; }
$JAVA_HOME =~ s/\\/\//g;

($GRASS_HOME)=$ENV{GRASS_HOME};
if (length $GRASS_HOME == 0) {
    die "ERROR: GRASS_HOME not found in your environment\n"; }
$GRASS_HOME =~ s/\\/\//g;

#Add in any Jar files from our lib directory
$class_add="";
opendir(DIR,"${GRASS_HOME}/lib/") or
            die "ERROR: Unable to open ${GRASS_HOME}/lib: $!";
while (defined($file = readdir(DIR))) {
    next if $file =~ /^\.\.?$/;
  $class_add .= "${GRASS_HOME}/lib/${file}$sep";
}

my $b_name = "";
my $target = "";
my $DEBUG = 0;

### Get the command line options ###
$result=GetOptions('business_name=s',\$b_name,'bn=s',\$b_name,
    'd',\$DEBUG, 'debug',\$DEBUG,
    'target=s', \$target, 't=s', \$target);
if(! $result || scalar @ARGV) {
     die "Usage: $0\n  [<-business_name/-bn = Business Name>]\n".
             "  [<-d/-debug>]\n" .
             "  [<-target/-t = Target>]\n";
}

if($target eq "test") {
  $b_name = "TestRec";
}

if ($target eq "deploy") {
    # check the appropriate parameters are set
    if (length $b_name == 0) {
        die "ERROR: -business_name/bn must be set to use target [$target]\n"; }
}

# set business to default to ExampleRec
if (length $b_name == 0 ) { $b_name = "TestRec"; }

$CLASSPATH="$JAVA_HOME/lib/tools.jar${sep}${class_add}$JAVA_HOME/lib/dev.jar";
print "Definitions:\n";
print "  -Dert.business=$b_name\n";
print "  -Dclasspath=$CLASSPATH\n";

($ANT_HOME)=$ENV{ANT_HOME};
if (length $ANT_HOME == 0) {
  system "$JAVA_HOME/bin/java -classpath '$CLASSPATH' -Dclasspath='$CLASSPATH' -Dert.business=$b_name org.apache.tools.ant.Main $target";
}
else {
  if($OS eq "WIN") {
    system "$ANT_HOME/bin/ant -Dert.business=$b_name -Dert.home=$GRASS_HOME $target";
  }
  else {
    system "$ANT_HOME/bin/ant  -Dclasspath '$CLASSPATH' -Dert.business=$b_name -Dert.home=$GRASS_HOME $target";
  }
}

exit;