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
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;

public abstract class DefaultTeXParserListener implements TeXParserListener
{
   public DefaultTeXParserListener(Writeable writeable)
   {
      this.writeable = writeable;
      init();
   }

   private void init()
   {
      activeTable = new Hashtable<Integer,ActiveChar>();
      csTable = new Hashtable<String,ControlSequence>();
      map = new Hashtable<String,String>();
      referencedFiles = new Vector<TeXPath>();
      specialListeners = new Vector<SpecialListener>();

      addPredefined();
   }

   protected void addPredefined()
   {
      Nbsp nbsp = new Nbsp();
      activeTable.put(new Integer((int)nbsp.getChar().charValue()), nbsp);

      Accent.addCommands(csTable);
      Symbol.addCommands(csTable, this);
      ParAlign.addCommands(csTable);

      csTable.put("above", new Above());
      csTable.put("abovewithdelims", new AboveWithDelims());
      csTable.put(" ", new ControlSpace());
      csTable.put("-", new DiscretionaryHyphen());
      csTable.put("/", new ItalicCorrection());
      csTable.put("overwithdelims", new OverWithDelims());
      csTable.put("a", new Accent("a"));
      csTable.put("relax", new Relax());
      csTable.put("string", new StringCs());
      csTable.put("endinput", new EndInput());
      csTable.put("par", new ParCs());
      csTable.put("special", new Special());

      // TeX font changing declarations

      csTable.put("rm", getTeXFontFamilyDeclaration("rm", 
         TeXSettings.FAMILY_RM));
      csTable.put("sf", getTeXFontFamilyDeclaration("sf", 
         TeXSettings.FAMILY_SF));
      csTable.put("tt", getTeXFontFamilyDeclaration("tt", 
         TeXSettings.FAMILY_TT));
      csTable.put("cal", getTeXFontFamilyDeclaration("cal", 
         TeXSettings.FAMILY_CAL));

      csTable.put("bf", getTeXFontWeightDeclaration("bf",
         TeXSettings.WEIGHT_BF));

      csTable.put("it", getTeXFontShapeDeclaration("it",
         TeXSettings.SHAPE_IT));
      csTable.put("sl", getTeXFontShapeDeclaration("sl",
         TeXSettings.SHAPE_SL));
      csTable.put("em", getTeXFontShapeDeclaration("em",
         TeXSettings.SHAPE_EM));
      csTable.put("sc", getTeXFontShapeDeclaration("sc",
         TeXSettings.SHAPE_SC));
   }

   public void putControlSequence(String name, ControlSequence cs)
   {
      csTable.put(name, cs);
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

   // Gets control sequence identified by name (doesn't include
   // leading backslash)
   public ControlSequence getControlSequence(String name)
   {
      ControlSequence cs = csTable.get(map(name));

      return cs == null ? createUndefinedCs(name) : cs;
   }

   public ControlSequence createUndefinedCs(String name)
   {
      return new Undefined(name);
   }

   // Gets active character identified by charCode.
   public ActiveChar getActiveChar(int charCode)
   {
      return activeTable.get(new Integer(charCode));
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

   public BigOperator createBigOperator(String name, int code)
   {
      return new BigOperator(name, code);
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

   public BigOperator createBigOperator(String name, int code1, int code2)
   {
      return new BigOperator(name, code1, code2);
   }

   public Group createGroup()
   {
      return new Group();
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

   public abstract void skipping(TeXParser parser, Ignoreable ignoreable)
      throws IOException;

   public abstract void overwithdelims(TeXParser parser, TeXObject first,
     TeXObject second, TeXObject before, TeXObject after)
    throws IOException;

   public String map(String key)
   {
      String value = map.get(key);

      return value == null ? key : value;
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

   public boolean special(TeXParser parser, String param)
     throws IOException
   {
      for (SpecialListener listener : specialListeners)
      {
         if (listener.process(parser, param))
         {
            return true;
         }
      }

      return false;
   }

   public void verb(TeXParser parser, boolean isStar, 
     char delim, String text)
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

   protected Hashtable<String,ControlSequence> csTable;

   protected Hashtable<Integer,ActiveChar> activeTable;

   protected Hashtable<String,String> map;

   protected Writeable writeable;

   protected Vector<TeXPath> referencedFiles;

   protected Vector<SpecialListener> specialListeners;

}
