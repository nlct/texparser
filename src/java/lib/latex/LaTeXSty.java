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
package com.dickimawbooks.texparserlib.latex;

import java.util.HashMap;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.Undefined;
import com.dickimawbooks.texparserlib.generic.TeXParserSetUndefAction;

public abstract class LaTeXSty extends LaTeXFile
{
   public LaTeXSty(KeyValList options, String name,
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      super(listener.getParser(), options, name, "sty", loadParentOptions);
   }

   public LaTeXSty(KeyValList options, String name, String ext,
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      super(listener.getParser(), options, name, ext, loadParentOptions);
   }

   public void parseFile(TeXObjectList stack) throws IOException
   {
      if (getFile().exists())
      {
         // This may not work if the package is too
         // complicated.

         UndefAction orgAction = listener.getUndefinedAction();
         CategoryCode orgCatCode = getParser().getCategoryCode('@');

         ControlSequence orgCurrNameCs = getParser().getControlSequence(
           "@currname");
         ControlSequence orgCurrExtCs = getParser().getControlSequence(
           "@currext");

         String orgCurrName = null;
         String orgCurrExt = null;

         if (orgCurrName != null)
         {
            orgCurrName = getParser().expandToString(orgCurrNameCs, getParser());
         }

         if (orgCurrExt != null)
         {
            orgCurrExt = getParser().expandToString(orgCurrExtCs, getParser());
         }

         TeXObjectList substack = listener.createStack();

         substack.add(new TeXParserSetUndefAction(UndefAction.WARN));

         if (orgCatCode != CategoryCode.LETTER)
         {
            substack.add(listener.getControlSequence("makeatletter"));
         }

         substack.add(listener.getControlSequence("def"));
         substack.add(new TeXCsRef("@currname"));
         substack.add(listener.createGroup(getName()));

         substack.add(listener.getControlSequence("def"));
         substack.add(new TeXCsRef("@currext"));
         substack.add(listener.createGroup(getExtension()));
         substack.add(listener.getControlSequence("input"));
         substack.add(new TeXPathObject(this));

         if (orgCurrName != null)
         {
            substack.add(listener.getControlSequence("def"));
            substack.add(new TeXCsRef("@currname"));
            substack.add(listener.createGroup(orgCurrName));
         }

         if (orgCurrExt != null)
         {
            substack.add(listener.getControlSequence("def"));
            substack.add(new TeXCsRef("@currext"));
            substack.add(listener.createGroup(orgCurrExt));
         }

         if (orgCatCode != CategoryCode.LETTER)
         {
            substack.add(listener.getControlSequence("catcode"));
            substack.add(new UserNumber((int)'@'));
            substack.add(listener.getOther('='));
            substack.add(new UserNumber(orgCatCode.getId()));
         }

         substack.add(new TeXParserSetUndefAction(orgAction));

         TeXParserUtils.process(substack, getParser(), stack);

      }
   }

   public abstract void addDefinitions();

   @Override
   protected void postOptions(TeXObjectList stack) throws IOException
   {
      if (getParser().isDebugMode(TeXParser.DEBUG_CS))
      {
         getParser().logMessage("Adding definitions for "+getName());
      }

      addDefinitions();
   }

   public void registerControlSequence(ControlSequence cs)
   {
      listener.registerControlSequence(this, cs);
   }

   public void registerNewCounter(String name)
   {
      listener.newcounter(name);
   }

   public void registerNewCounter(String name, int value)
   {
      listener.newcounter(name, value);
   }

   public void registerNewCounter(String name, String parent)
   {
      listener.newcounter(name, parent);
   }

   public void registerNewCounter(String name, String parent, String format)
   {
      listener.newcounter(name, parent, format);
   }

   public void registerNewCounter(String name, String parent, String format, int value)
   {
      listener.newcounter(name, parent, format, value);
   }

   public CountRegister registerNewCountRegister(String name)
   {
      return listener.getParser().getScoping().newcount(name);
   }

   public CountRegister registerNewCountRegister(String name, int value)
   {
      return listener.getParser().getScoping().newcount(name, value);
   }

   public DimenRegister registerNewLength(String name)
   {
      return listener.getParser().getScoping().newdimen(name);
   }

   public DimenRegister registerNewLength(String name, 
     float value, TeXUnit unit)
   {
      DimenRegister reg = registerNewLength(name);

      try
      {
         reg.setValue(listener.getParser(), 
           new UserDimension(value, unit));
      }
      catch (TeXSyntaxException e)
      {// shouldn't happen
      }

      return reg;
   }

}
