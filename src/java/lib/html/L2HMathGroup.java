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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class L2HMathGroup extends MathGroup
{
   public L2HMathGroup()
   {
      super();
   }

   public Object clone()
   {
      MathGroup math = new L2HMathGroup();
      math.setInLine(isInLine());

      for (TeXObject object : this)
      {
         math.add((TeXObject)object.clone());
      }

      return math;
   }

   public void process(TeXParser parser) throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (!isInLine())
      {
         listener.write("<div class=\"displaymath\">");
      }

      if (listener.useMathJax())
      {
         listener.write("$");
         super.process(parser);
         listener.write("$");
      }
      else
      {
         super.process(parser);
      }

      if (!isInLine())
      {
         listener.write("</div>");
      }
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (!isInLine())
      {
         listener.write("<div class=\"displaymath\">");
      }

      if (listener.useMathJax())
      {
         listener.write("$");
         super.process(parser, list);
         listener.write("$");
      }
      else
      {
         super.process(parser, list);
      }

      if (!isInLine())
      {
         listener.write("</div>");
      }
   }

}