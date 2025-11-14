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
package com.dickimawbooks.texparserlib.latex.geometry;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GeometrySty extends LaTeXSty
{
   public GeometrySty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "geometry", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      TeXParser parser = getParser();

      defOrSetDimenReg(PercentUnit.PAPER_WIDTH, "paperwidth", 614.295f, TeXUnit.PT);
      defOrSetDimenReg(PercentUnit.PAPER_HEIGHT, "paperheight", 794.96999f, TeXUnit.PT);
      defOrSetDimenReg(PercentUnit.TEXT_WIDTH, "textwidth", 430.00462f, TeXUnit.PT);
      defOrSetDimenReg(PercentUnit.TEXT_HEIGHT, "textheight", 556.47656f, TeXUnit.PT);
      defOrSetDimenReg(PercentUnit.MARGIN_WIDTH, "marginparwidth", 65, TeXUnit.PT);
      defOrSetDimenReg("marginparsep", 11, TeXUnit.PT);
      defOrSetDimenReg("oddsidemargin", 19.8752f, TeXUnit.PT);
      defOrSetDimenReg("evensidemargin", 19.8752f, TeXUnit.PT);
      defOrSetDimenReg("topmargin", -13.87262f, TeXUnit.PT);
      defOrSetDimenReg("headheight", 12, TeXUnit.PT);
      defOrSetDimenReg("headsep", 25, TeXUnit.PT);
      defOrSetDimenReg("footskip", 30, TeXUnit.PT);

      registerControlSequence(new Geometry(this));

      // TODO \newgeometry \restoregeometry
      // \savegeometry \loadgeometry
   }

   protected void defOrSetDimenReg(String csname, float value, TeXUnit unit)
   {
      TeXParser parser = getParser();

      DimenRegister reg = parser.getDimenRegister(csname);

      if (reg == null)
      {
         registerNewLength(csname, value, unit);
      }
      else
      {
         try
         {
            reg.setValue(getParser(), value, unit);
         }
         catch (TeXSyntaxException e)
         {
            getParser().warning(e);
         }
      }
   }

   protected void defOrSetDimenReg(int type, String csname, float value, TeXUnit unit)
   {
      defOrSetDimenReg(csname, value, unit);

      try
      {
         listener.setPageDimension(type, unit.toUnit(getParser(), value, FixedUnit.BP));
      }
      catch (TeXSyntaxException e)
      {
         getParser().warning(e);
      }
   }

   @Override
   public void processOptions(KeyValList options)
   throws IOException
   {
      processOptions(options, null);
   }

   public void processOptions(KeyValList options, TeXObjectList stack)
   throws IOException
   {
      if (options == null) return;

      TeXParser parser = getParser();

      // only supporting a subset of options

      Boolean portrait = null;
      TeXDimension paperWidth = null;
      TeXDimension paperHeight = null;
      TeXDimension textWidth = null;
      TeXDimension textHeight = null;
      TeXDimension width = null;
      TeXDimension height = null;
      TeXDimension totalWidth = null;
      TeXDimension totalHeight = null;
      TeXDimension left = null;
      TeXDimension right = null;
      TeXDimension top = null;
      TeXDimension bottom = null;

      for (Iterator<String> it = options.getOrderedKeyIterator(); it.hasNext();)
      {
         String option = it.next();

         TeXObject value = (TeXObject)options.getValue(option).clone();

         if (option.equals("paper") || option.equals("papername"))
         {
            setPaperSize(parser.expandToString(value, stack));
            paperWidth = getPaperWidth();
            paperHeight = getPaperHeight();
         }
         else if (option.equals("portrait"))
         {
            if (value == null)
            {
               portrait = Boolean.TRUE;
            }
            else
            {
               portrait = Boolean.valueOf(value.toString(parser));
            }
         }
         else if (option.equals("landscape"))
         {
            if (value == null)
            {
               portrait = Boolean.FALSE;
            }
            else
            {
               portrait = !Boolean.valueOf(value.toString(parser));
            }
         }
         else if (option.equals("headheight") || option.equals("head"))
         {
            setDimension("headheight", TeXParserUtils.toTeXDimension(value, parser, stack));
         }
         else if (option.equals("headsep"))
         {
            setDimension("headsep", TeXParserUtils.toTeXDimension(value, parser, stack));
         }
         else if (option.equals("nohead"))
         {
            setDimension("headheight", new UserDimension(0, FixedUnit.PT));
            setDimension("headsep", new UserDimension(0, FixedUnit.PT));
         }
         else if (option.equals("footskip") || option.equals("foot"))
         {
            setDimension("footskip", TeXParserUtils.toTeXDimension(value, parser, stack));
         }
         else if (option.equals("nofoot"))
         {
            setDimension("footskip", new UserDimension(0, FixedUnit.PT));
         }
         else if (option.equals("noheadfoot"))
         {
            setDimension("headheight", new UserDimension(0, FixedUnit.PT));
            setDimension("headsep", new UserDimension(0, FixedUnit.PT));
            setDimension("footskip", new UserDimension(0, FixedUnit.PT));
         }
         else if (option.equals("footnotesep"))
         {
         }
         else if (option.equals("marginparwidth") || option.equals("marginpar"))
         {
            setDimension("marginparwidth",
               TeXParserUtils.toTeXDimension(value, parser, stack));
         }
         else if (option.equals("marginparsep"))
         {
            setDimension("marginparsep",
               TeXParserUtils.toTeXDimension(value, parser, stack));
         }
         else if (option.equals("nomarginpar"))
         {
            setDimension("marginparwidth", new UserDimension(0, FixedUnit.PT));
            setDimension("marginparsep", new UserDimension(0, FixedUnit.PT));
         }
         else if (option.equals("columnsep"))
         {
            setDimension("columnsep",
               TeXParserUtils.toTeXDimension(value, parser, stack));
         }
         else if (option.equals("voffset"))
         {
            setDimension("voffset",
               TeXParserUtils.toTeXDimension(value, parser, stack));
         }
         else if (option.equals("hoffset"))
         {
            setDimension("hoffset",
               TeXParserUtils.toTeXDimension(value, parser, stack));
         }
         else if (option.equals("offset"))
         {
            TeXDimension dim = TeXParserUtils.toTeXDimension(value, parser, stack);
            setDimension("hoffset", dim);
            setDimension("voffset", dim);
         }
         else if (option.equals("textwidth"))
         {
            textWidth = TeXParserUtils.toTeXDimension(value, parser, stack);
         }
         else if (option.equals("textheight"))
         {
            textHeight = TeXParserUtils.toTeXDimension(value, parser, stack);
         }
         else if (option.equals("width"))
         {
            width = TeXParserUtils.toTeXDimension(value, parser, stack);
         }
         else if (option.equals("height"))
         {
            height = TeXParserUtils.toTeXDimension(value, parser, stack);
         }
         else if (option.equals("totalwidth"))
         {
            totalWidth = TeXParserUtils.toTeXDimension(value, parser, stack);
         }
         else if (option.equals("totalheight"))
         {
            totalHeight = TeXParserUtils.toTeXDimension(value, parser, stack);
         }
         else if (option.equals("left") || option.equals("lmargin")
               || option.equals("inner"))
         {
            left = TeXParserUtils.toTeXDimension(value, parser, stack);
         }
         else if (option.equals("right") || option.equals("rmargin")
               || option.equals("outer"))
         {
            right = TeXParserUtils.toTeXDimension(value, parser, stack);
         }
         else if (option.equals("top") || option.equals("tmargin"))
         {
            top = TeXParserUtils.toTeXDimension(value, parser, stack);
         }
         else if (option.equals("bottom") || option.equals("bmargin"))
         {
            bottom = TeXParserUtils.toTeXDimension(value, parser, stack);
         }
         else if (option.equals("hmargin"))
         {
         }
         else if (option.equals("vmargin"))
         {
         }
         else if (option.equals("margin"))
         {
         }
         else if (option.equals("hmarginratio"))
         {
         }
         else if (option.equals("vmarginratio"))
         {
         }
         else if (option.equals("marginratio") || option.equals("ratio"))
         {
         }
         else if (option.equals("hcentering"))
         {
         }
         else if (option.equals("vcentering"))
         {
         }
         else if (option.equals("centering"))
         {
         }
         else if (option.equals("reversemp") || option.equals("reversemarginpar"))
         {
         }
         else if (option.equals("onecolumn"))
         {
         }
         else if (option.equals("twocolumn"))
         {
         }
         else if (option.equals("twoside"))
         {
         }
         else if (option.equals("asymmetric"))
         {
         }
         else if (option.equals("bindingoffset"))
         {
         }
         else if (option.equals("hdivide"))
         {
         }
         else if (option.equals("vdivide"))
         {
         }
         else if (option.equals("divide"))
         {
         }
         else if (option.equals("includehead"))
         {
            if (value == null)
            {
               includehead = true;
            }
            else
            {
               includehead = Boolean.parseBoolean(value.toString(parser));
            }
         }
         else if (option.equals("includefoot"))
         {
            if (value == null)
            {
               includefoot = true;
            }
            else
            {
               includefoot = Boolean.parseBoolean(value.toString(parser));
            }
         }
         else if (option.equals("includeheadfoot"))
         {
            if (value == null)
            {
               includehead = true;
            }
            else
            {
               includehead = Boolean.parseBoolean(value.toString(parser));
            }

            includefoot = includehead;
         }
         else if (option.equals("includemp"))
         {
            if (value == null)
            {
               includemp = true;
            }
            else
            {
               includemp = Boolean.parseBoolean(value.toString(parser));
            }
         }
         else if (option.equals("includeall"))
         {
            if (value == null)
            {
               includehead = true;
            }
            else
            {
               includehead = Boolean.parseBoolean(value.toString(parser));
            }

            includefoot = includehead;
            includemp = includehead;
         }
         else if (option.equals("ignorehead"))
         {
            if (value == null)
            {
               includehead = false;
            }
            else
            {
               includehead = !Boolean.parseBoolean(value.toString(parser));
            }
         }
         else if (option.equals("ignorefoot"))
         {
            if (value == null)
            {
               includefoot = false;
            }
            else
            {
               includefoot = !Boolean.parseBoolean(value.toString(parser));
            }
         }
         else if (option.equals("ignoreheadfoot"))
         {
            if (value == null)
            {
               includehead = false;
            }
            else
            {
               includehead = !Boolean.parseBoolean(value.toString(parser));
            }

            includefoot = includehead;
         }
         else if (option.equals("ignoremp"))
         {
            if (value == null)
            {
               includemp = false;
            }
            else
            {
               includemp = !Boolean.parseBoolean(value.toString(parser));
            }
         }
         else if (option.equals("ignoreall"))
         {
            if (value == null)
            {
               includehead = false;
            }
            else
            {
               includehead = !Boolean.parseBoolean(value.toString(parser));
            }

            includefoot = includehead;
            includemp = includehead;
         }
         else if (option.equals("heightrounded"))
         {
         }
         else if (option.equals("hdivide"))
         {
         }
         else if (option.equals("vdivide"))
         {
         }
         else if (option.equals("divide"))
         {
         }
         else if (option.equals("driver"))
         {
         }
         else if (option.equals("dvips"))
         {
         }
         else if (option.equals("dvipdfm"))
         {
         }
         else if (option.equals("pdftex"))
         {
         }
         else if (option.equals("luatex"))
         {
         }
         else if (option.equals("xetex"))
         {
         }
         else if (option.equals("vtex"))
         {
         }
         else if (option.equals("verbose"))
         {
         }
         else if (option.equals("mag"))
         {
         }
         else if (option.equals("truedim"))
         {
         }
         else if (option.equals("pass"))
         {
         }
         else if (option.equals("showframe"))
         {
         }
         else if (option.equals("showcrop"))
         {
         }
         else if (value == null)
         {
            setPaperSize(parser.expandToString(value, stack));
            paperWidth = getPaperWidth();
            paperHeight = getPaperHeight();
         }
      }

      if (paperWidth == null)
      {
         paperWidth = getPaperWidth();

         if (paperWidth == null)
         {
            paperWidth = new UserDimension(210, TeXUnit.MM);
         }
      }

      if (paperHeight == null)
      {
         paperHeight = getPaperHeight();

         if (paperHeight == null)
         {
            paperHeight = new UserDimension(297, TeXUnit.MM);
         }
      }

      if (portrait != null)
      {
         if (portrait.booleanValue())
         {
            if (!isPortrait())
            {
               swapOrientation();
            }
         }
         else
         {
            if (!isLandscape())
            {
               swapOrientation();
            }
         }
      }

      if (width != null && totalWidth == null)
      {
         if (includemp)
         {
            TeXUnit unit = width.getUnit();

            TeXDimension marginParWidth = getDimension("marginparwidth");
            TeXDimension marginParSep = getDimension("marginparsep");

            totalWidth = new UserDimension(
             width.getValue()
             + unit.fromUnit(parser, marginParWidth.getValue(), marginParWidth.getUnit())
             + unit.fromUnit(parser, marginParSep.getValue(), marginParSep.getUnit()) ,
             unit);
         }
         else
         {
            totalWidth = width;
         }

         textWidth = width;
      }
      else if (totalWidth != null)
      {
         if (includemp)
         {
            TeXUnit unit = totalWidth.getUnit();

            TeXDimension marginParWidth = getDimension("marginparwidth");
            TeXDimension marginParSep = getDimension("marginparsep");

            textWidth = new UserDimension(
             totalWidth.getValue()
             - unit.fromUnit(parser, marginParWidth.getValue(), marginParWidth.getUnit())
             - unit.fromUnit(parser, marginParSep.getValue(), marginParSep.getUnit()) ,
             unit);
         }
         else
         {
            textWidth = totalWidth;
         }
      }

      if (textWidth == null && left != null && right != null)
      {
         TeXUnit unit = paperWidth.getUnit();

         textWidth = new UserDimension(
           paperWidth.getValue()
           - left.getUnit().toUnit(parser, left.getValue(), unit)
           - right.getUnit().toUnit(parser, right.getValue(), unit),
           unit
         );
      }

      if (textWidth != null)
      {
         setDimension("textwidth", textWidth);
      }
      else
      {
         textWidth = getTextWidth();
      }

      if (height != null && totalHeight == null)
      {
         if (includehead && includefoot)
         {
            TeXUnit unit = height.getUnit();

            TeXDimension headHeight = getDimension("headheight");
            TeXDimension headSep = getDimension("headsep");
            TeXDimension footSkip = getDimension("footskip");

            totalHeight = new UserDimension(
             height.getValue()
             + unit.fromUnit(parser, headHeight.getValue(), headHeight.getUnit())
             + unit.fromUnit(parser, headSep.getValue(), headSep.getUnit()) 
             + unit.fromUnit(parser, footSkip.getValue(), footSkip.getUnit()) ,
             unit);
         }
         else if (includehead)
         {
            TeXUnit unit = height.getUnit();

            TeXDimension headHeight = getDimension("headheight");
            TeXDimension headSep = getDimension("headsep");

            totalHeight = new UserDimension(
             height.getValue()
             + unit.fromUnit(parser, headHeight.getValue(), headHeight.getUnit())
             + unit.fromUnit(parser, headSep.getValue(), headSep.getUnit()) ,
             unit);
         }
         else if (includefoot)
         {
            TeXUnit unit = height.getUnit();

            TeXDimension footSkip = getDimension("footskip");

            totalHeight = new UserDimension(
             height.getValue()
             + unit.fromUnit(parser, footSkip.getValue(), footSkip.getUnit()) ,
             unit);
         }
         else
         {
            totalHeight = height;
         }

         textHeight = height;
      }
      else if (totalHeight != null)
      {
         if (includehead && includefoot)
         {
            TeXUnit unit = totalHeight.getUnit();

            TeXDimension headHeight = getDimension("headheight");
            TeXDimension headSep = getDimension("headsep");
            TeXDimension footSkip = getDimension("footskip");

            textHeight = new UserDimension(
             totalHeight.getValue()
             - unit.fromUnit(parser, headHeight.getValue(), headHeight.getUnit())
             - unit.fromUnit(parser, headSep.getValue(), headSep.getUnit()) 
             - unit.fromUnit(parser, footSkip.getValue(), footSkip.getUnit()) ,
             unit);
         }
         else if (includehead)
         {
            TeXUnit unit = totalHeight.getUnit();

            TeXDimension headHeight = getDimension("headheight");
            TeXDimension headSep = getDimension("headsep");

            textHeight = new UserDimension(
             totalHeight.getValue()
             - unit.fromUnit(parser, headHeight.getValue(), headHeight.getUnit())
             - unit.fromUnit(parser, headSep.getValue(), headSep.getUnit()) ,
             unit);
         }
         else if (includefoot)
         {
            TeXUnit unit = totalHeight.getUnit();

            TeXDimension footSkip = getDimension("footskip");

            textHeight = new UserDimension(
             totalHeight.getValue()
             - unit.fromUnit(parser, footSkip.getValue(), footSkip.getUnit()) ,
             unit);
         }
         else
         {
            textHeight = totalHeight;
         }
      }

      if (textHeight == null && top != null && bottom != null)
      {
         TeXUnit unit = paperHeight.getUnit();

         textHeight = new UserDimension(
           paperHeight.getValue()
           - top.getUnit().toUnit(parser, top.getValue(), unit)
           - bottom.getUnit().toUnit(parser, bottom.getValue(), unit),
           unit
         );
      }

      if (textHeight != null)
      {
         setDimension("textheight", textHeight);
      }
      else
      {
         textHeight = getTextHeight();
      }

      if (left != null)
      {
         TeXUnit unit = paperWidth.getUnit();

         setDimension("oddsidemargin",
            left.getUnit().toUnit(parser, left.getValue(), unit)
           - TeXUnit.IN.toUnit(parser, 1.0f, unit),
            unit);

         setDimension("evensidemargin",
           paperWidth.getValue()
           - totalWidth.getUnit().toUnit(parser, totalWidth.getValue(), unit)
           - left.getUnit().toUnit(parser, left.getValue(), unit)
           - TeXUnit.IN.toUnit(parser, 1.0f, unit),
           unit
          );
      }
      else if (right != null)
      {
         TeXUnit unit = paperWidth.getUnit();

         setDimension("evensidemargin",
            right.getUnit().toUnit(parser, right.getValue(), unit)
           - TeXUnit.IN.toUnit(parser, 1.0f, unit),
            unit);

         setDimension("oddsidemargin",
           paperWidth.getValue()
           - totalWidth.getUnit().toUnit(parser, totalWidth.getValue(), unit)
           - right.getUnit().toUnit(parser, right.getValue(), unit)
           - TeXUnit.IN.toUnit(parser, 1.0f, unit),
           unit
          );
      }

      if (top != null)
      {
         TeXUnit unit = paperHeight.getUnit();

         setDimension("topmargin",
            top.getUnit().toUnit(parser, top.getValue(), unit)
           - TeXUnit.IN.toUnit(parser, 1.0f, unit),
            unit);
      }
      else if (bottom != null)
      {
         TeXUnit unit = paperHeight.getUnit();

         setDimension("topmargin",
           paperHeight.getValue()
           - totalHeight.getUnit().toUnit(parser, totalHeight.getValue(), unit)
           - bottom.getUnit().toUnit(parser, bottom.getValue(), unit)
           - TeXUnit.IN.toUnit(parser, 1.0f, unit),
           unit
          );
      }
   }

   public boolean isLandscape()
   throws IOException
   {
      TeXDimension w = getPaperWidth();
      TeXDimension h = getPaperHeight();

      if (w != null && h != null)
      {
         TeXUnit wUnit = w.getUnit();
         TeXUnit hUnit = h.getUnit();

         if (wUnit == hUnit)
         {
            return w.getValue() > h.getValue();
         }
         else
         {
            return wUnit.toPt(getParser(), w.getValue())
                 > hUnit.toPt(getParser(), h.getValue());
         }
      }
      else
      {
         return false;
      }
   }

   public boolean isPortrait()
   throws IOException
   {
      TeXDimension w = getPaperWidth();
      TeXDimension h = getPaperHeight();

      if (w != null && h != null)
      {
         TeXUnit wUnit = w.getUnit();
         TeXUnit hUnit = h.getUnit();

         if (wUnit == hUnit)
         {
            return w.getValue() < h.getValue();
         }
         else
         {
            return wUnit.toPt(getParser(), w.getValue())
                 < hUnit.toPt(getParser(), h.getValue());
         }
      }
      else
      {
         return true;
      }
   }

   protected void swapOrientation()
   throws IOException
   {
      TeXDimension w = getPaperWidth();
      TeXDimension h = getPaperHeight();

      TeXDimension dim = h;

      h = w;
      w = dim;

      if (h != null)
      {
         setDimension("paperheight", h.getValue(), h.getUnit());
      }

      if (w != null)
      {
         setDimension("paperwidth", w.getValue(), w.getUnit());
      }

      w = getTextWidth();
      h = getTextHeight();

      dim = h;

      h = w;
      w = dim;

      if (h != null)
      {
         setDimension("textheight", h.getValue(), h.getUnit());
      }

      if (w != null)
      {
         setDimension("textwidth", w.getValue(), w.getUnit());
      }
   }

   public void setPaperSize(String name)
   throws IOException
   {
      if (name.equals("a0paper"))
      {
         setPaperSize(841, 1189, TeXUnit.MM);
      }
      else if (name.equals("a1paper"))
      {
         setPaperSize(594, 841, TeXUnit.MM);
      }
      else if (name.equals("a2paper"))
      {
         setPaperSize(420, 594, TeXUnit.MM);
      }
      else if (name.equals("a3paper"))
      {
         setPaperSize(297, 420, TeXUnit.MM);
      }
      else if (name.equals("a4paper"))
      {
         setPaperSize(210, 297, TeXUnit.MM);
      }
      else if (name.equals("a5paper"))
      {
         setPaperSize(148, 210, TeXUnit.MM);
      }
      else if (name.equals("a6paper"))
      {
         setPaperSize(105, 148, TeXUnit.MM);
      }
      else if (name.equals("b0paper"))
      {
         setPaperSize(1000, 1414, TeXUnit.MM);
      }
      else if (name.equals("b1paper"))
      {
         setPaperSize(707, 1000, TeXUnit.MM);
      }
      else if (name.equals("b2paper"))
      {
         setPaperSize(500, 707, TeXUnit.MM);
      }
      else if (name.equals("b3paper"))
      {
         setPaperSize(353, 500, TeXUnit.MM);
      }
      else if (name.equals("b4paper"))
      {
         setPaperSize(250, 353, TeXUnit.MM);
      }
      else if (name.equals("b5paper"))
      {
         setPaperSize(176, 250, TeXUnit.MM);
      }
      else if (name.equals("b6paper"))
      {
         setPaperSize(125, 176, TeXUnit.MM);
      }
      else if (name.equals("c0paper"))
      {
         setPaperSize(917, 1297, TeXUnit.MM);
      }
      else if (name.equals("c1paper"))
      {
         setPaperSize(648, 917, TeXUnit.MM);
      }
      else if (name.equals("c2paper"))
      {
         setPaperSize(458, 648, TeXUnit.MM);
      }
      else if (name.equals("c3paper"))
      {
         setPaperSize(324, 458, TeXUnit.MM);
      }
      else if (name.equals("c4paper"))
      {
         setPaperSize(229, 324, TeXUnit.MM);
      }
      else if (name.equals("c5paper"))
      {
         setPaperSize(162, 229, TeXUnit.MM);
      }
      else if (name.equals("c6paper"))
      {
         setPaperSize(114, 162, TeXUnit.MM);
      }
      else if (name.equals("b0j"))
      {
         setPaperSize(1030, 1456, TeXUnit.MM);
      }
      else if (name.equals("b1j"))
      {
         setPaperSize(728, 1030, TeXUnit.MM);
      }
      else if (name.equals("b2j"))
      {
         setPaperSize(515, 728, TeXUnit.MM);
      }
      else if (name.equals("b3j"))
      {
         setPaperSize(364, 515, TeXUnit.MM);
      }
      else if (name.equals("b4j"))
      {
         setPaperSize(257, 364, TeXUnit.MM);
      }
      else if (name.equals("b5j"))
      {
         setPaperSize(182, 257, TeXUnit.MM);
      }
      else if (name.equals("b6j"))
      {
         setPaperSize(128, 182, TeXUnit.MM);
      }
      else if (name.equals("ansiapaper"))
      {
         setPaperSize(8.5, 11, TeXUnit.IN);
      }
      else if (name.equals("ansibpaper"))
      {
         setPaperSize(11, 17, TeXUnit.IN);
      }
      else if (name.equals("ansicpaper"))
      {
         setPaperSize(17, 22, TeXUnit.IN);
      }
      else if (name.equals("ansidpaper"))
      {
         setPaperSize(22, 34, TeXUnit.IN);
      }
      else if (name.equals("ansiepaper"))
      {
         setPaperSize(34, 44, TeXUnit.IN);
      }
      else if (name.equals("letterpaper"))
      {
         setPaperSize(8.5, 11, TeXUnit.IN);
      }
      else if (name.equals("legalpaper"))
      {
         setPaperSize(8.5, 14, TeXUnit.IN);
      }
      else if (name.equals("executivepaper"))
      {
         setPaperSize(7.25, 10.5, TeXUnit.IN);
      }
      else if (name.equals("screen"))
      {
         setPaperSize(225, 180, TeXUnit.MM);
      }
      else
      {
         throw new LaTeXSyntaxException(getParser(),
           LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, name);
      }
   }

   public void setPaperSize(double w, double h, TeXUnit unit)
   throws IOException
   {
      setDimension("paperwidth", new UserDimension(w, unit));
      setDimension("paperheight", new UserDimension(h, unit));
   }

   public void setDimension(String name, double value, TeXUnit unit)
   throws IOException
   {
      setDimension(name, new UserDimension(value, unit));

      updatePageDimension(name, unit.toUnit(getParser(), (float)value, FixedUnit.BP));
   }

   public void setDimension(String name, TeXDimension newDim)
   throws IOException
   {
      TeXDimension dim = getDimension(name);

      if (dim == null)
      {
         dim = getParser().getSettings().newdimen(name);
      }

      dim.setDimension(getParser(), newDim);

      TeXUnit unit = newDim.getUnit();
      float value = newDim.getValue();
      updatePageDimension(name, unit.toUnit(getParser(), value, FixedUnit.BP));
   }

   protected void updatePageDimension(String name, float bpValue)
   {
      if (name.equals("paperwidth"))
      {
         listener.setPageDimension(PercentUnit.PAPER_WIDTH, bpValue);
      }
      else if (name.equals("paperheight"))
      {
         listener.setPageDimension(PercentUnit.PAPER_HEIGHT, bpValue);
      }
      else if (name.equals("hsize"))
      {
         listener.setPageDimension(PercentUnit.HSIZE, bpValue);
      }
      else if (name.equals("vsize"))
      {
         listener.setPageDimension(PercentUnit.VSIZE, bpValue);
      }
      else if (name.equals("textwidth"))
      {
         listener.setPageDimension(PercentUnit.TEXT_WIDTH, bpValue);
      }
      else if (name.equals("textheight"))
      {
         listener.setPageDimension(PercentUnit.TEXT_HEIGHT, bpValue);
      }
      else if (name.equals("marginparwidth"))
      {
         listener.setPageDimension(PercentUnit.MARGIN_WIDTH, bpValue);
      }
      else if (name.equals("columnwidth"))
      {
         listener.setPageDimension(PercentUnit.COLUMN_WIDTH, bpValue);
      }
      else if (name.equals("columnheight"))
      {
         listener.setPageDimension(PercentUnit.COLUMN_HEIGHT, bpValue);
      }
   }

   public TeXDimension getDimension(String name)
   throws IOException
   {
      ControlSequence cs = getParser().getControlSequence(name);

      if (cs == null)
      {
         return null;
      }

      return TeXParserUtils.toTeXDimension(cs, getParser(), null);
   }

   public TeXDimension getPaperWidth()
   throws IOException
   {
      return getDimension("paperwidth");
   }

   public TeXDimension getPaperHeight()
   throws IOException
   {
      return getDimension("paperheight");
   }

   public TeXDimension getTextWidth()
   throws IOException
   {
      return getDimension("textwidth");
   }

   public TeXDimension getTextHeight()
   throws IOException
   {
      return getDimension("textheight");
   }

   public TeXDimension getMarginParWidth()
   throws IOException
   {
      return getDimension("marginparwidth");
   }

   public TeXDimension getMarginParSep()
   throws IOException
   {
      return getDimension("marginparsep");
   }

   public TeXDimension getHeadHeight()
   throws IOException
   {
      return getDimension("headheight");
   }

   public TeXDimension getHeadSep()
   throws IOException
   {
      return getDimension("headsep");
   }

   public TeXDimension getFootSkip()
   throws IOException
   {
      return getDimension("footskip");
   }

   public TeXDimension getTopMargin()
   throws IOException
   {
      return getDimension("topmargin");
   }

   public TeXDimension getOddSideMargin()
   throws IOException
   {
      return getDimension("oddsidemargin");
   }

   public TeXDimension getEvenSideMargin()
   throws IOException
   {
      return getDimension("evensidemargin");
   }

   /**
    * Gets the location and size of the text body relative
    * to the top left corner of the page.
    * @param even true if for even page otherwise assume odd
    * @return the rectangular region in PostScript points
    */ 
   public Rectangle2D getTypeblock(boolean even)
   throws IOException
   {
      double x = 72.0;
      double y = 72.0;
      double w = 0.0;
      double h = 0.0;

      TeXDimension margin;

      if (even)
      {
         margin = getEvenSideMargin();

         if (margin == null)
         {
            margin = getOddSideMargin();
         }
      }
      else
      {
         margin = getOddSideMargin();
      }

      if (margin != null)
      {
         TeXUnit unit = margin.getUnit();
         float value = margin.getValue();

         x += unit.toUnit(getParser(), value, FixedUnit.BP);
      }

      TeXDimension topMargin = getTopMargin();

      if (topMargin != null)
      {
         TeXUnit unit = topMargin.getUnit();
         float value = topMargin.getValue();

         y += unit.toUnit(getParser(), value, FixedUnit.BP);
      }

      TeXDimension headHeight = getHeadHeight();

      if (headHeight != null)
      {
         TeXUnit unit = headHeight.getUnit();
         float value = headHeight.getValue();

         y += unit.toUnit(getParser(), value, FixedUnit.BP);
      }

      TeXDimension headSep = getHeadSep();

      if (headSep != null)
      {
         TeXUnit unit = headSep.getUnit();
         float value = headSep.getValue();

         y += unit.toUnit(getParser(), value, FixedUnit.BP);
      }

      TeXDimension textWidth = getTextWidth();
      TeXDimension textHeight = getTextHeight();

      if (textWidth != null)
      {
         TeXUnit unit = textWidth.getUnit();
         float value = textWidth.getValue();
         w = unit.toUnit(getParser(), value, FixedUnit.BP);
      }

      if (textHeight != null)
      {
         TeXUnit unit = textHeight.getUnit();
         float value = textHeight.getValue();
         h = unit.toUnit(getParser(), value, FixedUnit.BP);
      }

      return new Rectangle2D.Double(x, y, w, h);
   }

   boolean includehead = false;
   boolean includefoot = false;
   boolean includemp = false;
}
