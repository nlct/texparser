/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HEqnarray extends L2HMathDeclaration
{
   public L2HEqnarray()
   {
      super("eqnarray", TeXSettings.MODE_DISPLAY_MATH, true);
   }

   public L2HEqnarray(String name, boolean numbered)
   {
      super(name, TeXSettings.MODE_DISPLAY_MATH, numbered);
   }

   @Override
   public Object clone()
   {
      return new L2HEqnarray(getName(), isNumbered());
   }

   protected void startTabular(TeXParser parser)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      listener.putControlSequence(new L2HMathAlignNewline(isNumbered()));

      listener.writeln("<table>");

      TeXSettings settings = parser.getSettings();

      settings.setAlignmentList(listener.createTeXCellAlignList(
         listener.createString("rcl")));

      settings.startAlignment();
   }   

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      parser.startGroup();

      doModeSwitch(parser);

      listener.htmlcomment("start "+getName());
      listener.write("<div class=\"displaymath\">");

      startTabular(parser);
System.out.println("MARK(1)");

      AlignRow row = listener.createMathAlignRow(stack, isNumbered());
System.out.println("ROW: "+row.toString(parser));
System.out.println("STACK: "+stack);

      parser.processObject(row, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void end(TeXParser parser) throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      listener.writeln("</table>");

      listener.writeln("</div>");
      listener.htmlcomment("end "+getName());

      revertModeSwitch(parser);

      parser.endGroup();
   }
}
