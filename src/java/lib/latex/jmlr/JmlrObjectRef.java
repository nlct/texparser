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
package com.dickimawbooks.texparserlib.latex.jmlr;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class JmlrObjectRef extends Command
{
   public JmlrObjectRef()
   {
      this("objectref");
   }

   public JmlrObjectRef(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new JmlrObjectRef(getName());
   }

   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject labels = (stack == parser ?
          parser.popNextArg() :
          stack.popArg(parser));

      TeXObject singulartag = (stack == parser ?
          parser.popNextArg() :
          stack.popArg(parser));

      TeXObject pluraltag = (stack == parser ?
          parser.popNextArg() :
          stack.popArg(parser));

      TeXObject pre = (stack == parser ?
          parser.popNextArg() :
          stack.popArg(parser));

      TeXObject post = (stack == parser ?
          parser.popNextArg() :
          stack.popArg(parser));

      return expand(parser, labels, singulartag, pluraltag, pre, post);
    }

   protected void expandLabel(TeXParser parser, TeXObjectList expanded,
      TeXObject label)
       throws IOException
   {
      TeXParserListener listener = parser.getListener();

      expanded.add(new TeXCsRef("ref"));

      if (label instanceof Group || !(label instanceof TeXObjectList))
      {
         expanded.add(label);
      }
      else
      {
         Group grp = listener.createGroup();

         grp.addAll((TeXObjectList)label);
         expanded.add(grp);
      }
   }

    protected TeXObjectList expand(TeXParser parser, TeXObject labels,
     TeXObject singulartag, TeXObject pluraltag, TeXObject pre, TeXObject post)
       throws IOException
    {
      TeXParserListener listener = parser.getListener();
      TeXObjectList expanded = new TeXObjectList();

      CsvList csvList = CsvList.getList(parser, labels);

      int n = csvList.size();

      if (singulartag != null && pluraltag != null)
      {
         expanded.add(n == 1 ? singulartag : pluraltag);
         expanded.add(listener.getActiveChar('~'));
      }

      expanded.add(pre);

      for (int i = 0; i < n; i++)
      {
         TeXObject label = csvList.get(i);

         if (i > 0)
         {
            if (i == n-1)
            {
               expanded.add(new TeXCsRef("@jmlr@reflistlastsep"));
            }
            else
            {
               expanded.add(new TeXCsRef("@jmlr@reflistsep"));
            }
         }

         expandLabel(parser, expanded, label);
      }

      expanded.add(post);

      return expanded;
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList expanded = expandonce(parser, stack);

      stack.addAll(0, expanded);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }
}
