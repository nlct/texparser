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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.NewIf;
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.primitives.IfFalse;
import com.dickimawbooks.texparserlib.latex.KeyValList;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

public class GlossaryEntry extends HashMap<String,TeXObject>
{
   public GlossaryEntry(GlossariesSty sty, 
     String label, KeyValList options)
   throws IOException
   {
      super();
      this.label = label;
      this.sty = sty;
      TeXParser parser = sty.getParser();

      for (Iterator<String> it = options.keySet().iterator();
           it.hasNext(); )
      {
         String key = it.next();
         TeXObject value;

         if (sty.isFieldExpansionOn(key))
         {
            value = options.getExpandedValue(key, parser, parser);
         }
         else
         {
            value = options.getValue(key);
         }

         setField(key, value);
      }

      NewIf.createConditional(false, parser, "ifglo@"+label+"@flag");

      sty.addDefaultFieldValues(this);
   }

   public TeXObject setField(String key, TeXObject value)
     throws IOException
   {
      if (key.equals("type"))
      {
         if (!(value instanceof GlsType))
         {
            TeXObject typeVal = (TeXObject)value.clone();

            if (typeVal instanceof Expandable)
            {
               TeXObjectList expanded = ((Expandable)typeVal).expandfully(sty.getParser());

               if (expanded != null)
               {
                  typeVal = expanded;
               }
            }

            String type = typeVal.toString(sty.getParser());

            value = new GlsType("@@glstype", type, sty.getGlossary(type));
         }
      }
      else if (key.equals("category"))
      {
         TeXObject categoryVal = (TeXObject)value.clone();

         if (categoryVal instanceof Expandable)
         {
            TeXObjectList expanded =
              ((Expandable)categoryVal).expandfully(sty.getParser());

            if (expanded != null)
            {
               categoryVal = expanded;
            }
         }

         category = categoryVal.toString(sty.getParser());
      }
      else if (key.equals("parent"))
      {
         if (value == null || value.isEmpty())
         {
            level = 0;
            return remove("parent");
         }

         if (value instanceof GlsLabel)
         {
            GlossaryEntry parentEntry = ((GlsLabel)value).getEntry();

            level = parentEntry.getLevel()+1;
         }
         else
         {
            TeXObject parentVal = (TeXObject)value.clone();

            if (parentVal instanceof Expandable)
            {
               TeXObjectList expanded = ((Expandable)parentVal).expandfully(sty.getParser());

               if (expanded != null)
               {
                  parentVal = expanded;
               }
            }

            String parent = parentVal.toString(sty.getParser());

            if (parent.isEmpty())
            {
               level = 0;
               return remove(parent);
            }

            GlossaryEntry parentEntry = sty.getEntry(parent);

            if (parentEntry == null)
            {
               throw new LaTeXSyntaxException(sty.getParser(),
                  GlossariesSty.ENTRY_NOT_DEFINED, parent);
            }

            level = parentEntry.getLevel()+1;

            value = new GlsLabel("@@parent@label", parentEntry);
         }
      }
      else if (value == null)
      {
         return remove(key);
      }

      return put(key, value);
   }

   public int getLevel()
   {
      return level;
   }

   public String getCategory()
   {
      return category;
   }

   public String getType()
   {
      TeXObject val = get("type");

      if (val != null && val instanceof TextualContentCommand)
      {
         return ((TextualContentCommand)val).getText();
      }

      return "main";
   }

   public Glossary getGlossary() throws IOException
   {
      TeXObject val = get("type");

      if (val instanceof GlsType)
      {
         Glossary glossary = ((GlsType)val).getGlossary();

         if (glossary == null)
         {
            ((GlsType)val).refresh(sty);
            glossary = ((GlsType)val).getGlossary();

            if (glossary == null)
            {
               throw new LaTeXSyntaxException(sty.getParser(),
                  GlossariesSty.GLOSSARY_NOT_DEFINED, ((GlsType)val).getLabel());
            }
         }

         return glossary;
      }

      String type;

      if (val == null)
      {
         val = new TeXCsRef("glsdefaulttype");
      }

      if (val instanceof TextualContentCommand)
      {
         type = ((TextualContentCommand)val).getText();
      }
      else 
      {
         if (val instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)val).expandfully(sty.getParser());

            if (expanded != null)
            {
               val = expanded;
            }
         }

         type = val.toString(sty.getParser());
      }

      Glossary glossary = sty.getGlossary(type);

      if (glossary == null)
      {
         throw new LaTeXSyntaxException(sty.getParser(),
            GlossariesSty.GLOSSARY_NOT_DEFINED, type);
      }

      GlsType glstype = new GlsType("@@glstype", type, glossary);
      put("type", glstype);

      return glossary;
   }

   public String getLabel()
   {
      return label;
   }

   public GlossaryEntry getParent() throws IOException
   {
      TeXObject val = get("parent");

      if (val == null || val.isEmpty()) return null;

      if (val instanceof GlsLabel)
      {
         GlossaryEntry entry = ((GlsLabel)val).getEntry();

         if (entry == null)
         {
            ((GlsLabel)val).refresh(sty);

            entry = ((GlsLabel)val).getEntry();

            if (entry == null)
            {
               remove("parent");
               level = 0;

               throw new LaTeXSyntaxException(sty.getParser(),
                  GlossariesSty.ENTRY_NOT_DEFINED, ((GlsLabel)val).getLabel());
            }

            level = entry.getLevel()+1;
         }

         return entry;
      }

      String parent = val.toString(sty.getParser());
      GlossaryEntry entry = sty.getEntry(parent);

      if (entry == null)
      {
         remove("parent");
         level = 0;

         throw new LaTeXSyntaxException(sty.getParser(),
            GlossariesSty.ENTRY_NOT_DEFINED, parent);
      }

      level = entry.getLevel()+1;
      put("parent", new GlsLabel("@@parent@label", entry));

      return entry;
   }

   // mark as used.
   public void unset(boolean local)
   {
      TeXParser parser = sty.getParser();
      parser.putControlSequence(local, new IfTrue("ifglo@"+label+"@flag"));
   }

   // mark as unused.
   public void reset(boolean local)
   {
      TeXParser parser = sty.getParser();
      parser.putControlSequence(local, new IfFalse("ifglo@"+label+"@flag"));
   }

   // has this entry been marked as used?
   public boolean isUnset()
   {
      TeXParser parser = sty.getParser();

      ControlSequence cs = parser.getControlSequence("ifglo@"+label+"@flag");

      return (cs instanceof TeXBoolean && ((TeXBoolean)cs).booleanValue());
   }

   private String label;
   private String category;
   private int level=0;
   private GlossariesSty sty;
}
