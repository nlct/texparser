/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
      isOpen = true;
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

      isOpen = true;
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

   public TeXReader(File file, Charset charset)
    throws IOException
   {
      this(null, file, charset);
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
      if (isClosed()) return -1;

      int c = reader.read();

      if (c == -1)
      {
         eofFound = true;
      }

      return c;
   }

   public int read(char[] cbuf) throws IOException
   {
      if (isClosed()) return -1;

      int result = reader.read(cbuf);

      if (result == -1)
      {
         eofFound = true;
      }

      return result;
   }

   public int read(char[] cbuf, int off, int len) throws IOException
   {
      if (isClosed()) return -1;

      int result = reader.read(cbuf, off, len);

      if (result == -1)
      {
         eofFound = true;
      }

      return result;
   }

   public int read(CharBuffer cb) throws IOException
   {
      if (isClosed()) return -1;

      int result = reader.read(cb);

      if (result == -1)
      {
         eofFound = true;
      }

      return result;
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
      isOpen = false;
      reader.close();
   }

   public boolean isClosed()
   {
      return !isOpen;
   }

   public boolean isEnded()
   {
      return eofFound;
   }

   // forcibly close this and all ancestors and clear any pending
   // stacks
   public void closeAll(TeXApp app)
   {
      try
      {
         close();
      }
      catch (IOException e)
      {
         app.error(e);
      }

      pending = null;

      if (parent != null)
      {
         parent.closeAll(app);
      }
   }

   public Object getSource()
   {
      return source;
   }

   public TeXReader getParent()
   {
      return parent;
   }

   public TeXReader getBaseReader()
   {
      if (parent == null) return this;

      return parent.getBaseReader();
   }

   public Reader getReader()
   {
      return reader;
   }

   /* Is this a descendant of other */
   public boolean isDescendantOf(TeXReader other)
   {
      if (parent == null) return false;

      if (other == parent) return true;

      return parent.isDescendantOf(other);
   }

   public void setParent(TeXReader parentReader)
   {
      if (this == parentReader)
      {
         throw new IllegalArgumentException("Reader can't be its own parent");
      }

      if (parentReader != null && parentReader.isDescendantOf(this))
      {
         throw new IllegalArgumentException(
          "Parent reader can't be its child's descendant");
      }

      this.parent = parentReader;
   }

   @Override
   public String toString()
   {
      int line = -1;

      if (reader instanceof LineNumberReader)
      {
         line = ((LineNumberReader)reader).getLineNumber();
      }

      return String.format("%s[source=%s,line=%d,pending=%s,isOpen=%s,eofFound=%s,parent=%s]", 
        getClass().getSimpleName(), source, line, pending, isOpen, eofFound,
         parent == null ? null : parent.source);
   }

   public void setPending(TeXObjectList pending)
   {
      this.pending = pending;
   }

   public TeXObjectList getPending()
   {
      return pending;
   }

   public boolean hasPending()
   {
      return pending != null;
   }

   private Reader reader;
   private TeXReader parent;
   private Object source;
   private TeXObjectList pending;
   private boolean isOpen = false, eofFound=false;
}
