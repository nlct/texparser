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
import com.dickimawbooks.texparserlib.html.*;
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.primitives.IfFalse;

public class PrintUnsrtTable extends ControlSequence
{
   public PrintUnsrtTable(GlossariesSty sty)
   {
      this("printunsrttable", sty);
   }

   public PrintUnsrtTable(String name, GlossariesSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new PrintUnsrtTable(getName(), sty);
   }

   protected Vector<GlsLabel> getLabels(Glossary glossary,
     TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXSettings settings = parser.getSettings();
      TeXParserListener listener = parser.getListener();

      Vector<GlsLabel> labelList = new Vector<GlsLabel>();

      ControlSequence filterCs = listener.getControlSequence("glstableiffilter");

      NumericRegister reg = settings.getNumericRegister("@glsxtr@leveloffset");
      int levelOffset = reg.number(parser);

      for (String label : glossary)
      {
         GlossaryEntry entry = sty.getEntry(label);

         if (entry == null)
         {// shouldn't happen
            sty.undefWarnOrError(stack,
              GlossariesSty.ENTRY_NOT_DEFINED, label);

            continue;
         }

         int level = entry.getLevel() + levelOffset;

         if (level > 0)
         {
            continue;
         }

         GlsLabel glslabel = new GlsLabel("glscurrententrylabel",
          label, entry);

         if (!(filterCs instanceof AtNumberOfNumber))
         {// command has been redefined

            parser.putControlSequence(true, new AtFirstOfOne("glsxtr@process"));

            TeXObjectList list = listener.createStack();
            list.add(filterCs);
            list.add(glslabel);
            list.add(listener.getControlSequence("printunsrtglossaryskipentry"));
            list.add(listener.createGroup());

            TeXParserUtils.process(list, parser, stack);

            if (!(parser.getControlSequence("glsxtr@process") instanceof AtFirstOfOne))
            {
               continue;
            }
         }

         labelList.add(glslabel);
      }

      TeXParserUtils.process(
        listener.getControlSequence("printunsrtglossarypredoglossary"),
        parser, stack);

      return labelList;
   }

