/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class DocRef extends ControlSequence
{
   public DocRef()
   {
      this("docref", false, false);
   }

   public DocRef(String name, boolean quote, boolean altHtml)
   {
      super(name);
      this.quote = quote;
      this.altHtml = altHtml;
   }

   @Override
   public Object clone()
   {
      return new DocRef(getName(), quote, altHtml);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject text = popArg(parser, stack);
      String basename = popLabelString(parser, stack);

      String htmlname;

      if (altHtml)
      {
         htmlname = popLabelString(parser, stack)+".html";
      }
      else
      {
         htmlname = basename+".html";
      }

      TeXObjectList content = listener.createStack();

      content.add(listener.getControlSequence("href"));
      content.add(listener.createGroup(htmlname));

      Group grp = listener.createGroup();
      content.add(grp);

      if (quote)
      {
         grp.add(listener.getControlSequence("qt"));
         Group subgrp = listener.createGroup();
         grp.add(subgrp);

         grp = subgrp;
      }

      grp.add(text);

      TeXParserUtils.process(content, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   protected boolean quote=false, altHtml=false;
}
