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

import com.dickimawbooks.texparserlib.*;

/**
 * Information about a LaTeX file, package or class.
 */

public class LaTeXFile extends TeXPath
{
   public LaTeXFile(TeXParser parser, String texPath)
     throws IOException
   {
      this(parser, null, texPath, "tex");
   }

   public LaTeXFile(TeXParser parser, String texPath, String defExt)
     throws IOException
   {
      this(parser, null, texPath, defExt);
   }

   public LaTeXFile(TeXParser parser, KeyValList options, String texPath,
     String defExt)
     throws IOException
   {
      super(parser, texPath, defExt);

      String fileName = getFileName().toString();

      int idx = fileName.lastIndexOf(".");

      if (idx > -1)
      {
         this.ext = fileName.substring(idx);
         this.baseName = fileName.substring(0, idx);
      }
      else
      {
         this.ext = defExt;
         this.baseName = fileName;
      }

      this.options = options;
   }

   public String getName()
   {
      return baseName;
   }

   public String getExtension()
   {
      return ext;
   }

   public KeyValList getOptions()
   {
      return options;
   }

   public void addOptionIfAbsent(String key, TeXObject value)
   {
      if (options == null)
      {
         options = new KeyValList();
         options.put(key, value);
      }
      else
      {
         options.putIfAbsent(key, value);
      }
   }

   private String baseName;
   private KeyValList options;
   private String ext;
}
