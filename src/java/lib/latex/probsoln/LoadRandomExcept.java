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

   public Object clone()
   {
      return new LoadRandomExcept(getName(), sty);
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

      TeXNumber number = (stack==parser? parser.popNumber() 
        : stack.popNumber(parser));

      TeXObject fileNames = (stack==parser? parser.popNextArg()
        : stack.popArg(parser));

      if (fileNames instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (stack == parser)
         {
            expanded = ((Expandable)fileNames).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)fileNames).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            fileNames = expanded;
         }
      }

      TeXObject exceptions = (stack==parser? parser.popNextArg()
        : stack.popArg(parser));

      if (exceptions instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (stack == parser)
         {
            expanded = ((Expandable)exceptions).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)exceptions).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            exceptions = expanded;
         }
      }

      parser.startGroup();

      ProbSolnDatabase tmpDb = sty.getTmpDatabase();

      parser.putControlSequence(true,// local
        new GenericCommand(true, "prob@currentdb", null,
           parser.getListener().createString(tmpDb.getName())));

      CsvList csvList = CsvList.getList(parser, fileNames);

      for (int i = 0, n = csvList.size(); i < n; i++)
      {
         parser.getListener().input(new TeXPath(parser, 
           csvList.getValue(i).toString(parser)));
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

         int max = Integer.min(db.size(), number.getValue());

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
