/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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

public abstract class Declaration extends Command
{
   public Declaration(String name)
   {
      super(name);
   }

   /* Convenient method to push this Declaration to the current TeXSettings.
    Actual declarations that don't have an explicit end (e.g. \em) need to
    be pushed to the current settings so that the end can be called when
    the current group closes. This allows L2HConverter to add
    closing tags. Declarations that are actually environments that
    are always used with \begin{decl name} and \end{decl name}
    shouldn't usually be pushed. However, there are some declarations
    that may either be used explicitly or called as an environment,
    in which case, the process methods need to use pushEnd and end(TeXParser) will
    need to remove this using TeXSettings.removeDeclaration(this) to prevent the
    end being done twice. (See, for example, FontShapeDeclaration.)
   */ 
   protected void pushEnd(TeXParser parser)
   {
      parser.pushDeclaration(this);
   }

   public abstract void end(TeXParser parser) throws IOException;

   public abstract boolean isModeSwitcher();

   public EndDeclaration getEndDeclaration()
   {
      return endDeclaration;
   }

   public void setEndDeclaration(EndDeclaration decl)
   {
      endDeclaration = decl;
   }

   public String getArgTypes()
   {
      return argTypes;
   }

   protected void setArgTypes(String argTypes)
   {
      this.argTypes = argTypes;
   }

   protected EndDeclaration endDeclaration;
   private String argTypes=null;
}
