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
package com.dickimawbooks.texparserlib.generic;

import java.io.IOException;
import java.util.Hashtable;

import com.dickimawbooks.texparserlib.*;

public class ParAlign extends Declaration
{
   public ParAlign(String name, int align)
   {
      super(name);
      this.align = align;
      this.orgAlign = TeXSettings.INHERIT;
   }

   public static void addCommands(TeXParser parser)
   {
      parser.putControlSequence(
         new ParAlign("centering", TeXSettings.PAR_ALIGN_CENTER));
      parser.putControlSequence(
         new ParAlign("raggedright", TeXSettings.PAR_ALIGN_LEFT));
      parser.putControlSequence(
         new ParAlign("raggedleft", TeXSettings.PAR_ALIGN_RIGHT));
      parser.putControlSequence(
         new ParAlign("center", TeXSettings.PAR_ALIGN_CENTER));
      parser.putControlSequence(
         new ParAlign("flushright", TeXSettings.PAR_ALIGN_RIGHT));
      parser.putControlSequence(
         new ParAlign("flushleft", TeXSettings.PAR_ALIGN_LEFT));
   }

   public String getName()
   {
      return name;
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      process(parser);
   }

   public void process(TeXParser parser)
     throws IOException
   {
       TeXSettings settings = parser.getSettings();

       orgAlign = settings.getCurrentParAlign();

       settings.setParAlign(align);
   }

   public void end(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();
      settings.setParAlign(orgAlign);
   }

   public Object clone()
   {
      return new ParAlign(getName(), align);
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

   public boolean isModeSwitcher()
   {
      return false;
   }

   private int align, orgAlign;
}
