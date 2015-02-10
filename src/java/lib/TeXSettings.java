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

import java.awt.Color;
import java.util.Hashtable;

public class TeXSettings
{
   private TeXSettings()
   {
      activeTable = new Hashtable<Integer,ActiveChar>();
      csTable = new Hashtable<String,ControlSequence>();
   }

   public TeXSettings(TeXParser parser)
   {
      this(null, parser);
   }

   public TeXSettings(TeXSettings parent, TeXParser parser)
   {
      this();
      this.parent = parent;
      this.parser = parser;
   }

   public TeXSettings getRoot()
   {
      if (parent == null)
      {
         return this;
      }

      return parent.getRoot();
   }

   public int getCurrentMode()
   {
      return currentMode;
   }

   public int getMode()
   {
      if (currentMode == INHERIT)
      {
         if (parent == null)
         {
            return MODE_TEXT;
         }

         return parent.getMode();
      }

      return currentMode;
   }

   public void setMode(int setting)
   {
      if (setting == INHERIT
       || setting == MODE_TEXT
       || setting == MODE_INLINE_MATH
       || setting == MODE_DISPLAY_MATH)
      {
         currentMode = setting;
         return;
      }

      throw new IllegalArgumentException(
          "Invalid argument '"+setting+"' for setMode(int)");
   }

   public int getCurrentCharMapMode()
   {
      return currentCharMapMode;
   }

   public int getCharMapMode()
   {
      if (currentCharMapMode == INHERIT)
      {
         if (parent == null)
         {
            return CHAR_MAP_OFF;
         }

         return parent.getCharMapMode();
      }

      return currentCharMapMode;
   }

   public void setCharMapMode(int setting)
   {
      if (setting == INHERIT
       || setting == CHAR_MAP_OFF
       || setting == CHAR_MAP_ON)
      {
         currentCharMapMode = setting;
         return;
      }

      throw new IllegalArgumentException(
          "Invalid argument '"+setting+"' for setCharMapMode(int)");
   }

   public int getCurrentFontFamily()
   {
      return currentFontFamily;
   }

   public int getFontFamily()
   {
      if (currentFontFamily == INHERIT)
      {
         if (parent == null)
         {
            return FAMILY_RM;
         }

         return parent.getFontFamily();
      }

      return currentFontFamily;
   }

   public int getCurrentFontShape()
   {
      return currentFontShape;
   }

   public int getFontShape()
   {
      if (currentFontShape == INHERIT)
      {
         if (parent == null)
         {
            return SHAPE_UP;
         }

         return parent.getFontShape();
      }

      return currentFontShape;
   }

   public int getCurrentFontWeight()
   {
      return currentFontWeight;
   }

   public int getFontWeight()
   {
      if (currentFontWeight == INHERIT)
      {
         if (parent == null)
         {
            return WEIGHT_MD;
         }

         return parent.getFontWeight();
      }

      return currentFontWeight;
   }

   public int getCurrentFontSize()
   {
      return currentFontSize;
   }

   public int getFontSize()
   {
      if (currentFontSize == INHERIT)
      {
         if (parent == null)
         {
            return SIZE_NORMAL;
         }

         return parent.getFontSize();
      }

      return currentFontSize;
   }

   public int getCurrentMathFont()
   {
      return currentMathFont;
   }

   public int getMathFont()
   {
      if (currentMathFont == INHERIT)
      {
         if (parent == null)
         {
            return MATH_STYLE_NORMAL;
         }

         return parent.getMathFont();
      }

      return currentMathFont;
   }

   public int getCurrentParAlign()
   {
      return currentParAlign;
   }

   public int getParAlign()
   {
      if (currentParAlign == INHERIT)
      {
         if (parent == null)
         {
            return PAR_ALIGN_NORMAL;
         }

         return parent.getParAlign();
      }

      return currentParAlign;
   }

   public int getCurrentAlignMode()
   {
      return currentAlignMode;
   }

   public int getAlignMode()
   {
      if (currentAlignMode == INHERIT)
      {
         if (parent == null)
         {
            return ALIGN_MODE_FALSE;
         }

         return parent.getAlignMode();
      }

      return currentAlignMode;
   }

   public void setAlignMode(int setting)
   {
      if (setting == INHERIT
       || setting == ALIGN_MODE_FALSE
       || setting == ALIGN_MODE_TRUE)
      {
         currentAlignMode = setting;

         return;
      }

      throw new IllegalArgumentException(
          "Invalid argument '"+setting+"' for setAlignMode(int)");
   }

   public void endAlignment()
   {
      setAlignMode(ALIGN_MODE_FALSE);
      setStartRowMode(START_ROW_MODE_FALSE);
      setStartColumnMode(START_COLUMN_MODE_FALSE);
   }

   public void startAlignment()
   {
      setAlignMode(ALIGN_MODE_TRUE);
      startRow();
   }

   public void startRow()
   {
      setStartRowMode(START_ROW_MODE_TRUE);
      startColumn();
      resetAlignmentColumn();
   }

   public void startColumn()
   {
      setStartColumnMode(START_COLUMN_MODE_TRUE);
   }

   public int getCurrentStartRowMode()
   {
      return currentStartRowMode;
   }

   public int getStartRowMode()
   {
      if (currentStartRowMode == INHERIT)
      {
         if (parent == null)
         {
            return START_ROW_MODE_FALSE;
         }

         return parent.getStartRowMode();
      }

      return currentStartRowMode;
   }

   public void setStartRowMode(int setting)
   {
      if (setting == INHERIT
       || setting == START_ROW_MODE_FALSE
       || setting == START_ROW_MODE_TRUE)
      {
         currentStartRowMode = setting;

         if (setting == START_ROW_MODE_TRUE)
         {
            resetAlignmentColumn();
            setStartColumnMode(START_COLUMN_MODE_TRUE);
         }

         return;
      }

      throw new IllegalArgumentException(
          "Invalid argument '"+setting+"' for setStartRowMode(int)");
   }

   public int getCurrentStartColumnMode()
   {
      return currentStartColumnMode;
   }

   public int getStartColumnMode()
   {
      if (currentStartColumnMode == INHERIT)
      {
         if (parent == null)
         {
            return START_COLUMN_MODE_FALSE;
         }

         return parent.getStartColumnMode();
      }

      return currentStartColumnMode;
   }

   public void setStartColumnMode(int setting)
   {
      if (setting == INHERIT
       || setting == START_COLUMN_MODE_FALSE
       || setting == START_COLUMN_MODE_TRUE)
      {
         currentStartColumnMode = setting;

         if (setting == START_COLUMN_MODE_TRUE)
         {
            incAlignmentColumn();
         }

         return;
      }

      throw new IllegalArgumentException(
          "Invalid argument '"+setting+"' for setStartColumnMode(int)");
   }

   public int getCurrentAlignmentColumn()
   {
      return currentAlignmentColumn;
   }

   public int getAlignmentColumn()
   {
      if (currentAlignmentColumn == INHERIT)
      {
         if (parent == null)
         {
            return 0;
         }

         return parent.getAlignmentColumn();
      }

      return currentAlignmentColumn;
   }

   public TeXCellAlignList getCurrentAlignmentList()
   {
      return currentAlignmentList;
   }

   public TeXCellAlignList getAlignmentList()
   {
      if (currentAlignmentList == null)
      {
         if (parent == null)
         {
            return null;
         }

         return parent.getAlignmentList();
      }

      return currentAlignmentList;
   }

   public void setAlignmentList(TeXCellAlignList list)
   {
      currentAlignmentList = list;
   }

   private void resetAlignmentColumn()
   {
      currentAlignmentColumn = 0;
   }

   private void incAlignmentColumn()
   {
      currentAlignmentColumn = getAlignmentColumn()+1;
   }

   public int getCurrentAlignmentColumnCount()
   {
      return currentAlignmentList == null ? 0 :
             currentAlignmentList.size();
   }

   public int getAlignmentColumnCount()
   {
      if (currentAlignmentList == null)
      {
         if (parent == null)
         {
            return 0;
         }

         return parent.getAlignmentColumnCount();
      }

      return getCurrentAlignmentColumnCount();
   }

   public Color getFgColor()
   {
      if (currentFgColor == null)
      {
         if (parent == null)
         {
            return Color.black;
         }

         return parent.getFgColor();
      }

      return currentFgColor;
   }

   public Color getBgColor()
   {
      if (currentBgColor == null)
      {
         if (parent == null)
         {
            return null;
         }

         return parent.getBgColor();
      }

      return currentBgColor;
   }

   public void setFontFamily(int setting)
   {
      switch (setting)
      {
         case INHERIT:
         case FAMILY_RM:
         case FAMILY_SF:
         case FAMILY_TT:
         case FAMILY_CAL:
           currentFontFamily = setting;
         break;
         default:
           throw new IllegalArgumentException("Invalid setting '"
            + setting+"' for TeXSettings.setFontFamily(int)");
      }
   }

   public void setFontShape(int setting)
   {
      switch (setting)
      {
         case INHERIT:
         case SHAPE_UP:
         case SHAPE_IT:
         case SHAPE_SL:
         case SHAPE_EM:
         case SHAPE_SC:
           currentFontShape = setting;
         break;
         default:
           throw new IllegalArgumentException("Invalid setting '"
            + setting+"' for TeXSettings.setFontShape(int)");
      }
   }

   public void setFontWeight(int setting)
   {
      switch (setting)
      {
         case INHERIT:
         case WEIGHT_MD:
         case WEIGHT_BF:
           currentFontWeight = setting;
         break;
         default:
           throw new IllegalArgumentException("Invalid setting '"
            + setting+"' for TeXSettings.setFontWeight(int)");
      }
   }

   public void setFontSize(int setting)
   {
      switch (setting)
      {
         case INHERIT:
         case SIZE_NORMAL:
         case SIZE_LARGE:
         case SIZE_XLARGE:
         case SIZE_XXLARGE:
         case SIZE_HUGE:
         case SIZE_XHUGE:
         case SIZE_XXHUGE:
         case SIZE_SMALL:
         case SIZE_FOOTNOTE:
         case SIZE_SCRIPT:
         case SIZE_TINY:
           currentFontSize = setting;
         break;
         default:
           throw new IllegalArgumentException("Invalid setting '"
            + setting+"' for TeXSettings.setFontSize(int)");
      }
   }

   public void setMathFont(int setting)
   {
      switch (setting)
      {
         case INHERIT:
         case MATH_STYLE_RM:
         case MATH_STYLE_SF:
         case MATH_STYLE_TT: 
         case MATH_STYLE_IT:
         case MATH_STYLE_BF:
         case MATH_STYLE_CAL:
         case MATH_STYLE_BB:
         case MATH_STYLE_FRAK:
         case MATH_STYLE_BOLDSYMBOL:
         case MATH_STYLE_PMB:
           currentMathFont = setting;
         break;
         default:
           throw new IllegalArgumentException("Invalid setting '"
            + setting+"' for TeXSettings.setMathFont(int)");
      }
   }

