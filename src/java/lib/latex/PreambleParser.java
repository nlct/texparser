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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.Charset;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.primitives.Undefined;

/**
 * Parses LaTeX document preamble
 */

public class PreambleParser extends LaTeXParserListener
  implements Writeable
{
   public PreambleParser(TeXApp texApp)
     throws IOException
   {
      this(texApp, Undefined.ACTION_IGNORE);
   }

   public PreambleParser(TeXApp texApp, byte undefAction)
     throws IOException
   {
      super(null);
      this.texApp = texApp;

      setWriteable(this);

      setUndefinedAction(undefAction);
   }

   public TeXApp getTeXApp()
   {
      return texApp;
   }

   public TeXParser parse(File texFile)
     throws IOException
   {
      TeXParser parser = new TeXParser(this);

      parser.parse(texFile);

      return parser;
   }

   public TeXParser parse(File texFile, Charset charset)
     throws IOException
   {
      TeXParser parser = new TeXParser(this);

      parser.parse(texFile, charset);

      return parser;
   }

   public static String getSourceInputEncoding(TeXApp app, File texFile)
   throws IOException
   {
      PreambleParser listener = new PreambleParser(app);
      TeXParser parser = new TeXParser(listener);
      parser.parse(texFile);
    
      return listener.getInputEncoding();
   }

   public static LaTeXFile getDocumentClass(TeXApp app, File texFile)
   throws IOException
   {
      PreambleParser listener = new PreambleParser(app)
      {
         public void documentclass(KeyValList options, String clsName, 
            boolean loadParentOptions)
            throws IOException
         {
            super.documentclass(options, clsName, loadParentOptions);
            throw new EOFException();
         }
      };

      TeXParser parser = new TeXParser(listener);
      parser.parse(texFile);

      return listener.getDocumentClass();
   }

   public Writeable getWriteable()
   {
      return this;
   }

   public void write(String text)
     throws IOException
   {
   }

   public void writeln(String text)
     throws IOException
   {
   }

   public void write(char c)
     throws IOException
   {
   }

   public void writeCodePoint(int codePoint)
     throws IOException
   {
   }

   public void overwithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXObject before, TeXObject after)
    throws IOException
   {
   }

   public void abovewithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness, TeXObject before, 
     TeXObject after)
    throws IOException
   {
   }

   public void skipping(Ignoreable ignoreable)
      throws IOException
   {
   }

   public void substituting(String original, String replacement)
   {
      texApp.substituting(parser, original, replacement);
   }

   public void href(String url, TeXObject text)
      throws IOException
   {
   }

   public void includegraphics( 
     KeyValList options, String imgName)
     throws IOException
   {
   }

   public void subscript(TeXObject arg)
     throws IOException
   {
   }

   public void superscript(TeXObject arg)
     throws IOException
   {
   }

   public void endParse(File file)
      throws IOException
   {
      this.file = null;
   }

   public void beginParse(File file, Charset encoding)
      throws IOException
   {
      this.file = file;
   }

   public File getFile()
   {
      return file;
   }

   public void beginDocument()
     throws IOException
   {
      super.beginDocument();
      endDocument();
   }

   private TeXApp texApp;

   private File file;
}
