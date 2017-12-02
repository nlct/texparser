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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class ListItem extends ControlSequence
{
   public ListItem()
   {
      this("item");
   }

   public ListItem(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new ListItem(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject label = stack.popArg(parser, '[', ']');

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TrivListDec trivList; 

      try
      {
         trivList = listener.peekTrivListStack();
      }
      catch (java.util.EmptyStackException e)
      {
         throw new LaTeXSyntaxException(e, parser, 
           LaTeXSyntaxException.ERROR_LONELY_ITEM);
      }

      Group grp = listener.createGroup();

      grp.add(setuplabel(parser, label));

      makelabel(parser, trivList, grp);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObject label = parser.popNextArg('[', ']');

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TrivListDec trivList; 

      try
      {
         trivList = listener.peekTrivListStack();
      }
      catch (java.util.EmptyStackException e)
      {
         throw new LaTeXSyntaxException(e, parser, 
           LaTeXSyntaxException.ERROR_LONELY_ITEM);
      }

      Group grp = listener.createGroup();

      grp.add(setuplabel(parser, label));

      makelabel(parser, trivList, grp);
   }

   public TeXObject setuplabel(TeXParser parser, TeXObject label)
    throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (label != null)
      {
         return label;
      }

      if (listener.isIfTrue(listener.getControlSequence("if@nmbrlist")))
      {
         TeXObject cs = listener.getControlSequence("@listctr");

         if (cs instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)cs).expandfully(parser);

            if (expanded != null)
            {
               cs = expanded;
            }
         }

         listener.stepcounter(cs.toString(parser));
      } 

      return listener.getControlSequence("@itemlabel");
   }

   public void makelabel(TeXParser parser, TrivListDec trivList, TeXObject label)
    throws IOException
   {
      label.process(parser);
   }
}
