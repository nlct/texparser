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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class Tabular extends RobustDeclaration
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

   protected void startTabular(TeXParser parser, 
     int verticalAlignment, TeXObject columnSpecs)
     throws IOException
   {
      TeXSettings settings = parser.getSettings();

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      listener.putControlSequence(new TabularNewline());

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
      String vertAlignArg = parser.popOptionalString(stack);
      int vertAlign = -1;

      if (vertAlignArg != null)
      {
         vertAlignArg = vertAlignArg.trim();

         if (!vertAlignArg.isEmpty())
         {
            vertAlign = vertAlignArg.codePointAt(0);
         }
      }

      TeXObject columnSpecs = parser.popRequiredExpandFully(stack);

      startTabular(parser, vertAlign, columnSpecs);

      TeXSettings settings = parser.getSettings();

      AlignRow row = ((LaTeXParserListener)parser.getListener()).createAlignRow();

      while (!row.parse(parser, stack, getName()))
      {
         row.process(parser, stack);
      }
   }

   @Override
   public void end(TeXParser parser) throws IOException
   {
      parser.getSettings().endAlignment();
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }
}
