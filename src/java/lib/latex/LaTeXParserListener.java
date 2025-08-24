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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Vector;
import java.util.Stack;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.file.Path;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.auxfile.*;

import com.dickimawbooks.texparserlib.latex.amsmath.*;
import com.dickimawbooks.texparserlib.latex.booktabs.*;
import com.dickimawbooks.texparserlib.latex.bpchem.*;
import com.dickimawbooks.texparserlib.latex.color.*;
import com.dickimawbooks.texparserlib.latex.datatool.*;
import com.dickimawbooks.texparserlib.latex.etoolbox.*;
import com.dickimawbooks.texparserlib.latex.fontawesome.*;
import com.dickimawbooks.texparserlib.latex.fontenc.*;
import com.dickimawbooks.texparserlib.latex.fourier.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;
import com.dickimawbooks.texparserlib.latex.graphics.*;
import com.dickimawbooks.texparserlib.latex.hyperref.*;
import com.dickimawbooks.texparserlib.latex.ifthen.*;
import com.dickimawbooks.texparserlib.latex.inputenc.*;
import com.dickimawbooks.texparserlib.latex.jmlr.*;
import com.dickimawbooks.texparserlib.latex.keyval.*;
import com.dickimawbooks.texparserlib.latex.lipsum.*;
import com.dickimawbooks.texparserlib.latex.latex3.*;
import com.dickimawbooks.texparserlib.latex.mfirstuc.*;
import com.dickimawbooks.texparserlib.latex.mhchem.*;
import com.dickimawbooks.texparserlib.latex.mnsymbol.*;
import com.dickimawbooks.texparserlib.latex.natbib.*;
import com.dickimawbooks.texparserlib.latex.nlctdoc.*;
import com.dickimawbooks.texparserlib.latex.pifont.*;
import com.dickimawbooks.texparserlib.latex.probsoln.*;
import com.dickimawbooks.texparserlib.latex.shortvrb.*;
import com.dickimawbooks.texparserlib.latex.siunitx.*;
import com.dickimawbooks.texparserlib.latex.stix.*;
import com.dickimawbooks.texparserlib.latex.tcilatex.*;
import com.dickimawbooks.texparserlib.latex.textcase.*;
import com.dickimawbooks.texparserlib.latex.textcomp.*;
import com.dickimawbooks.texparserlib.latex.tipa.*;
import com.dickimawbooks.texparserlib.latex.twemojis.*;
import com.dickimawbooks.texparserlib.latex.upgreek.*;
import com.dickimawbooks.texparserlib.latex.wasysym.*;
import com.dickimawbooks.texparserlib.latex.xfor.*;
import com.dickimawbooks.texparserlib.latex.xspace.*;

public abstract class LaTeXParserListener extends DefaultTeXParserListener
{
   public LaTeXParserListener(Writeable writeable)
   {
      this(writeable, (AuxParser)null, false);
   }

   public LaTeXParserListener(Writeable writeable, boolean parseAux)
   {
      this(writeable, (AuxParser)null, true);
   }

   public LaTeXParserListener(Writeable writeable, AuxParser auxParser)
   {
      this(writeable, auxParser, false);
   }

   public LaTeXParserListener(Writeable writeable, AuxParser auxParser, boolean parseAux)
   {
      this(writeable, auxParser, parseAux, false);
   }

   public LaTeXParserListener(Writeable writeable, Vector<AuxData> auxData)
   {
      this(writeable, auxData, false);
   }

   public LaTeXParserListener(Writeable writeable, Vector<AuxData> auxData, boolean parseAux)
   {
      this(writeable, auxData, parseAux, false);
   }

   public LaTeXParserListener(Writeable writeable, Vector<AuxData> auxData, 
     boolean parseAux, boolean parsePackages)
   {
      super(writeable);
      setAuxData(auxData);
      setParseAuxEnabled(parseAux);
      counters = new Hashtable<String,Vector<String>>();
      indexes = new Hashtable<String,IndexRoot>();
      this.parsePackages = parsePackages;

      footnotes = new TeXObjectList();

      loadedPackages = new Vector<LaTeXFile>();
      verbEnv = new Vector<String>();
   }

   public LaTeXParserListener(Writeable writeable, AuxParser auxParser, 
     boolean parseAux, boolean parsePackages)
   {
      super(writeable);

      if (auxParser != null)
      {
         setAuxData(auxParser);
      }

      setParseAuxEnabled(parseAux);
      counters = new Hashtable<String,Vector<String>>();
      indexes = new Hashtable<String,IndexRoot>();
      this.parsePackages = parsePackages;

      footnotes = new TeXObjectList();

      loadedPackages = new Vector<LaTeXFile>();
      verbEnv = new Vector<String>();
   }

   public boolean isParseAuxEnabled()
   {
      return parseAux;
   }

   public void setParseAuxEnabled(boolean parseAux)
   {
      this.parseAux = parseAux;
   }

   public boolean isSaveDivisionsEnabled()
   {
      return saveDivisions;
   }

   // only relevant if parseAux = true
   public void enableSaveDivisions(boolean enable)
   {
      saveDivisions = enable;
   }

   public TeXObject getAnchor(String anchorName)
   {
      return null;
   }

   public CrossRefInfo getCrossRefInfo(String id)
   {
      CrossRefInfo info = null;

      if (labelData != null)
      {
         info = labelData.get(id);
      }

      if (info == null && citeData != null)
      {
         info = citeData.get(id);
      }

      if (info == null && linkLabelMap != null)
      {
         String label = linkLabelMap.get(id);

         if (label != null)
         {
            if (labelData != null)
            {
               info = labelData.get(label);
            }

            if (info == null && citeData != null)
            {
               info = citeData.get(label);
            }
         }
      }

      return info;
   }

   public CiteInfo getCiteInfo(String label)
   {
      return citeData == null ? null : citeData.get(label);
   }

   public TeXObject getCitation(TeXObject label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      if (citeData != null)
      {
         CiteInfo info = citeData.get(getParser().expandToString(label, null));

         if (info == null)
         {
            return createUnknownReference(label);
         }
         else
         {
            return info.getReference();
         }
      }

      return AuxData.getCitation(auxData, getParser(), label);
   }

   public TeXObject getCitation(String label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      if (citeData != null)
      {
         CiteInfo info = citeData.get(label);

         if (info == null)
         {
            return createUnknownReference(label);
         }
         else
         {
            return info.getReference();
         }
      }

      return AuxData.getCitation(auxData, getParser(), label);
   }

   public LabelInfo getLabelInfo(String label)
   {
      return labelData == null ? null : labelData.get(label);
   }

   public TeXObject getReference(TeXObject label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      if (labelData != null)
      {
         LabelInfo info = labelData.get(parser.expandToString(label, null));

         if (info == null)
         {
            return createUnknownReference(label);
         }
         else
         {
            return info.getReference();
         }
      }

      return AuxData.getReference(auxData, getParser(), label);
   }

   public TeXObject getReference(String label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      if (labelData != null)
      {
         LabelInfo info = labelData.get(label);

         if (info == null)
         {
            return createUnknownReference(label);
         }
         else
         {
            return info.getReference();
         }
      }

      return AuxData.getReference(auxData, getParser(), label);
   }

   public TeXObject getPageReference(TeXObject label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      if (labelData != null)
      {
         LabelInfo info = labelData.get(parser.expandToString(label, null));

         if (info == null)
         {
            return createUnknownReference(label);
         }
         else
         {
            return info.getPage();
         }
      }

      return AuxData.getPageReference(auxData, getParser(), label);
   }

   public TeXObject getPageReference(String label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      if (labelData != null)
      {
         LabelInfo info = labelData.get(label);

         if (info == null)
         {
            return createUnknownReference(label);
         }
         else
         {
            return info.getPage();
         }
      }

      return AuxData.getPageReference(auxData, getParser(), label);
   }

   public TeXObject getNameReference(TeXObject label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      if (labelData != null)
      {
         LabelInfo info = labelData.get(parser.expandToString(label, null));

         if (info == null)
         {
            return createUnknownReference(label);
         }
         else
         {
            return info.getTitle();
         }
      }

      return AuxData.getNameReference(auxData, getParser(), label);
   }

   public TeXObject getNameReference(String label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      if (labelData != null)
      {
         LabelInfo info = labelData.get(label);

         if (info == null)
         {
            return createUnknownReference(label);
         }
         else
         {
            return info.getTitle();
         }
      }

      return AuxData.getNameReference(auxData, getParser(), label);
   }

   public TeXObject getHyperReference(TeXObject label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      if (labelData != null)
      {
         LabelInfo info = labelData.get(parser.expandToString(label, null));

         if (info == null)
         {
            return createUnknownReference(label);
         }
         else
         {
            return createString(info.getTarget());
         }
      }

      return AuxData.getHyperReference(auxData, getParser(), label);
   }

   public TeXObject getHyperReference(String label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      if (labelData != null)
      {
         LabelInfo info = labelData.get(label);

         if (info == null)
         {
            return createUnknownReference(label);
         }
         else
         {
            return createString(info.getTarget());
         }
      }

      return AuxData.getHyperReference(auxData, getParser(), label);
   }

   @Deprecated
   public TeXObject getLabelForLink(TeXObject link)
     throws IOException
   {
      if (auxData == null)
      {
         return null;
      }

      if (labelData != null)
      {
         String linkName = parser.expandToString(link, null);

         if (linkLabelMap == null)
         {
            createLinkLabelMap();
         }

         String label = linkLabelMap.get(linkName);

         return label == null ? null : createString(label);
      }

      return AuxData.getLabelForLink(auxData, getParser(), link);
   }

   public TeXObject getLabelForLink(String link)
     throws IOException
   {
      if (auxData == null)
      {
         return null;
      }

      if (labelData != null)
      {
         if (linkLabelMap == null)
         {
            createLinkLabelMap();
         }

         String label = linkLabelMap.get(link);

         return label == null ? null : createString(label);
      }

      return AuxData.getLabelForLink(auxData, getParser(), link);
   }

   public String getStringLabelForLink(String link)
     throws IOException
   {
      if (auxData == null)
      {
         return null;
      }

      if (labelData != null)
      {
         if (linkLabelMap == null)
         {
            createLinkLabelMap();
         }

         return linkLabelMap.get(link);
      }

      return AuxData.getLabelForLink(auxData, getParser(), link).toString(getParser());
   }

   protected void createLinkLabelMap()
   {
      linkLabelMap = new HashMap<String,String>();

      for (String label : labelData.keySet())
      {
         LabelInfo info = labelData.get(label);

         String target = info.getTarget();

         if (target != null)
         {
            linkLabelMap.put(target, label);
         }
      }
   }

   public void addLabel(LabelInfo labelInfo)
   {
      if (labelData == null)
      {
         labelData = new HashMap<String,LabelInfo>();
      }

      labelData.put(labelInfo.getLabel(), labelInfo);

      if (linkLabelMap != null)
      {
         String target = labelInfo.getTarget();

         if (target != null)
         {
            linkLabelMap.put(target, labelInfo.getLabel());
         }
      }
   }

   public TeXObject getDivider(String name)
   {
      return getControlSequence("hrulefill");
   }

   public void addVerbEnv(String name)
   {
      verbEnv.add(name);
   }

   public boolean isVerbEnv(String name)
   {
      return verbEnv.contains(name);
   }

