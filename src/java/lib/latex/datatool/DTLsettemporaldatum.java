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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

public class DTLsettemporaldatum extends ControlSequence
{
   public DTLsettemporaldatum(DataToolBaseSty sty)
   {
      this("DTLsettemporaldatum", sty);
   }

   public DTLsettemporaldatum(String name, DataToolBaseSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DTLsettemporaldatum(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = TeXParserUtils.popControlSequence(parser, stack);
      String csname = cs.getName();
      TeXObject fmtValue = popArg(parser, stack);
      String iso = popLabelString(parser, stack);

      try
      {
         Julian julian = Julian.create(iso);

         DatumElement elem = julian.toDatumElement(parser.getListener(), fmtValue,
           false);

         parser.putControlSequence(true, DatumCommand.create(parser, csname, elem));
      }
      catch (IllegalArgumentException e)
      {
         throw new LaTeXSyntaxException(e, parser, 
          DataToolBaseSty.INVALID_DATE_TIME, iso);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataToolBaseSty sty;
}
