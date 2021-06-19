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
import java.util.Stack;

import com.dickimawbooks.texparserlib.*;

public class DocumentBlockDec extends RobustDeclaration
{
   public DocumentBlockDec(String name)
   {
      this(name, name);
   }

   public DocumentBlockDec(String name, String type)
   {
      super(name);
      this.type = type;
   }

   @Override
   public Object clone()
   {
      return new DocumentBlockDec(getName(), getType());
   }

   public String getType()
   {
      return type;
   }

   public DocumentBlock createBlock(TeXParser parser)
   {
      return new DocumentBlock(type, 
         isInLine() ? DocumentBlock.DISPLAY_INLINE : DocumentBlock.DISPLAY_BLOCK);
   }

   public void setBlockAttributes(DocumentBlock block,
      TeXParser parser, TeXObjectList stack)
   throws IOException
   {
   }

   /*
    * Sub-classes should override popLabel to return null if there
    * should be no look-ahead for \label at the start of the
    * environment. For example, if the label is already provided as 
    * an argument or its an environment that shouldn't be labelled.
    * This method will discard any leading white space or (if not inline) paragraph
    * breaks.
    */ 
   protected String popLabel(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      PopStyle popStyle;

      if (isInLine())
      {
         popStyle = PopStyle.IGNORE_LEADING_SPACE;
      }
      else
      {
         popStyle = PopStyle.IGNORE_LEADING_PAR_AND_SPACE;
      }

      TeXObject object = parser.popNextTokenResolveReference(stack, popStyle);

      if (object instanceof Label)
      {
         return parser.popRequiredString(stack);
      }

      if (parser == stack || stack == null)
      {
         parser.push(object);
      }
      else
      {
         stack.push(object);
      }

      return null;
   }

   /*
    * If overridden, sub-class should call process(...) after popping off
    * any arguments that need to be dealt with before
    * setBlockAttributes pops off any remaining arguments. The end
    * of process looks ahead for \label unless setBlockAttributes
    * has already set "label"
    */
   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      DocumentBlock block = createBlock(parser);
      setBlockAttributes(block, parser, stack);

      if (block.getAttribute("label") == null)
      {
         String label = popLabel(parser, stack);

         if (label != null)
         {
            block.setAttribute("label", label);
         }
      }

      blockStack.push(block);
      ((LaTeXParserListener)parser.getListener()).startBlock(block);
   }

   @Override
   public void end(TeXParser parser)
    throws IOException
   {
      DocumentBlock block = blockStack.pop();
      ((LaTeXParserListener)parser.getListener()).endBlock(block);
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

   public boolean isInLine()
   {
      return false;
   }

   protected String type;
   private Stack<DocumentBlock> blockStack = new Stack<DocumentBlock>();
}
