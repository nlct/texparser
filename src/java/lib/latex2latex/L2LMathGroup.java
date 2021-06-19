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

   @Override
   public AbstractTeXObjectList createList()
   {
      return new L2LMathGroup(isInLine(), openDelim, closeDelim);
   }

   @Override
   public void startGroup(TeXParser parser)
      throws IOException
   {
      parser.startGroup();

      Writeable writeable = parser.getListener().getWriteable();
      String delim = parser.getMathDelim(isInLine());
      orgMode = parser.getSettings().getCurrentMode();

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
   }

   @Override
   public void endGroup(TeXParser parser)
      throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();
      parser.getSettings().setMode(orgMode);
      writeable.write(endDelim);
      parser.endGroup();
   }

   @Override
   protected void processList(TeXParser parser, StackMarker marker)
    throws IOException
   {
      LaTeX2LaTeX listener = (LaTeX2LaTeX)parser.getListener();
      Writeable writeable = listener.getWriteable();
      TeXObjectList subStack = toList();

      while (subStack.size() > 0)
      {
         TeXObject object = subStack.remove(0);

         if (object instanceof TeXCsRef)
         {
            object = listener.getControlSequence(((TeXCsRef)object).getName());
         }

         if (object.equals(marker))
         {
            break;
         }

         if (object instanceof Group)
         {
            object = parseSubGroup(parser, (Group)object);

            if (object.process(parser, subStack, marker))
            {
               break;
            }
         }
         else if (object instanceof Obsolete
           && ((Obsolete)object).getOriginalCommand()
                   instanceof TeXFontDeclaration
            )
         {
            ControlSequence original = 
               ((Obsolete)object).getOriginalCommand();

            ControlSequence cs = listener.getControlSequence(
               "math"+original.getName());

            String replacement = cs.toString(parser);
            listener.substituting(original.toString(parser), replacement);

            Group grp = parser.getListener().createGroup();

            while (subStack.size() > 0)
            {
               object = subStack.pop();

               if (object.equals(marker)) break;

               if (!(grp.isEmpty() && (object instanceof SkippedSpaces)))
               {
                  grp.add(object);
               }
            }

            subStack.push(grp);

            if (cs.process(parser, subStack, marker))
            {
               break;
            }
         }
         else if (object instanceof Ignoreable
               || object instanceof WhiteSpace)
         {
            writeable.write(object.toString(parser));
         }
         else
         {
            if (object.process(parser, subStack, marker))
            {
               break;
            }
         }
      }

      clear();

      if (!subStack.isEmpty())
      {
         addAll(subStack);
      }
   }

   protected TeXObject parseSubGroup(TeXParser parser, Group subGroup)
      throws IOException
   {
      // get the first non-ignorable and test if its an obsolete
      // font command

      for (int i = 0; i < subGroup.size(); i++)
      {
         TeXObject object = subGroup.get(i);

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
                ((TeXCsRef)object).getName());
         }

         if (object instanceof Ignoreable) continue;

         if (object instanceof Obsolete
           && ((Obsolete)object).getOriginalCommand()
                   instanceof TeXFontDeclaration)
         {
            ControlSequence original = 
               ((Obsolete)object).getOriginalCommand();

            LaTeX2LaTeX listener = (LaTeX2LaTeX)parser.getListener();

            ControlSequence cs = listener.getControlSequence(
               "math"+original.getName());

            String replacement = cs.toString(parser);
            listener.substituting(original.toString(parser), replacement);

            subGroup.remove(i);

            TeXObjectList list = new TeXObjectList();
            list.add(cs);

            while (!subGroup.isEmpty())
            {
               object = subGroup.get(i);

               if (object instanceof SkippedSpaces)
               {
                  list.add(object);
                  subGroup.remove(i);
               }
               else
               {
                  break;
               }
            }

            list.add(subGroup);

            return list;
         }
         else
         {
            return subGroup;
         }
      }

      return subGroup;
   }

   private String endDelim;
   private int orgMode = TeXSettings.MODE_TEXT;
   private String openDelim, closeDelim;
}

