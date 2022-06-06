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

public class GlsXtrFieldListAdd extends ControlSequence
{
   public GlsXtrFieldListAdd()
   {
      this("glsxtrfieldlistadd", false, false);
   }

   public GlsXtrFieldListAdd(String name, boolean isGlobal, boolean expandItem)
   {
      super(name);
      this.isGlobal = isGlobal;
      this.expandItem = expandItem;
   }

   public Object clone()
   {
      return new GlsXtrFieldListAdd(getName(), isGlobal, expandItem);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      String label = popLabelString(parser, stack);
      String field = popLabelString(parser, stack);

      String csname;

      if (isGlobal)
      {
         if (expandItem)
         {
            csname = "listcsxadd";
         }
         else
         {
            csname = "listcsgadd";
         }
      }
      else if (expandItem)
      {
         csname = "listcseadd";
      }
      else
      {
         csname = "listcsadd";
      }

      ControlSequence cs = parser.getListener().getControlSequence(csname);

      TeXObjectList substack = parser.getListener().createStack();

      substack.add(cs);
      substack.add(parser.getListener().createGroup(
        String.format("glo@%s@%s", label, field)));

      if (parser == stack || stack == null)
      {
         substack.process(parser);
      }
      else
      {
         substack.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   protected boolean isGlobal, expandItem;
}
