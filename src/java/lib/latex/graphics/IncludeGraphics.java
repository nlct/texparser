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
package com.dickimawbooks.texparserlib.latex.graphics;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class IncludeGraphics extends ControlSequence
{
   public IncludeGraphics(GraphicsSty sty)
   {
      this("includegraphics", sty);
   }

   public IncludeGraphics(String name, GraphicsSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new IncludeGraphics(getName(), getSty());
   }

   protected void processGraphics(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      TeXObject option1 = (list == parser ? 
         parser.popNextArg('[', ']') : list.popArg(parser, '[', ']'));

      TeXObject option2 = null;

      TeXObjectList option1List = null;
      TeXObjectList option2List = null;

      if (option1 != null)
      {
         if (option1 instanceof TeXObjectList)
         {
            option1List = (TeXObjectList)option1;
         }
         else
         {
            option1List = new TeXObjectList();
            option1List.add(option1);
         }

         option2 = (list == parser ? 
            parser.popNextArg('[', ']') : list.popArg(parser, '[', ']'));

         if (option2 != null)
         {
            if (option2 instanceof TeXObjectList)
            {
               option2List = (TeXObjectList)option2;
            }
            else
            {
               option2List = new TeXObjectList();
               option2List.add(option2);
            }
         }

      }

      TeXObjectList llx = null;
      TeXObjectList lly = null;
      TeXObjectList urx = null;
      TeXObjectList ury = null;

      KeyValList keyValList = new KeyValList();

      if (option1List != null)
      {
         llx = new TeXObjectList();

         while (option1List.size() > 0)
         {
            TeXObject obj = option1List.pop();

            if ((obj instanceof CharObject)
             && ((CharObject)obj).getCharCode() == (int)',')
            {
               lly = new TeXObjectList();
               continue;
            }

            if (obj instanceof Ignoreable
             || obj instanceof WhiteSpace)
            {
               continue;
            }

            if (lly == null)
            {
               llx.add(obj);
            }
            else
            {
               lly.add(obj);
            }
         }

         if (option2List != null)
         {
            urx = new TeXObjectList();

            while (option2List.size() > 0)
            {
               TeXObject obj = option2List.pop();

               if ((obj instanceof CharObject)
                && ((CharObject)obj).getCharCode() == (int)',')
               {
                  ury = new TeXObjectList();
                  continue;
               }

               if (obj instanceof Ignoreable
                || obj instanceof WhiteSpace)
               {
                  continue;
               }

               if (ury == null)
               {
                  urx.add(obj);
               }
               else
               {
                  ury.add(obj);
               }
            }
         }

         if (llx != null)
         {
            keyValList.put("bbllx", llx);
         }

         if (lly != null)
         {
            keyValList.put("bblly", lly);
         }

         if (urx != null)
         {
            keyValList.put("bburx", urx);
         }

         if (ury != null)
         {
            keyValList.put("bblly", ury);
         }
      }

      process(parser, list, keyValList);
   }

   protected void processGraphicx(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      TeXObject options = list.popArg(parser, '[', ']');

      KeyValList keyValList = null;

      if (options != null)
      {
         keyValList = KeyValList.getList(parser, options);
      }

      process(parser, list, keyValList);
   }

   public void process(TeXParser parser, TeXObjectList list,
    KeyValList keyValList)
     throws IOException
   {
      TeXObject imgFile = (list == parser ? parser.popNextArg()
        : list.popArg(parser));

      TeXObjectList expanded = null;

      if (imgFile instanceof Expandable)
      {
         if (list == parser)
         {
            expanded = ((Expandable)imgFile).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)imgFile).expandfully(parser, list);
         }
      }

      String imgName;

      if (expanded == null)
      {
         imgName = imgFile.toString(parser);
      }
      else
      {
         imgName = expanded.toString(parser);
      }

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      listener.includegraphics(keyValList, imgName);
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (this != null && this.getName().equals("graphics"))
      {
         processGraphics(parser, list);
      }
      else
      {
         processGraphicx(parser, list);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public GraphicsSty getSty()
   {
      return sty;
   }

   private GraphicsSty sty;
}