   public boolean containsVerbatim(TeXObject object)
   {
      if (object instanceof VerbChar)
      {
         return true;
      }
      else if (object instanceof ControlSequence)
      {
         return getParser().isVerbCommand(((ControlSequence)object).getName());
      }
      else if (object instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)object;

         for (int i = 0; i < list.size(); i++)
         {
            object = list.get(i);

            if (object instanceof TeXObjectList)
            {
               if (containsVerbatim(object))
               {
                  return true;
               }
            }
            else if (object instanceof ControlSequence)
            {
               ControlSequence cs = (ControlSequence)object;
               String csName = cs.getName();

               if (getParser().isVerbCommand(csName))
               {
                  return true;
               }

               if (csName.equals("begin"))
               {
                  i++;
                  object = null;

                  while (i < list.size())
                  {
                     TeXObject nextObj = list.get(i);
                     i++;

                     if (!(nextObj instanceof Ignoreable))
                     {
                        object = nextObj;
                        break;
                     }
                  }

                  if (object == null)
                  {
                     return false;
                  }

                  if (object instanceof Group)
                  {
                     object = ((Group)object).toList();
                  }
                  else if (object instanceof BgChar)
                  {
                     TeXObjectList grp = new TeXObjectList();
                     i++;

                     while (i < list.size())
                     {
                        object = list.get(i);
                        i++;

                        if (object instanceof EgChar)
                        {
                           break;
                        }

                        grp.add(object);
                     }

                     object = grp;
                  }

                  String envName = object.toString(getParser());

                  if (isVerbEnv(envName))
                  {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public void beginVerbatim() throws IOException
   {
   }

   public void endVerbatim() throws IOException
   {
   }

   protected void addPredefined()
   {
      super.addPredefined();

      addVerbEnv("verbatim");
      addVerbEnv("verbatim*");
      addVerbEnv("filecontents");
      addVerbEnv("filecontents*");

      parser.putControlSequence(new Begin());
      parser.putControlSequence(new End());

      parser.putControlSequence(new DocumentClass());
      parser.putControlSequence(new UsePackage());

      if (parsePackages)
      {
         parser.putControlSequence(new LoadDocumentClass());
         parser.putControlSequence(
            new LoadDocumentClass("LoadClassWithOptions", true));
         parser.putControlSequence(new UsePackage("RequirePackage", false));
         parser.putControlSequence(
           new UsePackage("RequirePackageWithOptions", true));
         parser.putControlSequence(new PassOptionsToPackage());
         parser.putControlSequence(new PassOptionsToPackage(
            "PassOptionsToClass", "cls"));
         parser.putControlSequence(new ProvidesFile());
         parser.putControlSequence(new ProvidesPackage());
         parser.putControlSequence(new ProvidesPackage("ProvidesClass"));
         parser.putControlSequence(new DeclareOption());
         parser.putControlSequence(new ProcessOptions());
         parser.putControlSequence(new ExecuteOptions());
         parser.putControlSequence(new GobbleOpt("DeclareRelease", 0, 3));
         parser.putControlSequence(new GobbleOpt("DeclareCurrentRelease", 0, 2));
         parser.putControlSequence(new GobbleOptMandOpt("NeedsTeXFormat", 0, 1, 1));

         parser.putControlSequence(new AddToHook("AtBeginDocument",
           "@begindocumenthook"));
         parser.putControlSequence(new AddToHook("AtEndDocument",
           "@enddocumenthook"));

         parser.putControlSequence(new AtIfPackageLoaded());
         parser.putControlSequence(new AtIfClassLoaded());

         parser.putControlSequence(new PackageError());
         parser.putControlSequence(new PackageError(
          "PackageErrorNoLine", LaTeXSyntaxException.PACKAGE_ERROR));

         parser.putControlSequence(new PackageError(
          "ClassError", LaTeXSyntaxException.CLASS_ERROR));
         parser.putControlSequence(new PackageError(
          "ClassErrorNoLine", LaTeXSyntaxException.CLASS_ERROR));
      }

      parser.putControlSequence(new AtCtrErr());

      parser.putControlSequence(new NewCommand());
      parser.putControlSequence(new NewCommand("renewcommand",
        Overwrite.FORCE));
      parser.putControlSequence(new NewCommand("providecommand",
        Overwrite.SKIP));

      // make \DeclareRobustCommand behave like \providecommand
      // (this library has different expansion rules to TeX)
      parser.putControlSequence(new NewCommand("DeclareRobustCommand",
        Overwrite.SKIP));

      parser.putControlSequence(new NewEnvironment());
      parser.putControlSequence(new NewEnvironment("renewenvironment",
        Overwrite.FORCE));

      parser.putControlSequence(new Label());
      parser.putControlSequence(new Ref());
      parser.putControlSequence(new PageRef());
      parser.putControlSequence(new NameRef());
      parser.putControlSequence(new Input());
      parser.putControlSequence(new Input("@input", Input.NOT_FOUND_ACTION_WARN));
      parser.putControlSequence(new InputIfFileExists());
      parser.putControlSequence(new IfFileExists());
      parser.putControlSequence(new MakeAtLetter());
      parser.putControlSequence(new MakeAtOther());
      parser.putControlSequence(new Ignorespaces());
      parser.putControlSequence(new NoBreakSpace());
      parser.putControlSequence(new LaTeXSkip("hspace", Direction.HORIZONTAL));
      parser.putControlSequence(new LaTeXSkip("vspace", Direction.VERTICAL));
      parser.putControlSequence(new Centerline());
      parser.putControlSequence(new Verb());
      parser.putControlSequence(new Cr("\\"));
      parser.putControlSequence(new Cr("cr"));
      parser.putControlSequence(new TabularNewline());
      parser.putControlSequence(new MultiColumn());
      parser.putControlSequence(new Hline());
      parser.putControlSequence(new Cline());
      parser.putControlSequence(new EnsureMath());
      parser.putControlSequence(new Frac());
      parser.putControlSequence(new GenericCommand("@empty"));
      parser.putControlSequence(new SymbolCs());

      NewIf.createConditional(false, parser, "if@ignore", false);

      parser.putControlSequence(new GenericCommand(true,
       "ignorespacesafterend", null,
       TeXParserUtils.createStack(this, new TeXCsRef("global"),
        new TeXCsRef("let"), new TeXCsRef("if@ignore"), new TeXCsRef("iftrue"))));

      parser.putControlSequence(new TextualContentCommand("p@", "pt"));
      parser.putControlSequence(new TextualContentCommand("@minus", "minus"));
      parser.putControlSequence(new TextualContentCommand("@plus", "plus"));
      parser.putControlSequence(new AtFor());
      parser.putControlSequence(new AtIfNextChar());
      parser.putControlSequence(new AtFirstOfTwo());
      parser.putControlSequence(new AtSecondOfTwo());
      parser.putControlSequence(new AtFirstOfOne());
      parser.putControlSequence(new AtNumberOfNumber("@firstofthree", 1, 3));
      parser.putControlSequence(new AtNumberOfNumber("@secondofthree", 2, 3));
      parser.putControlSequence(new AtNumberOfNumber("@thirdofthree", 3, 3));
      parser.putControlSequence(new AtGobble());
      parser.putControlSequence(new AtGobble("@gobbletwo", 2));
      parser.putControlSequence(new AtGobble("@gobblethree", 3));
      parser.putControlSequence(new AtGobble("@gobblefour", 4));
      parser.putControlSequence(new AtGobble("@gobblefive", 5));
      parser.putControlSequence(new GobbleOpt("@gobble@om", 1, 1));
      parser.putControlSequence(new GobbleOpt("@gobble@som", 1, 1, '*'));
      parser.putControlSequence(new AtNameUse());
      parser.putControlSequence(new IfUndef("@ifundefined", true));

      parser.putControlSequence(new AtAlph("@Alph", AtAlph.UPPER));
      parser.putControlSequence(new AtAlph("@alph", AtAlph.LOWER));
      parser.putControlSequence(new AtRoman("@Roman", AtRoman.UPPER));
      parser.putControlSequence(new AtRoman("@roman", AtRoman.LOWER));

      parser.putControlSequence(new NewCounter());
      parser.putControlSequence(new AddToCounter());
      parser.putControlSequence(new StepCounter());
      parser.putControlSequence(new StepCounter("refstepcounter"));
      parser.putControlSequence(new SetCounter());
      parser.putControlSequence(new Value());
      parser.putControlSequence(new Value("arabic"));
      parser.putControlSequence(new NumberCs("@arabic"));
      parser.putControlSequence(new Roman());
      parser.putControlSequence(new Roman("roman", AtRoman.LOWER));
      parser.putControlSequence(new Alph());
      parser.putControlSequence(new Alph("alph", AtAlph.LOWER));
      parser.putControlSequence(new FnSymbol());

      parser.putControlSequence(new NewLength());
      parser.putControlSequence(new SetLength());

      parser.putControlSequence(new Verbatim());
      parser.putControlSequence(new Verbatim("verbatim*"));
      parser.putControlSequence(new Tabular());
      parser.putControlSequence(new Tabular("array"));
      parser.putControlSequence(new Bibliography());
      parser.putControlSequence(new BibliographyStyle());
      parser.putControlSequence(new BibItem());
      parser.putControlSequence(new Cite());
      parser.putControlSequence(new AddContentsLine());
      parser.putControlSequence(new Uppercase("MakeUppercase"));
      parser.putControlSequence(new Lowercase("MakeLowercase"));
      parser.putControlSequence(new Protect());
      parser.putControlSequence(new Index());
      parser.putControlSequence(new MakeIndex());
      parser.putControlSequence(new Appendix());
      parser.putControlSequence(new Typeout());

      parser.putControlSequence(createTeXParserSection("section"));
      parser.putControlSequence(createTeXParserSection("chapter"));
      parser.putControlSequence(createTeXParserSection("subsection"));
      parser.putControlSequence(createTeXParserSection("subsubsection"));
      parser.putControlSequence(createTeXParserSection("paragraph"));
      parser.putControlSequence(createTeXParserSection("subparagraph"));
      parser.putControlSequence(createTeXParserSection("part"));

      bibliographySection = new TeXObjectList();
      bibliographySection.add(new TeXCsRef("section"));
      bibliographySection.add(getOther('*'));
      bibliographySection.add(new TeXCsRef("refname"));

      parser.putControlSequence(
        new GenericCommand("refname", null, createString("References")));

      parser.putControlSequence(
        new GenericCommand("contentsname", null, createString("Contents")));

      parser.putControlSequence(
        new GenericCommand("abstractname", null, createString("Abstract")));
      parser.putControlSequence(new AbstractDec());

      parser.putControlSequence(new Today());
      parser.putControlSequence(new TextualContentCommand("@title", ""));
      parser.putControlSequence(new TextualContentCommand("@author", ""));
      parser.putControlSequence(new StoreDataCs("title"));
      parser.putControlSequence(new StoreDataCs("author"));
      parser.putControlSequence(new StoreDataCs("date"));
      parser.putControlSequence(new GenericCommand("@date", null, new TeXCsRef("today")));

      parser.putControlSequence(new TextualContentCommand("@subtitle", ""));
      parser.putControlSequence(new StoreDataCs("subtitle"));

      parser.putControlSequence(
        new GenericCommand("figurename", null, createString("Figure")));
      parser.putControlSequence(
        new GenericCommand("tablename", null, createString("Table")));

      parser.putControlSequence(
        new TextualContentCommand("indexname", "Index"));

      parser.putControlSequence(
        new GenericCommand("listtablename", null, createString("List of Tables")));
      parser.putControlSequence(
        new GenericCommand("listfigurename", null, createString("List of Figures")));

      parser.putControlSequence(new ListOfFloats("listoftables", 
        "listtablename", "lot"));
      parser.putControlSequence(new ListOfFloats("listoffigures", 
        "listfigurename", "lof"));

      newlength("fboxsep", 3, TeXUnit.PT);
      newlength("fboxrule", 0.4f, TeXUnit.PT);

      declareFrameBox(new FrameBox());
      declareFrameBox(new MBox(), false);

      ParBox parbox = new ParBox();
      declareFrameBox(parbox);
      parser.putControlSequence(new MiniPage(parbox));

      declareFrameBox(new FrameBox("framebox"));
      declareFrameBox(new MBox("makebox"));
      declareFrameBox(new MBox("frame",
        BorderStyle.SOLID,
        new UserDimension(1, FixedUnit.BP), 
        new UserDimension(0, FixedUnit.BP)), false);

      addSupplementaryBoxes();

      newlength("tabcolsep", 6, TeXUnit.PT);
      newlength("arraycolsep", 5, TeXUnit.PT);

      newlength("linewidth", 100, new PercentUnit());
      newlength("textwidth", 100, new PercentUnit(PercentUnit.TEXT_WIDTH));
      newlength("textheight", 100, new PercentUnit(PercentUnit.TEXT_HEIGHT));
      newlength("columnwidth", 100, new PercentUnit(PercentUnit.COLUMN_WIDTH));
      newlength("columnheight", 100, new PercentUnit(PercentUnit.COLUMN_HEIGHT));
      newlength("paperwidth", 100, new PercentUnit(PercentUnit.PAPER_WIDTH));
      newlength("paperheight", 100, new PercentUnit(PercentUnit.PAPER_HEIGHT));
      newlength("marginparwidth", 100, new PercentUnit(PercentUnit.MARGIN_WIDTH));

      newtoks(true, "toks@");

      newcounter("part", null, "@Roman");
      newcounter("section");
      newcounter("subsection", "section");
      newcounter("subsubsection", "subsection");
      newcounter("paragraph", "subsubsection");
      newcounter("subparagraph", "paragraph");

      newcounter("part*");
      newcounter("section*");
      newcounter("subsection*", "section*");
      newcounter("subsubsection*", "subsection*");
      newcounter("paragraph*", "subsubsection*");
      newcounter("subparagraph*", "paragraph*");

      newcounter("figure");
      newcounter("table");
      newcounter("equation");
      newcounter("enumi");
      newcounter("enumii", null, "@alph");
      newcounter("enumiii", null, "@roman");
      newcounter("enumiv", null, "@Alph");

      newcounter("footnote");
      newcounter("mpfootnote");

      newcounter("secnumdepth", 3);
      newcounter("tocdepth", 3);

      parser.getSettings().newcount("@listdepth");
      parser.getSettings().newcount("@enumdepth");
      parser.getSettings().newcount("@itemdepth");

      NewIf.createConditional(true, parser, "if@nmbrlist");

      parser.putControlSequence(new TrivListDec());
      parser.putControlSequence(new ListDec());
      parser.putControlSequence(new EnumerateDec());
      parser.putControlSequence(new ItemizeDec());
      parser.putControlSequence(new DescriptionDec());
      parser.putControlSequence(new GenericCommand(false, "descriptionlabel",
        new TeXObject[] {getParam(1)},
        new TeXObject[]
        {
           new TeXCsRef("hspace"),
           new TeXCsRef("labelsep"),
           new TeXCsRef("normalfont"),
           new TeXCsRef("bfseries"),
           getParam(1)
        }));
      parser.putControlSequence(new ListItem());
      parser.putControlSequence(new DescriptionItem());
      parser.putControlSequence(new UseCounter());

      parser.putControlSequence(new DescriptionDec("texparser@inlineblock@list",
        DescriptionStyle.INLINE_BLOCK_TITLE));
      parser.putControlSequence(new DescriptionDec("texparser@block@list",
        DescriptionStyle.BLOCK_TITLE));
      parser.putControlSequence(new AtFirstOfOne("texparser@listitem"));
      parser.putControlSequence(new AtFirstOfOne("texparser@listdesc"));

      parser.putControlSequence(
        new GenericCommand(true, "labelenumi", null, 
        new TeXObject[] {new TeXCsRef("theenumi"), getOther('.')}));

      parser.putControlSequence(
        new GenericCommand(true, "labelenumii", null, 
        new TeXObject[]
        {getOther('('), new TeXCsRef("theenumii"), getOther(')')}));

      parser.putControlSequence(
        new GenericCommand(true, "labelenumiii", null, 
        new TeXObject[] {new TeXCsRef("theenumiii"), getOther('.')}));

      parser.putControlSequence(
        new GenericCommand(true, "labelenumiv", null, 
        new TeXObject[] {new TeXCsRef("theenumiv"), getOther('.')}));

      parser.putControlSequence(
        new GenericCommand(true, "labelitemi", null, 
        new TeXObject[] {new TeXCsRef("textbullet")}));

      parser.putControlSequence(
        new GenericCommand(true, "labelitemii", null, 
        new TeXObject[]
        {
           new TeXCsRef("normalfont"),
           new TeXCsRef("bfseries"),
           new TeXCsRef("textendash")
        }));

      parser.putControlSequence(
        new GenericCommand(true, "labelitemiii", null, 
        new TeXObject[] {new TeXCsRef("textasteriskcentered")}));

      parser.putControlSequence(
        new GenericCommand(true, "labelitemiv", null, 
        new TeXObject[] {new TeXCsRef("textperiodcentered")}));

      parser.putControlSequence(
        new GenericCommand("@mpfn", null, createString("footnote")));
      parser.putControlSequence(
        new GenericCommand("thempfn", null, new TeXCsRef("thefootnote")));
      parser.putControlSequence(new Footnote());

      parser.putControlSequence(new MarginPar());
      parser.putControlSequence(new SwitchMarginSide("reversemarginpar", false));
      parser.putControlSequence(new SwitchMarginSide("normalmarginpar", true));

      parser.putControlSequence(new Thanks());
      parser.putControlSequence(new AtFnSymbol());

      parser.putControlSequence(new MathDeclaration("math"));

      MathDeclaration begMathDecl = new MathDeclaration("(");
      parser.putControlSequence(begMathDecl);
      parser.putControlSequence(new EndDeclaration(")", begMathDecl));
      parser.putControlSequence(
         new MathDeclaration("displaymath", TeXMode.DISPLAY_MATH));

      MathDeclaration begDispDecl = new MathDeclaration("[", TeXMode.DISPLAY_MATH);

      parser.putControlSequence(begDispDecl);
      parser.putControlSequence(new EndDeclaration("]", begDispDecl));
      parser.putControlSequence(
         new MathDeclaration("equation", TeXMode.DISPLAY_MATH, true));

      // Math font commands

      addMathFontCommand("mathrm",TeXFontMath.RM);
      addMathFontCommand("mathsf",TeXFontMath.SF);
      addMathFontCommand("mathtt",TeXFontMath.TT);
      addMathFontCommand("mathit",TeXFontMath.IT);
      addMathFontCommand("mathbf",TeXFontMath.BF);
      addMathFontCommand("mathcal",TeXFontMath.CAL);
      addMathFontCommand("mathbb",TeXFontMath.BB);
      addMathFontCommand("mathfrak",TeXFontMath.FRAK);
      addMathFontCommand("boldsymbol",TeXFontMath.BOLDSYMBOL);
      addMathFontCommand("pmb",TeXFontMath.PMB);

      // Font declarations
      addFontWeightDeclaration("mdseries", "textmd", TeXFontWeight.MD);
      addFontWeightDeclaration("bfseries", "textbf", TeXFontWeight.BF);

      addFontFamilyDeclaration("rmfamily", "textrm", TeXFontFamily.RM);
      addFontFamilyDeclaration("sffamily", "textsf", TeXFontFamily.SF);
      addFontFamilyDeclaration("ttfamily", "texttt", TeXFontFamily.TT);

      addFontShapeDeclaration("upshape", "textup", TeXFontShape.UP);
      addFontShapeDeclaration("itshape", "textit", TeXFontShape.IT);
      addFontShapeDeclaration("slshape", "textsl", TeXFontShape.SL);
      addFontShapeDeclaration("scshape", "textsc", TeXFontShape.SC);

      addFontSizeDeclaration("normalsize", TeXFontSize.NORMAL);
      addFontSizeDeclaration("large", TeXFontSize.LARGE);
      addFontSizeDeclaration("Large", TeXFontSize.XLARGE);
      addFontSizeDeclaration("LARGE", TeXFontSize.XXLARGE);
      addFontSizeDeclaration("huge", TeXFontSize.HUGE);
      addFontSizeDeclaration("Huge", TeXFontSize.XHUGE);
      addFontSizeDeclaration("HUGE", TeXFontSize.XXHUGE);
      addFontSizeDeclaration("small", TeXFontSize.SMALL);
      addFontSizeDeclaration("footnotesize", TeXFontSize.FOOTNOTE);
      addFontSizeDeclaration("scriptsize", TeXFontSize.SCRIPT);
      addFontSizeDeclaration("tiny", TeXFontSize.TINY);

      parser.putControlSequence(
        new GenericCommand(true, "@spaces", null, 
         new TeXObject[]{new TeXCsRef("space"), new TeXCsRef("space"),
          new TeXCsRef("space"), new TeXCsRef("space")}));

      parser.putControlSequence(new TextualContentCommand("obeyedspace", " "));
      parser.putControlSequence(new ObeySpaces());

      parser.putControlSequence(new PadNumber("two@digits", 2));

      parser.putControlSequence(new GenericError());
      parser.putControlSequence(new DocumentStyle());

      parser.putControlSequence(new ExternalDocument());

      // limited support for switching off babel shorthand
      parser.putControlSequence(new ShortHandOff());

      // Ignore but define to pick up \else and \fi
      NewIf.createConditional(false, parser, "if@endpe");
      parser.putControlSequence(new Relax("@doendpe"));

      // LaTeX3
      // minimal support, mainly to pick up cat code
      // changes to allow command names to be read properly
      parser.putControlSequence(new ExplSyntaxOn());
      parser.putControlSequence(new ExplSyntaxOff());
      parser.putControlSequence(new CharSetCatCodeNN());

      parser.putControlSequence(new ClearL3Object("tl_clear:N"));
      parser.putControlSequence(new ClearL3Object("tl_gclear:N"));
      parser.putControlSequence(new ClearL3Object("tl_clear:c"));
      parser.putControlSequence(new ClearL3Object("tl_gclear:c"));

      parser.putControlSequence(new ClearL3Object("seq_clear:N"));
      parser.putControlSequence(new ClearL3Object("seq_gclear:N"));
      parser.putControlSequence(new ClearL3Object("seq_clear:c"));
      parser.putControlSequence(new ClearL3Object("seq_gclear:c"));

      parser.putControlSequence(new ClearL3Object("prop_clear:N"));
      parser.putControlSequence(new ClearL3Object("prop_gclear:N"));
      parser.putControlSequence(new ClearL3Object("prop_clear:c"));
      parser.putControlSequence(new ClearL3Object("prop_gclear:c"));

      parser.putControlSequence(new ClearL3Object("clist_clear:N"));
      parser.putControlSequence(new ClearL3Object("clist_gclear:N"));
      parser.putControlSequence(new ClearL3Object("clist_clear:c"));
      parser.putControlSequence(new ClearL3Object("clist_gclear:c"));

      // Token list append and prepend
      parser.putControlSequence(new AddToL3Object("tl_put_right:Nn"));
      parser.putControlSequence(new AddToL3Object("tl_put_right:NV"));
      parser.putControlSequence(new AddToL3Object("tl_put_right:Nv"));
      parser.putControlSequence(new AddToL3Object("tl_put_right:Ne"));
      parser.putControlSequence(new AddToL3Object("tl_put_right:No"));
      parser.putControlSequence(new AddToL3Object("tl_put_right:cn"));
      parser.putControlSequence(new AddToL3Object("tl_put_right:cV"));
      parser.putControlSequence(new AddToL3Object("tl_put_right:cv"));
      parser.putControlSequence(new AddToL3Object("tl_put_right:ce"));
      parser.putControlSequence(new AddToL3Object("tl_put_right:co"));

      parser.putControlSequence(new AddToL3Object("tl_gput_right:Nn"));
      parser.putControlSequence(new AddToL3Object("tl_gput_right:NV"));
      parser.putControlSequence(new AddToL3Object("tl_gput_right:Nv"));
      parser.putControlSequence(new AddToL3Object("tl_gput_right:Ne"));
      parser.putControlSequence(new AddToL3Object("tl_gput_right:No"));
      parser.putControlSequence(new AddToL3Object("tl_gput_right:cn"));
      parser.putControlSequence(new AddToL3Object("tl_gput_right:cV"));
      parser.putControlSequence(new AddToL3Object("tl_gput_right:cv"));
      parser.putControlSequence(new AddToL3Object("tl_gput_right:ce"));
      parser.putControlSequence(new AddToL3Object("tl_gput_right:co"));

      parser.putControlSequence(new AddToL3Object("tl_put_left:Nn"));
      parser.putControlSequence(new AddToL3Object("tl_put_left:NV"));
      parser.putControlSequence(new AddToL3Object("tl_put_left:Nv"));
      parser.putControlSequence(new AddToL3Object("tl_put_left:Ne"));
      parser.putControlSequence(new AddToL3Object("tl_put_left:No"));
      parser.putControlSequence(new AddToL3Object("tl_put_left:cn"));
      parser.putControlSequence(new AddToL3Object("tl_put_left:cV"));
      parser.putControlSequence(new AddToL3Object("tl_put_left:cv"));
      parser.putControlSequence(new AddToL3Object("tl_put_left:ce"));
      parser.putControlSequence(new AddToL3Object("tl_put_left:co"));

      parser.putControlSequence(new AddToL3Object("tl_gput_left:Nn"));
      parser.putControlSequence(new AddToL3Object("tl_gput_left:NV"));
      parser.putControlSequence(new AddToL3Object("tl_gput_left:Nv"));
      parser.putControlSequence(new AddToL3Object("tl_gput_left:Ne"));
      parser.putControlSequence(new AddToL3Object("tl_gput_left:No"));
      parser.putControlSequence(new AddToL3Object("tl_gput_left:cn"));
      parser.putControlSequence(new AddToL3Object("tl_gput_left:cV"));
      parser.putControlSequence(new AddToL3Object("tl_gput_left:cv"));
      parser.putControlSequence(new AddToL3Object("tl_gput_left:ce"));
      parser.putControlSequence(new AddToL3Object("tl_gput_left:co"));

      // sequences append and prepend
      parser.putControlSequence(new AddToL3Object("seq_put_right:Nn"));
      parser.putControlSequence(new AddToL3Object("seq_put_right:NV"));
      parser.putControlSequence(new AddToL3Object("seq_put_right:Nv"));
      parser.putControlSequence(new AddToL3Object("seq_put_right:Ne"));
      parser.putControlSequence(new AddToL3Object("seq_put_right:No"));
      parser.putControlSequence(new AddToL3Object("seq_put_right:cn"));
      parser.putControlSequence(new AddToL3Object("seq_put_right:cV"));
      parser.putControlSequence(new AddToL3Object("seq_put_right:cv"));
      parser.putControlSequence(new AddToL3Object("seq_put_right:ce"));
      parser.putControlSequence(new AddToL3Object("seq_put_right:co"));

      parser.putControlSequence(new AddToL3Object("seq_gput_right:Nn"));
      parser.putControlSequence(new AddToL3Object("seq_gput_right:NV"));
      parser.putControlSequence(new AddToL3Object("seq_gput_right:Nv"));
      parser.putControlSequence(new AddToL3Object("seq_gput_right:Ne"));
      parser.putControlSequence(new AddToL3Object("seq_gput_right:No"));
      parser.putControlSequence(new AddToL3Object("seq_gput_right:cn"));
      parser.putControlSequence(new AddToL3Object("seq_gput_right:cV"));
      parser.putControlSequence(new AddToL3Object("seq_gput_right:cv"));
      parser.putControlSequence(new AddToL3Object("seq_gput_right:ce"));
      parser.putControlSequence(new AddToL3Object("seq_gput_right:co"));

      parser.putControlSequence(new AddToL3Object("seq_put_left:Nn"));
      parser.putControlSequence(new AddToL3Object("seq_put_left:NV"));
      parser.putControlSequence(new AddToL3Object("seq_put_left:Nv"));
      parser.putControlSequence(new AddToL3Object("seq_put_left:Ne"));
      parser.putControlSequence(new AddToL3Object("seq_put_left:No"));
      parser.putControlSequence(new AddToL3Object("seq_put_left:cn"));
      parser.putControlSequence(new AddToL3Object("seq_put_left:cV"));
      parser.putControlSequence(new AddToL3Object("seq_put_left:cv"));
      parser.putControlSequence(new AddToL3Object("seq_put_left:ce"));
      parser.putControlSequence(new AddToL3Object("seq_put_left:co"));

      parser.putControlSequence(new AddToL3Object("seq_gput_left:Nn"));
      parser.putControlSequence(new AddToL3Object("seq_gput_left:NV"));
      parser.putControlSequence(new AddToL3Object("seq_gput_left:Nv"));
      parser.putControlSequence(new AddToL3Object("seq_gput_left:Ne"));
      parser.putControlSequence(new AddToL3Object("seq_gput_left:No"));
      parser.putControlSequence(new AddToL3Object("seq_gput_left:cn"));
      parser.putControlSequence(new AddToL3Object("seq_gput_left:cV"));
      parser.putControlSequence(new AddToL3Object("seq_gput_left:cv"));
      parser.putControlSequence(new AddToL3Object("seq_gput_left:ce"));
      parser.putControlSequence(new AddToL3Object("seq_gput_left:co"));

      // CSV list append and prepend
      parser.putControlSequence(new AddToL3Object("clist_put_right:Nn"));
      parser.putControlSequence(new AddToL3Object("clist_put_right:NV"));
      parser.putControlSequence(new AddToL3Object("clist_put_right:Nv"));
      parser.putControlSequence(new AddToL3Object("clist_put_right:Ne"));
      parser.putControlSequence(new AddToL3Object("clist_put_right:No"));
      parser.putControlSequence(new AddToL3Object("clist_put_right:cn"));
      parser.putControlSequence(new AddToL3Object("clist_put_right:cV"));
      parser.putControlSequence(new AddToL3Object("clist_put_right:cv"));
      parser.putControlSequence(new AddToL3Object("clist_put_right:ce"));
      parser.putControlSequence(new AddToL3Object("clist_put_right:co"));

      parser.putControlSequence(new AddToL3Object("clist_gput_right:Nn"));
      parser.putControlSequence(new AddToL3Object("clist_gput_right:NV"));
      parser.putControlSequence(new AddToL3Object("clist_gput_right:Nv"));
      parser.putControlSequence(new AddToL3Object("clist_gput_right:Ne"));
      parser.putControlSequence(new AddToL3Object("clist_gput_right:No"));
      parser.putControlSequence(new AddToL3Object("clist_gput_right:cn"));
      parser.putControlSequence(new AddToL3Object("clist_gput_right:cV"));
      parser.putControlSequence(new AddToL3Object("clist_gput_right:cv"));
      parser.putControlSequence(new AddToL3Object("clist_gput_right:ce"));
      parser.putControlSequence(new AddToL3Object("clist_gput_right:co"));

      parser.putControlSequence(new AddToL3Object("clist_put_left:Nn"));
      parser.putControlSequence(new AddToL3Object("clist_put_left:NV"));
      parser.putControlSequence(new AddToL3Object("clist_put_left:Nv"));
      parser.putControlSequence(new AddToL3Object("clist_put_left:Ne"));
      parser.putControlSequence(new AddToL3Object("clist_put_left:No"));
      parser.putControlSequence(new AddToL3Object("clist_put_left:cn"));
      parser.putControlSequence(new AddToL3Object("clist_put_left:cV"));
      parser.putControlSequence(new AddToL3Object("clist_put_left:cv"));
      parser.putControlSequence(new AddToL3Object("clist_put_left:ce"));
      parser.putControlSequence(new AddToL3Object("clist_put_left:co"));

      parser.putControlSequence(new AddToL3Object("clist_gput_left:Nn"));
      parser.putControlSequence(new AddToL3Object("clist_gput_left:NV"));
      parser.putControlSequence(new AddToL3Object("clist_gput_left:Nv"));
      parser.putControlSequence(new AddToL3Object("clist_gput_left:Ne"));
      parser.putControlSequence(new AddToL3Object("clist_gput_left:No"));
      parser.putControlSequence(new AddToL3Object("clist_gput_left:cn"));
      parser.putControlSequence(new AddToL3Object("clist_gput_left:cV"));
      parser.putControlSequence(new AddToL3Object("clist_gput_left:cv"));
      parser.putControlSequence(new AddToL3Object("clist_gput_left:ce"));
      parser.putControlSequence(new AddToL3Object("clist_gput_left:co"));


      // Booleans
      parser.putControlSequence(new LaTeX3Boolean("c_false_bool", false));
      parser.putControlSequence(new LaTeX3Boolean("c_true_bool", true));

      parser.putControlSequence(new GenericCommand(true,
        "BooleanTrue", null, ONE));
      parser.putControlSequence(new GenericCommand(true,
        "BooleanFalse", null, ZERO));

      parser.putControlSequence(new IfBoolean());
      parser.putControlSequence(new IfBoolean("IfBooleanT", true, false));
      parser.putControlSequence(new IfBoolean("IfBooleanF", false, true));

      parser.putControlSequence(new IfValue());
      parser.putControlSequence(new IfValue("IfValueT", true, false));
      parser.putControlSequence(new IfValue("IfValueF", false, true));

      parser.putControlSequence(new IfNoValue());
      parser.putControlSequence(new IfNoValue("IfNoValueT", true, false));
      parser.putControlSequence(new IfNoValue("IfNoValueF", false, true));

      parser.putControlSequence(new NewDocumentCommand());
      parser.putControlSequence(new NewDocumentCommand(
      "RenewDocumentCommand", Overwrite.FORCE));
      parser.putControlSequence(new NewDocumentCommand(
      "ProvideDocumentCommand", Overwrite.SKIP));
      parser.putControlSequence(new NewDocumentCommand(
      "DeclareDocumentCommand", Overwrite.ALLOW));

      parser.putControlSequence(new NewDocumentEnvironment());
      parser.putControlSequence(new NewDocumentEnvironment(
      "RenewDocumentEnvironment", Overwrite.FORCE));
      parser.putControlSequence(new NewDocumentEnvironment(
      "ProvideDocumentEnvironment", Overwrite.SKIP));
      parser.putControlSequence(new NewDocumentEnvironment(
      "DeclareDocumentEnvironment", Overwrite.ALLOW));

      parser.putControlSequence(new GenericCommand(true,
       "obeyedline", null, createString(String.format("%n"))));
   }

   protected TeXParserSection createTeXParserSection(String sectionCsname)
   {
      return new TeXParserSection("texparser@"+sectionCsname, sectionCsname);
   }

   protected void addSupplementaryBoxes()
   {
      // These are designed for adding frames or overlays to symbols

      FrameBox boxFrame = new FrameBox("texparser@boxed",
        BorderStyle.SOLID, AlignHStyle.CENTER, AlignVStyle.MIDDLE, true,
        new UserDimension(2, FixedUnit.BP), new UserDimension());

      boxFrame.setId("boxed");
      boxFrame.setWidth(new UserDimension(1, TeXUnit.EM));
      boxFrame.setHeight(new UserDimension(1, TeXUnit.EM));

      declareFrameBox(boxFrame, false);

      boxFrame = new FrameBox("texparser@circled",
        BorderStyle.SOLID, AlignHStyle.CENTER, AlignVStyle.MIDDLE, true,
        new UserDimension(2, FixedUnit.BP), new UserDimension(1, TeXUnit.BP));

      boxFrame.setId("circled");
      boxFrame.setWidth(new UserDimension(1, TeXUnit.EM));
      boxFrame.setHeight(new UserDimension(1, TeXUnit.EM));

      boxFrame.setBorderRadius(new UserDimension(50, 
        new PercentUnit(PercentUnit.BOX_WIDTH)));

      declareFrameBox(boxFrame, false);

      boxFrame = new FrameBox("texparser@overlapped",
        BorderStyle.NONE, AlignHStyle.CENTER, AlignVStyle.DEFAULT, true, 
        null, new UserDimension());

      boxFrame.setId("overlapped");
      boxFrame.setWidth(new UserDimension(1, TeXUnit.EM));
      boxFrame.setHeight(new UserDimension(1, TeXUnit.EM));

      declareFrameBox(boxFrame, false);

      boxFrame = new FrameBox("texparser@overlapper",
        BorderStyle.NONE, AlignHStyle.CENTER, AlignVStyle.DEFAULT, true, 
        null, new UserDimension());

      boxFrame.setId("overlapper");
      boxFrame.setWidth(new UserDimension(1, TeXUnit.EM));
      boxFrame.setHeight(new UserDimension(1, TeXUnit.EM));
      boxFrame.setOuterMarginLeft(new UserDimension(-1, TeXUnit.EM));

      declareFrameBox(boxFrame, false);

      boxFrame = new FrameBox("texparser@overlapper@top",
        BorderStyle.NONE, AlignHStyle.CENTER, AlignVStyle.TOP, true, 
        null, new UserDimension());

      boxFrame.setId("overlappertop");
      boxFrame.setWidth(new UserDimension(1, TeXUnit.EM));
      boxFrame.setHeight(new UserDimension(1, TeXUnit.EM));
      boxFrame.setOuterMarginLeft(new UserDimension(-1, TeXUnit.EM));

      declareFrameBox(boxFrame, false);

      boxFrame = new FrameBox("texparser@partial@overlapper",
        BorderStyle.NONE, AlignHStyle.CENTER, AlignVStyle.DEFAULT, true, 
        null, new UserDimension());

      boxFrame.setId("partialoverlapper");
      boxFrame.setWidth(new UserDimension(1, TeXUnit.EM));
      boxFrame.setHeight(new UserDimension(1, TeXUnit.EM));
      boxFrame.setOuterMarginLeft(new UserDimension(-0.75f, TeXUnit.EM));

      declareFrameBox(boxFrame, false);

      parser.putControlSequence(new BoxOverlap("texparser@overlap@strike", 0x29F5));

      // rotate 1/4 left
      boxFrame = new FrameBox("texparser@quarterleft",
        BorderStyle.NONE, AlignHStyle.CENTER, AlignVStyle.MIDDLE, true, 
        null, new UserDimension());

      boxFrame.setId("quarterleft");
      boxFrame.setAngle(new Angle(-90, AngleUnit.DEGREES));

      declareFrameBox(boxFrame, false);

      // rotate 1/4 right
      boxFrame = new FrameBox("texparser@quarterright",
        BorderStyle.NONE, AlignHStyle.CENTER, AlignVStyle.MIDDLE, true, 
        null, new UserDimension());

      boxFrame.setId("quarterright");
      boxFrame.setAngle(new Angle(90, AngleUnit.DEGREES));

      declareFrameBox(boxFrame, false);

      // rotate 1/8 left
      boxFrame = new FrameBox("texparser@eighthleft",
        BorderStyle.NONE, AlignHStyle.CENTER, AlignVStyle.MIDDLE, true, 
        null, new UserDimension());

      boxFrame.setId("eighthleft");
      boxFrame.setAngle(new Angle(-45, AngleUnit.DEGREES));

      declareFrameBox(boxFrame, false);

      // rotate 1/8 right
      boxFrame = new FrameBox("texparser@eighthright",
        BorderStyle.NONE, AlignHStyle.CENTER, AlignVStyle.MIDDLE, true, 
        null, new UserDimension());

      boxFrame.setId("eighthright");
      boxFrame.setAngle(new Angle(45, AngleUnit.DEGREES));

      declareFrameBox(boxFrame, false);

      // rotate 1/2
      boxFrame = new FrameBox("texparser@halfturn",
        BorderStyle.NONE, AlignHStyle.CENTER, AlignVStyle.MIDDLE, true, 
        null, new UserDimension());

      boxFrame.setId("halfturn");
      boxFrame.setAngle(new Angle(180, AngleUnit.DEGREES));

      declareFrameBox(boxFrame, false);
   }


   protected void addMathFontCommand(String name, TeXFontMath style)
   {
      parser.putControlSequence(new MathFontCommand(name, style));
   }

   public void registerControlSequence(LaTeXSty sty, ControlSequence cs)
   {
      parser.putControlSequence(cs);
   }

   public SequenceCommand getSequenceCommand(String name, TeXObjectList stack)
   throws IOException
   {
      ControlSequence cs = getParser().getControlSequence(name);
      SequenceCommand seq = null;

      if (cs instanceof SequenceCommand)
      {
         seq = (SequenceCommand)cs;
      }
      else if (cs == null)
      {
         seq = new SequenceCommand(name);

         getTeXApp().warning(getParser(), getTeXApp().getMessage(
          TeXSyntaxException.ERROR_UNDEFINED, "\\"+name));

         parser.putControlSequence(true, seq);
      }
      else if (cs.isEmpty())
      {
         seq = new SequenceCommand(name);
         parser.putControlSequence(true, seq);
      }
      else
      {
         TeXObject obj = TeXParserUtils.expandOnce(cs, getParser(), stack);

         if (parser.isStack(obj))
         {
            seq = SequenceCommand.createFromSeqContent(parser, name,
               (TeXObjectList)obj);
            parser.putControlSequence(true, seq);
         }
      }

      if (seq == null)
      {
         throw new LaTeXSyntaxException(parser,
           LaTeXSyntaxException.ERROR_NOT_SEQUENCE, cs.toString(getParser()));
      }

      return seq;
   }

   public TokenListCommand popTokenListCommand(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      ControlSequence cs = TeXParserUtils.popControlSequence(parser, stack);

      if (cs instanceof TokenListCommand)
      {
         return (TokenListCommand)cs;
      }

      return getTokenListCommand(cs.getName(), stack);
   }

   public TokenListCommand getTokenListCommand(String name, TeXObjectList stack)
   throws IOException
   {
      ControlSequence cs = getParser().getControlSequence(name);
      TokenListCommand tl = null;

      if (cs instanceof TokenListCommand)
      {
         tl = (TokenListCommand)cs;
      }
      else if (cs instanceof TextualContentCommand)
      {
         tl = new TokenListCommand(name,
          createString(((TextualContentCommand)cs).getText()));
      }
      else if (cs == null)
      {
         tl = new TokenListCommand(name);

         getTeXApp().warning(getParser(), getTeXApp().getMessage(
          TeXSyntaxException.ERROR_UNDEFINED, "\\"+name));

         parser.putControlSequence(true, tl);
      }
      else if (cs.isEmpty())
      {
         tl = new TokenListCommand(name);
         parser.putControlSequence(true, tl);
      }
      else
      {
         TeXObject obj = TeXParserUtils.expandOnce(cs, getParser(), stack);

         if (parser.isStack(obj))
         {
            tl = new TokenListCommand(name, (TeXObjectList)obj);
            parser.putControlSequence(true, tl);
         }
      }

      if (tl == null)
      {
         throw new LaTeXSyntaxException(parser,
           LaTeXSyntaxException.ERROR_NOT_TOKEN_LIST, cs.toString(getParser()));
      }

      return tl;
   }

   public LaTeX3Boolean getLaTeX3Boolean(String name, TeXObjectList stack)
   throws IOException
   {
      ControlSequence cs = getParser().getControlSequence(name);
      LaTeX3Boolean bool = null;

      if (cs instanceof LaTeX3Boolean)
      {
         bool = (LaTeX3Boolean)cs;
      }
      else if (cs == null)
      {
         bool = new LaTeX3Boolean(name, false);

         getTeXApp().warning(getParser(), getTeXApp().getMessage(
          TeXSyntaxException.ERROR_UNDEFINED, "\\"+name));

         parser.putControlSequence(true, bool);
      }
      else
      {
         String str = getParser().expandToString(cs, stack);

         if (str.equals("0"))
         {
            bool = new LaTeX3Boolean(name, false);
         }
         else if (str.equals("1"))
         {
            bool = new LaTeX3Boolean(name, true);
         }
      }

      if (bool == null)
      {
         throw new LaTeXSyntaxException(parser,
           LaTeXSyntaxException.ERROR_NOT_BOOLEAN, cs.toString(getParser()));
      }

      return bool;
   }

   public void newcommand(Overwrite overwrite, 
     String type, String csName, boolean isShort,
     int numParams, TeXObject defValue, TeXObject definition)
   throws IOException
   {
      newcommand(false, overwrite, type, csName, isShort, numParams, defValue,
       definition);
   }

   public void newcommand(boolean isRobust, Overwrite overwrite, 
     String type, String csName, boolean isShort,
     int numParams, TeXObject defValue, TeXObject definition)
   throws IOException
   {
      ControlSequence cs = getParser().getControlSequence(csName);

      if (cs == null)
      {
         if (overwrite == Overwrite.FORCE)
         {
            throw new TeXSyntaxException(parser,
             TeXSyntaxException.ERROR_UNDEFINED,
             String.format("%s%s", 
              new String(Character.toChars(parser.getEscChar())), csName));
         }
      }
      else
      {
         if (overwrite == Overwrite.FORBID)
         {
            throw new LaTeXSyntaxException(parser,
             LaTeXSyntaxException.ERROR_DEFINED,
             cs.toString(parser));
         }
         else if (overwrite == Overwrite.SKIP)
         {
            return;
         }
      }

      addLaTeXCommand(isRobust, csName, isShort, numParams, defValue, definition);
   }

   public void addLaTeXCommand(String name, 
     boolean isShort, int numParams,
     TeXObject defValue, TeXObject definition)
   {
      addLaTeXCommand(false, name, isShort, numParams, defValue, definition);
   }

   public void addLaTeXCommand(boolean isRobust, String name, 
     boolean isShort, int numParams,
     TeXObject defValue, TeXObject definition)
   {
      TeXObjectList defList;

      if (definition instanceof TeXObjectList)
      {
         defList = (TeXObjectList)definition;
      }
      else
      {
         defList = new TeXObjectList(1);
         defList.add(definition);
      }

      if (numParams == 0)
      {
         putControlSequence(true,// local
           new LaTeXGenericCommand(isShort, name, isRobust, defList));
         return;
      }

      char[] syntax = new char[numParams];

      if (defValue == null)
      {
         syntax[0] = LaTeXGenericCommand.SYNTAX_MANDATORY;
      }
      else
      {
         syntax[0] = LaTeXGenericCommand.SYNTAX_OPTIONAL;
      }

      for (int i = 1; i < numParams; i++)
      {
         syntax[i] = LaTeXGenericCommand.SYNTAX_MANDATORY;
      }

      if (defValue == null)
      {
         putControlSequence(true,// local
           new LaTeXGenericCommand(isShort, name, isRobust, syntax, defList));
      }
      else
      {
         putControlSequence(true,// local
           new LaTeXGenericCommand(isShort, name, isRobust, syntax, defList,
             new TeXObject[]{defValue}));
      }
   }

   public void newenvironment(Overwrite overwrite, String type, String envName, 
     int numParams, TeXObject defValue, TeXObject definition, TeXObject endDefinition)
   throws IOException
   {
      ControlSequence cs = getControlSequence(envName);

      if (cs instanceof Undefined)
      {
         if (overwrite == Overwrite.FORCE)
         {
            throw new TeXSyntaxException(parser,
             TeXSyntaxException.ERROR_UNDEFINED,
             String.format("%s%s", 
              new String(Character.toChars(parser.getEscChar())), envName));
         }
      }
      else
      {
         if (overwrite == Overwrite.FORBID)
         {
            throw new LaTeXSyntaxException(parser,
             LaTeXSyntaxException.ERROR_DEFINED,
             cs.toString(parser));
         }
         else if (overwrite == Overwrite.SKIP)
         {
            return;
         }
      }

      addLaTeXEnvironment(envName, numParams, defValue, definition,
       endDefinition);
   }

   public void addLaTeXEnvironment(String name, int numParams,
     TeXObject defValue, TeXObject definition, TeXObject endDefinition)
   {
      if (numParams == 0)
      {
         putControlSequence(true,// local
           new LaTeXGenericEnvironment(name, definition, endDefinition));
         return;
      }

      TeXObjectList defList;

      if (definition instanceof TeXObjectList)
      {
         defList = (TeXObjectList)definition;
      }
      else
      {
         defList = new TeXObjectList(1);
         defList.add(definition);
      }

      char[] syntax = new char[numParams];

      if (defValue == null)
      {
         syntax[0] = LaTeXGenericCommand.SYNTAX_MANDATORY;
      }
      else
      {
         syntax[0] = LaTeXGenericCommand.SYNTAX_OPTIONAL;
      }

      for (int i = 1; i < numParams; i++)
      {
         syntax[i] = LaTeXGenericCommand.SYNTAX_MANDATORY;
      }

      ControlSequence cs;

      if (defValue == null)
      {
         cs = new LaTeXGenericCommand(false, "\\"+name, syntax, defList);
      }
      else
      {
         cs = new LaTeXGenericCommand(false, "\\"+name, syntax, defList,
             new TeXObject[]{defValue});
      }

      putControlSequence(true,// local
        new LaTeXGenericEnvironment(name, cs, endDefinition));
   }

   protected TextBlockCommand createTextBlockCommand(String textblockName,
    Declaration decl)
   {
      return new TextBlockCommand(textblockName, decl, TeXMode.TEXT);
   }

   private void addFontWeightDeclaration(
       String declName, String textblockName, TeXFontWeight weight)
   {
      Declaration decl = getFontWeightDeclaration(declName, weight);
      parser.putControlSequence(decl);
      parser.putControlSequence(createTextBlockCommand(textblockName, decl));
   }

   private void addFontShapeDeclaration(
       String declName, String textblockName, TeXFontShape shape)
   {
      Declaration decl = getFontShapeDeclaration(declName, shape);
      parser.putControlSequence(decl);
      parser.putControlSequence(createTextBlockCommand(textblockName, decl));
   }

   private void addFontSizeDeclaration(String name, TeXFontSize size)
   {
      parser.putControlSequence(getFontSizeDeclaration(name, size));
   }

   private void addFontFamilyDeclaration(
       String declName, String textblockName, TeXFontFamily family)
   {
      Declaration decl =  getFontFamilyDeclaration(declName, family);
      parser.putControlSequence(decl);
      parser.putControlSequence(createTextBlockCommand(textblockName, decl));
   }

   @Override
   public ControlSequence getTeXFontFamilyDeclaration(
      String name, TeXFontFamily family)
   {
      ControlSequence decl = super.getTeXFontFamilyDeclaration(name, family);

      String newName = decl.getName()+"family";

      ControlSequence newDecl = getControlSequence(newName);

      return newDecl == null ? decl : new Obsolete(decl, newDecl);
   }

   @Override
   public ControlSequence getTeXFontWeightDeclaration(
      String name, TeXFontWeight weight)
   {
      ControlSequence decl = super.getTeXFontWeightDeclaration(name, weight);

      String newName = decl.getName()+"series";

      ControlSequence newDecl = getControlSequence(newName);

      return newDecl == null ? decl : new Obsolete(decl, newDecl);
   }

   @Override
   public ControlSequence getTeXFontShapeDeclaration(
      String name, TeXFontShape shape)
   {
      if (name.equals("em"))
      {
         Declaration decl = getFontShapeDeclaration("em", TeXFontShape.EM);
         putControlSequence(createTextBlockCommand("emph", decl));
         return decl;
      }

      ControlSequence decl = super.getTeXFontShapeDeclaration(name, shape);

      String newName = decl.getName()+"shape";

      ControlSequence newDecl = getControlSequence(newName);

      return newDecl == null ? decl : new Obsolete(decl, newDecl);
   }

   public FontWeightDeclaration getFontWeightDeclaration(String name, TeXFontWeight weight)
   {
      return new FontWeightDeclaration(name, weight);
   }

   public FontSizeDeclaration getFontSizeDeclaration(String name, TeXFontSize size)
   {
      return new FontSizeDeclaration(name, size);
   }

   public FontShapeDeclaration getFontShapeDeclaration(String name, TeXFontShape shape)
   {
      return new FontShapeDeclaration(name, shape);
   }

   public FontFamilyDeclaration getFontFamilyDeclaration(String name, TeXFontFamily family)
   {
      return new FontFamilyDeclaration(name, family);
   }

   public TeXObject createUnknownReference(String label)
   {
      return new UnknownReference(this, label);
   }

   public TeXObject createUnknownReference(TeXObject label)
   {
      return new UnknownReference(this, label);
   }

   public TeXCellAlignList createTeXCellAlignList(TeXObject colSpecs)
     throws IOException
   {
      return new TeXCellAlignList(getParser(), colSpecs);
   }

   public boolean isInDocEnv()
   {
      return docEnvFound;
   }

   public void setIsInDocEnv(boolean inDocEnv)
   {
      this.docEnvFound = inDocEnv;
   }

   public boolean hasDocumentEnded()
   {
      return documentEnded;
   }

   public void atBeginDoc(TeXObject... code)
   {
      ControlSequence cs = parser.getControlSequence(
        "@begindocumenthook");

      if (cs == null)
      {
         cs = new GenericCommand(true, "@begindocumenthook", null,
          code);
         parser.putControlSequence(cs);
      }
      else if (cs instanceof GenericCommand)
      {
         TeXObjectList def = ((GenericCommand)cs).getDefinition();

         for (TeXObject obj : code)
         {
            def.add(obj);
         }
      }

      if (getParser().isDebugMode(TeXParser.DEBUG_PROCESSING))
      {
         getParser().logMessage("AtBeginDoc: "+cs);
      }
   }

   public void parseAux(File auxFile) throws IOException
   {
      parseAux(null, auxFile);
   }

   public void parseAux(String prefix, File auxFile) throws IOException
   {
      if (isParseAuxEnabled())
      {
         if (auxFile != null && auxFile.exists())
         {
            parser.debugMessage(TeXParser.DEBUG_IO, "Parsing AUX file: "+auxFile);

            AuxParser auxListener = new AuxParser(getTeXApp(), getCharSet(), prefix);
            TeXParser auxTeXParser = new TeXParser(auxListener);

            if (customAuxCommands != null)
            {
               for (AuxCommand auxCommand : customAuxCommands)
               {
                  auxListener.putControlSequence(auxCommand);
               }
            }

            auxListener.enableSaveDivisions(saveDivisions);
            auxListener.enableSaveLabels(true);
            auxListener.enableSaveCites(true);
            auxListener.parseAuxFile(auxTeXParser, auxFile, null);

            Vector<AuxData> data = auxListener.getAuxData();

            if (auxData == null)
            {
               auxData = data;
            }
            else if (data != null)
            {
               auxData.addAll(data);
            }

            divisionData = auxListener.getDivisionData();

            if (labelData == null)
            {
               labelData = auxListener.getLabelData();
            }
            else
            {
               HashMap<String,LabelInfo> auxLabelInfo = auxListener.getLabelData();

               if (auxLabelInfo != null)
               {
                  labelData.putAll(auxLabelInfo);
               }
            }

            if (citeData == null)
            {
               citeData = auxListener.getCiteData();
            }
            else
            {
               HashMap<String,CiteInfo> auxCiteInfo = auxListener.getCiteData();

               if (auxCiteInfo != null)
               {
                  citeData.putAll(auxCiteInfo);
               }
            }

            if (citeList == null)
            {
               citeList = auxListener.getCiteList();
            }
            else
            {
               Vector<CiteInfo> cl = auxListener.getCiteList();

               if (cl != null)
               {
                  citeList.addAll(cl);
               }
            }
         }
         else
         {
            parseAux = false;
            parser.debugMessage(TeXParser.DEBUG_IO, "No AUX file: "+auxFile);
         }
      }
      else
      {
         parser.debugMessage(TeXParser.DEBUG_IO, "AUX parser not enabled");
      }

   }

   @Deprecated
   public void beginDocument()
     throws IOException
   {
      beginDocument(getParser());
   }

   public void beginDocument(TeXObjectList stack)
     throws IOException
   {
      if (isInDocEnv())
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_MULTI_BEGIN_DOC);
      }

      getParser().getSettings().setCharMapMode(TeXSettings.CHAR_MAP_OFF);

      setIsInDocEnv(true);

      parseAux(getAuxFile());

      if (beginDocumentListeners != null)
      {
         BeginDocumentEvent evt = new BeginDocumentEvent(this, stack);

         for (BeginDocumentListener l : beginDocumentListeners)
         {
            l.documentBegun(evt);

            if (evt.isConsumed())
            {
               break;
            }
         }
      }

      ControlSequence cs = parser.getControlSequence(
        "@begindocumenthook");

      if (cs != null)
      {
         if (getParser().isDebugMode(TeXParser.DEBUG_PROCESSING))
         {
            getParser().logMessage("PROCESSING AtBeginDoc: "+cs);
         }

         TeXParserUtils.process(cs, parser, stack);
      }
   }

   public void addBeginDocumentListener(BeginDocumentListener l)
   {
      if (beginDocumentListeners == null)
      {
         beginDocumentListeners = new Vector<BeginDocumentListener>();
      }

      beginDocumentListeners.add(l);
   }

   @Deprecated
   public void endDocument()
     throws IOException
   {
      endDocument(getParser());
   }

   public void endDocument(TeXObjectList stack)
     throws IOException
   {
      if (!isInDocEnv())
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_NO_BEGIN_DOC);
      }

      processFootnotes(stack);

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
            parser.error(e);
         }
      }

