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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HStringConverter extends L2HConverter
{
   public L2HStringConverter(TeXApp app)
   {
      super(app);

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

   public String convert(String str, boolean atIsLetter)
    throws IOException
   {
      StringWriter writer = new StringWriter();
      setWriter(writer);

      setTeXParser(new TeXParser(this));

      if (atIsLetter)
      {
         parser.setCatCode('@', TeXParser.TYPE_LETTER);
      }

      StringReader reader = new StringReader(str);
      parser.parse(reader);

      String html = writer.toString();
      setWriter(null);

      return html;
   }

}
