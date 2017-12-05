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

public class Footnote extends ControlSequence
{
   public Footnote()
   {
      this("footnote");
   }

   public Footnote(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Footnote(getName());
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject opt = parser.popNextArg('[', ']');

      TeXObject arg = parser.popNextArg();

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject mpfn = listener.getControlSequence("@mpfn");

      if (mpfn instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)mpfn).expandfully(parser);
   
         if (expanded != null)
         {
            mpfn = expanded;
         }
      }

      String counter = mpfn.toString(parser);

      UserNumber orgValue = new UserNumber();
      NumericRegister reg = null;

      if (opt != null)
      {
         String registerName = String.format("c@%s", counter);

         reg = parser.getSettings().getNumericRegister(registerName);

         if (reg == null)
         {
            throw new TeXSyntaxException(parser, 
               TeXSyntaxException.ERROR_REGISTER_UNDEF, registerName);
         }

         orgValue.setValue(reg.number(parser));

         if (opt instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)opt).expandfully(parser);

            if (expanded != null)
            {
               opt = expanded;
            }
         }

         reg.setValue(parser, new UserNumber(parser, opt.toString(parser)));
      }
      else
      {
         listener.stepcounter(counter);
      }
   
      TeXObject thempfn = listener.getControlSequence("thempfn");

      String targetName = getTargetName(parser, counter, thempfn);

      if (thempfn instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)thempfn).expandfully(parser);
   
         if (expanded != null)
         {
            thempfn = expanded;
         }
      }
   
      if (opt != null)
      {
         reg.setValue(parser, orgValue);
      }

      footnote(parser, parser, targetName, thempfn, arg);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject opt = stack.popArg(parser, '[', ']');

      TeXObject arg = stack.popArg(parser);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject mpfn = listener.getControlSequence("@mpfn");

      if (mpfn instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)mpfn).expandfully(parser);
   
         if (expanded != null)
         {
            mpfn = expanded;
         }
      }

      String counter = mpfn.toString(parser);

      UserNumber orgValue = new UserNumber();
      NumericRegister reg = null;

      if (opt != null)
      {
         String registerName = String.format("c@%s", counter);

         reg = parser.getSettings().getNumericRegister(registerName);

         if (reg == null)
         {
            throw new TeXSyntaxException(parser, 
               TeXSyntaxException.ERROR_REGISTER_UNDEF, registerName);
         }

         orgValue.setValue(reg.number(parser));

         if (opt instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)opt).expandfully(parser);

            if (expanded != null)
            {
               opt = expanded;
            }
         }

         reg.setValue(parser, new UserNumber(parser, opt.toString(parser)));
      }
      else
      {
         listener.stepcounter(counter);
      }
   
      TeXObject thempfn = listener.getControlSequence("thempfn");

      String targetName = getTargetName(parser, counter, thempfn);

      if (thempfn instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)thempfn).expandfully(parser);
   
         if (expanded != null)
         {
            thempfn = expanded;
         }
      }
   
      if (opt != null)
      {
         reg.setValue(parser, orgValue);
      }

      footnote(parser, stack, targetName, thempfn, arg);
   }

   public String getTargetName(TeXParser parser, String counter, TeXObject thempfn)
   throws IOException
   {
      TeXObject thehcounter = parser.getControlSequence("theH"+counter);

      if (thehcounter == null)
      {
          thehcounter = thempfn;
      }

      if (thehcounter instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)thehcounter).expandfully(parser);

         if (expanded != null)
         {
            thehcounter = expanded;
         }
      }

      return counter+"."+thehcounter.toString(parser);
   }

   protected void footnote(TeXParser parser, TeXObjectList stack, 
     String targetName, TeXObject thempfn, TeXObject arg)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      Group grp = listener.createGroup();
      Group insGrp = listener.createGroup();

      ControlSequence hyperlink = parser.getControlSequence("hyperlink");

      if (hyperlink == null)
      {
         grp.add(thempfn);
         insGrp.add((TeXObject)thempfn.clone());
      }
      else
      {
         grp.add(hyperlink);
         insGrp.add(new TeXCsRef("hypertarget"));

         Group target = listener.createGroup(targetName);

         grp.add(target);
         insGrp.add((Group)target.clone());

         if (thempfn instanceof Group)
         {
            grp.add(thempfn);
            insGrp.add((Group)thempfn.clone());
         }
         else
         {
            Group text = listener.createGroup();
            text.add(thempfn);

            grp.add(text);
            insGrp.add((Group)text.clone());
         }
      }

      TeXObjectList list = new TeXObjectList();
      list.add(new TeXCsRef("textsuperscript"));
      list.add(insGrp);
      list.add(arg);

      listener.addFootnote(list);

      ControlSequence cs = listener.getControlSequence("textsuperscript");

      if (parser == stack || stack == null)
      {
         parser.push(grp);
         cs.process(parser);
      }
      else
      {
         stack.push(grp);
         cs.process(parser, stack);
      }
   }
}
