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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.primitives.IfFalse;

public class DoListLoop extends ControlSequence
{
   public DoListLoop()
   {
      this("dolistloop", false, true);
   }

   public DoListLoop(String name, boolean isCsname, boolean useDo)
   {
      super(name);
      this.isCsname = isCsname;
      this.useDo = useDo;
   }

   @Override
   public Object clone()
   {
      return new DoListLoop(getName(), isCsname, useDo);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject handler;

      if (useDo)
      {
         handler = parser.getListener().getControlSequence("do");
      }
      else
      {
         handler = popArg(parser, stack);
      }

      ControlSequence cs;

      if (isCsname)
      {
         String csname = popLabelString(parser, stack);

         cs = parser.getListener().getControlSequence(csname);
      }
      else
      {
         cs = popControlSequence(parser, stack);

         if (cs instanceof TeXCsRef)
         {
            cs = parser.getListener().getControlSequence(cs.getName());
         }
      }

      TeXObject defn = cs;

      if (cs instanceof AssignedControlSequence)
      {
         defn = ((AssignedControlSequence)cs).getBaseUnderlying();
      }

      if (defn instanceof GenericCommand)
      {
         defn = ((GenericCommand)defn).getDefinition();
      }
      else
      {
         defn = TeXParserUtils.expandOnce(defn, parser, stack);
      }

      if (defn instanceof TeXObjectList && ((TeXObjectList)defn).isStack()
          && ((TeXObjectList)defn).size() == 1)
      {
         defn = ((TeXObjectList)defn).firstElement();
      }

      EtoolboxList elist;

      if (defn instanceof EtoolboxList)
      {
         elist = (EtoolboxList)defn;
      }
      else
      {
         elist = new EtoolboxList();
         elist.add(defn);
      }

      // This isn't the same as etoolbox

      boolean orgBreak = false;

      ControlSequence boolCs = parser.getControlSequence("if@etoolbox@listbreak");

      if (boolCs != null)
      {
         TeXBoolean bool = TeXParserUtils.toBoolean(boolCs, parser);
         orgBreak = bool.booleanValue();
      }

      parser.putControlSequence(true, new IfFalse("if@etoolbox@listbreak"));

      for (int i = 0; i < elist.size(); i++)
      {
         TeXObjectList expanded = parser.getListener().createStack();

         expanded.add(handler);
         Group grp = parser.getListener().createGroup();
         expanded.add(grp);

         grp.add((TeXObject)elist.get(i).clone());

         if (parser == stack || stack == null)
         {
            expanded.process(parser);
         }
         else
         {
            expanded.process(parser, stack);
         }

         TeXBoolean bool = TeXParserUtils.toBoolean("if@etoolbox@listbreak", parser);

         if (bool.booleanValue())
         {
            break;
         }
      }

      parser.putControlSequence(true,
        orgBreak ? new IfTrue("if@etoolbox@listbreak") 
          : new IfFalse("if@etoolbox@listbreak"));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private boolean useDo, isCsname;
}
