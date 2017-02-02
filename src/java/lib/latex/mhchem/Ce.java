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
package com.dickimawbooks.texparserlib.latex.mhchem;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Ce extends ControlSequence
{
   public Ce()
   {
      this("ce");
   }

   public Ce(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Ce(getName());
   }

   private void popScript(TeXParser parser, TeXObjectList argList,
     Group grp)
     throws IOException
   {
      while (argList.size() > 0)
      {
         TeXObject obj = argList.popStack(parser);

         if (!(obj instanceof CharObject))
         {
            argList.push(obj);
            return;
         }

         int code = ((CharObject)obj).getCharCode();

         if (Character.isDigit(code) || code == '+' || code == '-')
         {
            grp.add(obj);
         }
         else
         {
            argList.push(obj);
            break;
         }
      }
   }

   private boolean isNumeric(TeXObject obj)
   {
      if (obj instanceof Numerical
        || (obj instanceof CharObject
          && Character.isDigit(((CharObject)obj).getCharCode())))
      {
         return true;
      }

      if (!(obj instanceof TeXObjectList))
      {
         return false;
      }

      TeXObjectList list = (TeXObjectList)obj;

      int n = list.size();

      if (n == 0)
      {
         return false;
      }

      TeXObject first = list.get(0);

      if (first instanceof CharObject)
      {
         int code = ((CharObject)first).getCharCode();

         if (code != '+' && code != '-' && !Character.isDigit(code))
         {
            return false;
         }
      }
      else if (!isNumeric(first))
      {
         return false;
      }

      for (int i = 1; i < n; i++)
      {
         if (!isNumeric(list.get(i))) return false;
      }

      return true;
   }

   private void pushFraction(TeXParser parser, TeXObjectList stack,
     TeXObject numObj, TeXObject denomObj)
     throws IOException
   {
      int num, denom;

      try
      {
         if (numObj instanceof Numerical)
         {
            num = ((Numerical)numObj).number(parser);
         }
         else if (numObj instanceof Group)
         {
            num = Integer.parseInt(((Group)numObj).toList().toString(parser));
         }
         else
         {
            num = Integer.parseInt(numObj.toString(parser));
         }

         if (denomObj instanceof Numerical)
         {
            denom = ((Numerical)denomObj).number(parser);
         }
         else if (denomObj instanceof Group)
         {
            denom = Integer.parseInt(
              ((Group)denomObj).toList().toString(parser));
         }
         else
         {
            denom = Integer.parseInt(denomObj.toString(parser));
         }

         if (num == 1 && denom == 2)
         {
            stack.push(parser.getListener().getOther(0x00BD));
         }
         else if (num == 1 && denom == 4)
         {
            stack.push(parser.getListener().getOther(0x00BC));
         }
         else if (num == 3 && denom == 4)
         {
            stack.push(parser.getListener().getOther(0x00BE));
         }
         else if (num == 1 && denom == 7)
         {
            stack.push(parser.getListener().getOther(0x2150));
         }
         else if (num == 1 && denom == 9)
         {
            stack.push(parser.getListener().getOther(0x2151));
         }
         else if (num == 1 && denom == 10)
         {
            stack.push(parser.getListener().getOther(0x2152));
         }
         else if (num == 1 && denom == 3)
         {
            stack.push(parser.getListener().getOther(0x2153));
         }
         else if (num == 2 && denom == 3)
         {
            stack.push(parser.getListener().getOther(0x2154));
         }
         else if (num == 1 && denom == 5)
         {
            stack.push(parser.getListener().getOther(0x2155));
         }
         else if (num == 2 && denom == 5)
         {
            stack.push(parser.getListener().getOther(0x2156));
         }
         else if (num == 3 && denom == 5)
         {
            stack.push(parser.getListener().getOther(0x2157));
         }
         else if (num == 4 && denom == 5)
         {
            stack.push(parser.getListener().getOther(0x2158));
         }
         else if (num == 1 && denom == 6)
         {
            stack.push(parser.getListener().getOther(0x2159));
         }
         else if (num == 5 && denom == 6)
         {
            stack.push(parser.getListener().getOther(0x215A));
         }
         else if (num == 1 && denom == 8)
         {
            stack.push(parser.getListener().getOther(0x215B));
         }
         else if (num == 3 && denom == 8)
         {
            stack.push(parser.getListener().getOther(0x215C));
         }
         else if (num == 5 && denom == 8)
         {
            stack.push(parser.getListener().getOther(0x215D));
         }
         else if (num == 7 && denom == 8)
         {
            stack.push(parser.getListener().getOther(0x215E));
         }
         else if (num == 0 && denom == 3)
         {
            stack.push(parser.getListener().getOther(0x2189));
         }
         else
         {
            stack.push(parser.getListener().getOther('('));
            stack.push(numObj);
            stack.push(parser.getListener().getOther('/'));
            stack.push(denomObj);
            stack.push(parser.getListener().getOther(')'));
         }
      }
      catch (NumberFormatException e)
      {
         stack.push(numObj);
         stack.push(parser.getListener().getOther('/'));
         stack.push(denomObj);
      }
   }

   protected TeXObject processArg(TeXParser parser, TeXObject arg)
     throws IOException
   {
      if (!(arg instanceof TeXObjectList))
      {
         return arg;
      }

      TeXObjectList argList = (TeXObjectList)arg;

      TeXObjectList stack = new TeXObjectList();

      boolean followsSpace = true;

      while (argList.size() > 0)
      {
         TeXObject obj = argList.popStack(parser);

         if (obj instanceof WhiteSpace)
         {
            followsSpace = true;
            stack.add(parser.getListener().getOther(SEP));
            continue;
         }

         if (obj instanceof CharObject)
         {
            int code = ((CharObject)obj).getCharCode();

            if (followsSpace)
            {
               if (code == '-')
               {
                  TeXObject nextObj = argList.firstElement();

                  if (nextObj != null && nextObj instanceof CharObject
                    && ((CharObject)nextObj).getCharCode() == '>')
                  {
                     nextObj = argList.popStack(parser);

                     stack.add(parser.getListener().getOther(0x27F6));

                     TeXObject opt = null;

                     if (!(argList.firstElement() instanceof WhiteSpace))
                     {
                        opt = argList.popArg(parser, '[', ']');
                     }

                     if (opt != null)
                     {
                        stack.add(new TeXCsRef("textsuperscript"));

                        if (opt instanceof Group)
                        {
                           stack.add(opt);
                        }
                        else if (opt instanceof TeXObjectList)
                        {
                           Group grp = parser.getListener().createGroup();
                           stack.add(grp);

                           grp.addAll((TeXObjectList)opt);
                        }
                        else
                        {
                           stack.add(opt);
                        }
                     }
                  }
                  else
                  {
                     stack.add(obj);
                  }
               }
               else if (Character.isDigit(code))
               {
                  argList.push(obj);

                  obj = argList.popNumber(parser);
                  stack.add(obj);
               }
               else
               {
                  stack.add(obj);
               }
            }
            else if (code == '/')
            {
               TeXObject nextObj = argList.firstElement();

               if (nextObj != null && isNumeric(nextObj) 
                    && stack.lastElement() != null)
               {
                  nextObj = argList.popNumber(parser);

                  pushFraction(parser, stack, stack.remove(stack.size()-1),
                     nextObj);
               }
               else
               {
                  stack.add(nextObj);
               }
            }
            else if (code == '+' || code == '-' || Character.isDigit(code))
            {
               stack.add(new TeXCsRef("textsubscript"));
               Group grp = parser.getListener().createGroup();
               stack.add(grp);

               grp.add(obj);

               popScript(parser, argList, grp);
            }
            else
            {
               stack.add(obj);
            }
         }
         else if (obj instanceof SpChar)
         {
            stack.add(new TeXCsRef("textsuperscript"));

            obj = argList.popStack(parser);
 
            if (!(obj instanceof CharObject))
            {
               stack.add(obj);
            }
            else
            {
               Group grp = parser.getListener().createGroup();
               stack.add(grp);

               grp.add(obj);

               popScript(parser, argList, grp);
            }
         }
         else if (obj instanceof SbChar)
         {
            stack.add(new TeXCsRef("textsubscript"));

            obj = argList.popStack(parser);
 
            if (!(obj instanceof CharObject))
            {
               stack.add(obj);
            }
            else
            {
               Group grp = parser.getListener().createGroup();
               stack.add(grp);

               grp.add(obj);

               popScript(parser, argList, grp);
            }
         }
         else
         {
            stack.add(obj);
         }

         followsSpace = false;
      }

      return stack;
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject arg = parser.popNextArg();

      processArg(parser, arg).process(parser);
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      TeXObject arg = list.popArg(parser);

      processArg(parser, arg).process(parser, list);
   }

   private static final int SEP = Space.MEDIUM_MATHEMATICAL_SPACE;
}
