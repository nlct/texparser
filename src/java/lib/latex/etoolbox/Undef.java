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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Undef extends ControlSequence
{
   public Undef()
   {
      this("undef", false, false);
   }

   public Undef(String name, boolean isGlobal, boolean isCsname)
   {
      super(name);
      this.isGlobal = isGlobal;
      this.isCsname = isCsname;
   }

   public Object clone()
   {
      return new Undef(getName(), isGlobal, isCsname);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg;
      String csname;

      if (stack == parser)
      {
         arg = parser.popNextArg();
      }
      else
      {
         arg = stack.popArg(parser);
      }

      if (isCsname)
      {
         if (arg instanceof Expandable)
         {
            TeXObjectList expanded;

            if (stack == parser)
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

         csname = arg.toString(parser);
      }
      else if (arg instanceof ControlSequence)
      {
         csname = ((ControlSequence)arg).getName();
      }
      else if (arg instanceof ActiveChar)
      {
         parser.removeActiveChar(!isGlobal, ((ActiveChar)arg).getCharCode());
         return;
      }
      else
      {
         throw new LaTeXSyntaxException(parser,
           LaTeXSyntaxException.ERROR_UNACCESSIBLE,
           arg.toString(parser));
      }

      parser.removeControlSequence(!isGlobal, name);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private boolean isGlobal, isCsname;
}
