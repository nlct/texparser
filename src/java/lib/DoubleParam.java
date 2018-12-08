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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class DoubleParam implements ParameterToken,Expandable
{
   public DoubleParam(ParameterToken param)
   {
      setNext(param);
   }

   public Object clone()
   {
      return new DoubleParam((ParameterToken)param.clone());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObjectList expanded = new TeXObjectList();

      expanded.add(next());

      return expanded;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }

   public ParameterToken next()
   {
      return param;
   }

   public Param tail()
   {
      if (param instanceof Param)
      {
         return (Param)param;
      }
      else
      {
         return param.tail();
      }
   }

   public void setNext(ParameterToken param)
   {
      this.param = param;
   }

   public String toString(TeXParser parser)
   {
      return String.format("%s%s", 
         new String(Character.toChars(parser.getParamChar())),
         next().toString(parser));
   }

   public String format()
   {
      return "#"+next().format();
   }

   public String toString()
   {
      return String.format("%s[param=%s]", getClass().getName(),
        next().toString());
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(parser.getListener().getOther(parser.getParamChar()));
      list.add(next());

      return list;
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      list.push(param);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      parser.push(param);
   }

   public boolean isPar()
   {
      return false;
   }

   private ParameterToken param;
}

