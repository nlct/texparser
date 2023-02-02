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

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GraphicsPath extends ControlSequence
{
   public GraphicsPath()
   {
      this("graphicspath");
   }

   public GraphicsPath(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GraphicsPath(getName());
   }

   private void processPath(TeXParser parser, TeXObject arg)
     throws IOException
   {
      TeXObjectList paths = new TeXObjectList();

      if (arg instanceof Group)
      {
         paths.add(((Group)arg).toList());
      }
      else if (arg instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)arg; 

         while (list.size() > 0)
         {
            TeXObject thisPath = list.popArg(parser);

            paths.add(thisPath);
         }
      }
      else
      {
         paths.add(arg);
      }

      ((LaTeXParserListener)parser.getListener()).setGraphicsPath(paths);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      processPath(parser, stack.popArg(parser));

   }

   public void process(TeXParser parser)
     throws IOException
   {
      processPath(parser, parser.popNextArg());
   }
}
