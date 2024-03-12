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

   public void setRef(String ref)
   {
      this.ref = ref;
   }

   public String getRef()
   {
      return ref;
   }

   protected String target, unit, ref;
   protected TeXObject prefix, title, location;

   protected Vector<String> labels;
}
