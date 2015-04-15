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
package com.dickimawbooks.texparserlib.generic;

import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.nio.charset.Charset;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;

public abstract class DefaultTeXParserListener extends TeXParserListener
{
   public DefaultTeXParserListener(Writeable writeable)
   {
      this.writeable = writeable;
      init();
   }

   private void init()
   {
      referencedFiles = new Vector<TeXPath>();
      specialListeners = new Vector<SpecialListener>();
   }

   protected void addPredefined()
   {
      parser.putActiveChar(new Nbsp());

      Accent.addCommands(parser);
      Symbol.addCommands(parser, this);
      ParAlign.addCommands(parser);

      parser.putControlSequence(new Above());
      parser.putControlSequence(new AboveWithDelims());
      parser.putControlSequence(new ControlSpace());
      parser.putControlSequence(new SpaceCs());
      parser.putControlSequence(new DiscretionaryHyphen());
      parser.putControlSequence(new ItalicCorrection());
      parser.putControlSequence(new OverWithDelims());
      parser.putControlSequence(new Accent("a"));
      parser.putControlSequence(new Relax());
      parser.putControlSequence(new StringCs());
      parser.putControlSequence(new EndInput());
      parser.putControlSequence(new ParCs());
      parser.putControlSequence(new Special());
      parser.putControlSequence(new GenericCommand("empty"));
      parser.putControlSequence(new Def());
      parser.putControlSequence(new Def("gdef", true, false));
      parser.putControlSequence(new Let());
      parser.putControlSequence(new Special());
      parser.putControlSequence(new Jobname());
      parser.putControlSequence(new NumberCs());
      parser.putControlSequence(new RomanNumeral());
      parser.putControlSequence(new TheCs());
      parser.putControlSequence(new ExpandAfter());
      parser.putControlSequence(new Csname());
      parser.putControlSequence(new EndCsname());
      parser.putControlSequence(new If());
      parser.putControlSequence(new Ifx());
      parser.putControlSequence(new IfTrue());
      parser.putControlSequence(new IfFalse());
      parser.putControlSequence(new Else());
      parser.putControlSequence(new Fi());
      parser.putControlSequence(new NewIf());
      parser.putControlSequence(new MathAccent("vec", 8407));
      parser.putControlSequence(new MathAccent("hat", 0x0302, 0x02C6));
      parser.putControlSequence(new MathAccent("check", 0x030C, 0x02C7));
      parser.putControlSequence(new MathAccent("breve", 0x0306, 0x02D8));
      parser.putControlSequence(new MathAccent("acute", 0x0301, 0x00B4));
      parser.putControlSequence(new MathAccent("grave", 0x0300, 0x0060));
      parser.putControlSequence(new MathAccent("tilde", 0x0303, 0x02DC));
      parser.putControlSequence(new MathAccent("bar", 0x0304, 0x00AF));
      parser.putControlSequence(new MathAccent("dot", 0x0307, 0x02D9));
      parser.putControlSequence(new MathAccent("ddot", 0x0308, 0x00A8));
      parser.putControlSequence(new DoubleLetterAccent("t", 0x035C, 0x203F));
      parser.putControlSequence(new Uppercase());
      parser.putControlSequence(new Lowercase());
      parser.putControlSequence(new Show());

      // TeX font changing declarations

      parser.putControlSequence(getTeXFontFamilyDeclaration("rm", 
         TeXSettings.FAMILY_RM));
      parser.putControlSequence(getTeXFontFamilyDeclaration("sf", 
         TeXSettings.FAMILY_SF));
      parser.putControlSequence(getTeXFontFamilyDeclaration("tt", 
         TeXSettings.FAMILY_TT));
      parser.putControlSequence(getTeXFontFamilyDeclaration("cal", 
         TeXSettings.FAMILY_CAL));

      parser.putControlSequence(getTeXFontWeightDeclaration("bf",
         TeXSettings.WEIGHT_BF));

      parser.putControlSequence(getTeXFontShapeDeclaration("it",
         TeXSettings.SHAPE_IT));
      parser.putControlSequence(getTeXFontShapeDeclaration("sl",
         TeXSettings.SHAPE_SL));
      parser.putControlSequence(getTeXFontShapeDeclaration("em",
         TeXSettings.SHAPE_EM));
      parser.putControlSequence(getTeXFontShapeDeclaration("sc",
         TeXSettings.SHAPE_SC));
   }

   public ControlSequence getTeXFontFamilyDeclaration(String name, int family)
   {
      return new TeXFontFamilyDeclaration(name, family);
   }

   public ControlSequence getTeXFontShapeDeclaration(String name, int shape)
   {
      return new TeXFontShapeDeclaration(name, shape);
   }

