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
   public TeXSyntaxException(int lineNumber, String errorTag)
   {
      this((File)null, lineNumber, errorTag);
   }

   public TeXSyntaxException(String errorTag, Object... params)
   {
      this((File)null, -1, errorTag, params);
   }

   public TeXSyntaxException(File file, String errorTag, Object... params)
   {
      this(file, -1, errorTag, params);
   }

   public TeXSyntaxException(int lineNumber, String errorTag, Object... params)
   {
      this((File)null, lineNumber, errorTag, params);
   }

   public TeXSyntaxException(TeXParser parser, String errorTag, Object... params)
   {
      this(parser == null ? null : parser.getCurrentFile(),
           parser == null ? -1 : parser.getLineNumber(), 
           errorTag,
           params);
   }

   public TeXSyntaxException(Throwable cause, TeXParser parser, String errorTag,
      Object... params)
   {
      this(cause, 
           parser == null ? null : parser.getCurrentFile(),
           parser == null ? -1 : parser.getLineNumber(), 
           errorTag,
           params);
   }

   public TeXSyntaxException(File file, int lineNumber, String errorTag,
     Object... params)
   {
      super("TeX syntax error code "+errorTag);
      this.file = file;
      this.errorTag = errorTag;
      this.params = params;
      this.lineNum = lineNumber;
   }

   public TeXSyntaxException(Throwable cause, File file, int lineNumber, 
     String errorTag, Object... params)
   {
      super("TeX syntax error code "+errorTag, cause);
      this.file = file;
      this.errorTag = errorTag;
      this.params = params;
      this.lineNum = lineNumber;
   }

   public String getErrorTag()
   {
      return errorTag;
   }

   public Object[] getParams()
   {
      return params;
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

   public String getMessage(TeXApp app)
   {
      String msg = app.getMessage(errorTag, params);

      if (file == null && lineNum == -1)
      {
         return msg;
      }
      else if (lineNum == -1)
      {
         return String.format("%s: %s", file.getName(), msg);
      }
      else if (file == null)
      {
         return String.format("l.%d: %s", lineNum, msg);
      }

      return String.format("%s:%d: %s", file.getName(), lineNum, msg);
   }

   private String errorTag;
   private Object[] params;
   private int lineNum = -1;
   private File file = null;

   public static final String ERROR_BAD_PARAM = "tex.error.bad_param";
   public static final String ERROR_NO_EG = "tex.error.no_eg";
   public static final String ERROR_PAR_BEFORE_EG = "tex.error.par_before_eg";
   public static final String ERROR_UNEXPECTED_EG = "tex.error.unexpected_eg";
   public static final String ERROR_MISSING_ENDMATH = 
      "tex.error.missing_endmath";
   public static final String ERROR_DOLLAR2_ENDED_WITH_DOLLAR = 
      "tex.error.dollar2_ended_with_dollar";
   public static final String ERROR_NOT_FOUND = "tex.error.not_found";
   public static final String ERROR_MISSING_PARAM = "tex.error.missing_param";
   public static final String ERROR_EMPTY_STACK = "tex.error.empty_stack";
   public static final String ERROR_NOT_MATH_MODE = "tex.error.not_math_mode";
   public static final String ERROR_INVALID_ACCENT = "tex.error.invalid_accent";
   public static final String ERROR_AMBIGUOUS_MIDCS = 
      "tex.error.ambiguous.mid_cs";
   public static final String ERROR_MISSING_CLOSING = 
      "tex.error.missing_closing";
   public static final String ERROR_DIMEN_EXPECTED = "tex.error.dimen_expected";
   public static final String ERROR_MISSING_UNIT = "tex.error.missing_unit";
   public static final String ERROR_EXPECTED = "tex.error.expected";
   public static final String ERROR_UNDEFINED = "tex.error.undefined";
   public static final String ERROR_UNDEFINED_CHAR = "tex.error.undefined_char";
   public static final String ERROR_CS_EXPECTED = "tex.error.cs_expected";
   public static final String ERROR_NUMBER_EXPECTED = 
      "tex.error.number_expected";
   public static final String ERROR_REGISTER_UNDEF = "tex.error.register_undef";
   public static final String ERROR_SYNTAX = "tex.error.syntax";
   public static final String ERROR_EXTRA = "tex.error.extra";
   public static final String ERROR_DOUBLE_SUBSCRIPT = 
      "tex.error.double_subscript";
   public static final String ERROR_DOUBLE_SUPERSCRIPT = 
      "tex.error.double_superscript";
   public static final String ERROR_MISPLACED_OMIT = 
      "tex.error.misplaced_omit";
   public static final String ERROR_ILLEGAL_ALIGN = 
      "tex.error.illegal_align";
   public static final String ERROR_IMPROPER_ALPHABETIC_CONSTANT =
      "tex.error.improper_alphabetic_constant";
   public static final String ERROR_EXTRA_OR_FORGOTTEN =
      "tex.error.extra_or_forgotten";
   public static final String ERROR_REGISTER_EXPECTED =
      "tex.error.register_expected";
   public static final String ERROR_REGISTER_EXPECTED_BUT_FOUND =
      "tex.error.register_expected_but_found";
   public static final String ERROR_NUMERIC_REGISTER_EXPECTED =
      "tex.error.numeric_register_expected";
   public static final String ERROR_REGISTER_NOT_NUMERIC =
      "tex.error.register_not_numeric";
   public static final String ERROR_REGISTER_NOT_TOKEN =
      "tex.error.register_not_token";
   public static final String ERROR_FILE_NOT_FOUND =
      "tex.error.file.not.found";
   public static final String ERROR_GENERIC =
      "tex.error.generic";
   public static final String ERROR_UNEXPANDABLE =
      "tex.error.unexpandable";
}
