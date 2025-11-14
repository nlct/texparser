/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.flowfram;

public class FrameHtmlOptions
{
   public FrameHtmlOptions(final int idx)
   {
      this.index = idx;
   }

   public int getIndex()
   {
      return index;
   }

   public String toString()
   {
      return String.format(
       "%s[index=%d,type=%s,frameId=%d,width=%s,height=%s,thepage=%s,theabsolutepage=%s]",
        getClass().getSimpleName(), index, type, frameId,
         width, height, thepage, theabsolutepage);
   }

   final int index;
   public String type;
   public int frameId;
   public String thepage, theabsolutepage;
   public String width, height;
}
