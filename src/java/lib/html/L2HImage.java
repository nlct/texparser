/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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

public class L2HImage implements TeXObject,Expandable
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

   @Override
   public Object clone()
   {
      return new L2HImage(path, mimetype, width, height, 
       name, alt == null ? null : (TeXObject)alt.clone());
   }

   @Override
   public int getTeXCategory()
   {
      return TYPE_OBJECT;
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

   @Override
   public String toString(TeXParser parser)
   {
      return alt == null ? "" : alt.toString(parser);
   }

   @Override
   public String toString()
   {
      return String.format(
       "%s[path=%s,width=%d,height=%d,type=%s,name=%s,alt=%s]",
        getClass().getName(), path, width, height, mimetype,
        name, alt);
   }

   @Override
   public boolean isPopStyleSkip(PopStyle popStyle)
   {
      return false;
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

   @Override
   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return alt == null ? new TeXObjectList() : alt.string(parser);
   }

   @Override
   public String format()
   {
      return alt == null ? "" : alt.format();
   }

   @Override
   public String stripToString(TeXParser parser)
     throws IOException
   {
      return format();
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return expandonce(parser);
   }

   @Override
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

   @Override
   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
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

   public boolean process(TeXParser parser, TeXObjectList stack, StackMarker marker)
    throws IOException
   {
      boolean foundMarker = false;

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
         foundMarker = alt.process(parser, stack, marker);
      }

      writer.write("</object>");

      return foundMarker;
   }

   private Path path;
   private String mimetype, name;
   private int width=0, height=0;
   private TeXObject alt;
}
