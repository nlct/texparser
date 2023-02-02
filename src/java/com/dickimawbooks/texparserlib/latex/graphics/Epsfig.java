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
package com.dickimawbooks.texparserlib.latex.graphics;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Epsfig extends ControlSequence
{
   public Epsfig()
   {
      this("epsfig");
   }

   public Epsfig(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Epsfig(getName());
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      TeXObjectList original = new TeXObjectList();
      TeXObjectList replacement = new TeXObjectList();
      original.add(this);

      ControlSequence gcs 
         = parser.getListener().getControlSequence("includegraphics");

      IncludeGraphics incGraphics = null;

      if (gcs instanceof IncludeGraphics)
      {
         incGraphics = (IncludeGraphics)gcs;;
      }

      replacement.add(gcs);

      TeXObject arg = list.popStack(parser);

      Group grp;

      if (arg instanceof Group)
      {
         grp = (Group)arg;
      }
      else
      {
         grp = parser.getListener().createGroup();
         grp.add(arg);
      }

      original.add(grp);

      String originalStr = original.toString(parser);

      KeyValList keyValList = null;

      keyValList = KeyValList.getList(parser, arg);

      TeXObject fileArg = keyValList.remove("file");

      if (fileArg == null)
      {
         fileArg = keyValList.remove("figure");
      }

      if (fileArg == null)
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_MISSING_KEY, "file");
      }

      grp = parser.getListener().createGroup();

      if (fileArg instanceof TeXObjectList)
      {
         grp.addAll(0, (TeXObjectList)fileArg);
      }
      else
      {
         grp.add(0, fileArg);
      }

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      replacement.add(listener.getOther('['));
      replacement.add(keyValList);
      replacement.add(listener.getOther(']'));
      replacement.add(grp);

      listener.substituting(originalStr, replacement.toString(parser));

      list.push(grp);

      if (incGraphics == null)
      {
         replacement.pop();
         gcs.process(parser, replacement);
      }
      else
      {
         incGraphics.process(parser, list, keyValList);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
