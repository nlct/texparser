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
package com.dickimawbooks.texparserlib.latex;

import java.util.Hashtable;
import java.io.IOException;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;

public abstract class LaTeXSty
{
   public LaTeXSty(String name, LaTeXParserListener listener)
   {
      this.name = name;
      this.listener = listener;
   }

   public abstract void addDefinitions();

   public void registerControlSequence(ControlSequence cs)
   {
      listener.registerControlSequence(this, cs);
   }

   public DimenRegister registerNewLength(String name)
   {
      return listener.getParser().getSettings().newdimen(name);
   }

   public abstract void processOption(String option)
    throws IOException;

   public void processOptions(KeyValList options)
   throws IOException
   {
      if (options == null) return;

      for (Iterator<String> it = options.keySet().iterator(); it.hasNext();)
      {
         String option = it.next();

         if (option != null && !option.isEmpty())
         {
            processOption(option);
         }
      }
   }

   protected abstract void preOptions()
     throws IOException;

   protected void postOptions()
     throws IOException
   {
      addDefinitions();
   }

   public void load(KeyValList options)
   throws IOException
   {
      preOptions();

      KeyValList clsOptions = listener.getDocumentClassOptions();

      if (clsOptions != options)
      {
         processOptions(clsOptions);
      }

      processOptions(options);
      postOptions();
   }

   public String getName()
   {
      return name;
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof LaTeXSty)) return false;

      return name.equals(((LaTeXSty)obj).getName());
   }

   public LaTeXParserListener getListener()
   {
      return listener;
   }

   private String name;

   private LaTeXParserListener listener;
}
