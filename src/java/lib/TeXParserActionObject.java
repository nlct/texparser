/*
    Copyright (C) 2022-2023 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib;

import java.io.IOException;
import java.io.File;

public class TeXParserActionObject extends AbstractTeXObject
{
   protected TeXParserActionObject(TeXParserAction action, TeXObjectList stack, Object data)
   {
      this.action = action;
      this.data = data;
      this.pending = stack;
   }

   public static TeXParserActionObject createInputAction(TeXParser parser, String filename)
    throws IOException
   {
      return createInputAction(new TeXPath(parser, filename));
   }

   public static TeXParserActionObject createInputAction(TeXParser parser, String filename, TeXObjectList stack)
    throws IOException
   {
      return createInputAction(new TeXPath(parser, filename), stack);
   }

   public static TeXParserActionObject createInputAction(TeXPath texPath)
   {
      return createInputAction(texPath, null);
   }

   public static TeXParserActionObject createInputAction(TeXPath texPath, TeXObjectList stack)
   {
      if (stack != null && !(stack instanceof TeXParser))
      {
         TeXObjectList pending = new TeXObjectList();
         pending.add(stack, true);
         stack.clear();

         stack = pending;
      }

      return new TeXParserActionObject(TeXParserAction.INPUT_FILE, stack, texPath);
   }

   public static TeXParserActionObject createInputAction(File file)
   {
      return createInputAction(file, null);
   }

   public static TeXParserActionObject createInputAction(File file, TeXObjectList stack)
   {
      if (stack != null && !(stack instanceof TeXParser))
      {
         TeXObjectList pending = new TeXObjectList();
         pending.add(stack, true);
         stack.clear();

         stack = pending;
      }

      return new TeXParserActionObject(TeXParserAction.INPUT_FILE, stack, file);
   }

   public static TeXParserActionObject createModeChangeAction(TeXMode mode)
   {
      return new TeXParserActionObject(TeXParserAction.MODE_CHANGE, null, mode);
   }

   @Override
   public Object clone()
   {
      return new TeXParserActionObject(action, pending, data);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (pending == null)
      {
         pending = stack;
      }
      else
      {
         pending.add(stack, true);
         stack.clear();
      }

      parser.processAction(this);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      parser.processAction(this);
   }

   public TeXObjectList getPending()
   {
      return pending;
   }

   public TeXParserAction getAction()
   {
      return action;
   }

   public Object getData()
   {
      return data;
   }

   public File getFile()
   {
      if (data instanceof File)
      {
         return (File)data;
      }

      if (data instanceof TeXPath)
      {
         return ((TeXPath)data).getFile();
      }

      return null;
   }

   @Override
   public String toString()
   {
      return String.format("%s[action=%s,data=%s]", getClass().getSimpleName(),
        action, data);
   }

   @Override
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public String format()
   {
      return "";
   }

   @Override
   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return new TeXObjectList();
   }

   @Override
   public String toString(TeXParser parser)
   {
      return "";
   }

   private TeXObjectList pending = null;
   private TeXParserAction action;
   private Object data;
}
