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

public class Jmlr2eName extends ControlSequence
{
   public Jmlr2eName(Jmlr2eSty sty)
   {
      this(sty, "name");
   }

   public Jmlr2eName(Jmlr2eSty sty, String name)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new Jmlr2eName(sty, getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      Group grp = parser.getListener().createGroup();

      TeXObject object;

      while ((object = stack.pop()) != null)
      {
         if (object instanceof ControlSequence)
         {
            String name = ((ControlSequence)object).getName();

            if (name.equals("\\") || name.equals("email") || name.equals("addr")
              || name.toLowerCase().equals("and"))
            {
               stack.push(object);
               break;
            }
         }

         grp.add(object);
      }

      sty.addAuthor((Group)grp.clone());

      grp.push(new TeXCsRef("bfseries"));
      grp.push(new TeXCsRef("upshape"));
      grp.push(new TeXCsRef("normalsize"));

      stack.push(grp);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      Group grp = parser.getListener().createGroup();

      TeXObject object;

      while ((object = parser.pop()) != null)
      {
         if (object instanceof ControlSequence)
         {
            String name = ((ControlSequence)object).getName();

            if (name.equals("\\") || name.equals("email") || name.equals("addr")
              || name.toLowerCase().equals("and"))
            {
               parser.push(object);
               break;
            }
         }

         grp.add(object);
      }

      sty.addAuthor((Group)grp.clone());

      grp.push(new TeXCsRef("bfseries"));
      grp.push(new TeXCsRef("upshape"));
      grp.push(new TeXCsRef("normalsize"));

      parser.push(grp);
   }

   private Jmlr2eSty sty;
}
