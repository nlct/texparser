/*
    Copyright (C) 2013 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLmessage extends ControlSequence
{
   public DTLmessage()
   {
      this("dtl@message");
   }

   public DTLmessage(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new DTLmessage(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException 
   {
      TeXObject msg = stack.popArg(parser);

      if (msg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)msg).expandfully(parser,stack);

         if (msg != null)
         {
            msg = expanded;
         }
      }

      parser.getListener().getTeXApp().message(msg.format());
   }

   public void process(TeXParser parser)
     throws IOException 
   {
      TeXObject msg = parser.popNextArg();

      if (msg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)msg).expandfully(parser);

         if (msg != null)
         {
            msg = expanded;
         }
      }

      parser.getListener().getTeXApp().message(msg.format());
   }
}
