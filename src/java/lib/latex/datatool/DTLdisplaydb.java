/*
    Copyright (C) 2023 Nicola L.C. Talbot
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
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLdisplaydb extends Command
{
   public DTLdisplaydb(DataToolSty sty)
   {
      this("DTLdisplaydb", false, sty);
   }

   public DTLdisplaydb(String name, boolean isLong, DataToolSty sty)
   {
      super(name);
      this.isLong = isLong;
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DTLdisplaydb(getName(), isLong, sty);
   }

   protected TeXObjectList construct(String dbLabel, 
      TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      DataBase db = sty.getDataBase(dbLabel);
      int numRows = db.getRowCount();
      int numCols = db.getColumnCount();

      parser.putControlSequence(true,
       new TextualContentCommand("dtldbname", dbLabel));

      parser.putControlSequence(true,
       new TokenListCommand("l__datatool_content_tl"));

      parser.putControlSequence(true,
       new TokenListCommand("l__datatool_align_tl"));

      parser.putControlSequence(true,
       new TokenListCommand("l__datatool_row_tl"));

      SequenceCommand cs;
      SequenceCommand colIndexes;

      colIndexes = listener.getSequenceCommand(
        "l__datatool_only_columns_seq", stack);

      if (colIndexes == null)
      {
         colIndexes = new SequenceCommand("l__datatool_only_columns_seq");
      }

      if (!colIndexes.isEmpty())
      {
         for (int i = 0; i < colIndexes.size(); i++)
         {
            TeXObject obj = colIndexes.get(i);

            Numerical num = TeXParserUtils.toNumerical(obj, parser, stack);
            colIndexes.set(i, num);
         }
      }
      else
      {
         cs = listener.getSequenceCommand(
           "l__datatool_only_keys_seq", stack);

         if (cs != null && !cs.isEmpty())
         {
            for (int i = 0; i < cs.size(); i++)
            {
               String key = parser.expandToString(cs.get(i), stack);
               DataToolHeader header = db.getHeader(key);
               int colIdx = header.getColumnIndex();

               colIndexes.append(new UserNumber(colIdx));
            }
         }
         else
         {
            Vector<Integer> excls = null;

            cs = listener.getSequenceCommand(
              "l__datatool_omit_columns_seq", stack);

            if (cs != null && !cs.isEmpty())
            {
               excls = new Vector<Integer>(cs.size());

               for (int i = 0; i < cs.size(); i++)
               {
                  TeXObject obj = cs.get(i);

                  Numerical num = TeXParserUtils.toNumerical(obj, parser, stack);
                  excls.add(Integer.valueOf(num.number(parser)));
               }
            }
            else
            {
               cs = listener.getSequenceCommand(
                 "l__datatool_omit_keys_seq", stack);

               if (cs != null && !cs.isEmpty())
               {
                  for (int i = 0; i < cs.size(); i++)
                  {
                     String key = parser.expandToString(cs.get(i), stack);
                     DataToolHeader header = db.getHeader(key);
                     int colIdx = header.getColumnIndex();
                     excls.add(Integer.valueOf(colIdx));
                  }
               }
            }

            for (int i = 1; i <= numCols; i++)
            {
               if (excls == null || !excls.contains(Integer.valueOf(i)))
               {
                  colIndexes.append(new UserNumber(i));
               }
            }
         }
      }

      if (colIndexes.isEmpty())
      {
         throw new LaTeXSyntaxException(parser, 
            DataToolSty.ERROR_NO_COLUMNS, dbLabel);
      }

      parser.getSettings().localSetRegister("l__datatool_max_cols_int",
        new UserNumber(colIndexes.size()));

      parser.getSettings().localSetRegister("dtlcolumnnum", UserNumber.ZERO);
      parser.getSettings().localSetRegister("dtlrownum", UserNumber.ZERO);

      TokenListCommand userAlign = listener.getTokenListCommand(
        "l__datatool_user_align_tl", stack);

      if (!userAlign.isEmpty())
      {
         parser.putControlSequence(true,
          new TokenListCommand("l__datatool_align_tl", userAlign));
      }

      TokenListCommand userHeader = listener.getTokenListCommand(
        "l__datatool_user_header_tl", stack);

      if (userHeader.isEmpty())
      {
         if (!listener.getLaTeX3Boolean(
              "l_datatool_include_header_bool", stack).booleanValue())
         {
            userHeader = new TokenListCommand("l__datatool_user_header_tl", 
              TeXParserUtils.createStack(parser, TokenListCommand.EMPTY));

            parser.putControlSequence(true, userHeader);
         }
      }
      else
      {
         parser.putControlSequence(true,
          new TokenListCommand("l__datatool_row_tl", userHeader));
      }


      // The tabular/longtable code should now be in the definition
      // of \l__datatool_content_tl

      return listener.getTokenListCommand("l__datatool_content_tl", stack)
               .getContent();
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean isKeyValOpt;

      if (isLong)
      {
         isKeyValOpt = true;
      }
      else
      {
         isKeyValOpt = (popModifier(parser, stack, '*') == '*');
      }

      TeXObject optArg = popOptArg(parser, stack);

      String dbLabel = popLabelString(parser, stack);

      if (sty.dbExists(dbLabel))
      {
         parser.startGroup();

         if (isKeyValOpt)
         {
            if (optArg != null && !optArg.isEmpty())
            {
               sty.processDisplayKeys(optArg, stack);
            }
         }
         else
         {
            if (optArg == null || optArg.isEmpty())
            {
               parser.putControlSequence(true,
                new SequenceCommand("l__datatool_omit_columns_seq"));
            }
            else
            {
               CsvList csvList = TeXParserUtils.toCsvList(optArg, parser);

               parser.putControlSequence(true,
                SequenceCommand.createFromClist(
                 parser, "l__datatool_omit_columns_seq", csvList));
            }

            parser.putControlSequence(true,
             new SequenceCommand("l__datatool_only_columns_seq"));

            parser.putControlSequence(true,
             new SequenceCommand("l__datatool_only_keys_seq"));

            parser.putControlSequence(true,
             new SequenceCommand("l__datatool_omit_keys_seq"));
         }

         TeXObjectList list = construct(dbLabel, parser, stack);

         parser.endGroup();

         return list;
      }
      else
      {
         throw new LaTeXSyntaxException(parser,
           DataToolSty.ERROR_DB_DOESNT_EXIST, dbLabel);
      }
   }

   protected boolean isLong;
   protected DataToolSty sty;
}
