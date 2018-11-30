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
      this(parser, texPath, true, "tex");
   }

   public TeXPath(TeXParser parser, String texPath, boolean useKpsewhich)
     throws IOException
   {
      this(parser, texPath, useKpsewhich, "tex");
   }

   public TeXPath(TeXParser parser, String texPath, String... defExt)
     throws IOException
   {
      this(parser, texPath, true, defExt);
   }

   public TeXPath(TeXParser parser, String texPath, String defExt,
     boolean useKpsewhich)
     throws IOException
   {
      init(parser, texPath, useKpsewhich, defExt);
   }

   public TeXPath(TeXParser parser, String texPath,
     boolean useKpsewhich, String... defExt)
     throws IOException
   {
      init(parser, texPath, useKpsewhich, defExt);
   }

   public TeXPath(TeXParser parser, File file)
     throws IOException
   {
      init(parser, file);
   }

   private void init(TeXParser parser, File file)
     throws IOException
   {
      File parent = (parser == null ? null : parser.getCurrentParentFile());

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

   private void init(TeXParser parser, String texPath, String... defExt)
     throws IOException
   {
      init(parser, texPath, true, defExt);
   }

   private void init(TeXParser parser, String texPath,
     boolean useKpsewhich, String... defExt)
     throws IOException
   {
      File parent = (parser == null ? null : parser.getCurrentParentFile());

      base = (parent == null ? null : parent.toPath());

      // This deals with the entire path enclosed within double
      // quotes.
      if (texPath.startsWith("\"") && texPath.endsWith("\""))
      {
         texPath = texPath.substring(1, texPath.length()-1);
      }

      String[] split = texPath.split("/");

      // is texPath an absolute path?

      File root = (new File(split[0].isEmpty() ? File.separator : split[0]));

      int i = 0;

      if (root.isAbsolute())
      {
         base = null;
         parent = root;
         i = 1;
      }

      int n = split.length-1;

      for (; i < n; i++)
      {
         if (split[i].isEmpty())
         {
            continue;
         }

         if (parent == null)
         {
            parent = new File(split[i]);
         }
         else
         {
            parent = new File(parent, split[i]);
         }
      }

      // This deals with the case where the last element is obtained
      // from \jobname
      if (split[n].startsWith("\"") && split[n].endsWith("\""))
      {
         split[n] = split[n].substring(1, split[n].length()-1);
      }

      File file = null;

      String baseName = split[n];

      boolean hasExtension = baseName.contains(".");

      if (hasExtension && (parent != null && parent.isAbsolute()))
      {
         file = new File(parent, split[n]);

         if (file.exists())
         {
            relative = file.toPath();
            return;
         }
      }

      if (defExt.length > 0 && !hasExtension)
      {
         for (String ext : defExt)
         {
            if (ext.isEmpty())
            {
               split[n] = baseName;
            }
            else
            {
               split[n] = baseName+"."+ext;
            }

            file = (parent == null ? new File(split[n]) : 
              new File(parent, split[n]));

            // Does file exist?

            if (file.exists())
            {
               if (base == null)
               {
                  relative = file.toPath();
               }
               else
               {
                  relative = base.relativize(file.toPath());
               }

               return;
            }

            if (useKpsewhich && parser != null)
            {
               // Can kpsewhich find the file?

               try
               {
                  String loc = parser.getListener().getTeXApp().kpsewhich(split[n]);

                  if (loc != null && !loc.isEmpty())
                  {
                     foundByKpsewhich = true;

                     init(parser, loc, false, "");

                     return;
                  }
               }
               catch (IOException|InterruptedException e)
               {
                  // kpsewhich couldn't find the file
               }
            }
         }

         split[n] = baseName+"."+defExt[0];
         useKpsewhich = false;
      }

      file = (parent == null ? new File(split[n]) : 
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

      if (!file.exists() && useKpsewhich && parser != null)
      {
         // Can kpsewhich find the file?

         try
         {
            String loc = parser.getListener().getTeXApp().kpsewhich(split[n]);

            if (loc != null && !loc.isEmpty())
            {
               foundByKpsewhich = true;

               init(parser, loc, false, "");
            }
         }
         catch (IOException|InterruptedException e)
         {
            // kpsewhich couldn't find the file
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

   public boolean isHidden()
   {
      if (base != null)
      {
         for (int i = base.getNameCount()-1; i >= 0; i--)
         {
            String name = base.getName(i).toString();

            if (name.startsWith(".") && !name.equals(".") && !name.equals(".."))
            {
               return true;
            }
         }
      }

      for (int i = relative.getNameCount()-1; i >= 0; i--)
      {
         String name = relative.getName(i).toString();

         if (name.startsWith(".") && !name.equals(".") && !name.equals(".."))
         {
            return true;
         }
      }

      return false;
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
      Path path = (base == null ? relative : base.resolve(relative));

      return path.toFile();
   }

   public Path getPath()
   {
      return (base == null ? relative : base.resolve(relative));
   }

   public Path getRelative()
   {
      return relative;
   }

   public Path getLeaf()
   {
      return relative.getName(relative.getNameCount()-1);
   }

   public String getExtension()
   {
      String name = getLeaf().toString();

      int idx = name.lastIndexOf(".");

      return idx == -1 ? null : name.substring(idx);
   }

   public Path getFileName()
   {
      return relative.getFileName();
   }

   public boolean isAbsolute()
   {
      return relative.isAbsolute();
   }

   public boolean exists()
   {
      File file = getFile();

      return file == null ? false : file.exists();
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

   public boolean wasFoundByKpsewhich()
   {
      return foundByKpsewhich;
   }

   private Path base, relative;

   private boolean foundByKpsewhich = false;
}
