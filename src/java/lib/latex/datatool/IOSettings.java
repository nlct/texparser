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

import java.io.IOException;

import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.CsvList;
import com.dickimawbooks.texparserlib.latex.KeyValList;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

import com.dickimawbooks.texparserlib.latex.latex3.PropertyCommand;

public class IOSettings
{
   public IOSettings(DataToolSty sty)
   {
      this.sty = sty;
   }

   public static IOSettings fetchReadSettings(DataToolSty sty,
     TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      IOSettings settings = new IOSettings(sty);
      settings.fetchSettings(true, parser, stack);
      return settings;
   }

   public static IOSettings fetchWriteSettings(DataToolSty sty,
        TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      IOSettings settings = new IOSettings(sty);
      settings.fetchSettings(false, parser, stack);
      return settings;
   }

   public void fetchSettings(boolean isRead, TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      defaultName = TeXParserUtils.getControlSequenceValue(DataToolSty.IO_NAME,
        null, parser, stack);

      defaultExtension = TeXParserUtils.getControlSequenceValue(
        DataToolSty.DEFAULT_EXT, null, parser, stack);

      incHeader = !TeXParserUtils.isTrue("ifdtlnoheader", parser);

      String val = TeXParserUtils.getControlSequenceValue(DataToolSty.FORMAT,
        "csv", parser, stack);

      String formatStr = val;

      int idx = val.indexOf('-');

      if (idx > -1)
      {
         fileVersion = val.substring(idx+1);

         if (!(fileVersion.equals("2") || fileVersion.equals("3")))
         {
            throw new LaTeXSyntaxException(parser,
             LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
             "format="+val, "datatool/io");
         }

         fileVersion += ".0";

         formatStr = val.substring(0, idx);
      }

      if (formatStr.equals("dbtex"))
      {
         format = FileFormatType.DBTEX;
      }
      else if (formatStr.equals("dtltex"))
      {
         format = FileFormatType.DTLTEX;
      }
      else if (formatStr.equals("csv"))
      {
         format = FileFormatType.CSV;
      }
      else if (formatStr.equals("tsv"))
      {
         format = FileFormatType.TSV;
      }
      else
      {
         throw new LaTeXSyntaxException(parser,
          LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
          "format="+val, "datatool/io");
      }

      if (format == FileFormatType.CSV || format == FileFormatType.TSV)
      {
         separator = sty.getSeparator();
         delimiter = sty.getDelimiter();

         val = TeXParserUtils.getControlSequenceValue(
          DataToolSty.CSV_ESCAPE_CHARS, "double-delim", parser, stack);

         EscapeCharsOption opt = EscapeCharsOption.fromOptionName(val);

         if (opt == null)
         {
            throw new LaTeXSyntaxException(parser,
             LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
               "csv-escape-chars="+val, "datatool/io");
         }

         escCharsOpt = opt;
      }

      if (isRead)
      {
         if (format == FileFormatType.CSV || format == FileFormatType.TSV)
         {
            autoKeys = TeXParserUtils.isTrue("ifdtlautokeys", parser);

            ControlSequence cs = parser.getControlSequence(DataToolSty.CSV_KEYS_PROP);

            if (cs != null && !cs.isEmpty() && cs instanceof PropertyCommand)
            {
               PropertyCommand propCs = (PropertyCommand)cs;
               keys = new String[propCs.size()];

               for (int i = 0; i < keys.length; i++)
               {
                  @SuppressWarnings("unchecked")
                  TeXObject kv = propCs.get(Integer.valueOf(i+1));

                  if (kv != null)
                  {
                     keys[i] = parser.expandToString((TeXObject)kv.clone(), stack);
                  }
               }
            }
            else
            {
               keys = null;
            }

            cs = parser.getControlSequence(DataToolSty.CSV_HEADERS_PROP);

            if (cs != null && !cs.isEmpty() && cs instanceof PropertyCommand)
            {
               PropertyCommand propCs = (PropertyCommand)cs;
               headers = new TeXObject[propCs.size()];

               for (int i = 0; i < headers.length; i++)
               {
                  @SuppressWarnings("unchecked")
                  TeXObject kv = propCs.get(Integer.valueOf(i+1));

                  if (kv != null)
                  {
                     headers[i] = (TeXObject)kv.clone();
                  }
               }
            }
            else
            {
               headers = null;
            }

            skipLines = TeXParserUtils.toInt(
              parser.getListener().getControlSequence(DataToolSty.OMIT_LINES),
              parser, stack);

            val = TeXParserUtils.getControlSequenceValue(DataToolSty.CSV_BLANK,
             "ignore", parser, stack);

            CsvBlankOption opt = CsvBlankOption.fromOptionName(val);

            if (opt == null)
            {
               throw new LaTeXSyntaxException(parser,
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                  "csv-blank="+val, "datatool/io");
            }

            csvLiteral = !TeXParserUtils.isFalse(
              DataToolSty.CSV_LITERAL_CONTENT_BOOL, parser);
         }

         appendAllowed = !TeXParserUtils.isFalse(
           DataToolSty.APPEND_ALLOWED_BOOL, parser);

         trimElement = !TeXParserUtils.isFalse(
           DataToolSty.NEW_ELEMENT_TRIM_BOOL, parser);

         strictQuotes = !TeXParserUtils.isFalse(
           DataToolSty.IO_STRICT_QUOTES_BOOL, parser);
      }
      else
      {
         if (defaultName == null)
         {
            defaultName = TeXParserUtils.getControlSequenceValue(
              DataToolSty.DEFAULT_NAME, "untitled", parser, stack);
         }

         val = TeXParserUtils.getControlSequenceValue(DataToolSty.IO_OVERWRITE,
          "error", parser, stack);

         FileOverwriteOption fopt = FileOverwriteOption.fromOptionName(val);

         if (fopt == null)
         {
            throw new LaTeXSyntaxException(parser,
             LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
               "overwrite="+val, "datatool/io");
         }

         fileOverwriteOpt = fopt;

         if (format != FileFormatType.DBTEX)
         {
            val = TeXParserUtils.getControlSequenceValue(DataToolSty.IO_EXPAND,
             "none", parser, stack);

            IOExpandOption expOpt = IOExpandOption.fromOptionName(val);

            if (expOpt == null)
            {
               throw new LaTeXSyntaxException(parser,
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                  "expand="+val, "datatool/io");
            }

            expandOpt = expOpt;
         }

         if (format == FileFormatType.CSV || format == FileFormatType.TSV)
         {
            val = TeXParserUtils.getControlSequenceValue(
             DataToolSty.IO_ADD_DELIMITER, "detect", parser, stack);

            if (val.equals("always"))
            {
               addDelimOpt = AddDelimiterOption.ALWAYS;
            }
            else if (val.equals("never"))
            {
               addDelimOpt = AddDelimiterOption.NEVER;
            }
            else if (val.equals("detect"))
            {
               addDelimOpt = AddDelimiterOption.DETECT;
            }
            else
            {
               throw new LaTeXSyntaxException(parser,
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                  "add-delimiter="+val, "datatool/io");
            }

         }
      }
   }

