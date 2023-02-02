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
import com.dickimawbooks.texparserlib.latex.*;

public class BPChem extends ControlSequence
{
   public BPChem()
   {
      this("BPChem");
   }

   public BPChem(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new BPChem(getName());
   }

   // \_ and \^ indicate subscripts and superscripts within
   // the argument of \BPChem

   protected TeXObject replaceCs(TeXParser parser, ControlSequence cs)
   {
      String name = cs.getName();

      if (name.equals("_"))
      {
         return parser.getListener().getControlSequence("textsubscript");
      }
      else if (name.equals("^"))
      {
         return parser.getListener().getControlSequence("textsuperscript");
      }

      return cs;
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject arg = parser.popNextArg();

      if (!(arg instanceof TeXObjectList))
      {
         arg.process(parser);
         return;
      }

      TeXObjectList argList = (TeXObjectList)arg;

      while (argList.size() > 0)
      {
         TeXObject obj = argList.popStack(parser);

         if (obj instanceof ControlSequence)
         {
            replaceCs(parser, (ControlSequence)obj).process(parser, argList);
         }
         else
         {
            obj.process(parser, argList);
         }
      }
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      TeXObject arg = list.popArg(parser);

      if (!(arg instanceof TeXObjectList))
      {
         arg.process(parser, list);
         return;
      }

      TeXObjectList argList = (TeXObjectList)arg;

      while (argList.size() > 0)
      {
         TeXObject obj = argList.popStack(parser);

         if (obj instanceof ControlSequence)
         {
            replaceCs(parser, (ControlSequence)obj).process(parser, argList);
         }
         else
         {
            obj.process(parser, argList);
         }
      }
   }

}
