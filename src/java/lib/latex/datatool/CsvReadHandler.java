/*
    Copyright (C) 2023 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class CsvReadHandler implements FileMapHandler
{
   public CsvReadHandler(DataBase database, IOSettings settings)
   {
      this.database = database;
      this.settings = settings;
   }

   @Override
   public void processLine(TeXParser parser, TeXObjectList line, int lineNumber)
   throws IOException
   {
      if (rowIdx == 0)
      {
         if (settings.getSkipLines() > lineNumber)
         {
            if (settings.isHeaderIncluded())
            {
               parseHeader(parser, line);
            }
            else
            {
               parseRow(parser, line);
            }
         }
      }
      else
      {
         parseRow(parser, line);
      }
   }

   protected void parseHeader(TeXParser parser, TeXObjectList line)
   throws IOException
   {
// TODO
   }

   protected void parseRow(TeXParser parser, TeXObjectList line)
   throws IOException
   {
// TODO
   }

   @Override
   public void processCompleted(TeXParser parser)
     throws IOException
   {
// TODO
   }

   DataBase database;
   IOSettings settings;
   int rowIdx = 0;
}
