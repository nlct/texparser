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
package com.dickimawbooks.texparserlib.bib;

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
 * Parses bib files
 */

public class BibParser extends DefaultTeXParserListener
  implements Writeable
{
   public BibParser(TeXApp texApp)
     throws IOException
   {
      this(texApp, null);
   }

   public BibParser(TeXApp texApp, Charset charset)
     throws IOException
   {
      super(null);
      this.texApp = texApp;
      this.charset = charset;

      setWriteable(this);

      bibData = new Vector<BibData>();
   }

   public TeXApp getTeXApp()
   {
      return texApp;
   }

   public TeXParser parseBibFile(File bibFile)
     throws IOException
   {
      return parseBibFile(bibFile, null);
   }

   public TeXParser parseBibFile(File bibFile, Charset charset)
     throws IOException
   {
      if (charset != null)
      {
         this.charset=charset;
      }

      TeXParser parser = new TeXParser(this);

      int atcode = parser.getCatCode('@');
      int hashcode = parser.getCatCode('#');
      parser.setCatCode('@', TeXParser.TYPE_ACTIVE);
      parser.setCatCode('#', TeXParser.TYPE_OTHER);
      parser.parse(bibFile);
      parser.setCatCode('@', atcode);
      parser.setCatCode('#', hashcode);

      return parser;
   }

   protected void addPredefined()
   {
      parser.putActiveChar(new At());
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

   public void addBibData(BibData data)
   {
      bibData.add(data);
   }

   public BibEntry getBibEntry(String id)
   {
      for (BibData data : bibData)
      {
         if (data instanceof BibEntry 
          && ((BibEntry)data).getId().equals(id))
         {
            return (BibEntry)data;
         }
      }

      return null;
   }

   public BibString getBibString(String key)
   {
      for (BibData data : bibData)
      {
         if (data instanceof BibString 
          && ((BibString)data).getKey().equals(key))
         {
            return (BibString)data;
         }
      }

      return null;
   }

   public Vector<BibData> getBibData()
   {
      return bibData;
   }

   public Charset getCharSet()
   {
      return charset;
   }

   // shouldn't be needed in bibFile
   public float emToPt(float emValue)
   {
      getTeXApp().warning(getParser(),
         "Can't convert from em to pt, no font information loaded");

      return 9.5f*emValue;
   }

   // shouldn't be needed in bibFile
   public float exToPt(float exValue)
   {
      getTeXApp().warning(getParser(),
         "Can't convert from ex to pt, no font information loaded");

      return 4.4f*exValue;
   }

   private Vector<BibData> bibData;
   private TeXApp texApp;

   private Charset charset=null;
}
