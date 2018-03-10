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

import com.dickimawbooks.texparserlib.*;

public class UsePackage extends ControlSequence
{
   public UsePackage()
   {
      this("usepackage", false);
   }

   public UsePackage(String name, boolean loadParentOptions)
   {
      super(name);
      this.loadParentOptions = loadParentOptions;
   }

   public Object clone()
   {
      return new UsePackage(getName(), loadParentOptions);
   }

   public void preProcess(TeXParser parser, TeXObjectList stack, 
      KeyValList keyvalList, String styNameList)
   throws IOException
   {
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject options;
      TeXObject sty;

      if (parser == stack)
      {
         options = parser.popNextArg('[', ']');
         sty = parser.popNextArg();
      }
      else
      {
         options = stack.popArg(parser, '[', ']');
         sty = stack.popArg(parser);
      }

      TeXObjectList expanded = null;

      if (sty instanceof Expandable)
      {
         if (parser == stack)
         {
            expanded = ((Expandable)sty).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)sty).expandfully(parser, stack);
         }
      }

      String styNameList;

      if (expanded == null)
      {
         styNameList = sty.toString(parser);
      }
      else
      {
         styNameList = expanded.toString(parser);
      }

      TeXObject version;

      if (parser == stack)
      {
         version = parser.popNextArg('[', ']');
      }
      else
      {
         version = stack.popArg(parser, '[', ']');
      }

      KeyValList keyValList = null;

      if (options != null)
      {
         keyValList = KeyValList.getList(parser, options);
      }

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      preProcess(parser, stack, keyValList, styNameList);

      String[] split = styNameList.split(",");

      for (int i = 0; i < split.length; i++)
      {
         String styName = split[i].trim();

         if (styName.equals("mnsymbol"))
         {
            styName = "MnSymbol";
         }

         listener.usepackage(keyValList, split[i].trim(), loadParentOptions);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected boolean loadParentOptions = false;
}
