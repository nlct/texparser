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
import com.dickimawbooks.texparserlib.primitives.NewIf;
import com.dickimawbooks.texparserlib.generic.Symbol;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;
import com.dickimawbooks.texparserlib.latex.color.ColorSty;
import com.dickimawbooks.texparserlib.html.L2HConverter;

public class UserGuideSty extends LaTeXSty
{
   public UserGuideSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions, ColorSty colorSty)
   throws IOException
   {
      super(options, "nlctuserguide", listener, loadParentOptions);
      this.colorSty = colorSty;
   }

   @Override
   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      if (listener instanceof L2HConverter)
      {
         ((L2HConverter)listener).addCssStyle("dfn { font-style: normal; font-weight: bold; } a { text-decoration: none; } div.tablefns { border-top: solid; }");
      }

      glossariesSty.setModifier(listener.getOther('+'), "format",
        listener.createString("glsnumberformat"));

      glossariesSty.setModifier(listener.getOther('!'), "format",
        listener.createString("glsignore"));

      registerControlSequence(new MainMatterOnly());
      registerControlSequence(new MainMatterOnly("@@mainmatteronly"));

      colorSty.putColor("cs", FG_CS);
      colorSty.putColor("styopt", FG_STYOPT);
      colorSty.putColor("csopt", FG_CSOPT);
      colorSty.putColor("comment", FG_COMMENT);

      colorSty.putColor("style1", new Color(0.32f,0.545f,0.545f));// DarkSlateGray4
      colorSty.putColor("style2", new Color(0.21f,0.392f,0.545f));// SteelBlue4
      colorSty.putColor("style3", new Color(0f,0f,0.545f));// Blue4
      colorSty.putColor("style4", new Color(0.332f,0.1f,0.545f));// Purple4
      colorSty.putColor("style5", new Color(0.28f,0.235f,0.545f));// SlateBlue4
      colorSty.putColor("style6", new Color(0.518f,0.44f,1.0f));// LightSlateBlue

      registerControlSequence(new GuideGls());

      addSemanticCommand("sidenote", 
       new TeXFontText(TeXFontSize.FOOTNOTE), false, true, FloatBoxStyle.RIGHT);

      addSemanticCommand("advantagefmt", "advantage", null, Color.GREEN, null, null);
      addSemanticCommand("disadvantagefmt", "disadvantage", null, Color.RED, null, null);

      addSemanticCommand("strong", TeXFontWeight.STRONG);
      addSemanticCommand("code", TeXFontFamily.VERB);
      addSemanticCommand("cmd", listener.getOther('\\'));
      addSemanticCommand("comment", FG_COMMENT, listener.createString("% "));
      addSemanticCommand("csfmt", TeXFontFamily.VERB, FG_CS, 
        listener.getOther('\\'), null);
      addSemanticCommand("csfmtfont", TeXFontFamily.TT);
      addSemanticCommand("csfmtcolourfont", TeXFontFamily.TT, FG_CS);
      addSemanticCommand("cmdfmt", TeXFontFamily.VERB, null, 
        listener.getOther('\\'), null);
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
      addSemanticCommand("summarylocfmt", TeXFontShape.IT);

      addSemanticCommand("cbeg", TeXFontFamily.VERB, null, 
        listener.createString("\\begin{"), listener.getOther('}'));
      addSemanticCommand("cend", TeXFontFamily.VERB, null, 
        listener.createString("\\end{"), listener.getOther('}'));

      registerControlSequence(new GenericCommand(true,
         "thispackagename", null, new TeXCsRef("jobname")));

      registerControlSequence(new GenericCommand(true, "thispackage", null, 
          new TeXObject[]{new TeXCsRef("styfmt"), new TeXCsRef("thispackagename")}));

      registerControlSequence(new GenericCommand(true, "examplesdir", null, 
         new TeXObject[] {new TeXCsRef("jobname"), 
            listener.createString("-examples")}));

      registerControlSequence(new GenericCommand(true, "mainfmt", null,
         new TeXCsRef("glsnumberformat")));

      addSemanticCommand("termslocfmt", TeXFontShape.IT);

      registerControlSequence(new GenericCommand(true, "glsaddterm", null,
         new TeXCsRef("glsadd")));

      listener.addLaTeXCommand("seclocfmt", true, 2, null, 
        TeXParserUtils.createStack(listener, 
         listener.getOther(0xA7),
         listener.getParam(1),
         listener.getSpace(), listener.getOther('('),
         listener.getParam(2), listener.getOther(')')));

      registerControlSequence(new GenericCommand(true,
        "glsxtrchapterlocfmt", null, new TeXCsRef("seclocfmt")));
      registerControlSequence(new GenericCommand(true,
        "glsxtrsectionlocfmt", null, new TeXCsRef("seclocfmt")));
      registerControlSequence(new GenericCommand(true,
        "glsxtrsubsectionlocfmt", null, new TeXCsRef("seclocfmt")));
      registerControlSequence(new GenericCommand(true,
        "glsxtrsubsubsectionlocfmt", null, new TeXCsRef("seclocfmt")));
      registerControlSequence(new GenericCommand(true,
        "glsxtrparagraphlocfmt", null, new TeXCsRef("seclocfmt")));
      registerControlSequence(new GenericCommand(true,
        "glsxtrsubparagraphlocfmt", null, new TeXCsRef("seclocfmt")));

      registerControlSequence(new TextualContentCommand("TeXLive", "TeX Live"));
      registerControlSequence(new TextualContentCommand("MikTeX", "MikTeX"));

      registerControlSequence(new TextualContentCommand("dhyphen", "-"));
      registerControlSequence(new TextualContentCommand("dcolon", ":"));
      registerControlSequence(new TextualContentCommand("dcomma", ","));
      registerControlSequence(new TextualContentCommand("dequals", "="));
      registerControlSequence(new TextualContentCommand("dfullstop", "."));
      registerControlSequence(new TextualContentCommand("longswitch", "--"));
      registerControlSequence(new TextualContentCommand("shortswitch", "-"));
      registerControlSequence(new TextualContentCommand("dunderscore", "_"));
      registerControlSequence(new TextualContentCommand("dsb", "_"));
      registerControlSequence(new TextualContentCommand("codebackslash", "\\"));

      registerControlSequence(new TextualContentCommand("texparser@currentsection",
        "chapter"));

      registerControlSequence(new GenericCommand(true, 
        "currentcounter", null, new TeXCsRef("texparser@currentsection")));

      registerControlSequence(new MainGlsAdd(glossariesSty));

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

      addNestedSemanticCommand("initvalnotefmt", new TeXFontText(TeXFontShape.EM),
        new TeXFontText(TeXFontFamily.RM),
        Color.BLACK, null, null);

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

      addTaggedColourBox("important", BG_IMPORTANT, FRAME_COL_IMPORTANT);
      addTaggedColourBox("warning", BG_WARNING, FRAME_COL_WARNING);
      addTaggedColourBox("information", BG_INFO, FRAME_COL_INFO);
      TaggedColourBox pinnedBox = addTaggedColourBox("pinnedbox",
         "definition", BG_DEF, Color.BLACK);
      TaggedColourBox terminalBox = 
        addTaggedColourBox("terminal", new TeXFontText(TeXFontFamily.VERB), 
           BG_TERMINAL, Color.BLACK);
      TaggedColourBox ctrBox = addTaggedColourBox("ctrbox",
         "counter", BG_DEF, Color.BLACK);

      TaggedColourBox codeBox = addTaggedColourBox("codebox",
         "code", new TeXFontText(TeXFontFamily.VERB), BG_CODE, Color.BLACK);

      registerControlSequence(new DuplicateEnv("codebox*", codeBox));

      TaggedColourBox resultBox = addTaggedColourBox("resultbox",
         "result", null, Color.BLACK);
      addTaggedColourBox("badcodebox",
         "badcode", new TeXFontText(TeXFontFamily.VERB), BG_CODE, Color.BLACK);
      addTaggedColourBox("unicodebox",
         "unicode", new TeXFontText(TeXFontFamily.VERB), BG_CODE, Color.BLACK);

      FrameBoxEnv compactcodeBox = new FrameBoxEnv(
        addSemanticCommand("@compactcodebox", "compactcodebox",
        new TeXFontText(TeXFontFamily.VERB),
        (Color)null, BG_CODE, Color.BLACK, null, null, false, true));

      registerControlSequence(compactcodeBox);
      registerControlSequence(new DuplicateEnv("compactcodebox*", compactcodeBox));

      FrameBox crc = addSemanticCommand("@sidebysidecode", "sidebysidecode",
        new TeXFontText(TeXFontFamily.VERB, TeXFontSize.SMALL),
        (Color)null, BG_CODE, Color.BLACK, null, null, true, true, 
         null, // left outer margin
         new UserDimension(0.03, new PercentUnit()), // right outer margin
         new UserDimension(), // top outer margin
         null, // bottom outer margin
         AlignHStyle.DEFAULT, AlignVStyle.TOP, 
         new UserDimension(0.47, new PercentUnit()));

      FrameBox crr = addSemanticCommand("@sidebysideresult", "sidebysideresult",
         null, (Color)null, null, Color.BLACK, null, null, true, true, 
         null, // left outer margin
         null, // right outer margin
         new UserDimension(), // top outer margin
         null, // bottom outer margin
         AlignHStyle.DEFAULT, AlignVStyle.TOP, 
         new UserDimension(0.47, new PercentUnit()));

      CodeResult codeResult = new CodeResult(
         new ColourBox("frame@coderesult@title", BorderStyle.NONE,
          AlignHStyle.CENTER, AlignVStyle.DEFAULT, false, true, null, null),
        crc, crr);

      registerControlSequence(codeResult);

      registerControlSequence(new DuplicateEnv("coderesult*", codeResult));

      FrameBox defnBox = addColourBox("defnbox", null, null,
        BG_DEF, Color.BLACK);
      FrameBox optionSummaryBox = addColourBox("optionsummarybox", null, null,
        BG_DEF, Color.BLACK);
      FrameBox optionValueSummaryBox = addSemanticCommand("optionvaluesummarybox",
         new UserDimension(40, FixedUnit.BP));

      FrameBox rightBox = addFloatBox("floatrightbox");

      FrameBox noteBox = new ColourBox("noteBox", BorderStyle.NONE,
        AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, false, null, null);
      listener.declareFrameBox(noteBox, false);

      registerControlSequence(new CmdDef(pinnedBox, rightBox, noteBox, glossariesSty));
      registerControlSequence(new EnvDef(pinnedBox, rightBox, noteBox, glossariesSty));
      registerControlSequence(new CtrDef(ctrBox, rightBox, noteBox, glossariesSty));
      registerControlSequence(new PkgDef(pinnedBox, rightBox, noteBox, glossariesSty));
      registerControlSequence(new ClsDef(pinnedBox, rightBox, noteBox, glossariesSty));

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
      registerControlSequence(new SummaryEnvironmentBox(defnBox, 
        rightBox, noteBox, glossariesSty));
      registerControlSequence(new SummaryCommandOptionBox(optionSummaryBox, 
        rightBox, noteBox, glossariesSty));
      registerControlSequence(new SummaryCommandOptionBox(
        "summaryglossentryoption", defnBox, 
        rightBox, noteBox, glossariesSty));
      registerControlSequence(new SummaryOptionValueBox(optionValueSummaryBox, 
        rightBox, noteBox, glossariesSty));

      createIndexItemBox(0);
      createIndexItemBox(1);
      createIndexItemBox(2);

      addColourBox("nlctusernavbox", null, null, null, null);
      addSemanticCommand("texparser@abstractheader", "abstractheader", 
        new TeXFontText(TeXFontWeight.BF), null, null, null, null, null, 
         false, false, null, AlignHStyle.CENTER);

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

      registerControlSequence(new InlineGlsDef(glossariesSty));
      registerControlSequence(new InlineGlsDef("inlineidxdef", "idx.", glossariesSty));
      registerControlSequence(new CmdDefSyntax(glossariesSty));
      registerControlSequence(new OptDefSyntax(glossariesSty));

      registerControlSequence(new PrintTerms());

      registerControlSequence(new PrintAbbrs(glossariesSty));
      registerControlSequence(new PrintIcons(glossariesSty));
      registerControlSequence(new PrintMain(glossariesSty));
      registerControlSequence(new PrintSummary(glossariesSty));
      registerControlSequence(new PrintCommandOptions(glossariesSty));
      registerControlSequence(new PrintCommonOptions(glossariesSty));
      registerControlSequence(new PrintIndex(glossariesSty));
      registerControlSequence(new IndexInitPostNameHooks());
      registerControlSequence(new AbbrPostNameHook(glossariesSty));

      registerControlSequence(new Dgls("idx", CaseChange.NO_CHANGE, glossariesSty));
      registerControlSequence(new Dgls("idxpl", 
       CaseChange.NO_CHANGE, true, glossariesSty));
      registerControlSequence(new Dgls("Idx", CaseChange.SENTENCE, glossariesSty));
      registerControlSequence(new Dgls("Idxpl", CaseChange.SENTENCE, true, glossariesSty));
      registerControlSequence(new Dglslink("idxc", false, glossariesSty));

      registerControlSequence(new Idxn(glossariesSty));

      // dual prefix list
      def = listener.createString("dual.,idx.,");
        def.add(listener.getControlSequence("empty"));
      registerControlSequence(new GenericCommand(true, "@glsxtr@labelprefixes",
       null, def));

      // \optval
      def = listener.createStack();
      def.add(new TeXCsRef("gls"));

      grp = listener.createGroup("opt.");
      def.add(grp);
      grp.add(listener.getParam(1));

      def.add(new TeXCsRef("optfmt"));
      grp = listener.createGroup("=");
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "optval", "mm", def));

      // \optvalm
      def = listener.createStack();
      def.add(new TeXCsRef("gls"));

      grp = listener.createGroup("opt.");
      def.add(grp);
      grp.add(listener.getParam(1));

      def.add(new TeXCsRef("optfmt"));
      grp = listener.createGroup("=");
      def.add(grp);

      grp.add(new TeXCsRef("marg"));

      subgrp = listener.createGroup();
      grp.add(subgrp);
      subgrp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "optvalm", "mm", def));

      // \opteqvalref
      def = listener.createStack();
      def.add(new TeXCsRef("gls"));

      grp = listener.createGroup("opt.");
      def.add(grp);
      grp.add(listener.getParam(1));

      def.add(new TeXCsRef("optfmt"));
      grp = listener.createGroup("=");
      def.add(grp);

      def.add(new TeXCsRef("gls"));

      grp = listener.createGroup("optval.");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp.add(listener.getOther('.'));
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "opteqvalref", "mm", def));

      // \optvalref
      def = listener.createStack();
      def.add(new TeXCsRef("gls"));

      grp = listener.createGroup("optval.");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp.add(listener.getOther('.'));
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "optvalref", "mm", def));

      // \fmtorcode
      def = listener.createStack();
      def.add(listener.getParam(1));

      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "fmtorcode", "mm", def));

      // \metaboolean
      def = listener.createStack();
      def.add(new TeXCsRef("meta"));
      def.add(listener.createGroup("boolean"));

      registerControlSequence(new GenericCommand(true,
       "metaboolean", null, def));

      // \keyval
      def = listener.createStack();
      def.add(new TeXCsRef("meta"));
      def.add(listener.createGroup("key"));
      def.add(listener.getOther('='));
      def.add(new TeXCsRef("meta"));
      def.add(listener.createGroup("value"));

      registerControlSequence(new GenericCommand(true,
       "keyval", null, def));

      // \keyvallist
      def = listener.createStack();
      def.add(new TeXCsRef("meta"));
      def.add(listener.createGroup("key=value list"));

      registerControlSequence(new GenericCommand(true,
       "keyvallist", null, def));

      // \keyeqvalue
      def = listener.createStack();
      def.add(listener.getParam(1));
      def.add(listener.getOther('='));
      def.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "keyeqvalue", "mm", def));

      // \keyeqvaluem
      def = listener.createStack();
      def.add(listener.getParam(1));
      def.add(listener.getOther('='));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(2)));

      registerControlSequence(new LaTeXGenericCommand(true,
       "keyeqvaluem", "mm", def));

      // \metafilefmt
      def = listener.createStack();
      def.add(new TeXCsRef("filefmt"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(1));

      def.add(new TeXCsRef("meta"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      def.add(new TeXCsRef("filefmt"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(3));

      registerControlSequence(new LaTeXGenericCommand(true,
       "metafilefmt", "mmm", def));

      // \metametafilefmt
      def = listener.createStack();
      def.add(new TeXCsRef("filefmt"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(1));

      def.add(new TeXCsRef("meta"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      def.add(new TeXCsRef("filefmt"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(3));

      def.add(new TeXCsRef("meta"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(4));

      def.add(new TeXCsRef("filefmt"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(5));

      registerControlSequence(new LaTeXGenericCommand(true,
       "metametafilefmt", "mmmmm", def));

      // \texdocref
      def = listener.createStack();
      def.add(new TeXCsRef("begin"));
      def.add(listener.createGroup("terminal"));
      def.add(new TeXCsRef("href"));
      def.add(listener.createGroup("https://www.tug.org/texdoc/"));
      def.add(listener.createGroup("texdoc"));
      def.add(listener.getSpace());
      def.add(listener.getParam(1));
      def.add(new TeXCsRef("end"));
      def.add(listener.createGroup("terminal"));

      registerControlSequence(new LaTeXGenericCommand(true,
       "texdocref", "m", def));

      // \tugboat
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup("https://tug.org/TUGboat/tb");
      def.add(grp);
      grp.add(listener.getParam(2));
      grp.add(listener.getOther('-'));
      grp.add(listener.getParam(4));
      grp.add(listener.getOther('/'));
      grp.add(listener.getParam(5));

      grp = listener.createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("qt"));
      grp.add(TeXParserUtils.createGroup(listener, 
        listener.getParam(1)));
      grp.add(listener.getSpace());
      grp.add(new TeXCsRef("emph"));
      grp.add(listener.createGroup("TUGboat"));
      grp.addAll(listener.createString(", Volume "));
      grp.add(listener.getParam(3));
      grp.add(listener.getSpace());
      grp.add(listener.getOther('('));
      grp.add(listener.getParam(2));
      grp.add(listener.createString("), No. "));
      grp.add(listener.getParam(4));

      registerControlSequence(new LaTeXGenericCommand(true,
       "tugboat", "mmmmm", def));

      // \CTANpkg
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup("https://ctan.org/pkg/");
      def.add(grp);
      grp.add(listener.getParam(1));
      def.add(listener.createGroup("CTAN"));

      registerControlSequence(new LaTeXGenericCommand(true,
       "CTANpkg", "m", def));

      // \ctanpkg
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup("https://ctan.org/pkg/");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup("ctan.org/pkg/");
      def.add(grp);
      grp.add(listener.getParam(1));

      registerControlSequence(new LaTeXGenericCommand(true,
       "ctanpkg", "m", def));

      // \ctanref
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup("https://ctan.org/");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "ctanref", "mm", def));

      // \ctanmirrornofn and \ctanmirror
      // (Treat identically.)
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup("http://mirrors.ctan.org/");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "ctanmirrornofn", "mm", def));
      registerControlSequence(new LaTeXGenericCommand(true,
       "ctanmirror", "mm", def));

      // \ctanmirrordocnofn use .html instead of .pdf
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup("http://mirrors.ctan.org/");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp.addAll(listener.createString(".html"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "ctanmirrordocnofn", "mm", def));

      // \ctanpkgmirror
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup("http://mirrors.ctan.org/pkg/");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "ctanpkgmirror", "mm", def));

      // \ctansupportmirror
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup("http://mirrors.ctan.org/support/");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "ctansupportmirror", "mm", def));

      // \texseref
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup("https://tex.stackexchange.com/");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "texseref", "mm", def));

      // \dickimawhrefnofn and \dickimawhref
      // (Treat identically.)
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup("https://www.dickimaw-books.com/");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "dickimawhrefnofn", "mm", def));

      registerControlSequence(new LaTeXGenericCommand(true,
       "dickimawhref", "mm", def));

      // \blog
      def = listener.createStack();
      def.add(new TeXCsRef("dickimawhref"));
      grp = listener.createGroup("blog/");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "blog", "mm", def));

      // \gallery
      def = listener.createStack();
      def.add(new TeXCsRef("dickimawhref"));
      def.add(listener.createGroup("gallery"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      registerControlSequence(new LaTeXGenericCommand(true,
       "gallery", "m", def));

      // \galleryref
      def = listener.createStack();
      def.add(new TeXCsRef("dickimawhref"));

      grp = listener.createGroup("gallery/");
      grp.add(listener.getParam(1));
      def.add(grp);
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(2)));

      registerControlSequence(new LaTeXGenericCommand(true,
       "galleryref", "mm", def));

      // \gallerytopic
      def = listener.createStack();
      def.add(new TeXCsRef("dickimawhref"));

      grp = listener.createGroup("gallery/#");
      grp.add(listener.getParam(1));
      def.add(grp);

      grp = listener.createGroup();
      def.add(grp);

      grp.add(new TeXCsRef("styfmt"));
 
      grp.add(TeXParserUtils.createGroup(listener, 
        listener.getParam(1)));

      grp.add(listener.getSpace());
      grp.add(listener.createString("gallery"));

      registerControlSequence(new LaTeXGenericCommand(true,
       "gallerytopic", "m", def));

      // \gallerypage
      def = listener.createStack();
      def.add(new TeXCsRef("dickimawhref"));
      grp = listener.createGroup("gallery/index.php?label=");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "gallerypage", "mm", def));

      // \faqspkg
      def = listener.createStack();
      def.add(new TeXCsRef("dickimawhref"));
      grp = listener.createGroup("faqs/");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp.add(listener.createString("faq.html"));

      grp = listener.createGroup();
      def.add(grp);

      grp.add(new TeXCsRef("styfmt"));
      grp.add(TeXParserUtils.createGroup(listener, 
         listener.getParam(1)));
      grp.add(listener.getSpace());
      grp.add(listener.createString("FAQ"));

      registerControlSequence(new LaTeXGenericCommand(true,
       "faqspkg", "m", def));

      // \faqpage
      def = listener.createStack();
      def.add(new TeXCsRef("dickimawhref"));
      grp = listener.createGroup("faq.php?category=");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "faqpage", "mm", def));

      // \faqitem
      def = listener.createStack();
      def.add(new TeXCsRef("dickimawhref"));
      grp = listener.createGroup("faq.php?itemlabel=");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "faqitem", "mm", def));

      registerControlSequence(new XrSectionRef());
      registerControlSequence(new DocRef());
      registerControlSequence(new DocRef("qtdocref", true, false));
      registerControlSequence(new DocRef("altdocref", false, true));

      registerControlSequence(new Plabel());

      // \sectionref
      registerControlSequence(new Ref("sectionref", new TeXCsRef("S")));

      // \exampleref
      registerControlSequence(new Ref("exampleref", false, 
         new TeXCsRef("Examplename"), listener.getSpace()));

      // \tableref
      registerControlSequence(new TextualContentCommand("Tablename", "Table"));
      registerControlSequence(new Ref("tableref", false,
       new TeXCsRef("Tablename"), listener.getSpace()));
      registerControlSequence(new Ref("Tableref", false,
       new TeXCsRef("Tablename"), listener.getSpace()));

      // \figureref
      registerControlSequence(new TextualContentCommand("Figurename", "Figure"));
      registerControlSequence(new Ref("figureref", false,
       new TeXCsRef("Figurename"), listener.getSpace()));
      registerControlSequence(new Ref("Figureref", false,
       new TeXCsRef("Figurename"), listener.getSpace()));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "unlimited", 0x221E));
      registerControlSequence(AccSuppObject.createSymbol(
        listener, "tick", 0x2713));
      registerControlSequence(AccSuppObject.createSymbol(
        listener, "yes", 0x2714));
      registerControlSequence(AccSuppObject.createSymbol(
        listener, "no", 0x2716));

      registerControlSequence(new Symbol("dash", 0x2015));

      registerControlSequence(new Symbol("nlctopensqbracket", '['));
      registerControlSequence(new Symbol("nlctclosesqbracket", ']'));

      registerControlSequence(new Symbol("codesym", 0x1F5B9));
      registerControlSequence(new Symbol("resultsym", 0x1F5BA));
      registerControlSequence(new Symbol("warningsym", 0x26A0));
      registerControlSequence(new Symbol("importantsym", 0x2139));
      registerControlSequence(new Symbol("informationsym", 0x1F6C8));
      registerControlSequence(new Symbol("definitionsym", 0x1F4CC));
      registerControlSequence(new GenericCommand(true,
        "valuesettingsym", null, new TeXCsRef("faSliders")));
      registerControlSequence(new Symbol("novaluesettingsym", 0x1D362));
      registerControlSequence(new GenericCommand(true,
        "toggleonsettingsym", null, new TeXCsRef("faToggleOn")));
      registerControlSequence(new GenericCommand(true,
        "toggleoffsettingsym", null, new TeXCsRef("faToggleOff")));
      registerControlSequence(new Symbol("optionvaluesym", 0x1F516));
      registerControlSequence(new Symbol("countersym", 0x2116));
      registerControlSequence(new GenericCommand(true,
        "terminalsym", null, new TeXCsRef("faTerminal")));

      // \deprecatedsym
      def = listener.createStack();

      def.add(new TeXCsRef("deprecatedorbannedfmt"));
      def.add(TeXParserUtils.createGroup(listener, new TeXCsRef("faTrashO")));

      registerControlSequence(new GenericCommand(true,
       "deprecatedsym", null, def));

      // \bannedsym
      def = listener.createStack();

      def.add(new TeXCsRef("deprecatedorbannedfmt"));
      def.add(TeXParserUtils.createGroup(listener, new TeXCsRef("faBan")));

      registerControlSequence(new GenericCommand(true,
       "bannedsym", null, def));

      // \badcodesym
      def = listener.createStack();
      def.add(listener.getControlSequence("texparser@overlapped"));
      def.add(new TeXCsRef("faFileTextO"));
      def.add(listener.getControlSequence("texparser@overlapper"));
      grp = listener.createGroup();
      def.add(grp);

      grp.add(new TeXCsRef("deprecatedorbannedfmt"));
      grp.add(new TeXCsRef("faBan"));

      registerControlSequence(new GenericCommand(true,
       "badcodesym", null, def));

      // unicodesym
      def = listener.createStack();
      def.add(listener.getControlSequence("texparser@overlapped"));
      def.add(new TeXCsRef("faFileO"));
      def.add(listener.getControlSequence("texparser@overlapper"));
      def.add(listener.createGroup("U"));

      registerControlSequence(new GenericCommand(true,
       "unicodesym", null, def));

      // \cmdnotefmt
      registerControlSequence(new AtFirstOfOne("cmdnotefmt"));

      // \conditionsyntax
      def = listener.createStack();
      def.add(listener.getSpace());
      def.add(new TeXCsRef("meta"));
      def.add(listener.createGroup("true"));
      def.add(new TeXCsRef("csfmt"));
      def.add(listener.createGroup("else"));
      def.add(new TeXCsRef("meta"));
      def.add(listener.createGroup("false"));
      def.add(new TeXCsRef("csfmt"));
      def.add(listener.createGroup("fi"));

      registerControlSequence(new GenericCommand(true,
       "conditionsyntax", null, def));

      // \proyes
      registerControlSequence(new GenericCommand(true, "proyes", null, 
         new TeXObject[] { new TeXCsRef("advantagefmt"), new TeXCsRef("yes")}));

      // \prono
      registerControlSequence(new GenericCommand(true, "prono", null, 
         new TeXObject[] { new TeXCsRef("disadvantagefmt"), new TeXCsRef("no")}));

      // \conno
      registerControlSequence(new GenericCommand(true, "conno", null, 
         new TeXObject[] { new TeXCsRef("advantagefmt"), new TeXCsRef("no")}));

      // \conyes
      registerControlSequence(new GenericCommand(true, "conyes", null, 
         new TeXObject[] { new TeXCsRef("disadvantagefmt"), new TeXCsRef("yes")}));


      // \summarynotefmt
      def = listener.createStack();
      def.add(listener.getOther('('));
      def.add(listener.getParam(1));
      def.add(listener.getOther(')'));

      registerControlSequence(new LaTeXGenericCommand(true,
       "summarynotefmt", "m", def));

      registerControlSequence(new TextualContentCommand(
         "glsxtrpostdescdualindexabbreviation", "."));

      listener.newcounter("example");

      registerControlSequence(new TextualContentCommand(
         "examplename", "example"));
      registerControlSequence(new TextualContentCommand(
         "Examplename", "Example"));
      registerControlSequence(new TextualContentCommand(
         "listofexamplesname", "List of Examples"));

      registerControlSequence(new GenericCommand(true,
        "listofexampleslabel", null, TeXParserUtils.createStack(listener,
          new TeXCsRef("label"), listener.createGroup("sec:listofexamples"))));

      registerControlSequence(new ListOfExamples());

      registerControlSequence(new GenericCommand(true,
        "nlctexampletag", null, TeXParserUtils.createStack(listener,
          new TeXCsRef("Examplename"), listener.getSpace(),
          new TeXCsRef("theexample"))));

      registerControlSequence(new CreateExample());
      registerControlSequence(new ExampleFileBaseName());

      registerControlSequence(new GenericCommand(true,
        "exampleattachtexicon", null, new AccSuppObject(
          AccSupp.createSymbol("TeX File Attachment", true),
          TeXParserUtils.createStack(listener,
          new TeXCsRef("faPaperclip"), 
          new TeXCsRef("textsuperscript"), 
          new TeXCsRef("faFileTextO")))));

      registerControlSequence(new GenericCommand(true,
        "exampleattachpdficon", null, new AccSuppObject(
          AccSupp.createSymbol("PDF File Attachment", true),
          TeXParserUtils.createStack(listener,
          new TeXCsRef("faPaperclip"), 
          new TeXCsRef("textsuperscript"), 
          new TeXCsRef("faFilePdfO")))));

      registerControlSequence(new GenericCommand(true,
        "exampledownloadtexicon", null, new AccSuppObject(
          AccSupp.createSymbol("Download TeX File", true),
          TeXParserUtils.createStack(listener,
          new TeXCsRef("faDownload"), 
          new TeXCsRef("textsuperscript"), 
          new TeXCsRef("faFileTextO")))));

      registerControlSequence(new GenericCommand(true,
        "exampledownloadpdficon", null, new AccSuppObject(
          AccSupp.createSymbol("Download PDF", true),
           TeXParserUtils.createStack(listener,
          new TeXCsRef("faDownload"), 
          new TeXCsRef("textsuperscript"), 
          new TeXCsRef("faFilePdfO")))));

      NewIf.createConditional(true, getParser(), "ifnlctdownloadlinks", true);

      registerControlSequence(new TextualContentCommand("filedownloadsubpath",
        "samples/"));

      // \filedownloadlink
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("filedownloadsubpath"));
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("faDownload"));

      registerControlSequence(new LaTeXGenericCommand(true, "filedownloadlink",
       "m", def));

      // \filetag
      def = listener.createStack();
      def.add(new TeXCsRef("faFileO"));
      def.add(new TeXCsRef("ifnlctdownloadlinks"));
      def.add(new TeXCsRef("filedownloadlink"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(1));
      def.add(new TeXCsRef("fi"));
      def.add(new TeXCsRef("space"));

      registerControlSequence(new LaTeXGenericCommand(true, "filetag",
       "m", def));

      // \filedef
      FrameBox fileDefBox = addSemanticCommand("@filedefbox", "filedef",
       new TeXFontText(TeXFontFamily.TT), null, null, null, null, null,
       false, true, null, AlignHStyle.LEFT);

      registerControlSequence(fileDefBox);

      def = listener.createStack();
      def.add(fileDefBox);
      grp = listener.createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("filetag"));
      grp.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
      grp.add(new TeXCsRef("mainglsadd"));
      subgrp = listener.createGroup("file.");
      grp.add(subgrp);
      subgrp.add(listener.getParam(1));
      grp.add(listener.createGroup("filedef"));
      grp.add(new TeXCsRef("glsxtrglossentry"));
      subgrp = listener.createGroup("file.");
      grp.add(subgrp);
      subgrp.add(listener.getParam(1));

      registerControlSequence(new LaTeXGenericCommand(true, "filedef",
       "m", def));

      // provide these commands in case they are redefined in the
      // document, but they're for bib2gls so they can be ignored
      registerControlSequence(new GenericCommand("nlctuserguidecustomentryaliases"));
      registerControlSequence(new GenericCommand("nlctuserguideignoredpuncrules"));
      registerControlSequence(new GenericCommand("nlctuserguidepuncrules"));
      registerControlSequence(new GenericCommand("nlctuserguidepreletterrules"));
      registerControlSequence(new GenericCommand("nlctuserguideletterrules"));
      registerControlSequence(new GenericCommand("nlctuserguideextrarules"));

      registerControlSequence(new TextualContentCommand("codepar", 
       String.format("%n")));

      registerControlSequence(new DefSemanticCmd(this));

      if (atSymGroup)
      {
         registerControlSequence(new SymbolGroupLabel("bibglsothergroup"));
         registerControlSequence(new SymbolGroupTitle("bibglsothergrouptitle"));
      }

      NewIf.createConditional(true, getParser(), "ifshowsummarytopgroupheaders", true);

      registerControlSequence(new DefListDec());
      registerControlSequence(new ItemDesc());

      registerControlSequence(new TextualContentCommand("optionlistprefix", "opt."));
      registerControlSequence(new TextualContentCommand("optionlisttag", "Option"));
      registerControlSequence(new TextualContentCommand("optionlisttags", "Options"));

      // \optionlistitemformat
      def = listener.createStack();
      def.add(new TeXCsRef("glsentrytext"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      registerControlSequence(new LaTeXGenericCommand(true, "optionlistitemformat",
       "m", def));

      registerControlSequence(new Option());
      registerControlSequence(new Options());
      registerControlSequence(new Options("optionsor", "or"));
      registerControlSequence(new OptionsTo());

      // \tablefnmark
      def = listener.createStack();
      def.add(new TeXCsRef("textsuperscript"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      registerControlSequence(new LaTeXGenericCommand(true, "tablefnmark",
       "m", def));

      // \tablefntext
      def = listener.createStack();
      def.add(new TeXCsRef("tablefnfmt"));
      grp = listener.createGroup();
      def.add(grp);

      grp.add(new TeXCsRef("tablefnmark"));
      grp.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true, "tablefntext",
       "mm", def));

      // \tablefnfmt
      addSemanticCommand("tablefnfmt", "tablefn", 
       new TeXFontText(TeXFontSize.FOOTNOTE), null, null, null, null, null,
       false, true);

      // \tablefn
      addSemanticCommand("tablefns", "tablefns", null, null, null, null,
        null, null, false, true, null, null, null, null,
        AlignHStyle.LEFT, AlignVStyle.DEFAULT, 
        new UserDimension(0.8f, new PercentUnit(PercentUnit.LINE_WIDTH)));

      // \fnsymtext
      def = listener.createStack();
      def.add(new TeXCsRef("tablefntext"));
      grp = listener.createGroup();
      def.add(grp);

      grp.add(new TeXCsRef("fnsymmarker"));
      grp.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      def.add(TeXParserUtils.createGroup(listener, listener.getParam(2)));

      registerControlSequence(new LaTeXGenericCommand(true, "fnsymtext",
       "mm", def));

      // \fnsym
      def = listener.createStack();
      def.add(new TeXCsRef("tablefnmark"));
      grp = listener.createGroup();
      def.add(grp);

      grp.add(new TeXCsRef("fnsymmark"));
      grp.add(TeXParserUtils.createGroup(listener, 
         new TeXCsRef("fnsymmarker"), 
         TeXParserUtils.createGroup(listener, listener.getParam(1))));

      registerControlSequence(new LaTeXGenericCommand(true, "fnsym",
       "m", def));

      // \fnsymmark
      registerControlSequence(new AtFirstOfOne("fnsymmark"));

      registerControlSequence(new FnSymMarker());

      // \araraline
      def = listener.createString("% arara: ");
      def.add(listener.getParam(1));

      registerControlSequence(new LaTeXGenericCommand(true, "araraline",
       "m", def));

      registerControlSequence(new AtFirstOfOne("settabcolsep"));

      registerControlSequence(new AtFirstOfOne("textsmaller"));
      registerControlSequence(new AtFirstOfOne("textlarger"));
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
      if (option.equals("deephierarchy"))
      {
         getListener().setcounter("secnumdepth", 
          LaTeXParserListener.SUBPARAGRAPH_LEVEL);
      }
      else if (option.equals("atsymgroup"))
      {
         atSymGroup = true;
      }
      else if (option.equals("noatsymgroup"))
      {
         atSymGroup = false;
      }
      else
      {
         glossariesSty.processOption(option, value);
      }
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

   protected void addSemanticCommand(String name, TeXFontShape shape)
   {
      addSemanticCommand(name, shape, null);
   }

   protected void addSemanticCommand(String name, TeXFontShape shape, Color fg)
   {
      TeXFontText font = new TeXFontText();
      font.setShape(shape);
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

   public FrameBox addSemanticCommand(String name, 
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
      return addSemanticCommand(name, id, font, fg, bg, frameCol, 
      prefix, suffix, isInLine, isMultiLine, leftOuterMargin, AlignHStyle.DEFAULT);
   }

   protected FrameBox addSemanticCommand(String name, String id, 
    TeXFontText font, Color fg, Color bg, Color frameCol, 
      TeXObject prefix, TeXObject suffix,
      boolean isInLine, boolean isMultiLine, TeXDimension leftOuterMargin,
      AlignHStyle halign)
   {
      return addSemanticCommand(name, id, font, fg, bg, frameCol, 
      prefix, suffix, isInLine, isMultiLine, leftOuterMargin,
      null, null, null, halign, AlignVStyle.DEFAULT, null);
   }

   protected FrameBox addSemanticCommand(String name, TeXFontText font, 
      boolean isInLine, boolean isMultiLine,
    FloatBoxStyle floatStyle)
   {
      return addSemanticCommand(name, name, font, null, null, null, 
      null, null, isInLine, isMultiLine, null,
      null, null, null, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, null, 
      floatStyle);
   }

   protected FrameBox addSemanticCommand(String name, String id, 
    TeXFontText font, Color fg, Color bg, Color frameCol, 
      TeXObject prefix, TeXObject suffix,
      boolean isInLine, boolean isMultiLine, TeXDimension leftOuterMargin,
      TeXDimension rightOuterMargin,
      TeXDimension topOuterMargin,
      TeXDimension bottomOuterMargin,
      AlignHStyle halign, AlignVStyle valign, TeXDimension width)
   {
      return addSemanticCommand(name, id, font, fg, bg, frameCol, 
      prefix, suffix, isInLine, isMultiLine, leftOuterMargin,
      rightOuterMargin, topOuterMargin, bottomOuterMargin,
      halign, valign, width, null);
   }

   protected FrameBox addSemanticCommand(String name, String id, 
    TeXFontText font, Color fg, Color bg, Color frameCol, 
      TeXObject prefix, TeXObject suffix,
      boolean isInLine, boolean isMultiLine, TeXDimension leftOuterMargin,
      TeXDimension rightOuterMargin,
      TeXDimension topOuterMargin,
      TeXDimension bottomOuterMargin,
      AlignHStyle halign, AlignVStyle valign, TeXDimension width,
      FloatBoxStyle floatStyle)
   {
      FrameBox boxFrame = new ColourBox(name, 
       frameCol == null ? BorderStyle.NONE : BorderStyle.SOLID,
       halign, valign, isInLine, null, null);

      boxFrame.setIsMultiLine(isMultiLine);
      boxFrame.setTextFont(font);
      boxFrame.setForegroundColor(fg);
      boxFrame.setBackgroundColor(bg);

      if (floatStyle != null)
      {
         boxFrame.setFloatStyle(floatStyle);
      }

      if (frameCol != null)
      {
         boxFrame.setBorderColor(frameCol);
         boxFrame.setBorderWidth(new UserDimension(1, FixedUnit.BP));
         boxFrame.setInnerMargin(new UserDimension(2, FixedUnit.BP));
      }

      if (width != null)
      {
         boxFrame.setWidth(width);
      }

      if (leftOuterMargin != null)
      {
         boxFrame.setOuterMarginLeft(leftOuterMargin);
      }

      if (rightOuterMargin != null)
      {
         boxFrame.setOuterMarginRight(rightOuterMargin);
      }

      if (topOuterMargin != null)
      {
         boxFrame.setOuterMarginTop(topOuterMargin);
      }

      if (bottomOuterMargin != null)
      {
         boxFrame.setOuterMarginBottom(bottomOuterMargin);
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

   public ColorSty getColorSty()
   {
      return colorSty;
   }

   protected GlossariesSty glossariesSty;
   protected ColorSty colorSty;

   protected boolean atSymGroup = true;

   public static final Color BG_DEF = new Color(1.0f, 1.0f, 0.75f);
   public static final Color BG_OPTION_DEF = new Color(1.0f, 1.0f, 0.89f);
   public static final Color BG_OPTION_VALUE_DEF = new Color(1.0f, 1.0f, 0.96f);
   public static final Color BG_CODE = new Color(0.98f, 0.98f, 0.98f);

   public static final Color BG_TERMINAL = new Color(0.98f, 0.98f, 0.98f);

   public static final Color FG_CS = new Color(0.328f,0.436f,0.1f);
   public static final Color FG_STYOPT = new Color(0.408f, 0.132f, 0.545f);// DarkOrchid4
   public static final Color FG_CSOPT = new Color(0.408f, 0.132f, 0.545f);
   public static final Color FG_COMMENT = new Color(0.37f,0.37f,0.37f);
   public static final Color FG_DEPRECATED_OR_BANNED = new Color(0.8f,0f,0f);

   public static final Color FRAME_COL_WARNING = Color.RED;
   public static final Color BG_WARNING = new Color(1.0f,0.92f,0.92f);

   public static final Color FRAME_COL_IMPORTANT = Color.RED;
   public static final Color BG_IMPORTANT = new Color(1.0f,0.92f,0.92f);

   public static final Color FRAME_COL_INFO = new Color(0f,0.5f,0.5f);
   public static final Color BG_INFO = new Color(0.94f,1.0f,1.0f);
}
