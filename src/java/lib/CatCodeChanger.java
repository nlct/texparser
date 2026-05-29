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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

/**
 * An interface to implement if a macro changes the category code
 * of one or more characters.
 */
public interface CatCodeChanger
{
   /**
    * Applies the necessary category code changes.
    * Note that if this method pops anything off the parser in order
    * to determine what changes need to be made, the popped objects must
    * be pushed back on again.
    * @param parser the parser that needs to be notified of the
    * category code change(s)
    * @throws IOException if an I/O or syntax error occurs
   */
   public void applyCatCodeChange(TeXParser parser) throws IOException;

   /** 
    * Returns a command with the same syntax as this that does nothing.
    * @return a control sequence with the same syntax as this that
    * does nothing (except pop the arguments as per the syntax).
    */
   public ControlSequence getNoOpCommand();
}

