/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
import com.dickimawbooks.texparserlib.latex.*;

public class L2HAbstract extends AbstractDec
{
   public L2HAbstract()
   {
      this("abstract");
   }

   public L2HAbstract(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HAbstract(getName());
   }

   @Override
   protected TeXObjectList getHeader(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      ControlSequence cs = parser.getControlSequence("chapter");

      TeXObjectList substack;

      if (cs == null)
      {
         substack = listener.createStack();

         substack.add(new StartElement("h2"));
         substack.add(new TeXCsRef("abstractname"));
         substack.add(new EndElement("h2"));
      }
      else
      {
         substack = super.getHeader(parser, stack);
      }

      return substack;
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      ControlSequence cs = parser.getControlSequence("chapter");

      if (cs == null)
      {
         listener.startPhantomSection("abstract", null, parser);

         parser.getListener().getWriteable().writeliteral(
           String.format("%n<div class=\"%s\">", getName()));

         listener.setCurrentBlockType(DocumentBlockType.BLOCK);
      }

      super.process(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      ControlSequence cs = parser.getControlSequence("chapter");

      if (cs == null)
      {
         listener.startPhantomSection("abstract", null, stack);

         listener.writeliteral(
          String.format("%n<div class=\"%s\">", getName()));

         listener.setCurrentBlockType(DocumentBlockType.BLOCK);
      }

      super.process(parser, stack);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      ControlSequence cs = parser.getControlSequence("chapter");

      if (cs == null)
      {
         listener.writeliteral(
          String.format("</div><!-- end of %s -->%n", getName()));

         listener.setCurrentBlockType(DocumentBlockType.BODY);
      }

   }
}
