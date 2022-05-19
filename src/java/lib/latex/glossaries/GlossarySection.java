/*
    Copyright (C) 2022 Nicola L.C. Talbot
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

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlossarySection extends ControlSequence
{
   public GlossarySection(String section, 
       boolean isNumbered, boolean isAutoLabel)
   {
      this("glossarysection", section, isNumbered, isAutoLabel);
   }

   public GlossarySection(String name, String section, 
       boolean isNumbered, boolean isAutoLabel)
   {
      super(name);
      this.section = section;
      this.isNumbered = isNumbered;
      this.isAutoLabel = isAutoLabel;
   }

   public Object clone()
   {
      return new GlossarySection(getName(), section, isNumbered, isAutoLabel);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (isAutoLabel)
      {
         TeXObject tocTitle = popOptArg(parser, stack);
         TeXObject title = popArg(parser, stack);

         Group grp = parser.getListener().createGroup();
         grp.add(parser.getListener().getControlSequence("glsautoprefix"));
         grp.add(parser.getListener().getControlSequence("@glo@type"));

         stack.push(grp);
         stack.push(parser.getListener().getControlSequence("label"));

         grp = parser.getListener().createGroup();
         grp.add(title);
         stack.push(grp);

         if (tocTitle != null)
         {
            stack.push(parser.getListener().getOther(']'));
            stack.push(tocTitle);
            stack.push(parser.getListener().getOther('['));
         }
      }

      if (!isNumbered)
      {
         stack.push(parser.getListener().getOther('*'));
      }

      stack.push(parser.getListener().getControlSequence(section));
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private String section;
   private boolean isNumbered;
   private boolean isAutoLabel;
}
