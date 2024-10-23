/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HFontFamilyDeclaration extends FontFamilyDeclaration 
{
   public L2HFontFamilyDeclaration(String name, int family)
   {
      super(name, family);
   }

   public L2HFontFamilyDeclaration(String name, TeXFontFamily family)
   {
      super(name, family);
   }

   @Override
   public Object clone()
   {
      return new L2HFontFamilyDeclaration(getName(), getFamily());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      super.process(parser);

      L2HConverter listener = (L2HConverter)parser.getListener();

      String spec = "";
      String tag = "span";

      switch (getFamily())
      {
         case RM:
            spec = "font-family: "+listener.getSerifCssFontNames()+"; ";
         break;
         case SF:
            spec = "font-family: "+listener.getSansSerifCssFontNames()+"; ";
         break;
         case TT:
            spec = "font-family: "+listener.getMonospaceCssFontNames()+"; ";
         break;
         case VERB:
            tag = "code";
         return;
      }

      if (spec.isEmpty())
      {
         listener.writeliteral(String.format("<%s>", tag));
      }
      else
      {
         parser.getListener().getWriteable().writeliteral(
           String.format("<%s style=\"%s\">", tag, spec));
      }
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      if (getFamily() == TeXFontFamily.VERB)
      {
         parser.getListener().getWriteable().writeliteral("</code>");
      }
      else
      {
         parser.getListener().getWriteable().writeliteral("</span>");
      }

      super.end(parser, stack);
   }

}
