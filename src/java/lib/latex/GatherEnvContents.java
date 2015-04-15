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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public abstract class GatherEnvContents extends Declaration
{
   public GatherEnvContents(String name)
   {
      super(name);
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

   public TeXObjectList popContents(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject object = stack.pop();

      TeXObjectList contents = new TeXObjectList();

      while (object != null)
      {
         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
              ((TeXCsRef)object).getName());
         }

         if (object instanceof End)
         {
            TeXObject nextObj = 
               stack == parser ? parser.popNextArg() : stack.popArg(parser);

            String envName = nextObj.toString(parser);

            Group grp = parser.getListener().createGroup(envName);

            if (envName.equals(getName()))
            {
               stack.push(grp);
               stack.push(object);
               break;
            }

            contents.add(object);
            contents.add(grp);
         }
         else
         {
            contents.add(object);
         }

         object = stack.pop();
      }

      return contents;
   }

   public boolean isModeSwitcher()
   {
      return false;
   }
}
