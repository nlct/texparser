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

public class TreeSubGlossEntry extends AbstractGlsCommand
{
   public TreeSubGlossEntry(L2HGlsStyleTree treeStyle)
   {
      this("subglossentry", treeStyle);
   }

   public TreeSubGlossEntry(String name, L2HGlsStyleTree treeStyle)
   {
      super(name, treeStyle.getGlossariesSty());
      this.treeStyle = treeStyle;
   }

   public Object clone()
   {
      return new TreeSubGlossEntry(getName(), treeStyle);
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
      int level = popInt(parser, stack);

      GlsLabel glslabel = popEntryLabel(parser, stack);

      GlossaryEntry entry = glslabel.getEntry();

      TeXObject location = popArg(parser, stack);

      IntegerContentCommand levelCs = treeStyle.getLevelCommand();
      int prevLevel = levelCs.getValue();

      TokenListCommand pendingCs = treeStyle.getPendingCommand();
      TeXObjectList pending = pendingCs.getContent();

      if (entry == null)
      {
         sty.undefWarnOrError(stack,
           GlossariesSty.ENTRY_NOT_DEFINED, glslabel.getLabel());
      }
      else
      {
         TeXParserListener listener = parser.getListener();

         TeXObjectList sublist = listener.createStack();

         if (prevLevel >= level)
         {
            for (int i = level; i <= prevLevel && !pending.isEmpty(); i++)
            {
               sublist.add(pending.pop());
            }
         }
         else
         {
            sublist.add(new StartElement("dd"));
            pending.push(new EndElement("dd"));
         }

         sublist.add(new StartElement("dt"));
         sublist.add(listener.getControlSequence("glsentryitem"));
         sublist.add(glslabel);

         sublist.add(listener.getControlSequence("glstreenamefmt"));
         Group grp = listener.createGroup();
         sublist.add(grp);

         grp.add(listener.getControlSequence("glstarget"));
         grp.add(glslabel);

         if (treeStyle.showChildName())
         {
            grp.add(TeXParserUtils.createGroup(listener, 
             listener.getControlSequence("glossentryname"), glslabel));
         }
         else
         {
            grp.add(listener.createGroup());
         }

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

         sublist.add(listener.getControlSequence("glstreechildpredesc"));
         sublist.add(listener.getControlSequence("glossentrydesc"));
         sublist.add(glslabel);
         sublist.add(listener.getControlSequence("glspostdescription"));
         sublist.add(listener.getControlSequence("space"));
         sublist.add(location);

         pending.add(new EndElement("dd"));

         TeXParserUtils.process(sublist, parser, stack);
      }

      levelCs.setValue(level);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected L2HGlsStyleTree treeStyle;
}
