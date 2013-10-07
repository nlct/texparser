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

import com.dickimawbooks.texparserlib.*;

public class Verbatim extends Environment
{
   public Verbatim()
   {
      this("verbatim");
   }

   public Verbatim(String name)
   {
      super(name);
   }

   public Object clone()
   {
      Verbatim env = new Verbatim(getName());

      env.addAll(this);

      return env;
   }

   public String toString(TeXParser parser)
   {
      StringBuilder builder = new StringBuilder();

      char esc = parser.getEscChar();
      char bg = parser.getBgChar();
      char eg = parser.getEgChar();

      builder.append(esc);
      builder.append("begin");
      builder.append(bg);
      builder.append(getName());
      builder.append(eg);

      boolean isStar = (getName().endsWith("*"));

      for (TeXObject object : this)
      {
         if (isStar && (object instanceof Space))
         {
            builder.appendCodePoint(0x2423);
         }
         else
         {
            builder.append(object.toString(parser));
         }
      }

      builder.append(esc);
      builder.append("end");
      builder.append(bg);
      builder.append(getName());
      builder.append(eg);

      return builder.toString();
   }

   public void popGroup(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      popGroup(parser);
   }

   public void popGroup(TeXParser parser)
     throws IOException
   {
// This doesn't allow for spaces after \end
      parser.readTo(""+parser.getEscChar()+"end"
        +parser.getBgChar()+getName()+parser.getEgChar(), this);
   }

   public void process(TeXParser parser)
    throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      boolean isStar = (getName().endsWith("*"));

      for (TeXObject object : this)
      {
         if (isStar && (object instanceof Space))
         {
            writeable.writeCodePoint(0x2423);
         }
         else
         {
            writeable.write(object.toString(parser));
         }
      }
   }

}
