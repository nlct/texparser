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

public class GlsDefHook extends ControlSequence
{
   public GlsDefHook(String name, String hookCsPrefix)
   {
      this(name, hookCsPrefix, true);
   }

   public GlsDefHook(String name, String hookCsPrefix, boolean allowEmptySuffix)
   {
      super(name);
      this.hookCsPrefix = hookCsPrefix;
      this.allowEmptySuffix = allowEmptySuffix;
   }

   public Object clone()
   {
      return new GlsDefHook(getName(), hookCsPrefix, allowEmptySuffix);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String suffix = popLabelString(parser, stack);
      TeXObject arg = popArg(parser, stack);

      if (!allowEmptySuffix && suffix.isEmpty())
      {
         throw new LaTeXSyntaxException(parser,
           GlossariesSty.EMPTY_CATEGORY_NOT_ALLOWED, toString(parser));
      }

      TeXObjectList def;

      if (parser.isStack(arg))
      {
         def = (TeXObjectList)arg;
      }
      else
      {
         def = parser.getListener().createStack();
         def.add(arg);
      }

      parser.putControlSequence(true,
         new GenericCommand(true, hookCsPrefix+suffix, null, def));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private String hookCsPrefix;
   private boolean allowEmptySuffix;
}
