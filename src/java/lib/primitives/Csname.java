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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class Csname extends Primitive implements Expandable
{
   public Csname()
   {
      this("csname");
   }

   public Csname(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Csname(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      String name = csname(parser, stack);

      ControlSequence cs = parser.getControlSequence(name);

      if (cs == null)
      {
         list.add(new TeXCsRef(name));
      }
      else
      {
         list.add(cs);
      }

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      ControlSequence cs = parser.getControlSequence(csname(parser, stack));

      if (cs == null)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(parser.getListener().getControlSequence("relax"));
         return list;
      }
      else if (cs instanceof Expandable)
      {
         return ((Expandable)cs).expandfully(parser, stack);
      }

      TeXObjectList list = new TeXObjectList();
      list.add(cs);
      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      ControlSequence cs = parser.getControlSequence(csname(parser, parser));

      if (cs == null)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(parser.getListener().getControlSequence("relax"));
         return list;
      }
      else if (cs instanceof Expandable)
      {
         return ((Expandable)cs).expandfully(parser);
      }

      TeXObjectList list = new TeXObjectList();
      list.add(cs);
      return list;
   }

   public String csname(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      TeXObject obj = stack.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);

      while (!(obj instanceof ControlSequence
                && ((ControlSequence)obj).getName().equals("endcsname")))
      {
         if (obj == null)
         {
            throw new TeXSyntaxException(new NullPointerException(), parser, 
             TeXSyntaxException.ERROR_EXPECTED, "\\endcsname");
         }

         list.add(obj);
         obj = stack.popStack(parser);
      }

      TeXObjectList expanded;

      if (parser == stack)
      {
         expanded = list.expandfully(parser);
      }
      else
      {
         expanded = list.expandfully(parser, stack);
      }

      if (expanded == null)
      {
         expanded = list;
      }

      return list.toString(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      ControlSequence cs = parser.getControlSequence(csname(parser, stack));

      if (cs != null)
      {
         cs.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      ControlSequence cs = parser.getControlSequence(
         csname(parser, parser));

      if (cs != null)
      {
         cs.process(parser);
      }
   }
}
