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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;
import com.dickimawbooks.texparserlib.latex.AtFirstOfTwo;
import com.dickimawbooks.texparserlib.latex.AtSecondOfTwo;

public class IfToggle extends Command
{
   public IfToggle()
   {
      this("iftoggle");
   }

   public IfToggle(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new IfToggle(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String toggleName = popLabelString(parser, stack);
      TeXObject trueArg = popArg(parser, stack);
      TeXObject falseArg = popArg(parser, stack);

      String csname = "etb@tgl@"+toggleName;

      ControlSequence cs = parser.getControlSequence(csname);

      if (cs == null)
      {
         throw new LaTeXSyntaxException(parser,
          "etoolbox.toggle_not_defined", toggleName);
      }

      TeXObjectList expanded;

      if (cs instanceof AtFirstOfTwo)
      {
         if (parser.isStack(trueArg))
         {
            expanded = (TeXObjectList)trueArg;
         }
         else
         {
            expanded = TeXParserUtils.createStack(parser, trueArg);
         }
      }
      else if (cs instanceof AtSecondOfTwo)
      {
         if (parser.isStack(falseArg))
         {
            expanded = (TeXObjectList)falseArg;
         }
         else
         {
            expanded = TeXParserUtils.createStack(parser, falseArg);
         }
      }
      else
      {
         expanded = parser.getListener().createStack();

         expanded.add(cs);
         expanded.add(TeXParserUtils.createGroup(parser, trueArg));
         expanded.add(TeXParserUtils.createGroup(parser, falseArg));
      }

      return expanded;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

}
