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
package com.dickimawbooks.texparserlib.latex.pifont;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Ding extends ControlSequence
{
   public Ding()
   {
      this("ding");
   }

   public Ding(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Ding(getName());
   }

   protected void ding(TeXObject arg, TeXParser parser)
     throws IOException
   {
      String str = arg.toString(parser);

      try
      {
         int n = Integer.parseInt(str);

         for (int i = 0; i < DING.length; i++)
         {
            if (DING[i][0] == n)
            {
               parser.getListener().getWriteable().writeCodePoint(DING[i][1]);
               return;
            }
         }

         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_BAD_PARAM, str);
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
      }
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser, stack);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      ding(arg, parser);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject arg = parser.popNextArg();

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      ding(arg, parser);
   }

   public static final int[][] DING = new int[][]
   {
      new int[] {32, 0x0020}, //32 space?
      new int[] {33, 0x2701}, //33 upper blade scissors
      new int[] {34, 0x2702}, //34 black scissors
      new int[] {35, 0x2703}, //35 lower blade scissors
      new int[] {36, 0x2704}, //36 white scissors
      new int[] {37, 0x260E}, //37 black telephone
      new int[] {38, 0x2706}, //38 telephone location
      new int[] {39, 0x2707}, //39 tape drive
      new int[] {40, 0x2708}, //40 aeroplane
      new int[] {41, 0x2709}, //41 envelope
      new int[] {42, 0x261B}, //42 black right pointing index
      new int[] {43, 0x261E}, //43 white right pointing index
      new int[] {44, 0x270C}, //44 victory hand
      new int[] {45, 0x270D}, //45 writing hand
      new int[] {46, 0x270E}, //46 lower right pencil
      new int[] {47, 0x270F}, //47 pencil
      new int[] {48, 0x2710}, //48 upper right pencil
      new int[] {49, 0x2711}, //49 white nib
      new int[] {50, 0x2712}, //50 black nib
      new int[] {51, 0x2713}, //51 check mark
      new int[] {52, 0x2714}, //52 heavy check mark
      new int[] {53, 0x2715}, //53 multiplication X
      new int[] {54, 0x2716}, //54 heavy multiplication X
      new int[] {55, 0x2717}, //55 ballot X
      new int[] {56, 0x2718}, //56 heavy ballot X
      new int[] {57, 0x2719}, //57 outlined Greek cross
      new int[] {58, 0x271A}, //58 heavy Greek cross
      new int[] {59, 0x271B}, //59 open centre cross
      new int[] {60, 0x271C}, //60 heavy open centre cross
      new int[] {61, 0x271D}, //61 Latin cross
      new int[] {62, 0x271E}, //62 shadowed Latin cross
      new int[] {63, 0x271F}, //63 outlined Latin cross
      new int[] {64, 0x2720}, //64 Maltese cross
      new int[] {65, 0x2721}, //65 Star of David
      new int[] {66, 0x2722}, //66 four teardrop-spoked asterisk
      new int[] {67, 0x2723}, //67 four balloon-spoked asterisk
      new int[] {68, 0x2724}, //68 heavy four balloon-spoked asterisk
      new int[] {69, 0x2725}, //69 four club-spoked asterisk
      new int[] {70, 0x2726}, //70 black four pointed star
      new int[] {71, 0x2727}, //71 white four pointed star
      new int[] {72, 0x2605}, //72 black star
      new int[] {73, 0x2729}, //73 stress outlined white star
      new int[] {74, 0x272A}, //74 circled white star
      new int[] {75, 0x272B}, //75 open centre black star
      new int[] {76, 0x272C}, //76 black centre white star
      new int[] {77, 0x272D}, //77 outlined black star
      new int[] {78, 0x272E}, //78 heavy outlined black star
      new int[] {79, 0x272F}, //79 pinwheel star
      new int[] {80, 0x2730}, //80 shadowed white star
      new int[] {81, 0x2731}, //81 heavy asterisk
      new int[] {82, 0x2732}, //82 open centre asterisk
      new int[] {83, 0x2733}, //83 eight spoked asterisk
      new int[] {84, 0x2734}, //84 eight pointed black star
      new int[] {85, 0x2735}, //85 eight pointed pinwheel star
      new int[] {86, 0x2736}, //86 six pointed black star
      new int[] {87, 0x2737}, //87 eight pointed rectilinear black star
      new int[] {88, 0x2738}, //88 heavy eight pointed rectilinear black star
      new int[] {89, 0x2739}, //89 twelve pointed black star
      new int[] {90, 0x273A}, //90 sixteen pointed asterisk
      new int[] {91, 0x273B}, //91 teardrop-spoked asterisk
      new int[] {92, 0x273C}, //92 open centred teardrop-spoked asterisk
      new int[] {93, 0x273D}, //93 heavy teardrop-spoked asterisk
      new int[] {94, 0x273E}, //94 six petalled black and white florette
      new int[] {95, 0x273F}, //95 black florette
      new int[] {96, 0x2740}, //96 white florette
      new int[] {97, 0x2741}, //97 eight petalled outlined black florette
      new int[] {98, 0x2742}, //98 circled open centre eight pointed star
      new int[] {99, 0x2743}, //99 heavy teardrop-spoked pinwheel asterisk
      new int[] {100, 0x2744}, //100 snowflake
      new int[] {101, 0x2745}, //101 tight trifoliate snowflake
      new int[] {102, 0x2746}, //102 heavy chevron snowflake
      new int[] {103, 0x2747}, //103 sparkle
      new int[] {104, 0x2748}, //104 heavy sparkle
      new int[] {105, 0x2749}, //105 balloon-spoked asterisk
      new int[] {106, 0x274A}, //106 eight teardrop-spoked propeller asterisk
      new int[] {107, 0x274B}, //107 heavy eight teardrop-spoked propeller asterisk
      new int[] {108, 0x2B24}, //108 large black circle?
      new int[] {109, 0x274D}, //109 shadowed white circle
      new int[] {110, 0x25A0}, //110 black square
      new int[] {111, 0x274F}, //111 lower right drop-shadowed white square
      new int[] {112, 0x2750}, //112 upper right drop-shadowed white square
      new int[] {113, 0x2751}, //113 lower right shadowed white square
      new int[] {114, 0x2752}, //114 upper right shadowed white square
      new int[] {115, 0x25B2}, //115 black up-pointing triangle
      new int[] {116, 0x25BC}, //116 black down-pointing triangle
      new int[] {117, 0x25C6}, //117 black diamond
      new int[] {118, 0x2756}, //118 black diamond minus white X
      new int[] {119, 0x25D7}, //119 right half black circle
      new int[] {120, 0x2758}, //120 light vertical bar
      new int[] {121, 0x2759}, //121 medium vertical bar
      new int[] {122, 0x275A}, //122 heavy vertical bar
      new int[] {123, 0x275B}, //123 heavy single turned comma quotation mark ornament
      new int[] {124, 0x275C}, //124 heavy single comma quotation mark ornament
      new int[] {125, 0x275D}, //125 heavy double turned comma quotation mark ornament
      new int[] {126, 0x275E}, //126 heavy double comma quotation mark ornament
      new int[] {161, 0x2761}, //161 curved stem paragraph sign ornament
      new int[] {162, 0x2762}, //162 heavy exclamation mark ornament
      new int[] {163, 0x2763}, //163 heavy heart exclamation mark ornament
      new int[] {164, 0x2764}, //164 heavy black heart
      new int[] {165, 0x2765}, //165 rotated heavy black heart bullet
      new int[] {166, 0x2766}, //166 floral heart
      new int[] {167, 0x2767}, //167 rotated floral heart
      new int[] {168, 0x2663}, //168 black club suit
      new int[] {169, 0x2666}, //169 black diamond suit
      new int[] {170, 0x2665}, //170 black heart suit
      new int[] {171, 0x2660}, //171 black spade suit
      new int[] {172, 0x2460}, //172 circled digit one
      new int[] {173, 0x2461}, //173 circled digit two
      new int[] {174, 0x2462}, //174 circled digit three
      new int[] {175, 0x2463}, //175 circled digit four
      new int[] {176, 0x2464}, //176 circled digit five
      new int[] {177, 0x2465}, //177 circled digit six
      new int[] {178, 0x2466}, //178 circled digit seven
      new int[] {179, 0x2467}, //179 circled digit eight
      new int[] {180, 0x2468}, //180 circled digit nine
      new int[] {181, 0x2469}, //181 circled digit ten
      new int[] {182, 0x2776}, //182 negative circled digit one
      new int[] {183, 0x2777}, //183 negative circled digit two
      new int[] {184, 0x2778}, //184 negative circled digit three
      new int[] {185, 0x2779}, //185 negative circled digit four
      new int[] {186, 0x277A}, //186 negative circled digit five
      new int[] {187, 0x277B}, //187 negative circled digit six
      new int[] {188, 0x277C}, //188 negative circled digit seven
      new int[] {189, 0x277D}, //189 negative circled digit eight
      new int[] {190, 0x277E}, //190 negative circled digit nine
      new int[] {191, 0x277F}, //191 negative circled digit ten
      new int[] {192, 0x2780}, //192 circled sans-serif digit one
      new int[] {193, 0x2781}, //193 circled sans-serif digit two
      new int[] {194, 0x2782}, //194 circled sans-serif digit three
      new int[] {195, 0x2783}, //195 circled sans-serif digit four
      new int[] {196, 0x2784}, //196 circled sans-serif digit five
      new int[] {197, 0x2785}, //197 circled sans-serif digit six
      new int[] {198, 0x2786}, //198 circled sans-serif digit seven
      new int[] {199, 0x2787}, //199 circled sans-serif digit eight
      new int[] {200, 0x2788}, //200 circled sans-serif digit nine
      new int[] {201, 0x2789}, //201 circled sans-serif digit ten
      new int[] {202, 0x278A}, //202 negative circled sans-serif digit one
      new int[] {203, 0x278B}, //203 negative circled sans-serif digit two
      new int[] {204, 0x278C}, //204 negative circled sans-serif digit three
      new int[] {205, 0x278D}, //205 negative circled sans-serif digit four
      new int[] {206, 0x278E}, //206 negative circled sans-serif digit five
      new int[] {207, 0x278F}, //207 negative circled sans-serif digit six
      new int[] {208, 0x2790}, //208 negative circled sans-serif digit seven
      new int[] {209, 0x2791}, //209 negative circled sans-serif digit eight
      new int[] {210, 0x2792}, //210 negative circled sans-serif digit nine
      new int[] {211, 0x2793}, //211 negative circled sans-serif digit ten
      new int[] {212, 0x2794}, //212 heavy wide-headed rightwards arrow
      new int[] {213, 0x2192}, //213 rightwards arrow?
      new int[] {214, 0x2194}, //214 left right arrow?
      new int[] {215, 0x2195}, //215 up down arrow?
      new int[] {216, 0x2798}, //216 heavy south east arrow
      new int[] {217, 0x2799}, //217 heavy rightwards arrow
      new int[] {218, 0x279A}, //218 heavy north east arrow
      new int[] {219, 0x279B}, //219 drafting point rightwards arrow
      new int[] {220, 0x279C}, //220 heavy round-tipped rightwards arrow
      new int[] {221, 0x279D}, //221 triangle-headed rightward arrow
      new int[] {222, 0x279E}, //222 heavy triangle-headed rightwards arrow
      new int[] {223, 0x279F}, //223 dashed triangle-headed rightwards arrow
      new int[] {224, 0x27A0}, //224 heavy dashed triangle-headed rightwards arrow
      new int[] {225, 0x27A1}, //225 black rightwards arrow
      new int[] {226, 0x27A2}, //226 three-D top-lighted rightwards arrowhead
      new int[] {227, 0x27A3}, //227 three-D bottom-lighted rightwards arrowhead
      new int[] {228, 0x27A4}, //228 black rightwards arrowhead
      new int[] {229, 0x27A5}, //229 heavy black curved downwards and rightwards arrow
      new int[] {230, 0x27A6}, //230 heavy black curved upwards and rightwards arrow
      new int[] {231, 0x27A7}, //231 squat black rightwards arrow
      new int[] {232, 0x27A8}, //232 heavy concave-pointed black rightwards arrow
      new int[] {233, 0x27A9}, //233 right-shaded white rightwards arrow
      new int[] {234, 0x27AA}, //234 left-shaded white rightwards arrow
      new int[] {235, 0x27AB}, //235 back-tilted shadowed white rightwards arrow
      new int[] {236, 0x27AC}, //236 front-tilted shadowed white rightwards arrow
      new int[] {237, 0x27AD}, //237 heavy lower right-shadowed white rightwards arrow
      new int[] {238, 0x27AE}, //238 heavy upper right-shadowed white rightwards arrow
      new int[] {239, 0x27AF}, //239 notched lower right-shadowed white rightwards arrow
      new int[] {241, 0x27B1}, //241 notched upper right-shadowed white rightwards arrow
      new int[] {242, 0x27B2}, //242 circled heavy white rightwards arrow
      new int[] {243, 0x27B3}, //243 white-feathered rightwards arrow
      new int[] {244, 0x27B4}, //244 black-feathered south east arrow
      new int[] {245, 0x27B5}, //245 black-feathered rightwards arrow
      new int[] {246, 0x27B6}, //246 black-feathered north east arrow
      new int[] {247, 0x27B7}, //247 heavy black-feathered south east arrow
      new int[] {248, 0x27B8}, //248 heavy black-feathered rightwards arrow
      new int[] {249, 0x27B9}, //249 heavy black-feathered north east arrow
      new int[] {250, 0x27BA}, //250 teardrop-barbed rightwards arrow
      new int[] {251, 0x27BB}, //251 heavy teardrop-shanked rightwards arrow
      new int[] {252, 0x27BC}, //252 wedge-tailed rightwards arrow
      new int[] {253, 0x27BD}, //253 heavy wedge-tailed rightwards arrow
      new int[] {254, 0x27BE} //254 open-outlined rightwards arrow
   };
}
