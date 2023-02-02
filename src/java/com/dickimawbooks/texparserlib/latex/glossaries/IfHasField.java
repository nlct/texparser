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

public class IfHasField extends AbstractGlsCommand
{
   public IfHasField(String name, GlossariesSty sty)
   {
      this(name, null, sty);
   }

   public IfHasField(String name, String field, GlossariesSty sty)
   {
      super(name, sty);
      this.field = field;
   }

   @Override
   public Object clone()
   {
      return new IfHasField(getName(), getField(), getSty());
   }

   protected TeXObject expand(GlsLabel glslabel, String fieldLabel,
     TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject value = getFieldValue(glslabel, fieldLabel);

      TeXObject trueCode = popArg(parser, stack);
      TeXObject falseCode = popArg(parser, stack);

      if (value != null)
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

      String fieldLabel = field;

      if (field == null)
      {
         fieldLabel = sty.getFieldName(popLabelString(parser, stack));
      }

      TeXObject obj = expand(glslabel, fieldLabel, parser, stack);

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

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);

      String fieldLabel = field;

      if (field == null)
      {
         fieldLabel = sty.getFieldName(popLabelString(parser, stack));
      }

      TeXObject obj = expand(glslabel, fieldLabel, parser, stack);

      TeXObjectList list = null;

      if (obj instanceof Expandable)
      {
         if (parser == stack || stack == null)
         {
            list = ((Expandable)obj).expandfully(parser);
         }
         else
         {
            list = ((Expandable)obj).expandfully(parser, stack);
         }
      }

      if (list == null)
      {
         list = parser.getListener().createStack();
         list.add(obj);
      }

      return list;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);

      String fieldLabel = field;

      if (field == null)
      {
         fieldLabel = sty.getFieldName(popLabelString(parser, stack));
      }

      TeXObject obj = expand(glslabel, fieldLabel, parser, stack);

      TeXParserUtils.process(obj, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public String getField()
   {
      return field;
   }

   protected String field;
}
