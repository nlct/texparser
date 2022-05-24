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
package com.dickimawbooks.texparserlib;

import java.io.IOException;
import java.io.File;
import java.nio.charset.Charset;

import com.dickimawbooks.texparserlib.generic.*;

public abstract class TeXParserListener
{
   // Called when parser starts to parse file. May be used to add
   // transcript messages.
   public abstract void beginParse(File file, Charset encoding)
      throws IOException;

   // Called when parser finishes parse file
   public abstract void endParse(File file)
      throws IOException;

   // Gets control sequence identified by name (doesn't include
   // leading backslash)

   public ControlSequence getControlSequence(String name)
   {
      ControlSequence cs = getParser().getControlSequence(name);

      return cs == null ? createUndefinedCs(name) : cs;
   }

   public abstract ControlSequence createUndefinedCs(String name);

   // Gets active character identified by charCode.
   public abstract ActiveChar getActiveChar(int charCode);

   public abstract BgChar getBgChar(int charCode);

   public abstract EgChar getEgChar(int charCode);

   public abstract Eol getEol();

   public abstract Par getPar();

   /**
    * Process a paragraph block that may have margins.
    * @param par the paragraph that needs processing
    * @param stack a sub-stack which may be null or the parser
    * @throws IOException if I/O error occurs
    */ 
   public abstract Paragraph createParagraph();

   public abstract Space getSpace();

   public abstract Spacer getSpacer(Direction direction, TeXDimension size, 
     boolean inline);

   public abstract Param getParam(int digit);

   public abstract DoubleParam getDoubleParam(ParameterToken param);

   public abstract Tab getTab();

   public abstract Letter getLetter(int charCode);

   public abstract Other getOther(int charCode);

   public abstract BigOperator createBigOperator(String name, int code1, int code2);

   public abstract Symbol createSymbol(String name, int code);

   public abstract ControlSequence createSymbol(String name, int code, FontEncoding enc);

   public abstract GreekSymbol createGreekSymbol(String name, int code);

   public abstract BinarySymbol createBinarySymbol(String name, int code);

   public abstract MathSymbol createMathSymbol(String name, int code);

   public abstract DelimiterSymbol createDelimiterSymbol(String name, int code);

   public abstract Group createGroup();

   public abstract Group createGroup(String text);

   public abstract TeXObjectList createStack();

   public abstract TeXObjectList createString(String text);

   public abstract DataObjectList createDataList();

   public abstract DataObjectList createDataList(String text);

   public abstract MathGroup createMathGroup();

   public abstract SpChar createSpChar();

   public abstract SbChar createSbChar();

   public abstract Comment createComment();

   public abstract SkippedSpaces createSkippedSpaces();

   public abstract SkippedEols createSkippedEols();

   // returns page layout length in bp
   public abstract float getPageDimension(int type);

   public TeXUnit createUnit(String unitName)
    throws TeXSyntaxException
   {
      if (unitName.equals("em"))
      {
         return TeXUnit.EM;
      }

      if (unitName.equals("ex"))
      {
         return TeXUnit.EX;
      }

      if (unitName.equals("mu"))
      {
         return TeXUnit.MU;
      }

      if (unitName.equals("fil"))
      {
         return TeXUnit.FIL;
      }

      if (unitName.equals("fill"))
      {
         return TeXUnit.FILL;
      }

      if (unitName.equals("filll"))
      {
         return TeXUnit.FILLL;
      }

      try
      {
         return new FixedUnit(unitName);
      }
      catch (IllegalArgumentException e)
      {
         throw new TeXSyntaxException(getParser(),
           TeXSyntaxException.ERROR_MISSING_UNIT, unitName);
      }
   }

   public abstract float emToPt(float emValue);

   public abstract float exToPt(float exValue);

   public abstract void skipping(Ignoreable ignoreable)
      throws IOException;

   public abstract void subscript(TeXObject arg)
     throws IOException;

   public abstract void superscript(TeXObject arg)
     throws IOException;

   public abstract void overwithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXObject before, TeXObject after)
    throws IOException;

   public abstract void abovewithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness, TeXObject before, TeXObject after)
    throws IOException;

   // Returns true if the path is processed
   public abstract boolean input(TeXPath path, TeXObjectList stack)
    throws IOException;

   public abstract Charset getCharSet();

   public abstract TeXObjectList special(String param)
     throws IOException;

   public abstract TeXObjectList directlua(String luacode)
     throws IOException;

   public abstract void verb(String name, boolean isStar, int delim,
     String text)
     throws IOException;

   public abstract void addFileReference(TeXPath texPath);

   public abstract void href(String url, TeXObject text)
     throws IOException;

   /**
    * Creates a hyperlink anchor.
    * May simply return the text if hyperlinks aren't supported.
    * @param anchorName the hyperlink anchor name
    * @param text the text with the anchor
    * @return an object that incorporates the text with a target or
    * just the text
    */
   public abstract TeXObject createAnchor(String anchorName, TeXObject text);

   /**
    * Creates a hyperlink.
    * May simply return the text if hyperlinks aren't supported.
    * @param anchorName the hyperlink anchor name
    * @param text the hyperlink text
    * @return an object that encapsulates the text with a hyperlink
    * or just the text
    */
   public abstract TeXObject createLink(String anchorName, TeXObject text);

   public abstract Writeable getWriteable();

   public abstract TeXApp getTeXApp();

   // Add all predefined commands
   protected abstract void addPredefined();

   public void setParser(TeXParser parser)
   {
      this.parser = parser;
      addPredefined();
   }

   public TeXParser getParser()
   {
      return parser;
   }

   protected TeXParser parser;
}
