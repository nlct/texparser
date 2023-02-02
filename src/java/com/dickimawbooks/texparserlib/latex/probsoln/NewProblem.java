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
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class NewProblem extends ControlSequence
{
   public NewProblem(ProbSolnSty sty)
   {
      this("newproblem", sty);
   }

   public NewProblem(String name, ProbSolnSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new NewProblem(getName(), sty);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject object = stack.peekStack();

      if (object instanceof CharObject
      && ((CharObject)object).getCharCode() == (int)'*')
      {
         if (stack == parser)
         {
            parser.popStack();
         }
         else
         {
            stack.popStack(parser);
         }

         processStar(parser, stack);
      }
      else
      {
         processUnstar(parser, stack);
      }
   }

   private void processUnstar(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      Numerical numArgs = null;
      TeXObjectList defArgs = null;
      TeXObject problem = null;
      TeXObject solution = null;

      String label;

      if (parser == stack)
      {
         numArgs = parser.popNumericalArg('[', ']');

         if (numArgs != null)
         {
            TeXObject obj = parser.popNextArg('[', ']');

            if (obj instanceof TeXObjectList)
            {
               defArgs = (TeXObjectList)obj;
            }
            else if (obj != null)
            {
               defArgs = new TeXObjectList();
               defArgs.add(obj);
            }
         }

         TeXObject labObj = parser.popNextArg();

         TeXObjectList expanded = null;

         if (labObj instanceof Expandable)
         {
            expanded = ((Expandable)labObj).expandfully(parser);
         }

         if (expanded == null)
         {
            label = labObj.toString(parser);
         }
         else
         {
            label = expanded.toString(parser);
         }

         problem = parser.popNextArg();
         solution = parser.popNextArg();
      }
      else
      {
         numArgs = stack.popNumericalArg(parser, '[', ']');

         if (numArgs != null)
         {
            TeXObject obj = stack.popArg(parser, '[', ']');

            if (obj instanceof TeXObjectList)
            {
               defArgs = (TeXObjectList)obj;
            }
            else if (obj != null)
            {
               defArgs = new TeXObjectList();
               defArgs.add(obj);
            }
         }

         TeXObject labObj = stack.popArg(parser);

         TeXObjectList expanded = null;

         if (labObj instanceof Expandable)
         {
            expanded = ((Expandable)labObj).expandfully(parser, stack);
         }

         if (expanded == null)
         {
            label = labObj.toString(parser);
         }
         else
         {
            label = expanded.toString(parser);
         }

         problem = stack.popArg(parser);
         solution = stack.popArg(parser);
      }

      TeXObjectList contents = new TeXObjectList();

      contents.add(new TeXCsRef("begin"));
      contents.add(parser.getListener().createGroup("probsolnquestion"));

      if (problem instanceof TeXObjectList)
      {
         contents.addAll((TeXObjectList)problem);
      }
      else
      {
         contents.add(problem);
      }

      contents.add(new TeXCsRef("end"));
      contents.add(parser.getListener().createGroup("probsolnquestion"));

      contents.add(new TeXCsRef("begin"));
      contents.add(parser.getListener().createGroup("onlysolution"));
      contents.add(new TeXCsRef("begin"));
      contents.add(parser.getListener().createGroup("solution"));

      if (solution instanceof TeXObjectList)
      {
         contents.addAll((TeXObjectList)solution);
      }
      else
      {
         contents.add(solution);
      }

      contents.add(new TeXCsRef("end"));
      contents.add(parser.getListener().createGroup("solution"));
      contents.add(new TeXCsRef("end"));
      contents.add(parser.getListener().createGroup("onlysolution"));

      ProbSolnData data = new ProbSolnData(label,
        numArgs == null ? 0 : numArgs.number(parser),
        defArgs, contents);

      sty.addProblem(data);
   }

   private void processStar(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      Numerical numArgs = null;
      TeXObjectList defArgs = null;
      TeXObjectList contents = null;

      String label;

      if (parser == stack)
      {
         numArgs = parser.popNumericalArg('[', ']');

         if (numArgs != null)
         {
            TeXObject obj = parser.popNextArg('[', ']');

            if (obj instanceof TeXObjectList)
            {
               defArgs = (TeXObjectList)obj;
            }
            else if (obj != null)
            {
               defArgs = new TeXObjectList();
               defArgs.add(obj);
            }
         }

         TeXObject labObj = parser.popNextArg();

         TeXObjectList expanded = null;

         if (labObj instanceof Expandable)
         {
            expanded = ((Expandable)labObj).expandfully(parser);
         }

         if (expanded == null)
         {
            label = labObj.toString(parser);
         }
         else
         {
            label = expanded.toString(parser);
         }

         TeXObject object = parser.popNextArg();

         if (object instanceof TeXObjectList)
         {
            contents = (TeXObjectList)object;
         }
         else
         {
            contents = new TeXObjectList();
            contents.add(object);
         }
      }
      else
      {
         numArgs = stack.popNumericalArg(parser, '[', ']');

         if (numArgs != null)
         {
            TeXObject obj = stack.popArg(parser, '[', ']');

            if (obj instanceof TeXObjectList)
            {
               defArgs = (TeXObjectList)obj;
            }
            else if (obj != null)
            {
               defArgs = new TeXObjectList();
               defArgs.add(obj);
            }
         }

         TeXObject labObj = stack.popArg(parser);

         TeXObjectList expanded = null;

         if (labObj instanceof Expandable)
         {
            expanded = ((Expandable)labObj).expandfully(parser, stack);
         }

         if (expanded == null)
         {
            label = labObj.toString(parser);
         }
         else
         {
            label = expanded.toString(parser);
         }

         TeXObject object = stack.popArg(parser);

         if (object instanceof TeXObjectList)
         {
            contents = (TeXObjectList)object;
         }
         else
         {
            contents = new TeXObjectList();
            contents.add(object);
         }
      }

      contents.push(parser.getListener().createGroup("probsolnquestion"));
      contents.push(new TeXCsRef("begin"));

      contents.add(new TeXCsRef("end"));
      contents.add(parser.getListener().createGroup("probsolnquestion"));

      ProbSolnData data = new ProbSolnData(label,
        numArgs == null ? 0 : numArgs.number(parser),
        defArgs, contents);

      sty.addProblem(data);
   }

   private ProbSolnSty sty;

}
