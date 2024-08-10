/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Vector;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.PadNumber;
import com.dickimawbooks.texparserlib.latex.*;

/**
 * Limited support for datagidx.sty. Mainly intended for datatooltk.
 */
public class DataGidxSty extends LaTeXSty
{
   public DataGidxSty(KeyValList options, LaTeXParserListener listener, 
      boolean loadParentOptions)
   throws IOException
   {
      super(options, "datagidx", listener, loadParentOptions);

      additionalKeyMap = new HashMap<String,String>();
      additionalKeyMap.put("description", "Description");
      additionalKeyMap.put("symbol", "Symbol");
      additionalKeyMap.put("short", "Short");
      additionalKeyMap.put("shortplural", "ShortPlural");
      additionalKeyMap.put("long", "Long");
      additionalKeyMap.put("longplural", "LongPlural");
      additionalKeyMap.put("see", "See");
      additionalKeyMap.put("seealso", "SeeAlso");
   }

   @Override
   protected void preOptions(TeXObjectList stack) throws IOException
   {
      datatoolSty = (DataToolSty)getListener().requirepackage(
         null, "datatool", true, stack);

      datatoolSty.setDataGidxSty(this);

      datatoolSty.createDataBase("datagidx", true);

      datatoolSty.addNewColumn("datagidx", "Glossary");
      datatoolSty.addNewColumn("datagidx", "Title");
      datatoolSty.addNewColumn("datagidx", "Heading");
      datatoolSty.addNewColumn("datagidx", "PostHeading");
      datatoolSty.addNewColumn("datagidx", "MultiCols");
      datatoolSty.addNewColumn("datagidx", "Sort");
      datatoolSty.addNewColumn("datagidx", "Style");
      datatoolSty.addNewColumn("datagidx", "ShowGroups");
      
   }

