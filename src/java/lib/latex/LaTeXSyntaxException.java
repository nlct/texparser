/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.TeXSyntaxException;
import com.dickimawbooks.texparserlib.TeXParser;

public class LaTeXSyntaxException extends TeXSyntaxException
{
   public LaTeXSyntaxException(TeXParser parser, String errorTag, 
      Object... params)
   {
      super(parser, errorTag, params);
   }

   public LaTeXSyntaxException(Throwable cause, TeXParser parser,
      String errorTag, Object... params)
   {
      super(cause, parser, errorTag, params);
   }

   public static final String ERROR_MULTI_BEGIN_DOC = 
      "latex.error.multi_begin_doc";
   public static final String ERROR_MISSING_BEGIN_DOC =
      "latex.error.missing_begin_doc";
   public static final String ERROR_NO_BEGIN_DOC = 
      "latex.error.no_begin_doc";
   public static final String ERROR_MULTI_CLS = 
      "latex.error.multi_cls";
   public static final String ERROR_MISSING_KEY = 
      "latex.error.missing_key";
   public static final String ERROR_MISSING_KEY_VALUE = 
      "latex.error.missing_key_value";
   public static final String ERROR_MISSING_OR = 
      "latex.error.missing_or";
   public static final String ERROR_EXTRA_END = 
      "latex.error.extra_end";
   public static final String ERROR_UNACCESSIBLE = 
      "latex.error.unaccessible";
   public static final String ERROR_DEFINED = 
      "latex.error.defined";
   public static final String ERROR_ILLEGAL_ARRAY_ARG_CHAR = 
      "latex.error.illegal_array_arg_char";
   public static final String ERROR_NO_ALIGNMENT =
      "latex.error.no_alignment";
   public static final String ERROR_UNDEFINED_COUNTER = 
      "latex.error.undefined_counter";
   public static final String ERROR_LONELY_ITEM = 
      "latex.error.lonely_item";
   public static final String PACKAGE_ERROR = 
      "latex.package.error";
   public static final String CLASS_ERROR = 
      "latex.class.error";
   public static final String ILLEGAL_ARG_TYPE = 
      "latex.illegal.argtype";
   public static final String ERROR_PACKAGE_NOT_LOADED = 
   "latex.package.not.loaded";
   public static final String ERROR_COUNTER_TOO_LARGE = 
   "latex.error.counter.too.large";
   public static final String ERROR_COUNTER_OUT_OF_RANGE = 
   "latex.error.counter.out.of.range";
   public static final String ERROR_UNSUPPORTED_XPARSE_TYPE =
   "latex.unsupported.xparse_type";
   public static final String ERROR_UNKNOWN_OPTION =
   "latex.unknown.option";
   public static final String ERROR_NOT_SEQUENCE =
   "latex.not.sequence";
   public static final String ERROR_NOT_TOKEN_LIST =
   "latex.not.tokenlist";
   public static final String ERROR_NOT_BOOLEAN =
   "latex.not.boolean";
   public static final String ERROR_TRAILING_CONTENT =
   "latex.trailing_content";
   public static final String ERROR_INVALID_OPTION_VALUE =
   "latex.invalid.option.value";
}
