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
package com.dickimawbooks.texparserlib;

import java.util.Vector;
import java.io.IOException;

public class TeXCellAlign
{
   public TeXCellAlign(int align)
   {
      this(null, null, 0, 0, null, null, align, null);
   }

   public TeXCellAlign(TeXObject before, TeXObject after,
     int preRules, int postRules,
     TeXObject preShift, TeXObject postShift,
     int align, TeXDimension width)
   {
      this.before = before;
      this.after = after;
      this.preRules = preRules;
      this.postRules = postRules;
      this.preShift = preShift;
      this.postShift = postShift;
      this.align = align;
      this.width = width;
   }

   public TeXObject getBefore()
   {
      return before;
   }

   public TeXObject getAfter()
   {
      return after;
   }

   public int preRuleCount()
   {
      return preRules;
   }

   public int postRuleCount()
   {
      return postRules;
   }

   public int getAlign()
   {
      return align;
   }

   public TeXDimension getWidth()
   {
      return width;
   }

   public TeXObject getPreShift()
   {
      return preShift;
   }

   public TeXObject getPostShift()
   {
      return postShift;
   }

   public void addBefore(TeXObject obj)
   {
      if (before == null)
      {
         before = obj;
         return;
      }

      if (!(before instanceof TeXObjectList) 
        || before instanceof Group)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(before);
         before = list;
      }

      ((TeXObjectList)before).add(obj);
   }

   public void addAfter(TeXObject obj)
   {
      if (after == null)
      {
         after = obj;
         return;
      }

      if (!(after instanceof TeXObjectList) 
        || after instanceof Group)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(after);
         after = list;
      }

      ((TeXObjectList)after).add(obj);
   }

   public void addPreShift(TeXObject obj)
   {
      if (preShift == null)
      {
         preShift = obj;
         return;
      }

      if (!(preShift instanceof TeXObjectList) 
        || preShift instanceof Group)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(preShift);
         preShift = list;
      }

      ((TeXObjectList)preShift).add(obj);
   }

   public void addPostShift(TeXObject obj)
   {
      if (postShift == null)
      {
         postShift = obj;
         return;
      }

      if (!(postShift instanceof TeXObjectList) 
        || postShift instanceof Group)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(postShift);
         postShift = list;
      }

      ((TeXObjectList)postShift).add(obj);
   }

   public void addPreRule()
   {
      preRules++;
   }

   public void addPostRule()
   {
      postRules++;
   }

   public void setAlign(int align)
   {
      this.align = align;
   }

   public void setWidth(TeXDimension width)
   {
      this.width = width;
   }

   public String toString()
   {
      return String.format("%s[format=%s]", getClass().getSimpleName(),
        format());
   }

   public String format()
   {
      String preR = "";

      for (int i = 0; i < preRules; i++)
      {
         preR += "|";
      }

      String postR = "";

      for (int i = 0; i < postRules; i++)
      {
         postR += "|";
      }

      return String.format("%s%s%s%s%s%s%s", 
        preR,
        before==null?"":"@{"+before.format()+"}",
        preShift==null?"":preShift.format(),
        align==-1?"":(char)align,
        postShift==null?"":postShift.format(),
        after==null?"":"@{"+after.format()+"}",
        postR);
   }

   private TeXObject before=null;
   private TeXObject after=null;
   private TeXObject preShift = null, postShift = null;
   private int preRules=0, postRules=0;

   private int align;
   private TeXDimension width;
}

