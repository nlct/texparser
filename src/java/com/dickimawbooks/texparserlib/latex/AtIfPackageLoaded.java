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

public class AtIfPackageLoaded extends Command
{
   public AtIfPackageLoaded()
   {
      this("@ifpackageloaded");
   }

   public AtIfPackageLoaded(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new AtIfPackageLoaded(getName());
   }

   public boolean isLoaded(LaTeXParserListener listener, String name)
   {
      return listener.isStyLoaded(name);
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObject arg1 = parser.popNextArg();

      if (arg1 instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg1).expandfully(parser);

         if (expanded != null)
         {
            arg1 = expanded;
         }
      }

      TeXObject arg2 = parser.popNextArg();
      TeXObject arg3 = parser.popNextArg();

      TeXObjectList list = new TeXObjectList();

      if (isLoaded((LaTeXParserListener)parser.getListener(), 
                   arg1.toString(parser)))
      {
         list.add(arg2);
      }
      else
      {
         list.add(arg3);
      }

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg1 = stack.popArg(parser);

      if (arg1 instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg1).expandfully(parser, stack);

         if (expanded != null)
         {
            arg1 = expanded;
         }
      }

      TeXObject arg2 = stack.popArg(parser);
      TeXObject arg3 = stack.popArg(parser);

      TeXObjectList list = new TeXObjectList();

      if (isLoaded((LaTeXParserListener)parser.getListener(), 
                   arg1.toString(parser)))
      {
         list.add(arg2);
      }
      else
      {
         list.add(arg3);
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      TeXObject arg1 = parser.popNextArg();

      if (arg1 instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg1).expandfully(parser);

         if (expanded != null)
         {
            arg1 = expanded;
         }
      }

      TeXObject arg2 = parser.popNextArg();
      TeXObject arg3 = parser.popNextArg();

      TeXObject arg;

      if (isLoaded((LaTeXParserListener)parser.getListener(), 
                   arg1.toString(parser)))
      {
         arg = arg2;
      }
      else
      {
         arg = arg3;
      }

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser);

         if (expanded != null)
         {
            return expanded;
         }
      }

      TeXObjectList list = new TeXObjectList();

      list.add(arg);

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg1 = stack.popArg(parser);

      if (arg1 instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg1).expandfully(parser, stack);

         if (expanded != null)
         {
            arg1 = expanded;
         }
      }

      TeXObject arg2 = stack.popArg(parser);
      TeXObject arg3 = stack.popArg(parser);

      TeXObject arg;

      if (isLoaded((LaTeXParserListener)parser.getListener(), 
                   arg1.toString(parser)))
      {
         arg = arg2;
      }
      else
      {
         arg = arg3;
      }

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser, stack);

         if (expanded != null)
         {
            return expanded;
         }
      }

      TeXObjectList list = new TeXObjectList();

      list.add(arg);

      return list;
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject arg1 = parser.popNextArg();

      if (arg1 instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg1).expandfully(parser);

         if (expanded != null)
         {
            arg1 = expanded;
         }
      }

      TeXObject arg2 = parser.popNextArg();
      TeXObject arg3 = parser.popNextArg();

      if (isLoaded((LaTeXParserListener)parser.getListener(), 
                   arg1.toString(parser)))
      {
         arg2.process(parser);
      }
      else
      {
         arg3.process(parser);
      }
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject arg1 = stack.popArg(parser);

      if (arg1 instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg1).expandfully(parser, stack);

         if (expanded != null)
         {
            arg1 = expanded;
         }
      }

      TeXObject arg2 = stack.popArg(parser);
      TeXObject arg3 = stack.popArg(parser);

      if (isLoaded((LaTeXParserListener)parser.getListener(), 
                   arg1.toString(parser)))
      {
         arg2.process(parser, stack);
      }
      else
      {
         arg3.process(parser, stack);
      }
   }

}
