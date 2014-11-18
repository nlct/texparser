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

public class EndDeclaration extends ControlSequence
{
   public EndDeclaration(String name, Declaration decl)
   {
      super(name);
      this.declName = decl.getName();

      decl.setEndDeclaration(this);
   }

   public EndDeclaration(String name)
   {
      super(name);

      if (name.startsWith("end"))
      {
         this.declName = name.substring(3);
      }
      else
      {
         setName("end"+name);
         this.declName = name;
      }
   }

   public Object clone()
   {
      EndDeclaration dec = new EndDeclaration(getName());
      dec.declName = declName;

      return dec;
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      doEnd(parser);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      doEnd(parser);
   }

   protected void doEnd(TeXParser parser)
      throws IOException
   {
      Declaration decl = getDeclaration(parser);

      if (decl != null)
      {
         decl.end(parser);
      }
   }

   public Declaration getDeclaration(TeXParser parser)
   {
      ControlSequence cs = parser.getListener().getControlSequence(declName);

      if (cs instanceof Declaration)
      {
         return (Declaration)cs;
      }

      return null;
   }

   public boolean isModeSwitcher(TeXParser parser)
   {
      Declaration decl = getDeclaration(parser);

      return decl == null ? false : decl.isModeSwitcher();
   }

   public EndDeclaration getEndDeclaration(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public void setEndDeclaration(EndDeclaration decl)
   {
   }

   public String getDeclarationName()
   {
      return declName;
   }

   private String declName;

}
