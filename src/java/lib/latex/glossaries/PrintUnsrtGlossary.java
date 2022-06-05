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

public class PrintUnsrtGlossary extends ControlSequence
{
   public PrintUnsrtGlossary(GlossariesSty sty)
   {
      this("printunsrtglossary", sty);
   }

   public PrintUnsrtGlossary(String name, GlossariesSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new PrintUnsrtGlossary(getName(), sty);
   }

   protected void initHooks(TeXParser parser)
     throws IOException
   {
      parser.putControlSequence(true, 
        new InternalGetGroupTitle("@gls@getgrouptitle", sty.isKnownField("group")));

      parser.putControlSequence(true, 
        new TextualContentCommand("@gls@currentlettergroup", ""));

      parser.putControlSequence(true, 
        new GenericCommand(true, "glscurrententrylevel", null, 
          new UserNumber(-1)));

      parser.putControlSequence(true, 
        new TextualContentCommand("glscurrentrootentry", ""));

      parser.putControlSequence(true, 
        new TextualContentCommand("glscurrenttoplevelentry", ""));

   }

   protected void initProcess(GlsLabel glslabel, 
      TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      GlossaryEntry entry = glslabel.getEntry();

      TeXBoolean flatten = TeXParserUtils.toBoolean("ifglsxtrprintglossflatten",
         parser);

      int offset = TeXParserUtils.toInt(parser.getControlSequence(
        "@glsxtr@leveloffset"), parser, stack);

      int level;

      if (flatten.booleanValue())
      {
         level = offset;
      }
      else
      {
         level = entry.getLevel()+offset;
      }

      parser.putControlSequence(true, 
        new GenericCommand(true, "glscurrententrylevel", null, 
           new UserNumber(level)));

      if (level == 0)
      {
         parser.putControlSequence(true, 
           new TextualContentCommand("glscurrenttoplevelentry", entry.getLabel()));
      }

      if (flatten.booleanValue() || entry.getLevel() == 0)
      {
         parser.putControlSequence(true, 
           new TextualContentCommand("glscurrentrootentry", entry.getLabel()));
      }

      parser.putControlSequence(true, new AtFirstOfOne("glsxtr@process"));
      parser.putControlSequence(true, new PrintUnsrtGlossarySkipEntry());

      ControlSequence cs = parser.getListener().getControlSequence(
        "printunsrtglossaryentryprocesshook");

      if (!(cs instanceof AtGobble))
      {
         if (parser == stack || stack == null)
         {
            parser.push(glslabel);
            cs.process(parser);
         }
         else
         {
            stack.push(glslabel);
            cs.process(parser, stack);
         }
      }
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean isStar = (popModifier(parser, stack, '*') == '*');

      parser.startGroup();

      KeyValList options = sty.popOptKeyValList(parser, stack);

      if (isStar)
      {
         TeXObject initCode = popArg(parser, stack);

         if (parser == stack || stack == null)
         {
            initCode.process(parser);
         }
         else
         {
            initCode.process(parser, stack);
         }
      }

      Glossary glossary = sty.initPrintGloss(IndexingOption.UNSRT, options, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      list.add(listener.getControlSequence("glossarysection"));
      list.add(listener.getOther('['));
      list.add(listener.getControlSequence("glossarytoctitle"));
      list.add(listener.getOther(']'));
      list.add(listener.getControlSequence("glossarytitle"));
      list.add(listener.getControlSequence("glossarypreamble"));

      if (!glossary.isEmpty())
      {
         initHooks(parser);

         TeXObjectList body = listener.createStack();

         ControlSequence cs = new GenericCommand(true, "@glsxtr@doglossary",
           null, body);

         parser.putControlSequence(true, cs);

         body.add(new TeXCsRef("begin"));
         body.add(listener.createGroup("theglossary"));
         body.add(new TeXCsRef("glossaryheader"));
         //body.add(new TeXCsRef("glsresetentrylist"));

         for (String label : glossary)
         {
            GlsLabel glslabel = new GlsLabel("glscurrententrylabel",
              label, sty.getEntry(label));

            parser.putControlSequence(true, glslabel);

            initProcess(glslabel, parser, stack);

            cs = parser.getControlSequence("glsxtr@process");

            if (cs instanceof AtFirstOfOne)
            {
               TeXBoolean groups = TeXParserUtils.toBoolean(
                "ifglsxtr@printgloss@groups", parser);

               if (groups.booleanValue())
               {
                  parser.putControlSequence(true, new TextualContentCommand(
                    "@glsxtr@groupheading", ""));

                  TeXObjectList substack = listener.createStack();
                  substack.add(listener.getControlSequence("glsxtraddgroup"));
                  substack.add(glslabel);
                  Group grp = listener.createGroup();
                  substack.add(grp);
                  grp.add(listener.getControlSequence("@glsxtr@checkgroup"));
                  grp.add(glslabel);

                  if (parser == stack || stack == null)
                  {
                     substack.process(parser);
                  }
                  else
                  {
                     substack.process(parser, stack);
                  }

                  cs = listener.getControlSequence("@glsxtr@groupheading");

                  if (!cs.isEmpty())
                  {
                     body.add(TeXParserUtils.expandOnce(cs, parser, stack));
                  }
               }

               body.add(listener.getControlSequence("@printunsrt@glossary@handler"));
               body.add(listener.createGroup(label));
            }
         }

         body.add(new TeXCsRef("end"));
         body.add(listener.createGroup("theglossary"));

         list.add(new TeXCsRef("printunsrtglossarypredoglossary"));
         list.add(new TeXCsRef("@glsxtr@doglossary"));
      }

      list.add(new TeXCsRef("glossarypostamble"));

      if (parser == stack || stack == null)
      {
         list.process(parser);
      }
      else
      {
         list.process(parser, stack);
      }

      parser.endGroup();
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private GlossariesSty sty;
}
