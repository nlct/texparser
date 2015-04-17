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
   public TeXSyntaxException(String errorTag)
   {
      this(null, -1, errorTag, (String)null);
   }

   public TeXSyntaxException(File file, String errorTag)
   {
      this(file, -1, errorTag, (String)null);
   }

   public TeXSyntaxException(int lineNumber, String errorTag)
   {
      this(null, lineNumber, errorTag, (String)null);
   }

   public TeXSyntaxException(File file, int lineNumber, String errorTag)
   {
      this(file, lineNumber, errorTag, (String)null);
   }

   public TeXSyntaxException(String errorTag, String param)
   {
      this(null, -1, errorTag, param);
   }

   public TeXSyntaxException(String errorTag, String[] params)
   {
      this(null, -1, errorTag, params);
   }

   public TeXSyntaxException(File file, String errorTag, String param)
   {
      this(file, -1, errorTag, param);
   }

   public TeXSyntaxException(File file, String errorTag, String[] params)
   {
      this(file, -1, errorTag, params);
   }

   public TeXSyntaxException(int lineNumber, String errorTag, String param)
   {
      this(null, lineNumber, errorTag, param);
   }

   public TeXSyntaxException(int lineNumber, String errorTag, String[] params)
   {
      this(null, lineNumber, errorTag, params);
   }

   public TeXSyntaxException(TeXParser parser, String errorTag, String param)
   {
      this(parser == null ? null : parser.getCurrentFile(),
           parser == null ? -1 : parser.getLineNumber(), 
           errorTag,
           param);
   }

   public TeXSyntaxException(TeXParser parser, String errorTag, String[] params)
   {
      this(parser == null ? null : parser.getCurrentFile(),
           parser == null ? -1 : parser.getLineNumber(), 
           errorTag,
           params);
   }

   public TeXSyntaxException(TeXParser parser, String errorTag,
      String param, Throwable cause)
   {
      this(parser == null ? null : parser.getCurrentFile(),
           parser == null ? -1 : parser.getLineNumber(), 
           errorTag,
           param,
           cause);
   }

   public TeXSyntaxException(TeXParser parser, String errorTag,
      String[] params, Throwable cause)
   {
      this(parser == null ? null : parser.getCurrentFile(),
           parser == null ? -1 : parser.getLineNumber(), 
           errorTag,
           params,
           cause);
   }

   public TeXSyntaxException(TeXParser parser, String errorTag)
   {
      this(parser == null ? null : parser.getCurrentFile(),
           parser == null ? -1 : parser.getLineNumber(), 
           errorTag,
           (String)null);
   }

   public TeXSyntaxException(TeXParser parser, String errorTag,
     Throwable cause)
   {
      this(parser == null ? null : parser.getCurrentFile(),
           parser == null ? -1 : parser.getLineNumber(), 
           errorTag,
           (String)null,
           cause);
   }

   public TeXSyntaxException(File file, int lineNumber, String errorTag,
     String[] params)
   {
      super("TeX syntax error code "+errorTag);
      this.file = file;
      this.errorTag = errorTag;
      this.params = params;
      this.lineNum = lineNumber;
   }

   public TeXSyntaxException(File file, int lineNumber, String errorTag,
       String param)
   {
      super("TeX syntax error code "+errorTag);
      this.file = file;
      this.errorTag = errorTag;
      this.lineNum = lineNumber;

      if (param != null)
      {
         this.params = new String[] {param};
      }
   }

   public TeXSyntaxException(File file, int lineNumber, String errorTag,
     String params[], Throwable cause)
   {
      super("TeX syntax error code "+errorTag, cause);
      this.file = file;
      this.errorTag = errorTag;
      this.params = params;
      this.lineNum = lineNumber;
   }

   public TeXSyntaxException(File file, int lineNumber, String errorTag,
     String param, Throwable cause)
   {
      super("TeX syntax error code "+errorTag, cause);
      this.file = file;
      this.errorTag = errorTag;
      this.lineNum = lineNumber;

      if (param != null)
      {
         this.params = new String[] {param};
      }
   }

   public String getErrorTag()
   {
      return errorTag;
   }

   public String[] getParams()
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
      String msg = 
        params == null ? app.getMessage(errorTag) 
                       : app.getMessage(errorTag, params);

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
   private String[] params;
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

}
