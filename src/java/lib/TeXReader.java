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
package com.dickimawbooks.texparserlib;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.CharBuffer;

public class TeXReader implements Readable,Closeable
{
   public TeXReader(TeXReader parent, String string)
    throws IOException
   {
      this.parent = parent;
      this.source = string;
      reader = new StringReader(string);
   }

   public TeXReader(String string)
    throws IOException
   {
      this(null, string);
   }

   public TeXReader(TeXReader parent, File file, Charset charset)
    throws IOException
   {
      this.parent = parent;
      this.source = file;

      if (charset == null)
      {
         reader = new LineNumberReader(new FileReader(file));
      }
      else
      {
         reader = new LineNumberReader(
           new LineNumberReader(Files.newBufferedReader(file.toPath(),
            charset)));
      }
   }

   public TeXReader(TeXReader parent, File file)
    throws IOException
   {
      this(parent, file, null);
   }

   public TeXReader(File file)
    throws IOException
   {
      this(null, file, null);
   }

   public int getLineNumber()
   {
      if (reader instanceof LineNumberReader)
      {
         return ((LineNumberReader)reader).getLineNumber();
      }

      return -1;
   }

   public void mark(int readAheadLimit) throws IOException
   {
      reader.mark(readAheadLimit);
   }

   public void reset() throws IOException
   {
      reader.reset();
   }

   public int read() throws IOException
   {
      return reader.read();
   }

   public int read(char[] cbuf) throws IOException
   {
      return reader.read(cbuf);
   }

   public int read(char[] cbuf, int off, int len) throws IOException
   {
      return reader.read(cbuf, off, len);
   }

   public int read(CharBuffer cb) throws IOException
   {
      return reader.read(cb);
   }

   public long skip(long n) throws IOException
   {
      return reader.skip(n);
   }

   public boolean ready() throws IOException
   {
      return reader.ready();
   }

   public void close() throws IOException
   {
      reader.close();
   }

   public Object getSource()
   {
      return source;
   }

   public TeXReader getParent()
   {
      return parent;
   }

   public void setParent(TeXReader parentReader)
   {
      if (this == parentReader)
      {
         throw new IllegalArgumentException("Reader can't be its own parent");
      }

      this.parent = parentReader;
   }

   public String toString()
   {
      return "TeXReader[reader:"+reader+",source:"+source
       +",parent="+parent+"]";
   }

   public void setPending(TeXObjectList pending)
   {
      this.pending = pending;
   }

   public TeXObjectList getPending()
   {
      return pending;
   }

   private Reader reader;
   private TeXReader parent;
   private Object source;
   private TeXObjectList pending;
}
