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
import com.dickimawbooks.texparserlib.primitives.Undefined;

public class IfDef extends ControlSequence implements Expandable
{
   public IfDef()
   {
      this("ifdef", false);
   }

   public IfDef(String name, boolean isCsname)
   {
      super(name);
      this.isCsname = isCsname;
   }

   @Override
   public Object clone()
   {
      return new IfDef(getName(), isCsname);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = null;

      if (isCsname)
      {
         String csname = popLabelString(parser, stack);
         cs = parser.getControlSequence(csname);
      }
      else
      {
         TeXObject obj = popArg(parser, stack);

         if (obj instanceof TeXCsRef)
         {
            String csname = ((TeXCsRef)obj).getName();
            cs = parser.getControlSequence(csname);
         }
         else if (obj instanceof Undefined)
         {
            cs = null;
         }
         else if (obj instanceof ControlSequence)
         {
            cs = (ControlSequence)obj;
         }
      }

      TeXObject truePart = popArg(parser, stack);
      TeXObject falsePart = popArg(parser, stack);

      if (cs != null)
      {
         if (parser.isStack(truePart))
         {
            return (TeXObjectList)truePart;
         }
         else
         {
            TeXObjectList list = new TeXObjectList();
            list.add(truePart);
            return list;
         }
      }
      else
      {
         if (parser.isStack(falsePart))
         {
            return (TeXObjectList)falsePart;
         }
         else
         {
            TeXObjectList list = new TeXObjectList();
            list.add(falsePart);
            return list;
         }
      }
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = null;

      if (isCsname)
      {
         String csname = popLabelString(parser, stack);
         cs = parser.getControlSequence(csname);
      }
      else
      {
         TeXObject obj = popArg(parser, stack);

         if (obj instanceof TeXCsRef)
         {
            String csname = ((TeXCsRef)obj).getName();
            cs = parser.getControlSequence(csname);
         }
         else if (obj instanceof Undefined)
         {
            cs = null;
         }
         else if (obj instanceof ControlSequence)
         {
            cs = (ControlSequence)obj;
         }
      }

      TeXObject truePart = popArg(parser, stack);
      TeXObject falsePart = popArg(parser, stack);

      TeXObject doCode;

      if (cs != null)
      {
         doCode = truePart;
      }
      else
      {
         doCode = falsePart;
      }

      if (stack == parser)
      {
         doCode.process(parser);
      }
      else
      {
         doCode.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private boolean isCsname;
}
