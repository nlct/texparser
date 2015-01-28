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
import java.io.File;

import com.dickimawbooks.texparserlib.TeXParser;

public class LaTeXSyntaxException extends IOException
{
   public LaTeXSyntaxException(int errorCode)
   {
      this(null, -1, errorCode, null);
   }

   public LaTeXSyntaxException(File file, int errorCode)
   {
      this(file, -1, errorCode, null);
   }

   public LaTeXSyntaxException(int lineNumber, int errorCode)
   {
      this(null, lineNumber, errorCode, null);
   }

   public LaTeXSyntaxException(File file, int lineNumber, int errorCode)
   {
      this(file, lineNumber, errorCode, null);
   }

   public LaTeXSyntaxException(int errorCode, String param)
   {
      this(null, -1, errorCode, param);
   }

   public LaTeXSyntaxException(File file, int errorCode, String param)
   {
      this(file, -1, errorCode, param);
   }

   public LaTeXSyntaxException(int lineNumber, int errorCode, String param)
   {
      this(null, lineNumber, errorCode, param);
   }

   public LaTeXSyntaxException(TeXParser parser, int errorCode, String param)
   {
      this(parser.getCurrentFile(), 
           parser.getLineNumber(),
           errorCode,
           param);
   }

   public LaTeXSyntaxException(TeXParser parser, int errorCode)
   {
      this(parser.getCurrentFile(), 
           parser.getLineNumber(),
           errorCode,
           null);
   }

   public LaTeXSyntaxException(File file, int lineNumber, int errorCode, String param)
   {
      super("LaTeX syntax error code "+errorCode);
      this.file = file;
      this.errorCode = errorCode;
      this.param = param;
      this.lineNum = lineNumber;
   }

   public int getErrorCode()
   {
      return errorCode;
   }

   public String getParam()
   {
      return param;
   }

   public void setLineNumber(int number)
   {
      lineNum = number;
   }

   public int getLineNumber()
   {
      return lineNum;
   }

   public File getFile()
   {
      return file;
   }

   public void setFile(File file)
   {
      this.file = file;
   }

   private int errorCode;
   private String param;
   private int lineNum;
   private File file;

   public static final int ERROR_MULTI_BEGIN_DOC=1,
      ERROR_NO_BEGIN_DOC=2, ERROR_MULTI_CLS=3,
      ERROR_MISSING_KEY=4, ERROR_EXTRA_END=5,
      ERROR_UNACCESSIBLE=6,
      ERROR_DEFINED=7;
}
