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

import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

public class TeXAppAdapter implements TeXApp
{
   public String kpsewhich(String arg)
     throws IOException,InterruptedException
   {
      return null;
   }

   public void epstopdf(File epsFile, File pdfFile)
     throws IOException,InterruptedException
   {
   }

   public void wmftoeps(File wmfFile, File epsFile)
     throws IOException,InterruptedException
   {
   }

   public void substituting(TeXParser parser, String original, String replacement)
   {
      message(original+" -> "+replacement);
   }

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

   public void message(String text)
   {
      System.out.println(text);
   }

   public void warning(TeXParser parser, String message)
   {
      System.err.println(message);
   }

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

   public void progress(int percentage)
   {
   }

   public void copyFile(File orgFile, File newFile)
     throws IOException,InterruptedException
   {
   }

   public String requestUserInput(String message)
     throws IOException
   {
      return javax.swing.JOptionPane.showInputDialog(null, message);
   }

   // These methods should be overridden to check for openin_any and
   // openout_any

   public boolean isReadAccessAllowed(TeXPath path)
   {
      return isReadAccessAllowed(path.getFile());
   }

   public boolean isReadAccessAllowed(File file)
   {
      return file.canRead();
   }

   public boolean isWriteAccessAllowed(TeXPath path)
   {
      return isWriteAccessAllowed(path.getFile());
   }

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

}
