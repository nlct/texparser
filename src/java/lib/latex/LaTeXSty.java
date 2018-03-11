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
package com.dickimawbooks.texparserlib.latex;

import java.util.HashMap;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.Undefined;

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

   public void parseFile() throws IOException
   {
      if (getFile().exists())
      {
         // This may not work if the package is too
         // complicated.

         byte orgAction = listener.getUndefinedAction();
         listener.setUndefinedAction(Undefined.ACTION_WARN);

         int orgCatCode = getParser().getCatCode('@');

         ControlSequence orgCurrName = getParser().getControlSequence(
           "@currname");
         ControlSequence orgCurrExt = getParser().getControlSequence(
           "@currext");

         getParser().putControlSequence(true, 
            new GenericCommand("@currname", null, 
              listener.createString(getName())));

         getParser().putControlSequence(true, 
            new GenericCommand("@currext", null, 
              listener.createString(getExtension())));

         try
         {
            getParser().setCatCode(true, '@', TeXParser.TYPE_LETTER);
            listener.input(this);
         }
         catch (IOException e)
         {
            listener.getTeXApp().error(e);
         }

         if (orgCurrName == null)
         {
            getParser().removeControlSequence(true, "@currname");
         }
         else
         {
            getParser().putControlSequence(true, orgCurrName);
         }

         if (orgCurrExt == null)
         {
            getParser().removeControlSequence(true, "@currext");
         }
         else
         {
            getParser().putControlSequence(true, orgCurrExt);
         }

         getParser().setCatCode(true, '@', orgCatCode);

         listener.setUndefinedAction(orgAction);
      }
   }

   public abstract void addDefinitions();

   protected void postOptions() throws IOException
   {
      addDefinitions();
   }

   public void registerControlSequence(ControlSequence cs)
   {
      listener.registerControlSequence(this, cs);
   }

   public DimenRegister registerNewLength(String name)
   {
      return listener.getParser().getSettings().newdimen(name);
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
