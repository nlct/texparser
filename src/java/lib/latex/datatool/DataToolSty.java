/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

import java.util.Vector;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.latex3.*;
import com.dickimawbooks.texparserlib.latex.ifthen.IfThenSty;
import com.dickimawbooks.texparserlib.primitives.*;

/**
 * Limited support for datatool.sty. This is mostly intended for use
 * by datatooltk for reading and writing database files. Additional
 * commands are supplied to make it easier to test. (It may
 * eventually be possible to add support to LaTeX2LaTeX to allow
 * expansion of the datatool commands, but support for datatooltk is
 * the primary focus at the moment.)
 */
public class DataToolSty extends LaTeXSty
{
   public DataToolSty(KeyValList options, LaTeXParserListener listener, 
      boolean loadParentOptions)
   throws IOException
   {
      super(options, "datatool", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      registerControlSequence(new DTLifDbExists(this));
      registerControlSequence(new DTLifDbEmpty(this));
      registerControlSequence(new DTLnewdb(this));
      registerControlSequence(new DTLnewdb("DTLgnewdb", true, this));
      registerControlSequence(new DTLnewrow(this));
      registerControlSequence(new DTLcleardb(this));
      registerControlSequence(new DTLcleardb("DTLgcleardb", true, this));
      registerControlSequence(new DTLdeletedb(this));
      registerControlSequence(new DTLdeletedb("DTLgdeletedb", true, this));
      registerControlSequence(new DTLrowcount(this));
      registerControlSequence(new DTLcolumncount(this));
      registerControlSequence(new DTLnewdbentry(this));
      registerControlSequence(new DTLmessage());
      registerControlSequence(new DTLforeach(this));
      registerControlSequence(new DTLdisplaydb(this));
      registerControlSequence(new DTLdisplaydb("DTLdisplaylongdb", true, this));
      registerControlSequence(new DTLsetdelimiter(this));
      registerControlSequence(new DTLsetseparator(this));
      registerControlSequence(new DTLsettabseparator(this));

      registerControlSequence(
         new DTLsetExpansion("dtlexpandnewvalue", true, this));
      registerControlSequence(
         new DTLsetExpansion("dtlnoexpandnewvalue", false, this));

      registerControlSequence(new EndGraf("DTLpar"));
      registerControlSequence(new DTLloaddbtex());
      registerControlSequence(new DTLloaddb(this));

      registerControlSequence(new TextualContentCommand("dtldefaultkey",
       "Column"));

      registerControlSequence(new GenericCommand("dtldisplaycr", null,
       TeXParserUtils.createStack(getParser(), new TeXCsRef("tabularnewline"))));

      registerControlSequence(new GenericCommand("DTLunsettype"));
      registerControlSequence(new GenericCommand("DTLstringtype", null,
        new UserNumber(DataToolHeader.TYPE_STRING)));
      registerControlSequence(new GenericCommand("DTLinttype", null,
        new UserNumber(DataToolHeader.TYPE_INT)));
      registerControlSequence(new GenericCommand("DTLrealtype", null,
        new UserNumber(DataToolHeader.TYPE_REAL)));
      registerControlSequence(new GenericCommand("DTLcurrencytype", null,
        new UserNumber(DataToolHeader.TYPE_CURRENCY)));

      NewIf.createConditional(true, getParser(), "ifdtlnoheader", false);
      NewIf.createConditional(true, getParser(), "ifdtlautokeys", false);

      registerControlSequence(
        new TextualContentCommand("@dtl@delimiter", "\""));

      registerControlSequence(
        new TextualContentCommand("@dtl@separator", ","));

      registerControlSequence(
        new TextualContentCommand("dtldisplayvalign", "c"));

      registerControlSequence(
         new TokenListCommand("dtldisplaystarttab"));

      registerControlSequence(
         new TokenListCommand("dtldisplayafterhead"));

      registerControlSequence(
         new TokenListCommand("dtldisplayendtab"));

      registerControlSequence(
         new TokenListCommand("dtldisplaystartrow"));

      registerControlSequence(
         new TextualContentCommand("dtlstringalign", "l"));

      registerControlSequence(
         new TextualContentCommand("dtlintalign", "r"));

      registerControlSequence(
         new TextualContentCommand("dtlrealalign", "r"));

      registerControlSequence(
         new TextualContentCommand("dtlcurrencyalign", "r"));

      registerControlSequence(
         new TokenListCommand("dtlbetweencols"));

      registerControlSequence(
         new TokenListCommand("dtlbeforecols"));

      registerControlSequence(
         new TokenListCommand("dtlaftercols"));

      getParser().getSettings().newcount(true, "dtlcolumnnum");
      getParser().getSettings().newcount(true, "dtlrownum");
      getParser().getSettings().newcount(true, "dtl@omitlines");

      // datatool v3.0:

      registerControlSequence(new DTLread(this));
      registerControlSequence(new DTLwrite(this));

      registerControlSequence(
        new TextualContentCommand("dtldisplaydbenv", "tabular"));

      registerControlSequence(
        new TextualContentCommand("dtldisplaylongdbenv", "longtable"));

      registerControlSequence(new DTLdisplaydbAddBegin());
      registerControlSequence(new DTLdisplaydbAddEnd());

      registerControlSequence(new GenericCommand(listener, true, "dtlcolumnheader",
        2, TeXParserUtils.createStack(getParser(),
            new TeXCsRef("multicolumn"),
            UserNumber.ONE,
            TeXParserUtils.createGroup(getParser(), listener.getParam(1)),
            TeXParserUtils.createGroup(getParser(), 
              new TeXCsRef("dtlheaderformat"),
               TeXParserUtils.createGroup(getParser(), listener.getParam(2))
            )
        )));

      registerControlSequence(new GenericCommand(listener, true, "dtlheaderformat",
       1, TeXParserUtils.createStack(getParser(), 
            new TeXCsRef("textbf"), 
              TeXParserUtils.createGroup(getParser(), listener.getParam(1)))));

      registerControlSequence(new AtNumberOfNumber(
       "__datatool_if_display_row:nNT", 3, 3));

      registerControlSequence(new DTLdbProvideData(this));
      registerControlSequence(new DTLdbNewRow(this));
      registerControlSequence(new DTLdbNewEntry(this));
      registerControlSequence(new DTLdbSetHeader(this));
      registerControlSequence(new DTLreconstructdata(this));

      registerControlSequence(new DTLaddalign());
      registerControlSequence(new DTLaddheaderalign());

      registerControlSequence(new DTLdisplayDbRow(this));

      getParser().getSettings().newcount(true, "l__datatool_max_cols_int");
      getParser().getSettings().newcount(true, "l__datatool_row_idx_int");
      getParser().getSettings().newcount(true, "l__datatool_col_idx_int");

      getParser().getSettings().newcount(true, "l__datatool_item_type_int");

      registerControlSequence(
        new LaTeX3Boolean("l__datatool_db_global_bool", true));
      registerControlSequence(
        new LaTeX3Boolean("l__datatool_new_element_trim_bool", true));
      registerControlSequence(
        new LaTeX3Boolean("l__datatool_db_store_datum_bool", false));
      registerControlSequence(
        new LaTeX3Boolean("l_datatool_include_header_bool", true));
      registerControlSequence(
        new LaTeX3Boolean("l__datatool_append_allowed_bool", true));
      registerControlSequence(
        new LaTeX3Boolean("l__datatool_csv_literal_content_bool", true));
      registerControlSequence(
        new TextualContentCommand("l__datatool_default_dbname_str", "untitled"));

      registerControlSequence(
        new PropertyCommand<Integer>("l__datatool_csv_headers_prop"));
      registerControlSequence(
        new PropertyCommand<Integer>("l__datatool_csv_keys_prop"));

      // display options

      registerControlSequence(
         new SequenceCommand("l__datatool_omit_columns_seq"));

      registerControlSequence(
         new SequenceCommand("l__datatool_omit_keys_seq"));

      registerControlSequence(
         new SequenceCommand("l__datatool_only_columns_seq"));

      registerControlSequence(
         new SequenceCommand("l__datatool_only_keys_seq"));

      registerControlSequence(
         new TokenListCommand("l__datatool_pre_display_tl"));

      registerControlSequence(
         new TokenListCommand("l_datatool_post_head_tl"));

      registerControlSequence(
         new TokenListCommand("l__datatool_user_align_tl"));

      registerControlSequence(
         new TokenListCommand("l__datatool_user_header_tl"));
   }

   @Override
   protected void preOptions(TeXObjectList stack) throws IOException
   {
      getListener().requirepackage(null, "etoolbox", false, stack);

      dataToolBaseSty = (DataToolBaseSty)getListener().requirepackage(
         null, "datatool-base", true, stack);
      dataToolBaseSty.setDataBaseSty(this);
   }

   public static String getContentsRegisterName(String dbName)
   {
      return String.format("dtldb@%s", dbName);
   }

   public static String getHeaderRegisterName(String dbName)
   {
      return String.format("dtlkeys@%s", dbName);
   }

   public static String getRowCountRegisterName(String dbName)
   {
      return String.format("dtlrows@%s", dbName);
   }

   public static String getColumnCountRegisterName(String dbName)
   {
      return String.format("dtlcols@%s", dbName);
   }

   public static String getColumnHeaderName(String dbName, String key)
   {
      return String.format("dtl@ci@%s@%s", dbName, key);
   }

   public static String getColumnHeaderName(String dbName,
       DataToolHeader header)
   {
      return getColumnHeaderName(dbName, header.getColumnLabel());
   }

   public int getRowCount(String name) throws IOException
   {
      DataBase db = update(name);

      return db.getRowCount();
   }

   public int getColumnCount(String name) throws IOException
   {
      DataBase db = update(name);

      return db.getColumnCount();
   }

   public void setExpansion(boolean on)
   {
      if (on)
      {
         getListener().getParser().putControlSequence(true,
           new IfTrue("if@dtl@expansion@on"));
      }
      else
      {
         getListener().getParser().putControlSequence(true,
           new IfFalse("if@dtl@expansion@on"));
      }
   }

   public boolean isExpansionOn()
   {
      ControlSequence cs = getListener().getParser().getControlSequence(
        "if@dtl@expansion@on");

      return (cs instanceof IfTrue);
   }

   public boolean dbExists(String name)
   {
      ControlSequence cs = getParser().getControlSequence(
         getContentsRegisterName(name));

      return cs != null;
   }

   public boolean dbEmpty(String name) throws IOException
   {
      return getRowCount(name) == 0;
   }

   public DataBase createDataBase(String name, boolean global)
      throws TeXSyntaxException
   {
      TeXParser parser = getListener().getParser();

      if (dbExists(name))
      {
         throw new LaTeXSyntaxException(parser, ERROR_DB_EXISTS, name);
      }

      TeXSettings settings = parser.getSettings();

      settings.newtoks(!global, getContentsRegisterName(name));
      settings.newtoks(!global, getHeaderRegisterName(name));
      settings.newcount(!global, getRowCountRegisterName(name));
      settings.newcount(!global, getColumnCountRegisterName(name));

      DataBase db = new DataBase(name);

      if (databases == null)
      {
         databases = new ConcurrentHashMap<String,DataBase>();
      }

      databases.put(name, db);
      return db;
   }

   public DataBase getDataBase(String name)
      throws IOException
   {
      DataBase db = null;

      if (databases != null)
      {
         db = databases.get(name);
      }

      if (db == null)
      {
         db = update(name);
      }

      return db;
   }

   public DataBase clearDataBase(String name, boolean global)
      throws TeXSyntaxException
   {
      TeXParser parser = getListener().getParser();

      DataBase db = null;

      if (databases == null)
      {
         databases = new ConcurrentHashMap<String,DataBase>();
      }
      else
      {
         db = databases.get(name);
      }

      TeXSettings settings = parser.getSettings();

      if (db != null)
      {
         DataToolHeaderRow headers = db.getHeaders();

         for (DataToolHeader header : headers)
         {
            if (global)
            {
               settings.removeGlobalControlSequence(
                 getColumnHeaderName(name, header));
            }
            else
            {
               settings.removeLocalControlSequence(
                 getColumnHeaderName(name, header));
            }
         }
      }

      if (global)
      {
         settings.globalSetRegister(getContentsRegisterName(name),
           new TeXObjectList());
         settings.globalSetRegister(getHeaderRegisterName(name),
           new TeXObjectList());
         settings.globalSetRegister(getRowCountRegisterName(name),
            (TeXObject)new UserNumber(0));
         settings.globalSetRegister(getColumnCountRegisterName(name),
            (TeXObject)new UserNumber(0));
      }
      else
      {
         settings.localSetRegister(getContentsRegisterName(name),
           new TeXObjectList());
         settings.localSetRegister(getHeaderRegisterName(name),
           new TeXObjectList());
         settings.localSetRegister(getRowCountRegisterName(name),
            (TeXObject)new UserNumber(0));
         settings.localSetRegister(getColumnCountRegisterName(name),
            (TeXObject)new UserNumber(0));
      }

      if (db == null)
      {
         db = new DataBase(name);
         databases.put(name, db);
      }
      else
      {
         db.update(null, null);
      }

      return db;
   }

   public DataToolEntryRow addNewRow(String name) throws IOException
   {
      DataToolRows rows = getContents(name);

      DataToolEntryRow row = new DataToolEntryRow(this);

      rows.add(row);
      update(name, rows);

      return row;
   }

   public DataToolHeader addNewColumn(String dbName, String key)
   throws IOException
   {
      DataToolHeaderRow headers = getHeaderContents(dbName);

      DataToolHeader header = headers.getHeader(key);

      if (header != null)
      {
         throw new LaTeXSyntaxException(getListener().getParser(),
           ERROR_HEADER_EXISTS, key);
      }

      header = new DataToolHeader(this, headers.getMaxIndex()+1, key);

      headers.add(header);
      update(dbName, headers);

      return header;
   }

   public DataToolHeader setColumnHeader(String dbName, String key,
     TeXObject headerValue)
   throws IOException
   {
      DataToolHeaderRow headers = getHeaderContents(dbName);

      DataToolHeader header = headers.getHeader(key);

      if (header == null)
      {
         throw new LaTeXSyntaxException(getListener().getParser(),
           ERROR_HEADER_DOESNT_EXIST, key);
      }

      header.setTitle(headerValue);

      return header;
   }

   public DataElement getElement(TeXObject entry)
     throws IOException
   {
      return dataToolBaseSty.getElement(entry);
   }

   public DataToolEntry addNewEntry(String dbName, String colLabel, 
     TeXObject element)
   throws IOException
   {
      DataToolRows rows = getContents(dbName);
      DataToolHeaderRow headers = getHeaderContents(dbName);

      DataToolEntryRow row;

      if (rows.size() == 0)
      {
         row = new DataToolEntryRow(this);
         rows.add(row);
      }
      else
      {
         row = rows.lastElement();
      }

      DataToolHeader header = headers.getHeader(colLabel);

      if (header == null)
      {
         header = new DataToolHeader(this, headers.getMaxIndex()+1, colLabel);
         headers.add(header);
      }

      DataToolEntry entry = new DataToolEntry(this, header.getColumnIndex(),
         element);
      row.add(entry);

      TeXObject contents = entry.getContents();

      if (contents instanceof DataElement)
      {
         header.updateType((DataElement)contents);
      }

      update(dbName, rows);

      return entry;
   }

   public void removeDataBase(String name, boolean global)
   {
      DataBase db = null;

      if (databases != null)
      {
         db = databases.remove(name);
      }

      TeXSettings settings = getListener().getParser().getSettings();

      if (db != null)
      {
         DataToolHeaderRow headers = db.getHeaders();

         for (DataToolHeader header : headers)
         {
            if (global)
            {
               settings.removeGlobalControlSequence(
                 getColumnHeaderName(name, header));
            }
            else
            {
               settings.removeLocalControlSequence(
                 getColumnHeaderName(name, header));
            }
         }
      }

      if (global)
      {
         settings.removeGlobalControlSequence(
            getContentsRegisterName(name));
         settings.removeGlobalControlSequence(
            getHeaderRegisterName(name));
         settings.removeGlobalControlSequence(
            getRowCountRegisterName(name));
         settings.removeGlobalControlSequence(
            getColumnCountRegisterName(name));
      }
      else
      {
         settings.removeLocalControlSequence(
            getContentsRegisterName(name));
         settings.removeLocalControlSequence(
            getHeaderRegisterName(name));
         settings.removeLocalControlSequence(
            getRowCountRegisterName(name));
         settings.removeLocalControlSequence(
            getColumnCountRegisterName(name));
      }
   }

   public void updateInternals(boolean global, String name)
   throws TeXSyntaxException
   {
      DataBase db = null;

      if (databases != null)
      {
         db = databases.get(name);
      }

      if (db == null)
      {
         throw new LaTeXSyntaxException(getParser(),
           ERROR_DB_DOESNT_EXIST, name);
      }

      String headerRegName = getHeaderRegisterName(name);
      String contentRegName = getContentsRegisterName(name);
      String rowCountRegName = getRowCountRegisterName(name);
      String colCountRegName = getColumnCountRegisterName(name);

      TeXSettings settings = getParser().getSettings();

      DataToolHeaderRow headers = db.getHeaders();

      if (global)
      {
         settings.globalSetRegister(headerRegName, headers);
         settings.globalSetRegister(contentRegName, db.getData());
         settings.globalSetRegister(rowCountRegName, db.getRowCount());
         settings.globalSetRegister(colCountRegName, db.getColumnCount());
      }
      else
      {
         settings.localSetRegister(headerRegName, headers);
         settings.localSetRegister(contentRegName, db.getData());
         settings.localSetRegister(rowCountRegName, db.getRowCount());
         settings.localSetRegister(colCountRegName, db.getColumnCount());
      }

      for (DataToolHeader header : headers)
      {
         String key = header.getColumnLabel();
         int idx = header.getColumnIndex();

         getParser().putControlSequence(!global, 
           new IntegerContentCommand(getColumnHeaderName(name, key), idx));
      }

   }

   public DataToolHeaderRow getHeaderContents(String name)
    throws IOException
   {
      TeXParser parser = getListener().getParser();

      ControlSequence cs = parser.getControlSequence(
         getHeaderRegisterName(name));

      if (cs == null || !(cs instanceof TokenRegister))
      {
         throw new LaTeXSyntaxException(parser, 
           ERROR_DB_DOESNT_EXIST, name);
      }

      TokenRegister reg = (TokenRegister)cs;

      TeXObject contents = reg.getContents(parser);

      if (contents instanceof DataToolHeaderRow)
      {
         return (DataToolHeaderRow)contents;
      }

      if (!(contents instanceof TeXObjectList))
      {
         throw new LaTeXSyntaxException(parser, 
           ERROR_INVALID_HEADER, contents.toString(parser));
      }

      DataToolHeaderRow row = DataToolHeaderRow.toHeaderRow(parser, 
        (TeXObjectList)contents, this);

      reg.setContents(parser, row);

      return row;
   }

   public DataToolRows getContents(String name) throws IOException
   {
      TeXParser parser = getListener().getParser();

      ControlSequence cs = parser.getControlSequence(
         getContentsRegisterName(name));

      if (cs == null || !(cs instanceof TokenRegister))
      {
         throw new LaTeXSyntaxException(parser, 
           ERROR_DB_DOESNT_EXIST, name);
      }

      TokenRegister reg = (TokenRegister)cs;

      TeXObject contents = reg.getContents(parser);

      if (contents instanceof DataToolRows)
      {
         return (DataToolRows)contents;
      }

      if (!(contents instanceof TeXObjectList))
      {
         throw new LaTeXSyntaxException(parser, 
           ERROR_INVALID_CONTENTS, contents);
      }

      DataToolRows rows;

      int n = ((TeXObjectList)contents).size();

      if (n == 0)
      {
         rows = new DataToolRows(this);
      }
      else
      {
         cs = parser.getControlSequence(getRowCountRegisterName(name));
         int rowCount = 0;

         if (cs instanceof CountRegister)
         {
            rowCount = ((CountRegister)cs).getValue();
         }

         rows = DataToolRows.toRows(parser, (TeXObjectList)contents, this,
           rowCount);
      }

      reg.setContents(parser, rows);

      return rows;
   }

   protected synchronized DataBase update(String name)
     throws IOException
   {
      return update(name, getHeaderContents(name), getContents(name));
   }

   protected synchronized DataBase update(String name, DataToolHeaderRow header)
     throws IOException
   {
      return update(name, header, getContents(name));
   }

   protected synchronized DataBase update(String name, DataToolRows rows)
     throws IOException
   {
      return update(name, getHeaderContents(name), rows);
   }

   protected synchronized DataBase update(String name, DataToolHeaderRow header,
      DataToolRows rows)
     throws TeXSyntaxException
   {
      DataBase db = null;

      if (databases == null)
      {
         databases = new ConcurrentHashMap<String,DataBase>();
      }
      else
      {
         db = databases.get(name);
      }

      boolean global = isDbGlobalOn();

      if (db == null)
      {
         db = new DataBase(name, header, rows);
         databases.put(name, db);
      }
      else
      {
         db.update(header, rows);
      }

      TeXParser parser = getListener().getParser();
      String colRegName = getColumnCountRegisterName(name);
      String rowRegName = getRowCountRegisterName(name);

      if (global)
      {
         parser.getSettings().globalSetRegister(colRegName, db.getColumnCount());
         parser.getSettings().globalSetRegister(rowRegName, db.getRowCount());
      }
      else
      {
         parser.getSettings().localSetRegister(colRegName, db.getColumnCount());
         parser.getSettings().localSetRegister(rowRegName, db.getRowCount());
      }

      return db;
   }

   public boolean isDbGlobalOn()
   {
      return TeXParserUtils.isTrue("l__datatool_db_global_bool", getParser());
   }

   public DataToolBaseSty getDataToolBaseSty()
   {
      return dataToolBaseSty;
   }

   public IfThenSty getIfThenSty()
   {
      return dataToolBaseSty.getIfThenSty();
   }

   public Iterator<String> getDataBaseKeySetIterator()
   {
      return databases == null ? null : databases.keySet().iterator();
   }

   public int getSeparator()
   throws IOException
   {
      ControlSequence cs = getParser().getControlSequence("@dtl@separator");

      int separator = -1;

      if (cs instanceof TextualContentCommand)
      {
         separator = ((TextualContentCommand)cs).getText().codePointAt(0);
      }
      else
      {
         String str = getParser().expandToString(cs, getParser());
         separator = str.codePointAt(0);
      }

      return separator;
   }

   public int getDelimiter()
   throws IOException
   {
      ControlSequence cs = getParser().getControlSequence("@dtl@delimiter");

      int delimiter = -1;

      if (cs instanceof TextualContentCommand)
      {
         delimiter = ((TextualContentCommand)cs).getText().codePointAt(0);
      }
      else
      {
         String str = getParser().expandToString(cs, getParser());
         delimiter = str.codePointAt(0);
      }

      return delimiter;
   }

   public void setSeparator(int charCode)
   {
      getParser().putControlSequence(true, 
       new TextualContentCommand("@dtl@separator", 
        new String(Character.toChars(charCode))));
   }

   public void setDelimiter(int charCode)
   {
      getParser().putControlSequence(true, 
       new TextualContentCommand("@dtl@delimiter", 
        new String(Character.toChars(charCode))));
   }

   public void processSetupOption(String key, TeXObject value, TeXObjectList stack)
   throws IOException
   {
      if (key.equals("default-name"))
      {
         getParser().putControlSequence(true,
           new TextualContentCommand("l__datatool_default_dbname_str",
              getParser().expandToString(value, stack)));
      }
      else if (key.equals("global"))
      {
         boolean boolVal = true;

         if (value != null)
         {
            boolVal = Boolean.parseBoolean(getParser().expandToString(value, stack));
         }

         getParser().putControlSequence(true,
           new LaTeX3Boolean("l__datatool_db_global_bool", boolVal));
      }
      else if (key.equals("store-datum"))
      {
         boolean boolVal = true;

         if (value != null)
         {
            boolVal = Boolean.parseBoolean(getParser().expandToString(value, stack));
         }

         getParser().putControlSequence(true,
           new LaTeX3Boolean("l__datatool_db_store_datum_bool", boolVal));
      }
      else if (key.equals("new-value-trim"))
      {
         boolean boolVal = true;

         if (value != null)
         {
            boolVal = Boolean.parseBoolean(getParser().expandToString(value, stack));
         }

         getParser().putControlSequence(true,
           new LaTeX3Boolean("l__datatool_new_element_trim_bool", boolVal));
      }
      else if (key.equals("new-value-expand"))
      {
         boolean boolVal = true;

         if (value != null)
         {
            boolVal = Boolean.parseBoolean(getParser().expandToString(value, stack));
         }

         ControlSequence cs;

         if (boolVal)
         {
            cs = getParser().getListener().getControlSequence("dtlexpandnewvalue");
         }
         else
         {
            cs = getParser().getListener().getControlSequence("dtlnoexpandnewvalue");
         }

         TeXParserUtils.process(cs, getParser(), stack);
      }
      else if (key.equals("delimiter"))
      {
         String str = getParser().expandToString(value, stack);
         setDelimiter(str.codePointAt(0));
      }
      else if (key.equals("separator"))
      {
         String str = getParser().expandToString(value, stack);
         setSeparator(str.codePointAt(0));
      }
      else if (key.equals("io"))
      {
         processIOKeys(value, stack);
      }
      else if (key.equals("action"))
      {
         processActionKeys(value, stack);
      }
      else if (key.equals("display"))
      {
         processDisplayKeys(value, stack);
      }
      else
      {
         throw new LaTeXSyntaxException(getParser(),
          LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
          key, "datatool");
      }
   }

   public void processIOKeys(TeXObject arg, TeXObjectList stack)
   throws IOException
   {
      TeXParser parser = getParser();

      KeyValList options = TeXParserUtils.toKeyValList(arg, parser);

      for (Iterator<String> it = options.keySet().iterator();
           it.hasNext(); )
      {  
         String key = it.next();
         
         TeXObject val = options.get(key);

         if (key.equals("name"))
         {
            parser.putControlSequence(true, 
              new TextualContentCommand("l__datatool_io_name_str",
                 parser.expandToString(val, stack)));
         }
         else if (key.equals("keys"))
         {
            PropertyCommand<Integer> prop 
              = new PropertyCommand<Integer>("l__datatool_csv_keys_prop");

            if (val != null)
            {
               CsvList csvList = TeXParserUtils.toCsvList(val, parser);

               for (int i = 0; i < csvList.size(); i++)
               {
                  TeXObject obj = csvList.getValue(i);

                  prop.put(Integer.valueOf(i+1), obj);
               }
            }

            parser.putControlSequence(true, prop);
         }
         else if (key.equals("headers"))
         {
            PropertyCommand<Integer> prop 
              = new PropertyCommand<Integer>("l__datatool_csv_headers_prop");

            if (val != null)
            {
               CsvList csvList = TeXParserUtils.toCsvList(val, parser);

               for (int i = 0; i < csvList.size(); i++)
               {
                  TeXObject obj = csvList.getValue(i);

                  prop.put(Integer.valueOf(i+1), obj);
               }
            }

            parser.putControlSequence(true, prop);
         }
         else if (key.equals("expand"))
         {
            String str = (val == null ? "protected" : parser.expandToString(val, stack));

            ControlSequence cs;

            if (str.equals("none"))
            {
               parser.putControlSequence(true, 
                 new TextualContentCommand("__texparser_io_expand_tl", str));

               cs = getParser().getListener().getControlSequence("dtlnoexpandnewvalue");
            }
            else if (str.equals("protected") || str.equals("full"))
            {
               parser.putControlSequence(true, 
                 new TextualContentCommand("__texparser_io_expand_tl", str));

               cs = getParser().getListener().getControlSequence("dtlexpandnewvalue");
            }
            else
            {
               throw new LaTeXSyntaxException(parser, 
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                key+"="+str, "datatool/io");
            }

            TeXParserUtils.process(cs, getParser(), stack);
         }
         else if (key.equals("format"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String format = parser.expandToString(val, stack);

            parser.putControlSequence(true, 
              new TextualContentCommand("l__datatool_format_str", format));

            if (format.startsWith("dbtex"))
            {
               parser.putControlSequence(true, 
                 new TextualContentCommand("l__datatool_default_ext_str",
                   "dbtex"));
            }
            else if (format.startsWith("dtltex"))
            {
               parser.putControlSequence(true, 
                 new TextualContentCommand("l__datatool_default_ext_str",
                   "dtltex"));
            }
            else if (format.equals("csv"))
            {
               parser.putControlSequence(true, 
                 new TextualContentCommand("l__datatool_default_ext_str",
                   "csv"));
            }
            else if (format.equals("tsv"))
            {
               parser.putControlSequence(true, 
                 new TextualContentCommand("l__datatool_default_ext_str",
                   "tsv"));
               setSeparator('\t');
            }
            else
            {
               throw new LaTeXSyntaxException(parser, 
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                key+"="+format, "datatool/io");
            }
         }
         else if (key.equals("add-delimiter"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String str = parser.expandToString(val, stack);

            if (str.equals("always") || str.equals("detect") || str.equals("never"))
            {
               parser.putControlSequence(true,
                 new TextualContentCommand("__texparser_io_add_delimiter_tl", str));
            }
            else
            {
               throw new LaTeXSyntaxException(parser,
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                  key+"="+str, "datatool/io");
            }
         }
         else if (key.equals("csv-escape-chars"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String str = parser.expandToString(val, stack);

            if (str.equals("none") || str.equals("delim") || str.equals("delim+bksl"))
            {
               parser.putControlSequence(true,
                 new TextualContentCommand("__texparser_io_csv_escape_chars_tl", str));
            }
            else
            {
               throw new LaTeXSyntaxException(parser,
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                  key+"="+str, "datatool/io");
            }
         }
         else if (key.equals("csv-content"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            boolean boolVal = true;

            String str = parser.expandToString(val, stack).trim();

            if (str.equals("tex"))
            {
               boolVal = false;
            }
            else if (str.equals("literal"))
            {
               boolVal = true;
            }
            else
            {
               throw new LaTeXSyntaxException(parser,
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                  key+"="+str, "datatool/io");
            }

            getParser().putControlSequence(true,
              new LaTeX3Boolean("l__datatool_csv_literal_content_bool", boolVal));
         }
         else if (key.equals("csv-blank"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String str = parser.expandToString(val, stack);

            if (str.equals("ignore") || str.equals("empty-row") || str.equals("end"))
            {
               parser.putControlSequence(true,
                 new TextualContentCommand("__texparser_io_csv_blank_tl", str));
            }
            else
            {
               throw new LaTeXSyntaxException(parser,
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                  key+"="+str, "datatool/io");
            }
         }
         else if (key.equals("csv-skip-lines") || key.equals("omitlines"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String str = val.toString(parser).trim();
            int num;

            if (str.equals("false"))
            {
               num = 0;
            }
            else
            {
               try
               {
                  num = Integer.parseInt(str);
               }
               catch (NumberFormatException e)
               {
                  num = TeXParserUtils.toInt(val, parser, stack);
               }
            }

            parser.getSettings().localSetRegister("dtl@omitlines", num);
         }
         else if (key.equals("no-header") || key.equals("noheader"))
         {
            String strVal = (val == null ? "" : val.toString(parser).trim());

            if (val.equals("") || val.equals("true"))
            {
               parser.putControlSequence(true, new IfTrue("ifdtlnoheader"));
            }
            else
            {
               parser.putControlSequence(true, new IfFalse("ifdtlnoheader"));
            }
         }
         else if (key.equals("auto-keys") || key.equals("autokeys"))
         {
            String strVal = (val == null ? "" : val.toString(parser).trim());

            if (val.equals("") || val.equals("true"))
            {
               parser.putControlSequence(true, new IfTrue("ifdtlautokeys"));
            }
            else
            {
               parser.putControlSequence(true, new IfFalse("ifdtlautokeys"));
            }
         }
         else if (key.equals("overwrite"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String str = parser.expandToString(val, stack);

            if (str.equals("error") || str.equals("warn") || str.equals("allow"))
            {
               parser.putControlSequence(true,
                 new TextualContentCommand("__texparser_io_overwrite_tl", str));
            }
            else
            {
               throw new LaTeXSyntaxException(parser,
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                  key+"="+str, "datatool/io");
            }
         }
         else if (key.equals("load-action"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            boolean boolVal = true;

            String str = parser.expandToString(val, stack).trim();

            // simplified to just a boolean allow/disallow appending
            if (str.equals("detect") || str.equals("append"))
            {
               boolVal = true;
            }
            else if (str.equals("create") || str.equals("old-style"))
            {
               boolVal = false;
            }
            else
            {
               throw new LaTeXSyntaxException(parser,
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                  key+"="+str, "datatool/io");
            }

            getParser().putControlSequence(true,
              new LaTeX3Boolean("l__datatool_append_allowed_bool", boolVal));
         }
         else if (key.equals("delimiter"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String str = parser.expandToString(val, stack);
            setDelimiter(str.codePointAt(0));
         }
         else if (key.equals("separator"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String str = parser.expandToString(val, stack);
            setSeparator(str.codePointAt(0));
         }
      }
   }

   public void processActionKeys(TeXObject arg, TeXObjectList stack)
   throws IOException
   {
      TeXParser parser = getParser();

      KeyValList options = TeXParserUtils.toKeyValList(arg, parser);

// TODO
   }

   public void processDisplayKeys(TeXObject arg, TeXObjectList stack)
   throws IOException
   {
      TeXParser parser = getParser();

      KeyValList options = TeXParserUtils.toKeyValList(arg, parser);

      for (Iterator<String> it = options.keySet().iterator();
           it.hasNext(); )
      {  
         String key = it.next();
         
         TeXObject val = options.get(key);

         if (key.equals("omit-columns") || key.equals("omit"))
         {
            if (val == null || val.isEmpty())
            {
               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_omit_columns_seq"));
            }
            else
            {
               CsvList csvList = TeXParserUtils.toCsvList(val, parser);

               parser.putControlSequence(true, 
                SequenceCommand.createFromClist(
                  parser, "l__datatool_omit_columns_seq", csvList));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_only_columns_seq"));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_only_keys_seq"));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_omit_keys_seq"));
            }
         }
         else if (key.equals("only-columns"))
         {
            if (val == null || val.isEmpty())
            {
               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_only_columns_seq"));
            }
            else
            {
               CsvList csvList = TeXParserUtils.toCsvList(val, parser);

               parser.putControlSequence(true, 
                SequenceCommand.createFromClist(
                  parser, "l__datatool_only_columns_seq", csvList));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_omit_columns_seq"));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_only_keys_seq"));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_omit_keys_seq"));
            }
         }
         else if (key.equals("omit-keys"))
         {
            if (val == null || val.isEmpty())
            {
               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_omit_keys_seq"));
            }
            else
            {
               CsvList csvList = TeXParserUtils.toCsvList(val, parser);

               parser.putControlSequence(true, 
                SequenceCommand.createFromClist(
                 parser, "l__datatool_omit_keys_seq", csvList));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_omit_columns_seq"));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_only_columns_seq"));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_only_keys_seq"));
            }
         }
         else if (key.equals("only-keys"))
         {
            if (val == null || val.isEmpty())
            {
               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_only_keys_seq"));
            }
            else
            {
               CsvList csvList = TeXParserUtils.toCsvList(val, parser);

               parser.putControlSequence(true, 
                SequenceCommand.createFromClist(
                  parser, "l__datatool_only_keys_seq", csvList));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_omit_keys_seq"));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_only_columns_seq"));

               parser.putControlSequence(true, 
                new SequenceCommand("l__datatool_omit_columns_seq"));
            }
         }
         else if (key.equals("row-condition"))
         {
            parser.putControlSequence(true, 
             new GenericCommand(parser.getListener(), true,
               "__datatool_if_display_row:nNT", 3, 
               TeXParserUtils.toList(val, parser)));
         }
         else if (key.equals("pre-content"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("l__datatool_pre_display_tl", val));
         }
         else if (key.equals("pre-head"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("dtldisplaystarttab", val));
         }
         else if (key.equals("post-head"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("l_datatool_post_head_tl", val));
         }
         else if (key.equals("align-specs"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("l__datatool_user_align_tl", val));
         }
         else if (key.equals("header-row"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("l__datatool_user_header_tl", val));
         }
         else if (key.equals("no-header"))
         {
            parser.putControlSequence(true, 
             new LaTeX3Boolean("l_datatool_include_header_bool", 
              !(val == null || val.isEmpty() 
                || val.toString(parser).trim().equals("true"))));
         }
         else if (key.equals("string-align"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("dtlstringalign", val));
         }
         else if (key.equals("int-align") || key.equals("integer-align"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("dtlintalign", val));
         }
         else if (key.equals("real-align") || key.equals("decimal-align"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("dtlrealalign", val));
         }
         else if (key.equals("currency-align"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("dtlcurrencyalign", val));
         }
         else if (key.equals("inter-col"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("dtlbetweencols", val));
         }
         else if (key.equals("pre-col"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("dtlbeforecols", val));
         }
         else if (key.equals("post-col"))
         {
            parser.putControlSequence(true, 
             new TokenListCommand("dtlaftercols", val));
         }
         else if (key.equals("tabular-env"))
         {
            String valStr = (val == null || val.isEmpty() ? "tabular"
              : parser.expandToString(val, stack).trim());

            parser.putControlSequence(true,
              new TextualContentCommand("dtldisplaydbenv", valStr));

            if (valStr.equals("tabular") || valStr.equals("array"))
            {
               String currValign = parser.expandToString(
                 parser.getControlSequence("dtldisplayvalign"), stack);

               if (!(currValign.equals("t") || currValign.equals("b")
                      || currValign.equals("c")))
               {
                  parser.putControlSequence(true,
                     new TextualContentCommand("dtldisplayvalign", "c"));
               }
            }
         }
         else if (key.equals("longtable-env"))
         {
            String valStr = (val == null || val.isEmpty() ? "longtable"
              : parser.expandToString(val, stack).trim());

            parser.putControlSequence(true,
              new TextualContentCommand("dtldisplaylongdbenv", valStr));
         }
         else if (key.equals("caption"))
         {
            parser.putControlSequence(true,
              new TokenListCommand("l_datatool_caption_tl", val));
         }
         else if (key.equals("short-caption") || key.equals("shortcaption"))
         {
            parser.putControlSequence(true,
              new TokenListCommand("l_datatool_short_caption_tl", val));
         }
         else if (key.equals("cont-caption") || key.equals("contcaption"))
         {
            parser.putControlSequence(true,
              new TokenListCommand("l_datatool_cont_caption_tl", val));
         }
         else if (key.equals("label"))
         {
            parser.putControlSequence(true,
              new TokenListCommand("l_datatool_label_tl", val));
         }
         else if (key.equals("foot"))
         {
            parser.putControlSequence(true,
              new TokenListCommand("l_datatool_foot_tl", val));
         }
         else if (key.equals("last-foot") || key.equals("lastfoot"))
         {
            parser.putControlSequence(true,
              new TokenListCommand("l_datatool_last_foot_tl", val));
         }
         else
         {
            throw new LaTeXSyntaxException(parser, 
             LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
             key, "datatool/display");
         }
      }
   }

   public void addFileLoadedListener(FileLoadedListener listener)
   {
      if (fileLoadedListeners == null)
      {
         fileLoadedListeners = new Vector<FileLoadedListener>();
      }

      fileLoadedListeners.add(listener);
   }

   public void registerFileLoaded(String dbLabel, String fileType,
    String formatVersion, TeXPath texPath)
   {
      if (fileLoadedListeners != null)
      {
         for (FileLoadedListener listener : fileLoadedListeners)
         {
            listener.fileLoaded(dbLabel, fileType, formatVersion, texPath);
         }
      }
   }

   /**
    * Writes information about the named database to STDOUT. For debugging.
    */ 
   public void showDbInfo(String name)
   throws TeXSyntaxException
   {
      DataBase db = (databases == null ? null : databases.get(name));
      TeXParser parser = getParser();
      TeXParserListener listener = getListener();

      int numCols = 0;
      int numRows = 0;

      Vector<String> keys = new Vector<String>();

      if (db == null)
      {
         System.out.println("No DataBase map");
      }
      else
      {
         numCols = db.getColumnCount();
         numRows = db.getRowCount();
   
         System.out.format("Database '%s': row count = %d, column count = %d%n",
          name, numRows, numCols);
   
         DataToolHeaderRow headers = db.getHeaders();
   
         if (headers == null)
         {
            System.out.println("No header set.");
         }
         else
         {
            System.out.println("Header row found. Number of columns: "
              + headers.size());
   
            for (DataToolHeader header : headers)
            {
               TeXObject title = header.getTitle();

               System.out.format("Header for column %d (key=%s, type=%d): %s%n",
                 header.getColumnIndex(), header.getColumnLabel(), header.getType(),
                 title == null ? "NULL" : title.toString(parser));

               keys.add(header.getColumnLabel());
            }
         }
   
         DataToolRows rows = db.getData();
   
         if (rows == null)
         {
            System.out.println("No content set.");
         }
         else
         {
            System.out.println("Content found. Number of rows: "
              + rows.size());
   
            for (DataToolEntryRow row : rows)
            {
               System.out.format("Row %d (column count: %d):%n",
                 row.getRowIndex(), row.size());
   
               for (DataToolEntry entry : row)
               {
                  TeXObject value = entry.getContents();
   
                  System.out.format("Column %d (%s): %s%n", 
                    entry.getColumnIndex(), value.getClass().getSimpleName(),
                    value.toString(parser));
               }
            }
         }
      }

      System.out.println("Internal Commands");

      ControlSequence headerCs = parser.getControlSequence(
         getHeaderRegisterName(name));

      if (headerCs == null)
      {
         System.out.println("No header command.");
      }
      else
      {
         System.out.println("Header command: " + headerCs);

         if (headerCs instanceof TokenRegister)
         {
            TeXObject contents = ((TokenRegister)headerCs).getContents(parser);

            if (contents.isEmpty())
            {
               System.out.println("Empty");
            }
            else if (contents instanceof DataToolHeaderRow)
            {
               ((DataToolHeaderRow)contents).info();
            }
            else
            {
               showHeaderContent(TeXParserUtils.toList(contents, parser));
            }
         }
         else
         {
            System.out.println("Not a TokenRegister!");
         }
      }

      ControlSequence contentsCs = parser.getControlSequence(
         getContentsRegisterName(name));

      if (contentsCs == null)
      {
         System.out.println("No contents command.");
      }
      else
      {
         System.out.println("Contents command: " + contentsCs);

         if (contentsCs instanceof TokenRegister)
         {
            TeXObject contents = ((TokenRegister)contentsCs).getContents(parser);

            if (contents.isEmpty())
            {
               System.out.println("Empty");
            }
            else if (contents instanceof DataToolRows)
            {
               ((DataToolRows)contents).info();
            }
            else
            {
               showDataContent(TeXParserUtils.toList(contents, parser));
            }
         }
         else
         {
            System.out.println("Not a TokenRegister!");
         }
      }

      ControlSequence rowCountCs = parser.getControlSequence(
         getRowCountRegisterName(name));

      if (rowCountCs == null)
      {
         System.out.println("No row count command.");
      }
      else
      {
         System.out.println("Row count command: " + rowCountCs);
      }

      ControlSequence colCountCs = parser.getControlSequence(
         getColumnCountRegisterName(name));

      if (colCountCs == null)
      {
         System.out.println("No column count command.");
      }
      else
      {
         System.out.println("Column count command: " + colCountCs);
      }

      for (String key : keys)
      {
         ControlSequence cs = parser.getControlSequence(
           getColumnHeaderName(name, key));

         if (cs == null)
         {
            System.out.format("No mapping command for column '%s'%n", key);
         }
         else
         {
            System.out.format("Mapping command for column '%s': %s%n",
               key, cs);
         }
      }
   }

   public void showHeaderContent(TeXObjectList contents)
   {
      TeXParser parser = getParser();
      TeXParserListener listener = getListener();

      int colIdx = 0;
      int i = 0;
      int actualColIdx;

      TeXObjectList substack = listener.createStack();

      for (i = 0; i < contents.size(); i++)
      {
         colIdx++;

         TeXObject obj = contents.get(i);

         while (obj instanceof Ignoreable && i < contents.size()-1)
         {
            i++;
            obj = contents.get(i);
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@plist@elt@w"))
         {
            System.out.println("Invalid start of header block "+colIdx
             + ". Expected \\db@plist@elt@w found: "+obj);

            i++;
            break;
         }

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing content after \\db@plist@elt@w for column index "+colIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@col@id@w"))
         {
            System.out.println(
              "Expected \\db@col@id@w after \\db@plist@elt@w for column index "
                + colIdx+". Found "+obj);

            i++;
            break;
         }

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj instanceof UserNumber)
         {
            actualColIdx = ((UserNumber)obj).getValue();
         }
         else
         {
            substack.clear();
            substack.add(obj);

            while (i + 1 < contents.size())
            {
               TeXObject nextObj = contents.get(i+1);

               if (nextObj instanceof CharObject)
               {
                  i++;
                  substack.add(nextObj);
               }
               else
               {
                  break;
               }
            }

            obj = substack;

            try
            {
               actualColIdx = Integer.parseInt(obj.toString(parser));
            }
            catch (NumberFormatException e)
            {
               System.out.println(
                 "Expected number after \\db@col@id@w. Found "+obj);
               break;
            }
         }

         if (actualColIdx != colIdx)
         {
            System.out.println("Header columns out of sequence. Found index "
             + actualColIdx + " in block "+colIdx);
         }

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing content after \\db@col@id@w "+actualColIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@col@id@end@"))
         {
            System.out.println(
              "Expected \\db@col@id@end@ after \\db@col@id@w "
                + actualColIdx+". Found "+obj);

            i++;
            break;
         }

         System.out.println("Header block for column "+actualColIdx);
         System.out.println("\\db@plist@elt@w");
         System.out.println("\\db@col@id@w "+actualColIdx+"%");
         System.out.println("\\db@col@id@end@");

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing content after \\db@col@id@end@ for header "
                + actualColIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@key@id@w"))
         {
            System.out.println(
              "Expected \\db@key@id@w after \\db@col@id@end@ for header "
                + actualColIdx+". Found "+obj);

            i++;
            break;
         }

         substack.clear();

         while (i < contents.size()-1)
         {
            i++;
            obj = contents.get(i);

            if (TeXParserUtils.isControlSequence(obj, "db@key@id@end@"))
            {
               break;
            }

            substack.add(obj);
         }

         if (i >= contents.size())
         {
            System.out.println(
              "Missing closing header block following \\db@key@id@w for column "
              +actualColIdx);
            break;
         }

         System.out.println("\\db@key@id@w "
           + substack.toString(parser)+"%");
         System.out.println("\\db@key@id@end@");

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing content after \\db@col@id@end@ for header "
                + actualColIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@type@id@w"))
         {
            System.out.println(
              "Expected \\db@type@id@w after \\db@key@id@end@ for header "
                + actualColIdx+". Found "+obj);

            i++;
            break;
         }

         substack.clear();

         while (i < contents.size()-1)
         {
            i++;
            obj = contents.get(i);

            if (TeXParserUtils.isControlSequence(obj, "db@type@id@end@"))
            {
               break;
            }

            substack.add(obj);
         }

         if (i >= contents.size())
         {
            System.out.println(
              "Missing closing header block following \\db@type@id@w for column "
              +actualColIdx);
            break;
         }

         System.out.println("\\db@type@id@w "
           + substack.toString(parser)+"%");
         System.out.println("\\db@type@id@end@");

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing content after \\db@col@id@end@ for header "
                + actualColIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@header@id@w"))
         {
            System.out.println(
              "Expected \\db@header@id@w after \\db@type@id@end@ for header "
                + actualColIdx+". Found "+obj);

            i++;
            break;
         }

         substack.clear();

         while (i < contents.size()-1)
         {
            i++;
            obj = contents.get(i);

            if (TeXParserUtils.isControlSequence(obj, "db@header@id@end@"))
            {
               break;
            }

            substack.add(obj);
         }

         if (i >= contents.size())
         {
            System.out.println(
              "Missing closing header block following \\db@header@id@w for column "
              +actualColIdx);
            break;
         }

         System.out.println("\\db@header@id@w "
           + substack.toString(parser)+"%");
         System.out.println("\\db@header@id@end@");

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing end header block \\db@col@id@w for column "+actualColIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@col@id@w"))
         {
            System.out.println(
              "Expected \\db@col@id@w after \\db@header@id@end@ for column "
                + actualColIdx+". Found "+obj);

            i++;
            break;
         }

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         int endColIdx;

         if (obj instanceof UserNumber)
         {
            endColIdx = ((UserNumber)obj).getValue();
         }
         else
         {
            substack.clear();
            substack.add(obj);

            while (i + 1 < contents.size())
            {
               TeXObject nextObj = contents.get(i+1);

               if (nextObj instanceof CharObject)
               {
                  i++;
                  substack.add(nextObj);
               }
               else
               {
                  break;
               }
            }

            obj = substack;

            try
            {
               endColIdx = Integer.parseInt(obj.toString(parser));
            }
            catch (NumberFormatException e)
            {
               System.out.println(
                 "Expected number after \\db@col@id@w. Found "+obj);
               break;
            }
         }

         if (endColIdx != actualColIdx)
         {
            System.out.println("End header block index "+endColIdx
             + "does not match start header block index "+actualColIdx);
         }

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing end header index block \\db@col@id@end@ for column "
               + actualColIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@col@id@end@"))
         {
            System.out.println(
              "Expected \\db@col@id@end@ after \\db@col@id@w "
                + actualColIdx+". Found "+obj);

            i++;
            break;
         }

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing end header block \\db@plist@elt@end@ for column "+actualColIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@plist@elt@end@"))
         {
            System.out.println(
              "Expected \\db@plist@elt@end@ after \\db@col@id@end@ "
                + actualColIdx+". Found "+obj);

            i++;
            break;
         }

         System.out.println("\\db@col@id@w " + actualColIdx+"%");
         System.out.println("\\db@col@id@end@");
         System.out.println("\\db@plist@elt@end@");
      }

      if (i < contents.size())
      {
         System.out.println("Remaining content: ");

         for ( ; i < contents.size(); i++)
         {
            TeXObject obj = contents.get(i);

            if (obj instanceof ControlSequence 
                && ((ControlSequence)obj).getName().startsWith("db@"))
            {
               System.out.println(obj.format());
            }
            else
            {
               System.out.print(obj.format());
            }
         }
      }
   }

   public void showDataContent(TeXObjectList contents)
   {
      TeXParser parser = getParser();
      TeXParserListener listener = getListener();

      int rowIdx = 0;
      int i = 0;
      int actualRowIdx;

      TeXObjectList substack = listener.createStack();

      for (i = 0; i < contents.size(); i++)
      {
         rowIdx++;

         TeXObject obj = contents.get(i);

         while (obj instanceof Ignoreable && i < contents.size()-1)
         {
            i++;
            obj = contents.get(i);
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@row@elt@w"))
         {
            System.out.println("Invalid start of data block "+rowIdx
             + ". Expected \\db@plist@elt@w found: "+obj);

            i++;
            break;
         }

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing content after \\db@row@elt@w for row index "+rowIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@row@id@w"))
         {
            System.out.println(
              "Expected \\db@col@id@w after \\db@row@id@w for row index "
                + rowIdx+". Found "+obj);

            i++;
            break;
         }

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj instanceof UserNumber)
         {
            actualRowIdx = ((UserNumber)obj).getValue();
         }
         else
         {
            substack.clear();
            substack.add(obj);

            while (i + 1 < contents.size())
            {
               TeXObject nextObj = contents.get(i+1);

               if (nextObj instanceof CharObject)
               {
                  i++;
                  substack.add(nextObj);
               }
               else
               {
                  break;
               }
            }

            obj = substack;

            try
            {
               actualRowIdx = Integer.parseInt(obj.toString(parser));
            }
            catch (NumberFormatException e)
            {
               System.out.println(
                 "Expected number after \\db@row@id@w. Found "+obj);
               break;
            }
         }

         if (actualRowIdx != rowIdx)
         {
            System.out.println("Rows out of sequence. Found index "
             + actualRowIdx + " in block "+rowIdx);
         }

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing content after \\db@row@id@w "+actualRowIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@row@id@end@"))
         {
            System.out.println(
              "Expected \\db@row@id@end@ after \\db@row@id@w "
                + actualRowIdx+". Found "+obj);

            i++;
            break;
         }

         System.out.println("Start of Row "+actualRowIdx);
         System.out.println("\\db@row@elt@w");
         System.out.println("\\db@row@id@w "+actualRowIdx+"%");
         System.out.println("\\db@row@id@end@");

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing content after \\db@row@id@end@");
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@row@elt@w", "db@col@id@w"))
         {
            System.out.println(
              "Expected \\db@col@id@w or \\db@row@elt@w after \\db@row@id@w "
                + actualRowIdx+". Found "+obj);

            i++;
            break;
         }

         ControlSequence markerCs = (ControlSequence)obj;

         int colIdx = 0;
         int actualColIdx = 0;

         while (!markerCs.getName().equals("db@row@elt@w"))
         {
            colIdx++;

            // \\db@col@id@w <n>db@col@id@end@

            i++;
            obj = null;

            if (i < contents.size())
            {
               obj = contents.get(i);

               while (obj instanceof Ignoreable && i < contents.size()-1)
               {
                  i++;
                  obj = contents.get(i);
               }
            }

            if (obj instanceof UserNumber)
            {
               actualColIdx = ((UserNumber)obj).getValue();
            }
            else
            {
               substack.clear();
               substack.add(obj);

               while (i < contents.size()-1)
               {
                  TeXObject nextObj = contents.get(i+1);

                  if (nextObj instanceof CharObject)
                  {
                     i++;
                     substack.add(nextObj);
                  }
                  else
                  {
                     break;
                  }
               }

               obj = substack;

               try
               {
                  actualColIdx = Integer.parseInt(obj.toString(parser));
               }
               catch (NumberFormatException e)
               {
                  System.out.println(
                    "Expected number after \\db@col@id@w. Found "+obj);
                  break;
               }
            }

            if (actualColIdx != colIdx)
            {
               System.out.println("Columns out of sequence. Found index "
                + actualColIdx + " in block "+colIdx +" for row "+actualRowIdx);
            }

            i++;
            obj = null;

            if (i < contents.size())
            {
               obj = contents.get(i);

               while (obj instanceof Ignoreable && i < contents.size()-1)
               {
                  i++;
                  obj = contents.get(i);
               }
            }

            if (obj == null || obj instanceof Ignoreable)
            {
               System.out.println(
                 "Missing content after \\db@col@id@w "+actualColIdx);
               break;
            }

            if (!TeXParserUtils.isControlSequence(obj, "db@col@id@end@"))
            {
               System.out.println(
                 "Expected \\db@col@id@end@ after \\db@col@id@w "
                   + actualColIdx+". Found "+obj);

               i++;
               break;
            }

            System.out.println("Column "+actualColIdx);
            System.out.println("\\db@col@id@w "+actualColIdx+"%");
            System.out.println("\\db@col@id@end@");

            // \\db@col@elt@w <value>\\db@col@elt@end@

            i++;
            obj = null;

            if (i < contents.size())
            {
               obj = contents.get(i);

               while (obj instanceof Ignoreable && i < contents.size()-1)
               {
                  i++;
                  obj = contents.get(i);
               }
            }

            if (obj == null || obj instanceof Ignoreable)
            {
               System.out.println(
                 "Missing content after \\db@col@id@end@ for column "+actualColIdx);
               break;
            }

            if (!TeXParserUtils.isControlSequence(obj, "db@col@elt@w"))
            {
               System.out.println(
                 "Expected \\db@col@elt@w after \\db@col@id@end@ for column "
                   + actualColIdx+". Found "+obj);

               i++;
               break;
            }

            substack.clear();

            while (i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);

               if (TeXParserUtils.isControlSequence(obj, "db@col@elt@end@"))
               {
                  break;
               }

               substack.add(obj);
            }

            if (i >= contents.size())
            {
               System.out.println(
                 "Missing closing value block following \\db@col@elt@w for column "
                 +actualColIdx);
               break;
            }

            System.out.println("\\db@col@elt@w "+substack.toString(parser)+"%");
            System.out.println("\\db@col@elt@end@");

            // end column marker

            i++;
            obj = null;

            if (i < contents.size())
            {
               obj = contents.get(i);

               while (obj instanceof Ignoreable && i < contents.size()-1)
               {
                  i++;
                  obj = contents.get(i);
               }
            }

            if (obj == null || obj instanceof Ignoreable)
            {
               System.out.println(
                 "Missing end column block \\db@col@id@w for column "+actualColIdx);
               break;
            }

            if (!TeXParserUtils.isControlSequence(obj, "db@col@id@w"))
            {
               System.out.println(
                 "Expected \\db@col@id@w after \\db@col@elt@end@ for column "
                   + actualColIdx+". Found "+obj);

               i++;
               break;
            }

            i++;
            obj = null;

            if (i < contents.size())
            {
               obj = contents.get(i);

               while (obj instanceof Ignoreable && i < contents.size()-1)
               {
                  i++;
                  obj = contents.get(i);
               }
            }

            int endColIdx;

            if (obj instanceof UserNumber)
            {
               endColIdx = ((UserNumber)obj).getValue();
            }
            else
            {
               substack.clear();
               substack.add(obj);

               while (i + 1 < contents.size())
               {
                  TeXObject nextObj = contents.get(i+1);

                  if (nextObj instanceof CharObject)
                  {
                     i++;
                     substack.add(nextObj);
                  }
                  else
                  {
                     break;
                  }
               }

               obj = substack;

               try
               {
                  endColIdx = Integer.parseInt(obj.toString(parser));
               }
               catch (NumberFormatException e)
               {
                  System.out.println(
                    "Expected number after \\db@col@id@w. Found "+obj);
                  break;
               }
            }

            if (endColIdx != actualColIdx)
            {
               System.out.println("End column block index "+endColIdx
                + "does not match start column block index "+actualColIdx);
            }

            i++;
            obj = null;

            if (i < contents.size())
            {
               obj = contents.get(i);

               while (obj instanceof Ignoreable && i < contents.size()-1)
               {
                  i++;
                  obj = contents.get(i);
               }
            }

            if (obj == null || obj instanceof Ignoreable)
            {
               System.out.println(
                 "Missing content after \\db@col@id@w "+endColIdx
                   + " for row "+actualRowIdx);
               break;
            }

            if (!TeXParserUtils.isControlSequence(obj, "db@col@id@end@"))
            {
               System.out.println(
                 "Expected \\db@col@id@end@ after \\db@col@id@w "+endColIdx
                   +" for row " + actualRowIdx+". Found "+obj);

               i++;
               break;
            }

            System.out.println("\\db@col@id@w "+endColIdx+"%");
            System.out.println("\\db@col@id@end@ % End of Column "+actualColIdx);

            // is there another column or is this the end of the row?

            i++;
            obj = null;

            if (i < contents.size())
            {
               obj = contents.get(i);

               while (obj instanceof Ignoreable && i < contents.size()-1)
               {
                  i++;
                  obj = contents.get(i);
               }
            }

            if (obj == null || obj instanceof Ignoreable)
            {
               System.out.println(
                 "Missing content after \\db@col@id@end@ for row "+actualRowIdx);
               break;
            }

            if (!TeXParserUtils.isControlSequence(obj, "db@row@elt@w", "db@col@id@w"))
            {
               System.out.println(
                 "Expected \\db@col@id@w or \\db@row@elt@w after \\db@col@id@end@ "
                   + actualRowIdx+". Found "+obj);

               i++;
               break;
            }

            markerCs = (ControlSequence)obj;
         }

         // marker should now be db@row@elt@w

         // \\db@row@id@w <n>\\db@row@id@end@

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing content after \\db@row@elt@w for row "+actualRowIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@row@id@w"))
         {
            System.out.println(
              "Expected \\db@row@id@w after \\db@row@elt@w at end of row "
                + actualRowIdx+". Found "+obj);

            i++;
            break;
         }

         int endRowIdx;

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Expected number after \\db@row@id@w for row "+actualRowIdx);
            break;
         }

         if (obj instanceof UserNumber)
         {
            endRowIdx = ((UserNumber)obj).getValue();
         }
         else
         {
            substack.clear();
            substack.add(obj);

            while (i + 1 < contents.size())
            {
               TeXObject nextObj = contents.get(i+1);

               if (nextObj instanceof CharObject)
               {
                  i++;
                  substack.add(nextObj);
               }
               else
               {
                  break;
               }
            }

            obj = substack;

            try
            {
               endRowIdx = Integer.parseInt(obj.toString(parser));
            }
            catch (NumberFormatException e)
            {
               System.out.println(
                 "Expected number after \\db@row@id@w. Found "+obj);
               break;
            }
         }

         if (endRowIdx != actualRowIdx)
         {
            System.out.println("End row block index "+endRowIdx
             + "does not match start row block index "+actualRowIdx);
         }

         i++;
         obj = null;

         if (i < contents.size())
         {
            obj = contents.get(i);

            while (obj instanceof Ignoreable && i < contents.size()-1)
            {
               i++;
               obj = contents.get(i);
            }
         }

         if (obj == null || obj instanceof Ignoreable)
         {
            System.out.println(
              "Missing content after \\db@row@id@w "+endRowIdx);
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "db@row@id@end@"))
         {
            System.out.println(
              "Expected \\db@row@id@end@ after \\db@row@id@w "+endRowIdx
                +". Found "+obj);

            i++;
            break;
         }

         System.out.println("End of Row "+actualRowIdx);
         System.out.println("\\db@row@elt@w");
         System.out.println("\\db@row@id@w "+endRowIdx+"%");
         System.out.println("\\db@row@id@end@");
      }

      if (i < contents.size())
      {
         System.out.println("Remaining content: ");

         for ( ; i < contents.size(); i++)
         {
            TeXObject obj = contents.get(i);

            if (obj instanceof ControlSequence 
                && ((ControlSequence)obj).getName().startsWith("db@"))
            {
               System.out.println(obj.format());
            }
            else
            {
               System.out.print(obj.format());
            }
         }
      }
   }

   private DataToolBaseSty dataToolBaseSty;

   private ConcurrentHashMap<String,DataBase> databases;

   private Vector<FileLoadedListener> fileLoadedListeners;

   public static final String ERROR_DB_EXISTS="datatool.db_exists";
   public static final String ERROR_DB_DOESNT_EXIST="datatool.db_doesnt_exist";
   public static final String ERROR_MISMATCHED="datatool.mismatched";
   public static final String ERROR_HEADER_EXISTS="datatool.header.exists";
   public static final String ERROR_HEADER_DOESNT_EXIST="datatool.header.doesnt_exist";
   public static final String ERROR_INVALID_HEADER="datatool.invalid.header";
   public static final String ERROR_INVALID_CONTENTS
     ="datatool.invalid.contents";
   public static final String ERROR_ROW_NOT_FOUND
     ="datatool.row.not.found";
   public static final String ERROR_NO_COLUMNS
     ="datatool.no.columns";

   public static final String MESSAGE_LOADDB
     ="datatool.loaddb.message";

}
