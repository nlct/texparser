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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class L2LOther extends Other
{
   public L2LOther(int charCode)
   {
      super(charCode);
   }


   public Object clone()
   {
      return new L2LOther(getCharCode());
   }

   public void process(TeXParser parser) throws IOException
   {
      if (getCharCode() == (int)'.')
      {
         TeXObject nextObj = parser.pop();

         if (nextObj != null
          && nextObj instanceof Other
          && ((Other)nextObj).getCharCode() == (int)'.')
         {
            TeXObject nextObj2 = parser.pop();

            if (nextObj2 != null
              && nextObj2 instanceof Other
              && ((Other)nextObj2).getCharCode() == (int)'.')
            {
               parser.getListener().getControlSequence("ldots").process(parser);
            }
            else
            {
               if (nextObj2 != null)
               {
                  parser.push(nextObj2);
               }

               parser.push(nextObj);
               super.process(parser);
            }
         }
         else
         {
            if (nextObj != null)
            {
               parser.push(nextObj);
            }

            super.process(parser);
         }
      }
      else
      {
         super.process(parser);
      }
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (getCharCode() == (int)'.')
      {
         TeXObject nextObj = null;

         nextObj = stack.pop();

         if (nextObj != null
          && nextObj instanceof Other
          && ((Other)nextObj).getCharCode() == (int)'.')
         {
            TeXObject nextObj2 = null;

            nextObj2 = stack.pop();

            if (nextObj2 != null
              && nextObj2 instanceof Other
              && ((Other)nextObj2).getCharCode() == (int)'.')
            {
               parser.getListener().getControlSequence("ldots").process(parser, stack);
            }
            else
            {
               if (nextObj2 != null)
               {
                  stack.push(nextObj2);
               }

               stack.push(nextObj);
               super.process(parser, stack);
            }
         }
         else
         {
            if (nextObj != null)
            {
               stack.push(nextObj);
            }

            super.process(parser, stack);
         }
      }
      else
      {
         super.process(parser, stack);
      }
   }


}

