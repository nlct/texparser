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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

/**
 * The command used to mark the end of an environment. See the
 * comments in the Begin class.
 */
public class End extends ControlSequence
{
   public End()
   {
      this("end");
   }

   public End(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new End(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String name = popLabelString(parser, stack);

      if (parser.isDebugMode(TeXParser.DEBUG_DECL))
      {
         parser.logMessage("END: "+name);
      }

      if (name.equals("document"))
      {
         listener.endDocument(stack);
         return;
      }

      doEnd(parser, stack, name);

      TeXObject currenvCs = parser.getControlSequence("@currenvir");

      if (currenvCs == null)
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_EXTRA_END, name);
      }

      String currenv = parser.expandToString(currenvCs, stack);

      parser.endGroup();

      if (!name.equals(currenv))
      {
         throw new LaTeXSyntaxException(parser, 
             LaTeXSyntaxException.ERROR_EXTRA_END, name);
      }

      endHook(name, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected void doEnd(TeXParser parser, TeXObjectList stack, String name)
      throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence("end"+name);

      if (cs instanceof EndDeclaration)
      {
         if (stack == parser || stack == null)
         {
            cs.process(parser);
         }
         else
         {
            cs.process(parser, stack);
         }
      }
      else
      {
         cs = parser.getListener().getControlSequence(name);

         if (cs instanceof Declaration)
         {
            ((Declaration)cs).end(parser, stack);
         }
      }
   }

   /**
    * Hook performed after scope ends.
    */ 
   protected void endHook(String name, TeXParser parser, TeXObjectList stack)
     throws IOException
   {
   }

}
