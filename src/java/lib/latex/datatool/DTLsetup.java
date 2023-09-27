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
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLsetup extends ControlSequence
{
   public DTLsetup(DataToolSty sty)
   {
      this("DTLsetup", sty);
   }

   public DTLsetup(String name, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DTLsetup(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      KeyValList options = TeXParserUtils.popKeyValList(parser, stack);

      for (Iterator<String> it = options.keySet().iterator();
           it.hasNext(); )
      {
         String key = it.next();

         TeXObject val = options.get(key);

         if (key.equals("default-name"))
         {
            parser.putControlSequence(true, 
              new TextualContentCommand("l__datatool_default_dbname_str",
                 parser.expandToString(val, stack)));
         }
         else if (key.equals("global"))
         {
            boolean boolVal = true;

            if (val != null)
            {
               boolVal = Boolean.parseBoolean(parser.expandToString(val, stack));
            }

            parser.putControlSequence(true, 
              new LaTeX3Boolean("l__datatool_db_global_bool", boolVal));
         }
         else if (key.equals("store-datum"))
         {
            boolean boolVal = true;

            if (val != null)
            {
               boolVal = Boolean.parseBoolean(parser.expandToString(val, stack));
            }

            parser.putControlSequence(true, 
              new LaTeX3Boolean("l__datatool_db_store_datum_bool", boolVal));
         }
         else if (key.equals("new-value-trim"))
         {
            boolean boolVal = true;

            if (val != null)
            {
               boolVal = Boolean.parseBoolean(parser.expandToString(val, stack));
            }

            parser.putControlSequence(true, 
              new LaTeX3Boolean("l__datatool_new_element_trim_bool", boolVal));
         }
         else if (key.equals("new-value-expand"))
         {
            boolean boolVal = true;

            if (val != null)
            {
               boolVal = Boolean.parseBoolean(parser.expandToString(val, stack));
            }

            ControlSequence cs;

            if (boolVal)
            {
               cs = parser.getListener().getControlSequence("dtlexpandnewvalue");
            }
            else
            {
               cs = parser.getListener().getControlSequence("dtlnoexpandnewvalue");
            }

            TeXParserUtils.process(cs, parser, stack);
         }
         else if (key.equals("delimiter"))
         {
            String str = parser.expandToString(val, stack);
            sty.setDelimiter(str.codePointAt(0));
         }
         else if (key.equals("separator"))
         {
            String str = parser.expandToString(val, stack);
            sty.setSeparator(str.codePointAt(0));
         }
         else if (key.equals("io"))
         {
            sty.processIOKeys(val, stack);
         }
         else if (key.equals("action"))
         {
            sty.processActionKeys(val, stack);
         }
         else if (key.equals("display"))
         {
            sty.processDisplayKeys(val, stack);
         }
         else
         {
            throw new LaTeXSyntaxException(parser, DataToolSty.ERROR_UNKNOWN_KEY,
             key, "datatool");
         }
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataToolSty sty;
}
