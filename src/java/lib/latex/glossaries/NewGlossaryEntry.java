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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class NewGlossaryEntry extends AbstractGlsCommand
{
   public NewGlossaryEntry(GlossariesSty sty)
   {
      this("newglossaryentry", NewCommand.OVERWRITE_FORBID, sty);
   }

   public NewGlossaryEntry(String name, byte overwrite, GlossariesSty sty)
   {
      super(name, sty);
      this.overwrite = overwrite;
   }

   public Object clone()
   {
      return new NewGlossaryEntry(getName(), overwrite, getSty());
   }

   protected void defineEntry(String label, KeyValList keyValList, 
    TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject name = keyValList.get("name");

      if (name == null)
      {
         TeXObject parent = keyValList.getExpandedValue("parent", parser, stack);

         if (parent != null)
         {
            String parentLabel = parent.toString(parser);

            GlossaryEntry entry = getEntry(parentLabel);

            if (entry != null)
            {
               TeXObject parentName = entry.get("name");

               if (parentName != null)
               {
                  name = (TeXObject)parentName.clone();
               }
            }
         }

         if (name == null)
         {
            keyValList.put("name", new MissingValue());
         }
         else
         {
            keyValList.put("name", name);
         }
      }

      TeXObject text = keyValList.get("text");

      if (text == null)
      {
         text = (TeXObject)name.clone();
         keyValList.put("text", text);
      }

      TeXObject plural = keyValList.get("plural");

      if (plural == null)
      {
         plural = (TeXObject)text.clone();

         if (plural instanceof TeXObjectList)
         {
            ((TeXObjectList)plural).add(new TeXCsRef("glspluralsuffix"));
         }
         else
         {
            TeXObjectList list = new TeXObjectList();
            list.add(plural);
            list.add(new TeXCsRef("glspluralsuffix"));
            plural = list;
         }

         keyValList.put("plural", plural);
      }

      TeXObject first = keyValList.get("first");
      TeXObject firstplural = keyValList.get("firstplural");

      if (first == null)
      {
         first = (TeXObject)text.clone();
         keyValList.put("first", first);

         if (firstplural == null)
         {
            firstplural = (TeXObject)plural.clone();
            keyValList.put("first", firstplural);
         }
      }
      else if (firstplural == null)
      {
         firstplural = (TeXObject)first.clone();

         if (firstplural instanceof TeXObjectList)
         {
            ((TeXObjectList)firstplural).add(new TeXCsRef("glspluralsuffix"));
         }
         else
         {
            TeXObjectList list = new TeXObjectList();
            list.add(firstplural);
            list.add(new TeXCsRef("glspluralsuffix"));
            firstplural = list;
         }

         keyValList.put("firstplural", firstplural);
      }

      TeXObject symbol = keyValList.get("symbol");

      if (symbol != null)
      {
         TeXObject symbolplural = keyValList.get("symbolplural");

         if (symbolplural == null)
         {
            keyValList.put("symbolplural", (TeXObject)symbol.clone());
         }
      }

      sty.addEntry(overwrite, new GlossaryEntry(sty, label, keyValList));
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String label = popLabelString(parser, stack);

      TeXObject options = popArg(parser, stack);

      KeyValList keyValList = KeyValList.getList(parser, options);

      defineEntry(label, keyValList, parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected byte overwrite;
}
