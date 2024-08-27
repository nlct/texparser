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
package com.dickimawbooks.texparserlib.latex.datatool;

public interface NewRowReadListener extends java.util.EventListener
{
   /**
    * Called when a new row has been read from an external file.
    * This method is called by DataToolSty.acceptNewRowRead(IOSettings,DataBase,DataObjectList)
    * for each registered listener. Note returning false won't
    * automatically prevent other registered listeners from 
    * applying their own method. The event must be consumed to prevent
    * that.
    *
    * @param settings the current I/O settings
    * @param database the database under construction
    * @param row a list of all the elements in the row
    * @return true if the new row should be accepted or
    * false if the new row should be skipped.

    */ 
   public boolean acceptNewRowRead(NewRowReadEvent evt);
}
