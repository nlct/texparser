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

public class GlsIfAttributeBool extends GlsIfAttribute
{
   public GlsIfAttributeBool(String name, GlossariesSty sty)
   {
      this(name, null, true, sty);
   }

   public GlsIfAttributeBool(String name, String attribute, boolean attrValue, GlossariesSty sty)
   {
      super(name, attribute, sty);
      this.attrValue = attrValue;
   }

   @Override
   public Object clone()
   {
      return new GlsIfAttributeBool(getName(), getAttribute(), attrValue, getSty());
   }

   protected TeXObject expand(GlsLabel glslabel, String attributeLabel,
     TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject trueCode = popArg(parser, stack);
      TeXObject falseCode = popArg(parser, stack);

      if (attrValue && sty.isAttributeTrue(glslabel, attributeLabel))
      {
         return trueCode;
      }
      else if (!attrValue && sty.isAttributeFalse(glslabel, attributeLabel))
      {
         return trueCode;
      }
      else
      {
         return falseCode;
      }
   }

   protected boolean attrValue;
}
