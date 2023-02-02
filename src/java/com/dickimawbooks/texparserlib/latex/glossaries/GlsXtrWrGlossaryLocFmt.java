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

public class GlsXtrWrGlossaryLocFmt extends ControlSequence
{
   public GlsXtrWrGlossaryLocFmt()
   {
      this("glsxtrwrglossarylocfmt");
   }

   public GlsXtrWrGlossaryLocFmt(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsXtrWrGlossaryLocFmt(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject location = popArg(parser, stack);
      TeXObject title = popArg(parser, stack);

      parser.startGroup();

      ControlSequence cs = parser.getControlSequence("@@glsxtrwrglosscountermark");

      if (cs != null && !(cs instanceof AtGobble))
      {
         TeXObjectList substack = parser.getListener().createStack();

         substack.add(cs);
         substack.add(TeXParserUtils.createGroup(parser, (TeXObject)location.clone()));

         TeXParserUtils.process(substack, parser, stack);
      }

      parser.putControlSequence(true, new AtSecondOfTwo("glsxtr@wrglossarylocation"));

      TeXParserUtils.process(location, parser, stack);

      parser.endGroup();
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
