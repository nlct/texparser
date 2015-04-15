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
package com.dickimawbooks.texparserlib.latex.probsoln;

import java.io.IOException;
import java.util.HashMap;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class ProbSolnData
{
   public ProbSolnData(String name, TeXObjectList contents)
   {
      this(name, 0, null, contents);
   }

   public ProbSolnData(String name, int numArgs,
      TeXObjectList defArgs, TeXObjectList contents)
   {
      super();
      setName(name);
      this.numArgs = numArgs;
      this.defArgs = defArgs;
      this.contents = contents;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public int getNumArgs()
   {
      return numArgs;
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject[] params = null;

      if (numArgs > 0)
      {
         params = new TeXObject[numArgs];

         for (int i = 0; i < numArgs; i++)
         {
            params[i] = parser.popNextArg();
         }
      }

      getData(parser, params).process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject[] params = null;

      if (numArgs > 0)
      {
         params = new TeXObject[numArgs];

         for (int i = 0; i < numArgs; i++)
         {
            params[i] = stack.popArg(parser);
         }
      }

      getData(parser, params).process(parser, stack);
   }

   private TeXObjectList getData(TeXParser parser, TeXObject[] params)
     throws IOException
   {
       return getData(parser, params, contents);
   }

   private TeXObjectList getData(TeXParser parser, TeXObject[] params,
     TeXObjectList contentsList)
     throws IOException
   {
      TeXObjectList list = contentsList.createList();

      for (TeXObject object : contentsList)
      {
         if (object instanceof Param)
         {
            int idx = ((Param)object).getDigit()-1;

            if (params == null || idx >= params.length)
            {
               throw new TeXSyntaxException(parser,
                 TeXSyntaxException.ERROR_SYNTAX,
                 object.toString(parser));
            }

            list.add((TeXObject)params[idx].clone());
         }
         else if (object instanceof DoubleParam)
         {
            list.add((TeXObject)((DoubleParam)object).getParam().clone());
         }
         else if (object instanceof TeXObjectList)
         {
            list.add(getData(parser, params, (TeXObjectList)object));
         }
         else
         {
            list.add((TeXObject)object.clone());
         }
      }

      return list;
   }

   private String name;

   private int numArgs = 0;

   private TeXObjectList contents, defArgs;
}
