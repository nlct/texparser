/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class Input extends ControlSequence
{
   public Input()
   {
      this("input");
   }

   public Input(String name)
   {
      this(name, NOT_FOUND_ACTION_ERROR);
   }

   public Input(String name, byte notFoundAction)
   {
      super(name);
      this.notFoundAction = notFoundAction;
   }

   @Override
   public Object clone()
   {
      return new Input(getName(), notFoundAction);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      if (!(arg instanceof TeXPathObject)
           && arg.canExpand() && arg instanceof Expandable)
      {
         arg = TeXParserUtils.expandFully(arg, parser, stack);
      }

      if (!doInput(parser, arg, stack))
      {
         switch (notFoundAction)
         {
            case NOT_FOUND_ACTION_WARN:

              TeXApp texapp = parser.getListener().getTeXApp();

              texapp.warning(parser, texapp.getMessage(
                 TeXSyntaxException.ERROR_FILE_NOT_FOUND, 
                  arg.toString(parser)));
            break;
            case NOT_FOUND_ACTION_ERROR:
               throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_FILE_NOT_FOUND, 
               arg.toString(parser));
         }
      }
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   protected String getDefaultExtension()
   {
      return "tex";
   }

   protected String toBasename(TeXParser parser, TeXObject arg)
       throws IOException
   {
      return arg.toString(parser);
   }

   protected boolean doInput(TeXParser parser, TeXObject arg, TeXObjectList stack)
       throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXPath texPath;

      if (arg instanceof TeXPathObject)
      {
         texPath = ((TeXPathObject)arg).getTeXPath();
      }
      else
      {
         texPath = new TeXPath(parser, toBasename(parser, arg),
           getDefaultExtension());
      }

      listener.addFileReference(texPath);

      return listener.input(texPath, stack);
  }

  private byte notFoundAction=NOT_FOUND_ACTION_ERROR;

  public static final byte NOT_FOUND_ACTION_ERROR=0;
  public static final byte NOT_FOUND_ACTION_WARN=1;
  public static final byte NOT_FOUND_ACTION_IGNORE=2;
}
