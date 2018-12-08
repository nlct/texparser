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
   public GenericCommand(String name)
   {
      this(true, name);
   }

   public GenericCommand(boolean isShort, String name)
   {
      this(isShort, name, null, new TeXObjectList(), false, 0);
   }

   public GenericCommand(String name, TeXObjectList syntax,
      TeXObject definition)
   {
      this(true, name, syntax, definition);
   }

   public GenericCommand(boolean isShort, String name, TeXObjectList syntax,
      TeXObject def)
   {
      super(name);
      this.isShort = isShort;

      if (def instanceof TeXObjectList
      && !(def instanceof Group))
      {
         this.definition = (TeXObjectList)def;
      }
      else
      {
         this.definition = new TeXObjectList();
         this.definition.add(def);
      }

      setSyntax(syntax);
   }

   public GenericCommand(TeXParserListener listener,
      boolean isShort, String name, int numberOfArgs, TeXObjectList definition)
   {
      super(name);
      this.isShort = isShort;
      this.definition = definition;

      setSyntax(listener, numberOfArgs);
   }

   private GenericCommand(boolean isShort, String name, TeXObjectList syntax,
      TeXObjectList definition, boolean isDelimited, int numArgs)
   {
      super(name);
      this.isShort = isShort;
      this.syntax = syntax;
      this.definition = definition;
      this.isDelimited = isDelimited;
      this.numArgs = numArgs;
   }

   public GenericCommand(boolean isShort, String name, TeXObject[] syntaxArray,
      TeXObject[] definitionArray)
   {
      super(name);
      this.isShort = isShort;

      setSyntax(syntaxArray);

      definition = new TeXObjectList(
        definitionArray == null || definitionArray.length == 0 ?
        1 : definitionArray.length);

      if (definitionArray != null)
      {
         for (int i = 0; i < definitionArray.length; i++)
         {
            definition.add(definitionArray[i]);
         }
      }
   }

   public Object clone()
   {
      return new GenericCommand(isShort, getName(), syntax, definition,
        isDelimited, numArgs);
   }

   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof GenericCommand)) return false;

      if (this == obj) return true;

      GenericCommand cs = (GenericCommand)obj;

      if (isShort != cs.isShort || isDelimited != cs.isDelimited
        || numArgs != cs.numArgs
        || (syntax == null && cs.syntax != null)
        || (syntax != null && cs.syntax == null)
        || !definition.equals(cs.definition))
      {
         return false;
      }

      if (syntax == cs.syntax) return true;

      return syntax.equals(cs.syntax);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return getReplacement(parser, list);
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return getReplacement(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return getReplacement(parser, list).expandfully(parser, list);
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return getReplacement(parser).expandfully(parser);
   }

   private TeXObjectList getReplacement(TeXParser parser,
     TeXObjectList remainingStack)
     throws IOException
   {
      TeXObject[] args = (numArgs == 0 ? null : new TeXObject[numArgs]);

      int n = 0;

      if (syntax != null)
      {
         byte popStyle = isShort ? TeXObjectList.POP_SHORT : 0;

         for (TeXObject obj : syntax)
         {
            if (obj instanceof Param)
            {
               if (((Param)obj).getDigit() != -1)
               {
                  if (isDelimited && n == numArgs-1)
                  {
                     args[n] = remainingStack.popToGroup(parser, popStyle);
                  }
                  else
                  {
                     args[n] = remainingStack.popStack(parser, popStyle);
                  }

                  n++;
               }
            }
            else
            {
               TeXObject nextObj = remainingStack.popArg(parser, popStyle);

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
            stack.add(args[((Param)obj).getDigit()-1]);
         }
         else if (obj instanceof DoubleParam)
         {
            stack.add(((DoubleParam)obj).next());
         }
         else if (obj instanceof TeXObjectList)
         {
            stack.add(replaceList(parser, ((TeXObjectList)obj), args));
         }
         else
         {
            stack.add(obj);
         }
      }

      return stack;
   }

   private TeXObjectList getReplacement(TeXParser parser)
     throws IOException
   {
      TeXObject[] args = (numArgs == 0 ? null : new TeXObject[numArgs]);

      int n = 0;

      if (syntax != null)
      {
         byte popStyle = isShort ? TeXObjectList.POP_SHORT : 0;

         for (TeXObject obj : syntax)
         {
            if (obj instanceof Param)
            {
               if (((Param)obj).getDigit() != -1)
               {
                  if (isDelimited && n == numArgs-1)
                  {
                     args[n] = parser.popToGroup(popStyle);
                  }
                  else
                  {
                     args[n] = parser.popNextArg(popStyle);
                  }

                  n++;
               }
            }
            else
            {
               TeXObject nextObj = parser.popNextArg(popStyle);

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
            stack.add(args[((Param)obj).getDigit()-1]);
         }
         else if (obj instanceof DoubleParam)
         {
            stack.add(((DoubleParam)obj).next());
         }
         else if (obj instanceof TeXObjectList)
         {
            stack.add(replaceList(parser, ((TeXObjectList)obj), args));
         }
         else
         {
            stack.add(obj);
         }
      }

      return stack;
   }

   private TeXObject replaceList(TeXParser parser,
     TeXObjectList list, TeXObject[] args)
   {
      TeXObjectList stack = list.createList();

      for (TeXObject obj : list)
      {
         if (obj instanceof Param)
         {
            stack.add(args[((Param)obj).getDigit()-1]);
         }
         else if (obj instanceof DoubleParam)
         {
            stack.add(((DoubleParam)obj).next());
         }
         else if (obj instanceof TeXObjectList)
         {
            stack.add(replaceList(parser, ((TeXObjectList)obj), args));
         }
         else
         {
            stack.add(obj);
         }
      }

      return stack;
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      getReplacement(parser, stack).process(parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      getReplacement(parser).process(parser);
   }

   public boolean isEmpty()
   {
      return definition.isEmpty();
   }

   public String show(TeXParser parser)
    throws IOException
   {
      StringBuilder builder = new StringBuilder();

      if (!isShort)
      {
         builder.appendCodePoint(parser.getEscChar());
         builder.append("long ");
      }

      builder.append("macro:");

      if (syntax != null)
      {
         for (TeXObject obj : syntax)
         {
            if (obj instanceof Param
             && ((Param)obj).getDigit() == -1)
            {
               builder.appendCodePoint(parser.getBgChar());
            }
            else
            {
               builder.append(obj.toString(parser));
            }
         }
      }

      builder.append("->");

      for (TeXObject obj : definition)
      {
         builder.append(obj.toString(parser));
      }

      builder.append(".");

      if (isDelimited)
      {
         builder.appendCodePoint(parser.getBgChar());
      }

      return builder.toString();
   }

   public TeXObjectList getDefinition()
   {
      return definition;
   }

   public String toString()
   {
      return String.format("%s[name=%s,prefix=%d,syntax=%s,definition=%s]",
       getClass().getSimpleName(), getName(), getPrefix(), getSyntax(),
       getDefinition());
   }

   private TeXObjectList definition;
}
