/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib;

public class AccSupp
{
   protected AccSupp()
   {
   }

   public String getType()
   {
      return type;
   }

   public String getTag()
   {
      return tag;
   }

   public String getText()
   {
      return text;
   }

   public String getAttribute()
   {
      return attr;
   }

   public String getId()
   {
      return id;
   }

   public static AccSupp createAbbr(String target, String longForm)
   {
      AccSupp accsupp = new AccSupp();

      accsupp.id = target;
      accsupp.type = TYPE_E;
      accsupp.tag = TAG_ABBR;
      accsupp.text = longForm;
      accsupp.attr = ATTR_TITLE;

      return accsupp;
   }

   public static AccSupp createDefn(String anchor)
   {
      AccSupp accsupp = new AccSupp();

      accsupp.id = anchor;
      accsupp.tag = TAG_DFN;

      return accsupp;
   }

   public static AccSupp createSymbol(String description)
   {
      AccSupp accsupp = new AccSupp();

      accsupp.type = TYPE_ACTUAL_TEXT;
      accsupp.text = description;
      accsupp.attr = ATTR_TITLE;

      return accsupp;
   }

   public static AccSupp createImage(String alt)
   {
      AccSupp accsupp = new AccSupp();

      accsupp.type = TYPE_ALT;
      accsupp.tag = TAG_IMG;
      accsupp.text = alt;
      accsupp.attr = ATTR_ALT;

      return accsupp;
   }

   private String type;
   private String tag;
   private String attr;
   private String text;
   private String id;

   public static final String ATTR_TITLE="title";
   public static final String ATTR_ALT="alt";

   public static final String TAG_ABBR="abbr";
   public static final String TAG_DFN="dfn";
   public static final String TAG_IMG="img";

  /**
   * Alt: description of some content that's non-textual (such as an
   * image). A word break is assumed after the content.
   */
   public static final String TYPE_ALT="Alt";

   /**
    * ActualText: a character or sequence of characters that
    * replaces textual content (such as a dropped capital, ligature
    * or symbol). No word break is assumed after the content.
    */ 
   public static final String TYPE_ACTUAL_TEXT="ActualText";

   /**
    * E: expansion of an abbreviation.
    */ 
   public static final String TYPE_E="E";
}

