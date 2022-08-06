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
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.NewIf;
import com.dickimawbooks.texparserlib.primitives.IfFalse;
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.primitives.Undefined;
import com.dickimawbooks.texparserlib.generic.TeXParserSetUndefAction;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.hyperref.HyperTarget;
import com.dickimawbooks.texparserlib.latex.hyperref.HyperLink;

/**
 * Limited support for the glossaries and glossaries-extra packages. They are
 * both far to large to comprehensively emulate. The aim is to
 * simply gather basic information and provide some limited
 * formatting support. 
 *
 * Note that texparserlib.latex.glossaries.* isn't used with bib2gls, which
 * uses texparserlib.bib.* to parse bib files. The glossary entries
 * in that case are sub-classes of BibEntry.
 */

public class GlossariesSty extends LaTeXSty
{
   public GlossariesSty(KeyValList options, 
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      this(options, "glossaries", listener, loadParentOptions);
   }

   public GlossariesSty(KeyValList options, String name, 
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      super(options, name, listener, loadParentOptions);

      if (name.equals("glossaries-extra"))
      {
         extra = true;
      }

      entries = new HashMap<String,GlossaryEntry>();
      glossaries = new HashMap<String,Glossary>();

      glossaryTypes = new Vector<String>();
      ignoredGlossaryTypes = new Vector<String>();

      expandField = new HashMap<String,Boolean>();
      expandField.put("name", Boolean.FALSE);
      expandField.put("description", Boolean.FALSE);
      expandField.put("descriptionplural", Boolean.FALSE);
      expandField.put("symbol", Boolean.FALSE);
      expandField.put("symbolplural", Boolean.FALSE);
      expandField.put("sort", Boolean.FALSE);

      knownFields = new Vector<String>();
      addField("name");
      addField("text");
      addField("plural");
      addField("first");
      addField("firstplural");
      addField("symbol");
      addField("symbolplural");
      addField("description", "desc");
      addField("descriptionplural", "descplural");
      addField("sort", "sortvalue");
      addField("counter");
      addField("level");
      addField("type", new TeXCsRef("glsdefaulttype"));
      addField("user1", "useri");
      addField("user2", "userii");
      addField("user3", "useriii");
      addField("user4", "useriv");
      addField("user5", "userv");
      addField("user6", "uservi");
      addField("short");
      addField("shortplural", "shortpl");
      addField("long");
      addField("longplural", "longpl");

      if (extra)
      {
         extraInit();
      }
   }

   protected void extraInit() throws IOException
   {
      addField("category", getListener().createString("general"));
   }

