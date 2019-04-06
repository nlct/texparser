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
import java.util.HashMap;
import java.util.Vector;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.nio.charset.Charset;
import java.nio.file.Files;

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
      pageDimensions = new HashMap<Integer,Float>();

      // Initialise to TeX's defaults (bp)
      pageDimensions.put(Integer.valueOf(PercentUnit.HSIZE), 
        Float.valueOf(468f));
      pageDimensions.put(Integer.valueOf(PercentUnit.VSIZE), 
        Float.valueOf(640.8f));

      pageDimensions.put(Integer.valueOf(PercentUnit.TEXT_WIDTH), 
         Float.valueOf(468f));
      pageDimensions.put(Integer.valueOf(PercentUnit.TEXT_HEIGHT), 
         Float.valueOf(640.8f));

      pageDimensions.put(Integer.valueOf(PercentUnit.LINE_WIDTH), 
         Float.valueOf(468f));

      pageDimensions.put(Integer.valueOf(PercentUnit.COLUMN_WIDTH), 
         Float.valueOf(468f));
      pageDimensions.put(Integer.valueOf(PercentUnit.COLUMN_HEIGHT), 
         Float.valueOf(640.8f));

      pageDimensions.put(Integer.valueOf(PercentUnit.PAPER_WIDTH), 
         Float.valueOf(612f));
      pageDimensions.put(Integer.valueOf(PercentUnit.PAPER_HEIGHT), 
         Float.valueOf(1008f));

      pageDimensions.put(Integer.valueOf(PercentUnit.MARGIN_WIDTH), 
         Float.valueOf(64.8f));

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
      parser.putControlSequence(new SpaceCs("\n"));
      parser.putControlSequence(new SpaceCs("\t"));
      parser.putControlSequence(new DiscretionaryHyphen());
      parser.putControlSequence(new ItalicCorrection());
      parser.putControlSequence(new OverWithDelims());
      parser.putControlSequence(new Accent("a"));
      parser.putControlSequence(new Relax());
      parser.putControlSequence(new Unexpanded());
      parser.putControlSequence(new DisplayStyle());
      parser.putControlSequence(new TextStyle());
      parser.putControlSequence(new StringCs());
      parser.putControlSequence(new Detokenize());
      parser.putControlSequence(new EndInput());
      parser.putControlSequence(new ParCs());
      parser.putControlSequence(new EndGraf());
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
      parser.putControlSequence(new CharCs());
      parser.putControlSequence(new ExpandAfter());
      parser.putControlSequence(new AfterGroup());
      parser.putControlSequence(new Csname());
      parser.putControlSequence(new EndCsname());
      parser.putControlSequence(new IgnoreSpaces());
      parser.putControlSequence(new If());
      parser.putControlSequence(new Ifx());
      parser.putControlSequence(IFTRUE);
      parser.putControlSequence(IFFALSE);
      parser.putControlSequence(new Else());
      parser.putControlSequence(new Or());
      parser.putControlSequence(new Fi());
      parser.putControlSequence(new IfCase());
      parser.putControlSequence(new NewIf());
      parser.putControlSequence(new NewCount());
      parser.putControlSequence(new NewDimen());
      parser.putControlSequence(new NewToks());
      parser.putControlSequence(new Global());
      parser.putControlSequence(new LongCs());
      parser.putControlSequence(new BeginGroup());
      parser.putControlSequence(new EndGroup());
      parser.putControlSequence(new AssignedControlSequence(
        "bgroup", getBgChar(parser.getBgChar())));
      parser.putControlSequence(new AssignedControlSequence(
        "egroup", getEgChar(parser.getEgChar())));
      parser.putControlSequence(new LoopCs());
      parser.putControlSequence(new Advance());
      parser.putControlSequence(new Multiply());
      parser.putControlSequence(new Divide());
      parser.putControlSequence(new IfNum());
      parser.putControlSequence(new IfDim());

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
      parser.putControlSequence(new GenericCommand(true, "lq", 
        null, new TeXObject[]{getOther('`')}));
      parser.putControlSequence(new GenericCommand(true, "rq", 
        null, new TeXObject[]{getOther('\'')}));
      parser.putControlSequence(new GenericCommand(true, ",", 
        null, new TeXObject[]{getOther(0x2006)}));

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

      newlength("hsize", 1, new PercentUnit(PercentUnit.HSIZE));
      newlength("vsize", 1, new PercentUnit(PercentUnit.VSIZE));
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

   public byte getUndefinedAction()
   {
      return undefAction;
   }

   public void setUndefinedAction(byte action)
   {
      undefAction = action;
   }

   public ControlSequence createUndefinedCs(String name)
   {
      return new Undefined(name, undefAction);
   }

   public DimenRegister newlength(boolean isLocal, String name)
   {
      return parser.getSettings().newdimen(isLocal, name);
   }

   public DimenRegister newlength(String name,
     TeXDimension dimen)
    throws TeXSyntaxException
   {
      DimenRegister reg = parser.getSettings().newdimen(name);

      reg.setDimension(getParser(), dimen);

      return reg;
   }

   public DimenRegister newlength(String name,
     float value, TeXUnit unit)
   {
      DimenRegister reg = parser.getSettings().newdimen(name);

      try
      {
         reg.setDimension(getParser(), new UserDimension(value, unit));
      }
      catch (TeXSyntaxException e)
      {
         // this shouldn't happen
      }

      return reg;
   }

   public CountRegister newcount(boolean isLocal, String name)
   {
      return parser.getSettings().newcount(isLocal, name);
   }

   public CountRegister newcount(String name, TeXNumber number)
    throws TeXSyntaxException
   {
      CountRegister reg = parser.getSettings().newcount(name);

      reg.setValue(getParser(), number);

      return reg;
   }

   public CountRegister newcount(String name, int number)
   {
      CountRegister reg = parser.getSettings().newcount(name);

      try
      {
         reg.setValue(getParser(), new UserNumber(number));
      }
      catch (TeXSyntaxException e)
      {
         // this shouldn't happen
      }

      return reg;
   }

   public TokenRegister newtoks(boolean isLocal, String name)
   {
      return parser.getSettings().newtoks(isLocal, name);
   }

   // Gets active character identified by charCode.
   public ActiveChar getActiveChar(int charCode)
   {
      return getParser().getActiveChar(Integer.valueOf(charCode));
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

   public DoubleParam getDoubleParam(ParameterToken param)
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

   public ControlSequence createSymbol(String name, int code, FontEncoding enc)
   {
      return new EncodingSymbol(name, code, enc);
   }

   public GreekSymbol createGreekSymbol(String name, int code)
   {
      return new GreekSymbol(name, code);
   }

   public MathSymbol createMathSymbol(String name, int code)
   {
      return new MathSymbol(name, code);
   }

   public DelimiterSymbol createDelimiterSymbol(String name, int code)
   {
      return new DelimiterSymbol(name, code);
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
      if (path != null && Files.exists(path.getPath()))
      {
         Charset charset = getCharSet();

         getParser().parse(path, charset);

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

   public boolean isFileLoaded(String name, String ext)
   {
      return isFileLoaded(String.format("%s.%s", name, ext));
   }

   public boolean isFileLoaded(String filename)
   {
      for (TeXPath texPath : referencedFiles)
      {
         if (texPath.getLeaf().toString().equals(filename))
         {
            return true;
         }
      }

      return false;
   }

   public void addFileReference(TeXPath texPath)
   {
      if (!referencedFiles.contains(texPath))
      {
         referencedFiles.add(texPath);
      }
   }

   public boolean removeFileReference(TeXPath texPath)
   {
      return referencedFiles.remove(texPath);
   }

   public TeXPath getLastFileReference()
   {
      int n = referencedFiles.size();
      return n == 0 ? null : referencedFiles.get(n-1);
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

   public void verb(String name, boolean isStar, int delim, String text)
     throws IOException
   {
      if (isStar)
      {
         for (int i = 0; i < text.length(); )
         {
            int c = text.codePointAt(i);
            i += Character.charCount(c);

            if (c == ' ')
            {
               writeable.writeCodePoint(0x2423);
            }
            else
            {
               writeable.writeCodePoint(c);
            }
         }
      }
      else
      {
         writeable.write(text);
      }
   }

   // By default this just processes the object.
   // Needs to be overridden if the rotation can actually be
   // performed.
   public void rotate(double angle, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      if (stack == parser || stack == null)
      {
         object.process(parser);
      }
      else
      {
         object.process(parser, stack);
      }
   }

   public void rotate(double angle, double originPercentX, 
      double originPercentY, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      rotate(angle, parser, stack, object);
   }

   public void rotate(double angle, TeXDimension orgX, TeXDimension orgY, 
      TeXParser parser, TeXObjectList stack, TeXObject object)
   throws IOException
   {
      rotate(angle, parser, stack, object);
   }

   public void scale(double factor, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      scale(factor, factor, parser, stack, object);
   }

   public void scale(double factorX, double factorY, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      if (stack == parser || stack == null)
      {
         object.process(parser);
      }
      else
      {
         object.process(parser, stack);
      }
   }

   public void scaleX(double factor, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      if (stack == parser || stack == null)
      {
         object.process(parser);
      }
      else
      {
         object.process(parser, stack);
      }
   }

   public void scaleY(double factor, TeXParser parser, 
      TeXObjectList stack, TeXObject object)
   throws IOException
   {
      if (stack == parser || stack == null)
      {
         object.process(parser);
      }
      else
      {
         object.process(parser, stack);
      }
   }

   public void resize(TeXDimension width, TeXDimension height, 
      TeXParser parser, TeXObjectList stack, TeXObject object)
   throws IOException
   {
      if (stack == parser || stack == null)
      {
         object.process(parser);
      }
      else
      {
         object.process(parser, stack);
      }
   }

   public void resizeX(TeXDimension width, 
      TeXParser parser, TeXObjectList stack, TeXObject object)
   throws IOException
   {
      if (stack == parser || stack == null)
      {
         object.process(parser);
      }
      else
      {
         object.process(parser, stack);
      }
   }

   public void resizeY(TeXDimension height, 
      TeXParser parser, TeXObjectList stack, TeXObject object)
   throws IOException
   {
      if (stack == parser || stack == null)
      {
         object.process(parser);
      }
      else
      {
         object.process(parser, stack);
      }
   }

   public void translate(TeXDimension width, TeXDimension height, 
      TeXParser parser, TeXObjectList stack, TeXObject object)
   throws IOException
   {
      if (stack == parser || stack == null)
      {
         object.process(parser);
      }
      else
      {
         object.process(parser, stack);
      }
   }

   public Vector<TeXPath> getFileList()
   {
      return referencedFiles;
   }

   public TeXObjectList requestUserInputAsList(String message)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      requestUserInput(message, list);

      return list;
   }

   public void requestUserInput(String message, TeXObjectList list)
     throws IOException
   {
      String response = getTeXApp().requestUserInput(message);

      if (response != null && !response.isEmpty())
      {
         getParser().scan(response, list);
      }
   }

   public boolean isIfTrue(ControlSequence cs)
   {
      return IFTRUE.equals(cs);
   }

   public boolean isIfFalse(ControlSequence cs)
   {
      return IFFALSE.equals(cs);
   }

   public float getPageDimension(int type)
   {
      Float val = pageDimensions.get(Integer.valueOf(type));

      return val == null ? 0f : val.floatValue();
   }

   public void setPageDimension(int type, float value)
   {
      pageDimensions.put(Integer.valueOf(type), Float.valueOf(value));
   }

   protected Writeable writeable;

   protected Vector<TeXPath> referencedFiles;

   protected Vector<SpecialListener> specialListeners;

   public static final IfTrue IFTRUE = new IfTrue();
   public static final IfFalse IFFALSE = new IfFalse();

   protected byte undefAction = Undefined.ACTION_ERROR;

   protected HashMap<Integer,Float> pageDimensions;
}