   public void setParAlign(int setting)
   {
      switch (setting)
      {
         case INHERIT:
         case PAR_ALIGN_NORMAL:
         case PAR_ALIGN_LEFT:
         case PAR_ALIGN_RIGHT:
         case PAR_ALIGN_CENTER:
           currentParAlign = setting;
         break;
         default:
           throw new IllegalArgumentException("Invalid setting '"
            + setting+"' for TeXSettings.setParAlign(int)");
      }
   }

   public void setCurrentFgColor(Color color)
   {
      currentFgColor = color;
   }

   public void setCurrentBgColor(Color color)
   {
      currentBgColor = color;
   }

   public Register getRegister(String name)
   {
      Register reg = localRegisters.get(name);

      if (reg == null && parent != null)
      {
         reg = parent.getRegister(name);
      }

      return reg;
   }

   public ControlSequence getControlSequence(String name)
   {
      ControlSequence cs = csTable.get(name);

      if (cs == null)
      {
         cs = localRegisters.get(name);
      }

      if (cs == null && parent != null)
      {
         cs = parent.getControlSequence(name);
      }

      return cs;
   }

   public void putControlSequence(ControlSequence cs)
   {
      csTable.put(cs.getName(), cs);
   }

   public ActiveChar getActiveChar(Integer code)
   {
      ActiveChar ac = activeTable.get(code);

      if (ac == null && parent != null)
      {
         ac = parent.getActiveChar(code);
      }

      return ac;
   }

   public void putActiveChar(ActiveChar activeChar)
   {
      activeTable.put(new Integer((int)activeChar.getChar().charValue()),
        activeChar);
   }

   public CountRegister countdef(String name, int alloc)
   {
      CountRegister reg = new CountRegister(name);
      parser.allocCount(alloc, reg);
      putRegister(reg);

      return reg;
   }

   public CountRegister newcount(boolean isLocal, String name)
   {
      if (isLocal || parent == null)
      {
         return newcount(name);
      }
      else
      {
         return parent.newcount(isLocal, name);
      }
   }

   public CountRegister newcount(String name)
   {
      CountRegister reg = new CountRegister(name);
      parser.allocCount(reg);
      putRegister(reg);

      return reg;
   }

   public DimenRegister dimendef(String name, int alloc)
   {
      DimenRegister reg = new DimenRegister(name);
      parser.allocDimen(alloc, reg);
      putRegister(reg);

      return reg;
   }

   public DimenRegister newdimen(String name)
   {
      DimenRegister reg = new DimenRegister(name);
      parser.allocDimen(reg);
      putRegister(reg);

      return reg;
   }

   public DimenRegister newdimen(boolean isLocal, String name)
   {
      if (isLocal || parent == null)
      {
         return newdimen(name);
      }
      else
      {
         return parent.newdimen(isLocal, name);
      }
   }

   protected Register putRegister(Register register)
   {
      Register rootReg = register;

      String name = register.getName();

      TeXSettings root = parser.getSettings();

      if (this != root)
      {
         Register reg = root.getRegister(name);

         if (reg == null)
         {
            rootReg = root.putRegister((Register)register.clone());
         }
      }

      localRegisters.put(name, register);

      return rootReg;
   }

