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

public class LoadGidx extends ControlSequence
{
   public LoadGidx(DataGidxSty sty)
   {
      this("loadgidx", sty);
   }

   public LoadGidx(String name, DataGidxSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new LoadGidx(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      KeyValList options = TeXParserUtils.popOptKeyValList(parser, stack);
      String filename = popLabelString(parser, stack);
      TeXObject title = popArg(parser, stack);

      TeXParserListener listener = parser.getListener();
      TeXApp texApp = listener.getTeXApp();

      parser.startGroup();

      IOSettings settings = IOSettings.fetchReadSettings(
         sty.getDataToolSty(), parser, stack);
      settings.setFileFormat(FileFormatType.DBTEX);

      String defExt = settings.getDefaultExtension();

      TeXPath texPath = new TeXPath(parser, filename, defExt, false);

      stack.push(new EndGidxRead(sty, options, title));

      DataBase.read(sty.getDataToolSty(), texPath, settings, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataGidxSty sty;
}
