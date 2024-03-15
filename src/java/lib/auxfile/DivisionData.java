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

import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

/**
 * Document division data.
 */

public class DivisionData
{
   public DivisionData(String unit, TeXObject prefix, TeXObject title,
     String target, TeXObject location)
   {
      if (unit == null)
      {
         throw new NullPointerException();
      }

      this.unit = unit;
      this.prefix = prefix;
      this.title = title;
      this.target = target;
      this.location = location;
   }

   public void addLabel(String label)
   {
      if (labels == null)
      {
         labels = new Vector<String>();
      }

      labels.add(label);
   }

   public String getLabel()
   {
      return labels == null || labels.isEmpty() ? null : labels.firstElement();
   }

   public boolean containsLabel(String label)
   {
      return labels == null ? false : labels.contains(label);
   }

   public void setTarget(String target)
   {
      this.target = target;
   }

   public String getTarget()
   {
      return target;
   }

   public void setUnit(String unit)
   {
      if (unit == null)
      {
         throw new NullPointerException();
      }

      this.unit = unit;
   }

   public String getUnit()
   {
      return unit;
   }

   public void setPrefix(TeXObject prefix)
   {
      this.prefix = prefix;
   }

   public TeXObject getPrefix()
   {
      return prefix;
   }

   public void setTitle(TeXObject title)
   {
      this.title = title;
   }

   public TeXObject getTitle()
   {
      return title;
   }

   public void setLocation(TeXObject location)
   {
      this.location = location;
   }

   public TeXObject getLocation()
   {
      return location;
   }

   /**
    * Set special information specific to the TeX Parser library.
    */ 
   public void setSpecial(Object special)
   {
      this.special = special;
   }

   /**
    * Gets special information specific to the TeX Parser library.
    */ 
   public Object getSpecial()
   {
      return special;
   }

   @Override
   public boolean equals(Object other)
   {
      if (this == other) return true;

      if (!(other instanceof DivisionData)) return false;

      DivisionData data = (DivisionData)other;

      if (!unit.equals(data.unit)) return false;

      if ((target != null && data.target == null)
       || (target == null && data.target != null))
      {
         return false;
      }

      if (target != null && !target.equals(data.target)) return false;

      if ((prefix != null && data.prefix == null)
       || (prefix == null && data.prefix != null))
      {
         return false;
      }

      if (prefix != null && !prefix.equals(data.prefix)) return false;

      if ((title != null && data.title == null)
       || (title == null && data.title != null))
      {
         return false;
      }

      if (title != null && !title.equals(data.title)) return false;

      if ((location != null && data.location == null)
       || (location == null && data.location != null))
      {
         return false;
      }

      if (location != null && !location.equals(data.location)) return false;

      if ((labels != null && data.labels == null)
       || (labels == null && data.labels != null))
      {
         return false;
      }

      return (labels == null || labels.equals(data.labels));
   }

   protected String target, unit;
   protected TeXObject prefix, title, location;

   protected Object special;

   protected Vector<String> labels;
}
