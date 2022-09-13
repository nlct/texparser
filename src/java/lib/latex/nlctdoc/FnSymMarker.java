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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class FnSymMarker extends ControlSequence
{
   public FnSymMarker()
   {
      this("fnsymmarker");
   }

   public FnSymMarker(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new FnSymMarker(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      int num = popInt(parser, stack);

      String marker;

      switch (num)
      {
         case 0: marker = ""; break;
         case 1: marker = "\u2217"; break;// centred asterisk
         case 2: marker = "\u2020"; break;// dagger
         case 3: marker = "\u2021"; break;// double dagger
         case 4: marker = "\u00A7"; break;// section
         case 5: marker = "\u29EB"; break;// lozenge
         case 6: marker = "\u00B6"; break;// pilcrow
         case 7: marker = "#"; break;
         case 8: marker = "\u203B"; break;// reference mark
         case 9: marker = "\u2051"; break;// vertical double asterisk
         case 10: marker = "\u2605"; break;// star
         case 11: marker = "\u273E"; break;// six petalled B&W florette
         default: marker = ""+num;
      }

      listener.getWriteable().write(marker);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
