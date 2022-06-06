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

public class GlsXtrFieldDoListLoop extends ControlSequence
{
   public GlsXtrFieldDoListLoop()
   {
      this("glsxtrfielddolistloop", true);
   }

   public GlsXtrFieldDoListLoop(String name, boolean useDo)
   {
      super(name);
      this.useDo = useDo;
   }

   public Object clone()
   {
      return new GlsXtrFieldDoListLoop(getName(), useDo);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      String label = popLabelString(parser, stack);
      String field = popLabelString(parser, stack);

      String csname;

      if (useDo)
      {
         csname = "forlistcsloop";
      }
      else
      {
         csname = "dolistcsloop";
      }

      ControlSequence cs = parser.getListener().getControlSequence(csname);

      TeXObjectList substack = parser.getListener().createStack();

      substack.add(cs);

      if (!useDo)
      {
         substack.add(popControlSequence(parser, stack));
      }

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

   protected boolean useDo;
}
