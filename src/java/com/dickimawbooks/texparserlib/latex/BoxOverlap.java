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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class BoxOverlap extends Command
{
   public BoxOverlap(int overlappingCodePoint)
   {
      this("texparser@boxoverlap@"+overlappingCodePoint, overlappingCodePoint);
   }

   public BoxOverlap(String name, int overlappingCodePoint)
   {
      this(name, overlappingCodePoint, "texparser@overlapper");
   }

   public BoxOverlap(int overlappingCodePoint, String overlapperCsName)
   {
      this("texparser@boxoverlap@"+overlappingCodePoint+"@"+overlapperCsName,
        overlappingCodePoint, overlapperCsName);
   }

   public BoxOverlap(String name, int overlappingCodePoint, String overlapperCsName)
   {
      super(name);
      this.overlappingCodePoint = overlappingCodePoint;
      this.overlapperCsName = overlapperCsName;
   }

   @Override
   public Object clone()
   {
      return new BoxOverlap(getName(), overlappingCodePoint, overlapperCsName);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      TeXObjectList expanded = parser.getListener().createStack();

      ControlSequence cs = parser.getListener().getControlSequence("texparser@overlapped");

      expanded.add(cs);

      Group grp = parser.getListener().createGroup();
      expanded.add(grp);

      grp.add(arg);

      cs = parser.getListener().getControlSequence(overlapperCsName);

      expanded.add(cs);
      expanded.add(parser.getListener().getOther(overlappingCodePoint));

      return expanded;
   }

   private int overlappingCodePoint;
   private String overlapperCsName = "texparser@overlapper";
}
