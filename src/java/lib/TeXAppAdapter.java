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

import java.nio.charset.Charset;

import java.nio.file.Files;
import java.nio.file.Path;

import java.io.*;

import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

/**
 * Provides a basic implementation of TeXApp. This doesn't
 * run any processes and the file copy method does nothing.
 * Errors and warnings are printed to STDERR and messages
 * are printed to STDOUT.
 *
 * You may want to override the isReadAccessAllowed
 * and isWriteAccessAllowed methods to check TeXLive's
 * openin_any and openout_any settings. This adapter simply
 * uses File.canRead() and File.canWrite().
 */

public class TeXAppAdapter implements TeXApp
{
   @Override
   public String kpsewhich(String arg)
     throws IOException,InterruptedException
   {
      return null;
   }

   @Override
   public void epstopdf(File epsFile, File pdfFile)
     throws IOException,InterruptedException
   {
   }

   @Override
   public void wmftoeps(File wmfFile, File epsFile)
     throws IOException,InterruptedException
   {
   }

   @Override
   public void convertimage(int inPage, String[] inOptions, File inFile,
     String[] outOptions, File outFile)
     throws IOException,InterruptedException
   {
   }

   @Override
   public void substituting(TeXParser parser, String original, String replacement)
   {
      message(original+" -> "+replacement);
   }

   @Override
   public String getMessage(String label, Object... params)
   {
      if (params.length == 0)
      {
         return label;
      }

      String msg = label;

      String pre = "[";

      for (int i = 0; i < params.length; i++)
      {
         msg += pre + (String)params[i];
         pre = ",";
      }

      msg += "]";

      return msg;
   }

   @Override
   public void message(String text)
   {
      System.out.println(text);
   }

   @Override
   public void warning(TeXParser parser, String message)
   {
      System.err.println(message);
   }

   @Override
   public void error(Exception e)
   {
      if (e instanceof TeXSyntaxException)
      {
         System.err.println(((TeXSyntaxException)e).getMessage(this));
      }
      else
      {
         e.printStackTrace();
      }
   }

   @Override
   public void progress(int percentage)
   {
   }

   @Override
   public void copyFile(File orgFile, File newFile)
     throws IOException,InterruptedException
   {
   }

   @Override
   public String requestUserInput(String message)
     throws IOException
   {
      return javax.swing.JOptionPane.showInputDialog(null, message);
   }

   @Override
   public boolean isReadAccessAllowed(TeXPath path)
   {
      return isReadAccessAllowed(path.getFile());
   }

   @Override
   public boolean isReadAccessAllowed(File file)
   {
      return file.canRead();
   }

   @Override
   public boolean isWriteAccessAllowed(TeXPath path)
   {
      return isWriteAccessAllowed(path.getFile());
   }

   @Override
   public boolean isWriteAccessAllowed(File file)
   {
      if (file.exists())
      {
         return file.canWrite();
      }

      File dir = file.getParentFile();

      if (dir != null)
      {
         return dir.canWrite();
      }

      return (new File(System.getProperty("user.dir"))).canWrite();
   }

   @Override
   public Charset getDefaultCharset()
   {
      return Charset.defaultCharset();
   }

   @Override
   public BufferedReader createBufferedReader(Path path,
     Charset charset) throws IOException, SecurityException
   {
      /*
       * The use of Files.newBufferedReader(Path,Charset)
       * can cause an exception when running inside a 
       * restricted container (see https://github.com/nlct/bib2gls/issues/30)
       * so first try the newer method but if that fails fallback
       * on the older method.
       */

      try
      {
         return Files.newBufferedReader(path, charset);
      }
      catch (Throwable e)
      {
         return new BufferedReader(
          new InputStreamReader(new FileInputStream(path.toFile()), charset));
      } 
   }

   @Override
   public BufferedWriter createBufferedWriter(Path path,
     Charset charset) throws IOException, SecurityException
   {
      try
      {
         return Files.newBufferedWriter(path, charset);
      }
      catch (Throwable e)
      {
         return new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(path.toFile()), charset));
      }
   }

   @Override
   public String getApplicationName()
   {
      return "TeX Parser Library";
   }

   @Override
   public String getApplicationVersion()
   {
      return TeXParser.VERSION;
   }

}
