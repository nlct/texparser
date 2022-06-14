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

public class GlsXtrPrependLabelPrefix extends ControlSequence
{
   public GlsXtrPrependLabelPrefix()
   {
      this("glsxtrprependlabelprefix");
   }

   public GlsXtrPrependLabelPrefix(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsXtrPrependLabelPrefix(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject prefix = popArg(parser, stack);

      if (prefix.isEmpty())
      {
         prefix = listener.getControlSequence("empty");
      }

      ControlSequence cs = parser.getControlSequence("@glsxtr@labelprefixes");

      TeXObjectList def;

      if (cs instanceof GenericCommand)
      {
         def = ((GenericCommand)cs).getDefinition();

         if (!def.isEmpty())
         {
            def.push(listener.getOther(','));
         }

         def.push(prefix);
      }
      else
      {
         String list = "";

         if (cs instanceof TextualContentCommand)
         {
            list = ((TextualContentCommand)cs).getText();
         }
         else if (cs != null)
         {
            list = parser.expandToString(cs, stack);
         }

         if (list.isEmpty())
         {
            if (prefix instanceof TeXObjectList)
            {
               def = (TeXObjectList)prefix;
            }
            else
            {
               def = listener.createStack();
               def.add(prefix);
            }
         }
         else
         {
            def = listener.createString(list);
            def.push(listener.getOther(','));
            def.push(prefix);
         }

         parser.putControlSequence(true,
               new GenericCommand(true, "@glsxtr@labelprefixes", null, def));
      }

   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
