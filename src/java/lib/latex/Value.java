/*
    Copyright (C) 2020 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class Value extends Command
{
   public Value()
   {
      this("value");
   }

   public Value(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Value(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject counter = stack.popArg(parser);

      if (counter instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)counter).expandfully(parser, stack);

         if (expanded != null)
         {
            counter = expanded;
         }
      }

      String name = counter.toString(parser);

      NumericRegister reg = parser.getSettings().getNumericRegister("c@"+name);

      if (reg == null)
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_UNDEFINED_COUNTER, name);
      }

      TeXObjectList list = new TeXObjectList();

      list.add(reg);
   
      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
    throws IOException
   {
      TeXObject counter = parser.popNextArg();

      if (counter instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)counter).expandfully(parser);

         if (expanded != null)
         {
            counter = expanded;
         }
      }

      String name = counter.toString(parser);

      NumericRegister reg = parser.getSettings().getNumericRegister("c@"+name);

      if (reg == null)
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_UNDEFINED_COUNTER, name);
      }

      TeXObjectList list = new TeXObjectList();

      list.add(reg);
   
      return list;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject counter = parser.popNextArg();

      if (counter instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)counter).expandfully(parser, stack);

         if (expanded != null)
         {
            counter = expanded;
         }
      }

      String name = counter.toString(parser);

      NumericRegister reg = parser.getSettings().getNumericRegister("c@"+name);

      if (reg == null)
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_UNDEFINED_COUNTER, name);
      }

      TeXObjectList list = new TeXObjectList();

      list.add(new UserNumber(reg.number(parser)));
   
      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      TeXObject counter = parser.popNextArg();

      if (counter instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)counter).expandfully(parser);

         if (expanded != null)
         {
            counter = expanded;
         }
      }

      String name = counter.toString(parser);

      NumericRegister reg = parser.getSettings().getNumericRegister("c@"+name);

      if (reg == null)
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_UNDEFINED_COUNTER, name);
      }

      TeXObjectList list = new TeXObjectList();

      list.add(new UserNumber(reg.number(parser)));
   
      return list;
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      TeXObject counter = list.popArg(parser);

      if (counter instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)counter).expandfully(parser, list);

         if (expanded != null)
         {
            counter = expanded;
         }
      }

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      int value = listener.getcountervalue(counter.toString(parser));

      listener.getWriteable().write(""+value);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject counter = parser.popNextArg();

      if (counter instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)counter).expandfully(parser);

         if (expanded != null)
         {
            counter = expanded;
         }
      }

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      int value = listener.getcountervalue(counter.toString(parser));

      listener.getWriteable().write(""+value);
   }
}
