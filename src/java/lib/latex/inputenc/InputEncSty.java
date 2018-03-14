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

   public String getOption(Charset charset)
     throws LaTeXSyntaxException
   {
      return getOption(getParser(), charset);
   }

   public static String getOption(TeXParser parser, Charset charset)
     throws LaTeXSyntaxException
   {
      String charsetName = charset.name();

      if (charsetName.equals("UTF-8"))
      {
         return "utf8";
      }

      if (charsetName.equals("US-ASCII"))
      {
         return "ascii";
      }

      if (charsetName.equals("ISO-8859-1"))
      {
         return "latin1";
      }

      if (charsetName.equals("ISO-8859-2"))
      {
         return "latin2";
      }

      if (charsetName.equals("ISO-8859-3"))
      {
         return "latin3";
      }

      if (charsetName.equals("ISO-8859-4"))
      {
         return "latin4";
      }

      if (charsetName.equals("ISO-8859-5"))
      {
         return "latin5";
      }

      if (charsetName.equals("ISO-8859-9"))
      {
         return "latin9";
      }

      if (charsetName.equals("ISO-8859-10"))
      {
         return "latin10";
      }

      if (charsetName.equals("DEC-MCS"))
      {
         return "decmulti";
      }

      if (charsetName.equals("Cp850"))
      {
         return "cp850";
      }

      if (charsetName.equals("Cp852"))
      {
         return "cp852";
      }

      if (charsetName.equals("Cp437"))
      {
         return "cp437";
      }

      if (charsetName.equals("Cp865"))
      {
         return "cp865";
      }

      if (charsetName.equals("MacRoman"))
      {
         return "applemac";
      }

      if (charsetName.equals("MacCentralEurope"))
      {
         return "macce";
      }

      if (charsetName.equals("Cp1250"))
      {
         return "cp1250";
      }

      if (charsetName.equals("Cp1252"))
      {
         return "cp1252";
      }

      if (charsetName.equals("Cp1257"))
      {
         return "cp1257";
      }

      throw new LaTeXSyntaxException(parser,
        ERROR_UNKNOWN_ENCODING, charsetName);
   }

   public static Charset getCharSet(String texCharset)
    throws IllegalCharsetNameException
   {
      return Charset.forName(getCharSetName(texCharset));
   }

   public static String getCharSetName(String texCharset)
    throws IllegalCharsetNameException
   {
      if (texCharset.equals("ascii"))
      {
         return "US-ASCII";
      }
      else if (texCharset.equals("latin1"))
      {
         return "ISO-8859-1";
      }
      else if (texCharset.equals("latin2"))
      {
         return "ISO-8859-2";
      }
      else if (texCharset.equals("latin3"))
      {
         return "ISO-8859-3";
      }
      else if (texCharset.equals("latin4"))
      {
         return "ISO-8859-4";
      }
      else if (texCharset.equals("latin5"))
      {
         return "ISO-8859-5";
      }
      else if (texCharset.equals("latin9"))
      {
         return "ISO-8859-9";
      }
      else if (texCharset.equals("latin10"))
      {
         return "ISO-8859-10";
      }
      else if (texCharset.equals("decmulti"))
      {
         return "DEC-MCS";
      }
      else if (texCharset.equals("cp850"))
      {
         return "Cp850";
      }
      else if (texCharset.equals("cp852"))
      {
         return "Cp852";
      }
      else if (texCharset.equals("cp858"))
      {
         return "Cp858";
      }
      else if (texCharset.equals("cp437") || texCharset.equals("cp437de")) 
      {
         return "Cp437";
      }
      else if (texCharset.equals("cp865"))
      {
         return "Cp865";
      }
      else if (texCharset.equals("applemac"))
      {
         return "MacRoman";
      }
      else if (texCharset.equals("macce"))
      {
         return "MacCentralEurope";
      }
      else if (texCharset.equals("next"))
      {
         // don't known appropriate Java encoding label for this one
      }
      else if (texCharset.equals("cp1250"))
      {
         return "Cp1250";
      }
      else if (texCharset.equals("cp1252") || texCharset.equals("ansinew"))
      {
         return "Cp1252";
      }
      else if (texCharset.equals("cp1257"))
      {
         return "Cp1257";
      }
      else if (texCharset.equals("utf8"))
      {
         return "UTF-8";
      }

      return texCharset;
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

   public static final String ERROR_UNKNOWN_ENCODING
    = "inputenc.unknown.encoding";
}
