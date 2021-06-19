/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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

   @Override
   public Object clone()
   {
      return new L2HMathDeclaration(getName(), getMode(), isNumbered());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      String label = null;

      if (isNumbered())
      {
         listener.stepcounter("equation");

         // Find label if there is one

         TeXObjectList list = new TeXObjectList();
         TeXObject obj = null;

         do
         {
            obj = parser.popNextTokenResolveReference(stack);

            if (obj instanceof Label)
            {
               label = parser.popRequiredString(stack);

               break;
            }

            obj = parser.expandFully(obj, stack);

            list.add(obj);

            if (obj instanceof End)
            {
               String name = parser.popRequiredString(stack);

               list.add(listener.createGroup(name));

               if (name.equals(getName()))
               {
                  break;
               }
            }
            else if (obj instanceof EndDeclaration)
            {
               Declaration dec = ((EndDeclaration)obj).getDeclaration(parser);
            }
         }
         while (obj != null);

         stack.push(list);
      }

      if (getMode() == TeXSettings.MODE_DISPLAY_MATH)
      {
         listener.addLinebreak();
         listener.htmlcomment("start displaymath");
         listener.write("<div class=\"displaymath");

         if (listener.isFleqn())
         {
            listener.write(" fleqn");
         }

         listener.write("\"");
      }
      else
      {
         listener.htmlcomment("start math");
         listener.write("<span class=\"math\"");
      }

      if (label != null)
      {
         listener.write(String.format(" id=\"%s\"", 
            listener.convertLabel(label)));
      }

      listener.write(" >");

      if (isNumbered())
      {
         processEquationNumber(parser);
      }

      doModeSwitch(parser);

      if (listener.useMathJax())
      {
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

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   @Override
   protected void processEquationNumber(TeXParser parser) throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      Writeable writeable = parser.getListener().getWriteable();

      writeable.write(String.format("<span class=\"%s\">",
        listener.isLeqno() ? "leqno" : "reqno"));

      listener.htmlcomment("start eqnnum");

      super.processEquationNumber(parser);

      listener.htmlcomment("end eqnnum");
      writeable.write("</span>");
   }

   @Override
   public void end(TeXParser parser) throws IOException
   {
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
         listener.htmlcomment("end displaymath");
         listener.addLinebreak();
      }
      else
      {
         listener.write("</span>");
         listener.htmlcomment("end math");
      }

      revertModeSwitch(parser);
   }
}
