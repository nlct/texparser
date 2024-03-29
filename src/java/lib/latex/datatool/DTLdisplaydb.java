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
import com.dickimawbooks.texparserlib.latex.latex3.*;

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

      TokenListCommand contentTl = new TokenListCommand(DataToolSty.CONTENT_VAR);
      parser.putControlSequence(true, contentTl);

      TokenListCommand alignTl = new TokenListCommand(DataToolSty.ALIGN_VAR);
      parser.putControlSequence(true, alignTl);

      TokenListCommand rowTl = new TokenListCommand(DataToolSty.ROW_VAR);
      parser.putControlSequence(true, rowTl);

      TokenListCommand tmpTl = new TokenListCommand(DataToolBaseSty.TMPB_VAR);

      SequenceCommand seqCs;
      SequenceCommand colIndexes;

      colIndexes = listener.getSequenceCommand(
        DataToolSty.ONLY_COLUMNS_SEQ, stack);

      if (colIndexes == null)
      {
         colIndexes = new SequenceCommand(DataToolSty.ONLY_COLUMNS_SEQ);
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
         seqCs = listener.getSequenceCommand(DataToolSty.ONLY_KEYS_SEQ, stack);

         if (seqCs != null && !seqCs.isEmpty())
         {
            for (int i = 0; i < seqCs.size(); i++)
            {
               String key = parser.expandToString(seqCs.get(i), stack);
               DataToolHeader header = db.getHeader(key);
               int colIdx = header.getColumnIndex();

               colIndexes.append(new UserNumber(colIdx));
            }
         }
         else
         {
            Vector<Integer> excls = null;

            seqCs = listener.getSequenceCommand(
              DataToolSty.OMIT_COLUMNS_SEQ, stack);

            if (seqCs != null && !seqCs.isEmpty())
            {
               excls = new Vector<Integer>(seqCs.size());

               for (int i = 0; i < seqCs.size(); i++)
               {
                  TeXObject obj = seqCs.get(i);

                  Numerical num = TeXParserUtils.toNumerical(obj, parser, stack);
                  excls.add(Integer.valueOf(num.number(parser)));
               }
            }
            else
            {
               seqCs = listener.getSequenceCommand(
                 DataToolSty.OMIT_KEYS_SEQ, stack);

               if (seqCs != null && !seqCs.isEmpty())
               {
                  for (int i = 0; i < seqCs.size(); i++)
                  {
                     String key = parser.expandToString(seqCs.get(i), stack);
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

      parser.putControlSequence(true, new SequenceCommand(
       DataToolSty.COLUMN_INDEXES_SEQ, colIndexes));

      parser.getSettings().localSetRegister(
        DataToolSty.MAX_COLS_INT,
        new UserNumber(colIndexes.size()));

      NumericRegister maxColsReg = parser.getSettings().getNumericRegister(
        DataToolSty.MAX_COLS_INT);

      parser.getSettings().localSetRegister("dtlcolumnnum", UserNumber.ZERO);
      parser.getSettings().localSetRegister("dtlrownum", UserNumber.ZERO);

      NumericRegister colNumReg =
        parser.getSettings().getNumericRegister("dtlcolumnnum");
      NumericRegister rowNumReg =
        parser.getSettings().getNumericRegister("dtlrownum");

      parser.getSettings().localSetRegister(DataToolSty.ROW_IDX_INT,
        UserNumber.ZERO);

      NumericRegister tabRowNumReg =
        parser.getSettings().getNumericRegister(DataToolSty.ROW_IDX_INT);

      TokenListCommand userAlign = listener.getTokenListCommand(
        DataToolSty.USER_ALIGN, stack);

      if (!userAlign.isEmpty())
      {
         parser.putControlSequence(true,
          new TokenListCommand(DataToolSty.ALIGN_VAR, userAlign));
      }

      TokenListCommand userHeader = listener.getTokenListCommand(
        DataToolSty.USER_HEADER, stack);

      if (userHeader.isEmpty())
      {
         if (!TeXParserUtils.isTrue(DataToolSty.INCLUDE_HEADER_BOOL, parser))
         {
            userHeader = new TokenListCommand(
              DataToolSty.USER_HEADER, 
              TeXParserUtils.createStack(parser, TokenListCommand.EMPTY));

            parser.putControlSequence(true, userHeader);
         }
      }
      else
      {
         parser.putControlSequence(true,
          new TokenListCommand(DataToolSty.ROW_VAR, userHeader));
      }

      if (userAlign.isEmpty() || userHeader.isEmpty())
      {
         for (int i = 0; i < colIndexes.size(); i++)
         {
            Numerical num = TeXParserUtils.toNumerical(colIndexes.get(i),
              parser, stack);
            int colIdx = num.number(parser);
            colNumReg.advance(parser, UserNumber.ONE);

            DataToolHeader header = db.getHeader(colIdx);
            TeXNumber type = header.getNumericalType(parser);

            TeXObjectList substack = listener.createStack();

            if (userAlign.isEmpty())
            {
               substack.add(listener.getControlSequence("dtladdalign"));
               substack.add(alignTl);
               substack.add(type);
               substack.add(colNumReg);
               substack.add(maxColsReg);

               TeXParserUtils.process(substack, parser, stack);
            }

            if (userHeader.isEmpty())
            {
               if (!rowTl.isEmpty())
               {
                  rowTl.append(listener.getTab());
               }

               tmpTl.clear();
               substack.add(listener.getControlSequence("dtladdheaderalign"));
               substack.add(tmpTl);
               substack.add(type);
               substack.add(colNumReg);
               substack.add(maxColsReg);

               TeXParserUtils.process(substack, parser, stack);

               TeXObjectList headerCell = listener.createStack();
               headerCell.add(listener.getControlSequence("dtlcolumnheader"));

               Group grp = listener.createGroup();
               headerCell.add(grp);
               grp.addAll(tmpTl.getContent());


               TeXObject title = header.getTitle();

               if (title == null)
               {
                  headerCell.add(listener.createGroup(header.getColumnLabel()));
               }
               else
               {
                  grp = listener.createGroup();
                  headerCell.add(grp);
                  grp.add((TeXObject)title.clone(), true);
               }

               rowTl.rightConcat(headerCell.expandonce(parser, stack));

            }
         }
      }

      TeXObjectList substack = listener.createStack();

      if (isLong)
      {//TODO
      }
      else
      {
         substack.add(listener.getControlSequence("DTLdisplaydbAddBegin"));
         substack.add(contentTl);
         substack.add(alignTl);

         Group grp = listener.createGroup();

         if (!rowTl.isEmpty())
         {
            grp.addAll(rowTl.getContent());
         }

         substack.add(grp);
      }

      TeXParserUtils.process(substack, parser, stack);

      for (int i = 1; i <= numRows; i++)
      {
         tabRowNumReg.setValue(parser, new UserNumber(i));

         substack.add(
           listener.getControlSequence(DataToolSty.IF_DISPLAY_ROW));
         substack.add(tabRowNumReg);
         substack.add(contentTl);
         substack.add(TeXParserUtils.createGroup(parser,
          listener.getControlSequence(DataToolSty.DISPLAY_DB_ROW),
          contentTl, tabRowNumReg
         ));

         TeXParserUtils.process(substack, parser, stack);
      }

      if (isLong)
      {//TODO
      }
      else
      {
         substack.add(listener.getControlSequence("DTLdisplaydbAddEnd"));
         substack.add(contentTl);
      }

      TeXParserUtils.process(substack, parser, stack);

      // The tabular/longtable code should now be in the definition
      // of \l__datatool_content_tl

      ControlSequence preCs =
        listener.getControlSequence(DataToolSty.PRE_DISPLAY);

      if (preCs.isEmpty())
      {
         return contentTl.getContent();
      }
      else
      {
         substack.add(preCs);
         substack.addAll(contentTl.getContent());
         return substack;
      }
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
                new SequenceCommand(DataToolSty.OMIT_COLUMNS_SEQ));
            }
            else
            {
               CsvList csvList = TeXParserUtils.toCsvList(optArg, parser);

               parser.putControlSequence(true,
                SequenceCommand.createFromClist(
                 parser, DataToolSty.OMIT_COLUMNS_SEQ, csvList));
            }

            parser.putControlSequence(true,
             new SequenceCommand(DataToolSty.ONLY_COLUMNS_SEQ));

            parser.putControlSequence(true,
             new SequenceCommand(DataToolSty.ONLY_KEYS_SEQ));

            parser.putControlSequence(true,
             new SequenceCommand(DataToolSty.OMIT_KEYS_SEQ));
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

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack);
   }

   protected boolean isLong;
   protected DataToolSty sty;
}
