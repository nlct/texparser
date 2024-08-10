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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class DatagidxWordifyGreek extends ControlSequence
{
   public DatagidxWordifyGreek()
   {
      this("datagidxwordifygreek");
   }

   public DatagidxWordifyGreek(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new DatagidxWordifyGreek(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      for (String csname : NAMES)
      {
         parser.putControlSequence(true,
            new TextualContentCommand(csname, csname));

         csname = csname.substring(0, 1).toUpperCase() + csname.substring(1);
       
         parser.putControlSequence(true,
            new TextualContentCommand(csname, csname));
      }

      parser.putControlSequence(true,
         new TextualContentCommand("varepsilon", "epsilon"));

      parser.putControlSequence(true,
         new TextualContentCommand("vartheta", "theta"));

      parser.putControlSequence(true,
         new TextualContentCommand("varpi", "pi"));

      parser.putControlSequence(true,
         new TextualContentCommand("varrho", "rho"));

      parser.putControlSequence(true,
         new TextualContentCommand("varsigma", "sigma"));

      parser.putControlSequence(true,
         new TextualContentCommand("varphi", "phi"));
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public static final String[] NAMES
    = {"alpha", "beta", "gamma", "delta", "epsilon", "zeta", "eta",
       "theta", "iota", "kappa", "lambda", "mu", "nu", "xi", "pi",
       "rho", "sigma", "tau", "upsilon", "phi", "chi", "psi",
       "omaga"};
}
