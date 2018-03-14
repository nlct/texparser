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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class JmlrTheorem extends Declaration
{
   public JmlrTheorem(LaTeXParserListener listener,
     String name, boolean isStar, String counter,
     TeXObject title, String outerCounter)
   {
      super(isStar ? name+"*" : name);
      setArgTypes("o");
      this.counter = counter;
      this.title = title;
      this.styleName = name;

      if (!isStar)
      {
         if (counter == null)
         {
            counter = name;
         }

         if (listener.getParser().getSettings().getRegister("c@"+counter) == null)
         {
            listener.newcounter(counter, outerCounter);
         }
      }
   }

   public JmlrTheorem(String name, String thmCounter, TeXObject title)
   {
      super(thmCounter == null ? name+"*" : name);
      setArgTypes("o");
      this.counter = thmCounter;
      this.title = title;
      this.styleName = name;
   }

   public Object clone()
   {
      return new JmlrTheorem(styleName, counter, (TeXObject)title.clone());
   }

   public TeXObjectList expandonce(TeXParser parser)
    throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (counter != null)
      {
         listener.stepcounter(counter);
      }

      TeXObject arg;

      if (parser == stack)
      {
         arg = parser.popNextArg('[', ']');
      }
      else
      {
         arg = stack.popArg(parser, '[', ']');
      }

      ControlSequence bodyFont = listener.getControlSequence(
        String.format("jmlr@thm@%s@body@font", styleName));
      ControlSequence headerFont = listener.getControlSequence(
        String.format("jmlr@thm@%s@header@font", styleName));
      ControlSequence sep = listener.getControlSequence(
        String.format("jmlr@thm@%s@sep", styleName));
      ControlSequence postHeader = listener.getControlSequence(
        String.format("jmlr@thm@%s@postheader", styleName));

      TeXObjectList list = new TeXObjectList();

      Group grp = listener.createGroup();
      list.add(grp);

      if (headerFont instanceof GenericCommand)
      {
         for (TeXObject obj : ((GenericCommand)headerFont).getDefinition())
         {
            grp.add((TeXObject)obj.clone());
         }
      }
      else
      {
         grp.add(headerFont);
      }

      grp.add((TeXObject)title.clone());

      if (counter != null)
      {
         grp.add(listener.getSpace());
         grp.add(new TeXCsRef("the"+counter));
      }

      if (arg != null)
      {
         grp.add(listener.getSpace());
         grp.add(listener.getOther('('));
         grp.add(arg);
         grp.add(listener.getOther(')'));
      }

      grp.add(postHeader);
      list.add(sep);
      list.add(bodyFont);
      list.add(listener.getControlSequence("ignorespaces"));

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ((LaTeXParserListener)parser.getListener()).startTheorem(styleName);

      TeXObjectList list = expandonce(parser, stack);

      if (parser == stack)
      {
         list.process(parser);
      }
      else
      {
         list.process(parser, stack);
      }
   }

   public void end(TeXParser parser) throws IOException
   {
      ((LaTeXParserListener)parser.getListener()).endTheorem(styleName);
   }

   public boolean isModeSwitcher()
   {
      return false;
   }

   public String toString()
   {
      return String.format("%s[name=%s,style=%s,counter=%s,title=%s]", getClass().getSimpleName(),
       getName(), styleName, counter, title);
   }

   private String counter, styleName;
   private TeXObject title;
}
