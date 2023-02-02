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
package com.dickimawbooks.texparserlib.latex.bpchem;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.Undefined;
import com.dickimawbooks.texparserlib.latex.*;

public class CNlabelnoref extends ControlSequence
{
   public CNlabelnoref()
   {
      this("CNlabelnoref");
   }

   public CNlabelnoref(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new CNlabelnoref(getName());
   }

   private void processArg(TeXParser parser, TeXObjectList stack, 
      TeXObject arg) throws IOException
   {
      String csname = "cnd@";

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser);

         if (expanded == null)
         {
            csname += arg.toString(parser);
         }
         else
         {
            csname += expanded.toString(parser);
         }
      }
      else
      {
         csname += arg.toString(parser);
      }

      ControlSequence cs = parser.getListener().getControlSequence(csname);

      if (!(cs instanceof Undefined))
      {
         return;
      }

      Group scope = parser.getListener().createGroup(); 

      scope.add(new TeXCsRef("refstepcounter"));
      scope.add(parser.getListener().createGroup("BPCno"));
      scope.add(new TeXCsRef("label"));

      Group grp = parser.getListener().createGroup("cn:");
      scope.add(grp);

      if (arg instanceof TeXObjectList)
      {
         grp.addAll((TeXObjectList)arg);
      }
      else
      {
         grp.add(arg);
      }

      if (parser == stack)
      {
         scope.process(parser);
      }
      else
      {
         scope.process(parser, stack);
      }

      parser.putControlSequence(false, new GenericCommand(csname, null, 
        parser.getListener().getLetter('x')));
   }

   public void process(TeXParser parser) throws IOException
   {
      processArg(parser, parser, parser.popNextArg());
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      processArg(parser, list, list.popArg(parser));
   }

}
