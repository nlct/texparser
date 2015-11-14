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

/**
 * Plain forenames [von] surname[, suffix] format
 */

public class PlainContributorFormat implements ContributorFormat
{
   public PlainContributorFormat()
   {
   }

   public String format(Contributor contributor)
   {
      StringBuilder builder = new StringBuilder();

      String forenames = contributor.getForenames();
      String von = contributor.getVonPart();
      String surname = contributor.getSurname();
      String suffix = contributor.getSuffix();

      if (forenames != null)
      {
         builder.append(forenames);
      }

      if (von != null)
      {
         if (builder.length() != 0)
         {
            builder.append(" ");
         }

         builder.append(von);
      }

      if (surname != null)
      {
         if (builder.length() != 0)
         {
            builder.append(" ");
         }

         builder.append(surname);
      }

      if (suffix != null)
      {
         if (builder.length() != 0)
         {
            builder.append(", ");
         }

         builder.append(suffix);
      }

      return builder.toString();
   }
}
