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

public class Environment extends Group
{
   public Environment(String name)
   {
      this(name, TeXSettings.INHERIT);
   }

   public Environment(String name, int mode)
   {
      this.name = name;
      this.mode = mode;
   }

   public Object clone()
   {
      Environment env = new Environment(name, mode);

      env.addAll(this);

      return env;
   }

   public String getName()
   {
      return name;
   }

   public String toString(TeXParser parser)
   {
      StringBuilder builder = new StringBuilder();

      char esc = parser.getEscChar();
      char bg = parser.getBgChar();
      char eg = parser.getEgChar();

      builder.append(esc);
      builder.append("begin");
      builder.append(bg);
      builder.append(name);
      builder.append(eg);

      for (TeXObject object : this)
      {
         builder.append(object.toString(parser));
      }

      builder.append(esc);
      builder.append("end");
      builder.append(bg);
      builder.append(name);
      builder.append(eg);

      return builder.toString();
   }

   private boolean checkObj(TeXParser parser, TeXObject object,
     TeXObjectList list)
    throws IOException
   {
      if (object instanceof ControlSequence)
      {
         if (((ControlSequence)object).getName().equals("end"))
         {
            // Have we found the end of this group?

            TeXObject arg = list.popArg();

            String str = arg.toString(parser);

            if (!str.equals(name))
            {
               throw new TeXSyntaxException(TeXSyntaxException.ERROR_EXPECTED,
                 object.toString(parser)+
                 parser.getBgChar()+name+parser.getEgChar());
            }

            return true;
         }
         else if (((ControlSequence)object).getName().equals("begin"))
         {
            TeXObject arg = list.popArg();

            String str = arg.toString(parser);

            Environment env = new Environment(str);

            env.popGroup(parser, list);
         }
         else
         {
            add(object);
         }
      }
      else
      {
         add(object);
      }

      return false;
   }

   private boolean checkObj(TeXParser parser, TeXObject object)
    throws IOException
   {
      if (object instanceof ControlSequence)
      {
         if (((ControlSequence)object).getName().equals("end"))
         {
            // Have we found the end of this group?

            TeXObject arg = parser.popNextArg();

            String str = arg.toString(parser);

            if (!str.equals(name))
            {
               throw new TeXSyntaxException(TeXSyntaxException.ERROR_EXPECTED,
                 object.toString(parser)+
                 parser.getBgChar()+name+parser.getEgChar());
            }

            return true;
         }
         else if (((ControlSequence)object).getName().equals("begin"))
         {
            TeXObject arg = parser.popNextArg();

            String str = arg.toString(parser);

            Environment env = new Environment(str);

            env.popGroup(parser);
         }
         else
         {
            add(object);
         }
      }
      else
      {
         add(object);
      }

      return false;
   }

   public void popGroup(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      while (list.size() > 0)
      {
         TeXObject object = list.pop();

         if (object instanceof Group)
         {
            add(object);
            continue;
         }

         TeXObjectList expanded = null;

         if (object instanceof Expandable)
         {
            expanded = ((Expandable)object).expandfully(parser, list);
         }

         if (expanded == null)
         {
            if (checkObj(parser, object, list)) return;
         }
         else
         {
            while (expanded.size() > 0)
            {
               object = expanded.pop();
               if (checkObj(parser, object, expanded)) return;
            }
         }
      }
   }

   public void popGroup(TeXParser parser)
     throws IOException
   {
      while (true)
      {
         TeXObject object = parser.popStack();

         if (object instanceof Group)
         {
            add(object);
            continue;
         }

         TeXObjectList expanded = null;

         if (object instanceof Expandable)
         {
            expanded = ((Expandable)object).expandfully(parser);
         }

         if (expanded == null)
         {
            if (checkObj(parser, object)) return;
         }
         else
         {
            while (expanded.size() > 0)
            {
               object = expanded.pop();
               if (checkObj(parser, object, expanded)) return;
            }
         }
      }

   }

   public void process(TeXParser parser)
    throws IOException
   {
      parser.startGroup();

      TeXSettings settings = parser.getSettings();
      int orgMode = settings.getCurrentMode();
      settings.setMode(mode);

      TeXObject object = parser.getListener().getControlSequence(getName());

      if (object instanceof Declaration)
      {
         pushDeclaration((Declaration)object);
      }

      object.process(parser, this);

      processList(parser);

      settings.setMode(orgMode);
      parser.endGroup();
   }

   public int getMode()
   {
      return mode;
   }

   private String name;

   private int mode = TeXSettings.INHERIT;
}
