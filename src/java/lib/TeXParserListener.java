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
package com.dickimawbooks.texparserlib;

import java.io.IOException;
import java.io.File;

import com.dickimawbooks.texparserlib.generic.*;

public interface TeXParserListener
{
   // Called when parser starts to parse file
   public void beginParse(TeXParser parser, File file)
      throws IOException;

   // Called when parser finishes parse file
   public void endParse(TeXParser parser, File file)
      throws IOException;

   // Gets control sequence identified by name (doesn't include
   // leading backslash)
   public ControlSequence getControlSequence(String name);

   // Gets active character identified by charCode.
   public ActiveChar getActiveChar(int charCode);

   public Eol getEol();

   public Par getPar();

   public Space getSpace();

   public Param getParam(int digit);

   public DoubleParam getDoubleParam(Param param);

   public Tab getTab();

   public Letter getLetter(int charCode);

   public Other getOther(int charCode);

   public BigOperator createBigOperator(String name, int code1, int code2);

   public Symbol createSymbol(String name, int code);

   public GreekSymbol createGreekSymbol(String name, int code);

   public BinarySymbol createBinarySymbol(String name, int code);

   public MathSymbol createMathSymbol(String name, int code);

   public Group createGroup();

   public MathGroup createMathGroup();

   public SpChar createSpChar();

   public SbChar createSbChar();

   public Comment createComment();

   public SkippedSpaces createSkippedSpaces();

   public SkippedEols createSkippedEols();

   public void skipping(TeXParser parser, Ignoreable ignoreable)
      throws IOException;

   public void subscript(TeXParser parser, TeXObject arg)
     throws IOException;

   public void superscript(TeXParser parser, TeXObject arg)
     throws IOException;

   public void tab(TeXParser parser)
     throws IOException;

   public void overwithdelims(TeXParser parser, TeXObject firstDelim,
     TeXObject secondDelim, TeXObject before, TeXObject after)
    throws IOException;

   public void abovewithdelims(TeXParser parser, TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness, TeXObject before, TeXObject after)
    throws IOException;

   public void input(TeXParser parser, TeXPath path)
    throws IOException;

   public void par() throws IOException;

   public boolean special(TeXParser parser, String param)
     throws IOException;

   public void verb(TeXParser parser, boolean isStar, char delim,
     String text)
     throws IOException;

   public void addFileReference(TeXPath texPath);

   public void href(TeXParser parser, String url, TeXObject text)
     throws IOException;

   public Writeable getWriteable();

   public TeXApp getTeXApp();
}
