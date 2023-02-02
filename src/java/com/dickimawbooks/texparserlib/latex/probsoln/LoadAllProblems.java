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

   @Override
   public Object clone()
   {
      return new LoadAllProblems(getName(), sty);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String dbName = popOptLabelString(parser, stack);

      if (dbName == null)
      {
         dbName = "default";
      }

      String fileName = popLabelString(parser, stack);

      Group grp = parser.getListener().createGroup();

      grp.add(parser.getListener().getControlSequence("def"));
      grp.add(new TeXCsRef("prob@currentdb"));
      grp.add(parser.getListener().createDataList(dbName));

      grp.add(parser.getListener().getControlSequence("input"));
      grp.add(new TeXPathObject(new TeXPath(parser, fileName)));

      stack.push(grp);
   }


   private ProbSolnSty sty;

}
