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

public interface TeXApp
{
   // Gets output of kpsewhich arg
   public String kpsewhich(String arg)
     throws IOException,InterruptedException;

   // Runs epstopdf --output=pdfFile epsFile
   public void epstopdf(File epsFile, File pdfFile)
     throws IOException,InterruptedException;

   // Runs wmftoeps -o epsFile wmfFile
   public void wmftoeps(File wmfFile, File epsFile)
     throws IOException,InterruptedException;

   public void substituting(TeXParser parser, String original, String replacement);

   public String getMessage(String label);

   public String getMessage(String label, String param);

   public String getMessage(String label, String[] params);

   public String requestUserInput(String message)
     throws IOException;

   public void message(String text);

   public void warning(TeXParser parser, String message);

   public void error(Exception e);

   public void copyFile(File orgFile, File newFile)
     throws IOException,InterruptedException;

   public static String MESSAGE_READING = "message.reading";
   public static String MESSAGE_WRITING = "message.writing";
}
