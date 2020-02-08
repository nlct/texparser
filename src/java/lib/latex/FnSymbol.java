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

public class FnSymbol extends Command
{
   public FnSymbol()
   {
      this("fnsymbol");
   }

   public FnSymbol(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new FnSymbol(getName());
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

      list.add(new TeXCsRef("@fnsymbol"));

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

      list.add(new TeXCsRef("@fnsymbol"));

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

      list.add(AtFnSymbol.getSymbol(parser, value));

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

      list.add(AtFnSymbol.getSymbol(parser, value));

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

      AtFnSymbol.getSymbol(parser, value).process(parser, stack);
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

      AtFnSymbol.getSymbol(parser, value).process(parser);
   }

}
