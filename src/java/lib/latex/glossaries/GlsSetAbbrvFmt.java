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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsSetAbbrvFmt extends AbstractGlsCommand
{
   public GlsSetAbbrvFmt(GlossariesSty sty)
   {
      this("glssetabbrvfmt", sty);
   }

   public GlsSetAbbrvFmt(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new GlsSetAbbrvFmt(getName(), getSty());
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
      String catLabel = popLabelString(parser, stack);

      ControlSequence styleCs 
         = parser.getControlSequence("@glsabbrv@current@"+catLabel);

      if (styleCs == null)
      {
         styleCs = parser.getControlSequence("@glsabbrv@current@abbreviation");
      }

      if (styleCs != null)
      {
         String styleName = parser.expandToString(styleCs, stack);

         ControlSequence cs = parser.getControlSequence(
          "@glsabbrv@dispstyle@fmts@" + styleName);

         if (cs != null)
         {
            if (parser == stack || stack == null)
            {
               cs.process(parser);
            }
            else
            {
               cs.process(parser, stack);
            }
         }
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
