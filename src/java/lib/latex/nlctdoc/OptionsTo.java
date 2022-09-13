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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class OptionsTo extends ControlSequence
{
   public OptionsTo()
   {
      this("optionsto");
   }

   public OptionsTo(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new OptionsTo(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      String label1 = popLabelString(parser, stack);
      String label2 = popLabelString(parser, stack);

      String prefix = parser.expandToString(
        listener.getControlSequence("optionlistprefix"), stack);

      label1 = prefix+label1;
      label2 = prefix+label2;

      TeXObjectList content = listener.createStack();

      content.add(listener.getControlSequence("optionlisttags"));
      content.add(listener.getControlSequence("glsxtrtaggedlistsep"));
      content.add(listener.getControlSequence("glslink"));
      content.add(listener.createGroup(label1));

      Group grp = listener.createGroup();
      content.add(grp);

      grp.add(listener.getControlSequence("optionlistitemformat"));
      grp.add(listener.createGroup(label1));

      content.add(listener.getOther(0x2013));

      content.add(listener.getControlSequence("glslink"));
      content.add(listener.createGroup(label2));

      grp = listener.createGroup();
      content.add(grp);

      grp.add(listener.getControlSequence("optionlistitemformat"));
      grp.add(listener.createGroup(label2));

      TeXParserUtils.process(content, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
