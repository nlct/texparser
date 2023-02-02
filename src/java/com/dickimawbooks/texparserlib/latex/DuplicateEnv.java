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

import com.dickimawbooks.texparserlib.*;

// Replicate another environment. Intended for starred versions that
// emulated unstarred versions.
public class DuplicateEnv extends Declaration
{
   public DuplicateEnv(String name, Declaration otherenv)
   {
      super(name);
      this.declaration = otherenv;
   }

   @Override
   public Object clone()
   {
      return new DuplicateEnv(getName(), declaration);
   }

   @Override
   public boolean canExpand()
   {
      return declaration.canExpand();
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return declaration.expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return declaration.expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return declaration.expandfully(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return declaration.expandfully(parser);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      declaration.process(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      declaration.process(parser, stack);
   }


   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      declaration.end(parser, stack);
   }

   @Override
   public boolean isModeSwitcher()
   {
      return declaration.isModeSwitcher();
   }

   protected Declaration declaration;
}
