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
package com.dickimawbooks.texparserlib.latex.lipsum;

import java.io.IOException;

import com.dickimawbooks.texparserlib.latex.*;

public class LipsumSty extends LaTeXSty
{
   public LipsumSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
    throws IOException
   {
      super(options, "lipsum", listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      registerControlSequence(new Lipsum(this));
      registerControlSequence(new SetLipsumDefault(this));
   }

   public String getDefaultRange()
   {
      return defaultRange;
   }

   public void setDefaultRange(String range)
   {
      defaultRange = range;
   }

   private String defaultRange = "1-7";
}
