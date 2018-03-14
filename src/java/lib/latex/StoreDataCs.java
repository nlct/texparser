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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class StoreDataCs extends ControlSequence
{
   public StoreDataCs(String name)
   {
      this(name, "@"+name);
   }

   public StoreDataCs(String name, String internalName)
   {
      this(name, null, internalName);
   }

   public StoreDataCs(String name, String optInternalName, String internalName)
   {
      super(name);
      setOptionalInternalName(optInternalName);
      setInternalName(internalName);
   }

   protected void setInternalName(String internalName)
   {
      this.internalName = internalName;
   }

   public String getInternalName()
   {
      return internalName;
   }

   protected void setOptionalInternalName(String optInternalName)
   {
      this.optInternalName = optInternalName;
   }

   public String getOptionalInternalName()
   {
      return optInternalName;
   }

   public Object clone()
   {
      return new StoreDataCs(getName(),
         getOptionalInternalName(), getInternalName());
   }

   protected void setData(TeXParser parser, TeXObject optArg, TeXObject arg)
   {
      ControlSequence cs = parser.getControlSequence(getInternalName());

      if (cs instanceof GenericCommand)
      {
         TeXObjectList definition = ((GenericCommand)cs).getDefinition();
         definition.clear();
         definition.add(arg);
      }
      else
      {
         cs = new GenericCommand(getInternalName(), null, arg);
         parser.putControlSequence(cs);
      }

      if (getOptionalInternalName() != null)
      {
         cs = parser.getControlSequence(getOptionalInternalName());

         if (cs instanceof GenericCommand)
         {
            TeXObjectList definition = ((GenericCommand)cs).getDefinition();
            definition.clear();

            if (optArg == null)
            {
               definition.add(arg);
            }
            else
            {
               definition.add(optArg);
            }
         }
         else
         {
            if (optArg == null)
            {
               cs = new GenericCommand(getOptionalInternalName(), null, arg);
            }
            else
            {
               cs = new GenericCommand(getOptionalInternalName(), null, optArg);
            }

            parser.putControlSequence(cs);
         }
      }
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject optArg = null;

      if (getOptionalInternalName() != null)
      {
         optArg = stack.popArg(parser, '[', ']');
      }

      TeXObject arg = stack.popArg(parser);
      setData(parser, optArg, arg);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObject optArg = null;

      if (getOptionalInternalName() != null)
      {
         optArg = parser.popNextArg('[', ']');
      }

      TeXObject arg = parser.popNextArg();
      setData(parser, optArg, arg);
   }

   private String internalName, optInternalName;
}
