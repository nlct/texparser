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
package com.dickimawbooks.texparserlib.latex.hyperref;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class NoLinkUrl extends ControlSequence
{
   public NoLinkUrl()
   {
      this("nolinkurl");
   }

   public NoLinkUrl(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new NoLinkUrl(getName());
   }

   protected void process(TeXParser parser, TeXObjectList stack, TeXObject arg)
     throws IOException
   {
      TeXSettings settings = parser.getSettings();

      int family = settings.getCurrentFontFamily();
      settings.setFontFamily(TeXSettings.FAMILY_TT);

      TeXObjectList expanded = null;

      if (arg instanceof Expandable)
      {
         if (parser == stack)
         {
            expanded = ((Expandable)arg).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)arg).expandfully(parser, stack);
         }
      }

      if (expanded == null)
      {
         parser.getListener().getWriteable().write(arg.toString(parser));
      }
      else
      {
         parser.getListener().getWriteable().write(expanded.toString(parser));
      }

      settings.setFontFamily(family);
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser, parser.popNextArg());
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      process(parser, list, list.popArg(parser));
   }

}
