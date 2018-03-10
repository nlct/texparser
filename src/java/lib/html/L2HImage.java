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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.nio.file.Path;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HImage implements Expandable
{
   public L2HImage(Path path)
   {
      this(path, null, 0, 0, null, null);
   }

   public L2HImage(Path path, String mimetype)
   {
      this(path, mimetype, 0, 0, null, null);
   }

   public L2HImage(Path path, String mimetype, 
     int width, int height, String name, TeXObject alt)
   {
      this.path = path;
      this.width = width;
      this.height = height;
      this.alt = alt;

      if (mimetype == null)
      {
         updateMimeType();
      }
      else
      {
         this.mimetype = mimetype;
      }

      if (name != null)
      {
         name = name.replaceAll("[^\\w]", "");
      }
   }

   public Object clone()
   {
      return new L2HImage(path, mimetype, width, height, 
       name, alt == null ? null : (TeXObject)alt.clone());
   }

   protected void updateMimeType()
   {
      String name = path.getName(path.getNameCount()-1).toString();

      int idx = name.lastIndexOf(".");

      String ext = "";

      if (idx > -1)
      {
         ext = name.substring(idx).toLowerCase();
      }

      if (ext.equals("jpg") || ext.equals("jpeg"))
      {
         mimetype = "image/jpeg";
      }
      else if (ext.equals("png"))
      {
         mimetype = "image/png";
      }
      else if (ext.equals("pdf"))
      {
         mimetype = "application/pdf";
      }
   }

   public void setAlt(TeXObject alt)
   {
      this.alt = alt;
   }

   public String getName()
   {
      return name;
   }

   public String getMimeType()
   {
      return mimetype;
   }

   public String toString()
   {
      return String.format(
       "%s[path=%s,width=%d,height=%d,type=%s,name=%s,alt=%s]",
        getClass().getName(), path, width, height, mimetype,
        name, alt);
   }

   public boolean isPar()
   {
      return false;
   }

   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return alt.string(parser);
   }

   public String format()
   {
      return alt == null ? "" : alt.format();
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandonce(TeXParser parser)
    throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      StringBuilder builder = new StringBuilder();

      builder.append(String.format("<object data=\"%s\"", getData()));

      if (width != 0)
      {
         builder.append(String.format(" width=\"%d\"", width));
      }

      if (height != 0)
      {
         builder.append(String.format(" height=\"%d\"", height));
      }

      if (mimetype != null)
      {
         builder.append(String.format(" type=\"%s\"", mimetype));
      }

      if (name != null)
      {
         builder.append(String.format(" name=\"%s\"", name));
      }

      builder.append(">");

      list.add(new HtmlTag(builder.toString()));

      if (alt != null)
      {
         list.add(alt);
      }

      list.add(new HtmlTag("</object>"));

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      return expandfully(parser, parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      StringBuilder builder = new StringBuilder();

      builder.append(String.format("<object data=\"%s\"", getData()));

      if (width != 0)
      {
         builder.append(String.format(" width=\"%d\"", width));
      }

      if (height != 0)
      {
         builder.append(String.format(" height=\"%d\"", height));
      }

      if (mimetype != null)
      {
         builder.append(String.format(" type=\"%s\"", mimetype));
      }

      if (name != null)
      {
         builder.append(String.format(" name=\"%s\"", name));
      }

      builder.append(">");

      list.add(new HtmlTag(builder.toString()));

      if (alt != null)
      {
         if (alt instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack)
            {
               expanded = ((Expandable)alt).expandfully(parser);
            }
            else
            {
               expanded = ((Expandable)alt).expandfully(parser, stack);
            }

            if (expanded == null)
            {
               list.add(alt);
            }
            else
            {
               list.addAll(expanded);
            }
         }
         else
         {
            list.add(alt);
         }
      }

      list.add(new HtmlTag("</object>"));

      return list;
   }

   public void process(TeXParser parser)
    throws IOException
   {
      process(parser, parser);
   }

   public Path getPath()
   {
      return path;
   }

   private String getData()
   {
      int n = path.getNameCount();

      if (n == 1)
      {
         return path.toString();
      }

      // need to use / as directory divider regardless of OS
      StringBuilder builder = new StringBuilder();

      builder.append(path.getName(0).toString());

      for (int i = 1; i < n; i++)
      {
         builder.append("/");
         builder.append(path.getName(i).toString());
      }

      return builder.toString();
   }

   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      Writeable writer = parser.getListener().getWriteable();

      writer.write(String.format("<object data=\"%s\"", getData()));

      if (width != 0)
      {
         writer.write(String.format(" width=\"%d\"", width));
      }

      if (height != 0)
      {
         writer.write(String.format(" height=\"%d\"", height));
      }

      if (mimetype != null)
      {
         writer.write(String.format(" type=\"%s\"", mimetype));
      }

      if (name != null)
      {
         writer.write(String.format(" name=\"%s\"", name));
      }

      writer.write(">");

      if (alt != null)
      {
         if (parser == stack)
         {
            alt.process(parser);
         }
         else
         {
            alt.process(parser, stack);
         }
      }

      writer.write("</object>");
   }

   private Path path;
   private String mimetype, name;
   private int width=0, height=0;
   private TeXObject alt;
}
