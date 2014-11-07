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

public class L2HCr extends ControlSequence
{
   public L2HCr()
   {
      this("cr");
   }

   public L2HCr(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HCr(getName());
   }

   // TODO check for enclosing tabular
   public void process(TeXParser parser, boolean isStar,
     TeXObject optArg)
      throws IOException
   {
      parser.getListener().getWriteable().writeln("<br>");
   }

   public void process(TeXParser parser) throws IOException
   {
      if (name.equals("\\"))
      {
         TeXObject obj = parser.popStack();

         boolean isStar;

         if (obj instanceof CharObject && obj.toString().equals("*"))
         {
            isStar = true;
         }
         else
         {
            isStar = false;
            parser.push(obj);
         }

         process(parser, isStar, parser.popNextArg('[', ']'));
      }
      else
      {
         parser.getListener().getWriteable().writeln("<br>");
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

         process(parser, isStar, list.popArg(parser, '[', ']'));
      }
      else
      {
         parser.getListener().getWriteable().writeln("<br>");
      }
   }

}
