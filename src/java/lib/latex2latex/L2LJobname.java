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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;
import java.io.Writer;

import com.dickimawbooks.texparserlib.*;

public class L2LJobname extends L2LControlSequence
{
   public L2LJobname()
   {
      this("jobname");
   }

   public L2LJobname(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2LJobname(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      LaTeX2LaTeX listener = (LaTeX2LaTeX)parser.getListener();

      if (listener.isReplaceJobnameOn())
      {
         String jobname = parser.getJobname();

         if (jobname == null)
         {
            super.process(parser, stack);
         }
         else
         {
            Writeable writeable = listener.getWriteable();
            writeable.write(jobname);
         }
      }
      else
      {
         super.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      LaTeX2LaTeX listener = (LaTeX2LaTeX)parser.getListener();

      if (listener.isReplaceJobnameOn())
      {
         String jobname = parser.getJobname();

         if (jobname == null)
         {
            super.process(parser);
         }
         else
         {
            Writeable writeable = listener.getWriteable();
            writeable.write(jobname);
         }
      }
      else
      {
         super.process(parser);
      }
   }
}
