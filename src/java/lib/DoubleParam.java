/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

   @Override
   public Object clone()
   {
      return new DoubleParam((ParameterToken)param.clone());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObjectList expanded = new TeXObjectList();

      expanded.add(next());

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public ParameterToken next()
   {
      return param;
   }

   @Override
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

   @Override
   public String toString(TeXParser parser)
   {
      return String.format("%s%s", 
         new String(Character.toChars(parser.getParamChar())),
         next().toString(parser));
   }

   @Override
   public String format()
   {
      return "#"+next().format();
   }

   @Override
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

   @Override
   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      list.push(param);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      parser.push(param);
   }

   @Override
   public boolean isPar()
   {
      return false;
   }

   @Override
   public boolean isEmpty()
   {
      return false;
   }

   private ParameterToken param;
}

