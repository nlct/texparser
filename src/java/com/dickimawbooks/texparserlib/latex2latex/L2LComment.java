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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class L2LComment extends Comment
{
   public L2LComment()
   {
      super();
   }

   public Object clone()
   {
      Comment obj = new L2LComment();
      obj.appendText(getText());

      return obj;
   }

   public void process(TeXParser parser)
      throws IOException
   {
      String comment = toString(parser);

      int n = comment.length();

      if (n == 0)
      {
         comment = String.format("%n");
      }
      else
      {
         char c = comment.charAt(n-1);

         if (c != '\n' && c != '\r')
         {
            comment = String.format("%s%n", comment);
         }
      }

      parser.getListener().getWriteable().write(comment);
   }

   public void process(TeXParser parser, TeXObjectList stack) 
      throws IOException
   {
      process(parser);
   }
}

