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

public class GlsPostDescription extends AbstractGlsCommand
{
   public GlsPostDescription(GlossariesSty sty)
   {
      this("glspostdescription", sty);
   }

   public GlsPostDescription(TeXObject punc, GlossariesSty sty)
   {
      this("glspostdescription", punc, sty);
   }

   public GlsPostDescription(String name, GlossariesSty sty)
   {
      this(name, null, sty);
   }

   public GlsPostDescription(String name, TeXObject punc, GlossariesSty sty)
   {
      super(name, sty);
      this.punc = punc;
   }

   public Object clone()
   {
      return new GlsPostDescription(getName(), 
        punc == null ? null : (TeXObject)punc.clone(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      if (punc == null)
      {
         list.add(listener.getControlSequence("ifglsnopostdot"));

         list.add(listener.getControlSequence("else"));

         list.add(listener.getOther('.'));
         list.add(listener.getSpace());

         list.add(listener.getControlSequence("fi"));
      }
      else
      {
         list.add((TeXObject)punc.clone());
      }

      if (sty.isExtra())
      {
         list.add(listener.getControlSequence("glsxtrpostdescription"));
      }

      return list;
   }

   private TeXObject punc = null;
}
