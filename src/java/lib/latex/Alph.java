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

public class Alph extends Command
{
   public Alph()
   {
      this("Alph", AtAlph.UPPER);
   }

   public Alph(String name, byte state)
   {
      super(name);

      if (state != AtAlph.UPPER && state != AtAlph.LOWER)
      {
         throw new IllegalArgumentException(String.format(
          "Invalid state '%d' for Alph ", state));
      }

      this.state = state;
   }

   public Object clone()
   {
      return new Alph(getName(), state);
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

      TeXObjectList list = new TeXObjectList();

      if (state == AtAlph.UPPER)
      {
         list.add(new TeXCsRef("@Alph"));
      }
      else
      {
         list.add(new TeXCsRef("@alph"));
      }

      list.add(new TeXCsRef("c@"+name));

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

      TeXObjectList list = new TeXObjectList();

      if (state == AtAlph.UPPER)
      {
         list.add(new TeXCsRef("@Alph"));
      }
      else
      {
         list.add(new TeXCsRef("@alph"));
      }

      list.add(new TeXCsRef("c@"+name));

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
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

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      int value = listener.getcountervalue(counter.toString(parser));

      TeXObjectList list = new TeXObjectList();

      list.add(AtAlph.getSymbol(parser, value, state));

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

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      int value = listener.getcountervalue(counter.toString(parser));

      TeXObjectList list = new TeXObjectList();

      list.add(AtAlph.getSymbol(parser, value, state));

      return list;
   }

   public void process(TeXParser parser, TeXObjectList stack)
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

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      int value = listener.getcountervalue(counter.toString(parser));

      AtAlph.getSymbol(parser, value, state).process(parser, stack);
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

      AtAlph.getSymbol(parser, value, state).process(parser);
   }

   private byte state = AtAlph.UPPER;
}
