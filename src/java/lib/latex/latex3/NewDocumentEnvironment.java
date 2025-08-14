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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.Overwrite;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

public class NewDocumentEnvironment extends ControlSequence
{
   public NewDocumentEnvironment()
   {
      this("NewDocumentEnvironment", Overwrite.FORBID);
   }

   public NewDocumentEnvironment(String name, Overwrite overwrite)
   {
      super(name, false);
      this.overwrite = overwrite;
   }

   public Object clone()
   {
      return new NewDocumentEnvironment(getName(), getOverwrite());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String envName = popLabelString(parser, stack).trim();

      TeXObject argSpecs = popArg(parser, stack);
      TeXObject beginArg = popArg(parser, stack);
      TeXObject endArg = popArg(parser, stack);

      ControlSequence cs = parser.getControlSequence(envName);

      if (cs == null)
      {
         if (overwrite == Overwrite.FORCE)
         {
            throw new TeXSyntaxException(parser,
             TeXSyntaxException.ERROR_UNDEFINED,
             String.format("%s%s", 
              new String(Character.toChars(parser.getEscChar())), envName));
         }
      }
      else
      {
         if (overwrite == Overwrite.FORBID)
         {
            throw new LaTeXSyntaxException(parser,
             LaTeXSyntaxException.ERROR_DEFINED,
             cs.toString(parser));
         }
         else if (overwrite == Overwrite.SKIP)
         {
            return;
         }
      }

      L3Arg[] argList;

      if (parser.isStack(argSpecs))
      {
         TeXObjectList list = (TeXObjectList)argSpecs;

         if (list.isEmpty())
         {
            argList = null;
         }
         else
         {
            Vector<L3Arg> specs = new Vector<L3Arg>();

            byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;

            while (!list.isEmpty())
            {
               TeXObject obj = list.popStack(parser, popStyle);

               if (obj == null) break;

               if (obj.isEmpty()) continue;

               boolean plus = false;
               boolean exclam = false;

               if (obj instanceof CharObject)
               {
                  int cp = ((CharObject)obj).getCharCode();

                  if (cp == '+')
                  {
                     plus = true;

                     obj = list.popStack(parser, popStyle);

                     if (obj instanceof CharObject)
                     {
                        cp = ((CharObject)obj).getCharCode();
                     }
                     else
                     {
                        throw new LaTeXSyntaxException(parser, 
                          LaTeXSyntaxException.ERROR_UNSUPPORTED_XPARSE_TYPE,
                          obj.toString(parser), getName());
                     }
                  }
                  else if (cp == '!')
                  {
                     exclam = true;

                     obj = list.popStack(parser, popStyle);

                     if (obj instanceof CharObject)
                     {
                        cp = ((CharObject)obj).getCharCode();
                     }
                     else
                     {
                        throw new LaTeXSyntaxException(parser, 
                          LaTeXSyntaxException.ERROR_UNSUPPORTED_XPARSE_TYPE,
                          obj.toString(parser), getName());
                     }
                  }

                  TeXObject token1 = null;
                  TeXObject token2 = null;
                  TeXObject defVal = null;

                  switch (cp)
                  {
                     case 'b':
                     case 'c':
                     case 'm':
                     case 'v':
                     case 'o':
                     case 's':
                     break;
                     case 'r':
                     case 'd':
                     case 'R':
                     case 'D':
                        token1 = list.popStack(parser, popStyle);
                        token2 = list.popStack(parser, popStyle);

                        if (cp == 'R' || cp == 'D')
                        {
                           defVal = list.popArg(parser, popStyle);
                        }

                     break;
                     case 'O':

                        defVal = list.popArg(parser, popStyle);

                     break;
                     case 't':

                        token1 = list.popStack(parser, popStyle);

                     break;
                     default:

                       throw new LaTeXSyntaxException(parser, 
                         LaTeXSyntaxException.ERROR_UNSUPPORTED_XPARSE_TYPE,
                         obj.toString(parser), getName());
                  }

                  int token1Cp = -1;

                  if (token1 != null)
                  {
                     if (token1 instanceof CharObject)
                     {
                        token1Cp = ((CharObject)token1).getCharCode();
                     }
                     else
                     {
                        token1Cp = token1.toString(parser).codePointAt(0);
                     }
                  }

                  int token2Cp = -1;

                  if (token2 != null)
                  {
                     if (token2 instanceof CharObject)
                     {
                        token2Cp = ((CharObject)token2).getCharCode();
                     }
                     else
                     {
                        token2Cp = token2.toString(parser).codePointAt(0);
                     }
                  }

                  specs.add(new L3Arg(cp, token1Cp, token2Cp, defVal, !plus, !exclam));
               }
               else
               {
                  throw new LaTeXSyntaxException(parser, 
                    LaTeXSyntaxException.ERROR_UNSUPPORTED_XPARSE_TYPE,
                    obj.toString(parser), getName());
               }
            }

            argList = new L3Arg[specs.size()];
            argList = specs.toArray(argList);
         }
      }
      else
      {
         String str = argSpecs.toString(parser).trim();

         if (str.isEmpty())
         {
            argList = null;
         }
         else
         {
            argList = new L3Arg[1];
            argList[0] = new L3Arg(str.codePointAt(0));
         }
      }

      TeXObjectList beginCode = TeXParserUtils.toList(beginArg, parser);
      TeXObjectList endCode = TeXParserUtils.toList(endArg, parser);

      LaTeX3GenericEnvironment newEnv = new LaTeX3GenericEnvironment(envName,
       argList, beginCode, endCode);

      parser.putControlSequence(true, newEnv);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public Overwrite getOverwrite()
   {
      return overwrite;
   }

   private Overwrite overwrite=Overwrite.FORBID;
}
