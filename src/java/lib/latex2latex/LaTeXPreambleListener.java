/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.io.Writer;
import java.io.StringWriter;
import java.nio.charset.Charset;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;

/**
 * Reads in LaTeX preamble.
 */

public class LaTeXPreambleListener extends LaTeX2LaTeX
{
   public LaTeXPreambleListener(TeXApp texApp, Writer writer)
    throws IOException
   {
      super(texApp, null, false);
      this.writer = writer;
   }

   public static String readPreamble(File file, final Charset charset)
    throws IOException
   {
      TeXApp texApp = new TeXAppAdapter()
       {
          @Override
          public Charset getDefaultCharset()
          {
             return charset == null ? Charset.defaultCharset() : charset;
          }
       };

      return readPreamble(file, texApp);
   }

   public static String readPreamble(File file, TeXApp texApp)
    throws IOException
   {
      StringWriter writer = new StringWriter();

      LaTeXPreambleListener listener = new LaTeXPreambleListener(texApp, writer);
      TeXParser parser = new TeXParser(listener);

      parser.parse(file);

      return writer.toString();
   }

   @Override
   public void beginDocument(TeXObjectList stack)
     throws IOException
   {
      throw new EOFException();
   }

   @Override
   public void beginParse(File file, Charset encoding)
     throws IOException
   {
      getParser().message(TeXApp.MESSAGE_READING, file.toString());

      if (encoding != null)
      {
         getParser().message(TeXApp.MESSAGE_ENCODING, encoding.name());
      }
   }

   @Override
   public void endParse(File file)
    throws IOException
   {
   }

   @Override
   public LaTeXSty requirepackage(KeyValList options, String styName, 
     boolean loadParentOptions, TeXObjectList stack)
     throws IOException
   {
      LaTeXSty sty = getLaTeXSty(options, styName, loadParentOptions, stack);
      addFileReference(sty);
      loadedPackages.add(sty);

      writeCodePoint(parser.getEscChar());
      write("RequirePackage");

      if (options != null)
      {
         write('[');
         write(options.toString(parser));
         write(']');
      }

      writeCodePoint(parser.getBgChar());
      write(styName);
      writeCodePoint(parser.getEgChar());

      return sty;
   }

   @Override
   public LaTeXSty usepackage(KeyValList options, String styName, 
     boolean loadParentOptions, TeXObjectList stack)
     throws IOException
   {
      LaTeXSty sty = getLaTeXSty(options, styName, loadParentOptions, stack);
      addFileReference(sty);
      loadedPackages.add(sty);

      writeCodePoint(parser.getEscChar());
      write("usepackage");

      if (options != null)
      {
         write('[');
         write(options.toString(parser));
         write(']');
      }

      writeCodePoint(parser.getBgChar());
      write(styName);
      writeCodePoint(parser.getEgChar());

      return sty;
   }

   @Override
   public void includegraphics(TeXObjectList stack, KeyValList options, String imgName)
     throws IOException
   {
      writeCodePoint(parser.getEscChar());
      write("includegraphics");

      if (options != null && options.size() > 0)
      {
         write('[');
         write(options.toString(parser));
         write(']');
      }

      writeCodePoint(parser.getBgChar());
      write(imgName);
      writeCodePoint(parser.getEgChar());
   }

   @Override
   public void writeCodePoint(int charCode) throws IOException
   {
      if (writer != null)
      {
         if (charCode <= Character.MAX_VALUE)
         {
            writer.write((char)charCode);
         }
         else
         {
            for (char c : Character.toChars(charCode))
            {
               writer.write(c);
            }
         }
      }
      else
      {
         getParser().warning("null writer");
      }
   }

   @Override
   public void write(char c) throws IOException
   {
      if (writer != null)
      {
         writer.write(c);
      }
      else
      {
         getParser().warning("null writer");
      }
   }

   @Override
   public void write(String string) throws IOException
   {
      if (writer != null)
      {
         writer.write(string);
      }
      else
      {
         getParser().warning("null writer");
      }
   }

   @Override
   public void writeln(String string) throws IOException
   {
      if (writer != null)
      {
         writer.write(String.format("%s%n", string));
      }
      else
      {
         getParser().warning("null writer");
      }
   }

   @Override
   public void writeliteral(String string) throws IOException
   {
      write(string);
   }

   @Override
   public void writeliteralln(String string) throws IOException
   {
      writeln(string);
   }

   public void writeln(char c) throws IOException
   {
      if (writer != null)
      {
         writer.write(String.format("%c%n", c));
      }
      else
      {
         getParser().warning("null writer");
      }
   }

   public void writeln() throws IOException
   {
      if (writer != null)
      {
         writer.write(String.format("%n"));
      }
      else
      {
         getParser().warning("null writer");
      }
   }

   private Writer writer;
}
