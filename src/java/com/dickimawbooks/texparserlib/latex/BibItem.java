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

public class BibItem extends ControlSequence
{
   public BibItem()
   {
      this("bibitem");
   }

   public BibItem(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new BibItem(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject opt = stack.popArg(parser, '[', ']');

      TeXObject arg = stack.popArg(parser);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      pushPostItem(parser, stack, arg);

      if (opt == null)
      {
         TeXObject cs = listener.getControlSequence("@listctr");

         if (cs instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)cs).expandfully(parser, stack);

            if (expanded != null)
            {
               cs = expanded;
            }
         }

         String counter = cs.toString(parser);

         listener.stepcounter(counter);

         stack.push(listener.getControlSequence("c@"+counter));
         stack.push(listener.getControlSequence("number"));
      }
      else
      {
         stack.push(opt);
      }

      pushPreItem(parser, stack, arg);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObject opt = parser.popNextArg('[', ']');

      TeXObject arg = parser.popNextArg();

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      pushPostItem(parser, parser, arg);

      if (opt == null)
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

         String counter = cs.toString(parser);

         listener.stepcounter(counter);

         parser.push(listener.getControlSequence("c@"+counter));
         parser.push(listener.getControlSequence("number"));
      }
      else
      {
         parser.push(opt);
      }

      pushPreItem(parser, parser, arg);
   }

   protected void pushPostItem(TeXParser parser, TeXObjectList stack,
     TeXObject arg)
    throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (parser == stack || stack == null)
      {
         parser.push(listener.getOther(']'));
      }
      else
      {
         stack.push(listener.getOther(']'));
      }
   }

   protected void pushPreItem(TeXParser parser, TeXObjectList stack,
      TeXObject arg)
    throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (parser == stack || stack == null)
      {
         parser.push(listener.getOther('['));
      }
      else
      {
         stack.push(listener.getOther('['));
      }
   }
}
