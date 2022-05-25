/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

package com.dickimawbooks.texparserlib.search;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class SearchControlSequence extends AssignedControlSequence
  implements SearchObject
{
   public SearchControlSequence(ControlSequence cs, 
     SearchMatcher searchMatcher,
     SearchTeXFiles searchListener)
   {
      super(cs.getName(), cs);
      this.searchListener = searchListener;
      this.searchMatcher = searchMatcher;
   }

   public void process(TeXParser parser)
      throws IOException
   {
      if (isMatcherFlagOn(ControlSequenceMatcher.FLAG_PROCESS))
      {
         searchListener.registerMatch(this);
      }

      super.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (isMatcherFlagOn(ControlSequenceMatcher.FLAG_PROCESS))
      {
         searchListener.registerMatch(this);
      }

      super.process(parser, stack);
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      if (isMatcherFlagOn(ControlSequenceMatcher.FLAG_EXPANSION))
      {
         searchListener.registerMatch(this);
      }

      return super.expandonce(parser);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (isMatcherFlagOn(ControlSequenceMatcher.FLAG_EXPANSION))
      {
         searchListener.registerMatch(this);
      }

      return super.expandonce(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      if (isMatcherFlagOn(ControlSequenceMatcher.FLAG_EXPANSION))
      {
         searchListener.registerMatch(this);
      }

      return super.expandfully(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (isMatcherFlagOn(ControlSequenceMatcher.FLAG_EXPANSION))
      {
         searchListener.registerMatch(this);
      }

      return super.expandfully(parser, stack);
   }

   public boolean isMatcherFlagOn(int flag)
   {
      if (!(searchMatcher instanceof ControlSequenceMatcher))
      {
         return true;// assume all
      }

      return ((((ControlSequenceMatcher)searchMatcher).getFlags() & flag) == flag);
   }

   public String getDescription(TeXParser parser)
   {
      return toString(parser);
   }

   public SearchMatcher getSearchMatcher()
   {
      return searchMatcher;
   }

   private SearchTeXFiles searchListener;
   private SearchMatcher searchMatcher;
}
