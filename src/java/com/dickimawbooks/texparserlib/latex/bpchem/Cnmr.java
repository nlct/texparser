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
package com.dickimawbooks.texparserlib.latex.bpchem;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Cnmr extends ControlSequence
{
   public Cnmr()
   {
      this("CNMR");
   }

   public Cnmr(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Cnmr(getName());
   }


   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      TeXParserListener listener = parser.getListener();

      list.push(new TeXCsRef("xspace"));

      MathGroup mgrp = listener.createMathGroup();
      mgrp.setInLine(true);
      mgrp.add(new TeXCsRef("delta"));

      list.push(mgrp);

      list.push(listener.getSpace());
      list.push(listener.getOther(':'));

      Group grp = listener.createGroup();
      grp.add(new TeXCsRef("^"));
      grp.add(listener.createGroup("13"));
      grp.add(listener.getLetter('C'));
      grp.add(listener.getOther('-'));
      grp.add(listener.getLetter('N'));
      grp.add(listener.getLetter('M'));
      grp.add(listener.getLetter('R'));

      list.push(grp);

      list.push(new TeXCsRef("IUPAC"));
   }

}
