/*
    Copyright (C) 2013-2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.graphics;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Epsfig extends ControlSequence
{
   public Epsfig()
   {
      this("epsfig");
   }

   public Epsfig(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Epsfig(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      StringBuilder original = new StringBuilder();
      original.append(toString(parser));

      KeyValList keyValList = TeXParserUtils.popKeyValList(parser, stack);

      original.appendCodePoint(parser.getBgChar());
      original.append(keyValList.toString(parser));
      original.appendCodePoint(parser.getEgChar());

      TeXObject file = keyValList.getValue("file");

      if (file == null)
      {
         throw new TeXSyntaxException(parser, 
          LaTeXSyntaxException.ERROR_MISSING_KEY, "file");
      }

      keyValList.remove("file");

      TeXObjectList replacement = listener.createStack();
      replacement.add(listener.getControlSequence("includegraphics"));

      if (!keyValList.isEmpty())
      {
         replacement.add(listener.getOther('['));
         replacement.add(keyValList);
         replacement.add(listener.getOther(']'));
      }

      Group grp = listener.createGroup();
      grp.add(file, true);

      replacement.add(grp);

      listener.getTeXApp().substituting(parser,
        original.toString(), replacement.toString(parser));

      TeXParserUtils.process(replacement, parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
