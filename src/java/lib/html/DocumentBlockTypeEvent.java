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
package com.dickimawbooks.texparserlib.html;

import java.io.Writer;

public class DocumentBlockTypeEvent
{
   public DocumentBlockTypeEvent(L2HConverter source,
    Writer writer, DocumentBlockType oldType, DocumentBlockType newType)
   {
      this.source = source;
      this.writer = writer;
      this.oldType = oldType;
      this.newType = newType;
   }

   public boolean isConsumed()
   {
      return consumed;
   }

   public void consume()
   {
      consumed = true;
   }

   public L2HConverter getSource()
   {
      return source;
   }

   public Writer getWriter()
   {
      return writer;
   }

   public DocumentBlockType getOldType()
   {
      return oldType;
   }

   public DocumentBlockType getNewType()
   {
      return newType;
   }

   private L2HConverter source;
   private Writer writer;
   private DocumentBlockType oldType, newType;
   private boolean consumed = false;
}
