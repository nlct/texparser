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
package com.dickimawbooks.texparserlib.latex.fontenc;

import java.io.IOException;
import java.util.HashMap;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class FontEncSty extends LaTeXSty
{
   public FontEncSty(KeyValList options,  
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      super(options, "fontenc", listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      registerControlSequence(new FontEncodingCs(this));
   }

   public void processOption(String option, TeXObject value)
    throws IOException
   {
      FontEncoding encoding = getEncoding(option);

      if (encoding != null)
      {
         LaTeXParserListener listener = getListener();

         TeXSettings settings = listener.getParser().getSettings();
         encoding.addDefinitions(settings);
         settings.setFontEncoding(encoding);
      }
   }

   protected void preOptions()
     throws IOException
   {
      registerEncoding(new T2AEncoding());
      registerEncoding(new T2BEncoding());
      registerEncoding(new T2CEncoding());
   }

   public FontEncoding getEncoding(String name)
   {
      if (encodings == null)
      {
         return null;
      }

      return encodings.get(name);
   }

   public void registerEncoding(FontEncoding encoding)
   {
      registerEncoding(encoding.getName(), encoding);
   }

   public void registerEncoding(String name, FontEncoding encoding)
   {
      if (encodings == null)
      {
         encodings = new HashMap<String,FontEncoding>();
      }

      encodings.put(name, encoding);
   }

   private HashMap<String,FontEncoding> encodings;
}
