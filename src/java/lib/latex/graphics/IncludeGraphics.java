/*
    Copyright (C) 2013-2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.graphics;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class IncludeGraphics extends ControlSequence
{
   public IncludeGraphics()
   {
      this("includegraphics");
   }

   public IncludeGraphics(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new IncludeGraphics(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      boolean isStar = popModifier(parser, stack, '*') == '*';

      TeXObject opt = popOptArg(parser, stack);
      KeyValList keyValList = null;

      if (opt instanceof KeyValList)
      {
         keyValList = (KeyValList)opt;
      }

      if (opt != null && parser.isStack(opt))
      {
         TeXObjectList list = (TeXObjectList)opt;

         if (list.size() == 1)
         {
            TeXObject firstElem = list.firstElement();

            if (firstElem instanceof KeyValList)
            {
               keyValList = (KeyValList)list.firstElement();
            }
         }
      }

      if (keyValList == null && opt != null && !opt.isEmpty())
      {
         TeXObjectList optList = TeXParserUtils.toList(opt, parser);

         TeXObject opt2 = popOptArg(parser, stack);

         if (opt2 == null && optList.contains(listener.getOther('=')))
         {
            keyValList = KeyValList.getList(parser, opt);
         }
         else
         {
            keyValList = new KeyValList();
            TeXDimension dim = optList.popDimension(parser);

            keyValList.put("bbllx", dim);

            TeXObject obj = optList.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);

            if (!(obj instanceof SingleToken
                 && ((SingleToken)obj).getCharCode() == ','))
            {
               throw new TeXSyntaxException(parser,
                 TeXSyntaxException.ERROR_NOT_FOUND, ",");
            }

            dim = optList.popDimension(parser);
            keyValList.put("bblly", dim);

            if (opt2 != null)
            {
               optList = TeXParserUtils.toList(opt2, parser);

               dim = optList.popDimension(parser);
               keyValList.put("bburx", dim);

               obj = optList.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);

               if (!(obj instanceof SingleToken
                    && ((SingleToken)obj).getCharCode() == ','))
               {
                  throw new TeXSyntaxException(parser,
                    TeXSyntaxException.ERROR_NOT_FOUND, ",");
               }

               dim = optList.popDimension(parser);
               keyValList.put("bbury", dim);
            }
         }
      }

      if (isStar)
      {
         if (keyValList == null)
         {
            keyValList = new KeyValList();
         }

         keyValList.put("clip", new MissingValue());
      }

      String imgName = popLabelString(parser, stack);

      listener.includegraphics(stack, keyValList, imgName);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
