/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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
import java.io.PrintWriter;
import java.io.BufferedReader;

import java.nio.file.Files;

import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.IllegalCharsetNameException;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Date;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.inputenc.InputEncSty;

public class DataBase
{
   public DataBase(String name)
   {
      this(name, null, null);
   }

   public DataBase(String name, DataToolHeaderRow headers,
    DataToolRows rows)
   {
      this.name = name;
      update(headers, rows);
   }

   public void update(DataToolHeaderRow headers,
    DataToolRows rows)
   {
      this.headerRow = headers;
      this.dataRows = rows;
   }

   public int getColumnCount()
   {
      return headerRow == null ? 0 : headerRow.size();
   }

   public int getRowCount()
   {
      return dataRows == null ? 0 : dataRows.size();
   }

   public DataToolHeaderRow getHeaders()
   {
      return headerRow;
   }

   public DataToolHeader getHeader(String key)
   {
      return headerRow.getHeader(key);
   }

   public DataToolHeader getHeader(int columnIndex)
   {
      return headerRow.getHeader(columnIndex);
   }

   public DataToolRows getData()
   {
      return dataRows;
   }

   public String getName()
   {
      return name;
   }

   /**
    * Reads a file containing data. The database is globally defined.
    * If successful, <code>\\dtllastloadeddb </code> will be defined to the
    * database name.
    */ 
   public static void read(DataToolSty sty, TeXPath texPath,
       IOSettings settings, 
       TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();
      TeXApp texApp = listener.getTeXApp();
      Charset charset = texPath.getEncoding();

      if (charset == null)
      {
         charset = listener.getCharSet();
      }

      if (charset != null)
      {
         texPath.setEncoding(charset);
      }

      FileFormatType format = settings.getFormat();
      String version = settings.getFileVersion();

      if (texPath.exists())
      {
         if (format == FileFormatType.DTLTEX || format  == FileFormatType.DBTEX)
         {
            stack.push(new PostReadHook(sty, texPath));

            listener.addFileReference(texPath);

            String charsetName = null;

            BufferedReader in = null;

            try
            {
               in = Files.newBufferedReader(texPath.getPath(), charset);
               String line = in.readLine();

               if (line != null)
               {
                  Matcher m = FILE_IDENTIFIER.matcher(line);

                  if (m.matches())
                  {
                     String formatStr = m.group(1).toLowerCase();
  
                     if (formatStr.equals("dbtex"))
                     {
                        format = FileFormatType.DBTEX;
                     }
                     else
                     {
                        format = FileFormatType.DTLTEX;
                     }

                     String formatVersion = m.group(2);

                     if (formatVersion.equals("2.0")
                       || formatVersion.equals("3.0")
                        )
                     {
                        version = formatVersion;
                     }
                     else
                     {
                        texApp.warning(parser,
                          texApp.getMessage(ERROR_FILE_FORMAT_VERSION, 
                            formatStr, formatVersion));
                     }

                     charsetName = m.group(3);

                     try
                     {
                        charset = Charset.forName(charsetName);
                     }
                     catch (IllegalCharsetNameException
                          | UnsupportedCharsetException e)
                     {
                        charsetName = InputEncSty.getCharSetName(m.group(3));
                        charset = Charset.forName(charsetName);
                     }

                     texPath.setEncoding(charset);

                     texApp.message(texApp.getMessage(FILE_INFO, 
                        formatStr, formatVersion, charset.name()));
                  }
               }
            }
            catch (MalformedInputException e)
            {
               texApp.warning(parser, 
                 texApp.getMessage(ERROR_FILE_INFO_FAILED,
                   texPath, texApp.getDefaultCharset()));

               parser.logMessage(e);
            }
            catch (UnsupportedCharsetException e)
            {
               texApp.warning(parser, 
                 texApp.getMessage(
                   InputEncSty.ERROR_UNKNOWN_ENCODING, charsetName));

               parser.logMessage(e);
            }

            if (in != null)
            {
               in.close();
            }

            parser.putControlSequence(true,
              new TextualContentCommand("__texparser_current_file_type_tl",
                format.toString()));

            parser.putControlSequence(true,
             new TextualContentCommand("__texparser_current_file_version_tl",
               version));

            listener.input(texPath, stack);
         }
         else
         {
            String name = settings.getDefaultName();

            DataBase db = null;

            if (sty.dbExists(name))
            {
               db = sty.getDataBase(name);
            }

            if (db == null)
            {
               db = sty.createDataBase(name, true);
            }
            else if (!settings.isAppendAllowed())
            {
               throw new LaTeXSyntaxException(parser,
                 DataToolSty.ERROR_DB_EXISTS, name);
            }

            int separator = settings.getSeparator();
            int delimiter = settings.getDelimiter();

            int catcode = parser.getCatCode(separator);

            if (catcode != TeXParser.TYPE_OTHER)
            {
               parser.setCatCode(true, separator, TeXParser.TYPE_OTHER);
            }

            catcode = parser.getCatCode(delimiter);

            if (catcode != TeXParser.TYPE_OTHER)
            {
               parser.setCatCode(true, delimiter, TeXParser.TYPE_OTHER);
            }

            FileMapType mapType;

            if (settings.isCsvLiteral())
            {
               mapType = FileMapType.VERBATIM_EXCEPT_ESC_SYM;
            }
            else
            {
               mapType = FileMapType.TEX;
            }

            parser.fileMap(texPath, mapType, new CsvReadHandler(db, settings));
         }
      }
   }

