/*
    Copyright (C) 2013 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class PreTo extends ControlSequence
{
   public PreTo()
   {
      this("preto");
   }

   public PreTo(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new PreTo(getName());
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      TeXObject arg = (list == parser ? parser.popNextArg():list.popArg(parser));

      if (arg instanceof TeXCsRef)
      {
         arg = parser.getListener().getControlSequence(((TeXCsRef)arg).getName());
      }

      if (!(arg instanceof ControlSequence))
      {
         throw new LaTeXSyntaxException(parser,
           LaTeXSyntaxException.ERROR_UNACCESSIBLE,
           arg.toString(parser));
      }

      ControlSequence hook = (ControlSequence)arg;

      TeXObject code = (list == parser ? parser.popNextArg():list.popArg(parser));

      TeXObjectList syntax = hook.getSyntax();
      TeXObjectList defn = new TeXObjectList();
      defn.add(code);

      if (hook instanceof GenericCommand)
      {
         for (TeXObject obj : ((GenericCommand)hook).getDefinition())
         {
            defn.add(obj);
         }
      }
      else if (hook instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)hook).expandonce(parser, list);

         if (expanded == null)
         {
            defn.add(hook);
         }
         else
         {
            for (TeXObject obj : expanded)
            {
               defn.add(obj);
            }
         }
      }
      else
      {
         defn.add(parser.getListener().createUndefinedCs(hook.toString(parser)));
      }

      GenericCommand cs = new GenericCommand(
        hook.isShort(), hook.getName(), syntax, defn);

      parser.putControlSequence(getPrefix() != PREFIX_GLOBAL, cs);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
