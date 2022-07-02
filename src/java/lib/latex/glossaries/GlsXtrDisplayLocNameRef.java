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

public class GlsXtrDisplayLocNameRef extends Command
{
   public GlsXtrDisplayLocNameRef()
   {
      this("glsxtrdisplaylocnameref");
   }

   public GlsXtrDisplayLocNameRef(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsXtrDisplayLocNameRef(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      String prefix = popLabelString(parser, stack);
      String counter = popLabelString(parser, stack);
      String format = popLabelString(parser, stack);

      TeXObject loc = popArg(parser, stack);

      TeXObject title = popArg(parser, stack);

      String anchor = popLabelString(parser, stack);

      String hloc = popLabelString(parser, stack);
      String externalFile = popLabelString(parser, stack);

      ControlSequence hyperCs = parser.getControlSequence("hyperlink");

      TeXObjectList expanded = listener.createStack();

      if (hyperCs == null)
      {
         expanded.add(listener.getControlSequence(format));
         Group grp = listener.createGroup();
         expanded.add(grp);
         grp.add(loc);
      }
      else
      {
         ControlSequence recentAnchor = new TextualContentCommand(
            "glsxtrrecentanchor", anchor);
         ControlSequence locAnchor = new TextualContentCommand(
            "glsxtrlocationanchor", counter+"."+hloc);

         parser.putControlSequence(true, recentAnchor);
         parser.putControlSequence(true, locAnchor);

         expanded.add(listener.getControlSequence("glsxtrsetactualanchor"));
         expanded.add(listener.createGroup(counter));

         ControlSequence cs = parser.getControlSequence("glsxtr"+counter+"locfmt");

         if (cs == null)
         {
            if (title.isEmpty() || counter.equals("page"))
            {
               expanded.add(listener.getControlSequence("glsxtrnamereflink"));
               expanded.add(listener.createGroup(format));

               Group grp = listener.createGroup();
               expanded.add(grp);

               grp.add(loc);
               expanded.add(new TeXCsRef("glsxtractualanchor"));
            }
            else
            {
               expanded.add(listener.getControlSequence("glsxtrtitlednamereflink"));
               expanded.add(listener.createGroup(format));

               expanded.add(TeXParserUtils.createGroup(listener, loc));
               expanded.add(TeXParserUtils.createGroup(listener, title));
            }

            expanded.add(listener.createGroup(externalFile));
         }
         else
         {
            expanded.add(listener.getControlSequence("glsxtrnamereflink"));
            expanded.add(listener.createGroup(format));

            Group grp = listener.createGroup();
            expanded.add(grp);

            grp.add(cs);

            Group subgrp = listener.createGroup();
            grp.add(subgrp);

            subgrp.add(loc);

            subgrp = listener.createGroup();
            grp.add(subgrp);

            subgrp.add(title);

            grp = listener.createGroup();
            expanded.add(grp);
            grp.add(new TeXCsRef("glsxtractualanchor"));
            expanded.add(listener.createGroup(externalFile));
         }
      }

      return expanded;
   }

}
