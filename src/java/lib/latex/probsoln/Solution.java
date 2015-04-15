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
package com.dickimawbooks.texparserlib.latex.probsoln;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Solution extends Declaration
{
   public Solution()
   {
      this("solution");
   }

   public Solution(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Solution(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
      throws IOException
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
      throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return null;
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      stack.push(new TeXCsRef("ignorespaces"));
      stack.push(new TeXCsRef("space"));

      Group grp = parser.getListener().createGroup();

      grp.add(new TeXCsRef("solutionname"));
      grp.add(parser.getListener().getOther(':'));

      stack.push(grp);
      stack.push(new TeXCsRef("textbf"));
      stack.push(new TeXCsRef("noindent"));

      stack.push(new TeXCsRef("par"));
   }

   public void end(TeXParser parser)
    throws IOException
   {
   }

   public boolean isModeSwitcher()
   {
      return false;
   }
}
