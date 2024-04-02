/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.latex3.TokenListCommand;
import com.dickimawbooks.texparserlib.html.L2HConverter;
import com.dickimawbooks.texparserlib.html.StartElement;
import com.dickimawbooks.texparserlib.html.EndElement;

/**
 * Implementation of list styles for use with L2HConverter.
 */
public class L2HGlsStyleTree extends ControlSequence
{
   public L2HGlsStyleTree(String styleName, String cssClass, GlossariesSty sty)
   {
      this(styleName, cssClass, false, false, sty);
   }

   public L2HGlsStyleTree(String styleName, String cssClass,
      boolean hasNavHeader, boolean hasGroupHeadings,
      GlossariesSty sty)
   {
      this(styleName, cssClass, hasNavHeader, hasGroupHeadings, true, sty);
   }

   public L2HGlsStyleTree(String styleName, String cssClass,
      boolean hasNavHeader, boolean hasGroupHeadings, boolean showChildName,
      GlossariesSty sty)
   {
      this("@glsstyle@"+styleName, styleName, cssClass,
         hasNavHeader, hasGroupHeadings, showChildName, sty);
   }

   public L2HGlsStyleTree(String csname, String styleName, String cssClass,
     boolean hasNavHeader, boolean hasGroupHeadings, boolean showChildName,
     GlossariesSty sty)
   {
      super(csname);
      this.styleName = styleName;
      this.cssClass = cssClass;
      this.hasNavHeader = hasNavHeader;
      this.hasGroupHeadings = hasGroupHeadings;
      this.showChildName = showChildName;
      this.sty = sty;
   }

   public Object clone()
   {
      return new L2HGlsStyleTree(getName(), styleName, cssClass,
        hasNavHeader, hasGroupHeadings, showChildName, sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObjectList beginCode = listener.createStack();

      StartElement startElem = new StartElement("dl", true);

      if (cssClass != null)
      {
         startElem.putAttribute("class", cssClass+" "+styleName);
      }
      else
      {
         startElem.putAttribute("class", styleName);
      }

      beginCode.add(startElem);
      beginCode.add(new TeXCsRef("tl_clear:N"));
      beginCode.add(new TeXCsRef(PENDING_CSNAME));

      TeXObjectList endCode = listener.createStack();

      endCode.add(new TeXCsRef(PENDING_CSNAME));
      endCode.add(new EndElement("dl", true));

      listener.newenvironment(Overwrite.ALLOW, "renewenvironment",
       "theglossary", 0, null, beginCode, endCode);

      TeXObjectList def;

      if (hasNavHeader)
      {
         def = listener.createStack();

         def.add(new TeXCsRef("glstreenavigation"));
         def.add(TeXParserUtils.createGroup(listener, new TeXCsRef("glsnavigation")));
      }
      else
      {
         listener.putControlSequence(true,
           new GenericCommand(true, "glossaryheader"));
      }

      if (hasGroupHeadings)
      {
         def = listener.createStack();

         startElem = new StartElement("dt");
         startElem.putAttribute("class", "header");

         def.add(startElem);
         def.add(new TeXCsRef("glstreegroupheaderfmt"));

         Group grp = listener.createGroup();
         def.add(grp);

         if (hasNavHeader)
         {
            grp.add(new TeXCsRef("glsnavhypertarget"));
            grp.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

            Group subgrp = listener.createGroup();
            grp.add(subgrp);

            grp = subgrp;
         }

         grp.add(new TeXCsRef("glsgetgrouptitle"));
         grp.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

         def.add(new EndElement("dt"));

         listener.putControlSequence(true, 
           new LaTeXGenericCommand(true, "glsgroupheading", "m", def));
      }
      else
      {
         listener.putControlSequence(true, new AtGobble("glsgroupheading"));
      }

      listener.putControlSequence(true, new TreeGlossEntry(this));
      listener.putControlSequence(true, new TreeSubGlossEntry(this));

      def = listener.createStack();

      def.add(new TeXCsRef("ifglsnogroupskip"));
      def.add(new TeXCsRef("else"));
      def.add(new TeXCsRef("indexspace"));
      def.add(new TeXCsRef("fi"));

      listener.putControlSequence(true,
        new GenericCommand(true, "glsgroupskip", null, def));
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public GlossariesSty getGlossariesSty()
   {
      return sty;
   }

   public String getStyleName()
   {
      return styleName;
   }

   public String getCssClass()
   {
      return cssClass;
   }

   public boolean hasNavHeader()
   {
      return hasNavHeader;
   }

   public boolean hasGroupHeadings()
   {
      return hasGroupHeadings;
   }

   public boolean showChildName()
   {
      return showChildName;
   }

   public IntegerContentCommand getLevelCommand()
   {
      ControlSequence cs = sty.getParser().getControlSequence(LEVEL_CSNAME);

      if (cs == null)
      {
         cs = new IntegerContentCommand(LEVEL_CSNAME, 0);
         sty.getParser().putControlSequence(true, cs);
      }

      return (IntegerContentCommand)cs;
   }

   public TokenListCommand getPendingCommand()
   {
      ControlSequence cs = sty.getParser().getControlSequence(PENDING_CSNAME);

      if (cs == null)
      {
         cs = new TokenListCommand(PENDING_CSNAME);
         sty.getParser().putControlSequence(true, cs);
      }

      return (TokenListCommand)cs;
   }

   protected String styleName, cssClass;
   protected boolean hasNavHeader=false, hasGroupHeadings=false, showChildName=true;
   protected GlossariesSty sty;

   public static final String LEVEL_CSNAME = "texparser@glstree@level";
   public static final String PENDING_CSNAME = "texparser@glstree@pending";
}
