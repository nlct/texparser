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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.GatherEnvContents;
import com.dickimawbooks.texparserlib.latex.End;

public class CodeResult extends GatherEnvContents
{
   public CodeResult()
   {
      this("coderesult");
   }

   public CodeResult(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new CodeResult(getName());
   }

   public TeXObjectList popContents(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject object = stack.pop();

      TeXObjectList codeBox = parser.getListener().createStack();
      TeXObjectList resultBox = null;

      TeXObjectList contents = codeBox;

      while (object != null)
      {
         String csname = null;

         if (object instanceof ControlSequence)
         {
            csname = ((TeXCsRef)object).getName();

            if (object instanceof TeXCsRef && !csname.equals("tcblower"))
            {
               object = parser.getListener().getControlSequence(csname);
            }
         }

         if (object instanceof End)
         {
            String envName = popLabelString(parser, stack);

            Group grp = parser.getListener().createGroup(envName);

            if (envName.equals(getName()))
            {
               stack.push(grp);
               stack.push(object);
               break;
            }

            contents.add(object);
            contents.add(grp);
         }
         else if ("tcblower".equals(csname))
         {
            resultBox = parser.getListener().createStack();
            contents = resultBox;
         }
         else
         {
            contents.add(object);
         }

         object = stack.pop();
      }

      contents = parser.getListener().createStack();

      contents.add(parser.getListener().getControlSequence("begin"));
      contents.add(parser.getListener().createGroup("codebox"));

      contents.addAll(codeBox);

      contents.add(parser.getListener().getControlSequence("end"));
      contents.add(parser.getListener().createGroup("codebox"));

      if (resultBox != null)
      {
         contents.add(parser.getListener().getControlSequence("begin"));
         contents.add(parser.getListener().createGroup("resultbox"));

         contents.addAll(resultBox);

         contents.add(parser.getListener().getControlSequence("end"));
         contents.add(parser.getListener().createGroup("resultbox"));
      }

      return contents;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObjectList contents = popContents(parser, stack);

      contents.process(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      TeXObjectList contents = popContents(parser, parser);

      contents.process(parser);
   }
   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
   }
}
