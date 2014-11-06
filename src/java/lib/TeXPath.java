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

import java.io.IOException;
import java.io.File;
import java.nio.file.Path;

import java.util.Iterator;

public class TeXPath
{
   public TeXPath(TeXParser parser, String texPath)
     throws IOException
   {
      this(parser, texPath, "tex");
   }

   public TeXPath(TeXParser parser, String texPath, String defExt)
     throws IOException
   {
      init(parser, texPath, defExt);
   }

   public TeXPath(TeXParser parser, File file)
     throws IOException
   {
      init(parser, file);
   }

   private void init(TeXParser parser, File file)
     throws IOException
   {
      File parent = parser.getCurrentParentFile();

      Path path = file.toPath();

      if (path.isAbsolute())
      {
         base = null;
         relative = path;
      }
      else
      {
         base = (parent == null ? null : parent.toPath());
         relative = (base == null ? path : base.relativize(path));
      }

   }

   private void init(TeXParser parser, String texPath, String defExt)
     throws IOException
   {
      File parent = parser.getCurrentParentFile();

      base = (parent == null ? null : parent.toPath());

      String[] split = texPath.split("/");

      int n = split.length-1;

      for (int i = 0; i < n; i++)
      {
         if (parent == null)
         {
            parent = new File(split[i]);
         }
         else
         {
            parent = new File(parent, split[i]);
         }
      }

      if (!split[n].contains("."))
      {
         split[n] += "."+defExt;
      }

      File file = (parent == null ? new File(split[n]) : 
        new File(parent, split[n]));

      if (base == null)
      {
         relative = file.toPath();
      }
      else
      {
         relative = base.relativize(file.toPath());
      }

      // Does file exist?

      if (!file.exists())
      {
         // Can kpsewhich find the file?

         try
         {
            String loc = parser.getListener().getTeXApp().kpsewhich(split[n]);

            if (loc != null && !loc.isEmpty())
            {
               File f = new File(loc);

               if (f.exists())
               {
                  init(parser, f);
               }
            }
         }
         catch (Exception e)
         {
            // Not on TeX path
         }
      }
   }

   public Path getRelativePath()
   {
      return relative;
   }

   public Path getBaseDir()
   {
      return base;
   }

   public String getTeXPath(boolean stripExtension)
   {
      StringBuilder builder = new StringBuilder();

      Iterator<Path> it = relative.iterator();

      while (it.hasNext())
      {
         Path path = it.next();

         if (it.hasNext())
         {
            builder.append(path.toString());
            builder.append('/');
         }
         else if (stripExtension)
         {
            String name = path.toString();

            int idx = name.lastIndexOf(".");

            if (idx != -1)
            {
               name = name.substring(0, idx);
            }

            builder.append(name);
         }
         else
         {
            builder.append(path.toString());
         }
      }

      return builder.toString();
   }

   public File getFile()
   {
      return (base == null ? relative.toFile() : 
         base.resolve(relative).toFile());
   }

   public Path getRelative()
   {
      return relative;
   }

   public Path getFileName()
   {
      return relative.getFileName();
   }

   public boolean exists()
   {
      return getFile().exists();
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof TeXPath)) return false;

      TeXPath texPath = (TeXPath)obj;

      if (base == null && texPath.base == null)
      {
         return relative.equals(texPath.relative);
      }

      if (base == null || texPath.base == null) return false;

      return relative.equals(texPath.relative);
   }

   public String toString()
   {
      return (base == null ? relative.toString() : 
         base.resolve(relative).toString());
   }

   private Path base, relative;
}
