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

import com.dickimawbooks.texparserlib.*;

public class ShortHandOff extends ControlSequence
  implements CatCodeChanger
{
   public ShortHandOff()
   {
      this("shorthandoff");
   }

   public ShortHandOff(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new ShortHandOff(getName());
   }

   // anything popped off parser must be pushed back on
   @Override
   public void applyCatCodeChange(TeXParser parser)
      throws IOException
   {
      TeXParserListener listener = parser.getListener();

      boolean isStar = (popModifier(parser, parser, '*') == '*');
      TeXObject arg = popArg(parser, parser);
      Group grp = listener.createGroup();
      grp.add(arg);

      String sequence = arg.toString(parser);

      for (int i = 0; i < sequence.length(); )
      {
         int cp = sequence.codePointAt(i);
         i += Character.charCount(cp);

         if (parser.isActive(cp))
         {
            int catCode = TeXParser.TYPE_OTHER;

            if (isStar)
            {
               if (cp == '\\')
               {
                  catCode = TeXParser.TYPE_ESC;
               }
               else if (cp == '{')
               {
                  catCode = TeXParser.TYPE_BG;
               }
               else if (cp == '}')
               {
                  catCode = TeXParser.TYPE_EG;
               }
               else if (cp == '$')
               {
                  catCode = TeXParser.TYPE_MATH;
               }
               else if (cp == '&')
               {
                  catCode = TeXParser.TYPE_TAB;
               }
               else if (cp == '#')
               {
                  catCode = TeXParser.TYPE_PARAM;
               }
               else if (cp == '^')
               {
                  catCode = TeXParser.TYPE_SP;
               }
               else if (cp == '_')
               {
                  catCode = TeXParser.TYPE_SB;
               }
            }

            parser.setCatCode(true, cp, catCode);
         }
      }

      parser.push(grp);

      if (isStar)
      {
         parser.push(listener.getOther('*'));
      }
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      boolean isStar = (popModifier(parser, stack, '*') == '*');
      TeXObject arg = popArg(parser, stack);

      String sequence = arg.toString(parser);

      for (int i = 0; i < sequence.length(); )
      {
         int cp = sequence.codePointAt(i);
         i += Character.charCount(cp);

         if (parser.isActive(cp))
         {
            int catCode = TeXParser.TYPE_OTHER;

            if (isStar)
            {
               if (cp == '\\')
               {
                  catCode = TeXParser.TYPE_ESC;
               }
               else if (cp == '{')
               {
                  catCode = TeXParser.TYPE_BG;
               }
               else if (cp == '}')
               {
                  catCode = TeXParser.TYPE_EG;
               }
               else if (cp == '$')
               {
                  catCode = TeXParser.TYPE_MATH;
               }
               else if (cp == '&')
               {
                  catCode = TeXParser.TYPE_TAB;
               }
               else if (cp == '#')
               {
                  catCode = TeXParser.TYPE_PARAM;
               }
               else if (cp == '^')
               {
                  catCode = TeXParser.TYPE_SP;
               }
               else if (cp == '_')
               {
                  catCode = TeXParser.TYPE_SB;
               }
               else if (cp == '%')
               {
                  catCode = TeXParser.TYPE_COMMENT;
               }
               else if (Character.isWhitespace(cp))
               {
                  catCode = TeXParser.TYPE_SPACE;
               }
            }

            parser.setCatCode(true, cp, catCode);
         }
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   @Override
   public ControlSequence getNoOpCommand()
   {
      return new GobbleOptMandOpt(getName(), 0, 1, 0, '*');
   }
}
