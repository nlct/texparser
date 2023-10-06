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

public class PostReadHook extends ControlSequence
{
   public PostReadHook(DataToolSty sty)
   {
      this("__texparser_post_read_hook", sty);
   }

   public PostReadHook(String name, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new PostReadHook(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence fileTypeCs = parser.getControlSequence("__datatool_current_file_type");

      if (fileTypeCs != null)
      {
         String fileType = parser.expandToString(fileTypeCs, stack);

         if (!fileType.equals("dbtex"))
         {
            String dbLabel = parser.expandToString(
              parser.getControlSequence("dtllastloadeddb"), stack);

            sty.updateInternals(true, dbLabel);
         }
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
