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
package com.dickimawbooks.texparserlib.aux;

import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import java.nio.file.Path;
import java.nio.file.Files;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;

/**
 * Commands used in aux files.
 */

public class AuxCommand extends ControlSequence
{
   public AuxCommand(String name, int numArgs)
   {
      super(name);
      this.numArgs = numArgs;
   }

   public int getNumArgs()
   {
      return numArgs;
   }

   public AuxCommand clone()
   {
      return new AuxCommand(getName(), getNumArgs());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      AuxParser auxParser = (AuxParser)parser.getListener();

      TeXObject[] args = new TeXObject[numArgs];

      for (int i = 0; i < numArgs; i++)
      {
         args[i] = stack.popArg(parser);
      }

      auxParser.addAuxData(new AuxData(getName(), args));
   }

   public void process(TeXParser parser)
     throws IOException
   {
      AuxParser auxParser = (AuxParser)parser.getListener();

      TeXObject[] args = new TeXObject[numArgs];

      for (int i = 0; i < numArgs; i++)
      {
         args[i] = parser.popNextArg();
      }

      auxParser.addAuxData(new AuxData(getName(), args));
   }

   private int numArgs;
}
