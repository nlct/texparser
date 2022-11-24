/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
      parser.setCatCode(true, 9, TeXParser.TYPE_IGNORE);
      parser.setCatCode(true, 32, TeXParser.TYPE_IGNORE);
      parser.setCatCode(true, 34, TeXParser.TYPE_OTHER);
      parser.setCatCode(true, 38, TeXParser.TYPE_TAB);
      parser.setCatCode(true, 58, TeXParser.TYPE_LETTER);
      parser.setCatCode(true, 94, TeXParser.TYPE_SP);
      parser.setCatCode(true, 95, TeXParser.TYPE_LETTER);
      parser.setCatCode(true, 124, TeXParser.TYPE_OTHER);
      parser.setCatCode(true, 126, TeXParser.TYPE_SPACE);
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
        new UserNumber(parser.getCatCode(9))));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(32)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCatCode(32))));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(34)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCatCode(34))));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(38)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCatCode(38))));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(58)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCatCode(58))));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(94)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCatCode(94))));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(95)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCatCode(95))));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(124)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCatCode(124))));
  
      def.add(new TeXCsRef("char_set_catcode:nn"));
      def.add(TeXParserUtils.createGroup(listener, new UserNumber(126)));
      def.add(TeXParserUtils.createGroup(listener, 
        new UserNumber(parser.getCatCode(126))));
  
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
