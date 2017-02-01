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
package com.dickimawbooks.texparserlib.latex.siunitx;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.primitives.Undefined;

public class SIunitxSty extends LaTeXSty
{
   public SIunitxSty(KeyValList options, LaTeXParserListener listener)
    throws IOException
   {
      super(options, "siunitx", listener);
   }

   public void addDefinitions()
   {
      registerControlSequence(new Si(this));
   }

   public void processOption(String option)
    throws IOException
   {
   }

   protected void preOptions()
     throws IOException
   {
   }

   public TeXObject parseUnit(TeXParser parser, TeXObject arg)
     throws IOException
   {
      if (arg instanceof ControlSequence)
      {
         TeXObject unit = getUnit(parser, (ControlSequence)arg);

         return unit == null ? arg : unit;
      }
      else if (arg instanceof TeXObjectList)
      {
         TeXObjectList list = new TeXObjectList();
         parseUnits(parser, (TeXObjectList)arg, list);
         return list;
      }

      return arg;
   }

   private void parseUnits(TeXParser parser, TeXObjectList argList,
     TeXObjectList list)
   throws IOException
   {
      for (int i = 0, n = argList.size(); i < n; i++)
      {
         TeXObject obj = argList.get(i);

         if (obj instanceof CharObject)
         {
            int code = ((CharObject)obj).getCharCode();

            if (code == '.' || code == '~')
            {
               list.add(createUnitSep(parser));
            }
            else
            {
               list.add(obj);
            }
         }
         else if (obj instanceof SbChar)
         {
            if (parser.isMathMode())
            {
               list.add(obj);

               if (i < n-1 && !isNumber(argList.get(i+1)))
               {
                  Group grp = createText(parser);
                  grp.add(argList.get(++i));
               }
            }
            else
            {
               list.add(new TeXCsRef("textsubscript"));
            }
         }
         else if (obj instanceof SpChar)
         {
            if (parser.isMathMode())
            {
               list.add(obj);

               if (i < n-1 && !isNumber(argList.get(i+1)))
               {
                  Group grp = createText(parser);
                  grp.add(argList.get(++i));
               }
            }
            else
            {
               list.add(new TeXCsRef("textsuperscript"));
            }
         }
         else if (obj instanceof ControlSequence)
         {
            list.add(getFormatting(parser, (ControlSequence) obj));
         }
         else
         {
            list.add(obj);
         }
      }
   }

   private TeXObject getFormatting(TeXParser parser, ControlSequence cs)
     throws IOException
   {
      String name = cs.getName();

      if (name.equals("per"))
      {
         return new SiPer(this);
      }
      else if (name.equals("square"))
      {
         return new SiPer(this, "square", 2);
      }
      else if (name.equals("cubic"))
      {
         return new SiPer(this, "cubic", 3);
      }
      else if (name.equals("squared"))
      {
         return new SiPower(this, "squared", 2);
      }
      else
      {
         TeXObject unit = getUnit(parser, cs);

         return unit == null ? cs : unit;
      }
   }

   public TeXObject createUnitSep(TeXParser parser) throws IOException
   {
      return parser.getListener().getOther(UNIT_SEP);
   }

   public static Group createText(TeXParser parser) throws IOException
   {
      Group grp = parser.getListener().createGroup();

      ControlSequence cs = parser.getControlSequence("text");

      if (cs instanceof Undefined)
      {
         grp.add(new TeXCsRef("mathrm"));
      }
      else
      {
         grp.add(cs);
      }

      return grp;
   }

   public static TeXObjectList createText(TeXParser parser, TeXObject arg)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      ControlSequence cs = parser.getControlSequence("text");

      if (cs instanceof Undefined)
      {
         list.add(new TeXCsRef("mathrm"));
      }
      else
      {
         list.add(cs);
      }

      list.add(arg);

      return list;
   }

   private boolean isNumber(TeXObject arg)
   {
      if (arg instanceof TeXNumber)
      {
         return true;
      }

      if (!(arg instanceof TeXObjectList))
      {
         if (arg instanceof CharObject
             && Character.isDigit(((CharObject)arg).getCharCode()))
         {
            return true;
         }
         else
         {
            return false;
         }
      }

      TeXObjectList argList = (TeXObjectList)arg;

      int n = argList.size();

      if (n == 0) return false;

      TeXObject obj = argList.get(0);

      if (obj instanceof CharObject)
      {
         int code = ((CharObject)obj).getCharCode();

         if (!Character.isDigit(code) && code != '+' && code != '-')
         {
            return false;
         }
      }

      for (int i = 1; i < n; i++)
      {
         if (obj instanceof CharObject)
         {
            if (!Character.isDigit(((CharObject)obj).getCharCode()))
            {
               return false;
            }
         }
         else
         {
            return false;
         }
      }

      return true;
   }

   public TeXObject getUnit(TeXParser parser, ControlSequence cs)
   {
      String name = cs.getName();

      for (int i = 0; i < UNITS.length; i++)
      {
         if (UNITS[i][0].equals(name))
         {
            return new SIUnitCs(this, name, UNITS[i][1]);
         }
      }

      for (int i = 0; i < PREFIXES.length; i++)
      {
         if (PREFIXES[i][0].equals(name))
         {
            return new SIPrefixCs(this, name, PREFIXES[i][1]);
         }
      }

      return null;
   }

   private static final String[][] UNITS = new String[][]
   {
      new String[]{"ampere", "A"},
      new String[]{"candela", "cd"},
      new String[]{"farad", "F"},
      new String[]{"gram", "g"},
      new String[]{"gray", "Gy"},
      new String[]{"kelvin", "K"},
      new String[]{"kilogram", "kg"},
      new String[]{"lux", "lx"},
      new String[]{"metre", "m"},
      new String[]{"mole", "mol"},
      new String[]{"second", "s"},
      new String[]{"volt", "V"},
   };

   private static final String[][] PREFIXES = new String[][]
   {
      new String[]{"centi", "c"},
      new String[]{"kilo", "k"},
      new String[]{"milli", "m"},
   };

   public static final int UNIT_SEP = Space.MEDIUM_MATHEMATICAL_SPACE;
}
