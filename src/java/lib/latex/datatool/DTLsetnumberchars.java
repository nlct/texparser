/*
    Copyright (C) 2023 Nicola L.C. Talbot
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

public class DTLsetnumberchars extends ControlSequence
{
   public DTLsetnumberchars()
   {
      this("DTLsetnumberchars");
   }

   public DTLsetnumberchars(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DTLsetnumberchars(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String numGrpChar = popLabelString(parser, stack);
      String decimalChar = popLabelString(parser, stack);

      parser.putControlSequence(true, 
        new TextualContentCommand("@dtl@numbergroupchar", numGrpChar));

      parser.putControlSequence(true, 
        new TextualContentCommand("@dtl@decimal", decimalChar));

      parser.putControlSequence(true, 
        new NumericFormatter(
          DataToolBaseSty.FMT_INTEGER_VALUE,
          new DecimalFormat("#"+numGrpChar+"##0"), decimalChar));

      parser.putControlSequence(true, 
        new NumericFormatter(
          DataToolBaseSty.FMT_DECIMAL_VALUE,
          new DecimalFormat("#"+numGrpChar+"##0"+decimalChar+"0######")));

      parser.putControlSequence(true, 
        new NumericFormatter(
          DataToolBaseSty.FMT_CURRENCY_VALUE,
          new DecimalFormat("#"+numGrpChar+"##0"+decimalChar+"00")));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
