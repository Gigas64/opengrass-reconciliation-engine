/*
 **
 ** Copyright (C) 2001 - 2006 The Software Conservancy as Trustee. All rights
 ** reserved.
 **
 ** Permission is hereby granted, free of charge, to any person obtaining a
 ** copy of this software and associated documentation files (the
 ** "Software"), to deal in the Software without restriction, including
 ** without limitation the rights to use, copy, modify, merge, publish,
 ** distribute, sublicense, and/or sell copies of the Software, and to
 ** permit persons to whom the Software is furnished to do so, subject to
 ** the following conditions:
 **
 ** The above copyright notice and this permission notice shall be included
 ** in all copies or substantial portions of the Software.
 **
 ** THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 ** OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 ** MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 ** NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 ** LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 ** OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 ** WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **
 ** Nothing in this notice shall be deemed to grant any rights to
 ** trademarks, copyrights, patents, trade secrets or any other intellectual
 ** property of the licensor or any contributor except as expressly stated
 ** herein. No patent license is granted separate from the Software, for
 ** code that you delete from the Software, or for combinations of the
 ** Software with other software or hardware.
 **
 */

/**
 **
 ** @ersion  : 1.00.000
 ** Date     : 26/08/2004
 */

package org.opengrass.openadaptor;

import org.openadaptor.dostrings.ObjectTransformer;

public class ToUpperTransformer implements ObjectTransformer
{
    public void init(java.util.Properties p, String s) {}

    public Object transform(Object o) throws org.openadaptor.dostrings.DOStringException
    {
        // Check the Object is a string before casting
        if (o instanceof String) {
            String my_string=(String)o;
            System.out.println("[DEBUG] woohoo - over here " + my_string + " !");
            return my_string.toUpperCase();
        }
        else {
            throw new org.openadaptor.dostrings.DOStringException("org.openadaptor.dostrings.DOStringException: Object parameter not of type String");
        }
    }

    public static void main(String[] args)
    {
        String sh = new String();
        String v1 = "23,345,000";
        String v2 = "1,450,285.34";
        String v3 = "31M";
        String v4 = "12MM";
        String v5 = "6MMM";
        try{

            ToUpperTransformer vt = new ToUpperTransformer();

            sh = (String)vt.transform((Object)v1);
            System.out.println(sh);
            sh = (String)vt.transform((Object)v2);
            System.out.println(sh);
            sh = (String)vt.transform((Object)v3);
            System.out.println(sh);
            sh = (String)vt.transform((Object)v4);
            System.out.println(sh);
            sh = (String)vt.transform((Object)v5);
            System.out.println(sh);
         }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
