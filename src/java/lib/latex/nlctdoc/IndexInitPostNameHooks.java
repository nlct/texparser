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
import java.awt.Color;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class IndexInitPostNameHooks extends ControlSequence
{
   public IndexInitPostNameHooks(UserGuideSty sty)
   {
      this("nlctguideindexinitpostnamehooks", sty);
   }

   public IndexInitPostNameHooks(String name, UserGuideSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new IndexInitPostNameHooks(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      sty.initIndexPostNameHooks();
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   UserGuideSty sty;
}
