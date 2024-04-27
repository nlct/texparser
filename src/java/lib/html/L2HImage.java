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

public class L2HImage extends AbstractTeXObject implements Expandable
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
      this(path, mimetype, width, height, name, alt, false);
   }

   public L2HImage(Path path, String mimetype, 
     int width, int height, String name, TeXObject alt, boolean useImgTag)
   {
      this.path = path;
      this.width = width;
      this.height = height;
      this.alt = alt;
      this.useImgTag = useImgTag;

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
   public String toString()
   {
      return String.format(
       "%s[path=%s,width=%d,height=%d,type=%s,name=%s,alt=%s,useImgTag=%s]",
        getClass().getName(), path, width, height, mimetype,
        name, alt, useImgTag);
   }

   @Override
   public boolean isPar()
   {
      return false;
   }

   @Override
   public String toString(TeXParser parser)
   {
      return alt.toString(parser);
   }

   @Override
   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return alt.string(parser);
   }

   @Override
   public String format()
   {
      return alt == null ? "" : alt.format();
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
    throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      if (useImgTag && mimetype.startsWith("image"))
      {
         L2HConverter listener = (L2HConverter)parser.getListener();

         VoidElement imgElem = listener.createVoidElement("img");

         imgElem.putAttribute("src",
           HtmlTag.encodeAttributeValue(getData(), true));

         if (width != 0)
         {
            imgElem.putAttribute("width", ""+width);
         }

         if (height != 0)
         {
            imgElem.putAttribute("height", ""+height);
         }

         if (name != null)
         {
            imgElem.putAttribute("id", name);
         }

         if (alt != null)
         {
            imgElem.putAttribute("alt", 
              HtmlTag.encodeAttributeValue(parser.expandToString(alt, stack), false));
         }

         list.add(imgElem);
      }
      else
      {
         StartElement startElem = new StartElement("object");

         startElem.putAttribute("data", 
           HtmlTag.encodeAttributeValue(getData(), true));

         if (width != 0)
         {
            startElem.putAttribute("width", ""+width);
         }

         if (height != 0)
         {
            startElem.putAttribute("height", ""+height);
         }

         if (mimetype != null)
         {
            startElem.putAttribute("type", mimetype);
         }

         if (name != null)
         {
            startElem.putAttribute("id", name);
         }

         list.add(startElem);

         if (alt != null)
         {
            list.add(alt);
         }

         list.add(new EndElement("object"));
      }

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

      if (useImgTag && mimetype.startsWith("image"))
      {
         L2HConverter listener = (L2HConverter)parser.getListener();

         VoidElement imgElem = listener.createVoidElement("img");

         imgElem.putAttribute("src",
            HtmlTag.encodeAttributeValue(getData(), true));

         if (width != 0)
         {
            imgElem.putAttribute("width", ""+width);
         }

         if (height != 0)
         {
            imgElem.putAttribute("height", ""+height);
         }

         if (name != null)
         {
            imgElem.putAttribute("id", name);
         }

         if (alt != null)
         {
            imgElem.putAttribute("alt", 
              HtmlTag.encodeAttributeValue(parser.expandToString(alt, stack), false));
         }

         list.add(imgElem);
      }
      else
      {
         StartElement startElem = new StartElement("object");

         startElem.putAttribute("data", 
           HtmlTag.encodeAttributeValue(getData(), true));

         if (width != 0)
         {
            startElem.putAttribute("width", ""+width);
         }

         if (height != 0)
         {
            startElem.putAttribute("height", ""+height);
         }

         if (mimetype != null)
         {
            startElem.putAttribute("type", mimetype);
         }

         if (name != null)
         {
            startElem.putAttribute("id", name);
         }

         list.add(startElem);

         if (alt != null)
         {
            alt = TeXParserUtils.expandFully(alt, parser, stack);

            list.add(alt, true);
         }

         list.add(new EndElement("object"));
      }

      return list;
   }

   @Override
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

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      L2HConverter listener = (L2HConverter)parser.getListener();

      listener.insertParIfRequired();

      if (useImgTag && mimetype.startsWith("image"))
      {
         listener.writeliteral(String.format("<img src=\"%s\"", 
           HtmlTag.encodeAttributeValue(getData(), true)));

         if (width != 0)
         {
            listener.writeliteral(String.format(" width=\"%d\"", width));
         }

         if (height != 0)
         {
            listener.writeliteral(String.format(" height=\"%d\"", height));
         }

         if (name != null)
         {
            listener.writeliteral(String.format(" id=\"%s\"", name));
         }

         if (alt != null)
         {
            listener.writeliteral(" alt=\"");

            listener.writeliteral(HtmlTag.encodeAttributeValue(
             listener.processToString(alt, stack), false));

            listener.writeliteral("\"");
         }

         if (listener.isXml())
         {
            listener.writeliteral("/");
         }

         listener.writeliteral(">");
      }
      else
      {
         listener.writeliteral(String.format("<object data=\"%s\"",
           HtmlTag.encodeAttributeValue(getData(), true)));

         if (width != 0)
         {
            listener.writeliteral(String.format(" width=\"%d\"", width));
         }

         if (height != 0)
         {
            listener.writeliteral(String.format(" height=\"%d\"", height));
         }

         if (mimetype != null)
         {
            listener.writeliteral(String.format(" type=\"%s\"", mimetype));
         }

         if (name != null)
         {
            listener.writeliteral(String.format(" id=\"%s\"", name));
         }

         listener.writeliteral(">");

         if (alt != null)
         {
            TeXParserUtils.process(alt, parser, stack);
         }

         listener.writeliteral("</object>");
      }
   }

   private Path path;
   private String mimetype, name;
   private int width=0, height=0;
   private TeXObject alt;
   protected boolean useImgTag = false;
}