   public static void write(DataToolSty sty, String filename,
       TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      IOSettings settings = IOSettings.fetchWriteSettings(sty, parser, stack);

      TeXApp texApp = parser.getListener().getTeXApp();

      String defExt = settings.getDefaultExtension();

      TeXPath path = new TeXPath(parser, filename, defExt, false);

      String name = settings.getDefaultName();

      DataBase db = sty.getDataBase(name);

      if (db == null)
      {
         throw new LaTeXSyntaxException(parser,
            DataToolSty.ERROR_DB_DOESNT_EXIST, name);
      }

      db.write(parser, stack, path, settings);
   }

   public void write(TeXParser parser, TeXObjectList stack, TeXPath path,
      IOSettings settings)
    throws IOException
   {
      TeXApp texApp = parser.getListener().getTeXApp();

      if (!texApp.isWriteAccessAllowed(path))
      {
         throw new TeXSyntaxException(parser, 
            TeXApp.MESSAGE_NO_WRITE, path);
      }

      if (path.exists())
      {
         switch (settings.getOverwriteOption())
         {
            case ERROR:
               throw new TeXSyntaxException(parser, 
                  TeXSyntaxException.ERROR_FILE_OVERWRITE_FORBIDDEN, path);
            case WARN:
               texApp.warning(parser,
                 texApp.getMessage(TeXApp.WARNING_OVERWRITING, path));
         }
      }

      Charset charset = texApp.getDefaultCharset();

      FileFormatType format = settings.getFormat();
      String version = settings.getFileVersion();
      boolean incHeader = settings.isHeaderIncluded();
      IOExpandOption expandOpt = settings.getExpandOption();

      PrintWriter writer = null;

      try
      {
         writer = new PrintWriter(Files.newBufferedWriter(path.getPath(), charset));

         texApp.message(texApp.getMessage(TeXApp.MESSAGE_WRITING, path));

         if (format == FileFormatType.DBTEX || format == FileFormatType.DTLTEX)
         {
            writer.print("% ");
            writer.print(format.toString().toUpperCase());
            writer.print(" ");
            writer.print(version);
            writer.print(" ");
            writer.println(charset.name());

            writer.print("% ");
            writer.println(parser.getListener().getTeXApp().getMessage(
              "datatool.write.comment",
              texApp.getApplicationName(),
              texApp.getApplicationVersion(),
              new Date()));

            if (format == FileFormatType.DBTEX)
            {
               if (version.equals("3.0"))
               {
                  writer.print("\\DTLdbProvideData{");
                  writer.print(name);
                  writer.println("}%");

                  writer.println("\\DTLreconstructdata");
                  writer.println("{% Header");

                  for (DataToolHeader header : headerRow)
                  {
                     writer.print("\\dtl@db@header@reconstruct{");
                     writer.print(header.getColumnIndex());
                     writer.print("}{");
                     writer.print(header.getColumnLabel());
                     writer.print("}{");
                     writer.print(header.getType());
                     writer.print("}{");

                     TeXObject title = header.getTitle();

                     if (title == null)
                     {
                        writer.print(header.getColumnLabel());
                     }
                     else
                     {
                        writer.print(title.toString(parser));
                     }

                     writer.println("}%");
                  }

                  writer.println("}% End of Header");

                  writer.println("{% Content");

                  for (DataToolEntryRow row : dataRows)
                  {
                     writer.print("% Row ");
                     writer.println(row.getRowIndex());
                     writer.print("\\dtl@db@row@reconstruct{");
                     writer.print(row.getRowIndex());
                     writer.println("}%");

                     writer.print("{% Row ");
                     writer.print(row.getRowIndex());
                     writer.println(" Content");

                     for (DataToolEntry entry : row)
                     {
                        writer.print("  \\dtl@db@col@reconstruct{");
                        writer.print(entry.getColumnIndex());
                        writer.print("}% Column ");
                        writer.println(entry.getColumnIndex());

                        writer.print("  {% Column ");
                        writer.print(entry.getColumnIndex());
                        writer.println("  Content");

                        TeXObject contents = entry.getContents();

                        if (parser.isStack(contents)
                             && ((TeXObjectList)contents).size() == 1
                             && ((TeXObjectList)contents).firstElement()
                                  instanceof DatumElement)
                        {
                           contents = ((TeXObjectList)contents).firstElement();
                        }

                        writer.print("   ");

                        if (contents instanceof DatumElement)
                        {
                           ((DatumElement)contents).write(parser,
                              writer, format, version);
                        }
                        else
                        {
                           writer.print("\\dtl@db@value@reconstruct{");
                           writer.print(contents.toString(parser));
                           writer.print("}");
                        }

                        writer.println("%");

                        writer.print("  }% End of Column ");
                        writer.print(entry.getColumnIndex());
                        writer.println("  Content");
                     }

                     writer.println("}%");
                  }

                  writer.println("}% End of Content");

                  writer.print("{");
                  writer.print(getRowCount());
                  writer.print("}");
                  writer.print("{");
                  writer.print(getColumnCount());
                  writer.println("}");

                  writer.println("{% Key to index");

                  for (DataToolHeader header : headerRow)
                  {
                     writer.print("\\dtl@db@reconstruct@keyindex{");
                     writer.print(header.getColumnLabel());
                     writer.print("}{");
                     writer.print(header.getColumnIndex());
                     writer.println("}%");
                  }

                  writer.println("}% End of key to index");
               }
               else
               {// dbtex v2.0
                  writer.print("\\DTLifdbexists{");
                  writer.print(name);
                  writer.println("}%");
                  writer.print("{\\PackageError{datatool}{Database `");
                  writer.print(name);
                  writer.println("' already exists}{}%");
                  writer.println("\\aftergroup\\endinput}{}%");
                  writer.println("\\bgroup\\makeatletter");
                  writer.print("\\dtl@message{Reconstructing database `");
                  writer.print(name);
                  writer.println("'}%");

                  writer.println("\\expandafter\\global\\expandafter");
                  writer.print("\\newtoks\\csname dtlkeys@");
                  writer.print(name);
                  writer.println("\\endcsname");

                  writer.println("\\expandafter\\global");
                  writer.print("\\csname dtlkeys@");
                  writer.print(name);
                  writer.println("\\endcsname={%");

                  for (DataToolHeader header : headerRow)
                  {
                     writer.print("% Header block for column ");
                     writer.println(header.getColumnIndex());

                     writer.println("\\db@plist@elt@w");
                     writer.print("\\db@col@id@w ");
                     writer.print(header.getColumnIndex());
                     writer.println("%");
                     writer.println("\\db@col@id@end@");

                     writer.print("\\db@key@id@w  ");
                     writer.print(header.getColumnLabel());
                     writer.println("%");
                     writer.println("\\db@key@id@end@");

                     writer.print("\\db@type@id@w  ");
                     writer.print(header.getType());
                     writer.println("%");
                     writer.println("\\db@type@id@end@");

                     writer.print("\\db@header@id@w  ");

                     TeXObject title = header.getTitle();

                     if (title == null)
                     {
                        writer.print(header.getColumnLabel());
                     }
                     else
                     {
                        writer.print(title.toString(parser));
                     }

                     writer.println("%");
                     writer.println("\\db@header@id@end@");

                     writer.print("\\db@col@id@w ");
                     writer.print(header.getColumnIndex());
                     writer.println("%");
                     writer.println("\\db@col@id@end@");
                     writer.println("\\db@plist@elt@end@");
                  }

                  writer.println("}%");

                  writer.println("\\expandafter\\global\\expandafter");
                  writer.print("\\newtoks\\csname dtldb@");
                  writer.print(name);
                  writer.println("\\endcsname");

                  writer.println("\\expandafter\\global");
                  writer.print("\\csname dtldb@");
                  writer.print(name);
                  writer.println("\\endcsname={%");

                  for (DataToolEntryRow row : dataRows)
                  {
                     writer.print("% Start of Row ");
                     writer.println(row.getRowIndex());

                     writer.println("\\db@row@elt@w");
                     writer.print("\\db@row@id@w ");
                     writer.print(row.getRowIndex());
                     writer.println("%");
                     writer.println("\\db@row@id@end@");

                     for (DataToolEntry entry : row)
                     {
                        writer.print("% Column ");
                        writer.println(entry.getColumnIndex());

                        writer.println("\\db@col@id@w ");
                        writer.print(entry.getColumnIndex());
                        writer.println("%");
                        writer.println("\\db@col@id@end@");

                        writer.print("\\db@col@elt@w ");

                        writer.print(entry.getContents().toString(parser));

                        writer.println("%");
                        writer.println("\\db@col@elt@end@");

                        writer.println("\\db@col@id@w ");
                        writer.print(entry.getColumnIndex());
                        writer.println("%");
                        writer.print("\\db@col@id@end@ % End of Column ");
                        writer.println(entry.getColumnIndex());
                     }

                     writer.print("% End of Row ");
                     writer.println(row.getRowIndex());

                     writer.println("\\db@row@elt@w");
                     writer.print("\\db@row@id@w ");
                     writer.print(row.getRowIndex());
                     writer.println("%");
                     writer.println("\\db@row@id@end@");
                  }

                  writer.println("}%"); // end of content

                  writer.println("\\expandafter\\global");
                  writer.println(" \\expandafter\\newcount\\csname dtlrows@");
                  writer.print(name);
                  writer.println("\\endcsname");

                  writer.println("\\expandafter\\global");
                  writer.println(" \\csname dtlrows@");
                  writer.print(name);
                  writer.println("\\endcsname=");
                  writer.print(getRowCount());
                  writer.println("\\relax");

                  writer.println("\\expandafter\\global");
                  writer.println(" \\expandafter\\newcount\\csname dtlcols@");
                  writer.print(name);
                  writer.println("\\endcsname");

                  writer.println("\\expandafter\\global");
                  writer.println(" \\csname dtlcols@");
                  writer.print(name);
                  writer.println("\\endcsname=");
                  writer.print(getColumnCount());
                  writer.println("\\relax");

                  for (DataToolHeader header : headerRow)
                  {
                     writer.println("\\expandafter");
                     writer.print(" \\gdef\\csname dtl@ci@");
                     writer.print(name);
                     writer.print("@");
                     writer.print(header.getColumnLabel());
                     writer.print("\\endcsname{");
                     writer.print(header.getColumnIndex());
                     writer.println("}%");
                  }

                  writer.print("\\def\\dtllastloadeddb{");
                  writer.print(name);
                  writer.println("}%");
               }
            }
            else
            {
               Hashtable<Integer,String> colMap
                  = new Hashtable<Integer,String>(getColumnCount());

               for (DataToolHeader header : headerRow)
               {
                  colMap.put(Integer.valueOf(header.getColumnIndex()),
                   header.getColumnLabel());
               }

               if (version.equals("3.0"))
               {// dtltex v3.0

                  writer.print("\\DTLdbProvideData{");
                  writer.print(name);
                  writer.println("}%");

                  for (DataToolEntryRow row : dataRows)
                  {
                     writer.println("\\DTLdbNewRow");

                     for (DataToolEntry entry : row)
                     {
                        TeXObject contents = entry.getContents();

                        switch (expandOpt)
                        {
                           case FULL:
                             contents = TeXParserUtils.expandFully(
                                           contents, parser, stack);
                           break;
                           case PROTECTED:
                             contents = TeXParserUtils.expandOnce(
                                           contents, parser, stack);
                           break;
                        }

                        writer.print("\\DTLdbNewEntry{");
                        writer.print(
                          colMap.get(Integer.valueOf(entry.getColumnIndex())));
                        writer.print("}{");
                        writer.print(contents.toString(parser));
                        writer.println("}%");
                     }
                  }

                  if (incHeader)
                  {
                     for (DataToolHeader header : headerRow)
                     {
                        writer.print("\\DTLdbSetHeader{");
                        writer.print(header.getColumnLabel());
                        writer.print("}{");

                        TeXObject title = header.getTitle();

                        if (title == null)
                        {
                           writer.print(header.getColumnLabel());
                        }
                        else
                        {
                           switch (expandOpt)
                           {
                              case FULL:
                                title = TeXParserUtils.expandFully(
                                           title, parser, stack);
                              break;
                              case PROTECTED:
                                title = TeXParserUtils.expandOnce(
                                           title, parser, stack);
                              break;
                           }

                           writer.print(title.toString(parser));
                        }

                        writer.println("}%");
                     }
                  }
               }
               else
               {// dtltex v2.0

                  writer.print("\\DTLnewdb{");
                  writer.print(name);
                  writer.println("}%");

                  for (DataToolEntryRow row : dataRows)
                  {
                     writer.println("\\DTLnewrow{");
                     writer.print(name);
                     writer.println("}%");

                     for (DataToolEntry entry : row)
                     {
                        TeXObject contents = entry.getContents();

                        switch (expandOpt)
                        {
                           case FULL:
                             contents = TeXParserUtils.expandFully(
                                           contents, parser, stack);
                           break;
                           case PROTECTED:
                             contents = TeXParserUtils.expandOnce(
                                           contents, parser, stack);
                           break;
                        }

                        writer.print("\\DTLnewdbentry{");
                        writer.print(name);
                        writer.print("}{");
                        writer.print(
                          colMap.get(Integer.valueOf(entry.getColumnIndex())));
                        writer.print("}{");
                        writer.print(contents.toString(parser));
                        writer.println("}%");
                     }
                  }

                  if (incHeader)
                  {
                     for (DataToolHeader header : headerRow)
                     {
                        writer.print("\\DTLsetheader{");
                        writer.print(name);
                        writer.print("}{");
                        writer.print(header.getColumnLabel());
                        writer.print("}{");

                        TeXObject title = header.getTitle();

                        if (title == null)
                        {
                           writer.print(header.getColumnLabel());
                        }
                        else
                        {
                           switch (expandOpt)
                           {
                              case FULL:
                                title = TeXParserUtils.expandFully(
                                           title, parser, stack);
                              break;
                              case PROTECTED:
                                title = TeXParserUtils.expandOnce(
                                           title, parser, stack);
                              break;
                           }

                           writer.print(title.toString(parser));
                        }

                        writer.println("}%");
                     }
                  }

                  writer.print("\\def\\dtllastloadeddb{");
                  writer.print(name);
                  writer.println("}%");
               }
            }
         }
         else
         {// csv or tsv

            int separator = settings.getSeparator();

            if (incHeader)
            {
               for (int i =0; i < headerRow.size(); i++)
               {
                  DataToolHeader header = headerRow.get(i);

                  if (i > 0)
                  {
                     writer.write(separator);
                  }

                  TeXObject title = header.getTitle();
                  String value;

                  if (title == null)
                  {
                     value = header.getColumnLabel();
                  }
                  else
                  {
                     switch (expandOpt)
                     {
                        case FULL:
                          title = TeXParserUtils.expandFully(title, parser, stack);
                        break;
                        case PROTECTED:
                          title = TeXParserUtils.expandOnce(title, parser, stack);
                        break;
                     }

                     value = title.toString(parser);
                  }

                  writer.print(prepareCSV(value, settings));
               }

               writer.println();
            }

            for (DataToolEntryRow row : dataRows)
            {
               for (int i = 0; i < row.size(); i++)
               {
                  DataToolEntry entry = row.get(i);

                  if (i > 0)
                  {
                     writer.write(separator);
                  }

                  TeXObject contents = entry.getContents();

                  switch (expandOpt)
                  {
                     case FULL:
                       contents = TeXParserUtils.expandFully(
                                     contents, parser, stack);
                     break;
                     case PROTECTED:
                       contents = TeXParserUtils.expandOnce(
                                     contents, parser, stack);
                     break;
                  }

                  String value = contents.toString(parser);

                  writer.print(prepareCSV(value, settings));
               }

               writer.println();
            }
         }
      }
      finally
      {
         if (writer != null)
         {
            writer.close();
         }
      }
   }

