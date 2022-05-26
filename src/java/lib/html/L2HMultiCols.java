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

public class L2HMultiCols extends Declaration
{
   public L2HMultiCols()
   {
      this("multicols");
   }

   public L2HMultiCols(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2HMultiCols(getName());
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      int num = popInt(parser, parser);

      TeXObject spread = popOptArg(parser, parser);

      parser.getListener().getWriteable().write(
        String.format("<div class=\"multicols%d\">", num));

      if (spread != null)
      {
         popOptArg(parser, parser);

         parser.getListener().getWriteable().write("<div class=\"multicolspan\">");
         spread.process(parser);
         parser.getListener().getWriteable().write("</div>");
      }

      super.process(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      int num = popInt(parser, stack);

      TeXObject spread = popOptArg(parser, stack);

      parser.getListener().getWriteable().write(
        String.format("<div class=\"multicols%d\">", num));

      if (spread != null)
      {
         popOptArg(parser, stack);

         parser.getListener().getWriteable().write("<div class=\"multicolspan\">");
         spread.process(parser);
         parser.getListener().getWriteable().write("</div>");
      }

      super.process(parser, stack);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      parser.getListener().getWriteable().write("</div>");
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }
}
