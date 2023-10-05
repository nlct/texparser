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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class Tabular extends Declaration
{
   public Tabular()
   {
      this("tabular");
   }

   public Tabular(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new Tabular(getName());
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
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
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
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

   @Deprecated
   protected void startTabular(TeXParser parser, 
     int verticalAlignment, TeXObject columnSpecs)
     throws IOException
   {
      startTabular(parser, parser, verticalAlignment, columnSpecs);
   }

   protected void startTabular(TeXParser parser, TeXObjectList stack,
     int verticalAlignment, TeXObject columnSpecs)
     throws IOException
   {
      TeXSettings settings = parser.getSettings();

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      parser.putControlSequence(true, new TabularNewline());

      settings.setAlignmentList(listener.createTeXCellAlignList(columnSpecs));
      settings.startAlignment();
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      String vertAlignArg = popOptLabelString(parser, stack);

      int vertAlign = -1;

      if (vertAlignArg != null)
      {
         vertAlignArg = vertAlignArg.trim();

         if (!vertAlignArg.isEmpty())
         {
            vertAlign = vertAlignArg.charAt(0);
         }
      }

      TeXObject columnSpecs = popArgExpandFully(parser, stack);

      startTabular(parser, stack, vertAlign, columnSpecs);

      AlignRow row = ((LaTeXParserListener)parser.getListener()).createAlignRow(stack);

      if (parser == stack || stack == null)
      {
         row.process(parser);
      }
      else
      {
         row.process(parser, stack);
      }
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      parser.getSettings().endAlignment();
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }
}
