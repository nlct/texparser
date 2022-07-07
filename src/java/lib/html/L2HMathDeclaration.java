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
      super.process(parser, stack);

      L2HConverter listener = (L2HConverter)parser.getListener();

      String label = null;

      if (isNumbered())
      {
         // Find label if there is one

         TeXObjectList list = new TeXObjectList();

         while (!stack.isEmpty())
         {
            TeXObject obj = stack.popStack(parser);

            obj = TeXParserUtils.resolve(obj, parser);

            if (obj instanceof Label)
            {
               label = popLabelString(parser, stack);

               break;
            }

            if (obj.canExpand() && obj instanceof Expandable)
            {
               TeXObjectList expanded = 
                  ((Expandable)obj).expandonce(parser, stack);

               if (expanded != null)
               {
                  obj = expanded;
               }
            }

            if (obj instanceof TeXObjectList)
            {
               stack.push(obj, true);

               obj = stack.popStack(parser);
            }

            obj = TeXParserUtils.resolve(obj, parser);

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
               label = popLabelString(parser, stack);

               break;
            }

            list.add(obj);
         }

         stack.push(list, true);
      }

      if (listener.useMathJax())
      {
         if (getMode() == TeXSettings.MODE_DISPLAY_MATH)
         {
            stack.push(new HtmlTag(listener.mathJaxStartDisplay()));
         }
         else
         {
            stack.push(new HtmlTag(listener.mathJaxStartInline()));
         }
      }

      if (isNumbered())
      {
         //stack.push(new HtmlTag("</span>"));
         stack.push(new EndElement("span"));
         stack.push(listener.getOther(')'));
         stack.push(listener.getControlSequence("theequation"));
         stack.push(listener.getOther('('));
         //stack.push(new HtmlTag("<span class=\"eqno\">"));

         StartElement elem = new StartElement("span");
         elem.putAttribute("class", "eqno");
         stack.push(elem);
      }

      if (getMode() == TeXSettings.MODE_DISPLAY_MATH)
      {
         if (label == null)
         {
            listener.write("<div class=\"displaymath\">");
         }
         else
         {
            listener.write(String.format("<div class=\"displaymath\" id=\"%s\">",
               label));
         }
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      super.process(parser);

      L2HConverter listener = (L2HConverter)parser.getListener();

      String label = null;

      if (isNumbered())
      {
         // Find label if there is one

         TeXObjectList list = new TeXObjectList();

         while (true)
         {
            TeXObject obj = parser.popStack();

            obj = TeXParserUtils.resolve(obj, parser);

            if (obj instanceof Label)
            {
               label = popLabelString(parser, parser);

               break;
            }

            if (obj.canExpand() && obj instanceof Expandable)
            {
               TeXObjectList expanded = 
                  ((Expandable)obj).expandonce(parser);

               if (expanded != null)
               {
                  obj = expanded;
               }
            }

            if (obj instanceof TeXObjectList)
            {
               parser.push(obj, true);

               obj = parser.popStack();
            }

            obj = TeXParserUtils.resolve(obj, parser);

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
               label = popLabelString(parser, parser);

               break;
            }

            list.add(obj);
         }

         parser.push(list, true);
      }

      if (listener.useMathJax())
      {
         if (getMode() == TeXSettings.MODE_DISPLAY_MATH)
         {
            parser.push(new HtmlTag(listener.mathJaxStartDisplay()));
         }
         else
         {
            parser.push(new HtmlTag(listener.mathJaxStartInline()));
         }
      }

      if (isNumbered())
      {
         parser.push(new HtmlTag("</span>"));
         parser.push(listener.getOther(')'));
         parser.push(listener.getControlSequence("theequation"));
         parser.push(listener.getOther('('));
         parser.push(new HtmlTag("<span class=\"eqno\">"));
      }

      if (getMode() == TeXSettings.MODE_DISPLAY_MATH)
      {
         if (label == null)
         {
            listener.write("<div class=\"displaymath\">");
         }
         else
         {
            listener.write(String.format("<div class=\"displaymath\" id=\"%s\">",
               label));
         }
      }
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      super.end(parser, stack);

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
