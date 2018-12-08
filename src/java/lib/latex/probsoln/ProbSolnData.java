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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class ProbSolnData
{
   public ProbSolnData(String name, TeXObjectList contents)
   {
      this(name, 0, null, contents, "default");
   }

   public ProbSolnData(String name, TeXObjectList contents, String dbLabel)
   {
      this(name, 0, null, contents, dbLabel);
   }

   public ProbSolnData(String name, int numArgs,
      TeXObjectList defArgs, TeXObjectList contents)
   {
      this(name, numArgs, defArgs, contents, "default");
   }

   public ProbSolnData(String name, int numArgs,
      TeXObjectList defArgs, TeXObjectList contents, String dbLabel)
   {
      setName(name);
      this.numArgs = numArgs;
      this.defArgs = defArgs;
      this.contents = contents;
      this.dbLabel = dbLabel;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public void setDataBaseLabel(String dbLabel)
   {
      this.dbLabel = dbLabel;
   }

   public String getDataBaseLabel()
   {
      return dbLabel;
   }

   public int getNumArgs()
   {
      return numArgs;
   }

   public TeXObjectList getDefaultArgs()
   {
      return defArgs;
   }

   public TeXObjectList getQuestion(TeXParser parser)
     throws IOException
   {
      TeXObject[] params = null;

      if (numArgs > 0)
      {
         params = new TeXObject[numArgs];

         for (int i = 0; i < numArgs; i++)
         {
            params[i] = (TeXObject)defArgs.get(i).clone();

            if (params[i] instanceof Group 
                && !(params[i] instanceof MathGroup))
            {
               params[i] = ((Group)params[i]).toList();
            }
         }
      }

      TeXObjectList list = getData(parser, params);

      TeXObjectList question = new TeXObjectList();

      getProblem(parser, list, question, true, false);

      return question;
   }

   public TeXObjectList getAnswer(TeXParser parser, boolean stripSolutionEnv)
     throws IOException
   {
      TeXObject[] params = null;

      if (numArgs > 0)
      {
         params = new TeXObject[numArgs];

         for (int i = 0; i < numArgs; i++)
         {
            params[i] = (TeXObject)defArgs.get(i).clone();

            if (params[i] instanceof Group 
                && !(params[i] instanceof MathGroup))
            {
               params[i] = ((Group)params[i]).toList();
            }
         }
      }

      TeXObjectList list = (TeXObjectList)getData(parser, params).clone();

      TeXObjectList answer = new TeXObjectList();

      getProblem(parser, list, answer, false, stripSolutionEnv);

      return answer;
   }

   private void getProblem(TeXParser parser, TeXObjectList stack,
     TeXObjectList problem, boolean question, boolean stripSolutionEnv)
   throws IOException
   {
      getProblem(parser, stack, problem, question, stripSolutionEnv, 
        false, false);
   }

   private void getProblem(TeXParser parser, TeXObjectList stack,
     TeXObjectList problem, boolean question, boolean stripSolutionEnv,
     boolean initOnlyProblem, boolean initOnlySolution)
   throws IOException
   {
      boolean onlyProblem = initOnlyProblem;
      boolean onlySolution = initOnlySolution;

      TeXParserListener listener = parser.getListener();

      while (!stack.isEmpty())
      {
         TeXObject object = stack.pop();
         boolean addObject = (question && !onlySolution)
           || (!question && !onlyProblem);

         if (object instanceof TeXCsRef)
         {
            object = listener.getControlSequence(((TeXCsRef)object).getName());
         }

         if (object instanceof Group && !(object instanceof MathGroup))
         {
            if (addObject)
            {
               Group subGrp = listener.createGroup();
               problem.add(subGrp);

               getProblem(parser, (Group)object, subGrp, question,
                  stripSolutionEnv, onlyProblem, onlySolution);
            }
         }
         else if (object instanceof TeXObjectList && !(object instanceof Group))
         {
            stack.addAll(0, (TeXObjectList)object);
         }
         else if (object instanceof OnlyProblem || object instanceof Question)
         {
            onlyProblem = true;
         }
         else if (object instanceof OnlySolution)
         {
            onlySolution = true;
         }
         else if (object instanceof EndDeclaration)
         {
            Declaration dec = ((EndDeclaration)object).getDeclaration(parser);

            if (dec instanceof OnlyProblem || object instanceof Question)
            {
               onlyProblem = false;
            }
            else if (dec instanceof OnlySolution)
            {
               onlySolution = false;
            }
            else if (addObject)
            {
               problem.add(object);
            }
         }
         else if (object instanceof Begin)
         {
            TeXObject envName = stack.popArg(parser);

            if (envName instanceof Expandable)
            {
               TeXObjectList expanded = ((Expandable)envName).expandfully(
                 parser, stack);

               if (expanded != null)
               {
                  envName = expanded;
               }
            }

            String envStr = envName.toString(parser);

            if (envStr.equals("onlyproblem")
                 || envStr.equals("probsolnquestion"))
            {
               onlyProblem = true;
            }
            else if (envStr.equals("onlysolution"))
            {
               onlySolution = true;
            }
            else if (!(stripSolutionEnv && envStr.equals("solution"))
                     && addObject)
            {
               problem.add(object);

               Group subGrp = listener.createGroup();
               problem.add(subGrp);

               if (envName instanceof TeXObjectList)
               {
                  subGrp.addAll((TeXObjectList)envName);
               }
               else
               {
                  subGrp.add(envName);
               }
            }
         }
         else if (object instanceof End)
         {
            TeXObject envName = stack.popArg(parser);

            if (envName instanceof Expandable)
            {
               TeXObjectList expanded = ((Expandable)envName).expandfully(
                 parser, stack);

               if (expanded != null)
               {
                  envName = expanded;
               }
            }

            String envStr = envName.toString(parser);

            if (envStr.equals("onlyproblem")
                 || envStr.equals("probsolnquestion"))
            {
               onlyProblem = false;
            }
            else if (envStr.equals("onlysolution"))
            {
               onlySolution = false;
            }
            else if (!(stripSolutionEnv && envStr.equals("solution"))
                     && addObject)
            {
               problem.add(object);

               Group subGrp = listener.createGroup();
               problem.add(subGrp);

               if (envName instanceof TeXObjectList)
               {
                  subGrp.addAll((TeXObjectList)envName);
               }
               else
               {
                  subGrp.add(envName);
               }
            }
         }
         else if (addObject)
         {
            problem.add(object);
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject[] params = null;

      if (numArgs > 0)
      {
         params = new TeXObject[numArgs];

         for (int i = 0; i < numArgs; i++)
         {
            params[i] = parser.popNextArg();
         }
      }

      getData(parser, params).process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject[] params = null;

      if (numArgs > 0)
      {
         params = new TeXObject[numArgs];

         for (int i = 0; i < numArgs; i++)
         {
            params[i] = stack.popArg(parser);
         }
      }

      getData(parser, params).process(parser, stack);
   }

   private TeXObjectList getData(TeXParser parser, TeXObject[] params)
     throws IOException
   {
      return getData(parser, params, contents);
   }

   private TeXObjectList getData(TeXParser parser, TeXObject[] params,
     TeXObjectList contentsList)
     throws IOException
   {
      TeXObjectList list = contentsList.createList();

      for (TeXObject object : contentsList)
      {
         if (object instanceof Param)
         {
            int idx = ((Param)object).getDigit()-1;

            if (params == null || idx >= params.length)
            {
               throw new TeXSyntaxException(parser,
                 TeXSyntaxException.ERROR_SYNTAX,
                 object.toString(parser));
            }

            list.add((TeXObject)params[idx].clone());
         }
         else if (object instanceof DoubleParam)
         {
            list.add((TeXObject)((DoubleParam)object).next().clone());
         }
         else if (object instanceof TeXObjectList)
         {
            list.add(getData(parser, params, (TeXObjectList)object));
         }
         else
         {
            list.add((TeXObject)object.clone());
         }
      }

      return list;
   }

   public String toString()
   {
      return String.format("ProbSolnData[name=%s,db=%s,args=(n=%d,default=%s),contents=%s]",
        name, dbLabel, numArgs, defArgs, contents);
   }

   private String name, dbLabel;

   private int numArgs = 0;

   private TeXObjectList contents, defArgs;
}
