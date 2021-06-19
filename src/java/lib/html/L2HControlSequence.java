/*
    Copyright (C) 2020 Nicola L.C. Talbot
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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HControlSequence extends AssignedControlSequence
{
   public L2HControlSequence(ControlSequence cs)
   {
      super(cs.getName(), cs);
   }

   protected L2HControlSequence(String name, TeXObject object)
   {
      super(name, object);
   }

   @Override
   public Object clone()
   {
      return new L2HControlSequence(getName(), getUnderlying());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      return listener.inMathJaxMode() ? null : super.expandonce(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      return listener.inMathJaxMode() ? null : super.expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      return listener.inMathJaxMode() ? null : super.expandfully(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      return listener.inMathJaxMode() ? null : super.expandfully(parser, stack);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject base = getBaseUnderlying();
      Declaration dec = null;

      if (base instanceof EndDeclaration)
      {
         dec = ((EndDeclaration)base).getDeclaration(parser);

         if (dec instanceof MathDeclaration)
         {
            dec.end(parser);
            return;
         }
      }

      if (listener.inMathJaxMode())
      {
         if (base instanceof End)
         {
            String name = parser.popRequiredString(stack);

            TeXObject cs = parser.resolveReference(
               listener.getControlSequence(name));

            if (cs instanceof MathDeclaration)
            {
               ((MathDeclaration)cs).end(parser);
            }
            else
            {
               listener.write(String.format("\\end{%s}", name));
            }

            return;
         }

         listener.write(toString(parser));

         if (isControlWord(parser))
         {
            listener.write(' ');
         }
      }
      else if (dec != null)
      {
         dec.end(parser);
      }
      else
      {
         parser.processObject(base, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

}
