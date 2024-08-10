/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class NewAcro extends ControlSequence
{
   public NewAcro(DataGidxSty sty)
   {
      this("newacro", sty);
   }

   public NewAcro(String name, DataGidxSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new NewAcro(getName(), sty);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      KeyValList options = TeXParserUtils.popOptKeyValList(parser, stack);
      TeXObject shortVal = popArg(parser, stack);
      TeXObject longVal = popArg(parser, stack);

      TeXObject name = (TeXObject)shortVal.clone();

      if (name instanceof CaseChangeable)
      {
         name = ((CaseChangeable)name).toUpperCase(parser);
      }

      if (options == null)
      {
         options = new KeyValList();
      }

      TeXObjectList list;
      Group grp;

      TeXObject sortVal = options.getValue("sort");

      if (sortVal == null)
      {
         options.put("sort", (TeXObject)shortVal.clone());
      }

      TeXObject labelVal = options.getValue("label");

      if (labelVal == null)
      {
         options.put("label", (TeXObject)shortVal.clone());
      }

      TeXObject shortPl = options.get("shortplural");

      if (shortPl == null)
      {
         shortPl = (TeXObject)shortVal.clone();
         list = TeXParserUtils.toList(shortPl, parser);
         list.add(listener.getLetter('s'));
      }

      TeXObject longPl = options.get("longplural");

      if (longPl == null)
      {
         longPl = (TeXObject)longVal.clone();
         list = TeXParserUtils.toList(longPl, parser);
         list.add(listener.getLetter('s'));
      }

      // text

      list = listener.createStack();
      list.add(new TeXCsRef("DTLgidxAcrStyle"));
      grp = listener.createGroup();
      list.add(grp);      
      grp.add((TeXObject)longVal.clone(), true);

      grp = listener.createGroup();
      list.add(grp);      
      grp.add(new TeXCsRef("acronymfont"));

      Group subGrp = listener.createGroup();
      grp.add(subGrp);      
      subGrp.add((TeXObject)shortVal.clone(), true);

      options.put("text", list);

      // plural

      list = listener.createStack();
      list.add(new TeXCsRef("DTLgidxAcrStyle"));
      grp = listener.createGroup();
      list.add(grp);      
      grp.add((TeXObject)longPl.clone(), true);

      grp = listener.createGroup();
      list.add(grp);      
      grp.add(new TeXCsRef("acronymfont"));

      subGrp = listener.createGroup();
      grp.add(subGrp);      
      subGrp.add((TeXObject)shortPl.clone(), true);

      options.put("plural", list);

      // short

      list = listener.createStack();
      list.add(new TeXCsRef("acronymfont"));
      grp = listener.createGroup();
      list.add(grp);
      list.add(shortVal, true);

      options.put("short", list);

      options.put("long", longVal);

      sty.addTerm(options, name, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataGidxSty sty;
}
