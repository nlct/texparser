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
package com.dickimawbooks.texparserlib.latex.ifthen;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.primitives.BeginGroup;
import com.dickimawbooks.texparserlib.primitives.EndGroup;

public class IfThenSty extends LaTeXSty
{
   public IfThenSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "ifthen", listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      registerControlSequence(new IfThenElse(this));

      addConditionalControlSequence(new IFisodd());
      addConditionalControlSequence(new IFand());
      addConditionalControlSequence(new IFand("AND"));
      addConditionalControlSequence(new IFor());
      addConditionalControlSequence(new IFor("OR"));
      addConditionalControlSequence(new IFnot());
      addConditionalControlSequence(new IFnot("NOT"));
      addConditionalControlSequence(new BeginConditionGroup());
      addConditionalControlSequence(new EndConditionGroup());
   }

   public void addConditionalControlSequence(ControlSequence cs)
   {
      if (localControlSequences == null)
      {
         localControlSequences = new Vector<ControlSequence>();
      }

      localControlSequences.add(cs);
   }

   protected TeXBoolean popNumericalCondition(TeXObjectList stack)
     throws IOException
   {
      byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;

      TeXParser parser = getListener().getParser();

      Numerical num1 = stack.popNumerical(parser);
      TeXObject signArg = stack.popToken(popStyle);

      Numerical num2 = stack.popNumerical(parser);

      if (!(signArg instanceof CharObject))
      {
         throw new LaTeXSyntaxException(parser,
              ERROR_INVALID_CONDITION, 
               String.format("%s %s %s", num1, signArg, num2));
      }

      int cp = ((CharObject)signArg).getCharCode();

      if (cp == '=')
      {
         return new UserBoolean(num1.number(parser) == num2.number(parser));
      }

      if (cp == '<')
      {
         return new UserBoolean(num1.number(parser) < num2.number(parser));
      }

      if (cp == '>')
      {
         return new UserBoolean(num1.number(parser) > num2.number(parser));
      }

      throw new LaTeXSyntaxException(parser,
           ERROR_INVALID_CONDITION, 
            String.format("%s %s %s", num1, signArg, num2));
   }

   protected TeXObject popCondition(TeXObjectList stack)
     throws IOException
   {
      byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;

      TeXParser parser = getListener().getParser();

      TeXObject obj = stack.peekStack(popStyle);

      if (obj == null) return null;

      if (obj instanceof ConditionGroup || obj instanceof TeXBoolean)
      {
         return stack.popToken(popStyle);
      }
      else
      {
         return popNumericalCondition(stack);
      }
   }

   protected boolean parseCondition(TeXObjectList stack)
     throws IOException
   {
      byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;

      TeXParser parser = getListener().getParser();

      TeXObject obj = stack.peekStack(popStyle);

      UnaryConditionalOperator unaryOp = null;

      if (obj instanceof UnaryConditionalOperator)
      {
         unaryOp = (UnaryConditionalOperator)stack.popToken(popStyle);
         obj = popCondition(stack);
      }
      else
      {
         obj = popCondition(stack);
      }

      if (obj == null)
      {
         throw new LaTeXSyntaxException(parser,
              ERROR_INVALID_CONDITION, stack.toString(parser));
      }

      boolean result;

      if (obj instanceof TeXBoolean)
      {
         result = ((TeXBoolean)obj).booleanValue();
      }
      else if (obj instanceof ConditionGroup)
      {
         result = parseCondition((ConditionGroup)obj);
      }
      else
      {
         throw new LaTeXSyntaxException(parser,
              ERROR_INVALID_CONDITION, obj.toString(parser));
      }

      if (unaryOp != null)
      {
         result = unaryOp.evaluate(result);
      }

      while ((obj = stack.peekStack(popStyle)) != null)
      {
         if (obj instanceof UnaryConditionalOperator)
         {
            unaryOp = (UnaryConditionalOperator)stack.popToken(popStyle);
         }
         else
         {
            unaryOp = null;
         }

         obj = stack.popToken(popStyle);

         if (obj == null)
         {
            if (unaryOp == null)
            {
               break;
            }
            else
            {
               throw new LaTeXSyntaxException(parser,
                    ERROR_INVALID_CONDITION, 
                    ((TeXObject)unaryOp).toString(parser));
            }
         }

         if (!(obj instanceof BinaryConditionalOperator))
         {
            if (unaryOp == null)
            {
                throw new LaTeXSyntaxException(parser,
                    ERROR_INVALID_CONDITION,
                    String.format("%s %s", result, obj.toString(parser)));
            }
            else
            {
                throw new LaTeXSyntaxException(parser,
                    ERROR_INVALID_CONDITION,
                    String.format("%s %s %s", result,
                     unaryOp.toString(parser), obj.toString(parser)));
            }
         }

         BinaryConditionalOperator op = (BinaryConditionalOperator)obj;

         obj = popCondition(stack);

         if (obj instanceof TeXBoolean)
         {
            result = op.evaluate(result, ((TeXBoolean)obj).booleanValue());
         }
         else if (obj instanceof ConditionGroup)
         {
            result = op.evaluate(result, parseCondition((ConditionGroup)obj));
         }
         else if (obj == null)
         {
            throw new LaTeXSyntaxException(parser,
                 ERROR_INVALID_CONDITION, 
                  String.format("%s %s %s", result, 
                   op.toString(parser)));
         }
         else
         {
            throw new LaTeXSyntaxException(parser,
                 ERROR_INVALID_CONDITION, 
                  String.format("%s %s %s", result, 
                   op.toString(parser), obj.toString(parser)));
         }
      }

      return result;
   }

   public boolean evaluate(TeXObject condition)
     throws IOException
   {
      TeXParser parser = getListener().getParser();

      parser.startGroup();

      for (ControlSequence cs : localControlSequences)
      {
         parser.putControlSequence(true, cs);
      }

      if (condition instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)condition).expandfully(parser);

         if (expanded != null)
         {
            condition = expanded;
         }
      }

      parser.endGroup();

      if (!(condition instanceof TeXObjectList))
      {
         throw new LaTeXSyntaxException(parser,
              ERROR_INVALID_CONDITION, condition.toString(parser));
      }

      return parseCondition((TeXObjectList)condition);
   }

   private Vector<ControlSequence> localControlSequences;

   public static final String ERROR_INVALID_CONDITION="ifthen.invalid.condition";
}
