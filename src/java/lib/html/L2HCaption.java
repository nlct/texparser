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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.ParAlign;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HCaption extends ControlSequence
{
   public L2HCaption()
   {
      this("caption");
   }

   public L2HCaption(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HCaption(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      popOptArg(parser, stack);

      TeXObject arg = stack.popStack(parser);

      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject capType = parser.getControlSequence("@captype");

      Group numArg = listener.createGroup();

      String type = null;

      if (capType != null)
      {
         type = parser.expandToString(capType, stack);

         listener.stepcounter(type);

         ControlSequence cs = parser.getControlSequence("fnum@"+type);

         if (cs == null)
         {
            numArg.add(listener.getControlSequence(type+"name"));
            numArg.add(listener.getControlSequence("nobreakspace"));
            numArg.add(listener.getControlSequence("the"+type));
         }
         else
         {
            numArg.add(cs);
         }
      }

      // Is there a label following the caption?

      byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;
      TeXObject object = stack.popStack(parser, popStyle);

      String id = null;

      object = TeXParserUtils.resolve(object, parser);

      if (object instanceof Label)
      {
         String label = popLabelString(parser, stack);

         id = HtmlTag.getUriFragment(label);
         object = stack.popStack(parser, TeXObjectList.POP_IGNORE_LEADING_SPACE);

         object = TeXParserUtils.resolve(object, parser);
      }

      boolean isTable = "table".equals(type);
      String tag;
      String classAttr = null;

      if ("figure".equals(type) && listener.isHtml5())
      {
         tag = "figcaption";
      }
      else if (isTable)
      {
         tag = "caption";
      }
      else
      {
         tag = "div";
         classAttr = "caption";
      }

      TeXObject nextObj = object;

      if (isTable)
      {
         ParAlign align = null;

         if (object instanceof ParAlign)
         {
            align = (ParAlign)object;
            object = stack.popStack(parser, TeXObjectList.POP_IGNORE_LEADING_SPACE);

            object = TeXParserUtils.resolve(object, parser);
         }

         if (object instanceof Begin)
         {
            String env = popLabelString(parser, stack);

            if (env.equals("tabular"))
            {
               TeXObject optArg = popOptArg(parser, stack);
               TeXObject specs = stack.popStack(parser);

               stack.push(createCaption(parser, tag, classAttr, id, numArg, arg));
               stack.push(specs);

               if (optArg != null)
               {
                  stack.push(listener.getOther(']'));
                  stack.push(optArg, true);
                  stack.push(listener.getOther('['));
               }

               stack.push(listener.createGroup(env));

               if (align != null)
               {
                  stack.push(object);
                  nextObj = align;
               }
            }
            else
            {
               stack.push(listener.createGroup(env));
               stack.push(object);

               nextObj = createCaption(parser, "div", "caption", id, numArg, arg);

               if (align != null)
               {
                  stack.push(nextObj);
                  nextObj = align;
               }
            }
         }
         else
         {
            stack.push(object);

            nextObj = createCaption(parser, "div", "caption", id, numArg, arg);

            if (align != null)
            {
               stack.push(nextObj);
               nextObj = align;
            }
         }
      }
      else
      {
         stack.push(object);

         nextObj = createCaption(parser, tag, classAttr, id, numArg, arg);
      }

      if (parser == stack)
      {
         nextObj.process(parser);
      }
      else
      {
         nextObj.process(parser, stack);
      }
   }

   protected TeXObjectList createCaption(TeXParser parser, String tag,
      String classAttr, String id, TeXObject numArg, TeXObject textArg)
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createDataList();

      StartElement startElem = new StartElement(tag);

      if (classAttr != null)
      {
         startElem.putAttribute("class", classAttr);
      }

      if (id != null)
      {
         startElem.putAttribute("id", id);
      }

      list.add(startElem);

      list.add(listener.getControlSequence("@makecaption"));
      list.add(numArg);
      list.add(textArg);
      list.add(new EndElement(tag));

      return list;
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

}
