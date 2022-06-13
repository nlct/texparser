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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class FrameBoxEnv extends Declaration
{
   public FrameBoxEnv(FrameBox fbox)
   {
      this(fbox.getId(), fbox);
   }

   public FrameBoxEnv(String name, FrameBox fbox)
   {
      super(name);
      this.fbox = fbox;
      setEndDeclaration(new EndDeclaration(name));
   }

   @Override
   public Object clone()
   {
      return new FrameBoxEnv(getName(), fbox);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      StartFrameBox obj = new StartFrameBox(fbox);
      obj.process(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      StartFrameBox obj = new StartFrameBox(fbox);
      obj.process(parser);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      EndFrameBox obj = new EndFrameBox(fbox);
      obj.process(parser, stack);
   }

   public FrameBox getFrameBox()
   {
      return fbox;
   }

   public boolean isInLine()
   {
      return fbox.isInLine();
   }

   public boolean isMultiLine()
   {
      return fbox.isMultiLine();
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
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

   protected FrameBox fbox;
}
