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

import java.util.Vector;
import java.util.HashMap;
import java.io.IOException;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.NewIf;
import com.dickimawbooks.texparserlib.primitives.Relax;
import com.dickimawbooks.texparserlib.generic.Symbol;
import com.dickimawbooks.texparserlib.auxfile.AuxCommand;
import com.dickimawbooks.texparserlib.auxfile.AuxData;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;
import com.dickimawbooks.texparserlib.latex.color.ColorSty;
import com.dickimawbooks.texparserlib.html.L2HConverter;
import com.dickimawbooks.texparserlib.html.WidgetMenu;
import com.dickimawbooks.texparserlib.html.Widget;

public class UserGuideSty extends LaTeXSty
  implements BeginDocumentListener
{
   public UserGuideSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions, ColorSty colorSty)
   throws IOException
   {
      this(options, "nlctuserguide", listener, loadParentOptions, colorSty);
   }

   public UserGuideSty(KeyValList options, String styName, LaTeXParserListener listener, 
     boolean loadParentOptions, ColorSty colorSty)
   throws IOException
   {
      super(options, styName, listener, loadParentOptions);
      this.colorSty = colorSty;

      listener.addBeginDocumentListener(this);
   }

   @Override
   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      registerControlSequence(new VersionDate());
      registerControlSequence(new AtFirstOfOne("hologoRobust"));
      registerControlSequence(new GenericCommand(true,
        "visiblespace", null, new TeXCsRef("textvisiblespace")));

      addCssStyles();
      addSemanticCommands();
      addExtraSyntaxSemanticCommands();
      addCrossRefCommands();
      addFootnoteCommands();
      addDocRefCommands();
      addSymbolCommands();
      addGlsIconCommands();
      addDiscretionaryCommands();
      addTextCommands();
      addListCommands();
      addBoxCommands();
      addInlineDefCommands();
      addExampleCommands();
      addLocationCommands();
      addPrintCommands();
      addBib2GlsCommands();
      addGlsCommands();

      glossariesSty.setModifier(listener.getOther('+'), "format",
        listener.createString("glsnumberformat"));

      glossariesSty.setModifier(listener.getOther('!'), "format",
        listener.createString("glsignore"));

      registerControlSequence(new MainMatterOnly());
      registerControlSequence(new MainMatterOnly("@@mainmatteronly"));

      registerControlSequence(new GuideGls());

      registerControlSequence(new GenericCommand(true,
         "thispackagename", null, new TeXCsRef("jobname")));

      registerControlSequence(new GenericCommand(true, "thispackage", null, 
          new TeXObject[]{new TeXCsRef("styfmt"), new TeXCsRef("thispackagename")}));

      registerControlSequence(new TextualContentCommand("texparser@currentsection",
        "chapter"));

      registerControlSequence(new GenericCommand(true, 
        "currentcounter", null, new TeXCsRef("texparser@currentsection")));

      registerControlSequence(new TextualContentCommand("cmddefbookmarkleveloffset", "1"));


      registerControlSequence(new AtGobble("settabcolsep"));
      registerControlSequence(new AtGobble("tcbset"));
      registerControlSequence(new AtGobble("tcbuselibrary"));

      registerControlSequence(new GobbleOpt("RedeclareSectionCommand", 1, 1));

      registerControlSequence(new AtFirstOfOne("textsmaller"));
      registerControlSequence(new AtFirstOfOne("textlarger"));

      registerControlSequence(new Relax("nlctnovref"));
      registerControlSequence(new Relax("nlctusevref"));
      registerControlSequence(new Relax("htmlavailable"));

      registerControlSequence(new AtGobble("nlctdocatnum"));

   }

   protected void addCssStyles()
   {
      LaTeXParserListener listener = getListener();

      if (listener instanceof L2HConverter)
      {
         ((L2HConverter)listener).addCssStyle("dfn { font-style: normal; font-weight: bold; } a { text-decoration: none; } a:hover { text-decoration: underline; } div.tablefns { border-top: solid; } div.example { border-bottom: solid silver; padding: 20px; } div.example div.title { font-weight: bold; font-size: large; } .pageimage { padding: 10px; vertical-align: top; } .boolsuffix { text-decoration: underline; }");
      }
   }

   protected void addSemanticCommands()
   {
      TeXObjectList def;

      registerControlSequence(new DefSemanticCmd(this));

      colorSty.putColor("cs", FG_CS);
      colorSty.putColor("styopt", FG_STYOPT);
      colorSty.putColor("csopt", FG_CSOPT);
      colorSty.putColor("comment", FG_COMMENT);

      colorSty.putColor("style1", new Color(0.32f,0.545f,0.545f));// DarkSlateGray4
      colorSty.putColor("style2", new Color(0.21f,0.392f,0.545f));// SteelBlue4
      colorSty.putColor("style3", new Color(0f,0f,0.545f));// Blue4
      colorSty.putColor("style4", new Color(0.332f,0.1f,0.545f));// Purple4
      colorSty.putColor("style5", new Color(0.28f,0.235f,0.545f));// SlateBlue4
      colorSty.putColor("style6", new Color(0.545f,0.352f,0.17f));// Tan4

      addSemanticCommand("strong", TeXFontWeight.STRONG);
      addSemanticCommand("booktitle", TeXFontShape.EM);

      addSemanticCommand("sidenote", 
       new TeXFontText(TeXFontSize.FOOTNOTE), false, true, FloatBoxStyle.RIGHT);

      addSemanticCommand("advantagefmt", "advantage", null, Color.GREEN, null, null);
      addSemanticCommand("disadvantagefmt", "disadvantage", null, Color.RED, null, null);

      registerControlSequence(new LaTeXGenericCommand(true,
       "smcode", "m", TeXParserUtils.createStack(listener, 
         new TeXCsRef("code"),
         TeXParserUtils.createGroup(listener, listener.getParam(1)))));

      addSemanticCommand("@code", "code", 
        new TeXFontText(TeXFontFamily.VERB), null, null, null);
      addSemanticCommand("cmd", TeXFontFamily.VERB, null, listener.getOther('\\'), null);
      addSemanticCommand("cmdfmt", TeXFontFamily.TT, null, 
        listener.getOther('\\'), null);
      registerControlSequence(new InlineCode());

      addSemanticCommand("code@comment", "comment",
        null, FG_COMMENT, listener.createString("% "), null);

      registerControlSequence(new CodeComment());
      registerControlSequence(new CodeComment("commentdbsp",
        new TeXCsRef("dbspace")));

      addSemanticCommand("csfmt", TeXFontFamily.VERB, FG_CS, 
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
      addSemanticCommand("summarylocfmt", TeXFontShape.IT);

      registerControlSequence(new LaTeXGenericCommand(true, "commentnl", "m", 
        TeXParserUtils.createStack(listener, 
         new TeXCsRef("comment"),
         TeXParserUtils.createGroup(listener, listener.getParam(1)),
         new TeXCsRef("newline")
       )));

      registerControlSequence(new GenericCommand("optiondefhook"));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "menusep", 0x279C, "menu separator", true));

      registerControlSequence(new Widget("menufmt", "menu"));
      registerControlSequence(new WidgetMenu("menu", "menusep"));

      registerControlSequence(new TextualContentCommand("codebackslash", "\\"));
      registerControlSequence(new TextualContentCommand("longswitch", "--"));
      registerControlSequence(new TextualContentCommand("shortswitch", "-"));

      addSemanticCommand("cbeg", TeXFontFamily.VERB, null, 
        listener.createString("\\begin{"), listener.getOther('}'));
      addSemanticCommand("cend", TeXFontFamily.VERB, null, 
        listener.createString("\\end{"), listener.getOther('}'));

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
        null, listener.getOther(0x2329), listener.getOther(0x232A));

      registerControlSequence(new LaTeXGenericCommand(true,
        "texmeta", "m", TeXParserUtils.createStack(listener, 
         new TeXCsRef("meta"), listener.getParam(1))));

      addSemanticCommand("faded", FADED);

      addNestedSemanticCommand("initvalnotefmt", new TeXFontText(TeXFontShape.EM),
        new TeXFontText(TeXFontFamily.RM),
        Color.BLACK, null, null);

      addSemanticCommand("summarytagfmt", "summarytag", 
        new TeXFontText(TeXFontShape.IT),
        null, null, null, null, listener.createString(": "), true, false);

      // \marg
      def = listener.createStack();
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

      // \oargnobr
      def = listener.createStack();
      def.add(listener.getOther('['));
      def.add(listener.getParam(1));
      def.add(listener.getOther(']'));
      registerControlSequence(new LaTeXGenericCommand(true, "oargnobr",
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

      // \optvalrefeq
      def = listener.createStack();
      def.add(new TeXCsRef("gls"));

      grp = listener.createGroup("optval.");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp.add(listener.getOther('.'));
      grp.add(listener.getParam(2));

      def.add(new TeXCsRef("optfmt"));
      def.add(TeXParserUtils.createGroup(listener,
         listener.getOther('='), listener.getParam(3)));

      registerControlSequence(new LaTeXGenericCommand(true,
       "optvalrefeq", "mmm", def));

      // \childoptval
      def = listener.createStack();
      def.add(new TeXCsRef("gls"));

      grp = listener.createGroup("optval.");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp.add(listener.getOther('.'));
      grp.add(listener.getParam(2));

      def.add(new TeXCsRef("optfmt"));
      grp = listener.createGroup("=");
      def.add(grp);
      grp.add(listener.getParam(3));

      registerControlSequence(new LaTeXGenericCommand(true,
       "childoptval", "mmm", def));

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

      // \csmetafmt
      def = listener.createStack();
      def.add(new TeXCsRef("csfmt"));
      grp = listener.createGroup();
      def.add(grp);

      grp.add(listener.getParam(1));
      grp.add(new TeXCsRef("meta"));
      grp.add(TeXParserUtils.createGroup(listener, listener.getParam(2)));
      grp.add(listener.getParam(3));

      registerControlSequence(new LaTeXGenericCommand(true,
       "csmetafmt", "mmm", def));

      // \csmetametafmt
      def = listener.createStack();
      def.add(new TeXCsRef("csfmt"));
      grp = listener.createGroup();
      def.add(grp);

      grp.add(listener.getParam(1));
      grp.add(new TeXCsRef("meta"));
      grp.add(TeXParserUtils.createGroup(listener, listener.getParam(2)));
      grp.add(listener.getParam(3));

      grp.add(new TeXCsRef("meta"));
      grp.add(TeXParserUtils.createGroup(listener, listener.getParam(4)));
      grp.add(listener.getParam(5));

      registerControlSequence(new LaTeXGenericCommand(true,
       "csmetametafmt", "mmmmm", def));

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

      // homefilefmt
      def = listener.createStack();
      def.add(new TeXCsRef("filefmt"));
      def.add(TeXParserUtils.createGroup(listener, new TeXCsRef("homedir"),
       listener.getOther('/'), listener.getParam(1)));

      registerControlSequence(new LaTeXGenericCommand(true,
       "homefilefmt", "m", def));

      registerControlSequence(new Symbol("homedir", '~'));

      // urlfootref
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(2)));

      registerControlSequence(new LaTeXGenericCommand(true,
       "urlfootref", "mm", def));

      // \cmdnotefmt
      registerControlSequence(new AtFirstOfOne("cmdnotefmt"));

      // \pkgnotefmt
      registerControlSequence(new AtFirstOfOne("pkgnotefmt"));

      // \optnotefmt
      registerControlSequence(new AtFirstOfOne("optnotefmt"));

      // \appnotefmt
      registerControlSequence(new AtFirstOfOne("appnotefmt"));

      // \switchnotefmt
      registerControlSequence(new AtFirstOfOne("switchnotefmt"));

      // \summarynotefmt
      def = listener.createStack();
      def.add(listener.getOther('('));
      def.add(listener.getParam(1));
      def.add(listener.getOther(')'));

      registerControlSequence(new LaTeXGenericCommand(true,
       "summarynotefmt", "m", def));

      // \araraline
      def = listener.createString("% arara: ");
      def.add(listener.getParam(1));

      registerControlSequence(new LaTeXGenericCommand(true, "araraline",
       "m", def));

      // \araracont
      def = listener.createStack();
      def.add(new TeXCsRef("araraline"));
      def.add(TeXParserUtils.createGroup(listener, 
       new TeXCsRef("longswitch"), listener.getOther('>'), 
       listener.getSpace()));

      registerControlSequence(new GenericCommand(true, "araracont",
       null, def));

   }

   protected void addExtraSyntaxSemanticCommands()
   {
      TeXObjectList def;

      registerControlSequence(new ExpFunc(glossariesSty));
      registerControlSequence(new PredCs(glossariesSty));

      registerControlSequence(new TextualContentCommand("explsuffix", ""));
      registerControlSequence(new TextualContentCommand("explTFsuffix", "TF"));

      registerControlSequence(new CondCs("condcsT", "T", glossariesSty));
      registerControlSequence(new CondCs("condcsF", "F", glossariesSty));

      registerControlSequence(new GenericCommand(true,
        "TFsyntax", null, TeXParserUtils.createStack(listener, 
         new TeXCsRef("margm"), listener.createGroup("true"),
         listener.getSpace(),
         new TeXCsRef("margm"), listener.createGroup("false")
       )));

      addSemanticCommand("@explboolsyntaxfmt",
       "boolsuffix", new TeXFontText(TeXFontShape.EM),
        null, null, null, null, null, true, false);


      // \conditionsyntax
      def = listener.createStack();
      def.add(listener.getSpace());
      def.add(new TeXCsRef("meta"));
      def.add(listener.createGroup("true"));
      def.add(new TeXCsRef("csfmt"));
      def.add(listener.createGroup("else"));
      def.add(listener.getSpace());
      def.add(new TeXCsRef("meta"));
      def.add(listener.createGroup("false"));
      def.add(new TeXCsRef("csfmt"));
      def.add(listener.createGroup("fi"));

      registerControlSequence(new GenericCommand(true,
       "conditionsyntax", null, def));

   }

   protected void addCrossRefCommands()
   {
      TeXObjectList def;

      registerControlSequence(new Plabel());
      registerControlSequence(new Pref());

      // \phyperref
      def = listener.createStack();
      def.add(new TeXCsRef("hyperref"));
      def.add(listener.getOther('['));
      def.add(listener.getParam(2));
      def.add(listener.getOther(']'));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      registerControlSequence(new LaTeXGenericCommand(true, "phyperref",
       "mm", def));

      registerControlSequence(new Symbol("sectionrefprefix", 0x00A7));
      registerControlSequence(new TextualContentCommand(
        "sectionsrefprefix", "\u00A7\u00A7"));

      registerControlSequence(new Symbol("Sectionrefprefix", 0x00A7));
      registerControlSequence(new TextualContentCommand(
        "Sectionsrefprefix", "\u00A7\u00A7"));

      registerControlSequence(new TextualContentCommand(
        "refslistsep", ", "));

      registerControlSequence(new TextualContentCommand(
        "refslistlastsep", " & "));

      // \sectionref
      registerControlSequence(new Ref("sectionref", new TeXCsRef("sectionrefprefix")));

      registerControlSequence(new RefsList("sectionsref",
        new TeXCsRef("sectionsrefprefix"), 
        new TeXCsRef("refslistsep"),
        new TeXCsRef("refslistlastsep")
      ));

      // \Sectionref
      registerControlSequence(new Ref("Sectionref", new TeXCsRef("Sectionrefprefix")));

      registerControlSequence(new RefsList("Sectionsref",
        new TeXCsRef("Sectionsrefprefix"), 
        new TeXCsRef("refslistsep"),
        new TeXCsRef("refslistlastsep")
      ));

      registerControlSequence(new TextualContentCommand("examplerefprefix", "Example "));
      registerControlSequence(new TextualContentCommand("Examplerefprefix", "Example "));

      // \exampleref
      registerControlSequence(new Ref("exampleref", false, 
         new TeXCsRef("examplerefprefix")));

      // \Exampleref
      registerControlSequence(new Ref("Exampleref", false, 
         new TeXCsRef("Examplerefprefix")));

      registerControlSequence(new TextualContentCommand("examplesrefprefix", "Examples "));
      registerControlSequence(new TextualContentCommand("Examplesrefprefix", "Examples "));

      registerControlSequence(new RefsList("examplesref",
        new TeXCsRef("examplesrefprefix"), 
        new TeXCsRef("refslistsep"),
        new TeXCsRef("refslistlastsep")
      ));

      registerControlSequence(new RefsList("Examplesref",
        new TeXCsRef("Examplesrefprefix"), 
        new TeXCsRef("refslistsep"),
        new TeXCsRef("refslistlastsep")
      ));

      registerControlSequence(new MExampleRef());
      registerControlSequence(new MExampleRef("mexampleref", false));
      registerControlSequence(new ExampleMarginRef());

      def = listener.createStack();
      def.add(new TeXCsRef("exampleref"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
      def.add(listener.getSpace());
      def.add(listener.getOther('('));
      def.add(new TeXCsRef("nameref"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
      def.add(listener.getOther(')'));

      registerControlSequence(new LaTeXGenericCommand(true, "examplenameref",
       "m", def));

      // \tableref
      registerControlSequence(new TextualContentCommand("tablerefprefix", "Table "));
      registerControlSequence(new TextualContentCommand("Tablerefprefix", "Table "));

      registerControlSequence(new Ref("tableref", false,
       new TeXCsRef("tablerefprefix")));
      registerControlSequence(new Ref("Tableref", false,
       new TeXCsRef("Tablerefprefix")));

      registerControlSequence(new TextualContentCommand("tablesrefprefix", "Tables "));
      registerControlSequence(new TextualContentCommand("Tablesrefprefix", "Tables "));

      registerControlSequence(new RefsList("tablesref",
        new TeXCsRef("tablesrefprefix"), 
        new TeXCsRef("refslistsep"),
        new TeXCsRef("refslistlastsep")
      ));

      registerControlSequence(new RefsList("Tablesref",
        new TeXCsRef("Tablesrefprefix"), 
        new TeXCsRef("refslistsep"),
        new TeXCsRef("refslistlastsep")
      ));

      // \figureref
      registerControlSequence(new TextualContentCommand("figurerefprefix", "Figure "));
      registerControlSequence(new TextualContentCommand("Figurerefprefix", "Figure "));

      registerControlSequence(new Ref("figureref", false,
       new TeXCsRef("figurerefprefix")));
      registerControlSequence(new Ref("Figureref", false,
       new TeXCsRef("Figurerefprefix")));

      registerControlSequence(new TextualContentCommand("figuresrefprefix", "Figures "));
      registerControlSequence(new TextualContentCommand("Figuresrefprefix", "Figures "));

      registerControlSequence(new RefsList("figuresref",
        new TeXCsRef("figuresrefprefix"), 
        new TeXCsRef("refslistsep"),
        new TeXCsRef("refslistlastsep")
      ));

      registerControlSequence(new RefsList("Figuresref",
        new TeXCsRef("Figuresrefprefix"), 
        new TeXCsRef("refslistsep"),
        new TeXCsRef("refslistlastsep")
      ));

      registerControlSequence(new Option());
      registerControlSequence(new Options());
      registerControlSequence(new Options("optionsor", "or"));
      registerControlSequence(new OptionsTo());

      registerControlSequence(new AtGobble("GetTitleStringSetup"));
   }

   protected void addFootnoteCommands()
   {
      TeXObjectList def;
      Group grp;

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
        new UserDimension(80.0f, new PercentUnit(PercentUnit.LINE_WIDTH)));

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
      registerControlSequence(new FnSym());

      // \fnsymmark
      registerControlSequence(new AtFirstOfOne("fnsymmark"));

      registerControlSequence(new FnSymMarker());

   }

   protected void addDocRefCommands()
   {
      TeXObjectList def;
      Group grp;

      registerControlSequence(new MirrorSampleFile());

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

      // \texfaq
      def = listener.createStack();
      def.add(new TeXCsRef("href"));
      grp = listener.createGroup("https://texfaq.org/");
      def.add(grp);
      grp.add(listener.getParam(1));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(2));

      registerControlSequence(new LaTeXGenericCommand(true,
       "texfaq", "mm", def));

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

      // \galleryurl
      def = listener.createStack();
      def.add(new TeXCsRef("dickimawhref"));

      grp = listener.createGroup("gallery/");
      grp.add(listener.getParam(1));
      def.add(grp);

      grp = listener.createGroup("dickimaw-books.com/gallery/");
      grp.add(listener.getParam(1));
      def.add(grp);

      registerControlSequence(new LaTeXGenericCommand(true,
       "galleryurl", "m", def));

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
      grp = listener.createGroup("faqs.php?category=");
      def.add(grp);
      grp.add(listener.getParam(1));

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

   }

   protected void addSymbolCommands()
   {
      TeXObjectList def;
      Group grp;

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "tabsym", 0x21B9, "TAB", true));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "upsym", 0x2B71, "Up", true));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "unlimited", 0x221E));
      registerControlSequence(AccSuppObject.createSymbol(
        listener, "tick", 0x2713));
      registerControlSequence(AccSuppObject.createSymbol(
        listener, "yes", 0x2714));
      registerControlSequence(AccSuppObject.createSymbol(
        listener, "no", 0x2716));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "asteriskmarker", 0x2217, // centred asterisk
        "asterisk marker"));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "daggermarker", 0x2020, // dagger
        "dagger marker"));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "doubledaggermarker", 0x2021, // double dagger
        "double dagger marker"));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "sectionmarker", 0x00A7, // section
        "section marker"));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "lozengemarker", 0x29EB, // lozenge
        "lozenge marker"));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "pilcrowmarker", 0x00B6, // pilcrow
        "pilcrow marker"));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "hashmarker", '#', // hash
        "hash marker"));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "referencemarker", 0x203B, // reference mark
        "reference marker"));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "vdoubleasteriskmarker", 0x2051,
        "vertical double asterisk marker"));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "starmarker", 0x2605, // star
        "star marker"));

      registerControlSequence(AccSuppObject.createSymbol(
        listener, "florettemarker", 0x273E, // six petalled B&W florette
        "florette marker"));

      // don't bother with thin spaces for these commands
      registerControlSequence(new Symbol("dash", 0x2014));
      registerControlSequence(new Symbol("Slash", '/'));

      registerControlSequence(new Symbol("nlctopensqbracket", '['));
      registerControlSequence(new Symbol("nlctclosesqbracket", ']'));

      registerControlSequence(new Symbol("nlctopenparen", '('));
      registerControlSequence(new Symbol("nlctcloseparen", ')'));

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

      registerControlSequence(new TextualContentCommand("terminalsym", "\u232A_"));
      registerControlSequence(new Symbol("transcriptsym", 0x1F50E));

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

   }

   protected void addGlsIconCommands()
   {
      TeXObjectList def;
      Group grp;

      // \icon
      def = listener.createStack();
      def.add(new TeXCsRef("stepcounter"));
      def.add(listener.createGroup("icon"));
      def.add(new TeXCsRef("glssymbol"));
      def.addAll(listener.createString("[counter=icon]"));
      grp = listener.createGroup("sym.");
      def.add(grp);
      grp.add(listener.getParam(1));

      registerControlSequence(new LaTeXGenericCommand(true,
       "icon", "m", def));

      // \icontext
      def = listener.createStack();
      def.add(new TeXCsRef("stepcounter"));
      def.add(listener.createGroup("icon"));
      def.add(new TeXCsRef("glstext"));
      def.addAll(listener.createString("[counter=icon]"));
      grp = listener.createGroup("sym.");
      def.add(grp);
      grp.add(listener.getParam(1));

      registerControlSequence(new LaTeXGenericCommand(true,
       "icontext", "m", def));

      listener.newcounter("icon");

   }

   protected void addDiscretionaryCommands()
   {
      registerControlSequence(new TextualContentCommand("dhyphen", "-"));
      registerControlSequence(new TextualContentCommand("dcolon", ":"));
      registerControlSequence(new TextualContentCommand("dcomma", ","));
      registerControlSequence(new TextualContentCommand("dequals", "="));
      registerControlSequence(new TextualContentCommand("dfullstop", "."));
      registerControlSequence(new TextualContentCommand("dunderscore", "_"));
      registerControlSequence(new TextualContentCommand("dsb", "_"));

   }

   protected void addTextCommands()
   {
      registerControlSequence(new TextualContentCommand("TeXLive", "TeX Live"));
      registerControlSequence(new TextualContentCommand("MikTeX", "MikTeX"));

      registerControlSequence(new TextualContentCommand("optionlistprefix", "opt."));
      registerControlSequence(new TextualContentCommand("optionlisttag", "Option"));
      registerControlSequence(new TextualContentCommand("optionlisttags", "Options"));

   }

   protected void addListCommands()
   {
      TeXObjectList def;

      registerControlSequence(new DefListDec());
      registerControlSequence(new ItemDesc());

      // \optionlistitemformat
      def = listener.createStack();
      def.add(new TeXCsRef("glsentrytext"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      registerControlSequence(new LaTeXGenericCommand(true, "optionlistitemformat",
       "m", def));
   }

   protected void addBoxCommands()
   {
      addBasicBoxCommands();
      addStandaloneDefCommands();
      addCodeBoxCommands();
      addSummaryBoxCommands();
      addIndexBoxCommands();
   }

   protected void addBasicBoxCommands()
   {
      addTaggedColourBox("important", BG_IMPORTANT, FRAME_COL_IMPORTANT);
      addTaggedColourBox("warning", BG_WARNING, FRAME_COL_WARNING);
      addTaggedColourBox("information", BG_INFO, FRAME_COL_INFO);

      terminalBox = 
        addTaggedColourBox("terminal", new TeXFontText(TeXFontFamily.VERB), 
           BG_TERMINAL, Color.BLACK);

      transcriptBox = 
        addTaggedColourBox("transcript", new TeXFontText(TeXFontFamily.VERB), 
           BG_TERMINAL, Color.BLACK);

      rightBox = addFloatBox("floatrightbox");

      noteBox = new ColourBox("noteBox", BorderStyle.NONE,
        AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, false, null, null);
      listener.declareFrameBox(noteBox, false);

   }

   protected FrameBoxEnv createPinnedBox()
   {
      return addTaggedColourBox("pinnedbox", "definition", BG_DEF, Color.BLACK);
   }

   protected FrameBoxEnv createSettingsBox()
   {
      return addTaggedColourBox("settingsbox",
         "valuesetting", BG_OPTION_DEF, Color.BLACK);
   }

   protected void addStandaloneDefCommands()
   {
      TeXObjectList def;
      Group grp, subgrp;

      pinnedBox = createPinnedBox();

      settingsBox = createSettingsBox();

      ctrBox = addTaggedColourBox("ctrbox", "counter", BG_DEF, Color.BLACK);

      registerControlSequence(new CmdDef(pinnedBox, rightBox, noteBox, glossariesSty));
      registerControlSequence(new EnvDef(pinnedBox, rightBox, noteBox, glossariesSty));
      registerControlSequence(new CtrDef(ctrBox, rightBox, noteBox, glossariesSty));
      registerControlSequence(new PkgDef(pinnedBox, rightBox, noteBox, glossariesSty));
      registerControlSequence(new ClsDef(pinnedBox, rightBox, noteBox, glossariesSty));

      registerControlSequence(new OptionDef(settingsBox, rightBox, noteBox, glossariesSty));

      optValBox = addTaggedColourBox("optionvaluebox",
         "optionvalue", BG_OPTION_VALUE_DEF, Color.BLACK);

      registerControlSequence(new OptionValDef(optValBox, rightBox, noteBox, glossariesSty));

      registerControlSequence(new AppDef(terminalBox, rightBox, noteBox, glossariesSty));
      registerControlSequence(new SwitchDef(settingsBox, rightBox, noteBox, glossariesSty));



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

   }

   protected void addCodeBoxCommands()
   {
      codeBox = addTaggedColourBox("codebox",
         "code", new TeXFontText(TeXFontFamily.VERB), BG_CODE, Color.BLACK);

      registerControlSequence(new DuplicateEnv("codebox*", codeBox));

      registerControlSequence(new TextualContentCommand("codepar", 
       String.format("%n")));

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
         new UserDimension(3.0f, new PercentUnit()), // right outer margin
         new UserDimension(), // top outer margin
         null, // bottom outer margin
         AlignHStyle.DEFAULT, AlignVStyle.TOP, 
         new UserDimension(47.0f, new PercentUnit()));

      FrameBox crr = addSemanticCommand("@sidebysideresult", "sidebysideresult",
         null, (Color)null, null, Color.BLACK, null, null, true, true, 
         null, // left outer margin
         null, // right outer margin
         new UserDimension(), // top outer margin
         null, // bottom outer margin
         AlignHStyle.DEFAULT, AlignVStyle.TOP, 
         new UserDimension(47.0f, new PercentUnit()));

      CodeResult codeResult = new CodeResult(
         new ColourBox("frame@coderesult@title", BorderStyle.NONE,
          AlignHStyle.CENTER, AlignVStyle.DEFAULT, false, true, null, null),
        crc, crr);

      registerControlSequence(codeResult);

      registerControlSequence(new DuplicateEnv("coderesult*", codeResult));

      CodeResult uniCodeResult = new CodeResult("unicoderesult",
         new ColourBox("frame@unicoderesult@title", BorderStyle.NONE,
          AlignHStyle.CENTER, AlignVStyle.DEFAULT, false, true, null, null),
        crc, crr, "unicode");

      registerControlSequence(uniCodeResult);

      registerControlSequence(new DuplicateEnv("unicoderesult*", uniCodeResult));
   }

   protected void addSummaryBoxCommands()
   {
      optionSummaryBox = addColourBox("optionsummarybox", null, null,
        BG_DEF, Color.BLACK);

      optionValueSummaryBox = addSemanticCommand("optionvaluesummarybox",
         new UserDimension(40, FixedUnit.BP));

      defnBox = addColourBox("defnbox", null, null,
        BG_DEF, Color.BLACK);

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

      registerControlSequence(new SummaryCommandOptionBox(
        "summaryglossentrypackageoption", defnBox, 
        rightBox, noteBox, glossariesSty));

      registerControlSequence(new SummaryCommandOptionBox(
        "summaryglossentryclassoption", defnBox, 
        rightBox, noteBox, glossariesSty));

      registerControlSequence(new SummaryOptionValueBox(optionValueSummaryBox, 
        rightBox, noteBox, glossariesSty));

      registerControlSequence(new SummaryPackageBox(defnBox, 
        rightBox, noteBox, glossariesSty));

      registerControlSequence(new SummaryClassBox(defnBox, 
        rightBox, noteBox, glossariesSty));
   }

   protected void addIndexBoxCommands()
   {
      createIndexItemBox(0);
      createIndexItemBox(1);
      createIndexItemBox(2);
      createIndexItemBox(3);

      addColourBox("nlctusernavbox", null, null, null, null);
      addSemanticCommand("texparser@abstractheader", "abstractheader", 
        new TeXFontText(TeXFontWeight.BF), null, null, null, null, null, 
         false, false, null, AlignHStyle.CENTER);
   }

   protected void addInlineDefCommands()
   {
      registerControlSequence(new InlineGlsDef(glossariesSty));
      registerControlSequence(new InlineGlsDef("inlineidxdef", "idx.", glossariesSty));
      registerControlSequence(new InlineGlsDef("inlineidxfdef", "idx.",
        "first", true, glossariesSty));
      registerControlSequence(new InlineGlsDef("inlineidxpdef", "idx.",
        "plural", true, glossariesSty));

      registerControlSequence(new InlineGlsDef("Inlineidxdef", "idx.",
        CaseChange.SENTENCE, glossariesSty));

      registerControlSequence(new InlineGlsDef("inlineswitchdef", "switch.", glossariesSty));
      registerControlSequence(new InlineGlsDef("inlineoptdef", "opt.", glossariesSty));
      registerControlSequence(new InlineGlsDef("inlinepkgdef", "pkg.", glossariesSty));
      registerControlSequence(new InlineGlsDef("inlineappdef", "app.", glossariesSty));
      registerControlSequence(new InlineGlsDef("inlinefiledef", "file.", glossariesSty));
      registerControlSequence(new CmdDefSyntax(glossariesSty));
      registerControlSequence(new OptDefSyntax(glossariesSty));

   }

   protected void addExampleCommands()
   {
      TeXObjectList def;
      Group grp;

      registerControlSequence(new GenericCommand(true, "examplesdir", null, 
         new TeXObject[] {new TeXCsRef("jobname"), 
            listener.createString("-examples")}));

      listener.newcounter("example");

      registerControlSequence(new TextualContentCommand(
         "examplename", "Example"));
      registerControlSequence(new TextualContentCommand(
         "listofexamplesname", "List of Examples"));

      registerControlSequence(new GenericCommand(true,
        "listofexampleslabel", null, TeXParserUtils.createStack(listener,
          new TeXCsRef("label"), listener.createGroup("sec:listofexamples"))));

      registerControlSequence(new ListOfExamples());
      registerControlSequence(new ListOfExamplesHeader());

      registerControlSequence(new GenericCommand(true,
        "nlctexampletag", null, TeXParserUtils.createStack(listener,
          new TeXCsRef("examplename"), listener.getSpace(),
          new TeXCsRef("theexample"))));

      registerControlSequence(new CreateExample(this));
      registerControlSequence(new ExampleFileBaseName());
      registerControlSequence(new ExampleEnv());

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

      registerControlSequence(
        new TextualContentCommand("nl", String.format("%n")));
      registerControlSequence(
        new TextualContentCommand("nlsp", String.format("%n ")));
      registerControlSequence(
        new TextualContentCommand("dbspace", String.format("  ")));
      registerControlSequence(
        new TextualContentCommand("dbdbspace", String.format("    ")));
      registerControlSequence(
        new TextualContentCommand("dbdbdbspace", String.format("      ")));
      registerControlSequence(
        new TextualContentCommand("nldbsp", String.format("%n  ")));
      registerControlSequence(
        new TextualContentCommand("nldbdbsp", String.format("%n    ")));
      registerControlSequence(
        new TextualContentCommand("nldbdbdbsp", String.format("%n      ")));

      getParser().getSettings().newcount(true,
        "l_nlctdoc_extag_item_threshold_int", 4);

      registerControlSequence(new ExampleTagRef(this));
      registerControlSequence(new AtRefAtNumName());
      registerControlSequence(new AtRefAtNumName("s@ref@numname", true));

      getListener().addAuxCommand(new AuxCommand("nlctdoc@extag", 2));

      registerControlSequence(new TextualContentCommand("exampletagreflistsep",
        ";"));
      registerControlSequence(new TextualContentCommand("exampletagreflistpretitle",
        ". "));
      registerControlSequence(new TextualContentCommand("exampletagrefprelist",
        "the following examples:"));
   }

   @Override
   public void documentBegun(BeginDocumentEvent evt)
    throws IOException
   {
      Vector<AuxData> auxData = evt.getListener().getAuxData();
      TeXObjectList stack = evt.getStack();

      for (AuxData d : auxData)
      {
         if (d.getName().equals("nlctdoc@extag"))
         {
            TeXObject tagArg = d.getArg(0);
            TeXObject labelArg = d.getArg(0);

            String tag = getParser().expandToString(tagArg, stack);
            String label = getParser().expandToString(labelArg, stack);

            if (tagGroups == null)
            {
               tagGroups = new HashMap<String,Vector<String>>();
            }

            Vector<String> list = tagGroups.get(tag);

            if (list == null)
            {
               list = new Vector<String>();
               tagGroups.put(tag, list);
            }

            list.add(label);
         }
      }
   }

   public Vector<String> getTagGroup(String tag)
   {
      if (tagGroups == null)
      {
         return null;
      }

      return tagGroups.get(tag);
   }

   protected void addLocationCommands()
   {
      addSemanticCommand("crossreftag", TeXFontShape.IT);

      addSemanticCommand("crossref", "crossref", null, null, null, null,
       null, null, false, true, new UserDimension(1, FixedUnit.EM), null,
       null, null, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, null, null
      );

      listener.addLaTeXCommand(true, "glsseeformat", true,
       3, new TeXCsRef("seename"), TeXParserUtils.createStack(listener,
        new TeXCsRef("crossref"), 
        TeXParserUtils.createGroup(listener, 
          new TeXCsRef("crossreftag"), 
           TeXParserUtils.createGroup(listener, listener.getParam(1)),
          listener.getSpace(),
          new TeXCsRef("glsseelist"), 
           TeXParserUtils.createGroup(listener, listener.getParam(2))
        )
      ));

      listener.addLaTeXCommand("seclocfmt", true, 2, null, 
        TeXParserUtils.createStack(listener, 
         listener.getOther(0xA7),
         listener.getParam(1)));

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

      listener.addLaTeXCommand("glsxtrtablelocfmt", true, 2, null, 
        TeXParserUtils.createStack(listener, 
         new TeXCsRef("tablename"), listener.getSpace(),
         listener.getParam(1)));

      listener.addLaTeXCommand("glsxtrfigurelocfmt", true, 2, null, 
        TeXParserUtils.createStack(listener, 
         new TeXCsRef("figurename"), listener.getSpace(),
         listener.getParam(1)));

      registerControlSequence(new TextualContentCommand("bibglslocationgroupsep",
       "; "));

   }

   protected void addPrintCommands()
   {
      NewIf.createConditional(true, getParser(), "ifshowsummarytopgroupheaders", true);

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


      registerControlSequence(
        new TextualContentCommand("idxenvname", "environment"));

      registerControlSequence(
        new TextualContentCommand("idxpackagename", "package"));

      registerControlSequence(
        new TextualContentCommand("idxclassname", "class"));

      registerControlSequence(
        new TextualContentCommand("idxcountername", "counter"));
   }

   protected void addBib2GlsCommands()
   {
      // provide these commands in case they are redefined in the
      // document, but they're for bib2gls so they can be ignored
      registerControlSequence(new GenericCommand("nlctuserguidecustomentryaliases"));
      registerControlSequence(new GenericCommand("nlctuserguideignoredpuncrules"));
      registerControlSequence(new GenericCommand("nlctuserguidepuncrules"));
      registerControlSequence(new GenericCommand("nlctuserguidepreletterrules"));
      registerControlSequence(new GenericCommand("nlctuserguideletterrules"));
      registerControlSequence(new GenericCommand("nlctuserguideextrarules"));
      registerControlSequence(new GenericCommand("nlctuserguidebibextrapreamble"));

      if (atSymGroup)
      {
         registerControlSequence(new SymbolGroupLabel("bibglsothergroup"));
         registerControlSequence(new SymbolGroupTitle("bibglsothergrouptitle"));
      }

   }

   protected void addGlsCommands()
   {
      TeXObjectList def;
      Group grp;

      registerControlSequence(new MainGlsAdd(glossariesSty));

      registerControlSequence(new GenericCommand(true, "mainfmt", null,
         new TeXCsRef("glsnumberformat")));

      addSemanticCommand("termslocfmt", TeXFontShape.IT);

      registerControlSequence(new GenericCommand(true, "glsaddterm", null,
         new TeXCsRef("glsadd")));

      // \\glscmd
      def = listener.createStack();
      def.add(new TeXCsRef("glsentrytext"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      registerControlSequence(new LaTeXGenericCommand(true, "glscmd", "m", def));


      // \\glscsname
      def = listener.createStack();
      def.add(new TeXCsRef("glslink"));
      def.add(listener.getOther('['));
      def.add(listener.getParam(1));
      def.add(listener.getOther(']'));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(2)));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("csfmtfont"));
      grp.add(TeXParserUtils.createGroup(listener, listener.getParam(2)));

      registerControlSequence(new LaTeXGenericCommand(true, "glscsname",
        "om", def, TeXParserUtils.createStack(listener, listener.createStack())));


      addGlsFmtTextCommand("filetext", "file.");
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

      registerControlSequence(new Dglsfield("sym", glossariesSty, CaseChange.NO_CHANGE,
        "symbol"));

      registerControlSequence(new Dgls("idx", CaseChange.NO_CHANGE, glossariesSty));
      registerControlSequence(new Dgls("idxpl", 
       CaseChange.NO_CHANGE, true, glossariesSty));
      registerControlSequence(new Dgls("Idx", CaseChange.SENTENCE, glossariesSty));
      registerControlSequence(new Dgls("Idxpl", CaseChange.SENTENCE, true, glossariesSty));
      registerControlSequence(new Dglslink("idxc", false, glossariesSty));

      registerControlSequence(new Dglsfield("idxn", glossariesSty, 
         CaseChange.NO_CHANGE, "name"));
      registerControlSequence(new Dglsfield("idxf", glossariesSty, 
         CaseChange.NO_CHANGE, "first"));

      // dual prefix list
      def = listener.createString("dual.,idx.,idx.sym.,");
        def.add(listener.getControlSequence("empty"));
      registerControlSequence(new GenericCommand(true, "@glsxtr@labelprefixes",
       null, def));

      registerControlSequence(new InitValRef(glossariesSty));
      registerControlSequence(new InitValOpt(glossariesSty));

      // \aliasref
      def = listener.createStack();
      def.add(new TeXCsRef("glshyperlink"));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(listener.getParam(1));

      registerControlSequence(new LaTeXGenericCommand(true,
       "aliasref", "m", def));

      registerControlSequence(new TextualContentCommand(
         "glsxtrpostdescdualindexabbreviation", "."));

      // \starredcs (simplified definition)
      def = listener.createStack();
      def.add(new TeXCsRef("glslink"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
      grp = listener.createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("csfmt"));
      grp.add(TeXParserUtils.createGroup(listener, listener.getParam(1),
        listener.getOther('*')));

      registerControlSequence(new LaTeXGenericCommand(true, "starredcs",
       "m", def));

      // \starredenv (simplified definition)

      def = listener.createStack();
      def.add(new TeXCsRef("glslink"));

      grp = listener.createGroup("env.");
      grp.add(listener.getParam(1));
      def.add(grp);

      grp = listener.createGroup();
      def.add(grp);

      grp.add(new TeXCsRef("envfmt"));
      grp.add(TeXParserUtils.createGroup(listener,
        listener.getParam(1), listener.getOther('*')));

      registerControlSequence(new LaTeXGenericCommand(true, "starredenv",
       "m", def));

      registerControlSequence(new TheCtr());
      registerControlSequence(new TheCtr("theHctr", "theH"));
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
      getListener().requirepackage(null, "twemojis", false, stack);
      getListener().requirepackage(null, "fontawesome", false, stack);
      getListener().requirepackage(null, "hyperref", false, stack);

      KeyValList options = new KeyValList();

      options.put("record", getListener().createString("nameref"));
      options.put("indexcounter", null);
      options.put("index", null);
      options.put("symbols", null);
      options.put("floats", null);
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
      else if (option.equals("draft"))
      {
         draft = true;
      }
      else if (option.equals("final"))
      {
         draft = false;
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

      TeXParserListener listener = getListener();

      TeXObjectList list = listener.createStack();
      list.add(listener.getControlSequence("setabbreviationstyle"));
      list.add(listener.getOther('['));
      list.add(listener.createString("termabbreviation"), true);
      list.add(listener.getOther(']'));
      list.add(listener.createGroup("long-short-desc"));

      list.add(listener.getControlSequence("setabbreviationstyle"));
      list.add(listener.getOther('['));
      list.add(listener.createString("termacronym"), true);
      list.add(listener.getOther(']'));
      list.add(listener.createGroup("short-nolong-desc"));

      // redefine glsxtrshortdescname
      list.add(new TeXCsRef("renewcommand"));
      list.add(new TeXCsRef("glsxtrshortdescname"));

      Group def = listener.createGroup();
      list.add(def);

      def.add(new TeXCsRef("protect"));
      def.add(new TeXCsRef("glslongfont"));
      def.add(TeXParserUtils.createGroup(listener,
        new TeXCsRef("the"), new TeXCsRef("glslongtok")));

      def.add(new TeXCsRef("protect"));
      def.add(new TeXCsRef("glsxtrfullsep"));
      def.add(TeXParserUtils.createGroup(listener,
        new TeXCsRef("the"), new TeXCsRef("glslabeltok")));

      def.add(new TeXCsRef("protect"));
      def.add(new TeXCsRef("glsxtrparen"));
      def.add(TeXParserUtils.createGroup(listener,
        new TeXCsRef("protect"), new TeXCsRef("glsabbrvfont"),
        TeXParserUtils.createGroup(listener, 
          new TeXCsRef("the"), new TeXCsRef("glsshorttok"))));

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
      list.add(new TeXCsRef("icon"));
      list.add(listener.createGroup(tag));

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

      TaggedColourBox taggedBox = createTaggedColourBox(boxFrame, titleFrame, tag);
      registerControlSequence(taggedBox);

      return taggedBox;
   }

   protected TaggedColourBox createTaggedColourBox(
    FrameBox boxFrame, FrameBox titleFrame, TeXObject tag)
   {
      return new TaggedColourBox(boxFrame, titleFrame, tag);
   }

   protected void createIndexItemBox(int level)
   { 
      FrameBox idxBox = new FrameBox("nlctuserguideidx"+level,
       BorderStyle.NONE, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, false);

      idxBox.setOuterMarginLeft(new UserDimension(level*20, FixedUnit.BP));

      getListener().declareFrameBox(idxBox, false);
   }

   public GlossariesSty getGlossariesSty()
   {
      return glossariesSty;
   }

   public ColorSty getColorSty()
   {
      return colorSty;
   }

   public boolean isDraft()
   {
      return draft;
   }

   protected GlossariesSty glossariesSty;
   protected ColorSty colorSty;

   protected FrameBoxEnv terminalBox;
   protected FrameBoxEnv transcriptBox; 
   protected FrameBoxEnv ctrBox;
   protected FrameBoxEnv codeBox;
   protected FrameBoxEnv pinnedBox;

   protected FrameBox defnBox;
   protected FrameBox optionSummaryBox;
   protected FrameBox optionValueSummaryBox;
   protected FrameBox rightBox;
   protected FrameBox noteBox;

   protected FrameBoxEnv settingsBox;
   protected TaggedColourBox optValBox;

   protected boolean draft=false;

   protected boolean atSymGroup = true;

   protected HashMap<String,Vector<String>> tagGroups;

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

   public static final Color FADED = Color.GRAY;

   public static final String ERROR_UNKNOWN_TAG_GROUP = "nlctdoc.unknown_tag_group";
}