   protected void html(boolean showHeader, boolean showRules,
     boolean showGroups, int blocksperrow, int colsperblock,
     boolean showCaption, Glossary glossary,
     TeXObjectList content, TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      int totalCols = blocksperrow * colsperblock;

      L2HConverter listener = (L2HConverter)parser.getListener();

      parser.putControlSequence(true, new AtNumberOfNumber(
       "glstable@parcase", 1, 3));

      String blockAlignStr = parser.expandToString(
        listener.getControlSequence("glstable@blockalignsep"), stack);

      boolean hasBlockSep = blockAlignStr.equals("|");

      String nameAlignStr = parser.expandToString(
        listener.getControlSequence("glstablenamecolalign"), stack);

      String descAlignStr = parser.expandToString(
        listener.getControlSequence("glstabledesccolalign"), stack);

      String symbolAlignStr = parser.expandToString(
        listener.getControlSequence("glstablesymbolcolalign"), stack);

      String nameClass = "glossary-name";

      if (nameAlignStr.equals("c"))
      {
         nameClass += " cell-noborder-bothsep-c";
      }
      else if (nameAlignStr.equals("l"))
      {
         nameClass += " cell-noborder-bothsep-l";
      }
      else if (nameAlignStr.equals("r"))
      {
         nameClass += " cell-noborder-bothsep-r";
      }

      String otherClass = "glossary-other";
      String descClass = "glossary-desc";

      if (descAlignStr.equals("c"))
      {
         descClass += " cell-noborder-bothsep-c";
         otherClass += " cell-noborder-bothsep-c";
      }
      else if (descAlignStr.equals("l"))
      {
         descClass += " cell-noborder-bothsep-l";
         otherClass += " cell-noborder-bothsep-l";
      }
      else if (descAlignStr.equals("r"))
      {
         descClass += " cell-noborder-bothsep-r";
         otherClass += " cell-noborder-bothsep-r";
      }

      String symbolClass = "glossary-symbol";

      if (symbolAlignStr.equals("c"))
      {
         symbolClass += " cell-noborder-bothsep-c";
      }
      else if (symbolAlignStr.equals("l"))
      {
         symbolClass += " cell-noborder-bothsep-l";
      }
      else if (symbolAlignStr.equals("r"))
      {
         symbolClass += " cell-noborder-bothsep-r";
      }

      Vector<GlsLabel> labelList = getLabels(glossary, parser, stack);

      StartElement startElem = new StartElement("table", true);

      if (showRules)
      {
         startElem.putAttribute("class", "glossary-ruled");
      }
      else
      {
         startElem.putAttribute("class", "glossary");
      }

      content.add(startElem);

      if (showCaption)
      {
         listener.stepcounter("table");

         StartElement captionElem = new StartElement("caption", true);

         TeXObject labelObj = TeXParserUtils.expandOnce(
           listener.getControlSequence("@@glossaryseclabel"), parser, stack);

         if (!labelObj.isEmpty() && parser.isStack(labelObj))
         {
            TeXObjectList list = (TeXObjectList)labelObj;

            TeXObject obj = list.peekStack();

            if (obj instanceof ControlSequence 
                && ((ControlSequence)obj).getName().equals("label"))
            {
               obj = list.popStack(parser);
               String label = TeXParserUtils.popLabelString(parser, list);

               if (!label.isEmpty())
               {
                  captionElem.putAttribute("id", label);
               }
            }
         }

         content.add(captionElem);

         content.add(listener.getControlSequence("@makecaption"));
         Group grp = listener.createGroup();
         content.add(grp);

         grp.add(listener.getControlSequence("tablename"));
         grp.add(listener.getControlSequence("nobreakspace"));
         grp.add(listener.getControlSequence("thetable"));

         content.add(listener.getControlSequence("glossarytitle"));

         content.add(new EndElement("caption", true));
      }

      TeXObject preamble = TeXParserUtils.expandOnce(
       listener.getControlSequence("glossarypreamble"), parser, stack);

      if (!preamble.isEmpty())
      {
         content.add(new StartElement("tr"));

         startElem = new StartElement("td");

         startElem.putAttribute("colspan", ""+totalCols);
         startElem.putAttribute("class", "glossary-preamble");

         content.add(startElem);
         content.add(preamble);
         content.add(new EndElement("td"));
         content.add(new EndElement("tr"));
      }

      if (showHeader)
      {
         startElem = new StartElement("tr");

         if (showRules)
         {
            startElem.putAttribute("class", "glossary-ruled");
         }

         content.add(startElem);
      }

      TeXObject headerObj = TeXParserUtils.expandOnce(
       listener.getControlSequence("glstableblockheader"), parser, stack);

      TeXObjectList blockHeader = listener.createStack();

      String[] colClasses = new String[colsperblock];
      int columnIndex = 0;

      if (headerObj instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)headerObj;

         startElem = null;
         EndElement endElem = null;

         while (!list.isEmpty())
         {
            TeXObject obj = list.popStack(parser);

            if (obj instanceof Tab)
            {
               if (startElem == null)
               {
                  blockHeader.add(new StartElement("th"));
               }

               endElem = new EndElement("th");
               blockHeader.add(endElem);
               startElem = null;

               columnIndex++;
            }
            else if (obj instanceof ControlSequence)
            {
               String name = ((ControlSequence)obj).getName();

               if (name.equals("glstableHeaderFmt"))
               {
                  startElem = new StartElement("th", true);
                  blockHeader.add(startElem);
                  endElem = null;
               }
               else
               {
                  if (startElem == null)
                  {
                     startElem = new StartElement("th", true);
                     blockHeader.add(startElem);
                     endElem = null;
                  }

                  if (name.equals("glstablenameheader"))
                  {
                     startElem.putAttribute("class", nameClass);
                     colClasses[columnIndex] = nameClass;
                  }
                  else if (name.equals("glstabledescheader"))
                  {
                     startElem.putAttribute("class", descClass);
                     colClasses[columnIndex] = descClass;
                  }
                  else if (name.equals("glstablesymbolheader"))
                  {
                     startElem.putAttribute("class", symbolClass);
                     colClasses[columnIndex] = symbolClass;
                  }
                  else if (name.equals("glstableotherheader"))
                  {
                     startElem.putAttribute("class", otherClass);
                     colClasses[columnIndex] = otherClass;
                  }

                  blockHeader.add(obj);
               }
            }
            else
            {
               blockHeader.add(obj);
            }
         }

         if (startElem != null && endElem == null)
         {
            blockHeader.add(new EndElement("th"));
         }
      }
      else
      {
         blockHeader.add(new StartElement("th"));
         blockHeader.add(headerObj);
         blockHeader.add(new EndElement("th"));
      }

