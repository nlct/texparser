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

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public abstract class Action
{
   protected Action()
   {
   }

   public abstract void doAction() throws IOException;

   public static Action getAction(String actionName, DataToolSty sty,
     KeyValList options, TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      Action action;

      if (actionName.equals("new"))
      {
         action = new ActionNew();
      }
      else if (actionName.equals("new row"))
      {
         action = new ActionNewRow();
      }
      else if (actionName.equals("new entry"))
      {
         action = new ActionNewEntry();
      }
      else if (actionName.equals("add column"))
      {
         action = new ActionAddColumn();
      }
      else if (actionName.equals("display"))
      {
         action = new ActionDisplay();
      }
      else
      {
         throw new LaTeXSyntaxException(parser,
           "datatool.unsupported.action", actionName);
      }

      action.actionName = actionName;
      action.sty = sty;
      action.parser = parser;
      action.stack = stack;

      if (options == null)
      {
         return action;
      }

      action.dbName = options.getString("name", parser, stack);

      if (action.dbName != null)
      {
         action.dbNames = action.dbName.trim().split(" *, *");
         action.dbName = action.dbNames[0];
      }

      Numerical num = options.getNumerical("column", parser, stack);

      if (num != null)
      {
         action.columnIndex = num.number(parser);
      }

      num = options.getNumerical("column2", parser, stack);

      if (num != null)
      {
         action.column2Index = num.number(parser);
      }

      num = options.getNumerical("row", parser, stack);

      if (num != null)
      {
         action.rowIndex = num.number(parser);
      }

      num = options.getNumerical("row2", parser, stack);

      if (num != null)
      {
         action.row2Index = num.number(parser);
      }

      String typeStr = options.getString("type", parser, stack);

      if (typeStr != null)
      {
         typeStr = typeStr.trim();

         if (typeStr.equals("string"))
         {
            action.dataType = DatumType.STRING;
         }
         else if (typeStr.equals("int") || typeStr.equals("integer"))
         {
            action.dataType = DatumType.INTEGER;
         }
         else if (typeStr.equals("decimal") || typeStr.equals("real"))
         {
            action.dataType = DatumType.DECIMAL;
         }
         else if (typeStr.equals("currency"))
         {
            action.dataType = DatumType.CURRENCY;
         }
         else
         {
            throw new LaTeXSyntaxException(parser, 
             LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, "type", typeStr);
         }
      }

      action.columnKey = options.getString("key", parser, stack);
      action.column2Key = options.getString("key2", parser, stack);

      TeXObject obj = options.getValue("options");

      if (obj != null)
      {
         action.optionsList = CsvList.getList(parser, obj);
      }

      obj = options.getValue("assign");

      if (obj != null)
      {
         action.assignList = CsvList.getList(parser, obj);
      }

      obj = options.getValue("value");

      if (obj != null)
      {
         action.value = obj;
      }

      obj = options.getValue("expand-once-value");

      if (obj != null)
      {
         action.value = TeXParserUtils.expandOnce(obj, parser, stack);
      }

      obj = options.getValue("expand-value");

      if (obj != null)
      {
         action.value = TeXParserUtils.expandFully(obj, parser, stack);
      }

      return action;
   }

   public String getDataBaseName() throws IOException
   {
      if (dbName == null)
      {
         dbName = "untitled";

         ControlSequence cs
           = parser.getControlSequence("l__datatool_default_dbname_tl");

         if (cs != null)
         {
            dbName = parser.expandToString(cs, stack);
         }
      }

      return dbName;
   }

   public DataBase getDataBase() throws IOException
   {
      if (db != null)
      {
         return db;
      }

      db = sty.getDataBase(getDataBaseName());

      return db;
   }

   public String getColumnKey(boolean require, boolean appendMissing)
      throws IOException
   {
      if (columnKey != null)
      {
         return columnKey;
      }

      if (columnIndex == 0)
      {
         if (require)
         {
            throw new LaTeXSyntaxException(parser,
              LaTeXSyntaxException.ERROR_MISSING_OR, "key", "column");
         }
         else
         {
            return null;
         }
      }

      if (getDataBase() == null)
      {
         throw new LaTeXSyntaxException(parser,
            DataToolSty.ERROR_DB_DOESNT_EXIST, dbName);
      }

      DataToolHeaderRow headers = db.getHeaders();
      DataToolHeader header = headers.getHeader(columnIndex);

      if (header == null)
      {
         if (appendMissing && columnIndex == headers.getMaxIndex()+1)
         {
            TeXObjectList title = TeXParserUtils.createStack(parser,
              parser.getListener().getControlSequence("dtldefaultkey"),
              new UserNumber(columnIndex));

            String columnKey = parser.expandToString(title, stack);

            header = new DataToolHeader(sty, columnIndex, columnKey,
              dataType, parser.getListener().createString(columnKey));
            headers.add(header);
         }
         else
         {
            throw new LaTeXSyntaxException(parser,
               DataToolBaseSty.INDEX_OUT_OF_RANGE, columnIndex);
         }
      }

      columnKey = header.getColumnLabel();

      return columnKey;
   }

   protected DataToolSty sty;
   protected String actionName;
   protected TeXParser parser;
   protected TeXObjectList stack;

   protected String[] dbNames;
   protected String dbName, columnKey, column2Key;
   protected int columnIndex=0, column2Index=0, rowIndex=0, row2Index=0;

   protected DatumType dataType = DatumType.UNKNOWN;

   protected CsvList optionsList, assignList;

   protected TeXObject value;

   protected DataBase db;
}

