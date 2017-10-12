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
package com.dickimawbooks.texparserlib.latex.tipa;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class TextIpa extends ControlSequence
{
   public TextIpa(TipaSty sty)
   {
      this("textipa", sty);
   }

   public TextIpa(String name, TipaSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new TextIpa(getName(), getSty());
   }

   protected void processArg(TeXObject arg, TeXParser parser, 
     TeXObjectList stack)
     throws IOException
   {
      if (arg instanceof CharObject)
      {
         int cp = getSty().fetch(((CharObject)arg).getCharCode());

         parser.getListener().getWriteable().writeCodePoint(cp);
      }
      else if (arg instanceof TeXObjectList
            && !(arg instanceof MathGroup))
      {
         TeXObjectList list = (TeXObjectList)arg;

         while (list.size() > 0)
         {
            TeXObject obj = list.popToken();

            processArg(obj, parser, list);
         }
      }
      else if (stack == null)
      {
         arg.process(parser);
      }
      else
      {
         arg.process(parser, stack);
      }
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = (parser==stack?parser.popNextArg():stack.popArg(parser));

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack)
         {
            expanded = ((Expandable)arg).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)arg).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      processArg(arg, parser, null);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public TipaSty getSty()
   {
      return sty;
   }

   private TipaSty sty;
}
