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

public class LaTeXSyntaxException extends IOException
{
   public LaTeXSyntaxException(int errorCode)
   {
      this(-1, errorCode, null);
   }

   public LaTeXSyntaxException(int lineNumber, int errorCode)
   {
      this(lineNumber, errorCode, null);
   }

   public LaTeXSyntaxException(int errorCode, String param)
   {
      this(-1, errorCode, param);
   }

   public LaTeXSyntaxException(int lineNumber, int errorCode, String param)
   {
      super("LaTeX syntax error code "+errorCode);
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

   private int errorCode;
   private String param;
   private int lineNum;

   public static final int ERROR_MULTI_BEGIN_DOC=1,
      ERROR_NO_BEGIN_DOC=2, ERROR_MULTI_CLS=3,
      ERROR_MISSING_KEY=4, ERROR_EXTRA_END=5;
}
