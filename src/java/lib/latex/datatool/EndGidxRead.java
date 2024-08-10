/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class EndGidxRead extends ControlSequence
{
   public EndGidxRead(DataGidxSty sty, KeyValList options, TeXObject title)
   {
      this(DataToolSty.END_READ, sty, options, title);
   }

   public EndGidxRead(String name, DataGidxSty sty,
      KeyValList options, TeXObject title)
   {
      super(name);
      this.sty = sty;
      this.options = options;
      this.title = title;
   }

   @Override
   public Object clone()
   {
      return this;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = parser.getControlSequence(DataToolSty.LAST_LOADED_NAME);

      parser.endGroup();

      if (cs != null)
      {
         parser.putControlSequence(true, cs);

         String label = parser.expandToString(cs, stack);

         if (label != null && !label.isEmpty())
         {
            sty.indexDataBaseLoaded(label, options, title, stack);
         }
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   DataGidxSty sty;
   KeyValList options;
   TeXObject title;
}
