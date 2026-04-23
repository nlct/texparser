/*
    Copyright (C) 2026 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.glossaries;

import com.dickimawbooks.texparserlib.*;

public class GlossaryGroup
{
   public GlossaryGroup(String refLabel, String glossaryType,
     int level, String parentLabel, String groupLabel, TeXObject groupTitle)
   {
      this.refLabel = refLabel;
      this.glossaryType = glossaryType;
      this.level = level;
      this.parentLabel = parentLabel;
      this.groupLabel = groupLabel;
      this.groupTitle = groupTitle;
   }

   public String getRefLabel()
   {
      return refLabel;
   }

   public boolean hasRefLabel()
   {
      return !refLabel.isEmpty();
   }

   public String getGlossaryType()
   {
      return glossaryType;
   }

   public String getParentLabel()
   {
      return parentLabel;
   }

   public boolean hasParent()
   {
      return !parentLabel.isEmpty();
   }

   public int getLevel()
   {
      return level;
   }

   public String getGroupLabel()
   {
      return groupLabel;
   }

   public TeXObject getGroupTitle()
   {
      return groupTitle;
   }

   public boolean hasGroupTitle()
   {
      return !groupTitle.isEmpty();
   }

   String refLabel, glossaryType, parentLabel, groupLabel;
   TeXObject groupTitle;
   int level=0;
}
