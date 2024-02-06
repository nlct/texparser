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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

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

         if (val.equals("none"))
         {
            escCharsOpt = EscapeCharsOption.NONE;
         }
         else if (val.equals("double-delim"))
         {
            escCharsOpt = EscapeCharsOption.DOUBLE_DELIM;
         }
         else if (val.equals("delim"))
         {
            escCharsOpt = EscapeCharsOption.ESC_DELIM;
         }
         else if (val.equals("delim+bksl"))
         {
            escCharsOpt = EscapeCharsOption.ESC_DELIM_BKSL;
         }
         else
         {
            throw new LaTeXSyntaxException(parser,
             LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
               "csv-escape-chars="+val, "datatool/io");
         }

      }

      if (isRead)
      {
         if (format == FileFormatType.CSV || format == FileFormatType.TSV)
         {
            autoKeys = TeXParserUtils.isTrue("ifdtlautokeys", parser);

            skipLines = TeXParserUtils.toInt(
              parser.getListener().getControlSequence(DataToolSty.OMIT_LINES),
              parser, stack);

            val = TeXParserUtils.getControlSequenceValue(DataToolSty.CSV_BLANK,
             "ignore", parser, stack);

            if (val.equals("ignore"))
            {
               csvBlankOpt = CsvBlankOption.IGNORE;
            }
            else if (val.equals("empty-row"))
            {
               csvBlankOpt = CsvBlankOption.EMPTY_ROW;
            }
            else if (val.equals("end"))
            {
               csvBlankOpt = CsvBlankOption.END;
            }
            else
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

         if (val.equals("allow"))
         {
            fileOverwriteOpt = FileOverwriteOption.WARN;
         }
         else if (val.equals("warn"))
         {
            fileOverwriteOpt = FileOverwriteOption.WARN;
         }
         else if (val.equals("error"))
         {
            fileOverwriteOpt = FileOverwriteOption.ERROR;
         }
         else
         {
            throw new LaTeXSyntaxException(parser,
             LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
               "overwrite="+val, "datatool/io");
         }

         if (format != FileFormatType.DBTEX)
         {
            val = TeXParserUtils.getControlSequenceValue(DataToolSty.IO_EXPAND,
             "none", parser, stack);

            if (val.equals("none"))
            {
               expandOpt = IOExpandOption.NONE;
            }
            else if (val.equals("protected"))
            {
               expandOpt = IOExpandOption.PROTECTED;
            }
            else if (val.equals("full"))
            {
               expandOpt = IOExpandOption.FULL;
            }
            else
            {
               throw new LaTeXSyntaxException(parser,
                LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
                  "expand="+val, "datatool/io");
            }
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

   public void setFileVersion(String version)
   throws TeXSyntaxException
   {
      if (
           (format == FileFormatType.DBTEX || format == FileFormatType.DTLTEX)
          && !(version.equals("2.0") || version.equals("3.0"))
         )
      {
         throw new LaTeXSyntaxException(sty.getParser(),
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

   public DataToolSty getSty()
   {
      return sty;
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
}
