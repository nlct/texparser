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

public class GlsXtrLoadResources extends ControlSequence
{
   public GlsXtrLoadResources()
   {
      this("GlsXtrLoadResources");
   }

   public GlsXtrLoadResources(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsXtrLoadResources(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXSettings settings = parser.getSettings();

      popOptArg(parser, stack);

      NumericRegister register = settings.getNumericRegister("glsxtrresourcecount");

      int n = register.number(parser);

      TeXObject basename;
      String jobname = parser.getJobname();

      if (n == 0)
      {
         basename = parser.getListener().createDataList(jobname+".glstex");
      }
      else
      {
         basename = parser.getListener().createDataList(jobname+".glstex"+n+".glstex");
      }

      stack.push(basename);

      if (parser == stack || stack == null)
      {
         parser.getListener().getControlSequence("input").process(parser);
      }
      else
      {
         parser.getListener().getControlSequence("input").process(parser, stack);
      }

      settings.globalAdvanceRegister("glsxtrresourcecount", UserNumber.ONE);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
