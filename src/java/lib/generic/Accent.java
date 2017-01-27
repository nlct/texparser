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
import java.util.Hashtable;

import com.dickimawbooks.texparserlib.*;

public class Accent extends ControlSequence
{
   public Accent(String name)
   {
      super(name);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject object = parser.popNextArg();
      TeXObjectList remaining = null;

      String accent = name;

      if (name.equals("a"))
      {
         if (object instanceof Group)
         {
            object = ((Group)object).toList();
         }

         if (object instanceof Expandable)
         {
            remaining = ((Expandable)object).expandfully(parser);

            if (remaining.size() == 0)
            {
               object = parser.popStack();
            }
            else
            {
               object = remaining.pop();
            }
         }

         if (object instanceof CharObject)
         {
            accent = ""+(char)((CharObject)object).getCharCode();
         }
         else
         {
            throw new TeXSyntaxException(parser,
              TeXSyntaxException.ERROR_INVALID_ACCENT, 
              object.toString(parser));
         }

         if (remaining != null && remaining.size() > 0)
         {
            object = remaining.pop();
         }
         else
         {
            object = parser.popStack();
         }
      }

      if (object instanceof Group)
      {
         if (((Group)object).size() == 0)
         {
            parser.getListener().getWriteable().write(getText(accent, -1));

            return;
         }
         else
         {
            remaining = ((Group)object).toList();
            object = remaining.pop();
         }
      }

      if (object instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)object).expandfully(parser, remaining);

         if (expanded != null)
         {
            if (remaining == null)
            {
               remaining = expanded;
            }
            else
            {
               remaining.add(0, expanded);
            }
         }

