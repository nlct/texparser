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
package com.dickimawbooks.texparserlib.latex.jmlr;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.primitives.IfTrue;

public class FloatConts extends ControlSequence
{
   public FloatConts()
   {
      this("floatconts", null);
   }

   public FloatConts(String name)
   {
      this(name+"conts", name);
   }

   public FloatConts(String name, String type)
   {
      super(name);
      this.type = type;
   }

   public Object clone()
   {
      return new FloatConts(getName(), type);
   }

   public String getCaptionType(TeXParser parser) throws IOException
   {
      TeXObject cs = parser.getListener().getControlSequence("@captype");

      if (cs == null)
      {
         return "table";
      }

      if (cs instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)cs).expandfully(parser);

         if (expanded != null)
         {
            cs = expanded;
         }
      }

      return cs.toString(parser);
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject labelArg;

      if (parser == stack)
      {
         labelArg = parser.popNextArg();
      }
      else
      {
         labelArg = stack.popArg(parser);
      }

      if (labelArg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack)
         {
            expanded = ((Expandable)labelArg).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)labelArg).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            labelArg = expanded;
         }
      }

      TeXObject captionArg;

      if (parser == stack)
      {
         captionArg = parser.popNextArg();
      }
      else
      {
         captionArg = stack.popArg(parser);
      }

      String captionType;

      if (type == null)
      {
         captionType = getCaptionType(parser);
      }
      else
      {
         captionType = type;
      }

      TeXObject contentsArg;

      if (parser == stack)
      {
         contentsArg = parser.popNextArg();
      }
      else
      {
         contentsArg = stack.popArg(parser);
      }

      ControlSequence cs = parser.getControlSequence(
         String.format("if%scaptiontop", captionType));

      boolean top=false;

      if (cs instanceof IfTrue)
      {
         top = true;
      }

      String label = labelArg.toString(parser);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObjectList list = new TeXObjectList();

      TeXObject anchor = listener.getAnchor(label);

      if (anchor != null)
      {
         list.add(anchor);
      }

      if (top)
      {
         list.add(captionArg);

         if (anchor == null)
         {
            list.add(new TeXCsRef("label"));
            list.add(listener.createGroup(label));
         }

         list.add(listener.getControlSequence("bigskip"));
      }

      Group grp = listener.createGroup();
      list.add(grp);
      grp.add(listener.getControlSequence("centering"));
      grp.add(contentsArg);
      grp.add(listener.getControlSequence("par"));

      if (!top)
      {
         list.add(listener.getControlSequence("bigskip"));

         list.add(captionArg);

         if (anchor == null)
         {
            list.add(new TeXCsRef("label"));
            list.add(listener.createGroup(label));
         }
      }

      if (parser == stack)
      {
         list.process(parser);
      }
      else
      {
         list.process(parser, stack);
      }
   }

   private String type;
}
