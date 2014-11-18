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

import com.dickimawbooks.texparserlib.*;

public class MathDeclaration extends Declaration
{
   public MathDeclaration()
   {
      this("(", TeXSettings.MODE_INLINE_MATH, false);
   }

   public MathDeclaration(String name)
   {
      this(name, TeXSettings.MODE_INLINE_MATH, false);
   }

   public MathDeclaration(String name, int mode)
   {
      this(name, mode, false);
   }

   public MathDeclaration(String name, int mode, boolean numbered)
   {
      super(name);
      this.mode = mode;
      this.numbered = numbered;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   public Object clone()
   {
      return new MathDeclaration(getName(), mode, numbered);
   }

   public void doModeSwitch(TeXParser parser)
   {
      TeXSettings settings = parser.getSettings();
      orgMode = settings.getCurrentMode();
      settings.setMode(mode);
   }

   public void revertModeSwitch(TeXParser parser)
   {
      TeXSettings settings = parser.getSettings();
      settings.setMode(orgMode);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      doModeSwitch(parser);

      if (isNumbered())
      {
         ((LaTeXParserListener)parser.getListener()).stepcounter("equation");
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      doModeSwitch(parser);

      if (isNumbered())
      {
         ((LaTeXParserListener)parser.getListener()).stepcounter("equation");
      }
   }

   public void end(TeXParser parser) throws IOException
   {
      revertModeSwitch(parser);
   }

   public int getMode()
   {
      return mode;
   }

   public boolean isNumbered()
   {
      return numbered;
   }

   public boolean isModeSwitcher()
   {
      return true;
   }

   private int orgMode;

   private int mode = TeXSettings.MODE_INLINE_MATH;

   private boolean numbered;
}
