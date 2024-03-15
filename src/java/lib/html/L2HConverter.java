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
package com.dickimawbooks.texparserlib.html;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.Stack;
import java.util.Iterator;
import java.util.HashMap;
import java.awt.Color;
import java.awt.Dimension;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.auxfile.*;

public class L2HConverter extends LaTeXParserListener
   implements Writeable
{
   public L2HConverter(TeXApp app)
   {
      this(app, true, null, (AuxParser)null, false, null, false);
   }

   public L2HConverter(TeXApp app, AuxParser auxParser)
   {
      this(app, true, null, auxParser, false, null, false);
   }

   public L2HConverter(TeXApp app, Vector<AuxData> auxData)
   {
      this(app, true, null, auxData, false, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, boolean parseAux)
   {
      this(app, useMathJax, null, (AuxParser)null, parseAux, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, Vector<AuxData> auxData)
   {
      this(app, useMathJax, null, auxData, false, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, AuxParser auxParser)
   {
      this(app, useMathJax, null, auxParser, false, null, false);
   }

   public L2HConverter(TeXApp app, File outDir)
   {
      this(app, true, outDir, (AuxParser)null, false, null, false);
   }

   public L2HConverter(TeXApp app, File outDir, Vector<AuxData> auxData)
   {
      this(app, true, outDir, auxData, false, null, false);
   }

   public L2HConverter(TeXApp app, File outDir, AuxParser auxParser)
   {
      this(app, true, outDir, auxParser, false, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir)
   {
      this(app, useMathJax, outDir, (AuxParser)null, false, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     Vector<AuxData> auxData)
   {
      this(app, useMathJax, outDir, auxData, false, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     AuxParser auxParser)
   {
      this(app, useMathJax, outDir, auxParser, false, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     boolean parseAux)
   {
      this(app, useMathJax, outDir, (AuxParser)null, parseAux, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     Vector<AuxData> auxData, boolean parseAux)
   {
      this(app, useMathJax, outDir, auxData, parseAux, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     Vector<AuxData> auxData, boolean parseAux, Charset outCharSet)
   {
      this(app, useMathJax, outDir, auxData, parseAux, outCharSet, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     AuxParser auxParser, boolean parseAux, Charset outCharSet)
   {
      this(app, useMathJax, outDir, auxParser, parseAux, outCharSet, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     Charset outCharSet, boolean parsePackages, int splitLevel)
   {
      this(app, useMathJax, outDir, (AuxParser)null, true, outCharSet, parsePackages);

      setSplit(splitLevel);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     Vector<AuxData> auxData, boolean parseAux, Charset outCharSet, boolean parsePackages)
   {
      super(null, auxData, parseAux, parsePackages);
      this.texApp = app;
      this.outPath = (outDir == null ? null : outDir.toPath());
      this.htmlCharSet = outCharSet;

      if (htmlCharSet == null)
      {
         htmlCharSet = texApp.getDefaultCharset();
      }

      this.styCs = new Vector<String>();
      defaultStyles = new HashMap<String,String>();
      internalReferences = new HashMap<String,TeXObject>();

      setWriteable(this);
      setUseMathJax(useMathJax);

      setImageExtensions("svg", "SVG", "png", "PNG", "jpg", "JPG", "jpeg", "JPEG",
        "gif", "GIF", "pdf", "PDF");
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     AuxParser auxParser, boolean parseAux, Charset outCharSet, boolean parsePackages)
   {
      super(null, auxParser, parseAux, parsePackages);
      this.texApp = app;
      this.outPath = (outDir == null ? null : outDir.toPath());
      this.htmlCharSet = outCharSet;

      if (htmlCharSet == null)
      {
         htmlCharSet = texApp.getDefaultCharset();
      }

      this.styCs = new Vector<String>();
      defaultStyles = new HashMap<String,String>();
      internalReferences = new HashMap<String,TeXObject>();

      setWriteable(this);
      setUseMathJax(useMathJax);

      setImageExtensions("svg", "SVG", "png", "PNG", "jpg", "JPG", "jpeg", "JPEG",
        "gif", "GIF", "pdf", "PDF");
   }

   /**
    * Sets the split level. This must be set before the file is parsed. 
    * Has no effect if the division data isn't available. This method
    * will automatically switch on save divisions, but it won't have an effect
    * if the aux file isn't subsequently parsed.
    *
    * The split level isn't the same as secnumdepth. Level 0 is the start of the
    * document, level 1 is the first division unit to be found (which may be
    * part, chapter or section, depending on the document class, or some custom
    * unit).
    */ 
   public void setSplit(int splitLevel)
   {
      this.splitLevel = splitLevel;

      if (splitLevel > 0)
      {
         enableSaveDivisions(true);
      }
   }

   /**
    * Sets the split level and whether or not to also use the basename as a prefix.
    */ 
   public void setSplit(int splitLevel, boolean useBaseNamePrefix)
   {
      setSplit(splitLevel);
      setSplitUseBaseNamePrefix(useBaseNamePrefix);
   }

   public void setSplitUseBaseNamePrefix(boolean usePrefix)
   {
      splitUseBaseNamePrefix = usePrefix;
   }

   @Override
   protected void addPredefined()
   {
      super.addPredefined();

      // Add 
      // \providecommand{\IfTeXParserLib}[2]{#2}
      // to the document to provide a conditional that depends on
      // whether or not the TeX parser library is interpreting the
      // code.
      putControlSequence(new AtFirstOfTwo("IfTeXParserLib"));

      // syntax: \TeXParserLibToImage[options]{code}
      putControlSequence(new L2HToImage());

      putControlSequence(new HCode());

      parser.putControlSequence(new TextualContentCommand("TeX", "TeX"));
      parser.putControlSequence(new TextualContentCommand("LaTeX", "LaTeX"));
      parser.putControlSequence(new TextualContentCommand("LaTeXe", "LaTeX2e"));
      parser.putControlSequence(new TextualContentCommand("eTeX", "eTeX"));
      parser.putControlSequence(new TextualContentCommand("XeTeX", "XeTeX"));
      parser.putControlSequence(new TextualContentCommand("XeLaTeX", "XeLaTeX"));
      parser.putControlSequence(new TextualContentCommand("LuaTeX", "LuaTeX"));
      parser.putControlSequence(new TextualContentCommand("LuaLaTeX", "LuaLaTeX"));
      parser.putControlSequence(new TextualContentCommand("pdfTeX", "pdfTeX"));
      parser.putControlSequence(new TextualContentCommand("pdfLaTeX", "pdfLaTeX"));
      parser.putControlSequence(new TextualContentCommand("BibTeX", "BibTeX"));

      parser.putControlSequence(new GenericCommand("indexspace", null,
        new HtmlTag("<div class=\"indexspace\"></div>")));

      putControlSequence(new Relax("nonumberline"));

      putControlSequence(new L2HAmp());
      putControlSequence(new L2HNoBreakSpace());
      putControlSequence(new SpaceCs("newblock"));
      putControlSequence(new L2HTheBibliography());

      addToBibliographySection(new TeXCsRef("label"));
      addToBibliographySection(createGroup("bib"));

      addInternalReference("bib", new TeXCsRef("refname"));
      addInternalReference("toc", new TeXCsRef("contentsname"));

      putControlSequence(new AtSecondOfTwo("texparser@ifintoc"));
      putControlSequence(new L2HTableOfContents());
      putControlSequence(new L2HAtStartToc());
      putControlSequence(new L2HContentsLine());
      putControlSequence(new L2HBibItem());
      putControlSequence(new L2HMaketitle());

      putControlSequence(new L2HTextSuperscript());
      putControlSequence(new L2HTextSubscript());

      putControlSequence(new L2HSection());
      putControlSequence(new L2HSection("subsection"));
      putControlSequence(new L2HSection("subsubsection"));
      putControlSequence(new L2HSection("paragraph"));
      putControlSequence(new L2HSection("subparagraph"));
      putControlSequence(new L2HSection("part"));
      putControlSequence(new L2HNumberline());

      putControlSequence(new L2HCaption());
      putControlSequence(new L2HAtMakeCaption());

      putControlSequence(new L2HFloat("figure"));
      putControlSequence(new L2HFloat("table"));

      putControlSequence(new L2HAbstract());
      putControlSequence(new L2HMultiCols());
      putControlSequence(new L2HMultiCols("multicols*"));

      putControlSequence(new L2HItem());

      putControlSequence(new L2HDescriptionLabel());
      putControlSequence(new L2HDescriptionItem());
      putControlSequence(new L2HAltListItem());
      putControlSequence(new L2HAltListDesc());
      putControlSequence(new L2HAltListDescEnv());

      putControlSequence(new L2HQuote());

      putControlSequence(new L2HMathDeclaration("math"));

      MathDeclaration begMathDecl = new L2HMathDeclaration("(");
      parser.putControlSequence(begMathDecl);
      parser.putControlSequence(new EndDeclaration(")", begMathDecl));
      parser.putControlSequence(
         new L2HMathDeclaration("displaymath", TeXMode.DISPLAY_MATH));

      MathDeclaration begDispDecl = new L2HMathDeclaration("[", TeXMode.DISPLAY_MATH);

      parser.putControlSequence(begDispDecl);
      parser.putControlSequence(new EndDeclaration("]", begDispDecl));
      parser.putControlSequence(
         new L2HMathDeclaration("equation", TeXMode.DISPLAY_MATH, true));

      parser.putControlSequence(new L2HTabular());
      parser.putControlSequence(new L2HTabular("array"));
      parser.putControlSequence(new L2HLongTable());

      parser.putControlSequence(new L2HEqnarray());
      parser.putControlSequence(new L2HEqnarray("eqnarray*", false));

      parser.putControlSequence(new Relax("strut"));

      putControlSequence(new GenericCommand(true, "bigskip", null, 
       new TeXObject[] {new HtmlTag("<div class=\"bigskip\"></div>")}));
      putControlSequence(new GenericCommand(true, "medskip", null, 
       new TeXObject[] {new HtmlTag("<div class=\"medskip\"></div>")}));
      putControlSequence(new GenericCommand(true, "smallskip", null, 
       new TeXObject[] {new HtmlTag("<div class=\"smallskip\"></div>")}));

      putControlSequence(new L2Hhfill("hfill"));
      putControlSequence(new L2Hhfill("hfil"));

      putControlSequence(new GenericCommand(true, "quad", null, 
       new TeXObject[] {new HtmlTag("<span class=\"quad\"></span>")}));
      putControlSequence(new GenericCommand(true, "qquad", null, 
       new TeXObject[] {new HtmlTag("<span class=\"qquad\"></span>")}));

      // ignore \\addvspace
      putControlSequence(new AtGobble("addvspace"));

      putControlSequence(new L2HNormalFont());

      putControlSequence(new GenericCommand(true, "labelitemii", null,
       new HtmlTag("&#x2013;")));

      /* indent/noindent not implemented */
      putControlSequence(new Relax("indent"));
      putControlSequence(new Relax("noindent"));

      putControlSequence(new Relax("allowbreak"));

      putControlSequence(new GenericCommand(true, "newline", null,
       new HtmlTag("<br>")));

      putControlSequence(new AtGobble("pagenumbering"));
      putControlSequence(new Input("include"));

      putControlSequence(new L2HNewFontFamily());

      try
      {
         LaTeXSty sty = requirepackage("hyperref", getParser());
      }
      catch (IOException e)
      {
      }
   }

   @Override
   protected TeXParserSection createTeXParserSection(String sectionCsname)
   {
      return new L2HTeXParserSection("texparser@"+sectionCsname, sectionCsname);
   }

   public void addInternalReference(String label, TeXObject object)
   {
      internalReferences.put(label, object);
   }

   public TeXObject createUnknownReference(String label)
   {
      TeXObject object = internalReferences.get(label);

      if (object != null)
      {
         return object;
      }

      return super.createUnknownReference(label);
   }

   public TeXObject createUnknownReference(TeXObject label)
   {
      try
      {
         if (label instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)label).expandfully(parser);

            if (expanded != null)
            {
               label = expanded;
            }
         }

         return createUnknownReference(label.toString(parser));
      }
      catch (IOException e)
      {
         getParser().error(e);
      }

      return super.createUnknownReference(label);
   }

   public L2HImage toImage(String preamble, 
    String content, String mimeType, TeXObject alt, String name, boolean crop)
   throws IOException
   {
      return null;
   }

   @Override
   public BigOperator createBigOperator(String name, int code1, int code2)
   {
      return new L2HBigOperator(name, code1, code2);
   }

   @Override
   public BigOperator createBigOperator(String name, int code)
   {
      return new L2HBigOperator(name, code);
   }

   @Override
   public MathSymbol createMathSymbol(String name, int code)
   {
      return new L2HMathSymbol(name, code);
   }

   @Override
   public BinarySymbol createBinarySymbol(String name, int code)
   {
      return new L2HBinarySymbol(name, code);
   }

   @Override
   public GreekSymbol createGreekSymbol(String name, int code)
   {
      return new L2HGreekSymbol(name, code);
   }

   @Override
   public Letter getLetter(int charCode)
   {
      return new L2HLetter(charCode);
   }

   @Override
   public Other getOther(int charCode)
   {
      return new L2HOther(charCode);
   }

   @Override
   public Par getPar()
   {
      return new L2HPar();
   }

   @Override
   public Paragraph createParagraph()
   {
      return new L2HParagraph();
   }

   @Override
   public Spacer getSpacer(Direction direction, TeXDimension size, boolean inline)
   {
      return new L2HSpacer(direction, size, inline);
   }

   @Override
   public TeXObject getDivider(String name)
   {
      return new HtmlTag(String.format("<div class=\"%s\"><hr></div>", name));
   }

   @Override
   public Group createGroup()
   {
      return new L2HGroup();
   }

   @Override
   public Group createGroup(String text)
   {
      return new L2HGroup(this, text); 
   }

   @Override
   public MathGroup createMathGroup()
   {
      return new L2HMathGroup();
   }

   @Override
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

   @Override
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
         writeliteral(String.format("<br>%n"));
      }
   }

   public void setWriter(Writer writer)
   {
      this.writer = writer;
      currentWriter = writer;
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
            case RM:
               style = "font-family: serif; ";
               break;
            case SF:
               style = "font-family: sans-serif; ";
               break;
            case TT:
            case VERB:
               style = "font-family: monospace; ";
               break;
            case CAL:
               style = "font-family: cursive; ";
               break;
         }

         switch (settings.getCurrentFontShape())
         {
            case UP:
               style += "font-style: normal; font-variant: normal; ";
               break;
            case IT:
               style += "font-style: italic; font-variant: normal; ";
               break;
            case SL:
               style += "font-style: oblique; font-variant: normal; ";
               break;
            case EM:
               TeXSettings parent = settings.getParent();

               if (parent != null)
               {
                  TeXFontShape parentStyle = parent.getFontShape();

                  if (parentStyle == TeXFontShape.UP
                    ||parentStyle == TeXFontShape.INHERIT)
                  {
                     if (settings.getFontFamily() == TeXFontFamily.SF)
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
                  if (settings.getFontFamily() == TeXFontFamily.SF)
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
            case SC:
               style += "font-style: normal; font-variant: small-caps; ";
               break;
         }

         switch (settings.getCurrentFontWeight())
         {
            case MD:
               style += "font-weight: normal; ";
               break;
            case BF:
               style += "font-weight: bold; ";
               break;
         }
      }

      return style;
   }

   @Override
   public FontWeightDeclaration getFontWeightDeclaration(String name, TeXFontWeight weight)
   {
      return new L2HFontWeightDeclaration(name, weight);
   }

   @Override
   public FontSizeDeclaration getFontSizeDeclaration(String name, TeXFontSize size)
   {
      return new L2HFontSizeDeclaration(name, size);
   }

   @Override
   public FontShapeDeclaration getFontShapeDeclaration(String name, TeXFontShape shape)
   {
      return new L2HFontShapeDeclaration(name, shape);
   }

   @Override
   public FontFamilyDeclaration getFontFamilyDeclaration(String name, TeXFontFamily family)
   {
      return new L2HFontFamilyDeclaration(name, family);
   }

   @Override
   public void writeliteralln(String string) throws IOException
   {
      writeliteral(String.format("%s%n", string));
   }

   @Override
   public void writeliteral(String string) throws IOException
   {
      if (currentWriter == null)
      {
         currentWriter = writer;
      }

      if (currentWriter == null)
      {
         parser.debugMessage(TeXParser.DEBUG_IO, 
           "No writer available. writeliteral: "+string);
      }
      else
      {
         currentWriter.write(string);
      }
   }

   public void setUseEntities(boolean useEntities)
   {
      this.useHtmlEntities = useEntities;
   }

   @Override
   public void writeCodePoint(int codePoint)
     throws IOException
   {
      if (currentWriter == null)
      {
         currentWriter = writer;

         if (currentWriter == null)
         {
            if (!Character.isWhitespace(codePoint))
            {
               parser.debugMessage(TeXParser.DEBUG_IO, 
                 "No writer available. writeCodePoint: "+codePoint);
            }

            return;
         }
      }

      if (inPreamble && !Character.isWhitespace(codePoint))
      {
         throw new LaTeXSyntaxException(getParser(),
           LaTeXSyntaxException.ERROR_MISSING_BEGIN_DOC, 
             new String(Character.toChars(codePoint)));
      }

      if (codePoint == '<')
      {
         currentWriter.write("&lt;");
      }
      else if (codePoint == '>')
      {
         currentWriter.write("&gt;");
      }
      else if (codePoint == '&')
      {
         currentWriter.write("&amp;");
      }
      else if (codePoint >= 128 && useHtmlEntities)
      {
         currentWriter.write(String.format("&#x%x;", codePoint));
      }
      else if (codePoint <= 0xFFFF)
      {
         currentWriter.write((char)codePoint);
      }
      else
      {
         char[] chars = Character.toChars(codePoint);

         for (char c : chars)
         {
            currentWriter.write(c);
         }
      }

   }

   @Override
   public void write(String str)
     throws IOException
   {
      if (currentWriter == null)
      {
         currentWriter = writer;

         if (currentWriter == null)
         {
            if (!str.trim().isEmpty())
            {
               parser.debugMessage(TeXParser.DEBUG_IO, 
                  "No writer available. write: "+str);
            }

            return;
         }
      }

      if (inPreamble && !str.trim().isEmpty())
      {
         throw new LaTeXSyntaxException(getParser(),
           LaTeXSyntaxException.ERROR_MISSING_BEGIN_DOC, str);
      }

      if (useHtmlEntities)
      {
         for (int i = 0; i < str.length(); )
         {
            int cp = str.codePointAt(i);
            i += Character.charCount(cp);

            if (cp == '&')
            {
               currentWriter.write("&amp;");
            }
            else if (cp == '<')
            {
               currentWriter.write("&lt;");
            }
            else if (cp == '>')
            {
               currentWriter.write("&gt;");
            }
            else if (cp >= 128)
            {
               currentWriter.write(String.format("&#x%x;", cp));
            }
            else
            {
               currentWriter.write((char)cp);
            }
         }
      }
      else
      {
         currentWriter.write(str);
      }
   }

   @Override
   public void write(char c)
     throws IOException
   {
      if (currentWriter == null)
      {
         currentWriter = writer;

         if (currentWriter == null)
         {
            if (!Character.isWhitespace(c))
            {
               parser.debugMessage(TeXParser.DEBUG_IO, 
                 "No writer available. write(char): "+c);
            }

            return;
         }
      }

      if (inPreamble && !Character.isWhitespace(c))
      {
         throw new LaTeXSyntaxException(getParser(),
           LaTeXSyntaxException.ERROR_MISSING_BEGIN_DOC, c);
      }

      if (useHtmlEntities)
      {
         if (c == '&')
         {
            currentWriter.write("&amp;");
         }
         else if (c == '<')
         {
            currentWriter.write("&lt;");
         }
         else if (c == '>')
         {
            currentWriter.write("&gt;");
         }
         else if (c >= 128)
         {
            currentWriter.write(String.format("&#x%x;", c));
         }
         else
         {
            currentWriter.write(c);
         }
      }
      else
      {
         currentWriter.write(c);
      }
   }

   @Override
   public void writeln(String str)
     throws IOException
   {
      if (currentWriter == null)
      {
         currentWriter = writer;

         if (currentWriter == null)
         {
            if (!str.trim().isEmpty())
            {
               parser.debugMessage(TeXParser.DEBUG_IO, 
                 "No writer available. writeln: "+str);
            }

            return;
         }
      }

      if (inPreamble && !str.trim().isEmpty())
      {
         throw new LaTeXSyntaxException(getParser(),
           LaTeXSyntaxException.ERROR_MISSING_BEGIN_DOC, str);
      }

      write(String.format("%s%n", str));
      currentWriter.flush();
   }

   @Override
   public void href(String url, TeXObject text)
     throws IOException
   {
      if (currentWriter == null)
      {
         currentWriter = writer;

         if (currentWriter == null)
         {
            throw new IOException("No writer available");
         }
      }

      currentWriter.write("<a href=\"");

      write(url);// escape HTML entities

      currentWriter.write("\"");

      if (text instanceof AccSuppObject)
      {
         AccSupp accsupp = ((AccSuppObject)text).getAccSupp();

         if (accsupp.isIcon())
         {
            currentWriter.write(" class=\"icon\"");
         }
      }

      currentWriter.write(">");

      text.process(parser);

      currentWriter.write("</a>");
   }

   public boolean isIcon(AccSupp accsupp)
   {
      return accsupp.isIcon();
   }

   public boolean isIcon(TeXObject obj)
   {
      if (obj instanceof AccSuppObject)
      {
         return isIcon(((AccSuppObject)obj).getAccSupp());
      }

      return false;
   }

   @Override
   public TeXObject applyAccSupp(AccSupp accsupp, TeXObject object)
   {
      String tag = accsupp.getTag();
      String attr = accsupp.getAttribute();
      String text = accsupp.getText();
      String id = accsupp.getId();

      TeXObjectList list;

      if (tag != null && object instanceof TeXObjectList 
            && !object.isEmpty())
      {
         list = (TeXObjectList)object;

         if (list.firstElement() instanceof StartElement
              && (list.size() == 1 || list.lastElement() instanceof EndElement))
         {
            StartElement elem = (StartElement)list.firstElement();
            EndElement endElem = null;

            if (list.size() > 1)
            {
               endElem = (EndElement)list.lastElement();
            }

            if (elem.getName().equals(tag) 
                  && (endElem == null || endElem.getName().equals(tag)))
            {
               String elemId = elem.getAttribute("id");
               String elemText = null;

               if (attr != null)
               {
                  elem.getAttribute(attr);
               }

               if ((elemId == null || id == null || elemId.equals(id))
                  && (text == null || elemText == null || text.equals(elemText))
                  )
               {
                  if (id != null)
                  {
                     elem.putAttribute("id", id);
                  }

                  if (text != null)
                  {
                     elem.putAttribute(attr, text);
                  }

                  return object;
               }
            }
         }
      }

      list = createStack();

      if (tag == null)
      {
         tag = "span";
      }

      StartElement startElem = new StartElement(tag);

      if (id != null)
      {
         startElem.putAttribute("id", id);
      }

      if (text != null)
      {
         if (attr == null)
         {
            startElem.putAttribute("title", text);
         }
         else
         {
            startElem.putAttribute(attr, text);
         }
      }

      if (isIcon(accsupp))
      {
         startElem.putAttribute("class", "icon");
      }

      list.add(startElem);
      list.add(object);
      list.add(new EndElement(tag));

      return list;
   }

   @Override
   public TeXObject createAnchor(String anchorName, TeXObject text)
    throws IOException
   {
      if (text instanceof TeXObjectList && !text.isEmpty() 
           && ((TeXObjectList)text).firstElement() instanceof StartElement)
      {
         StartElement elem = (StartElement)((TeXObjectList)text).firstElement();

         if (!elem.hasAttribute("id"))
         {
            elem.putAttribute("id", anchorName);

            return text;
         }
      }

      TeXObjectList stack = createStack();
      String tag = "a";
      String id = HtmlTag.getUriFragment(anchorName);
      String attrName = null;
      String attrValue = null;

      if (text instanceof AccSuppObject)
      {
         AccSupp accsupp = ((AccSuppObject)text).getAccSupp();

         if ((accsupp.getId() == null || accsupp.getId().equals(id))
             && accsupp.getTag() != null)
         {
            tag = accsupp.getTag();

            attrName = accsupp.getAttribute();
            attrValue = accsupp.getText();

            if (attrValue != null && attrName == null)
            {
               attrName = "title";
            }

            text = ((AccSuppObject)text).getObject();
         }
      }

      StartElement elem = new StartElement(tag);
      elem.putAttribute("id", id);

      if (attrName != null)
      {
         elem.putAttribute(attrName, attrValue);
      }

      stack.add(elem);
      stack.add(text);

      stack.add(new EndElement(tag));

      return stack;
   }

   @Override
   public TeXObject createLink(String anchorName, TeXObject text)
    throws IOException
   {
      Vector<AuxData> auxData = getAuxData();

      if (auxData != null)
      {
         TeXObject label = AuxData.getLabelForLink(auxData, getParser(), anchorName);

         if (label != null)
         {
            anchorName = label.toString(parser);
         }
      }

      TeXObjectList stack = createStack();

      StartElement elem = new StartElement("a");

      if (text instanceof TeXObjectList && ((TeXObjectList)text).isStack()
        && ((TeXObjectList)text).size() == 1)
      {
         text = ((TeXObjectList)text).firstElement();

         if (isIcon(text))
         {
            elem.putAttribute("class", "icon");
         }
      }

      elem.putAttribute("href", "#"+HtmlTag.getUriFragment(anchorName));
      stack.add(elem);

      stack.add(text);

      stack.add(new EndElement("a"));

      return stack;
   }

   @Override
   public void substituting(String original, String replacement)
   {
      texApp.substituting(parser, original, replacement);
   }

   @Override
   public void skipping(Ignoreable ignoreable)
     throws IOException
   {
   }

   public boolean supportUnicodeScript()
   {
      return unicodeScriptSupport;
   }

   public void setSupportUnicodeScript(boolean support)
   {
      unicodeScriptSupport = support;
   }

   public boolean useMathJax()
   {
      return useMathJax;
   }

   public void setUseMathJax(boolean useMathJax)
   {
      this.useMathJax = useMathJax;
   }

   public String mathJaxStartInline()
   {
      return "\\(";
   }

   public String mathJaxEndInline()
   {
      return "\\)";
   }

   public String mathJaxStartDisplay()
   {
      return "\\[";
   }

   public String mathJaxEndDisplay()
   {
      return "\\]";
   }

   public void writeMathJaxHeader()
     throws IOException
   {
      setUseMathJax(true);

      writeliteralln("<!-- MathJax -->");
      writeliteralln("<script type=\"text/x-mathjax-config\">");
      writeliteralln("MathJax.Hub.Config({tex2jax:");
      writeliteralln("{");
      writeliteralln(String.format("  inlineMath: [['%s','%s']],",
        mathJaxStartInline().replace("\\", "\\\\"),
        mathJaxEndInline().replace("\\", "\\\\")));
      writeliteralln(String.format("  displayMath: [ ['%s','%s'] ]",
        mathJaxStartDisplay().replace("\\", "\\\\"),
        mathJaxEndDisplay().replace("\\", "\\\\")));
      writeliteralln("}});");

      writeliteralln("</script>");

      writeliteral("<script type=\"text/javascript\" async src=");
      writeliteralln(
       "\"https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=TeX-AMS-MML_HTMLorMML\">");
      writeliteralln("</script>");
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

      writeliteralln("table.tabular-"+suffix);
      writeliteralln("{");
      writeliteralln("  display: inline-table;");
      writeliteralln("  border-collapse: collapse;");

      if (halign != null)
      {
         writeliteralln("  align: "+halign+";");
      }

      if (valign != null)
      {
         writeliteralln("  vertical-align: "+valign+";");
      }

      writeliteralln("}");
   }

   public void writeCssStyles()
     throws IOException
   {
      writeliteralln("#main {margin-left: 5%; margin-right: 15%}");
      writeliteralln("div.tomain {position: absolute; left: 0pt; width: 5%; text-align: right; font-size: x-small;}");
      writeliteralln("div.tomain a {text-decoration: none;}");
      writeliteralln("div.labellink {display: inline; font-size: x-small; margin-left: 1em; margin-right: 1em;}");
      writeliteralln("div.marginleft {position: absolute; left: 0pt; width: 5%;}");
      writeliteralln("div.marginright {position: absolute; right: 0pt; width: 15%;}");

      writeliteralln("div.displaymath { display: block; text-align: center; }");
      writeliteralln("span.eqno { float: right; }");
      writeliteralln("div.table { display: block; text-align: center; }");

      writeliteralln(".linkicon { display: inline-block; }");
      writeliteralln(".linkiconleft { display: inline-block; padding-right: .25em; }");
      writeliteralln(".linkiconright { display: inline-block; padding-left: .25em; }");
      writeliteralln("a.icon { white-space: nowrap; }");
      writeliteralln("a.icon span { white-space: normal; }");

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

      writeliteralln("pre { white-space: pre-wrap; }");
      writeliteralln("div.figure { display: block; text-align: center; }");
      writeliteralln("div.caption { display: block; text-align: center; }");
      writeliteralln("div.marginpar { float: right; }");
      writeliteralln("div.abstract { display: block; margin-right: 4em; margin-left: 4em;}");
      writeliteralln("div.title { display: block; text-align: center; font-size: x-large;}");
      writeliteralln("div.author { display: block; text-align: center; font-size: large;}");
      writeliteralln("div.date { display: block; text-align: center; font-size: medium;}");
      writeliteralln("div.bibliography { display: block; margin-left: 4em; }");
      writeliteralln("div.bibitem { display: inline; float: left; text-indent: -3em; }");
      writeliteralln("span.numberline { display: inline-block; width: 3em; }");
      writeliteralln(".toc-subsection span.numberline { display: inline-block; width: 3em; }");
      writeliteralln(".toc-subsubsection span.numberline { display: inline-block; width: 4em; }");
      writeliteralln(".toc-paragraph span.numberline { display: inline-block; width: 5em; }");
      writeliteralln(".toc-subparagraph span.numberline { display: inline-block; width: 6em; }");
      writeliteralln("nav ul { list-style-type: none; }");
      writeliteralln("@media screen and (min-width: 400px)");
      writeliteralln("{");
      writeliteralln(" nav#doc-nav { background: #fffc; padding: 5px; }");
      writeliteralln(" div.nav-content { position: fixed; top: 10px; right: 15px; max-width: 14%; max-height: 75vh; overflow: auto; z-index: 1; hyphens: auto; }");
      writeliteralln("}");
      writeliteralln("div.nav-content ul { padding-left: 10px; }");
      writeliteralln("div.prevpage { float: left; max-width: 30%; }");
      writeliteralln("div.uppage { display: inline-block;  max-width: 30%; }");
      writeliteralln("div.nextpage { float: right;  max-width: 30%; }");
      writeliteralln("footer.doc-nav { margin-top: 5px; margin-bottom: 5px; padding-right: 15px; text-align: center; }");
      writeliteralln("a.current { font-weight: bold; }");
      writeliteralln(".toc-part { padding-left: 0em; padding-bottom: 1ex; padding-top: 1ex; font-weight: bold; font-size: large;}");
      writeliteralln(".toc-chapter { padding-left: 0em; padding-bottom: .25ex; padding-top: .25ex; font-weight: bold; }");
      writeliteralln(".toc-section { padding-left: .5em; }");
      writeliteralln(".toc-subsection { padding-left: 1.5em; }");
      writeliteralln(".toc-subsubsection { padding-left: 2em; }");
      writeliteralln(".toc-paragraph { padding-left: 2.5em; }");
      writeliteralln(".toc-subparagraph { padding-left: 3em; }");

      writeliteralln(".part { font-size: x-large; font-weight: bold; }");

      writeliteralln("div.bigskip { padding-left: 0pt; padding-right: 0pt; padding-top: 0pt; padding-bottom: 2ex;}");
      writeliteralln("div.medskip { padding-left: 0pt; padding-right: 0pt; padding-top: 0pt; padding-bottom: 1ex;}");
      writeliteralln("div.smallskip { padding-left: 0pt; padding-right: 0pt; padding-top: 0pt; padding-bottom: .5ex;}");

      writeliteralln("span.quad { padding-left: 0pt; padding-right: 1em; padding-top: 0pt; padding-bottom: 0pt;}");
      writeliteralln("span.qquad { padding-left: 0pt; padding-right: 2em; padding-top: 0pt; padding-bottom: 0pt;}");

      writeliteralln(".displaylist { display: block; list-style-type: none; }");
      writeliteralln(".inlinelist { display: inline; }");

      writeliteralln("dl.inlinetitle dt { display: inline-block; margin-left: 0; margin-right: 1em;}");
      writeliteralln("dl.inlinetitle dd { display: inline; margin: 0; }");
      writeliteralln("dl.inlinetitle dd::after { display: block; content: ''; }");

      // TODO The inlineblock needs adjusting
      writeliteralln("dl.inlineblock dt { display: inline-block; margin-left: 0; margin-right: 1em;}");
      writeliteralln("dl.inlineblock dd { display: inline; margin: 0; }");
      writeliteralln("dl.inlineblock dd::after { display: block; content: ''; }");

      writeliteralln(".clearfix::after { content: \"\"; clear: both; display: table; }");
      writeliteralln("span.inlineitem { margin-right: .5em; margin-left: .5em; }");
      writeliteralln("span.numitem { float: left; margin-left: -3em; text-align: right; min-width: 2.5em; }");
      writeliteralln("span.bulletitem { float: left; margin-left: -1em; }");
      writeliteralln("span.descitem { font: normal; font-weight: bold; }");

      writeliteralln("div.indexspace { min-height: 2ex; }");

      for (Iterator<String> it = defaultStyles.keySet().iterator();
           it.hasNext();)
      {
         String style = it.next();
         writeliteralln(String.format(".%s {%s}", defaultStyles.get(style), style));
      }

      for (String style : extraCssStyles)
      {
         writeliteralln(style);
      }
   }

   public void addCssStyle(String style)
   {
      extraCssStyles.add(style);
   }

   public void addToHead(String content)
   {
      if (extraHead == null)
      {
         extraHead = new Vector<String>();
      }

      extraHead.add(content);
   }

   /**
    * Writes the DOCTYPE line.
    */ 
   protected void writeDocType()
     throws IOException
   {
      writeliteralln("<!DOCTYPE html>");
   }

   @Override
   public void documentclass(KeyValList options, String clsName, 
      boolean loadParentOptions, TeXObjectList stack)
     throws IOException
   {
      super.documentclass(options, clsName, loadParentOptions, stack);

      if (parser.getControlSequence("c@chapter") != null)
      {
         putControlSequence(new L2HSection("chapter"));
      }

      writeDocType();
      writeliteralln("<html>");
      writeliteralln("<head>");

      writeliteralln(String.format(
       "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=%s\">", 
       htmlCharSet.name()));

      ControlSequence cs = parser.getControlSequence("TeXParserLibGeneratorName");

      if (cs != null)
      {
         generator = processToString(cs, stack).replaceAll("\"", "");
      }

      if (!generator.isEmpty())
      {
         writeliteralln(String.format("<meta name=\"generator\" content=\"%s\">",
           generator));
      }

      if (useMathJax())
      {
         writeMathJaxHeader();
      }

      if (extraHead != null)
      {
         for (String content : extraHead)
         {
            writeliteralln(content);
         }
      }

      inPreamble = true;
   }

   /**
    * Add support for known MathJax commands. This needs to be done
    * after packages have been loaded to ensure that the original
    * command is defined. Called by beginDocument(TeXObjectList) if
    * the useMathJax flag is set. Commands like \abovewithdelims are 
    * dealt with by the corresponding TeXParserListener method.
    * Known symbol commands defined as the wrapper classes, such as
    * L2HMathSymbol, are dealt with by the sub-class.
    * Unknown commands will be dealt with by L2HUndefined.
    */
   protected void addMathJaxCommands()
   {
      // \begin and \end currently not implemented
      //putControlSequence(new L2HBegin());
      //putControlSequence(new L2HEnd());

      putControlSequence(new L2HMathJaxCommand(getControlSequence("textstyle")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("displaystyle")));

      putControlSequence(new L2HMathJaxCommand(getControlSequence("text")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("mbox")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("textrm")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("textsf")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("texttt")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("textbf")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("textmd")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("textit")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("textsl")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("textup")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("textsc")));

      putControlSequence(new L2HMathJaxCommand(getControlSequence("mathrm")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("mathsf")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("mathtt")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("mathit")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("mathbf")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("mathcal")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("mathbb")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("mathfrak")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("boldsymbol")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("pmb")));

      putControlSequence(new L2HMathJaxCommand(getControlSequence(" ")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("_")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence(",")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence(";")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence(":")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("!")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("{")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("}")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("&")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("#")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("%")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence(">")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("|")));
      putControlSequence(new L2HMathJaxCommand(getControlSequence("$")));
   }

   @Override
   public void beginDocument(TeXObjectList stack)
     throws IOException
   {
      inPreamble = false;

      TeXObject cs = getControlSequence("@title");
      TeXObject title = null;

      if (!(cs instanceof Undefined) && !cs.isEmpty())
      {
         title = TeXParserUtils.expandFully(cs, getParser(), stack);

         writeliteral("<title>");
         write(title.purified());
         writeliteralln("</title>");
      }
      else
      {
         writeliteralln("<!-- no title found -->");
      }

      writeliteralln("<style type=\"text/css\">");

      addDefaultTabularStyles();
      writeCssStyles();
      writeliteralln("</style>");

      writeliteralln("</head>");
      writeliteral("<body");

      Color fgCol = getParser().getSettings().getFgColor();
      Color bgCol = getParser().getSettings().getBgColor();

      if (fgCol != null && fgCol != Color.BLACK)
      {
         writeliteral(String.format(" text=\"%s\"", getHtmlColor(fgCol)));
      }

      if (bgCol != null && bgCol != Color.WHITE)
      {
         writeliteral(String.format(" background=\"%s\"", 
           getHtmlColor(bgCol)));
      }

      writeliteralln(">");

      super.beginDocument(stack);

      if (useMathJax())
      {
         addMathJaxCommands();
      }

      getParser().getSettings().setCharMapMode(TeXSettings.CHAR_MAP_ON);

      if (divisionData != null && !divisionData.isEmpty())
      {
         if (title != null)
         {
            DivisionInfo divInfo = divisionData.firstElement();
            divInfo.setTitle(title);
         }

         createDivisionTree(stack);

         writeNavigationList();
      }

      writeliteralln("<div id=\"main\">");
   }

   @Override
   public void endDocument(TeXObjectList stack)
     throws IOException
   {
      if (!isInDocEnv())
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_NO_BEGIN_DOC);
      }

      if (currentSection != null)
      {
         writeliteral(String.format("%n</section><!-- end of section %s -->%n", currentSection));

         currentSection = null;
      }

      processFootnotes(stack);

      if (currentNode != null)
      {
         footerNav();
      }

      writeliteralln("</div><!-- end of main -->");// ends <div id="main">

      ControlSequence cs = parser.getControlSequence(
        "@enddocumenthook");

      if (cs != null)
      {
         try
         {
            TeXParserUtils.process(cs, parser, stack);
         }
         catch (IOException e)
         {
            getParser().error(e);
         }
      }

      writeliteralln("</body>");
      writeliteralln("</html>");

      documentEnded = true;
      writer.close();

      throw new EOFException();
   }

   protected void startDivisionFile(TeXObjectList stack)
   throws IOException
   {
      writeDocType();
      writeliteralln("<html>");
      writeliteralln("<head>");

      writeliteralln(String.format(
       "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=%s\">", 
       htmlCharSet.name()));

      if (!generator.isEmpty())
      {
         writeliteralln(String.format("<meta name=\"generator\" content=\"%s\">",
           generator));
      }

      if (useMathJax())
      {
         writeMathJaxHeader();
      }

      if (extraHead != null)
      {
         for (String content : extraHead)
         {
            writeliteralln(content);
         }
      }

      writeliteral("<title>");
      writeliteral(currentNode.getTitle());
      writeliteral("</title>");

      writeliteralln("<style type=\"text/css\">");
      writeCssStyles();
      writeliteralln("</style>");

      writeliteralln("</head>");
      writeliteral("<body");

      Color fgCol = getParser().getSettings().getFgColor();
      Color bgCol = getParser().getSettings().getBgColor();

      if (fgCol != null && fgCol != Color.BLACK)
      {
         writeliteral(String.format(" text=\"%s\"", getHtmlColor(fgCol)));
      }

      if (bgCol != null && bgCol != Color.WHITE)
      {
         writeliteral(String.format(" background=\"%s\"", 
           getHtmlColor(bgCol)));
      }

      writeliteralln(">");

      writeNavigationList();

      writeliteralln("<div id=\"main\">");

   }

   protected void endDivisionFile(TeXObjectList stack)
   throws IOException
   {
      if (currentSection != null)
      {
         writeliteral(String.format("%n</section><!-- end of section %s -->%n", currentSection));

         currentSection = null;
      }

      processFootnotes(stack);

      footerNav();

      writeliteralln("</div><!-- end of main -->");// ends <div id="main">

      writeliteralln("</body>");
      writeliteralln("</html>");
   }

   protected void footerNav() throws IOException
   {
      writeliteralln("<footer class=\"doc-nav\">");

      int idx = currentNode.getIndex();

      DivisionNode prevNode = null;

      for (int i = idx-1; i >= 0; i--)
      {
         DivisionInfo info = divisionData.get(i);
         DivisionNode node = (DivisionNode)info.getSpecial();

         if (!node.getRef().startsWith("#"))
         {
            prevNode = node;
            break;
         }
      }

      DivisionNode nextNode = null;

      for (int i = idx+1; i < divisionData.size(); i++)
      {
         DivisionInfo info = divisionData.get(i);
         DivisionNode node = (DivisionNode)info.getSpecial();

         if (!node.getRef().startsWith("#"))
         {
            nextNode = node;
            break;
         }
      }

      if (prevNode != null)
      {
         writeliteralln("<div title=\"Previous\" class=\"prevpage\">");

         writeliteral(String.format("<a href=\"%s\">", prevNode.getRef()));

         writeliteral("<div class=\"linkiconleft\">&#x23F4;</div>");

         String prefix = prevNode.getPrefix();

         if (prefix != null)
         {
            writeliteral(prefix);
            writeliteral(" ");
         }

         writeliteral(prevNode.getTitle());

         writeliteralln("</a>");

         writeliteralln("</div>");
      }

      writeliteralln("<div title=\"Up\" class=\"uppage\">");

      DivisionNode upNode = currentNode.getParent();

      while (upNode != null && upNode.getRef().startsWith("#"))
      {
         upNode = upNode.getParent();
      }

      if (upNode != null)
      {
         writeliteral(String.format("<a href=\"%s\">", upNode.getRef()));

         writeliteral("<div class=\"linkiconleft\">&#x23F6;</div>");

         String prefix = upNode.getPrefix();

         if (prefix != null)
         {
            writeliteral(prefix);
            writeliteral(" ");
         }

         writeliteral(upNode.getTitle());
         writeliteral("</a>");
      }

      writeliteralln("</div>");

      if (nextNode != null)
      {
         writeliteralln("<div title=\"Next\" class=\"nextpage\">");

         writeliteral(String.format("<a href=\"%s\">", nextNode.getRef()));

         String prefix = nextNode.getPrefix();

         if (prefix != null)
         {
            writeliteral(prefix);
            writeliteral(" ");
         }

         writeliteral(nextNode.getTitle());

         writeliteral("<div class=\"linkiconright\">&#x23F5;</div>");

         writeliteralln("</a>");

         writeliteralln("</div>");
      }

      writeliteralln("</footer>");

   }

   @Override
   public void addFootnote(TeXObject footnote, TeXObjectList stack)
   throws IOException
   {
      if (footnoteWriter == null)
      {
         footnoteWriter = new StringWriter();
      }
      else
      {
         footnoteWriter.write("<p>");
      }

      try
      {
         currentWriter = footnoteWriter;
         TeXParserUtils.process(footnote, getParser(), stack);
      }
      finally
      {
         currentWriter = writer;
      }
   }

   @Override
   public void processFootnotes(TeXObjectList stack)
   throws IOException
   {
      if (footnoteWriter != null)
      {
         doFootnoteRule();

         writer.write(footnoteWriter.toString());

         footnoteWriter = null;
      }
   }

   public String processToString(TeXObject obj, TeXObjectList stack)
   throws IOException
   {
      StringWriter strWriter = new StringWriter();

      try
      {
         currentWriter = strWriter;
         TeXParserUtils.process(obj, getParser(), stack);
      }
      finally
      {
         currentWriter = writer;
      }

      return strWriter.toString();
   }

   protected void createDivisionTree(TeXObjectList stack)
    throws IOException
   {
      if (divisionData == null || divisionData.isEmpty()) return;

      divisionMap = new HashMap<String,DivisionNode>();

      DivisionNode prevNode = null;

      for (int i = 0; i < divisionData.size(); i++)
      {
         DivisionInfo info = divisionData.get(i);

         DivisionNode parent = null;

         if (prevNode != null)
         {
            if (prevNode.getUnit().equals(info.getUnit()))
            {
               parent = prevNode.getParent();
            }
            else
            {
               parent = prevNode.getAncestorAtUnit(info.getUnit());

               if (parent == null)
               {
                  DivisionNode childNode = prevNode.getFirstChild();

                  if (childNode == null || childNode.getUnit().equals(info.getUnit()))
                  {
                     parent = prevNode;
                  }
               }
               else
               {
                  parent = parent.getParent();
               }
            }
         }

         DivisionNode node = new DivisionNode(i, info, parent);

         if (i == 0) currentNode = node;

         String label = info.getLabel();

         if (label == null)
         {
            label = info.getTarget();

            if (label == null)
            {
               label = "node"+i;
            }
         }

         if (node.getLevel() == 0)
         {
            File f = new File(outPath.toFile(), baseName+"."+getSuffix());

            node.setFile(f);
            node.setRef(f.getName());
         }
         else if (splitLevel >= node.getLevel() || node.getParent() == null)
         {
            File f = new File(outPath.toFile(), getFileNameForNode(node));

            node.setFile(f);
            node.setRef(f.getName());
         }
         else if (label != null)
         {
            DivisionNode pNode = node.getParent();

            if (pNode.getRef().startsWith("#"))
            {
               node.setRef("#"+HtmlTag.getUriFragment(label));
            }
            else
            {
               node.setRef(pNode.getRef() + "#"+HtmlTag.getUriFragment(label));
            }
         }

         TeXObject obj = info.getTitle();
         String title = "Untitled";

         if (obj != null)
         {
            title = processToString(obj, stack);
         }

         node.setTitle(title);

         obj = info.getPrefix();

         if (obj != null)
         {
            node.setPrefix(processToString(obj, stack));
         }

         divisionMap.put(label, node);

         prevNode = node;
      }

   }

   protected void writeNavigationList()
    throws IOException
   {
      writeliteralln("<div class=\"nav-content\">");
      writeliteralln("<nav id=\"doc-nav\" aria-label=\"Document Navigation\">");
      writeNavigationList(null, null);
      writeliteralln("</nav>");
      writeliteralln("</div>");
   }

   protected void writeNavigationList(String cssClass, String cssId)
    throws IOException
   {
      if (divisionData == null) return;

      File currentFile = null;

      if (currentNode != null)
      {
         currentFile = currentNode.getFile();
      }

      writeliteralln("<!-- Navigation -->");

      writeliteral("<ul");

      if (cssId != null)
      {
         writeliteral(" id=\"");
         writeliteral(cssId);
         writeliteral("\"");
      }

      if (cssClass != null)
      {
         writeliteral(" class=\"");
         writeliteral(cssClass);
         writeliteral("\"");
      }

      writeliteralln(">");

      DivisionNode prevNode = null;

      for (DivisionInfo divInfo : divisionData)
      {
         DivisionNode node = (DivisionNode)divInfo.getSpecial();

         if (node == null)
         {
            parser.debugMessage(TeXParser.DEBUG_IO, 
              "No node associated with: "+divInfo);

            continue;
         }

         if (prevNode == null || prevNode.getLevel() == node.getLevel())
         {
         }
         else if (prevNode.getLevel() < node.getLevel())
         {
            writeliteralln("<ul>");
         }
         else
         {
            writeliteralln("</ul>");
         }

         writeliteral("<li>");

         String prefix = node.getPrefix();
         String ref = node.getRef();

         if (currentFile != null && currentFile.equals(node.getFile()))
         {
            int idx = ref.indexOf("#");

            if (idx > -1)
            {
               ref = ref.substring(idx);
            }
            else
            {
               ref = "#main";
            }
         }

         writeliteral(String.format("<a href=\"%s\"", ref));

         if (node == currentNode)
         {
            writeliteral(" class=\"current\"");
         }

         writeliteral(">");

         if (prefix != null)
         {
            writeliteral(prefix);
            writeliteral(" ");
         }

         writeliteral(node.getTitle());

         writeliteralln("</a>");

         prevNode = node;
      }

      writeliteralln("</ul>");

      writeliteralln("<!-- End of Navigation -->");
   }

   protected String getFileNameForNode(DivisionNode node)
   {
      String label = node.getData().getLabel();

      if (label == null)
      {
         label = node.getData().getTarget();

         if (label == null)
         {
            label = "node"+node.getIndex();
         }
         else
         {
            label = label.replaceAll("\\*", "star");
            label = label.replaceAll("\\.", "");
         }
      }
      else
      {
         int idx = label.indexOf(":");

         if (idx >= 0)
         {
            label = label.substring(idx+1);
         }
      }

      if (splitUseBaseNamePrefix)
      {
         return baseName + "-" + label + "." + suffix;
      }
      else
      {
         return label + "." + suffix;
      }
   }

   public DivisionNode getDivisionNode(String label)
   {
      return divisionMap == null ? null : divisionMap.get(label);
   }

   @Override
   public void overwithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXObject before, TeXObject after)
    throws IOException
   {
      if (useMathJax())
      {
         if (firstDelim != null || secondDelim != null)
         {
            writeliteral("\\left");
            write(firstDelim==null?".":firstDelim.toString(getParser()));
         }

         writeliteral("\\frac{");
         before.process(getParser());
         writeliteral("}{");
         after.process(getParser());
         writeliteral("}");

         if (firstDelim != null || secondDelim != null)
         {
            writeliteral("\\right");
            write(secondDelim==null?".":secondDelim.toString(getParser()));
         }

         return;
      }

      if (firstDelim != null)
      {
        firstDelim.process(parser);
      }

      writeliteral("<table style=\"display: inline;\"><tr style=\"border-bottom-style: solid;\"><td>");
      before.process(parser);
      writeliteral("</td></tr>");
      writeliteral("<tr><td>");
      after.process(parser);
      writeliteral("</td></tr><table>");

      if (secondDelim != null)
      {
         secondDelim.process(parser);
      }
   }

   @Override
   public void abovewithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness,
     TeXObject before, TeXObject after)
    throws IOException
   {
      if (useMathJax())
      {
         write(before.toString(getParser()));
         writeliteral("\\abovewithdelims ");
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

      writeliteral("<table><tr><td>");
      before.process(parser);
      writeliteral("</td></tr>");
      writeliteral("<tr><td>");
      after.process(parser);
      writeliteral("</td></tr><table>");

      if (secondDelim != null)
      {
         secondDelim.process(parser);
      }
   }

   @Override
   public void subscript(TeXObject arg)
    throws IOException
   {
      if (useMathJax())
      {
         writeliteral("_{");
         arg.process(parser);
         writeliteral("}");
      }
      else
      {
         writeliteral("<sub>");
         arg.process(parser);
         writeliteral("</sub>");
      }
   }

   @Override
   public void superscript(TeXObject arg)
    throws IOException
   {
      if (useMathJax())
      {
         writeliteral("^{");
         arg.process(parser);
         writeliteral("}");
      }
      else
      {
         writeliteral("<sup>");
         arg.process(parser);
         writeliteral("</sup>");
      }
   }

   public void verb(String name, boolean isStar, char delim,
     String text)
    throws IOException
   {
      writeliteral("<code style=\"white-space: pre;\">");

      super.verb(name, isStar, delim, text);

      writeliteral("</code>");
   }

   public void beginVerbatim() throws IOException
   {
      writeliteral("<pre>");
   }

   public void endVerbatim() throws IOException
   {
      writeliteral("</pre>");
   }

   public String getImagePreamble() throws IOException
   {
      String preamble = null;

      ControlSequence cs = parser.getControlSequence(
         "TeXParserLibToImagePreamble");

      if (cs != null && cs instanceof Expandable)
      {
         TeXObjectList expanded;

         expanded = ((Expandable)cs).expandonce(parser);

         if (expanded != null)
         {
            preamble = expanded.toString(parser);
         }
      }

      if (preamble == null)
      {
         LaTeXFile cls = getDocumentClass();

         StringBuilder builder = new StringBuilder();

         if (cls == null)
         {
            builder.append("\\documentclass{article}");
         }
         else
         {
            builder.append("\\documentclass");

            KeyValList styOpts = cls.getOptions();

            if (styOpts != null)
            {
               builder.append(String.format("[%s]", styOpts.format()));
            }

            builder.append(String.format("{%s}%n", cls.getName()));
         }

         for (LaTeXFile lf : getLoadedPackages())
         {
            builder.append("\\usepackage");

            KeyValList styOpts = lf.getOptions();

            if (styOpts != null)
            {
               builder.append(String.format("[%s]", styOpts.format()));
            }

            builder.append(String.format("{%s}%n", lf.getName()));
         }

         builder.append("\\pagestyle{empty}%n");

         preamble = builder.toString();
      }

      return preamble;
   }

   public static String getMimeType(String filename)
   {
      int idx = filename.lastIndexOf(".")+1;

      if (idx < 0)
      {
         return null;
      }

      String ext = filename.substring(idx).toLowerCase();

      if (ext.equals("pdf"))
      {
         return MIME_TYPE_PDF;
      }

      if (ext.equals("png"))
      {
         return MIME_TYPE_PNG;
      }

      if (ext.equals("jpeg") || ext.equals("jpg"))
      {
         return MIME_TYPE_JPEG;
      }

      return null;
   }

   public Dimension getImageSize(File file, String mimetype)
   {
      return null;
   }

   @Override
   public void includegraphics(TeXObjectList stack, 
       KeyValList options, String filename)
    throws IOException
   {
      File file = getImageFile(filename);

      if (file == null || !file.exists())
      {
         throw new TeXSyntaxException(parser, 
          TeXSyntaxException.ERROR_FILE_NOT_FOUND, filename);
      }

      Path imagePath = file.toPath();
      Path relPath;

      if (imagePath.startsWith(basePath))
      {
         relPath = basePath.relativize(imagePath);
      }
      else if (imagePath.isAbsolute())
      {
         relPath = imagePath.getName(imagePath.getNameCount()-1);
      }
      else
      {
         relPath = imagePath;
      }

      TeXObject alt = null;
      String cssClass = null;
      String cssStyle = null;

      String type=getMimeType(file.getName());
      L2HImage image=null;

      double scale = 1;
      int zoom = 100;

      StringBuilder optionsBuilder = new StringBuilder();

      if (options != null)
      {
         for (Iterator<String> it = options.getOrderedKeyIterator();
              it.hasNext();)
         {
            String key = it.next();
            TeXObject value = options.get(key);

            if (key.equals("alt"))
            {
               alt = value;
            }
            else if (key.equals("class"))
            {
               cssClass = parser.expandToString(value, stack);
            }
            else if (key.equals("style"))
            {
               cssStyle = parser.expandToString(value, stack);
            }
            else if (key.equals("scale"))
            {
               if (value != null)
               {
                  scale = TeXParserUtils.toDouble(value, parser, stack);
               }
            }
            else if (key.equals("zoom"))
            {
               if (value != null)
               {
                  zoom = TeXParserUtils.toInt(value, parser, stack);
               }
            }
            else
            {
               if (optionsBuilder.length() > 0)
               {
                  optionsBuilder.append(',');
               }

               optionsBuilder.append(key);

               if (value != null && !value.isEmpty())
               {
                  optionsBuilder.append('=');
                  optionsBuilder.append(value.toString(parser));
               }
            }
         }
      }

      if (optionsBuilder.length() > 0)
      {
         StringBuilder content = new StringBuilder("\\includegraphics[");
         String sep = null;

         content.append(optionsBuilder);

         content.append("]{");
         content.append(filename);
         content.append('}');

         if (getParser().isDebugMode(TeXParser.DEBUG_IO))
         {
            getParser().logMessage("Creating image "+content.toString());
         }

         image = toImage(getImagePreamble(),
          content.toString(), type, alt, null, true);
      }

      if (image != null)
      {
         TeXParserUtils.process(image, parser, stack);
      }
      else
      {
         Path dest = (outPath == null ? relPath : outPath.resolve(relPath));

         Dimension dim = getImageSize(file, type);

         String uri = getUri(relPath);

         if (MIME_TYPE_PDF.equals(type))
         {
            uri += String.format("?#zoom=%d", zoom);
         }

         writeliteral(String.format("<object data=\"%s\"", uri));

         if (type != null)
         {
            writeliteral(String.format(" type=\"%s\"", type));
         }

         if (cssClass != null)
         {
            writeliteral(String.format(" class=\"%s\"", cssClass));
         }

         if (cssStyle != null)
         {
            writeliteral(String.format(" style=\"%s\"", cssStyle));
         }

         if (dim != null)
         {
            writeliteral(String.format(" width=\"%d\" height=\"%d\"",
              (int)Math.round(scale*dim.width), (int)Math.round(scale*dim.height)));
         }

         writeliteral(">");

         try
         {
            if (alt != null)
            {
               TeXParserUtils.process(alt, parser, stack);
            }
         }
         finally
         {
            writeliteral("</object>");
         }

         try
         {
            getTeXApp().copyFile(file, dest.toFile());
         }
         catch (InterruptedException e)
         {
            getParser().error(e);
         }
      }
   }

   protected void writeTransform(String tag, String property)
   throws IOException
   {
      writeTransform(tag, property, null);
   }

   protected void writeTransform(String tag, String property, String originProp)
   throws IOException
   {
      writeliteral(String.format(
        "<%s style=\"display: inline-block; transform: %s; -ms-transform: %s; -webkit-transform: %s;",
        tag, property, property, property));

      if (originProp != null)
      {
         writeliteral(String.format(
          " transform-origin: %s; -ms-transform-origin: %s; -webkit-transform-origin: %s;",
          originProp, originProp, originProp));
      }

      writeliteral("\">");
   }

   public void transform(String function, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      writeTransform("div", function);

      if (stack == parser || stack == null)
      {
         object.process(parser);
      }
      else
      {
         object.process(parser, stack);
      }

      writeliteral("</div>");
   }

   public void rotate(double angle, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      transform(String.format("rotate(%fdeg)", -angle), parser, stack, object);
   }

   public void rotate(double angle, double originPercentX, 
      double originPercentY, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      if (originPercentX == 0 && originPercentY == 0)
      {
         transform(String.format("rotate(%fdeg)", -angle),
            parser, stack, object);
      }
      else
      {
         transform(String.format("rotate(%fdeg)", -angle, 
           String.format("%d%% %d%%", originPercentX, originPercentY)),
           parser, stack, object);
      }
   }

   public void rotate(double angle, TeXDimension orgX, TeXDimension orgY,
      TeXParser parser, TeXObjectList stack, TeXObject object)
   throws IOException
   {
      if (orgX == null && orgX == null)
      {
         transform(String.format("rotate(%fdeg)", -angle),
            parser, stack, object);
      }
      else
      {
         String x = "0%";
         String y = "0%";

         if (orgX != null)
         {
            x = String.format("%f%s", orgX.format());
         }

         if (orgY != null)
         {
            y = String.format("%f%s", orgY.format());
         }

         transform(String.format("rotate(%fdeg)", -angle, 
           String.format("%s %s", x, y)),
           parser, stack, object);
      }
   }

   public void scale(double factorX, double factorY, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      transform(String.format("scale(%f,%f)", factorX, factorY),
        parser, stack, object);
   }

   public void scaleX(double factor, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      transform(String.format("scaleX(%f)", factor),
        parser, stack, object);
   }

   public void scaleY(double factor, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      transform(String.format("scaleY(%f)", factor),
        parser, stack, object);
   }

   public void resize(TeXDimension width, TeXDimension height,
      TeXParser parser, TeXObjectList stack, TeXObject object)
   throws IOException
   {// not implemented

      writeliteral("<div style=\"display: inline-block;");

      if (width != null)
      {
         writeliteral(String.format(" width: %s;", getHtmlDimension(width)));
      }

      if (height != null)
      {
         writeliteral(String.format(" height: %s;", getHtmlDimension(height)));
      }

      writeliteral("\">");

      if (stack == parser || stack == null)
      {
         object.process(parser);
      }
      else
      {
         object.process(parser, stack);
      }

      writeliteral("</div>");
   }

   @Override
   public TeXApp getTeXApp()
   {
      return texApp;
   }

   @Override
   public void endParse(File file)
    throws IOException
   {
   }

   @Override
   public void beginParse(File file, Charset encoding)
    throws IOException
   {
      getParser().message(TeXApp.MESSAGE_READING, file);

      if (encoding != null)
      {
         getParser().message(TeXApp.MESSAGE_ENCODING, encoding);
      }

      File parentFile = file.getParentFile();

      if (parentFile == null)
      {
         parentFile = new File(".");
      }

      basePath = parentFile.toPath();

      if (writer == null)
      {
         Files.createDirectories(outPath);

         baseName = file.getName();

         int idx = baseName.lastIndexOf(".");

         if (idx > -1)
         {
            baseName = baseName.substring(0,idx);
         }

         File outFile = new File(outPath.toFile(), baseName+"."+getSuffix());

         getParser().message(TeXApp.MESSAGE_WRITING, outFile);

         getParser().message(TeXApp.MESSAGE_ENCODING, htmlCharSet);

         writer = new PrintWriter(Files.newBufferedWriter(outFile.toPath(),
            htmlCharSet));
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

   @Override
   public ControlSequence createUndefinedCs(String name)
   {
      return new L2HUndefined(name, getUndefinedAction());
   }

   public ActiveChar getUndefinedActiveChar(int charCode)
   {
      return new UndefinedActiveChar(charCode, getUndefinedAction());
   }

   public void marginpar(TeXObject leftText, TeXObject rightText)
     throws IOException
   {
      writeliteral("<div class=\"margin");

      try
      {
         if (isMarginRight())
         {
            writeliteral("right\">");
            rightText.process(parser);
         }
         else
         {
            writeliteral("left\">");
            leftText.process(parser);
         }
      }
      finally
      {
         writeliteral("</div>");
      }
   }

   public void doFootnoteRule() throws IOException
   {
      writeliteralln("<p><hr><p>");
   }

   public IndexLocation createIndexLocation(String indexLabel)
    throws IOException
   {
      indexLoc++;
      String anchor = String.format("idx-%s-%d", 
        HtmlTag.getUriFragment(indexLabel), indexLoc);

      writeliteral(String.format("<a id=\"%s\"></a>", anchor));

      return new IndexLocation(new HtmlTag(
        String.format("<a ref=\"#%s\">%d</a>", anchor, indexLoc)));
   }

   @Override
   public void registerControlSequence(LaTeXSty sty, ControlSequence cs)
   {
      styCs.add(cs.getName());
      putControlSequence(cs);
   }

   public boolean isStyControlSequence(ControlSequence cs)
   {
      return styCs.contains(cs.getName());
   }

   public void startList(TrivListDec trivlist) throws IOException
   {
      super.startList(trivlist);

      if (trivlist instanceof DescriptionDec)
      {
         switch (((DescriptionDec)trivlist).getStyle())
         {
            case INLINE_TITLE:
               writeliteral(String.format("%n<dl class=\"inlinetitle\">%n"));
            break;
            case INLINE_BLOCK_TITLE:
               writeliteral(String.format("%n<dl class=\"inlineblock\">%n"));
            break;
            default:
               writeliteral(String.format("%n<dl>%n"));
         }
      }
      else if (trivlist.isInLine())
      {
         writeliteral("<div class=\"inlinelist\">");
      }
      else
      {
         if (isIfTrue(getControlSequence("if@nmbrlist")))
         {
            writeliteral(String.format("%n<ol class=\"displaylist\">%n"));
         }
         else
         {
            writeliteral(String.format("%n<ul class=\"displaylist\">%n"));
         }
      }
   }

   public void endList(TrivListDec trivlist) throws IOException
   {
      if (trivlist instanceof DescriptionDec)
      {
         writeliteral(String.format("%n</dl>%n"));
      }
      else if (trivlist.isInLine())
      {
         writeliteral("</div>");
      }
      else
      {
         if (isIfTrue(getControlSequence("if@nmbrlist")))
         {
            writeliteral(String.format("%n</ol>%n"));
         }
         else
         {
            writeliteral(String.format("%n</ul>%n"));
         }
      }

      super.endList(trivlist);
   }

   public String getHtmlColor(Color col)
   {
      if (col == Color.BLACK)
      {
         return "black";
      }
      else if (col == Color.BLUE)
      {
         return "blue";
      }
      else if (col == Color.CYAN)
      {
         return "cyan";
      }
      else if (col == Color.GRAY)
      {
         return "gray";
      }
      else if (col == Color.GREEN)
      {
         return "green";
      }
      else if (col == Color.MAGENTA)
      {
         return "magenta";
      }
      else if (col == Color.ORANGE)
      {
         return "orange";
      }
      else if (col == Color.PINK)
      {
         return "pink";
      }
      else if (col == Color.RED)
      {
         return "red";
      }
      else if (col == Color.WHITE)
      {
         return "white";
      }
      else if (col == Color.YELLOW)
      {
         return "yellow";
      }
      else
      {
         return String.format("rgb(%d,%d,%d)", col.getRed(), col.getGreen(),
           col.getBlue());
      }
   }

   public String getHtmlDimension(TeXDimension dimen)
    throws IOException
   {
      float value = dimen.getValue();
      TeXUnit unit = dimen.getUnit();

      if (unit instanceof FixedUnit)
      {
         int id = ((FixedUnit)unit).getId();

         switch (id)
         {
            case FixedUnit.UNIT_IN:
            case FixedUnit.UNIT_CM:
            case FixedUnit.UNIT_MM:
              return String.format("%f%s", value, FixedUnit.UNIT_NAMES[id]);
            case FixedUnit.UNIT_BP:
              return String.format("%fpt", value);
         }
      }
      else if (unit == TeXUnit.EM)
      {
         return String.format("%fem", value);
      }
      else if (unit == TeXUnit.EX)
      {
         return String.format("%fex", value);
      }
      else if (unit instanceof PercentUnit)
      {
         return String.format("%f%%", value*100);
      }

      return String.format("%fpt", unit.toUnit(getParser(), value, TeXUnit.BP));
   }

   public String getUri(Path path)
   {
      if (path.isAbsolute())
      {
         return path.toUri().toString();
      }

      String str;
      int n = path.getNameCount();

      if (n == 1)
      {
         str = path.toString();
      }
      else
      {
         StringBuilder builder = new StringBuilder(path.getName(0).toString());

         for (int i = 1; i < n; i++)
         {
            builder.append('/');
            builder.append(path.getName(i).toString());
         }

         str = builder.toString();
      }

      try
      {
         return (new URI(str)).toString();
      }
      catch (URISyntaxException e)
      {
         getParser().error(e);
         return str;
      }
   }

   public void startColor(Color color, boolean isForeground)
     throws IOException
   {
      if (isInDocEnv())
      {
         writeliteral("<span style=\"");

         if (isForeground)
         {
            writeliteral("color: ");
         }
         else
         {
            writeliteral("background-color: ");
         }

         writeliteral(String.format(" %s\">", getHtmlColor(color)));
      }
   }

   public void endColor(boolean isForeground)
     throws IOException
   {
      if (isInDocEnv())
      {
         writeliteral("</span>");
      }
   }

   protected String getElementTag(FrameBox fbox)
   {
      TeXFontText font = fbox.getTextFont();

      if (fbox.isInLine() && !fbox.isMultiLine())
      {
         if (font != null && font.getFamily() == TeXFontFamily.VERB)
         {
            return "code";
         }
         else if (font != null && font.getWeight() == TeXFontWeight.STRONG)
         {
            return "strong";
         }
         else if (font != null && font.getShape() == TeXFontShape.EM)
         {
            return "em";
         }
         else
         {
            return "span";
         }
      }
      else
      {
         if (font != null && font.getFamily() == TeXFontFamily.VERB)
         {
            return "pre";
         }
         else
         {
            return "div";
         }
      }
   }

   @Override
   public void declareFrameBox(FrameBox fbox, boolean isChangeable)
   {
      super.declareFrameBox(fbox, isChangeable);

      if (!fbox.isStyleChangeable())
      {
         try
         {
            String specs = getFrameBoxSpecs(fbox);

            String css = String.format("%s.%s {%s}", getElementTag(fbox),
                 fbox.getId(), specs);

            if (isInDocEnv())
            {
               writeliteral(String.format("<style>%s</style>", css));
            }
            else
            {
               addCssStyle(css);
            }
         }
         catch (IOException e)
         {
            getParser().warning(e);
         }
      }
   }

   protected String getFrameBoxSpecs(FrameBox fbox)
    throws IOException
   {
      boolean isInlineBlock = fbox.isInLine() && fbox.isMultiLine();

      StringBuilder builder = new StringBuilder();

      String tag = getElementTag(fbox);

      TeXFontText font = fbox.getTextFont();

      if (font != null)
      {
         builder.append(font.getCss(getParser()));
      }

      switch (fbox.getFloatStyle())
      {
         case LEFT:
            builder.append("float: left; ");
         break;
         case RIGHT:
            builder.append("float: right; ");
         break;
      }

      switch (fbox.getHAlign())
      {
         case LEFT:
            builder.append("text-align: left; ");
         break;
         case CENTER:
            builder.append("text-align: center; ");
         break;
         case RIGHT:
            builder.append("text-align: right; ");
         break;
      }

      switch (fbox.getVAlign())
      {
         case TOP:
            builder.append("vertical-align: top; ");
         break;
         case MIDDLE:
            builder.append("vertical-align: middle; ");
         break;
         case BOTTOM:
            builder.append("vertical-align: bottom; ");
         break;
         case BASE:
            builder.append("vertical-align: base; ");
         break;
      }

      switch (fbox.getStyle())
      {
         case NONE:
           builder.append("border-style: none; ");
         break;
         case SOLID:
           builder.append("border-style: solid; ");
         break;
         case DOUBLE:
           builder.append("border-style: double; ");
         break;
         case DOTTED:
           builder.append("border-style: dotted; ");
         break;
         case DASHED:
           builder.append("border-style: dashed; ");
         break;
         case GROOVE:
           builder.append("border-style: groove; ");
         break;
         case RIDGE:
           builder.append("border-style: ridge; ");
         break;
         case INSET:
           builder.append("border-style: inset; ");
         break;
         case OUTSET:
           builder.append("border-style: outset; ");
         break;
      }

      Color col = fbox.getBorderColor(getParser());

      if (col != null)
      {
         builder.append(String.format("border-color: %s; ", getHtmlColor(col)));
      }

      if (fbox.getStyle() != BorderStyle.NONE)
      {
         TeXDimension borderwidth = fbox.getBorderWidth(getParser());

         if (borderwidth != null)
         {
            builder.append(String.format("border-width: %s; ", 
               getHtmlDimension(borderwidth)));
         }
      }

      TeXDimension innersep = fbox.getInnerMargin(getParser());

      if (innersep != null)
      {
         builder.append(String.format("padding: %s; ", getHtmlDimension(innersep)));
      }

      TeXDimension margin = fbox.getOuterMarginLeft(getParser());

      if (margin != null)
      {
         builder.append(String.format("margin-left: %s; ", getHtmlDimension(margin)));
      }

      margin = fbox.getOuterMarginRight(getParser());

      if (margin != null)
      {
         builder.append(String.format("margin-right: %s; ", getHtmlDimension(margin)));
      }

      margin = fbox.getOuterMarginTop(getParser());

      if (margin != null)
      {
         builder.append(String.format("margin-top: %s; ", getHtmlDimension(margin)));
      }

      margin = fbox.getOuterMarginBottom(getParser());

      if (margin != null)
      {
         builder.append(String.format("margin-bottom: %s; ", getHtmlDimension(margin)));
      }

      TeXDimension radius = fbox.getBorderRadius(getParser());

      if (radius != null)
      {
         builder.append(String.format("border-radius: %s;", getHtmlDimension(radius)));
      }

      col = fbox.getForegroundColor(getParser());

      if (col != null)
      {
         builder.append(String.format("color: %s; ", getHtmlColor(col)));
      }

      col = fbox.getBackgroundColor(getParser());

      if (col != null)
      {
         builder.append(String.format("background-color: %s; ",
           getHtmlColor(col)));
      }

      TeXDimension width = fbox.getWidth(getParser());

      if (width != null)
      {
         if (fbox.isInLine())
         {
            isInlineBlock = true;
         }

         builder.append(String.format("width: %s; ", getHtmlDimension(width)));

         if (fbox.isMultiLine())
         {
            builder.append("overflow: auto; ");
         }
      }

      TeXDimension height = fbox.getHeight(getParser());

      if (height != null)
      {
         if (fbox.isInLine())
         {
            isInlineBlock = true;
         }

         builder.append(String.format("height: %s; ", 
           getHtmlDimension(height)));
      }

      Angle angle = fbox.getAngle(parser);

      if (angle != null)
      {
         String transform = String.format("rotate(%fdeg)", angle.toDegrees());

         builder.append(String.format("transform: %s; ", transform));
         builder.append(String.format("-ms-transform: %s; ", transform));
         builder.append(String.format("-webkit-transform: %s; ", transform));

         isInlineBlock = true;
      }

      if (isInlineBlock)
      {
         builder.append("display: inline-block; ");
      }

      return builder.toString();
   }

   public void startFrameBox(FrameBox fbox)
    throws IOException
   {
      String tag = getElementTag(fbox);

      writeliteral(String.format("<%s ", tag));

      if (getDeclaredFrameBox(fbox.getId()) == null)
      {
         String specs = getFrameBoxSpecs(fbox);

         String style = defaultStyles.get(specs);

         if (style == null)
         {
            writeliteral(String.format("style=\"%s\"", specs));
         }
         else if (!fbox.isInLine() && fbox.isMultiLine())
         {
            writeliteral(String.format("class=\"%s clearfix\"", style));
         }
         else
         {
            writeliteral(String.format("class=\"%s\"", style));
         }
      }
      else
      {
         writeliteral(String.format("class=\"%s\"", fbox.getId()));
      }

      writeliteral(">");
   }

   public void endFrameBox(FrameBox fbox)
    throws IOException
   {
      String tag = getElementTag(fbox);

      writeliteral(String.format("</%s>", tag));

      if (fbox.getFloatStyle() != FloatBoxStyle.NONE)
      {
         writeliteral("<span class=\"clearfix\"></span>");
      }

      if (tag.equals("div") && !fbox.isInLine() 
           && !getParser().getSettings().inVerb())
      {
         writeliteralln(String.format("<!-- end of %s -->", fbox.getId()));
      }
   }

   public void startTheorem(String name) throws IOException
   {
      writeliteral("<div class=\"");
      writeliteral(name);
      writeliteral("\">");
   }

   public void endTheorem(String name) throws IOException
   {
      writeliteral(String.format("</div><!-- end of %s -->", name));
   }

   public HtmlTag createLinkBox(String label)
   {
      ControlSequence cs = parser.getControlSequence("TeXParserLibLinkName");
      String text = "[link]";

      if (cs instanceof Expandable && cs.canExpand())
      {
         try
         {
            text = parser.expandToString(cs, parser);
         }
         catch (IOException e)
         {
            getParser().error(e);
         }
      }

      return new HtmlTag(String.format(
       "<div class=\"labellink\"><a href=\"#%s\">%s</a></div>",
         label, text));
   }

   public void startSection(boolean isNumbered, String tag, String name,
     String id, TeXObjectList stack)
    throws IOException
   {
      if (currentSection != null)
      {
         writeliteral(String.format("%n</section><!-- end of section %s -->%n", currentSection));
      }

      if (currentNode != null && id != null)
      {
         DivisionNode nextNode = divisionMap.get(id);
         DivisionInfo nextData = null;

         if (nextNode != null)
         {
            nextData = nextNode.getData();
         }

         if (nextNode != null && nextData != null)
         {
            File file = nextNode.getFile();

            if (file != null && !file.equals(currentNode.getFile()))
            {
               endDivisionFile(stack);

               currentNode = nextNode;

               writer.close();

               writer = new PrintWriter(Files.newBufferedWriter(file.toPath(),
                 htmlCharSet));

               setWriter(writer);

               startDivisionFile(stack);
            }
            else
            {
               currentNode = nextNode;
            }
         }
      }

      if (id == null)
      {
         currentSection = tag+"-"+name;

         writeliteral(String.format("%n<section><!-- start of section %s -->", currentSection));
      }
      else
      {
         currentSection = id;

         writeliteral(String.format("%n<section id=\"%s\"><!-- start of section %s -->",
           id, currentSection));
      }

      ControlSequence cs = parser.getControlSequence("TeXParserLibToTopName");
      String text = "[top]";

      if (cs != null)
      {
         text = parser.expandToString(cs, stack);
      }

      writeliteral(String.format(
       "<div class=\"tomain\"><a href=\"#main\">%s</a></div>",
       text));
   }

   @Override
   public TeXObject getAnchor(String anchorName)
   {
      TeXObjectList stack = createStack();
      StartElement startElem = new StartElement("a");
      startElem.putAttribute("id", anchorName);

      stack.add(startElem);
      stack.add(new EndElement("a"));

      return stack;
   }

   protected void addDefaultTabularStyles()
    throws IOException
   {
      Register reg = parser.getSettings().getRegister("tabcolsep");

      if (reg == null || !(reg instanceof DimenRegister))
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_DIMEN_EXPECTED,
           String.format("%stabcolsep",
             new String(Character.toChars(parser.getEscChar()))));
      }

      TeXDimension dim = ((DimenRegister)reg).getDimension();

      if (dim instanceof TeXGlue)
      {
         dim = ((TeXGlue)dim).getFixed();
      }

      String colSep = dim.format();

      HashMap<String,String> css = createCellCss("center");
      addDefaultStyle("cell-noborder-nosep-c", css);

      css = createCellCss("right");
      addDefaultStyle("cell-noborder-nosep-r", css);

      css = createCellCss("left");
      addDefaultStyle("cell-noborder-nosep-l", css);

      css = createCellCss("center");
      css.put("padding-right", colSep);

      addDefaultStyle("cell-noborder-rightsep-c", css);

      css = createCellCss("center");
      css.put("padding-left", colSep);

      addDefaultStyle("cell-noborder-rightsep-c", css);

      css = createCellCss("center");
      css.put("padding-left", colSep);
      css.put("padding-right", colSep);

      addDefaultStyle("cell-noborder-bothsep-c", css);

      css = createCellCss("left");
      css.put("padding-right", colSep);

      addDefaultStyle("cell-noborder-rightsep-l", css);

      css = createCellCss("left");
      css.put("padding-left", colSep);

      addDefaultStyle("cell-noborder-rightsep-l", css);

      css = createCellCss("left");
      css.put("padding-left", colSep);
      css.put("padding-right", colSep);

      addDefaultStyle("cell-noborder-bothsep-l", css);

      css = createCellCss("right");
      css.put("padding-right", colSep);

      addDefaultStyle("cell-noborder-rightsep-r", css);

      css = createCellCss("right");
      css.put("padding-left", colSep);

      addDefaultStyle("cell-noborder-leftsep-r", css);

      css = createCellCss("right");
      css.put("padding-left", colSep);
      css.put("padding-right", colSep);

      addDefaultStyle("cell-noborder-bothsep-r", css);

      css = createCellCss("left");
      css.put("padding-left", colSep);
      css.put("padding-right", colSep);
      css.put("width", "50.000000%");

      addDefaultStyle("cell-noborder-bothsep-halfwidth", css);

      css = createCellCss("left");
      css.put("padding-left", colSep);
      css.put("padding-right", colSep);
      css.put("width", "25.000000%");

      addDefaultStyle("cell-noborder-bothsep-quarterwidth", css);

      css = createCellCss("left");
      css.put("padding-left", colSep);
      css.put("padding-right", colSep);
      css.put("width", "75.000000%");

      addDefaultStyle("cell-noborder-bothsep-threequarterwidth", css);

      css = createCellCss("left");
      css.put("padding-left", colSep);
      css.put("padding-right", colSep);
      css.put("width", "80.000000%");

      addDefaultStyle("cell-noborder-bothsep-eightypc", css);

   }

   protected HashMap<String,String> createCellCss(String textAlign)
    throws IOException
   {
      HashMap<String,String> css = new HashMap<String,String>();

      css.put("padding-left", "0px");
      css.put("padding-right", "0px");

      css.put("text-align", textAlign);

      css.put("border-left", "none");
      css.put("border-right", "none");
      css.put("border-top", "none");

      return css;
   }

   public void addDefaultStyle(String name, HashMap<String,String> styleAttrs)
    throws IOException
   {
      if (defaultStyleMaps==null)
      {
         defaultStyleMaps = new HashMap<HashMap<String,String>,String>();
      }

      defaultStyleMaps.put(styleAttrs, name);

      String specs = cssAttributesToString(styleAttrs);

      defaultStyles.put(specs, name);

      if (isInDocEnv())
      {
         writeliteral(String.format("<style>%s: {%s}</style>", name, specs));
      }
   }

   public String getCssClass(HashMap<String,String> css)
   {
      if (css == null || css.isEmpty())
      {
         return null;
      }

      if (defaultStyleMaps != null)
      {
         return defaultStyleMaps.get(css);
      }

      return null;
   }

   public String getStyleOrClass(HashMap<String,String> css)
   {
      if (css == null || css.isEmpty())
      {
         return "";
      }

      String name = getCssClass(css);

      if (name != null)
      {
         return String.format(" class=\"%s\"", name);
      }

      return String.format(" style=\"%s\"", cssAttributesToString(css));
   }

   public String cssAttributesToString(HashMap<String,String> css)
   {
      StringBuilder builder = new StringBuilder();

      for (Iterator<String> it=css.keySet().iterator(); it.hasNext(); )
      {
         String key = it.next();
         builder.append(String.format("%s: %s; ", key, css.get(key)));
      }

      return builder.toString();
   }

   private Vector<String> styCs;

   private int indexLoc = 0;

   private Writer writer, currentWriter;

   private StringWriter footnoteWriter = null;

   protected boolean inPreamble = false;

   private Charset htmlCharSet = null;

   private TeXApp texApp;

   private Path outPath, basePath;

   private boolean useMathJax=true;

   private boolean unicodeScriptSupport=true;

   private boolean useHtmlEntities = false;

   protected String baseName;
   protected String suffix = "html";

   protected boolean splitUseBaseNamePrefix = false;

   private Vector<String> extraHead=null;

   private Vector<String> extraCssStyles = new Vector<String>();

   private HashMap<String,TeXObject> internalReferences;

   private HashMap<String,String> defaultStyles;

   private HashMap<HashMap<String,String>,String> defaultStyleMaps;

   private String currentSection = null;

   private DivisionNode currentNode = null;

   private HashMap<String,DivisionNode> divisionMap;

   private int splitLevel = 0;

   protected String generator = "TeX Parser Library";

   private Stack<TrivListDec> trivListStack = new Stack<TrivListDec>();

   public static final String MIME_TYPE_PDF = "application/pdf";
   public static final String MIME_TYPE_PNG = "image/png";
   public static final String MIME_TYPE_JPEG = "image/jpeg";
}
