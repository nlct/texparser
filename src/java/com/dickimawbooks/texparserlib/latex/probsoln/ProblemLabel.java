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
package com.dickimawbooks.texparserlib.latex.probsoln;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class ProblemLabel extends TextualContentCommand
{
   public ProblemLabel(ProbSolnData entry)
   {
      this("thisproblemlabel", entry.getName(), entry);
   }

   public ProblemLabel(String label, ProbSolnData entry)
   {
      this("thisproblemlabel", label, entry);
   }

   public ProblemLabel(String name, String label, ProbSolnData entry)
   {
      super(name, label, entry);
   }

   @Override
   public Object clone()
   {
      return new ProblemLabel(getName(), getLabel(), getEntry());
   }

   public String getLabel()
   {
      return getText();
   }

   public ProbSolnData getEntry()
   {
      return (ProbSolnData)getData();
   }

   public void refresh(ProbSolnSty sty, String db)
     throws ProbSolnException
   {
      ProbSolnData data = getEntry();

      if (data == null)
      {
         ProbSolnDatabase database = sty.getDatabase(db);
         data = database.get(getLabel());
      }
   }
}
