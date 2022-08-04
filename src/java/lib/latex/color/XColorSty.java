/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.color;

import java.io.IOException;
import java.util.HashMap;
import java.awt.Color;
import java.awt.color.ColorSpace;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.TeXParserSetUndefAction;
import com.dickimawbooks.texparserlib.primitives.Undefined;
import com.dickimawbooks.texparserlib.latex.*;

public class XColorSty extends LaTeXSty
{
   public XColorSty(KeyValList options, LaTeXParserListener listener,
      boolean loadParentOptions, ColorSty colorSty)
    throws IOException
   {
      this(options, "xcolor", listener, loadParentOptions, colorSty);
   }

   public XColorSty(KeyValList options, String styname, 
     LaTeXParserListener listener, boolean loadParentOptions, ColorSty colorSty)
    throws IOException
   {
      super(options, styname, listener, loadParentOptions);
      this.colorSty = colorSty;
   }

   @Override
   public void addDefinitions()
   {
      registerControlSequence(new TextualContentCommand("colornameprefix", "XC@"));

      registerControlSequence(new PrepareColorSet(colorSty));
   }

   @Override
   public void processOption(String option, TeXObject value)
    throws IOException
   {
      if (option.equals("x11names"))
      {
         loadX11 = true;
      }
      else if (option.equals("svgnames"))
      {
         loadSvg = true;
      }
   }

   @Override
   protected void postOptions(TeXObjectList stack) throws IOException
   {
      super.postOptions(stack);

      if (loadX11 || loadSvg)
      {
         byte orgAction = listener.getUndefinedAction();
         int orgCatCode = getParser().getCatCode('@');

         TeXObjectList substack = getListener().createStack();

         substack.add(new TeXParserSetUndefAction(Undefined.ACTION_WARN));

         if (orgCatCode != TeXParser.TYPE_LETTER)
         {
            substack.add(listener.getControlSequence("makeatletter"));
         }

         if (loadX11)
         {
            substack.add(TeXParserActionObject.createInputAction(
              getParser(), "x11name.def")); 

            loadX11 = false;
         }

         if (loadSvg)
         {
            substack.add(TeXParserActionObject.createInputAction(
              getParser(), "svgname.def")); 

            loadX11 = false;
         }

         if (orgCatCode != TeXParser.TYPE_LETTER)
         {
            substack.add(listener.getControlSequence("catcode"));
            substack.add(new UserNumber((int)'@'));
            substack.add(listener.getOther('='));
            substack.add(new UserNumber(orgCatCode));
         }

         substack.add(new TeXParserSetUndefAction(orgAction));

         if (!substack.isEmpty())
         {
            if (getParser() == stack || stack == null)
            {
               getParser().push(substack, true);
            }
            else
            {
               substack.process(getParser(), stack);
            }
         }
      }

   }

   private ColorSty colorSty;
   private boolean loadX11 = false, loadSvg = false;
}
