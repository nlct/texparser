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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.BeginGroup;
import com.dickimawbooks.texparserlib.primitives.EndGroup;

public class BlockElement extends AbstractGroup
{
   public BlockElement(String type)
   {
      super();
      block = new DocumentBlock(type);
   }

   public BlockElement(String type, int displayStyle)
   {
      super();
      block = new DocumentBlock(type, displayStyle);
   }

   public BlockElement(int capacity, String type, int displayStyle)
   {
      super(capacity);
      block = new DocumentBlock(type, displayStyle);
   }

   public BlockElement(String type, int displayStyle, TeXObject content)
   {
      super();
      block = new DocumentBlock(type, displayStyle);

      if (content instanceof TeXObjectList)
      {
         addAll((TeXObjectList)content);
      }
      else
      {
         add(content);
      }
   }

   @Override
   public AbstractTeXObjectList createList()
   {
      return new BlockElement(size(), getType(), getDisplayStyle());
   }

   @Override
   public TeXObjectList deconstruct(TeXParser parser) throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      list.add(new InternalDocumentBlockCs(block));
      Group grp = parser.getListener().createGroup();
      list.add(grp);

      grp.addAll(this);

      return list;
   }

   @Override
   public TeXObjectList toList()
   {
      TeXObjectList list = new TeXObjectList(capacity());
      list.addAll(this);
      return list;
   }

   @Override
   public boolean isPar()
   {
      return false;
   }

   @Override
   public boolean isEmptyObject()
   {
      return false;
   }

   public String getType()
   {
      return block.getType();
   }

   public int getDisplayStyle()
   {
      return block.getDisplayStyle();
   }

   public void setAttribute(String key, Object value)
   {
      block.setAttribute(key, value);
   }

   public Object getAttribute(String key)
   {
      return block.getAttribute(key);
   }

   @Override
   public Object clone()
   {
      BlockElement element = new BlockElement(block.getType(), 
        block.getDisplayStyle());

      for (Iterator<String> it = block.getKeySet().iterator(); it.hasNext();)
      {
         String key = it.next();
         Object value = block.getAttribute(key);

         if (value instanceof TeXObject)
         {
            block.setAttribute(key, ((TeXObject)value).clone());
         }
         else
         {
            block.setAttribute(key, value);
         }
      }

      for (TeXObject obj : this)
      {
         element.add((TeXObject)obj.clone());
      }

      return element;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList(capacity());

      InternalDocumentBlockCs cs = new InternalDocumentBlockCs(block);

      parser.putControlSequence(true, cs);

      list.add(cs);
      Group grp = parser.getListener().createGroup();
      list.add(grp);

      grp.addAll(this);

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   @Override
   public void startGroup(TeXParser parser)
      throws IOException
   {
      ((LaTeXParserListener)parser.getListener()).startBlock(block);
   }

   @Override
   public void endGroup(TeXParser parser)
      throws IOException
   {
      ((LaTeXParserListener)parser.getListener()).endBlock(block);
   }

   public BeginGroupObject getBegin(TeXParser parser)
   {
      return new BeginBlockElement(getType(), getDisplayStyle());
   }

   public EndGroupObject getEnd(TeXParser parser)
   {
      return new EndBlockElement();
   }


   @Override
   public String toString()
   {
      return String.format("%s[block=%s,content=%s]", 
        getClass().getSimpleName(), block, super.toString());
   }

   private DocumentBlock block;
}

class BeginBlockElement extends BeginGroup
{
   public BeginBlockElement(String type, int displayStyle)
   {
      this("blockelement@internal@begingroup", type, displayStyle);
   }

   public BeginBlockElement(String name, String type, int displayStyle)
   {
      super(name);
      this.type = type;
      this.displayStyle = displayStyle;
   }

   @Override
   public AbstractGroup createGroup(TeXParser parser)
   {
      return new BlockElement(type, displayStyle);
   }

   @Override
   public Object clone()
   {
      return new BeginBlockElement(getName(), type, displayStyle);
   }

   private int displayStyle;
   private String type;
}

class EndBlockElement extends EndGroup
{
   public EndBlockElement()
   {
      super("blockelement@internal@begingroup");
   }

   public EndBlockElement(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new EndBlockElement(getName());
   }

   @Override
   public boolean matches(BeginGroupObject bg)
   {
      return bg instanceof BeginBlockElement;
   }
}
