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

public class TeXSyntaxException extends IOException
{
   public TeXSyntaxException(int errorCode)
   {
      this(null, -1, errorCode, null);
   }

   public TeXSyntaxException(File file, int errorCode)
   {
      this(file, -1, errorCode, null);
   }

   public TeXSyntaxException(int lineNumber, int errorCode)
   {
      this(null, lineNumber, errorCode, null);
   }

   public TeXSyntaxException(File file, int lineNumber, int errorCode)
   {
      this(file, lineNumber, errorCode, null);
   }

   public TeXSyntaxException(int errorCode, String param)
   {
      this(null, -1, errorCode, param);
   }

   public TeXSyntaxException(File file, int errorCode, String param)
   {
      this(file, -1, errorCode, param);
   }

   public TeXSyntaxException(int lineNumber, int errorCode, String param)
   {
      this(null, lineNumber, errorCode, param);
   }

   public TeXSyntaxException(TeXParser parser, int errorCode, String param)
   {
      this(parser.getListenerFile(),
           parser.getLineNumber(), 
           errorCode,
           param);
   }

   public TeXSyntaxException(TeXParser parser, int errorCode)
   {
      this(parser.getListenerFile(),
           parser.getLineNumber(), 
           errorCode,
           null);
   }

   public TeXSyntaxException(File file, int lineNumber, int errorCode, String param)
   {
      super("TeX syntax error code "+errorCode);
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
   private int lineNum = -1;
   private File file = null;

   public static final int ERROR_BAD_PARAM = 0, ERROR_NO_EG = 1,
     ERROR_PAR_BEFORE_EG = 2, ERROR_UNEXPECTED_EG = 3,
     ERROR_MISSING_ENDMATH = 4, ERROR_DOLLAR2_ENDED_WITH_DOLLAR = 5,
     ERROR_NOT_FOUND = 6, ERROR_MISSING_PARAM = 7,
     ERROR_NOT_MATH_MODE=8, ERROR_INVALID_ACCENT=9,
     ERROR_AMBIGUOUS_MIDCS=10, ERROR_MISSING_CLOSING=11,
     ERROR_DIMEN_EXPECTED=12, ERROR_MISSING_UNIT=13,
     ERROR_EXPECTED=14, ERROR_UNDEFINED=15,
     ERROR_CS_EXPECTED=16, ERROR_NUMBER_EXPECTED=17,
     ERROR_REGISTER_UNDEF=18;

}
