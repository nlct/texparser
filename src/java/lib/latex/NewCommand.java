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
      this("newcommand");
   }

   public NewCommand(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new NewCommand(getName());
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
      TeXObject object = stack.popArg();

      boolean isStar = false;

      if (object.toString().equals("*"))
      {
         isStar = true;
         object = stack.popArg();
      }

      if (object instanceof TeXObjectList
       &&((TeXObjectList)object).size() == 1)
      {
         object = ((TeXObjectList)object).pop();
      }

      if (!(object instanceof ControlSequence))
      {
         throw new TeXSyntaxException(
            parser,
            TeXSyntaxException.ERROR_CS_EXPECTED,
            object.toString());
      }

      String csName = ((ControlSequence)object).getName();

      object = stack.popArg(parser, '[', ']');

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

         defValue = stack.popArg(parser, '[', ']');
      }

      TeXObject definition = stack.popArg();

      ((LaTeXParserListener)parser.getListener()).newcommand(
         name, csName, isStar, numParams, defValue, definition);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject object = parser.popNextArg();

      boolean isStar = false;

      if (object.toString().equals("*"))
      {
         isStar = true;
         object = parser.popNextArg();
      }

      if (object instanceof TeXObjectList
       &&((TeXObjectList)object).size() == 1)
      {
         object = ((TeXObjectList)object).pop();
      }

      if (!(object instanceof ControlSequence))
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_CS_EXPECTED, object.toString());
      }

      String csName = ((ControlSequence)object).getName();

      object = parser.popNextArg(true, '[', ']');

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
               expanded = ((Expandable)object).expandfully(parser);
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

         defValue = parser.popNextArg(true, '[', ']');
      }

      TeXObject definition = parser.popNextArg(isStar);

      ((LaTeXParserListener)parser.getListener()).newcommand(
         name, csName, isStar, numParams, defValue, definition);
   }

}
