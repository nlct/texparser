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
package com.dickimawbooks.texparserlib.latex.graphics;

import java.util.Hashtable;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GraphicsSty extends LaTeXSty
{
   public GraphicsSty(KeyValList options, 
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      this(options, "graphics", listener, loadParentOptions);
   }

   public GraphicsSty(KeyValList options, String name, 
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      super(options, name, listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      registerControlSequence(new IncludeGraphics(this));
      registerControlSequence(new RotateBox());
      registerControlSequence(new ScaleBox());
      registerControlSequence(new ReflectBox());
      registerControlSequence(new ResizeBox());
      registerControlSequence(new GraphicsPath());
      registerControlSequence(new Epsfig("epsfig"));
      registerControlSequence(new Epsfig("psfig"));
   }

   public static double getDouble(TeXObject object, TeXParser parser)
      throws TeXSyntaxException
   {
      String string = object.toString(parser);

      try
      {
         return Double.valueOf(string);
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(parser,
          TeXSyntaxException.ERROR_NUMBER_EXPECTED, string);
      }
   }

   public static TeXDimension getDimension(TeXObject object, TeXParser parser)
      throws IOException
   {
      if (object instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)object).expandfully(parser);

         if (expanded != null)
         {
            object = expanded;
         }
      }

      if (object instanceof TeXDimension)
      {
         return (TeXDimension)object;
      }
      else if (object instanceof TeXObjectList)
      {
         return ((TeXObjectList)object).popDimension(parser);
      }
      else
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_DIMEN_EXPECTED, object.toString(parser));
      }
   }

}
