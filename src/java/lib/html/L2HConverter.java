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
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.aux.*;

public class L2HConverter extends LaTeXParserListener
   implements Writeable
{
   public L2HConverter(TeXApp app)
   {
      this(app, true, null, null, false);
   }

   public L2HConverter(TeXApp app, Vector<AuxData> auxData)
   {
      this(app, null, auxData);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, boolean parseAux)
   {
      this(app, useMathJax, null, parseAux);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, Vector<AuxData> auxData)
   {
      this(app, useMathJax, null, auxData);
   }

   public L2HConverter(TeXApp app, File outDir)
   {
      this(app, true, outDir);
   }

   public L2HConverter(TeXApp app, File outDir, Vector<AuxData> auxData)
   {
      this(app, true, outDir, auxData);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir)
   {
      this(app, useMathJax, outDir, null);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     Vector<AuxData> auxData)
   {
      this(app, useMathJax, outDir, auxData, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     boolean parseAux)
   {
      this(app, useMathJax, outDir, null, parseAux);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     Vector<AuxData> auxData, boolean parseAux)
   {
      super(null, auxData, parseAux);
      this.texApp = app;
      this.outPath = (outDir == null ? null : outDir.toPath());

      setWriteable(this);
      setUseMathJax(useMathJax);
   }

   protected void addPredefined()
   {
      super.addPredefined();

      putControlSequence(new L2HHypertarget());
      putControlSequence(new L2HHyperlink());

      putControlSequence(new L2HAmp());
      putControlSequence(new L2HNoBreakSpace());
      putControlSequence(new SpaceCs("newblock"));
      putControlSequence(new L2HTheBibliography());
      putControlSequence(new L2HTableOfContents());
      putControlSequence(new L2HContentsLine());
      putControlSequence(new L2HBibItem());
      putControlSequence(new L2HMaketitle());
      putControlSequence(new L2HMbox());

      putControlSequence(new L2HTextSuperscript());

      putControlSequence(new L2HSection());
      putControlSequence(new L2HSection("subsection"));
      putControlSequence(new L2HSection("subsubsection"));
      putControlSequence(new L2HSection("paragraph"));
      putControlSequence(new L2HSection("subparagraph"));
      putControlSequence(new L2HSection("part"));

      putControlSequence(new L2HCaption());
      putControlSequence(new L2HAtMakeCaption());

      putControlSequence(new L2HFloat("figure"));
      putControlSequence(new L2HFloat("table"));

      putControlSequence(new L2HAbstract());

      putControlSequence(new L2HMathDeclaration("math"));

      MathDeclaration begMathDecl = new L2HMathDeclaration("(");
      parser.putControlSequence(begMathDecl);
      parser.putControlSequence(new EndDeclaration(")", begMathDecl));
      parser.putControlSequence(
         new L2HMathDeclaration("displaymath", TeXSettings.MODE_DISPLAY_MATH));

      MathDeclaration begDispDecl = new L2HMathDeclaration("[", TeXSettings.MODE_DISPLAY_MATH);

      parser.putControlSequence(begDispDecl);
      parser.putControlSequence(new EndDeclaration("]", begDispDecl));
      parser.putControlSequence(
         new L2HMathDeclaration("equation", TeXSettings.MODE_DISPLAY_MATH, true));

      parser.putControlSequence(new L2HTabular());
      parser.putControlSequence(new L2HTabular("array"));

      parser.putControlSequence(new L2HEqnarray());
      parser.putControlSequence(new L2HEqnarray("eqnarray*", false));

      putControlSequence(new L2Hhfill("hfill"));
      putControlSequence(new L2Hhfill("hfil"));

      try
      {
         LaTeXSty sty = getLaTeXSty("hyperref");

         if (sty != null)
         {
            sty.load(null);
         }
      }
      catch (IOException e)
      {
      }
   }

   public BigOperator createBigOperator(String name, int code1, int code2)
   {
      return new L2HBigOperator(name, code1, code2);
   }

   public BigOperator createBigOperator(String name, int code)
   {
      return new L2HBigOperator(name, code);
   }

   public Letter getLetter(int charCode)
   {
      return new L2HLetter(charCode);
   }

   public Other getOther(int charCode)
   {
      return new L2HOther(charCode);
   }

   public Par getPar()
   {
      return new L2HPar();
   }

   public MathGroup createMathGroup()
   {
      return new L2HMathGroup();
   }

   public AlignRow createAlignRow(TeXObjectList stack)
     throws IOException
   {
      return new L2HAlignRow(getParser(), stack);
   }

   public AlignRow createMathAlignRow(TeXObjectList stack, boolean isNumbered)
     throws IOException
   {
      return new L2HMathAlignRow(getParser(), stack, isNumbered);
   }

   public void cr(boolean isStar, TeXObject optArg)
     throws IOException
   {
      TeXSettings settings = getParser().getSettings();

      if (settings.getAlignMode() == TeXSettings.ALIGN_MODE_TRUE)
      {
         settings.startRow();
      }
      else
      {
         writeln("<br>\n");
      }
   }


   public void setWriter(Writer writer)
   {
      this.writer = writer;
   }

   public Writer getWriter()
   {
      return writer;
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

   public FontWeightDeclaration getFontWeightDeclaration(String name, int weight)
   {
      return new L2HFontWeightDeclaration(name, weight);
   }

   public FontSizeDeclaration getFontSizeDeclaration(String name, int size)
   {
      return new L2HFontSizeDeclaration(name, size);
   }

   public FontShapeDeclaration getFontShapeDeclaration(String name, int shape)
   {
      return new L2HFontShapeDeclaration(name, shape);
   }

   public FontFamilyDeclaration getFontFamilyDeclaration(String name, int family)
   {
      return new L2HFontFamilyDeclaration(name, family);
   }

   public void writeCodePoint(int codePoint)
     throws IOException
   {
      if (writer == null) return;

/*
      String style = getStyle();

      if (!style.isEmpty())
      {
         writer.write("<span style=\""+style+"\">");
      }
*/

      if (codePoint >= 32 && codePoint <= 126)
      {
         writer.write((char)codePoint);
      }
      else
      {
         writer.write("&#x"+Integer.toHexString(codePoint)+";");
      }

/*
      if (!style.isEmpty())
      {
         writer.write("</span>");
      }
*/
   }

   public void write(String str)
     throws IOException
   {
      if (writer == null) return;

/*
      String style = getStyle();

      if (!style.isEmpty())
      {
         writer.write("<span style=\""+style+"\">");
      }
*/

      writer.write(str);

/*
      if (!style.isEmpty())
      {
         writer.write("</span>");
      }
*/
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

      write(String.format("%s%n", str));
      writer.flush();
   }

   public void href(String url, TeXObject text)
     throws IOException
   {
      if (writer == null) return;

      writer.write("<a href=\""+url+"\">");

      text.process(parser);

      writer.write("</a>");
   }

   public void substituting(String original, String replacement)
   {
      texApp.substituting(parser, original, replacement);
   }

   public void skipping(Ignoreable ignoreable)
     throws IOException
   {
   }

   public boolean useMathJax()
   {
      return useMathJax;
   }

   public void setUseMathJax(boolean useMathJax)
   {
      this.useMathJax = useMathJax;
   }

   public void writeMathJaxHeader()
     throws IOException
   {
      setUseMathJax(true);

      writeable.writeln("<!-- MathJax -->");
      writeable.writeln("<script type=\"text/x-mathjax-config\">");
      writeable.writeln("MathJax.Hub.Config({tex2jax: { inlineMath: [['$','$'],['\\\\(','\\\\)']], displayMath: [ ['$$','$$'], ['\\\\[','\\\\]'] ]}});");
      writeable.writeln("</script>");

      writeable.write("<script type=\"text/javascript\" src=");
      writeable.writeln(
       "\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\">");
      writeable.writeln("</script>");
   }

   protected void writeTabularCss(String halign, String valign)
     throws IOException
   {
      String suffix = "";

      if (halign != null)
      {
         suffix = suffix + halign.charAt(0);
      }

      if (valign != null)
      {
         suffix = suffix + valign.charAt(0);
      }

      writeln("table.tabular-"+suffix);
      writeln("{");
      writeln("  display: inline-table;");
      writeln("  border-collapse: collapse;");

      if (halign != null)
      {
         writeln("  align: "+halign+";");
      }

      if (valign != null)
      {
         writeln("  vertical-align: "+valign+";");
      }

      writeln("}");
   }

   public void writeCssStyles()
     throws IOException
   {
      writeln("div.displaymath { display: block; text-align: center; }");
      writeln("span.eqno { float: right; }");
      writeln("div.table { display: block; text-align: center; }");

      writeTabularCss("center", "middle");
      writeTabularCss("center", "bottom");
      writeTabularCss("center", "top");

      writeTabularCss("left", "middle");
      writeTabularCss("left", "bottom");
      writeTabularCss("left", "top");

      writeTabularCss("right", "middle");
      writeTabularCss("right", "bottom");
      writeTabularCss("right", "top");

      writeTabularCss(null, "middle");
      writeTabularCss(null, "bottom");
      writeTabularCss(null, "top");

      writeTabularCss("left", null);
      writeTabularCss("center", null);
      writeTabularCss("right", null);

      writeln("div.figure { display: block; text-align: center; }");
      writeln("div.caption { display: block; text-align: center; }");
      writeln("div.marginpar { float: right; }");
      writeln("div.abstract { display: block; margin-right: 4em; margin-left: 4em;}");
      writeln("div.title { display: block; text-align: center; font-size: x-large;}");
      writeln("div.author { display: block; text-align: center; font-size: large;}");
      writeln("div.date { display: block; text-align: center; font-size: medium;}");
      writeln("div.bibliography { display: block; margin-left: 4em; }");
      writeln("div.bibitem { display: inline; float: left; text-indent: -3em; }");
      writeln("div.mbox { display: inline; }");

      for (String style : extraCssStyles)
      {
         writeln(style);
      }
   }

   public void addCssStyle(String style)
   {
      extraCssStyles.add(style);
   }

   public void documentclass(KeyValList options, String clsName)
     throws IOException
   {
      super.documentclass(options, clsName);

      writeable.writeln("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
      writeable.writeln("<html>");
      writeable.writeln("<head>");


      writeable.writeln("<style type=\"text/css\">");
      writeCssStyles();
      writeable.writeln("</style>");

      if (useMathJax())
      {
         writeMathJaxHeader();
      }
   }

   public void beginDocument()
     throws IOException
   {
      TeXObject cs = getParser().getControlSequence("@title");

      if (!(cs instanceof Undefined) && cs != null)
      {
         if (cs instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)cs).expandfully(getParser());

            if (expanded != null)
            {
               cs = expanded;
            }
         }

         writeable.write("<title>");
         writeable.write(cs.toString(getParser()));
         writeable.writeln("</title>");
      }

      writeable.writeln("</head>");
      writeable.writeln("<body>");

      super.beginDocument();

      getParser().getSettings().setCharMapMode(TeXSettings.CHAR_MAP_ON);
   }

   public void endDocument()
     throws IOException
   {
      processFootnotes();

      writeable.writeln("</body>");
      writeable.writeln("</html>");
      super.endDocument();
   }

   public void overwithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXObject before, TeXObject after)
    throws IOException
   {
      if (useMathJax())
      {
         if (firstDelim != null || secondDelim != null)
         {
            write("\\left");
            write(firstDelim==null?".":firstDelim.toString(getParser()));
         }

         write("\\frac{");
         before.process(getParser());
         write("}{");
         after.process(getParser());
         write("}");

         if (firstDelim != null || secondDelim != null)
         {
            write("\\right");
            write(secondDelim==null?".":secondDelim.toString(getParser()));
         }

         return;
      }

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

   public void abovewithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness,
     TeXObject before, TeXObject after)
    throws IOException
   {
      if (useMathJax())
      {
         write(before.toString(getParser()));
         write("\\abovewithdelims ");
         write(firstDelim==null?".":firstDelim.toString(getParser()));
         write(secondDelim==null?".":secondDelim.toString(getParser()));
         write(thickness.toString(getParser()));

         write(after.toString(getParser()));

         return;
      }

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

   public void subscript(TeXObject arg)
    throws IOException
   {
      if (useMathJax())
      {
         write("_{"+arg.toString(getParser())+"}");
      }
      else
      {
         write("<sb>");
         arg.process(parser);
         write("</sb>");
      }
   }

   public void superscript(TeXObject arg)
    throws IOException
   {
      if (useMathJax())
      {
         write("^{"+arg.toString(getParser())+"}");
      }
      else
      {
         write("<sp>");
         arg.process(parser);
         write("</sp>");
      }
   }

   public void verb(boolean isStar, char delim,
     String text)
    throws IOException
   {
      write("<tt>"+text+"</tt>");
   }

   public void includegraphics(KeyValList options, String file)
     throws IOException
   {
      // This doesn't take the options or file format into account.
      write(String.format("<img src=\"%s\"/>", file));
   }

   public TeXApp getTeXApp()
   {
      return texApp;
   }

   public void endParse(File file)
    throws IOException
   {
   }

   public void beginParse(File file)
    throws IOException
   {
      getTeXApp().message(TeXApp.MESSAGE_READING, file.getAbsolutePath());

      basePath = file.getParentFile().toPath();

      if (writer == null)
      {
         Files.createDirectories(outPath);

         String baseName = file.getName();

         int idx = baseName.lastIndexOf(".");

         if (idx > -1)
         {
            baseName = baseName.substring(0,idx);
         }

         File outFile = new File(outPath.toFile(), baseName+"."+getSuffix());

         getTeXApp().message(TeXApp.MESSAGE_WRITING, outFile.getAbsolutePath());
         writer = new PrintWriter(outFile);
      }
   }

   public void setSuffix(String suffix)
   {
      this.suffix = suffix;
   }

   public String getSuffix()
   {
      return suffix;
   }

   public ControlSequence createUndefinedCs(String name)
   {
      return new L2HUndefined(name);
   }

   public void doFootnoteRule() throws IOException
   {
      writeln("<p><hr><p>");
   }

   private Writer writer;

   private TeXApp texApp;

   private Path outPath, basePath;

   private boolean useMathJax=true;

   private String suffix = "html";

   private Vector<String> extraCssStyles = new Vector<String>();
}
