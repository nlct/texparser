/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.latex3.TokenListCommand;
import com.dickimawbooks.texparserlib.html.StartElement;
import com.dickimawbooks.texparserlib.html.EndElement;

public class TreeGlossEntry extends AbstractGlsCommand
{
   public TreeGlossEntry(L2HGlsStyleTree treeStyle)
   {
      this("glossentry", treeStyle);
   }

   public TreeGlossEntry(String name, L2HGlsStyleTree treeStyle)
   {
      super(name, treeStyle.getGlossariesSty());
      this.treeStyle = treeStyle;
   }

   public Object clone()
   {
      return new TreeGlossEntry(getName(), treeStyle);
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

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);

      GlossaryEntry entry = glslabel.getEntry();

      TeXObject location = popArg(parser, stack);

      IntegerContentCommand levelCs = treeStyle.getLevelCommand();

      TokenListCommand pendingCs = treeStyle.getPendingCommand();

      TeXParserUtils.process(pendingCs.getContent(), parser, stack);

      if (entry == null)
      {
         sty.undefWarnOrError(stack,
           GlossariesSty.ENTRY_NOT_DEFINED, glslabel.getLabel());
      }
      else
      {
         TeXParserListener listener = parser.getListener();

         TeXObjectList sublist = listener.createStack();

         sublist.add(new StartElement("dt"));
         sublist.add(listener.getControlSequence("glsentryitem"));
         sublist.add(glslabel);

         sublist.add(listener.getControlSequence("glstreenamefmt"));
         Group grp = listener.createGroup();
         sublist.add(grp);

         grp.add(listener.getControlSequence("glstarget"));
         grp.add(glslabel);
         grp.add(TeXParserUtils.createGroup(listener, 
          listener.getControlSequence("glossentryname"), glslabel));

         sublist.add(new EndElement("dt"));

         sublist.add(new StartElement("dd"));

         if (entry.hasField("symbol"))
         {
            sublist.add(listener.getControlSequence("space"));
            sublist.add(listener.getOther('('));
            sublist.add(listener.getControlSequence("glossentrysymbol"));
            sublist.add(glslabel);
            sublist.add(listener.getOther(')'));
         }

         sublist.add(listener.getControlSequence("glstreepredesc"));
         sublist.add(listener.getControlSequence("glossentrydesc"));
         sublist.add(glslabel);
         sublist.add(listener.getControlSequence("glspostdescription"));
         sublist.add(listener.getControlSequence("space"));
         sublist.add(location);

         pendingCs.append(new EndElement("dd"));

         TeXParserUtils.process(sublist, parser, stack);
      }

      levelCs.setValue(0);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected L2HGlsStyleTree treeStyle;
}
