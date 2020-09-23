/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

package com.dickimawbooks.texparserlib.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dickimawbooks.texparserlib.ControlSequence;
import com.dickimawbooks.texparserlib.AssignedMacro;

public class ControlSequenceMatcher implements SearchMatcher
{
   public ControlSequenceMatcher(Pattern pattern)
   {
      this.pattern = pattern;
   }

   public ControlSequenceMatcher(Pattern pattern, int flags)
   {
      this.pattern = pattern;
      this.flags = flags;
   }

   public boolean isMatch(Object queryObject)
   {
      if (!(queryObject instanceof ControlSequence))
      {
         return false;
      }

      ControlSequence cs = (ControlSequence)queryObject;

      Matcher matcher = pattern.matcher(cs.getName());

      if (matcher.matches())
      {
         return true;
      }

      if (cs instanceof AssignedMacro)
      {
         return isMatch(((AssignedMacro)cs).getUnderlying());
      }

      return false;
   }

   public int getFlags()
   {
      return flags;
   }

   private Pattern pattern;
   private int flags;

// Register when control sequence is processed.
   public static final int FLAG_PROCESS=1;
// Register when control sequence is expanded.
   public static final int FLAG_EXPANSION=2;
}
