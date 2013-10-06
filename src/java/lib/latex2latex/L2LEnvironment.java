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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2LEnvironment extends Environment
{
   public L2LEnvironment(String name)
   {
      super(name);
   }

   public L2LEnvironment(String name, int mode)
   {
      super(name, mode);
   }

   public Object clone()
   {
      L2LEnvironment env = new L2LEnvironment(getName(), getMode());

      env.addAll(this);

      return env;
   }

   private void processInlineMath(TeXParser parser)
    throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      char esc = parser.getEscChar();
      char bg = parser.getBgChar();
      char eg = parser.getEgChar();

      L2LMathGroup grp = new L2LMathGroup(true, 
        ""+esc+"begin"+bg+getName()+eg,
        ""+esc+"end"+bg+getName()+eg);

      while (size() > 0)
      {
         grp.add(pop());
      }

      grp.process(parser);

   }

   private void processDisplayMath(TeXParser parser)
    throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      char esc = parser.getEscChar();
      char bg = parser.getBgChar();
      char eg = parser.getEgChar();

      String startDelim;
      String endDelim;

      String name = getName();

      if (name.equals("displaymath"))
      {
         startDelim = esc+"[";
         endDelim = esc+"]";
      }
      else
      {
         startDelim = ""+esc+"begin"+bg+name+eg;
         endDelim = ""+esc+"end"+bg+name+eg;
      }

      L2LMathGroup grp = new L2LMathGroup(false, 
        startDelim, endDelim);

      while (size() > 0)
      {
         grp.add(pop());
      }

      grp.process(parser);
   }

   private void processText(TeXParser parser)
    throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      char esc = parser.getEscChar();
      char bg = parser.getBgChar();
      char eg = parser.getEgChar();

      writeable.write(esc);
      writeable.write("begin");
      writeable.write(bg);
      writeable.write(getName());
      writeable.write(eg);
      parser.startGroup();

      while (size() > 0)
      {
         TeXObject object = pop();

         if (object instanceof Ignoreable
           || object instanceof WhiteSpace)
         {
            writeable.write(object.toString(parser));
         }
         else
         {
            object.process(parser, this);
         }
      }

      parser.endGroup();

      writeable.write(esc);
      writeable.write("end");
      writeable.write(bg);
      writeable.write(getName());
      writeable.write(eg);
   }

   public void process(TeXParser parser)
    throws IOException
   {
      int mode = getMode();
      mode = (mode == TeXSettings.INHERIT ? parser.getSettings().getMode()
        : mode);

      switch (mode)
      {
         case TeXSettings.MODE_INLINE_MATH:
            processInlineMath(parser);
         break;
         case TeXSettings.MODE_DISPLAY_MATH:
            processDisplayMath(parser);
         break;
         default:
            processText(parser);
      }
      
   }

   public void process(TeXParser parser, TeXObjectList list)
    throws IOException
   {
      process(parser);
   }
}
