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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
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

   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject optArg = stack.popArg(parser, '[', ']');

      TeXObject arg = stack.popStack(parser);

      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject capType = parser.getControlSequence("@captype");

      Group grp = listener.createGroup();

      if (capType != null)
      {
         if (capType instanceof Expandable)
         {
            TeXObjectList expanded =
               ((Expandable)capType).expandfully(parser, stack);

            if (expanded != null)
            {
               capType = expanded;
            }
         }

         String type = capType.toString(parser);

         listener.stepcounter(type);

         grp.add(listener.getControlSequence(type+"name"));
         grp.add(listener.getControlSequence("nobreakspace"));
         grp.add(listener.getControlSequence("the"+type));
      }

      // Is there a label following the caption?

      TeXObject object = stack.popStack(parser);

      while (object instanceof WhiteSpace)
      {
         object = stack.popStack(parser);
      }

      if (object instanceof TeXCsRef)
      {
         object = listener.getControlSequence(((TeXCsRef)object).getName());

         if (object instanceof Label)
         {
            TeXObject label = stack.popArg(parser);

            if (label == null)
            {
               label = parser.popNextArg();
            }

            if (label instanceof Expandable)
            {
               TeXObjectList expanded = ((Expandable)label).expandfully(parser, stack);

               if (expanded != null)
               {
                  label = expanded;
               }
            }

            grp.push(new HtmlTag("<a name=\""
              +HtmlTag.getUriFragment(label.toString(parser))+"\"/>"));
            grp.add(new HtmlTag("</a>"));
         }
         else
         {
            stack.push(object);
         }
      }

      listener.write("<div class=\"caption\">");

      stack.push(arg);
      stack.push(grp);

      listener.getControlSequence("@makecaption").process(parser, stack);

      listener.write("</div>");
   }

   public void process(TeXParser parser)
   throws IOException
   {
      TeXObject optArg = parser.popNextArg('[', ']');

      TeXObject arg = parser.popStack();

      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject capType = parser.getControlSequence("@captype");

      Group grp = listener.createGroup();

      if (capType != null)
      {
         if (capType instanceof Expandable)
         {
            TeXObjectList expanded =
               ((Expandable)capType).expandfully(parser);

            if (expanded != null)
            {
               capType = expanded;
            }
         }

         String type = capType.toString(parser);

         listener.stepcounter(type);

         grp.add(listener.getControlSequence(type+"name"));
         grp.add(listener.getControlSequence("nobreakspace"));
         grp.add(listener.getControlSequence("the"+type));
      }

      // Is there a label following the caption?

      TeXObject object = parser.popStack();

      while (object instanceof WhiteSpace)
      {
         object = parser.popStack();
      }

      if (object instanceof TeXCsRef)
      {
         object = listener.getControlSequence(((TeXCsRef)object).getName());

         if (object instanceof Label)
         {
            TeXObject label = parser.popNextArg();

            if (label instanceof Expandable)
            {
               TeXObjectList expanded = ((Expandable)label).expandfully(parser);

               if (expanded != null)
               {
                  label = expanded;
               }
            }

            grp.push(new HtmlTag("<a name=\""
              +HtmlTag.getUriFragment(label.toString(parser))+"\"/>"));
            grp.add(new HtmlTag("</a>"));
         }
         else
         {
            parser.push(object);
         }
      }

      listener.write("<div class=\"caption\">");

      ControlSequence cs = listener.getControlSequence("@makecaption");
      parser.push(arg);
      parser.push(grp);

      cs.process(parser);

      listener.write("</div>");
   }
}