      for (int i = 0; i < blocksperrow; i++)
      {
         if (i == 0)
         {
            if (showHeader)
            {
               content.addAll(blockHeader);
            }
         }
         else
         {
            TeXObjectList list = (TeXObjectList)blockHeader.clone();

            if (hasBlockSep && !list.isEmpty())
            {
               TeXObject obj = list.firstElement();

               if (obj instanceof StartElement)
               {
                  String className = ((StartElement)obj).getAttribute("class");

                  if (className == null)
                  {
                     className = "cell-left-border";
                  }
                  else
                  {
                     className += " cell-left-border";
                  }

                  ((StartElement)obj).putAttribute("class", className);
               }
            }

            if (showHeader)
            {
               content.addAll(list);
            }
         }
      }

      if (showHeader)
      {
         content.add(new EndElement("tr", true));
      }

      TeXParserUtils.process(content, parser, stack);

      int currentBlock = 0;
      StartElement startRowElem = null;
      EndElement endRowElem = null;

      ControlSequence entryCs = listener.getControlSequence("glstableblockentry");

      TeXObjectList blockList = listener.createStack();

      blockList = listener.createStack();
      TeXObjectList list = listener.createStack();
      list.add(new TeXCsRef("glscurrententrylabel"));

      blockList.add(TeXParserUtils.expandOnce(entryCs, parser, list), true);

      for (GlsLabel glslabel : labelList)
      {
         parser.putControlSequence(true, glslabel);

         if (currentBlock == 0)
         {
            startRowElem = new StartElement("tr", true);
            content.add(startRowElem);
            endRowElem = null;
         }

         startElem = null;
         EndElement endElem = null;

         columnIndex = 0;

         for (TeXObject obj : blockList)
         {
            if (obj instanceof Tab)
            {
               if (startElem != null)
               {
                  endElem = new EndElement("td");
                  content.add(endElem);

                  columnIndex++;
               }

               startElem = new StartElement("td", true);
               startElem.putAttribute("class", colClasses[columnIndex]);

               content.add(startElem);
               endElem = null;
            }
            else
            {
               if (startElem == null)
               {
                  startElem = new StartElement("td", true);

                  String className = colClasses[columnIndex];

                  if (hasBlockSep && columnIndex == 0 && currentBlock > 0)
                  {
                     className += " cell-left-border";
                  }

                  startElem.putAttribute("class", className);

                  content.add(startElem);
                  endElem = null;
               }

               content.add((TeXObject)obj.clone());
            }
         }

         currentBlock++;

         if (currentBlock >= blocksperrow)
         {
            currentBlock = 0;
            endRowElem = new EndElement("tr");
            content.add(endRowElem);
            startRowElem = null;
         }

         TeXParserUtils.process(content, parser, stack);
      }

      if (startRowElem != null && endRowElem == null)
      {
         for (int i = currentBlock; i < blocksperrow; i++)
         {
            startElem = new StartElement("td");
            startElem.putAttribute("colspan", ""+colsperblock);

            if (hasBlockSep && currentBlock > 0)
            {
               startElem.putAttribute("class", "cell-left-border");
            }

            content.add(startElem);
            content.add(new EndElement("td"));
         }

         content.add(new EndElement("tr"));
      }

      TeXObject postamble = TeXParserUtils.expandOnce(
       listener.getControlSequence("glossarypostamble"), parser, stack);

      if (!postamble.isEmpty())
      {
         content.add(new StartElement("tr"));

         startElem = new StartElement("td");

         startElem.putAttribute("colspan", ""+totalCols);
         startElem.putAttribute("class", "glossary-postamble");

         content.add(startElem);
         content.add(postamble);
         content.add(new EndElement("td"));
         content.add(new EndElement("tr"));
      }

