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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.NewIf;
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.primitives.IfFalse;
import com.dickimawbooks.texparserlib.latex.KeyValList;

public class GlossaryEntry extends HashMap<String,TeXObject>
{
   public GlossaryEntry(GlossariesSty sty, 
     String label, KeyValList options)
   throws IOException
   {
      super();
      this.label = label;
      this.sty = sty;
      TeXParser parser = sty.getParser();

      TeXObject typeVal = null;

      for (Iterator<String> it = options.keySet().iterator();
           it.hasNext(); )
      {
         String key = it.next();

         if (key.equals("type"))
         {
            typeVal = options.getExpandedValue(key, parser, parser);
         }
         else if (sty.isFieldExpansionOn(key))
         {
            put(key, options.getExpandedValue(key, parser, parser));
         }
         else
         {
            put(key, options.getValue(key));
         }
      }

      if (typeVal == null)
      {
         typeVal = sty.getListener().getControlSequence("glsdefaulttype");

         if (typeVal instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)typeVal).expandfully(parser);

            if (expanded != null)
            {
               typeVal = expanded;
            }
         }
      }

      type = typeVal.toString(parser);

      NewIf.createConditional(false, parser, "ifglo@"+label+"@flag");
   }

   public String getType()
   {
      return type;
   }

   public String getLabel()
   {
      return label;
   }

   // mark as used.
   public void unset(boolean local)
   {
      TeXParser parser = sty.getParser();
      parser.putControlSequence(local, new IfTrue("ifglo@"+label+"@flag"));
   }

   // mark as unused.
   public void reset(boolean local)
   {
      TeXParser parser = sty.getParser();
      parser.putControlSequence(local, new IfFalse("ifglo@"+label+"@flag"));
   }

   // has this entry been marked as used?
   public boolean isUnset()
   {
      TeXParser parser = sty.getParser();

      ControlSequence cs = parser.getControlSequence("ifglo@"+label+"@flag");

      return (cs instanceof TeXBoolean && ((TeXBoolean)cs).booleanValue());
   }

   private String label;
   private String type;
   private GlossariesSty sty;
}
