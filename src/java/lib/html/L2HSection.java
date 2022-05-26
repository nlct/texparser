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
import com.dickimawbooks.texparserlib.latex.*;

public class L2HSection extends Section
{
   public L2HSection()
   {
      this("section");
   }

   public L2HSection(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2HSection(getName());
   }

   protected String popLabel(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject object;

      if (parser == stack || stack == null)
      {
         object = parser.popStack();
      }
      else
      {
         object = stack.popStack(parser);
      }

      while (object != null && (object instanceof WhiteSpace || object.isPar()))
      {
         if (parser == stack || stack == null)
         {
            object = parser.popStack();
         }
         else
         {
            object = stack.popStack(parser);
         }
      }

      if (object instanceof TeXCsRef)
      {
         object = listener.getControlSequence(((TeXCsRef)object).getName());
      }

      if (object instanceof Label)
      {
         return popLabelString(parser, stack);
      }

      if (stack == null)
      {
         parser.push(object);
      }
      else
      {
         stack.push(object);
      }

      return null;
   }

   protected void unnumbered(TeXParser parser, TeXObjectList stack,
       TeXObject arg)
   throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      String tag = getTag();

      listener.startSection(false, tag, getName());

      TeXObjectList substack = listener.createStack();

      TeXObject cs = parser.getControlSequence("theH"+getName()+"*");
      String labelName = null;

      if (cs == null)
      {
         cs = parser.getControlSequence("the"+getName()+"*");
      }

      if (cs != null)
      {
         String theHctr = parser.expandToString(cs, stack);

         String counter = getName()+"*";

         labelName = popLabel(parser, stack);

         if (labelName == null)
         {
            labelName = HtmlTag.getUriFragment(counter+"."+theHctr);
         }

         listener.stepcounter(counter);
      }

      if (tag == null)
      {
         if (labelName == null)
         {
            substack.add(new HtmlTag(
             String.format("%n<div class=\"%s\"><!-- start of %s header -->",
                getName(), getName())));
         }
         else
         {
            substack.add(new HtmlTag(
             String.format("%n<div id=\"%s\" class=\"%s\"><!-- start of %s header -->",
                labelName, getName(), getName())));
         }
      }
      else
      {
         if (labelName == null)
         {
            substack.add(new HtmlTag(
              String.format("%n<%s><!-- start of %s header -->", tag, getName())));
         }
         else
         {
            substack.add(new HtmlTag(
              String.format("%n<%s id=\"%s\"><!-- start of %s header -->", 
                  tag, labelName, getName())));
         }
      }

      substack.add(arg);

      if (labelName != null)
      {
         substack.add(listener.createLinkBox(labelName));
      }

      if (tag == null)
      {
         substack.add(new HtmlTag(
           String.format("</div><!-- end of %s header -->%n", getName())));
      }
      else
      {
         substack.add(new HtmlTag(
            String.format("</%s><!-- end of %s header -->%n", tag, getName())));
      }

      if (parser == stack || stack == null)
      {
         substack.process(parser);
      }
      else
      {
         substack.process(parser, stack);
      }

   }

   protected void numbered(TeXParser parser, TeXObjectList stack,
     TeXObject optArg, TeXObject arg)
       throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObjectList list = new TeXObjectList();

      String tag = getTag();

      listener.startSection(true, tag, getName());

      String labelName=null;

      // Is there a label following the section command?

      String label = popLabel(parser, stack);

      if (label != null)
      {
         labelName = HtmlTag.getUriFragment(label);
      }
      else
      {
         TeXObject cs = parser.getControlSequence("theH"+getName());

         if (cs == null)
         {
            cs = parser.getControlSequence("the"+getName());
         }

         if (cs != null)
         {
            String theHctr = parser.expandToString(cs, stack);

            labelName = HtmlTag.getUriFragment(String.format("%s.%s", 
              getName(), theHctr));
         }
      }

      if (tag == null)
      {
         if (labelName == null)
         {
            list.add(new HtmlTag(
             String.format("%n<div class=\"%s\"><!-- start of %s header -->",
                 getName(), getName())));
         }
         else
         {
            list.add(new HtmlTag(
             String.format("%n<div id=\"%s\" class=\"%s\"><!-- start of %s header -->",
                labelName, getName(), getName())));
         }
      }
      else
      {
         if (labelName == null)
         {
            list.add(new HtmlTag(String.format("%n<%s><!-- start of %s header -->",
               tag, getName())));
         }
         else
         {
            list.add(new HtmlTag(
               String.format("%n<%s id=\"%s\"><!-- start of %s header -->", 
                  tag, labelName, getName())));
         }
      }

      list.add(listener.getControlSequence("the"+getName()));
      list.add(listener.getOther('.'));
      list.add(listener.getSpace());

      list.add(arg);

      if (labelName != null)
      {
         list.add(listener.createLinkBox(labelName));
      }

      if (tag == null)
      {
         list.add(new HtmlTag(String.format("</div><!-- end of %s header -->%n",
           getName())));
      }
      else
      {
         list.add(new HtmlTag(String.format("</%s><!-- end of %s header -->%n",
            tag, getName())));
      }

      if (parser == stack || stack == null)
      {
         if (!parser.isPar(parser.peekStack()))
         {
            list.add(listener.getPar());
         }

         list.process(parser);
      }
      else
      {
         if (!parser.isPar(stack.peekStack()))
         {
            list.add(listener.getPar());
         }

         list.process(parser, stack);
      }
   }

   public String getTag()
   {
      String name = getName();

      for (int i = 0; i < TAGS.length; i++)
      {
         if (TAGS[i][0].equals(name))
         {
            return TAGS[i][1];
         }
      }

      return null;
   }

   private static final String[][] TAGS = new String[][]
   {
      new String[] {"chapter", "h1"},
      new String[] {"section", "h2"},
      new String[] {"subsection", "h3"},
      new String[] {"subsubsection", "h4"},
      new String[] {"paragraph", "h5"}
   };
}
