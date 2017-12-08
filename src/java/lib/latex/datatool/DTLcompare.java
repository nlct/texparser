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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLcompare extends ControlSequence
{
   public DTLcompare()
   {
      this("dtlcompare", true);
   }

   public DTLcompare(String name, boolean caseSensitive)
   {
      super(name);
      this.caseSensitive = caseSensitive;
   }

   public Object clone()
   {
      return new DTLcompare(getName(), caseSensitive);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = stack.popControlSequence(parser);
      TeXObject string1 = stack.popArg(parser);
      TeXObject string2 = stack.popArg(parser);

      if (cs instanceof TeXCsRef)
      {
         cs = parser.getListener().getControlSequence(cs.getName());
      }

      if (!(cs instanceof CountRegister))
      {
         throw new TeXSyntaxException(parser,  
            TeXSyntaxException.ERROR_NUMERIC_REGISTER_EXPECTED);
      }

      CountRegister reg = (CountRegister)cs;

      String str1 = string1.toString(parser);
      String str2 = string2.toString(parser);

      if (!caseSensitive)
      {
         str1 = str1.toLowerCase();
         str2 = str2.toLowerCase();
      }

      reg.setValue(str1.compareTo(str2));
   }

   public void process(TeXParser parser)
     throws IOException
   {
      ControlSequence cs = parser.popControlSequence(parser);
      TeXObject string1 = parser.popNextArg();
      TeXObject string2 = parser.popNextArg();

      if (cs instanceof TeXCsRef)
      {
         cs = parser.getListener().getControlSequence(cs.getName());
      }

      if (!(cs instanceof CountRegister))
      {
         throw new TeXSyntaxException(parser,  
            TeXSyntaxException.ERROR_NUMERIC_REGISTER_EXPECTED);
      }

      CountRegister reg = (CountRegister)cs;

      String str1 = string1.toString(parser);
      String str2 = string2.toString(parser);

      if (!caseSensitive)
      {
         str1 = str1.toLowerCase();
         str2 = str2.toLowerCase();
      }

      reg.setValue(str1.compareTo(str2));
   }

   private boolean caseSensitive;
}
