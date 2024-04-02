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
import com.dickimawbooks.texparserlib.html.L2HConverter;
import com.dickimawbooks.texparserlib.html.StartElement;
import com.dickimawbooks.texparserlib.html.EndElement;

/**
 * Implementation of list styles for use with L2HConverter.
 */
public class L2HGlsStyleList extends ControlSequence
{
   public L2HGlsStyleList(String styleName, String cssClass)
   {
      this(styleName, cssClass, false, false);
   }

   public L2HGlsStyleList(String styleName, String cssClass,
      boolean hasNavHeader, boolean hasGroupHeadings)
   {
      this("@glsstyle@"+styleName, styleName, cssClass,
         hasNavHeader, hasGroupHeadings);
   }

   public L2HGlsStyleList(String csname, String styleName, String cssClass,
     boolean hasNavHeader, boolean hasGroupHeadings)
   {
      super(csname);
      this.styleName = styleName;
      this.cssClass = cssClass;
      this.hasNavHeader = hasNavHeader;
      this.hasGroupHeadings = hasGroupHeadings;
   }

   public Object clone()
   {
      return new L2HGlsStyleList(getName(), styleName, cssClass,
        hasNavHeader, hasGroupHeadings);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObjectList beginCode = listener.createStack();

      ControlSequence cs = parser.getControlSequence("glslistinit");

      if (cs != null)
      {
         beginCode.add(cs);
      }

      StartElement startElem = new StartElement("dl", true);

      if (cssClass != null)
      {
         startElem.putAttribute("class", cssClass);
      }

      beginCode.add(startElem);

      TeXObjectList endCode = listener.createStack();

      endCode.add(new EndElement("dl", true));

      listener.newenvironment(Overwrite.ALLOW, "renewcommand",
       "theglossary", 0, null, beginCode, endCode);

      TeXObjectList def;

      if (hasNavHeader)
      {
         def = listener.createStack();

         def.add(new TeXCsRef("glslistnavigation"));
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
         def.add(new TeXCsRef("glslistgroupheaderfmt"));

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

      def = listener.createStack();

      def.add(new StartElement("dt"));
      def.add(new TeXCsRef("glsentryitem"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
      def.add(new TeXCsRef("glstarget"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
      def.add(TeXParserUtils.createGroup(listener,
        new TeXCsRef("glossentryname"),
           TeXParserUtils.createGroup(listener, listener.getParam(1))));
      def.add(new EndElement("dt"));

      def.add(new StartElement("dd"));

      if (parser.getControlSequence("glslistdesc") != null)
      {
         def.add(new TeXCsRef("glslistdesc"));
         def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
      }
      else
      {
         def.add(new TeXCsRef("glossentrydesc"));
         def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
         def.add(new TeXCsRef("glspostdescription"));
      }

      if (parser.getControlSequence("glslistprelocation") != null)
      {
         def.add(new TeXCsRef("glslistprelocation"));
      }
      else
      {
         def.add(new TeXCsRef("space"));
      }

      def.add(listener.getParam(2));
      def.add(new EndElement("dd"));

      listener.putControlSequence(true,
        new LaTeXGenericCommand(true, "glossentry", "mm", def));

      def = listener.createStack();

      def.add(new StartElement("dt"));
      def.add(new TeXCsRef("glssubentryitem"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(2)));
      def.add(new TeXCsRef("glstarget"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(2)));
      def.add(listener.createGroup());
      def.add(new EndElement("dt"));

      def.add(new StartElement("dd"));
      def.add(new TeXCsRef("glossentrydesc"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(2)));
      def.add(new TeXCsRef("glspostdescription"));

      if (parser.getControlSequence("glslistchildprelocation") != null)
      {
         def.add(new TeXCsRef("glslistchildprelocation"));
      }
      else
      {
         def.add(new TeXCsRef("space"));
      }

      def.add(listener.getParam(3));

      if (parser.getControlSequence("glslistchildpostlocation") != null)
      {
         def.add(new TeXCsRef("glslistchildpostlocation"));
      }
      else
      {
         def.add(listener.getOther('.'));
      }

      def.add(new EndElement("dd"));

      listener.putControlSequence(true,
        new LaTeXGenericCommand(true, "subglossentry", "mmm", def));

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

   protected String styleName, cssClass;
   protected boolean hasNavHeader=false, hasGroupHeadings;
}
