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
package com.dickimawbooks.texparserlib.html;

import java.io.*;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HStringConverter extends LaTeXParserListener
  implements Writeable
{
   protected L2HStringConverter(TeXApp app)
   {
      super(null);
      this.texApp = app;

      writer = new StringWriter();
      setWriteable(this);
   }

   public static String convert(String str, boolean atIsLetter)
    throws IOException
   {
      return convert(new TeXAppAdapter(), str, atIsLetter);
   }

   public static String convert(TeXApp app, String str, boolean atIsLetter)
    throws IOException
   {
      L2HStringConverter listener = new L2HStringConverter(app);
      TeXParser parser = new TeXParser(listener);

      if (atIsLetter)
      {
         parser.setCatCode('@', TeXParser.TYPE_LETTER);
      }

      StringReader reader = new StringReader(str);
      parser.parse(reader);

      return listener.writer.toString();
   }

   public void writeCodePoint(int codePoint)
     throws IOException
   {
      writer.write(codePoint);
   }

   public void write(String str)
     throws IOException
   {
      writer.write(str);
   }

   public void write(char c)
     throws IOException
   {
      writer.write(""+c);
   }

   public void writeln(String str)
     throws IOException
   {
      writer.write(str+"<br>");
   }

   public void href(TeXParser parser, String url, TeXObject text)
     throws IOException
   {
      writer.write("<a href=\""+url+"\">");

      text.process(parser);

      writer.write("</a>");
   }

   public void substituting(TeXParser parser, String original, String replacement)
   {
      texApp.substituting(parser.getLineNumber(), original, replacement);
   }

   public void skipping(TeXParser parser, Ignoreable ignoreable)
     throws IOException
   {
   }

   public void environment(TeXParser parser, Environment env)
    throws IOException
   {
      env.process(parser);
   }

   // TODO sort out MathML stuff
   // This is just temporary HTML approximation

   public void overwithdelims(TeXParser parser, TeXObject firstDelim,
     TeXObject secondDelim, TeXObjectList before, TeXObjectList after)
    throws IOException
   {
      firstDelim.process(parser);
      write("<table><tr><td>");
      before.process(parser);
      write("</td></tr>");
      write("<tr><td>");
      after.process(parser);
      write("</td></tr><table>");
      secondDelim.process(parser);
   }

   public void abovewithdelims(TeXParser parser, TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness,
     TeXObjectList before, TeXObjectList after)
    throws IOException
   {
      firstDelim.process(parser);
      write("<table><tr><td>");
      before.process(parser);
      write("</td></tr>");
      write("<tr><td>");
      after.process(parser);
      write("</td></tr><table>");
      secondDelim.process(parser);
   }

   public void subscript(TeXParser parser, TeXObject arg)
    throws IOException
   {
      write("<sb>");
      arg.process(parser);
      write("</sb>");
   }

   public void superscript(TeXParser parser, TeXObject arg)
    throws IOException
   {
      write("<sp>");
      arg.process(parser);
      write("</sp>");
   }

   public void tab(TeXParser parser)
     throws IOException
   {
      // TODO
   }

   public void par() throws IOException
   {
      write("<p>");
   }

   public void verb(TeXParser parser, boolean isStar, char delim,
     String text)
    throws IOException
   {
      write("<tt>"+text+"</tt>");
   }

   public TeXApp getTeXApp()
   {
      return texApp;
   }

   public void endParse(TeXParser parser, File file)
    throws IOException
   {
   }

   public void beginParse(TeXParser parser, File file)
    throws IOException
   {
   }

   private StringWriter writer;

   private TeXApp texApp;
}
