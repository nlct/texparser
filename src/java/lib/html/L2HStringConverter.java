/*
    Copyright (C) 2013 Nicola L.C. Talbot
    www.dickimaw-books.com

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package com.dickimawbooks.texparserlib.html;

import java.io.*;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.aux.AuxData;

public class L2HStringConverter extends L2HConverter
{
   public L2HStringConverter(TeXApp app)
   {
      this(app, null, false);
   }

   public L2HStringConverter(TeXApp app, Vector<AuxData> data)
   {
      this(app, data, false);
   }

   public L2HStringConverter(TeXApp app, Vector<AuxData> data, boolean parsePackages)
   {
      super(app, true, null, data, false, null, parsePackages);

      setWriteable(this);
   }

   public L2HStringConverter()
   {
      this(new TeXAppAdapter());
   }

   public static String convert(TeXApp app, String str, boolean atIsLetter)
    throws IOException
   {
      return (new L2HStringConverter(app)).convert(str, atIsLetter);
   }

   public static String convert(TeXApp app, String str, boolean atIsLetter,
    boolean useMathJax)
    throws IOException
   {
      return (new L2HStringConverter(app)).convert(str, atIsLetter, useMathJax);
   }

   public String convert(String str, boolean atIsLetter)
    throws IOException
   {
      setIsInDocEnv(true);

      StringWriter writer = new StringWriter();
      setWriter(writer);

      TeXParser parser = new TeXParser(this);

      if (atIsLetter)
      {
         parser.setCatCode('@', TeXParser.TYPE_LETTER);
      }

      parser.parse(new TeXReader(str));

      String html = writer.toString();
      setWriter(null);

      return html;
   }

   public String convert(String str, boolean atIsLetter, boolean useMathJax)
    throws IOException
   {
      setIsInDocEnv(true);
      setUseMathJax(useMathJax);

      StringWriter writer = new StringWriter();
      setWriter(writer);

      TeXParser parser = new TeXParser(this);

      if (atIsLetter)
      {
         parser.setCatCode('@', TeXParser.TYPE_LETTER);
      }

      parser.parse(new TeXReader(str));

      String html = writer.toString();
      setWriter(null);

      return html;
   }

   public void startSection(boolean isNumbered, String tag, String name)
    throws IOException
   {
   }

}
