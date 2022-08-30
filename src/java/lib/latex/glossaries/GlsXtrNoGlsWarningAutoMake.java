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

public class GlsXtrNoGlsWarningAutoMake extends Command
{
   public GlsXtrNoGlsWarningAutoMake()
   {
      this("GlsXtrNoGlsWarningAutoMake");
   }

   public GlsXtrNoGlsWarningAutoMake(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsXtrNoGlsWarningAutoMake(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createString(
       "You may need to rerun ");

      expanded.add(listener.getControlSequence("LaTeX"));

      expanded.addAll(listener.createString(". If you already have, it may be that "));

      expanded.add(listener.getControlSequence("TeX"));

      expanded.addAll(listener.createString("'s shell escape doesn't allow you to run "));

      expanded.add(listener.getControlSequence("ifglsxindy"));

      expanded.addAll(listener.createString("xindy"));

      expanded.add(listener.getControlSequence("else"));

      expanded.addAll(listener.createString("makeindex"));

      expanded.add(listener.getControlSequence("fi"));

      expanded.addAll(listener.createString(". Check the transcript file "));

      expanded.add(listener.getControlSequence("jobname"));

      expanded.addAll(listener.createString(".log. If the shell escape is disabled, try one of the following:"));

      expanded.add(listener.getControlSequence("begin"));
      expanded.add(listener.createGroup("itemize"));

      expanded.add(listener.getControlSequence("item"));

      expanded.addAll(listener.createString("Run the external (Lua) application:"));

      expanded.add(listener.getPar());

      expanded.addAll(listener.createString("makeglossaries-lite \""));
      expanded.add(listener.getControlSequence("jobname"));
      expanded.add(listener.getOther('"'));

      expanded.add(listener.getControlSequence("item"));

      expanded.addAll(listener.createString("Run the external (Perl) application:"));

      expanded.add(listener.getPar());

      expanded.addAll(listener.createString("makeglossaries \""));
      expanded.add(listener.getControlSequence("jobname"));
      expanded.add(listener.getOther('"'));

      expanded.add(listener.getControlSequence("end"));
      expanded.add(listener.createGroup("itemize"));

      expanded.addAll(listener.createString("Then rerun "));

      expanded.add(listener.getControlSequence("LaTeX"));

      expanded.addAll(listener.createString(" on this document."));

      return expanded;
   }

}
