/*
    Copyright (C) 2023 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class L2HMathJaxCommand extends ControlSequence
{
   public L2HMathJaxCommand(ControlSequence cs)
   {
      this(cs, cs.getName());
   }

   public L2HMathJaxCommand(TeXObject object, String name)
   {
      this(object, name, name);
   }

   public L2HMathJaxCommand(TeXObject object, String csname, String mathJaxName)
   {
      super(csname);
      this.underlying = object;
      this.mathJaxName = mathJaxName;
   }

   public TeXObject getUnderlying()
   {
      return underlying;
   }

   @Override
   public Object clone()
   {
      return new L2HMathJaxCommand(getUnderlying(), getName(), mathJaxName);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (parser.isMathMode() && listener.useMathJax())
      {
         listener.write(String.format("\\%s ", mathJaxName));
      }
      else
      {
         underlying.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (parser.isMathMode() && listener.useMathJax())
      {
         listener.write(String.format("\\%s ", mathJaxName));
      }
      else
      {
         underlying.process(parser);
      }
   }

   protected TeXObject underlying;
   protected String mathJaxName;
}
