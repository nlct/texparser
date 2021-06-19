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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public abstract class SkipEnvContents extends RobustDeclaration
{
   public SkipEnvContents(String name)
   {
      super(name);
   }

   public void popContents(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject object = parser.popNextTokenResolveReference(stack);

      while (object != null)
      {
         if (object instanceof End)
         {
            String envName = parser.popRequiredString(stack);

            Group grp = parser.getListener().createGroup(envName);

            if (envName.equals(getName()))
            {
               stack.push(grp);
               stack.push(object);
               break;
            }

         }

         object = parser.popNextTokenResolveReference(stack);
      }
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }
}
