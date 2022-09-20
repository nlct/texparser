/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

      TeXSettings settings = parser.getSettings();

      CountRegister reg = (CountRegister)settings.getNumericRegister("@curr@toclevel@"+typeStr);

      if (reg == null && typeStr.equals("part"))
      {// use chapter register instead
         reg = (CountRegister)settings.getNumericRegister("@curr@toclevel@chapter");
      }

      StartElement startElem;
      int currLevel = -1;
      int prevLevel = -1;

      String tag;

      if (reg == null)
      {
         tag = "div";
      }
      else
      {
         tag = "li";
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
               list.add(new StartElement("ul", true));

               if (def != null)
               {
                  def.add(new EndElement("ul"));
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
      }

      startElem = new StartElement(tag, true);
      startElem.putAttribute("class", "toc-"+typeStr);

      list.add(startElem);

      startElem = new StartElement("a");
      startElem.putAttribute("href", "#"+HtmlTag.getUriFragment(link));
      list.add(startElem);

      list.add(title);
      list.add(new EndElement("a"));
      list.add(new EndElement(tag));
      list.add(new HtmlTag(String.format("<!-- end of toc-%s -->%n", typeStr)));

      return list;
   }

   @Override
   public TeXObjectList contentsline(TeXParser parser, TeXObject type,
    TeXObject title, TeXObject page)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      String typeStr = type.toString(parser);

      list.add(new HtmlTag(String.format("<div class=\"toc-%s\">",
        typeStr)));
      list.add(title);
      list.add(new HtmlTag(String.format("</div><!-- end of toc-%s -->%n", typeStr)));

      return list;
   }

}
