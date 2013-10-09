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
   protected L2HStringConverter(TeXApp app)
   {
      super(app);

      setWriteable(this);
   }

   public static String convert(String str, boolean atIsLetter)
    throws IOException
   {
      return convert(new TeXAppAdapter(), str, atIsLetter);
   }

   public static String convert(TeXApp app, String str, boolean atIsLetter)
    throws IOException
   {
      L2HStringConverter listener = new L2HStringConverter(app);
      StringWriter writer = new StringWriter();
      listener.setWriter(writer);

      listener.setTeXParser(new TeXParser(listener));

      if (atIsLetter)
      {
         listener.parser.setCatCode('@', TeXParser.TYPE_LETTER);
      }

      StringReader reader = new StringReader(str);
      listener.parser.parse(reader);

      String html = writer.toString();
      listener.setWriter(null);

      return html;
   }

}
