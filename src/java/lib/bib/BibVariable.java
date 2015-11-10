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
package com.dickimawbooks.texparserlib.bib;

import java.util.Vector;
import java.util.HashMap;

import com.dickimawbooks.texparserlib.*;

/**
 * Bib variable
 */

public class BibVariable implements BibValue
{
   public BibVariable(TeXObject string, String variableName)
   {
      this.string = string;
      this.variableName = variableName;
   }

   public TeXObject getContents()
   {
      return string;
   }

   public String getVariableName()
   {
      return variableName;
   }

   public String applyDelim(byte fieldDelimChange)
   {
      return string.format();
   }

   private TeXObject string;
   private String variableName;
}
