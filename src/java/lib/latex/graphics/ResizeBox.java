/*
    Copyright (C) 2017 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.graphics;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class ResizeBox extends ControlSequence
{
   public ResizeBox()
   {
      this("resizebox");
   }

   public ResizeBox(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new ResizeBox(getName());
   }

   protected TeXDimension getLength(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject length = (stack == parser ? parser.popNextArg()
        : stack.popArg(parser));

      if (length instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (stack == parser)
         {
            expanded = ((Expandable)length).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)length).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            length = expanded;
         }

         if (length instanceof TeXObjectList 
             && ((TeXObjectList)length).size()==0)
         {
            length = ((TeXObjectList)length).popStack(parser);
         }
      }

      if (length instanceof CharObject 
           && ((CharObject)length).getCharCode() == '!')
      {
         return null;
      }

      return GraphicsSty.getDimension(length, parser);
   }

   // This is difficult to implement as the font information and
   // page dimensions are unknown.
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject obj = stack.peekStack();

      if (obj instanceof CharObject && ((CharObject)obj).getCharCode()=='*')
      {
         stack.popToken();
      }

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXDimension width = getLength(parser, stack);
      TeXDimension height = getLength(parser, stack);

      TeXObject object = (stack == parser ? parser.popNextArg()
        : stack.popArg(parser));

      if (object instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (stack == parser)
         {
            expanded = ((Expandable)object).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)object).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            object = expanded;
         }
      }

      listener.resize(width, height, parser, stack, object);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
