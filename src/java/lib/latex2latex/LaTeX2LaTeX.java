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
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.nio.file.Path;
import java.nio.file.Files;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;

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
      this(texApp, outDir, false);
   }

   public LaTeX2LaTeX(TeXApp texApp, File outDir, boolean replaceGraphicsPath)
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

   public DoubleParam getDoubleParam(Param param)
   {
      return new L2LDoubleParam((L2LParam)param);
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

      write(parser.getEscChar());
      write("begin");
      write(parser.getBgChar());
      write("document");
      write(parser.getEgChar());
   }

   public void endDocument()
     throws IOException
   {
      try
      {
         write(parser.getEscChar());
         write("end");
         write(parser.getBgChar());
         write("document");
         writeln(parser.getEgChar());

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

   public void documentclass(KeyValList options, String clsName)
     throws IOException
   {
      super.documentclass(options, clsName);

      write(parser.getEscChar()+"documentclass");

      if (options != null)
      {
         write('[');
         write(options.toString(parser));
         write(']');
      }

      write(parser.getBgChar()+clsName+parser.getEgChar());
   }

   public void usepackage(KeyValList options, String styName) throws IOException
   {
      if (styName.equals("graphics") || styName.equals("epsfig"))
      {
         getTeXApp().substituting(parser.getLineNumber(),
           styName, "graphicx");

         styName = "graphicx";
      }

      super.usepackage(options, styName);

      write(parser.getEscChar());
      write("usepackage");

      if (options != null)
      {
         write('[');
         write(options.toString(parser));
         write(']');
      }

      write(parser.getBgChar());
      write(styName);
      write(parser.getEgChar());
   }

   public void substituting(String original, String replacement)
     throws IOException
   {
      getTeXApp().substituting(parser.getLineNumber(),
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

   protected Path copyImageFile(String[] grpaths, TeXPath path)
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

      write(parser.getEscChar());
      write("includegraphics");

      if (options != null && options.size() > 0)
      {
         write('[');
         write(options.toString(parser));
         write(']');
      }

      write(parser.getBgChar());

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

      write(parser.getEgChar());

   }

   public void setGraphicsPath(TeXObjectList paths)
     throws IOException
   {
      super.setGraphicsPath(paths);

      if (!isReplaceGraphicsPathEnabled())
      {
         write(parser.getEscChar());
         write("graphicspath");

         String bg = ""+parser.getBgChar();
         String eg = ""+parser.getEgChar();

         write(bg);

         for (TeXObject path : paths)
         {
            write(bg+path.toString(parser)+eg);
         }

         write(eg);
      }
   }

   public void bibliography(TeXPath[] bibPaths)
     throws IOException
   {
      for (int i = 0; i < bibPaths.length; i++)
      {
         File file = bibPaths[i].getFile();

         if (file.exists())
         {
             try
             {
                getTeXApp().copyFile(file, 
                  outPath.resolve(bibPaths[i].getRelative()).toFile());
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
         writer.print((char)charCode);
      }
   }

   public void write(char c) throws IOException
   {
      if (writer != null)
      {
         writer.print(c);
      }
   }

   public void write(String string) throws IOException
   {
      if (writer != null)
      {
         writer.print(string);
      }
   }

   public void writeln(String string) throws IOException
   {
      if (writer != null)
      {
         writer.println(string);
      }
   }

   public void writeln(char c) throws IOException
   {
      if (writer != null)
      {
         writer.println(c);
      }
   }

   public void writeln() throws IOException
   {
      if (writer != null)
      {
         writer.println();
      }
   }

   public void overwithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXObject before, TeXObject after)
    throws IOException
   {
      char esc = parser.getEscChar();
      char bg = parser.getBgChar();
      char eg = parser.getEgChar();

      if (firstDelim instanceof Other 
       && secondDelim instanceof Other
       && ((Other)firstDelim).getCharCode()==(int)'.'
       && ((Other)secondDelim).getCharCode()==(int)'.')
      {
         write(esc);
         write("frac");
         write(bg);
         write(before.toString(parser));
         write(eg);
         write(bg);
         write(after.toString(parser));
         write(eg);
      }
      else if (isStyLoaded("amsmath"))
      {
         write(esc);
         write("genfrac");

         // left-delim:
         write(bg);
         write(firstDelim.toString(parser));
         write(eg);

         // right-delim:
         write(bg);
         write(secondDelim.toString(parser));
         write(eg);

         // thickness:
         write(bg);
         write(eg);

         // mathstyle:
         write(bg);
         write(eg);

         // numerator:
         write(bg);
         write(before.toString(parser));
         write(eg);

         // denominator:
         write(bg);
         write(after.toString(parser));
         write(eg);
      }
      else
      {
         write(esc);
         write("left");
         write(firstDelim.toString(parser));
         write(esc);
         write("frac");
         write(bg);
         write(before.toString(parser));
         write(eg);
         write(bg);
         write(after.toString(parser));
         write(eg);
         write(esc);
         write("right");
         write(secondDelim.toString(parser));
      }
   }

   public void abovewithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness, 
     TeXObject before, TeXObject after)
    throws IOException
   {
      char esc = parser.getEscChar();
      char bg = parser.getBgChar();
      char eg = parser.getEgChar();

      if (isStyLoaded("amsmath"))
      {
         write(esc);
         write("genfrac");

         // left-delim:
         write(bg);
         write(firstDelim.toString(parser));
         write(eg);

         // right-delim:
         write(bg);
         write(secondDelim.toString(parser));
         write(eg);

         // thickness:
         write(bg);
         write(thickness.toString(parser));
         write(eg);

         // mathstyle:
         write(bg);
         write(eg);

         // numerator:
         write(bg);
         write(before.toString(parser));
         write(eg);

         // denominator:
         write(bg);
         write(after.toString(parser));
         write(eg);
      }
      else if (firstDelim instanceof Other 
       && secondDelim instanceof Other
       && ((Other)firstDelim).getCharCode()==(int)'.'
       && ((Other)secondDelim).getCharCode()==(int)'.')
      {
         write(bg);
         write(before.toString(parser));
         write(esc);
         write("above ");
         write(thickness.toString(parser));
         write(after.toString(parser));
         write(eg);
      }
      else
      {
         write(bg);
         write(before.toString(parser));
         write(esc);
         write("abovewithdelims ");
         write(firstDelim.toString(parser));
         write(secondDelim.toString(parser));
         write(thickness.toString(parser));
         write(after.toString(parser));
         write(eg);
      }
   }

   public void skipping(Ignoreable ignoreable)
     throws IOException
   {
   }

   public void subscript(TeXObject arg)
     throws IOException
   {
      write(parser.getSbChar());
      write(parser.getBgChar());
      write(arg.toString(parser));
      write(parser.getEgChar());
   }

   public void superscript(TeXObject arg)
     throws IOException
   {
      write(parser.getSpChar());
      write(parser.getBgChar());
      write(arg.toString(parser));
      write(parser.getEgChar());
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

   public File getFile()
   {
      return inFile;
   }

   public void beginParse(File file)
     throws IOException
   {
      inFile = file;

      getTeXApp().message(TeXApp.MESSAGE_READING, file.getAbsolutePath());

      basePath = file.getParentFile().toPath();

      if (writer == null)
      {
         Files.createDirectories(outPath);

         File outFile = new File(outPath.toFile(), file.getName());

         getTeXApp().message(TeXApp.MESSAGE_WRITING, outFile.getAbsolutePath());
         writer = new PrintWriter(outFile);
      }
   }

   public void endParse(File file)
    throws IOException
   {
      if (getParser().getCurrentParentFile() == null)
      {
         inFile = null;

         if (writer != null)
         {
            writer.close();
            writer = null;
         }
      }
   }

   public void href(String url, TeXObject text)
    throws IOException
   {
      String bg = ""+parser.getBgChar();
      String eg = ""+parser.getEgChar();

      write(parser.getEscChar()+"href");
      write(bg+url+eg);
      write(bg+text.toString(parser)+eg);
   }

   public void verb(String name, boolean isStar, char delim, String text)
     throws IOException
   {
      write(parser.getEscChar()+name);

      if (isStar)
      {
         write("*");
      }
      else if (parser.isLetter(delim))
      {
         write(" ");
      }

      write(delim);
      write(text);
      write(delim);
   }

   public void newcommand(byte overwrite,
     String type, String csName, boolean isShort,
     int numParams, TeXObject defValue, TeXObject definition)
    throws IOException
   {
      char bg = parser.getBgChar();
      char eg = parser.getEgChar();
      char esc = parser.getEscChar();

      write(""+esc+type);

      if (isShort)
      {
         write("*");
      }

      write(""+bg+esc+csName+eg);

      if (numParams > 0)
      {
         write("["+numParams+"]");

         if (defValue != null)
         {
            write("["+defValue.toString(parser)+"]");
         }
      }

      write(""+bg+definition.toString(parser)+eg);
   }

   private File inFile;

   private Path outPath, basePath;
   private PrintWriter writer;

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
