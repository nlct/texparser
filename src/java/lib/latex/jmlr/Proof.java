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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Proof extends Declaration
{
   public Proof()
   {
      this("proof");
   }

   public Proof(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Proof(getName());
   }

   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = new TeXObjectList();

      list.add(listener.getControlSequence("par"));
      list.add(listener.getControlSequence("noindent"));

      Group grp = listener.createGroup();
      list.add(grp);
      grp.add(listener.getControlSequence("bfseries"));
      grp.add(listener.getControlSequence("upshape"));
      grp.add(listener.getControlSequence("proofname"));
      grp.add(listener.getSpace());

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObjectList list = expandonce(parser, stack);

      if (parser == stack)
      {
         list.process(parser);
      }
      else
      {
         list.process(parser, stack);
      }
   }

   public void end(TeXParser parser)
    throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence("jmlrQED");

      cs.process(parser);
   }

   public boolean isModeSwitcher()
   {
      return false;
   }

}
