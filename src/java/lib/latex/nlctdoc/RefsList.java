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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class RefsList extends ControlSequence
{
   public RefsList(String name, TeXObject prefix, TeXObject sep,
     TeXObject lastSep)
   {
      super(name);
      this.prefix = prefix;
      this.sep = sep;
      this.lastSep = lastSep;
   }

   @Override
   public Object clone()
   {
      return new RefsList(getName(), prefix, sep, lastSep);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      boolean isStar = (popModifier(parser, stack, '*') == '*');
      String labelList = popLabelString(parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList content = listener.createStack();

      ControlSequence refCs = listener.getControlSequence("ref");
      Other star = listener.getOther('*');

      if (prefix != null)
      {
         if (prefix instanceof TeXObjectList)
         {
            content.add((TeXObject)prefix.clone(), true);
         }
         else
         {
            content.add(prefix);
         }
      }

      String[] labels = labelList.trim().split("\\s*,\\s*");

      for (int i = 0, j = labels.length-2; i < labels.length; i++)
      {
         content.add(refCs);

         if (isStar)
         {
            content.add(star);
         }

         content.add(listener.createGroup(labels[i]));

         if (i == j)
         {
            if (lastSep instanceof TeXObjectList)
            {
               content.add((TeXObject)lastSep.clone(), true);
            }
            else
            {
               content.add(lastSep);
            }
         }
         else if (i < j)
         {
            if (sep instanceof TeXObjectList)
            {
               content.add((TeXObject)sep.clone(), true);
            }
            else
            {
               content.add(sep);
            }
         }
      }

      TeXParserUtils.process(content, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   private TeXObject prefix, sep, lastSep;
}