   public ControlSequence getTeXFontWeightDeclaration(String name, int weight)
   {
      return new TeXFontWeightDeclaration(name, weight);
   }

   public void putControlSequence(ControlSequence cs)
   {
      getParser().putControlSequence(cs);
   }

   public void putControlSequence(boolean isLocal, ControlSequence cs)
   {
      getParser().putControlSequence(isLocal, cs);
   }

   public ControlSequence createUndefinedCs(String name)
   {
      return new Undefined(name);
   }

   // Gets active character identified by charCode.
   public ActiveChar getActiveChar(int charCode)
   {
      return getParser().getActiveChar(new Integer(charCode));
   }

   public void putActiveChar(ActiveChar activeChar)
   {
      getParser().putActiveChar(activeChar);
   }

   public BgChar getBgChar(int codePoint)
   {
      return new BgChar(codePoint);
   }

   public EgChar getEgChar(int codePoint)
   {
      return new EgChar(codePoint);
   }

   public Eol getEol()
   {
      return new Eol();
   }

   public Par getPar()
   {
      return new Par();
   }

   public Space getSpace()
   {
      return new Space();
   }

   public Param getParam(int digit)
   {
      return new Param(digit);
   }

   public DoubleParam getDoubleParam(Param param)
   {
      return new DoubleParam(param);
   }

   public Tab getTab()
   {
      return new Tab();
   }

   public Letter getLetter(int charCode)
   {
      return new Letter(charCode);
   }

   public Other getOther(int charCode)
   {
      return new Other(charCode);
   }

   public TeXObjectList createString(String string)
   {
      return new TeXObjectList(this, string);
   }

   public SkippedSpaces createSkippedSpaces()
   {
      return new SkippedSpaces();
   }

   public SkippedEols createSkippedEols()
   {
      return new SkippedEols();
   }

   public BinarySymbol createBinarySymbol(String name, int code)
   {
      return new BinarySymbol(name, code);
   }

   public Symbol createSymbol(String name, int code)
   {
      return new Symbol(name, code);
   }

   public GreekSymbol createGreekSymbol(String name, int code)
   {
      return new GreekSymbol(name, code);
   }

   public MathSymbol createMathSymbol(String name, int code)
   {
      return new MathSymbol(name, code);
   }

   public BigOperator createBigOperator(String name, int code)
   {
      return new BigOperator(name, code);
   }

   public BigOperator createBigOperator(String name, int code1, int code2)
   {
      return new BigOperator(name, code1, code2);
   }

   public Group createGroup()
   {
      return new Group();
   }

   public Group createGroup(String text)
   {
      return new Group(this, text);
   }

   public MathGroup createMathGroup()
   {
      return new MathGroup();
   }

   public SpChar createSpChar()
   {
      return new SpChar();
   }

   public SbChar createSbChar()
   {
      return new SbChar();
   }

   public Comment createComment()
   {
      return new Comment();
   }

   public boolean input(TeXPath path)
    throws IOException
   {
      File file = path.getFile();

      if (file != null && file.exists())
      {
         Charset charset = getCharSet();

         getParser().parse(file, charset);

         return true;
      }

      return false;
   }

   public Writeable getWriteable()
   {
      return writeable;
   }

   public void setWriteable(Writeable writeable)
   {
      this.writeable = writeable;
   }

   public void addFileReference(TeXPath texPath)
   {
      if (!referencedFiles.contains(texPath))
      {
         referencedFiles.add(texPath);
      }
   }

   public void addSpecialListener(SpecialListener listener)
   {
      specialListeners.add(listener);
   }

   public void removeSpecialListener(SpecialListener listener)
   {
      specialListeners.remove(listener);
   }

   public TeXObjectList special(String param)
     throws IOException
   {
      for (SpecialListener listener : specialListeners)
      {
         TeXObjectList expanded = listener.process(parser, param);

         if (expanded != null)
         {
            return expanded;
         }
      }

      return null;
   }

   public void verb(String name, boolean isStar, char delim, String text)
     throws IOException
   {
      if (isStar)
      {
         StringCharacterIterator iter = new StringCharacterIterator(text);

         for (char c = iter.first(); c != CharacterIterator.DONE;
              c = iter.next())
         {
            if (c == ' ')
            {
               writeable.writeCodePoint(0x2423);
            }
            else
            {
               writeable.write(c);
            }
         }
      }
      else
      {
         writeable.write(text);
      }
   }

   public Vector<TeXPath> getFileList()
   {
      return referencedFiles;
   }

   protected Writeable writeable;

   protected Vector<TeXPath> referencedFiles;

   protected Vector<SpecialListener> specialListeners;
}
