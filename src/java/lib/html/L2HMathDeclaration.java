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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HMathDeclaration extends MathDeclaration
{
   public L2HMathDeclaration()
   {
      super();
   }

   public L2HMathDeclaration(String name)
   {
      super(name);
   }

   public L2HMathDeclaration(String name, int mode)
   {
      super(name, mode);
   }

   public L2HMathDeclaration(String name, int mode, boolean numbered)
   {
      super(name, mode, numbered);
   }

   public Object clone()
   {
      return new L2HMathDeclaration(getName(), getMode(), isNumbered());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      super.process(parser, stack);

      L2HConverter listener = (L2HConverter)parser.getListener();

      if (getMode() == TeXSettings.MODE_DISPLAY_MATH)
      {
         listener.write("<div class=\"displaymath\">");
      }

      if (listener.useMathJax())
      {
         if (isNumbered())
         {
            // Find label if there is one

            TeXObjectList list = new TeXObjectList();

            while (stack.size() > 0)
            {
               TeXObject obj = stack.popStack(parser);

               if (obj instanceof TeXCsRef)
               {
                  obj = listener.getControlSequence(((TeXCsRef)obj).getName());
               }

               if (obj instanceof Label)
               {
                  obj.process(parser, stack);

                  break;
               }

               if (obj instanceof Expandable)
               {
                  TeXObjectList expanded = 
                     ((Expandable)obj).expandonce(parser, stack);

                  if (expanded != null)
                  {
                     obj = expanded;
                  }
               }

               if (obj instanceof TeXObjectList
                && !(obj instanceof Group))
               {
                  stack.addAll(0, (TeXObjectList)obj);

                  obj = stack.popStack(parser);
               }

               if (obj instanceof TeXCsRef)
               {
                  obj = listener.getControlSequence(((TeXCsRef)obj).getName());
               }

               if (obj instanceof End)
               {
                  TeXObject arg = stack.peekStack();

                  if (arg instanceof Group)
                  {
                     arg = ((Group)arg).toList();
                  }

                  if (getName().equals(arg.toString(parser)))
                  {
                     list.add(obj);

                     break;
                  }
               }
               else if (obj instanceof EndDeclaration)
               {
                  if (((EndDeclaration)obj).getDeclarationName().equals(getName()))
                  {
                     list.add(obj);

                     break;
                  }
               }
               else if (obj instanceof Label)
               {
                  obj.process(parser, stack);

                  break;
               }

               list.add(obj);
            }

            stack.addAll(0, list);
         }

         if (getMode() == TeXSettings.MODE_DISPLAY_MATH)
         {
            listener.write(listener.mathJaxStartDisplay());
         }
         else
         {
            listener.write(listener.mathJaxStartInline());
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      super.process(parser);

      L2HConverter listener = (L2HConverter)parser.getListener();

      if (getMode() == TeXSettings.MODE_DISPLAY_MATH)
      {
         listener.write("<div class=\"displaymath\">");
      }

      if (listener.useMathJax())
      {
         if (isNumbered())
         {
            // Find label if there is one

            TeXObjectList list = new TeXObjectList();

            while (true)
            {
               TeXObject obj = parser.popStack();

               if (obj instanceof TeXCsRef)
               {
                  obj = listener.getControlSequence(((TeXCsRef)obj).getName());
               }

               if (obj instanceof Label)
               {
                  obj.process(parser);

                  break;
               }

               if (obj instanceof Expandable)
               {
                  TeXObjectList expanded = 
                     ((Expandable)obj).expandonce(parser);

                  if (expanded != null)
                  {
                     obj = expanded;
                  }
               }

               if (obj instanceof TeXObjectList
                && !(obj instanceof Group))
               {
                  parser.addAll(0, (TeXObjectList)obj);

                  obj = parser.popStack();
               }

               if (obj instanceof TeXCsRef)
               {
                  obj = listener.getControlSequence(((TeXCsRef)obj).getName());
               }

               if (obj instanceof End)
               {
                  TeXObject arg = parser.peekStack();

                  if (arg instanceof Group)
                  {
                     arg = ((Group)arg).toList();
                  }

                  if (getName().equals(arg.toString(parser)))
                  {
                     list.add(obj);

                     break;
                  }
               }
               else if (obj instanceof EndDeclaration)
               {
                  if (((EndDeclaration)obj).getDeclarationName().equals(getName()))
                  {
                     list.add(obj);

                     break;
                  }
               }
               else if (obj instanceof Label)
               {
                  obj.process(parser);

                  break;
               }

               list.add(obj);
            }

            parser.addAll(0, list);

            listener.write("<span class=\"eqno\">(");
            listener.getControlSequence("theequation").process(parser);
            listener.write(")</span>");
         }

         if (getMode() == TeXSettings.MODE_DISPLAY_MATH)
         {
            listener.write(listener.mathJaxStartDisplay());
         }
         else
         {
            listener.write(listener.mathJaxStartInline());
         }
      }
   }

   public void end(TeXParser parser) throws IOException
   {
      super.end(parser);

      L2HConverter listener = (L2HConverter)parser.getListener();

      if (listener.useMathJax())
      {
         if (getMode() == TeXSettings.MODE_DISPLAY_MATH)
         {
            listener.write(listener.mathJaxEndDisplay());
         }
         else
         {
            listener.write(listener.mathJaxEndInline());
         }
      }

      if (getMode() == TeXSettings.MODE_DISPLAY_MATH)
      {
         listener.write("</div>");
      }
   }
}
