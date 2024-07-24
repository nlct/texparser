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
package com.dickimawbooks.texparserlib;

import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.charset.Charset;

import java.util.Iterator;

import com.dickimawbooks.texparserlib.latex.latex3.SequenceCommand;

public class TeXPath
{
   public TeXPath(TeXParser parser, String texPath)
     throws IOException
   {
      this(parser, texPath, (Charset)null);
   }

   public TeXPath(TeXParser parser, String texPath, Charset charset)
     throws IOException
   {
      this(parser, texPath, !texPath.contains("/"), "tex");
      setEncoding(charset);
   }

   public TeXPath(TeXParser parser, String texPath, boolean useKpsewhich)
     throws IOException
   {
      this(parser, texPath, useKpsewhich, "tex");
   }

   public TeXPath(TeXParser parser, String texPath, String... defExt)
     throws IOException
   {
      this(parser, texPath, !texPath.contains("/"), defExt);
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

   public TeXPath(TeXParser parser, File file, Charset charset)
     throws IOException
   {
      init(parser, file);
      setEncoding(charset);
   }

   private TeXPath()
   {
   }

   protected File getDefaultBaseDir(TeXParser parser)
   {
      if (parser == null)
      {
         return null;
      }

      File file = parser.getBaseDir();

      if (file != null && file.getName().equals("."))
      {
         return null;
      }

      return file;
   }

   /**
    * References an output file that may not exist.
    */
   public static TeXPath newOutputPath(TeXParser parser, String texPathStr,
     String defExt)
   {
      File parent = null;

      if (parser != null)
      {
         parent = parser.getListener().getOutputDir();
      }

      return newOutputPath(parent, texPathStr, defExt);
   }

   public static TeXPath newOutputPath(File outputDir, String texPathStr,
     String defExt)
   {
      File parent = outputDir;

      if (texPathStr.startsWith("\"") && texPathStr.endsWith("\""))
      {
         texPathStr = texPathStr.substring(1, texPathStr.length()-1);
      }

      String[] split = texPathStr.split("/");

      File root = new File(split.length == 1 ? split[0] : split[0] + File.separator);

      TeXPath texPath = new TeXPath();

      int i = 0;

      if (root.isAbsolute())
      {
         texPath.base = null;
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

      String baseName = split[n];

      if (defExt != null && !defExt.isEmpty())
      {
         boolean hasExtension = baseName.contains(".");

         if (!hasExtension)
         {
            baseName += "."+defExt;
         }
      }

      File file = new File(parent, baseName);

      Path path = file.toPath();

      if (path.isAbsolute())
      {
         texPath.relative = path;
      }
      else
      {
         texPath.base = (parent == null ? null : parent.toPath());
         texPath.relative = (texPath.base == null ? path : texPath.base.relativize(path));
      }

      return texPath;
   }

   private void init(TeXParser parser, File file)
     throws IOException
   {
      File parent = getDefaultBaseDir(parser);

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
      init(parser, texPath, !texPath.contains("/"), defExt);
   }

   private void init(TeXParser parser, String texPath,
     boolean useKpsewhich, String... defExt)
     throws IOException
   {
      init(parser, texPath, useKpsewhich, true, defExt);
   }

   private void init(TeXParser parser, String texPath,
     boolean useKpsewhich, boolean useL3SearchPath, String... defExt)
     throws IOException
   {
      File parent = getDefaultBaseDir(parser);

      base = (parent == null ? null : parent.toPath());

      // This deals with the entire path enclosed within double
      // quotes.
      if (texPath.startsWith("\"") && texPath.endsWith("\""))
      {
         texPath = texPath.substring(1, texPath.length()-1);
      }

      String[] split = texPath.split("/");

      /*
       * Check if texPath is an absolute path. 
       *
       * In the event texPath is an absolute path, then:
       *
       * On Unix-like systems, split[0] will be empty so "/" is
       * required to identify the root directory. On Windows,
       * split[0] will be the drive identifier (e.g. "C:") so this
       * needs to be followed by the file separator (e.g. "C:\").
       * See https://github.com/nlct/bib2gls/issues/3#issuecomment-597620407
       *
       * If texPath is a relative path, then appending the file
       * separator to the first element shouldn't be a problem
       * provided it's a directory. For example, if texPath is
       * "foo/bar.tex" then it should be okay to identify the first
       * element as "foo/" (or "foo\" for Windows). The only thing
       * to watch out for is if texPath is just a file name "foo.tex",
       * in which case split.length=1. (There's no reason I can
       * think of for texPath to be just the root directory, since
       * texPath should typically be a path to a file.)
       */

      File root = new File(split.length == 1 ? split[0] : split[0] + File.separator);

      int i = 0;

      if (root.isAbsolute())
      {
         useKpsewhich = false;
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

      if (!hasExtension)
      {
         // First try without extension
         if (tryExt(parser, useKpsewhich, useL3SearchPath, root, parent,
              split, baseName, null))
         {
            return;
         }
      }

      if (defExt.length > 0 && !hasExtension)
      {
         for (String ext : defExt)
         {
            if (tryExt(parser, useKpsewhich, useL3SearchPath, root, parent,
                  split, baseName, ext))
            {
               return;
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

      if (!file.exists() && parser != null)
      {
         if (useL3SearchPath && !root.isAbsolute())
         {
            File f = trySearchPath(parser, split);

            if (f != null)
            {
               base = f.getParentFile().toPath();
               relative = base.relativize(f.toPath());
            }
         }

         if (useKpsewhich)
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
   }

   protected boolean tryExt(TeXParser parser,
      boolean useKpsewhich, boolean useL3SearchPath,
      File root, File parent, String[] split, String baseName, String ext)
   throws IOException
   {
      int n = split.length-1;

      if (ext == null || ext.isEmpty())
      {
         split[n] = baseName;
      }
      else
      {
         split[n] = baseName+"."+ext;
      }

      File file = (parent == null ? new File(split[n]) : 
        new File(parent, split[n]));

      // Does file exist?

      if (file.exists())
      {
         // if no extension and file is a directory, skip

         if (file.isDirectory() && (ext == null || ext.isEmpty()))
         {
            return false;
         }

         if (base == null)
         {
            relative = file.toPath();
         }
         else
         {
            relative = base.relativize(file.toPath());
         }

         return true;
      }

      if (parser != null)
      {
         if (useL3SearchPath && !root.isAbsolute())
         {
            File f = trySearchPath(parser, split);

            if (f != null)
            {
               base = f.getParentFile().toPath();
               relative = base.relativize(f.toPath());
            }
         }

         if (useKpsewhich)
         {
            // Can kpsewhich find the file?

            try
            {
               String loc = parser.getListener().getTeXApp().kpsewhich(split[n]);

               if (loc != null && !loc.isEmpty())
               {
                  foundByKpsewhich = true;

                  init(parser, loc, false, "");

                  return true;
               }
            }
            catch (IOException|InterruptedException e)
            {
               // kpsewhich couldn't find the file
            }
         }
      }

      return false;
   }

   protected File trySearchPath(TeXParser parser, String[] split)
     throws IOException
   {
      // has a search path been provided?

      ControlSequence cs = parser.getControlSequence(
         "l_file_search_path_seq");

      if (cs instanceof SequenceCommand)
      {
         for (TeXObject item : ((SequenceCommand)cs).getContent())
         {
            String itemStr = parser.expandToString(
              (TeXObject)item.clone(), null);

            if (!itemStr.equals(".") && !itemStr.isEmpty())
            {
               String[] itemSplit = itemStr.split("/");

               File itemRoot = new File(
                 itemSplit.length == 1 ?
                   itemSplit[0] : itemSplit[0]+File.separator);

               File f = (itemRoot.isAbsolute() ? itemRoot : null);

               for (int j = (f == null ? 0 : 1);
                    j < itemSplit.length; j++)
               {
                  if (!itemSplit[j].isEmpty())
                  {
                     f = new File(f, itemSplit[j]);
                  }
               }

               for (int j = 0; j < split.length; j++)
               {
                  if (!split[j].isEmpty())
                  {
                     f = new File(f, split[j]);
                  }
               }

               if (f.exists())
               {
                  return f;
               }
            }
         }
      }

      return null;
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

   // Use getRelativePath() instead
   @Deprecated
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

   /**
    * Gets the file encoding or null if not known. Assume default,
    * if null.
    */ 
   public Charset getEncoding()
   {
      return charset;
   }

   /**
    * Sets the file encoding.
    */ 
   public void setEncoding(Charset charset)
   {
      this.charset = charset;
   }

   private Path base, relative;

   private boolean foundByKpsewhich = false;

   private Charset charset; // encoding if known
}
