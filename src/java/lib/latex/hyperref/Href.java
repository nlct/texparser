/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

   @Override
   public Object clone()
   {
      return new Href(getName(), sty);
   }

   protected void process(TeXParser parser, TeXObject url, TeXObject text)
     throws IOException
   {
      parser.getListener().href(sty.toFullUrl(url.toString(parser)), text);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      popOptArg(parser, stack);

      TeXObject urlArg = popArgExpandFully(parser, stack);

      TeXObject text = popArgExpandFully(parser, stack);
      
      if (urlArg instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)urlArg;

         for (int i = 0; i < list.size(); i++)
         {
            TeXObject obj = list.get(i);

            if (obj instanceof CharObject 
                  && ((CharObject)obj).getCharCode()==0x00A0)
            {
               list.set(i, parser.getListener().getOther('~'));
            }
         }
      }

      process(parser, urlArg, text);
   }

   private HyperrefSty sty;
}
