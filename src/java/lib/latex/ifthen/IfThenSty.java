/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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

   @Override
   public void addDefinitions()
   {
      registerControlSequence(new IfThenElse(this));

      addConditionalControlSequence(new IFisodd(), "isodd");
      addConditionalControlSequence(new IFand(), "and", "AND");
      addConditionalControlSequence(new IFor(), "or", "OR");
      addConditionalControlSequence(new IFnot(), "not", "NOT");
      addConditionalControlSequence(new BeginConditionGroup(), "(");
      addConditionalControlSequence(new EndConditionGroup(), ")");
   }

   public void addConditionalControlSequence(ControlSequence cs, String... other)
   {
      registerControlSequence(cs);

      if (localControlSequences == null)
      {
         localControlSequences = new Vector<ControlSequence>();
      }

      for (String name : other)
      {
         localControlSequences.add(new AssignedControlSequence(name, cs));
      }
   }

   protected TeXBoolean popNumericalCondition(AbstractTeXObjectList stack)
     throws IOException
   {
      PopStyle popStyle = PopStyle.IGNORE_LEADING_SPACE;

      TeXParser parser = getListener().getParser();

      Numerical num1 = stack.popNumerical(parser);
      TeXObject signArg = stack.popToken(popStyle);
      Numerical num2 = stack.popNumerical(parser);

      int cp = parser.toCodePoint(signArg);

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

   protected TeXObject popCondition(AbstractTeXObjectList stack)
     throws IOException
   {
      PopStyle popStyle = PopStyle.IGNORE_LEADING_SPACE;

      TeXParser parser = getListener().getParser();

      TeXObject obj = stack.peekStack(popStyle);

      if (obj == null) return null;

      BeginGroupObject bg = parser.toBeginGroup(obj);

      if (bg != null)
      {
         stack.popToken(popStyle);
         AbstractGroup group = bg.createGroup(parser);
         stack.popRemainingGroup(parser, group, popStyle, bg);
         return group;
      }
      else if (obj instanceof ConditionGroup || obj instanceof TeXBoolean)
      {
         return stack.popToken(popStyle);
      }
      else
      {
         return popNumericalCondition(stack);
      }
   }

   protected boolean parseCondition(AbstractTeXObjectList stack)
     throws IOException
   {
      PopStyle popStyle = PopStyle.IGNORE_LEADING_SPACE;

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

   public void pushLocalControlSequences()
   {
      TeXParser parser = getListener().getParser();

      for (ControlSequence cs : localControlSequences)
      {
         parser.putControlSequence(true, cs);
      }
   }

   public boolean evaluate(TeXObjectList stack, TeXObject condition)
     throws IOException
   {
      TeXParser parser = getListener().getParser();

      parser.startGroup();
      pushLocalControlSequences();

      boolean result;

      try
      {
         condition = parser.expandFully(condition, stack);

         result = evaluate(condition);
      }
      finally
      {
         parser.endGroup();
      }

      return result;
   }

   protected boolean evaluate(TeXObject condition)
     throws IOException
   {
      TeXParser parser = getListener().getParser();

      boolean result;

      TeXBoolean bool = parser.toBoolean(condition);

      if (bool != null)
      {
         result = bool.booleanValue();
      }
      else if (condition instanceof AbstractTeXObjectList)
      {
         result = parseCondition((AbstractTeXObjectList)condition);
      }
      else
      {
         throw new LaTeXSyntaxException(parser,
              ERROR_INVALID_CONDITION, condition.toString(parser));
      }

      return result;
   }

   private Vector<ControlSequence> localControlSequences;

   public static final String ERROR_INVALID_CONDITION="ifthen.invalid.condition";
}
