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
package com.dickimawbooks.texparserlib.latex.inputenc;

import java.io.IOException;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class InputEncSty extends LaTeXSty
{
   public InputEncSty()
   {
      super("inputenc");
   }

   public void addDefinitions(LaTeXParserListener listener)
   {
      listener.putControlSequence(new InputEncoding());
   }

   public void load(LaTeXParserListener listener,
      TeXParser parser, KeyValList options)
   throws IOException
   {
      if (options != null)
      {
         for (Iterator<String> en=options.keySet().iterator();
              en.hasNext();)
         {
            String key = en.next();

            if (!key.equals(""))
            {
               listener.setInputEncoding(key);
            }
         }
      }

      addDefinitions(listener);
   }

}
