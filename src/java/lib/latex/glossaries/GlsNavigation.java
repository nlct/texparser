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

public class GlsNavigation extends ControlSequence
{
   public GlsNavigation()
   {
      this("glsnavigation");
   }

   public GlsNavigation(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsNavigation(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      ControlSequence cs = parser.getControlSequence("@glo@type");

      String type = "";

      if (cs != null)
      {
         type = parser.expandToString(cs, stack);
      }

      cs = parser.getControlSequence("@gls@hypergrouplist@"+type);

      String[] labels = null;

      if (cs != null)
      {
         labels = parser.expandToString(cs, stack).split(" *, *");
      }

      if (labels != null && labels.length > 0)
      {
         TeXParserListener listener = parser.getListener();

         TeXObjectList list = listener.createStack();

         for (int i = 0; i < labels.length; i++)
         {
            if (i > 1)
            {
               list.add(listener.getControlSequence("glshypernavsep"));
            }

            list.add(listener.getControlSequence("@gls@getgrouptitle"));
            list.add(listener.createGroup(labels[i]));
            list.add(new TeXCsRef("@gls@grptitle"));
            list.add(listener.getControlSequence("glsnavhyperlink"));
            list.add(listener.createGroup(labels[i]));
            list.add(new TeXCsRef("@gls@grptitle"));
         }

         if (parser == stack || stack == null)
         {
            list.process(parser);
         }
         else
         {
            list.process(parser, stack);
         }
      }
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }
}
