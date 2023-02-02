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

public class AtGlsAtHypergroup extends ControlSequence
{
   public AtGlsAtHypergroup()
   {
      this("@gls@hypergroup");
   }

   public AtGlsAtHypergroup(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new AtGlsAtHypergroup(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String type = popLabelString(parser, stack);
      String grpLabel = popLabelString(parser, stack);

      String csname = "@gls@hypergrouplist@"+type;
      ControlSequence cs = parser.getControlSequence(csname);

      if (cs == null || cs.isEmpty())
      {
         cs = new TextualContentCommand(csname, grpLabel);
         parser.putControlSequence(false, cs);
      }
      else if (cs instanceof TextualContentCommand)
      {
         TextualContentCommand tcc = (TextualContentCommand)cs;
         String text = tcc.getText();

         if (!(text.equals(grpLabel) || text.endsWith(","+grpLabel)))
         {
            text += ","+grpLabel;
            tcc.setText(text);
         }
      }
      else
      {
         String text = parser.expandToString(cs, stack);

         if (!(text.equals(grpLabel) || text.endsWith(","+grpLabel)))
         {
            cs = new TextualContentCommand(csname, text+","+grpLabel);
            parser.putControlSequence(false, cs);
         }
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
