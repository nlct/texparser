/*
    Copyright (C) 2013 Nicola L.C. Talbot
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

public class DataToolSty extends LaTeXSty
{
   public DataToolSty(KeyValList options, LaTeXParserListener listener, 
      boolean loadParentOptions)
   throws IOException
   {
      super(options, "datatool", listener, loadParentOptions);
   }

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

      registerControlSequence(new GenericCommand("dtldefaultkey",
       null, getListener().createString("Column")));

      registerControlSequence(new GenericCommand("DTLunsettype"));
      registerControlSequence(new GenericCommand("DTLstringtype", null,
        new UserNumber(DataToolHeader.TYPE_STRING)));
      registerControlSequence(new GenericCommand("DTLinttype", null,
        new UserNumber(DataToolHeader.TYPE_INT)));
      registerControlSequence(new GenericCommand("DTLrealtype", null,
        new UserNumber(DataToolHeader.TYPE_REAL)));
      registerControlSequence(new GenericCommand("DTLcurrencytype", null,
        new UserNumber(DataToolHeader.TYPE_CURRENCY)));
   }

   protected void preOptions() throws IOException
   {
      getListener().requirepackage(null, "etoolbox", false);

      dataToolBaseSty = (DataToolBaseSty)getListener().requirepackage(
         null, "datatool-base", true);
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
   {
      return separator;
   }

   public int getDelimiter()
   {
      return delimiter;
   }

   public void setSeparator(int charCode)
   {
      separator = charCode;
   }

   public void setDelimiter(int charCode)
   {
      delimiter = charCode;
   }

   private int separator=',', delimiter='"';

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

   public static final String MESSAGE_LOADDB
     ="datatool.loaddb.message";
}
