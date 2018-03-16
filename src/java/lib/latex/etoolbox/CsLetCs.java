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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.Let;

public class CsLetCs extends Let
{
   public CsLetCs()
   {
      this("csletcs", true, true);
   }

   public CsLetCs(String name, boolean precsname, boolean postcsname)
   {
      super(name);
      this.precsname = precsname;
      this.postcsname = postcsname;
   }

   public Object clone()
   {
      return new CsLetCs(getName(), precsname, postcsname);
   }

   protected TeXObject popArg(TeXParser parser, TeXObjectList stack, 
     boolean iscsname)
      throws IOException
   {
      TeXObject arg;

      if (parser == stack)
      {
         arg = parser.popNextArg();
      }
      else
      {
         arg = stack.popArg(parser);
      }

      if (iscsname)
      {
         if (arg instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack)
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

         arg = new TeXCsRef(arg.toString(parser));
      }

      return arg;
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject firstArg = popArg(parser, stack, precsname);
      TeXObject secondArg = popArg(parser, stack, postcsname);

      doAssignment(parser, firstArg, secondArg);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   private boolean precsname, postcsname;
}
