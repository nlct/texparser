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

public class SiPower extends ControlSequence
{
   public SiPower(SIunitxSty sty, String name, int power)
   {
      super(name);
      this.sty = sty;
      this.power = power;
   }

   public Object clone()
   {
      return new SiPower(sty, getName(), power);
   }

   public int getPower()
   {
      return power;
   }

   protected TeXObject getPowerObject(int pow)
   {
      return new UserNumber(pow);
   }

   protected void process(TeXParser parser, TeXObjectList stack,
     int pow, TeXObject prefix, TeXObject arg)
    throws IOException
   {
      TeXObject nextObj = stack.peekStack();

      if (nextObj instanceof SIUnitCs)
      {
         stack.push(sty.createUnitSep(parser));
      }

      stack.push(getPowerObject(pow));

      if (parser.isMathMode())
      {
         stack.push(parser.getListener().createSpChar());
      }
      else
      {
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

      int pow = power;

      if (arg instanceof SiPower)
      {
         pow *= ((SiPower)arg).getPower();
         arg = parser.popNextArg();
      }

      TeXObject prefix = null;

      if (arg instanceof SIPrefixCs)
      {
         prefix = arg;
         arg = parser.popNextArg();
      }

      process(parser, parser, pow, prefix, arg);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      int pow = power;

      if (arg instanceof SiPower)
      {
         pow *= ((SiPower)arg).getPower();
         arg = stack.popArg(parser);
      }

      TeXObject prefix = null;

      if (arg instanceof SIPrefixCs)
      {
         prefix = arg;
         arg = stack.popArg(parser);
      }

      process(parser, stack, pow, prefix, arg);
   }


   protected SIunitxSty sty;
   private int power;
}
