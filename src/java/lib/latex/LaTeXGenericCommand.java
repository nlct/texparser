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
      super(isShort, name, null, definition);
      latexSyntax = null;
   }

   public LaTeXGenericCommand(boolean isShort, String name,
     String syntaxString, TeXObjectList definition)
   {
      this(isShort, name, syntaxString.toCharArray(), definition, 
       (TeXObjectList)null);
   }

   public LaTeXGenericCommand(boolean isShort, String name,
     char[] syntaxArray, TeXObjectList definition)
   {
      this(isShort, name, syntaxArray, definition, (TeXObjectList)null);
   }

   public LaTeXGenericCommand(boolean isShort, String name,
     String syntaxString, TeXObjectList definition,
     TeXObjectList defaultArgList)
   {
      this(isShort, name, syntaxString.toCharArray(), definition, 
           defaultArgList);
   }

   public LaTeXGenericCommand(boolean isShort, String name,
     char[] syntaxArray, TeXObjectList definition, TeXObjectList defaultArgList)
   {
      super(isShort, name, null, definition);
      latexSyntax = syntaxArray;

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
      super(isShort, name, null, definition);
      latexSyntax = syntaxArray;

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

   public Object clone()
   {
      return new LaTeXGenericCommand(isShort, getName(), latexSyntax, 
        (TeXObjectList)getDefinition().clone());
   }

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

      byte popStyle = isShort ? TeXObjectList.POP_SHORT : 0;

      if (numArgs > 0)
      {
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

               break;
               case SYNTAX_MANDATORY: 
                  object = remainingStack.popArg(parser, popStyle);
               break;
            }

            args[i] = object;
         }
      }

      TeXObjectList replacement = new TeXObjectList();

      addReplacements(parser, replacement, args, getDefinition());

      return replacement;
   }

   private TeXObjectList getReplacement(TeXParser parser)
     throws IOException
   {
      TeXObject[] args = (numArgs == 0 ? null : new TeXObject[numArgs]);

      if (numArgs > 0)
      {
         byte popStyle = isShort ? TeXObjectList.POP_SHORT : 0;

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

               break;
               case SYNTAX_MANDATORY: 
                  object = parser.popNextArg(popStyle);
               break;
            }

            args[i] = object;
         }
      }

      TeXObjectList replacement = new TeXObjectList();

      addReplacements(parser, replacement, args, getDefinition());

      return replacement;
   }

   private void addReplacements(TeXParser parser, TeXObjectList replacement, 
     TeXObject[] args, TeXObjectList list)
   {
      for (TeXObject object : list)
      {
         if (object instanceof Param)
         {
            int idx = ((Param)object).getDigit()-1;

            replacement.add((TeXObject)args[idx].clone());
         }
         else if (object instanceof DoubleParam)
         {
            replacement.add(((DoubleParam)object).next());
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
      return String.format("%s[name=%s,syntax=%s,definition=%s]",
       getClass().getSimpleName(), getName(),  
       getLaTeXSyntaxString(), getDefinition());
   }

   private char[] latexSyntax;

   private TeXObject[] defaultArgs;

   public static final char SYNTAX_OPTIONAL  = 'o';
   public static final char SYNTAX_MANDATORY = 'm';
}
