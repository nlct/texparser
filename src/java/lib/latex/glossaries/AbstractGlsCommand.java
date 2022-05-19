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
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public abstract class AbstractGlsCommand extends ControlSequence
{
   public AbstractGlsCommand(String name, GlossariesSty sty)
   {
      super(name);
      this.sty = sty;
   }

   protected GlossaryEntry getEntry(String label)
   {
      return sty.getEntry(label);
   }

   protected Glossary getGlossary(String label)
   {
      return sty.getGlossary(label);
   }

   protected KeyValList popModifier(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject object;

      if (stack == null)
      {
         object = parser.peekStack();
      }
      else
      {
         object = stack.peekStack();
      }

      if (object instanceof CharObject)
      {
         KeyValList options = sty.getModifierOptions((CharObject)object);

         if (options != null)
         {
            if (parser == stack || stack == null)
            {
               parser.popStack();
            }
            else
            {
               stack.popStack(parser);
            }
         }

         return options;
      }

      return null;
   }

   protected KeyValList popOptKeyValList(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return popOptKeyValList(parser, stack, false);
   }

   protected KeyValList popOptKeyValList(TeXParser parser, TeXObjectList stack,
     boolean checkModifier)
     throws IOException
   {
      KeyValList modOptions = null;

      if (checkModifier)
      {
         modOptions = popModifier(parser, stack);
      }

      KeyValList options = null;

      TeXObject arg = stack.peek();

      if (arg instanceof KeyValList)
      {
         stack.pop();
         options = (KeyValList)arg;
      }
      else
      {
         arg = popOptArg(parser, stack);

         if (arg != null)
         {
            options = KeyValList.getList(parser, arg);
         }
      }

      if (options == null)
      {
         options = modOptions;
      }
      else if (modOptions != null)
      {
         for (Iterator<String> it = modOptions.keySet().iterator(); it.hasNext(); )
         {
            String key = it.next();
            options.putIfAbsent(key, modOptions.get(key));
         }
      }

      return options;
   }

   protected GlsLabel popEntryLabel(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      if (arg instanceof GlsLabel)
      {
         ((GlsLabel)arg).refresh(sty);

         return (GlsLabel)arg;
      }

      String label = parser.expandToString(arg, stack);

      GlossaryEntry entry = getEntry(label);

      return new GlsLabel("@@glslabel", label, entry);
   }

   protected GlsType popGlossaryLabel(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      if (arg instanceof GlsType)
      {
         ((GlsType)arg).refresh(sty);

         return (GlsType)arg;
      }

      String label = parser.expandToString(arg, stack);

      Glossary glossary = getGlossary(label);

      return new GlsType("@@glstype", label, glossary);
   }

   public GlossariesSty getSty()
   {
      return sty;
   }

   protected GlossariesSty sty;
}
