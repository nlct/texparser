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
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;
import com.dickimawbooks.texparserlib.html.L2HConverter;

public class UserGuideSty extends LaTeXSty
{
   public UserGuideSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "nlctuserguide", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      if (listener instanceof L2HConverter)
      {
         ((L2HConverter)listener).addCssStyle("dfn { font-style: normal; font-weight: bold; } a.icon { text-decoration: none; }");
      }

      registerControlSequence(new GuideGls());

      addSemanticCommand("strong", TeXFontWeight.STRONG);
      addSemanticCommand("code", TeXFontFamily.VERB);
      addSemanticCommand("cmd", listener.getOther('\\'));
      addSemanticCommand("comment", FG_COMMENT, listener.createString("% "));
      addSemanticCommand("csfmt", TeXFontFamily.VERB, 
        new Color(0.41f, 0.545f, 0.41f), 
        listener.getOther('\\'), null);
      addSemanticCommand("csfmtfont", TeXFontFamily.TT);
      addSemanticCommand("csfmtcolourfont", TeXFontFamily.TT, FG_CS);
      addSemanticCommand("appfmt", TeXFontFamily.TT);
      addSemanticCommand("styfmt", TeXFontFamily.TT);
      addSemanticCommand("clsfmt", TeXFontFamily.TT);
      addSemanticCommand("envfmt", TeXFontFamily.TT);
      addSemanticCommand("optfmt", TeXFontFamily.TT);
      addSemanticCommand("csoptfmt", TeXFontFamily.TT, FG_CSOPT);
      addSemanticCommand("styoptfmt", TeXFontFamily.TT,FG_STYOPT);
      addSemanticCommand("clsoptfmt", TeXFontFamily.TT);
      addSemanticCommand("ctrfmt", TeXFontFamily.TT);
      addSemanticCommand("filefmt", TeXFontFamily.TT);
      addSemanticCommand("extfmt", TeXFontFamily.TT);
      addSemanticCommand("deprecatedorbannedfmt", FG_DEPRECATED_OR_BANNED);

      registerControlSequence(new GenericCommand(true,
         "thispackagename", null, new TeXCsRef("jobname")));

      registerControlSequence(new GenericCommand(true, "thispackage", null, 
          new TeXObject[]{new TeXCsRef("styfmt"), new TeXCsRef("thispackagename")}));

      registerControlSequence(new GenericCommand(true, "examplesdir", null, 
         new TeXObject[] {new TeXCsRef("jobname"), 
            listener.createString("-examples")}));

      registerControlSequence(new GenericCommand(true, "mainfmt", null,
         new TeXCsRef("glsnumberformat")));

      registerControlSequence(new TextualContentCommand("dhyphen", "-"));
      registerControlSequence(new TextualContentCommand("dcolon", ":"));
      registerControlSequence(new TextualContentCommand("dcomma", ","));
      registerControlSequence(new TextualContentCommand("dequals", "="));
      registerControlSequence(new TextualContentCommand("dfullstop", "."));
      registerControlSequence(new TextualContentCommand("longswitch", "--"));
      registerControlSequence(new TextualContentCommand("shortswitch", "-"));

      addSemanticCommand("longargfmt", TeXFontFamily.TT,
        null, new TeXCsRef("longswitch"), null);

      addSemanticCommand("shortargfmt", TeXFontFamily.TT,
        null, new TeXCsRef("shortswitch"), null);

      addSemanticCommand("qt", (TeXFontText)null,
        null, listener.getOther(0x201C), listener.getOther(0x201D));

      addNestedSemanticCommand("qtt", new TeXFontText(TeXFontFamily.VERB),
        null, null, listener.getOther(0x201C), listener.getOther(0x201D));

      addNestedSemanticCommand("meta", new TeXFontText(TeXFontShape.EM),
        new TeXFontText(TeXFontFamily.RM),
        Color.BLACK, listener.getOther(0x2329), listener.getOther(0x232A));

