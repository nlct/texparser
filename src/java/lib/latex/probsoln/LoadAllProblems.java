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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class LoadAllProblems extends ControlSequence
{
   public LoadAllProblems(ProbSolnSty sty)
   {
      this("loadallproblems", sty);
   }

   public LoadAllProblems(String name, ProbSolnSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new LoadAllProblems(getName(), sty);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject optArg = (stack==parser? parser.popNextArg('[', ']') 
        : stack.popArg(parser, '[', ']'));

      String dbName = "default";

      if (optArg != null)
      {
         if (optArg instanceof Expandable)
         {
            TeXObjectList expanded = null;

            if (stack == parser)
            {
               expanded = ((Expandable)optArg).expandfully(parser);
            }
            else
            {
               expanded = ((Expandable)optArg).expandfully(parser, stack);
            }

            if (expanded != null)
            {
               optArg = expanded;
            }
         }

         dbName = optArg.toString(parser);
      }

      TeXObject fileName = (stack==parser? parser.popNextArg()
        : stack.popArg(parser));

      if (fileName instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (stack == parser)
         {
            expanded = ((Expandable)fileName).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)fileName).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            fileName = expanded;
         }
      }

      parser.startGroup();

      parser.putControlSequence(true,// local
        new GenericCommand(true, "prob@currentdb", null,
           parser.getListener().createString(dbName)));

      parser.getListener().input(new TeXPath(parser, 
        fileName.toString(parser)));

      parser.endGroup();
   }


   private ProbSolnSty sty;

}
