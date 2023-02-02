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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsHyperNumber extends ControlSequence
{
   public GlsHyperNumber()
   {
      this("glshypernumber");
   }

   public GlsHyperNumber(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsHyperNumber(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject arg = popArg(parser, stack);
      TeXObject result;

      ControlSequence hyperCs = parser.getControlSequence("hyperlink");

      if (hyperCs == null)
      {
         result = arg;
      }
      else
      {
         ControlSequence prefixCs = parser.getControlSequence("@glo@counterprefix");
         ControlSequence counterCs = parser.getControlSequence("glsentrycounter");

         String prefix = "";

         if (prefixCs != null)
         {
            prefix = parser.expandToString(prefixCs, stack);
         }

         String counter = "";

         if (counterCs != null)
         {
            counter = parser.expandToString(counterCs, stack);
         }

         if (counter.isEmpty() || counter.equals("page"))
         {
            result = arg;
         }
         else
         {
            String target = counter+prefix;

            TeXObjectList expanded = parser.getListener().createStack();

            if (parser.isStack(arg))
            {
               TeXObjectList list = (TeXObjectList)arg;

               TeXObjectList substack = null;

               while (!list.isEmpty())
               {
                  TeXObject obj = list.popStack(parser);

                  if (obj instanceof ControlSequence)
                  {
                     String csname = ((ControlSequence)obj).getName();

                     if (csname.equals("nohyperpage"))
                     {
                        if (substack != null)
                        {
                           expanded.addAll(substack);
                           substack = null;
                        }

                        expanded.add(list.popArg(parser));
                     }
                     else if (csname.equals("delimN") || csname.equals("delimR"))
                     {
                        if (substack != null)
                        {
                           expanded.add(hyperCs);
                           String num = substack.toString(parser);

                           expanded.add(listener.createGroup(target+num));

                           Group grp = listener.createGroup();
                           expanded.add(grp);
                           grp.addAll(substack);

                           substack = null;
                        }

                        expanded.add(obj);
                     }
                     else
                     {
                        if (substack == null)
                        {
                           substack = parser.getListener().createStack();
                        }

                        substack.add(obj);
                     }
                  }
                  else
                  {
                     if (substack == null)
                     {
                        substack = parser.getListener().createStack();
                     }

                     substack.add(obj);
                  }
               }

               if (substack != null)
               {
                  expanded.add(hyperCs);
                  String num = substack.toString(parser);

                  expanded.add(listener.createGroup(target+num));

                  Group grp = listener.createGroup();
                  expanded.add(grp);
                  grp.addAll(substack);
               }
            }
            else
            {
               String num = arg.toString(parser);

               expanded.add(hyperCs);
               expanded.add(listener.createGroup(target+num));
               expanded.add(arg);
            }

            result = expanded;
         }
      }

      if (parser == stack || stack == null)
      {
         result.process(parser);
      }
      else
      {
         result.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
