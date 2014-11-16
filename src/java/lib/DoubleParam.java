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

public class DoubleParam implements TeXObject
{
   public DoubleParam(Param param)
   {
      setParam(param);
   }

   public Object clone()
   {
      return new DoubleParam((Param)param.clone());
   }

   public Param getParam()
   {
      return param;
   }

   public void setParam(Param param)
   {
      this.param = param;
   }

   public String toString(TeXParser parser)
   {
      return ""+parser.getParamChar()+param.toString(parser);
   }

   public String toString()
   {
      return "#"+getParam().toString();
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(new Other((int)parser.getParamChar()));
      list.add(getParam());

      return list;
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
   }

   public void process(TeXParser parser)
     throws IOException
   {
   }

   public boolean isPar()
   {
      return false;
   }

   private Param param;
}

