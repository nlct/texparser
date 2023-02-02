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

public class GlsXtrInternalLocationHyperlink extends ControlSequence
{
   public GlsXtrInternalLocationHyperlink()
   {
      this("GlsXtrInternalLocationHyperlink");
   }

   public GlsXtrInternalLocationHyperlink(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsXtrInternalLocationHyperlink(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = parser.getControlSequence("glsentrycounter");

      TeXParserListener listener = parser.getListener();

      TeXObjectList substack = listener.createStack();

      boolean isWrGloss = false;

      if (cs != null)
      {
         String currCounter = parser.expandToString(cs, stack);

         if (currCounter.equals("wrglossary"))
         {
            isWrGloss = true;
         }
      }

      if (isWrGloss)
      {
         substack.add(listener.getControlSequence("@glsxtr@wrglossary@locationhyperlink"));
         
      }
      else
      {
         TeXObject arg1 = popArg(parser, stack);
         TeXObject arg2 = popArg(parser, stack);
         TeXObject arg3 = popArg(parser, stack);

         substack.add(listener.getControlSequence("glsxtrhyperlink"));

         Group grp = listener.createGroup();
         substack.add(grp);

         grp.add(arg1, true);
         grp.add(arg2, true);
         grp.add((TeXObject)arg3.clone(), true);

         grp = listener.createGroup();
         substack.add(grp);

         grp.add((TeXObject)arg3.clone(), true);
      }

      if (parser == stack || stack == null)
      {
         substack.process(parser);
      }
      else
      {
         substack.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
