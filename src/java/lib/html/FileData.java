/*
    Copyright (C) 2026 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.html;

import java.io.File;

public class FileData
{
   public FileData(File file, String mimeType)
   {
      this(file, null, mimeType);
   }

   public FileData(File file, String id, String mimeType)
   {
      if (file == null || mimeType == null)
      {
         throw new NullPointerException();
      }

      this.file = file;
      this.mimeType = mimeType;

      if (id == null)
      {
         String name = file.getName();

         int idx = name.lastIndexOf('.');

         if (idx > 0)
         {
            name = name.substring(0, idx);
         }

         this.id = name;
      }
      else
      {
         this.id = id;
      }
   }

   public FileData(DivisionNode node, String mimeType)
   {
      if (node == null || mimeType == null)
      {
         throw new NullPointerException();
      }

      this.node = node;
      this.file = node.getFile();
      this.id = node.getId();
      this.mimeType = mimeType;
   }

   public File getFile()
   {
      return file;
   }

   public String getId()
   {
      return id;
   }

   public String getMimeType()
   {
      return mimeType;
   }

   public DivisionNode getNode()
   {
      return node;// may be null
   }

   @Override
   public boolean equals(Object other)
   {
      if (!(other instanceof FileData)) return false;

      FileData fd = (FileData)other;

      return id.equals(fd.id);
   }

   @Override
   public String toString()
   {
      return String.format("%s[file=%s,id=%s,type=%s,node=%s]",
       getClass().getSimpleName(), file, id, mimeType, node);
   }

   File file;
   String id;
   String mimeType;
   DivisionNode node;
}
