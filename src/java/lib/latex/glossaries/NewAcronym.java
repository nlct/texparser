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

public class NewAcronym extends NewGlossaryEntry
{
   public NewAcronym(GlossariesSty sty)
   {
      this("newacronym", Overwrite.FORBID, sty);
   }

   public NewAcronym(String name, GlossariesSty sty)
   {
      this(name, Overwrite.FORBID, sty);
   }

   public NewAcronym(String name, Overwrite overwrite, GlossariesSty sty)
   {
      this(name, overwrite, sty, sty.isExtra() && name.endsWith("abbreviation"));
   }

   public NewAcronym(String name, Overwrite overwrite, GlossariesSty sty, 
      boolean isAbbrv)
   {
      super(name, overwrite, sty);
      this.isAbbrv = isAbbrv;
   }

   public Object clone()
   {
      return new NewAcronym(getName(), overwrite, getSty(), isAbbrv);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject options = popOptArg(parser, stack);

      String label = popLabelString(parser, stack);

      KeyValList keyValList;

      if (options == null)
      {
         keyValList = new KeyValList();
      }
      else
      {
         keyValList = KeyValList.getList(parser, options);
      }

      if (sty.isExtra())
      {
         parser.putControlSequence(true, 
           new GenericCommand("ExtraCustomAbbreviationFields"));
      }

      TeXObject shortArg = popArg(parser, stack);
      TeXObject longArg = popArg(parser, stack);

      String catLabel = isAbbrv ? "abbreviation" : "acronym";

      if (sty.isExtra())
      {
         TeXObject catVal = keyValList.get("category");

         if (catVal == null)
         {
            keyValList.put("category", parser.getListener().createString("acronym"));
         }
         else
         {
            catLabel = parser.expandToString(catVal, stack);
         }

         parser.putControlSequence(true, 
           new TextualContentCommand("glscategorylabel", catLabel));
      }

      keyValList.put("short", shortArg);
      keyValList.put("long", longArg);

      TokenRegister reg = parser.getSettings().getTokenRegister("glslabeltok");
      reg.setContents(parser, parser.getListener().createString(label));

      reg = parser.getSettings().getTokenRegister("glsshorttok");
      reg.setContents(parser, (TeXObject)shortArg.clone());

      reg = parser.getSettings().getTokenRegister("glslongtok");
      reg.setContents(parser, (TeXObject)longArg.clone());

      if (sty.isExtra())
      {
         ControlSequence styleCs = parser.getControlSequence(
           "@glsabbrv@current@"+catLabel);

         if (styleCs == null)
         {
            styleCs = parser.getControlSequence(
             "@glsabbrv@current@abbreviation");
         }

         if (styleCs != null)
         {
            if (stack == parser || stack == null)
            {
               styleCs.process(parser);
            }
            else
            {
               styleCs.process(parser, stack);
            }
         }
      }

      if (sty.isExtra())
      {
         ControlSequence hookCs = parser.getListener().getControlSequence(
           "glsxtrnewabbrevpresetkeyhook");

         if (stack == parser || stack == null)
         {
            Group grp = parser.getListener().createGroup();
            grp.add((TeXObject)longArg.clone());
            parser.push(grp);

            grp = parser.getListener().createGroup();
            grp.add((TeXObject)shortArg.clone());
            parser.push(grp);

            parser.push(parser.getListener().createGroup(label));

            hookCs.process(parser);
         }
         else
         {
            Group grp = parser.getListener().createGroup();
            grp.add((TeXObject)longArg.clone());
            stack.push(grp);

            grp = parser.getListener().createGroup();
            grp.add((TeXObject)shortArg.clone());
            stack.push(grp);

            stack.push(parser.getListener().createGroup(label));

            hookCs.process(parser, stack);
         }
      }

      TeXObject shortPluralArg = keyValList.get("shortplural");

      if (shortPluralArg == null)
      {
         TeXObjectList shortPl = parser.getListener().createStack();

         shortPl.add((TeXObject)shortArg.clone(), true);

         if (sty.isExtra())
         {
            if (sty.isAttributeTrue(catLabel, "noshortplural"))
            {// no plural suffix
            }
            else if (sty.isAttributeTrue(catLabel, "aposplural"))
            {
               shortPl.add(parser.getListener().getOther(0x2019));
               shortPl.add(new TeXCsRef("abbrvpluralsuffix"));
            }
            else
            {
               shortPl.add(new TeXCsRef("abbrvpluralsuffix"));
            }
         }
         else
         {
            shortPl.add(new TeXCsRef("acrpluralsuffix"));
         }

         keyValList.put("shortplural", shortPl);

         shortPluralArg = shortPl;
      }

      if (sty.isExtra())
      {
         reg = parser.getSettings().getTokenRegister("glsshortpltok");
         reg.setContents(parser, (TeXObject)shortPluralArg.clone());
      }

      TeXObject longPluralArg = keyValList.get("longplural");

      if (longPluralArg == null)
      {
         TeXObjectList longPl = parser.getListener().createStack();

         longPl.add((TeXObject)longArg.clone(), true);
         longPl.add(new TeXCsRef("glspluralsuffix"));

         keyValList.put("longplural", longPl);

         longPluralArg = longPl;
      }

      if (sty.isExtra())
      {
         reg = parser.getSettings().getTokenRegister("glslongpltok");
         reg.setContents(parser, (TeXObject)longPluralArg.clone());
      }

      if (keyValList.get("type") == null)
      {
         if (isAbbrv)
         {
            keyValList.put("type", new TeXCsRef("glsxtrabbrvtype"));
         }
         else
         {
            keyValList.put("type", new TeXCsRef("acronymtype"));
         }
      }

      if (sty.isExtra())
      {
         ControlSequence hookCs = parser.getListener().getControlSequence(
           "newabbreviationhook");

         if (stack == parser || stack == null)
         {
            hookCs.process(parser);
         }
         else
         {
            hookCs.process(parser, stack);
         }
      }

      TeXObject nameArg = keyValList.get("name");

      if (nameArg == null)
      {
         keyValList.put("name", (TeXObject)shortArg.clone());
      }

      KeyValList extraFields;

      if (sty.isExtra())
      {
         TeXObject fieldList = parser.getListener().getControlSequence(
           "CustomAbbreviationFields");

         if (fieldList.canExpand() && fieldList instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack || stack == null)
            {
               expanded = ((Expandable)fieldList).expandonce(parser);
            }
            else
            {
               expanded = ((Expandable)fieldList).expandonce(parser, stack);
            }

            if (expanded != null)
            {
               fieldList = expanded;
            }
         }

         if (fieldList instanceof KeyValList)
         {
            extraFields = (KeyValList)fieldList;
         }
         else
         {
            extraFields = KeyValList.getList(parser, fieldList);
         }

         keyValList.putAll(extraFields);

         fieldList = parser.getListener().getControlSequence(
           "ExtraCustomAbbreviationFields");

         if (fieldList.canExpand() && fieldList instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack || stack == null)
            {
               expanded = ((Expandable)fieldList).expandonce(parser);
            }
            else
            {
               expanded = ((Expandable)fieldList).expandonce(parser, stack);
            }

            if (expanded != null)
            {
               fieldList = expanded;
            }
         }

