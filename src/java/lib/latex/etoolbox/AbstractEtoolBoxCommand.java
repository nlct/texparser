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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public abstract class AbstractEtoolBoxCommand extends Command
{
   public AbstractEtoolBoxCommand(String name, boolean isCsname)
   {
      super(name);
      this.isCsname = isCsname;
   }

   /**
    * Pops either a control sequence or a control sequence name and
    * returns the control sequence. The class variable isCsname
    * indicates whether or not the argument is a control sequence
    * name.
    * @param parser the parser
    * @param stack the current stack or the parser if no stack
    * @return the control sequence
    */ 
   protected ControlSequence popCsArg(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return popCsArg(parser, stack, isCsname);
   }

   /**
    * Pops either a control sequence or a control sequence name and
    * returns the control sequence.
    * @param parser the parser
    * @param stack the current stack or the parser if no stack
    * @param isCsname true if control sequence name should be popped
    * @return the control sequence
    */ 
   public static ControlSequence popCsArg(TeXParser parser, TeXObjectList stack,
      boolean isCsname)
   throws IOException
   {
      ControlSequence cs = null;

      if (isCsname)
      {
         String csname = TeXParserUtils.popLabelString(parser, stack);

         cs = parser.getListener().getControlSequence(csname);
      }
      else
      {
         cs = TeXParserUtils.popControlSequence(parser, stack);

         if (cs instanceof TeXCsRef)
         {
            cs = parser.getListener().getControlSequence(cs.getName());
         }
      }

      return cs;
   }

   protected boolean isCsname;
}
