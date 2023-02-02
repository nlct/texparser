/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class InternalGetGroupTitle extends ControlSequence
{
   public InternalGetGroupTitle(String name, boolean useGroup)
   {
      super(name);
      this.useGroup = useGroup;
   }

   @Override
   public TeXObject clone()
   {
      return new InternalGetGroupTitle(getName(), useGroup);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String groupLabel = popLabelString(parser, stack);
      ControlSequence csArg = popControlSequence(parser, stack);

       ControlSequence cs = parser.getControlSequence("glsxtr@grouptitle@"+groupLabel);
      if (useGroup)
      {
         if (cs == null)
         {
            parser.putControlSequence(true, new TextualContentCommand(
              csArg.getName(), groupLabel));
         }
         else
         {
            parser.putControlSequence(true, new GenericCommand(true,
               csArg.getName(), null, cs));
         }
      }
      else if (!groupLabel.isEmpty())
      {
         if (cs == null)
         {
            try
            {
               int num = Integer.parseInt(groupLabel);

               if (Character.isAlphabetic(num))
               {
                  parser.putControlSequence(true, new GenericCommand(true,
                    csArg.getName(), null, parser.getListener().getLetter(num)));
               }
               else
               {
                  parser.putControlSequence(true, new GenericCommand(true,
                    csArg.getName(), null, 
                    parser.getListener().getControlSequence("glssymbolsgroupname")));
               }
            }
            catch (NumberFormatException e)
            {
               cs = parser.getControlSequence(groupLabel+"groupname");

               if (cs == null)
               {
                  parser.putControlSequence(true, new TextualContentCommand(
                    csArg.getName(), groupLabel));
               }
               else
               {
                  parser.putControlSequence(true, new GenericCommand(true,
                     csArg.getName(), null, cs));
               }
            }
         }
         else
         {
            parser.putControlSequence(true, new GenericCommand(true,
             csArg.getName(), null, cs));
         }
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected boolean useGroup;
}
