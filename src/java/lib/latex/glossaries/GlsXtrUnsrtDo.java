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
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsXtrUnsrtDo extends AbstractGlsCommand
{
   public GlsXtrUnsrtDo(GlossariesSty sty)
   {
      this("glsxtrunsrtdo", sty);
   }

   public GlsXtrUnsrtDo(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new GlsXtrUnsrtDo(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);
      GlossaryEntry entry = glslabel.getEntry();
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      if (entry != null)
      {
         String locationField = "location";

         ControlSequence cs = parser.getControlSequence("GlsXtrLocationField");

         if (cs != null)
         {
            locationField = parser.expandToString(cs, stack);
         }

         TeXObject location = entry.get(locationField);

         if (location != null)
         {// not using loclist field

            DataObjectList noexpand = listener.createDataList(true);
            list.add(noexpand);

            noexpand.add(new TeXCsRef("gdef"));
            noexpand.add(new TeXCsRef("@gls@location"));
            Group grp = listener.createGroup();
            noexpand.add(grp);

            grp.add((TeXObject)location.clone());
         }

         NumericRegister levelReg = parser.getSettings().getNumericRegister("gls@level");
         int offset = TeXParserUtils.toInt(
            parser.getControlSequence("@glsxtr@leveloffset"), parser, stack);

         TeXBoolean flatten = TeXParserUtils.toBoolean("ifglsxtrprintglossflatten",
            parser);

         int level;

         if (flatten != null && flatten.booleanValue())
         {
            level = offset;
         }
         else
         {
            level = entry.getLevel()+offset;
         }

         DataObjectList noexpand = listener.createDataList(true);
         list.add(noexpand);

         noexpand.add(levelReg);
         noexpand.add(new UserNumber(level));

         boolean isChild = (level > 0);

         noexpand.add(listener.getControlSequence("let"));
         noexpand.add(new TeXCsRef("@glsxtr@ifischild"));

         if (isChild)
         {
            noexpand.add(listener.getControlSequence("@firstoftwo"));
         }
         else
         {
            noexpand.add(listener.getControlSequence("@secondoftwo"));
         }

         if (isChild)
         {
            list.add(listener.getControlSequence("subglossentry"));
            list.add(new UserNumber(level));
         }
         else
         {
            list.add(listener.getControlSequence("glossentry"));
         }

         list.add(glslabel);
         Group grp = listener.createGroup();
         list.add(grp);

         if (location != null)
         {
            grp.add(listener.getControlSequence("glossaryentrynumbers"));

            Group subgrp = listener.createGroup();
            grp.add(subgrp);
            subgrp.add(location);
         }
      }

      return list;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);
      GlossaryEntry entry = glslabel.getEntry();
      TeXParserListener listener = parser.getListener();

      if (entry != null)
      {
         String locationField = "location";

         ControlSequence cs = parser.getControlSequence("GlsXtrLocationField");

         if (cs != null)
         {
            locationField = parser.expandToString(cs, stack);
         }

         TeXObject location = entry.get(locationField);

         if (location != null)
         {// not using loclist field

            parser.putControlSequence(false,
              new GenericCommand(true, "@gls@location", null,
                  (TeXObject)location.clone()));
         }

         NumericRegister levelReg = parser.getSettings().getNumericRegister("gls@level");
         int offset = TeXParserUtils.toInt(
            parser.getControlSequence("@glsxtr@leveloffset"), parser, stack);

         TeXBoolean flatten = TeXParserUtils.toBoolean("ifglsxtrprintglossflatten",
            parser);

         int level;

         if (flatten != null && flatten.booleanValue())
         {
            level = offset;
         }
         else
         {
            level = entry.getLevel()+offset;
         }

         levelReg.setValue(parser, new UserNumber(level));

         boolean isChild = (level > 0);

         if (isChild)
         {
            parser.putControlSequence(true, new AtFirstOfTwo("@glsxtr@ifischild"));
         }
         else
         {
            parser.putControlSequence(true, new AtSecondOfTwo("@glsxtr@ifischild"));
         }

         TeXObjectList list = listener.createStack();

         if (isChild)
         {
            list.add(listener.getControlSequence("subglossentry"));
            list.add(new UserNumber(level));
         }
         else
         {
            list.add(listener.getControlSequence("glossentry"));
         }

         list.add(glslabel);
         Group grp = listener.createGroup();
         list.add(grp);

         if (location != null)
         {
            grp.add(listener.getControlSequence("glossaryentrynumbers"));
            Group subgrp = listener.createGroup();
            grp.add(subgrp);
            subgrp.add(location);
         }

         if (parser == stack || stack == null)
         {
            list.process(parser);
         }
         else
         {
            list.process(parser, stack);
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
