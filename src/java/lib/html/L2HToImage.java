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
import java.util.Vector;
import java.util.Iterator;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HToImage extends ControlSequence
{
   public L2HToImage()
   {
      this("TeXParserLibToImage");
   }

   public L2HToImage(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HToImage(getName());
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject options;
      TeXObject arg;

      if (stack == parser)
      {
         options = parser.popNextArg('[', ']');
         arg = parser.popNextArg();
      }
      else
      {
         options = stack.popArg(parser, '[', ']');
         arg = stack.popArg(parser);
      }


      KeyValList keyValList = null;

      TeXObject alt = null;
      TeXObject preAlt = null;
      TeXObject postAlt = null;
      String type = null;
      String name = null;
      boolean crop = true;

      if (options != null)
      {
         keyValList = KeyValList.getList(parser, options);
         alt = keyValList.getValue("alt");
         preAlt =keyValList.getValue("pre-alt"); 
         postAlt =keyValList.getValue("post-alt"); 

         TeXObject nameObj = keyValList.getExpandedValue("name", parser, stack);

         if (nameObj != null)
         {
            name = nameObj.toString(parser);
         }

         TeXObject typeObj = keyValList.getExpandedValue("type", parser, stack);

         if (typeObj != null)
         {
            type = typeObj.toString(parser);
         }

         Boolean boolVal = keyValList.getBoolean("crop", parser, stack);

         crop = (boolVal == null ? false : boolVal.booleanValue());
      }

      if (alt == null)
      {
         alt = arg;
      }

      if (preAlt != null)
      {
         if (alt instanceof TeXObjectList && !(alt instanceof Group))
         {
            ((TeXObjectList)alt).push(preAlt);
         }
         else
         {
            TeXObjectList list = new TeXObjectList();
            list.add(preAlt);
            list.add(alt);
            alt = list;
         }
      }

      if (postAlt != null)
      {
         if (alt instanceof TeXObjectList && !(alt instanceof Group))
         {
            ((TeXObjectList)alt).add(postAlt);
         }
         else
         {
            TeXObjectList list = new TeXObjectList();
            list.add(alt);
            list.add(postAlt);
            alt = list;
         }
      }

      L2HConverter listener = (L2HConverter)parser.getListener();

      String preamble = listener.getImagePreamble();

      StringBuilder content = new StringBuilder();

      Color fgCol = parser.getSettings().getFgColor();
      Color bgCol = parser.getSettings().getBgColor();

      if (fgCol != null && fgCol != Color.BLACK)
      {
         content.append(String.format("\\color[rgb]{%0.3f,%0.3f,%0.3f}",
           fgCol.getRed()/255.0f, fgCol.getGreen()/255.0f,
           fgCol.getBlue()/255.0f));
      }

      if (bgCol != null && bgCol != Color.WHITE)
      {
         content.append(String.format("\\pagecolor[rgb]{%0.3f,%0.3f,%0.3f}",
           bgCol.getRed()/255.0f, bgCol.getGreen()/255.0f,
           bgCol.getBlue()/255.0f));
      }

      content.append(arg.toString(parser));

      L2HImage image = listener.toImage(preamble, 
       content.toString(), type, alt, name, crop);

      if (image != null)
      {
         image.process(parser);
      }
      else
      {
         if (name == null)
         {
            name = image.getName();
         }

         if (name == null)
         {
            listener.getWriteable().write("<!-- Image Creation Failed -->");
            alt.process(parser, stack);
            listener.getWriteable().write("<!-- End of Image Alt Block -->");
         }
         else
         {
            listener.getWriteable().write(
              String.format("<!-- Image %s Creation Failed -->", name));
            alt.process(parser, stack);
            listener.getWriteable().write(
               String.format("<!-- End of Image %s Alt Block -->", name));
         }
      }
   }
}
