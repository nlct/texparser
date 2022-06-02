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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class PrintUnsrtGlossaries extends AbstractGlsCommand
{
   public PrintUnsrtGlossaries(GlossariesSty sty)
   {
      this("printunsrtglossaries", sty);
   }

   public PrintUnsrtGlossaries(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new PrintUnsrtGlossaries(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      Vector<String> types = getSty().getNonIgnoredGlossaries();

      ControlSequence cs = listener.getControlSequence("printunsrtglossary");

      for (int i = types.size()-1; i >= 0; i--)
      {
         KeyValList options = new KeyValList();
         options.put("type", listener.createDataList(types.get(i)));

         list.add(cs);
         list.add(listener.getOther('['));
         list.add(options);
         list.add(listener.getOther(']'));
      }

      return list;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      Vector<String> types = getSty().getNonIgnoredGlossaries();

      ControlSequence cs = listener.getControlSequence("printunsrtglossary");

      for (int i = types.size()-1; i >= 0; i--)
      {
         KeyValList options = new KeyValList();
         options.put("type", listener.createDataList(types.get(i)));

         stack.push(options);
         stack.push(cs);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
