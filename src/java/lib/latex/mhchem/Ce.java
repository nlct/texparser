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

   private TeXObject processMathGroup(TeXParser parser, MathGroup mgrp)
   {
      TeXObjectList list = new TeXObjectList();

      for (TeXObject obj : mgrp)
      {
         if (obj instanceof ControlSequence)
         {
            String str = getMathSymbol((ControlSequence)obj);

            if (obj == null) return mgrp;

            list.add(parser.getListener().createString(str));
         }
         else if (!(obj instanceof CharObject))
         {
            return mgrp;
         }
         else
         {
            int code = ((CharObject)obj).getCharCode();

            if (code == 'h')
            {
               list.add(parser.getListener().getOther(0x210E));
            }
            else if (code >= 'a' && code <= 'z')
            {
               list.add(parser.getListener().getOther(code-'a'+0x1D44E));
            }
            else if (code >= 'A' && code <= 'Z')
            {
               list.add(parser.getListener().getOther(code-'A'+0x1D434));
            }
            else
            {
               return mgrp;
            }
         }
      }

      return list;
   }

   private String getSymbol(ControlSequence cs)
   {
      String name = cs.getName();

      for (int i = 0; i < KNOWN_COMMANDS.length; i++)
      {
         if (name.equals(KNOWN_COMMANDS[i][0]))
         {
            return KNOWN_COMMANDS[i][1];
         }
      }

      return null;
   }

   private String getMathSymbol(ControlSequence cs)
   {
      String name = cs.getName();

      for (int i = 0; i < KNOWN_MATH_COMMANDS.length; i++)
      {
         if (name.equals(KNOWN_MATH_COMMANDS[i][0]))
         {
            return KNOWN_MATH_COMMANDS[i][1];
         }
      }

      return null;
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
         else if (obj instanceof ControlSequence)
         {
            String str = getSymbol((ControlSequence)obj);

            if (str == null)
            {
               stack.add(obj);
            }
            else
            {
               stack.add(parser.getListener().createString(str));
            }
         }
         else if (obj instanceof MathGroup)
         {
            stack.add(processMathGroup(parser, (MathGroup)obj));
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

   private static final String[][] KNOWN_COMMANDS = new String[][]
   {
      new String[]{"alpha", ""+(char)0x03B1},
      new String[]{"beta", ""+(char)0x03B2},
      new String[]{"gamma", ""+(char)0x03B3},
      new String[]{"delta", ""+(char)0x03B4},
      new String[]{"varepsilon", ""+(char)0x03B5},
      new String[]{"zeta", ""+(char)0x03B6},
      new String[]{"eta", ""+(char)0x03B7},
      new String[]{"theta", ""+(char)0x03B8},
      new String[]{"iota", ""+(char)0x03B9},
      new String[]{"kappa", ""+(char)0x03BA},
      new String[]{"lambda", ""+(char)0x03BB},
      new String[]{"mu", ""+(char)0x03BC},
      new String[]{"nu", ""+(char)0x03BD},
      new String[]{"xi", ""+(char)0x03BE},
      new String[]{"omicron", ""+(char)0x03BF},
      new String[]{"pi", ""+(char)0x03C0},
      new String[]{"rho", ""+(char)0x03C1},
      new String[]{"varsigma", ""+(char)0x03C2},
      new String[]{"sigma", ""+(char)0x03C3},
      new String[]{"tau", ""+(char)0x03C4},
      new String[]{"upsilon", ""+(char)0x03C5},
      new String[]{"phi", ""+(char)0x03C6},
      new String[]{"chi", ""+(char)0x03C7},
      new String[]{"psi", ""+(char)0x03C8},
      new String[]{"omega", ""+(char)0x03C9},
      new String[]{"epsilon", ""+(char)0x03F5},
      new String[]{"Alpha", ""+(char)0x0391},
      new String[]{"Beta", ""+(char)0x0392},
      new String[]{"Gamma", ""+(char)0x0393},
      new String[]{"Delta", ""+(char)0x0394},
      new String[]{"Epsilon", ""+(char)0x0395},
      new String[]{"Zeta", ""+(char)0x0396},
      new String[]{"Eta", ""+(char)0x0397},
      new String[]{"Theta", ""+(char)0x0398},
      new String[]{"Iota", ""+(char)0x0399},
      new String[]{"Kappa", ""+(char)0x039A},
      new String[]{"Lambda", ""+(char)0x039B},
      new String[]{"Mu", ""+(char)0x039C},
      new String[]{"Nu", ""+(char)0x039D},
      new String[]{"Xi", ""+(char)0x039E},
      new String[]{"Omicron", ""+(char)0x039F},
      new String[]{"Pi", ""+(char)0x03A0},
      new String[]{"Rho", ""+(char)0x03A1},
      new String[]{"Sigma", ""+(char)0x03A3},
      new String[]{"Tau", ""+(char)0x03A4},
      new String[]{"Upsilon", ""+(char)0x03A5},
      new String[]{"Phi", ""+(char)0x03A6},
      new String[]{"Chi", ""+(char)0x03A7},
      new String[]{"Psi", ""+(char)0x03A8},
      new String[]{"Omega", ""+(char)0x03A9},
   };

   private static final String[][] KNOWN_MATH_COMMANDS = new String[][]
   {
      new String[]{"alpha", new String(new int[]{0x1D6FC}, 0, 1)},
      new String[]{"beta", new String(new int[]{0x1D6FD}, 0, 1)},
      new String[]{"gamma", new String(new int[]{0x1D6FE}, 0, 1)},
      new String[]{"delta", new String(new int[]{0x1D6FF}, 0, 1)},
      new String[]{"varepsilon", new String(new int[]{0x1D700}, 0, 1)},
      new String[]{"zeta", new String(new int[]{0x1D701}, 0, 1)},
      new String[]{"eta", new String(new int[]{0x1D702}, 0, 1)},
      new String[]{"theta", new String(new int[]{0x1D703}, 0, 1)},
      new String[]{"iota", new String(new int[]{0x1D704}, 0, 1)},
      new String[]{"kappa", new String(new int[]{0x1D705}, 0, 1)},
      new String[]{"lambda", new String(new int[]{0x1D706}, 0, 1)},
      new String[]{"mu", new String(new int[]{0x1D707}, 0, 1)},
      new String[]{"nu", new String(new int[]{0x1D708}, 0, 1)},
      new String[]{"xi", new String(new int[]{0x1D709}, 0, 1)},
      new String[]{"omicron", new String(new int[]{0x1D70A}, 0, 1)},
      new String[]{"pi", new String(new int[]{0x1D70B}, 0, 1)},
      new String[]{"rho", new String(new int[]{0x1D70C}, 0, 1)},
      new String[]{"varsigma", new String(new int[]{0x1D70D}, 0, 1)},
      new String[]{"sigma", new String(new int[]{0x1D70E}, 0, 1)},
      new String[]{"tau", new String(new int[]{0x1D70F}, 0, 1)},
      new String[]{"upsilon", new String(new int[]{0x1D710}, 0, 1)},
      new String[]{"varphi", new String(new int[]{0x1D711}, 0, 1)},
      new String[]{"chi", new String(new int[]{0x1D712}, 0, 1)},
      new String[]{"psi", new String(new int[]{0x1D713}, 0, 1)},
      new String[]{"omega", new String(new int[]{0x1D714}, 0, 1)},
      new String[]{"epsilon", new String(new int[]{0x1D716}, 0, 1)},
      new String[]{"vartheta", new String(new int[]{0x1D717}, 0, 1)},
      new String[]{"varkappa", new String(new int[]{0x1D718}, 0, 1)},
      new String[]{"phi", new String(new int[]{0x1D719}, 0, 1)},
      new String[]{"varrho", new String(new int[]{0x1D71A}, 0, 1)},
      new String[]{"varpi", new String(new int[]{0x1D71B}, 0, 1)},
      new String[]{"Alpha", new String(new int[]{0x1D6E2}, 0, 1)},
      new String[]{"Beta", new String(new int[]{0x1D6E3}, 0, 1)},
      new String[]{"Gamma", new String(new int[]{0x1D6E4}, 0, 1)},
      new String[]{"Delta", new String(new int[]{0x1D6E5}, 0, 1)},
      new String[]{"Epsilon", new String(new int[]{0x1D6E6}, 0, 1)},
      new String[]{"Zeta", new String(new int[]{0x1D6E7}, 0, 1)},
      new String[]{"Eta", new String(new int[]{0x1D6E8}, 0, 1)},
      new String[]{"Theta", new String(new int[]{0x1D6E9}, 0, 1)},
      new String[]{"Iota", new String(new int[]{0x1D6EA}, 0, 1)},
      new String[]{"Kappa", new String(new int[]{0x1D6EB}, 0, 1)},
      new String[]{"Lambda", new String(new int[]{0x1D6EC}, 0, 1)},
      new String[]{"Mu", new String(new int[]{0x1D6ED}, 0, 1)},
      new String[]{"Nu", new String(new int[]{0x1D6EE}, 0, 1)},
      new String[]{"Xi", new String(new int[]{0x1D6EF}, 0, 1)},
      new String[]{"Omicron", new String(new int[]{0x1D6F0}, 0, 1)},
      new String[]{"Pi", new String(new int[]{0x1D6F1}, 0, 1)},
      new String[]{"Rho", new String(new int[]{0x1D6F2}, 0, 1)},
      new String[]{"Theta", new String(new int[]{0x1D6F3}, 0, 1)},
      new String[]{"Sigma", new String(new int[]{0x1D6F4}, 0, 1)},
      new String[]{"Tau", new String(new int[]{0x1D6F5}, 0, 1)},
      new String[]{"Upsilon", new String(new int[]{0x1D6F6}, 0, 1)},
      new String[]{"Phi", new String(new int[]{0x1D6F7}, 0, 1)},
      new String[]{"Chi", new String(new int[]{0x1D6F8}, 0, 1)},
      new String[]{"Psi", new String(new int[]{0x1D6F9}, 0, 1)},
      new String[]{"Omega", new String(new int[]{0x1D6FA}, 0, 1)},
      new String[]{"nabla", new String(new int[]{0x1D6FB}, 0, 1)},
      new String[]{"infty", ""+(char)0x221E},
   };
}
