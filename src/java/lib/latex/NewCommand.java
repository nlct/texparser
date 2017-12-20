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

public class NewCommand extends Command
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

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      byte popStyle = TeXObjectList.POP_SHORT;

      TeXObject object = (stack == parser ? 
        parser.popNextArg(popStyle) : stack.popArg(parser, popStyle));

      boolean isStar = false;

      if (object instanceof CharObject
       && ((CharObject)object).getCharCode() == (int)'*')
      {
         isStar = true;
         object = (stack == parser ?
            parser.popNextArg(popStyle) : stack.popArg(parser, popStyle));
      }

      if (object instanceof TeXObjectList)
      {
         // Use popArg in case there are spaces before or after the
         // control sequence.

         object = ((TeXObjectList)object).popArg(parser, popStyle);
      }

      if (!isStar)
      {
         popStyle = 0;
      }

      String csName;

      if (object instanceof ControlSequence)
      {
         csName = ((ControlSequence)object).getName();
      }
      else
      {
         throw new TeXSyntaxException(
            parser,
            TeXSyntaxException.ERROR_CS_EXPECTED,
            object.format());
      }

      object = (stack == parser ?
           parser.popNextArg(popStyle, '[', ']')
         : stack.popArg(parser, popStyle, '[', ']'));

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
            TeXObjectList expanded = null;

            if (object instanceof Expandable)
            {
               expanded = ((Expandable)object).expandfully(parser, stack);
            }

            try
            {
               if (expanded == null)
               {
                  numParams = Integer.parseInt(object.toString(parser));
               }
               else
               {
                  numParams = Integer.parseInt(expanded.toString(parser));
               }
            }
            catch (NumberFormatException e)
            {
               throw new TeXSyntaxException(
                 parser,
                 TeXSyntaxException.ERROR_NUMBER_EXPECTED, 
                  object.toString(parser));
            }
         }

         defValue = (stack == parser ?
              parser.popNextArg(popStyle, '[', ']')
            : stack.popArg(parser, popStyle, '[', ']'));
      }

      TeXObject definition = (stack == parser ?
             parser.popNextArg(popStyle) : stack.popArg(parser, popStyle));

      ((LaTeXParserListener)parser.getListener()).newcommand(
         overwrite, name, csName, isStar, numParams, defValue, definition);
   }

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
