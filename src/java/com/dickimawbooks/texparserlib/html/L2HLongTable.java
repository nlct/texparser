/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HLongTable extends Tabular
{
   public L2HLongTable()
   {
      this("longtable");
   }

   public L2HLongTable(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2HLongTable(getName());
   }

   @Override
   protected void startTabular(TeXParser parser, TeXObjectList stack,
     int verticalAlignment, TeXObject columnSpecs)
     throws IOException
   {
      parser.startGroup();

      super.startTabular(parser, stack, verticalAlignment, columnSpecs);

      Writeable writeable = parser.getListener().getWriteable();

      String cls = "longtable-";

      switch (horizontalAlignment)
      {
         case LEFT:
           cls += "l";
         break;
         case RIGHT:
           cls += "r";
         break;
         case CENTER:
           cls += "c";
         break;
      }

      if (id == null)
      {
         writeable.writeliteralln(String.format("<table class=\"%s\">", cls));
      }
      else
      {
         writeable.writeliteralln(String.format("<table id=\"%s\" class=\"%s\">", id, cls));
      }
   }

   protected void findLabel(TeXObjectList list, TeXParser parser)
    throws IOException
   {
      TeXObjectList pending = parser.getListener().createStack();

      while (!list.isEmpty())
      {
         TeXObject obj = list.popStack(parser);

         if (obj instanceof ControlSequence 
               && ((ControlSequence)obj).getName().equals("label"))
         {
            id = popLabelString(parser, list);
         }
         else if (obj instanceof TeXObjectList)
         {
            findLabel(((TeXObjectList)obj), parser);
            pending.add(obj);
         }
         else
         {
            pending.add(obj);
         }
      }

      list.addAll(pending);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      String hAlignArg = popOptLabelString(parser, stack);

      horizontalAlignment = AlignHStyle.CENTER;

      if (hAlignArg != null)
      {
         hAlignArg = hAlignArg.trim();

         if (hAlignArg.equals("l"))
         {
            horizontalAlignment = AlignHStyle.LEFT;
         }
         else if (hAlignArg.equals("c"))
         {
            horizontalAlignment = AlignHStyle.CENTER;
         }
         else if (hAlignArg.equals("r"))
         {
            horizontalAlignment = AlignHStyle.RIGHT;
         }
      }

      TeXObject columnSpecs = popArgExpandFully(parser, stack);

      id = null;

      // find caption, label, header and footer

      TeXObject caption = null;
      TeXObject headCaption = null;
      TeXObject firstHeadCaption = null;

      boolean captionNumbered = true;
      boolean headCaptionNumbered = true;
      boolean firstHeadCaptionNumbered = true;

      String headId = null;
      String firstHeadId = null;

      TeXObjectList firstHead = null;
      TeXObjectList head = null;
      TeXObjectList lastFoot = null;
      TeXObjectList foot = null;

      TeXObjectList list = parser.getListener().createStack();

      boolean done = false;

      while (!done)
      {
         TeXObject obj = stack.popStack(parser);
         boolean skip = false;

         if (obj instanceof TeXObjectList && ((TeXObjectList)obj).isStack())
         {
            stack.push(obj, true);
            obj = stack.popStack(parser);
         }

         if (obj instanceof ControlSequence)
         {
            String csname = ((ControlSequence)obj).getName();

            if (csname.equals("endhead"))
            {
               if (caption != null)
               {
                  headCaption = caption;
                  headCaptionNumbered = captionNumbered;
                  caption = null;
               }

               if (id != null)
               {
                  headId = id;
                  id = null;
               }

               head = list;
               list = parser.getListener().createStack();
               skip = true;
            }
            else if (csname.equals("endfirsthead"))
            {
               if (caption != null)
               {
                  firstHeadCaption = caption;
                  firstHeadCaptionNumbered = captionNumbered;
                  caption = null;
               }

               if (id != null)
               {
                  firstHeadId = id;
                  id = null;
               }

               firstHead = list;
               list = parser.getListener().createStack();
               skip = true;
            }
            else if (csname.equals("endfoot"))
            {
               foot = list;
               list = parser.getListener().createStack();
               skip = true;
            }
            else if (csname.equals("endlastfoot"))
            {
               foot = list;
               list = parser.getListener().createStack();
               skip = true;
            }
            else if (csname.equals("label") && id == null)
            {
               id = popLabelString(parser, stack);
               skip = true;
            }
            else if (csname.equals("caption"))
            {
               if (caption == null)
               {
                  captionNumbered = popModifier(parser, stack, '*') == -1;
                  popOptArg(parser, stack);
                  caption = popArg(parser, stack);

                  if (caption instanceof TeXObjectList)
                  {
                     findLabel((TeXObjectList)caption, parser);
                  }

                  skip = true;
               }
            }
            else if (csname.equals("end"))
            {
               String env = popLabelString(parser, stack);

               skip = true;

               if (env.equals(getName()))
               {
                  done = true;
               }

               list.add(obj);
               list.add(parser.getListener().createGroup(env));
            }
         }

         if (!skip)
         {
            list.add(obj);
         }
      }

      if (lastFoot != null)
      {
         list.addAll(lastFoot);
      }
      else if (foot != null)
      {
         list.addAll(foot);
      }

      if (firstHead != null)
      {
         list.addAll(0, firstHead);

         id = firstHeadId;
      }
      else if (head != null)
      {
         list.addAll(0, head);

         id = headId;
      }

      if (firstHeadCaption != null)
      {
         caption = firstHeadCaption;
         captionNumbered = firstHeadCaptionNumbered;
      }
      else if (headCaption != null)
      {
         caption = headCaption;
         captionNumbered = headCaptionNumbered;
      }

      parser.putControlSequence(true, new TextualContentCommand("@captype", "table"));

      startTabular(parser, stack, -1, columnSpecs);

      if (caption != null)
      {
         L2HConverter listener = (L2HConverter)parser.getListener();
         TeXObjectList capList = listener.createStack();

         capList.add(new StartElement("caption"));

         if (captionNumbered)
         {
            listener.stepcounter("table");

            capList.add(listener.getControlSequence("@makecaption"));
            Group grp = listener.createGroup();
            capList.add(grp);

            grp.add(listener.getControlSequence("tablename"));
            grp.add(listener.getControlSequence("nobreakspace"));
            grp.add(listener.getControlSequence("thetable"));
            grp.add(listener.getControlSequence("space"));

            grp = listener.createGroup();
            capList.add(grp);

            grp.add(caption, true);
         }
         else
         {
            capList.add(caption, true);
         }

         capList.add(new EndElement("caption"));

         if (parser == stack)
         {
            capList.process(parser);
         }
         else
         {
            capList.process(parser, stack);
         }
      }

      stack.push(list, true);

      AlignRow row = ((LaTeXParserListener)parser.getListener()).createAlignRow(stack);

      if (parser == stack)
      {
         row.process(parser);
      }
      else
      {
         row.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      writeable.writeliteralln("</table>");

      super.end(parser, stack);

      parser.endGroup();
   }

   protected AlignHStyle horizontalAlignment = AlignHStyle.CENTER;
   protected String id = null;
}
