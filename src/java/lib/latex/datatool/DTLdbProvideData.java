/*
    Copyright (C) 2023 Nicola L.C. Talbot
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

public class DTLdbProvideData extends ControlSequence
{
   public DTLdbProvideData(DataToolSty sty)
   {
      this("DTLdbProvideData", sty);
   }

   public DTLdbProvideData(String name, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DTLdbProvideData(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String label = popLabelString(parser, stack);

      ControlSequence nameCs = parser.getControlSequence(DataToolSty.IO_NAME);

      if (nameCs != null)
      {
         String name = parser.expandToString(nameCs, stack).trim();

         if (!name.isEmpty())
         {
            label = name;
         }
      }

      parser.putControlSequence(true, 
       new TextualContentCommand(DataToolSty.DEFAULT_NAME, label));

      parser.putControlSequence(true, 
       new TextualContentCommand(DataToolSty.LAST_LOADED_NAME, label));

      parser.putControlSequence(true,
        new TextualContentCommand(DataToolSty.CURRENT_FILE_TYPE, "dtltex"));

      parser.putControlSequence(true,
        new TextualContentCommand(DataToolSty.CURRENT_FILE_VERSION, "3.0"));

      if (!sty.dbExists(label))
      {
         sty.createDataBase(label, sty.isDbGlobalOn());
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataToolSty sty;
}
