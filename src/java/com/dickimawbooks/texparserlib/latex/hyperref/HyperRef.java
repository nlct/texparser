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
package com.dickimawbooks.texparserlib.latex.hyperref;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.LaTeXParserListener;

public class HyperRef extends ControlSequence
{
   public HyperRef(HyperrefSty sty)
   {
      this("hyperref", sty);
   }

   public HyperRef(String name, HyperrefSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new HyperRef(getName(), sty);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String label = popOptLabelString(parser, stack);

      if (label == null)
      {// syntax \hyperref{URL}{category}{name}{text}

         TeXObject urlArg = popArgExpandFully(parser, stack);

         String category = popLabelString(parser, stack);
         String name = popLabelString(parser, stack);

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

         String url = String.format("%s#%s.%s", urlArg.toString(parser),
           category, name);

         listener.href(sty.toFullUrl(url), text);
      }
      else
      {// syntax \hyperref[label]{text}
      
         TeXObject link = listener.createLink(label, popArgExpandFully(parser, stack));

         if (parser == stack || stack == null)
         {
            link.process(parser);
         }
         else
         {
            link.process(parser, stack);
         }
      }
   }

   private HyperrefSty sty;
}
