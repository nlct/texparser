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
      this(name, ACTION_ERROR);
   }

   public L2HUndefined(String name, byte action)
   {
      super(name, action);
   }

   public Object clone()
   {
      return new L2HUndefined(getName(), getAction());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

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
         TeXApp texApp = listener.getTeXApp();

         try
         {
            throw new TeXSyntaxException(
              parser, TeXSyntaxException.ERROR_UNDEFINED, getName());

         }
         catch (TeXSyntaxException e)
         {
            switch (getAction())
            {
               case ACTION_ERROR:
                  texApp.error(e);
               break;
               case ACTION_WARN:
                  texApp.warning(parser, e.getMessage(texApp));
               break;
               case ACTION_MESSAGE:
                  texApp.message(e.getMessage(texApp));
               break;
            }
         }
      }
   }
}
