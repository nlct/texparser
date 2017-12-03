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
import java.util.Vector;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class LoadExceptProblems extends ControlSequence
{
   public LoadExceptProblems(ProbSolnSty sty)
   {
      this("loadexceptproblems", sty);
   }

   public LoadExceptProblems(String name, ProbSolnSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new LoadExceptProblems(getName(), sty);
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

      TeXObject labelList = (stack==parser? parser.popNextArg()
        : stack.popArg(parser));

      if (labelList instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (stack == parser)
         {
            expanded = ((Expandable)labelList).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)labelList).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            labelList = expanded;
         }
      }

      CsvList csvList = CsvList.getList(parser, labelList);

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

      ProbSolnDatabase tmpDb = sty.getTmpDatabase();

      parser.putControlSequence(true,// local
        new GenericCommand(true, "prob@currentdb", null,
           parser.getListener().createString(tmpDb.getName())));

      parser.getListener().input(new TeXPath(parser, 
        fileName.toString(parser)));

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

      Vector<String> exceptions = new Vector<String>(csvList.size());

      for (int i = 0; i < csvList.size(); i++)
      {
         exceptions.add(csvList.getValue(i).toString(parser));
      }

      for (Iterator<String> it = tmpDb.keySet().iterator(); it.hasNext(); )
      {
         String label = it.next();

         if (!exceptions.contains(label))
         {
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
