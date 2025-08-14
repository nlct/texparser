/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.latex3;

import java.io.IOException;
import java.util.Vector;
import java.util.Arrays;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;
import com.dickimawbooks.texparserlib.latex.End;

public class LaTeX3GenericEnvironment extends Declaration
{
   public LaTeX3GenericEnvironment(String name, 
     L3Arg[] argList, TeXObjectList beginCode, TeXObjectList endCode)
   {
      this(name, argList, false, beginCode, endCode);
   }

   public LaTeX3GenericEnvironment(String name, L3Arg[] argList,
     boolean isModeSwitcher, TeXObjectList beginCode, TeXObjectList endCode)
   {
      super(name);

      this.argList = argList;
      this.beginCode = beginCode;
      this.endCode = endCode;
   }

   public Object clone()
   {
      return new LaTeX3GenericEnvironment(getName(), argList, 
        (TeXObjectList)beginCode.clone(),
        (TeXObjectList)endCode.clone());
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof LaTeX3GenericEnvironment)) return false;

      if (this == obj) return true;

      LaTeX3GenericEnvironment cs = (LaTeX3GenericEnvironment)obj;

      if (!beginCode.equals(cs.beginCode) || !endCode.equals(cs.endCode))
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
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   protected TeXObjectList getBeginReplacement(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return getReplacement(beginCode, parser, stack);
   }

   protected TeXObjectList getReplacement(TeXObjectList code,
     TeXParser parser,
     TeXObjectList remainingStack)
     throws IOException
   {
      if (argList == null)
      {
         return (TeXObjectList)code.clone();
      }

      params = new TeXObject[argList.length];

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

            object = TeXParserUtils.popOptArg(popStyle, parser, remainingStack, 
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

               object = TeXParserUtils.popArg(parser, remainingStack, popStyle);

            break;
            case 'b': 

               object = popBody(parser, remainingStack, popStyle);

               if (argList[i].isIgnoreSpace())
               {
                  object = TeXParserUtils.trim(object);
               }

            break;
            case 'c': 

               if (remainingStack == parser || remainingStack == null)
               {
                  object = parser.popVerbToEndEnv(getName());
               }
               else
               {
                  object = popVerbBody(parser, remainingStack);
               }

               if (argList[i].isIgnoreSpace())
               {
                  object = TeXParserUtils.trim(object);
               }

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

         params[i] = object;
      }

      TeXObjectList replacement = new TeXObjectList();

      addReplacements(parser, replacement, params, code);

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

   @Override
   public boolean isModeSwitcher()
   {
      return isModeSwitcher;
   }

   public TeXObjectList popBody(TeXParser parser, byte popStyle)
     throws IOException
   {
      return popBody(parser, parser, popStyle);
   }

   public TeXObjectList popBody(TeXParser parser, TeXObjectList stack,
      byte popStyle)
     throws IOException
   {
      TeXObject object = stack.pop();

      TeXObjectList contents = parser.getListener().createStack();

      while (object != null)
      {
         object = TeXParserUtils.resolve(object, parser);

         if (object instanceof End)
         {
            String envName = popLabelString(parser, stack);

            Group grp = parser.getListener().createGroup(envName);

            if (envName.equals(getName()))
            {
               stack.push(grp);
               stack.push(object);
               break;
            }

            contents.add(object);
            contents.add(grp);
         }
         else
         {
            contents.add(object);
         }

         object = stack.pop();
      }

      return contents;
   }

   public TeXObjectList popVerbBody(TeXParser parser)
     throws IOException
   {
      return popVerbBody(parser, parser);
   }

   public TeXObjectList popVerbBody(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      byte popStyle = TeXObjectList.POP_RETAIN_IGNOREABLES;

      TeXObject object = TeXParserUtils.pop(parser, stack, popStyle);

      TeXObjectList contents = listener.createStack();

      while (object != null)
      {
         if (object instanceof ControlSequence
              && ((ControlSequence)object).getName().equals("end"))
         {
            String envName = popLabelString(parser, stack);

            Group grp = listener.createGroup(envName);

            if (envName.equals(getName()))
            {
               stack.push(grp);
               stack.push(object);
               break;
            }

            contents.add(object);
            contents.add(grp);
         }
         else if (object instanceof Eol || object instanceof Par)
         {
            contents.add(listener.getControlSequence("obeyedline"));
         }
         else if (object instanceof Comment)
         {
            contents.add(listener.createString(
             new String(Character.toChars(parser.getCommentChar()))
             + ((Comment)object).getText()));

            contents.add(listener.getControlSequence("obeyedline"));
         }
         else
         {
            contents.add(listener.createString(object.toString(parser)));
         }

         object = TeXParserUtils.pop(parser, stack, popStyle);
      }

      return contents;
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_GENERIC_CS))
      {
         parser.logMessage("Processing begin "+toString());
      }

      TeXObjectList repl = getBeginReplacement(parser, parser);

      repl.process(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_GENERIC_CS))
      {
         parser.logMessage("Processing begin "+toString());
      }

      TeXObjectList repl = getBeginReplacement(parser, stack);

      repl.process(parser, stack);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (endCode != null && !endCode.isEmpty())
      {
         if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_GENERIC_CS))
         {
            parser.logMessage("Processing end "+toString());
         }

         TeXObjectList replacement = new TeXObjectList();

         addReplacements(parser, replacement, params, endCode);

         if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
         {
            parser.logMessage("Replacement: "+replacement);
         }

         if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION))
         {
            parser.logMessage("Replacement: "+replacement.toString(parser));
         }

         TeXParserUtils.process(replacement, parser, stack);
      }
   }

   public String toString()
   {
      return String.format("%s[name=%s,syntax=%s,modeSwitcher=%s,begin=%s,end=%s]",
       getClass().getSimpleName(), getName(),   
       getLaTeXSyntaxString(), isModeSwitcher,
       beginCode, endCode);
   }

   private L3Arg[] argList;

   private boolean isModeSwitcher=false;

   TeXObjectList beginCode, endCode;

   TeXObject[] params;
}
