/*
    Copyright (C) 2023 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.datatool;

import com.dickimawbooks.texparserlib.TeXPath;

public interface FileLoadedListener
{
   /**
    * Called when a database has been loaded from a file.
    * @param dbLabel the label of the database
    * @param fileType the file format
    * @param fileVersion the file format version (empty if unknown)
    * @param texPath the file
    */ 
   public void fileLoaded(String dbLabel, String fileType, String formatVersion,
     TeXPath texPath);
}
