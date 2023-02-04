/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
      this(app, true, null, null, false, null, false);
   }

   public L2HConverter(TeXApp app, Vector<AuxData> auxData)
   {
      this(app, true, null, auxData, false, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, boolean parseAux)
   {
      this(app, useMathJax, null, null, parseAux, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, Vector<AuxData> auxData)
   {
      this(app, useMathJax, null, auxData, false, null, false);
   }

   public L2HConverter(TeXApp app, File outDir)
   {
      this(app, true, outDir, null, false, null, false);
   }

   public L2HConverter(TeXApp app, File outDir, Vector<AuxData> auxData)
   {
      this(app, true, outDir, auxData, false, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir)
   {
      this(app, useMathJax, outDir, null, false, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     Vector<AuxData> auxData)
   {
      this(app, useMathJax, outDir, auxData, false, null, false);
   }

   public L2HConverter(TeXApp app, boolean useMathJax, File outDir,
     boolean parseAux)
   {
      this(app, useMathJax, outDir, null, parseAux, null, false);
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
     Vector<AuxData> auxData, boolean parseAux, Charset outCharSet, boolean parsePackages)
   {
      super(null, auxData, parseAux, parsePackages);
      this.texApp = app;
      this.outPath = (outDir == null ? null : outDir.toPath());
      this.htmlCharSet = outCharSet;

      this.styCs = new Vector<String>();
      defaultStyles = new HashMap<String,String>();
      internalReferences = new HashMap<String,TeXObject>();

      setWriteable(this);
      setUseMathJax(useMathJax);

      setImageExtensions("svg", "SVG", "png", "PNG", "jpg", "JPG", "jpeg", "JPEG",
        "gif", "GIF", "pdf", "PDF");
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

      writeliteral("<script type=\"text/javascript\" src=");
      writeliteralln(
       "\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\">");
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
      addDefaultTabularStyles();

      writeliteralln("#main {margin-left: 5%; margin-right: 15%}");
      writeliteralln("div.tomain {position: absolute; left: 0pt; width: 5%; text-align: right; font-size: x-small;}");
      writeliteralln("div.tomain a {text-decoration: none;}");
      writeliteralln("div.labellink {display: inline; font-size: x-small; margin-left: 1em; margin-right: 1em;}");
      writeliteralln("div.marginleft {position: absolute; left: 0pt; width: 5%;}");
      writeliteralln("div.marginright {position: absolute; right: 0pt; width: 15%;}");

      writeliteralln("div.displaymath { display: block; text-align: center; }");
      writeliteralln("span.eqno { float: right; }");
      writeliteralln("div.table { display: block; text-align: center; }");

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
       htmlCharSet == null ? 
         Charset.defaultCharset().name() : 
         htmlCharSet.name()));

      ControlSequence cs = parser.getControlSequence("TeXParserLibGeneratorName");
      String generator = "TeX Parser Library";

      if (cs != null)
      {
         generator = parser.expandToString(cs, stack);
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

   @Override
   public void beginDocument(TeXObjectList stack)
     throws IOException
   {
      inPreamble = false;

      TeXObject cs = getControlSequence("@title");

      if (!(cs instanceof Undefined) && !cs.isEmpty())
      {
         TeXObject title = TeXParserUtils.expandFully(cs, parser, stack);

         writeliteral("<title>");
         write(title.purified());
         writeliteralln("</title>");
      }
      else
      {
         writeliteralln("<!-- no title found -->");
      }

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

      super.beginDocument(stack);

      writeliteralln("<div id=\"main\">");

      getParser().getSettings().setCharMapMode(TeXSettings.CHAR_MAP_ON);
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
      }
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

         String baseName = file.getName();

         int idx = baseName.lastIndexOf(".");

         if (idx > -1)
         {
            baseName = baseName.substring(0,idx);
         }

         File outFile = new File(outPath.toFile(), baseName+"."+getSuffix());

         getParser().message(TeXApp.MESSAGE_WRITING, outFile);

         if (htmlCharSet == null)
         {
            writer = new PrintWriter(outFile);
         }
         else
         {
            getParser().message(TeXApp.MESSAGE_ENCODING, htmlCharSet);

            writer = new PrintWriter(outFile, htmlCharSet.name());
         }
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

   private String suffix = "html";

   private Vector<String> extraHead=null;

   private Vector<String> extraCssStyles = new Vector<String>();

   private HashMap<String,TeXObject> internalReferences;

   private HashMap<String,String> defaultStyles;

   private HashMap<HashMap<String,String>,String> defaultStyleMaps;

   private String currentSection = null;

   private Stack<TrivListDec> trivListStack = new Stack<TrivListDec>();

   public static final String MIME_TYPE_PDF = "application/pdf";
   public static final String MIME_TYPE_PNG = "image/png";
   public static final String MIME_TYPE_JPEG = "image/jpeg";
}
