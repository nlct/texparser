/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class GlsCsName extends Command
{
   public GlsCsName()
   {
      this("glscsname");
   }

   public GlsCsName(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new GlsCsName(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject options = popOptArg(parser, stack);
      TeXObject csname = popArgExpandFully(parser, stack);

      TeXObjectList expanded = listener.createStack();

      expanded.add(new TeXCsRef("glslink"));

      if (options != null && !options.isEmpty())
      {
         expanded.add(listener.getOther('['));
         expanded.add(options, true);
         expanded.add(listener.getOther(']'));
      }

      Group grp = listener.createGroup();
      expanded.add(grp);

      if (parser.isStack(csname))
      {
         for (TeXObject obj : (TeXObjectList)csname)
         {
            if (!TeXParserUtils.isControlSequence(obj, "_"))
            {
               grp.add(obj);
            }
         }
      }
      else
      {
         grp.add(csname);
      }

      grp = listener.createGroup();
      expanded.add(grp);

      grp.add(listener.getControlSequence("csfmtcolourfont"));
      grp.add(TeXParserUtils.createGroup(listener, csname));

      return expanded;
   }

}
