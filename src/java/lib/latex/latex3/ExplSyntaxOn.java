/*
    Copyright (C) 2022-2023 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.latex3;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.Relax;

public class ExplSyntaxOn extends ControlSequence
  implements CatCodeChanger
{
   public ExplSyntaxOn()
   {
      this("ExplSyntaxOn");
   }

   public ExplSyntaxOn(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new ExplSyntaxOn(getName());
   }

   @Override
   public void applyCatCodeChange(TeXParser parser) throws IOException
   {
      parser.setCategoryCode(true, 9, CategoryCode.IGNORE);
      parser.setCategoryCode(true, 32, CategoryCode.IGNORE);
      parser.setCategoryCode(true, 34, CategoryCode.OTHER);
      parser.setCategoryCode(true, 38, CategoryCode.TAB);
      parser.setCategoryCode(true, 58, CategoryCode.LETTER);
      parser.setCategoryCode(true, 94, CategoryCode.SP);
      parser.setCategoryCode(true, 95, CategoryCode.LETTER);
      parser.setCategoryCode(true, 124, CategoryCode.OTHER);
      parser.setCategoryCode(true, 126, CategoryCode.SPACE);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList def = listener.createStack();

      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(9)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCategoryCode(9).getId())));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(32)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCategoryCode(32).getId())));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(34)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCategoryCode(34).getId())));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(38)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCategoryCode(38).getId())));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(58)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCategoryCode(58).getId())));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(94)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCategoryCode(94).getId())));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(95)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCategoryCode(95).getId())));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(124)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCategoryCode(124).getId())));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(126)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCategoryCode(126).getId())));
  
      parser.putControlSequence(true, new GenericCommand(true,
       "ExplSyntaxOff", null, def));

      applyCatCodeChange(parser);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   @Override
   public ControlSequence getNoOpCommand()
   {
      return new Relax(getName());
   }
}