   @Override
   public void addDefinitions()
   {
      registerControlSequence(new TextualContentCommand("glssymbolsgroupname",
        "Symbols"));
      registerControlSequence(new TextualContentCommand("glsnumbersgroupname",
        "Numbers"));
      registerControlSequence(new TextualContentCommand("glscounter", "page"));

      registerControlSequence(new TextualContentCommand("glsdefaulttype", "main"));
      registerControlSequence(new GenericCommand(true, "acronymtype", 
              null, new TeXCsRef("glsdefaulttype")));

      registerControlSequence(new TextualContentCommand("glossaryname", "Glossary"));
      registerControlSequence(new TextualContentCommand("acronymname", "Acronyms"));


      registerControlSequence(new NewGlossaryStyle());
      registerControlSequence(new SetGlossaryStyle());
      registerControlSequence(new GlossEntryField("glossentryname",
         "name", this));
      registerControlSequence(new GlossEntryField("glossentrydesc",
         "description", this));
      registerControlSequence(new GlossEntryField("glossentrysymbol",
         "symbol", this));

      registerControlSequence(new GlossEntryField("Glossentryname",
         "name", CaseChange.SENTENCE, this));
      registerControlSequence(new GlossEntryField("Glossentrydesc",
         "description", CaseChange.SENTENCE, this));

      registerControlSequence(new PrintGlossary(this));
      registerControlSequence(new PrintGlossaries(this));
      registerControlSequence(new TextualContentCommand("glsresetentrylist", ""));
      registerControlSequence(new AtFirstOfOne("glsnamefont"));
      registerControlSequence(new AtFirstOfOne("glossaryentrynumbers"));
      registerControlSequence(new SetEntryCounter());
      registerControlSequence(new GenericCommand(true, "glsnumberformat", null,
        new TeXCsRef("glshypernumber")));
      registerControlSequence(new GlsHyperNumber());
      registerControlSequence(new HyperFont("hyperrm", "textrm"));
      registerControlSequence(new HyperFont("hypersf", "textsf"));
      registerControlSequence(new HyperFont("hypertt", "texttt"));
      registerControlSequence(new HyperFont("hyperem", "emph"));
      registerControlSequence(new HyperFont("hyperit", "textit"));
      registerControlSequence(new HyperFont("hypersl", "textsl"));
      registerControlSequence(new HyperFont("hypersc", "textsc"));
      registerControlSequence(new HyperFont("hyperbf", "textbf"));
      registerControlSequence(new HyperFont("hypermd", "textmd"));

      registerControlSequence(new TextualContentCommand("glossarytitle", ""));
      registerControlSequence(new TextualContentCommand("glossarytoctitle", ""));
      registerControlSequence(new TextualContentCommand("glossarypreamble", ""));
      registerControlSequence(new TextualContentCommand("glossarypostamble", ""));
      registerControlSequence(new TextualContentCommand("delimR", "\u2013"));
      registerControlSequence(new TextualContentCommand("delimN", ", "));

      registerControlSequence(new GlsNoIdxDisplayLoc());

      if (getParser().getControlSequence("glossentry") == null)
      {
         registerControlSequence(new TextualContentCommand("glossaryheader", ""));
         registerControlSequence(new AtGobble("glsgroupheading"));
         registerControlSequence(new DescriptionDec("theglossary"));
         registerControlSequence(new GlossEntry(this));
         registerControlSequence(new SubGlossEntry(this));
      }

      registerControlSequence(new TextualContentCommand("glolinkprefix", "glo:"));
      registerControlSequence(new TextualContentCommand("glspluralsuffix", "s"));

      boolean isHyper = listener.isStyLoaded("hyperref");

      NewIf.createConditional(true, getParser(), "ifKV@glslink@hyper", isHyper);

      if (isHyper)
      {
         registerControlSequence(new AtGlsTarget());
         registerControlSequence(new HyperLink("@glslink"));

         registerControlSequence(new GenericCommand(true, "glsifhyperon", null,
           new TeXObject[]{new TeXCsRef("ifKV@glslink@hyper"), 
             getListener().getParam(1), new TeXCsRef("else"),
             getListener().getParam(2), new TeXCsRef("fi")}));

         registerControlSequence(new GenericCommand(true, 
           "glsdisablehyper", null, new TeXObject[]
           {
              new TeXCsRef("KV@glslink@hyperfalse"),
              new TeXCsRef("let"),
              new TeXCsRef("@glslink"),
              new TeXCsRef("glsdonohyperlink"),
              new TeXCsRef("let"),
              new TeXCsRef("@glstarget"),
              new TeXCsRef("glsdonohypertarget")
           }));

         registerControlSequence(new GenericCommand(true, 
           "glsenablehyper", null, new TeXObject[]
           {
              new TeXCsRef("KV@glslink@hypertrue"),
              new TeXCsRef("let"),
              new TeXCsRef("@glslink"),
              new TeXCsRef("glsdohyperlink"),
              new TeXCsRef("let"),
              new TeXCsRef("@glstarget"),
              new TeXCsRef("glsdohypertarget")
           }));

         registerControlSequence(new GlsTarget(this));
      }
      else
      {
         registerControlSequence(new AtSecondOfTwo("glsifhyperon"));
         registerControlSequence(new AtSecondOfTwo("glstarget"));
         registerControlSequence(new AtSecondOfTwo("@glstarget"));
         registerControlSequence(new AtSecondOfTwo("@glslink"));
         registerControlSequence(new GenericCommand("glsdisablehyper"));
         registerControlSequence(new GenericCommand("glsenablehyper"));
      }

      registerControlSequence(new AtSecondOfTwo("glsdonohypertarget"));
      registerControlSequence(new AtSecondOfTwo("glsdonohyperlink"));
      registerControlSequence(new HyperTarget("glsdohypertarget"));
      registerControlSequence(new HyperLink("glsdohyperlink"));
      registerControlSequence(new GlsHyperlink());

      registerControlSequence(new AtGobble("glsentryitem"));
      registerControlSequence(new AtGobble("glssubentryitem"));
      registerControlSequence(new GlsPostDescription(this));

      registerControlSequence(new GlsSeeFormat());
      registerControlSequence(new GlsSeeList());
      registerControlSequence(new GlsSeeItem());
      registerControlSequence(new TextualContentCommand("seename", "see"));
      registerControlSequence(new TextualContentCommand("glsseesep", ", "));
      registerControlSequence(new GenericCommand(true,
        "glsseelastsep", null, TeXParserUtils.createStack(listener,
        new TeXCsRef("space"), new TeXCsRef("andname"), new TeXCsRef("space"))));

      registerControlSequence(new GenericCommand(true,
        "andname", null, new TeXCsRef("&")));

      registerControlSequence(new NewGlossaryEntry(this));
      registerControlSequence(new LongNewGlossaryEntry(this));
      registerControlSequence(new LoadGlsEntries());

      registerControlSequence(new GlsExpandFields(this));
      registerControlSequence(new GlsExpandFields("glsnoexpandfields", false, true, this));
      registerControlSequence(new GlsExpandFields("glssetnoexpandfield", false, false, this));
      registerControlSequence(new GlsExpandFields("glssetexpandfield", true, false, this));

      registerControlSequence(new TextualContentCommand("glsautoprefix", ""));

      registerControlSequence(new GenericCommand("glspostlinkhook"));
      registerControlSequence(new GenericCommand("glslinkcheckfirsthyperhook"));
      registerControlSequence(new GenericCommand("glslinkpostsetkeys"));
      registerControlSequence(new GenericCommand("@gls@setdefault@glslink@opts"));
      registerControlSequence(new DoAtGlsDisableHyperInList(this));
      registerControlSequence(new AtGlsAtAtLink(this));
      registerControlSequence(new AtGlsAtAtLink("glslink", this, true));
      registerControlSequence(new AtGlsAtAtLink("glsdisp", this, true, true));
      registerControlSequence(new AtGlsAtLink(this));
      registerControlSequence(new AtGlsAtFieldAtLink(this));
      registerControlSequence(new GlsEntryFmt(this));
      registerControlSequence(new GlsGenEntryFmt(this));
      registerControlSequence(new AtFirstOfOne("glstextformat"));

      registerControlSequence(new GenericCommand("glscapitalisewords", null,
         new TeXCsRef("capitalisewords")));

      NewIf.createConditional(true, getParser(), "ifglsnogroupskip", false);
      registerControlSequence(new TextualContentCommand("glsgroupskip", ""));

      NewIf.createConditional(true, getParser(), "ifglsnopostdot", isExtra());

      registerControlSequence(new GlsGetGroupTitle());
      registerControlSequence(new AtGlsAtGetGroupTitle());
      registerControlSequence(new GlsNavHyperLink());
      registerControlSequence(new GlsNavHyperLinkName());
      registerControlSequence(new GlsNavHyperTarget());
      registerControlSequence(new GlsNavigation());
      registerControlSequence(new GlsSymbolNav());
      registerControlSequence(new TextualContentCommand("glshypernavsep", " | "));

      registerControlSequence(new AtGlsAtHypergroup());
      registerControlSequence(new AtGlsNavHyperTarget());

      registerControlSequence(new GlsAdd(this));

      registerControlSequence(new Gls(this));
      registerControlSequence(new Gls("Gls", CaseChange.SENTENCE, this));
      registerControlSequence(new Gls("GLS", CaseChange.TO_UPPER, this));

      registerControlSequence(new Gls("glspl", CaseChange.NO_CHANGE, true, this));
      registerControlSequence(new Gls("Glspl", CaseChange.SENTENCE, true, this));
      registerControlSequence(new Gls("GLSpl", CaseChange.TO_UPPER, true, this));

      registerControlSequence(new IfGlsUsed(this));

      registerControlSequence(new GlsFieldLink("glsname", "name", this));
      registerControlSequence(new GlsFieldLink("Glsname", "name", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFieldLink("GLSname", "name", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsFieldLink("glstext", "text", this));
      registerControlSequence(new GlsFieldLink("Glstext", "text", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFieldLink("GLStext", "text", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsFieldLink("glsplural", "plural", true, this));
      registerControlSequence(new GlsFieldLink("Glsplural", "plural", CaseChange.SENTENCE, true, this));
      registerControlSequence(new GlsFieldLink("GLSplural", "plural", CaseChange.TO_UPPER, true, this));

      registerControlSequence(new GlsFieldLink("glsfirst", "first", this));
      registerControlSequence(new GlsFieldLink("Glsfirst", "first", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFieldLink("GLSfirst", "first", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsFieldLink("glsfirstplural", "firstplural", true, this));
      registerControlSequence(new GlsFieldLink("Glsfirstplural", "firstplural", CaseChange.SENTENCE, true, this));
      registerControlSequence(new GlsFieldLink("GLSfirstplural", "firstplural", CaseChange.TO_UPPER, true, this));

      registerControlSequence(new GlsFieldLink("glssymbol", "symbol", this));
      registerControlSequence(new GlsFieldLink("Glssymbol", "symbol", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFieldLink("GLSsymbol", "symbol", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsFieldLink("glssymbolplural", "symbolplural", true, this));
      registerControlSequence(new GlsFieldLink("Glssymbolplural", "symbolplural", CaseChange.SENTENCE, true, this));
      registerControlSequence(new GlsFieldLink("GLSsymbolplural", "symbolplural", CaseChange.TO_UPPER, true, this));

      registerControlSequence(new GlsFieldLink("glsdesc", "description", this));
      registerControlSequence(new GlsFieldLink("Glsdesc", "description", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFieldLink("GLSdesc", "description", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsFieldLink("glsdescplural", "descriptionplural", true, this));
      registerControlSequence(new GlsFieldLink("Glsdescplural", "descriptionplural", CaseChange.SENTENCE, true, this));
      registerControlSequence(new GlsFieldLink("GLSdescplural", "descriptionplural", CaseChange.TO_UPPER, true, this));

      registerControlSequence(new GlsFieldLink("glsuseri", "user1", this));
      registerControlSequence(new GlsFieldLink("Glsuseri", "user1", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFieldLink("GLSuseri", "user1", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsFieldLink("glsuserii", "user2", this));
      registerControlSequence(new GlsFieldLink("Glsuserii", "user2", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFieldLink("GLSuserii", "user2", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsFieldLink("glsuseriii", "user3", this));
      registerControlSequence(new GlsFieldLink("Glsuseriii", "user3", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFieldLink("GLSuseriii", "user3", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsFieldLink("glsuseriv", "user4", this));
      registerControlSequence(new GlsFieldLink("Glsuseriv", "user4", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFieldLink("GLSuseriv", "user4", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsFieldLink("glsuserv", "user5", this));
      registerControlSequence(new GlsFieldLink("Glsuserv", "user5", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFieldLink("GLSuserv", "user5", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsFieldLink("glsuservi", "user6", this));
      registerControlSequence(new GlsFieldLink("Glsuservi", "user6", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFieldLink("GLSuservi", "user6", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsEntryField("glsunexpandedfieldvalue", 
        true, this));
      registerControlSequence(new GlsEntryField("@gls@entry@field", this));

      registerControlSequence(new GlsEntryType(this));
      registerControlSequence(new GlsEntryParent(this));
      registerControlSequence(new GlsEntryField("glsentryname", "name", this));
      registerControlSequence(new GlsEntryField("glsentrytext", "text", this));
      registerControlSequence(new GlsEntryField("glsentryplural", "plural", this));
      registerControlSequence(new GlsEntryField("glsentryfirst", "first", this));
      registerControlSequence(new GlsEntryField("glsentryfirstplural", 
        "firstplural", this));
      registerControlSequence(new GlsEntryField("glsentrydesc",
        "description", this));
      registerControlSequence(new GlsEntryField("glsentrydescplural", 
        "descriptionplural", this));
      registerControlSequence(new GlsEntryField("glsentrysymbol",
        "symbol", this));
      registerControlSequence(new GlsEntryField("glsentrysymbolplural",
        "symbolplural", this));

      registerControlSequence(new GlsEntryField("Glsentryname", 
        "name", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("Glsentrytext", "text",
        CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("Glsentryplural",
        "plural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("Glsentryfirst",
        "first", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("Glsentryfirstplural", 
        "firstplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("Glsentrydesc",
        "description", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("Glsentrydescplural", 
        "descriptionplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("Glsentrysymbol",
        "symbol", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("Glsentrysymbolplural",
        "symbolplural", CaseChange.SENTENCE, this));

      registerControlSequence(new GlsEntryField("glsentryshort", "short", this));
      registerControlSequence(new GlsEntryField("Glsentryshort", "short",
        CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("glsentryshortpl", "shortplural",
        this));
      registerControlSequence(new GlsEntryField("Glsentryshortpl", "shortplural",
        CaseChange.SENTENCE, this));

      registerControlSequence(new GlsEntryField("glsentrylong", "long", this));
      registerControlSequence(new GlsEntryField("Glsentrylong", "long",
        CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("glsentrylongpl", "longplural",
        this));
      registerControlSequence(new GlsEntryField("Glsentrylongpl", "longplural",
        CaseChange.SENTENCE, this));

      registerControlSequence(new IfHasField("ifglshassymbol", "symbol", this));
      registerControlSequence(new IfHasField("ifglshasdesc", "description", this));
      registerControlSequence(new IfHasField("ifglshasshort", "short", this));
      registerControlSequence(new IfHasField("ifglshaslong", "long", this));

      registerControlSequence(new GlsSetField("glsfielddef", this));
      registerControlSequence(new GlsSetField("glsfieldgdef", false, true, this));
      registerControlSequence(new GlsSetField("glsfieldxdef", true, true, this));
      registerControlSequence(new GlsSetField("glsfieldedef", true, false, this));

      LaTeXParserListener listener = (LaTeXParserListener)getParser().getListener();

      setModifier(listener.getOther('*'), "hyper", new UserBoolean(true));
      setModifier(listener.getOther('+'), "hyper", new UserBoolean(false));

      registerControlSequence(new GobbleOpt("makeglossaries", 1, 0));

      registerControlSequence(new GlsAddStorageKey(this));

      listener.newtoks(true, "glslabeltok");
      listener.newtoks(true, "glsshorttok");
      listener.newtoks(true, "glslongtok");

      registerControlSequence(new NewAcronym(this));

      registerControlSequence(new GlsEntryFull(this));
      registerControlSequence(new GlsEntryFull("Glsentryfull", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryFull("glsentryfullpl", true, this));
      registerControlSequence(new GlsEntryFull("Glsentryfullpl", CaseChange.SENTENCE, true, this));

      getParser().getSettings().newcount("gls@level");

      registerControlSequence(new GenericCommand(true, "@gls@counter", null,
         new TeXCsRef("glscounter")));

      registerControlSequence(new KVAtGlsLinkAtCounter());

      registerControlSequence(new TextualContentCommand("glsopenbrace", "{"));
      registerControlSequence(new TextualContentCommand("glsclosebrace", "}"));
      registerControlSequence(new TextualContentCommand("glsbackslash", "\\"));
      registerControlSequence(new TextualContentCommand("glspercentchar", "%"));
      registerControlSequence(new TextualContentCommand("glstildechar", "~"));

      registerControlSequence(new LaTeXGenericCommand(true, "glsquote",
       "m", TeXParserUtils.createStack(listener,
         listener.getOther('"'), listener.getParam(1), listener.getOther('"'))));

      registerControlSequence(new GlsSeeItemFormat(this));

      if (extra)
      {
         addExtraDefinitions();
      }
      else
      {
         KeyValList keyValList = new KeyValList();
         TeXObjectList val = listener.createStack();
         val.add(new TeXCsRef("the"));
         val.add(new TeXCsRef("glslongtok"));

         keyValList.put("description", val);
         registerControlSequence(new GenericCommand(true, "GenericAcronymFields",
          null, keyValList));

         registerControlSequence(new GenericCommand("newacronymhook"));
         registerControlSequence(new AcrFullFmt(this));
         registerControlSequence(new AcrFullFmt("Acrfullfmt", CaseChange.SENTENCE, this));
         registerControlSequence(new AcrFullFmt("ACRfullfmt", CaseChange.TO_UPPER, this));
         registerControlSequence(new AcrFullFmt("acrfullplfmt", true, this));
         registerControlSequence(new AcrFullFmt("Acrfullplfmt", CaseChange.SENTENCE, true, this));
         registerControlSequence(new AcrFullFmt("ACRfullplfmt", CaseChange.TO_UPPER, true, this));

         registerControlSequence(new NewAcronymStyle(this));
         registerControlSequence(new NewAcronymStyle("renewacronymstyle",
           Overwrite.FORCE, this));

         registerControlSequence(new SetAcronymStyle(this));

         registerControlSequence(new AtGlsAcrAtDispStyleAtLongShort(this));
         registerControlSequence(new AtGlsAcrAtStyleDefsAtLongShort(this));

         registerControlSequence(new GlsGenAcFmt(this));
      }
   }

   protected void addExtraDefinitions()
   {
      listener.newtoks(true, "glsshortpltok");
      listener.newtoks(true, "glslongpltok");

      registerControlSequence(new TextualContentCommand("seealsoname", "see also"));

      registerControlSequence(new GlsXtrGetGroupTitle());
      registerControlSequence(new GlsXtrSetGroupTitle());
      registerControlSequence(new GlsXtrSetGroupTitle(
        "glsxtrlocalsetgrouptitle", true));

      registerControlSequence(new TextualContentCommand("abbreviationsname", "Abbreviations"));

      registerControlSequence(new GenericCommand(true, "glsxtrabbrvtype", 
        null, new TeXCsRef("glsdefaulttype")));

      registerControlSequence(
        new TextualContentCommand("GlsXtrDefaultResourceOptions", ""));

      getParser().getSettings().newcount("glsxtrresourcecount");

      registerControlSequence(new GlsXtrResourceFile());
      registerControlSequence(new GlsXtrLoadResources());

      registerControlSequence(new GlsSetAbbrvFmt(this));
      registerControlSequence(new GlsXtrGenAbbrvFmt(this));

      registerControlSequence(new GlsXtrIfHasField(this));
      registerControlSequence(new GlsEntryField("glscategory", "category", this));
      registerControlSequence(new GlsIfAttributeBool("glsifregular",
       "regular", true, this));
      registerControlSequence(new GlsIfAttributeBool("glsifnotregular",
       "regular", false, this));

      registerControlSequence(new GlsIfAttribute(this));
      registerControlSequence(new GlsIfAttribute("glsifcategoryattribute", true, this));
      registerControlSequence(new GlsHasAttribute(this));
      registerControlSequence(new GlsHasAttribute("glshascategoryattribute", true, this));
      registerControlSequence(new GlsSetAttribute(this));
      registerControlSequence(new GlsSetAttribute("glssetcategoryattribute", true, this));
      registerControlSequence(new GlsSetAttribute("glssetcategoryattributes", true, this));
      registerControlSequence(new GlsSetAttribute("glssetcategoriesattribute", true, this));
      registerControlSequence(new GlsSetAttribute("glssetcategoriesattributes", true, this));

      registerControlSequence(new GlsSeeList("glsxtrseelist"));
      registerControlSequence(new GenericCommand(true,
        "glsseelastoxfordsep", null, new TeXCsRef("glsseelastsep")));

      registerControlSequence(new GlsXtrPostDescription(this));
      registerControlSequence(new PrintUnsrtGlossaries(this));
      registerControlSequence(new PrintUnsrtGlossary(this));
      registerControlSequence(new GlsXtrUnsrtDo(this));
      registerControlSequence(new GlsXtrAddGroup(this));
      registerControlSequence(new PrintUnsrtGlossaryHandler());
      registerControlSequence(new AtPrintUnsrtAtGlossaryAtHandler(this));
      registerControlSequence(new AtGobble("printunsrtglossaryentryprocesshook"));
      registerControlSequence(new AtGlsXtrAtCheckGroup(this));
      registerControlSequence(new GlsSubGroupHeading());
      registerControlSequence(new TextualContentCommand(
        "printunsrtglossarypredoglossary", ""));
      registerControlSequence(new GlsXtrPostNameHook(this));
      registerControlSequence(new AtGobble("glsextrapostnamehook"));

      registerControlSequence(new NewAcronym("newabbreviation", this));

      registerControlSequence(new NewAbbreviationStyle(this));
      registerControlSequence(new NewAbbreviationStyle("renewabbreviationstyle",
         Overwrite.FORCE, this));
      registerControlSequence(new LetAbbreviationStyle(this));
      registerControlSequence(new LetAbbreviationStyle("@glsxtr@deprecated@abbrstyle", this));
      registerControlSequence(new GlsXtrUseAbbrStyleSetup(this));
      registerControlSequence(new GlsXtrUseAbbrStyleFmts(this));
      registerControlSequence(new SetAbbreviationStyle(this));
      registerControlSequence(new GlsXtrSetComplexStyle(this));
      registerControlSequence(new IfApplyInnerFmtField(this));
      registerControlSequence(new GlsExclApplyInnerFmtField(this));

      registerControlSequence(new TextualContentCommand(
         "glsxtrgroupfield", "group"));
      registerControlSequence(new TextualContentCommand(
         "GlsXtrLocationField", "location"));

      getParser().getSettings().newcount("@glsxtr@leveloffset");

      NewIf.createConditional(true, getParser(), "ifglsxtr@printgloss@groups", true);
      NewIf.createConditional(true, getParser(), "ifglsxtrprintglossflatten", false);

      NewIf.createConditional(true, getParser(), "ifglsxtrinsertinside", false);

      registerControlSequence(new GenericCommand("glsxtrfieldtitlecasecs", null,
         new TeXCsRef("glscapitalisewords")));

      registerControlSequence(new GlsEntryField("glsxtrusefield", this));
      registerControlSequence(new GlsEntryField("Glsxtrusefield", null,
         CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("GLSxtrusefield", null,
         CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsSetField("GlsXtrSetField", this));
      registerControlSequence(new GlsSetField("gGlsXtrSetField", false, true, this));
      registerControlSequence(new GlsSetField("xGlsXtrSetField", true, true, this));
      registerControlSequence(new GlsSetField("eGlsXtrSetField", true, false, this));
      registerControlSequence(new GlsSetField("glsxtrdeffield", false, false, false, this));
      registerControlSequence(new GlsSetField("glsxtredeffield", true, false, false, this));
      registerControlSequence(new GlsXtrAppToCsvField(this));

      registerControlSequence(new GlsXtrFieldListAdd());
      registerControlSequence(new GlsXtrFieldListAdd("glsxtrfieldlisteadd", false, true));
      registerControlSequence(new GlsXtrFieldListAdd("glsxtrfieldlistgadd", true, false));
      registerControlSequence(new GlsXtrFieldListAdd("glsxtrfieldlistxadd", true, true));

      registerControlSequence(new GlsXtrFieldDoListLoop());
      registerControlSequence(new GlsXtrFieldDoListLoop("glsxtrfieldforlistloop", false));

      registerControlSequence(new TextualContentCommand("glsxtrundeftag", "??"));

      registerControlSequence(new TextualContentCommand(
         "abbreviationname", "Abbreviations"));

      registerControlSequence(new GlossEntryField("glossentrynameother", this));

      registerControlSequence(new GlsAddStorageKey("glsxtrprovidestoragekey",
        Overwrite.SKIP, this));

      registerControlSequence(new AtGobble("glsxtrnewabbrevpresetkeyhook", 3));
      registerControlSequence(new GenericCommand("newabbreviationhook"));
      registerControlSequence(new GenericCommand("GlsXtrPostNewAbbreviation"));
      registerControlSequence(new GenericCommand("CustomAbbreviationFields"));

      registerControlSequence(new AtFirstOfOne("glsxtrabbreviationfont"));
      registerControlSequence(new AtFirstOfOne("glsxtrregularfont"));

      registerControlSequence(new AtFirstOfOne("glsxtrgenentrytextfmt"));
      registerControlSequence(new AtFirstOfOne("glsabbrvdefaultfont"));
      registerControlSequence(new AtFirstOfOne("glslongdefaultfont"));
      registerControlSequence(new GlsXtrParen());
      registerControlSequence(new GlsXtrFullSep());
      registerControlSequence(new GlsFmtField(this));
      registerControlSequence(new GlsFmtField("Glsfmtfield", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsFmtField("GLSfmtfield", CaseChange.TO_UPPER, this));
      registerControlSequence(new GlsFmtInsert());
      registerControlSequence(new GlsFmtInsert("GLSfmtinsert", CaseChange.TO_UPPER));

      registerControlSequence(new GlsXtrFullFormat(this));
      registerControlSequence(new GlsXtrFullFormat("Glsxtrfullformat",
        CaseChange.SENTENCE, this));
      registerControlSequence(new GlsXtrFullFormat("GLSxtrfullformat",
        CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsXtrFullFormat("glsxtrfullplformat", true, this));
      registerControlSequence(new GlsXtrFullFormat("Glsxtrfullplformat",
        CaseChange.SENTENCE, true, this));
      registerControlSequence(new GlsXtrFullFormat("GLSxtrfullplformat",
        CaseChange.TO_UPPER, true, this));

      registerControlSequence(new GenericCommand(true,
       "glsxtrinlinefullformat", null, new TeXCsRef("glsxtrfullformat")));

      registerControlSequence(new GenericCommand(true,
       "Glsxtrinlinefullformat", null, new TeXCsRef("Glsxtrfullformat")));

      registerControlSequence(new GenericCommand(true,
       "GLSxtrinlinefullformat", null, new TeXCsRef("GLSxtrfullformat")));

      registerControlSequence(new GenericCommand(true,
       "glsxtrinlinefullplformat", null, new TeXCsRef("glsxtrfullplformat")));

      registerControlSequence(new GenericCommand(true,
       "Glsxtrinlinefullplformat", null, new TeXCsRef("Glsxtrfullplformat")));

      registerControlSequence(new GenericCommand(true,
       "GLSxtrinlinefullplformat", null, new TeXCsRef("GLSxtrfullplformat")));

      registerControlSequence(new GlsXtrSubSequentFmt(this));
      registerControlSequence(new GlsXtrSubSequentFmt("Glsxtrsubsequentfmt",
        CaseChange.SENTENCE, this));
      registerControlSequence(new GlsXtrSubSequentFmt("GLSxtrsubsequentfmt",
        CaseChange.TO_UPPER, this));
      registerControlSequence(new GlsXtrSubSequentFmt("glsxtrsubsequentplfmt",
        true, this));
      registerControlSequence(new GlsXtrSubSequentFmt("Glsxtrsubsequentplfmt",
        CaseChange.SENTENCE, true, this));
      registerControlSequence(new GlsXtrSubSequentFmt("GLSxtrsubsequentplfmt",
        CaseChange.TO_UPPER, true, this));

      registerControlSequence(new GlsXtrSubSequentFmt("glsxtrdefaultsubsequentfmt", this));
      registerControlSequence(new GlsXtrSubSequentFmt("Glsxtrdefaultsubsequentfmt",
        CaseChange.SENTENCE, this));
      registerControlSequence(new GlsXtrSubSequentFmt("GLSxtrdefaultsubsequentfmt",
        CaseChange.TO_UPPER, this));
      registerControlSequence(new GlsXtrSubSequentFmt("glsxtrdefaultsubsequentplfmt",
        true, this));
      registerControlSequence(new GlsXtrSubSequentFmt("Glsxtrdefaultsubsequentplfmt",
        CaseChange.SENTENCE, true, this));
      registerControlSequence(new GlsXtrSubSequentFmt("GLSxtrdefaultsubsequentplfmt",
        CaseChange.TO_UPPER, true, this));

      registerControlSequence(new GenericCommand(true,
       "glsfirstabbrvfont", null, new TeXCsRef("glsfirstabbrvdefaultfont")));

      registerControlSequence(new GenericCommand(true,
       "glsfirstlongfont", null, new TeXCsRef("glsfirstlongdefaultfont")));

      registerControlSequence(new GenericCommand(true,
       "glsfirstlongdefaultfont", null, new TeXCsRef("glslongdefaultfont")));

      registerControlSequence(new GenericCommand(true,
       "glslongfont", null, new TeXCsRef("glslongdefaultfont")));

      registerControlSequence(new GenericCommand(true,
       "glsfirstabbrvdefaultfont", null, new TeXCsRef("glsabbrvdefaultfont")));

      registerControlSequence(new GenericCommand(true,
       "glsabbrvfont", null, new TeXCsRef("glsabbrvdefaultfont")));

      registerControlSequence(new GenericCommand(true,
       "glsxtrabbrvpluralsuffix", null, new TeXCsRef("glspluralsuffix")));

      registerControlSequence(new GenericCommand(true,
       "abbrvpluralsuffix", null, new TeXCsRef("glsxtrabbrvpluralsuffix")));

      registerControlSequence(new GlsInnerFmtFont("glsfirstinnerfmtabbrvfont"));
      registerControlSequence(new GlsInnerFmtFont("glsinnerfmtabbrvfont"));
      registerControlSequence(new GlsInnerFmtFont("glsfirstinnerfmtlongfont"));
      registerControlSequence(new GlsInnerFmtFont("glsinnerfmtlongfont"));

      registerControlSequence(new GlsXpFont("glsfirstxpabbrvfont", this));
      registerControlSequence(new GlsXpFont("glsxpabbrvfont", this));
      registerControlSequence(new GlsXpFont("glsfirstxplongfont", this));
      registerControlSequence(new GlsXpFont("glsxplongfont", this));

      registerControlSequence(new AtGobble("glsxtrAccSuppAbbrSetNoLongAttrs"));
      registerControlSequence(new AtGobble("glsxtrAccSuppAbbrSetFirstLongAttrs"));
      registerControlSequence(new AtGobble("glsxtrAccSuppAbbrSetTextShortAttrs"));
      registerControlSequence(new AtGobble("glsxtrAccSuppAbbrSetNameShortAttrs"));
      registerControlSequence(new AtGobble("glsxtrAccSuppAbbrSetNameLongAttrs"));

      registerControlSequence(new GlsAccessField("glsaccessname", "name", this));
      registerControlSequence(new GlsAccessField("Glsaccessname", "name", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessname", "name", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtname", "name", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtname", "name", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtname", "name", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccesstext", "text", this));
      registerControlSequence(new GlsAccessField("Glsaccesstext", "text", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccesstext", "text", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmttext", "text", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmttext", "text", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmttext", "text", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessplural", "plural", this));
      registerControlSequence(new GlsAccessField("Glsaccessplural", "plural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessplural", "plural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtplural", "plural", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtplural", "plural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtplural", "plural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessfirst", "first", this));
      registerControlSequence(new GlsAccessField("Glsaccessfirst", "first", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessfirst", "first", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtfirst", "first", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtfirst", "first", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtfirst", "first", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessfirstplural", "firstplural", this));
      registerControlSequence(new GlsAccessField("Glsaccessfirstplural", "firstplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessfirstplural", "firstplural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtfirstplural", "firstplural", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtfirstplural", "firstplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtfirstplural", "firstplural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccesssymbol", "symbol", this));
      registerControlSequence(new GlsAccessField("Glsaccesssymbol", "symbol", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccesssymbol", "symbol", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtsymbol", "symbol", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtsymbol", "symbol", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtsymbol", "symbol", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccesssymbolplural", "symbolplural", this));
      registerControlSequence(new GlsAccessField("Glsaccesssymbolplural", "symbolplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccesssymbolplural", "symbolplural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtsymbolplural", "symbolplural", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtsymbolplural", "symbolplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtsymbolplural", "symbolplural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessdesc", "description", this));
      registerControlSequence(new GlsAccessField("Glsaccessdesc", "description", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessdesc", "description", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtdesc", "description", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtdesc", "description", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtdesc", "description", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessdescplural", "descriptionplural", this));
      registerControlSequence(new GlsAccessField("Glsaccessdescplural", "descriptionplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessdescplural", "descriptionplural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtdescplural", "descriptionplural", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtdescplural", "descriptionplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtdescplural", "descriptionplural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessshort", "short", this));
      registerControlSequence(new GlsAccessField("Glsaccessshort", "short", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessshort", "short", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtshort", "short", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtshort", "short", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtshort", "short", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessshortpl", "shortplural", this));
      registerControlSequence(new GlsAccessField("Glsaccessshortpl", "shortplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessshortpl", "shortplural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtshortpl", "shortplural", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtshortpl", "shortplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtshortpl", "shortplural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccesslong", "long", this));
      registerControlSequence(new GlsAccessField("Glsaccesslong", "long", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccesslong", "long", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtlong", "long", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtlong", "long", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtlong", "long", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccesslongpl", "longplural", this));
      registerControlSequence(new GlsAccessField("Glsaccesslongpl", "longplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccesslongpl", "longplural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtlongpl", "longplural", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtlongpl", "longplural", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtlongpl", "longplural", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessuseri", "user1", this));
      registerControlSequence(new GlsAccessField("Glsaccessuseri", "user1", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessuseri", "user1", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtuseri", "user1", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtuseri", "user1", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtuseri", "user1", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessuserii", "user2", this));
      registerControlSequence(new GlsAccessField("Glsaccessuserii", "user2", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessuserii", "user2", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtuserii", "user2", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtuserii", "user2", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtuserii", "user2", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessuseriii", "user3", this));
      registerControlSequence(new GlsAccessField("Glsaccessuseriii", "user3", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessuseriii", "user3", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtuseriii", "user3", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtuseriii", "user3", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtuseriii", "user3", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessuseriv", "user4", this));
      registerControlSequence(new GlsAccessField("Glsaccessuseriv", "user4", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessuseriv", "user4", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtuseriv", "user4", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtuseriv", "user4", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtuseriv", "user4", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessuserv", "user5", this));
      registerControlSequence(new GlsAccessField("Glsaccessuserv", "user5", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessuserv", "user5", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtuserv", "user5", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtuserv", "user5", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtuserv", "user5", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessField("glsaccessuservi", "user6", this));
      registerControlSequence(new GlsAccessField("Glsaccessuservi", "user6", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessField("GLSaccessuservi", "user6", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsAccessFmtField("glsaccessfmtuservi", "user6", this));
      registerControlSequence(new GlsAccessFmtField("Glsaccessfmtuservi", "user6", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsAccessFmtField("GLSaccessfmtuservi", "user6", CaseChange.TO_UPPER, this));

      registerControlSequence(new GlsXtrSaveInsert());
      registerControlSequence(new GenericCommand(true, "glsxtrfullsaveinsert",
        null, new TeXCsRef("glsxtrsaveinsert")));
      registerControlSequence(new GlsXtrSetUpFullDefs());

      registerControlSequence(new GlsXtrAbbrvField("glsxtrshort", "short", this));
      registerControlSequence(new GlsXtrAbbrvField("Glsxtrshort", "short", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsXtrAbbrvField("GLSxtrshort", "short", CaseChange.TO_UPPER, this));
      registerControlSequence(new GlsXtrAbbrvField("glsxtrshortpl", "shortplural", true, this));
      registerControlSequence(new GlsXtrAbbrvField("Glsxtrshortpl", "shortplural", CaseChange.SENTENCE, true, this));
      registerControlSequence(new GlsXtrAbbrvField("GLSxtrshortpl", "shortplural", CaseChange.TO_UPPER, true, this));

      registerControlSequence(new GlsXtrAbbrvField("glsxtrlong", "long", this));
      registerControlSequence(new GlsXtrAbbrvField("Glsxtrlong", "long", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsXtrAbbrvField("GLSxtrlong", "long", CaseChange.TO_UPPER, this));
      registerControlSequence(new GlsXtrAbbrvField("glsxtrlongpl", "longplural", true, this));
      registerControlSequence(new GlsXtrAbbrvField("Glsxtrlongpl", "longplural", CaseChange.SENTENCE, true, this));
      registerControlSequence(new GlsXtrAbbrvField("GLSxtrlongpl", "longplural", CaseChange.TO_UPPER, true, this));

      registerControlSequence(new GlsXtrFull("glsxtrfull", this));
      registerControlSequence(new GlsXtrFull("Glsxtrfull", CaseChange.SENTENCE, this));
      registerControlSequence(new GlsXtrFull("GLSxtrfull", CaseChange.TO_UPPER, this));
      registerControlSequence(new GlsXtrFull("glsxtrfullpl", true, this));
      registerControlSequence(new GlsXtrFull("Glsxtrfullpl", CaseChange.SENTENCE, true, this));
      registerControlSequence(new GlsXtrFull("GLSxtrfullpl", CaseChange.TO_UPPER, true, this));

      registerControlSequence(new TextualContentCommand("glsxtrtitleopts", 
         "noindex,hyper=false"));
      addGlsXtrTitleCommands("short");
      addGlsXtrTitleCommands("shortpl", "shortplural", true);
      addGlsXtrTitleCommands("long");
      addGlsXtrTitleCommands("longpl", "longplural", true);
      addGlsXtrTitleCommands("name");
      addGlsXtrTitleCommands("text");
      addGlsXtrTitleCommands("plural", true);
      addGlsXtrTitleCommands("first");
      addGlsXtrTitleCommands("firstpl", "firstplural", "firstplural", true);

      addGlsXtrTitleFullCommands(false);
      addGlsXtrTitleFullCommands(true);

      registerControlSequence(new GlsXtrDisplayLocNameRef());
      registerControlSequence(new GlsXtrEquationLocFmt());
      registerControlSequence(new GlsXtrWrGlossaryLocFmt());
      registerControlSequence(new GlsXtrNameRefLink());
      registerControlSequence(new GlsXtrFmtInternalNameRef());
      registerControlSequence(new GlsXtrFmtExternalNameRef());
      registerControlSequence(new AtGobble("glsxtrsetactualanchor"));
      registerControlSequence(new GlsXtrTitledNameRefLink());

      registerControlSequence(new GlsXtrInternalLocationHyperlink());
      registerControlSequence(new GlsXtrLocationHyperLink());

      registerControlSequence(new AtGobble("glsxtr@inc@wrglossaryctr"));

      registerControlSequence(new AtGobble("@@glsxtrwrglosscountermark"));
      registerControlSequence(new AtGlsXtrWrGlossCounterMark());

      FrameBox wrglossCtrMark = new FrameBox("glsxtrwrglosscountermark",
        BorderStyle.NONE, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, true);
      wrglossCtrMark.setTextFont(
       new TeXFontText(TeXFontFamily.TT, TeXFontSize.SMALL));
      wrglossCtrMark.setPrefix(getListener().getOther('['));
      wrglossCtrMark.setSuffix(getListener().getOther(']'));

      getListener().declareFrameBox(wrglossCtrMark);

      NewIf.createConditional(true, getParser(), "ifKV@glslink@noindex", false);

      registerControlSequence(new AtGlsXtrAtFieldAtLinkDefs());

      registerControlSequence(new AtNumberOfNumber(
        "glsxtr@wrglossarylocation", 1, 2, true));
      registerControlSequence(new GlsXtrIndexCounterLink(this));
      registerControlSequence(new GlsXtrDualBackLink(this));

      registerControlSequence(new TextualContentCommand("GlsXtrDualField",
       "dual"));

      registerControlSequence(new TextualContentCommand("@glsxtr@labelprefixes",
       ""));
      registerControlSequence(new GlsXtrClearLabelPrefixes());
      registerControlSequence(new GlsXtrAddLabelPrefix());
      registerControlSequence(new GlsXtrPrependLabelPrefix());

      registerControlSequence(new Dgls(this));
      registerControlSequence(new Dgls("dGls", CaseChange.SENTENCE, this));
      registerControlSequence(new Dgls("dGLS", CaseChange.TO_UPPER, this));

      registerControlSequence(new Dgls("dglspl", CaseChange.NO_CHANGE, true, this));
      registerControlSequence(new Dgls("dGlspl", CaseChange.SENTENCE, true, this));
      registerControlSequence(new Dgls("dGLSpl", CaseChange.TO_UPPER, true, this));

      registerControlSequence(new Dglslink(this));
      registerControlSequence(new Dglslink("dglsdisp", true, this));

      registerControlSequence(new GlsXtrGlossEntry(this));
      registerControlSequence(new GlsXtrStandaloneEntryName(this));
      registerControlSequence(new GenericCommand(true,
        "GlsXtrStandaloneGlossaryType", null, 
        TeXParserUtils.createStack(getListener(), new TeXCsRef("glsentrytype"),
          new TeXCsRef("glscurrententrylabel"))));
      registerControlSequence(new GlsXtrStandaloneSubEntryItem(this));
      registerControlSequence(new GlsXtrStandaloneEntryOther(this));
      registerControlSequence(new GlsXtrGlossEntryOther(this));

      registerControlSequence(new TextualContentCommand("glsxtrtitleopts",
        "noindex,hyper=false"));

      registerControlSequence(new GlsXtrAtTitleAtField(this));

      registerControlSequence(new GenericCommand(true, 
       "glsxtrtitletext", null, TeXParserUtils.createStack(getListener(),
       new TeXCsRef("glsxtr@title@field"), new TeXCsRef("glstext"))));

      registerControlSequence(new TextualContentCommand("glshex", "\\u"));
      registerControlSequence(new TextualContentCommand("glscapturedgroup", "\\$"));
      registerControlSequence(new TextualContentCommand("glshashchar", "#"));

      registerControlSequence(new GlsXtrUseSee(this));
      registerControlSequence(new GlsXtrUseSeeFormat());
      registerControlSequence(new GlsXtrUseSeeAlso(this));
      registerControlSequence(new GlsXtrUseSeeAlsoFormat());

      registerControlSequence(new GlsXtrNewGls(this));
      registerControlSequence(new GlsXtrNewGlsLike(this));
      registerControlSequence(new GlsXtrNewGLSLike(this));

      registerControlSequence(new GlsXtrNewGlsLink(this));
      registerControlSequence(new GlsXtrNewGlsLink("glsxtrnewglsdisp", true, this));
   }

   protected void addGlsXtrTitleCommands(String field)
   {
      addGlsXtrTitleCommands(field, field);
   }

   protected void addGlsXtrTitleCommands(String field, boolean isPlural)
   {
      addGlsXtrTitleCommands(field, field, isPlural);
   }

   protected void addGlsXtrTitleCommands(String csFieldTag, String field)
   {
      addGlsXtrTitleCommands(csFieldTag, field, false);
   }

   protected void addGlsXtrTitleCommands(String csFieldTag,
     String field, boolean isPlural)
   {
      addGlsXtrTitleCommands(csFieldTag, csFieldTag, field, isPlural);
   }

   protected void addGlsXtrTitleCommands(String csXtrFieldTag,
     String csFmtFieldTag, String field, boolean isPlural)
   {
      if (field.startsWith("short") || field.startsWith("long"))
      {
         registerControlSequence(new GlsXtrTitleAbbrvField("glsxtrtitle"+csXtrFieldTag,
           field, isPlural, this));

         registerControlSequence(new GlsXtrTitleAbbrvField("Glsxtrtitle"+csXtrFieldTag,
           field, CaseChange.SENTENCE, isPlural, this));

         registerControlSequence(new GlsXtrTitleAbbrvField("GLSxtrtitle"+csXtrFieldTag,
           field, CaseChange.TO_UPPER, isPlural, this));
      }
      else
      {
         registerControlSequence(new GlsXtrTitleField("glsxtrtitle"+csXtrFieldTag,
           field, isPlural, this));

         registerControlSequence(new GlsXtrTitleField("Glsxtrtitle"+csXtrFieldTag,
           field, CaseChange.SENTENCE, isPlural, this));

         registerControlSequence(new GlsXtrTitleField("GLSxtrtitle"+csXtrFieldTag,
           field, CaseChange.TO_UPPER, isPlural, this));
      }

      registerControlSequence(new GenericCommand(true, "glsfmt"+csFmtFieldTag,
        null, new TeXCsRef("glsxtrtitle"+csXtrFieldTag)));

      registerControlSequence(new GenericCommand(true, "Glsfmt"+csFmtFieldTag,
        null, new TeXCsRef("Glsxtrtitle"+csXtrFieldTag)));

      registerControlSequence(new GenericCommand(true, "GLSfmt"+csFmtFieldTag,
        null, new TeXCsRef("GLSxtrtitle"+csXtrFieldTag)));

   }

   protected void addGlsXtrTitleFullCommands(boolean isPlural)
   {
      String suffix = (isPlural ? "fullpl" : "full");

      TeXObjectList def = getListener().createStack();

      def.add(new TeXCsRef(isPlural ? "glsentrylongpl": "glsentrylong"));
      Group grp = getListener().createGroup();
      grp.add(getListener().getParam(1));
      def.add(grp);

      def.add(new TeXCsRef(isPlural ? "glsentryshortpl": "glsentryshort"));
      grp = getListener().createGroup();
      grp.add(getListener().getParam(1));
      def.add(grp);

      registerControlSequence(new LaTeXGenericCommand(true, "glspdffmt"+suffix,
        "m", def));

      registerControlSequence(new GlsXtrTitleFull("glsxtrtitle"+suffix, isPlural, this));
      registerControlSequence(new GlsXtrTitleFull("Glsxtrtitle"+suffix,
         CaseChange.SENTENCE, isPlural, this));
      registerControlSequence(new GlsXtrTitleFull("GLSxtrtitle"+suffix,
         CaseChange.TO_UPPER, isPlural, this));

      registerControlSequence(new GenericCommand(true, "glsfmt"+suffix,
        null, new TeXCsRef("glsxtrtitle"+suffix)));

      registerControlSequence(new GenericCommand(true, "Glsfmt"+suffix,
        null, new TeXCsRef("Glsxtrtitle"+suffix)));

      registerControlSequence(new GenericCommand(true, "GLSfmt"+suffix,
        null, new TeXCsRef("GLSxtrtitle"+suffix)));

   }

   @Override
   protected void preOptions(TeXObjectList stack) throws IOException
   {
      getListener().requirepackage(null, "etoolbox", false, stack);
      getListener().requirepackage(null, "textcase", false, stack);
      getListener().requirepackage(null, "mfirstuc", false, stack);
      getListener().requirepackage(null, "ifthen", false, stack);
      getListener().requirepackage(null, "keyval", false, stack);
      getListener().requirepackage(null, "datatool-base", false, stack);

      if (getParser().getControlSequence("chapter") != null)
      {
         section = "chapter";
      }
   }

   @Override
   protected void postOptions(TeXObjectList stack) throws IOException
   {
      super.postOptions(stack);

      if (indexcounter)
      {
         getListener().newcounter("wrglossary");
         registerControlSequence(new TextualContentCommand("glscounter", 
           "wrglossary"));

         registerControlSequence(new GlsXtrAtIncAtWrGlossaryCtr());
         registerControlSequence(new GlsXtrAtIncAtWrGlossaryCtr(
           "@@do@wrglossary"));
      }

      TeXObjectList substack = getListener().createStack();

      if (createMain)
      {
         createGlossary("main", new TeXCsRef("glossaryname"), null,
           "glg", "gls", "glo");
         createMain = false;
      }

      if (createIndex)
      {
         createGlossary("index", new TeXCsRef("indexname"), null,
           "ilg", "ind", "idx");
         createIndex = false;
// TODO define newterm and printindex
      }

      if (createSymbols)
      {
         createGlossary("symbols", new TeXCsRef("glssymbolsgroupname"), null,
           "slg", "sls", "slo");
         createSymbols = false;
      }

      if (createNumbers)
      {
         createGlossary("numbers", new TeXCsRef("glsnumbersgroupname"), null,
           "nlg", "nls", "nlo");
         createNumbers = false;
      }

      if (createAbbreviations)
      {
         registerControlSequence(new TextualContentCommand("glsxtrabbrvtype", "abbreviations"));
         createGlossary("abbreviations", new TeXCsRef("abbreviationsname"), null,
           "glg-abr", "gls-abr", "glo-abr");

         declareAbbreviationGlossary("abbreviations");

         if (!createAcronyms)
         {
            registerControlSequence(new GenericCommand(true, "acronymtype",  null,
              new TeXCsRef("glsxtrabbrvtype")));
         }

         createAbbreviations = false;
      }

      if (createAcronyms)
      {
         createGlossary("acronym", new TeXCsRef("acronymname"), null,
           "alg", "acr", "acn");

         registerControlSequence(new TextualContentCommand("acronymtype", 
              "acronym"));

         declareAbbreviationGlossary("acronym");

         createAcronyms = false;
      }

      getListener().putControlSequence(true, 
        new GlossarySection(section, isNumberedSection, isAutoLabel));

      boolean loadStyles = (loadList || loadTree || extra);

      byte orgAction = listener.getUndefinedAction();
      int orgCatCode = getParser().getCatCode('@');

      if (loadStyles)
      {
         substack.add(new TeXParserSetUndefAction(Undefined.ACTION_WARN));

         if (orgCatCode != TeXParser.TYPE_LETTER)
         {
            substack.add(listener.getControlSequence("makeatletter"));
         }
      }

      if (loadList)
      {
         substack.add(TeXParserActionObject.createInputAction(
           getParser(), "glossary-list.sty")); 

         loadList = false;
      }

      if (loadTree)
      {
         substack.add(TeXParserActionObject.createInputAction(
           getParser(), "glossary-tree.sty")); 

         loadTree = false;
      }

      if (extra)
      {
         String defFileName = "glossaries-extra-abbrstyles.def";
         TeXPath texPath = new TeXPath(getParser(), defFileName);

         if (texPath.exists())
         {
            substack.add(TeXParserActionObject.createInputAction(
              texPath)); 
         }
         else
         {
            TeXApp texApp = listener.getTeXApp();

            texApp.warning(getParser(), 
              texApp.getMessage(ABBREVIATION_STYLE_FILE_NOT_FOUND, defFileName));
         }
      }

      if (loadStyles)
      {
         if (orgCatCode != TeXParser.TYPE_LETTER)
         {
            substack.add(listener.getControlSequence("catcode"));
            substack.add(new UserNumber((int)'@'));
            substack.add(listener.getOther('='));
            substack.add(new UserNumber(orgCatCode));
         }

         substack.add(new TeXParserSetUndefAction(orgAction));
      }

      if (extra)
      {
         if (indexingOption == IndexingOption.UNSRT)
         {
            undefWarn = true;
            addField("group");
            addField("location");
         }
      }
      else
      {
         substack.add(new TeXCsRef("setacronymstyle"));
         substack.add(getListener().createGroup("long-short"));
      }

      if (initialStyle != null)
      {
         substack.add(new TeXCsRef("@glsstyle@"+initialStyle));
      }

      if (!substack.isEmpty())
      {
         if (getParser() == stack || stack == null)
         {
            getParser().push(substack, true);
         }
         else
         {
            substack.process(getParser(), stack);
         }
      }
   }

   @Override
   public void processOption(String option, TeXObject value)
    throws IOException
   {
      TeXParser parser = getParser();

      if (option.equals("nomain"))
      {
         createMain = false;
      }
      else if (option.equals("index"))
      {
         createIndex = true;
      }
      else if (option.equals("symbols"))
      {
         createSymbols = true;
      }
      else if (option.equals("numbers"))
      {
         createNumbers = true;
      }
      else if (option.equals("acronyms") || option.equals("acronym"))
      {
         if (value == null)
         {
            createAcronyms = true;
         }
         else
         {
            createAcronyms = value.toString(parser).equals("true");
         }
      }
      else if (option.equals("abbreviations"))
      {
         createAbbreviations = true;
      }
      else if (option.equals("accsupp"))
      {
         accsupp = true;
      }
      else if (option.equals("makeindex"))
      {
         indexingOption = IndexingOption.MAKEINDEX;
      }
      else if (option.equals("xindy"))
      {
         indexingOption = IndexingOption.XINDY;
      }
      else if (option.equals("record"))
      {
         record = (value == null ? "only" : value.toString(parser).trim());

         if (record.isEmpty())
         {
            record = "only";
         }

         if (record.equals("only") || record.equals("nameref"))
         {
            indexingOption = IndexingOption.UNSRT;
         }
      }
      else if (option.equals("indexcounter"))
      {
         indexcounter = true;
      }
      else if (option.equals("debug"))
      {
         if (value != null)
         {
            String debugOpt = value.toString(parser);

            setDebug(debugOpt);
         }
      }
      else if (option.equals("counter"))
      {
         registerControlSequence(new TextualContentCommand("glscounter", 
           value.toString(parser)));
      }
      else if (option.equals("stylemods"))
      {
         if (value == null)
         {
            stylemods = "";
         }
         else
         {
            stylemods = value.toString(parser);
         }
      }
      else if (option.equals("nolist"))
      {
         loadList = false;
      }
      else if (option.equals("notree"))
      {
         loadTree = false;
      }
      else if (option.equals("nolong"))
      {
         loadLong = false;
      }
      else if (option.equals("nosuper"))
      {
         loadSuper = false;
      }
      else if (option.equals("nostyles"))
      {
         loadList = false;
         loadTree = false;
         loadLong = false;
         loadSuper = false;
      }
      else if (option.equals("style"))
      {
         initialStyle = value.toString(parser);
      }
      else if (option.equals("undefaction"))
      {
         undefWarn = value.toString(parser).equals("warn");
      }
      else if (option.equals("section"))
      {
         if (value == null || value.isEmpty())
         {
            section = "section";
         }
         else
         {
            section = getParser().expandToString(value, null);
         }
      }
      else if (option.equals("numberedsection"))
      {
         if (value == null || value.isEmpty())
         {
            isNumberedSection = true;
            isAutoLabel = false;
         }
         else
         {
            String valStr = getParser().expandToString(value, null);

            if (valStr.equals("nolabel"))
            {
               isNumberedSection = true;
               isAutoLabel = false;
            }
            else if (valStr.equals("false"))
            {
               isNumberedSection = false;
               isAutoLabel = false;
            }
            else if (valStr.equals("autolabel"))
            {
               isNumberedSection = true;
               isAutoLabel = true;
            }
            else
            {
               isNumberedSection = false;
               isAutoLabel = true;
            }
         }
      }
      else if (extra && option.equals("record"))
      {
         if (value == null || !value.equals("off"))
         {
            addField("group");
            addField("location");
         }
      }
      else if (option.equals("postdot"))
      {
         ControlSequence cs = listener.getControlSequence("glsnopostdotfalse");
         cs.process(parser);
         registerControlSequence(new GlsPostDescription(this));
      }
      else if (option.equals("nopostdot"))
      {
         String valStr = "true";

         if (value != null)
         {
            valStr = parser.expandToString(value, parser);
         }

         ControlSequence cs = listener.getControlSequence("glsnopostdot"+valStr);
         cs.process(parser);
         registerControlSequence(new GlsPostDescription(this));
      }
      else if (extra && option.equals("postpunc"))
      {
         String valStr = "";

         if (value != null)
         {
            valStr = parser.expandToString(value, parser);
         }

         if (valStr.equals("none"))
         {
            ControlSequence cs = listener.getControlSequence("glsnopostdottrue");
            cs.process(parser);
            value = null;
         }
         else if (valStr.equals("dot"))
         {
            value = listener.createDataList(". ");
         }
         else if (valStr.equals("comma"))
         {
            value = listener.createDataList(", ");
         }

         registerControlSequence(new GlsPostDescription(value, this));
      }
   }

   public void setDebug(String debugOpt)
   {
      if (debugOpt.equals("showwrgloss") || debugOpt.equals("all"))
      {
          registerControlSequence(new AtGlsXtrWrGlossCounterMark(
           "@@glsxtrwrglosscountermark"));
      }
   }

   public boolean isAccSupp()
   {
      return accsupp;
   }

   public boolean isExtra()
   {
      return extra;
   }

   // in case both glossaries and glossaries-extra explicitly loaded
   public void addExtra(String styName, KeyValList extraOptions, TeXObjectList stack)
    throws IOException
   {
      if (extra)
      {
         // already set up
         return;
      }

      updateName(styName);

      loadParentOptions();

      extra = true;

      extraInit();
      addExtraDefinitions();

      if (extraOptions != null)
      {
         processOptions(extraOptions);
         addOptions(extraOptions);
      }
   }

   public void setup(KeyValList options, TeXObjectList stack) throws IOException
   {
      processOptions(options);
      postOptions(stack);
   }

   public TeXObject popOptArg(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (parser == stack || stack == null)
      {
         return parser.popNextArg('[', ']');
      }
      else
      {
         return stack.popArg(parser, '[', ']');
      }
   }

   public KeyValList popModifier(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject object;

      if (stack == null)
      {
         object = parser.peekStack();
      }
      else
      {
         object = stack.peekStack();
      }

      if (object instanceof CharObject)
      {
         KeyValList options = getModifierOptions((CharObject)object);

         if (options != null)
         {
            if (parser == stack || stack == null)
            {
               parser.popStack();
            }
            else
            {
               stack.popStack(parser);
            }
         }

         return options;
      }

      return null;
   }

   public KeyValList popOptKeyValList(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return popOptKeyValList(parser, stack, false);
   }

   public KeyValList popOptKeyValList(TeXParser parser, TeXObjectList stack,
     boolean checkModifier)
     throws IOException
   {
      KeyValList modOptions = null;

      if (checkModifier)
      {
         modOptions = popModifier(parser, stack);
      }

      KeyValList options = null;

      TeXObject arg = stack.peek();

      if (arg instanceof KeyValList)
      {
         stack.pop();
         options = (KeyValList)arg;
      }
      else
      {
         arg = popOptArg(parser, stack);

         if (arg != null)
         {
            options = KeyValList.getList(parser, arg);
         }
      }

      if (options == null)
      {
         options = modOptions;
      }
      else if (modOptions != null)
      {
         for (Iterator<String> it = modOptions.keySet().iterator(); it.hasNext(); )
         {
            String key = it.next();
            options.putIfAbsent(key, modOptions.get(key));
         }
      }

      return options;
   }


   public void undefWarnOrError(TeXObjectList stack,
     String messageTag, Object... params)
     throws IOException
   {
      TeXParser parser = getParser();

      if (undefWarn)
      {
         TeXApp texApp = parser.getListener().getTeXApp();

         texApp.warning(parser, texApp.getMessage(messageTag, params));

         if (((LaTeXParserListener)parser.getListener()).isInDocEnv())
         {
            stack.push(new TeXCsRef("glsxtrundeftag"));
         }
      }
      else
      {
         throw new LaTeXSyntaxException(parser, messageTag, params);
      }
   }

   public GlossaryEntry getDualEntry(String label)
   throws IOException
   {
      ControlSequence cs = getParser().getControlSequence("@glsxtr@labelprefixes");

      if (cs != null)
      {
         String[] list = getParser().expandToString(cs, getParser()).trim().split(",");

         for (String prefix : list)
         {
            String l = prefix+label;

            GlossaryEntry entry = getEntry(l);

            if (entry != null)
            {
               return entry;
            }
         }
      }

      return getEntry(label);
   }

   public GlossaryEntry getEntry(String label)
   {
      return entries.get(label);
   }

   public boolean isEntryDefined(String label)
   {
      return entries.containsKey(label);
   }

   public boolean isGlossaryDefined(String label)
   {
      return glossaries.containsKey(label);
   }

   public Glossary getGlossary(GlossaryEntry entry)
   {
      return getGlossary(entry.getType());
   }

   public Glossary getGlossary(String label)
   {
      return glossaries.get(label);
   }

   public void createGlossary(String label, TeXObject title)
     throws TeXSyntaxException
   {
      createGlossary(label, title, false);
   }

   public void createGlossary(String label, TeXObject title, boolean isIgnored)
     throws TeXSyntaxException
   {
      createGlossary(label, title, null, null, null, null, isIgnored, false,
        Overwrite.FORBID);
   }

   public void createGlossary(String label, TeXObject title,
    String counter, String glg, String gls, String glo)
     throws TeXSyntaxException
   {
      createGlossary(label, title, counter, glg, gls, glo, false, false,
        Overwrite.FORBID);
   }

   public void createGlossary(String label, TeXObject title,
    String counter, String glg, String gls, String glo, boolean isIgnored,
    boolean noHyper, Overwrite overwrite)
     throws TeXSyntaxException
   {
      if (isGlossaryDefined(label))
      {
         if (overwrite == Overwrite.FORBID)
         {
            throw new LaTeXSyntaxException(getParser(), 
              GLOSSARY_EXISTS, label);
         }
         else if (overwrite == Overwrite.SKIP)
         {
            return;
         }
      }

      Glossary glossary = new Glossary(label, title, counter, glg, gls, glo,
        isIgnored, noHyper);

      glossaries.put(label, glossary);

      if (noHyper)
      {
         declareNoHyperList(label);
      }

      if (isIgnored)
      {
         ignoredGlossaryTypes.add(label);
      }
      else
      {
         glossaryTypes.add(label);
      }
   }

   public Vector<String> getNonIgnoredGlossaries()
   {
      return glossaryTypes;
   }

   public void addEntry(Overwrite overwrite, GlossaryEntry entry)
     throws TeXSyntaxException
   {
      String label = entry.getLabel();

      if (isEntryDefined(label))
      {
         switch (overwrite)
         {
            case FORBID :
              throw new LaTeXSyntaxException(getParser(), 
                 ENTRY_EXISTS, label);
            case SKIP: return;
         }
      }

      Glossary glossary = getGlossary(entry);

      if (glossary == null)
      {
         throw new LaTeXSyntaxException(getParser(), 
          GLOSSARY_NOT_DEFINED, entry.getType());
      }

      glossary.add(label);

      entries.put(label, entry);
   }

   public void setFieldExpansionOn(boolean on)
   {
      expandFields = on;
   }

   public void setFieldExpansionOn(String field, boolean on)
   {
      expandField.put(field, Boolean.valueOf(on));
   }

   public boolean isFieldExpansionOn(String field)
   {
      Boolean expand = expandField.get(field);

      if (expand != null)
      {
         return expand;
      }

      return expandFields;
   }

   public void addField(String fieldName)
   {
      addField(fieldName, null, null);
   }

   public void addField(String fieldName, TeXObject defValue)
   {
      addField(fieldName, null, defValue);
   }

   public void addField(String fieldName, String internalFieldName)
   {
      addField(fieldName, internalFieldName, null);
   }

   public void addField(String fieldName, String internalFieldName, 
      TeXObject defValue)
   {
      if (!knownFields.contains(fieldName))
      {
         knownFields.add(fieldName);
      }

      if (internalFieldName != null && !fieldName.equals(internalFieldName))
      {
         if (fieldMap == null)
         {
            fieldMap = new HashMap<String,String>();
         }

         fieldMap.put(internalFieldName, fieldName);
      }

      if (defValue != null)
      {
         if (fieldDefaultValues == null)
         {
            fieldDefaultValues = new HashMap<String,TeXObject>();
         }

         fieldDefaultValues.put(fieldName, defValue);
      }
   }

   public void addDefaultFieldValues(GlossaryEntry entry, TeXObjectList stack)
     throws IOException
   {
      if (fieldDefaultValues != null)
      {
         for (Iterator<String> it=fieldDefaultValues.keySet().iterator(); 
               it.hasNext(); )
         {
            String field = it.next();

            TeXObject val = entry.get(field);

            if (val == null)
            {
               val = (TeXObject)fieldDefaultValues.get(field).clone();

               if (isFieldExpansionOn(field))
               {
                  val = TeXParserUtils.expandFully(val, getParser(), stack);
               }

               if (getParser().getDebugLevel() > 0)
               {
                  getParser().logMessage("ADDING DEFAULT FIELD "+field 
                    + " -> "+val.toString(getParser()));
               }

               entry.setField(field, val, stack);
            }
         }
      }

      if (indexingOption != IndexingOption.UNSRT)
      {
         TeXObject sort = entry.get("sort");

         if (sort == null)
         {
            TeXObject name = entry.get("name");

            if (name != null)
            {
               TeXObject val = (TeXObjectList)name.clone();

               if (getParser().getDebugLevel() > 0)
               {
                  getParser().logMessage("ADDING DEFAULT sort FIELD" 
                    + " -> "+val.toString(getParser()));
               }

               entry.setField("sort", val, stack);
            }
         }
      }
   }

   public String getFieldName(String internalFieldName)
   {
      if (fieldMap == null)
      {
         return internalFieldName;
      }

      String val = fieldMap.get(internalFieldName);

      return val == null ? internalFieldName : val;
   }

   public String getInternalFieldName(String fieldName)
   {
      if (fieldMap == null)
      {
         return fieldName;
      }

      for (Iterator<String> it=fieldMap.keySet().iterator(); it.hasNext(); )
      {
         String key = it.next();

         if (fieldName.equals(fieldMap.get(key)))
         {
            return key;
         }
      }

      return fieldName;
   }

   public boolean isKnownField(String field)
   {
      return knownFields.contains(field);
   }

   public String getTarget(GlsLabel glslabel)
   {
      ControlSequence cs = getParser().getControlSequence("glolinkprefix");
      String prefix = "";

      if (cs instanceof TextualContentCommand)
      {
         prefix = ((TextualContentCommand)cs).getText();
      }
      else if (cs instanceof GenericCommand)
      {
         prefix = ((GenericCommand)cs).getDefinition().toString(getParser());
      }

      return prefix + glslabel.getLabel();
   }

   public void setModifier(CharObject token, String key, TeXObject value)
   {
      KeyValList options = new KeyValList();
      options.put(key, value);
      setModifier(token, options);
   }

   public void setModifier(CharObject token, KeyValList options)
   {
      if (modifierOptions == null)
      {
         modifierOptions = new HashMap<Integer,KeyValList>();
      }

      modifierOptions.put(Integer.valueOf(token.getCharCode()), options);
   }

   public KeyValList getModifierOptions(CharObject token)
   {
      if (modifierOptions == null)
      {
         return null;
      }

      return modifierOptions.get(Integer.valueOf(token.getCharCode()));
   }

   public boolean isNoHyperGlossary(GlsType type)
   {
      return isNoHyperGlossary(type.getLabel());
   }

   public boolean isNoHyperGlossary(String type)
   {
      if (nohyperlist == null) return false;

      return nohyperlist.contains(type);
   }

   public void declareNoHyperList(String type)
   {
      if (nohyperlist == null)
      {
         nohyperlist = new Vector<String>();
      }

      nohyperlist.add(type);
   }

   public boolean isRegular(GlossaryEntry entry)
   {
      return isAttributeTrue(entry, "regular");
   }

   public boolean isRegular(GlsLabel glslabel)
   {
      return isAttributeTrue(glslabel, "regular");
   }

   public boolean isNotRegular(GlossaryEntry entry)
   {
      return isAttributeFalse(entry, "regular");
   }

   public boolean isNotRegular(GlsLabel glslabel)
   {
      return isAttributeFalse(glslabel, "regular");
   }

   public Category getCategory(GlsLabel glslabel)
   {
      return getCategory(glslabel.getEntry());
   }

   public Category getCategory(GlossaryEntry entry)
   {
      if (entry == null) return null;

      String catLabel = entry.getCategory();

      Category cat = null;

      if (categories != null)
      {
         cat = categories.get(catLabel);
      }

      if (cat == null)
      {
         cat = addCategory(catLabel);
      }

      return cat;
   }

   public Category getCategory(String categoryLabel)
   {
      if (categories == null || categoryLabel == null) return null;

      return categories.get(categoryLabel);
   }

   public Category addCategory(String categoryLabel)
   {
      if (categories == null)
      {
         categories = new HashMap<String,Category>();
      }

      Category category = new Category(categoryLabel);
      categories.put(categoryLabel, category);

      return category;
   }

   public boolean isAttributeTrue(GlossaryEntry entry, String attr)
   {
      Category category = getCategory(entry);

      return category != null && category.isAttributeTrue(attr);
   }

   public boolean isAttributeTrue(GlsLabel glslabel, String attr)
   {
      Category category = getCategory(glslabel);

      return category != null && category.isAttributeTrue(attr);
   }

   public boolean isAttributeTrue(String categoryLabel, String attr)
   {
      Category category = getCategory(categoryLabel);

      return category != null && category.isAttributeTrue(attr);
   }

   public boolean isAttributeFalse(GlossaryEntry entry, String attr)
   {
      Category category = getCategory(entry);

      return category != null && category.isAttributeFalse(attr);
   }

   public boolean isAttributeFalse(GlsLabel glslabel, String attr)
   {
      Category category = getCategory(glslabel);

      return category != null && category.isAttributeFalse(attr);
   }

   public boolean isAttributeFalse(String categoryLabel, String attr)
   {
      Category category = getCategory(categoryLabel);

      return category != null && category.isAttributeFalse(attr);
   }

   public boolean isAttributeValue(GlossaryEntry entry, 
       String attrName, String attrValue)
   {
      Category category = getCategory(entry);

      return category != null && category.isAttribute(attrName, attrValue);
   }

   public boolean isAttributeValue(GlsLabel glslabel, 
       String attrName, String attrValue)
   {
      Category category = getCategory(glslabel);

      return category != null && category.isAttribute(attrName, attrValue);
   }

   public boolean isAttributeValue(String categoryLabel, 
       String attrName, String attrValue)
   {
      Category category = getCategory(categoryLabel);

      return category != null && category.isAttribute(attrName, attrValue);
   }

   public String getAttribute(GlossaryEntry entry, String attrName)
   {
      Category category = getCategory(entry);

      if (category == null) return null;

      return getAttribute(category, attrName);
   }

   public String getAttribute(GlsLabel glslabel, String attrName)
   {
      Category category = getCategory(glslabel);

      if (category == null) return null;

      return getAttribute(category, attrName);
   }

   public String getAttribute(String categoryLabel, String attrName)
   {
      Category category = getCategory(categoryLabel);

      if (category == null) return null;

      return category.getAttribute(attrName);
   }

   public String getAttribute(Category category, String attrName)
   {
      return category.getAttribute(attrName);
   }

   public boolean hasAttribute(GlossaryEntry entry, String attrName)
   {
      Category category = getCategory(entry);

      if (category == null) return false;

      return hasAttribute(category, attrName);
   }

   public boolean hasAttribute(GlsLabel glslabel, String attrName)
   {
      Category category = getCategory(glslabel);

      if (category == null) return false;

      return hasAttribute(category, attrName);
   }

   public boolean hasAttribute(String categoryLabel, String attrName)
   {
      Category category = getCategory(categoryLabel);

      if (category == null) return false;

      return category.getAttribute(attrName) != null;
   }

   public boolean hasAttribute(Category category, String attrName)
   {
      return category.getAttribute(attrName) != null;
   }

   public void setAttribute(String categoryLabel, String attrName, String attrVal)
   {
      Category category;

      if (categories == null)
      {
         categories = new HashMap<String,Category>();
         category = new Category(categoryLabel);
         categories.put(categoryLabel, category);
      }
      else
      {
         category = categories.get(categoryLabel);

         if (category == null)
         {
            category = new Category(categoryLabel);
            categories.put(categoryLabel, category);
         }
      }

      category.setAttribute(attrName, attrVal);
   }

   public Glossary initPrintGloss(KeyValList options, 
     TeXObjectList stack) throws IOException
   {
      return initPrintGloss(indexingOption, options, stack);
   }

   public Glossary initPrintGloss(IndexingOption indexingOpt, KeyValList options, 
     TeXObjectList stack) throws IOException
   {
      TeXParser parser = getParser();

      TeXObject typeObj = null;
      TeXObject title = null;
      TeXObject styleObj = null;

      if (options != null)
      {
         typeObj = options.getExpandedValue("type", parser, stack);

         title = options.getExpandedValue("title", parser, stack);

         styleObj = options.getExpandedValue("style", parser, stack);

         if (indexingOpt == IndexingOption.UNSRT)
         {
            TeXObject val = options.get("flatten");

            if (val != null)
            {
               String flatten = parser.expandToString(val, stack).trim();

               if (!flatten.equals("false"))
               {
                  parser.putControlSequence(true,
                    new IfTrue("ifglsxtrprintglossflatten"));
               }
               else
               {
                  parser.putControlSequence(true,
                    new IfFalse("ifglsxtrprintglossflatten"));
               }
            }

            val = options.get("groups");

            if (val != null)
            {
               String groups = parser.expandToString(val, stack).trim();

               if (!groups.equals("false"))
               {
                  parser.putControlSequence(true,
                    new IfTrue("ifglsxtr@printgloss@groups"));
               }
               else
               {
                  parser.putControlSequence(true,
                    new IfFalse("ifglsxtr@printgloss@groups"));
               }
            }

            val = options.get("leveloffset");

            if (val != null)
            {
               String str = parser.expandToString(val, stack);

               boolean inc = str.startsWith("++");

               if (inc)
               {
                  str = str.substring(1);
               }

               int offset = 0;

               try
               {
                  offset = Integer.parseInt(str);
               }
               catch (NumberFormatException e)
               {
                  throw new TeXSyntaxException(e, parser,
                    TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
               }

               NumericRegister reg = parser.getSettings().getNumericRegister("@glsxtr@leveloffset");

               if (reg != null)
               {
                  if (inc)
                  {
                     offset = reg.number(parser) + offset;
                  }

                  reg.setValue(parser, new UserNumber(offset));
               }
            }
         }
      }

      String type = "main";

      if (typeObj == null)
      {
         type = parser.expandToString(getListener().getControlSequence(
            "glsdefaulttype"), stack);
      }
      else
      {
         type = typeObj.toString(parser);
      }

      Glossary glossary = getGlossary(type);

      if (glossary == null)
      {
         throw new LaTeXSyntaxException(parser, GLOSSARY_NOT_DEFINED, type);
      }

      getListener().putControlSequence(true, 
         new GlsType("currentglossary", "type", glossary));

      if (title == null)
      {
         title = glossary.getTitle();

         if (title == null)
         {
            title = getListener().getControlSequence("glossaryname");
         }
      }

      if (title != null)
      {
         getListener().putControlSequence(true, 
            new GenericCommand("glossarytitle", null, (TeXObject)title.clone()));
      }

      if (styleObj != null)
      {
         String style = parser.expandToString(styleObj, stack);

         ControlSequence cs = parser.getControlSequence("@glsstyle@"+style);

         if (cs == null)
         {
            TeXApp texApp = getListener().getTeXApp();
            texApp.warning(parser, 
              texApp.getMessage(GLOSSARY_STYLE_NOT_DEFINED, style));
         }
         else
         {
            TeXParserUtils.process(cs, parser, stack);
         }
      }

      ControlSequence cs = getListener().getControlSequence("glossentry");

      if (!(cs instanceof GlossEntryWithLabel))
      {
         parser.putControlSequence(true, 
          new AssignedControlSequence("gls@org@glossaryentryfield", cs));
      }

      cs = getListener().getControlSequence("subglossentry");

      if (!(cs instanceof SubGlossEntryWithLabel))
      {
         parser.putControlSequence(true, 
          new AssignedControlSequence("gls@org@glossarysubentryfield", cs));
      }

      getListener().putControlSequence(true,
        new GlossEntryWithLabel(this));

      getListener().putControlSequence(true,
        new SubGlossEntryWithLabel(this));

      return glossary;
   }

   /**
    * Issue a warning if no style has been set.
    * Only display the warning once otherwise it will cause too much
    * clutter.
    */  
   public void noStyleWarning()
   {
      if (!nostyleWarningIssued)
      {
         TeXApp texApp = getListener().getTeXApp();
         texApp.warning(getParser(), texApp.getMessage(GLOSSARY_NO_STYLE));
         nostyleWarningIssued = true;
      }
   }

   public void declareAbbreviationGlossary(String type)
   {
      if (abbreviationTypes == null)
      {
         abbreviationTypes = new Vector<String>();
         abbreviationTypes.add(type);
      }
      else if (!abbreviationTypes.contains(type))
      {
         abbreviationTypes.add(type);
      }
   }

   public boolean isAbbreviationGlossary(String type)
   {
      return abbreviationTypes != null && abbreviationTypes.contains(type);
   }

   public Vector<String> getAbbreviationGlossaries()
   {
      return abbreviationTypes;
   }

   public void addInnerFmtExclusion(String label, String field)
   {
      Vector<String> list = null;

      if (innerFmtExclusions == null)
      {
         innerFmtExclusions = new HashMap<String,Vector<String>>();
      }
      else
      {
         list = innerFmtExclusions.get(label);
      }

      if (list == null)
      {
         list = new Vector<String>();
         list.add(field);

         innerFmtExclusions.put(label, list);
      }
      else if (!list.contains(field))
      {
         list.add(field);
      }
   }

   public boolean isApplyInnerFmt(String label, String field)
   {
      if (innerFmtExclusions == null) return true;

      Vector<String> list = innerFmtExclusions.get(label);

      return list == null || !list.contains(field);
   }

   public Gls createGls(String csname, String prefix)
   {
      return createGls(csname, prefix, null, CaseChange.NO_CHANGE);
   }

   public Gls createGls(String csname, String prefix, KeyValList options)
   {
      return createGls(csname, prefix, options, CaseChange.NO_CHANGE);
   }

   public Gls createGls(String csname, String prefix, KeyValList options,
      CaseChange caseChange)
   {
      Gls gls = new Gls(csname, caseChange, this);

      gls.setEntryLabelPrefix(prefix);
      gls.setDefaultOptions(options);

      return gls;
   }

   public boolean isFieldIcon(String field)
   {
      return iconFields != null && iconFields.contains(field);
   }

   public void unsetIconField(String field)
   {
      if (iconFields != null)
      {
         iconFields.remove(field);
      }
   }

   public void setIconField(String field)
   {
      if (iconFields == null)
      {
         iconFields = new Vector<String>();
         iconFields.add(field);
      }
      else if (!iconFields.contains(field))
      {
         iconFields.add(field);
      }
   }

   public void registerTarget(GlsLabel glslabel, String targetname)
   {
      registerTarget(glslabel.getLabel(), targetname);
   }

   public void registerTarget(String label, String targetname)
   {
      Vector<String> targets = null;

      if (targetMap == null)
      {
         targetMap = new HashMap<String,Vector<String>>();
      }
      else
      {
         targets = targetMap.get(label);
      }

      if (targets == null)
      {
         targets = new Vector<String>();
         targetMap.put(label, targets);
      }

      targets.add(targetname);
   }

   public Vector<String> getTargets(GlsLabel glslabel)
   {
      return getTargets(glslabel.getLabel());
   }

   public Vector<String> getTargets(String label)
   {
      if (targetMap == null)
      {
         return null;
      }

      return targetMap.get(label);
   }

   private HashMap<String,GlossaryEntry> entries;

   private HashMap<String,Glossary> glossaries;

   private Vector<String> glossaryTypes;
   private Vector<String> ignoredGlossaryTypes;
   private Vector<String> abbreviationTypes;
   private Vector<String> iconFields;

   private HashMap<String,Category> categories;

   private boolean createMain = true;
   private boolean createAbbreviations = false;
   private boolean createAcronyms = false;
   private boolean createIndex = false;
   private boolean createSymbols = false;
   private boolean createNumbers = false;

   private boolean expandFields = true;

   private boolean undefWarn = false;

   private boolean extra = false;

   private HashMap<String,Boolean> expandField;

   private Vector<String> knownFields;

   private HashMap<String,String> fieldMap;
   private HashMap<String,TeXObject> fieldDefaultValues;

   private HashMap<Integer,KeyValList> modifierOptions;

   private HashMap<String,Vector<String>> innerFmtExclusions;

   private Vector<String> nohyperlist;

   private String section = "section";

   private boolean isNumberedSection = false;

   private boolean isAutoLabel = false;

   private boolean indexcounter = false;

   private String initialStyle = "list";
   private boolean loadList = true;
   private boolean loadTree = true;
   private boolean loadLong = true;
   private boolean loadSuper = true;
   private boolean accsupp = false;

   private String stylemods = null;

   private boolean nostyleWarningIssued = false;

   private IndexingOption indexingOption = IndexingOption.MAKEINDEX;
   private String record = "off";

   private HashMap<String,Vector<String>> targetMap;

   public static final String GLOSSARY_NOT_DEFINED 
    = "glossaries.glossary.not.defined";
   public static final String ENTRY_NOT_DEFINED 
    = "glossaries.entry.not.defined";
   public static final String FIELD_NOT_DEFINED 
    = "glossaries.field.not.defined";
   public static final String GLOSSARY_EXISTS 
    = "glossaries.glossary.exists";
   public static final String ENTRY_EXISTS 
    = "glossaries.entry.exists";
   public static final String GLOSSARY_STYLE_NOT_DEFINED 
    = "glossaries.glossary.style.not.defined";
   public static final String GLOSSARY_NO_STYLE 
    = "glossaries.glossary.no_style";
   public static final String ACRONYM_STYLE_DEFINED 
    = "glossaries.acronym.style.defined";
   public static final String ACRONYM_STYLE_NOT_DEFINED 
    = "glossaries.acronym.style.not.defined";
   public static final String ABBREVIATION_STYLE_DEFINED 
    = "glossaries.abbreviation.style.defined";
   public static final String ABBREVIATION_STYLE_NOT_DEFINED 
    = "glossaries.abbreviation.style.not.defined";
   public static final String ABBREVIATION_STYLE_FILE_NOT_FOUND 
    = "glossaries.abbreviation.style.file.not.found";
}
