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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class NewCommand extends ControlSequence
{
   public NewCommand()
   {
      this("newcommand", OVERWRITE_FORBID);
   }

   public NewCommand(String name, byte overwrite)
   {
      super(name);
      this.overwrite = overwrite;
   }

   public Object clone()
   {
      return new NewCommand(getName(), getOverwrite());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      PopStyle popStyle = PopStyle.SHORT;

      boolean isStar = parser.isNextChar('*', stack, popStyle);

      ControlSequence cs = parser.popRequiredControlSequence(stack);

      if (!isStar)
      {
         popStyle = PopStyle.DEFAULT;
      }

      String csName = cs.getName();

      TeXObject object = parser.popOptionalExpandFully(stack);

      int numParams = 0;
      TeXObject defValue = null;

      if (object != null)
      {
         if (object instanceof TeXNumber)
         {
            numParams = ((TeXNumber)object).getValue();
         }
         else
         {
            try
            {
               numParams = Integer.parseInt(object.toString(parser));
            }
            catch (NumberFormatException e)
            {
               throw new TeXSyntaxException(
                 parser,
                 TeXSyntaxException.ERROR_NUMBER_EXPECTED, 
                  object.toString(parser));
            }
         }

         defValue = parser.popOptional(stack);
      }

      TeXObject definition = parser.popRequired(stack, popStyle);

      ((LaTeXParserListener)parser.getListener()).newcommand(
         overwrite, name, csName, isStar, numParams, defValue, definition);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public byte getOverwrite()
   {
      return overwrite;
   }

   public static final byte OVERWRITE_FORBID=(byte)0;
   public static final byte OVERWRITE_FORCE=(byte)1;
   public static final byte OVERWRITE_SKIP=(byte)2;
   public static final byte OVERWRITE_ALLOW=(byte)3;

   private byte overwrite=OVERWRITE_FORBID;
}
