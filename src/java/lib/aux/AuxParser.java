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
package com.dickimawbooks.texparserlib.aux;

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

/**
 * Parses aux files
 */

public class AuxParser extends DefaultTeXParserListener
  implements Writeable
{
   public AuxParser(TeXApp texApp)
     throws IOException
   {
      this(texApp, null);
   }

   public AuxParser(TeXApp texApp, Charset charset)
     throws IOException
   {
      super(null);
      this.texApp = texApp;
      this.charset = charset;

      setWriteable(this);

      auxData = new Vector<AuxData>();
   }

   public TeXApp getTeXApp()
   {
      return texApp;
   }

   public TeXParser parseAuxFile(File auxFile)
     throws IOException
   {
      return parseAuxFile(auxFile, null);
   }

   public TeXParser parseAuxFile(File auxFile, Charset charset)
     throws IOException
   {
      if (charset != null)
      {
         this.charset=charset;
      }

      TeXParser parser = new TeXParser(this);

      int code = parser.getCatCode('@');
      parser.setCatCode('@', TeXParser.TYPE_LETTER);
      parser.parse(auxFile);
      parser.setCatCode('@', code);

      return parser;
   }

   protected void addPredefined()
   {
      super.addPredefined();

      addAuxCommand("newlabel", 2);
      addAuxCommand("bibstyle", 1);
      addAuxCommand("citation", 1);
      addAuxCommand("bibdata", 1);
      addAuxCommand("bibcite", 2);
   }

   public void addAuxCommand(String name, int numArgs)
   {
      putControlSequence(new AuxCommand(name, numArgs));
   }

   public ControlSequence getControlSequence(String name)
   {
      ControlSequence cs = super.getControlSequence(name);

      return (cs instanceof AuxCommand) ? cs : new AuxIgnoreable(name);
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

   public void href(String url, TeXObject text)
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
   }

   public void beginParse(File file)
      throws IOException
   {
      getTeXApp().message(getTeXApp().getMessage(
         TeXApp.MESSAGE_READING, file.getAbsolutePath()));
   }

   public void addAuxData(AuxData data)
   {
      auxData.add(data);
   }

   public Vector<AuxData> getAuxData(String name)
   {
      Vector<AuxData> list = new Vector<AuxData>();

      for (AuxData data : auxData)
      {
         if (data.getName().equals(name))
         {
            list.add(data);
         }
      }

      return list;
   }

   public Vector<AuxData> getAuxData()
   {
      return auxData;
   }

   public Charset getCharSet()
   {
      return charset;
   }

   // shouldn't be needed in auxFile
   public float emToPt(float emValue)
   {
      getTeXApp().warning(getParser(),
         "Can't convert from em to pt, no font information loaded");

      return 9.5f*emValue;
   }

   // shouldn't be needed in auxFile
   public float exToPt(float exValue)
   {
      getTeXApp().warning(getParser(),
         "Can't convert from ex to pt, no font information loaded");

      return 4.4f*exValue;
   }

   private Vector<AuxData> auxData;
   private TeXApp texApp;

   private Charset charset=null;
}
