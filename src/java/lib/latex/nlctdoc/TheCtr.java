/*
    Copyright (C) 2024 Nicola L.C. Talbot
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

public class TheCtr extends Command
{
   public TheCtr()
   {
      this("thectr", "the");
   }

   public TheCtr(String name, String csNamePrefix)
   {
      super(name);
      this.csNamePrefix = csNamePrefix;
   }

   @Override
   public Object clone()
   {
      return new TheCtr(getName(), csNamePrefix);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      String ctrName = popLabelString(parser, stack);

      TeXObjectList expanded = listener.createStack();

      expanded.add(new TeXCsRef("glslink"));
      expanded.add(listener.createGroup("ctr."+ctrName));

      Group grp = listener.createGroup();
      expanded.add(grp);

      grp.add(new TeXCsRef("csfmt"));
      grp.add(listener.createGroup(csNamePrefix+ctrName));

      return expanded;
   }

   String csNamePrefix;
}
