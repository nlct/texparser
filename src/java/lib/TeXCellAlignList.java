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

import java.util.Vector;
import java.io.IOException;

public class TeXCellAlignList extends Vector<TeXCellAlign>
{
   public TeXCellAlignList(int capacity)
   {
      super(capacity);
   }

   public TeXCellAlignList()
   {
      super();
   }

   public TeXCellAlignList(TeXParser parser, TeXObject alignSpecs)
     throws IOException
   {
      super();
      parseAlignment(parser, alignSpecs);
   }

   public void parseAlignment(TeXParser parser, TeXObject alignSpecs)
    throws IOException
   {
      if (alignSpecs instanceof CharObject)
      {
         int charCode = ((CharObject)alignSpecs).getCharCode();

         add(new TeXCellAlign(charCode));

         return;
      }

      if (!(alignSpecs instanceof TeXObjectList))
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_ILLEGAL_ALIGN,
           alignSpecs.toString(parser));
      }

      TeXObjectList list = (TeXObjectList)alignSpecs;

      TeXCellAlign cellAlign = null;

      while (list.size() > 0)
      {
         TeXObject obj = list.popArg(parser);

         if (!(obj instanceof CharObject))
         {
            throw new TeXSyntaxException(parser,
              TeXSyntaxException.ERROR_ILLEGAL_ALIGN,
              alignSpecs.toString(parser));
         }

         int code = ((CharObject)obj).getCharCode();

         // Treating '!' the same as '@' for now
         if (code == '@'|| code == '!')
         {
            TeXObject arg = list.popArg(parser);

            if (cellAlign == null)
            {
               cellAlign = new TeXCellAlign(-1);
               cellAlign.addBefore(arg);
            }
            else
            {
               if (cellAlign.getAlign() == -1)
               {
                  cellAlign.addBefore(arg);
               }
               else
               {
                  cellAlign.addAfter(arg);
               }
            }
         }
         else if (code == '>')
         {
            TeXObject arg = list.popArg(parser);

            if (cellAlign == null)
            {
               cellAlign = new TeXCellAlign(-1);
               cellAlign.addPreShift(arg);
            }
            else
            {
               if (cellAlign.getAlign() == -1)
               {
                  cellAlign.addPreShift(arg);
               }
               else
               {
                  throw new TeXSyntaxException(parser,
                    TeXSyntaxException.ERROR_ILLEGAL_ALIGN,
                    alignSpecs.toString(parser));
               }
            }
         }
         else if (code == '|')
         {
            if (cellAlign == null)
            {
               cellAlign = new TeXCellAlign(-1);
            }

            if (cellAlign.getAlign() == -1)
            {
               cellAlign.addPreRule();
            }
            else
            {
               cellAlign.addPostRule();
            }
         }
         else
         {
            TeXDimension width = null;

            if (requiresDimension(code))
            {
               TeXObject arg = list.popArg(parser);

               if (arg instanceof TeXDimension)
               {
                  width = (TeXDimension)arg;
               }
               else if (arg instanceof TeXObjectList)
               {
                  width = ((TeXObjectList)arg).popDimension(parser);
               }
               else
               {
                  throw new TeXSyntaxException(parser,
                    TeXSyntaxException.ERROR_DIMEN_EXPECTED,
                      arg.toString(parser));
               }
            }

            if (cellAlign == null) 
            {
               cellAlign = new TeXCellAlign(code);
            }
            else if (cellAlign.getAlign() == -1)
            {
               cellAlign.setAlign(code);
            }
            else
            {
               add(cellAlign);
               cellAlign = new TeXCellAlign(code);
            }

            cellAlign.setWidth(width);
         }
      }

      if (cellAlign != null && cellAlign.getAlign() != -1)
      {
         add(cellAlign);
      }
   }

   public boolean requiresDimension(int code)
   {
      return (code == 'p' || code == 'm' || code == 'b');
   }
}

