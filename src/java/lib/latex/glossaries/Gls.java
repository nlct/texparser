/*
    Copyright (C) 2022 Nicola L.C. Talbot
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

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Gls extends AbstractGlsCommand
{
   public Gls(GlossariesSty sty)
   {
      this("gls", CaseChange.NO_CHANGE, false, sty);
   }

   public Gls(String name, CaseChange caseChange, GlossariesSty sty)
   {
      this("gls", caseChange, false, sty);
   }

   public Gls(String name, CaseChange caseChange, boolean isPlural, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
      this.isPlural = isPlural;
   }

   public Object clone()
   {
      return new Gls(getName(), getCaseChange(), isPlural(), getSty());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean localUnset = false;

      TeXObject options = popOptArg(parser, stack);

      KeyValList keyValList = null;

      if (options != null)
      {
         keyValList = KeyValList.getList(parser, options);
      }
      else
      {
         keyValList = new KeyValList();
      }

      String label = popLabelString(parser, stack);

      TeXObject insert = popOptArg(parser, stack);

      GlossaryEntry entry = getEntry(label);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (entry == null)
      {
         sty.undefWarnOrError(parser, stack, 
           GlossariesSty.ENTRY_NOT_DEFINED, label);
      }
      else
      {
         boolean isUnset = entry.isUnset();

         String type = entry.getType();

         ControlSequence glslabel = new GlsLabel(label, entry);

         listener.putControlSequence(true, glslabel);

         listener.putControlSequence(true, new GenericCommand("glscustomtext"));

         if (insert == null)
         {
            listener.putControlSequence(true, new GenericCommand("glsinsert"));
         }
         else
         {
            listener.putControlSequence(true, 
              new GenericCommand("glsinsert", null, insert));
         }

         if (isPlural)
         {
            listener.putControlSequence(true, new AtFirstOfTwo("glsifplural"));
         }
         else
         {
            listener.putControlSequence(true, new AtSecondOfTwo("glsifplural"));
         }

         switch (caseChange)
         {
            case SENTENCE:
              listener.putControlSequence(true, 
                new AtNumberOfNumber("glscapscase", 2, 3));
            break;
            case TO_UPPER:
              listener.putControlSequence(true, 
                new AtNumberOfNumber("glscapscase", 3, 3));
            break;
            default:
              listener.putControlSequence(true, 
                 new AtNumberOfNumber("glscapscase", 1, 3));
         }

         ControlSequence cs = parser.getControlSequence("gls@"+type+"@entryfmt");

         if (cs == null)
         {
            cs = listener.getControlSequence("glsentryfmt");
         }

         stack.push(cs);
         stack.push(glslabel);

         if (keyValList != null)
         {
            stack.push(keyValList);
         }

         if (stack == parser || stack == null)
         {
            listener.getControlSequence("@gls@link").process(parser);
         }
         else
         {
            listener.getControlSequence("@gls@link").process(parser, stack);
         }

         if (!isUnset)
         {
            entry.unset(localUnset);
         }
      }

      stack.push(listener.getControlSequence("glspostlinkhook"));
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public CaseChange getCaseChange()
   {
      return caseChange;
   }

   public boolean isPlural()
   {
      return isPlural;
   }

   protected CaseChange caseChange;
   protected boolean isPlural;
}
