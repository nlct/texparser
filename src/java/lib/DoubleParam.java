/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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

public class DoubleParam extends AbstractTeXObject
   implements ParameterToken,Expandable
{
   public DoubleParam(ParameterToken param)
   {
      setNext(param);
   }

   @Override
   public Object clone()
   {
      DoubleParam dblParam = new DoubleParam((ParameterToken)param.clone());

      dblParam.setCharCode(getCharCode());

      return dblParam;
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

      list.addAll(param.splitTokens(parser));

      return list;
   }

   @Override
   public TeXObject reconstitute(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      byte popStyle = TeXObjectList.POP_SHORT;
      boolean pop = false;

      TeXObject nextToken = TeXParserUtils.peek(parser, stack, popStyle);

      ParameterToken paramToken;

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
            Param param = parser.getListener().getParam(cp-'0');
            param.setCharCode(getCharCode());
            paramToken = param;
            pop = true;
         }
         else if (cat == TeXParser.TYPE_BG)
         {
            Param param = parser.getListener().getParam(-1);
            param.setCharCode(getCharCode());
            paramToken = param;
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
         else
         {
            Param param = parser.getListener().getParam(0);
            param.setCharCode(getCharCode());
            paramToken = param;
         }
      }
      else
      {
         Param param = parser.getListener().getParam(0);
         param.setCharCode(getCharCode());
         paramToken = param;
      }

      if (pop)
      {
         TeXParserUtils.pop(parser, stack, popStyle);
      }

      return paramToken;
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObjectList expanded = new TeXObjectList();

      expanded.add(next());

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public ParameterToken next()
   {
      return param;
   }

   @Override
   public Param tail()
   {
      if (param instanceof Param)
      {
         return (Param)param;
      }
      else
      {
         return param.tail();
      }
   }

   public void setNext(ParameterToken param)
   {
      this.param = param;
   }

   @Override
   public String toString(TeXParser parser)
   {
      return String.format("%s%s", 
         new String(Character.toChars(charCode)),
         next().toString(parser));
   }

   @Override
   public String format()
   {
      return String.format("%s%s", 
         new String(Character.toChars(charCode)),
         next().format());
   }

   @Override
   public String toString()
   {
      return String.format("%s[param=%s]", getClass().getSimpleName(),
        next().toString());
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(parser.getListener().getOther(charCode));
      list.add(next());

      return list;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      list.push(param);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      parser.push(param);
   }

   private ParameterToken param;
   protected int charCode = '#';
}

