/*
    Copyright (C) 2025 Nicola L.C. Talbot
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

package com.dickimawbooks.texparserlib.latex.flowfram;

import java.io.IOException;
import java.io.File;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.KeyValList;
import com.dickimawbooks.texparserlib.html.L2HConverter;

public class IncludeTeXImage extends ControlSequence
{
   public IncludeTeXImage()
   {
      this("includeteximage", null);
   }

   public IncludeTeXImage(String name, String cssClass)
   {
      super(name);
      this.cssClass = cssClass;
   }

   @Override
   public Object clone()
   {
      return new IncludeTeXImage(getName(), cssClass);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      KeyValList options = TeXParserUtils.popOptKeyValList(parser, stack);
      String pathStr = popLabelString(parser, stack);

      TeXObjectList expanded = listener.createStack();

      if (listener instanceof L2HConverter)
      {
         TeXPath texPath = new TeXPath(parser, pathStr, "png", "jpg", "jpeg", "pdf");

         File file = texPath.getFile();

         if (file.exists())
         {
            expanded.add(listener.getControlSequence("includegraphics"));

            if (cssClass != null)
            {
               options = new KeyValList();
               options.put("class", listener.createString(cssClass));
            }

            if (options != null && !options.isEmpty())
            {
               expanded.add(listener.getOther('['));
               expanded.add(options);
               expanded.add(listener.getOther(']'));
            }

            expanded.add(TeXParserUtils.createGroup(listener, new TeXPathObject(texPath)));
         }
         else
         {
            int idx = pathStr.lastIndexOf("/");
            String name = pathStr;

            if (idx > -1)
            {
               name = pathStr.substring(idx);
            }

            idx = name.lastIndexOf(".");

            if (idx > -1)
            {
               name = name.substring(0, idx);
            }

            String optionStr = null;

            if (options == null)
            {
               options = new KeyValList();
            }
            else
            {
               optionStr = options.toString(parser);
            }

            options.put("name", listener.createString(name));

            expanded.add(listener.getControlSequence("TeXParserLibToImage"));

            expanded.add(listener.getOther('['));
            expanded.add(options);
            expanded.add(listener.getOther(']'));

            Group grp = listener.createGroup();
            expanded.add(grp);

            grp.add(listener.getControlSequence("includeteximage"));

            if (optionStr != null)
            {
               expanded.add(listener.getOther('['));
               expanded.add(listener.createString(optionStr));
               expanded.add(listener.getOther(']'));
            }

            grp.add(listener.createGroup(pathStr));
         }
      }
      else
      {
         expanded.add(listener.getControlSequence("input"));

         expanded.add(listener.createGroup(pathStr));
      }

      TeXParserUtils.process(expanded, parser, stack);
   }

   String cssClass;
}
