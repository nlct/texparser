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

public class Appendix extends ControlSequence
{
   public Appendix()
   {
      this("appendix");
   }

   public Appendix(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Appendix(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      Register reg = parser.getSettings().getRegister("c@chapter");
      String counter;

      if (reg == null)
      {
         counter = "section";
      }
      else
      {
         counter = "chapter";

         ControlSequence cs = parser.getControlSequence("@chapapp");

         if (cs instanceof GenericCommand)
         {
            TeXObjectList definition = ((GenericCommand)cs).getDefinition();
            definition.clear();
            definition.add(new TeXCsRef("appendixname"));
         }
         else
         {
            parser.putControlSequence(true, new GenericCommand(true, 
              "@chapapp", null, 
               new TeXObject[] {new TeXCsRef("appendixname")}));
         }
      }

      listener.resetcounter(counter);

      String thectr = "the"+counter;

      ControlSequence cs = parser.getControlSequence(thectr);

      if (cs instanceof GenericCommand)
      {
         TeXObjectList definition = ((GenericCommand)cs).getDefinition();
         definition.clear();
         definition.add(new TeXCsRef("@Alph"));
         definition.add(new TeXCsRef("c@"+counter));
      }
      else
      {
         parser.putControlSequence(true, new GenericCommand(true, 
           thectr, null, new TeXObject[] {new TeXCsRef("@Alph"),
           new TeXCsRef("c@"+counter)}));
      }
   }

}
