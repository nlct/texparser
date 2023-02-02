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
package com.dickimawbooks.texparserlib.latex.siunitx;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class SIUnitCs extends ControlSequence
{
   public SIUnitCs(SIunitxSty sty, String name, String notation)
   {
      this(sty, name, notation, false);
   }

   public SIUnitCs(SIunitxSty sty, String name, String notation, 
      boolean isPrefix)
   {
      super(name);
      this.notation = notation;
      this.sty = sty;
      this.prefix = isPrefix;
   }

   public Object clone()
   {
      return new SIUnitCs(sty, getName(), notation, prefix);
   }

   public String getNotation()
   {
      return notation;
   }

   public void process(TeXParser parser) throws IOException
   {
      if (parser.isMathMode())
      {
         sty.createText(parser, 
          parser.getListener().createGroup(notation)).process(parser);
      }
      else
      {
         parser.getListener().getWriteable().write(notation);
      }

      TeXObject nextObj = parser.peekStack();

      if ((nextObj instanceof SIUnitCs && !prefix)
         || nextObj instanceof SiPer)
      {
         parser.push(sty.createUnitSep(parser));
      }
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (parser.isMathMode())
      {
         TeXObjectList list = sty.createText(parser, 
           parser.getListener().createGroup(notation));

         list.process(parser, stack);
      }
      else
      {
         parser.getListener().getWriteable().write(notation);
      }

      TeXObject nextObj = stack.peekStack();

      if ((nextObj instanceof SIUnitCs && !prefix)
         || nextObj instanceof SiPer)
      {
         stack.push(sty.createUnitSep(parser));
      }
   }

   private String notation;
   protected boolean prefix = false;
   protected SIunitxSty sty;
}