class ActionNew extends Action
{
   @Override
   public void doAction() throws IOException
   {
      sty.createDataBase(getDataBaseName(), true);
   }
}

class ActionNewRow extends Action
{
   @Override
   public void doAction() throws IOException
   {
      DataToolRows rows = sty.getContents(getDataBaseName());

      DataToolEntryRow row = new DataToolEntryRow(sty);
      rows.add(row);

      if (assignList != null)
      {
         DataToolHeaderRow headers = sty.getHeaderContents(dbName);

         for (int i = 0; i < assignList.size(); i++)
         {
            TeXObjectList list
              = TeXParserUtils.toList(assignList.getValue(i), parser);

            TeXObjectList keyList = parser.getListener().createStack();

            while (!list.isEmpty())
            {
               TeXObject obj = list.popToken();

               if (obj instanceof CharObject
                    && ((CharObject)obj).getCharCode() == '=')
               {
                  break;
               }

               keyList.add(obj);
            }

            String key = parser.expandToString(keyList, list).trim();

            if (!key.isEmpty())
            {
               TeXObject element = null;
               list.trim();

               if (list.size() == 1)
               {
                  element = TeXParserUtils.popArg(parser, list);
               }
               else
               {
                  element = list;
               }

               DataToolHeader header = headers.getHeader(key);

               if (header == null)
               {
                  header = new DataToolHeader(sty, headers.getMaxIndex()+1, key);
                  headers.add(header);
               }

               DataToolEntry entry = new DataToolEntry(sty, header.getColumnIndex(),
                  element);
               row.add(entry);

               TeXObject contents = entry.getContents();

               if (contents instanceof DataElement)
               {
                  header.updateType((DataElement)contents);
               }

            }
         }

      }

      sty.update(dbName, rows);
   }
}

class ActionNewEntry extends Action
{
   @Override
   public void doAction() throws IOException
   {
      sty.addNewEntry(getDataBaseName(), getColumnKey(true, true), value);
   }
}

class ActionAddColumn extends Action
{
   @Override
   public void doAction() throws IOException
   {
      DataToolHeaderRow headers = sty.getHeaderContents(getDataBaseName());
      DataToolHeader header = headers.getHeader(getColumnKey(true, false));

      if (header != null)
      {
         throw new LaTeXSyntaxException(parser,
           DataToolSty.ERROR_HEADER_EXISTS, columnKey);
      }

      if (columnIndex == 0)
      {
         columnIndex = headers.getMaxIndex()+1;
      }

      TeXObject title = value;

      if (title == null)
      {
         title = parser.getListener().createString(columnKey);
      }

      header = new DataToolHeader(sty, columnIndex, columnKey, dataType, title);

      headers.add(header);

      sty.update(dbName, headers);
   }
}

class ActionDisplay extends Action
{
   @Override
   public void doAction() throws IOException
   {
      TeXParserListener listener = parser.getListener();
      TeXObjectList list = parser.getListener().createStack();

      if (optionsList != null)
      {
         sty.processDisplayKeys(optionsList, stack);
      }

      list.add(listener.getControlSequence("DTLdisplaydb"));

      list.add(listener.createGroup(getDataBaseName()));

      TeXParserUtils.process(list, parser, stack);
   }
}

