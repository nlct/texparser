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

public class JmlrNewTheorem extends ControlSequence
{
   public JmlrNewTheorem(JmlrUtilsSty sty)
   {
      this(sty, "newtheorem");
   }

   public JmlrNewTheorem(JmlrUtilsSty sty, String name)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new JmlrNewTheorem(sty, getName());
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();
      boolean isStar = false;

      TeXObject arg;
      TeXObject counterArg=null;
      TeXObject outerCounterArg=null;

      if (parser == stack)
      {
         arg = parser.popNextArg();
      }
      else
      {
         arg = stack.popArg(parser);
      }

      if (arg instanceof CharObject && ((CharObject)arg).getCharCode() == '*')
      {
         isStar = true;

         if (parser == stack)
         {
            arg = parser.popNextArg();
         }
         else
         {
            arg = stack.popArg(parser);
         }
      }

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

      String envname = arg.toString(parser);

      if (!isStar)
      {
         if (parser == stack)
         {
            counterArg = parser.popNextArg('[', ']');
         }
         else
         {
            counterArg = stack.popArg(parser, '[', ']');
         }

         if (counterArg instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack)
            {
               expanded = ((Expandable)counterArg).expandfully(parser);
            }
            else
            {
               expanded = ((Expandable)counterArg).expandfully(parser, stack);
            }

            if (expanded != null)
            {
               counterArg = expanded;
            }
         }
      }

      TeXObject titleArg;

      if (parser == stack)
      {
         titleArg = parser.popNextArg();
      }
      else
      {
         titleArg = stack.popArg(parser);
      }

      if (!isStar)
      {
         if (parser == stack)
         {
            outerCounterArg = parser.popNextArg('[', ']');
         }
         else
         {
            outerCounterArg = stack.popArg(parser, '[', ']');
         }

         if (outerCounterArg instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack)
            {
               expanded = ((Expandable)outerCounterArg).expandfully(parser);
            }
            else
            {
               expanded = ((Expandable)outerCounterArg).expandfully(parser, stack);
            }

            if (expanded != null)
            {
               outerCounterArg = expanded;
            }
         }
      }

      sty.newtheorem(envname, 
        counterArg == null ? null : counterArg.toString(parser),
        titleArg,
        outerCounterArg == null ? null : outerCounterArg.toString(parser));
   }

   private JmlrUtilsSty sty;
}
