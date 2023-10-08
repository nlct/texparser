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
   public PostReadHook(DataToolSty sty, TeXPath texPath)
   {
      this("__texparser_post_read_hook", sty, texPath);
   }

   public PostReadHook(String name, DataToolSty sty, TeXPath texPath)
   {
      super(name);
      this.sty = sty;
      this.texPath = texPath;
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
      String fileType = "tex";
      String fileVersion = "";

      ControlSequence fileTypeCs = parser.getControlSequence("__texparser_current_file_type_tl");
      ControlSequence fileVersionCs = parser.getControlSequence("__texparser_current_file_version_tl");

      String dbLabel = parser.expandToString(
        parser.getControlSequence("dtllastloadeddb"), stack);

      if (fileTypeCs != null)
      {
         fileType = parser.expandToString(fileTypeCs, stack);
      }

      if (fileVersionCs != null)
      {
         fileVersion = parser.expandToString(fileVersionCs, stack);
      }

      if (!fileType.equals("dbtex"))
      {
         sty.updateInternals(true, dbLabel);
      }

      sty.registerFileLoaded(dbLabel, fileType, fileVersion, texPath);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataToolSty sty;
   protected TeXPath texPath;
}
