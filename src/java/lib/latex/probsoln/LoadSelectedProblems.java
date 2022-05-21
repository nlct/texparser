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

public class LoadSelectedProblems extends ControlSequence
{
   public LoadSelectedProblems(ProbSolnSty sty)
   {
      this("loadselectedproblems", sty);
   }

   public LoadSelectedProblems(String name, ProbSolnSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new LoadSelectedProblems(getName(), sty);
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

      TeXObject labelList = popArgExpandFully(parser, stack);

      CsvList csvList = CsvList.getList(parser, labelList);

      String fileName = popLabelString(parser, stack);

      parser.startGroup();

      ProbSolnDatabase tmpDb = sty.getTmpDatabase();

      parser.putControlSequence(true,// local
        new GenericCommand(true, "prob@currentdb", null,
           parser.getListener().createString(tmpDb.getName())));

      parser.getListener().input(new TeXPath(parser, fileName), stack);

      parser.putControlSequence(true,// local
        new GenericCommand(true, "prob@currentdb", null,
           parser.getListener().createString(dbName)));

      ProbSolnDatabase db;

      if (sty.isDatabaseDefined(dbName))
      {
         db = sty.getDatabase(dbName);
      }
      else
      {
         db = sty.addDatabase(dbName);
      }

      for (int i = 0; i < csvList.size(); i++)
      {
         String label = csvList.getValue(i).toString(parser);

         ProbSolnData data = tmpDb.get(label);

         if (data != null)
         {
            db.put(data.getName(), data);
         }
      }

      sty.clearTmpDatabase();

      parser.endGroup();
   }


   private ProbSolnSty sty;

}
