/*
    Copyright (C) 2013-2024 Nicola L.C. Talbot
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
import com.dickimawbooks.texparserlib.auxfile.CiteInfo;

public class Cite extends ControlSequence
{
   public Cite()
   {
      this("cite");
   }

   public Cite(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new Cite(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      boolean isStar = (popModifier(parser, stack, '*') == '*');

      TeXObject opt1 = popOptArg(parser, stack);

      TeXObject opt2 = null;

      if (opt1 != null)
      {
         opt2 = popOptArg(parser, stack);
      }

      TeXObject arg = popArgExpandFully(parser, stack);

      CsvList csvList = CsvList.getList(parser, arg);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObjectList list = new TeXObjectList();

      addPreCite(parser, list, isStar, opt1, opt2);

      for (int i = 0, n = csvList.size(); i < n; i++)
      {
         TeXObject cite = csvList.get(i);
         String label = cite.toString(parser);

         addCiteSep(parser, list, isStar, i, n);

         CiteInfo info = listener.getCiteInfo(label);

         if (info == null)
         {
            addLinkCitation(parser, list, isStar, cite, 
               expandCitation(parser, isStar, opt1, opt2, cite));
         }
         else
         {
            addLinkCitation(parser, list, isStar, info, 
               expandCitation(parser, isStar, opt1, opt2, info));
         }
      }

      addPostCite(parser, list, isStar, opt1, opt2);

      TeXParserUtils.process(list, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
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
       TeXObject opt1, TeXObject opt2, CiteInfo info)
   throws IOException
   {
      return (TeXObject)info.getReference().clone();
   }

   public TeXObject expandCitation(TeXParser parser, boolean isStar,
       TeXObject opt1, TeXObject opt2, TeXObject arg)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String label = arg.toString(parser);

      TeXObject cite = listener.getCitation(label);

      return cite == null ? listener.createUnknownReference(label) : cite;
   }

   public void addLinkCitation(TeXParser parser, TeXObjectList list,
      boolean isStar, CiteInfo info, TeXObject citeText)
    throws IOException
   {
      list.add(parser.getListener().createLink(info, citeText));
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
