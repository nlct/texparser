/*
    Copyright (C) 2018-2022 Nicola L.C. Talbot
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
import com.dickimawbooks.texparserlib.primitives.Uppercase;
import com.dickimawbooks.texparserlib.latex.*;

public class MfirstucSty extends LaTeXSty
{
   public MfirstucSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "mfirstuc", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      NewIf.createConditional(true, getParser(), "ifMFUhyphen", false);

      registerControlSequence(new MFUsentencecase(this));
      registerControlSequence(new GlsMakeFirstUc());
      registerControlSequence(new GlsMakeFirstUc(
       "mfugrabfirstuc", GlsMakeFirstUc.GRABFIRST));
      registerControlSequence(new MakeFirstUc(this));
      registerControlSequence(new MakeFirstUc("xmakefirstuc",
        MakeFirstUc.EXPANSION_ONCE, this));
      registerControlSequence(new MakeFirstUc("emakefirstuc",
        MakeFirstUc.EXPANSION_FULL, this));
      registerControlSequence(new CapitaliseWords(this));
      registerControlSequence(new CapitaliseWords(this, "xcapitalisewords",
        MakeFirstUc.EXPANSION_ONCE));
      registerControlSequence(new CapitaliseWords(this, "ecapitalisewords",
        MakeFirstUc.EXPANSION_FULL));
      registerControlSequence(new CapitaliseFmtWords(this));
      registerControlSequence(new MakeFirstUc("MFUcapword", this));
      registerControlSequence(new MFUhyphencapword());
      registerControlSequence(new MFUnocap(this));
      registerControlSequence(new MFUnocap(this, "gMFUnocap", true));
      registerControlSequence(new MFUclear(this));
      registerControlSequence(new MFUskippunc());
      registerControlSequence(new MFUwordbreak());

      registerControlSequence(new MFUexcl(this));
      registerControlSequence(new MFUblocker(this));
      registerControlSequence(new MFUaddmap(this));

      addExclusion("MFUskippunc");

      // not used but implement in case it's used explicitly

      if (listener.isStyLoaded("textcase") || listener.isStyLoaded("glossaries"))
      {
         registerControlSequence(new GenericCommand(true,
            "mfirstucMakeUppercase", null,
             new TeXObject[] { new TeXCsRef("MakeTextUppercase") }));

         addExclusion("NoCaseChange");
      }
      else
      {
         registerControlSequence(new Uppercase("mfirstucMakeUppercase"));
      }
   }

   @Override
   public void processOption(String option, TeXObject value)
    throws IOException
   {
      if (option.equals("expanded"))
      {
         registerControlSequence(new GlsMakeFirstUc(GlsMakeFirstUc.EXPANDED));
      }
      else if (option.equals("unexpanded"))
      {
         registerControlSequence(new GlsMakeFirstUc(GlsMakeFirstUc.UNEXPANDED));
      }
      else if (option.equals("grabfirst"))
      {
         registerControlSequence(new GlsMakeFirstUc(GlsMakeFirstUc.GRABFIRST));
      }
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

   public void clearExceptions()
   {
      clearExceptions(false);
   }

   public void clearExceptions(boolean global)
   {
      getParser().putControlSequence(!global,
         new GenericCommand("@mfu@nocaplist"));
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

   /**
    * Tests if the given control sequence name has been marked as an
    * exclusion.
    * @param name control sequence name
    * @return true if the associated control sequence is an
    * exclusion
    */ 
   public boolean isExclusion(String name)
   {
      ControlSequence cs = getParser().getControlSequence(EXCLUSION_TL);

      if (cs instanceof GenericCommand)
      {
         TeXObjectList def = ((GenericCommand)cs).getDefinition();

         for (int i = 0; i < def.size(); i++)
         {
            TeXObject obj = def.get(i);

            if ((obj instanceof ControlSequence) 
                  && ((ControlSequence)obj).getName().equals(name))
            {
               return true;
            }
         }
      }

      return false;
   }

   public void addExclusion(String name)
   {
      ControlSequence cs = getParser().getControlSequence(EXCLUSION_TL);

      if (cs instanceof GenericCommand)
      {
         TeXObjectList def = ((GenericCommand)cs).getDefinition();
         def.add(new TeXCsRef(name));
      }
      else
      {
         cs = new GenericCommand(true, EXCLUSION_TL, null, new TeXCsRef(name));
         getParser().putControlSequence(true, cs);
      }
   }

   /**
    * Tests if the given control sequence name has been marked as a
    * blocker.
    * @param name control sequence name
    * @return true if the associated control sequence is a
    * blocker
    */ 
   public boolean isBlocker(String name)
   {
      ControlSequence cs = getParser().getControlSequence(BLOCKER_TL);

      if (cs instanceof GenericCommand)
      {
         TeXObjectList def = ((GenericCommand)cs).getDefinition();

         for (int i = 0; i < def.size(); i++)
         {
            TeXObject obj = def.get(i);

            if ((obj instanceof ControlSequence) 
                  && ((ControlSequence)obj).getName().equals(name))
            {
               return true;
            }
         }
      }

      return false;
   }

   public void addBlocker(String name)
   {
      ControlSequence cs = getParser().getControlSequence(BLOCKER_TL);

      if (cs instanceof GenericCommand)
      {
         TeXObjectList def = ((GenericCommand)cs).getDefinition();
         def.add(new TeXCsRef(name));
      }
      else
      {
         cs = new GenericCommand(true, BLOCKER_TL, null, new TeXCsRef(name));
         getParser().putControlSequence(true, cs);
      }
   }

   public TeXObject getMapping(String key)
   {
      ControlSequence cs = getParser().getControlSequence(MAPPINGS_PROP);

      if (cs instanceof GenericCommand)
      {
         TeXObjectList def = ((GenericCommand)cs).getDefinition();

         TeXObject firstElem = def.firstElement();

         if (firstElem instanceof KeyValList)
         {
            return ((KeyValList)firstElem).get(key);
         }
      }

      return null;
   }

   public void addMapping(String key, TeXObject value)
   {
      ControlSequence cs = getParser().getControlSequence(MAPPINGS_PROP);
      KeyValList mappings;

      if (cs instanceof GenericCommand)
      {
         TeXObjectList def = ((GenericCommand)cs).getDefinition();

         TeXObject firstElem = def.firstElement();

         if (firstElem instanceof KeyValList)
         {
            mappings = (KeyValList)firstElem;
         }
         else
         {
            getParser().debugMessage(TeXParser.DEBUG_STY_DATA, 
              "KeyValList expected as definition of "+cs);

            mappings = new KeyValList();
            cs = new GenericCommand(true, MAPPINGS_PROP, null, mappings);
            getParser().putControlSequence(true, cs);
         }
      }
      else
      {
         if (cs != null)
         {
            getParser().debugMessage(TeXParser.DEBUG_STY_DATA, 
              "KeyValList expected as definition of "+cs);
         }

         mappings = new KeyValList();
         cs = new GenericCommand(true, MAPPINGS_PROP, null, mappings);
         getParser().putControlSequence(true, cs);
      }

      mappings.put(key, value);
   }

   public static final String EXCLUSION_TL = "l_text_case_exclude_arg_tl";
   public static final String BLOCKER_TL = "l__mfirstuc_blocker_tl";
   public static final String MAPPINGS_PROP = "l__mfirstuc_mappings_prop";

}
