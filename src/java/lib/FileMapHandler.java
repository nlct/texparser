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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public interface FileMapHandler
{
   /**
     Handler function used by TeXParser.fileMap(TeXParser,FileMapType,FileMapHandler) to process each line of the file.
     The line number corresponds to the number of "lines" read so far, 
     but one or more of the lines may include EOL with FileMapType.TEX,
     in which case the line number won't correspond to the actual 
     file line number. Use TeXParser.getLineNumber() for the actual file line number.
     Parsing can be prematurely stopped by throwing EOFException.
     @param parser the calling parser
     @param line the line read in from the file
     @param lineNumber the number of lines (that is, data rows) read so far
    */ 
   public void processLine(TeXParser parser, TeXObjectList line, int lineNumber)
      throws IOException;

   /**
     Called by TeXParser.fileMap(TeXParser,FileMapType,FileMapHandler)
     when parsing is finished. Not called if parsing prematurely
     stopped.
    */ 
   public void processCompleted(TeXParser parser)
     throws IOException;
}

