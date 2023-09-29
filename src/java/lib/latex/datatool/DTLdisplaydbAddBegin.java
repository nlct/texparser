/*
    Copyright (C) 2023 Nicola L.C. Talbot
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
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLdisplaydbAddBegin extends ControlSequence
{
   public DTLdisplaydbAddBegin()
   {
      this("DTLdisplaydbAddBegin");
   }

   public DTLdisplaydbAddBegin(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DTLdisplaydbAddBegin(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TokenListCommand contentTl = listener.popTokenListCommand(parser, stack);
      TokenListCommand alignTl = listener.popTokenListCommand(parser, stack);
      TeXObject header = popArg(parser, stack);

      contentTl.append(listener.getControlSequence("begin"));

      contentTl.append(listener.createGroup(
        parser.expandToString(listener.getControlSequence("dtldisplaydbenv"), stack)));

      String valign = parser.expandToString(
       listener.getControlSequence("dtldisplayvalign"), stack);

      if (!valign.isEmpty())
      {
         contentTl.rightConcat(listener.createString( "["+valign+"]"));
      }

      Group grp = listener.createGroup();

      grp.addAll(alignTl.getContent());
      contentTl.append(grp);

      contentTl.appendValue(listener.getControlSequence("dtldisplaystarttab"), 
        parser, stack);

      if (!header.isEmpty())
      {
         if (parser.isStack(header))
         {
            contentTl.rightConcat((TeXObjectList)header);
         }
         else
         {
            contentTl.append(header);
         }
      }

      contentTl.appendValue(
        listener.getControlSequence("l_datatool_post_head_tl"), parser, stack);

      contentTl.appendValue(listener.getControlSequence("dtldisplaycr"), 
        parser, stack);
      contentTl.appendValue(listener.getControlSequence("dtldisplayafterhead"), 
        parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
