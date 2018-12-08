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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.nio.file.Path;
import java.nio.file.Files;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.graphics.*;
import com.dickimawbooks.texparserlib.latex.inputenc.InputEncSty;

/**
 * Reads in and writes out LaTeX code, replacing
 * obsolete/problematic commands.
 */

public class LaTeX2LaTeX extends LaTeXParserListener
  implements Writeable
{
   public LaTeX2LaTeX(TeXApp texApp, File outDir)
     throws IOException
   {
      this(texApp, outDir, null, false);
   }

   public LaTeX2LaTeX(TeXApp texApp, File outDir, Charset outCharset)
     throws IOException
   {
      this(texApp, outDir, outCharset, false);
   }

   public LaTeX2LaTeX(TeXApp texApp, File outDir, boolean replaceGraphicsPath)
     throws IOException
   {
      this(texApp, outDir, null, replaceGraphicsPath);
   }

   public LaTeX2LaTeX(TeXApp texApp, File outDir, Charset outCharset, 
      boolean replaceGraphicsPath)
     throws IOException
   {
      super(null);
      this.outPath = outDir.toPath();
      this.texApp = texApp;
      setReplaceGraphicsPath(replaceGraphicsPath);

      setWriteable(this);
   }

   public void setReplaceGraphicsPath(boolean replaceGraphicsPath)
   {
      this.replaceGraphicsPath = replaceGraphicsPath;
   }

   public boolean isReplaceGraphicsPathEnabled()
   {
      return replaceGraphicsPath;
   }

   protected void addPredefined()
   {
      super.addPredefined();

      putControlSequence(new L2LMathDeclaration("math"));

      L2LMathDeclaration begMathDecl = new L2LMathDeclaration("(");
      putControlSequence(begMathDecl);
      putControlSequence(new EndDeclaration(")", begMathDecl));

      L2LMathDeclaration begDispDecl =
         new L2LMathDeclaration("[", TeXSettings.MODE_DISPLAY_MATH);

      putControlSequence(begDispDecl);
      putControlSequence(new EndDeclaration("]", begDispDecl));

      putControlSequence(
         new L2LMathDeclaration("displaymath", TeXSettings.MODE_DISPLAY_MATH));
      putControlSequence(
         new L2LMathDeclaration("equation", TeXSettings.MODE_DISPLAY_MATH, true));
      putControlSequence(
         new L2LMathDeclaration("equation*", TeXSettings.MODE_DISPLAY_MATH));

      putControlSequence(
         new L2LMathDeclaration("align", TeXSettings.MODE_DISPLAY_MATH, true));
      putControlSequence(
         new L2LMathDeclaration("align*", TeXSettings.MODE_DISPLAY_MATH));

      putControlSequence(new L2LBibliography());
      putControlSequence(new L2LVerbatim());
      putControlSequence(new L2LVerbatim("verbatim*"));
      putControlSequence(new L2LVerbatim("lstlisting"));
      putControlSequence(new L2LBegin());
      putControlSequence(new L2LEnd());
      putControlSequence(new Verb("lstinline"));
      putControlSequence(new Input());
      putControlSequence(new Input("include"));
   }

   protected void addMathFontCommand(String name, int style)
   {
      parser.putControlSequence(new L2LMathFontCommand(name, style));
   }

   public ControlSequence getControlSequence(String name)
   {
      if (isSkipCmd(name))
      {
         return new L2LIgnoreable(name);
      }

      if (isReplaceCmd(name))
      {
         return super.getControlSequence(name);
      }

      return new L2LControlSequence(name);
   }

   public ControlSequence createUndefinedCs(String name)
   {
      return new L2LControlSequence(name);
   }

   public Comment createComment()
   {
      return new L2LComment();
   }

   public SkippedSpaces createSkippedSpaces()
   {
      return new L2LSkippedSpaces();
   }

   public SkippedEols createSkippedEols()
   {
      return new L2LSkippedEols();
   }

   public Eol getEol()
   {
      return new L2LEol();
   }

   public Space getSpace()
   {
      return new L2LSpace();
   }

   public ActiveChar getActiveChar(int charCode)
   {
      return new L2LActiveChar(charCode);
   }

   public Param getParam(int digit)
   {
      return new L2LParam(digit);
   }

   public DoubleParam getDoubleParam(ParameterToken param)
   {
      return new L2LDoubleParam(param);
   }

   public Other getOther(int charCode)
   {
      return new L2LOther(charCode);
   }

   public Par getPar()
   {
      return new L2LPar();
   }

   public Tab getTab()
   {
      return new L2LTab();
   }

   public BigOperator createBigOperator(String name, int code1, int code2)
   {
      return new L2LBigOperator(name, code1, code2);
   }

   public Symbol createSymbol(String name, int code)
   {
      return new L2LSymbol(name, code);
   }

   public ControlSequence createSymbol(String name, int code, FontEncoding enc)
   {
      return new L2LSymbol(name, code);
   }

   public GreekSymbol createGreekSymbol(String name, int code)
   {
      return new L2LGreekSymbol(name, code);
   }

   public BinarySymbol createBinarySymbol(String name, int code)
   {
      return new L2LBinarySymbol(name, code);
   }

   public MathSymbol createMathSymbol(String name, int code)
   {
      return new L2LMathSymbol(name, code);
   }

   public Group createGroup()
   {
      return new L2LGroup();
   }

   public Group createGroup(String text)
   {
      return new L2LGroup(this, text);
   }

   public MathGroup createMathGroup()
   {
      return new L2LMathGroup();
   }

   public void beginDocument()
     throws IOException
   {
      super.beginDocument();

      writeCodePoint(parser.getEscChar());
      write("begin");
      writeCodePoint(parser.getBgChar());
      write("document");
      writeCodePoint(parser.getEgChar());
   }

   public void endDocument()
     throws IOException
   {
      try
      {
         writeCodePoint(parser.getEscChar());
         write("end");
         writeCodePoint(parser.getBgChar());
         write("document");
         writeCodePoint(parser.getEgChar());
         writeln();

         super.endDocument();
      }
      finally
      {
         if (writer != null)
         {
            writer.close();
            writer = null;
         }
      }
   }

   public void documentclass(KeyValList options, String clsName, 
     boolean loadParentOptions)
     throws IOException
   {
      if (docCls != null)
      {
         throw new LaTeXSyntaxException(
            parser,
            LaTeXSyntaxException.ERROR_MULTI_CLS);
      }

      docCls = getLaTeXCls(options, clsName, loadParentOptions);

      addFileReference(docCls);

      writeCodePoint(parser.getEscChar());
      write("documentclass");

      if (options != null)
      {
         write('[');
         write(options.toString(parser));
         write(']');
      }

      writeCodePoint(parser.getBgChar());
      write(clsName);
      writeCodePoint(parser.getEgChar());
   }

   public LaTeXSty requirepackage(KeyValList options, String styName, 
     boolean loadParentOptions)
     throws IOException
   {
      LaTeXSty sty = getLaTeXSty(options, styName, loadParentOptions);;
      addFileReference(sty);
      loadedPackages.add(sty);

      writeCodePoint(parser.getEscChar());
      write("RequirePackage");

      if (options != null)
      {
         write('[');
         write(options.toString(parser));
         write(']');
      }

      writeCodePoint(parser.getBgChar());
      write(styName);
      writeCodePoint(parser.getEgChar());

      return sty;
   }

   public LaTeXSty usepackage(KeyValList options, String styName, 
     boolean loadParentOptions)
     throws IOException
   {
      if (isStyLoaded(styName))
      {
         return null;
      }

      GraphicsSty graphicsSty = null;

      if (styName.equals("epsfig"))
      {
         graphicsSty = new GraphicsSty(options, this, false);

         graphicsSty.registerControlSequence(new Epsfig("epsfig"));
         graphicsSty.registerControlSequence(new Epsfig("psfig"));

         getTeXApp().substituting(parser, styName, "graphicx");

         styName = "graphicx";
      }
      else if (styName.equals("graphics"))
      {
         graphicsSty = new GraphicsSty(options, this, false);
         getTeXApp().substituting(parser, styName, "graphicx");

         styName = "graphicx";
      }
      else if (styName.equals("graphicx"))
      {
         graphicsSty = new GraphicsSty(options, this, false);
      }

      if (graphicsSty != null)
      {
         graphicsSty.registerControlSequence(new IncludeGraphics(graphicsSty));
      }

      LaTeXSty sty = getLaTeXSty(options, styName, loadParentOptions);;
      addFileReference(sty);
      loadedPackages.add(sty);

      if (styName.equals("inputenc"))
      {
         try
         {
            String enc = InputEncSty.getOption(parser, outCharset == null ? 
               Charset.defaultCharset() : outCharset);

            if (options == null || options.get(enc) == null)
            {
               substituting(String.format("\\usepackage[%s]{inputenc}",
                 options == null ? "" : options.toString(parser)),
                 String.format("\\usepackage[%s]{inputenc}", enc));
            }

            writeCodePoint(parser.getEscChar());
            write("usepackage[");
            write(enc);
            write(']');
            writeCodePoint(parser.getBgChar());
            write(styName);
            writeCodePoint(parser.getEgChar());
         }
         catch (LaTeXSyntaxException e)
         {
            getTeXApp().error(e);
         }

         return sty;
      }

      writeCodePoint(parser.getEscChar());
      write("usepackage");

      if (options != null)
      {
         write('[');
         write(options.toString(parser));
         write(']');
      }

      writeCodePoint(parser.getBgChar());
      write(styName);
      writeCodePoint(parser.getEgChar());

      return sty;
   }

   public void substituting(String original, String replacement)
     throws IOException
   {
      getTeXApp().substituting(parser,
        original, replacement);
   }

   protected void copyImageFile(File file, File destFile)
    throws IOException,InterruptedException
   {
      getTeXApp().copyFile(file, destFile);

      String name = destFile.getName();

      if (name.toLowerCase().endsWith(".wmf"))
      {
         File epsFile = new File(destFile.getParentFile(),
            name.substring(0, name.length()-3)+"eps");

         getTeXApp().wmftoeps(destFile, epsFile);

         destFile = epsFile;
         name = destFile.getName();
      }

      if (name.toLowerCase().endsWith(".eps"))
      {
         File pdfFile = new File(destFile.getParentFile(),
            name.substring(0, name.length()-3)+"pdf");

         getTeXApp().epstopdf(destFile, pdfFile);
      }
   }

   public Path copyImageFile(String[] grpaths, TeXPath path)
    throws IOException,InterruptedException
   {
      if (grpaths == null)
      {
         File file = path.getFile();

         if (file.exists())
         {
            File destFile = outPath.resolve(path.getRelative()).toFile();

            copyImageFile(file, destFile);

            return path.getRelative();
         }
      }
      else
      {
         Path basePath = path.getBaseDir();

         for (int i = 0; i < grpaths.length; i++)
         {
            Path subPath = 
            (new File(File.separatorChar == '/' ?
              grpaths[i] : 
              grpaths[i].replaceAll("/", File.separator)
            ).toPath()).resolve(path.getRelative());

            File file = (basePath == null ?  subPath :
              basePath.resolve(subPath)).toFile();

            if (file.exists())
            {
               File destFile = outPath.resolve(subPath).toFile();

               copyImageFile(file, destFile);

               return subPath;
            }
         }
      }

      return null;
   }

   public void includegraphics(KeyValList options, String imgName)
     throws IOException
   {
      String[] grpaths = getGraphicsPaths();

      Path imagePath = null;

      try
      {
         if (imgName.contains("."))
         {
            TeXPath path = new TeXPath(parser, imgName);

            imagePath = copyImageFile(grpaths, path);
         }
         else
         {
            for (int i = 0; i < IMAGE_EXT.length; i++)
            {
                String name = imgName+"."+IMAGE_EXT[i];

                TeXPath path = new TeXPath(parser, name);

                imagePath = copyImageFile(grpaths, path);

                if (imagePath != null)
                {
                   break;
                }
            }
         }
      }
      catch (InterruptedException e)
      {
         getTeXApp().error(e);
      }

      if (isReplaceGraphicsPathEnabled() && imagePath != null)
      {
         StringBuilder builder = new StringBuilder();

         Iterator<Path> it = imagePath.iterator();

         while (it.hasNext())
         {
            if (builder.length() > 0)
            {
               builder.append('/');
            }

            builder.append(it.next().toString());
         }

         imgName = builder.toString();
      }

      writeCodePoint(parser.getEscChar());
      write("includegraphics");

      if (options != null && options.size() > 0)
      {
         write('[');
         write(options.toString(parser));
         write(']');
      }

      writeCodePoint(parser.getBgChar());

      String lc = imgName.toLowerCase();

      if (lc.endsWith(".eps")
       || lc.endsWith(".ps")
       || lc.endsWith(".wmf"))
      {
         write(imgName.substring(0, imgName.lastIndexOf(".")));
      }
      else
      {
         write(imgName);
      }

      writeCodePoint(parser.getEgChar());

   }

   public void setGraphicsPath(TeXObjectList paths)
     throws IOException
   {
      super.setGraphicsPath(paths);

      if (!isReplaceGraphicsPathEnabled())
      {
         writeCodePoint(parser.getEscChar());
         write("graphicspath");

         int bg = parser.getBgChar();
         int eg = parser.getEgChar();

         writeCodePoint(bg);

         for (TeXObject path : paths)
         {
            writeCodePoint(bg);
            write(path.toString(parser));
            writeCodePoint(eg);
         }

         writeCodePoint(eg);
      }
   }

   public void bibliography(TeXPath[] bibPaths)
     throws IOException
   {
      for (int i = 0; i < bibPaths.length; i++)
      {
         if (bibPaths[i].wasFoundByKpsewhich())
         {
            continue;
         }

         File file = bibPaths[i].getFile();

         if (file.exists())
         {
             Path dest = bibPaths[i].getRelative();

             if (dest.isAbsolute())
             {
                dest = outPath.resolve(bibPaths[i].getLeaf());
             }
             else
             {
                dest = outPath.resolve(bibPaths[i].getRelative());
             }

             try
             {
                getTeXApp().copyFile(file, dest.toFile());
             }
             catch (InterruptedException e)
             {
                getTeXApp().error(e);
             }
         }
      }
   }

   public void writeCodePoint(int charCode) throws IOException
   {
      if (writer != null)
      {
         if (charCode <= Character.MAX_VALUE)
         {
            writer.print((char)charCode);
         }
         else
         {
            for (char c : Character.toChars(charCode))
            {
               writer.print(c);
            }
         }
      }
      else
      {
         getTeXApp().warning(getParser(), "null writer");
      }
   }

   public void write(char c) throws IOException
   {
      if (writer != null)
      {
         writer.print(c);
      }
      else
      {
         getTeXApp().warning(getParser(), "null writer");
      }
   }

   public void write(String string) throws IOException
   {
      if (writer != null)
      {
         writer.print(string);
      }
      else
      {
         getTeXApp().warning(getParser(), "null writer");
      }
   }

   public void writeln(String string) throws IOException
   {
      if (writer != null)
      {
         writer.println(string);
      }
      else
      {
         getTeXApp().warning(getParser(), "null writer");
      }
   }

   public void writeln(char c) throws IOException
   {
      if (writer != null)
      {
         writer.println(c);
      }
      else
      {
         getTeXApp().warning(getParser(), "null writer");
      }
   }

   public void writeln() throws IOException
   {
      if (writer != null)
      {
         writer.println();
      }
      else
      {
         getTeXApp().warning(getParser(), "null writer");
      }
   }

   public void overwithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXObject before, TeXObject after)
    throws IOException
   {
      int esc = parser.getEscChar();
      int bg = parser.getBgChar();
      int eg = parser.getEgChar();

      if (firstDelim instanceof Other 
       && secondDelim instanceof Other
       && ((Other)firstDelim).getCharCode()=='.'
       && ((Other)secondDelim).getCharCode()=='.')
      {
         writeCodePoint(esc);
         write("frac");
         writeCodePoint(bg);
         write(before.toString(parser));
         writeCodePoint(eg);
         writeCodePoint(bg);
         write(after.toString(parser));
         writeCodePoint(eg);
      }
      else if (isStyLoaded("amsmath"))
      {
         writeCodePoint(esc);
         write("genfrac");

         // left-delim:
         writeCodePoint(bg);
         write(firstDelim.toString(parser));
         writeCodePoint(eg);

         // right-delim:
         writeCodePoint(bg);
         write(secondDelim.toString(parser));
         writeCodePoint(eg);

         // thickness:
         writeCodePoint(bg);
         writeCodePoint(eg);

         // mathstyle:
         writeCodePoint(bg);
         writeCodePoint(eg);

         // numerator:
         writeCodePoint(bg);
         write(before.toString(parser));
         writeCodePoint(eg);

         // denominator:
         writeCodePoint(bg);
         write(after.toString(parser));
         writeCodePoint(eg);
      }
      else
      {
         writeCodePoint(esc);
         write("left");
         write(firstDelim.toString(parser));
         writeCodePoint(esc);
         write("frac");
         writeCodePoint(bg);
         write(before.toString(parser));
         writeCodePoint(eg);
         writeCodePoint(bg);
         write(after.toString(parser));
         writeCodePoint(eg);
         writeCodePoint(esc);
         write("right");
         write(secondDelim.toString(parser));
      }
   }

   public void abovewithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness, 
     TeXObject before, TeXObject after)
    throws IOException
   {
      int esc = parser.getEscChar();
      int bg = parser.getBgChar();
      int eg = parser.getEgChar();

      if (isStyLoaded("amsmath"))
      {
         writeCodePoint(esc);
         write("genfrac");

         // left-delim:
         writeCodePoint(bg);
         write(firstDelim.toString(parser));
         writeCodePoint(eg);

         // right-delim:
         writeCodePoint(bg);
         write(secondDelim.toString(parser));
         writeCodePoint(eg);

         // thickness:
         writeCodePoint(bg);
         write(thickness.toString(parser));
         writeCodePoint(eg);

         // mathstyle:
         writeCodePoint(bg);
         writeCodePoint(eg);

         // numerator:
         writeCodePoint(bg);
         write(before.toString(parser));
         writeCodePoint(eg);

         // denominator:
         writeCodePoint(bg);
         write(after.toString(parser));
         writeCodePoint(eg);
      }
      else if (firstDelim instanceof Other 
       && secondDelim instanceof Other
       && ((Other)firstDelim).getCharCode()=='.'
       && ((Other)secondDelim).getCharCode()=='.')
      {
         writeCodePoint(bg);
         write(before.toString(parser));
         writeCodePoint(esc);
         write("above ");
         write(thickness.toString(parser));
         write(after.toString(parser));
         writeCodePoint(eg);
      }
      else
      {
         writeCodePoint(bg);
         write(before.toString(parser));
         writeCodePoint(esc);
         write("abovewithdelims ");
         write(firstDelim.toString(parser));
         write(secondDelim.toString(parser));
         write(thickness.toString(parser));
         write(after.toString(parser));
         writeCodePoint(eg);
      }
   }

   public void skipping(Ignoreable ignoreable)
     throws IOException
   {
      write(ignoreable.toString(getParser()));
   }

   public void subscript(TeXObject arg)
     throws IOException
   {
      writeCodePoint(parser.getSbChar());
      writeCodePoint(parser.getBgChar());
      write(arg.toString(parser));
      writeCodePoint(parser.getEgChar());
   }

   public void superscript(TeXObject arg)
     throws IOException
   {
      writeCodePoint(parser.getSpChar());
      writeCodePoint(parser.getBgChar());
      write(arg.toString(parser));
      writeCodePoint(parser.getEgChar());
   }

   public TeXApp getTeXApp()
   {
      return texApp;
   }

   public boolean isReplaceCmd(String name)
   {
      for (int i = 0; i < CHECK_CMDS.length; i++)
      {
         if (CHECK_CMDS[i].equals(name)) return true;
      }

      return false;
   }

   public boolean isSkipCmd(String name)
   {
      for (int i = 0; i < SKIP_CMDS.length; i++)
      {
         if (SKIP_CMDS[i].equals(name)) return true;
      }

      return false;
   }

   public void beginParse(File file, Charset encoding)
     throws IOException
   {
      getTeXApp().message(getTeXApp().getMessage(
         TeXApp.MESSAGE_READING, file));

      if (encoding != null)
      {
         getTeXApp().message(getTeXApp().getMessage(
            TeXApp.MESSAGE_ENCODING, encoding));
      }

      basePath = file.getParentFile().toPath();

      if (writer == null)
      {
         Files.createDirectories(outPath);

         File outFile = new File(outPath.toFile(), getOutFileName(file));

         getTeXApp().message(getTeXApp().getMessage(
            TeXApp.MESSAGE_WRITING, outFile));

         if (outCharset == null)
         {
            writer = new PrintWriter(outFile);
         }
         else
         {
            getTeXApp().message(getTeXApp().getMessage(
               TeXApp.MESSAGE_ENCODING, outCharset));

            writer = new PrintWriter(outFile, outCharset.name());
         }
      }
   }

   public String getOutFileName(File inFile)
   {
      return inFile.getName();
   }

   public void endParse(File file)
    throws IOException
   {
      TeXReader reader = getParser().getReader();

      if (reader != null)
      {
         reader = reader.getParent();
      }

      if (writer != null && reader == null)
      {
         writer.flush();
         writer.close();
         writer = null;
      }
   }

   public void href(String url, TeXObject text)
    throws IOException
   {
      int bg = parser.getBgChar();
      int eg = parser.getEgChar();

      writeCodePoint(parser.getEscChar());
      write("href");
      writeCodePoint(bg);
      write(url);
      writeCodePoint(eg);
      writeCodePoint(bg);
      write(text.toString(parser));
      writeCodePoint(eg);
   }

   public void verb(String name, boolean isStar, int delim, String text)
     throws IOException
   {
      writeCodePoint(parser.getEscChar());
      write(name);

      if (isStar)
      {
         write("*");
      }
      else if (parser.isLetter(delim))
      {
         write(" ");
      }

      writeCodePoint(delim);
      write(text);
      writeCodePoint(delim);
   }

   public void newcommand(byte overwrite,
     String type, String csName, boolean isShort,
     int numParams, TeXObject defValue, TeXObject definition)
    throws IOException
   {
      String bg = new String(Character.toChars(parser.getBgChar()));
      String eg = new String(Character.toChars(parser.getEgChar()));
      String esc = new String(Character.toChars(parser.getEscChar()));

      write(esc);
      write(type);

      if (isShort)
      {
         write('*');
      }

      write(String.format("%s%s%s%s", bg, esc, csName, eg));

      if (numParams > 0)
      {
         write("["+numParams+"]");

         if (defValue != null)
         {
            write("["+defValue.toString(parser)+"]");
         }
      }

      write(String.format("%s%s%s", bg, definition.toString(parser), eg));
   }

   private Path outPath, basePath;
   private PrintWriter writer;

   private Charset outCharset=null;

   private boolean replaceGraphicsPath = false;

   private TeXApp texApp;

   public static final String[] CHECK_CMDS = new String[]
   {
      "epsfig", "psfig", "centerline", "special", 
      "rm", "tt", "sf", "bf", "it", "sl", "sc", "cal",
      "includegraphics", "usepackage", "graphicspath",
      "documentclass", "documentstyle", "begin", "end",
      "FRAME", "Qcb",
      "verb", "lstinline", "verbatim", "endverbatim",
      "lstlistings", "lstlistings*",
      "[", "]", "(", ")",
      "displaymath", "enddisplaymath", "math", "endmath",
      "equation", "endequation", "equation*", "endequation*",
      "align", "endalign", "align*", "endalign*",
      "input", "newcommand", "renewcommand", "providecommand",
      "bibliography"
   };

   public static final String[] SKIP_CMDS = new String[]
   {
      "bigskip"
   };
}
