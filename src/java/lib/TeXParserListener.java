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
package com.dickimawbooks.texparserlib;

import java.io.IOException;
import java.io.File;
import java.nio.charset.Charset;

import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.auxfile.CrossRefInfo;

public abstract class TeXParserListener
{
   // Called when parser starts to parse file. May be used to add
   // transcript messages.
   public abstract void beginParse(File file, Charset encoding)
      throws IOException;

   // Called when parser finishes parse file
   public abstract void endParse(File file)
      throws IOException;

   public abstract File getOutputDir();

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

   public abstract Tab getTab(int charCode);

   public Tab getTab()
   {
      return getTab(parser.getTabChar());
   }

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

   public abstract DataObjectList createDataList(boolean protect);

   public abstract DataObjectList createDataList(String text);

   public abstract DataObjectList createDataList(String text, boolean protect);

   public abstract MathGroup createMathGroup();

   public abstract SpChar createSpChar(int charCode);

   public SpChar createSpChar()
   {
      return createSpChar(parser.getSpChar());
   }

   public abstract SbChar createSbChar(int charCode);

   public SbChar createSbChar()
   {
      return createSbChar(parser.getSbChar());
   }

   public abstract Comment createComment();

   public abstract SkippedSpaces createSkippedSpaces();

   public abstract SkippedEols createSkippedEols();

   // returns page layout length in bp
   public abstract float getPageDimension(int type);

   /**
    * Gets the normal font size in pt rounded to an integer.
    * @return the normal font size in pt
    */
   public int getNormalFontSize()
   {
      return normalFontSize;
   }

   /**
    * Sets the normal font size in pt.
    * @param size the size in pt
    * @throws TeXSyntaxException if the size is &lt;=0
    */
   public void setNormalFontSize(int size)
   throws TeXSyntaxException
   {
      if (size <= 0)
      {
         throw new TeXSyntaxException(getParser(),
          TeXSyntaxException.ERROR_POSITIVE_DIMEN_EXPECTED_FOUND, size);
      }

      normalFontSize = size;
      normalFontDimension.setValue(size, TeXUnit.PT);
   }

   public void setNormalFontSize(double size)
   throws TeXSyntaxException
   {
      setNormalFontSize((float)size);
   }

   public void setNormalFontSize(float size)
   throws TeXSyntaxException
   {
      if (size <= 0)
      {
         throw new TeXSyntaxException(getParser(),
          TeXSyntaxException.ERROR_POSITIVE_DIMEN_EXPECTED_FOUND, size);
      }

      normalFontDimension.setValue(size, FixedUnit.PT);
      normalFontSize = (int)Math.round(size);
   }

   public void setNormalFontSize(double size, TeXUnit unit)
   throws TeXSyntaxException
   {
      setNormalFontSize((float)size, unit);
   }

   public void setNormalFontSize(float size, TeXUnit unit)
   throws TeXSyntaxException
   {
      if (size <= 0)
      {
         throw new TeXSyntaxException(getParser(),
          TeXSyntaxException.ERROR_POSITIVE_DIMEN_EXPECTED_FOUND, size);
      }

      normalFontDimension.setValue(size, unit);

      normalFontSize = (int)Math.round(normalFontDimension.getUnit().toPt(getParser(),
         normalFontDimension.getValue()));
   }

   /**
    * Sets the normal font size from a dimension.
    * The provided value must be expanded first unless it's a
    * <code>TeXDimension</code> or <code>Numerical</code>. If no unit is provided, pt is
    * assumed.
    * @param value the dimension or number
    * @throws TeXSyntaxException if the size is &lt;=0
    */
   public void setNormalFontSize(TeXObject value)
    throws TeXSyntaxException
   {
      if (value == null || value.isEmpty())
      {
         throw new TeXSyntaxException(getParser(),
          TeXSyntaxException.ERROR_DIMEN_EXPECTED);
      }

      if (value instanceof TeXDimension)
      {
         TeXDimension dim = (TeXDimension)value;

         if (dim.getValue() <= 0)
         {
            throw new TeXSyntaxException(getParser(),
             TeXSyntaxException.ERROR_POSITIVE_DIMEN_EXPECTED_FOUND,
               dim.getValue()+dim.getUnit().format());
         }

         normalFontDimension.setDimension(getParser(), dim);
      }
      else if (value instanceof TeXNumber)
      {
         setNormalFontSize(((TeXNumber)value).doubleValue());
      }
      else if (value instanceof Numerical)
      {
         setNormalFontSize(((Numerical)value).number(getParser()));
      }
      else
      {
         String valStr = value.toString(getParser());

         normalFontDimension.setFrom(getParser(), valStr, true);

         if (normalFontDimension.getValue() <= 0)
         {
            normalFontDimension.setValue(normalFontSize, TeXUnit.PT);

            throw new TeXSyntaxException(getParser(),
             TeXSyntaxException.ERROR_POSITIVE_DIMEN_EXPECTED_FOUND, valStr);
         }
      }

      normalFontSize = (int)Math.round(normalFontDimension.getUnit().toPt(getParser(),
         normalFontDimension.getValue()));
   }

   public UserDimension getNormalFontDimension()
   {
      return normalFontDimension;
   }

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

   public Charset getCharSet()
   {
      return getTeXApp().getDefaultCharset();
   }

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
   public abstract TeXObject createAnchor(String anchorName, TeXObject text)
    throws IOException;

   /**
    * Creates a hyperlink.
    * May simply return the text if hyperlinks aren't supported.
    * @param anchorName the hyperlink anchor name
    * @param text the hyperlink text
    * @return an object that encapsulates the text with a hyperlink
    * or just the text
    */
   public abstract TeXObject createLink(String anchorName, TeXObject text)
    throws IOException;

   public TeXObject createLink(CrossRefInfo info, TeXObject text)
    throws IOException
   {
      return createLink(info.getTarget(), text);
   }

   /**
    * Creates a new object that has the given accessibility support
    * applied to the given object. If no support available, the
    * given object is returned.
    * @param accsupp accessibility support
    * @param object the object
    * @return the object with accessibility support applied or the
    * object itself if no support available
    */ 
   public TeXObject applyAccSupp(AccSupp accsupp, TeXObject object)
   {
      return object;
   }

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

   private int normalFontSize = 10;// in pt
   private UserDimension normalFontDimension = new UserDimension(10, TeXUnit.PT);
}
