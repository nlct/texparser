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
     TeXObject verticalAlignment, TeXObject columnSpecs)
     throws IOException
   {
      TeXSettings settings = parser.getSettings();

      int columnCount = 1;// TODO fix this

      orgMode = settings.getStartColumnMode();
      settings.setStartRowMode(TeXSettings.START_ROW_MODE_TRUE);

      settings.setAlignmentColumnCount(columnCount);
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject vertAlign = parser.popNextArg('[', ']');
      TeXObject columnSpecs = parser.popNextArg();

      startTabular(parser, vertAlign, columnSpecs);
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      TeXObject vertAlign = list.popArg(parser, '[', ']');
      TeXObject columnSpecs = list.popArg();

      startTabular(parser, vertAlign, columnSpecs);
   }

   public void end(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();
      settings.setStartColumnMode(orgMode);
   }

   public boolean isModeSwitcher()
   {
      return false;
   }

   private int orgMode = TeXSettings.INHERIT;
}
