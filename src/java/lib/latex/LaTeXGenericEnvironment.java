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

import java.io.IOException;
import java.util.Vector;
import java.util.Arrays;

import com.dickimawbooks.texparserlib.*;

public class LaTeXGenericEnvironment extends Declaration
{
   public LaTeXGenericEnvironment(String name,
     TeXObject beginCode, TeXObject endCode)
   {
      this(name, beginCode, endCode, false);
   }

   public LaTeXGenericEnvironment(String name,
     TeXObject beginCode, TeXObject endCode, 
     boolean isModeSwitcher)
   {
      super(name);
      this.beginCode = beginCode;
      this.endCode = endCode;
      this.isModeSwitcher = isModeSwitcher;
   }

   public Object clone()
   {
      return new LaTeXGenericEnvironment(getName(),  
        (TeXObject)beginCode.clone(), (TeXObject)endCode.clone());
   }

   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof LaTeXGenericEnvironment)) return false;

      if (this == obj) return true;

      LaTeXGenericEnvironment env = (LaTeXGenericEnvironment)obj;

      return beginCode.equals(env.beginCode) 
        && endCode.equals(env.endCode)
        && isModeSwitcher == env.isModeSwitcher;
   }

   public String toString()
   {
      return String.format("%s[name=%s,begin=%s,end=%s]",
       getClass().getSimpleName(), getName(),  
       beginCode, endCode);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   {
      return null;
   }

   public void process(TeXParser parser) throws IOException
   {
      parser.push((TeXObject)beginCode.clone(), true);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      stack.push((TeXObject)beginCode.clone(), true);
   }

   public void end(TeXParser parser) throws IOException
   {
      parser.push((TeXObject)endCode.clone(), true);
   }

   public boolean isModeSwitcher()
   {
      return isModeSwitcher;
   }

   protected TeXObject beginCode, endCode;

   protected boolean isModeSwitcher = false;
}
