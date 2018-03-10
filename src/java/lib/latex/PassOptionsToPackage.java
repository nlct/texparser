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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class PassOptionsToPackage extends ControlSequence
{
   public PassOptionsToPackage()
   {
      this("PassOptionsToPackage", "sty");
   }

   public PassOptionsToPackage(String name, String extension)
   {
      super(name);
      this.ext = extension;
   }

   public Object clone()
   {
      return new PassOptionsToPackage(getName(), getExtension());
   }

   public String getExtension()
   {
      return ext;
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject options;
      TeXObject sty;

      if (parser == stack)
      {
         options = parser.popNextArg();

         if (options instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)options).expandfully(parser);

            if (expanded != null)
            {
               options = expanded;
            }
         }

         sty = parser.popNextArg();

         if (sty instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)sty).expandfully(parser);

            if (expanded != null)
            {
               sty = expanded;
            }
         }

      }
      else
      {
         options = stack.popArg(parser);

         if (options instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)options).expandfully(parser,
               stack);

            if (expanded != null)
            {
               options = expanded;
            }
         }

         sty = stack.popArg(parser);

         if (sty instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)sty).expandfully(parser,
              stack);

            if (expanded != null)
            {
               sty = expanded;
            }
         }

      }

      passOptions((LaTeXParserListener)parser.getListener(), 
         sty.toString(parser), KeyValList.getList(parser, options));
   }

   protected void passOptions(LaTeXParserListener listener,
      String name, KeyValList options)
   {
      listener.passOptionsTo(String.format("%s.%s", name, ext), options);
   }

   private String ext;
}
