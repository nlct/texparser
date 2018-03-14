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
package com.dickimawbooks.texparserlib.latex.jmlr;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class JmlrSet extends ControlSequence
{
   public JmlrSet()
   {
      this("set");
   }

   public JmlrSet(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new JmlrSet(getName());
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject arg;

      if (parser == stack)
      {
         arg = parser.popNextArg();
      }
      else
      {
         arg = stack.popArg(parser);
      }

      ControlSequence cs = parser.getListener().getControlSequence("mathcal");

      Group grp = parser.getListener().createGroup();
      grp.add(arg);

      if (parser.isMathMode())
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         list.add(grp);

         if (parser == stack)
         {
            list.process(parser);
         }
         else
         {
            list.process(parser, stack);
         }
      }
      else
      {
         MathGroup mgrp = parser.getListener().createMathGroup();
         mgrp.setInLine(true);

         mgrp.add(cs);
         mgrp.add(grp);

         if (parser == stack)
         {
            mgrp.process(parser);
         }
         else
         {
            mgrp.process(parser, stack);
         }
      }
   }

}
