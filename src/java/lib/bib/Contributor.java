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
 * Author/editor contributor
 */

public class Contributor
{
   public Contributor()
   {
   }

   public Contributor(String forenames, String surname)
   {
      this(forenames, null, surname, null);
   }

   public Contributor(String forenames, String von, String surname, String suffix)
   {
      this.forenames = forenames;
      this.von = von;
      this.surname = surname;
      this.suffix = suffix;
   }

   public Contributor(TeXObject forenameList, TeXObject vonList, 
     TeXObject surnameList, TeXObject suffixList)
   {
      forenamesObject = forenameList;
      vonObject = vonList;
      surnameObject = surnameList;
      suffixObject = suffixList;

      if (forenameList != null)
      {
         forenames = forenameList.format();
      }

      if (vonList != null)
      {
         von = vonList.format();
      }

      if (surnameList != null)
      {
         surname = surnameList.format();
      }

      if (suffixList != null)
      {
         suffix = suffixList.format();
      }
   }

   public String getForenames()
   {
      return forenames;
   }

   public TeXObject getForenamesObject()
   {
      return forenamesObject;
   }

   public String getSurname()
   {
      return surname;
   }

   public TeXObject getSurnameObject()
   {
      return surnameObject;
   }

   public String getVonPart()
   {
      return von;
   }

   public TeXObject getVonPartObject()
   {
      return vonObject;
   }

   public String getSuffix()// Jr etc
   {
      return suffix;
   }

   public TeXObject getSuffixObject()
   {
      return suffixObject;
   }

   public String format()
   {
      StringBuilder builder = new StringBuilder();

      if (von != null && !von.isEmpty())
      {
         builder.append(von+" ");
      }

      if (surname != null)
      {
         builder.append(surname);
      }

      if (suffix != null && !suffix.isEmpty())
      {
         if (surname != null)
         {
            builder.append(", ");
         }

         builder.append(suffix);
      }

      if (forenames != null && !forenames.isEmpty())
      {
         if (surname != null || suffix != null)
         {
            builder.append(", ");
         }

         builder.append(forenames);
      }

      return builder.toString();
   }

   public String toString()
   {
      return String.format("%s[forenames=\"%s\",von=\"%s\",surname=\"%s\",suffix=\"%s\"]",
        getClass().getSimpleName(), forenames, von, surname, suffix);
   }

   public void setForenames(String name)
   {
      forenames = name;
   }

   public void setForenames(String name, TeXObject object)
   {
      forenames = name;
      forenamesObject = object;
   }

   public void setSurname(String name)
   {
      surname = name;
   }

   public void setSurname(String name, TeXObject object)
   {
      surname = name;
      surnameObject = object;
   }

   public void setVonPart(String name)
   {
      von = name;
   }

   public void setVonPart(String name, TeXObject object)
   {
      von = name;
      vonObject = object;
   }

   public void setSuffix(String name)
   {
      suffix = name;
   }

   public void setSuffix(String name, TeXObject object)
   {
      suffix = name;
      suffixObject = object;
   }

   private String forenames;
   private String surname;
   private String von;
   private String suffix;

   private TeXObject forenamesObject=null;
   private TeXObject surnameObject=null;
   private TeXObject vonObject=null;
   private TeXObject suffixObject=null;
}
