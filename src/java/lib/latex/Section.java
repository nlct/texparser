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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class Section extends ControlSequence
{
   public Section()
   {
      this("section", 1);
   }

   public Section(String name, int level)
   {
      this(name, level, name);
   }

   public Section(String name, int level, String type)
   {
      super(name);
      this.level = level;
      this.type = type;
   }

   public int getLevel()
   {
      return level;
   }

   public String getType()
   {
      return type;
   }

   @Override
   public Object clone()
   {
      return new Section(getName(), getLevel(), getType());
   }

   /*
    * Used to lookahead for \label following the section command. 
    * Should return null if not found or for sub-classes to
    * suppress lookahead
    */ 
   protected TeXObject popLabel(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject object = parser.popNextTokenResolveReference(stack, 
         PopStyle.IGNORE_LEADING_PAR_AND_SPACE);

      if (object instanceof Label)
      {
         return parser.popRequiredExpandFully(stack);
      }

      if (parser == stack || stack == null)
      {
         parser.push(object);
      }
      else
      {
         stack.push(object);
      }

      return null;
   }

   protected HierarchicalBlock createBlock(TeXParser parser)
   {
      return new HierarchicalBlock(getLevel(), getType());
   }

   protected void setBlockAttributes(HierarchicalBlock block,
     TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      boolean isStar = parser.isNextChar('*', stack);

      TeXObject optArg = null;

      if (!isStar)
      {
         optArg = parser.popOptional(stack);

         if (optArg != null)
         {
            block.setAttribute("toctitle", optArg);
         }
      }

      TeXObject arg = parser.popRequired(stack);

      block.setAttribute("title", arg);

      // TODO: search for label in title argument
      // At the moment just search the start of the stack

      boolean isNumbered = !isStar;

      if (isNumbered)
      {
         int secnumdepth = listener.getcountervalue("secnumdepth");

         if (secnumdepth < level)
         {
            isNumbered = false;
         }
      }

      String countername = isNumbered ? getName() : getName()+"*";

      listener.stepcounter(countername);

      TeXObject label = popLabel(parser, stack);

      if (label != null)
      {
         block.setAttribute("label", label.format());
      }
      else
      {
         ControlSequence cs = parser.getControlSequence("theH"+countername);

         if (cs == null || !(cs instanceof Expandable))
         {
            try
            {
               int val = listener.getcountervalue(countername);
               block.setAttribute("label", countername+"."+val);
            }
            catch (TeXSyntaxException e)
            {
            }
         }
         else
         {
            TeXObjectList expanded = ((Expandable)cs).expandfully(parser);

            if (expanded != null)
            {
               block.setAttribute("label", countername+"."+expanded.format());
            }
         }
      }

      if (isNumbered)
      {
         TeXObjectList prefix = new TeXObjectList();

         TeXObject head = getPrefixHead(parser);
         TeXObject tail = getPrefixTail(parser);

         if (head != null)
         {
            prefix.add(head);
         }

         prefix.add(new TeXCsRef("the"+getName()));

         if (tail != null)
         {
            prefix.add(tail);
         }

         block.setAttribute("prefix", prefix);
      }
   }

   /* before the number */
   protected TeXObject getPrefixHead(TeXParser parser)
   {
      if (level == -1)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(new TeXCsRef("partname"));
         list.add(parser.getListener().getSpace());
      }

      ControlSequence cs = parser.getControlSequence("@chapapp");

      if (cs == null)
      {
         return null;
      }

      if (level == 0)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         list.add(parser.getListener().getSpace());

         return list;
      }

      return null;
   }

   /* after the number */
   protected TeXObject getPrefixTail(TeXParser parser)
   {
      return parser.getListener().getSpace();
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      HierarchicalBlock block = createBlock(parser);
      setBlockAttributes(block, parser, stack);

      ((LaTeXParserListener)parser.getListener()).startHierarchicalBlock(block);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      HierarchicalBlock block = createBlock(parser);
      setBlockAttributes(block, parser, parser);

      ((LaTeXParserListener)parser.getListener()).startHierarchicalBlock(block);
   }

   protected int level;
   protected String type;
}
