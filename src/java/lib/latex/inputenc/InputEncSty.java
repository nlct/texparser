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
package com.dickimawbooks.texparserlib.latex.inputenc;

import java.io.IOException;
import java.util.Iterator;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class InputEncSty extends LaTeXSty
{
   public InputEncSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
    throws IOException
   {
      super(options, "inputenc", listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      registerControlSequence(new InputEncoding());
   }

   public void processOption(String option, TeXObject value)
    throws IOException
   {
      if (isKnownEncoding(option))
      {
         getListener().setInputEncoding(option);
      }
   }

   public static Charset getCharSet(String encoding)
    throws IllegalCharsetNameException
   {
      if (encoding.equals("ascii"))
      {
         return Charset.forName("US-ASCII");
      }
      else if (encoding.equals("utf8"))
      {
         return Charset.forName("UTF-8");
      }
      else if (encoding.equals("latin1"))
      {
         return Charset.forName("ISO-8859-1");
      }

      return Charset.forName(encoding);
   }

   public boolean isKnownEncoding(String value)
   {
      for (int i = 0; i < KNOWN_ENCODINGS.length; i++)
      {
         if (KNOWN_ENCODINGS[i].equals(value))
         {
            return true;
         }
      }

      return false;
   }

   public static final String[] KNOWN_ENCODINGS = new String[]
   {"ascii", "latin1", "latin2", "latin3", "latin4", "latin5",
    "latin9", "latin10", "decmulti", "cp850", "cp852", "cp858",
    "cp437", "cp437de", "cp865", "applemac", "macce", "next",
    "cp1250", "cp1252", "cp1257", "ansinew", "utf8"};
}
