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

   @Override
   public Object clone()
   {
      return new LaTeXGenericEnvironment(getName(),  
        (TeXObject)beginCode.clone(), (TeXObject)endCode.clone());
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof LaTeXGenericEnvironment)) return false;

      if (this == obj) return true;

      LaTeXGenericEnvironment env = (LaTeXGenericEnvironment)obj;

      return beginCode.equals(env.beginCode) 
        && endCode.equals(env.endCode)
        && isModeSwitcher == env.isModeSwitcher;
   }

   @Override
   public String toString()
   {
      return String.format("%s[name=%s,begin=%s,end=%s]",
       getClass().getSimpleName(), getName(),  
       beginCode, endCode);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject obj = (TeXObject)beginCode.clone();

      if (parser.isStack(obj))
      {
         return (TeXObjectList)obj;
      }

      TeXObjectList expanded = parser.getListener().createStack();

      expanded.add(obj);

      return expanded;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
    throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject obj = (TeXObject)beginCode.clone();

      TeXObjectList expanded ;

      if (obj instanceof Expandable)
      {
         if (parser == stack || stack == null)
         {
            expanded = ((Expandable)obj).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)obj).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            return expanded;
         }
      }

      expanded = parser.getListener().createStack();

      expanded.add(obj);

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      parser.push((TeXObject)beginCode.clone(), true);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      stack.push((TeXObject)beginCode.clone(), true);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject obj = (TeXObject)endCode.clone();

      if (stack == null || stack == parser)
      {
         obj.process(parser);
      }
      else
      {
         obj.process(parser, stack);
      }
   }

   @Override
   public boolean isModeSwitcher()
   {
      return isModeSwitcher;
   }

   protected TeXObject beginCode, endCode;

   protected boolean isModeSwitcher = false;
}
