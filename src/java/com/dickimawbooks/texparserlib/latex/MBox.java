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
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;

public class MBox extends FrameBox
{
   public MBox()
   {
      this("mbox");
   }

   public MBox(String name)
   {
      super(name, BorderStyle.NONE, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, true);
   }

   @Deprecated
   public MBox(String name, byte borderStyle, 
      TeXDimension borderWidth, TeXDimension innerMargin)
   {
      super(name, borderStyle, ALIGN_DEFAULT, ALIGN_DEFAULT, true,
        borderWidth, innerMargin);
   }

   public MBox(String name, BorderStyle borderStyle, 
      TeXDimension borderWidth, TeXDimension innerMargin)
   {
      super(name, borderStyle, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, true,
        borderWidth, innerMargin);
   }

   @Override
   public FrameBox createBox()
   {
      return new MBox(getName());
   }

   public TeXDimension getBorderWidth(TeXParser parser) throws IOException
   {
      return currentBorderWidth;
   }

   public TeXDimension getInnerMargin(TeXParser parser) throws IOException
   {
      return currentInnerMargin;
   }

}
