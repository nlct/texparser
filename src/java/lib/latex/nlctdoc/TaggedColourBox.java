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
   }

   public TaggedColourBox(String name, FrameBox fbox, FrameBox titleBox, TeXObject title)
   {
      super(name, fbox);
      this.titleBox = titleBox;
      this.defaultTitle = title;
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
      TeXObject options = popOptArg(parser, stack);

      KeyValList keyValList = null;

      TeXObject title = defaultTitle;

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
            stack.push(new EndFrameBox(titleBox));
         }

         stack.push((TeXObject)title.clone(), true);

         if (titleBox != null)
         {
            stack.push(new StartFrameBox(titleBox));
         }
      }

      stack.push(new StartFrameBox(fbox));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public void setTitle(TeXObject title)
   {
      defaultTitle = title;
   }

   public TeXObject getTitle()
   {
      return defaultTitle;
   }

   protected TeXObject defaultTitle;
   protected FrameBox titleBox;
}
