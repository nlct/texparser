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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class TaggedColourBox extends FrameBoxEnv
{
   public TaggedColourBox(FrameBox fbox)
   {
      this(fbox, null, null);
   }

   public TaggedColourBox(FrameBox fbox, FrameBox titleBox, TeXObject title)
   {
      super(fbox);
      this.titleBox = titleBox;
      this.defaultTitle = title;
      this.currentTitle = title;
   }

   public TaggedColourBox(String name, FrameBox fbox, FrameBox titleBox, TeXObject title)
   {
      super(name, fbox);
      this.titleBox = titleBox;
      this.defaultTitle = title;
      this.currentTitle = title;
   }

   @Override
   public Object clone()
   {
      return new TaggedColourBox(getName(), fbox, titleBox, defaultTitle);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (parser.getDebugLevel() > 0)
      {
         parser.debugMessage(1, "Processing " + toString(parser));
      }

      TeXObject options = popOptArg(parser, stack);

      KeyValList keyValList = null;

      TeXObjectList substack = parser.getListener().createStack();

      substack.add(new StartFrameBox(fbox));

      TeXObject title = currentTitle;

      if (options != null && !options.isEmpty())
      {
         keyValList = KeyValList.getList(parser, options);

         if (keyValList.containsKey("title"))
         {
            title = keyValList.get("title");
         }
      }

      if (title != null)
      {
         if (titleBox != null)
         {
            substack.add(new StartFrameBox(titleBox));
         }

         substack.add((TeXObject)title.clone(), true);

         if (titleBox != null)
         {
            substack.add(new EndFrameBox(titleBox));
         }
      }

      if (parser.getDebugLevel() > 0)
      {
         parser.debugMessage(1, "TAGGED BOX content " + substack.toString(parser));
      }

      TeXParserUtils.process(substack, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public void setTitle(TeXObject title)
   {
      currentTitle = title;
   }

   public TeXObject getTitle()
   {
      return currentTitle;
   }

   public void restoreTitle()
   {
      currentTitle = defaultTitle;
   }

   protected TeXObject defaultTitle, currentTitle;
   protected FrameBox titleBox;
}
