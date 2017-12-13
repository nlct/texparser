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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.Writer;

import com.dickimawbooks.texparserlib.*;

public class Undefined extends Primitive
{
   public Undefined()
   {
      this("undefined", ACTION_ERROR);
   }

   public Undefined(String name, byte action)
   {
      super(name);
      setAction(action);
   }

   public Object clone()
   {
      return new Undefined(getName(), action);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXApp texApp = parser.getListener().getTeXApp();

      switch (action)
      {
         case ACTION_ERROR:
            throw new TeXSyntaxException(
            parser, TeXSyntaxException.ERROR_UNDEFINED, getName());
         case ACTION_WARN:
            texApp.warning(parser, 
              texApp.getMessage(TeXSyntaxException.ERROR_UNDEFINED, getName()));
         break;
         case ACTION_MESSAGE:
            texApp.message( 
              texApp.getMessage(TeXSyntaxException.ERROR_UNDEFINED, getName()));
         break;
      }
   }

   public void setAction(byte newAction)
   {
      switch (newAction)
      {
         case ACTION_ERROR:
         case ACTION_WARN:
         case ACTION_MESSAGE:
         case ACTION_IGNORE:
            action = newAction;
         break;
         default:
           throw new IllegalArgumentException(
              "Invalid undefined action "+newAction);
      }
   }

   public byte getAction()
   {
      return action;
   }

   private byte action=ACTION_ERROR;
   public static final byte ACTION_ERROR=0;
   public static final byte ACTION_WARN=1;
   public static final byte ACTION_MESSAGE=2;
   public static final byte ACTION_IGNORE=3;
}
