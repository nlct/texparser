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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

/**
 * Objects that implement Expandable may be able to expand. If they
 * can't these methods should return null, otherwise they should
 * return the expansion as a stack.
 */
public interface Expandable
{
   // Use parser.popNextArg() if arguments required
   public TeXObjectList expandonce(TeXParser parser) throws IOException;

   public TeXObjectList expandfully(TeXParser parser) throws IOException;

   // Local stack:

   // Use stack.popArg(parser) if arguments required
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException;

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException;
}

