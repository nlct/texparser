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

public class GlsXtrTaggedList extends ControlSequence
{
   public GlsXtrTaggedList()
   {
      this("glsxtrtaggedlist");
   }

   public GlsXtrTaggedList(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new GlsXtrTaggedList(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject singularTag = popArg(parser, stack);
      TeXObject pluralTag = popArg(parser, stack);
      String prefix = popLabelString(parser, stack);
      String labelList = popLabelString(parser, stack);

      if (labelList.isEmpty())
      {
         return;
      }

      String[] labels = labelList.split(",");

      parser.startGroup();

      TeXParserListener listener = parser.getListener();
      TeXObjectList content = listener.createStack();

      if (labels.length == 1)
      {
         content.add(singularTag);
      }
      else
      {
         content.add(pluralTag);
      }

      content.add(listener.getControlSequence("glsxtrtaggedlistsep"));
      content.add(listener.getControlSequence("glsseelist"));
      Group grp;

      if (prefix.isEmpty() || labels.length == 1)
      {
         grp = listener.createGroup(prefix+labelList);
      }
      else
      {
         grp = listener.createGroup(prefix+labels[0]);

         for (int i = 1; i < labels.length; i++)
         {
            grp.addAll(listener.createString(","+prefix+labels[i]));
         }
      }

      content.add(grp);

      TeXParserUtils.process(content, parser, stack);

      parser.endGroup();
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
