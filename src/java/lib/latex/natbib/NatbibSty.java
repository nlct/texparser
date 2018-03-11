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
package com.dickimawbooks.texparserlib.latex.natbib;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class NatbibSty extends LaTeXSty
{
   public NatbibSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "natbib", listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      registerControlSequence(new Citep(this));
      registerControlSequence(new Citet(this));

      if (citestyle == CITE_NUMBERS)
      {
         registerControlSequence(new Citep("cite", this));
      }
      else
      {
         registerControlSequence(new Citet("cite", this));
      }
   }

   public void processOption(String option, TeXObject value)
    throws IOException
   {
      if (option.equals("round"))
      {
         bracket = BRACKET_ROUND;
      }
      else if (option.equals("square"))
      {
         bracket = BRACKET_SQUARE;
      }
      else if (option.equals("curly"))
      {
         bracket = BRACKET_CURLY;
      }
      else if (option.equals("angle"))
      {
         bracket = BRACKET_ANGLE;
      }
      else if (option.equals("semicolon") || option.equals("colon"))
      {
         separator = SEP_SEMICOLON;
      }
      else if (option.equals("comma"))
      {
         separator = SEP_COMMA;
      }
      else if (option.equals("authoryear"))
      {
         citestyle = CITE_AUTHORYEAR;
      }
      else if (option.equals("numbers"))
      {
         citestyle = CITE_NUMBERS;
      }
      else if (option.equals("super"))
      {
         isSuper = true;
         citestyle = CITE_NUMBERS;
      }
   }

   public TeXObject getOpenBracket()
   {
      switch (bracket)
      {
         case BRACKET_ROUND: return getListener().getOther('(');
         case BRACKET_SQUARE: return getListener().getOther('[');
         case BRACKET_CURLY: return new TeXCsRef("textbraceleft");
         case BRACKET_ANGLE: return getListener().getOther('⟨');
      }

      return new TeXObjectList();
   }

   public TeXObject getCloseBracket()
   {
      switch (bracket)
      {
         case BRACKET_ROUND: return getListener().getOther(')');
         case BRACKET_SQUARE: return getListener().getOther(']');
         case BRACKET_CURLY: return new TeXCsRef("textbraceright");
         case BRACKET_ANGLE: return getListener().getOther('⟩');
      }

      return new TeXObjectList();
   }

   public TeXObject getSeparator()
   {
      switch (separator)
      {
         case SEP_SEMICOLON: return getListener().getOther(';');
         case SEP_COMMA: return getListener().getOther(',');
      }

      return new TeXObjectList();
   }

   public int getCiteStyle()
   {
      return citestyle;
   }

   public boolean isSuper()
   {
      return isSuper;
   }

   public static final int BRACKET_ROUND=0;
   public static final int BRACKET_SQUARE=1;
   public static final int BRACKET_CURLY=2;
   public static final int BRACKET_ANGLE=3;

   private int bracket=BRACKET_SQUARE;

   public static final int SEP_SEMICOLON=0;
   public static final int SEP_COMMA=1;

   private int separator = SEP_COMMA;

   public static final int CITE_AUTHORYEAR=0;
   public static final int CITE_NUMBERS=1;

   private int citestyle = CITE_AUTHORYEAR;

   private boolean isSuper = false;
}
