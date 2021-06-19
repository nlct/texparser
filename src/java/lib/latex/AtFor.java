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

   @Override
   public Object clone()
   {
      return new AtFor(getName());
   }

   public TeXObjectList expandLoop(TeXParser parser, TeXObjectList stack,
     GenericCommand cs, CsvList csvList, TeXObject code)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObjectList list = new TeXObjectList();

      boolean xfor = listener.isStyLoaded("xfor");

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
                 listener.createUndefinedCs("@nnil"));
            }
            else
            {
               parser.putControlSequence(true, new GenericCommand(true,
                  "@xfor@nextelement", null, 
                    new TeXObject[] {csvList.getValue(i+1)}));
            }
         }

         TeXObject loopBody = parser.expandOnce((TeXObject)code.clone(), stack);

         if (loopBody instanceof TeXObjectList)
         {
            list.addAll((TeXObjectList)loopBody);
         }
         else
         {
            list.add(loopBody);
         }

         if (xfor)
         {
            if (parser.isControlSequenceTrue("if@endfor"))
            {
               CsvList remainder = listener.createCsvList();

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
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObjectList list = new TeXObjectList();

      boolean xfor = listener.isStyLoaded("xfor");

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
                 listener.createUndefinedCs("@nnil"));
            }
            else
            {
               parser.putControlSequence(true, new GenericCommand(true,
                  "@xfor@nextelement", null, 
                    new TeXObject[] {csvList.getValue(i+1)}));
            }
         }

         TeXObject loopBody = parser.expandFully((TeXObject)code.clone(), stack);

         if (loopBody instanceof TeXObjectList)
         {
            list.addAll((TeXObjectList)loopBody);
         }
         else
         {
            list.add(loopBody);
         }

         if (xfor)
         {
            if (parser.isControlSequenceTrue("if@endfor"))
            {
               CsvList remainder = listener.createCsvList();

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
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      boolean xfor = listener.isStyLoaded("xfor");

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
                 listener.createUndefinedCs("@nnil"));
            }
            else
            {
               parser.putControlSequence(true, new GenericCommand(true,
                  "@xfor@nextelement", null, 
                    new TeXObject[] {csvList.getValue(i+1)}));
            }
         }

         TeXObject loopBody = parser.expandOnce((TeXObject)code.clone(), stack);

         parser.processObject(loopBody, stack);

         if (xfor)
         {
            if (parser.isControlSequenceTrue("if@endfor"))
            {
               CsvList remainder = listener.createCsvList();

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

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = parser.popRequired(stack);

      if (!(arg instanceof ControlSequence))
      {
         throw new LaTeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_CS_EXPECTED, arg.toString(parser),
                arg.getClass().getSimpleName());
      }
      
      GenericCommand cs = new GenericCommand(((ControlSequence)arg).getName());
      parser.putControlSequence(true, cs);

      if (!parser.isNextWord(":=", stack, PopStyle.IGNORE_LEADING_SPACE))
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

      if (arg instanceof TeXCsRef)
      {
         arg = parser.getListener().getControlSequence(
           ((TeXCsRef)arg).getName());
      }

      CsvList csvList = null;

      if (arg instanceof CsvList)
      {
         csvList = (CsvList)arg;
      }
      else if (arg instanceof Expandable)
      {
         arg = parser.expandOnce(arg, stack);

         if (arg instanceof CsvList)
         {
            csvList = (CsvList)arg;
         }
      }

      if (csvList == null)
      {
         csvList = CsvList.getList(parser, arg);
      }

      TeXObject code = parser.popRequired(stack);

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
      TeXObject arg = parser.popRequired(stack);

      if (!(arg instanceof ControlSequence))
      {
         throw new LaTeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_CS_EXPECTED, arg.toString(parser),
                arg.getClass().getSimpleName());
      }
      
      GenericCommand cs = new GenericCommand(((ControlSequence)arg).getName());
      parser.putControlSequence(true, cs);

      if (!parser.isNextWord(":=", stack, PopStyle.IGNORE_LEADING_SPACE))
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

      if (arg instanceof TeXCsRef)
      {
         arg = parser.getListener().getControlSequence(
           ((TeXCsRef)arg).getName());
      }

      CsvList csvList = null;

      if (arg instanceof CsvList)
      {
         csvList = (CsvList)arg;
      }
      else if (arg instanceof Expandable)
      {
         arg = parser.expandOnce(arg, stack);

         if (arg instanceof CsvList)
         {
            csvList = (CsvList)arg;
         }
      }

      if (csvList == null)
      {
         csvList = CsvList.getList(parser, arg);
      }

      TeXObject code = parser.popRequired(stack);

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
      TeXObject arg = parser.popRequired(stack);

      if (!(arg instanceof ControlSequence))
      {
         throw new LaTeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_CS_EXPECTED, arg.toString(parser),
                arg.getClass().getSimpleName());
      }
      
      GenericCommand cs = new GenericCommand(((ControlSequence)arg).getName());
      parser.putControlSequence(true, cs);

      if (!parser.isNextWord(":=", stack, PopStyle.IGNORE_LEADING_SPACE))
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

      if (arg instanceof TeXCsRef)
      {
         arg = parser.getListener().getControlSequence(
           ((TeXCsRef)arg).getName());
      }

      CsvList csvList = null;

      if (arg instanceof CsvList)
      {
         csvList = (CsvList)arg;
      }
      else if (arg instanceof Expandable)
      {
         arg = parser.expandOnce(arg, stack);

         if (arg instanceof CsvList)
         {
            csvList = (CsvList)arg;
         }
      }

      if (csvList == null)
      {
         csvList = CsvList.getList(parser, arg);
      }

      TeXObject code = parser.popRequired(stack);

      processLoop(parser, stack, cs, csvList, code);
   }

}