   @Override
   public void addDefinitions()
   {
      registerControlSequence(new GenericCommand("newtermlabelhook"));

      registerControlSequence(new GenericCommand(DEFAULT_DATABASE_NAME_TL));
      registerControlSequence(new GenericCommand(POST_HEADING_TL));
      registerControlSequence(new TextualContentCommand(MULTICOLS_TL, "multicols"));
      registerControlSequence(new TextualContentCommand(STYLE_TL, "index"));
      registerControlSequence(new TextualContentCommand(SHOWGROUPS_TL, "false"));
      registerControlSequence(new DTLgidxSetDefaultDB());
      registerControlSequence(new NewGidx(this));
      registerControlSequence(new LoadGidx(this));
      registerControlSequence(new NewTerm(this));
      registerControlSequence(new NewAcro(this));

      TeXObjectList list = getListener().createStack();

      if (getParser().getControlSequence("chapter") == null)
      {
         list.add(new TeXCsRef("section"));
      }
      else
      {
         list.add(new TeXCsRef("chapter"));
      }

      list.add(getListener().getOther('*'));

      registerControlSequence(new GenericCommand(true, HEADING_TL,
        null, list));

      list = getListener().createStack();
      list.add(new TeXCsRef("DTLsortdata"));
      list.add(getListener().getOther('['));
      list.addAll(getListener().createString("save-group-key=LetterGroup"));
      list.add(getListener().getOther(']'));

      list.add(TeXParserUtils.createGroup(getListener(),
        new TeXCsRef("DTLgidxCurrentdb")));

      Group grp = getListener().createGroup();
      list.add(grp);

      grp.addAll(getListener().createString("HierSort="));
      grp.add(getListener().createGroup("replacements=Sort"));
      grp.addAll(getListener().createString(",FirstId"));

      registerControlSequence(new GenericCommand(true, SORT_TL, null, list));

      registerControlSequence(new AtFirstOfOne("__datagidx_punc:n"));
      registerControlSequence(new AtGobble("DTLgidxIgnore"));
      registerControlSequence(new AtGobble("DTLgidxGobble"));
      registerControlSequence(new AtFirstOfOne("DTLgidxNoFormat"));

      registerControlSequence(new AtFirstOfOne("DTLgidxMac"));
      registerControlSequence(new AtFirstOfOne("DTLgidxSaint"));

      registerControlSequence(new AtSecondOfTwo("DTLgidxPlace"));
      registerControlSequence(new AtSecondOfTwo("DTLgidxSubject"));

      registerControlSequence(new GenericCommand("newtermlabelhook"));
      registerControlSequence(new GenericCommand("newtermsorthook"));

      registerControlSequence(new LaTeXGenericCommand(true,
       "DTLgidxStripBackslash", "m", TeXParserUtils.createStack(getListener(),
         new TeXCsRef("cs_to_str:N"), getListener().getParam(1))));

      registerControlSequence(new LaTeXGenericCommand(true,
       "DTLgidxName", "mm", TeXParserUtils.createStack(getListener(),
         getListener().getParam(1), new TeXCsRef("space"),
         getListener().getParam(2))));

      registerControlSequence(new LaTeXGenericCommand(true,
       "DTLgidxNameNum", "m", TeXParserUtils.createStack(getListener(),
         new TeXCsRef("@Roman"),
          TeXParserUtils.createGroup(getListener(), getListener().getParam(1)))));

      registerControlSequence(new LaTeXGenericCommand(true,
       "DTLgidxOffice", "mm", TeXParserUtils.createStack(getListener(),
         getListener().getParam(2), new TeXCsRef("space"),
         getListener().getOther('('), getListener().getParam(1),
         getListener().getOther(')')
         )));

      registerControlSequence(new LaTeXGenericCommand(true,
       "DTLgidxRank", "mm", TeXParserUtils.createStack(getListener(),
         getListener().getParam(1), new TeXCsRef("nobreakspace"),
         getListener().getParam(2))));

      registerControlSequence(new LaTeXGenericCommand(true,
       "DTLgidxParticle", "mm", TeXParserUtils.createStack(getListener(),
         getListener().getParam(1), new TeXCsRef("nobreakspace"),
         getListener().getParam(2))));

      registerControlSequence(new LaTeXGenericCommand(true,
       "DTLgidxParen", "m", TeXParserUtils.createStack(getListener(),
         new TeXCsRef("space"),
         getListener().getOther('('), getListener().getParam(1),
         getListener().getOther(')')
         )));

      registerControlSequence(new DatagidxWordifyGreek());
   }

