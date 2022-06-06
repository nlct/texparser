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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsXtrAppToCsvField extends AbstractGlsCommand
{
   public GlsXtrAppToCsvField(GlossariesSty sty)
   {
      this("glsxtrapptocsvfield", sty);
   }

   public GlsXtrAppToCsvField(String name, GlossariesSty sty)
   {
      this(name, false, sty);
   }

   public GlsXtrAppToCsvField(String name, boolean global, GlossariesSty sty)
   {
      super(name, sty);
      this.global = global;
   }

   public Object clone()
   {
      return new GlsXtrAppToCsvField(getName(), global, getSty());
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);

      String field = sty.getFieldName(popLabelString(parser, stack));

      TeXObject defn = popArg(parser, stack);

      GlossaryEntry entry = glslabel.getEntry();

      CsvList csvlist;

      if (entry == null)
      {
         String internalField = sty.getInternalFieldName(field);

         String csname = String.format("glo@%s@%s", 
            glslabel.getLabel(), internalField);

         if (defn instanceof CsvList)
         {
            csvlist = (CsvList)defn;
         }
         else
         {
            csvlist = CsvList.getList(parser, defn);
         }

         sty.getParser().putControlSequence(!global,
            new GenericCommand(true, csname, null, csvlist));
      }
      else
      {
         TeXObject val = entry.get(field);

         if (val == null)
         {
            if (defn instanceof CsvList)
            {
               csvlist = (CsvList)defn;
            }
            else
            {
               csvlist = CsvList.getList(parser, defn);
            }
         }
         else
         {
            if (val instanceof CsvList)
            {
               csvlist = (CsvList)val;
            }
            else
            {
               csvlist = CsvList.getList(parser, val);
            }

            csvlist.addAll(CsvList.getList(parser, defn));
         }

         entry.setField(field, csvlist, !global, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected boolean global = false;
}
