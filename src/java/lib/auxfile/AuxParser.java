/*
    Copyright (C) 2013-2020 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.auxfile;

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
import com.dickimawbooks.texparserlib.primitives.Primitive;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.Input;

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
      this(texApp, charset, null);
   }

   public AuxParser(TeXApp texApp, Charset charset, String labelPrefix)
     throws IOException
   {
      super(null);
      this.texApp = texApp;
      this.charset = charset;
      this.labelPrefix = labelPrefix;

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

   @Override
   protected void addPredefined()
   {
      super.addPredefined();

      putControlSequence(new Input("@input", Input.NOT_FOUND_ACTION_WARN));

      addAuxCommand("newlabel", 2, labelPrefix);
      addAuxCommand("bibstyle", 1);
      addAuxCommand("citation", 1);
      addAuxCommand("bibdata", 1);
      addAuxCommand("bibcite", 2, labelPrefix);

      putControlSequence(new AuxProvideCommand());

      putControlSequence(new AuxIgnoreable("@writefile", false, 
        new boolean[]{true, true}));

      putControlSequence(new AuxIgnoreable("selectlanguage", true, new boolean[]{true}));
   }

   public void addAuxCommand(String name, int numArgs)
   {
      addAuxCommand(name, numArgs, null);
   }

   public void addAuxCommand(String name, int numArgs, String prefix)
   {
      putControlSequence(new AuxCommand(name, numArgs, prefix));
   }

   /*
    * The aux parser is just intended to gather certain information,
    * so most commands should be ignored, but there are a few that
    * need interpreting.
    */ 
   public boolean isAllowedAuxCommand(ControlSequence cs)
   {
      return (cs instanceof Input
              || cs instanceof AuxCommand 
              || cs instanceof AuxIgnoreable 
              || cs instanceof AssignedControlSequence
              || cs instanceof AuxProvideCommand
              || cs instanceof Primitive);
   }

   public ControlSequence getControlSequence(String name)
   {
      ControlSequence cs = getParser().getControlSequence(name);

      return isAllowedAuxCommand(cs) ? cs : new AuxIgnoreable(name);
   }

   public ControlSequence createUndefinedCs(String name)
   {
      return new AuxIgnoreable(name);
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

   public void beginParse(File file, Charset encoding)
      throws IOException
   {
      getParser().message(TeXApp.MESSAGE_READING, file);

      if (encoding != null)
      {
         getParser().message(TeXApp.MESSAGE_ENCODING, encoding);
      }
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
      getParser().warning(
         "Can't convert from em to pt, no font information loaded");

      return 9.5f*emValue;
   }

   // shouldn't be needed in auxFile
   public float exToPt(float exValue)
   {
      getParser().warning(
         "Can't convert from ex to pt, no font information loaded");

      return 4.4f*exValue;
   }

   private Vector<AuxData> auxData;
   private TeXApp texApp;

   private Charset charset=null;
   private String labelPrefix = null;
}
