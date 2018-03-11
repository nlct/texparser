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
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.Random;
import java.util.Vector;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.NewIf;
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.latex.*;

public class ProbSolnSty extends LaTeXSty
{
   public ProbSolnSty(KeyValList options, LaTeXParserListener listener,
     boolean loadParentOptions)
   throws IOException
   {
      this(16, false, options, listener, loadParentOptions);
   }

   public ProbSolnSty(int dbInitialCapacity, boolean saveDefOrder, 
     KeyValList options, LaTeXParserListener listener,
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "probsoln", listener, loadParentOptions);
      this.dbInitialCapacity = dbInitialCapacity;

      databases = new ConcurrentHashMap<String,ProbSolnDatabase>();

      if (saveDefOrder)
      {
         allEntries = new Vector<ProbSolnData>(dbInitialCapacity);
      }

      ProbSolnDatabase db = new ProbSolnDatabase(dbInitialCapacity, "default");
      databases.put("default", db);

      random = new Random();
   }

   public void addDefinitions()
   {
      registerControlSequence(new DefProblem(this));
      registerControlSequence(new OnlyProblem());
      registerControlSequence(new OnlySolution());
      registerControlSequence(new UseProblem(this));
      registerControlSequence(new NewProblem(this));
      registerControlSequence(new Question());
      registerControlSequence(new TextEnum());
      registerControlSequence(new ForEachProblem(this));
      registerControlSequence(new LoadAllProblems(this));
      registerControlSequence(new LoadSelectedProblems(this));
      registerControlSequence(new LoadExceptProblems(this));
      registerControlSequence(new LoadRandomProblems(this));
      registerControlSequence(new LoadRandomExcept(this));
      registerControlSequence(new RandSeed(this));

      registerControlSequence(new GenericCommand("prob@currentdb",
        null, getListener().createString("default")));

      if (getParser().getControlSequence("solution") == null)
      {
         registerControlSequence(new Solution());
      }

      registerControlSequence(new GenericCommand("solutionname",
         null, getListener().createString("Solution")));

       TeXObjectList list = new TeXObjectList();

       list.add(new TeXCsRef("let"));
       list.add(new TeXCsRef("ifshowanswers"));
       list.add(new TeXCsRef("iftrue"));

       registerControlSequence(new GenericCommand(true, "showanswers",
         null, list));

       list = new TeXObjectList();

       list.add(new TeXCsRef("let"));
       list.add(new TeXCsRef("ifshowanswers"));
       list.add(new TeXCsRef("iffalse"));

       registerControlSequence(new GenericCommand(true, "hideanswers",
         null, list));

   }

   public void processOption(String option, TeXObject value)
    throws IOException
   {
      if (option.equals("answers"))
      {
         getListener().getControlSequence("showanswerstrue")
           .process(getParser());
      }
      else if (option.equals("noanswers"))
      {
	 getListener().getControlSequence("showanswersfalse")
           .process(getParser());
      }
      else if (option.equals("usedefaultargs"))
      {
         getListener().getControlSequence("usedefaultprobargstrue")
           .process(getParser());
      }
      else if (option.equals("usenodefaultargs"))
      {
         getListener().getControlSequence("usedefaultprobargsfalse")
           .process(getParser());
      }
   }

   public boolean useDefaultArgs()
   {
      ControlSequence cs = getListener().getControlSequence(
         "ifusedefaultprobargs");

      return getListener().isIfTrue(cs);
   }

   protected void preOptions()
     throws IOException
   {
      NewIf.createConditional(true, getListener().getParser(),
        "ifshowanswers");
      NewIf.createConditional(true, getListener().getParser(),
        "ifusedefaultprobargs");
   }

   public ProbSolnDatabase getDatabase(String name)
     throws ProbSolnException
   {
      if (tmpDatabase != null && tmpDatabase.getName().equals(name))
      {
         return tmpDatabase;
      }

      ProbSolnDatabase db = databases.get(name);

      if (db == null)
      {
         throw new ProbSolnException(getParser(),
           ProbSolnException.ERROR_NO_SUCH_DB, name);
      }

      return db;
   }

   public ProbSolnData getProblem(String label, String dbName)
    throws ProbSolnException
   {
      ProbSolnData prob = getDatabase(dbName).get(label);

      if (prob == null)
      {
         throw new ProbSolnException(getParser(),
           ProbSolnException.ERROR_NO_SUCH_ENTRY_IN_DB,
           label, dbName);
      }

      return prob;
   }

   public boolean isDatabaseDefined(String name)
   {
      return databases.containsKey(name);
   }

   public ProbSolnDatabase addDatabase(String name)
     throws ProbSolnException
   {
      if (databases.containsKey(name))
      {
         throw new ProbSolnException(getParser(), 
           ProbSolnException.ERROR_DB_EXISTS, name);
      }

      ProbSolnDatabase db = new ProbSolnDatabase(dbInitialCapacity, name);
      databases.put(name, db);

      return db;
   }

   public void moveProblem(String label, String source, String target)
   throws ProbSolnException
   {
      ProbSolnDatabase db = databases.get(source);

      if (db == null)
      {
         throw new ProbSolnException(getParser(),
           ProbSolnException.ERROR_NO_SUCH_DB, source);
      }

      ProbSolnData data = db.remove(label);

      if (data == null)
      {
         throw new ProbSolnException(getParser(),
           ProbSolnException.ERROR_NO_SUCH_ENTRY_IN_DB,
           label, source);
      }

      db = databases.get(target);

      if (db == null)
      {
         throw new ProbSolnException(getParser(),
           ProbSolnException.ERROR_NO_SUCH_DB, target);
      }

      data.setDataBaseLabel(target);
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

   public int getTotalProblemCount()
   {
      return allEntries == null ? 0 : allEntries.size();
   }

   public void addProblem(ProbSolnData data)
   throws IOException
   {
      String dbName = getCurrentDb();

      ProbSolnDatabase db;

      if (tmpDatabase != null && tmpDatabase.getName().equals(dbName))
      {
         db = tmpDatabase;
      }
      else
      {
         db = databases.get(dbName);
      }

      if (db == null)
      {
         addDatabase(dbName);
      }

      data.setDataBaseLabel(dbName);
      db.put(data.getName(), data);

      if (allEntries != null && !(allEntries.contains(data)))
      {
         allEntries.add(data);
      }
   }

   public Iterator<ProbSolnData> allEntriesIterator()
   {
      return allEntries == null ? null : allEntries.iterator();
   }

   public String getCurrentDb() throws IOException
   {
      ControlSequence cs = getListener().getControlSequence("prob@currentdb");

      if (cs == null)
      {
         return "default";
      }

      if (cs instanceof Expandable)
      {
         TeXParser parser = getListener().getParser();

         TeXObjectList expanded = ((Expandable)cs).expandfully(parser);

         if (expanded != null)
         {
            return expanded.toString(parser);
         }
      }

      return "default";
   }

   public ProbSolnDatabase getTmpDatabase()
   {
      if (tmpDatabase == null)
      {
         tmpDatabase = new ProbSolnDatabase(dbInitialCapacity, "PROBSOLN#TMP");
      }

      return tmpDatabase;
   }

   public void clearTmpDatabase()
   {
      if (tmpDatabase != null)
      {
         tmpDatabase.clear();
      }
   }

   public Random getRandom()
   {
      return random;
   }

   public void setRandomSeed(long seed)
   {
      random.setSeed(seed);
   }

   private ConcurrentHashMap<String,ProbSolnDatabase> databases;

   private ProbSolnDatabase tmpDatabase;

   private Vector<ProbSolnData> allEntries=null;

   private int dbInitialCapacity=16;

   private Random random;
}
