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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.graphics.*;
import com.dickimawbooks.texparserlib.latex.amsmath.*;
import com.dickimawbooks.texparserlib.latex.tcilatex.*;
import com.dickimawbooks.texparserlib.latex.lipsum.*;
import com.dickimawbooks.texparserlib.latex.jmlr.*;

public abstract class LaTeXParserListener extends DefaultTeXParserListener
{
   public LaTeXParserListener(Writeable writeable)
   {
      super(writeable);
   }

   public void addInlineMathEnv(String name)
   {
      inLineMathEnv.add(name);
   }

   public void addDisplayMathEnv(String name)
   {
      displayMathEnv.add(name);
   }

   public boolean isInlineMathEnv(String name)
   {
      return inLineMathEnv.contains(name);
   }

   public boolean isDisplayMathEnv(String name)
   {
      return displayMathEnv.contains(name);
   }

   public void putEnvironment(Environment env)
   {
      envTable.put(env.getName(), env);
   }

   protected void addPredefined()
   {
      loadedPackages = new Vector<LaTeXFile>();
      envTable = new Hashtable<String,Environment>();
      inLineMathEnv = new Vector<String>();
      displayMathEnv = new Vector<String>();

      addInlineMathEnv("math");
      addDisplayMathEnv("displaymath");
      addDisplayMathEnv("equation");

      putEnvironment(new Verbatim());
      putEnvironment(new Verbatim("verbatim*"));

      parser.putControlSequence(new Begin());
      parser.putControlSequence(new End());
      parser.putControlSequence(new DocumentClass());
      parser.putControlSequence(new UsePackage());
      parser.putControlSequence(new NewCommand());
      parser.putControlSequence(new NewCommand("renewcommand"));
      parser.putControlSequence(new NewCommand("providecommand"));

      parser.putControlSequence(new Input());
      parser.putControlSequence(new InputIfFileExists());
      parser.putControlSequence(new IfFileExists());
      parser.putControlSequence(new MakeAtLetter());
      parser.putControlSequence(new MakeAtOther());
      parser.putControlSequence(new Centerline());
      parser.putControlSequence(new Verb());
      parser.putControlSequence(new MathCs());
      parser.putControlSequence(new DisplayMathCs());
      parser.putControlSequence(new NoLinkUrl());
      parser.putControlSequence(new Cr("\\"));
      parser.putControlSequence(new Cr("cr"));
      parser.putControlSequence(new Href());
      parser.putControlSequence(new Frac());
      parser.putControlSequence(new Empty("@empty"));

      // Math font commands

      addMathFontCommand("mathrm",TeXSettings.MATH_STYLE_RM);
      addMathFontCommand("mathsf",TeXSettings.MATH_STYLE_SF);
      addMathFontCommand("mathtt",TeXSettings.MATH_STYLE_TT);
      addMathFontCommand("mathit",TeXSettings.MATH_STYLE_IT);
      addMathFontCommand("mathbf",TeXSettings.MATH_STYLE_BF);
      addMathFontCommand("mathcal",TeXSettings.MATH_STYLE_CAL);
      addMathFontCommand("mathbb",TeXSettings.MATH_STYLE_BB);
      addMathFontCommand("mathfrak",TeXSettings.MATH_STYLE_FRAK);
      addMathFontCommand("boldsymbol",TeXSettings.MATH_STYLE_BOLDSYMBOL);
      addMathFontCommand("pmb",TeXSettings.MATH_STYLE_PMB);

      // Font declarations
      addFontWeightDeclaration("mdseries", "textmd", TeXSettings.WEIGHT_MD);
      addFontWeightDeclaration("bfseries", "textbf", TeXSettings.WEIGHT_BF);

      addFontFamilyDeclaration("rmfamily", "textrm", TeXSettings.FAMILY_RM);
      addFontFamilyDeclaration("sffamily", "textsf", TeXSettings.FAMILY_SF);
      addFontFamilyDeclaration("ttfamily", "texttt", TeXSettings.FAMILY_TT);
      addFontFamilyDeclaration("calfamily", "textcal", TeXSettings.FAMILY_CAL);

      addFontShapeDeclaration("upshape", "textup", TeXSettings.SHAPE_UP);
      addFontShapeDeclaration("itshape", "textit", TeXSettings.SHAPE_IT);
      addFontShapeDeclaration("slshape", "textsl", TeXSettings.SHAPE_SL);
      addFontShapeDeclaration("scshape", "textsc", TeXSettings.SHAPE_SC);

      addFontSizeDeclaration("normalsize", TeXSettings.SIZE_NORMAL);
      addFontSizeDeclaration("large", TeXSettings.SIZE_LARGE);
      addFontSizeDeclaration("Large", TeXSettings.SIZE_XLARGE);
      addFontSizeDeclaration("LARGE", TeXSettings.SIZE_XXLARGE);
      addFontSizeDeclaration("huge", TeXSettings.SIZE_HUGE);
      addFontSizeDeclaration("Huge", TeXSettings.SIZE_XHUGE);
      addFontSizeDeclaration("HUGE", TeXSettings.SIZE_XXHUGE);
      addFontSizeDeclaration("small", TeXSettings.SIZE_SMALL);
      addFontSizeDeclaration("footnotesize", TeXSettings.SIZE_FOOTNOTE);
      addFontSizeDeclaration("scriptsize", TeXSettings.SIZE_SCRIPT);
      addFontSizeDeclaration("tiny", TeXSettings.SIZE_TINY);

      super.addPredefined();

      parser.putControlSequence(new DocumentStyle());
   }

