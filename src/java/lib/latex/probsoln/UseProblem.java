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
package com.dickimawbooks.texparserlib.latex.probsoln;

import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class UseProblem extends ControlSequence
{
   public UseProblem(ProbSolnSty sty)
   {
      this("useproblem", sty);
   }

   public UseProblem(String name, ProbSolnSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new UseProblem(getName(), sty);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String db = "default";

      TeXObject dataset = stack.popArg(parser, '[', ']');

      if (dataset != null)
      {
         TeXObjectList expanded = null;

         if (dataset instanceof Expandable)
         {
            expanded = ((Expandable)dataset).expandfully(parser, stack);

            if (expanded != null)
            {
               dataset = expanded;
            }
         }

         db = dataset.toString(parser);
      }

      TeXObject object = stack.expandedPopStack(parser);

      if (object instanceof Group)
      {
         object = ((Group)object).toList();
      }

      sty.getProblem(object.toString(parser), db).process(parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      String db = "default";

      TeXObject dataset = parser.popNextArg('[', ']');

      if (dataset != null)
      {
         TeXObjectList expanded = null;

         if (dataset instanceof Expandable)
         {
            expanded = ((Expandable)dataset).expandfully(parser);

            if (expanded != null)
            {
               dataset = expanded;
            }
         }

         db = dataset.toString(parser);
      }

      TeXObject object = parser.expandedPopStack(TeXObjectList.POP_SHORT);

      if (object instanceof Group)
      {
         object = ((Group)object).toList();
      }

      sty.getProblem(object.toString(parser), db).process(parser);
   }

   private ProbSolnSty sty;

}
