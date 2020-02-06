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

public class AtGobble extends Command
{
   public AtGobble()
   {
      this("@gobble", 1);
   }

   public AtGobble(String name, int numParams)
   {
      super(name);
      this.numParams = numParams;
   }

   public Object clone()
   {
      return new AtGobble(getName(), getNumParams());
   }

   public int getNumParams()
   {
      return numParams;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      for (int i = 0; i < numParams; i++)
      {
         parser.popNextArg();
      }

      return new TeXObjectList();
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      for (int i = 0; i < numParams; i++)
      {
         stack.popArg(parser);
      }

      return new TeXObjectList();
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack);
   }

   public void process(TeXParser parser) throws IOException
   {
      for (int i = 0; i < numParams; i++)
      {
         parser.popNextArg();
      }
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      for (int i = 0; i < numParams; i++)
      {
         list.popArg(parser);
      }
   }

   private int numParams;
}