      documentEnded = true;

      throw new EOFException();
   }

   @Deprecated
   public void processFootnotes()
   throws IOException
   {
      processFootnotes(getParser());
   }

   public void processFootnotes(TeXObjectList stack)
   throws IOException
   {
      if (footnotes.size() > 0)
      {
         doFootnoteRule();

         while (footnotes.size() > 0)
         {
            TeXParserUtils.process(footnotes.pop(), getParser(), stack);
            TeXParserUtils.process(getPar(), getParser(), stack);
         }
      }

   }

   public void doFootnoteRule() throws IOException
   {
   }

   public LaTeXCls getDocumentClass()
   {
      return docCls;
   }

   public KeyValList getDocumentClassOptions()
   {
      return docCls == null ? null : docCls.getOptions();
   }

   public boolean isClassLoaded(String name)
   {
      if (docCls == null)
      {
         return false;
      }

      if (docCls.getName().equals(name))
      {
         return true;
      }

      if (loadedClasses == null)
      {
         return false;
      }

      // may have been loaded by \LoadClass

      for (LaTeXCls cls : loadedClasses)
      {
         if (cls.getName().equals(name))
         {
            return true;
         }
      }

      return false;
   }

   public void loadclass(KeyValList options,
     String clsName, boolean loadParentOptions, TeXObjectList stack)
     throws IOException
   {
      if (docCls == null)
      {
         documentclass(options, clsName, loadParentOptions, stack);
         return;
      }

      LaTeXCls cls = getLaTeXCls(options, clsName, loadParentOptions);

      addFileReference(cls);

      if (loadedClasses == null)
      {
         loadedClasses = new Vector<LaTeXCls>();
      }

      loadedClasses.add(cls);

      if (cls instanceof UnknownCls)
      {
         parsePackageFile(cls, stack);
      }
      else
      {
         cls.processOptions(stack);
      }
   }

   public void documentclass(KeyValList options,
     String clsName, boolean loadParentOptions, TeXObjectList stack)
     throws IOException
   {
      if (docCls != null)
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_MULTI_CLS);
      }

      docCls = getLaTeXCls(options, clsName, loadParentOptions);

      addFileReference(docCls);

      if (docCls instanceof UnknownCls)
      {
         if (isBookClass(clsName) || isReportClass(clsName))
         {
            newcounter("chapter");
            newcounter("chapter*");

            updateChapterDependentCounter("section");
            addtoreset("section*", "chapter*");

            updateChapterDependentCounter("figure");
            updateChapterDependentCounter("table");
            updateChapterDependentCounter("equation");

            NewIf.createConditional(true, parser, "if@mainmatter", true);
            parser.putControlSequence(new FrontMatter());
            parser.putControlSequence(new MainMatter());
            parser.putControlSequence(new BackMatter());
         }

         parsePackageFile(docCls, stack);
      }
      else
      {
         docCls.processOptions(stack);
      }
   }

   protected void updateChapterDependentCounter(String ctrname)
   {
      addtoreset(ctrname, "chapter");

      parser.putControlSequence(new GenericCommand(true, "the"+ctrname, null,
       new TeXObject[] {
         new TeXCsRef("thechapter"),
         getOther('.'),
         new TeXCsRef("number"),
         new TeXCsRef("c@"+ctrname)}));

   }

   protected boolean isBookClass(String name)
   {
      return (name.contains("book") || name.equals("memoir"));
   }

   protected boolean isReportClass(String name)
   {
      return (name.contains("report") || name.equals("scrreprt"));
   }

   public LaTeXCls getLaTeXCls(KeyValList options, String clsName, 
     boolean loadParentOptions)
    throws IOException
   {
      if (clsName.equals("jmlr"))
      {
         return new JmlrCls(options, this, loadParentOptions);
      }

      if (clsName.equals("jmlrbook"))
      {
         return new JmlrBookCls(options, this, loadParentOptions);
      }

      return new UnknownCls(options, clsName, this, loadParentOptions);
   }

   public void removePackage(LaTeXSty sty)
   {
      removeFileReference(sty);
      loadedPackages.remove(sty);
   }

   public LaTeXSty requirepackage(String name, TeXObjectList stack)
   throws IOException
   {
      return requirepackage(null, name, false, stack);
   }

   public LaTeXSty loadpackage(KeyValList options, String name,
    boolean loadParentOptions, boolean enforceParse, TeXObjectList stack)
   throws IOException
   {
      parser.debugMessage(TeXParser.DEBUG_IO, "Loading package "+name);

      LaTeXSty sty = getLoadedPackage(name);

      if (sty != null)
      {
         return sty;
      }

      sty = getLaTeXSty(options, name, loadParentOptions, stack);

      addFileReference(sty);
      loadedPackages.add(sty);

      if (enforceParse)
      {// if it's known that the file is fairly simple
         sty.parseFile(stack);
      }
      else if (sty instanceof UnknownSty)
      {
         parsePackageFile(sty, stack);
      }
      else
      {
         sty.processOptions(stack);
      }

      return sty;
   }

   // returns file if already loaded
   public LaTeXSty requirepackage(KeyValList options, 
     String name, boolean loadParentOptions, TeXObjectList stack)
   throws IOException
   {
      LaTeXSty sty = getLoadedPackage(name);

      if (sty != null)
      {
         return sty;
      }

      sty = getLaTeXSty(options, name, loadParentOptions, stack);

      addFileReference(sty);
      loadedPackages.add(sty);

      parser.debugMessage(TeXParser.DEBUG_IO, "Requiring package "+name);

      if (sty instanceof UnknownSty)
      {
         parsePackageFile(sty, stack);
      }
      else
      {
         sty.processOptions(stack);
      }

      return sty;
   }

   /**
    * Register that the given package has been loaded.
    * Note that this doesn't implement any package options or add
    * definitions provided by the package. Use
    * LaTeXSty.usepackage(LaTeXSty,TeXObjectList) to ensure supplied commands are
    * available.
    */
   public void usepackage(LaTeXSty sty)
   {
      if (isStyLoaded(sty.getName()))
      {
         removePackage(sty);
      }

      if (sty instanceof FontEncSty)
      {
         fontEncSty = (FontEncSty)sty;
      }

      addFileReference(sty);
      loadedPackages.add(sty);
   }

   /**
    * Register that the given package has been loaded and ensure
    * all definitions and post option actions are implemented.
    */
   public void usepackage(LaTeXSty sty, TeXObjectList stack)
     throws IOException
   {
      usepackage(sty, null, stack);
   }

   /**
    * Register that the given package has been loaded, add the
    * supplied options and ensure all definitions and post option actions
    * are implemented.
    * @param sty the package implementation
    * @param options the package options to add or null if no
    * options required
    * @param stack the current stack (which may be null or the
    * parser if no local stack)
    */
   public void usepackage(LaTeXSty sty, KeyValList options, TeXObjectList stack)
     throws IOException
   {
      usepackage(sty);

      KeyValList passedOptions
         = getPassedOptions(sty.getName()+"."+sty.getExtension());

      if (passedOptions != null)
      {
         sty.addOptions(passedOptions);
      }

      if (options != null)
      {
         sty.addOptions(options);
      }

      sty.processOptions(stack);
   }

   public void addPackage(LaTeXSty sty)
   {
      addFileReference(sty);
      loadedPackages.add(sty);
   }

   public boolean isParsePackageSupportOn()
   {
      return parsePackages;
   }

   public void setParsePackageSupport(boolean on)
   {
      parsePackages = on;
   }

   public void parsePackageFile(LaTeXSty sty, TeXObjectList stack) throws IOException
   {
      // If not found by kpsewhich then possibly a custom package/class
      // which might be simple enough to parse.
      // Otherwise ignore unknown class/packages

      if (parsePackages && !sty.wasFoundByKpsewhich())
      {
         sty.parseFile(stack);
      }
   }

   // returns null if already loaded
   public LaTeXSty usepackage(KeyValList options, String styName, 
     boolean loadParentOptions)
   throws IOException
   {
      return usepackage(options, styName, loadParentOptions, getParser());
   }

   public LaTeXSty usepackage(KeyValList options, String styName, 
     boolean loadParentOptions, TeXObjectList stack)
   throws IOException
   {
      if (!isStyLoaded(styName))
      {
         parser.debugMessage(TeXParser.DEBUG_IO, "Use package "+styName);
         LaTeXSty sty = getLaTeXSty(options, styName, loadParentOptions, stack);

         addFileReference(sty);
         loadedPackages.add(sty);

         if (sty instanceof UnknownSty)
         {
            parsePackageFile(sty, stack);
         }
         else
         {
            sty.processOptions(stack);
         }

         parser.debugMessage(TeXParser.DEBUG_IO, "Finished use package "+styName);

         return sty;
      }

      return null;
   }

   public LaTeXSty getLoadedPackage(String styName)
   {
      if (loadedPackages == null)
      {
         return null;
      }

      for (LaTeXFile lfile : loadedPackages)
      {
         if (lfile instanceof LaTeXSty)
         {
            LaTeXSty sty = (LaTeXSty)lfile;

            if (sty.isName(styName))
            {
               return sty;
            }
         }
      }

      return null;
   }

   public FontEncSty getFontEncSty()
   {
      return fontEncSty;
   }

   public ColorSty getColorSty()
   {
      return colorSty;
   }

   protected LaTeXSty getLaTeXSty(KeyValList suppliedOptions, String styName, 
      boolean loadParentOptions, TeXObjectList stack)
   throws IOException
   {
      KeyValList options = getPassedOptions(styName+".sty");

      if (options == null)
      {
         options = suppliedOptions;
      }
      else
      {
         passOptions.remove(styName+".sty");

         if (suppliedOptions != null)
         {
            options.putAll(suppliedOptions);
         }
      }

      if (styName.equals("graphics")
        || styName.equals("graphicx")
        || styName.equals("epsfig"))
      {
         if (styName.equals("epsfig")
           && !isStyLoaded("graphicx"))
         {
            LaTeXFile lfile = new LaTeXFile(parser, null, "graphicx", "sty");

            addFileReference(lfile);
            loadedPackages.add(lfile);
         }

         return new GraphicsSty(options, styName, this, loadParentOptions);
      }

      if (styName.equals("amsmath"))
      {
         return new AmsmathSty(options, this, loadParentOptions);
      }

      if (styName.equals("amssymb"))
      {
         return new AmsSymbSty(options, this, loadParentOptions);
      }

      if (styName.equals("booktabs"))
      {
         return new BooktabsSty(options, this, loadParentOptions);
      }

      if (styName.equals("bpchem"))
      {
         return new BpChemSty(options, this, loadParentOptions);
      }

      if (styName.equals("color"))
      {
         colorSty = new ColorSty(options, styName, this, loadParentOptions);
         return colorSty;
      }

      if (styName.equals("xcolor"))
      {
         if (colorSty == null)
         {
            colorSty = new ColorSty(options, styName, this, loadParentOptions);
         }

         return new XColorSty(options, styName, this, loadParentOptions, colorSty);
      }

      if (styName.equals("datatool"))
      {
         return new DataToolSty(options, this, loadParentOptions);
      }

      if (styName.equals("datatool-base"))
      {
         return new DataToolBaseSty(options, this, loadParentOptions);
      }

      if (styName.equals("datagidx"))
      {
         return new DataGidxSty(options, this, loadParentOptions);
      }

      if (styName.equals("doc"))
      {
         return new DocSty(options, this, loadParentOptions);
      }

      if (styName.equals("etoolbox"))
      {
         return new EtoolboxSty(options, this, loadParentOptions);
      }

      if (styName.equals("keyval") || styName.equals("xkeyval"))
      {
         return new KeyValSty(options, styName, this, loadParentOptions);
      }

      if (styName.equals("fontawesome"))
      {
         return new FontAweSomeSty(options, this, loadParentOptions);
      }

      if (styName.equals("fontenc"))
      {
         fontEncSty = new FontEncSty(options, this, loadParentOptions);
         return fontEncSty;
      }

      if (styName.equals("fourier"))
      {
         return new FourierSty(options, this, loadParentOptions);
      }

      if (styName.equals("glossaries"))
      {
         if (glossariesSty == null)
         {
            glossariesSty =
               new GlossariesSty(options, styName, this, loadParentOptions);

            return glossariesSty;
         }
         else
         {
            return new GlossaryStyleSty("glossaries", glossariesSty);
         }
      }

      if (styName.equals("glossaries-extra"))
      {
         if (glossariesSty != null)
         {
            removePackage(glossariesSty);
         }

         glossariesSty = new GlossariesSty(options, styName, this, loadParentOptions);

         addPackage(new GlossaryStyleSty("glossaries", glossariesSty));

         return glossariesSty;
      }

      if (styName.startsWith("glossary-"))
      {
         if (glossariesSty == null)
         {
            glossariesSty =
               new GlossariesSty(options, "glossaries", this, loadParentOptions);

            addPackage(glossariesSty);
         }

         return glossariesSty.loadStylePackage(styName.substring(9), stack);
      }

      if (styName.equals("hyperref"))
      {
         return new HyperrefSty(options, this, loadParentOptions);
      }

      if (styName.equals("ifthen"))
      {
         return new IfThenSty(options, this, loadParentOptions);
      }

      if (styName.equals("inputenc"))
      {
         return new InputEncSty(options, this, loadParentOptions);
      }

      if (styName.equals("jmlrutils"))
      {
         return new JmlrUtilsSty(options, this, loadParentOptions);
      }

      if (styName.equals("jmlr2e"))
      {
         return new Jmlr2eSty(options, this, loadParentOptions);
      }

      if (styName.equals("lipsum"))
      {
         return new LipsumSty(options, this, loadParentOptions);
      }

      if (styName.equals("mfirstuc"))
      {
         return new MfirstucSty(options, this, loadParentOptions);
      }

      if (styName.equals("mfirstuc-english"))
      {
         return new MfirstucEnglishSty(options, this, loadParentOptions);
      }

      if (styName.equals("mhchem"))
      {
         return new MhchemSty(options, this, loadParentOptions);
      }

      if (styName.toLowerCase().equals("mnsymbol"))
      {
         return new MnSymbolSty(options, this, loadParentOptions);
      }

      if (styName.equals("natbib"))
      {
         return new NatbibSty(options, this, loadParentOptions);
      }

      if (styName.equals("nlctuserguide"))
      {
         if (colorSty == null)
         {
            colorSty = new ColorSty(options, styName, this, loadParentOptions);
         }

         return new UserGuideSty(options, this, loadParentOptions, colorSty);
      }

      if (styName.equals("pifont"))
      {
         return new PifontSty(options, this, loadParentOptions);
      }

      if (styName.equals("probsoln"))
      {
         return new ProbSolnSty(options, this, loadParentOptions);
      }

      if (styName.equals("shortvrb"))
      {
         return new ShortVrbSty(options, this, loadParentOptions);
      }

      if (styName.equals("siunitx"))
      {
         return new SIunitxSty(options, this, loadParentOptions);
      }

      if (styName.equals("stix"))
      {
         return new StixSty(options, this, loadParentOptions);
      }

      if (styName.equals("textcase"))
      {
         return new TextCaseSty(options, this, loadParentOptions);
      }

      if (styName.equals("textcomp"))
      {
         return new TextCompSty(options, this, loadParentOptions);
      }

      if (styName.equals("tipa"))
      {
         return new TipaSty(options, this, loadParentOptions);
      }

      if (styName.equals("twemojis"))
      {
         return new TwemojisSty(options, this, loadParentOptions);
      }

      if (styName.equals("upgreek"))
      {
         return new UpGreekSty(options, this, loadParentOptions);
      }

      if (styName.equals("wasysym"))
      {
         return new WasysymSty(options, this, loadParentOptions);
      }

      if (styName.equals("xfor"))
      {
         return new XforSty(options, this, loadParentOptions);
      }

      if (styName.equals("xspace"))
      {
         return new XspaceSty(options, this, loadParentOptions);
      }

      return new UnknownSty(options, styName, this, loadParentOptions);
   }

   public abstract void substituting( 
    String original, String replacement)
     throws IOException;

   @Deprecated
   public void includegraphics(KeyValList options, String imgName)
     throws IOException
   {
      includegraphics(getParser(), options, imgName);
   }

   public abstract void includegraphics(TeXObjectList stack, 
     KeyValList options, String imgName)
     throws IOException;

   public boolean isStyLoaded(String name)
   {
      for (LaTeXFile f : loadedPackages)
      {
         if (f.getName().equals(name))
         {
            return true;
         }
      }

      return false;
   }

   public Vector<LaTeXFile> getLoadedPackages()
   {
      return loadedPackages;
   }

   @Override
   public boolean input(TeXPath path, TeXObjectList stack)
     throws IOException
   {
      if (path.toString().endsWith("tcilatex.tex"))
      {
         usepackage(null, "amsmath", false, stack);
         usepackage(null, "graphicx", false, stack);
         addSpecialListener(new SWSpecialListener());

         parser.putControlSequence(new SWFrame());
         parser.putControlSequence(new Qcb());
         parser.putControlSequence(new BF());
         parser.putControlSequence(new NEG());
         parser.putControlSequence(new QATOP());
         parser.putControlSequence(new QTATOP());
         parser.putControlSequence(new QDATOP());
         parser.putControlSequence(new QABOVE());
         parser.putControlSequence(new QTABOVE());
         parser.putControlSequence(new QDABOVE());
         parser.putControlSequence(new QOVERD());
         parser.putControlSequence(new QTOVERD());
         parser.putControlSequence(new QDOVERD());
         parser.putControlSequence(new QATOPD());
         parser.putControlSequence(new QTATOPD());
         parser.putControlSequence(new QDATOPD());

         return true;
      }

      return super.input(path, stack);
   }

   public boolean bibliography(TeXPath[] bibPaths, TeXPath bblPath, TeXObjectList stack)
    throws IOException
   {
      return (bblPath.exists() ? super.input(bblPath, stack) : false);
   }

   public Charset getCharSet()
   {
      Charset charset = null;

      if (inputEncoding != null && !inputEncoding.equals("utf8"))
      {
         try
         {
            charset = InputEncSty.getCharSet(inputEncoding);
         }
         catch (IllegalCharsetNameException e)
         {
            parser.error(e);
            charset = null;
         }
      }

      return charset == null ? super.getCharSet() : charset;
   }

   public void setGraphicsPath(TeXObjectList paths)
     throws IOException
   {
      graphicsPath = paths;
   }

   public TeXObjectList getGraphicsPath()
   {
      return graphicsPath;
   }

   public void setImageExtensions(String... ext)
   {
      setImageExtensions(true, ext);
   }

   public void setImageExtensions(boolean addLeadingDotIfMissing, String... ext)
   {
      if (addLeadingDotIfMissing)
      {
         imageExtensions = new String[ext.length];

         for (int i = 0; i < ext.length; i++)
         {
            if (ext[i].startsWith("."))
            {
               imageExtensions[i] = ext[i];
            }
            else
            {
               imageExtensions[i] = "."+ext[i];
            }
         }
      }
      else
      {
         imageExtensions = ext;
      }
   }

   public String[] getImageExtensions()
   {
      return imageExtensions;
   }

   public String getInputEncoding()
   {
      return inputEncoding;
   }

   public void setInputEncoding(String enc)
   {
      inputEncoding = enc;
   }

   protected File getImageFile(String[] grpaths, TeXPath path)
    throws IOException,InterruptedException
   {
      if (grpaths == null)
      {
         File file = path.getFile();

         if (file.exists())
         {
            return file;
         }

      }
      else
      {
         Path basePath = path.getBaseDir();

         if (basePath == null)
         {
            basePath = getParser().getBaseDir().toPath();
         }

         basePath = basePath.toAbsolutePath();

         for (int i = 0; i < grpaths.length; i++)
         {
            Path subPath = 
            (new File(File.separatorChar == '/' ?
              grpaths[i] : 
              grpaths[i].replaceAll("/", File.separator)
            ).toPath()).resolve(path.getRelativePath());

            File file = (basePath == null ?  subPath :
              basePath.resolve(subPath)).toFile();

            if (file.exists())
            {
               return file;
            }
         }
      }

      String name = getTeXApp().kpsewhich(path.getFileName().toString());

      if (name != null && !name.isEmpty())
      {
         return new File(name);
      }

      return null;
   }

   public String[] getGraphicsPaths()
     throws IOException
   {
      TeXObjectList graphicsPath = getGraphicsPath();

      String[] grpaths = null;

      if (graphicsPath != null && graphicsPath.size() > 0)
      {
         int n = graphicsPath.size();

         grpaths = new String[n];

         for (int i = 0; i < n; i++)
         {
            TeXObject object = (TeXObject)graphicsPath.get(i).clone();
            grpaths[i] = parser.expandToString(object, parser);
         }
      }

      return grpaths;
   }

   public File getImageFile(String imageName)
     throws IOException
   {
      return getImage(getGraphicsPaths(), imageName);
   }

   public File getImage(String[] grpaths, String imgName)
     throws IOException
   {
      try
      {
         if (imgName.contains("."))
         {
            TeXPath path = new TeXPath(parser, imgName);

            return getImageFile(grpaths, path);
         }
         else
         {
            for (int i = 0; i < imageExtensions.length; i++)
            {
                String name = imgName+imageExtensions[i];

                TeXPath path = new TeXPath(parser, name);

                File file = getImageFile(grpaths, path);

                if (file != null)
                {
                   return file;
                }
            }
         }
      }
      catch (InterruptedException e)
      {
         parser.error(e);
      }

      return null;
   }

   public void cr(boolean isStar, TeXObject optArg)
     throws IOException
   {
      TeXSettings settings = getParser().getSettings();

      if (settings.getAlignMode() == TeXSettings.ALIGN_MODE_TRUE)
      {
         settings.startRow();
      }
   }

   public AlignRow createAlignRow(TeXObjectList stack)
     throws IOException
   {
      return new AlignRow(getParser(), stack);
   }

   public void setAuxData(AuxParser auxParser)
   {
      this.auxData = auxParser.getAuxData();

      saveDivisions = auxParser.isSaveDivisionsEnabled();

      divisionData = auxParser.getDivisionData();

      if (labelData == null)
      {
         labelData = auxParser.getLabelData();
      }
      else
      {
         HashMap<String,LabelInfo> auxLabelInfo = auxParser.getLabelData();

         if (auxLabelInfo != null)
         {
            labelData.putAll(auxLabelInfo);
         }
      }

      if (citeData == null)
      {
         citeData = auxParser.getCiteData();
      }
      else
      {
         HashMap<String,CiteInfo> auxCiteInfo = auxParser.getCiteData();

         if (auxCiteInfo != null)
         {
            citeData.putAll(auxCiteInfo);
         }
      }

      if (citeList == null)
      {
         citeList = auxParser.getCiteList();
      }
      else
      {
         Vector<CiteInfo> cl = auxParser.getCiteList();

         if (cl != null)
         {
            citeList.addAll(cl);
         }
      }
   }

   public void setDivisionData(Vector<DivisionInfo> divisionData)
   {
      this.divisionData = divisionData;
   }

   public Vector<DivisionInfo> getDivisionData()
   {
      return divisionData;
   }

   public void setAuxData(Vector<AuxData> auxData)
   {
      this.auxData = auxData;
   }

   public Vector<AuxData> getAuxData()
   {
      return auxData;
   }

   public File getAuxFile()
   {
      return getAuxFile("aux");
   }

   // needs to be done before aux file is parsed
   public void addAuxCommand(AuxCommand auxCommand)
   {
      if (auxCommand == null)
      {
         throw new NullPointerException();
      }

      if (customAuxCommands == null)
      {
         customAuxCommands = new Vector<AuxCommand>();
      }

      customAuxCommands.add(auxCommand);
   }

   public File getBblFile()
   {
      return getAuxFile("bbl");
   }

   public File getAuxFile(String ext)
   {
      if (getParser() == null) return null;

      File dir = getParser().getCurrentParentFile();

      String jobname = parser.getJobname();

      if (dir == null)
      {
         return new File(jobname+"."+ext);
      }
      else
      {
         return new File(dir, jobname+"."+ext);
      }
   }

   public void newcounter(String name)
   {
      newcounter(name, null);
   }

   public void newcounter(String name, int value)
   {
      newcounter(name, null, "number", value);
   }

   public void newcounter(String name, String parent)
   {
      newcounter(name, parent, "number");
   }

   public void newcounter(String name, String parent, String format)
   {
      newcounter(name, parent, format, 0);
   }

   public void newcounter(String name, String parent, String format, int value)
   {
      // counters are global
      CountRegister reg = parser.getSettings().newcount(false, "c@"+name);
      reg.setValue(value);

      if (parent == null)
      {
         parser.putControlSequence(new GenericCommand(true, "the"+name, null,
             new TeXObject[] {new TeXCsRef(format), new TeXCsRef("c@"+name)}));

      }
      else
      {
         parser.putControlSequence(new GenericCommand(true, "the"+name, null,
             new TeXObject[] {
               new TeXCsRef("the"+parent),
               getOther('.'),
               new TeXCsRef("number"),
               new TeXCsRef("c@"+name)}));
         addtoreset(name, parent);
      }
   }

   public void addtoreset(String name, String parent)
   {
      Vector<String> dependents = counters.get(parent);

      if (dependents == null)
      {
         dependents = new Vector<String>();
         counters.put(parent, dependents);
      }

      dependents.add(name);
   }

   public int getcountervalue(String name)
    throws TeXSyntaxException,LaTeXSyntaxException
   {
      NumericRegister reg = parser.getSettings().getNumericRegister("c@"+name);

      if (reg == null)
      {
         throw new LaTeXSyntaxException(parser, 
            LaTeXSyntaxException.ERROR_UNDEFINED_COUNTER, name);
      }

      return reg.number(parser);
   }

   public void addtocounter(String name, Numerical value)
     throws TeXSyntaxException
   {
      parser.getSettings().globalAdvanceRegister("c@"+name, value);
   }

   public void setcounter(String name, Numerical value)
     throws TeXSyntaxException
   {
      parser.getSettings().globalSetRegister("c@"+name, value);
   }

   public void resetcounter(String name)
     throws TeXSyntaxException
   {
      setcounter(name, ZERO);
   }

   public void stepcounter(String name)
     throws TeXSyntaxException
   {
      addtocounter(name, ONE);

      Vector<String> dependents = counters.get(name);

      if (dependents != null)
      {
         for (String dep : dependents)
         {
            resetcounter(dep);
         }
      }
   }

   public TeXObjectList getBibliographySection()
   {
      return bibliographySection;
   }

   public void setBibliographySection(TeXObjectList object)
   {
      bibliographySection = object;
   }

   public void addToBibliographySection(TeXObject object)
   {
      bibliographySection.add(object);
   }

   @Deprecated
   public void addFootnote(TeXObject footnote)
   {
      footnotes.add(footnote);
   }

   public void addFootnote(TeXObject footnote, TeXObjectList stack)
    throws IOException
   {
      footnotes.add(footnote);
   }

   public boolean isMarginRight()
   {
      return marginright;
   }

   public void setMarginRight(boolean isRight)
   {
      marginright = isRight;
   }

   public void marginpar(TeXObject leftText, TeXObject rightText)
     throws IOException
   {
   }

   public TeXObjectList getAuthor()
   {
      ControlSequence cs = getControlSequence("@author");

      if (cs instanceof GenericCommand)
      {
         return ((GenericCommand)cs).getDefinition();
      }

      return null;
   }

   // null argument indicates default index (mainIndex)
   // returns null if indexing is disabled or if index doesn't exist
   public IndexRoot getIndexRoot(String ref)
   {
      if (!isIndexingEnabled()) return null;

      return indexes.get(ref == null ? mainIndex : ref);
   }

   public IndexLocation createIndexLocation(String indexLabel)
    throws IOException
   {
      return new IndexLocation(new TeXCsRef("thepage"));
   }

   public void index(String ref, TeXObject arg) throws IOException
   {
      IndexRoot indexRoot = getIndexRoot(ref);

      if (indexRoot != null)
      {
         indexRoot.addEntry(getParser(), arg,
            createIndexLocation(ref == null ? mainIndex : ref));
      }
   }

   public float emToPt(float emValue)
   {
      getParser().warning(
        "Can't convert from em to pt, no font information loaded");

      // approximate

      float base = 10f;

      if (docCls == null)
      {
         KeyValList opts = docCls.getOptions();

         if (opts != null)
         {
            for (Iterator<String> it = opts.keySet().iterator(); it.hasNext();)
            {
               Matcher m = PTSIZE_PATTERN.matcher(it.next());

               if (m.matches())
               {
                  try
                  {
                     base = (float)Integer.parseInt(m.group(1));
                     break;
                  }
                  catch (NumberFormatException e)
                  {// this won't happen
                  }
               }
            }
         }
      }

      // TODO take into account font size change

      return base*0.95f*emValue;
   }

   public float exToPt(float exValue)
   {
      getParser().warning(
        "Can't convert from ex to pt, no font information loaded");

      // approximate!!!

      float base = 10f;

      if (docCls == null)
      {
         KeyValList opts = docCls.getOptions();

         if (opts != null)
         {
            for (Iterator<String> it = opts.keySet().iterator(); it.hasNext();)
            {
               Matcher m = PTSIZE_PATTERN.matcher(it.next());

               if (m.matches())
               {
                  try
                  {
                     base = (float)Integer.parseInt(m.group(1));
                     break;
                  }
                  catch (NumberFormatException e)
                  {// this won't happen
                  }
               }
            }
         }
      }

      // TODO take into account font size change

      return base*0.44f*exValue;
   }

   public boolean isIndexingEnabled()
   {
      return indexingEnabled;
   }

   public void enableIndexing(boolean enable)
   {
      enableIndexing(mainIndex, enable);
   }

   public void enableIndexing(String indexLabel, boolean enable)
   {
      indexingEnabled = enable;

      if (indexingEnabled)
      {
         IndexRoot index = getIndexRoot(indexLabel);

         if (index == null)
         {
            indexes.put(
              indexLabel == null ? mainIndex : indexLabel,
              index);
         }
      }
   }

   public void setMainIndexLabel(String label)
   {
      mainIndex = label;
   }

   public void startList(TrivListDec trivlist) throws IOException
   {
      trivListStack.push(trivlist);
   }

   public void endList(TrivListDec trivlist) throws IOException
   {
      trivListStack.pop();
   }

   public TrivListDec peekTrivListStack()
   {
      return trivListStack.peek();
   }

   public void startColor(Color color, boolean isForeground)
     throws IOException
   {
   }

   public void endColor(boolean isForeground)
     throws IOException
   {
   }

   public void startFrameBox(FrameBox fbox)
    throws IOException
   {
   }

   public void endFrameBox(FrameBox fbox)
    throws IOException
   {
   }

   public void startTheorem(String name) throws IOException
   {
      getPar().process(parser);
   }

   public void endTheorem(String name) throws IOException
   {
      getPar().process(parser);
   }

   public void setCurrentSty(LaTeXFile sty, String ext)
   {
      if (currentSty == null)
      {
         if (sty == null)
         {
            currentExt = null;
            return;
         }

         currentSty = new HashMap<String,LaTeXFile>();
      }

      if (sty == null)
      {
         currentSty.remove(ext);
         currentExt = null;
      }
      else
      {
         currentSty.put(ext, sty);
         currentExt = ext;

      }
   }

   public LaTeXFile getCurrentSty(String ext)
   {
      return currentSty == null ? null : currentSty.get(ext);
   }

   public String getCurrentExtension()
   {
      return currentExt;
   }

   public LaTeXFile getCurrentSty() throws IOException
   {
      String ext = getCurrentExtension();

      if (ext == null)
      {
         ControlSequence cs = parser.getControlSequence("@currext");

         if (cs instanceof Expandable)
         {
            TeXObjectList expanded;

            expanded = ((Expandable)cs).expandfully(parser);

            if (expanded != null)
            {
               ext = expanded.toString(parser);
            }
         }

         if (ext == null)
         {
            ext = getCurrentExtension();

            if (ext == null)
            {
               getParser().warningMessage(
                    TeXSyntaxException.ERROR_UNEXPANDABLE, "@currext");

               ext = "sty";
            }
         }
      }

      LaTeXFile sty = getCurrentSty(ext);

      if (sty == null)
      {
         ControlSequence cs = parser.getControlSequence("@currname");
         String name=null;

         if (cs instanceof Expandable)
         {
            TeXObjectList expanded;

            expanded = ((Expandable)cs).expandfully(parser);

            if (expanded != null)
            {
               name = expanded.toString(parser);
            }
         }

         if (name == null)
         {
            throw new LaTeXSyntaxException(parser, 
              getTeXApp().getMessage(
                 TeXSyntaxException.ERROR_UNEXPANDABLE, "@currname"));
         }

         sty = getLoadedPackage(name);

         if (sty == null)
         {
            throw new LaTeXSyntaxException(parser, 
              getTeXApp().getMessage(
                 LaTeXSyntaxException.ERROR_PACKAGE_NOT_LOADED, name));
         }
      }

      return sty;
   }

   public void passOptionsTo(String name, KeyValList options)
   {
      if (passOptions == null)
      {
         passOptions = new HashMap<String,KeyValList>();
      }
      else
      {
         KeyValList value = passOptions.get(name);

         if (value != null)
         {
            value.putAll(options);
            return;
         }
      }

      passOptions.put(name, options);
   }

   public KeyValList getPassedOptions(String name)
   {
      if (passOptions == null)
      {
         return null;
      }

      return passOptions.get(name);
   }

   public void declareFrameBox(FrameBox fbox)
   {
      declareFrameBox(fbox, true);
   }

   public void declareFrameBox(FrameBox fbox, boolean isChangeable)
   {
      if (!isChangeable)
      {
         fbox.fixStyle();
      }

      if (!fbox.isStyleChangeable())
      {
         if (frameBoxes == null)
         {
            frameBoxes = new HashMap<String,FrameBox>();
         }

         String id = fbox.getId();

         frameBoxes.put(id, fbox);
      }

      parser.putControlSequence(fbox);
   }

   public FrameBox getDeclaredFrameBox(String id)
   {
      if (frameBoxes == null || id == null)
      {
         return null;
      }

      return frameBoxes.get(id);
   }

   public FrameBox removeDeclaredFrameBox(String id)
   {
      if (frameBoxes != null && id != null)
      {
         return frameBoxes.remove(id);
      }

      return null;
   }

   public void setCurrentContentsList(String toc)
   {
      currentTOC = toc;
   }

   public String getCurrentContentsList()
   {
      return currentTOC;
   }

   private Vector<String> verbEnv;

   protected Vector<LaTeXFile> loadedPackages;
   protected Vector<LaTeXCls> loadedClasses;

   private boolean parseAux = false;

   private Vector<AuxData> auxData;

   protected Vector<AuxCommand> customAuxCommands;
   protected Vector<BeginDocumentListener> beginDocumentListeners;

   private boolean saveDivisions;
   protected Vector<DivisionInfo> divisionData;
   protected HashMap<String,LabelInfo> labelData;
   protected HashMap<String,CiteInfo> citeData;
   protected HashMap<String,String> linkLabelMap;

   protected Vector<CiteInfo> citeList;

   private Hashtable<String,Vector<String>> counters;

   protected LaTeXCls docCls;

   private HashMap<String,LaTeXFile> currentSty = null;

   private String currentExt = null;

   private HashMap<String,KeyValList> passOptions=null;

   private boolean parsePackages = false;

   private TeXObjectList graphicsPath = null;

   protected String[] imageExtensions = new String[]
   {
      ".pdf", ".PDF", ".png", ".PNG", ".jpg", ".JPG", ".jpeg", ".JPEG",
      ".eps", ".EPS", ".ps", ".PS", ".gif", ".GIF"
   };

   private boolean docEnvFound = false;

   protected boolean documentEnded = false;

   private String inputEncoding = null;

   protected FontEncSty fontEncSty = null;

   protected ColorSty colorSty = null;

   protected GlossariesSty glossariesSty = null;

   private TeXObjectList bibliographySection;

   private TeXObjectList footnotes;

   private boolean marginright=true;

   private Hashtable<String,IndexRoot> indexes;

   private String mainIndex = "main";

   private boolean indexingEnabled = false;

   private Stack<TrivListDec> trivListStack = new Stack<TrivListDec>();

   protected HashMap<String,FrameBox> frameBoxes;

   protected String currentTOC = null;

   public static final UserNumber ZERO = UserNumber.ZERO;
   public static final UserNumber ONE = UserNumber.ONE;
   public static final UserNumber MINUS_ONE = UserNumber.MINUS_ONE;

   public static final UserNumber PART_LEVEL = MINUS_ONE;
   public static final UserNumber CHAPTER_LEVEL = ZERO;
   public static final UserNumber SECTION_LEVEL = ONE;
   public static final UserNumber SUBSECTION_LEVEL = UserNumber.TWO;
   public static final UserNumber SUBSUBSECTION_LEVEL = UserNumber.THREE;
   public static final UserNumber PARAGRAPH_LEVEL = UserNumber.FOUR;
   public static final UserNumber SUBPARAGRAPH_LEVEL = UserNumber.FIVE;

   public static final Pattern PTSIZE_PATTERN = Pattern.compile("(\\d+)pt");
}
