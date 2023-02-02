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
      TeXParserListener listener = parser.getListener();

      int num = popInt(parser, stack);

      TeXObject marker = null;

      switch (num)
      {
         case 1: marker = listener.getControlSequence("asteriskmarker"); break;
         case 2: marker = listener.getControlSequence("daggermarker"); break;
         case 3: marker = listener.getControlSequence("doubledaggermarker"); break;
         case 4: marker = listener.getControlSequence("sectionmarker"); break;
         case 5: marker = listener.getControlSequence("lozengemarker"); break;
         case 6: marker = listener.getControlSequence("pilcrowmarker"); break;
         case 7: marker = listener.getControlSequence("hashmarker"); break;
         case 8: marker = listener.getControlSequence("referencemarker"); break;
         case 9: marker = listener.getControlSequence("vdoubleasteriskmarker"); break;
         case 10: marker = listener.getControlSequence("starmarker"); break;
         case 11: marker = listener.getControlSequence("florettemarker"); break;
         default: marker = listener.createString(""+num);
      }

      if (marker != null)
      {
         TeXParserUtils.process(marker, parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
