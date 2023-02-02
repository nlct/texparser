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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.IfTrue;

public class AtFor extends Command
{
   public AtFor()
   {
      this("@for");
   }

   public AtFor(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new AtFor(getName());
   }

   public TeXObjectList expandLoop(TeXParser parser, TeXObjectList stack,
     GenericCommand cs, CsvList csvList, TeXObject code)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      boolean xfor = ((LaTeXParserListener)parser.getListener()).isStyLoaded(
        "xfor");

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         cs.getDefinition().clear();
         cs.getDefinition().add(obj);

         if (xfor)
         {
            if (i == csvList.size())
            {
               parser.putControlSequence(true, 
                 parser.getListener().createUndefinedCs("@nnil"));
            }
            else
            {
               parser.putControlSequence(true, new GenericCommand(true,
                  "@xfor@nextelement", null, 
                    new TeXObject[] {csvList.getValue(i+1)}));
            }
         }

         TeXObject loopBody = (TeXObject)code.clone();

         if (loopBody instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack)
            {
               expanded = ((Expandable)loopBody).expandonce(parser);
            }
            else
            {
               expanded = ((Expandable)loopBody).expandonce(parser, stack);
            }

            if (expanded != null)
            {
               loopBody = expanded;
            }
         }

         if (loopBody instanceof TeXObjectList 
               && !(loopBody instanceof Group))
         {
            list.addAll((TeXObjectList)loopBody);
         }
         else
         {
            list.add(loopBody);
         }

         if (xfor)
         {
            ControlSequence ifCs = parser.getControlSequence("if@endfor");

            if (ifCs instanceof IfTrue)
            {
               CsvList remainder = new CsvList();

               for (int j = i+1; j < csvList.size(); j++)
               {
                  remainder.add(csvList.get(j));
               }

               parser.putControlSequence(true, new GenericCommand(true,
                 "@forremainder", null, new TeXObject[] {remainder}));

               break;
            }
         }
      }

      return list;
   }

   public TeXObjectList fullyExpandLoop(TeXParser parser, TeXObjectList stack,
     GenericCommand cs, CsvList csvList, TeXObject code)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      boolean xfor = ((LaTeXParserListener)parser.getListener()).isStyLoaded(
        "xfor");

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         cs.getDefinition().clear();
         cs.getDefinition().add(obj);

         if (xfor)
         {
            if (i == csvList.size())
            {
               parser.putControlSequence(true, 
                 parser.getListener().createUndefinedCs("@nnil"));
            }
            else
            {
               parser.putControlSequence(true, new GenericCommand(true,
                  "@xfor@nextelement", null, 
                    new TeXObject[] {csvList.getValue(i+1)}));
            }
         }

         TeXObject loopBody = (TeXObject)code.clone();

         if (loopBody instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack)
            {
               expanded = ((Expandable)loopBody).expandfully(parser);
            }
            else
            {
               expanded = ((Expandable)loopBody).expandfully(parser, stack);
            }

            if (expanded != null)
            {
               loopBody = expanded;
            }
         }

         if (loopBody instanceof TeXObjectList 
               && !(loopBody instanceof Group))
         {
            list.addAll((TeXObjectList)loopBody);
         }
         else
         {
            list.add(loopBody);
         }

         if (xfor)
         {
            ControlSequence ifCs = parser.getControlSequence("if@endfor");

            if (ifCs instanceof IfTrue)
            {
               CsvList remainder = new CsvList();

               for (int j = i+1; j < csvList.size(); j++)
               {
                  remainder.add(csvList.get(j));
               }

               parser.putControlSequence(true, new GenericCommand(true,
                 "@forremainder", null, new TeXObject[] {remainder}));

               break;
            }
         }
      }

      return list;
   }

   public void processLoop(TeXParser parser, TeXObjectList stack,
     GenericCommand cs, CsvList csvList, TeXObject code)
     throws IOException
   {
      boolean xfor = ((LaTeXParserListener)parser.getListener()).isStyLoaded(
        "xfor");

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         cs.getDefinition().clear();
         cs.getDefinition().add(obj);

         if (xfor)
         {
            if (i == csvList.size())
            {
               parser.putControlSequence(true, 
                 parser.getListener().createUndefinedCs("@nnil"));
            }
            else
            {
               parser.putControlSequence(true, new GenericCommand(true,
                  "@xfor@nextelement", null, 
                    new TeXObject[] {csvList.getValue(i+1)}));
            }
         }

         TeXObject loopBody = (TeXObject)code.clone();

         if (loopBody instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack)
            {
               expanded = ((Expandable)loopBody).expandonce(parser);
            }
            else
            {
               expanded = ((Expandable)loopBody).expandonce(parser, stack);
            }

            if (expanded != null)
            {
               loopBody = expanded;
            }
         }

         if (parser == stack)
         {
            loopBody.process(parser);
         }
         else
         {
            loopBody.process(parser, stack);
         }

         if (xfor)
         {
            ControlSequence ifCs = parser.getControlSequence("if@endfor");

            if (ifCs instanceof IfTrue)
            {
               CsvList remainder = new CsvList();

               for (int j = i+1; j < csvList.size(); j++)
               {
                  remainder.add(csvList.get(j));
               }

               parser.putControlSequence(true, new GenericCommand(true,
                 "@forremainder", null, new TeXObject[] {remainder}));

               break;
            }
         }
      }

   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   protected CsvList argToCsvList(TeXObject arg, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      arg = TeXParserUtils.resolve(arg, parser);

      CsvList csvList = null;

      if (arg instanceof CsvList)
      {
         csvList = (CsvList)arg;
      }
      else if (arg instanceof GenericCommand)
      {
         TeXObjectList def = ((GenericCommand)arg).getDefinition();

         if (def instanceof CsvList)
         {
            csvList = (CsvList)def;
         }
         else if (def.size() == 1 && def.firstElement() instanceof CsvList)
         {
            csvList = (CsvList)def.firstElement();
         }
         else
         {
            arg = (TeXObject)def.clone();
         }
      }
      else
      {
         arg = TeXParserUtils.expandOnce(arg, parser, stack);
      }

      if (csvList == null)
      {
         csvList = CsvList.getList(parser, arg);
      }

      return csvList;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence csArg = popControlSequence(parser, stack);
      
      GenericCommand cs = new GenericCommand(csArg.getName());
      parser.putControlSequence(true, cs);

      TeXObject arg = stack.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);

      if (!(arg instanceof CharObject
             && ((CharObject)arg).getCharCode() == ':'))
      {
         throw new LaTeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_SYNTAX, getName());
      }

      arg = stack.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);

      if (!(arg instanceof CharObject
             && ((CharObject)arg).getCharCode() == '='))
      {
         throw new LaTeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_SYNTAX, getName());
      }

      TeXObjectList list = stack.popToCsMarker(parser, "do");

      if (list.size() == 1)
      {
         arg = list.firstElement();
      }
      else
      {
         arg = list;
      }

      CsvList csvList = argToCsvList(arg, parser, stack);

      TeXObject code = popArg(parser, stack);

      return expandLoop(parser, stack, cs, csvList, code);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence csArg = popControlSequence(parser, stack);

      GenericCommand cs = new GenericCommand(csArg.getName());
      parser.putControlSequence(true, cs);

      TeXObject arg = stack.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);

      if (!(arg instanceof CharObject
             && ((CharObject)arg).getCharCode() == ':'))
      {
         throw new LaTeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_SYNTAX, getName());
      }

      arg = stack.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);

      if (!(arg instanceof CharObject
             && ((CharObject)arg).getCharCode() == '='))
      {
         throw new LaTeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_SYNTAX, getName());
      }

      TeXObjectList list = stack.popToCsMarker(parser, "do");

      if (list.size() == 1)
      {
         arg = list.firstElement();
      }
      else
      {
         arg = list;
      }

      CsvList csvList = argToCsvList(arg, parser, stack);

      TeXObject code = popArg(parser, stack);

      return fullyExpandLoop(parser, stack, cs, csvList, code);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      ControlSequence csArg = popControlSequence(parser, stack);

      GenericCommand cs = new GenericCommand(csArg.getName());
      parser.putControlSequence(true, cs);

      TeXObject arg = stack.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);

      if (!(arg instanceof CharObject
             && ((CharObject)arg).getCharCode() == ':'))
      {
         throw new LaTeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_SYNTAX, getName());
      }

      arg = stack.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);

      if (!(arg instanceof CharObject
             && ((CharObject)arg).getCharCode() == '='))
      {
         throw new LaTeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_SYNTAX, getName());
      }

      TeXObjectList list = stack.popToCsMarker(parser, "do");

      if (list.size() == 1)
      {
         arg = list.firstElement();
      }
      else
      {
         arg = list;
      }

      CsvList csvList = argToCsvList(arg, parser, stack);

      TeXObject code = popArg(parser, stack);

      processLoop(parser, stack, cs, csvList, code);
   }

}
