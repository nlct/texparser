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
package com.dickimawbooks.texparserlib.latex.mfirstuc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.latex.*;

public class MFUhyphencapword extends ControlSequence
{
   public MFUhyphencapword()
   {
      this("MFUhyphencapword");
   }

   public MFUhyphencapword(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new MFUhyphencapword(getName());
   }

   public boolean isHyphen(TeXObject object)
   {
      return (object instanceof CharObject
             && ((CharObject)object).getCharCode() == '-');
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      TeXObjectList expanded = parser.getListener().createStack();

      if (parser.isStack(arg))
      {
         TeXObjectList argList = (TeXObjectList)arg;
         TeXObjectList word = parser.getListener().createStack();

         for (TeXObject obj : argList)
         {
            if (isHyphen(obj))
            {
               if (!word.isEmpty())
               {
                  expanded.add(new TeXCsRef("MFUcapword"));
                  expanded.add(TeXParserUtils.createGroup(parser, word));
               }

               expanded.add(obj);
               word = parser.getListener().createStack();
            }
            else
            {
               word.add(obj);
            }
         }

         if (!word.isEmpty())
         {
            expanded.add(new TeXCsRef("MFUcapword"));
            expanded.add(TeXParserUtils.createGroup(parser, word));
         }
      }
      else
      {
         expanded.add(new TeXCsRef("MFUcapword"));
         expanded.add(TeXParserUtils.createGroup(parser, arg));
      }

      TeXParserUtils.process(expanded, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
