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

      if (outDir != null)
      {
         this.outPath = outDir.toPath();
      }

      this.texApp = texApp;
      setReplaceGraphicsPath(replaceGraphicsPath);

      if (outCharset == null)
      {
         this.outCharset = texApp.getDefaultCharset();
      }
      else
      {
         this.outCharset = outCharset;
      }

      setWriteable(this);
   }

   @Override
   public File getOutputDir()
   {
      return outPath == null ? null : outPath.toFile();
   }

   public void setReplaceGraphicsPath(boolean replaceGraphicsPath)
   {
      this.replaceGraphicsPath = replaceGraphicsPath;
   }

   public boolean isReplaceGraphicsPathEnabled()
   {
      return replaceGraphicsPath;
   }

   /**
    * Set the destination path for images. If null, the relative
    * path structure of the original document will be maintained.
    */ 
   public void setImageDestinationPath(Path p)
   {
      imageDestPath = p;
   }

   public Path getImageDestinationPath()
   {
      return imageDestPath;
   }

   @Override
   protected void addPredefined()
   {
      super.addPredefined();

      putControlSequence(new L2LJobname());
      putControlSequence(new L2LMathDeclaration("math"));

      L2LMathDeclaration begMathDecl = new L2LMathDeclaration("(");
      putControlSequence(begMathDecl);
      putControlSequence(new EndDeclaration(")", begMathDecl));

      L2LMathDeclaration begDispDecl =
         new L2LMathDeclaration("[", TeXMode.DISPLAY_MATH);

      putControlSequence(begDispDecl);
      putControlSequence(new EndDeclaration("]", begDispDecl));

      putControlSequence(
         new L2LMathDeclaration("displaymath", TeXMode.DISPLAY_MATH));
      putControlSequence(
         new L2LMathDeclaration("equation", TeXMode.DISPLAY_MATH, true));
      putControlSequence(
         new L2LMathDeclaration("equation*", TeXMode.DISPLAY_MATH));

      putControlSequence(
         new L2LMathDeclaration("align", TeXMode.DISPLAY_MATH, true));
      putControlSequence(
         new L2LMathDeclaration("align*", TeXMode.DISPLAY_MATH));

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

   @Override
   protected void addMathFontCommand(String name, TeXFontMath style)
   {
      parser.putControlSequence(new L2LMathFontCommand(name, style));
   }

   @Override
   public ControlSequence getControlSequence(String name)
   {
      ControlSequence cs = super.getControlSequence(name);

      if (!(isReplaceCmd(name) || cs instanceof L2LControlSequence))
      {
         if (isSkipCmd(name))
         {
            cs = new L2LIgnoreable(name);
         }
         else
         {
            cs = new L2LControlSequence(name);
         }
      }

      return cs;
   }

   @Override
   public ControlSequence createUndefinedCs(String name)
   {
      return new L2LControlSequence(name);
   }

   @Override
   public Comment createComment()
   {
      return new L2LComment();
   }

   @Override
   public SkippedSpaces createSkippedSpaces()
   {
      return new L2LSkippedSpaces();
   }

   @Override
   public SkippedEols createSkippedEols()
   {
      return new L2LSkippedEols();
   }

   @Override
   public Eol getEol()
   {
      return new L2LEol();
   }

   @Override
   public Space getSpace()
   {
      return new L2LSpace();
   }

   @Override
   public ActiveChar getActiveChar(int charCode)
   {
      return new L2LActiveChar(charCode);
   }

   @Override
   public Param getParam(int digit)
   {
      return new L2LParam(digit);
   }

   @Override
   public DoubleParam getDoubleParam(ParameterToken param)
   {
      return new L2LDoubleParam(param);
   }

   @Override
   public Other getOther(int charCode)
   {
      return new L2LOther(charCode);
   }

   @Override
   public Par getPar()
   {
      return new L2LPar();
   }

   @Override
   public Tab getTab()
   {
      return new L2LTab();
   }

   @Override
   public BigOperator createBigOperator(String name, int code1, int code2)
   {
      return new L2LBigOperator(name, code1, code2);
   }

   @Override
   public Symbol createSymbol(String name, int code)
   {
      return new L2LSymbol(name, code);
   }

   @Override
   public ControlSequence createSymbol(String name, int code, FontEncoding enc)
   {
      return new L2LSymbol(name, code);
   }

   @Override
   public GreekSymbol createGreekSymbol(String name, int code)
   {
      return new L2LGreekSymbol(name, code);
   }

   @Override
   public BinarySymbol createBinarySymbol(String name, int code)
   {
      return new L2LBinarySymbol(name, code);
   }

   @Override
   public MathSymbol createMathSymbol(String name, int code)
   {
      return new L2LMathSymbol(name, code);
   }

   @Override
   public Group createGroup()
   {
      return new L2LGroup();
   }

   @Override
   public Group createGroup(String text)
   {
      return new L2LGroup(this, text);
   }

   @Override
   public MathGroup createMathGroup()
   {
      return new L2LMathGroup();
   }

   @Override
   public void beginDocument(TeXObjectList stack)
     throws IOException
   {
      super.beginDocument(stack);

      writeCodePoint(parser.getEscChar());
      write("begin");
      writeCodePoint(parser.getBgChar());
      write("document");
      writeCodePoint(parser.getEgChar());
   }

   @Override
   public void endDocument(TeXObjectList stack)
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

         super.endDocument(stack);
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

   @Override
   public void documentclass(KeyValList options, String clsName, 
     boolean loadParentOptions, TeXObjectList stack)
     throws IOException
   {
      if (docCls != null)
      {
         throw new LaTeXSyntaxException(parser,
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

   @Override
   public LaTeXSty requirepackage(KeyValList options, String styName, 
     boolean loadParentOptions, TeXObjectList stack)
     throws IOException
   {
      LaTeXSty sty = getLaTeXSty(options, styName, loadParentOptions, stack);
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

   @Override
   public LaTeXSty usepackage(KeyValList options, String styName, 
     boolean loadParentOptions, TeXObjectList stack)
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
         graphicsSty.registerControlSequence(new IncludeGraphics());
      }

      LaTeXSty sty = getLaTeXSty(options, styName, loadParentOptions, stack);
      addFileReference(sty);
      loadedPackages.add(sty);

      if (styName.equals("inputenc"))
      {
         try
         {
            String enc = InputEncSty.getOption(parser, outCharset);

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
            getParser().error(e);
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

   @Override
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
      if (outPath == null) return null;

      if (grpaths == null)
      {
         File file = path.getFile();

         if (file.exists())
         {
            File destFile = outPath.resolve(path.getRelativePath()).toFile();

            copyImageFile(file, destFile);

            return path.getRelativePath();
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
            ).toPath()).resolve(path.getRelativePath());

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

   public Path copyImageFile(String[] grpaths, TeXPath orgPath, Path destPath)
    throws IOException,InterruptedException
   {
      if (outPath == null) return null;

      if (grpaths == null)
      {
         File file = orgPath.getFile();

         if (file.exists())
         {
            File destFile = outPath.resolve(destPath).toFile();

            copyImageFile(file, destFile);

            return destPath;
         }
      }
      else
      {
         Path basePath = orgPath.getBaseDir();

         for (int i = 0; i < grpaths.length; i++)
         {
            Path subPath = 
            (new File(File.separatorChar == '/' ?
              grpaths[i] : 
              grpaths[i].replaceAll("/", File.separator)
            ).toPath()).resolve(orgPath.getRelativePath());

            File file = (basePath == null ?  subPath :
              basePath.resolve(subPath)).toFile();

            if (file.exists())
            {
               File destFile = outPath.resolve(destPath).toFile();

               copyImageFile(file, destFile);

               return destPath;
            }
         }
      }

      return null;
   }

   @Override
   public void includegraphics(TeXObjectList stack, KeyValList options, String imgName)
     throws IOException
   {
      String[] grpaths = getGraphicsPaths();

      Path imagePath = null;

      try
      {
         if (imgName.contains("."))
         {
            TeXPath path = new TeXPath(parser, imgName);

            if (imageDestPath == null)
            {
               imagePath = copyImageFile(grpaths, path);
            }
            else
            {
               imagePath =
                  copyImageFile(grpaths, path, 
                    imageDestPath.resolve(path.getLeaf()));
            }
         }
         else
         {
            for (int i = 0; i < imageExtensions.length; i++)
            {
                String name = imgName+imageExtensions[i];

                TeXPath path = new TeXPath(parser, name);

                if (imageDestPath == null)
                {
                   imagePath = copyImageFile(grpaths, path);
                }
                else
                {
                   imagePath =
                      copyImageFile(grpaths, path, 
                        imageDestPath.resolve(path.getLeaf()));
                }

                if (imagePath != null)
                {
                   break;
                }
            }
         }
      }
      catch (InterruptedException e)
      {
         getParser().error(e);
      }

      if (imagePath != null
           && (isReplaceGraphicsPathEnabled() || imageDestPath != null))
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

         if (file.exists() && outPath != null)
         {
             Path dest = bibPaths[i].getRelativePath();

             if (dest.isAbsolute())
             {
                dest = outPath.resolve(bibPaths[i].getLeaf());
             }
             else
             {
                dest = outPath.resolve(bibPaths[i].getRelativePath());
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
   }

   @Override
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
         getParser().warning("null writer");
      }
   }

   @Override
   public void write(char c) throws IOException
   {
      if (writer != null)
      {
         writer.print(c);
      }
      else
      {
         getParser().warning("null writer");
      }
   }

   @Override
   public void write(String string) throws IOException
   {
      if (writer != null)
      {
         writer.print(string);
      }
      else
      {
         getParser().warning("null writer");
      }
   }

   @Override
   public void writeln(String string) throws IOException
   {
      if (writer != null)
      {
         writer.println(string);
      }
      else
      {
         getParser().warning("null writer");
      }
   }

   @Override
   public void writeliteral(String string) throws IOException
   {
      write(string);
   }

   @Override
   public void writeliteralln(String string) throws IOException
   {
      writeln(string);
   }

   public void writeln(char c) throws IOException
   {
      if (writer != null)
      {
         writer.println(c);
      }
      else
      {
         getParser().warning("null writer");
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
         getParser().warning("null writer");
      }
   }

   @Override
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

   @Override
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

   @Override
   public void skipping(Ignoreable ignoreable)
     throws IOException
   {
      write(ignoreable.toString(getParser()));
   }

   @Override
   public void subscript(TeXObject arg)
     throws IOException
   {
      writeCodePoint(parser.getSbChar());
      writeCodePoint(parser.getBgChar());
      write(arg.toString(parser));
      writeCodePoint(parser.getEgChar());
   }

   @Override
   public void superscript(TeXObject arg)
     throws IOException
   {
      writeCodePoint(parser.getSpChar());
      writeCodePoint(parser.getBgChar());
      write(arg.toString(parser));
      writeCodePoint(parser.getEgChar());
   }

   @Override
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

   @Override
   public void beginParse(File file, Charset encoding)
     throws IOException
   {
      getParser().message(TeXApp.MESSAGE_READING, file.toString());

      if (encoding != null)
      {
         getParser().message(TeXApp.MESSAGE_ENCODING, encoding.name());
      }

      basePath = file.getParentFile().toPath();

      if (writer == null)
      {
         Files.createDirectories(outPath);

         File outFile = new File(outPath.toFile(), getOutFileName(file));

         getParser().message(TeXApp.MESSAGE_WRITING, outFile);

         getParser().message(TeXApp.MESSAGE_ENCODING, outCharset);

         writer = new PrintWriter(Files.newBufferedWriter(outFile.toPath(),
           outCharset));
      }
   }

   public String getOutFileName(File inFile)
   {
      return inFile.getName();
   }

   @Override
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

   @Override
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

   @Override
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

   @Override
   public void newcommand(boolean isRobust, Overwrite overwrite,
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

   @Override
   public void newenvironment(Overwrite overwrite, String type, String envName,
     int numParams, TeXObject defValue, TeXObject definition, TeXObject endDefinition)
   throws IOException
   {
      String bg = new String(Character.toChars(parser.getBgChar()));
      String eg = new String(Character.toChars(parser.getEgChar()));
      String esc = new String(Character.toChars(parser.getEscChar()));
      
      write(esc);
      write(type);
      write(bg+envName+eg);

      if (numParams > 0)
      {
         write("["+numParams+"]");

         if (defValue != null)
         {
            write("["+defValue.toString(parser)+"]");
         }
      }

      write(bg + definition.toString(parser) + eg);
      write(bg + endDefinition.toString(parser) + eg);
   }

   public boolean isReplaceJobnameOn()
   {
      return replaceJobname;
   }

   public void enableReplaceJobname(boolean enable)
   {
      this.replaceJobname = enable;
   }

   private Path outPath, basePath, imageDestPath;
   private PrintWriter writer;

   private Charset outCharset=null;

   private boolean replaceGraphicsPath = false;
   private boolean replaceJobname = false; 

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
      "newenvironment", "renewenvironment", "bibliography"
   };

   public static final String[] SKIP_CMDS = new String[]
   {
      "bigskip"
   };
}
