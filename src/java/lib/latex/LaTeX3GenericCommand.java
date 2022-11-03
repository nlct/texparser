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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.Vector;
import java.util.Arrays;

import com.dickimawbooks.texparserlib.*;

public class LaTeX3GenericCommand extends GenericCommand
{
   public LaTeX3GenericCommand(String name, 
     L3Arg[] argList, TeXObjectList definition)
   {
      this(name, true, argList, definition);
   }

   public LaTeX3GenericCommand(String name, boolean isRobust,
     L3Arg[] argList, TeXObjectList definition)
   {
      super(true, name, null, definition);
      this.isRobust = isRobust;

      this.argList = argList;
   }

   public Object clone()
   {
      return new LaTeX3GenericCommand(getName(), isRobust, argList, 
        (TeXObjectList)getDefinition().clone());
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof LaTeX3GenericCommand)) return false;

      if (this == obj) return true;

      LaTeX3GenericCommand cs = (LaTeX3GenericCommand)obj;

      if (isShort != cs.isShort || !getDefinition().equals(cs.getDefinition()))
      {
         return false;
      }

      if (argList == null && cs.argList == null)
      {
         return true;
      }

      if (argList == null || cs.argList == null)
      {
         return false;
      }


      return Arrays.equals(argList, cs.argList);
   }

   @Override
   public boolean canExpand()
   {
      return !isRobust;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return isRobust ? null : getReplacement(parser, list);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return isRobust ? null : getReplacement(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return isRobust ? null : getReplacement(parser, list).expandfully(parser, list);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return isRobust ? null : getReplacement(parser).expandfully(parser);
   }

   @Override
   protected TeXObjectList getReplacement(TeXParser parser,
     TeXObjectList remainingStack)
     throws IOException
   {
      if (argList == null)
      {
         return (TeXObjectList)getDefinition().clone();
      }

      TeXObject[] args = new TeXObject[argList.length];

      for (int i = 0; i < argList.length; i++)
      {
         TeXObject object=null;

         byte popStyle = argList[i].getPopStyle();

         int token1 = argList[i].getToken1();
         int token2 = argList[i].getToken2();

         switch (argList[i].getId())
         {
            case 'o': 
            case 'O': 
              token1 = '[';
              token2 = ']';
            case 'r': 
            case 'R': 
            case 'd': 
            case 'D': 

            object = remainingStack.popArg(parser, popStyle, 
              token1, token2);

            if (object == null)
            {
               object = argList[i].getDefaultValue();

               if (object != null)
               {
                  object = (TeXObject)object.clone();
               }
            }

            break;
            case 'm': 

               object = remainingStack.popArg(parser, popStyle);

            break;
            case 's':
               token1 = '*';
            case 't':

               if (popModifier(parser, remainingStack, token1) == token1)
               {
                  object = parser.getListener().getControlSequence("BooleanTrue");
               }
               else
               {
                  object = parser.getListener().getControlSequence("BooleanFalse");
               }

            break;
            default:

               throw new LaTeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_UNSUPPORTED_XPARSE_TYPE,
                 argList[i], toString(parser));
         }

         if (object == null)
         {
            object = L3Arg.createNoValue(parser);
         }

         args[i] = object;
      }

      TeXObjectList replacement = new TeXObjectList();

      addReplacements(parser, replacement, args, getDefinition());

      if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
      {
         parser.logMessage("Replacement: "+replacement);
      }

      if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION))
      {
         parser.logMessage("Replacement: "+replacement.toString(parser));
      }

      return replacement;
   }

   @Override
   protected TeXObjectList getReplacement(TeXParser parser)
     throws IOException
   {
      if (argList == null)
      {
         return (TeXObjectList)getDefinition().clone();
      }

      TeXObject[] args = new TeXObject[argList.length];

      for (int i = 0; i < argList.length; i++)
      {
         TeXObject object=null;

         byte popStyle = argList[i].getPopStyle();

         int token1 = argList[i].getToken1();
         int token2 = argList[i].getToken2();

         switch (argList[i].getId())
         {
            case 'o': 
            case 'O': 
              token1 = '[';
              token2 = ']';
            case 'r': 
            case 'R': 
            case 'd': 
            case 'D': 

            object = parser.popNextArg(popStyle, token1, token2);

            if (object == null)
            {
               object = argList[i].getDefaultValue();

               if (object != null)
               {
                  object = (TeXObject)object.clone();
               }
            }

            break;
            case 'm': 

               object = parser.popNextArg(popStyle);

            break;
            case 's':
               token1 = '*';
            case 't':

               if (popModifier(parser, parser, token1) == token1)
               {
                  object = parser.getListener().getControlSequence("BooleanTrue");
               }
               else
               {
                  object = parser.getListener().getControlSequence("BooleanFalse");
               }

            break;
            case 'v':

               object = parser.popVerb();

            break;
            default:

               throw new LaTeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_UNSUPPORTED_XPARSE_TYPE,
                 argList[i], toString(parser));
         }

         if (object == null)
         {
            object = L3Arg.createNoValue(parser);
         }

         args[i] = object;
      }

      TeXObjectList replacement = new TeXObjectList();

      addReplacements(parser, replacement, args, getDefinition());

      if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
      {
         parser.logMessage("Replacement: "+replacement);
      }

      if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION))
      {
         parser.logMessage("Replacement: "+replacement.toString(parser));
      }

      return replacement;
   }

   protected void addReplacements(TeXParser parser, TeXObjectList replacement, 
     TeXObject[] args, TeXObjectList list)
   throws TeXSyntaxException
   {
      for (TeXObject object : list)
      {
         if (object instanceof Param)
         {
            int idx = ((Param)object).getDigit();

            if (args == null || idx > args.length)
            {
               throw new TeXSyntaxException(parser,
                TeXSyntaxException.ERROR_ILLEGAL_PARAM, idx, toString(parser));
            }

            idx--;

            replacement.add((TeXObject)args[idx].clone());
         }
         else if (object instanceof DoubleParam)
         {
            replacement.add((TeXObject)((DoubleParam)object).next().clone());
         }
         else if (object instanceof TeXObjectList)
         {
            TeXObjectList subList = ((TeXObjectList)object).createList();
            replacement.add(subList);

            addReplacements(parser, subList, args, (TeXObjectList)object);
         }
         else
         {
            replacement.add((TeXObject)object.clone());
         }
      }
   }

   public String getLaTeXSyntaxString()
   {
      StringBuilder builder = new StringBuilder();

      if (argList != null)
      {
         for (L3Arg arg : argList)
         {
            builder.append(arg.toString());
         }
      }

      return builder.toString();
   }

   public String toString()
   {
      return String.format("%s[name=%s,robust=%s,syntax=%s,definition=%s]",
       getClass().getSimpleName(), getName(), isRobust,  
       getLaTeXSyntaxString(), getDefinition());
   }

   private L3Arg[] argList;

   protected boolean isRobust = true;
}
