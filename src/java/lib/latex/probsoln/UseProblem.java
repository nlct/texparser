/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
      String db = popOptLabelString(parser, stack);

      if (db == null)
      {
         db = "default";
      }

      TeXObject object = popArg(parser, stack);

      ProbSolnData data;

      if (object instanceof ProblemLabel)
      {
         data = ((ProblemLabel)object).getEntry();
      }
      else
      {
         String label = parser.expandToString(object, stack);
         data = sty.getProblem(label, db);
      }

      if (parser == stack || stack == null)
      {
         data.process(parser);
      }
      else
      {
         data.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private ProbSolnSty sty;

}