   /**
    * Applies settings from CSV list without changing underlying
    * control sequences or package settings.
    */
   public void apply(KeyValList options, TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      for (Iterator<String> it = options.keySet().iterator();
           it.hasNext(); )
      {
         String key = it.next();

         TeXObject val = options.get(key);

         if (key.equals("name"))
         {
            if (val == null)
            {
               defaultName = null;
            }
            else
            {
               defaultName = parser.expandToString(val, stack);
            }
         }
         else if (key.equals("keys"))
         {
            if (val == null)
            {
               keys = null;
            }
            else
            {
               String strVal = parser.expandToString(val, stack).trim();

               if (strVal.isEmpty())
               {
                  keys = null;
               }
               else
               {
                  keys = strVal.split(" *, *");
               }
            }
         }
         else if (key.equals("headers"))
         {
            if (val == null && val.isEmpty())
            {
               headers = null;
            }
            else
            {
               CsvList csvList = TeXParserUtils.toCsvList(val, parser);
               headers = new TeXObject[csvList.size()];

               for (int i = 0; i < headers.length; i++)
               {
                  TeXObject kv = csvList.getValue(i);

                  if (!kv.isEmpty())
                  {
                     headers[i] = kv;
                  }
               }
            }
         }
         else if (key.equals("expand"))
         {
            String strVal = (val == null ?
               "protected" : parser.expandToString(val, stack).trim());

            IOExpandOption optVal = IOExpandOption.fromOptionName(strVal);

            if (optVal == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }

            expandOpt = optVal;
         }
         else if (key.equals("format"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String strVal = parser.expandToString(val, stack).trim();

            if (strVal.endsWith("-3"))
            {
               fileVersion = "3.0";
               strVal = strVal.substring(0, strVal.length()-2);
            }
            else if (strVal.endsWith("-2"))
            {
               fileVersion = "2.0";
               strVal = strVal.substring(0, strVal.length()-2);
            }
            else if (strVal.equals("tex"))
            {
               strVal = "dbtex";
               fileVersion = "2.0";
            }
            else if (!(strVal.equals("tsv") || strVal.equals("csv")))
            {
               fileVersion = "3.0";
            }

            FileFormatType optVal = FileFormatType.valueOf(strVal.toUpperCase());

            if (optVal == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }

            format = optVal;

            switch (format)
            {
               case CSV:
                  defaultExtension = "csv";
               break;
               case TSV:
                  separator = '\t';
                  defaultExtension = "tsv";
               break;
               case DBTEX:
                  defaultExtension = "dbtex";
               break;
               case DTLTEX:
                  defaultExtension = "dtltex";
               break;
            }
         }
         else if (key.equals("add-delimiter"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String strVal = parser.expandToString(val, stack).trim();

            AddDelimiterOption optVal = AddDelimiterOption.fromOptionName(strVal);

            if (optVal == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }

            addDelimOpt = optVal;
         }
         else if (key.equals("csv-escape-chars"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String strVal = parser.expandToString(val, stack).trim();

            EscapeCharsOption optVal = EscapeCharsOption.fromOptionName(strVal);

            if (optVal == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }

            escCharsOpt = optVal;
         }
         else if (key.equals("csv-content"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String strVal = parser.expandToString(val, stack).trim();

            if (strVal.equals("literal"))
            {
               csvLiteral = true;
            }
            else if (strVal.equals("tex"))
            {
               csvLiteral = false;
            }
            else
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }
         }
         else if (key.equals("csv-blank"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String strVal = parser.expandToString(val, stack).trim();

            CsvBlankOption optVal = CsvBlankOption.fromOptionName(strVal);

            if (optVal == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }

            csvBlankOpt = optVal;
         }
         else if (key.equals("csv-skip-lines") || key.equals("omitlines"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String strVal = val.toString(parser).trim();

            if (strVal.equals("false"))
            {
               skipLines = 0;
            }
            else
            {
               try
               {
                  skipLines = Integer.parseInt(strVal);
               }
               catch (NumberFormatException e)
               {
                  skipLines = TeXParserUtils.toInt(val, parser, stack);
               }
            }
         }
         else if (key.equals("no-header") || key.equals("noheader"))
         {
            String strVal = (val == null ? "" : val.toString(parser).trim());

            if (strVal.equals("") || strVal.equals("true"))
            {
               incHeader = false;
            }
            else if (strVal.equals("false"))
            {
               incHeader = true;
            }
            else
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }
         }
         else if (key.equals("auto-keys") || key.equals("autokeys"))
         {
            String strVal = (val == null ? "" : val.toString(parser).trim());

            if (strVal.equals("") || strVal.equals("true"))
            {
               autoKeys = true;
            }
            else if (strVal.equals("false"))
            {
               autoKeys = false;
            }
            else
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }
         }
         else if (key.equals("overwrite"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String strVal = parser.expandToString(val, stack).trim();

            FileOverwriteOption optVal = FileOverwriteOption.fromOptionName(strVal);

            if (optVal == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }

            fileOverwriteOpt = optVal;
         }
         else if (key.equals("load-action"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String strVal = parser.expandToString(val, stack).trim();
            boolean boolVal;

            if (strVal.equals("detect") || strVal.equals("append"))
            {
               boolVal = true;
            }
            else if (strVal.equals("create") || strVal.equals("old-style"))
            {
               boolVal = false;
            }
            else
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }

            appendAllowed = boolVal;
         }
         else if (key.equals("delimiter"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String strVal = parser.expandToString(val, stack);
            delimiter = strVal.codePointAt(0);
         }
         else if (key.equals("separator"))
         {
            if (val == null)
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
            }

            String strVal = parser.expandToString(val, stack);
            separator = strVal.codePointAt(0);
         }
         else if (key.equals("strict-quotes"))
         {// not supported with datatool.sty
            String strVal = (val == null ? "" : val.toString(parser).trim());

            if (strVal.equals("") || strVal.equals("true"))
            {
               strictQuotes = true;
            }
            else if (strVal.equals("false"))
            {
               strictQuotes = false;
            }
            else
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }
         }
         else if (key.equals("trim"))
         {
            String strVal = (val == null ? "" : val.toString(parser).trim());

            if (strVal.equals("") || strVal.equals("true"))
            {
               trimElement = true;
            }
            else if (strVal.equals("false"))
            {
               trimElement = false;
            }
            else
            {
               throw new TeXSyntaxException(parser,
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
            }
         }
         else
         {
            TeXApp texApp = parser.getListener().getTeXApp();

            texApp.warning(parser,
              texApp.getMessage(LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
               key));
         }
      }
   }

   public FileFormatType getFormat()
   {
      return format;
   }

   public void setFileFormat(FileFormatType format)
   {
      this.format = format;
   }

   public void setFileFormat(FileFormatType format, String version)
   throws TeXSyntaxException
   {
      this.format = format;
      setFileVersion(version);
   }

   public String getDefaultName()
   {
      return defaultName;
   }

   public void setDefaultName(String name)
   {
      defaultName = name;
   }

   public String getDefaultExtension()
   {
      return defaultExtension;
   }

   public void setDefaultExtension(String defExt)
   {
      defaultExtension = defExt;
   }

   public String getFileVersion()
   {
      return fileVersion;
   }

   public TeXParser getParser()
   {
      return sty == null ? null : sty.getParser();
   }

   public void setFileVersion(String version)
   throws TeXSyntaxException
   {
      if (
           (format == FileFormatType.DBTEX || format == FileFormatType.DTLTEX)
          && !(version.equals("2.0") || version.equals("3.0"))
         )
      {
         throw new LaTeXSyntaxException(getParser(),
           DataBase.ERROR_FILE_FORMAT_VERSION, version, format);
      }
      else
      {
         fileVersion = version;
      }
   }

   public boolean isHeaderIncluded()
   {
      return incHeader;
   }

   public void setHeaderIncluded(boolean isIncluded)
   {
      incHeader = isIncluded;
   }

   public int getSeparator()
   {
      return separator;
   }

   public void setSeparator(int codePoint)
   {
      separator = codePoint;
   }

   public int getDelimiter()
   {
      return delimiter;
   }

   public void setDelimiter(int codePoint)
   {
      delimiter = codePoint;
   }

   public FileOverwriteOption getOverwriteOption()
   {
      return fileOverwriteOpt;
   }

   public void setFileOverwriteOption(FileOverwriteOption opt)
   {
      fileOverwriteOpt = opt;
   }

   public AddDelimiterOption getAddDelimiterOption()
   {
      return addDelimOpt;
   }

   public void setAddDelimiterOption(AddDelimiterOption opt)
   {
      addDelimOpt = opt;
   }

   public EscapeCharsOption getEscapeCharsOption()
   {
      return escCharsOpt;
   }

   public void setEscapeCharsOption(EscapeCharsOption opt)
   {
      escCharsOpt = opt;
   }

   public boolean isCsvStrictQuotes()
   {
      return strictQuotes;
   }

   public void setCsvStrictQuotes(boolean on)
   {
      strictQuotes = on;
   }

   public CsvBlankOption getCsvBlankOption()
   {
      return csvBlankOpt;
   }

   public void setCsvBlankOption(CsvBlankOption opt)
   {
      csvBlankOpt = opt;
   }

   public boolean isCsvLiteral()
   {
      return csvLiteral;
   }

   public void setCsvLiteral(boolean isLiteral)
   {
      csvLiteral = isLiteral;
   }

   public IOExpandOption getExpandOption()
   {
      return expandOpt;
   }

   public void setExpandOption(IOExpandOption opt)
   {
      expandOpt = opt;
   }

   public boolean isAppendAllowed()
   {
      return appendAllowed;
   }

   public void setAppendAllowed(boolean allowed)
   {
      appendAllowed = allowed;
   }

   public boolean isAutoKeysOn()
   {
      return autoKeys;
   }

   public void setAutoKeys(boolean on)
   {
      autoKeys = on;
   }

   public int getSkipLines()
   {
      return skipLines;
   }

   public void setSkipLines(int value)
   {
      skipLines = value;
   }

   public boolean isTrimElementOn()
   {
      return trimElement;
   }

   public void setTrimElement(boolean on)
   {
      trimElement = on;
   }

   public String getColumnKey(int colIdx)
   {
      if (keys == null || keys.length < colIdx)
      {
         return null;
      }
      else
      {
         String key = keys[colIdx-1];

         return (key == null || "".equals(key)) ? null : key;
      }
   }

   public TeXObject getColumnHeader(int colIdx)
   {
      if (headers == null || headers.length < colIdx)
      {
         return null;
      }
      else
      {
         return headers[colIdx-1];
      }
   }

   public DataToolSty getSty()
   {
      return sty;
   }

   public void setSty(DataToolSty sty)
   {
      this.sty = sty;
   }

   DataToolSty sty;
   FileFormatType format = FileFormatType.CSV;
   String defaultName = null;
   String defaultExtension = "csv";
   String fileVersion = "3.0";
   boolean incHeader = true;
   int separator = ',';
   int delimiter = '"';
   FileOverwriteOption fileOverwriteOpt = FileOverwriteOption.ERROR;
   AddDelimiterOption addDelimOpt = AddDelimiterOption.DETECT;
   EscapeCharsOption escCharsOpt = EscapeCharsOption.DOUBLE_DELIM;
   CsvBlankOption csvBlankOpt = CsvBlankOption.IGNORE;
   boolean csvLiteral = true;
   IOExpandOption expandOpt = IOExpandOption.NONE;
   boolean appendAllowed = false;
   boolean autoKeys = false;
   int skipLines = 0;
   boolean trimElement = true;

   String[] keys = null;
   TeXObject[] headers = null;

   /*
    * This setting isn't supported with datatool.sty but is with
    * datatooltk.
    */
   boolean strictQuotes = false;
}
