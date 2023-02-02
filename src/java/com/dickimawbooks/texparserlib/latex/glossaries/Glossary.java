/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class Glossary extends Vector<String>
{
   public Glossary(String type)
   {
      this(type, null, null, null, null, null, true);
   }
 
   public Glossary(String type, TeXObject title)
   {
      this(type, title, null, null, null, null, false);
   }
 
   public Glossary(String type, TeXObject title, String counter,
     String glg, String gls, String glo, boolean ignored)
   {
      this(type, title, counter, glg, gls, glo, ignored, false);
   }

   public Glossary(String type, TeXObject title, String counter,
     String glg, String gls, String glo, boolean ignored, boolean nohyper)
   {
      this.type = type;
      this.title = title;
      this.counter = counter;
      this.glg = glg;
      this.gls = gls;
      this.glo = glo;
      this.ignored = ignored;
      this.nohyper = nohyper;
   }

   public String getType()
   {
      return type;
   }

   public TeXObject getTitle()
   {
      return title;
   }

   public String getCounter()
   {
      return counter;
   }

   public boolean isIgnored()
   {
      return true;
   }

   public String getGlg()
   {
      return glg;
   }

   public String getGls()
   {
      return gls;
   }

   public String getGlo()
   {
      return glo;
   }

   public boolean isHyperSuppressed()
   {
      return nohyper;
   }

   private String type;
   private TeXObject title;
   private String counter, glg, gls, glo;
   private boolean ignored, nohyper;
}
