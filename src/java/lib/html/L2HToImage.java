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
      String type = null;
      String name = null;
      boolean crop = true;

      if (options != null)
      {
         keyValList = KeyValList.getList(parser, options);
         alt = keyValList.getValue("alt");

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

         TeXObject cropObj = keyValList.getExpandedValue("crop", parser, stack);

         if (cropObj != null)
         {
            String cropVal = cropObj.toString(parser).trim();

            if (!cropVal.isEmpty())
            {
               crop = Boolean.valueOf(cropVal).booleanValue();
            }
         }
      }

      if (alt == null)
      {
         alt = arg;
      }

      String preamble = null;

      ControlSequence cs = parser.getControlSequence(
         "TeXParserLibToImagePreamble");

      if (cs != null && cs instanceof Expandable)
      {
         TeXObjectList expanded;

         if (stack == parser)
         {
            expanded = ((Expandable)cs).expandonce(parser);
         }
         else
         {
            expanded = ((Expandable)cs).expandonce(parser, stack);
         }

         if (expanded != null)
         {
            preamble = expanded.toString(parser);
         }
      }

      L2HConverter listener = (L2HConverter)parser.getListener();

      if (preamble == null)
      {
         StringBuilder builder = new StringBuilder();

         LaTeXFile cls = listener.getDocumentClass();

         if (cls == null)
         {
            builder.append("\\documentclass{article}");
         }
         else
         {
            builder.append("\\documentclass");

            KeyValList styOpts = cls.getOptions();

            if (styOpts != null)
            {
               builder.append(String.format("[%s]", styOpts.format()));
            }

            builder.append(String.format("{%s}%n", cls.getName()));
         }

         for (LaTeXFile lf : listener.getLoadedPackages())
         {
            builder.append("\\usepackage");

            KeyValList styOpts = lf.getOptions();

            if (styOpts != null)
            {
               builder.append(String.format("[%s]", styOpts.format()));
            }

            builder.append(String.format("{%s}%n", lf.getName()));
         }

         builder.append("\\pagestyle{empty}%n");

         preamble = builder.toString();
      }

      L2HImage image = listener.toImage(parser, preamble, 
       arg.toString(parser), type, alt, name, crop);

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
