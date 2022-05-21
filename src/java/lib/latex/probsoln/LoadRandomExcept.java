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
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class LoadRandomExcept extends ControlSequence
{
   public LoadRandomExcept(ProbSolnSty sty)
   {
      this("loadrandomexcept", sty);
   }

   public LoadRandomExcept(String name, ProbSolnSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new LoadRandomExcept(getName(), sty);
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

      int number = popInt(parser, stack);

      TeXObject fileNames = popArgExpandFully(parser, stack);

      TeXObject exceptions = popArgExpandFully(parser, stack);

      parser.startGroup();

      ProbSolnDatabase tmpDb = sty.getTmpDatabase();

      parser.putControlSequence(true,// local
        new GenericCommand(true, "prob@currentdb", null,
           parser.getListener().createString(tmpDb.getName())));

      CsvList csvList = CsvList.getList(parser, fileNames);

      for (int i = 0, n = csvList.size(); i < n; i++)
      {
         parser.getListener().input(new TeXPath(parser, 
           csvList.getValue(i).toString(parser)), stack);
      }

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

      if (db.size() > 0)
      {
         csvList = CsvList.getList(parser, exceptions);

         int n = csvList.size();

         Vector<String> exceptionList = new Vector<String>(n);

         for (int i = 0; i < n; i++)
         {
            exceptionList.add(csvList.getValue(i).toString(parser));
         }

         Vector<String> labels = new Vector<String>(db.size());

         for (Iterator<String> it=db.keySet().iterator(); it.hasNext(); )
         {
            String label = it.next();

            if (!exceptionList.contains(label))
            {
               labels.add(label);
            }
         }

         Collections.shuffle(labels, sty.getRandom());

         int max = Integer.min(db.size(), number);

         for (int i = 0; i < max; i++)
         {
            String label = labels.get(i);

            ProbSolnData data = tmpDb.get(label);

            if (data != null)
            {
               db.put(data.getName(), data);
            }
         }
      }

      sty.clearTmpDatabase();

      parser.endGroup();
   }


   private ProbSolnSty sty;

}
