/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLnumitemsinlist extends ControlSequence
{
   public DTLnumitemsinlist()
   {
      this("DTLnumitemsinlist");
   }

   public DTLnumitemsinlist(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DTLnumitemsinlist(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      CsvList csvList = CsvList.popCsvListFromStack(parser, stack, true);

      ControlSequence cmd = parser.popRequiredControlSequence(stack, true);

      String csName = cmd.getName();

      int n = 0;

      boolean skipEmpty = parser.isControlSequenceTrue("ifDTLlistskipempty");

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         if (!skipEmpty || !(obj instanceof TeXObjectList 
                && ((TeXObjectList)obj).size() == 0))
         {
            n++;
         }
      }

      parser.putControlSequence(true, new GenericCommand(true,
         csName, null, new UserNumber(n)));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
