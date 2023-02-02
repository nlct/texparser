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
package com.dickimawbooks.texparserlib.latex.probsoln;

import java.io.IOException;

import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;
import com.dickimawbooks.texparserlib.TeXParser;

public class ProbSolnException extends LaTeXSyntaxException
{
   public ProbSolnException(TeXParser parser, String errorTag, Object... params)
   {
      super(parser, errorTag, params);
   }

   public ProbSolnException(Throwable cause, TeXParser parser, String errorTag,
      Object... params)
   {
      super(cause, parser, errorTag, params);
   }

   public static final String ERROR_NO_SUCH_DB = "probsoln.no_such_db";
   public static final String ERROR_NO_SUCH_ENTRY_IN_DB = 
      "probsoln.no_such_entry_in_db";
   public static final String ERROR_DB_EXISTS = "probsoln.db_exists";
}
