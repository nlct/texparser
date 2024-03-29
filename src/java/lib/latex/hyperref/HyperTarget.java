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
package com.dickimawbooks.texparserlib.latex.hyperref;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class HyperTarget extends Command
{
   public HyperTarget()
   {
      this("hypertarget");
   }

   public HyperTarget(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new HyperTarget(getName());
   }

   protected TeXObject createAnchor(TeXParser parser, String target, TeXObject text)
    throws IOException
   {
      return parser.getListener().createAnchor(target, text);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String target = popLabelString(parser, stack);

      TeXObject text = popArg(parser, stack);

      TeXObjectList expanded = parser.getListener().createStack();

      expanded.add(createAnchor(parser, target, text));

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String target = popLabelString(parser, stack);

      TeXObject text = popArgExpandFully(parser, stack);

      TeXObjectList expanded = parser.getListener().createStack();

      expanded.add(createAnchor(parser, target, text));

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String target = popLabelString(parser, stack);
      TeXObject text = popArg(parser, stack);

      createAnchor(parser, target, text).process(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      String target = popLabelString(parser, parser);
      TeXObject text = popArg(parser, parser);

      createAnchor(parser, target, text).process(parser);
   }

}