   protected void addToCatalogue(TeXObjectList stack,
      KeyValList options, String dbLabel, TeXObject title)
    throws IOException
   {
      TeXObject heading = null;
      TeXObject postHeading = null;
      String multicols = null;
      String style = null;
      Boolean showGroups = null;
      TeXObject sort = null;

      if (options != null)
      {
         sort = options.getValue("sort");
         style = options.getString("style", getParser(), stack);
         heading = options.getValue("heading");
         postHeading = options.getValue("post-heading");

         if (postHeading == null)
         {
            postHeading = options.getValue("postheading");
         }

         Boolean boolVal = options.getBoolean("balance", getParser(), stack);

         if (boolVal != null)
         {
            multicols = (boolVal.booleanValue() ? "multicols" : "multicols*");
         }

         showGroups = options.getBoolean("show-groups", getParser(), stack);

         if (showGroups == null)
         {
            showGroups = options.getBoolean("showgroups", getParser(), stack);
         }
      }

      datatoolSty.addNewRow("datagidx");

      datatoolSty.addNewEntry("datagidx", "Glossary", 
         getListener().createString(dbLabel));

      datatoolSty.addNewEntry("datagidx", "Title", title);

      if (heading == null)
      {
         ControlSequence cs = getParser().getControlSequence(HEADING_TL);

         if (cs != null)
         {
            heading = TeXParserUtils.expandOnce(cs, getParser(), stack);
         }
      }

      if (heading != null)
      {
         datatoolSty.addNewEntry("datagidx", "Heading", heading);
      }

      if (postHeading == null)
      {
         ControlSequence cs = getParser().getControlSequence(POST_HEADING_TL);

         if (cs != null)
         {
            postHeading = TeXParserUtils.expandOnce(cs, getParser(), stack);
         }
      }

      if (postHeading != null)
      {
         datatoolSty.addNewEntry("datagidx", "PostHeading", postHeading);
      }

      if (multicols == null)
      {
         ControlSequence cs = getParser().getControlSequence(MULTICOLS_TL);

         if (cs == null)
         {
            multicols = "multicols";
         }
         else
         {
            multicols = getParser().expandToString(cs, stack);
         }
      }

      datatoolSty.addNewEntry("datagidx", "MultiCols",
        getListener().createString(multicols));

      if (sort == null)
      {
         ControlSequence cs = getParser().getControlSequence(SORT_TL);

         if (cs != null)
         {
            sort = TeXParserUtils.expandOnce(cs, getParser(), stack);
         }
      }

      if (sort != null)
      {
         datatoolSty.addNewEntry("datagidx", "Sort", sort);
      }

      if (style == null)
      {
         ControlSequence cs = getParser().getControlSequence(STYLE_TL);

         if (cs == null)
         {
            style = "index";
         }
         else
         {
            style = getParser().expandToString(cs, stack);
         }
      }

      datatoolSty.addNewEntry("datagidx", "Style",
        getListener().createString(style));

      if (showGroups == null)
      {
         ControlSequence cs = getParser().getControlSequence(SHOWGROUPS_TL);

         if (cs == null)
         {
            showGroups = Boolean.FALSE;
         }
         else
         {
            showGroups = Boolean.valueOf(getParser().expandToString(cs, stack));
         }
      }

      datatoolSty.addNewEntry("datagidx", "ShowGroups",
        getListener().createString(showGroups.toString()));
   }

   public DataBase createDataBase(TeXObjectList stack,
      KeyValList options, String dbLabel, TeXObject title)
    throws IOException
   {
      addToCatalogue(stack, options, dbLabel, title);

      return createDataBase(dbLabel);
   }

   protected DataBase createDataBase(String dbLabel)
   throws IOException
   {
      DataBase db = datatoolSty.createDataBase(dbLabel, true);

      DataToolHeader header = datatoolSty.addNewColumn(dbLabel, "Label");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "Used");
      header.setType(DatumType.INTEGER);

