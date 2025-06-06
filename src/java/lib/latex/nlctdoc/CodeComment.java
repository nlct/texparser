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
import com.dickimawbooks.texparserlib.latex.*;

public class CodeComment extends ControlSequence
{
   public CodeComment()
   {
      this("comment", null);
   }

   public CodeComment(String name, TeXObject postEol)
   {
      super(name);
      this.postEol = postEol;
   }

   @Override
   public Object clone()
   {
      return new CodeComment(getName(), postEol);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject arg = popArg(parser, stack);

      TeXObject nextObj = TeXParserUtils.peek(parser,
        stack, TeXObjectList.POP_RETAIN_IGNOREABLES);

      TeXObjectList content = listener.createStack();

      content.add(listener.getControlSequence("code@comment"));
      content.add(TeXParserUtils.createGroup(listener, arg));

      if (nextObj instanceof Eol || nextObj instanceof Par)
      {
         content.add(TeXParserUtils.pop(parser, stack,
           TeXObjectList.POP_RETAIN_IGNOREABLES));
      }
      else
      {
         content.add(listener.getEol());
      }

      if (postEol != null)
      {
         TeXObject following;

         if (postEol instanceof TeXCsRef)
         {
            following = listener.getControlSequence(((TeXCsRef)postEol).getName());
         }
         else
         {
            following = (TeXObject)postEol.clone();
         }

         content.add(following, true);
      }

      TeXParserUtils.process(content, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   TeXObject postEol;
}
