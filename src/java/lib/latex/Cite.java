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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class Cite extends Command
{
   public Cite()
   {
      this("cite");
   }

   public Cite(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Cite(getName());
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      boolean isStar = false;

      TeXObject obj = parser.peekStack();

      if (obj instanceof CharObject)
      {
         if (((CharObject)obj).getCharCode() == (int)'*')
         {
            isStar = true;
            parser.popStack();
         }
      }

      TeXObject opt1 = parser.popNextArg('[', ']');

      TeXObject opt2 = null;

      if (opt1 != null)
      {
         opt2 = parser.popNextArg('[', ']');
      }

      TeXObject arg = parser.popNextArg();

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      CsvList csvList = CsvList.getList(parser, arg);

      TeXObjectList list = new TeXObjectList();

      addPreCite(parser, list, isStar, opt1, opt2);

      for (int i = 0, n = csvList.size(); i < n; i++)
      {
         TeXObject cite = csvList.get(i);

         addCiteSep(parser, list, isStar, i, n);

         addLinkCitation(parser, list, isStar, cite, 
            expandCitation(parser, isStar, opt1, opt2, cite));
      }

      addPostCite(parser, list, isStar, opt1, opt2);

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      boolean isStar = false;

      TeXObject obj = stack.peekStack();

      if (obj instanceof CharObject)
      {
         if (((CharObject)obj).getCharCode() == (int)'*')
         {
            isStar = true;
            stack.popStack(parser);
         }
      }

      TeXObject opt1 = stack.popArg(parser, '[', ']');

      TeXObject opt2 = null;

      if (opt1 != null)
      {
         opt2 = stack.popArg(parser, '[', ']');
      }

      TeXObject arg = stack.popArg(parser);

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser, stack);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      CsvList csvList = CsvList.getList(parser, arg);

      TeXObjectList list = new TeXObjectList();

      addPreCite(parser, list, isStar, opt1, opt2);

      for (int i = 0, n = csvList.size(); i < n; i++)
      {
         TeXObject cite = csvList.get(i);

         addCiteSep(parser, list, isStar, i, n);

         addLinkCitation(parser, list, isStar, cite, 
            expandCitation(parser, isStar, opt1, opt2, cite));
      }

      addPostCite(parser, list, isStar, opt1, opt2);

      return list;
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList expanded = expandonce(parser, stack);

      if (expanded != null)
      {
         stack.addAll(0, expanded);
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObjectList expanded = expandonce(parser);

      if (expanded != null)
      {
         parser.addAll(0, expanded);
      }
   }

   public void addPreCite(TeXParser parser, TeXObjectList list, boolean isStar,
       TeXObject opt1, TeXObject opt2)
   throws IOException
   {
      list.add(parser.getListener().getOther('['));
   }

   public void addPostCite(TeXParser parser, TeXObjectList list, boolean isStar,
      TeXObject opt1, TeXObject opt2)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      if (opt1 != null)
      {
         list.add(listener.getOther(','));
         list.add(listener.getSpace());
         list.add(opt1);
      }

      list.add(listener.getOther(']'));
   }

   public void addCiteSep(TeXParser parser, TeXObjectList list, boolean isStar,
       int index, int numCites)
    throws IOException
   {
      if (index > 0)
      {
         list.add(parser.getListener().getOther(','));
         list.add(parser.getListener().getSpace());
      }
   }


   public TeXObject expandCitation(TeXParser parser, boolean isStar,
       TeXObject opt1, TeXObject opt2, TeXObject arg)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject cite = listener.getCitation(arg);

      return cite == null ? listener.createUnknownReference(arg) : cite;
   }

   public void addLinkCitation(TeXParser parser, TeXObjectList list,
      boolean isStar, TeXObject label, TeXObject cite)
    throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (cite instanceof UnknownReference || !listener.isStyLoaded("hyperref"))
      {
         list.add(cite);
         return;
      }

      list.add(new TeXCsRef("hyperlink"));

      if (label instanceof Group)
      {
         list.add(label);
      }
      else
      {
         Group grp = listener.createGroup();

         if (label instanceof TeXObjectList)
         {
            grp.addAll((TeXObjectList)label);
         }
         else
         {
            grp.add(label);
         }

         list.add(grp);
      }

      if (cite instanceof Group)
      {
         list.add(cite);
      }
      else
      {
         Group grp = listener.createGroup();

         if (cite instanceof TeXObjectList)
         {
            grp.addAll((TeXObjectList)cite);
         }
         else
         {
            grp.add(cite);
         }

         list.add(grp);
      }
   }

}