      addSemanticCommand("summarytagfmt", "summarytag", 
        new TeXFontText(TeXFontShape.IT),
        null, null, null, null, listener.createString(": "), true, false);

      // \marg
      TeXObjectList def = listener.createStack();
      def.add(listener.getOther('{'));
      def.add(listener.getParam(1));
      def.add(listener.getOther('}'));
      registerControlSequence(new LaTeXGenericCommand(true, "marg",
        "m", def));

      // \oarg
      def = listener.createStack();
      def.add(listener.getOther('['));
      def.add(listener.getParam(1));
      def.add(listener.getOther(']'));
      registerControlSequence(new LaTeXGenericCommand(true, "oarg",
        "m", def));

      // \margm
      def = listener.createStack();
      def.add(new TeXCsRef("marg"));
      Group grp = listener.createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("meta"));
      Group subgrp = listener.createGroup();
      grp.add(subgrp);
      subgrp.add(listener.getParam(1));
      registerControlSequence(new LaTeXGenericCommand(true, "margm",
        "m", def));

      // \oargm
      def = listener.createStack();
      def.add(new TeXCsRef("oarg"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("meta"));
      subgrp = listener.createGroup();
      grp.add(subgrp);
      subgrp.add(listener.getParam(1));
      registerControlSequence(new LaTeXGenericCommand(true, "oargm",
        "m", def));

      addTaggedColourBox("important", null, Color.RED);
      addTaggedColourBox("warning", null, Color.RED);
      addTaggedColourBox("information", null, Color.BLUE);
      TaggedColourBox pinnedBox = addTaggedColourBox("pinnedbox",
         "definition", BG_DEF, Color.BLACK);
      TaggedColourBox terminalBox = 
        addTaggedColourBox("terminal", new TeXFontText(TeXFontFamily.VERB), 
           null, Color.BLACK);

      FrameBox defnBox = addColourBox("defnbox", null, null,
        BG_DEF, Color.BLACK);
      FrameBox optionSummaryBox = addColourBox("optionsummarybox", null, null,
        null, null);
      FrameBox optionValueSummaryBox = addSemanticCommand("optionvaluesummarybox",
         new UserDimension(40, FixedUnit.BP));

      FrameBox rightBox = addFloatBox("floatrightbox");

      FrameBox noteBox = new ColourBox("noteBox", BorderStyle.NONE,
        AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, false, null, null);
      getListener().declareFrameBox(noteBox, false);

      registerControlSequence(new CmdDef(pinnedBox, rightBox, noteBox, glossariesSty));

      TaggedColourBox settingsBox = addTaggedColourBox("settingsbox",
         "valuesetting", BG_OPTION_DEF, Color.BLACK);

      registerControlSequence(new OptionDef(settingsBox, rightBox, noteBox, glossariesSty));

      TaggedColourBox optValBox = addTaggedColourBox("optionvaluebox",
         "optionvalue", BG_OPTION_VALUE_DEF, Color.BLACK);

      registerControlSequence(new OptionValDef(optValBox, rightBox, noteBox, glossariesSty));

      registerControlSequence(new AppDef(terminalBox, rightBox, noteBox, glossariesSty));

      registerControlSequence(new SummaryBox(defnBox, 
        rightBox, noteBox, glossariesSty));
      registerControlSequence(new SummaryCommandBox(defnBox, 
        rightBox, noteBox, glossariesSty));
      registerControlSequence(new SummaryCommandOptionBox(optionSummaryBox, 
        rightBox, noteBox, glossariesSty));
      registerControlSequence(new SummaryOptionValueBox(optionValueSummaryBox, 
        rightBox, noteBox, glossariesSty));

      createIndexItemBox(0);
      createIndexItemBox(1);
      createIndexItemBox(2);

      addColourBox("nlctusernavbox", null, null, null, null);

      addGlsFmtTextCommand("stytext", "pkg.");
      addGlsFmtTextCommand("clstext", "cls.");
      addGlsFmtTextCommand("opttext", "opt.");
      addGlsFmtTextCommand("envtext", "env.");
      addGlsFmtTextCommand("ctrtext", "ctr.");
      addGlsFmtTextCommand("actext", "dual.");
      addGlsFmtTextCommand("exttext", "ext.");
      addGlsFmtTextCommand("apptext", "app.");
      addGlsFmtTextCommand("switchtext", "switch.");

      registerControlSequence(glossariesSty.createGls("sty", "pkg."));
      registerControlSequence(glossariesSty.createGls("cls", "cls."));
      registerControlSequence(glossariesSty.createGls("opt", "opt."));
      registerControlSequence(glossariesSty.createGls("env", "env."));
      registerControlSequence(glossariesSty.createGls("ctr", "ctr."));
      registerControlSequence(glossariesSty.createGls("ac", "dual."));
      registerControlSequence(glossariesSty.createGls("ext", "ext."));
      registerControlSequence(glossariesSty.createGls("app", "app."));
      registerControlSequence(glossariesSty.createGls("switch", "switch."));
      registerControlSequence(glossariesSty.createGls("cmdmod", "idx.mod."));
      registerControlSequence(glossariesSty.createGls("file", "file."));

      registerControlSequence(new GenericCommand(true, 
       "printterms", null, new TeXObject[]{ new TeXCsRef("printabbrs"),
         new TeXCsRef("printicons"), new TeXCsRef("printmain")
       }));

      registerControlSequence(new PrintAbbrs(glossariesSty));
      registerControlSequence(new PrintIcons(glossariesSty));
      registerControlSequence(new PrintMain(glossariesSty));
      registerControlSequence(new PrintSummary(glossariesSty));
      registerControlSequence(new PrintCommandOptions(glossariesSty));
      registerControlSequence(new PrintIndex(glossariesSty));

      registerControlSequence(new Dgls("idx", CaseChange.NO_CHANGE, glossariesSty));
      registerControlSequence(new Dgls("idxpl", 
       CaseChange.NO_CHANGE, true, glossariesSty));
      registerControlSequence(new Dgls("Idx", CaseChange.SENTENCE, glossariesSty));
      registerControlSequence(new Dgls("Idxpl", CaseChange.SENTENCE, true, glossariesSty));
      registerControlSequence(new Dglslink("idxc", false, glossariesSty));

      // dual prefix list
      def = getListener().createString("dual.,idx.,");
        def.add(getListener().getControlSequence("empty"));
      registerControlSequence(new GenericCommand(true, "@glsxtr@labelprefixes",
       null, def));

      // \optval
      def = getListener().createStack();
      def.add(new TeXCsRef("gls"));

      grp = getListener().createGroup("opt.");
      def.add(grp);
      grp.add(getListener().getParam(1));

      def.add(new TeXCsRef("optfmt"));
      grp = getListener().createGroup("=");
      def.add(grp);
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "optval", "mm", def));

      // \optvalm
      def = getListener().createStack();
      def.add(new TeXCsRef("gls"));

      grp = getListener().createGroup("opt.");
      def.add(grp);
      grp.add(getListener().getParam(1));

      def.add(new TeXCsRef("optfmt"));
      grp = getListener().createGroup("=");
      def.add(grp);

      grp.add(new TeXCsRef("marg"));

      subgrp = getListener().createGroup();
      grp.add(subgrp);
      subgrp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "optvalm", "mm", def));

      // \opteqvalref
      def = getListener().createStack();
      def.add(new TeXCsRef("gls"));

      grp = getListener().createGroup("opt.");
      def.add(grp);
      grp.add(getListener().getParam(1));

      def.add(new TeXCsRef("optfmt"));
      grp = getListener().createGroup("=");
      def.add(grp);

      def.add(new TeXCsRef("gls"));

      grp = getListener().createGroup("optval.");
      def.add(grp);
      grp.add(getListener().getParam(1));
      grp.add(getListener().getOther('.'));
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "opteqvalref", "mm", def));

      // \optvalref
      def = getListener().createStack();
      def.add(new TeXCsRef("gls"));

      grp = getListener().createGroup("optval.");
      def.add(grp);
      grp.add(getListener().getParam(1));
      grp.add(getListener().getOther('.'));
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "optvalref", "mm", def));

      // \fmtorcode
      def = getListener().createStack();
      def.add(getListener().getParam(1));

      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "fmtorcode", "mm", def));

      // \metaboolean
      def = getListener().createStack();
      def.add(new TeXCsRef("meta"));
      def.add(getListener().createGroup("boolean"));

      registerControlSequence(new LaTeXGenericCommand(true,
       "metaboolean", "m", def));

      // \keyval
      def = getListener().createStack();
      def.add(new TeXCsRef("meta"));
      def.add(getListener().createGroup("key"));
      def.add(getListener().getOther('='));
      def.add(new TeXCsRef("meta"));
      def.add(getListener().createGroup("value"));

      registerControlSequence(new LaTeXGenericCommand(true,
       "keyval", "m", def));

      // \metafilefmt
      def = getListener().createStack();
      def.add(new TeXCsRef("filefmt"));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(1));

      def.add(new TeXCsRef("meta"));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      def.add(new TeXCsRef("filefmt"));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(3));

      registerControlSequence(new LaTeXGenericCommand(true,
       "metafilefmt", "mmm", def));

      // \metametafilefmt
      def = getListener().createStack();
      def.add(new TeXCsRef("filefmt"));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(1));

      def.add(new TeXCsRef("meta"));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      def.add(new TeXCsRef("filefmt"));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(3));

      def.add(new TeXCsRef("meta"));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(4));

      def.add(new TeXCsRef("filefmt"));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(5));

      registerControlSequence(new LaTeXGenericCommand(true,
       "metametafilefmt", "mmmmm", def));

      // \texdocref
      def = getListener().createStack();
      def.add(new TeXCsRef("begin"));
      def.add(getListener().createGroup("terminal"));
      def.add(new TeXCsRef("href"));
      def.add(getListener().createGroup("https://www.tug.org/texdoc/"));
      def.add(getListener().createGroup("texdoc"));
      def.add(getListener().getSpace());
      def.add(getListener().getParam(1));
      def.add(new TeXCsRef("end"));
      def.add(getListener().createGroup("terminal"));

      registerControlSequence(new LaTeXGenericCommand(true,
       "texdocref", "m", def));

      // \CTANpkg
      def = getListener().createStack();
      def.add(new TeXCsRef("href"));
      grp = getListener().createGroup("https://ctan.org/pkg/");
      def.add(grp);
      grp.add(getListener().getParam(1));
      def.add(getListener().createGroup("CTAN"));

      registerControlSequence(new LaTeXGenericCommand(true,
       "CTANpkg", "m", def));

      // \ctanref
      def = getListener().createStack();
      def.add(new TeXCsRef("href"));
      grp = getListener().createGroup("https://ctan.org/");
      def.add(grp);
      grp.add(getListener().getParam(1));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "ctanref", "mm", def));

      // \ctanmirrornofn and \ctanmirror
      // (Treat identically.)
      def = getListener().createStack();
      def.add(new TeXCsRef("href"));
      grp = getListener().createGroup("http://mirrors.ctan.org/");
      def.add(grp);
      grp.add(getListener().getParam(1));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "ctanmirrornofn", "mm", def));
      registerControlSequence(new LaTeXGenericCommand(true,
       "ctanmirror", "mm", def));

      // \ctanpkgmirror
      def = getListener().createStack();
      def.add(new TeXCsRef("href"));
      grp = getListener().createGroup("http://mirrors.ctan.org/pkg/");
      def.add(grp);
      grp.add(getListener().getParam(1));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "ctanpkgmirror", "mm", def));

      // \ctansupportmirror
      def = getListener().createStack();
      def.add(new TeXCsRef("href"));
      grp = getListener().createGroup("http://mirrors.ctan.org/support/");
      def.add(grp);
      grp.add(getListener().getParam(1));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "ctansupportmirror", "mm", def));

      // \dickimawhrefnofn and \dickimawhref
      // (Treat identically.)
      def = getListener().createStack();
      def.add(new TeXCsRef("href"));
      grp = getListener().createGroup("https://www.dickimaw-books.com/");
      def.add(grp);
      grp.add(getListener().getParam(1));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "dickimawhrefnofn", "mm", def));

      registerControlSequence(new LaTeXGenericCommand(true,
       "dickimawhref", "mm", def));

      // \blog
      def = getListener().createStack();
      def.add(new TeXCsRef("dickimawhref"));
      grp = getListener().createGroup("blog/");
      def.add(grp);
      grp.add(getListener().getParam(1));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "blog", "mm", def));

      // \gallery
      def = getListener().createStack();
      def.add(new TeXCsRef("dickimawhref"));
      grp = getListener().createGroup("gallery");
      def.add(grp);
      grp.add(getListener().getParam(1));

      registerControlSequence(new LaTeXGenericCommand(true,
       "gallery", "m", def));

      // \gallerypage
      def = getListener().createStack();
      def.add(new TeXCsRef("dickimawhref"));
      grp = getListener().createGroup("gallery/index.php?label=");
      def.add(grp);
      grp.add(getListener().getParam(1));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "gallerypage", "mm", def));

      // \faqpage
      def = getListener().createStack();
      def.add(new TeXCsRef("dickimawhref"));
      grp = getListener().createGroup("faq.php?category=");
      def.add(grp);
      grp.add(getListener().getParam(1));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "faqpage", "mm", def));

      // \faqitem
      def = getListener().createStack();
      def.add(new TeXCsRef("dickimawhref"));
      grp = getListener().createGroup("faq.php?itemlabel=");
      def.add(grp);
      grp.add(getListener().getParam(1));
      grp = getListener().createGroup();
      def.add(grp);
      grp.add(getListener().getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "faqitem", "mm", def));

      // \sectionref
      registerControlSequence(new Ref("sectionref", new TeXCsRef("S")));

      // \tableref
      registerControlSequence(new TextualContentCommand("Tablename", "Table"));
      registerControlSequence(new Ref("tableref", false,
       new TeXCsRef("Tablename"), listener.getSpace()));

      // \badcodesym
      def = getListener().createStack();
      def.add(getListener().getControlSequence("texparser@overlapped"));
      def.add(new TeXCsRef("faFileTextO"));
      def.add(getListener().getControlSequence("texparser@overlapper"));
      grp = getListener().createGroup();
      def.add(grp);

      grp.add(new TeXCsRef("deprecatedorbannedfmt"));
      grp.add(new TeXCsRef("faBan"));

      registerControlSequence(new GenericCommand(true,
       "badcodesym", null, def));

      registerControlSequence(new TextualContentCommand(
         "glsxtrpostdescdualindexabbreviation", "."));
   }

   protected void addGlsFmtTextCommand(String name, String prefix)
   {
      TeXObjectList syntax = getListener().createStack();
      syntax.add(getListener().getParam(1));

      TeXObjectList def = getListener().createStack();
      def.add(new TeXCsRef("glsfmttext"));

      Group grp = getListener().createGroup(prefix);
      def.add(grp);

      grp.add(getListener().getParam(1));

      registerControlSequence(new GenericCommand(true, name, syntax,
         def));
   }

   @Override
   protected void preOptions(TeXObjectList stack) throws IOException
   {
      getListener().requirepackage(null, "fontawesome", false, stack);
      getListener().requirepackage(null, "hyperref", false, stack);

      KeyValList options = new KeyValList();

      options.put("record", getListener().createString("nameref"));
      options.put("indexcounter", null);
      options.put("index", null);
      options.put("symbols", null);
      options.put("nosuper", null);
      options.put("stylemods", getListener().createString("mcols,bookindex,topic,longextra"));

      glossariesSty = (GlossariesSty)getListener().requirepackage(options, "glossaries-extra", false, stack);

      glossariesSty.addField("modifiers");
      glossariesSty.addField("syntax");
      glossariesSty.addField("defaultvalue");
      glossariesSty.addField("initvalue");
      glossariesSty.addField("status", getListener().createString("default"));
      glossariesSty.addField("note");
      glossariesSty.addField("providedby");
      glossariesSty.addField("pdftitlecasename");
      glossariesSty.addField("defaultkeys");

      addAccessFields();
   }

   @Override
   public void processOption(String option, TeXObject value)
    throws IOException
   {
      glossariesSty.processOption(option, value);
   }

   @Override
   protected void postOptions(TeXObjectList stack) throws IOException
   {
      super.postOptions(stack);

      TeXObjectList list = getListener().createStack();
      list.add(getListener().getControlSequence("setabbreviationstyle"));
      list.add(getListener().getOther('['));
      list.add(getListener().createString("termabbreviation"), true);
      list.add(getListener().getOther(']'));
      list.add(getListener().createGroup("long-short-desc"));

      list.add(getListener().getControlSequence("setabbreviationstyle"));
      list.add(getListener().getOther('['));
      list.add(getListener().createString("termacronym"), true);
      list.add(getListener().getOther(']'));
      list.add(getListener().createGroup("short-nolong-desc"));

      if (stack == null || stack == getParser())
      {
         getParser().add(list);
      }
      else
      {
         list.process(getParser(), stack);
      }
   }

   protected void addAccessFields()
   {
      TeXObjectList defVal = new TeXObjectList();

      defVal.add(new TeXCsRef("glsentryname"));
      defVal.add(new TeXCsRef("glslabel"));
      glossariesSty.addField("symbolaccess", defVal);
      glossariesSty.setFieldExpansionOn("symbolaccess", true);
      glossariesSty.setIconField("symbol");

      defVal = new TeXObjectList();

      defVal.add(new TeXCsRef("glsentrylong"));
      defVal.add(new TeXCsRef("glslabel"));
      glossariesSty.addField("shortaccess", defVal);
      glossariesSty.setFieldExpansionOn("shortaccess", true);
   }

   protected void addSemanticCommand(String name, TeXFontFamily family)
   {
      addSemanticCommand(name, family, null);
   }

   protected void addSemanticCommand(String name, TeXFontFamily family, Color fg)
   {
      TeXFontText font = new TeXFontText();
      font.setFamily(family);
      addSemanticCommand(name, font, fg);
   }

   protected void addSemanticCommand(String name, TeXFontFamily family, Color fg,
     TeXObject prefix, TeXObject suffix)
   {
      TeXFontText font = new TeXFontText();
      font.setFamily(family);
      addSemanticCommand(name, font, fg, prefix, suffix);
   }

   protected void addSemanticCommand(String name, TeXFontWeight weight)
   {
      addSemanticCommand(name, weight, null);
   }

   protected void addSemanticCommand(String name, TeXFontWeight weight, Color fg)
   {
      TeXFontText font = new TeXFontText();
      font.setWeight(weight);
      addSemanticCommand(name, font, fg);
   }

   protected void addSemanticCommand(String name, TeXFontText font, Color fg)
   {
      addSemanticCommand(name, font, fg, null, null);
   }

   protected void addSemanticCommand(String name, Color fg)
   {
      addSemanticCommand(name, (TeXFontText)null, fg, null, null);
   }

   protected FrameBox addSemanticCommand(String name, 
    TeXFontText font, Color fg, TeXObject prefix, TeXObject suffix)
   {
      return addSemanticCommand(name, name, font, fg, prefix, suffix);
   }

   protected FrameBox addSemanticCommand(String name, String id, 
    TeXFontText font, Color fg, TeXObject prefix, TeXObject suffix)
   {
      return addSemanticCommand(name, id, font, fg, null, null, prefix, suffix, true, false);
   }

   protected FrameBox addSemanticCommand(String name, Color fg, TeXObject prefix)
   {
      return addSemanticCommand(name, name, null, fg, null, null, 
        prefix, null, true, false);
   }

   protected FrameBox addSemanticCommand(String name, TeXObject prefix)
   {
      return addSemanticCommand(name, name, null, null, null, null, 
        prefix, null, true, false);
   }

   protected FrameBox addSemanticCommand(String name, String id, 
    TeXFontText font, Color fg, Color bg, Color frameCol, 
      TeXObject prefix, TeXObject suffix,
      boolean isInLine, boolean isMultiLine)
   {
      return addSemanticCommand(name, id, font, fg, bg, frameCol, 
      prefix, suffix, isInLine, isMultiLine, null);
   }

   protected FrameBox addSemanticCommand(String name, TeXDimension leftOuterMargin)
   {
      return addSemanticCommand(name, name, (TeXFontText)null, 
      (Color)null, (Color)null, (Color)null, 
      (TeXObject)null, (TeXObject)null, false, true, leftOuterMargin);
   }

   protected FrameBox addSemanticCommand(String name, String id, 
    TeXFontText font, Color fg, Color bg, Color frameCol, 
      TeXObject prefix, TeXObject suffix,
      boolean isInLine, boolean isMultiLine, TeXDimension leftOuterMargin)
   {
      FrameBox boxFrame = new ColourBox(name, 
       frameCol == null ? BorderStyle.NONE : BorderStyle.SOLID,
       AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, isInLine, null, null);

      boxFrame.setIsMultiLine(isMultiLine);
      boxFrame.setTextFont(font);
      boxFrame.setForegroundColor(fg);
      boxFrame.setBackgroundColor(bg);

      if (frameCol != null)
      {
         boxFrame.setBorderColor(frameCol);
         boxFrame.setBorderWidth(new UserDimension(1, FixedUnit.BP));
         boxFrame.setInnerMargin(new UserDimension(2, FixedUnit.BP));
      }

      if (leftOuterMargin != null)
      {
         boxFrame.setOuterMarginLeft(leftOuterMargin);
      }

      boxFrame.setPrefix(prefix);
      boxFrame.setSuffix(suffix);
      boxFrame.setId(id);

      getListener().declareFrameBox(boxFrame, false);

      return boxFrame;
   }

   protected FrameBox addFloatBox(String id)
   {
      return addFloatBox("user@"+id, id, FloatBoxStyle.RIGHT);
   }

   protected FrameBox addFloatBox(String name, String id, FloatBoxStyle style)
   {
      FrameBox box = new ColourBox(name, BorderStyle.NONE,
       AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, true, null, null);

      box.setFloatStyle(style);
      box.setId(id);

      getListener().declareFrameBox(box, false);

      return box;
   }

   protected FrameBox addColourBox(String name,  
    TeXFontText font, Color fg, Color bg, Color frameCol)
   {
      return addSemanticCommand(name, name, font, fg, bg, frameCol, 
        null, null, false, true);
   }

   protected void addNestedSemanticCommand(String name, TeXFontText innerFont,
    TeXFontText outerFont, Color fg,
    TeXObject prefix, TeXObject suffix)
   {
      String innerId;

      if (outerFont == null && fg == null)
      {
         innerId = name;
      }
      else
      {
         innerId = name+"inner";
      }

      FrameBox innerBox = addSemanticCommand("inner@"+name, innerId, innerFont, null,
       null, null);

      TeXObjectList list = getListener().createStack();

      if (outerFont == null && fg == null)
      {
         if (prefix != null)
         {
            list.add(prefix);
         }

         list.add(innerBox);
         Group grp = listener.createGroup();
         grp.add(listener.getParam(1));
         list.add(grp);

         if (suffix != null)
         {
            list.add(suffix);
         }
      }
      else
      {
         FrameBox outerBox = addSemanticCommand("outer@"+name, name, outerFont, 
            fg, prefix, suffix);

         list.add(outerBox);
         Group grp = listener.createGroup();
         list.add(grp);

         grp.add(innerBox);

         Group subgrp = listener.createGroup();
         grp.add(subgrp);

         subgrp.add(listener.getParam(1));
      }

      registerControlSequence(new LaTeXGenericCommand(true,
         name, "m", list));
   }

   protected TaggedColourBox addTaggedColourBox(String tag, Color bg, Color frameCol)
   {
      return addTaggedColourBox(tag, tag, bg, frameCol);
   }

   protected TaggedColourBox addTaggedColourBox(String name, String tag, Color bg, Color frameCol)
   {
      return addTaggedColourBox(name, tag, null, bg, frameCol);
   }

   protected TaggedColourBox addTaggedColourBox(String name, TeXFontText font,
      Color bg, Color frameCol)
   {
      return addTaggedColourBox(name, name, font, bg, frameCol);
   }

   protected TaggedColourBox addTaggedColourBox(String name, String tag, TeXFontText font,
      Color bg, Color frameCol)
   {
      TeXObjectList list = listener.createStack();
      list.add(new TeXCsRef("glssymbol"));
      list.add(listener.createGroup("sym."+tag));

      return addTaggedColourBox(name, false, font, null, bg, frameCol, list);
   }

   protected TaggedColourBox addTaggedColourBox(String name, 
      Color fg, Color bg, Color frameCol, TeXObject tag)
   {
      return addTaggedColourBox(name, false, null, fg, bg, frameCol, tag);
   }

   protected TaggedColourBox addTaggedColourBox(String name, TeXFontText font, 
      Color fg, Color bg, Color frameCol, TeXObject tag)
   {
      return addTaggedColourBox(name, false, font, fg, bg, frameCol, tag);
   }

   protected TaggedColourBox addTaggedColourBox(String name, boolean isInLine, TeXFontText font, 
      Color fg, Color bg, Color borderCol, TeXObject tag)
   {
      FrameBox boxFrame = new ColourBox("frame@"+name, BorderStyle.SOLID,
       AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, isInLine,
        true,// is multiline 
        new UserDimension(2, TeXUnit.BP), // border width
        new UserDimension(2, TeXUnit.BP));// inner margin

      boxFrame.setId(name);
      boxFrame.setTextFont(font);
      boxFrame.setForegroundColor(fg);
      boxFrame.setBackgroundColor(bg);
      boxFrame.setBorderColor(borderCol);

      FrameBox titleFrame = new ColourBox("frame@"+name+"title", BorderStyle.NONE,
       AlignHStyle.RIGHT, AlignVStyle.DEFAULT, false, true, null, null);

      titleFrame.setForegroundColor(borderCol);
      titleFrame.setId(name+"title");

      getListener().declareFrameBox(titleFrame, false);
      getListener().declareFrameBox(boxFrame, false);

      TaggedColourBox taggedBox = new TaggedColourBox(boxFrame, titleFrame, tag);
      registerControlSequence(taggedBox);

      return taggedBox;
   }

   protected void createIndexItemBox(int level)
   { 
      FrameBox idxBox = new FrameBox("nlctuserguideidx"+level,
       BorderStyle.NONE, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, false);

      idxBox.setOuterMarginLeft(new UserDimension(level*20, FixedUnit.BP));

      getListener().declareFrameBox(idxBox, false);
   }

   protected GlossariesSty glossariesSty;

   public static final Color BG_DEF = new Color(1.0f, 1.0f, 0.75f);
   public static final Color BG_OPTION_DEF = new Color(1.0f, 1.0f, 0.89f);
   public static final Color BG_OPTION_VALUE_DEF = new Color(1.0f, 1.0f, 0.96f);
   public static final Color BG_CODE = new Color(0.05f, 0.05f,0.05f);

   public static final Color FG_CS = new Color(0.41f,0.545f,0.41f);
   public static final Color FG_STYOPT = new Color(0.408f, 0.132f, 0.545f);
   public static final Color FG_CSOPT = new Color(0.408f, 0.132f, 0.545f);
   public static final Color FG_COMMENT = Color.GRAY;
   public static final Color FG_DEPRECATED_OR_BANNED = Color.RED;
}
