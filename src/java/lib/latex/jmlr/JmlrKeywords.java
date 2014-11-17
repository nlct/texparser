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
package com.dickimawbooks.texparserlib.latex.jmlr;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class JmlrKeywords extends Declaration
{
   public JmlrKeywords()
   {
      this("keywords");
   }

   public JmlrKeywords(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new JmlrKeywords(getName());
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
      Group grp = parser.getListener().createGroup("Keywords:");

      parser.push(new TeXCsRef("ignorespaces"));
      parser.push(parser.getListener().getSpace());
      parser.push(grp);
      parser.push(new TeXCsRef("textbf"));
      parser.push(new TeXCsRef("small"));
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      Group grp = parser.getListener().createGroup("Keywords:");

      stack.push(new TeXCsRef("ignorespaces"));
      stack.push(parser.getListener().getSpace());
      stack.push(grp);
      stack.push(new TeXCsRef("textbf"));

      (new TeXCsRef("small")).process(parser, stack);
   }

   public void end(TeXParser parser)
    throws IOException
   {
      ControlSequence cs = parser.getControlSequence("endsmall");

      if (cs == null)
      {
         cs = parser.getListener().getControlSequence("small");

         if (cs instanceof Declaration)
         {
            ((Declaration)cs).end(parser);
         }

         return;
      }

      cs.process(parser);
   }

   public boolean isModeSwitcher()
   {
      return false;
   }
}
