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
import java.io.BufferedReader;

import java.nio.file.Files;

import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnsupportedCharsetException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.inputenc.InputEncSty;

public class DTLread extends ControlSequence
{
   public DTLread(DataToolSty sty)
   {
      this("DTLread", sty);
   }

   public DTLread(String name, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DTLread(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      KeyValList options = TeXParserUtils.popOptKeyValList(parser, stack);

      String filename = popLabelString(parser, stack);

      TeXParserListener listener = parser.getListener();

      parser.startGroup();

      if (options != null)
      {
         sty.processIOKeys(options, stack);
      }

      String format = parser.expandToString(
       listener.getControlSequence("l__datatool_format_str"), stack);

      String ext = parser.expandToString(
       listener.getControlSequence("l__datatool_default_ext_str"), stack);

      TeXPath texPath = new TeXPath(parser, filename, ext, false);

      if (format.startsWith("dtltex") || format.startsWith("dbtex"))
      {
         stack.push(listener.getControlSequence("endgroup"));

         if (texPath.exists())
         {
            listener.addFileReference(texPath);

            String charsetName = null;

            BufferedReader in = null;

            try
            {
               in = Files.newBufferedReader(texPath.getPath(), listener.getCharSet());
               String line = in.readLine();

               if (line != null)
               {
                  Matcher m = FILE_IDENTIFIER.matcher(line);

                  if (m.matches())
                  {
                     String fileType = m.group(1);
                     String version = m.group(2);
                     charsetName = InputEncSty.getCharSetName(m.group(3));
                     texPath.setEncoding(Charset.forName(charsetName));

                     listener.getTeXApp().message(
                      listener.getTeXApp().getMessage(FILE_INFO, 
                        fileType, version, charsetName));
                  }
               }
            }
            catch (MalformedInputException e)
            {
               parser.logMessage(e);
               listener.getTeXApp().warning(parser, 
                 listener.getTeXApp().getMessage(ERROR_FILE_INFO_FAILED,
                   texPath, listener.getTeXApp().getDefaultCharset()));
            }
            catch (UnsupportedCharsetException e)
            {
               listener.getTeXApp().warning(parser, 
                 listener.getTeXApp().getMessage(
                   InputEncSty.ERROR_UNKNOWN_ENCODING, charsetName));
               parser.logMessage(e);
            }

            if (in != null)
            {
               in.close();
            }

            listener.input(texPath, stack);
         }
      }
      else
      {
         int separator = sty.getSeparator();
         int delimiter = sty.getDelimiter();

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

// TODO

         parser.endGroup();
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataToolSty sty;

   public static Pattern FILE_IDENTIFIER = Pattern.compile("% (DBTEX|DTLTEX) ([0-9\\.]+) ([a-zA-Z0-9\\-]+)");

   public static String FILE_INFO = "datatool.file_info";
   public static String ERROR_FILE_INFO_FAILED = "datatool.file_info_failed";
}
