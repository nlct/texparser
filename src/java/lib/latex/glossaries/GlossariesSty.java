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
      registerControlSequence(new TextualContentCommand("glsdefaulttype", "main"));
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
      registerControlSequence(new GobbleOpt("setentrycounter", 1, 1));
      registerControlSequence(new AtFirstOfOne("glsnumberformat"));
      registerControlSequence(new TextualContentCommand("glossarytitle", ""));
      registerControlSequence(new TextualContentCommand("glossarytoctitle", ""));
      registerControlSequence(new TextualContentCommand("glossarypreamble", ""));
      registerControlSequence(new TextualContentCommand("glossarypostamble", ""));

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
         registerControlSequence(new HyperTarget("@glstarget"));
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

         registerControlSequence(new GlsTarget());
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

      registerControlSequence(new AtGobble("glsentryitem"));
      registerControlSequence(new AtGobble("glssubentryitem"));
      registerControlSequence(new GlsPostDescription(this));

      registerControlSequence(new NewGlossaryEntry(this));
      registerControlSequence(new LoadGlsEntries());

      registerControlSequence(new TextualContentCommand("glsautoprefix", ""));

      registerControlSequence(new GenericCommand("glspostlinkhook"));
      registerControlSequence(new GenericCommand("glslinkcheckfirsthyperhook"));
      registerControlSequence(new GenericCommand("glslinkpostsetkeys"));
      registerControlSequence(new GenericCommand("@gls@setdefault@glslink@opts"));
      registerControlSequence(new DoAtGlsDisableHyperInList(this));
      registerControlSequence(new AtGlsAtAtLink(this));
      registerControlSequence(new AtGlsAtAtLink("glslink", this, true));
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

      registerControlSequence(new IfHasField("ifglshassymbol", "symbol", this));
      registerControlSequence(new IfHasField("ifglshasdesc", "description", this));
      registerControlSequence(new IfHasField("ifglshasshort", "short", this));
      registerControlSequence(new IfHasField("ifglshaslong", "long", this));

      TeXParserListener listener = getParser().getListener();

      setModifier(listener.getOther('*'), "hyper", new UserBoolean(true));
      setModifier(listener.getOther('+'), "hyper", new UserBoolean(false));

      registerControlSequence(new GobbleOpt("makeglossaries", 1, 0));

      registerControlSequence(new GlsAddStorageKey(this));

      if (extra)
      {
         addExtraDefinitions();
      }
   }

   protected void addExtraDefinitions()
   {
      registerControlSequence(
        new TextualContentCommand("GlsXtrDefaultResourceOptions", ""));

      getParser().getSettings().newcount("glsxtrresourcecount");

      registerControlSequence(new GlsXtrResourceFile());
      registerControlSequence(new GlsXtrLoadResources());

      registerControlSequence(new GlsXtrPostDescription(this));

      registerControlSequence(new GenericCommand("glsxtrfieldtitlecasecs", null,
         new TeXCsRef("glscapitalisewords")));

      registerControlSequence(new GlsEntryField("glsxtrusefield", null, this));
      registerControlSequence(new GlsEntryField("Glsxtrusefield", null,
         CaseChange.SENTENCE, this));
      registerControlSequence(new GlsEntryField("GLSxtrusefield", null,
         CaseChange.TO_UPPER, this));

      registerControlSequence(new TextualContentCommand("glsxtrundeftag", "??"));

      registerControlSequence(new TextualContentCommand(
         "abbreviationname", "Abbreviations"));

      registerControlSequence(new GlossEntryField("glossentrynameother", this));

      registerControlSequence(new GlsAddStorageKey("providestoragekey",
        Overwrite.SKIP, this));
   }

   @Override
   protected void preOptions(TeXObjectList stack) throws IOException
   {
      getListener().requirepackage(null, "etoolbox", false, stack);
      getListener().requirepackage(null, "textcase", false, stack);
      getListener().requirepackage(null, "mfirstuc", false, stack);
      getListener().requirepackage(null, "ifthen", false, stack);
      getListener().requirepackage(null, "keyval", false, stack);
      getListener().requirepackage(null, "datatool-base", true, stack);

      if (getParser().getControlSequence("chapter") != null)
      {
         section = "chapter";
      }
   }

   @Override
   protected void postOptions(TeXObjectList stack) throws IOException
   {
      super.postOptions(stack);

      TeXObjectList substack = getListener().createStack();

      if (createMain)
      {
         createGlossary("main", new TeXCsRef("glossaryname"), null,
           "glg", "gls", "glo");
         createMain = false;
      }

      getListener().putControlSequence(true, 
        new GlossarySection(section, isNumberedSection, isAutoLabel));

      if (loadList)
      {
         getListener().loadpackage(null, "glossary-list", false, true, substack);

         loadList = false;
      }

      if (loadTree)
      {
         getListener().loadpackage(null, "glossary-tree", false, true, substack);

         loadTree = false;
      }

      if (extra)
      {
         extraPostOptions(stack);
      }

      if (initialStyle != null)
      {
         substack.add(new TeXCsRef("@glsstyle@"+initialStyle));
      }

      if (!substack.isEmpty())
      {
         substack.process(getParser(), stack);
      }
   }

   protected void extraPostOptions(TeXObjectList stack) throws IOException
   {
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
         initialStyle = getParser().expandToString(value, null);
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

      extraPostOptions(stack);
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
      knownFields.add(fieldName);

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

   public void addDefaultFieldValues(GlossaryEntry entry)
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

               if (isFieldExpansionOn(field) && val instanceof Expandable)
               {
                  TeXObjectList expanded = ((Expandable)val).expandfully(getParser());

                  if (expanded != null)
                  {
                     val = expanded;
                  }
               }

               entry.setField(field, val);
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

   public boolean isKnownField(String field)
   {
      return knownFields.contains(field);
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
         modifierOptions = new HashMap<CharObject,KeyValList>();
      }

      modifierOptions.put(token, options);
   }

   public KeyValList getModifierOptions(CharObject token)
   {
      if (modifierOptions == null)
      {
         return null;
      }

      return modifierOptions.get(token);
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

      return getCategory(entry.getCategory());
   }

   public Category getCategory(String categoryLabel)
   {
      if (categories == null || categoryLabel == null) return null;

      return categories.get(categoryLabel);
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

      return getAttribute(category.getLabel(), attrName);
   }

   public String getAttribute(GlsLabel glslabel, String attrName)
   {
      Category category = getCategory(glslabel);

      if (category == null) return null;

      return getAttribute(category.getLabel(), attrName);
   }

   public String getAttribute(String categoryLabel, String attrName)
   {
      Category category = getCategory(categoryLabel);

      if (category == null) return null;

      return category.getAttribute(attrName);
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
      TeXParser parser = getParser();

      TeXObject typeObj = null;
      TeXObject title = null;
      TeXObject styleObj = null;

      if (options != null)
      {
         typeObj = options.getExpandedValue("type", parser, stack);

         title = options.getExpandedValue("title", parser, stack);

         styleObj = options.getExpandedValue("style", parser, stack);
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
            title = getListener().getControlSequence("glossarytitle");
         }
      }

      if (title != null)
      {
         getListener().putControlSequence(true, 
            new GenericCommand("glossarytitle", null, (TeXObject)title.clone()));
      }

      TeXObjectList substack = getListener().createStack();

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
            substack.add(cs);
         }
      }

      substack.add(getListener().getControlSequence("let"));
      substack.add(new TeXCsRef("gls@org@glossaryentryfield"));
      substack.add(getListener().getControlSequence("glossentry"));

      substack.add(getListener().getControlSequence("let"));
      substack.add(new TeXCsRef("gls@org@glossarysubentryfield"));
      substack.add(getListener().getControlSequence("subglossentry"));

      substack.process(parser);

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

   private HashMap<String,GlossaryEntry> entries;

   private HashMap<String,Glossary> glossaries;

   private Vector<String> glossaryTypes;
   private Vector<String> ignoredGlossaryTypes;

   private HashMap<String,Category> categories;

   private boolean createMain = true;

   private boolean expandFields = true;

   private boolean undefWarn = false;

   private boolean extra = false;

   private HashMap<String,Boolean> expandField;

   private Vector<String> knownFields;

   private HashMap<String,String> fieldMap;
   private HashMap<String,TeXObject> fieldDefaultValues;

   private HashMap<CharObject,KeyValList> modifierOptions;

   private Vector<String> nohyperlist;

   private String section = "section";

   private boolean isNumberedSection = false;

   private boolean isAutoLabel = false;

   private String initialStyle = "list";
   private boolean loadList = true;
   private boolean loadTree = true;
   private boolean loadLong = true;
   private boolean loadSuper = true;

   private boolean nostyleWarningIssued = false;

   public static final String GLOSSARY_NOT_DEFINED 
    = "glossaries.glossary.not.defined";
   public static final String ENTRY_NOT_DEFINED 
    = "glossaries.entry.not.defined";
   public static final String GLOSSARY_EXISTS 
    = "glossaries.glossary.exists";
   public static final String ENTRY_EXISTS 
    = "glossaries.entry.exists";
   public static final String GLOSSARY_STYLE_NOT_DEFINED 
    = "glossaries.glossary.style.not.defined";
   public static final String GLOSSARY_NO_STYLE 
    = "glossaries.glossary.no_style";
}
