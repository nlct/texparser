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

public class SiPer extends ControlSequence
{
   public SiPer(SIunitxSty sty)
   {
      this(sty, "per");
   }

   public SiPer(SIunitxSty sty, String name)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new SiPer(sty, getName());
   }

   protected void process(TeXParser parser, TeXObjectList stack,
     SiPrePower perPower, TeXObject prefix, TeXObject arg, SiPower postPower)
    throws IOException
   {
      TeXObject nextObj = stack.peekStack();

      if (nextObj instanceof SIUnitCs || arg instanceof SIPrefixCs)
      {
         stack.push(sty.createUnitSep(parser));
      }

      int power = -1;

      if (perPower != null)
      {
         power = -perPower.getPower();
      }

      if (postPower != null)
      {
         power *= postPower.getPower();
      }

      Group grp = parser.getListener().createGroup();
      stack.push(grp);

      if (parser.isMathMode())
      {
         grp.add(new UserNumber(power));
         stack.push(parser.getListener().createSpChar());
      }
      else
      {
         if (power < 0)
         {
            grp.add(new TeXCsRef("textminus"));
            grp.add(new UserNumber(-power));
         }
         else
         {
            grp.add(new UserNumber(power));
         }

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
      SiPrePower perPower = null;
      SiPower postPower = null;

      if (arg instanceof SiPrePower)
      {
         perPower = (SiPrePower)arg;
         arg = parser.popNextArg();
      }

      if (arg instanceof SIPrefixCs)
      {
         prefix = arg;
         arg = parser.popNextArg();
      }

      TeXObject nextArg = parser.peekStack();

      if (nextArg instanceof SiPower)
      {
         postPower = (SiPower)parser.popNextArg();
      }

      process(parser, parser, perPower, prefix, arg, postPower);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      TeXObject prefix = null;
      SiPrePower perPower = null;
      SiPower postPower = null;

      if (arg instanceof SiPrePower)
      {
         perPower = (SiPrePower)arg;
         arg = stack.popArg(parser);
      }

      if (arg instanceof SIPrefixCs)
      {
         prefix = arg;
         arg = stack.popArg(parser);
      }

      TeXObject nextArg = stack.peekStack();

      if (nextArg instanceof SiPower)
      {
         postPower = (SiPower)stack.popArg(parser);
      }

      process(parser, stack, perPower, prefix, arg, postPower);
   }

   private SIunitxSty sty;
}
