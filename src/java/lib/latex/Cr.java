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

public class Cr extends ControlSequence
{
   public Cr()
   {
      this("cr");
   }

   public Cr(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Cr(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack, boolean isStar,
     TeXObject optArg)
      throws IOException
   {
      ((LaTeXParserListener)parser.getListener()).cr(isStar, optArg);
   }

   public void process(TeXParser parser) throws IOException
   {
      if (name.equals("\\"))
      {
         TeXObject obj = parser.popStack();

         boolean isStar;

         if (obj instanceof CharObject &&
            ((CharObject)obj).getCharCode() == (int)'*')
         {
            isStar = true;
         }
         else
         {
            isStar = false;
            parser.push(obj);
         }

         process(parser, parser, isStar, parser.popNextArg('[', ']'));
      }
      else
      {
         process(parser, parser, false, null);
      }
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      if (name.equals("\\"))
      {
         TeXObject obj = list.pop();

         boolean isStar;

         if (obj instanceof CharObject && obj.toString().equals("*"))
         {
            isStar = true;
         }
         else
         {
            isStar = false;
            list.push(obj);
         }

         process(parser, list, isStar, list.popArg(parser, '[', ']'));
      }
      else
      {
         process(parser, list, false, null);
      }
   }

}