      header = datatoolSty.addNewColumn(dbLabel, "Location");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "UnsafeLocation");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "CurrentLocation");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "FirstId");
      header.setType(DatumType.INTEGER);

      header = datatoolSty.addNewColumn(dbLabel, "Name");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "Text");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "Plural");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "Parent");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "Child");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "Description");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "HierSort");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "LetterGroup");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "Symbol");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "Long");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "LongPlural");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "Short");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "ShortPlural");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "See");
      header.setType(DatumType.STRING);

      header = datatoolSty.addNewColumn(dbLabel, "SeeAlso");
      header.setType(DatumType.STRING);

      if (gidxDatabases == null)
      {
         gidxDatabases = new Vector<String>();
      }

      gidxDatabases.add(dbLabel);

      if (databaseMap == null)
      {
         databaseMap = new HashMap<String,DataBase>();
      }

      databaseMap.put(dbLabel, db);

      return db;
   }

   public void addTerm(KeyValList options, TeXObject name, TeXObjectList stack)
    throws IOException
   {
      TeXParser parser = getParser();

      String label = null;
      String database = null;
      TeXObject sortVal = null;

      if (options != null)
      {
         database = options.getString("database", parser, stack);
         label = options.getString("label", parser, stack);
         sortVal = options.getValue("sort");
      }

      if (database == null)
      {
         ControlSequence cs = parser.getControlSequence(DEFAULT_DATABASE_NAME_TL);

         if (cs != null)
         {
            database = parser.expandToString(cs, stack);
         }
         else
         {
            database = getLatestIndexDataBaseName();

            if (database == null)
            {
               throw new LaTeXSyntaxException(parser,
                 ERROR_TERM_NO_DATABASES);
            }
         }
      }

      DataBase db = getIndexDataBase(database);

      if (db == null)
      {
         throw new LaTeXSyntaxException(parser,
           ERROR_DATABASE_NOT_DEFINED, database);
      }

      if (label == null || sortVal == null)
      {
         parser.startGroup();

         parser.putControlSequence(new AtGobble("glsadd"));
         parser.putControlSequence(new AtFirstOfOne("MakeUppercase"));
         parser.putControlSequence(new AtFirstOfOne("MakeTextUppercase"));
         parser.putControlSequence(new AtFirstOfOne("MakeLowercase"));
         parser.putControlSequence(new AtFirstOfOne("MakeTextLowercase"));
         parser.putControlSequence(new AtFirstOfOne("acronymfont"));
         parser.putControlSequence(new AtFirstOfOne("textrm"));
         parser.putControlSequence(new AtFirstOfOne("texttt"));
         parser.putControlSequence(new AtFirstOfOne("textsf"));
         parser.putControlSequence(new AtFirstOfOne("textsc"));
         parser.putControlSequence(new AtFirstOfOne("textbf"));
         parser.putControlSequence(new AtFirstOfOne("textmd"));
         parser.putControlSequence(new AtFirstOfOne("textit"));
         parser.putControlSequence(new AtFirstOfOne("textsl"));
         parser.putControlSequence(new AtFirstOfOne("emph"));
         parser.putControlSequence(new AtFirstOfOne("textsuperscript"));
         parser.putControlSequence(new AtFirstOfOne("ensuremath"));

         TeXParserUtils.process(
            getListener().getControlSequence("datagidxwordifygreek"),
            parser, stack);

         if (label == null)
         {
            boolean addGroup = (sortVal != null);

            if (addGroup)
            {
               parser.startGroup();
            }

            parser.putControlSequence(new AtGobble("DTLgidxParen"));
            parser.putControlSequence(new AtGobble("__datagidx_punc:n"));
            parser.putControlSequence(new AtSecondOfTwo("DTLgidxName"));
            parser.putControlSequence(new AtSecondOfTwo("DTLgidxOffice"));

            parser.putControlSequence(new DatagidxAtInvert("DTLgidxPlace"));
            parser.putControlSequence(new DatagidxAtInvert("DTLgidxSubject"));
            parser.putControlSequence(new DatagidxAtBothOfTwo("DTLgidxParticle"));

            ControlSequence cs = getParser().getControlSequence("newtermlabelhook");

            if (cs != null && !cs.isEmpty())
            {
               TeXParserUtils.process(cs, parser, stack);
            }

            label = parser.expandToString((TeXObject)name.clone(), stack);
            label = label.replaceAll("[=\\-]", "");

            if (addGroup)
            {
               parser.endGroup();
            }
         }

         if (sortVal == null)
         {
            parser.putControlSequence(new AtFirstOfOne("__datagidx_punc:n", false));

            parser.putControlSequence(new LaTeXGenericCommand(true,
              "DTLgidxName", "mm", TeXParserUtils.createStack(
                getListener(), getListener().getParam(2),
                new TeXCsRef("__datagidx_punc:n"),
                new TeXCsRef("datatoolpersoncomma"),
                getListener().getParam(1)
              )));

            parser.putControlSequence(new LaTeXGenericCommand(true,
              "DTLgidxOffice", "mm", TeXParserUtils.createStack(
                getListener(), getListener().getParam(2),
                new TeXCsRef("__datagidx_punc:n"),
                new TeXCsRef("datatoolpersoncomma"),
                getListener().getParam(1)
              )));

            parser.putControlSequence(new LaTeXGenericCommand(true,
              "DTLgidxPlace", "mm", TeXParserUtils.createStack(
                getListener(), getListener().getParam(2),
                new TeXCsRef("__datagidx_punc:n"),
                new TeXCsRef("datatoolplacecomma"),
                getListener().getParam(1)
              )));

            parser.putControlSequence(new LaTeXGenericCommand(true,
              "DTLgidxSubject", "mm", TeXParserUtils.createStack(
                getListener(), getListener().getParam(2),
                new TeXCsRef("__datagidx_punc:n"),
                new TeXCsRef("datatoolsubjectcomma"),
                getListener().getParam(1)
              )));


            parser.putControlSequence(new LaTeXGenericCommand(true,
              "DTLgidxParen", "m", TeXParserUtils.createStack(
                getListener(),
                new TeXCsRef("__datagidx_punc:n"),
                new TeXCsRef("datatoolparenstart"),
                getListener().getParam(1)
              )));

            parser.putControlSequence(new LaTeXGenericCommand(true,
              "DTLgidxMac", "m",
              getListener().createString("Mac")));

            parser.putControlSequence(new LaTeXGenericCommand(true,
              "DTLgidxSaint", "m",
              getListener().createString("Saint")));

            parser.putControlSequence(new LaTeXGenericCommand(true,
              "DTLgidxRank", "mm", TeXParserUtils.createStack(
               getListener(), getListener().getParam(2),
               getListener().getOther('.'))));

            parser.putControlSequence(new LaTeXGenericCommand(true,
              "DTLgidxParticle", "mm", TeXParserUtils.createStack(
               getListener(), getListener().getParam(2),
               getListener().getOther('.'))));

            parser.putControlSequence(new PadNumber("DTLgidxNameNum", 2));
            parser.putControlSequence(new AtGobble("DTLgidxIgnore"));

            ControlSequence cs = getParser().getControlSequence("newtermsorthook");

            if (cs != null && !cs.isEmpty())
            {
               TeXParserUtils.process(cs, parser, stack);
            }

            sortVal = TeXParserUtils.expandFully((TeXObject)name.clone(),
              getParser(), stack);
         }

         parser.endGroup();
      }

      datatoolSty.addNewRow(database);

      if (labelToDatabase == null)
      {
         labelToDatabase = new HashMap<String,String>();
      }

      labelToDatabase.put(label, database);

      datatoolSty.addNewEntry(database, "Label", 
         getListener().createString(label));

      datatoolSty.addNewEntry(database, "Name", name);
      datatoolSty.addNewEntry(database, "Sort", sortVal);

      DataToolEntryRow parentRow = null;
      TeXObject text = null;
      TeXObject plural = null;

      if (options != null)
      {
         String parent = options.getString("parent", parser, stack);

         if (parent != null)
         {
            String parentDatabase = labelToDatabase.get(parent);

            if (parentDatabase == null)
            {
               throw new LaTeXSyntaxException(getParser(),
                 ERROR_TERM_NOT_DEFINED, parent);
            }

            if (parentDatabase.equals(database))
            {
               parentRow = getTerm(db, parent, stack);
            }
            else
            {
               parentRow = getTerm(parentDatabase, parent, stack);
            }

            datatoolSty.addNewEntry(database, "Parent", 
             getListener().createString(parent));

            DataToolHeader header = db.getHeader("HierSort");
            int columnIndex = header.getColumnIndex();

            DataToolEntry entry = parentRow.getEntry(columnIndex);

            if (entry == null)
            {
               header = db.getHeader("Child");
               columnIndex = header.getColumnIndex();

               entry = parentRow.getEntry(columnIndex);

               if (entry == null)
               {
                  entry = new DataToolEntry(datatoolSty, columnIndex,
                   getListener().createString(label));
                  parentRow.add(entry);
               }
               else
               {
                  String labelList = parser.expandToString(entry.getContents(),
                     stack);

                  if (labelList.isEmpty())
                  {
                     labelList = label;
                  }
                  else
                  {
                     labelList += ","+ label;
                  }

                  entry.setContents(getListener().createString(labelList));
                  entry = null;
               }

               header = db.getHeader("Sort");
               columnIndex = header.getColumnIndex();

               entry = parentRow.getEntry(columnIndex);
            }

            if (entry == null)
            {
               TeXObject hierSort = (TeXObjectList)entry.getContents().clone();

               TeXObjectList list = TeXParserUtils.toList(hierSort, parser);
               list.add(new TeXCsRef("datatoolctrlboundary"));
               list.add(new TeXCsRef("datatoolasciistart"));
               list.add((TeXObject)sortVal.clone(), true);

               datatoolSty.addNewEntry(database, "HierSort", hierSort);
            }
         }

         text = options.getValue("text");
         plural = options.getValue("plural");

         TeXObject shortVal = null;
         TeXObject shortPluralVal = null;
         TeXObject longVal = null;
         TeXObject longPluralVal = null;

         for (Iterator<String> it = additionalKeyMap.keySet().iterator();
              it.hasNext(); )
         {
            String key = it.next();

            TeXObject obj = options.get(key);

            if (obj != null)
            {
               if (key.equals("short"))
               {
                  shortVal = obj;
               }
               else if (key.equals("shortplural"))
               {
                  shortPluralVal = obj;
               }
               else if (key.equals("long"))
               {
                  longVal = obj;
               }
               else if (key.equals("longplural"))
               {
                  longPluralVal = obj;
               }

               datatoolSty.addNewEntry(database, additionalKeyMap.get(key), obj);
            }
         }

         if (shortVal != null && shortPluralVal == null)
         {
            shortPluralVal = (TeXObject)shortVal.clone();

            TeXObjectList list = TeXParserUtils.toList(shortPluralVal, parser);

            list.add(getListener().getLetter('s'));

            datatoolSty.addNewEntry(database, "ShortPlural", list);
         }

         if (longVal != null && longPluralVal == null)
         {
            longPluralVal = (TeXObject)longVal.clone();

            TeXObjectList list = TeXParserUtils.toList(longPluralVal, parser);

            list.add(getListener().getLetter('s'));

            datatoolSty.addNewEntry(database, "LongPlural", list);
         }
      }

      if (text == null)
      {
         text = (TeXObject)name.clone();
      }

      if (plural == null)
      {
         plural = (TeXObject)name.clone();
         TeXObjectList list = TeXParserUtils.toList(plural, parser);

         list.add(getListener().getOther('s'));

         plural = list;
      }

      datatoolSty.addNewEntry(database, "Text", text);
      datatoolSty.addNewEntry(database, "Plural", plural);

      datatoolSty.addNewEntry(database, "Used", UserNumber.ZERO);
   }

   public DataToolEntryRow getTerm(String database, String label, TeXObjectList stack)
   throws IOException
   {
      DataBase db = getIndexDataBase(database);

      if (db == null)
      {
         throw new LaTeXSyntaxException(getParser(),
           ERROR_DATABASE_NOT_DEFINED, database);
      }

      return getTerm(db, label, stack);
   }

   public DataToolEntryRow getTerm(DataBase db, String label, TeXObjectList stack)
   throws IOException
   {
      TeXParser parser = getParser();

      DataToolHeader header = db.getHeader("Label");
      int columnIndex = header.getColumnIndex();

      DataToolRows rows = db.getData();
      DataToolEntryRow row = null;

      for (DataToolEntryRow r : rows)
      {
         DataToolEntry entry = r.getEntry(columnIndex);

         if (entry != null)
         {
            TeXObject contents = entry.getContents();

            String l = parser.expandToString((TeXObject)contents.clone(),
               stack);

            if (l.equals(label))
            {
               row = r;
               break;
            }
         }
      }

      if (row == null)
      {
         throw new LaTeXSyntaxException(parser, ERROR_TERM_NOT_DEFINED, label);
      }

      return row;
   }

   public void indexDataBaseLoaded(String name,
       KeyValList options, TeXObject title, TeXObjectList stack)
     throws IOException
   {
      TeXParser parser = getParser();
      TeXApp texApp = getListener().getTeXApp();
      DataBase db = datatoolSty.getDataBase(name);

      if (db == null)
      {
         throw new LaTeXSyntaxException(parser, ERROR_DATABASE_NOT_DEFINED, name);
      }

      DataToolHeaderRow headers = db.getHeaders();
      DataToolRows data = db.getData();

      DataToolHeader header = headers.getHeader("Label");

      if (header == null)
      {
         texApp.warning(parser, texApp.getMessage(ERROR_NOT_INDEX_DATABASE, name));

         return;
      }

      int labelIdx = header.getColumnIndex();

      if (gidxDatabases == null)
      {
         gidxDatabases = new Vector<String>();
      }

      gidxDatabases.add(name);

      if (databaseMap == null)
      {
         databaseMap = new HashMap<String,DataBase>();
      }

      databaseMap.put(name, db);

      if (labelToDatabase == null)
      {
         labelToDatabase = new HashMap<String,String>();
      }

      for (DataToolEntryRow row : data)
      {
         DataToolEntry entry = row.getEntry(labelIdx);

         if (entry != null)
         {
            String label = parser.expandToString(
               (TeXObject)entry.getContents().clone(), stack);

            labelToDatabase.put(label, name);
         }
      }

      if (headers.getHeader("HierSort") == null)
      {
         header = datatoolSty.addNewColumn(name, "HierSort");
         header.setType(DatumType.STRING);
      }

      if (headers.getHeader("LetterGroup") == null)
      {
         header = datatoolSty.addNewColumn(name, "LetterGroup");
         header.setType(DatumType.STRING);
      }

      if (headers.getHeader("UnsafeLocation") == null)
      {
         header = datatoolSty.addNewColumn(name, "UnsafeLocation");
         header.setType(DatumType.STRING);
      }

      addToCatalogue(stack, options, name, title);
   }

   public DataToolSty getDataToolSty()
   {
      return datatoolSty;
   }

   public int getIndexDataBaseCount()
   {
      return gidxDatabases == null ? 0 : gidxDatabases.size();
   }

   public String getLatestIndexDataBaseName()
   {
      return getIndexDataBaseCount() == 0 ? null : gidxDatabases.lastElement();
   }

   public DataBase getIndexDataBase(String dbname)
   {
      return databaseMap == null ? null : databaseMap.get(dbname);
   }

   protected DataToolSty datatoolSty;

   protected Vector<String> gidxDatabases;
   protected HashMap<String,DataBase> databaseMap;
   protected HashMap<String,String> labelToDatabase;
   protected HashMap<String,String> additionalKeyMap;

   public static final String ERROR_TERM_NOT_DEFINED
     = "datagidx.term_not_defined";

   public static final String ERROR_DATABASE_NOT_DEFINED
     = "datagidx.database_not_defined";

   public static final String ERROR_TERM_NO_DATABASES
     = "datagidx.no_databases";

   public static final String ERROR_NOT_INDEX_DATABASE
     = "datagidx.not_index_database";

   public static final String DEFAULT_DATABASE_NAME_TL
    = "l__datagidx_default_database_tl";

   public static final String HEADING_TL
    = "l__datagidx_heading_tl";

   public static final String POST_HEADING_TL
    = "l__datagidx_post_heading_tl";

   public static final String MULTICOLS_TL
    = "l__datagidx_multicols_tl";

   public static final String SORT_TL = "l__datagidx_sort_tl";
   public static final String STYLE_TL = "datagidx@style";
   public static final String SHOWGROUPS_TL = "datagidx@showgroups";
}
