/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.auxfile;

import com.dickimawbooks.texparserlib.*;

/**
 * Cite information obtained from <code>\bibcite</code>.
 */

public class CiteInfo
{
   protected CiteInfo(String label)
   {
      this.label = label;
   }

   public static CiteInfo createCite(AuxData auxData, TeXParser parser)
   {
      String label = auxData.getArg(0).toString(parser);

      CiteInfo info = new CiteInfo(label);

      info.reference = auxData.getArg(1);

      return info;
   }

   public String getLabel()
   {
      return label;
   }

   public TeXObject getReference()
   {
      return reference;
   }

   protected String label;
   protected TeXObject reference;
}
