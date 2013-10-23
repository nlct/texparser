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

public abstract class L2HConverter extends LaTeXParserListener
   implements Writeable
{
   public L2HConverter(TeXApp app)
   {
      super(null);
      this.texApp = app;

      setWriteable(this);
   }

   public void setWriter(Writer writer)
   {
      this.writer = writer;
   }

   public Writer getWriter()
   {
      return writer;
   }

   public void setTeXParser(TeXParser parser)
   {
      this.parser = parser;
   }

   public TeXParser getParser()
   {
      return parser;
   }

   public String getStyle()
   {
      String style = "";

      if (parser != null)
      {
         TeXSettings settings = parser.getSettings();

         switch (settings.getCurrentFontFamily())
         {
            case TeXSettings.FAMILY_RM:
               style = "font-family: serif; ";
               break;
            case TeXSettings.FAMILY_SF:
               style = "font-family: sans-serif; ";
               break;
            case TeXSettings.FAMILY_TT:
               style = "font-family: monospace; ";
               break;
         }

         switch (settings.getCurrentFontShape())
         {
            case TeXSettings.SHAPE_UP:
               style += "font-style: normal; font-variant: normal; ";
               break;
            case TeXSettings.SHAPE_IT:
               style += "font-style: italic; font-variant: normal; ";
               break;
            case TeXSettings.SHAPE_SL:
               style += "font-style: oblique; font-variant: normal; ";
               break;
            case TeXSettings.SHAPE_EM:
               TeXSettings parent = settings.getParent();

               if (parent != null)
               {
                  int parentStyle = parent.getFontShape();

                  if (parentStyle == TeXSettings.SHAPE_UP
                    ||parentStyle == TeXSettings.INHERIT)
                  {
                     if (settings.getFontFamily() == TeXSettings.FAMILY_SF)
                     {
                        style += "font-style: oblique; ";
                     }
                     else
                     {
                        style += "font-style: italic; ";
                     }
                  }
                  else
                  {
                     style += "font-style: normal; ";
                  }
               }
               else
               {
                  if (settings.getFontFamily() == TeXSettings.FAMILY_SF)
                  {
                     style += "font-style: oblique; ";
                  }
                  else
                  {
                     style += "font-style: italic; ";
                  }
               }

               style += "font-variant: normal; ";

               break;
            case TeXSettings.SHAPE_SC:
               style += "font-style: normal; font-variant: small-caps; ";
               break;
         }

         switch (settings.getCurrentFontWeight())
         {
            case TeXSettings.WEIGHT_MD:
               style += "font-weight: normal; ";
               break;
            case TeXSettings.WEIGHT_BF:
               style += "font-weight: bold; ";
               break;
         }
      }

      return style;
   }

   public void writeCodePoint(int codePoint)
     throws IOException
   {
      if (writer == null) return;

      String style = getStyle();

      if (!style.isEmpty())
      {
         writer.write("<span style=\""+style+"\">");
      }

      if (codePoint >= 32 && codePoint <= 126)
      {
         writer.write((char)codePoint);
      }
      else
      {
         writer.write("&#x"+Integer.toHexString(codePoint)+";");
      }

      if (!style.isEmpty())
      {
         writer.write("</span>");
      }
   }

   public void write(String str)
     throws IOException
   {
      if (writer == null) return;

      String style = getStyle();

      if (!style.isEmpty())
      {
         writer.write("<span style=\""+style+"\">");
      }

      writer.write(str);

      if (!style.isEmpty())
      {
         writer.write("</span>");
      }
   }

   public void write(char c)
     throws IOException
   {
      if (writer == null) return;

      write(""+c);
   }

   public void writeln(String str)
     throws IOException
   {
      if (writer == null) return;

      write(str+"\n");
   }

   public void href(TeXParser parser, String url, TeXObject text)
     throws IOException
   {
      if (writer == null) return;

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

   // TODO sort out MathML stuff
   // This is just temporary HTML approximation
   // Maybe better just to use MathJax

   public void overwithdelims(TeXParser parser, TeXObject firstDelim,
     TeXObject secondDelim, TeXObject before, TeXObject after)
    throws IOException
   {
      if (firstDelim != null)
      {
        firstDelim.process(parser);
      }

      write("<table style=\"display: inline;\"><tr style=\"border-bottom-style: solid;\"><td>");
      before.process(parser);
      write("</td></tr>");
      write("<tr><td>");
      after.process(parser);
      write("</td></tr><table>");

      if (secondDelim != null)
      {
         secondDelim.process(parser);
      }
   }

   public void abovewithdelims(TeXParser parser, TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness,
     TeXObject before, TeXObject after)
    throws IOException
   {
      if (firstDelim != null)
      {
         firstDelim.process(parser);
      }

      write("<table><tr><td>");
      before.process(parser);
      write("</td></tr>");
      write("<tr><td>");
      after.process(parser);
      write("</td></tr><table>");

      if (secondDelim != null)
      {
         secondDelim.process(parser);
      }
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

   private Writer writer;

   private TeXApp texApp;

   protected TeXParser parser;
}
