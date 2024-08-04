/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class ExampleTagRef extends ControlSequence
{
   public ExampleTagRef(UserGuideSty sty)
   {
      this("exampletagref", sty);
   }

   public ExampleTagRef(String name, UserGuideSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new ExampleTagRef(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      String tag = popLabelString(parser, stack);
      TeXObject post = popArg(parser, stack);

      Vector<String> tagGrp = sty.getTagGroup(tag);

      if (tagGrp == null)
      {
         throw new TeXSyntaxException(parser,
            UserGuideSty.ERROR_UNKNOWN_TAG_GROUP, tag);
      }

      TeXObjectList content = listener.createStack();

      int n = tagGrp.size();

      if (n == 1)
      {
         content.add(listener.getControlSequence("examplenameref"));
         content.add(listener.createGroup(tagGrp.firstElement()));
         content.add(post, true);
      }
      else
      {
         NumericRegister reg = parser.getSettings().getNumericRegister(
          "l_nlctdoc_extag_item_threshold_int");

         int threshold = reg.number(parser);

         if (n < threshold)
         {
            content.add(listener.getControlSequence("examplesrefprefix"));

            content.add(listener.getControlSequence("@ref@numname"));
            content.add(listener.createGroup(tagGrp.firstElement()));

            for (int i = 2, m = n-1; i <= m; i++)
            {
               content.add(listener.getControlSequence("refslistsep"));

               content.add(listener.getControlSequence("@ref@numname"));
               content.add(listener.createGroup(tagGrp.get(i-1)));
            }

            content.add(listener.getControlSequence("refslistlastsep"));

            content.add(listener.getControlSequence("@ref@numname"));
            content.add(listener.createGroup(tagGrp.lastElement()));
            content.add(post, true);
         }
         else
         {
            content.add(listener.getControlSequence("exampletagrefprelist"));
            content.add(listener.getControlSequence("begin"));
            content.add(listener.createGroup("itemize"));

            for (int i = 1; i <= n; i++)
            {
               content.add(listener.getControlSequence("item"));
               content.add(listener.getControlSequence("ref"));
               content.add(listener.createGroup(tagGrp.get(i-1)));


               content.add(listener.getControlSequence("exampletagreflistpretitle"));
               content.add(listener.getControlSequence("nameref"));
               content.add(listener.createGroup(tagGrp.get(i-1)));

               if (i == n)
               {
                  content.add(post, true);
               }
               else
               {
                  content.add(listener.getControlSequence("exampletagreflistsep"));
               }
            }

            content.add(listener.getControlSequence("end"));
            content.add(listener.createGroup("itemize"));
         }
      }

      TeXParserUtils.process(content, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   UserGuideSty sty;
}
