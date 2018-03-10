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
import java.io.File;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HTableOfContents extends ControlSequence
{
   public L2HTableOfContents()
   {
      this("tableofcontents");
   }

   public L2HTableOfContents(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HTableOfContents(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject cs = parser.getControlSequence("chapter");

      if (cs == null || cs instanceof Undefined)
      {
         cs = listener.getControlSequence("section");
      }

      stack.push(new HtmlTag("</div>"));

      File tocFile = listener.getAuxFile("toc");

      if (tocFile.exists())
      {
         stack.push(listener.createGroup(tocFile.getName()));
         stack.push(listener.getControlSequence("input"));
      }

      stack.push(listener.createGroup("toc"));
      stack.push(new TeXCsRef("label"));
      stack.push(new TeXCsRef("contentsname"));
      stack.push(listener.getOther('*'));
      stack.push(cs);
      stack.push(new HtmlTag("<div class=\"toc\">"));
   }

   public void process(TeXParser parser)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject cs = parser.getControlSequence("chapter");

      if (cs == null || cs instanceof Undefined)
      {
         cs = listener.getControlSequence("section");
      }

      parser.push(new HtmlTag("</div>"));

      File tocFile = listener.getAuxFile("toc");

      if (tocFile.exists())
      {
         parser.push(listener.createGroup(tocFile.getName()));
         parser.push(listener.getControlSequence("input"));
      }

      parser.push(listener.createGroup("toc"));
      parser.push(new TeXCsRef("label"));
      parser.push(new TeXCsRef("contentsname"));
      parser.push(listener.getOther('*'));
      parser.push(cs);
      parser.push(new HtmlTag("<div class=\"toc\">"));
   }
}
