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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsIfAttribute extends AbstractGlsCommand
{
   public GlsIfAttribute(String name, GlossariesSty sty)
   {
      this(name, null, sty);
   }

   public GlsIfAttribute(String name, String attribute, GlossariesSty sty)
   {
      super(name, sty);
      this.attribute = attribute;
   }

   @Override
   public Object clone()
   {
      return new GlsIfAttribute(getName(), getAttribute(), getSty());
   }

   protected TeXObject expand(GlsLabel glslabel, String attributeLabel,
     TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      String attrValue = popLabelString(parser, stack);
      TeXObject trueCode = popArg(parser, stack);
      TeXObject falseCode = popArg(parser, stack);

      if (sty.isAttributeValue(glslabel, attributeLabel, attrValue))
      {
         return trueCode;
      }
      else
      {
         return falseCode;
      }
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);

      String attributeLabel = attribute;

      if (attribute == null)
      {
         attributeLabel = popLabelString(parser, stack);
      }

      TeXObject obj = expand(glslabel, attributeLabel, parser, stack);

      if (parser.isStack(obj))
      {
         return (TeXObjectList)obj;
      }
      else
      {
         TeXObjectList list = parser.getListener().createStack();
         list.add(obj);
         return list;
      }
   }

   public String getAttribute()
   {
      return attribute;
   }

   protected String attribute;
}
