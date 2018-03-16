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
import com.dickimawbooks.texparserlib.primitives.Def;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

public class CsDef extends Def
{
   public CsDef()
   {
      this("csdef");
   }

   public CsDef(String name)
   {
      this(name, true, true, false);
   }

   public CsDef(String name, boolean isShort, boolean isLocal, boolean expand)
   {
      super(name, isShort, isLocal);
      this.expanddef = expand;
   }

   public Object clone()
   {
      return new CsDef(getName(), isShort(), isLocal(), expanddef);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject csname;

      if (stack == parser)
      {
         csname = parser.popNextArg();
      }
      else
      {
         csname = stack.popArg(parser);
      }

      if (csname instanceof Expandable)
      {
         TeXObjectList expanded;

         if (stack == parser)
         {
            expanded = ((Expandable)csname).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)csname).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            csname = expanded;
         }
      }

      TeXObjectList syntax = new TeXObjectList();
      TeXObject nextObject = stack.popStack(parser);

      while (!(nextObject instanceof Group))
      {
         syntax.add(nextObject);
         nextObject = stack.popStack(parser);
      }

      TeXObjectList definition = ((Group)nextObject).toList();

      if (expanddef)
      {
         TeXObjectList expanded;

         if (stack == parser)
         {
            expanded = definition.expandfully(parser);
         }
         else
         {
            expanded = definition.expandfully(parser, stack);
         }

         if (expanded != null)
         {
            definition = expanded;
         }
      }

      parser.putControlSequence(isLocal(),
        new GenericCommand(isShort(), csname.toString(parser),
             syntax, definition));
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   private boolean expanddef;
}
