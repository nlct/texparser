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

      registerControlSequence(new NewGlossaryEntry(this));
      registerControlSequence(new LoadGlsEntries());

      registerControlSequence(new GenericCommand("glspostlinkhook"));
      registerControlSequence(new GenericCommand("glslinkcheckfirsthyperhook"));
      registerControlSequence(new GenericCommand("glslinkpostsetkeys"));
      registerControlSequence(new GenericCommand("@gls@setdefault@glslink@opts"));
      registerControlSequence(new AtGlsAtAtLink(this));
      registerControlSequence(new AtGlsAtLink(this));
      registerControlSequence(new GlsEntryFmt(this));
      registerControlSequence(new GlsGenEntryFmt(this));
      registerControlSequence(new AtFirstOfOne("glstextformat"));
      registerControlSequence(new AtSecondOfTwo("glsdonohyperlink"));
      registerControlSequence(new AtSecondOfTwo("@glslink"));
      registerControlSequence(new TextualContentCommand("glolinkprefix", "glo:"));

      registerControlSequence(new GenericCommand("glscapitalisewords", null,
         new TeXCsRef("capitalisewords")));

      NewIf.createConditional(true, getParser(), "ifKV@glslink@hyper");

      registerControlSequence(new GenericCommand(true, "glsifhyperon", null,
           new TeXObject[]{new TeXCsRef("ifKV@glslink@hyper"), 
             getListener().getParam(1), new TeXCsRef("else"),
             getListener().getParam(2), new TeXCsRef("fi")}));

      registerControlSequence(new Gls(this));
      registerControlSequence(new IfGlsUsed(this));
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
   }

   @Override
   protected void preOptions() throws IOException
   {
      getListener().requirepackage(null, "etoolbox", false);
      getListener().requirepackage(null, "mfirstuc", false);
      getListener().requirepackage(null, "ifthen", false);
      getListener().requirepackage(null, "keyval", false);
      getListener().requirepackage(null, "datatool-base", true);
   }

   @Override
   protected void postOptions() throws IOException
   {
      super.postOptions();

      if (createMain)
      {
         createGlossary("main", new TeXCsRef("glossaryname"));
      }

      if (extra)
      {
         extraPostOptions();
      }
   }

   protected void extraPostOptions() throws IOException
   {
   }

   @Override
   public void processOption(String option, TeXObject value)
    throws IOException
   {
      if (option.equals("nomain"))
      {
         createMain = false;
      }
      else if (extra && option.equals("record"))
      {
         if (value == null || !value.equals("off"))
         {
            addField("group");
            addField("location");
         }
      }
   }

   public boolean isExtra()
   {
      return extra;
   }

   // in case both glossaries and glossaries-extra explicitly loaded
   public void addExtra(String styName, KeyValList extraOptions)
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

      extraPostOptions();
   }

   public void undefWarnOrError(TeXParser parser, TeXObjectList stack,
     String messageTag, Object... params)
     throws IOException
   {
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
        NewCommand.OVERWRITE_FORBID);
   }

   public void createGlossary(String label, TeXObject title,
    String counter, String glg, String gls, String glo, boolean isIgnored,
    boolean noHyper, byte overwrite)
     throws TeXSyntaxException
   {
      if (isGlossaryDefined(label))
      {
         if (overwrite == NewCommand.OVERWRITE_FORBID)
         {
            throw new LaTeXSyntaxException(getParser(), 
              GLOSSARY_EXISTS, label);
         }
         else if (overwrite == NewCommand.OVERWRITE_SKIP)
         {
            return;
         }
      }

      Glossary glossary = new Glossary(label, title, counter, glg, gls, glo,
        isIgnored, noHyper);

      glossaries.put(label, glossary);

      if (isIgnored)
      {
         ignoredGlossaryTypes.add(label);
      }
      else
      {
         glossaryTypes.add(label);
      }
   }

   public void addEntry(byte overwrite, GlossaryEntry entry)
     throws TeXSyntaxException
   {
      String label = entry.getLabel();

      if (isEntryDefined(label))
      {
         switch (overwrite)
         {
            case NewCommand.OVERWRITE_FORBID :
              throw new LaTeXSyntaxException(getParser(), 
                 ENTRY_EXISTS, label);
            case NewCommand.OVERWRITE_SKIP:
              return;
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

   private HashMap<String,GlossaryEntry> entries;

   private HashMap<String,Glossary> glossaries;

   private Vector<String> glossaryTypes;
   private Vector<String> ignoredGlossaryTypes;

   private boolean createMain = true;

   private boolean expandFields = true;

   private boolean undefWarn = false;

   private boolean extra = false;

   private HashMap<String,Boolean> expandField;

   private Vector<String> knownFields;

   private HashMap<String,String> fieldMap;
   private HashMap<String,TeXObject> fieldDefaultValues;

   public static final String GLOSSARY_NOT_DEFINED 
    = "glossaries.glossary.not.defined";
   public static final String ENTRY_NOT_DEFINED 
    = "glossaries.entry.not.defined";
   public static final String GLOSSARY_EXISTS 
    = "glossaries.glossary.exists";
   public static final String ENTRY_EXISTS 
    = "glossaries.entry.exists";
}
