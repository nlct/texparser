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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class GenericError extends ControlSequence
{
   public GenericError()
   {
      this("GenericError");
   }

   public GenericError(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GenericError(getName());
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject continuation = parser.popNextArg();
      TeXObject msg = parser.popNextArg();
      TeXObject furtherInfo = parser.popNextArg();
      TeXObject help = parser.popNextArg();

      if (msg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)msg).expandfully(parser);

         if (expanded != null)
         {
            msg = expanded;
         }
      }

      throw new TeXSyntaxException(parser, 
        TeXSyntaxException.ERROR_GENERIC, msg.toString(parser));
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      TeXObject continuation = list.popArg(parser);
      TeXObject msg = list.popArg(parser);
      TeXObject furtherInfo = list.popArg(parser);
      TeXObject help = list.popArg(parser);

      if (msg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)msg).expandfully(parser, list);

         if (expanded != null)
         {
            msg = expanded;
         }
      }

      throw new TeXSyntaxException(parser, 
        TeXSyntaxException.ERROR_GENERIC, msg.toString(parser));
   }

}
