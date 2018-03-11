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

/*
 * This is a very primitive implementation if siunitx. The main
 * purpose is to help bib2gls interpret 'name' fields containing 
 * scientific units when the 'sort' field is missing. This class is
 * not intended as a fully-functional implementation of a large and
 * complex package.
 */

public class SIunitxSty extends LaTeXSty
{
   public SIunitxSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
    throws IOException
   {
      super(options, "siunitx", listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      registerControlSequence(new Si(this));
   }

   public TeXObject parseUnit(TeXParser parser, TeXObject arg)
     throws IOException
   {
      if (arg instanceof ControlSequence)
      {
         TeXObject unit = getUnit((ControlSequence)arg);

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
         return new SiPower(this, "square", 2);
      }
      else if (name.equals("cubic"))
      {
         return new SiPower(this, "cubic", 3);
      }
      else if (name.equals("squared"))
      {
         return new SiPower(this, "squared", 2);
      }
      else if (name.equals("cubed"))
      {
         return new SiPower(this, "cubed", 3);
      }
      else
      {
         TeXObject unit = getUnit(cs);

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

   public TeXObject getUnit(ControlSequence cs)
   {
      String name = cs.getName();

      String val = getUnit(name, BASE_UNITS);

      if (val != null)
      {
         return new SIUnitCs(this, name, val);
      }

      val = getUnit(name, PREFIXES);

      if (val != null)
      {
         return new SIPrefixCs(this, name, val);
      }

      val = getUnit(name, BINARY_PREFIXES);

      if (val != null)
      {
         return new SIPrefixCs(this, name, val);
      }

      val = getUnit(name, DERIVED_UNITS);

      if (val != null)
      {
         return new SIUnitCs(this, name, val);
      }

      val = getUnit(name, NON_SI_UNITS);

      if (val != null)
      {
         return new SIUnitCs(this, name, val);
      }

      val = getUnit(name, ABBREVIATED_UNITS);

      if (val != null)
      {
         return new SIUnitCs(this, name, val);
      }

      return null;
   }

   public String getUnit(String name, String[][] array)
   {
      for (int i = 0; i < array.length; i++)
      {
         if (array[i][0].equals(name))
         {
            return array[i][1];
         }
      }

      return null;
   }

   private static final String[][] BASE_UNITS = new String[][]
   {
      new String[]{"ampere", "A"},
      new String[]{"candela", "cd"},
      new String[]{"kelvin", ""+(char)0x212A},
      new String[]{"kilogram", "kg"},
      new String[]{"gram", "g"},
      new String[]{"metre", "m"},
      new String[]{"meter", "m"},
      new String[]{"mole", "mol"},
      new String[]{"second", "s"},
   };

   private static final String[][] DERIVED_UNITS = new String[][]
   {
      new String[]{"becquerel", "Bq"},
      new String[]{"degreeCelsius", ""+(char)0x2103},
      new String[]{"coulomb", "C"},
      new String[]{"farad", "F"},
      new String[]{"gray", "Gy"},
      new String[]{"hertz", "Hz"},
      new String[]{"henry", "H"},
      new String[]{"joule", "J"},
      new String[]{"katal", "kat"},
      new String[]{"lumen", "lm"},
      new String[]{"lux", "lx"},
      new String[]{"newton", "N"},
      new String[]{"ohm", ""+(char)0x2126},
      new String[]{"pascal", "Pa"},
      new String[]{"radian", "rad"},
      new String[]{"siemens", "S"},
      new String[]{"sievert", "Sv"},
      new String[]{"steradian", "sr"},
      new String[]{"tesla", "T"},
      new String[]{"volt", "V"},
      new String[]{"watt", "W"},
      new String[]{"weber", "Wb"},
   };

   private static final String[][] NON_SI_UNITS = new String[][]
   {
      new String[]{"day", "d"},
      new String[]{"degree", ""+(char)+0x00B0},
      new String[]{"hectare", "ha"},
      new String[]{"hour", "h"},
      new String[]{"litre", "l"},
      new String[]{"liter", "L"},
      new String[]{"minute", "min"},
      new String[]{"arcminute", ""+(char)0x2032},
      new String[]{"arcsecond", ""+(char)0x2033},
      new String[]{"tonne", "t"},
      new String[]{"angstrom", ""+(char)0x212B},
      new String[]{"bar", "bar"},
      new String[]{"barn", "b"},
      new String[]{"bel", "B"},
      new String[]{"decibel", "dB"},
      new String[]{"knot", "kn"},
      new String[]{"mmHg", "mmHg"},
      new String[]{"nauticalmile", "M"},
      new String[]{"neper", "Np"},
      new String[]{"astronomicalunit", "ua"},
      new String[]{"atomicmassunit", "u"},
      new String[]{"dalton", "Da"},
      new String[]{"electronvolt", "eV"},
      new String[]{"bohr", 
        new String(new int[]{0x1D44E, 0x2080}, 0, 2)},
      new String[]{"clight",
        new String(new int[]{0x1D450, 0x2080}, 0, 2)},
      new String[]{"electronmass", 
        new String(new int[]{0x1D45A, 0x2091}, 0, 2)},
      new String[]{"elementarycharge", 
        new String(new int[]{0x1D452}, 0, 1)},
      new String[]{"hartree", 
        new String(new int[]{0x1D438, 0x2095}, 0, 2)},
      new String[]{"planckbar", ""+(char)0x210F},
   };

   private static final String[][] ABBREVIATED_UNITS = new String[][]
   {
      new String[]{"fg", "fg"},
      new String[]{"pg", "pg"},
      new String[]{"ng", "ng"},
      new String[]{"ug", new String(new int[]{0x00B5, 'g'}, 0, 2)},
      new String[]{"mg", "mg"},
      new String[]{"g", "g"},
      new String[]{"kg", "kg"},
      new String[]{"amu", "u"},
      new String[]{"pm", "pm"},
      new String[]{"nm", "nm"},
      new String[]{"um", new String(new int[]{0x00B5, 'm'}, 0, 2)},
      new String[]{"mm", "mm"},
      new String[]{"cm", "cm"},
      new String[]{"dm", "dm"},
      new String[]{"m", "m"},
      new String[]{"km", "km"},
      new String[]{"as", "as"},
      new String[]{"fs", "fs"},
      new String[]{"ps", "ps"},
      new String[]{"ns", "ns"},
      new String[]{"us", new String(new int[]{0x00B5, 's'}, 0, 2)},
      new String[]{"ms", "ms"},
      new String[]{"s", "s"},
      new String[]{"fmol", "fmol"},
      new String[]{"pmol", "pmol"},
      new String[]{"nmol", "nmol"},
      new String[]{"umol", new String(new int[]{0x00B5, 'm', 'o', 'l'}, 0, 4)},
      new String[]{"mmol", "mmol"},
      new String[]{"mol", "mol"},
      new String[]{"kmol", "kmol"},
      new String[]{"pA", "pA"},
      new String[]{"nA", "nA"},
      new String[]{"uA", new String(new int[]{0x00B5, 'A'}, 0, 2)},
      new String[]{"A", "A"},
      new String[]{"kA", "kA"},
      new String[]{"ul", new String(new int[]{0x00B5, 'l'}, 0, 2)},
      new String[]{"ml", "ml"},
      new String[]{"l", "l"},
      new String[]{"hl", "hl"},
      new String[]{"uL", new String(new int[]{0x00B5, 'L'}, 0, 2)},
      new String[]{"mL", "mL"},
      new String[]{"L", "L"},
      new String[]{"hL", "hL"},
      new String[]{"mHz", "mHz"},
      new String[]{"Hz", "Hz"},
      new String[]{"kHz", "kHz"},
      new String[]{"MHz", "MHz"},
      new String[]{"GHz", "GHz"},
      new String[]{"THz", "THz"},
      new String[]{"mN", "mN"},
      new String[]{"N", "N"},
      new String[]{"kN", "kN"},
      new String[]{"MN", "MN"},
      new String[]{"Pa", "Pa"},
      new String[]{"kPa", "kPa"},
      new String[]{"MPa", "MPa"},
      new String[]{"GPa", "GPa"},
      new String[]{"mohm", new String(new int[]{'m', 0x2126}, 0, 2)},
      new String[]{"kohm", new String(new int[]{'k', 0x2126}, 0, 2)},
      new String[]{"Mohm", new String(new int[]{'M', 0x2126}, 0, 2)},
      new String[]{"pV", "pV"},
      new String[]{"nV", "nV"},
      new String[]{"uV", new String(new int[]{0x00B5, 'V'}, 0, 2)},
      new String[]{"mV", "mV"},
      new String[]{"V", "V"},
      new String[]{"kV", "kV"},
      new String[]{"W", "W"},
      new String[]{"uW", new String(new int[]{0x00B5, 'W'}, 0, 2)},
      new String[]{"mW", "mW"},
      new String[]{"kW", "kW"},
      new String[]{"MW", "MW"},
      new String[]{"GW", "GW"},
      new String[]{"J", "J"},
      new String[]{"kJ", "kJ"},
      new String[]{"eV", "eV"},
      new String[]{"meV", "meV"},
      new String[]{"keV", "keV"},
      new String[]{"MeV", "MeV"},
      new String[]{"GeV", "GeV"},
      new String[]{"TeV", "TeV"},
      new String[]{"kWh", "kWh"},
      new String[]{"F", "F"},
      new String[]{"fF", "fF"},
      new String[]{"pF", "pF"},
      new String[]{"K", ""+(char)0x212A},
      new String[]{"dB", "dB"},
   };

   private static final String[][] PREFIXES = new String[][]
   {
      new String[]{"centi", "c"},
      new String[]{"kilo", "k"},
      new String[]{"milli", "m"},
      new String[]{"mega", "M"},
      new String[]{"giga", "G"},
      new String[]{"tera", "T"},
      new String[]{"yocto", "y"},
      new String[]{"zepto", "z"},
      new String[]{"atto", "a"},
      new String[]{"femto", "f"},
      new String[]{"pico", "p"},
      new String[]{"nano", "n"},
      new String[]{"micro", ""+(char)0x00B5},
      new String[]{"deci", "d"},
      new String[]{"deca", "da"},
      new String[]{"hecto", "h"},
      new String[]{"peta", "P"},
      new String[]{"exa", "E"},
      new String[]{"zetta", "Z"},
      new String[]{"yotta", "Y"},
   };

   private static final String[][] BINARY_PREFIXES = new String[][]
   {
      new String[]{"kibi", "Ki"},
      new String[]{"mebi", "Mi"},
      new String[]{"gibi", "Gi"},
      new String[]{"tebi", "Ti"},
      new String[]{"pebi", "Pi"},
      new String[]{"exbi", "Ei"},
      new String[]{"zebi", "Zi"},
      new String[]{"yobi", "Yi"},
   };

   public static final int UNIT_SEP = Space.MEDIUM_MATHEMATICAL_SPACE;
}
