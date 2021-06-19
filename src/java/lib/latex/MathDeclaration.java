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

import com.dickimawbooks.texparserlib.*;

public class MathDeclaration extends RobustDeclaration
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
         LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

         listener.stepcounter("equation");

         if (listener.isLeqno())
         {
            processEquationNumber(parser);
         }
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected void processEquationNumber(TeXParser parser) throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence("@eqnnum");

      cs.process(parser);
   }

   @Override
   public void end(TeXParser parser) throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (isNumbered() && !listener.isLeqno())
      {
         processEquationNumber(parser);
      }

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

   @Override
   public boolean isModeSwitcher()
   {
      return true;
   }

   private int orgMode;

   private int mode = TeXSettings.MODE_INLINE_MATH;

   private boolean numbered;
}
