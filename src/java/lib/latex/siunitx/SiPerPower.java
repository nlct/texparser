/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

public class SiPerPower extends ControlSequence
{
   public SiPerPower(SIunitxSty sty, String name, int power)
   {
      super(name);
      this.sty = sty;
      this.power = power;
   }

   public Object clone()
   {
      return new SiPerPower(sty, getName(), power);
   }

   public int getPower()
   {
      return power;
   }

   protected void process(TeXParser parser, TeXObjectList stack,
     TeXObject prefix, TeXObject arg)
    throws IOException
   {
      TeXObject nextObj = stack.peekStack();

      if (nextObj instanceof SIUnitCs)
      {
         stack.push(sty.createUnitSep(parser));
      }

      Group grp = parser.getListener().createGroup();
      stack.push(grp);

      if (parser.isMathMode())
      {
         grp.add(new UserNumber(-power));
         stack.push(parser.getListener().createSpChar());
      }
      else
      {
         grp.add(new TeXCsRef("textminus"));
         grp.add(new UserNumber(power));
         stack.push(new TeXCsRef("textsuperscript"));
      }

      stack.push(arg);

      if (prefix != null)
      {
         stack.push(prefix);
      }
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject arg = parser.popNextArg();

      TeXObject prefix = null;

      if (arg instanceof SIPrefixCs)
      {
         prefix = arg;
         arg = parser.popNextArg();
      }

      process(parser, parser, prefix, arg);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      TeXObject prefix = null;

      if (arg instanceof SIPrefixCs)
      {
         prefix = arg;
         arg = stack.popArg(parser);
      }

      process(parser, stack, prefix, arg);
   }

   protected SIunitxSty sty;
   private int power;
}
