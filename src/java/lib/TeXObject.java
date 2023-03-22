/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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

public interface TeXObject extends Cloneable
{
   // Process this. Arguments can be fetched with
   // parser.popStack()
   public void process(TeXParser parser)
      throws IOException;

   // Process with local stack.
   // If stack is null or empty, same as process(parser)
   // otherwise arguments can be fetched with stack.popArg()
   // If you need more arguments once stack is empty, use
   // parser.popNextArg()
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException;

   public Object clone();

   public String toString(TeXParser parser);

   public TeXObjectList string(TeXParser parser)
    throws IOException;

   public String format();

   public String purified();

   public boolean isPar();

   public boolean isEmpty();

   public boolean canExpand();

   public boolean isExpansionBlocker();

   public boolean isDataObject();

   public boolean isSingleToken();
}
