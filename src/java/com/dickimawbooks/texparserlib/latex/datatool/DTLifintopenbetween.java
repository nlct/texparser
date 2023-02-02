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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLifintopenbetween extends ControlSequence
{
   public DTLifintopenbetween()
   {
      this("dtlifintopenbetween");
   }

   public DTLifintopenbetween(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new DTLifintopenbetween(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject numObj = stack.popArg(parser);

      if (numObj instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)numObj).expandfully(parser,
            stack);

         if (expanded != null)
         {
            numObj = expanded;
         }
      }

      TeXObject minObj = stack.popArg(parser);

      if (minObj instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)minObj).expandfully(parser,
            stack);

         if (expanded != null)
         {
            minObj = expanded;
         }
      }

      TeXObject maxObj = stack.popArg(parser);

      if (maxObj instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)maxObj).expandfully(parser,
            stack);

         if (expanded != null)
         {
            maxObj = expanded;
         }
      }

      TeXObject trueArg = stack.popArg(parser);
      TeXObject falseArg = stack.popArg(parser);

      try
      {
         int num = Integer.parseInt(numObj.toString(parser));
         int min = Integer.parseInt(minObj.toString(parser));
         int max = Integer.parseInt(maxObj.toString(parser));

         if (min < num && num < max)
         {
            trueArg.process(parser, stack);
         }
         else
         {
            falseArg.process(parser, stack);
         }
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(e, parser, 
          TeXSyntaxException.ERROR_NUMBER_EXPECTED);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject numObj = parser.popNextArg();

      if (numObj instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)numObj).expandfully(parser);

         if (expanded != null)
         {
            numObj = expanded;
         }
      }

      TeXObject minObj = parser.popNextArg();

      if (minObj instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)minObj).expandfully(parser);

         if (expanded != null)
         {
            minObj = expanded;
         }
      }

      TeXObject maxObj = parser.popNextArg();

      if (maxObj instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)maxObj).expandfully(parser);

         if (expanded != null)
         {
            maxObj = expanded;
         }
      }

      TeXObject trueArg = parser.popNextArg();
      TeXObject falseArg = parser.popNextArg();

      try
      {
         int num = Integer.parseInt(numObj.toString(parser));
         int min = Integer.parseInt(minObj.toString(parser));
         int max = Integer.parseInt(maxObj.toString(parser));

         if (min < num && num < max)
         {
            trueArg.process(parser);
         }
         else
         {
            falseArg.process(parser);
         }
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(e, parser, 
          TeXSyntaxException.ERROR_NUMBER_EXPECTED);
      }
   }

}
