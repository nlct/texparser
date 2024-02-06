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
import java.text.DecimalFormat;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLdefcurrency extends ControlSequence implements CatCodeChanger
{
   public DTLdefcurrency(DataToolBaseSty sty)
   {
      this("DTLdefcurrency", sty);
   }

   public DTLdefcurrency(String name, DataToolBaseSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DTLdefcurrency(getName(), sty);
   }

   @Override
   public void applyCatCodeChange(TeXParser parser)
      throws IOException
   {
      parser.setCatCode(true, '$', TeXParser.TYPE_OTHER);
   }

   @Override
   public ControlSequence getNoOpCommand()
   {
      return new GobbleOpt(getName(), 1, 3);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      parser.startGroup();

      TeXObject fmt = popOptArg(parser, stack);

      String label = popLabelString(parser, stack);

      TeXObject sym = popArg(parser, stack);

      applyCatCodeChange(parser);

      TeXObject str = popArg(parser, stack);

      parser.endGroup();

      sty.defineCurrency(fmt, label, sym, str);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private DataToolBaseSty sty;
}
