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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.Charset;

import com.dickimawbooks.texparserlib.*;

public class L2HVerbatimInput extends ControlSequence
{
   public L2HVerbatimInput()
   {
      this("verbatiminput", null);
   }

   public L2HVerbatimInput(String name, String cssClassName)
   {
      super(name);
      this.cssClassName = cssClassName;
   }

   @Override
   public Object clone()
   {
      return new L2HVerbatimInput(getName(), cssClassName);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      boolean visSpaces = (popModifier(parser, stack, '*') != -1);

      // allow for \lstinputlisting to be treated as \verbatiminput:

      popOptArg(parser, stack);// ignore optional argument

      TeXPath texPath = TeXParserUtils.popTeXPath(parser, stack);

      listener.writeliteral("<pre");

      if (cssClassName != null)
      {
         listener.writeliteral(" class=\"");
         listener.writeliteral(cssClassName);
         listener.writeliteral("\"");
      }

      listener.writeliteral(">");

      listener.writeliteral("<!-- ");
      listener.write(getName());
      listener.write(": ");
      listener.write(texPath.toString());
      listener.writeliteralln(" -->");

      Path path = texPath.getPath();
      Charset charset = texPath.getEncoding();

      if (charset == null)
      {
         charset = listener.getCharSet();
      }

      listener.setCurrentBlockType(DocumentBlockType.BLOCK);

      BufferedReader reader = null;

      try
      {
         reader = Files.newBufferedReader(path, charset);

         String line;

         while ((line = reader.readLine()) != null)
         {
            if (visSpaces)
            {
               line = line.replaceAll(" ", "\u2423");
            }

            listener.writeln(line);
         }
      }
      finally
      {
         if (reader != null)
         {
            reader.close();
         }

         listener.setCurrentBlockType(DocumentBlockType.BODY);
         listener.writeliteralln("</pre>");
      }
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   protected String cssClassName;
}