         object = remaining.pop();
      }

      if (object instanceof CharObject)
      {
         int charCode = ((CharObject)object).getCharCode();

         parser.getListener().getWriteable().write(getText(accent, charCode));
      }
      else
      {
         object.process(parser, remaining);
      }
      
      if (remaining != null && remaining.size() > 0)
      {
         parser.push(remaining);
      }
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      if (stack == null || stack.size() == 0 || stack == parser)
      {
         process(parser);
         return;
      }

      TeXObject object = stack.popArg(parser);

      TeXObjectList remaining = null;

      String accent = name;

      if (name.equals("a"))
      {
         if (object instanceof Group)
         {
            object = ((Group)object).toList();
         }

         if (object instanceof Expandable)
         {
            remaining = ((Expandable)object).expandfully(parser, stack);

            if (remaining == null || remaining.size() == 0)
            {
               if (stack.size() == 0)
               {
                  object = parser.popNextArg();
               }
               else
               {
                  object = stack.popArg(parser);
               }
            }
            else
            {
               object = remaining.popArg(parser);
            }
         }

         if (object instanceof CharObject)
         {
            accent = ""+(char)((CharObject)object).getCharCode();
         }
         else
         {
            throw new TeXSyntaxException(parser,
              TeXSyntaxException.ERROR_INVALID_ACCENT,
              object.toString(parser));
         }

         if (remaining != null && remaining.size() > 0)
         {
            object = remaining.popArg(parser);
         }
         else if (stack.size() > 0)
         {
            object = stack.popArg(parser);
         }
         else
         {
            object = parser.popNextArg();
         }
      }

      if (object instanceof Group)
      {
         if (((Group)object).size() == 0)
         {
            parser.getListener().getWriteable().write(getText(accent, -1));

            return;
         }
         else
         {
            remaining = ((Group)object).toList();

            if (remaining.size() == 0)
            {
               object = stack.popStack(parser);
            }
            else
            {
               object = remaining.popStack(parser);
            }
         }
      }

      if (object instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (remaining == null || remaining.size() == 0)
         {
            expanded = ((Expandable)object).expandfully(parser, stack);
         }
         else
         {
            expanded = ((Expandable)object).expandfully(parser, remaining);
         }

         if (expanded != null)
         {
            if (remaining == null)
            {
               remaining = expanded;
            }
            else
            {
               remaining.add(0, expanded);
            }
         }

         if (remaining.size() == 0)
         {
            object = stack.popStack(parser);
         }
         else
         {
            object = remaining.popStack(parser);
         }
      }

      if (object instanceof CharObject)
      {
         int charCode = ((CharObject)object).getCharCode();

         parser.getListener().getWriteable().write(getText(accent, charCode));
      }
      else
      {
         if (remaining != null && remaining.size() > 0)
         {
            stack.push(remaining);
         }

         object.process(parser, stack);

         throw new TeXSyntaxException(
             parser,
             TeXSyntaxException.ERROR_INVALID_ACCENT,
             object.toString(parser));
      }
      
      if (remaining != null && remaining.size() > 0)
      {
         stack.push(remaining);
      }
   }

   public static void addCommands(TeXParser parser)
   {
      parser.putControlSequence(new Accent("'"));
      parser.putControlSequence(new Accent("`"));
      parser.putControlSequence(new Accent("\""));
      parser.putControlSequence(new Accent("u"));
      parser.putControlSequence(new Accent("c"));
      parser.putControlSequence(new Accent("~"));
      parser.putControlSequence(new Accent("^"));
      parser.putControlSequence(new Accent("r"));
      parser.putControlSequence(new Accent("b"));
      parser.putControlSequence(new Accent("d"));
      parser.putControlSequence(new Accent("="));
      parser.putControlSequence(new Accent("."));
      parser.putControlSequence(new Accent("v"));
      parser.putControlSequence(new Accent("H"));
      parser.putControlSequence(new Accent("k"));
   }

   public static boolean isAccentCommand(String csName)
   {
      if (csName.length() != 1)
      {
         return false;
      }

      char c = csName.charAt(0);

      return c == '`' || c == '\'' || c == '"'
          || c == 'u' || c == 'c' || c == '~'
          || c == '^' || c == 'r' || c == 'b'
          || c == 'd' || c == '=' || c == '.'
          || c == 'v' || c == 'H' || c == 'k';
   }

   private String getText(String accent, int codePoint)
      throws IOException
   {
      if (accent.equals("'"))
      {
         return getAccent(codePoint, ACUTE_ACCENTS, 0x0301);
      }

      if (accent.equals("`"))
      {
         return getAccent(codePoint, GRAVE_ACCENTS, 0x0300);
      }

      if (accent.equals("\""))
      {
         return getAccent(codePoint, UMLAUT_ACCENTS, 0x0308);
      }

      if (accent.equals("u"))
      {
         return getAccent(codePoint, BREVE_ACCENTS, 0x0306);
      }

      if (accent.equals("c"))
      {
         return getAccent(codePoint, CEDILLA_ACCENTS, 0x0327);
      }

      if (accent.equals("~"))
      {
         return getAccent(codePoint, TILDE_ACCENTS, 0x0303);
      }

      if (accent.equals("^"))
      {
         return getAccent(codePoint, CIRCUM_ACCENTS, 0x0302);
      }

      if (accent.equals("r"))
      {
         return getAccent(codePoint, RING_ACCENTS, 0x030A);
      }

      if (accent.equals("b"))
      {
         return getAccent(codePoint, UNDERBAR_ACCENTS, 0x0320);
      }

      if (accent.equals("d"))
      {
         return getAccent(codePoint, DOT_UNDER_ACCENTS, 0x0323);
      }

      if (accent.equals("="))
      {
         return getAccent(codePoint, MACRON_ACCENTS, 0x0304);
      }

      if (accent.equals("."))
      {
         return getAccent(codePoint, DOT_ACCENTS, 0x0307);
      }

      if (accent.equals("v"))
      {
         return getAccent(codePoint, CARON_ACCENTS, 0x030C);
      }

      if (accent.equals("H"))
      {
         return getAccent(codePoint, DOUBLE_ACUTE_ACCENTS, 0x030B);
      }

      if (accent.equals("k"))
      {
         return getAccent(codePoint, OGONEK_ACCENTS, 0x02DB);
      }

      return ""+(char)codePoint;
   }

   private String getAccent(int codePoint, int[][] array)
      throws IOException
   {
      return getAccent(codePoint, array, -1);
   }

   private String getAccent(int codePoint, int[][] array, int combiningCode)
      throws IOException
   {
      StringBuilder builder = new StringBuilder(2);

      if (codePoint == -1)
      {
         builder.append(' ');

         if (combiningCode != -1)
         {
            builder.appendCodePoint(combiningCode);
         }

         return builder.toString();
      }

      for (int i = 0; i < array.length; i++)
      {
         if (codePoint == array[i][0])
         {
            builder.appendCodePoint(array[i][1]);
            return builder.toString();
         }
      }

      builder.appendCodePoint(codePoint);

      if (combiningCode != -1)
      {
         builder.appendCodePoint(combiningCode);
      }

      return builder.toString();
   }

   public Object clone()
   {
      return new Accent(getName());
   }

   public static final int[][] ACUTE_ACCENTS =
   {
      new int[] {(int)'A', 0x00C1},
      new int[] {(int)'a', 0x00E1},
      new int[] {(int)'E', 0x00C9},
      new int[] {(int)'e', 0x00E9},
      new int[] {(int)'I', 0x00CD},
      new int[] {(int)'i', 0x00ED},
      new int[] {(int)'O', 0x00D3},
      new int[] {(int)'o', 0x00F3},
      new int[] {(int)'U', 0x00DA},
      new int[] {(int)'u', 0x00FA},
      new int[] {(int)'y', 0x00FD},
      new int[] {(int)'C', 0x0106},
      new int[] {(int)'c', 0x0107},
      new int[] {(int)'L', 0x0139},
      new int[] {(int)'l', 0x013A},
      new int[] {(int)'N', 0x0143},
      new int[] {(int)'n', 0x0144},
      new int[] {(int)'R', 0x0154},
      new int[] {(int)'r', 0x0155},
      new int[] {(int)'S', 0x015A},
      new int[] {(int)'s', 0x015B},
      new int[] {(int)'Z', 0x0179},
      new int[] {(int)'z', 0x017A},
   };

   public static final int[][] GRAVE_ACCENTS =
   {
      new int[] {(int)'A', 0x00C0},
      new int[] {(int)'a', 0x00E0},
      new int[] {(int)'E', 0x00C8},
      new int[] {(int)'e', 0x00E8},
      new int[] {(int)'I', 0x00CC},
      new int[] {(int)'i', 0x00EC},
      new int[] {(int)'O', 0x00D2},
      new int[] {(int)'o', 0x00F2},
      new int[] {(int)'U', 0x00D9},
      new int[] {(int)'u', 0x00F9},
      new int[] {(int)'y', 0x00FD}
   };

   public static final int[][] CIRCUM_ACCENTS =
   {
      new int[] {(int)'A', 0x00C2},
      new int[] {(int)'a', 0x00E2},
      new int[] {(int)'E', 0x00CA},
      new int[] {(int)'e', 0x00EA},
      new int[] {(int)'I', 0x00CE},
      new int[] {(int)'i', 0x00EE},
      new int[] {(int)'U', 0x00DB},
      new int[] {(int)'u', 0x00FB},
      new int[] {(int)'C', 0x0108},
      new int[] {(int)'c', 0x0109},
      new int[] {(int)'G', 0x011C},
      new int[] {(int)'g', 0x011D},
      new int[] {(int)'H', 0x0124},
      new int[] {(int)'h', 0x0125},
      new int[] {(int)'J', 0x0134},
      new int[] {(int)'j', 0x0135},
      new int[] {(int)'S', 0x015C},
      new int[] {(int)'s', 0x015D},
      new int[] {(int)'W', 0x0174},
      new int[] {(int)'w', 0x0175},
      new int[] {(int)'Y', 0x0176},
      new int[] {(int)'y', 0x0177},
   };

   public static final int[][] TILDE_ACCENTS =
   {
      new int[] {(int)'A', 0x00C3},
      new int[] {(int)'a', 0x00E3},
      new int[] {(int)'O', 0x00D5},
      new int[] {(int)'o', 0x00F5},
      new int[] {(int)'N', 0x00D1},
      new int[] {(int)'n', 0x00F1},
      new int[] {(int)'I', 0x0128},
      new int[] {(int)'i', 0x0129},
   };

   public static final int[][] UMLAUT_ACCENTS =
   {
      new int[] {(int)'A', 0x00C4},
      new int[] {(int)'a', 0x00E4},
      new int[] {(int)'E', 0x00CB},
      new int[] {(int)'e', 0x00EB},
      new int[] {(int)'I', 0x00CF},
      new int[] {(int)'i', 0x00EF},
      new int[] {(int)'O', 0x00D6},
      new int[] {(int)'o', 0x00F6},
      new int[] {(int)'U', 0x00DC},
      new int[] {(int)'u', 0x00FC},
      new int[] {(int)'y', 0x00FF},
      new int[] {(int)'Y', 0x0178}
   };

   public static final int[][] CEDILLA_ACCENTS =
   {
      new int[] {(int)'C', 0x00C7},
      new int[] {(int)'c', 0x00E7}
   };

   public static final int[][] RING_ACCENTS =
   {
      new int[] {(int)'A', 0x00C5},
      new int[] {(int)'a', 0x00E5},
      new int[] {(int)'U', 0x016E},
      new int[] {(int)'u', 0x016F}
   };

   public static final int[][] MACRON_ACCENTS =
   {
      new int[] {(int)'A', 0x0100},
      new int[] {(int)'a', 0x0101},
      new int[] {(int)'E', 0x0112},
      new int[] {(int)'a', 0x0113},
      new int[] {(int)'I', 0x012A},
      new int[] {(int)'i', 0x012B},
      new int[] {(int)'O', 0x014C},
      new int[] {(int)'o', 0x014D},
      new int[] {(int)'U', 0x016A},
      new int[] {(int)'u', 0x016B},
   };

   public static final int[][] BREVE_ACCENTS =
   {
      new int[] {(int)'A', 0x0102},
      new int[] {(int)'a', 0x0103},
      new int[] {(int)'E', 0x0114},
      new int[] {(int)'e', 0x0115},
      new int[] {(int)'G', 0x011E},
      new int[] {(int)'g', 0x011F},
      new int[] {(int)'I', 0x012C},
      new int[] {(int)'i', 0x012D},
      new int[] {(int)'O', 0x014E},
      new int[] {(int)'o', 0x014F},
      new int[] {(int)'U', 0x016C},
      new int[] {(int)'u', 0x016D},
   };

   public static final int[][] DOT_ACCENTS =
   {
      new int[] {(int)'C', 0x010A},
      new int[] {(int)'c', 0x010B},
      new int[] {(int)'E', 0x0116},
      new int[] {(int)'e', 0x0117},
      new int[] {(int)'G', 0x0120},
      new int[] {(int)'g', 0x0121},
      new int[] {(int)'I', 0x0130},
      new int[] {(int)'Z', 0x017B},
      new int[] {(int)'z', 0x017C},
      new int[] {(int)'B', 0x1E02},
      new int[] {(int)'B', 0x1E03},
      new int[] {(int)'D', 0x1E0A},
      new int[] {(int)'d', 0x1E0B},
      new int[] {(int)'F', 0x1E1E},
      new int[] {(int)'f', 0x1E1F},
      new int[] {(int)'H', 0x1E22},
      new int[] {(int)'h', 0x1E23},
      new int[] {(int)'N', 0x1E44},
      new int[] {(int)'n', 0x1E45},
   };

   public static final int[][] CARON_ACCENTS =
   {
      new int[] {(int)'C', 0x010C},
      new int[] {(int)'c', 0x010D},
      new int[] {(int)'D', 0x010E},
      new int[] {(int)'E', 0x011A},
      new int[] {(int)'e', 0x011B},
      new int[] {(int)'N', 0x0147},
      new int[] {(int)'n', 0x0148},
      new int[] {(int)'R', 0x0158},
      new int[] {(int)'r', 0x0159},
      new int[] {(int)'S', 0x0160},
      new int[] {(int)'s', 0x0161},
      new int[] {(int)'T', 0x0164},
      new int[] {(int)'Z', 0x017D},
      new int[] {(int)'z', 0x017E},
   };

   public static final int[][] DOT_UNDER_ACCENTS =
   {
      new int[] {(int)'B', 0x1E04},
      new int[] {(int)'b', 0x1E05},
      new int[] {(int)'D', 0x1E0C},
      new int[] {(int)'d', 0x1E0D},
      new int[] {(int)'H', 0x1E24},
      new int[] {(int)'h', 0x1E25},
      new int[] {(int)'K', 0x1E32},
      new int[] {(int)'k', 0x1E33},
      new int[] {(int)'L', 0x1E36},
      new int[] {(int)'l', 0x1E37},
      new int[] {(int)'M', 0x1E42},
      new int[] {(int)'m', 0x1E43},
      new int[] {(int)'N', 0x1E46},
      new int[] {(int)'n', 0x1E47},
      new int[] {(int)'R', 0x1E5A},
      new int[] {(int)'r', 0x1E5B},
      new int[] {(int)'S', 0x1E62},
      new int[] {(int)'s', 0x1E63},
      new int[] {(int)'T', 0x1E6C},
      new int[] {(int)'t', 0x1E6D},
      new int[] {(int)'V', 0x1E7E},
      new int[] {(int)'v', 0x1E7F},
      new int[] {(int)'W', 0x1E88},
      new int[] {(int)'w', 0x1E89},
      new int[] {(int)'Z', 0x1E92},
      new int[] {(int)'z', 0x1E93},
      new int[] {(int)'A', 0x1EA0},
      new int[] {(int)'a', 0x1EA1},
   };

   public static final int[][] UNDERBAR_ACCENTS =
   {
      new int[] {(int)'B', 0x1E06},
      new int[] {(int)'b', 0x1E07},
      new int[] {(int)'D', 0x1E0E},
      new int[] {(int)'d', 0x1E0F},
      new int[] {(int)'K', 0x1E34},
      new int[] {(int)'k', 0x1E35},
      new int[] {(int)'L', 0x1E3A},
      new int[] {(int)'l', 0x1E3B},
      new int[] {(int)'N', 0x1E48},
      new int[] {(int)'n', 0x1E49},
      new int[] {(int)'R', 0x1E5E},
      new int[] {(int)'r', 0x1E5F},
      new int[] {(int)'T', 0x1E6E},
      new int[] {(int)'t', 0x1E6F},
      new int[] {(int)'Z', 0x1E94},
      new int[] {(int)'z', 0x1E95},
      new int[] {(int)'h', 0x1E96},
   };

   public static final int[][] DOUBLE_ACUTE_ACCENTS =
   {
      new int[] {(int)'O', 0x0150},
      new int[] {(int)'o', 0x0151},
      new int[] {(int)'U', 0x0170},
      new int[] {(int)'u', 0x0171},
   };

   public static final int[][] OGONEK_ACCENTS =
   {
      new int[] {(int)'O', 0x01EA},
      new int[] {(int)'o', 0x01EB},
      new int[] {(int)'U', 0x0172},
      new int[] {(int)'u', 0x0173},
      new int[] {(int)'i', 0x012F},
      new int[] {(int)'I', 0x012E},
      new int[] {(int)'e', 0x0119},
      new int[] {(int)'E', 0x0118},
      new int[] {(int)'a', 0x0105},
      new int[] {(int)'A', 0x0104},
   };

}
