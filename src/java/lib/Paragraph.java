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
package com.dickimawbooks.texparserlib;

import java.io.IOException;
import java.util.Vector;
import java.util.ArrayDeque;

import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.Begin;
import com.dickimawbooks.texparserlib.latex.End;

/**
 * A paragraph with margins. A null margin indicates 0pt.
 */

public class Paragraph extends DataObjectList
{
   public Paragraph()
   {
      super();
   }

   public Paragraph(int capacity)
   {
      super(capacity);
   }

   public Paragraph(TeXParserListener listener, String text)
   {
      super(listener, text);
   }

   public void setLeftMargin(TeXDimension margin)
   {
      leftMargin = margin;
   }

   public void setRightMargin(TeXDimension margin)
   {
      rightMargin = margin;
   }

   public void setTopMargin(TeXDimension margin)
   {
      topMargin = margin;
   }

   public void setBottomMargin(TeXDimension margin)
   {
      bottomMargin = margin;
   }

   public void setParIndent(TeXDimension indent)
   {
      parIndent = indent;
   }

   public TeXDimension getLeftMargin()
   {
      return leftMargin;
   }

   public TeXDimension getRightMargin()
   {
      return rightMargin;
   }

   public TeXDimension getTopMargin()
   {
      return topMargin;
   }

   public TeXDimension getBottomMargin()
   {
      return bottomMargin;
   }

   public TeXDimension getParIndent()
   {
      return parIndent;
   }

   @Override
   public boolean isStack()
   {
      return false;
   }

   @Override
   public TeXObjectList createList()
   {
      return new Paragraph(capacity());
   }

   protected TeXObject getHead(TeXParser parser)
   {
      return null;
   }

   protected TeXObject getTail(TeXParser parser)
   {
      return parser.getListener().getPar();
   }

   public boolean isIncomplete()
   {
      return incomplete;
   }

   public void build(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      boolean parFound = false;
      byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;

      boolean isStart = !incomplete;

      String currenv = null;
      Vector<String> envs = null;

      TeXDimension indent = null;

      if (isStart)
      {
         TeXDimension dim = parser.getSettings().getCurrentParIndent();

         if (dim != null)
         {
            indent = new UserDimension();
            indent.advance(parser, dim);
         }
      }

      while (!parFound)
      {
         TeXObject token = stack.popToken(popStyle);

         if (token == null)
         {
            break;
         }

         popStyle = (byte)0;

         token = TeXParserUtils.resolve(token, parser);

         if (token.canExpand() && token instanceof Expandable
              && !token.isDataObject())
         {
            stack.push(TeXParserUtils.expandOnce(token, parser, stack), true);
         }
         else if (token.isPar())
         {
            parFound = true;
         }
         else if (token instanceof Else || token instanceof Fi
                || token instanceof Or)
         {
            stack.push(token);
            break;
         }
         else if (token instanceof ParIndent && isStart)
         {
            if (parser == stack)
            {
               token.process(parser);
            }
            else
            {
               token.process(parser, stack);
            }

            TeXDimension dim = parser.getSettings().getCurrentParIndent();

            if (dim != null)
            {
               if (indent == null)
               {
                  indent = new UserDimension();
               }

               indent.advance(parser, dim);
            }
         }
         else if (isStart && token instanceof SpacingObject
                   && ((SpacingObject)token).getDirection() == Direction.HORIZONTAL)
         {
            if (indent == null)
            {
               indent = new UserDimension();
            }

            indent.advance(parser, ((SpacingObject)token).getSize(parser, stack));
         }
         else if (token instanceof SpacingObject
                   && ((SpacingObject)token).getDirection() == Direction.VERTICAL)
         {
            parFound = true;
            stack.push(token);
         }
         else if (token instanceof Relax)
         {
            add(token);
         }
         else if (token instanceof Begin)
         {
            isStart = false;

            add(token, true);

            currenv = ((Begin)token).popLabelString(parser, stack);

            add(parser.getListener().createGroup(currenv));

            if (envs == null)
            {
               envs = new Vector<String>();
            }

            envs.add(currenv);
         }
         else if (token instanceof End)
         {
            isStart = false;

            String name = ((End)token).popLabelString(parser, stack);

            if (envs == null || envs.isEmpty() || !name.equals(currenv))
            {
               stack.push(parser.getListener().createGroup(name));
               stack.push(token);

               parFound = true;
            }
            else
            {
               add(token, true);
               add(parser.getListener().createGroup(name));
               currenv = envs.remove(envs.size()-1);
            }
         }
         else
         {
            isStart = false;

            add(token, true);
         }
      }

      if (indent != null)
      {
         setParIndent(indent);
      }

      incomplete = !parFound;
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (incomplete)
      {
         build(parser, stack);
         incomplete = false;
      }

      TeXObject head = getHead(parser);

      if (head != null)
      {
         head.process(parser, stack);
      }

      super.process(parser, stack);

      TeXObject tail = getTail(parser);

      if (tail != null)
      {
         tail.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      if (incomplete)
      {
         build(parser, parser);
         incomplete = false;
      }

      TeXObject head = getHead(parser);

      if (head != null)
      {
         head.process(parser);
      }

      super.process(parser);

      TeXObject tail = getTail(parser);

      if (tail != null)
      {
         tail.process(parser);
      }
   }

   @Override
   protected String toStringExtraIdentifier()
   {
      return String.format("(left=%s,right=%s,top=%s,bottom=%s,indent=%s,incomplete=%s)",
       leftMargin, rightMargin, topMargin, bottomMargin, parIndent, incomplete);
   }

   protected TeXDimension leftMargin, rightMargin, topMargin, bottomMargin, parIndent;

   protected boolean incomplete = false;
}
