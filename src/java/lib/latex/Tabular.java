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

   public Object clone()
   {
      return new Tabular(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
      throws IOException
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
      throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return null;
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

   public void process(TeXParser parser) throws IOException
   {
      TeXObject vertAlignArg = parser.popNextArg('[', ']');

      int vertAlign = -1;

      if (vertAlignArg != null)
      {
         if (vertAlignArg instanceof Expandable)
         {
            TeXObjectList list = ((Expandable)vertAlignArg).expandfully(parser);

            if (list != null)
            {
               vertAlignArg = list;
            }
         }

         String arg = vertAlignArg.toString(parser).trim();

         if (!arg.isEmpty())
         {
            vertAlign = arg.charAt(0);
         }
      }

      TeXObject columnSpecs = parser.popNextArg();

      if (columnSpecs instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)columnSpecs).expandfully(parser);

         if (expanded != null)
         {
            columnSpecs = expanded;
         }
      }

      startTabular(parser, vertAlign, columnSpecs);

      AlignRow row = ((LaTeXParserListener)parser.getListener()).createAlignRow(parser);

      row.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject vertAlignArg = stack.popArg(parser, '[', ']');
      int vertAlign = -1;

      if (vertAlignArg != null)
      {
         if (vertAlignArg instanceof Expandable)
         {
            TeXObjectList list = ((Expandable)vertAlignArg).expandfully(parser, stack);

            if (list != null)
            {
               vertAlignArg = list;
            }
         }

         String arg = vertAlignArg.toString(parser).trim();

         if (!arg.isEmpty())
         {
            vertAlign = arg.charAt(0);
         }
      }

      TeXObject columnSpecs = stack.popArg(parser);

      if (columnSpecs instanceof Expandable)
      {
         TeXObjectList expanded =
            ((Expandable)columnSpecs).expandfully(parser, stack);

         if (expanded != null)
         {
            columnSpecs = expanded;
         }
      }

      startTabular(parser, vertAlign, columnSpecs);

      AlignRow row = ((LaTeXParserListener)parser.getListener()).createAlignRow(stack);

      row.process(parser, stack);
   }

   public void end(TeXParser parser) throws IOException
   {
      parser.getSettings().endAlignment();
   }

   public boolean isModeSwitcher()
   {
      return false;
   }
}
