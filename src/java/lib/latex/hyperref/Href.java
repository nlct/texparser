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
package com.dickimawbooks.texparserlib.latex.hyperref;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class Href extends ControlSequence
{
   public Href(HyperrefSty sty)
   {
      this("href", sty);
   }

   public Href(String name, HyperrefSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new Href(getName(), sty);
   }

   protected void process(TeXParser parser, TeXObject url, TeXObject text)
     throws IOException
   {
      parser.getListener().href(url.toString(parser), text);
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser.popNextArg(), parser.popNextArg());
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      process(parser, list.popArg(parser), list.popArg(parser));
   }

   private HyperrefSty sty;
}
