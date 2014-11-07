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
import java.util.Vector;

public class GenericCommand extends Command
{
   private GenericCommand()
   {
   }

   public GenericCommand(String name)
   {
      this(name, null, new TeXObjectList());
   }

   public GenericCommand(String name, TeXObjectList syntax,
      TeXObjectList definition)
   {
      this(true, name, syntax, definition);
   }

   public GenericCommand(boolean isShort, String name, TeXObjectList syntax,
      TeXObjectList definition)
   {
      super();
      this.isShort = isShort;
      this.name = name;
      this.syntax = syntax;
      this.definition = definition;
      this.isDelimited = false;

      if (syntax != null)
      {
         numArgs = 0;

         for (TeXObject obj : syntax)
         {
            if (obj instanceof Param)
            {
               if (((Param)obj).getDigit() == -1)
               {
                  isDelimited = true;
               }
               else
               {
                  numArgs++;
               }
            }
         }
      }
   }

   public String getName()
   {
      return name;
   }

   public Object clone()
   {
      return new GenericCommand(isShort, name, syntax, definition);
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

   public void process(TeXParser parser, TeXObjectList remainingStack)
     throws IOException
   {
      TeXObject[] args = (numArgs == 0 ? null : new TeXObject[numArgs]);

      int n = 0;

      if (syntax != null)
      {
         for (TeXObject obj : syntax)
         {
            if (obj instanceof Param)
            {
               if (((Param)obj).getDigit() != -1)
               {
                  if (isDelimited && n == numArgs-1)
                  {
                     args[n] = remainingStack.popToGroup(parser, isShort);
                  }
                  else
                  {
                     args[n] = remainingStack.popStack(isShort);
                  }

                  n++;
               }
            }
            else
            {
               TeXObject nextObj = remainingStack.popArg();

               if (nextObj == null || !obj.equals(nextObj))
               {
                  throw new TeXSyntaxException(parser,
                    TeXSyntaxException.ERROR_SYNTAX,
                    toString(parser));
               }
            }
         }
      }

      TeXObjectList stack = new TeXObjectList(definition.size());

      if (n != numArgs)
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_SYNTAX,
           toString(parser));
      }

      for (TeXObject obj : definition)
      {
         if (obj instanceof Param)
         {
            stack.add(args[((Param)obj).getDigit()]);
         }
         else
         {
            stack.add(obj);
         }
      }

      stack.process(parser, remainingStack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject[] args = (numArgs == 0 ? null : new TeXObject[numArgs]);

      int n = 0;

      if (syntax != null)
      {
         for (TeXObject obj : syntax)
         {
            if (obj instanceof Param)
            {
               if (((Param)obj).getDigit() != -1)
               {
                  if (isDelimited && n == numArgs-1)
                  {
                     args[n] = parser.popToGroup(isShort);
                  }
                  else
                  {
                     args[n] = parser.popNextArg(isShort);
                  }

                  n++;
               }
            }
            else
            {
               TeXObject nextObj = parser.popNextArg(isShort);

               if (nextObj == null)
               {
                  throw new TeXSyntaxException(parser,
                    TeXSyntaxException.ERROR_SYNTAX,
                    toString(parser));
               }
               else if (!obj.equals(nextObj))
               {
                  throw new TeXSyntaxException(parser,
                    TeXSyntaxException.ERROR_SYNTAX,
                    toString(parser));
               }
            }
         }
      }

      TeXObjectList stack = new TeXObjectList(definition.size());

      if (n != numArgs)
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_SYNTAX,
           toString(parser));
      }

      for (TeXObject obj : definition)
      {
         if (obj instanceof Param)
         {
            stack.add(args[((Param)obj).getDigit()]);
         }
         else
         {
            stack.add(obj);
         }
      }

      stack.process(parser);
   }

   private String name;
   private TeXObjectList syntax, definition;
   private int numArgs=0;
   private boolean isShort;
   private boolean isDelimited;
}
