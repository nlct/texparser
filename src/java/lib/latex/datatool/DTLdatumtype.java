/*
    Copyright (C) 2024 Nicola L.C. Talbot
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

public class DTLdatumtype extends Command
{
   public DTLdatumtype(DataToolBaseSty sty)
   {
      this("DTLdatumtype", sty);
   }

   public DTLdatumtype(String name, DataToolBaseSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DTLdatumtype(getName(), sty);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList content = parser.getListener().createStack();

      ControlSequence cs = TeXParserUtils.popResolvedControlSequence(parser, stack);

      if (cs instanceof DatumCommand)
      {
         DatumCommand datumCs = (DatumCommand)cs;

         DatumType type = datumCs.getType();

         content.add(type.getCs(parser.getListener()));
      }
      else if (sty.isNull(cs))
      {
         content.add(DatumType.UNKNOWN.getCs(parser.getListener()));
      }
      else if (cs instanceof ControlSequence)
      {
         TeXObject obj = TeXParserUtils.expandOnce(cs, parser, stack);

         if (parser.isStack(obj))
         {
            content.add(new TeXCsRef("datatool_datum_type:Nnnnn"));
            content.add(obj, true);
         }
         else
         {
            content.add(DatumType.UNKNOWN.getCs(parser.getListener()));
         }
      }

      return content;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   DataToolBaseSty sty;
}
