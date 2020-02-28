/*
    Copyright (C) 2020 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.auxfile;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

/*
 * Most instances of \providecommand within aux files simply provide
 * commands that ignore their argument. This is usually done to
 * allow the document to compile without error after a package has
 * been removed from the document preamble. It's not essential for
 * the aux parser to know the definitions of these commands, but
 * it's useful to know the syntax to make it easier to ignore them.
 *
 * Obviously, the commands that the aux parser is supposed to be
 * searching for shouldn't be overwritten by \providecommand, but
 * since these should all be set up in AuxParser.addPredefined()
 * they should already be defined by the time \providecommand is
 * processed.
 */

public class AuxProvideCommand extends ControlSequence
{
   public AuxProvideCommand()
   {
      this("providecommand");
   }

   public AuxProvideCommand(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new AuxProvideCommand(getName());
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
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_CS_EXPECTED,
            object.format(), object.getClass().getSimpleName());
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
               throw new TeXSyntaxException(parser,
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

      // Only define command if it isn't already defined.

      ControlSequence cs = parser.getControlSequence(csName);

      if (cs == null)
      {
         if (numParams > 0)
         {
            boolean[] argflags = new boolean[numParams];

            argflags[0] = (defValue == null);

            for (int i = 1; i < numParams; i++)
            {
               argflags[i] = true;
            }

            parser.putControlSequence(new AuxIgnoreable(csName,
             false, argflags));
         }
         else
         {
            parser.putControlSequence(new AuxIgnoreable(csName));
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