      content.add(new EndElement("table", true));
   }

   protected void latex(boolean showHeader, boolean showRules,
     boolean showGroups,int blocksperrow, int colsperblock,
     boolean showCaption, Glossary glossary, 
     TeXObjectList content, TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXSettings settings = parser.getSettings();

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      Group alignSpec = listener.createGroup();

      TeXObject blockSpec = parser.expandfully(
        listener.getControlSequence("glstableblockalign"), stack);

      TeXObjectList blockSpecList;

      if (parser.isStack(blockSpec))
      {
         blockSpecList = (TeXObjectList)blockSpec;
      }
      else
      {
         blockSpecList = listener.createStack();
         blockSpecList.add(blockSpec);
      }

      for (int i = 0; i < blocksperrow; i++)
      {
         alignSpec.addAll((TeXObjectList)blockSpecList.clone());
      }

      content.add(listener.getControlSequence("begin"));
      content.add(listener.createGroup("longtable"));
      content.add(alignSpec);

      content.add(listener.getControlSequence("caption"));
      content.add(listener.getOther('['));
      content.add(listener.getControlSequence("glossarytoctitle"));
      content.add(listener.getOther(']'));
      content.add(listener.getControlSequence("glossarytitle"));

      ControlSequence labelCs = listener.getControlSequence("@@glossaryseclabel");

      content.add(TeXParserUtils.expandOnce(labelCs, parser, stack), true);

      if (showRules)
      {
         content.add(new TeXCsRef("toprule"));
      }

      content.add(new TeXCsRef("tabularnewline"));
      content.add(new TeXCsRef("endfirsthead"));

      content.add(listener.getControlSequence("caption"));
      content.add(listener.getOther('['));
      content.add(listener.getOther(']'));
      content.add(listener.getControlSequence("glossarytoctitle"));

      if (showRules)
      {
         content.add(new TeXCsRef("toprule"));
      }

      content.add(new TeXCsRef("tabularnewline"));
      content.add(new TeXCsRef("endhead"));

      NumericRegister reg = settings.getNumericRegister("glstablecurrentblockindex");
      reg.setValue(parser, UserNumber.ONE);

      Vector<GlsLabel> labelList = getLabels(glossary, parser, stack);

// TODO

      content.add(listener.getControlSequence("end"));
      content.add(listener.createGroup("longtable"));
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      KeyValList options = TeXParserUtils.popOptKeyValList(parser, stack);

      parser.startGroup();

      TeXParserListener listener = parser.getListener();
      TeXSettings settings = parser.getSettings();

      TeXObjectList content = listener.createStack();

      if (options != null)
      {
         options.remove("style");
      }

      Glossary glossary = sty.initPrintGloss(IndexingOption.UNSRT, options, 
        stack);

      Boolean showHeaderBool = null;
      boolean showHeader = true;

      Boolean showRulesBool = null;
      boolean showRules = true;

      Boolean showGroupsBool = null;
      boolean showGroups = true;

      Boolean showCaptionBool = null;
      boolean showCaption = true;

      NumericRegister reg = settings.getNumericRegister("glstableblockperrowcount");
      int blocksperrow = reg.number(parser);

      TeXObject initCode = parser.getControlSequence("glstable@init");

      if (options != null)
      {
         Numerical num = options.getNumerical("blocks", parser, stack);

         if (num != null)
         {
            blocksperrow = num.number(parser);
            reg.setValue(parser, num);
         }

         showHeaderBool = options.getBoolean("header", parser, stack);
         showRulesBool = options.getBoolean("rules", parser, stack);
         showGroupsBool = options.getBoolean("groups", parser, stack);
         showCaptionBool = options.getBoolean("caption", parser, stack);

         TeXObject val = options.getValue("blocksep");

         if (val != null)
         {
            parser.putControlSequence(true, 
             new GenericCommand(true, "glstable@blockalignsep", null, val));
         }

         val = options.getExpandedValue("par", parser, stack);

         if (val != null)
         {
            String strVal = val.toString(parser);

            if (strVal.equals("false"))
            {
               parser.putControlSequence(true, 
                new AtNumberOfNumber("glstable@parcase", 1, 3));
            }
            else if (strVal.equals("justified"))
            {
               parser.putControlSequence(true, 
                new AtNumberOfNumber("glstable@parcase", 2, 3));
            }
            else
            {
               parser.putControlSequence(true, 
                new AtNumberOfNumber("glstable@parcase", 3, 3));
            }
         }

         val = options.getExpandedValue("other", parser, stack);

         if (val != null)
         {
            parser.putControlSequence(true, 
              new TextualContentCommand("glstableotherfield", val.toString(parser)));
         }

         val = options.getExpandedValue("block-style", parser, stack);

         if (val != null)
         {
            content.add(listener.getControlSequence("glstablesetstyle"));
            content.add(TeXParserUtils.createGroup(listener, val));
         }

         val = options.getValue("init");

         if (val != null)
         {
            TeXObjectList def;

            if (parser.isStack(val))
            {
               def = (TeXObjectList)val;
            }
            else
            {
               def = TeXParserUtils.createStack(listener, val);
            }

            initCode = new GenericCommand(true, "glstable@init", null, def);
         }
      }

      if (showCaptionBool == null)
      {
         showCaption = TeXParserUtils.isTrue("ifKV@printglosstable@caption", parser);
      }
      else
      {
         showCaption = showCaptionBool.booleanValue();
      }

      if (TeXParserUtils.isTrue("if@glsxtr@floats", parser))
      {
         if (showCaption)
         {
            parser.putControlSequence(true, 
             new TextualContentCommand("glscounter", "table"));
         }
      }

      if (!TeXParserUtils.isVoid(initCode, parser))
      {
         TeXParserUtils.process(initCode, parser, stack);
      }

      if (!content.isEmpty())
      {
         TeXParserUtils.process(content, parser, stack);
      }

      if (showHeaderBool == null)
      {
         showHeader = TeXParserUtils.isTrue("ifKV@printglosstable@header", parser);
      }
      else
      {
         showHeader = showHeaderBool.booleanValue();

         if (showHeader)
         {
            parser.putControlSequence(true, 
              new IfTrue("ifKV@printglosstable@header"));
         }
         else
         {
            parser.putControlSequence(true, 
              new IfFalse("ifKV@printglosstable@header"));
         }
      }

      if (showRulesBool == null)
      {
         showRules = TeXParserUtils.isTrue("ifKV@printglosstable@rules", parser);
      }
      else
      {
         showRules = showRulesBool.booleanValue();

         if (showRules)
         {
            parser.putControlSequence(true, 
              new IfTrue("ifKV@printglosstable@rules"));
         }
         else
         {
            parser.putControlSequence(true, 
              new IfFalse("ifKV@printglosstable@rules"));
         }
      }

      if (showGroupsBool == null)
      {
         showGroups = TeXParserUtils.isTrue("ifglsxtr@printgloss@groups", parser);
      }
      else
      {
         showGroups = showGroupsBool.booleanValue();
      }

      if (showGroups)
      {
         parser.putControlSequence(true, 
          new LaTeXGenericCommand(true, "glstable@groupheading", "m",
            TeXParserUtils.createStack(listener,
              listener.getControlSequence("glstablegroupheading"),
              TeXParserUtils.createGroup(listener, listener.getParam(1)))));
      }
      else
      {
         parser.putControlSequence(true, 
          new AtGobble("glstable@groupheading"));
      }

      reg = settings.getNumericRegister("glstablecolsperblock");
      int colsperblock = reg.number(parser);

      reg = settings.getNumericRegister("glstabletotalcols");
      reg.setValue(parser, new UserNumber(colsperblock * blocksperrow));

      if (listener instanceof L2HConverter)
      {
         html(showHeader, showRules, showGroups, blocksperrow, colsperblock,
           showCaption, glossary, content, parser, stack);
      }
      else
      {
         latex(showHeader, showRules, showGroups, 
           blocksperrow, colsperblock, showCaption, glossary, content, parser, stack);
      }

      TeXParserUtils.process(content, parser, stack);

      parser.endGroup();
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private GlossariesSty sty;
}
