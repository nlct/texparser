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
package com.dickimawbooks.texparserlib.latex.tcilatex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

/*
 * This is an attempt to convert the definitions use by Scientific
 * Word export to LaTeX. This is mostly guesswork as to what this
 * command is actually supposed to do.
 */

public class SWFrame extends ControlSequence
{
   public SWFrame()
   {
      this("FRAME");
   }

   public SWFrame(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new SWFrame(getName());
   }

   private void addArg(TeXParser parser, TeXObjectList list, TeXObject arg)
   {
      Group grp = parser.getListener().createGroup();

      if (arg instanceof TeXObjectList)
      {
         grp.addAll((TeXObjectList)arg);
      }
      else
      {
         grp.add(arg);
      }

      list.add(grp);
   }

   // Don't know the purpose of arg (first argument of \\Frame)
   // and arg2 (sixth argument of \\Frame).

   private void processArg(TeXParser parser, TeXObject arg,
     TeXObject widthArg, TeXObject heightArg,
     TeXObject depthArg, TeXObject captionArg,
     TeXObject arg2,
     TeXObject typeArg, TeXObject contentsArg)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObjectList original = new TeXObjectList();

      original.add(this);

      addArg(parser, original, arg);
      addArg(parser, original, widthArg);
      addArg(parser, original, heightArg);
      addArg(parser, original, depthArg);
      addArg(parser, original, captionArg);
      addArg(parser, original, arg2);
      addArg(parser, original, typeArg);
      addArg(parser, original, contentsArg);

      String originalStr = original.toString(parser);

      TeXObjectList expanded = null;

      if (typeArg instanceof Expandable)
      {
         expanded = ((Expandable)typeArg).expandfully(parser);
      }

      String envName = (expanded == null ? typeArg.toString(parser)
         : expanded.toString(parser)).toLowerCase();

      TeXObjectList env = new TeXObjectList();

      if (contentsArg instanceof Expandable)
      {
         expanded = ((Expandable)contentsArg).expandonce(parser);

         if (expanded != null)
         {
            contentsArg = expanded;
         }
      }

      if (captionArg instanceof Expandable)
      {
         expanded = ((Expandable)captionArg).expandonce(parser);

         if (expanded != null)
         {
            captionArg = expanded;
         }
      }

      Group grpName = listener.createGroup(envName);

      env.add(listener.getControlSequence("begin"));
      env.add(grpName);

      env.add(listener.getControlSequence("centering"));

      if (envName.equals("figure"))
      {
         env.add(contentsArg);
         env.add(captionArg);
      }
      else
      {
         env.add(captionArg);
         env.add(contentsArg);
      }

      env.add(listener.getControlSequence("end"));
      env.add(grpName);

      listener.substituting(originalStr, env.toString(parser));

      env.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      processArg(parser, 
        stack.popArg(parser),
        stack.popArg(parser),
        stack.popArg(parser),
        stack.popArg(parser),
        stack.popArg(parser),
        stack.popArg(parser),
        stack.popArg(parser),
        stack.popArg(parser));
   }

   public void process(TeXParser parser)
      throws IOException
   {
      processArg(parser, 
         parser.popNextArg(),
         parser.popNextArg(),
         parser.popNextArg(),
         parser.popNextArg(),
         parser.popNextArg(),
         parser.popNextArg(),
         parser.popNextArg(),
         parser.popNextArg());
   }
}
