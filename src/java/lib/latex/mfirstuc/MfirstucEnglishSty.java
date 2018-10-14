/*
    Copyright (C) 2018 Nicola L.C. Talbot
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
import com.dickimawbooks.texparserlib.latex.*;

public class MfirstucEnglishSty extends LaTeXSty
{
   public MfirstucEnglishSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "mfirstuc-english", listener, loadParentOptions);
   }

   protected void preOptions() throws IOException
   {
      mfirstucSty = (MfirstucSty)getListener().requirepackage(
         null, "mfirstuc", true);
   }

   public void addDefinitions()
   {
      mfirstucSty.addException("a");
      mfirstucSty.addException("an");
      mfirstucSty.addException("and");
      mfirstucSty.addException("but");
      mfirstucSty.addException("for");
      mfirstucSty.addException("in");
      mfirstucSty.addException("of");
      mfirstucSty.addException("or");
      mfirstucSty.addException("no");
      mfirstucSty.addException("nor");
      mfirstucSty.addException("so");
      mfirstucSty.addException("some");
      mfirstucSty.addException("the");
      mfirstucSty.addException("with");
      mfirstucSty.addException("yet");
   }

   private MfirstucSty mfirstucSty;
}