   protected CharSequence prepareCSV(String value, IOSettings settings)
   {
      int delimiter = settings.getDelimiter();
      int separator = settings.getSeparator();
      AddDelimiterOption addDelimOpt = settings.getAddDelimiterOption();
      EscapeCharsOption escCharsOpt = settings.getEscapeCharsOption();

      StringBuilder builder = new StringBuilder();

      boolean foundSep = false;

      for (int i = 0; i < value.length(); )
      {
         int cp = value.codePointAt(i);
         i += Character.charCount(cp);

         if (cp == separator)
         {
            foundSep = true;
         }
         else if (cp == delimiter)
         {
            if (escCharsOpt != EscapeCharsOption.NONE)
            {
               builder.appendCodePoint('\\');
            }
         }
         else if (cp == '\\')
         {
            if (escCharsOpt == EscapeCharsOption.DELIM_BKSL)
            {
               builder.appendCodePoint('\\');
            }
         }
         else if (cp == '\n' || cp == '\r' || cp == '\f')
         {
            if (addDelimOpt == AddDelimiterOption.NEVER)
            {
               cp = ' ';
            }
            else
            {// ensure delimiters are added
               foundSep = true;
            }
         }

         builder.appendCodePoint(cp);
      }

      if (addDelimOpt == AddDelimiterOption.ALWAYS
          || (foundSep && addDelimOpt == AddDelimiterOption.DETECT))
      {
         value = builder.toString();
         builder.setLength(0);
         builder.appendCodePoint(delimiter); 
         builder.append(value); 
         builder.appendCodePoint(delimiter); 
      }

      return builder;
   }

   public String toString()
   {
      return String.format("%s[name=%s,header=%s,data=%s]",
       getClass().getSimpleName(), name, headerRow, dataRows);
   }

   private DataToolHeaderRow headerRow;
   private DataToolRows dataRows;
   private String name;

   public static Pattern FILE_IDENTIFIER = Pattern.compile("% (DBTEX|DTLTEX) ([0-9\\.]+) ([a-zA-Z0-9\\-]+)");

   public static String FILE_INFO = "datatool.file_info";
   public static String ERROR_FILE_INFO_FAILED = "datatool.file_info_failed";
   public static String ERROR_FILE_FORMAT_VERSION = "datatool.unknown_file_format_version";
}
