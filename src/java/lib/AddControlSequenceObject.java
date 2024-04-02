/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class AddControlSequenceObject extends AbstractTeXObject
{
   public AddControlSequenceObject(ControlSequence controlSeq)
   {
      if (controlSeq == null)
      {
         throw new NullPointerException();
      }

      this.controlSeq = controlSeq;
   }

   @Override
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      parser.putControlSequence(true, controlSeq);
   }

   @Override
   public Object clone()
   {
      return new AddControlSequenceObject(controlSeq);
   }

   @Override
   public String toString(TeXParser parser)
   {
      return "";
   }

   @Override
   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return new TeXObjectList();
   }

   @Override
   public String format()
   {
      return "";
   }

   @Override
   public String toString()
   {
      return String.format("%s[cs=%s]", getClass().getSimpleName(), controlSeq);
   }

   public ControlSequence getControlSequence()
   {
      return controlSeq;
   }

   protected ControlSequence controlSeq;
}