   protected void addMathFontCommand(String name, int style)
   {
      parser.putControlSequence(new MathFontCommand(name, style));
   }

   public void newcommand(String type, String csName, boolean isShort,
     int numParams, TeXObject defValue, TeXObject definition)
   throws IOException
   {
      putControlSequence(true,
        new LaTeXCommand(csName, isShort, numParams, defValue, definition));
   }

   private void addFontWeightDeclaration(
       String declName, String textblockName, int weight)
   {
      Declaration decl = getFontWeightDeclaration(declName, weight);
      parser.putControlSequence(decl);
      parser.putControlSequence(new TextBlockCommand(textblockName, decl));
   }

   private void addFontShapeDeclaration(
       String declName, String textblockName, int shape)
   {
      Declaration decl = getFontShapeDeclaration(declName, shape);
      parser.putControlSequence(decl);
      parser.putControlSequence(new TextBlockCommand(textblockName, decl));
   }

   private void addFontSizeDeclaration(String name, int size)
   {
      parser.putControlSequence(getFontSizeDeclaration(name, size));
   }

   private void addFontFamilyDeclaration(
       String declName, String textblockName, int family)
   {
      Declaration decl =  getFontFamilyDeclaration(declName, family);
      parser.putControlSequence(decl);
      parser.putControlSequence(new TextBlockCommand(textblockName, decl));
   }

   public ControlSequence getTeXFontFamilyDeclaration(
      String name, int family)
   {
      ControlSequence decl = super.getTeXFontFamilyDeclaration(name, family);

      String newName = decl.getName()+"family";

      ControlSequence newDecl = getControlSequence(newName);

      return newDecl == null ? decl : new Obsolete(decl, newDecl);
   }

   public ControlSequence getTeXFontWeightDeclaration(
      String name, int weight)
   {
      ControlSequence decl = super.getTeXFontWeightDeclaration(name, weight);

      String newName = decl.getName()+"series";

      ControlSequence newDecl = getControlSequence(newName);

      return newDecl == null ? decl : new Obsolete(decl, newDecl);
   }

   public ControlSequence getTeXFontShapeDeclaration(
      String name, int shape)
   {
      if (name.equals("em"))
      {
         Declaration decl = getFontShapeDeclaration("em", TeXSettings.SHAPE_EM);
         putControlSequence(new TextBlockCommand("emph", decl));
         return decl;
      }

      ControlSequence decl = super.getTeXFontShapeDeclaration(name, shape);

      String newName = decl.getName()+"shape";

      ControlSequence newDecl = getControlSequence(newName);

      return newDecl == null ? decl : new Obsolete(decl, newDecl);
   }

