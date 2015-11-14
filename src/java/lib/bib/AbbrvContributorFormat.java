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
 * Abbreviated initials [von] surname[, suffix] format
 */

public class AbbrvContributorFormat implements ContributorFormat
{
   public AbbrvContributorFormat()
   {
      this(".", " ");
   }

   public AbbrvContributorFormat(String interInitials, String sep)
   {
      this.interInitials = interInitials;
      this.sep = sep;
   }

   public Object clone()
   {
      return new AbbrvContributorFormat(interInitials, sep);
   }

   public String getInitials(Contributor contributor)
   {
      String forenames = contributor.getForenames();

      if (forenames == null) return null;

      return forenames.replaceAll("\\b(\\p{javaUpperCase})\\p{javaLowerCase}+", "$1"+interInitials);
   }

   public String format(Contributor contributor)
   {
      StringBuilder builder = new StringBuilder();

      String forenames = getInitials(contributor);
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
            builder.append(sep);
         }

         builder.append(von);
      }

      if (surname != null)
      {
         if (builder.length() != 0)
         {
            builder.append(sep);
         }

         builder.append(surname);
      }

      if (suffix != null)
      {
         if (builder.length() != 0)
         {
            builder.append(","+sep);
         }

         builder.append(suffix);
      }

      return builder.toString();
   }

   private String interInitials, sep;
}
