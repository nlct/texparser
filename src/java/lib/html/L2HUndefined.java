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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.io.Writer;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;

public class L2HUndefined extends Undefined
{
   public L2HUndefined()
   {
      this("undefined");
   }

   public L2HUndefined(String name)
   {
      this(name, UndefAction.ERROR);
   }

   public L2HUndefined(String name, UndefAction action)
   {
      super(name, action);
   }

   @Deprecated
   public L2HUndefined(String name, byte actionId)
   {
      super(name, actionId);
   }

   @Override
   public Object clone()
   {
      return new L2HUndefined(getName(), getAction());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (parser.isMathMode() && listener.useMathJax())
      {
         listener.write(toString(parser));
      }
      else
      {
         try
         {
            throw new TeXSyntaxException(parser, 
               TeXSyntaxException.ERROR_UNDEFINED, getName());

         }
         catch (TeXSyntaxException e)
         {
            switch (getAction())
            {
               case ERROR:
                  parser.error(e);
               break;
               case WARN:
                  parser.warning(e);
               break;
               case MESSAGE:
                  parser.message(e);
               break;
            }
         }
      }
   }
}
