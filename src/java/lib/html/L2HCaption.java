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

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject optArg = popOptArg(parser, stack);

      TeXObject arg = stack.popStack(parser);

      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject capType = parser.getControlSequence("@captype");

      Group grp = listener.createGroup();

      String type = null;

      if (capType != null)
      {
         type = parser.expandToString(capType, stack);

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

      String id = null;

      if (object instanceof TeXCsRef)
      {
         object = listener.getControlSequence(((TeXCsRef)object).getName());

         if (object instanceof Label)
         {
            String label = popLabelString(parser, stack);

            id = HtmlTag.getUriFragment(label);
         }
         else
         {
            parser.push(object);
         }
      }

      String tag;
      String classAttr = null;

      if ("figure".equals(type))
      {
         tag = "figcaption";
      }
      else if ("table".equals(type))
      {
         tag = "caption";
      }
      else
      {
         tag = "div";
         classAttr = "caption";
      }

      StartElement startElem = new StartElement(tag);

      if (classAttr != null)
      {
         startElem.putAttribute("class", classAttr);
      }

      if (id != null)
      {
         startElem.putAttribute("id", id);
      }

      listener.write(startElem.toString());

      ControlSequence cs = listener.getControlSequence("@makecaption");
      parser.push(arg);
      parser.push(grp);

      if (parser == stack)
      {
         cs.process(parser);
      }
      else
      {
         cs.process(parser, stack);
      }

      listener.write(String.format("</%s>", tag));
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

}
