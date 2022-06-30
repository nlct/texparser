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

public class GlsXtrGetGroupTitle extends ControlSequence
{
   public GlsXtrGetGroupTitle()
   {
      this("glsxtrgetgrouptitle");
   }

   public GlsXtrGetGroupTitle(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new GlsXtrGetGroupTitle(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String grpLabel = popLabelString(parser, stack);
      ControlSequence cs = popControlSequence(parser, stack);
      String csname = cs.getName();

      ControlSequence grpTitleCs = parser.getControlSequence(
        "glsxtr@grouptitle@"+grpLabel);

      if (grpTitleCs == null)
      {
         if (grpLabel.length() == 1
             || grpLabel.equals("glssymbols") || grpLabel.equals("glsnumbers"))
         {
            grpTitleCs = parser.getControlSequence(grpLabel+"groupname");
         }
      }

      if (grpTitleCs == null)
      {
          parser.putControlSequence(true, 
             new TextualContentCommand(csname, grpLabel));
      }
      else
      {
          parser.putControlSequence(true, 
             new AssignedControlSequence(csname, grpTitleCs));
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
