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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class UnknownReference extends TeXObjectList
{
   public UnknownReference(TeXParserListener listener, TeXObject labelObj)
   {
      this(listener, labelObj, null);
   }

   public UnknownReference(TeXParserListener listener, String label)
   {
      this(listener, null, label);
   }

   public UnknownReference(TeXParserListener listener, 
     TeXObject labelObj, String label)
   {
      super(listener, "??");
      this.labelObject = labelObj;
      this.label = label;
   }

   private UnknownReference(TeXObject labelObj, String label)
   {
      super();
      this.labelObject = labelObj;
      this.label = label;
   }

   public TeXObjectList createList()
   {
      return new UnknownReference(getLabelObject(), getLabel());
   }

   public TeXObject getLabelObject()
   {
      return labelObject;
   }

   public String getLabel()
   {
      return label;
   }

   public String getLabel(TeXParser parser)
   {
      return label == null ?
             (labelObject == null ? "??" : labelObject.toString(parser)) :
             label;
   }

   private TeXObject labelObject=null;
   private String label=null;
}
