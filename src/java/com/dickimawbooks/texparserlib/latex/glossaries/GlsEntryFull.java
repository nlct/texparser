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

public class GlsEntryFull extends AbstractGlsCommand
{
   public GlsEntryFull(GlossariesSty sty)
   {
      this("glsentryfull", CaseChange.NO_CHANGE, false, sty);
   }

   public GlsEntryFull(String name, boolean isPlural, GlossariesSty sty)
   {
      this(name, CaseChange.NO_CHANGE, isPlural, sty);
   }

   public GlsEntryFull(String name, CaseChange caseChange, GlossariesSty sty)
   {
      this(name, caseChange, false, sty);
   }

   public GlsEntryFull(String name, CaseChange caseChange, boolean isPlural, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
      this.isPlural = isPlural;
   }

   @Override
   public Object clone()
   {
      return new GlsEntryFull(getName(), getCaseChange(), isPlural(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);

      GlossaryEntry entry = glslabel.getEntry();

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObjectList substack = listener.createStack();

      if (entry == null)
      {
         sty.undefWarnOrError(stack, 
           GlossariesSty.ENTRY_NOT_DEFINED, glslabel.getLabel());
      }
      else
      {
         ControlSequence cs;

         switch (caseChange)
         {
            case SENTENCE:
               if (isPlural)
               {
                  if (sty.isExtra())
                  {
                     cs = listener.getControlSequence("Glsxtrinlinefullplformat");
                  }
                  else
                  {
                     cs = listener.getControlSequence("Genplacrfullformat");
                  }
               }
               else
               {
                  if (sty.isExtra())
                  {
                     cs = listener.getControlSequence("Glsxtrinlinefullformat");
                  }
                  else
                  {
                     cs = listener.getControlSequence("Genacrfullformat");
                  }
               }
            break;
            default:
               if (isPlural)
               {
                  if (sty.isExtra())
                  {
                     cs = listener.getControlSequence("glsxtrinlinefullplformat");
                  }
                  else
                  {
                     cs = listener.getControlSequence("genplacrfullformat");
                  }
               }
               else
               {
                  if (sty.isExtra())
                  {
                     cs = listener.getControlSequence("glsxtrinlinefullplformat");
                  }
                  else
                  {
                     cs = listener.getControlSequence("genacrfullformat");
                  }
               }
         }

         substack.add(cs);
         substack.add(glslabel);
         substack.add(listener.createGroup());
      }

      return substack;
   }

   public boolean isPlural()
   {
      return isPlural;
   }

   public CaseChange getCaseChange()
   {
      return caseChange;
   }

   protected boolean isPlural;
   protected CaseChange caseChange;
}
