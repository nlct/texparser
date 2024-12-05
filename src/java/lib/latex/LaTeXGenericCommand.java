/*
    Copyright (C) 2013-2024 Nicola L.C. Talbot
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

public class LaTeXGenericCommand extends GenericCommand
{
   public LaTeXGenericCommand(String name)
   {
      this(true, name);
   }

   public LaTeXGenericCommand(boolean isShort, String name)
   {
      super(isShort, name, null, new TeXObjectList());
      latexSyntax = null;
   }

   public LaTeXGenericCommand(boolean isShort, String name, 
       TeXObjectList definition)
   {
      this(isShort, name, false, definition);
   }

   public LaTeXGenericCommand(boolean isShort, String name, 
       boolean isRobust, TeXObjectList definition)
   {
      super(isShort, name, null, definition);
      latexSyntax = null;
      this.isRobust = isRobust;
   }

   public LaTeXGenericCommand(boolean isShort, String name,
     String syntaxString, TeXObjectList definition)
   {
      this(isShort, name, false, syntaxString, definition);
   }

   public LaTeXGenericCommand(boolean isShort, String name, boolean isRobust,
     String syntaxString, TeXObjectList definition)
   {
      this(isShort, name, syntaxString.toCharArray(), definition, 
       (TeXObjectList)null);
   }

   public LaTeXGenericCommand(boolean isShort, String name,
     char[] syntaxArray, TeXObjectList definition)
   {
      this(isShort, name, false, syntaxArray, definition);
   }

   public LaTeXGenericCommand(boolean isShort, String name,
     boolean isRobust, char[] syntaxArray, TeXObjectList definition)
   {
      this(isShort, name, isRobust, syntaxArray, definition, (TeXObjectList)null);
   }

   public LaTeXGenericCommand(boolean isShort, String name,
     String syntaxString, TeXObjectList definition,
     TeXObjectList defaultArgList)
   {
      this(isShort, name, false, syntaxString, definition, defaultArgList);
   }

   public LaTeXGenericCommand(boolean isShort, String name, boolean isRobust,
     String syntaxString, TeXObjectList definition,
     TeXObjectList defaultArgList)
   {
      this(isShort, name, isRobust, syntaxString.toCharArray(), definition, 
           defaultArgList);
   }

   public LaTeXGenericCommand(boolean isShort, String name,
     char[] syntaxArray, TeXObjectList definition, TeXObjectList defaultArgList)
   {
      this(isShort, name, false, syntaxArray, definition, defaultArgList);
   }

   public LaTeXGenericCommand(boolean isShort, String name, boolean isRobust,
     char[] syntaxArray, TeXObjectList definition, TeXObjectList defaultArgList)
   {
      super(isShort, name, null, definition);
      latexSyntax = syntaxArray;
      this.isRobust = isRobust;

      int numDefArgs = (defaultArgList == null ? 0 : defaultArgList.size());

      defaultArgs = (numDefArgs == 0 ? null : new TeXObject[numDefArgs]);

      numArgs = 0;

      if (latexSyntax != null)
      {
         int defArgIdx = 0;

         for (int i = 0; i < latexSyntax.length; i++)
         {
            if (latexSyntax[i] == SYNTAX_MANDATORY)
            {
               numArgs++;
            }
            else if (latexSyntax[i] == SYNTAX_OPTIONAL)
            {
               numArgs++;

               defaultArgs[defArgIdx] = 
                  (TeXObject)defaultArgList.get(defArgIdx).clone();

               defArgIdx++;
            }
            else
            {
               throw new IllegalArgumentException(
                 "Invalid argument identifier: "+latexSyntax[i]);
            }
         }
      }
   }

   public LaTeXGenericCommand(boolean isShort, String name,
     char[] syntaxArray, TeXObjectList definition, TeXObject[] defaultArgArray)
   {
      this(isShort, name, false, syntaxArray, definition, defaultArgArray);
   }

   public LaTeXGenericCommand(boolean isShort, String name, boolean isRobust,
     char[] syntaxArray, TeXObjectList definition, TeXObject[] defaultArgArray)
   {
      super(isShort, name, null, definition);
      latexSyntax = syntaxArray;
      this.isRobust = isRobust;

      int numDefArgs = (defaultArgArray == null ? 0 : defaultArgArray.length);

      defaultArgs = (numDefArgs == 0 ? null : new TeXObject[numDefArgs]);

      numArgs = 0;

      if (latexSyntax != null)
      {
         int defArgIdx = 0;

         for (int i = 0; i < latexSyntax.length; i++)
         {
            if (latexSyntax[i] == SYNTAX_MANDATORY)
            {
               numArgs++;
            }
            else if (latexSyntax[i] == SYNTAX_OPTIONAL)
            {
               numArgs++;

               defaultArgs[defArgIdx] = 
                  (TeXObject)defaultArgArray[defArgIdx].clone();

               defArgIdx++;
            }
            else
            {
               throw new IllegalArgumentException(
                 "Invalid argument identifier: "+latexSyntax[i]);
            }
         }
      }
   }

   @Override
   public Object clone()
   {
      return new LaTeXGenericCommand(isShort, getName(), isRobust, latexSyntax, 
        (TeXObjectList)getDefinition().clone());
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof LaTeXGenericCommand)) return false;

      if (this == obj) return true;

      LaTeXGenericCommand cs = (LaTeXGenericCommand)obj;

      if (isShort != cs.isShort || !getDefinition().equals(cs.getDefinition()))
      {
         return false;
      }

      return Arrays.equals(latexSyntax, cs.latexSyntax)
           && Arrays.equals(defaultArgs, cs.defaultArgs);
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
      TeXObject[] args = (numArgs == 0 ? null : new TeXObject[numArgs]);

      if (numArgs > 0)
      {
         byte popStyle = TeXObjectList.getArgPopStyle(isShort);

         int optIdx = 0;

         for (int i = 0; i < numArgs; i++)
         {
            TeXObject object=null;

            switch (latexSyntax[i])
            {
               case SYNTAX_OPTIONAL: 

                  object = remainingStack.popArg(parser, popStyle, '[', ']');

                  if (object == null)
                  {
                     object = defaultArgs[optIdx++];
                  }

                  if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
                  {
                     parser.logMessage(String.format("OARG[%d]: %s", i, object));
                  }

                  if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION))
                  {
                     parser.logMessage(String.format("OARG[%d]: %s", i, 
                       object.toString(parser)));
                  }

               break;
               case SYNTAX_MANDATORY: 
                  object = remainingStack.popArg(parser, popStyle);

                  if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
                  {
                     parser.logMessage(String.format("MARG[%d]: %s", i, object));
                  }

                  if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION))
                  {
                     parser.logMessage(String.format("MARG[%d]: %s", i, 
                        object.toString(parser)));
                  }

               break;
            }

            args[i] = object;
         }
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
      TeXObject[] args = (numArgs == 0 ? null : new TeXObject[numArgs]);

      if (numArgs > 0)
      {
         byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;

         if (isShort)
         {
            popStyle = (byte)(TeXObjectList.POP_SHORT | popStyle);
         }

         int optIdx = 0;

         for (int i = 0; i < numArgs; i++)
         {
            TeXObject object=null;

            switch (latexSyntax[i])
            {
               case SYNTAX_OPTIONAL: 

                  object = parser.popNextArg(popStyle, '[', ']');

                  if (object == null)
                  {
                     object = defaultArgs[optIdx++];
                  }

                  if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
                  {
                     parser.logMessage(String.format("OARG[%d]: %s", i, object));
                  }

                  if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION))
                  {
                     parser.logMessage(String.format("OARG[%d]: %s", i, 
                       object.toString(parser)));
                  }

               break;
               case SYNTAX_MANDATORY: 
                  object = parser.popNextArg(popStyle);

                  if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
                  {
                     parser.logMessage(String.format("MARG[%d]: %s", i, object));
                  }

                  if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION))
                  {
                     parser.logMessage(String.format("MARG[%d]: %s", i, 
                       object.toString(parser)));
                  }

               break;
            }

            args[i] = object;
         }
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

   public char[] getLaTeXSyntax()
   {
      return latexSyntax;
   }

   public String getLaTeXSyntaxString()
   {
      return latexSyntax == null ? "" : new String(latexSyntax);
   }

   public String toString()
   {
      return String.format("%s[name=%s,robust=%s,syntax=%s,definition=%s]",
       getClass().getSimpleName(), getName(), isRobust,  
       getLaTeXSyntaxString(), getDefinition());
   }

   private char[] latexSyntax;

   private TeXObject[] defaultArgs;

   protected boolean isRobust = false;

   public static final char SYNTAX_OPTIONAL  = 'o';
   public static final char SYNTAX_MANDATORY = 'm';
}
