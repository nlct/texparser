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
import com.dickimawbooks.texparserlib.primitives.RomanNumeral;
import com.dickimawbooks.texparserlib.latex.*;

public class AtGlsXtrAtCheckGroup extends AbstractGlsCommand
{
   public AtGlsXtrAtCheckGroup(GlossariesSty sty)
   {
      this("@glsxtr@checkgroup", sty);
   }

   public AtGlsXtrAtCheckGroup(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new AtGlsXtrAtCheckGroup(getName(), getSty());
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
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);
      TeXObject grpArg = null;
      String grpLabel = "";

      if (sty.isKnownField("group"))
      {
         String field = parser.expandToString(
           parser.getControlSequence("glsxtrgroupfield"), stack).trim();

         TeXObject val = glslabel.getEntry().get(field);

         if (val != null && !val.isEmpty())
         {
            grpLabel = parser.expandToString(val, stack);
            grpArg = parser.getListener().createGroup(grpLabel);
         }
      }
      else
      {
         TeXObject val = glslabel.getEntry().get("sort");

         if (val != null && !val.isEmpty())
         {
            grpLabel = parser.expandToString(val, stack);

            if (!grpLabel.isEmpty())
            {
               int cp = grpLabel.codePointAt(0);

               grpArg = new UserNumber(cp);

               grpLabel = "" + cp;
            }
         }
      }

      if (grpArg != null)
      {
         int level = TeXParserUtils.toInt(
           parser.getControlSequence("glscurrententrylevel"), parser, stack);

         String csname = "@gls@currentlettergroup";

         if (level > 0)
         {
            csname += RomanNumeral.romannumeral(level);
         }

         ControlSequence cs = parser.getControlSequence(csname);

         if (cs == null)
         {
            parser.putControlSequence(true, 
              new TextualContentCommand(csname, ""));
         }
         else
         {
            TeXObjectList body = parser.getListener().createStack();

            parser.putControlSequence(true, 
              new GenericCommand(true, "@glsxtr@groupheading", null, body));

            String prevLabel = parser.expandToString(cs, stack);

            if (!prevLabel.equals(grpLabel))
            {
               if (level > 0)
               {
                  body.add(new TeXCsRef("glssubgroupheading"));
                  body.add(new UserNumber(TeXParserUtils.toInt(
                   parser.getControlSequence("@gls@currentlettergroup@level"),
                   parser, stack)));
                  body.add(new UserNumber(level));
                  body.add(grpArg);
               }
               else
               {
                  if (!prevLabel.isEmpty())
                  {
                     body.add(new TeXCsRef("glsgroupskip"));
                  }

                  body.add(new TeXCsRef("glsgroupheading"));
                  body.add(grpArg);
               }

               parser.putControlSequence(true, new GenericCommand(true,
                 "@gls@currentlettergroup@level", null, new UserNumber(level)));

               parser.putControlSequence(true, 
                  new TextualContentCommand(csname, grpLabel));
            }
         }
      }
      else
      {
         parser.putControlSequence(true, 
           new TextualContentCommand("@glsxtr@groupheading", ""));
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
