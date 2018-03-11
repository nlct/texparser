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

import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.generic.*;

/*
 * This is only included so makejmlrbookgui can extract the author
 * names from submitted articles that have used the old jmlr2e
 * package instead of the new jmlr class.
 */
public class Jmlr2eSty extends LaTeXSty
{
   public Jmlr2eSty(KeyValList options, 
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      this(options, "jmlr2e", listener, loadParentOptions);
   }

   public Jmlr2eSty(KeyValList options, String name, 
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      super(options, name, listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      registerControlSequence(new StoreDataCs("editor"));
      registerControlSequence(new Jmlr2eName(this));
   }

   public void addAuthor(Group author)
   {
      if (authors == null)
      {
         authors = new Vector<Group>();
      }

      authors.add(author);
   }

   public Vector<Group> getAuthors()
   {
      return authors;
   }

   private Vector<Group> authors = null;
}
