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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.auxfile.LabelInfo;

public class L2HContentsLine extends ContentsLine
{
   public L2HContentsLine()
   {
      this("contentsline");
   }

   public L2HContentsLine(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2HContentsLine(getName());
   }

   @Override
   public TeXObjectList contentsline(TeXParser parser, TeXObject type,
    TeXObject title, TeXObject page, String link)
      throws IOException
   {
      if (link == null || link.isEmpty())
      {
         return contentsline(parser, type, title, page);
      }

      String typeStr = type.toString(parser);
      TeXObjectList list = new TeXObjectList();

      L2HConverter listener = (L2HConverter)parser.getListener();
      TeXSettings settings = parser.getSettings();

      CountRegister reg = (CountRegister)settings.getNumericRegister("@curr@toclevel@"+typeStr);

      if (reg == null && typeStr.equals("part"))
      {// use chapter register instead
         reg = (CountRegister)settings.getNumericRegister("@curr@toclevel@chapter");
      }

      L2HItem startItem;
      TeXObject endElem = null;

      int currLevel = -1;
      int prevLevel = -1;

      if (reg == null)
      {
         StartElement startElem = new StartElement("div");
         startElem.putAttribute("class", "toc-"+typeStr);
         list.add(startElem);

         endElem = new EndElement("div");
      }
      else
      {
         currLevel = reg.number(parser);
         reg = (CountRegister)settings.getNumericRegister("@curr@toclevel");
         prevLevel = reg.number(parser);
         reg.setValue(currLevel);

         ControlSequence cs = parser.getControlSequence("@toc@endtags");

         TeXObjectList def=null;

         if (cs instanceof GenericCommand)
         {
            def = ((GenericCommand)cs).getDefinition();
         }

         if (currLevel > prevLevel)
         {
            for (int i = prevLevel; i < currLevel; i++)
            {
               L2HList listDec = new L2HList();
               list.add(listDec);

               if (def != null)
               {
                  def.add(new EndDeclaration(listDec));
               }
            }
         }
         else if (currLevel < prevLevel)
         {
            for (int i = currLevel; i < prevLevel; i++)
            {
               if (def != null)
               {
                  int n = def.size();

                  if (n >= 0)
                  {
                     list.add(def.remove(n-1));
                  }
               }
            }
         }

         startItem = new L2HItem();
         startItem.putAttribute("class", "toc-"+typeStr);

         list.add(startItem);
      }

      LabelInfo info = listener.getLabelInfo(link);

      if (info == null)
      {
         String label = listener.getStringLabelForLink(link);

         if (label != null)
         {
            info = listener.getLabelInfo(label);
         }
      }

      if (info == null)
      {
         StartElement startElem = new StartElement("a");
         startElem.putAttribute("href", "#"+HtmlTag.getUriFragment(link));
         list.add(startElem);

         list.add(title);
         list.add(new EndElement("a"));
      }
      else
      {
         list.add(listener.createLink(info, title), true);
      }

      if (endElem != null)
      {
         list.add(endElem);
         list.add(new HtmlLiteral(String.format("<!-- end of toc-%s -->%n", typeStr)));
      }

      return list;
   }

   @Override
   public TeXObjectList contentsline(TeXParser parser, TeXObject type,
    TeXObject title, TeXObject page)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      String typeStr = type.toString(parser);

      StartElement startElem = new StartElement("div");

      startElem.putAttribute("class", "toc-"+typeStr);

      list.add(startElem);
      list.add(title);
      list.add(new EndElement("div"));
      list.add(new HtmlLiteral(String.format("<!-- end of toc-%s -->%n", typeStr)));

      return list;
   }

}
