/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

public class Param extends AbstractTeXObject implements ParameterToken
{
   public Param(int digit)
   {
      setDigit(digit);
   }

   @Override
   public Object clone()
   {
      Param param = new Param(digit);

      param.setCharCode(getCharCode());

      return param;
   }

   public int getDigit()
   {
      return digit;
   }

   public void setDigit(int digit)
   {
      this.digit = digit;
   }

   @Override
   public boolean isSingleToken()
   {
      return false;
   }

   public void setCharCode(int charCode)
   {
      this.charCode = charCode;
   }

   @Override
   public int getCharCode()
   {
      return charCode;
   }

   public int getCatCode()
   {
      return TeXParser.TYPE_PARAM;
   }

   @Override
   public TeXObjectList splitTokens(TeXParser parser)
   {
      TeXObjectList list = new TeXObjectList();

      list.add(new SpecialToken(this, charCode, TeXParser.TYPE_PARAM));

      if (digit > 0)
      {
         list.add(parser.getListener().getOther('0'+digit));
      }

      return list;
   }

   @Override
   public TeXObject reconstitute(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      byte popStyle = TeXObjectList.POP_SHORT;
      boolean pop = false;

      TeXObject nextToken = TeXParserUtils.peek(parser, stack, popStyle);

      Param param = parser.getListener().getParam(0);
      param.setCharCode(getCharCode());

      ParameterToken paramToken = param;

      if (nextToken instanceof ParameterToken)
      {
         DoubleParam dblParam = parser.getListener().getDoubleParam(
           (ParameterToken)nextToken);

         dblParam.setCharCode(getCharCode());

         paramToken = dblParam;

         pop = true;
      }
      else if (nextToken.isSingleToken())
      {
         int cp = ((SingleToken)nextToken).getCharCode();
         int cat = ((SingleToken)nextToken).getCatCode();

         if (cp >= '1' && cp <= '9')
         {
            param.setDigit(cp - '0');
            pop = true;
         }
         else if (cat == TeXParser.TYPE_BG)
         {
            param.setDigit(-1);
         }
         else if (cat == TeXParser.TYPE_PARAM && nextToken instanceof SpecialToken)
         {
            SpecialToken st = (SpecialToken)nextToken;

            TeXParserUtils.pop(parser, stack, popStyle);

            ParameterToken nextParam = (ParameterToken)
               st.getObject().reconstitute(parser, stack);

            DoubleParam dblParam = parser.getListener().getDoubleParam(nextParam);

            dblParam.setCharCode(getCharCode());

            paramToken = dblParam;
         }
      }

      if (pop)
      {
         TeXParserUtils.pop(parser, stack, popStyle);
      }

      return paramToken;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (digit <= 0)
      {
         TeXObject nextToken = stack.peek();

         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_BAD_PARAM,
            nextToken == null ? "" : nextToken.toString(parser));
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      if (digit <= 0)
      {
         TeXObject nextToken = parser.peekStack();

         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_BAD_PARAM,
            nextToken == null ? "" : nextToken.toString(parser));
      }
   }

   @Override
   public String toString(TeXParser parser)
   {
      return format();
   }

   @Override
   public String format()
   {
      if (digit <= 0)
      {
         return new String(Character.toChars(charCode));
      }
      else
      {
         return String.format("%s%d", new String(Character.toChars(charCode)), digit);
      }
   }

   @Override
   public String toString()
   {
      return String.format("%s[%s]", 
        getClass().getSimpleName(), format());
   }

   @Override
   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(parser.getListener().getOther(charCode));

      if (digit != -1)
      {
         list.add(parser.getListener().getOther(0x30+digit));
      }

      return list;
   }

   @Override
   public ParameterToken next()
   {
      return null;
   }

   @Override
   public Param tail()
   {
      return this;
   }

   // -1 indicates #{ 
   // 0 indicates # not followed by digit or {
   private int digit;

   protected int charCode = '#';
}

