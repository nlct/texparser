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

      registerControlSequence(new GenericCommand("DTLunsettype"));
      registerControlSequence(new GenericCommand("DTLstringtype", null,
        new UserNumber(DataToolHeader.TYPE_STRING)));
      registerControlSequence(new GenericCommand("DTLinttype", null,
        new UserNumber(DataToolHeader.TYPE_INT)));
      registerControlSequence(new GenericCommand("DTLrealtype", null,
        new UserNumber(DataToolHeader.TYPE_REAL)));
      registerControlSequence(new GenericCommand("DTLcurrencytype", null,
        new UserNumber(DataToolHeader.TYPE_CURRENCY)));

      registerControlSequence(
        new TextualContentCommand("@dtl@delimiter", "\""));

      registerControlSequence(
        new TextualContentCommand("@dtl@separator", ","));

      registerControlSequence(
        new TextualContentCommand("dtldisplayvalign", "c"));

      registerControlSequence(
         new TokenListCommand("dtldisplaystarttab"));

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

      // datatool v3.0:

      registerControlSequence(
        new TextualContentCommand("dtldisplaydbenv", "tabular"));

      registerControlSequence(
        new TextualContentCommand("dtldisplaylongdbenv", "longtable"));

      registerControlSequence(new AtNumberOfNumber(
       "__datatool_if_display_row:nNT", 3, 3));

      registerControlSequence(new DTLsetup(this));
      registerControlSequence(new DTLdbProvideData(this));
      registerControlSequence(new DTLdbNewRow(this));
      registerControlSequence(new DTLdbNewEntry(this));
      registerControlSequence(new DTLdbSetHeader(this));

      registerControlSequence(new DTLaddalign());
      registerControlSequence(new DTLaddheaderalign());

      getParser().getSettings().newcount(true, "l__datatool_max_cols_int");
      getParser().getSettings().newcount(true, "l__datatool_row_idx_int");
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
         new TokenListCommand("l__datatool_post_head_tl"));

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
      ControlSequence cs = getListener().getParser().getControlSequence(
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

      settings.newtoks(global, getContentsRegisterName(name));
      settings.newtoks(global, getHeaderRegisterName(name));
      settings.newcount(global, getRowCountRegisterName(name));
      settings.newcount(global, getColumnCountRegisterName(name));

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
      TeXParser parser = getListener().getParser();

      // does it start with a currency marker?

      if (entry instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)entry;
         TeXObject first = list.peekStack();

         if (first == null)
         {// empty
            return null;
         }

         if (dataToolBaseSty.isCurrencySymbol(first))
         {
            first = list.popStack(parser);

            // is the remainder numerical?

            try
            {
               return new DataCurrencyElement(this, first, 
                Double.parseDouble(list.toString(parser).trim()));
            }
            catch (NumberFormatException e)
            {// not numeric

               list.add(0, first);
               return new DataStringElement(list);
            }
         }
      }

      String str = entry.toString(parser).trim();

      // is it an integer?

      try
      {
         return new DataIntElement(this, Integer.parseInt(str));
      }
      catch (NumberFormatException e)
      {
      }

      // is it a real number?

      try
      {
         return new DataRealElement(this, Double.parseDouble(str));
      }
      catch (NumberFormatException e)
      {
      }

      if (entry instanceof TeXObjectList)
      {
         return new DataStringElement((TeXObjectList)entry);
      }

      DataStringElement elem = new DataStringElement();
      elem.add(entry);
      return elem;
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

      ControlSequence cs = parser.getControlSequence(
         getColumnCountRegisterName(name));

      if (cs == null || !(cs instanceof CountRegister))
      {
         throw new LaTeXSyntaxException(parser, 
           ERROR_DB_DOESNT_EXIST, name);
      }

      ((CountRegister)cs).setValue(db.getColumnCount());

      cs = parser.getControlSequence(
         getRowCountRegisterName(name));

      if (cs == null || !(cs instanceof CountRegister))
      {
         throw new LaTeXSyntaxException(parser, 
           ERROR_DB_DOESNT_EXIST, name);
      }

      ((CountRegister)cs).setValue(db.getRowCount());

      return db;
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
         {// TODO
         }
         else if (key.equals("headers"))
         {// TODO
         }
         else if (key.equals("expand"))
         {// TODO
         }
         else if (key.equals("format"))
         {// TODO
         }
         else if (key.equals("add-delimiter"))
         {// TODO
         }
         else if (key.equals("csv-escape-chars"))
         {// TODO
         }
         else if (key.equals("csv-content"))
         {// TODO
         }
         else if (key.equals("csv-blank"))
         {// TODO
         }
         else if (key.equals("csv-skip-lines") || key.equals("omitlines"))
         {// TODO
         }
         else if (key.equals("no-header") || key.equals("noheader"))
         {// TODO
         }
         else if (key.equals("auto-keys") || key.equals("autokeys"))
         {// TODO
         }
         else if (key.equals("overwrite"))
         {// TODO
         }
         else if (key.equals("load-action"))
         {// TODO
         }
         else if (key.equals("delimiter"))
         {
            String str = parser.expandToString(val, stack);
            setDelimiter(str.codePointAt(0));
         }
         else if (key.equals("separator"))
         {
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

   private DataToolBaseSty dataToolBaseSty;

   private ConcurrentHashMap<String,DataBase> databases;

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
