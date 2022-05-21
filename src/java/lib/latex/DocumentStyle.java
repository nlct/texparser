/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class DocumentStyle extends ControlSequence
{
   public DocumentStyle()
   {
      this("documentstyle");
   }

   public DocumentStyle(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new DocumentClass();
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject options = popOptArg(parser, stack);

      String clsName = popLabelString(parser, stack);

      KeyValList keyValList = null;

      if (options != null)
      {
         keyValList = KeyValList.getList(parser, options);
      }

      process(parser, keyValList, clsName, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, KeyValList keyValList, String clsName,
    TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      Vector<String> styList = new Vector<String>(keyValList.size());

      Set<String> keys = keyValList.keySet();

      for (Iterator<String> it = keys.iterator(); it.hasNext(); )
      {
         String key = it.next();

         try
         {
            String loc = listener.getTeXApp().kpsewhich(key+".sty");

            if (loc != null && !loc.isEmpty())
            {
               keyValList.remove(key);
               styList.add(key);
            }
         }
         catch (InterruptedException e)
         {
         }
      }

      listener.substituting(toString(parser), (new DocumentClass()).toString(parser));
      listener.documentclass(keyValList, clsName, false, stack);

      for (String sty : styList)
      {
         listener.usepackage(null, sty, false, stack);
      }
   }
}