   public FontWeightDeclaration getFontWeightDeclaration(String name, int weight)
   {
      return new FontWeightDeclaration(name, weight);
   }

   public FontSizeDeclaration getFontSizeDeclaration(String name, int size)
   {
      return new FontSizeDeclaration(name, size);
   }

   public FontShapeDeclaration getFontShapeDeclaration(String name, int shape)
   {
      return new FontShapeDeclaration(name, shape);
   }

   public FontFamilyDeclaration getFontFamilyDeclaration(String name, int family)
   {
      return new FontFamilyDeclaration(name, family);
   }

   public Environment createEnvironment(String name)
   {
      Environment env = envTable.get(name);

      if (env == null)
      {
         if (isInlineMathEnv(name))
         {
            return new Environment(name, TeXSettings.MODE_INLINE_MATH);
         }

         if (isDisplayMathEnv(name))
         {
            return new Environment(name, TeXSettings.MODE_DISPLAY_MATH);
         }
      }

      return env == null ? new Environment(name) : (Environment)env.clone();
   }

   public void environment(Environment env)
     throws IOException
   {
      env.process(parser);
   }

   public boolean isInDocEnv()
   {
      return docEnvFound;
   }

   public void beginDocument()
     throws IOException
   {
      if (docEnvFound)
      {
         throw new LaTeXSyntaxException(
            parser,
            LaTeXSyntaxException.ERROR_MULTI_BEGIN_DOC);
      }

      docEnvFound = true;
   }

   public void endDocument()
     throws IOException
   {
      if (!docEnvFound)
      {
         throw new LaTeXSyntaxException(
            parser,
            LaTeXSyntaxException.ERROR_NO_BEGIN_DOC);
      }

      throw new EOFException();
   }

   public LaTeXFile getDocumentClass()
   {
      return docCls;
   }

   public void documentclass(KeyValList options,
     String clsName)
     throws IOException
   {
      if (docCls != null)
      {
         throw new LaTeXSyntaxException(
            parser,
            LaTeXSyntaxException.ERROR_MULTI_CLS);
      }

      docCls = new LaTeXFile(parser, options, clsName, "cls");

      addFileReference(docCls);

      LaTeXCls cls = getLaTeXCls(clsName);

      if (cls != null)
      {
         cls.load(this, parser, options);
      }
   }

   public LaTeXCls getLaTeXCls(String clsName)
    throws IOException
   {
      if (clsName.equals("jmlr"))
      {
         return new JmlrCls();
      }

      if (clsName.equals("jmlrbook"))
      {
         return new JmlrBookCls();
      }

      return null;
   }

   public void usepackage(KeyValList options, String styName)
   throws IOException
   {
      if (!isStyLoaded(styName))
      {
         LaTeXFile lfile = new LaTeXFile(parser, options, styName, "sty");

         addFileReference(lfile);
         loadedPackages.add(lfile);

         LaTeXSty sty = getLaTeXSty(styName);

         if (sty != null)
         {
            sty.load(this, parser, options);
         }
      }
   }

   public LaTeXSty getLaTeXSty(String styName)
   throws IOException
   {
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

         return new GraphicsSty(styName);
      }

      if (styName.equals("amsmath"))
      {
         return new AmsmathSty(styName);
      }

      if (styName.equals("lipsum"))
      {
         return new LipsumSty();
      }

      return null;
   }

   public abstract void substituting( 
    String original, String replacement)
     throws IOException;

   public abstract void includegraphics( 
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

   public void input(TeXPath path)
     throws IOException
   {
      if (path.toString().endsWith("tcilatex.tex"))
      {
         usepackage(null, "amsmath");
         usepackage(null, "graphicx");
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
      }
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

   private Hashtable<String,Environment> envTable;

   private Vector<String> inLineMathEnv, displayMathEnv;

   private Vector<LaTeXFile> loadedPackages;

   private LaTeXFile docCls;

   private TeXObjectList graphicsPath = null;

   private boolean docEnvFound = false;
}
