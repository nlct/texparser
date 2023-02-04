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

import com.dickimawbooks.texparserlib.*;

public class MathDeclaration extends Declaration
{
   public MathDeclaration()
   {
      this("(", TeXMode.INLINE_MATH, false);
   }

   public MathDeclaration(String name)
   {
      this(name, TeXMode.INLINE_MATH, false);
   }

   @Deprecated
   public MathDeclaration(String name, int mode)
   {
      this(name, mode, false);
   }

   @Deprecated
   public MathDeclaration(String name, int modeId, boolean numbered)
   {
      super(name);
      this.numbered = numbered;

      if (modeId == TeXSettings.MODE_INLINE_MATH)
      {
         mode = TeXMode.INLINE_MATH;
      }
      else if (modeId == TeXSettings.MODE_DISPLAY_MATH)
      {
         mode = TeXMode.DISPLAY_MATH;
      }
      else
      {
         throw new IllegalArgumentException("Illegal modeId argument "+modeId);
      }
   }

   public MathDeclaration(String name, TeXMode mode)
   {
      this(name, mode, false);
   }

   public MathDeclaration(String name, TeXMode mode, boolean numbered)
   {
      super(name);
      this.numbered = numbered;

      if (TeXMode.isMath(mode))
      {
         this.mode = mode;
      }
      else
      {
         throw new IllegalArgumentException("Illegal mode argument "+mode);
      }
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
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
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
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

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      doModeSwitch(parser);

      if (isNumbered())
      {
         ((LaTeXParserListener)parser.getListener()).stepcounter("equation");
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      doModeSwitch(parser);

      if (isNumbered())
      {
         ((LaTeXParserListener)parser.getListener()).stepcounter("equation");
      }
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      revertModeSwitch(parser);
   }

   public TeXMode getMode()
   {
      return mode;
   }

   public boolean isNumbered()
   {
      return numbered;
   }

   @Override
   public boolean isModeSwitcher()
   {
      return true;
   }

   private TeXMode orgMode;

   private TeXMode mode = TeXMode.INLINE_MATH;

   private boolean numbered;
}
