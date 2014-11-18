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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HBibItem extends BibItem
{
   public L2HBibItem()
   {
      this("bibitem");
   }

   public L2HBibItem(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HBibItem(getName());
   }

   protected void pushPostItem(TeXParser parser, TeXObjectList stack,
     TeXObject arg)
    throws IOException
   {
      if (parser == stack || stack == null)
      {
         parser.push(new HtmlTag("</div><div>"));
      }
      else
      {
         stack.push(new HtmlTag("</div><div>"));
      }

      super.pushPostItem(parser, stack, arg);
      
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (parser == stack || stack == null)
      {
         parser.push(new HtmlTag("</a>"));
      }
      else
      {
         stack.push(new HtmlTag("</a>"));
      }
   }

   protected void pushPreItem(TeXParser parser, TeXObjectList stack,
      TeXObject arg)
    throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack || stack == null)
         {
            expanded = ((Expandable)arg).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)arg).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      String label = HtmlTag.getUriFragment(arg.toString(parser));

      if (parser == stack || stack == null)
      {
         parser.push(new HtmlTag("<a name=\""+label+"\">"));
      }
      else
      {
         stack.push(new HtmlTag("<a name=\""+label+"\">"));
      }

      super.pushPreItem(parser, stack, arg);

      if (parser == stack || stack == null)
      {
         parser.push(new HtmlTag("</div><div class=\"bibitem\">"));
      }
      else
      {
         stack.push(new HtmlTag("</div><div class=\"bibitem\">"));
      }

   }
}
