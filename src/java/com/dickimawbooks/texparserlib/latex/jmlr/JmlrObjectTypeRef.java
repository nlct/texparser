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
package com.dickimawbooks.texparserlib.latex.jmlr;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class JmlrObjectTypeRef extends JmlrObjectRef
{
   public JmlrObjectTypeRef(String tag)
   {
      this(tag, tag+"ref", new TeXCsRef("@empty"), new TeXCsRef("@empty"));
   }

   public JmlrObjectTypeRef(String tag, TeXObject pre, TeXObject post)
   {
      this(tag, tag+"ref", pre, post);
   }

   public JmlrObjectTypeRef(String tag, String name, 
     TeXObject pre, TeXObject post)
   {
      this(tag, name, pre, post, true);
   }

   public JmlrObjectTypeRef(String tag, String name, 
     TeXObject pre, TeXObject post, boolean prefixName)
   {
      super(name);
      this.tag = tag;
      this.pre = pre;
      this.post = post;
      this.prefixName = prefixName;
   }

   public Object clone()
   {
      return new JmlrObjectTypeRef(getTag(), getName(),
       (TeXObject)getPre().clone(), (TeXObject)getPost().clone(),
       prefixName);
   }

   public String getTag()
   {
      return tag;
   }

   public TeXObject getPre()
   {
      return pre;
   }

   public TeXObject getPost()
   {
      return post;
   }

   public boolean useNamePrefix()
   {
      return prefixName;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject labels = (stack == parser ?
          parser.popNextArg() :
          stack.popArg(parser));

      TeXObject singulartag = null;
      TeXObject pluraltag = null;

      if (prefixName)
      {
         singulartag = new TeXCsRef(getTag()+"refname");
         pluraltag = new TeXCsRef(getTag()+"srefname");
      }

      return expand(parser, labels, singulartag, pluraltag, pre, post);
   }

   private String tag;
   private TeXObject pre, post;
   private boolean prefixName;
}
