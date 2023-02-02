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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2LMathGroup extends MathGroup
{
   public L2LMathGroup()
   {
      this(true);
   }

   public L2LMathGroup(boolean isInLine)
   {
      this(isInLine, null, null);
   }

   public L2LMathGroup(boolean isInLine, String openDelim, String closeDelim)
   {
      setInLine(isInLine);
      this.openDelim = openDelim;
      this.closeDelim = closeDelim;
   }

   public TeXObjectList createList()
   {
      return new L2LMathGroup(isInLine(), openDelim, closeDelim);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      parser.startGroup();

      Writeable writeable = parser.getListener().getWriteable();

      String delim = parser.getMathDelim(isInLine());

      String endDelim;

      int orgMode = parser.getSettings().getCurrentMode();

      if (isInLine())
      {
         endDelim = (closeDelim == null ? delim : closeDelim);
         delim = (openDelim == null ? delim : openDelim);
         parser.getSettings().setMode(TeXSettings.MODE_INLINE_MATH);
      }
      else
      {
         String orgDelim = delim;

         String esc = new String(Character.toChars(parser.getEscChar()));

         delim = (openDelim == null ? String.format("%s[", esc) : openDelim);
         endDelim = (closeDelim == null ? String.format("%s]", esc):closeDelim);

         LaTeX2LaTeX listener = (LaTeX2LaTeX)parser.getListener();

         StringBuilder argStr = new StringBuilder();

         for (TeXObject obj : this)
         {
            if (obj instanceof Obsolete)
            {
               argStr.append(((Obsolete)obj).getOriginalCommand().toString(parser));
            }
            else
            {
               argStr.append(obj.toString(parser));
            }
         }

         if (!delim.equals(openDelim) && !endDelim.equals(closeDelim))
         {
            listener.substituting( 
                orgDelim+argStr+orgDelim, delim+argStr+endDelim);
         }

         parser.getSettings().setMode(TeXSettings.MODE_DISPLAY_MATH);
      }

      writeable.write(delim);

      while (size() > 0)
      {
         TeXObject object = pop();

         if (object instanceof Obsolete
           && ((Obsolete)object).getOriginalCommand()
                   instanceof TeXFontDeclaration
            )
         {
            ControlSequence original = 
               ((Obsolete)object).getOriginalCommand();

            LaTeX2LaTeX listener = (LaTeX2LaTeX)parser.getListener();

            ControlSequence cs = listener.getControlSequence(
               "math"+original.getName());

            String replacement = cs.toString(parser);
            listener.substituting(original.toString(parser), replacement);

            Group grp = parser.getListener().createGroup();

            while (size() > 0)
            {
               grp.add(pop());
            }

            cs.process(parser, grp);
         }
         else if (object instanceof Ignoreable
               || object instanceof WhiteSpace)
         {
            writeable.write(object.toString(parser));
         }
         else
         {
            if (stack != parser && size() == 0)
            {
               object.process(parser, stack);
            }
            else
            {
               object.process(parser, this);
            }
         }
      }

      parser.getSettings().setMode(orgMode);

      writeable.write(endDelim);

      parser.endGroup();
   }

   private String openDelim, closeDelim;
}

