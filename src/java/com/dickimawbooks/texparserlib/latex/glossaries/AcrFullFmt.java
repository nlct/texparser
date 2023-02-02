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

public class AcrFullFmt extends AbstractGlsCommand
{
   public AcrFullFmt(GlossariesSty sty)
   {
      this("acrfullfmt", CaseChange.NO_CHANGE, false, sty);
   }

   public AcrFullFmt(String name, boolean isPlural, GlossariesSty sty)
   {
      this(name, CaseChange.NO_CHANGE, isPlural, sty);
   }

   public AcrFullFmt(String name, CaseChange caseChange, GlossariesSty sty)
   {
      this(name, caseChange, false, sty);
   }

   public AcrFullFmt(String name, CaseChange caseChange, boolean isPlural, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
      this.isPlural = isPlural;
   }

   @Override
   public Object clone()
   {
      return new AcrFullFmt(getName(), getCaseChange(), isPlural(), getSty());
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      KeyValList keyValList = popKeyValList(parser, stack);

      if (keyValList == null)
      {
         keyValList = new KeyValList();
      }

      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObject insert = popArg(parser, stack);

      GlossaryEntry entry = glslabel.getEntry();

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (entry == null)
      {
         sty.undefWarnOrError(stack, 
           GlossariesSty.ENTRY_NOT_DEFINED, glslabel.getLabel());
      }
      else
      {
         Group grp = listener.createGroup();
         TeXObject linkText = grp;

         switch (caseChange)
         {
            case SENTENCE:
               if (isPlural)
               {
                  grp.add(listener.getControlSequence("Genplacrfullformat"));
               }
               else
               {
                  grp.add(listener.getControlSequence("Genacrfullformat"));
               }
            break;
            case TO_UPPER:
               grp.add(listener.getControlSequence("mfirstucMakeUppercase"));
               Group subgrp = listener.createGroup();
               grp.add(subgrp);
               grp = subgrp;

               // fall through
            default:
               if (isPlural)
               {
                  grp.add(listener.getControlSequence("genplacrfullformat"));
               }
               else
               {
                  grp.add(listener.getControlSequence("genacrfullformat"));
               }
         }

         grp.add(glslabel);
         grp.add(insert);

         TeXObjectList substack = listener.createStack();
         substack.add(listener.getControlSequence("glslink"));
         substack.add(keyValList);
         substack.add(glslabel);
         substack.add(linkText);

         TeXParserUtils.process(substack, parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
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