   public void localSetRegister(String name, Numerical value)
     throws TeXSyntaxException
   {
      Register reg = localRegisters.get(name);

      if (reg == null)
      {
         if (parent != null)
         {
            reg = parent.getRegister(name);
         }

         if (reg == null)
         {
            throw new TeXSyntaxException(
               parser.getCurrentFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
         }

         if (parent != null)
         {
            reg = (Register)reg.clone();
            localRegisters.put(name, reg);
         }
      }

      reg.setValue(parser, value);
   }

   public void globalSetRegister(String name, Numerical value)
     throws TeXSyntaxException
   {
      Register reg = getRegister(name);

      if (reg == null)
      {
         throw new TeXSyntaxException(
            parser.getCurrentFile(),
            parser.getLineNumber(),
            TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
      }

      reg.setValue(parser, value);

      if (parent != null)
      {
         TeXSettings root = parent.getRoot();

         Register rootReg = root.getRegister(name);

         if (rootReg == null)
         {
            root.putRegister((Register)reg.clone());
         }
         else if (reg != rootReg)
         {
            rootReg.setValue(parser, reg);
         }
      }
   }

   public void localAdvanceRegister(String name, Numerical value)
     throws TeXSyntaxException
   {
      Register reg = localRegisters.get(name);

      if (reg == null)
      {
         if (parent != null)
         {
            reg = parent.getRegister(name);
         }

         if (reg == null)
         {
            throw new TeXSyntaxException(
               parser.getCurrentFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
         }

         if (parent != null)
         {
            reg = (Register)reg.clone();
            localRegisters.put(name, reg);
         }
      }

      reg.advance(parser, value);
   }

   public void globalAdvanceRegister(String name, Numerical value)
     throws TeXSyntaxException
   {
      Register reg = getRegister(name);

      if (reg == null)
      {
         throw new TeXSyntaxException(
            parser.getCurrentFile(),
            parser.getLineNumber(),
            TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
      }

      reg.advance(parser, value);

      if (parent != null)
      {
         TeXSettings root = parent.getRoot();

         Register rootReg = root.getRegister(name);

         if (rootReg == null)
         {
            root.putRegister((Register)reg.clone());
         }
         else if (reg != rootReg)
         {
            rootReg.setValue(parser, reg);
         }
      }
   }

   public void localMultiplyRegister(String name, Numerical value)
     throws TeXSyntaxException
   {
      Register reg = localRegisters.get(name);

      if (reg == null)
      {
         if (parent != null)
         {
            reg = parent.getRegister(name);
         }

         if (reg == null)
         {
            throw new TeXSyntaxException(
               parser.getCurrentFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
         }

         if (parent != null)
         {
            reg = (Register)reg.clone();
            localRegisters.put(name, reg);
         }
      }

      reg.multiply(value.number(parser));
   }

   public void globalMultiplyRegister(String name, Numerical value)
     throws TeXSyntaxException
   {
      Register reg = getRegister(name);

      if (reg == null)
      {
         throw new TeXSyntaxException(
            parser.getCurrentFile(),
            parser.getLineNumber(),
            TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
      }

      reg.multiply(value.number(parser));

      if (parent != null)
      {
         TeXSettings root = parent.getRoot();

         Register rootReg = root.getRegister(name);

         if (rootReg == null)
         {
            root.putRegister((Register)reg.clone());
         }
         else if (reg != rootReg)
         {
            rootReg.setValue(parser, reg);
         }
      }
   }

   public void localDivideRegister(String name, Numerical value)
     throws TeXSyntaxException
   {
      Register reg = localRegisters.get(name);

      if (reg == null)
      {
         if (parent != null)
         {
            reg = parent.getRegister(name);
         }

         if (reg == null)
         {
            throw new TeXSyntaxException(
               parser.getCurrentFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
         }

         if (parent != null)
         {
            reg = (Register)reg.clone();
            localRegisters.put(name, reg);
         }
      }

      reg.divide(value.number(parser));
   }

   public void globalDivideRegister(String name, Numerical value)
     throws TeXSyntaxException
   {
      Register reg = getRegister(name);

      if (reg == null)
      {
         throw new TeXSyntaxException(
            parser.getCurrentFile(),
            parser.getLineNumber(),
            TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
      }

      reg.divide(value.number(parser));

      if (parent != null)
      {
         TeXSettings root = parent.getRoot();

         Register rootReg = root.getRegister(name);

         if (rootReg == null)
         {
            root.putRegister((Register)reg.clone());
         }
         else if (reg != rootReg)
         {
            rootReg.setValue(parser, reg);
         }
      }
   }

   private void removeLocalRegister(String name)
   {
      TeXSettings root = parser.getSettings();

      if (this == root || parent == null)
      {
         return;
      }

      localRegisters.remove(name);

      parent.removeLocalRegister(name);
   }

   public boolean isMathBold()
   {
      if (currentMathFont == MATH_STYLE_BF || currentMathFont == MATH_STYLE_BOLDSYMBOL
       || currentMathFont == MATH_STYLE_PMB)
      {
         return true;
      }

      if (currentMathFont == INHERIT)
      {
         if (parent == null)
         {
            return false;
         }

         return parent.isMathBold();
      }

      return false;
   }

   public boolean isMathSf()
   {
      if (currentMathFont == MATH_STYLE_SF)
      {
         return true;
      }

      if (currentMathFont == INHERIT)
      {
         if (parent == null)
         {
            return false;
         }

         return parent.isMathSf();
      }

      return false;
   }

   public boolean isMathRm()
   {
      if (currentMathFont == MATH_STYLE_RM)
      {
         return true;
      }

      if (currentMathFont == INHERIT)
      {
         if (parent == null)
         {
            return false;
         }

         return parent.isMathRm();
      }

      return false;
   }

   public boolean isMathTt()
   {
      if (currentMathFont == MATH_STYLE_TT)
      {
         return true;
      }

      if (currentMathFont == INHERIT)
      {
         if (parent == null)
         {
            return false;
         }

         return parent.isMathTt();
      }

      return false;
   }

   public boolean isMathCal()
   {
      if (currentMathFont == MATH_STYLE_CAL)
      {
         return true;
      }

      if (currentMathFont == INHERIT)
      {
         if (parent == null)
         {
            return false;
         }

         return parent.isMathCal();
      }

      return false;
   }

   public boolean isMathFrak()
   {
      if (currentMathFont == MATH_STYLE_FRAK)
      {
         return true;
      }

      if (currentMathFont == INHERIT)
      {
         if (parent == null)
         {
            return false;
         }

         return parent.isMathFrak();
      }

      return false;
   }

   public boolean isMathBb()
   {
      if (currentMathFont == MATH_STYLE_BB)
      {
         return true;
      }

      if (currentMathFont == INHERIT)
      {
         if (parent == null)
         {
            return false;
         }

         return parent.isMathBb();
      }

      return false;
   }

   public boolean isMathIt()
   {
      if (currentMathFont == MATH_STYLE_RM || currentMathFont == MATH_STYLE_SF
        || currentMathFont == MATH_STYLE_TT || currentMathFont == MATH_STYLE_BF
        || currentMathFont == MATH_STYLE_CAL || currentMathFont == MATH_STYLE_BB)
      {
          return false;
      }

      if (currentMathFont == INHERIT)
      {
         if (parent == null)
         {
            return true;
         }

         return parent.isMathIt();
      }

      return true;
   }

   public boolean isTextSansSerif()
   {
      if (currentFontFamily == FAMILY_SF)
      {
         return true;
      }

      if (currentFontFamily == INHERIT)
      {
         if (parent == null)
         {
            return false;
         }

         return parent.isTextSansSerif();
      }

      return true;
   }

   public boolean isTextItalic()
   {
      if (currentFontShape == SHAPE_IT || currentFontShape == SHAPE_SL)
      {
         return true;
      }

      if (currentFontShape == SHAPE_EM)
      {
         if (parent == null)
         {
            return true;
         }

         return !parent.isTextItalic();
      }

      if (currentFontShape == INHERIT)
      {
         if (parent == null)
         {
            return false;
         }
      }

      return false;
   }

   public int getCharCode(int charCode)
   {
      if (getCharMapMode() == CHAR_MAP_OFF)
      {
         return -1;
      }

      switch (getMode())
      {
         case MODE_TEXT :

           if (charCode == (int)'\'')
           {
              return 0x2019;
           }

           return -1;

         case MODE_INLINE_MATH:
         case MODE_DISPLAY_MATH:

            if (charCode == (int)'-')
            {
               charCode = 0x2212;
            }
            else if (charCode == (int)'/')
            {
               charCode = 0x2215;
            }

            if (isMathBb())
            {
               return getCode(charCode, BLACKBOARD_BOLD);
            }

            if (isMathFrak())
            {
               if (isMathBold())
               {
                  return getCode(charCode, BLACKBOARD_BOLD);
               }

               return getCode(charCode, FRAKTUR); 
            }

            if (isMathCal())
            {
               if (isMathBold())
               {
                  return getCode(charCode, MATHCALSCRIPT_BOLD); 
               }

               return getCode(charCode, MATHCALSCRIPT);
            }

            if (isMathIt())
            {
               if (isMathBold())
               {
                  if (isMathSf())
                  {
                     return getCode(charCode, MATH_IT_BOLD_SF);
                  }

                  if (!isMathTt())
                  {
                     return getCode(charCode, MATH_IT_BOLD);
                  }
               }

               if (isMathSf())
               {
                  return getCode(charCode, MATH_IT_SF);
               }

               if (isMathTt())
               {
                  return getCode(charCode, MATH_IT);
               }
            }

            if (isMathSf())
            {
               if (isMathBold())
               {
                  return getCode(charCode, MATH_UP_BOLD_SF);
               }

               return getCode(charCode, MATH_UP_SF);
            }

            if (isMathTt())
            {
               return getCode(charCode, MATH_TT);
            }

            if (isMathBold())
            {
               return getCode(charCode, MATH_UP_BOLD);
            }

      }

      return -1;
   }

   public static int getCode(int charCode, int[][] array)
   {
      for (int i = 0; i < array.length; i++)
      {
         if (array[i][0] == charCode)
         {
            return array[i][1];
         }
      }

      return -1;
   }

   public TeXSettings getParent()
   {
      return parent;
   }

   private TeXSettings parent;

   public static final int INHERIT=-1, USER=-2;

   public static final int FAMILY_RM=0, FAMILY_SF=1, FAMILY_TT=2, FAMILY_CAL=3;

   public static final int SHAPE_UP=0, SHAPE_IT=1, SHAPE_SL=2,
     SHAPE_EM=3, SHAPE_SC=4;

   public static final int WEIGHT_MD=0, WEIGHT_BF=1;

   public static final int SIZE_NORMAL=0, SIZE_LARGE=1, SIZE_XLARGE=2,
     SIZE_XXLARGE=3, SIZE_HUGE=4, SIZE_XHUGE=5, SIZE_XXHUGE=6,
     SIZE_SMALL=7, SIZE_FOOTNOTE=8, SIZE_SCRIPT=9, SIZE_TINY=10;

   public static final int MATH_STYLE_RM=0, MATH_STYLE_SF=1, MATH_STYLE_TT=2, 
     MATH_STYLE_IT=3, MATH_STYLE_BF=4, MATH_STYLE_CAL=5, MATH_STYLE_BB=6,
     MATH_STYLE_FRAK=7, MATH_STYLE_BOLDSYMBOL=8, MATH_STYLE_PMB=9,
     MATH_STYLE_NORMAL=10;

   public static final int PAR_ALIGN_NORMAL=0, PAR_ALIGN_LEFT=1,
     PAR_ALIGN_RIGHT=2, PAR_ALIGN_CENTER=3;

   public static final int MODE_TEXT = 0, MODE_INLINE_MATH = 1,
      MODE_DISPLAY_MATH = 2;

   public static final int CHAR_MAP_OFF = 0, CHAR_MAP_ON = 1;

   public static final int ALIGN_MODE_FALSE=0, ALIGN_MODE_TRUE=1;

   public static final int START_ROW_MODE_FALSE=0, START_ROW_MODE_TRUE=1;

   public static final int START_COLUMN_MODE_FALSE=0, START_COLUMN_MODE_TRUE=1;

   public static final int[][] BLACKBOARD_BOLD =
   {
      new int[] {(int)'C', 0x2102},
      new int[] {(int)'H', 0x210D},
      new int[] {(int)'N', 0x2115},
      new int[] {(int)'P', 0x2119},
      new int[] {(int)'Q', 0x211A},
      new int[] {(int)'R', 0x211D},
      new int[] {(int)'Z', 0x2124},
      new int[] {(int)'D', 0x2145},
      new int[] {(int)'d', 0x2146},
      new int[] {(int)'e', 0x2147},
      new int[] {(int)'i', 0x2148},
      new int[] {(int)'j', 0x2149},
      new int[] {(int)'A', 0x1D538},
      new int[] {(int)'B', 0x1D539},
      new int[] {(int)'D', 0x1D53B},
      new int[] {(int)'E', 0x1D53C},
      new int[] {(int)'F', 0x1D53D},
      new int[] {(int)'G', 0x1D53E},
      new int[] {(int)'I', 0x1D540},
      new int[] {(int)'J', 0x1D541},
      new int[] {(int)'K', 0x1D542},
      new int[] {(int)'L', 0x1D543},
      new int[] {(int)'L', 0x1D544},
      new int[] {(int)'L', 0x1D546},
      new int[] {(int)'S', 0x1D54A},
      new int[] {(int)'T', 0x1D54B},
      new int[] {(int)'U', 0x1D54C},
      new int[] {(int)'V', 0x1D54D},
      new int[] {(int)'W', 0x1D54E},
      new int[] {(int)'X', 0x1D54F},
      new int[] {(int)'Y', 0x1D550},
      new int[] {(int)'a', 0x1D552},
      new int[] {(int)'b', 0x1D552},
      new int[] {(int)'c', 0x1D553},
      new int[] {(int)'d', 0x1D555},
      new int[] {(int)'e', 0x1D556},
      new int[] {(int)'f', 0x1D557},
      new int[] {(int)'g', 0x1D558},
      new int[] {(int)'h', 0x1D559},
      new int[] {(int)'i', 0x1D55A},
      new int[] {(int)'j', 0x1D55B},
      new int[] {(int)'k', 0x1D55C},
      new int[] {(int)'l', 0x1D55D},
      new int[] {(int)'m', 0x1D55E},
      new int[] {(int)'n', 0x1D55F},
      new int[] {(int)'o', 0x1D560},
      new int[] {(int)'p', 0x1D561},
      new int[] {(int)'q', 0x1D562},
      new int[] {(int)'r', 0x1D563},
      new int[] {(int)'s', 0x1D564},
      new int[] {(int)'t', 0x1D565},
      new int[] {(int)'u', 0x1D566},
      new int[] {(int)'v', 0x1D567},
      new int[] {(int)'w', 0x1D568},
      new int[] {(int)'x', 0x1D569},
      new int[] {(int)'y', 0x1D56A},
      new int[] {(int)'z', 0x1D56B},
      new int[] {(int)'0', 0x1D7D8},
      new int[] {(int)'1', 0x1D7D9},
      new int[] {(int)'2', 0x1D7DA},
      new int[] {(int)'3', 0x1D7DB},
      new int[] {(int)'4', 0x1D7DC},
      new int[] {(int)'5', 0x1D7DD},
      new int[] {(int)'6', 0x1D7DE},
      new int[] {(int)'7', 0x1D7DF},
      new int[] {(int)'8', 0x1D7E0},
      new int[] {(int)'9', 0x1D7E1},
      new int[]{0x1D70B, 0x213C}, // pi
      new int[]{0x1D6FE, 0x213D}, // gamma
      new int[]{0x1D6E4, 0x213E}, // Gamma
      new int[]{0x1D6F1, 0x213F}, // Pi
      new int[]{0x1D6F4, 0x2140}, // Sigma
   };

   public static final int[][] FRAKTUR =
   {
      new int[] {(int)'A', 0x1D504},
      new int[] {(int)'B', 0x1D505},
      new int[] {(int)'D', 0x1D507},
      new int[] {(int)'E', 0x1D508},
      new int[] {(int)'F', 0x1D509},
      new int[] {(int)'G', 0x1D50A},
      new int[] {(int)'J', 0x1D50D},
      new int[] {(int)'K', 0x1D50E},
      new int[] {(int)'L', 0x1D50F},
      new int[] {(int)'M', 0x1D510},
      new int[] {(int)'N', 0x1D511},
      new int[] {(int)'O', 0x1D512},
      new int[] {(int)'P', 0x1D513},
      new int[] {(int)'Q', 0x1D514},
      new int[] {(int)'S', 0x1D516},
      new int[] {(int)'T', 0x1D517},
      new int[] {(int)'U', 0x1D518},
      new int[] {(int)'V', 0x1D519},
      new int[] {(int)'W', 0x1D51A},
      new int[] {(int)'X', 0x1D51B},
      new int[] {(int)'Y', 0x1D51C},
      new int[] {(int)'a', 0x1D51E},
      new int[] {(int)'b', 0x1D51F},
      new int[] {(int)'c', 0x1D520},
      new int[] {(int)'d', 0x1D521},
      new int[] {(int)'e', 0x1D522},
      new int[] {(int)'f', 0x1D523},
      new int[] {(int)'g', 0x1D524},
      new int[] {(int)'h', 0x1D525},
      new int[] {(int)'i', 0x1D526},
      new int[] {(int)'j', 0x1D527},
      new int[] {(int)'k', 0x1D528},
      new int[] {(int)'l', 0x1D529},
      new int[] {(int)'m', 0x1D52A},
      new int[] {(int)'n', 0x1D52B},
      new int[] {(int)'o', 0x1D52C},
      new int[] {(int)'p', 0x1D52D},
      new int[] {(int)'q', 0x1D52E},
      new int[] {(int)'r', 0x1D52F},
      new int[] {(int)'s', 0x1D530},
      new int[] {(int)'t', 0x1D531},
      new int[] {(int)'t', 0x1D531},
      new int[] {(int)'u', 0x1D532},
      new int[] {(int)'v', 0x1D533},
      new int[] {(int)'w', 0x1D534},
      new int[] {(int)'x', 0x1D535},
      new int[] {(int)'y', 0x1D536},
      new int[] {(int)'z', 0x1D537},
   };

   public static final int[][] FRAKTUR_BOLD =
   {
      new int[] {(int)'A', 0x1D56C},
      new int[] {(int)'B', 0x1D56D},
      new int[] {(int)'C', 0x1D56E},
      new int[] {(int)'D', 0x1D56F},
      new int[] {(int)'E', 0x1D570},
      new int[] {(int)'F', 0x1D571},
      new int[] {(int)'G', 0x1D572},
      new int[] {(int)'H', 0x1D573},
      new int[] {(int)'I', 0x1D574},
      new int[] {(int)'J', 0x1D575},
      new int[] {(int)'K', 0x1D576},
      new int[] {(int)'L', 0x1D577},
      new int[] {(int)'M', 0x1D578},
      new int[] {(int)'N', 0x1D579},
      new int[] {(int)'O', 0x1D57A},
      new int[] {(int)'P', 0x1D57B},
      new int[] {(int)'Q', 0x1D57C},
      new int[] {(int)'R', 0x1D57D},
      new int[] {(int)'S', 0x1D57E},
      new int[] {(int)'T', 0x1D57F},
      new int[] {(int)'U', 0x1D580},
      new int[] {(int)'V', 0x1D581},
      new int[] {(int)'W', 0x1D582},
      new int[] {(int)'X', 0x1D583},
      new int[] {(int)'Y', 0x1D584},
      new int[] {(int)'Z', 0x1D585},
      new int[] {(int)'a', 0x1D586},
      new int[] {(int)'b', 0x1D587},
      new int[] {(int)'c', 0x1D588},
      new int[] {(int)'d', 0x1D589},
      new int[] {(int)'e', 0x1D58A},
      new int[] {(int)'f', 0x1D58B},
      new int[] {(int)'g', 0x1D58C},
      new int[] {(int)'h', 0x1D58D},
      new int[] {(int)'i', 0x1D58E},
      new int[] {(int)'j', 0x1D58F},
      new int[] {(int)'k', 0x1D590},
      new int[] {(int)'l', 0x1D591},
      new int[] {(int)'m', 0x1D592},
      new int[] {(int)'n', 0x1D593},
      new int[] {(int)'o', 0x1D594},
      new int[] {(int)'p', 0x1D595},
      new int[] {(int)'q', 0x1D596},
      new int[] {(int)'r', 0x1D597},
      new int[] {(int)'s', 0x1D598},
      new int[] {(int)'t', 0x1D599},
      new int[] {(int)'u', 0x1D59A},
      new int[] {(int)'v', 0x1D59B},
      new int[] {(int)'w', 0x1D59C},
      new int[] {(int)'x', 0x1D59D},
      new int[] {(int)'y', 0x1D59E},
      new int[] {(int)'z', 0x1D59F}
   };

   public static final int[][] MATHCALSCRIPT =
   {
      new int[] {(int)'g', 0x210A},
      new int[] {(int)'H', 0x210B},
      new int[] {(int)'I', 0x2110},
      new int[] {(int)'L', 0x2112},
      new int[] {(int)'R', 0x211B},
      new int[] {(int)'B', 0x212C},
      new int[] {(int)'e', 0x212F},
      new int[] {(int)'E', 0x2130},
      new int[] {(int)'F', 0x2131},
      new int[] {(int)'M', 0x2133},
      new int[] {(int)'o', 0x2134},
      new int[] {(int)'A', 0x1D49C},
      new int[] {(int)'C', 0x1D49E},
      new int[] {(int)'D', 0x1D49F},
      new int[] {(int)'G', 0x1D4A2},
      new int[] {(int)'J', 0x1D4A5},
      new int[] {(int)'K', 0x1D4A6},
      new int[] {(int)'N', 0x1D4A9},
      new int[] {(int)'O', 0x1D4AA},
      new int[] {(int)'P', 0x1D4AB},
      new int[] {(int)'Q', 0x1D4AC},
      new int[] {(int)'S', 0x1D4AE},
      new int[] {(int)'T', 0x1D4AF},
      new int[] {(int)'U', 0x1D4B0},
      new int[] {(int)'V', 0x1D4B1},
      new int[] {(int)'W', 0x1D4B2},
      new int[] {(int)'X', 0x1D4B3},
      new int[] {(int)'Y', 0x1D4B4},
      new int[] {(int)'Z', 0x1D4B5},
      new int[] {(int)'a', 0x1D4B6},
      new int[] {(int)'b', 0x1D4B7},
      new int[] {(int)'c', 0x1D4B8},
      new int[] {(int)'d', 0x1D4B9},
      new int[] {(int)'f', 0x1D4BB},
      new int[] {(int)'h', 0x1D4BD},
      new int[] {(int)'i', 0x1D4BE},
      new int[] {(int)'j', 0x1D4BF},
      new int[] {(int)'k', 0x1D4C0},
      new int[] {(int)'l', 0x1D4C1},
      new int[] {(int)'m', 0x1D4C2},
      new int[] {(int)'n', 0x1D4C3},
      new int[] {(int)'p', 0x1D4C5},
      new int[] {(int)'q', 0x1D4C6},
      new int[] {(int)'r', 0x1D4C7},
      new int[] {(int)'s', 0x1D4C8},
      new int[] {(int)'t', 0x1D4C9},
      new int[] {(int)'u', 0x1D4CA},
      new int[] {(int)'v', 0x1D4CB},
      new int[] {(int)'w', 0x1D4CC},
      new int[] {(int)'x', 0x1D4CD},
      new int[] {(int)'y', 0x1D4CE},
      new int[] {(int)'z', 0x1D4CF}
   };

   public static final int[][] MATHCALSCRIPT_BOLD =
   {
      new int[] {(int)'A', 0x1D4D0},
      new int[] {(int)'B', 0x1D4D1},
      new int[] {(int)'C', 0x1D4D2},
      new int[] {(int)'D', 0x1D4D3},
      new int[] {(int)'E', 0x1D4D4},
      new int[] {(int)'F', 0x1D4D5},
      new int[] {(int)'G', 0x1D4D6},
      new int[] {(int)'H', 0x1D4D7},
      new int[] {(int)'I', 0x1D4D8},
      new int[] {(int)'J', 0x1D4D9},
      new int[] {(int)'K', 0x1D4DA},
      new int[] {(int)'L', 0x1D4DB},
      new int[] {(int)'M', 0x1D4DC},
      new int[] {(int)'N', 0x1D4DD},
      new int[] {(int)'O', 0x1D4DE},
      new int[] {(int)'P', 0x1D4DF},
      new int[] {(int)'Q', 0x1D4E0},
      new int[] {(int)'R', 0x1D4E1},
      new int[] {(int)'S', 0x1D4E2},
      new int[] {(int)'T', 0x1D4E3},
      new int[] {(int)'U', 0x1D4E4},
      new int[] {(int)'V', 0x1D4E5},
      new int[] {(int)'W', 0x1D4E6},
      new int[] {(int)'X', 0x1D4E7},
      new int[] {(int)'Y', 0x1D4E8},
      new int[] {(int)'Z', 0x1D4E9},
      new int[] {(int)'a', 0x1D4EA},
      new int[] {(int)'b', 0x1D4EB},
      new int[] {(int)'c', 0x1D4EC},
      new int[] {(int)'d', 0x1D4ED},
      new int[] {(int)'e', 0x1D4EE},
      new int[] {(int)'f', 0x1D4EF},
      new int[] {(int)'g', 0x1D4F0},
      new int[] {(int)'h', 0x1D4F1},
      new int[] {(int)'i', 0x1D4F2},
      new int[] {(int)'j', 0x1D4F3},
      new int[] {(int)'k', 0x1D4F4},
      new int[] {(int)'l', 0x1D4F5},
      new int[] {(int)'m', 0x1D4F6},
      new int[] {(int)'n', 0x1D4F7},
      new int[] {(int)'o', 0x1D4F8},
      new int[] {(int)'p', 0x1D4F9},
      new int[] {(int)'q', 0x1D4FA},
      new int[] {(int)'r', 0x1D4FB},
      new int[] {(int)'s', 0x1D4FC},
      new int[] {(int)'t', 0x1D4FD},
      new int[] {(int)'u', 0x1D4FE},
      new int[] {(int)'v', 0x1D4FF},
      new int[] {(int)'w', 0x1D500},
      new int[] {(int)'x', 0x1D501},
      new int[] {(int)'y', 0x1D502},
      new int[] {(int)'z', 0x1D503}
   };

   public static final int[][] MATH_IT =
   {
      new int[] {(int)'A', 0x1D434},
      new int[] {(int)'B', 0x1D435},
      new int[] {(int)'C', 0x1D436},
      new int[] {(int)'D', 0x1D437},
      new int[] {(int)'E', 0x1D438},
      new int[] {(int)'F', 0x1D439},
      new int[] {(int)'G', 0x1D43A},
      new int[] {(int)'H', 0x1D43B},
      new int[] {(int)'I', 0x1D43C},
      new int[] {(int)'J', 0x1D43D},
      new int[] {(int)'K', 0x1D43E},
      new int[] {(int)'L', 0x1D43F},
      new int[] {(int)'M', 0x1D440},
      new int[] {(int)'N', 0x1D441},
      new int[] {(int)'O', 0x1D442},
      new int[] {(int)'P', 0x1D443},
      new int[] {(int)'Q', 0x1D444},
      new int[] {(int)'R', 0x1D445},
      new int[] {(int)'S', 0x1D446},
      new int[] {(int)'T', 0x1D447},
      new int[] {(int)'U', 0x1D448},
      new int[] {(int)'V', 0x1D449},
      new int[] {(int)'W', 0x1D44A},
      new int[] {(int)'X', 0x1D44B},
      new int[] {(int)'Y', 0x1D44C},
      new int[] {(int)'Z', 0x1D44D},
      new int[] {(int)'a', 0x1D44E},
      new int[] {(int)'b', 0x1D44F},
      new int[] {(int)'c', 0x1D450},
      new int[] {(int)'d', 0x1D451},
      new int[] {(int)'e', 0x1D452},
      new int[] {(int)'f', 0x1D453},
      new int[] {(int)'g', 0x1D454},
      new int[] {(int)'i', 0x1D456},
      new int[] {(int)'j', 0x1D457},
      new int[] {(int)'k', 0x1D458},
      new int[] {(int)'l', 0x1D459},
      new int[] {(int)'m', 0x1D45A},
      new int[] {(int)'n', 0x1D45B},
      new int[] {(int)'o', 0x1D45C},
      new int[] {(int)'p', 0x1D45D},
      new int[] {(int)'q', 0x1D45E},
      new int[] {(int)'r', 0x1D45F},
      new int[] {(int)'s', 0x1D460},
      new int[] {(int)'t', 0x1D461},
      new int[] {(int)'u', 0x1D462},
      new int[] {(int)'v', 0x1D463},
      new int[] {(int)'w', 0x1D464},
      new int[] {(int)'x', 0x1D465},
      new int[] {(int)'y', 0x1D466},
      new int[] {(int)'z', 0x1D467},
      new int[]{0x1D6E2, 0x1D6E2}, // Alpha
      new int[]{0x1D6E3, 0x1D6E3}, // Beta
      new int[]{0x1D6E4, 0x1D6E4}, // Gamma
      new int[]{0x1D6E5, 0x1D6E5}, // Delta
      new int[]{0x1D6E6, 0x1D6E6}, // Epsilon
      new int[]{0x1D6E7, 0x1D6E7}, // Zeta
      new int[]{0x1D6E8, 0x1D6E8}, // Eta
      new int[]{0x1D6E9, 0x1D6E9}, // Theta
      new int[]{0x1D6EA, 0x1D6EA}, // Iota
      new int[]{0x1D6EB, 0x1D6EB}, // Kappa
      new int[]{0x1D6EC, 0x1D6EC}, // Lambda
      new int[]{0x1D6ED, 0x1D6ED}, // Mu
      new int[]{0x1D6EE, 0x1D6EE}, // Nu
      new int[]{0x1D6EF, 0x1D6EF}, // Xi
      new int[]{0x1D6F0, 0x1D6F0}, // Omicron
      new int[]{0x1D6F1, 0x1D6F1}, // Pi
      new int[]{0x1D6F2, 0x1D6F2}, // Rho
      new int[]{0x1D6F3, 0x1D6F3}, // Theta
      new int[]{0x1D6F4, 0x1D6F4}, // Sigma
      new int[]{0x1D6F5, 0x1D6F5}, // Tau
      new int[]{0x1D6F6, 0x1D6F6}, // Upsilon
      new int[]{0x1D6F7, 0x1D6F7}, // Phi
      new int[]{0x1D6F8, 0x1D6F8}, // Chi
      new int[]{0x1D6F9, 0x1D6F9}, // Psi
      new int[]{0x1D6FA, 0x1D6FA}, // Omega
      new int[]{0x1D6FB, 0x2207}, // nabla
      new int[]{0x1D6FC, 0x1D6FC}, // alpha
      new int[]{0x1D6FD, 0x1D6FD}, // beta
      new int[]{0x1D6FE, 0x1D6FE}, // gamma
      new int[]{0x1D6FF, 0x1D6FF}, // delta
      new int[]{0x1D700, 0x1D700}, // varepsilon
      new int[]{0x1D701, 0x1D701}, // zeta
      new int[]{0x1D702, 0x1D702}, // eta
      new int[]{0x1D703, 0x1D703}, // theta
      new int[]{0x1D704, 0x1D704}, // iota
      new int[]{0x1D705, 0x1D705}, // kappa
      new int[]{0x1D706, 0x1D706}, // lambda
      new int[]{0x1D707, 0x1D707}, // mu
      new int[]{0x1D708, 0x1D708}, // nu
      new int[]{0x1D709, 0x1D709}, // xi
      new int[]{0x1D70A, 0x1D70A}, // omicron
      new int[]{0x1D70B, 0x1D70B}, // pi
      new int[]{0x1D70C, 0x1D70C}, // rho
      new int[]{0x1D70D, 0x1D70D}, // varsigma
      new int[]{0x1D70E, 0x1D70E}, // sigma
      new int[]{0x1D70F, 0x1D70F}, // tau
      new int[]{0x1D710, 0x1D710}, // upsilon
      new int[]{0x1D711, 0x1D711}, // varphi
      new int[]{0x1D712, 0x1D712}, // chi
      new int[]{0x1D713, 0x1D713}, // psi
      new int[]{0x1D714, 0x1D714}, // omega
      new int[]{0x1D715, 0x1D715}, // partial
      new int[]{0x1D716, 0x1D716}, // epsilon
      new int[]{0x1D717, 0x1D717}, // vartheta
      new int[]{0x1D718, 0x1D718}, // varkappa
      new int[]{0x1D719, 0x1D719}, // phi
      new int[]{0x1D71A, 0x1D71A}, // varrho
      new int[]{0x1D71B, 0x1D71B}, // varpi
   };

   public static final int[][] MATH_IT_BOLD =
   {
      new int[] {(int)'A', 0x1D468},
      new int[] {(int)'B', 0x1D469},
      new int[] {(int)'C', 0x1D46A},
      new int[] {(int)'D', 0x1D46B},
      new int[] {(int)'E', 0x1D46C},
      new int[] {(int)'F', 0x1D46D},
      new int[] {(int)'G', 0x1D46E},
      new int[] {(int)'H', 0x1D46F},
      new int[] {(int)'I', 0x1D470},
      new int[] {(int)'J', 0x1D471},
      new int[] {(int)'K', 0x1D472},
      new int[] {(int)'L', 0x1D473},
      new int[] {(int)'M', 0x1D474},
      new int[] {(int)'N', 0x1D475},
      new int[] {(int)'O', 0x1D476},
      new int[] {(int)'P', 0x1D477},
      new int[] {(int)'Q', 0x1D478},
      new int[] {(int)'R', 0x1D479},
      new int[] {(int)'S', 0x1D47A},
      new int[] {(int)'T', 0x1D47B},
      new int[] {(int)'U', 0x1D47C},
      new int[] {(int)'V', 0x1D47D},
      new int[] {(int)'W', 0x1D47E},
      new int[] {(int)'X', 0x1D47F},
      new int[] {(int)'Y', 0x1D480},
      new int[] {(int)'Z', 0x1D481},
      new int[] {(int)'a', 0x1D482},
      new int[] {(int)'b', 0x1D483},
      new int[] {(int)'c', 0x1D484},
      new int[] {(int)'d', 0x1D485},
      new int[] {(int)'e', 0x1D486},
      new int[] {(int)'f', 0x1D487},
      new int[] {(int)'g', 0x1D488},
      new int[] {(int)'h', 0x1D489},
      new int[] {(int)'i', 0x1D48A},
      new int[] {(int)'j', 0x1D48B},
      new int[] {(int)'k', 0x1D48C},
      new int[] {(int)'l', 0x1D48D},
      new int[] {(int)'m', 0x1D48E},
      new int[] {(int)'n', 0x1D48F},
      new int[] {(int)'o', 0x1D490},
      new int[] {(int)'p', 0x1D491},
      new int[] {(int)'q', 0x1D492},
      new int[] {(int)'r', 0x1D493},
      new int[] {(int)'s', 0x1D494},
      new int[] {(int)'t', 0x1D495},
      new int[] {(int)'u', 0x1D496},
      new int[] {(int)'v', 0x1D497},
      new int[] {(int)'w', 0x1D498},
      new int[] {(int)'x', 0x1D499},
      new int[] {(int)'y', 0x1D49A},
      new int[] {(int)'z', 0x1D49B},
      new int[]{0x1D6E2, 0x1D71C}, // Alpha
      new int[]{0x1D6E3, 0x1D71D}, // Beta
      new int[]{0x1D6E4, 0x1D71E}, // Gamma
      new int[]{0x1D6E5, 0x1D71F}, // Delta
      new int[]{0x1D6E6, 0x1D720}, // Epsilon
      new int[]{0x1D6E7, 0x1D721}, // Zeta
      new int[]{0x1D6E8, 0x1D722}, // Eta
      new int[]{0x1D6E9, 0x1D723}, // Theta
      new int[]{0x1D6EA, 0x1D724}, // Iota
      new int[]{0x1D6EB, 0x1D725}, // Kappa
      new int[]{0x1D6EC, 0x1D726}, // Lambda
      new int[]{0x1D6ED, 0x1D727}, // Mu
      new int[]{0x1D6EE, 0x1D728}, // Nu
      new int[]{0x1D6EF, 0x1D729}, // Xi
      new int[]{0x1D6F0, 0x1D72A}, // Omicron
      new int[]{0x1D6F1, 0x1D72B}, // Pi
      new int[]{0x1D6F2, 0x1D72C}, // Rho
      new int[]{0x1D6F3, 0x1D72D}, // Theta
      new int[]{0x1D6F4, 0x1D72E}, // Sigma
      new int[]{0x1D6F5, 0x1D72F}, // Tau
      new int[]{0x1D6F6, 0x1D730}, // Upsilon
      new int[]{0x1D6F7, 0x1D731}, // Phi
      new int[]{0x1D6F8, 0x1D732}, // Chi
      new int[]{0x1D6F9, 0x1D733}, // Psi
      new int[]{0x1D6FA, 0x1D734}, // Omega
      new int[]{0x1D6FB, 0x1D735}, // nabla
      new int[]{0x1D6FC, 0x1D736}, // alpha
      new int[]{0x1D6FD, 0x1D737}, // beta
      new int[]{0x1D6FE, 0x1D738}, // gamma
      new int[]{0x1D6FF, 0x1D739}, // delta
      new int[]{0x1D700, 0x1D73A}, // varepsilon
      new int[]{0x1D701, 0x1D73B}, // zeta
      new int[]{0x1D702, 0x1D73C}, // eta
      new int[]{0x1D703, 0x1D73D}, // theta
      new int[]{0x1D704, 0x1D73E}, // iota
      new int[]{0x1D705, 0x1D73F}, // kappa
      new int[]{0x1D706, 0x1D740}, // lambda
      new int[]{0x1D707, 0x1D741}, // mu
      new int[]{0x1D708, 0x1D742}, // nu
      new int[]{0x1D709, 0x1D743}, // xi
      new int[]{0x1D70A, 0x1D744}, // omicron
      new int[]{0x1D70B, 0x1D745}, // pi
      new int[]{0x1D70C, 0x1D746}, // rho
      new int[]{0x1D70D, 0x1D747}, // varsigma
      new int[]{0x1D70E, 0x1D748}, // sigma
      new int[]{0x1D70F, 0x1D749}, // tau
      new int[]{0x1D710, 0x1D74A}, // upsilon
      new int[]{0x1D711, 0x1D74B}, // varphi
      new int[]{0x1D712, 0x1D74C}, // chi
      new int[]{0x1D713, 0x1D74D}, // psi
      new int[]{0x1D714, 0x1D74E}, // omega
      new int[]{0x1D715, 0x1D74F}, // partial
      new int[]{0x1D716, 0x1D750}, // epsilon
      new int[]{0x1D717, 0x1D751}, // vartheta
      new int[]{0x1D718, 0x1D752}, // varkappa
      new int[]{0x1D719, 0x1D753}, // phi
      new int[]{0x1D71A, 0x1D754}, // varrho
      new int[]{0x1D71B, 0x1D755}, // varpi
   };

   public static final int[][] MATH_UP_BOLD =
   {
      new int[] {(int)'A', 0x1D400},
      new int[] {(int)'B', 0x1D401},
      new int[] {(int)'C', 0x1D402},
      new int[] {(int)'D', 0x1D403},
      new int[] {(int)'E', 0x1D404},
      new int[] {(int)'F', 0x1D405},
      new int[] {(int)'G', 0x1D406},
      new int[] {(int)'H', 0x1D407},
      new int[] {(int)'I', 0x1D408},
      new int[] {(int)'J', 0x1D409},
      new int[] {(int)'K', 0x1D40A},
      new int[] {(int)'L', 0x1D40B},
      new int[] {(int)'M', 0x1D40C},
      new int[] {(int)'N', 0x1D40D},
      new int[] {(int)'O', 0x1D40E},
      new int[] {(int)'P', 0x1D40F},
      new int[] {(int)'Q', 0x1D410},
      new int[] {(int)'R', 0x1D411},
      new int[] {(int)'S', 0x1D412},
      new int[] {(int)'T', 0x1D413},
      new int[] {(int)'U', 0x1D414},
      new int[] {(int)'V', 0x1D415},
      new int[] {(int)'W', 0x1D416},
      new int[] {(int)'X', 0x1D417},
      new int[] {(int)'Y', 0x1D418},
      new int[] {(int)'Z', 0x1D419},
      new int[] {(int)'a', 0x1D41A},
      new int[] {(int)'b', 0x1D41B},
      new int[] {(int)'c', 0x1D41C},
      new int[] {(int)'d', 0x1D41D},
      new int[] {(int)'e', 0x1D41E},
      new int[] {(int)'f', 0x1D41F},
      new int[] {(int)'g', 0x1D420},
      new int[] {(int)'h', 0x1D421},
      new int[] {(int)'i', 0x1D422},
      new int[] {(int)'j', 0x1D423},
      new int[] {(int)'k', 0x1D424},
      new int[] {(int)'l', 0x1D425},
      new int[] {(int)'m', 0x1D426},
      new int[] {(int)'n', 0x1D427},
      new int[] {(int)'o', 0x1D428},
      new int[] {(int)'p', 0x1D429},
      new int[] {(int)'q', 0x1D42A},
      new int[] {(int)'r', 0x1D42B},
      new int[] {(int)'s', 0x1D42C},
      new int[] {(int)'t', 0x1D42D},
      new int[] {(int)'u', 0x1D42E},
      new int[] {(int)'v', 0x1D42F},
      new int[] {(int)'w', 0x1D430},
      new int[] {(int)'x', 0x1D431},
      new int[] {(int)'y', 0x1D432},
      new int[] {(int)'z', 0x1D433},
      new int[] {(int)'0', 0x1D7CE},
      new int[] {(int)'1', 0x1D7CF},
      new int[] {(int)'2', 0x1D7D0},
      new int[] {(int)'3', 0x1D7D1},
      new int[] {(int)'4', 0x1D7D2},
      new int[] {(int)'5', 0x1D7D3},
      new int[] {(int)'6', 0x1D7D4},
      new int[] {(int)'7', 0x1D7D5},
      new int[] {(int)'8', 0x1D7D6},
      new int[] {(int)'9', 0x1D7D7},
      new int[]{0x1D6E2, 0x1D6A8}, // Alpha
      new int[]{0x1D6E3, 0x1D6A9}, // Beta
      new int[]{0x1D6E4, 0x1D6AA}, // Gamma
      new int[]{0x1D6E5, 0x1D6AB}, // Delta
      new int[]{0x1D6E6, 0x1D6AC}, // Epsilon
      new int[]{0x1D6E7, 0x1D6AD}, // Zeta
      new int[]{0x1D6E8, 0x1D6AE}, // Eta
      new int[]{0x1D6E9, 0x1D6AF}, // Theta
      new int[]{0x1D6EA, 0x1D6B0}, // Iota
      new int[]{0x1D6EB, 0x1D6B1}, // Kappa
      new int[]{0x1D6EC, 0x1D6B2}, // Lambda
      new int[]{0x1D6ED, 0x1D6B3}, // Mu
      new int[]{0x1D6EE, 0x1D6B4}, // Nu
      new int[]{0x1D6EF, 0x1D6B5}, // Xi
      new int[]{0x1D6F0, 0x1D6B6}, // Omicron
      new int[]{0x1D6F1, 0x1D6B7}, // Pi
      new int[]{0x1D6F2, 0x1D6B8}, // Rho
      new int[]{0x1D6F3, 0x1D6B9}, // Theta
      new int[]{0x1D6F4, 0x1D6BA}, // Sigma
      new int[]{0x1D6F5, 0x1D6BB}, // Tau
      new int[]{0x1D6F6, 0x1D6BC}, // Upsilon
      new int[]{0x1D6F7, 0x1D6BD}, // Phi
      new int[]{0x1D6F8, 0x1D6BE}, // Chi
      new int[]{0x1D6F9, 0x1D6BF}, // Psi
      new int[]{0x1D6FA, 0x1D6C0}, // Omega
      new int[]{0x1D6FB, 0x1D6C1}, // nabla
      new int[]{0x1D6FC, 0x1D6C2}, // alpha
      new int[]{0x1D6FD, 0x1D6C3}, // beta
      new int[]{0x1D6FE, 0x1D6C4}, // gamma
      new int[]{0x1D6FF, 0x1D6C5}, // delta
      new int[]{0x1D700, 0x1D6C6}, // varepsilon
      new int[]{0x1D701, 0x1D6C7}, // zeta
      new int[]{0x1D702, 0x1D6C8}, // eta
      new int[]{0x1D703, 0x1D6C9}, // theta
      new int[]{0x1D704, 0x1D6CA}, // iota
      new int[]{0x1D705, 0x1D6CB}, // kappa
      new int[]{0x1D706, 0x1D6CC}, // lambda
      new int[]{0x1D707, 0x1D6CD}, // mu
      new int[]{0x1D708, 0x1D6CE}, // nu
      new int[]{0x1D709, 0x1D6CF}, // xi
      new int[]{0x1D70A, 0x1D6D0}, // omicron
      new int[]{0x1D70B, 0x1D6D1}, // pi
      new int[]{0x1D70C, 0x1D6D2}, // rho
      new int[]{0x1D70D, 0x1D6D3}, // varsigma
      new int[]{0x1D70E, 0x1D6D4}, // sigma
      new int[]{0x1D70F, 0x1D6D5}, // tau
      new int[]{0x1D710, 0x1D6D6}, // upsilon
      new int[]{0x1D711, 0x1D6D7}, // varphi
      new int[]{0x1D712, 0x1D6D8}, // chi
      new int[]{0x1D713, 0x1D6D9}, // psi
      new int[]{0x1D714, 0x1D6DA}, // omega
      new int[]{0x1D715, 0x1D6DB}, // partial
      new int[]{0x1D716, 0x1D6DC}, // epsilon
      new int[]{0x1D717, 0x1D6DD}, // vartheta
      new int[]{0x1D718, 0x1D6DE}, // varkappa
      new int[]{0x1D719, 0x1D6DF}, // phi
      new int[]{0x1D71A, 0x1D6E0}, // varrho
      new int[]{0x1D71B, 0x1D6E1}, // varpi
   };

   public static final int[][] MATH_UP_SF =
   {
      new int[] {(int)'A', 0x1D5A0},
      new int[] {(int)'B', 0x1D5A1},
      new int[] {(int)'C', 0x1D5A2},
      new int[] {(int)'D', 0x1D5A3},
      new int[] {(int)'E', 0x1D5A4},
      new int[] {(int)'F', 0x1D5A5},
      new int[] {(int)'G', 0x1D5A6},
      new int[] {(int)'H', 0x1D5A7},
      new int[] {(int)'I', 0x1D5A8},
      new int[] {(int)'J', 0x1D5A9},
      new int[] {(int)'K', 0x1D5AA},
      new int[] {(int)'L', 0x1D5AB},
      new int[] {(int)'M', 0x1D5AC},
      new int[] {(int)'N', 0x1D5AD},
      new int[] {(int)'O', 0x1D5AE},
      new int[] {(int)'P', 0x1D5AF},
      new int[] {(int)'Q', 0x1D5B0},
      new int[] {(int)'R', 0x1D5B1},
      new int[] {(int)'S', 0x1D5B2},
      new int[] {(int)'T', 0x1D5B3},
      new int[] {(int)'U', 0x1D5B4},
      new int[] {(int)'V', 0x1D5B5},
      new int[] {(int)'W', 0x1D5B6},
      new int[] {(int)'X', 0x1D5B7},
      new int[] {(int)'Y', 0x1D5B8},
      new int[] {(int)'Z', 0x1D5B9},
      new int[] {(int)'a', 0x1D5BA},
      new int[] {(int)'b', 0x1D5BB},
      new int[] {(int)'c', 0x1D5BC},
      new int[] {(int)'d', 0x1D5BD},
      new int[] {(int)'e', 0x1D5BE},
      new int[] {(int)'f', 0x1D5BF},
      new int[] {(int)'g', 0x1D5C0},
      new int[] {(int)'h', 0x1D5C1},
      new int[] {(int)'i', 0x1D5C2},
      new int[] {(int)'j', 0x1D5C3},
      new int[] {(int)'k', 0x1D5C4},
      new int[] {(int)'l', 0x1D5C5},
      new int[] {(int)'m', 0x1D5C6},
      new int[] {(int)'n', 0x1D5C7},
      new int[] {(int)'o', 0x1D5C8},
      new int[] {(int)'p', 0x1D5C9},
      new int[] {(int)'q', 0x1D5CA},
      new int[] {(int)'r', 0x1D5CB},
      new int[] {(int)'s', 0x1D5CC},
      new int[] {(int)'t', 0x1D5CD},
      new int[] {(int)'u', 0x1D5CE},
      new int[] {(int)'v', 0x1D5CF},
      new int[] {(int)'w', 0x1D5D0},
      new int[] {(int)'x', 0x1D5D1},
      new int[] {(int)'y', 0x1D5D2},
      new int[] {(int)'z', 0x1D5D3},
      new int[] {(int)'0', 0x1D7E2},
      new int[] {(int)'1', 0x1D7E3},
      new int[] {(int)'2', 0x1D7E4},
      new int[] {(int)'3', 0x1D7E5},
      new int[] {(int)'4', 0x1D7E6},
      new int[] {(int)'5', 0x1D7E7},
      new int[] {(int)'6', 0x1D7E8},
      new int[] {(int)'7', 0x1D7E9},
      new int[] {(int)'8', 0x1D7EA},
      new int[] {(int)'9', 0x1D7EB}
   };

   public static final int[][] MATH_UP_BOLD_SF =
   {
      new int[]{(int)'A', 0x1D5D4},
      new int[]{(int)'B', 0x1D5D5},
      new int[]{(int)'C', 0x1D5D6},
      new int[]{(int)'D', 0x1D5D7},
      new int[]{(int)'E', 0x1D5D8},
      new int[]{(int)'F', 0x1D5D9},
      new int[]{(int)'G', 0x1D5DA},
      new int[]{(int)'H', 0x1D5DB},
      new int[]{(int)'I', 0x1D5DC},
      new int[]{(int)'J', 0x1D5DD},
      new int[]{(int)'K', 0x1D5DE},
      new int[]{(int)'L', 0x1D5DF},
      new int[]{(int)'M', 0x1D5E0},
      new int[]{(int)'N', 0x1D5E1},
      new int[]{(int)'O', 0x1D5E2},
      new int[]{(int)'P', 0x1D5E3},
      new int[]{(int)'Q', 0x1D5E4},
      new int[]{(int)'R', 0x1D5E5},
      new int[]{(int)'S', 0x1D5E6},
      new int[]{(int)'T', 0x1D5E7},
      new int[]{(int)'U', 0x1D5E8},
      new int[]{(int)'V', 0x1D5E9},
      new int[]{(int)'W', 0x1D5EA},
      new int[]{(int)'X', 0x1D5EB},
      new int[]{(int)'Y', 0x1D5EC},
      new int[]{(int)'Z', 0x1D5ED},
      new int[]{(int)'a', 0x1D5EE},
      new int[]{(int)'b', 0x1D5EF},
      new int[]{(int)'c', 0x1D5F0},
      new int[]{(int)'d', 0x1D5F1},
      new int[]{(int)'e', 0x1D5F2},
      new int[]{(int)'f', 0x1D5F3},
      new int[]{(int)'g', 0x1D5F4},
      new int[]{(int)'h', 0x1D5F5},
      new int[]{(int)'i', 0x1D5F6},
      new int[]{(int)'j', 0x1D5F7},
      new int[]{(int)'k', 0x1D5F8},
      new int[]{(int)'l', 0x1D5F9},
      new int[]{(int)'m', 0x1D5FA},
      new int[]{(int)'n', 0x1D5FB},
      new int[]{(int)'o', 0x1D5FC},
      new int[]{(int)'p', 0x1D5FD},
      new int[]{(int)'q', 0x1D5FE},
      new int[]{(int)'r', 0x1D5FF},
      new int[]{(int)'s', 0x1D600},
      new int[]{(int)'t', 0x1D601},
      new int[]{(int)'u', 0x1D602},
      new int[]{(int)'v', 0x1D603},
      new int[]{(int)'w', 0x1D604},
      new int[]{(int)'x', 0x1D605},
      new int[]{(int)'y', 0x1D606},
      new int[]{(int)'z', 0x1D607},
      new int[]{(int)'0', 0x1D7EC},
      new int[]{(int)'1', 0x1D7ED},
      new int[]{(int)'2', 0x1D7EE},
      new int[]{(int)'3', 0x1D7EF},
      new int[]{(int)'4', 0x1D7F0},
      new int[]{(int)'5', 0x1D7F1},
      new int[]{(int)'6', 0x1D7F2},
      new int[]{(int)'7', 0x1D7F3},
      new int[]{(int)'8', 0x1D7F4},
      new int[]{(int)'9', 0x1D7F5},
      new int[]{0x1D6E2, 0x1D756}, // Alpha
      new int[]{0x1D6E3, 0x1D757}, // Beta
      new int[]{0x1D6E4, 0x1D758}, // Gamma
      new int[]{0x1D6E5, 0x1D759}, // Delta
      new int[]{0x1D6E6, 0x1D75A}, // Epsilon
      new int[]{0x1D6E7, 0x1D75B}, // Zeta
      new int[]{0x1D6E8, 0x1D75C}, // Eta
      new int[]{0x1D6E9, 0x1D75D}, // Theta
      new int[]{0x1D6EA, 0x1D75E}, // Iota
      new int[]{0x1D6EB, 0x1D75F}, // Kappa
      new int[]{0x1D6EC, 0x1D760}, // Lambda
      new int[]{0x1D6ED, 0x1D761}, // Mu
      new int[]{0x1D6EE, 0x1D762}, // Nu
      new int[]{0x1D6EF, 0x1D763}, // Xi
      new int[]{0x1D6F0, 0x1D764}, // Omicron
      new int[]{0x1D6F1, 0x1D765}, // Pi
      new int[]{0x1D6F2, 0x1D766}, // Rho
      new int[]{0x1D6F3, 0x1D767}, // Theta
      new int[]{0x1D6F4, 0x1D768}, // Sigma
      new int[]{0x1D6F5, 0x1D769}, // Tau
      new int[]{0x1D6F6, 0x1D76A}, // Upsilon
      new int[]{0x1D6F7, 0x1D76B}, // Phi
      new int[]{0x1D6F8, 0x1D76C}, // Chi
      new int[]{0x1D6F9, 0x1D76D}, // Psi
      new int[]{0x1D6FA, 0x1D76E}, // Omega
      new int[]{0x1D6FB, 0x1D76F}, // nabla
      new int[]{0x1D6FC, 0x1D770}, // alpha
      new int[]{0x1D6FD, 0x1D771}, // beta
      new int[]{0x1D6FE, 0x1D772}, // gamma
      new int[]{0x1D6FF, 0x1D773}, // delta
      new int[]{0x1D700, 0x1D774}, // varepsilon
      new int[]{0x1D701, 0x1D775}, // zeta
      new int[]{0x1D702, 0x1D776}, // eta
      new int[]{0x1D703, 0x1D777}, // theta
      new int[]{0x1D704, 0x1D778}, // iota
      new int[]{0x1D705, 0x1D779}, // kappa
      new int[]{0x1D706, 0x1D77A}, // lambda
      new int[]{0x1D707, 0x1D77B}, // mu
      new int[]{0x1D708, 0x1D77C}, // nu
      new int[]{0x1D709, 0x1D77D}, // xi
      new int[]{0x1D70A, 0x1D77E}, // omicron
      new int[]{0x1D70B, 0x1D77F}, // pi
      new int[]{0x1D70C, 0x1D780}, // rho
      new int[]{0x1D70D, 0x1D781}, // varsigma
      new int[]{0x1D70E, 0x1D782}, // sigma
      new int[]{0x1D70F, 0x1D783}, // tau
      new int[]{0x1D710, 0x1D784}, // upsilon
      new int[]{0x1D711, 0x1D785}, // varphi
      new int[]{0x1D712, 0x1D786}, // chi
      new int[]{0x1D713, 0x1D787}, // psi
      new int[]{0x1D714, 0x1D788}, // omega
      new int[]{0x1D715, 0x1D789}, // partial
      new int[]{0x1D716, 0x1D78A}, // epsilon
      new int[]{0x1D717, 0x1D78B}, // vartheta
      new int[]{0x1D718, 0x1D78C}, // varkappa
      new int[]{0x1D719, 0x1D78D}, // phi
      new int[]{0x1D71A, 0x1D78E}, // varrho
      new int[]{0x1D71B, 0x1D78F}, // varpi
   };

   public static final int[][] MATH_IT_SF =
   {
      new int[]{(int)'A', 0x1D608},
      new int[]{(int)'B', 0x1D609},
      new int[]{(int)'C', 0x1D60A},
      new int[]{(int)'D', 0x1D60B},
      new int[]{(int)'E', 0x1D60C},
      new int[]{(int)'F', 0x1D60D},
      new int[]{(int)'G', 0x1D60E},
      new int[]{(int)'H', 0x1D60F},
      new int[]{(int)'I', 0x1D610},
      new int[]{(int)'J', 0x1D611},
      new int[]{(int)'K', 0x1D612},
      new int[]{(int)'L', 0x1D613},
      new int[]{(int)'M', 0x1D614},
      new int[]{(int)'N', 0x1D615},
      new int[]{(int)'O', 0x1D616},
      new int[]{(int)'P', 0x1D617},
      new int[]{(int)'Q', 0x1D618},
      new int[]{(int)'R', 0x1D619},
      new int[]{(int)'S', 0x1D61A},
      new int[]{(int)'T', 0x1D61B},
      new int[]{(int)'U', 0x1D61C},
      new int[]{(int)'V', 0x1D61D},
      new int[]{(int)'W', 0x1D61E},
      new int[]{(int)'X', 0x1D61F},
      new int[]{(int)'Y', 0x1D620},
      new int[]{(int)'Z', 0x1D621},
      new int[]{(int)'a', 0x1D622},
      new int[]{(int)'b', 0x1D623},
      new int[]{(int)'c', 0x1D624},
      new int[]{(int)'d', 0x1D625},
      new int[]{(int)'e', 0x1D626},
      new int[]{(int)'f', 0x1D627},
      new int[]{(int)'g', 0x1D628},
      new int[]{(int)'h', 0x1D629},
      new int[]{(int)'i', 0x1D62A},
      new int[]{(int)'j', 0x1D62B},
      new int[]{(int)'k', 0x1D62C},
      new int[]{(int)'l', 0x1D62D},
      new int[]{(int)'m', 0x1D62E},
      new int[]{(int)'n', 0x1D62F},
      new int[]{(int)'o', 0x1D630},
      new int[]{(int)'p', 0x1D631},
      new int[]{(int)'q', 0x1D632},
      new int[]{(int)'r', 0x1D633},
      new int[]{(int)'s', 0x1D634},
      new int[]{(int)'t', 0x1D635},
      new int[]{(int)'u', 0x1D636},
      new int[]{(int)'v', 0x1D637},
      new int[]{(int)'w', 0x1D638},
      new int[]{(int)'x', 0x1D639},
      new int[]{(int)'y', 0x1D63A},
      new int[]{(int)'z', 0x1D63B}
   };

   public static final int[][] MATH_IT_BOLD_SF =
   {
      new int[]{(int)'A', 0x1D63C},
      new int[]{(int)'B', 0x1D63D},
      new int[]{(int)'C', 0x1D63E},
      new int[]{(int)'D', 0x1D63F},
      new int[]{(int)'E', 0x1D640},
      new int[]{(int)'F', 0x1D641},
      new int[]{(int)'G', 0x1D642},
      new int[]{(int)'H', 0x1D643},
      new int[]{(int)'I', 0x1D644},
      new int[]{(int)'J', 0x1D645},
      new int[]{(int)'K', 0x1D646},
      new int[]{(int)'L', 0x1D647},
      new int[]{(int)'M', 0x1D648},
      new int[]{(int)'N', 0x1D649},
      new int[]{(int)'O', 0x1D64A},
      new int[]{(int)'P', 0x1D64B},
      new int[]{(int)'Q', 0x1D64C},
      new int[]{(int)'R', 0x1D64D},
      new int[]{(int)'S', 0x1D64E},
      new int[]{(int)'T', 0x1D64F},
      new int[]{(int)'U', 0x1D650},
      new int[]{(int)'V', 0x1D651},
      new int[]{(int)'W', 0x1D652},
      new int[]{(int)'X', 0x1D653},
      new int[]{(int)'Y', 0x1D654},
      new int[]{(int)'Z', 0x1D655},
      new int[]{(int)'a', 0x1D656},
      new int[]{(int)'b', 0x1D657},
      new int[]{(int)'c', 0x1D658},
      new int[]{(int)'d', 0x1D659},
      new int[]{(int)'e', 0x1D65A},
      new int[]{(int)'f', 0x1D65B},
      new int[]{(int)'g', 0x1D65C},
      new int[]{(int)'h', 0x1D65D},
      new int[]{(int)'i', 0x1D65E},
      new int[]{(int)'j', 0x1D65F},
      new int[]{(int)'k', 0x1D660},
      new int[]{(int)'l', 0x1D661},
      new int[]{(int)'m', 0x1D662},
      new int[]{(int)'n', 0x1D663},
      new int[]{(int)'o', 0x1D664},
      new int[]{(int)'p', 0x1D665},
      new int[]{(int)'q', 0x1D666},
      new int[]{(int)'r', 0x1D667},
      new int[]{(int)'s', 0x1D668},
      new int[]{(int)'t', 0x1D669},
      new int[]{(int)'u', 0x1D66A},
      new int[]{(int)'v', 0x1D66B},
      new int[]{(int)'w', 0x1D66C},
      new int[]{(int)'x', 0x1D66D},
      new int[]{(int)'y', 0x1D66E},
      new int[]{(int)'z', 0x1D66F},
      new int[]{0x1D6E2, 0x1D790}, // Alpha
      new int[]{0x1D6E3, 0x1D791}, // Beta
      new int[]{0x1D6E4, 0x1D792}, // Gamma
      new int[]{0x1D6E5, 0x1D793}, // Delta
      new int[]{0x1D6E6, 0x1D794}, // Epsilon
      new int[]{0x1D6E7, 0x1D795}, // Zeta
      new int[]{0x1D6E8, 0x1D796}, // Eta
      new int[]{0x1D6E9, 0x1D797}, // Theta
      new int[]{0x1D6EA, 0x1D798}, // Iota
      new int[]{0x1D6EB, 0x1D799}, // Kappa
      new int[]{0x1D6EC, 0x1D79A}, // Lambda
      new int[]{0x1D6ED, 0x1D79B}, // Mu
      new int[]{0x1D6EE, 0x1D79C}, // Nu
      new int[]{0x1D6EF, 0x1D79D}, // Xi
      new int[]{0x1D6F0, 0x1D79E}, // Omicron
      new int[]{0x1D6F1, 0x1D79F}, // Pi
      new int[]{0x1D6F2, 0x1D7A0}, // Rho
      new int[]{0x1D6F3, 0x1D7A1}, // Theta
      new int[]{0x1D6F4, 0x1D7A2}, // Sigma
      new int[]{0x1D6F5, 0x1D7A3}, // Tau
      new int[]{0x1D6F6, 0x1D7A4}, // Upsilon
      new int[]{0x1D6F7, 0x1D7A5}, // Phi
      new int[]{0x1D6F8, 0x1D7A6}, // Chi
      new int[]{0x1D6F9, 0x1D7A7}, // Psi
      new int[]{0x1D6FA, 0x1D7A8}, // Omega
      new int[]{0x1D6FB, 0x1D7A9}, // nabla
      new int[]{0x1D6FC, 0x1D7AA}, // alpha
      new int[]{0x1D6FD, 0x1D7AB}, // beta
      new int[]{0x1D6FE, 0x1D7AC}, // gamma
      new int[]{0x1D6FF, 0x1D7AD}, // delta
      new int[]{0x1D700, 0x1D7AE}, // varepsilon
      new int[]{0x1D701, 0x1D7AF}, // zeta
      new int[]{0x1D702, 0x1D7B0}, // eta
      new int[]{0x1D703, 0x1D7B1}, // theta
      new int[]{0x1D704, 0x1D7B2}, // iota
      new int[]{0x1D705, 0x1D7B3}, // kappa
      new int[]{0x1D706, 0x1D7B4}, // lambda
      new int[]{0x1D707, 0x1D7B5}, // mu
      new int[]{0x1D708, 0x1D7B6}, // nu
      new int[]{0x1D709, 0x1D7B7}, // xi
      new int[]{0x1D70A, 0x1D7B8}, // omicron
      new int[]{0x1D70B, 0x1D7B9}, // pi
      new int[]{0x1D70C, 0x1D7BA}, // rho
      new int[]{0x1D70D, 0x1D7BB}, // varsigma
      new int[]{0x1D70E, 0x1D7BC}, // sigma
      new int[]{0x1D70F, 0x1D7BD}, // tau
      new int[]{0x1D710, 0x1D7BE}, // upsilon
      new int[]{0x1D711, 0x1D7BF}, // varphi
      new int[]{0x1D712, 0x1D7C0}, // chi
      new int[]{0x1D713, 0x1D7C1}, // psi
      new int[]{0x1D714, 0x1D7C2}, // omega
      new int[]{0x1D715, 0x1D7C3}, // partial
      new int[]{0x1D716, 0x1D7C4}, // epsilon
      new int[]{0x1D717, 0x1D7C5}, // vartheta
      new int[]{0x1D718, 0x1D7C6}, // varkappa
      new int[]{0x1D719, 0x1D7C7}, // phi
      new int[]{0x1D71A, 0x1D7C8}, // varrho
      new int[]{0x1D71B, 0x1D7C9}, // varpi
   };

   public static final int[][] MATH_TT =
   {
      new int[]{(int)'A', 0x1D670},
      new int[]{(int)'B', 0x1D671},
      new int[]{(int)'C', 0x1D672},
      new int[]{(int)'D', 0x1D673},
      new int[]{(int)'E', 0x1D674},
      new int[]{(int)'F', 0x1D675},
      new int[]{(int)'G', 0x1D676},
      new int[]{(int)'H', 0x1D677},
      new int[]{(int)'I', 0x1D678},
      new int[]{(int)'J', 0x1D679},
      new int[]{(int)'K', 0x1D67A},
      new int[]{(int)'L', 0x1D67B},
      new int[]{(int)'M', 0x1D67C},
      new int[]{(int)'N', 0x1D67D},
      new int[]{(int)'O', 0x1D67E},
      new int[]{(int)'P', 0x1D67F},
      new int[]{(int)'Q', 0x1D680},
      new int[]{(int)'R', 0x1D681},
      new int[]{(int)'S', 0x1D682},
      new int[]{(int)'T', 0x1D683},
      new int[]{(int)'U', 0x1D684},
      new int[]{(int)'V', 0x1D685},
      new int[]{(int)'W', 0x1D686},
      new int[]{(int)'X', 0x1D687},
      new int[]{(int)'Y', 0x1D688},
      new int[]{(int)'Z', 0x1D689},
      new int[]{(int)'a', 0x1D68A},
      new int[]{(int)'b', 0x1D68B},
      new int[]{(int)'c', 0x1D68C},
      new int[]{(int)'d', 0x1D68D},
      new int[]{(int)'e', 0x1D68E},
      new int[]{(int)'f', 0x1D68F},
      new int[]{(int)'g', 0x1D690},
      new int[]{(int)'h', 0x1D691},
      new int[]{(int)'i', 0x1D692},
      new int[]{(int)'j', 0x1D693},
      new int[]{(int)'k', 0x1D694},
      new int[]{(int)'l', 0x1D695},
      new int[]{(int)'m', 0x1D696},
      new int[]{(int)'n', 0x1D697},
      new int[]{(int)'o', 0x1D698},
      new int[]{(int)'p', 0x1D699},
      new int[]{(int)'q', 0x1D69A},
      new int[]{(int)'r', 0x1D69B},
      new int[]{(int)'s', 0x1D69C},
      new int[]{(int)'t', 0x1D69D},
      new int[]{(int)'u', 0x1D69E},
      new int[]{(int)'v', 0x1D69F},
      new int[]{(int)'w', 0x1D6A0},
      new int[]{(int)'x', 0x1D6A1},
      new int[]{(int)'y', 0x1D6A2},
      new int[]{(int)'z', 0x1D6A3},
      new int[]{(int)'0', 0x1D7F6},
      new int[]{(int)'1', 0x1D7F7},
      new int[]{(int)'2', 0x1D7F8},
      new int[]{(int)'3', 0x1D7F9},
      new int[]{(int)'4', 0x1D7FA},
      new int[]{(int)'5', 0x1D7FB},
      new int[]{(int)'6', 0x1D7FC},
      new int[]{(int)'7', 0x1D7FD},
      new int[]{(int)'8', 0x1D7FE},
      new int[]{(int)'9', 0x1D7FF}
   };


   private int currentFontFamily   = INHERIT;
   private int currentFontShape    = INHERIT;
   private int currentFontWeight   = INHERIT;
   private int currentFontSize     = INHERIT;
   private int currentMathFont = INHERIT;

   private int currentParAlign = INHERIT;

   private int currentMode = INHERIT;

   private int currentCharMapMode = INHERIT;

   private int currentAlignMode = INHERIT;
   private int currentStartRowMode = INHERIT;
   private int currentStartColumnMode = INHERIT;

   private int currentAlignmentColumn = INHERIT;

   private TeXCellAlignList currentAlignmentList = null;

   private Color currentFgColor = null;
   private Color currentBgColor = null;

   private TeXParser parser;

   private Hashtable<String,Register> localRegisters 
     = new Hashtable<String,Register>();

   protected Hashtable<String,ControlSequence> csTable;

   protected Hashtable<Integer,ActiveChar> activeTable;
}

