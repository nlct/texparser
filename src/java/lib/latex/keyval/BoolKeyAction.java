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
package com.dickimawbooks.texparserlib.latex.keyval;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class BoolKeyAction extends ControlSequence
{
   public BoolKeyAction(String name, String boolName, TeXObject success,
     TeXObject failure)
   {
      super(name);
   }

   public Object clone()
   {
      return new BoolKeyAction(getName(), boolName, success, failure);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject valueArg;

      if (parser == stack || stack == null)
      {
         valueArg = parser.popNextArg();
      }
      else
      {
         valueArg = stack.popArg(parser);
      }

      if (valueArg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack || stack == null)
         {
            valueArg = ((Expandable)valueArg).expandfully(parser);
         }
         else
         {
            valueArg = ((Expandable)valueArg).expandfully(parser, stack);
         }
      }

      String value = valueArg.toString(parser).trim().toLowerCase();

      if (value.equals("true") || value.equals("false"))
      {
         if (stack == null)
         {
            parser.push((TeXObject)success.clone());
            parser.push(new TeXCsRef(boolName+value));
         }
         else
         {
            stack.push((TeXObject)success.clone());
            stack.push(new TeXCsRef(boolName+value));
         }
      }
      else if (failure != null)
      {
         if (stack == null)
         {
            parser.push((TeXObject)failure.clone());
         }
         else
         {
            stack.push((TeXObject)failure.clone());
         }
      }
      else
      {// TODO
      }
   }

   private String boolName;
   private TeXObject success, failure;
}
