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
   public LaTeXGenericEnvironment(String name)
   {
      this(name, null, null, false);
   }

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
       beginCode == null ? null : (TeXObject)beginCode.clone(), 
       endCode == null ? null : (TeXObject)endCode.clone());
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof LaTeXGenericEnvironment)) return false;

      if (this == obj) return true;

      LaTeXGenericEnvironment env = (LaTeXGenericEnvironment)obj;

      if (isModeSwitcher != env.isModeSwitcher) return false;

      if (beginCode == null && env.beginCode != null) return false;
      if (endCode == null && env.endCode != null) return false;

      return (beginCode == env.beginCode || beginCode.equals(env.beginCode)) 
        && (endCode == env.endCode || endCode.equals(env.endCode));
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
      if (beginCode == null)
      {
         return parser.getListener().createStack();
      }

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
      if (beginCode == null)
      {
         return parser.getListener().createStack();
      }

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
      if (beginCode != null)
      {
         TeXParserUtils.process((TeXObject)beginCode.clone(), parser, parser);
      }
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      if (beginCode != null)
      {
         TeXParserUtils.process((TeXObject)beginCode.clone(), parser, stack);
      }
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      if (endCode != null)
      {
         TeXParserUtils.process((TeXObject)endCode.clone(), parser, stack);
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
