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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.html.*;

public class GlsTableSubEntries extends GatherEnvContents
{
   public GlsTableSubEntries()
   {
      this("glstablesubentries");
   }

   public GlsTableSubEntries(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new GlsTableSubEntries(getName());
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXParserListener listener = parser.getListener();
      TeXObjectList contents = popContents(parser, stack);

      if (listener instanceof L2HConverter)
      {
         TeXObjectList list = contents;
         contents = listener.createStack();

         StartElement startElem = new StartElement("div", true);
         startElem.putAttribute("class", "glossary-children");
         contents.add(startElem);

         while (!list.isEmpty())
         {
            TeXObject obj = list.pop();

            if (obj instanceof ControlSequence
                && ((ControlSequence)obj).getName().equals("glstableblocksubentrysep"))
            {
               obj = TeXParserUtils.expandOnce(obj, parser, stack);

               if (obj instanceof TeXObjectList 
                   && ((TeXObjectList)obj).size() == 1)
               {
                  obj = ((TeXObjectList)obj).firstElement();
               }

               if (obj instanceof ControlSequence)
               {
                  String name = ((ControlSequence)obj).getName();

                  if (name.equals("tabularnewline") || name.equals("\\")
                      || name.equals("cr"))
                  {
                     obj = new HtmlTag("<br>");
                  }
               }
            }

            contents.add(obj);
         }

         contents.add(new EndElement("div", true));
      }
      else
      {
         contents.push(listener.createGroup("l"));
         contents.push(listener.getOther('['));
         contents.push(listener.createGroup("t"));
         contents.push(listener.getOther(']'));
         contents.push(listener.createGroup("tabular"));
         contents.push(listener.getControlSequence("begin"));

         contents.add(listener.getControlSequence("end"));
         contents.push(listener.createGroup("tabular"));
      }

      TeXParserUtils.process(contents, parser, stack);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

   public boolean isInLine()
   {
      return true;
   }
}
