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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public abstract class ContentsLine extends ControlSequence
{
   public ContentsLine()
   {
      this("contentsline");
   }

   public ContentsLine(String name)
   {
      super(name);
   }

   public abstract TeXObjectList contentsline(TeXParser parser, TeXObject type,
    TeXObject title, TeXObject page, TeXObject link)
      throws IOException;

   public abstract TeXObjectList contentsline(TeXParser parser, TeXObject type,
    TeXObject title, TeXObject page)
      throws IOException;

   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject type = stack.popArg(parser);
      TeXObject title = stack.popArg(parser);
      TeXObject page = stack.popArg(parser);
      TeXObject link = null;

      if (listener.isStyLoaded("hyperref"))
      {
         link = stack.popArg(parser);
      }

      TeXObjectList list;

      if (link != null)
      {
         // Is there a label corresponding to this link?

         TeXObject label = listener.getLabelForLink(link);

         if (label != null)
         {
            link = label;
         }

         list = contentsline(parser, type, title, page, link);
      }
      else
      {
         list = contentsline(parser, type, title, page);
      }

      stack.addAll(0, list);
   }

   public void process(TeXParser parser)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject type = parser.popNextArg();
      TeXObject title = parser.popNextArg();
      TeXObject page = parser.popNextArg();
      TeXObject link = null;

      if (listener.isStyLoaded("hyperref"))
      {
         link = parser.popNextArg();
      }

      TeXObjectList list;

      if (link != null)
      {
         // Is there a label corresponding to this link?

         TeXObject label = listener.getLabelForLink(link);

         if (label != null)
         {
            link = label;
         }

         list = contentsline(parser, type, title, page, link);
      }
      else
      {
         list = contentsline(parser, type, title, page);
      }

      parser.addAll(0, list);
   }

}
