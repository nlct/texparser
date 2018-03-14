/*
    Copyright (C) 2015 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class NewIf extends Primitive
{
   public NewIf()
   {
      this("newif");
   }

   public NewIf(String name)
   {
      super(name);
      setAllowsPrefix(true);
   }

   public Object clone()
   {
      return new NewIf(getName());
   }

   public static void createConditional(boolean isLocal,
      TeXParser parser, TeXObject obj)
      throws IOException
   {
       if (!(obj instanceof ControlSequence))
       {
          throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_CS_EXPECTED, obj.toString(parser));
       }

       createConditional(isLocal, parser, ((ControlSequence)obj).getName());
   }

   public static void createConditional(boolean isLocal,
      TeXParser parser, String name)
   {
      createConditional(isLocal, parser, name, false);
   }

   public static void createConditional(boolean isLocal,
      TeXParser parser, String name, boolean initialValue)
   {
       parser.putControlSequence(isLocal,
         initialValue ? new IfTrue(name) : new IfFalse(name));

       TeXObjectList list = new TeXObjectList();

       String conditional = name.substring(2);

       list.add(new TeXCsRef("let"));
       list.add(new TeXCsRef("if"+conditional));
       list.add(new TeXCsRef("iffalse"));

       parser.putControlSequence(new GenericCommand(true, conditional+"false",
         null, list));

       list = new TeXObjectList();

       list.add(new TeXCsRef("let"));
       list.add(new TeXCsRef("if"+conditional));
       list.add(new TeXCsRef("iftrue"));

       parser.putControlSequence(new GenericCommand(true, conditional+"true",
         null, list));
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      createConditional(getPrefix() != PREFIX_GLOBAL, parser, stack.popToken());
      clearPrefix();
   }

   public void process(TeXParser parser)
      throws IOException
   {
      createConditional(getPrefix() != PREFIX_GLOBAL, parser, 
        parser.popToken());
      clearPrefix();
   }
}
