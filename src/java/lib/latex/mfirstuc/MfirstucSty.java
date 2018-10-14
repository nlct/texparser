/*
    Copyright (C) 2018 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.mfirstuc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.NewIf;
import com.dickimawbooks.texparserlib.latex.*;

public class MfirstucSty extends LaTeXSty
{
   public MfirstucSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "mfirstuc", listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      NewIf.createConditional(true, getParser(), "ifMFUhyphen", false);

      registerControlSequence(new GlsMakeFirstUc());
      registerControlSequence(new MakeFirstUc());
      registerControlSequence(new MakeFirstUc("xmakefirstuc",
        MakeFirstUc.EXPANSION_ONCE));
      registerControlSequence(new MakeFirstUc("emakefirstuc",
        MakeFirstUc.EXPANSION_FULL));
      registerControlSequence(new CapitaliseWords(this));
      registerControlSequence(new CapitaliseWords(this, "xcapitalisewords",
        MakeFirstUc.EXPANSION_ONCE));
      registerControlSequence(new CapitaliseWords(this, "ecapitalisewords",
        MakeFirstUc.EXPANSION_FULL));
      registerControlSequence(new CapitaliseFmtWords(this));
      registerControlSequence(new MakeFirstUc("MFUcapword"));
      registerControlSequence(new MFUnocap(this));
      registerControlSequence(new MFUnocap(this, "gMFUnocap", true));
   }

   public void addException(String word)
   {
      addException(getListener().createString(word), false);
   }

   public void addException(TeXObject word, boolean global)
   {
      ControlSequence cs = getParser().getControlSequence("@mfu@nocaplist");

      TeXObjectList list;

      if (cs instanceof GenericCommand)
      {
         list = ((GenericCommand)cs).getDefinition();
      }
      else
      {
         list = new TeXObjectList();
      }

      list.add((TeXObject)word.clone());

      getParser().putControlSequence(!global, 
        new GenericCommand("@mfu@nocaplist", null, list));
   }

   public boolean isException(TeXObject word)
   {
      ControlSequence cs = getParser().getControlSequence("@mfu@nocaplist");

      TeXObjectList list;

      if (cs instanceof GenericCommand)
      {
         list = ((GenericCommand)cs).getDefinition();
      }
      else
      {
         list = new TeXObjectList();
      }

      return list.contains(word);
   }

}
