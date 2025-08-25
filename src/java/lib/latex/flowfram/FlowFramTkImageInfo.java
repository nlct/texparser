/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.flowfram;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.KeyValList;

public class FlowFramTkImageInfo extends ControlSequence
{
   public FlowFramTkImageInfo()
   {
      this("flowframtkimageinfo");
   }

   public FlowFramTkImageInfo(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new FlowFramTkImageInfo(getName());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      KeyValList opts = TeXParserUtils.popKeyValList(parser, stack);

      TeXObjectList list = listener.createStack();

      TeXObject title = opts.getValue("title");

      if (title != null)
      {
         list.add(listener.getControlSequence("flowframtkSetTitle"));
         list.add(TeXParserUtils.createGroup(listener, title));
      }

      TeXObject date = opts.getValue("creationdate");

      if (date != null)
      {
         list.add(listener.getControlSequence("flowframtkSetCreationDate"));
         list.add(TeXParserUtils.createGroup(listener, date));
      }

      TeXParserUtils.process(list, parser, stack);
   }
}
