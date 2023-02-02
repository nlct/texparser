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

public class MarginPar extends ControlSequence
{
   public MarginPar()
   {
      this("marginpar");
   }

   public MarginPar(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new MarginPar(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject oarg = stack.popArg(parser, '[', ']');

      if (oarg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)oarg).expandfully(parser, stack);

         if (expanded != null)
         {
            oarg = expanded;
         }
      }

      TeXObject marg = stack.popArg(parser);

      if (marg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)marg).expandfully(parser, stack);

         if (expanded != null)
         {
            marg = expanded;
         }
      }

      if (oarg == null)
      {
         oarg = marg;
      }

      ((LaTeXParserListener)parser.getListener()).marginpar(oarg, marg);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject oarg = parser.popNextArg('[', ']');

      if (oarg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)oarg).expandfully(parser);

         if (expanded != null)
         {
            oarg = expanded;
         }
      }

      TeXObject marg = parser.popNextArg();

      if (marg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)marg).expandfully(parser);

         if (expanded != null)
         {
            marg = expanded;
         }
      }

      if (oarg == null)
      {
         oarg = marg;
      }

      ((LaTeXParserListener)parser.getListener()).marginpar(oarg, marg);
   }
}
