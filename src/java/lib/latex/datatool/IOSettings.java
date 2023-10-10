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
      defaultName = getControlSequenceValue("l__datatool_io_name_str",
        null, parser, stack);

      defaultExtension = getControlSequenceValue("l__datatool_default_ext_str",
        null, parser, stack);

      incHeader = !TeXParserUtils.isTrue("ifdtlnoheader", parser);

      String val = getControlSequenceValue("l__datatool_format_str",
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

         val = getControlSequenceValue("__texparser_io_csv_escape_chars_tl",
          "none", parser, stack);

         if (val.equals("none"))
         {
            escCharsOpt = EscapeCharsOption.NONE;
         }
         else if (val.equals("delim"))
         {
            escCharsOpt = EscapeCharsOption.DELIM;
         }
         else if (val.equals("delim+bksl"))
         {
            escCharsOpt = EscapeCharsOption.DELIM_BKSL;
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
              parser.getListener().getControlSequence("dtl@omitlines"),
              parser, stack);

            val = getControlSequenceValue("__texparser_io_csv_blank_tl",
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
              "l__datatool_csv_literal_content_bool", parser);
         }

         appendAllowed = !TeXParserUtils.isFalse(
           "l__datatool_append_allowed_bool", parser);

         trimElement = !TeXParserUtils.isFalse(
           "l__datatool_new_element_trim_bool", parser);
      }
      else
      {
         if (defaultName == null)
         {
            defaultName = getControlSequenceValue("l__datatool_default_dbname_str",
              "untitled", parser, stack);
         }

         val = getControlSequenceValue("__texparser_io_overwrite_tl",
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
            val = getControlSequenceValue("__texparser_io_expand_tl",
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
            val = getControlSequenceValue("__texparser_io_add_delimiter_tl",
             "detect", parser, stack);

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

   protected String getControlSequenceValue(String csname, String defValue,
     TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      ControlSequence cs = parser.getControlSequence(csname);

      if (cs == null) return defValue;

      if (cs instanceof TextualContentCommand)
      {
         return ((TextualContentCommand)cs).getText();
      }

      return parser.expandToString(cs, stack);
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
   EscapeCharsOption escCharsOpt = EscapeCharsOption.NONE;
   CsvBlankOption csvBlankOpt = CsvBlankOption.IGNORE;
   boolean csvLiteral = true;
   IOExpandOption expandOpt = IOExpandOption.NONE;
   boolean appendAllowed = false;
   boolean autoKeys = false;
   int skipLines = 0;
   boolean trimElement = true;
}
