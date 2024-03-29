/*
    Copyright (C) 2018 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.mfirstuc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsMakeFirstUc extends Command
{
   public GlsMakeFirstUc()
   {
      this("glsmakefirstuc", UNEXPANDED);
   }

   public GlsMakeFirstUc(int setting)
   {
      this("glsmakefirstuc", setting);
   }

   public GlsMakeFirstUc(String name, int setting)
   {
      super(name);
      this.setting = setting;
   }

   public Object clone()
   {
      return new GlsMakeFirstUc(getName(), setting);
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      TeXObjectList expanded = new TeXObjectList();

      if (arg instanceof MathGroup)
      {
         expanded.add(arg);
      }
      else if (!arg.isEmpty())
      {
         if (setting == GRABFIRST)
         {
            expanded.add(parser.getListener().getControlSequence("MakeUppercase"));
            expanded.add(arg, true);
         }
         else
         {
            expanded.add(parser.getListener().getControlSequence("MFUsentencecase"));
            Group grp = parser.getListener().createGroup();
            expanded.add(grp);

            if (setting == EXPANDED)
            {
               grp.add(arg, true);
            }
            else
            {
               grp.add(parser.getListener().getControlSequence("unexpanded"));

               Group subGrp = parser.getListener().createGroup();
               grp.add(subGrp);
               subGrp.add(arg, true);
            }
         }
      }

      return expanded;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      expandonce(parser, stack).process(parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      expandonce(parser).process(parser);
   }

   public static final int UNEXPANDED=0;
   public static final int EXPANDED=1;
   public static final int GRABFIRST=2;

   private int setting = UNEXPANDED;
}
