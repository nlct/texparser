/*
    Copyright (C) 2021 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.mfirstuc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

/*
 * Used for marking punctuation that should be considered a word
 * break.
 * Acts like \@firstofone but not expandable.
 */
public class MFUwordbreak extends ControlSequence
{
   public MFUwordbreak()
   {
      this("MFUwordbreak");
   }

   public MFUwordbreak(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new MFUwordbreak(getName());
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject arg1 = parser.popNextArg();

      arg1.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      TeXObject arg1 = list.popArg(parser);

      arg1.process(parser, list);
   }

}
