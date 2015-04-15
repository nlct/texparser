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
import java.util.HashMap;
import java.util.Set;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.NewIf;
import com.dickimawbooks.texparserlib.latex.*;

public class ProbSolnSty extends LaTeXSty
{
   public ProbSolnSty(LaTeXParserListener listener)
   {
      super("probsoln", listener);

      databases = new HashMap<String,ProbSolnDatabase>();

      try
      {
         addDatabase("default");
      }
      catch (ProbSolnException e)
      {
         // won't happen
      }
   }

   public void addDefinitions()
   {
      registerControlSequence(new DefProblem(this));
      registerControlSequence(new OnlyProblem());
      registerControlSequence(new OnlySolution());
      registerControlSequence(new UseProblem(this));
   }

   public void processOption(String option)
    throws IOException
   {
   }

   protected void preOptions()
     throws IOException
   {
      NewIf.createConditional(getListener().getParser(),
        "ifshowanswers");
   }

   public ProbSolnDatabase getDatabase(String name) throws ProbSolnException
   {
      ProbSolnDatabase db = databases.get(name);

      if (db == null)
      {
         throw new ProbSolnException(
           "ProbSoln Database '"+name+"' doesn't exist");
      }

      return db;
   }

   public ProbSolnData getProblem(String label, String dbName)
    throws ProbSolnException
   {
      ProbSolnData prob = getDatabase(dbName).get(label);

      if (prob == null)
      {
         throw new ProbSolnException(String.format(
           "Entry '%s' doesn't exist in ProbSoln database '%s'",
           label, dbName));
      }

      return prob;
   }

   public void addDatabase(String name) throws ProbSolnException
   {
      if (databases.containsKey(name))
      {
         throw new ProbSolnException(
           "ProbSoln Database '"+name+"' already exists");
      }

      ProbSolnDatabase db = new ProbSolnDatabase(name);
      databases.put(name, db);
   }

   public void moveProblem(String label, String source, String target)
    throws ProbSolnException
   {
      ProbSolnDatabase db = databases.get(source);

      if (db == null)
      {
         throw new ProbSolnException(
           "ProbSoln Database '"+source+"' doesn't exist");
      }

      ProbSolnData data = db.remove(label);

      if (data == null)
      {
         throw new ProbSolnException(String.format(
           "Entry '%s' doesn't exist in ProbSoln database '%s'",
           label, source));
      }

      db = databases.get(target);

      if (db == null)
      {
         throw new ProbSolnException(
           "ProbSoln Database '"+target+"' doesn't exist");
      }

      db.put(label, data);
   }

   public Set<String> getDatabaseLabels()
   {
      return databases.keySet();
   }

   public int getDatabaseCount()
   {
      return databases.size();
   }

   public void addProblem(ProbSolnData data) throws ProbSolnException
   {
      ProbSolnDatabase db = getDatabase(currentDb);

      db.put(data.getName(), data);
   }

   private String currentDb = "default";

   private HashMap<String,ProbSolnDatabase> databases;
}