         if (fieldList instanceof KeyValList)
         {
            extraFields = (KeyValList)fieldList;
         }
         else
         {
            extraFields = KeyValList.getList(parser, fieldList);
         }

         keyValList.putAll(extraFields);
      }
      else
      {
         TeXObject fieldList = parser.getListener().getControlSequence(
           "GenericAcronymFields");

         if (fieldList.canExpand() && fieldList instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack || stack == null)
            {
               expanded = ((Expandable)fieldList).expandonce(parser);
            }
            else
            {
               expanded = ((Expandable)fieldList).expandonce(parser, stack);
            }

            if (expanded != null)
            {
               fieldList = expanded;
            }
         }

         if (fieldList instanceof KeyValList)
         {
            extraFields = (KeyValList)fieldList;
         }
         else
         {
            extraFields = KeyValList.getList(parser, fieldList);
         }

         keyValList.putAll(extraFields);
      }

      defineEntry(label, keyValList, parser, stack);

      String type = parser.expandToString(keyValList.get("type"), stack);

      sty.declareAbbreviationGlossary(type);

      if (sty.isExtra())
      {
         ControlSequence hookCs = parser.getListener().getControlSequence(
           "GlsXtrPostNewAbbreviation");

         if (stack == parser || stack == null)
         {
            hookCs.process(parser);
         }
         else
         {
            hookCs.process(parser, stack);
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }


   protected boolean isAbbrv;
}
