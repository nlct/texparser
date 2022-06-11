/*
    Copyright (C) 2022 Nicola L.C. Talbot
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

import java.util.Vector;
import java.util.Iterator;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.NewIf;
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.primitives.IfFalse;
import com.dickimawbooks.texparserlib.latex.KeyValList;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

public class GlossaryEntry
{
   public GlossaryEntry(GlossariesSty sty, 
     String label, KeyValList options, TeXObjectList stack)
   throws IOException
   {
      super();
      this.label = label;
      this.sty = sty;
      fields = new Vector<String>();

      TeXParser parser = sty.getParser();

      if (parser.getDebugLevel() > 0)
      {
         parser.logMessage("Defining GlossaryEntry "+label);
      }

      for (Iterator<String> it = options.keySet().iterator();
           it.hasNext(); )
      {
         String key = it.next();
         TeXObject value;

         if (sty.isFieldExpansionOn(key))
         {
            value = options.getExpandedValue(key, parser, stack);
         }
         else
         {
            value = options.getValue(key);
         }

         if (parser.getDebugLevel() > 0)
         {
            parser.logMessage("FIELD "+key+" -> "+value.toString(parser));
         }

         setField(key, value, stack);
      }

      NewIf.createConditional(false, parser, "ifglo@"+label+"@flag");

      sty.addDefaultFieldValues(this, stack);
   }

   public void setField(String key, TeXObject value, TeXObjectList stack)
     throws IOException
   {
      setField(key, value, true, stack);
   }

   public void setField(String key, TeXObject value, boolean local,
       TeXObjectList stack)
   throws IOException
   {
      if (key.equals("type"))
      {
         if (!(value instanceof GlsType))
         {
            TeXObject typeVal = (TeXObject)value.clone();

            String type = sty.getParser().expandToString(typeVal, stack);

            value = new GlsType("@@glstype", type, sty.getGlossary(type));
         }
      }
      else if (key.equals("category"))
      {
         if (!(value instanceof GlsCatLabel))
         {
            TeXObject categoryVal = (TeXObject)value.clone();

            String category = sty.getParser().expandToString(categoryVal, stack);

            value = new GlsCatLabel("@@glscategory", category, sty.getCategory(category));
         }
      }
      else if (key.equals("parent"))
      {
         if (value == null || value.isEmpty())
         {
            level = 0;
            remove("parent", local);
            return;
         }

         if (value instanceof GlsLabel)
         {
            GlossaryEntry parentEntry = ((GlsLabel)value).getEntry();

            level = parentEntry.getLevel()+1;
         }
         else
         {
            TeXObject parentVal = (TeXObject)value.clone();

            String parent = sty.getParser().expandToString(parentVal, stack);

            if (parent.isEmpty())
            {
               level = 0;
               remove(parent, local);
               return;
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

      String internalField = sty.getInternalFieldName(key);

      String csname = String.format("glo@%s@%s", getLabel(), internalField);

      if (value == null)
      {
         fields.remove(key);

         sty.getParser().removeControlSequence(local, csname);
      }
      else
      {
         if (fields.contains(key))
         {
            fields.add(key);
         }

         sty.getParser().putControlSequence(local,
            new GenericCommand(true, csname, null, value));
      }
   }

   public void remove(String key)
   {
      remove(key, true);
   }

   public void remove(String key, boolean local)
   {
      String internalField = sty.getInternalFieldName(key);

      String csname = String.format("glo@%s@%s", getLabel(), internalField);

      fields.remove(key);

      sty.getParser().removeControlSequence(local, csname);
   }

   /**
    * Determines whether the given field has been explicitly set.
    * Doesn't check if a value has been set by assigning the
    * associated internal command. If that test is required, test if
    * get(String) returns non null.
    * @param fieldName the field (key) name
    * @return true if the field has been assigned
    */ 
   public boolean hasField(String fieldName)
   {
      return fields.contains(fieldName);
   }

   public TeXObject get(String field)
   {
      String internalField = sty.getInternalFieldName(field);

      String csname = String.format("glo@%s@%s", getLabel(), internalField);

      ControlSequence cs = sty.getParser().getControlSequence(csname);

      if (cs == null)
      {
         return null;
      }

      if (!fields.contains(field))
      {
         fields.add(field);
      }

      if (cs instanceof GenericCommand)
      {
         return ((GenericCommand)cs).getDefinition();
      }

      return cs;
   }

   public int getLevel()
   {
      return level;
   }

   public String getCategory()
   {
      TeXObject val = get("category");

      if (val instanceof TeXObjectList && ((TeXObjectList)val).size() == 1)
      {
         val = ((TeXObjectList)val).firstElement();
      }

      if (val != null && val instanceof TextualContentCommand)
      {
         return ((TextualContentCommand)val).getText();
      }

      return "general";
   }

   public String getType()
   {
      TeXObject val = get("type");

      if (val instanceof TeXObjectList && ((TeXObjectList)val).size() == 1)
      {
         val = ((TeXObjectList)val).firstElement();
      }

      if (val != null && val instanceof TextualContentCommand)
      {
         return ((TextualContentCommand)val).getText();
      }

      return "main";
   }

   public Glossary getGlossary(TeXObjectList stack) throws IOException
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

      setField("type", glstype, stack);

      return glossary;
   }

   public String getLabel()
   {
      return label;
   }

   public GlossaryEntry getParent(TeXObjectList stack) throws IOException
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

      String parent = sty.getParser().expandToString(val, stack);
      GlossaryEntry entry = sty.getEntry(parent);

      if (entry == null)
      {
         remove("parent");
         level = 0;

         throw new LaTeXSyntaxException(sty.getParser(),
            GlossariesSty.ENTRY_NOT_DEFINED, parent);
      }

      level = entry.getLevel()+1;
      setField("parent", new GlsLabel("@@parent@label", entry), stack);

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
   private int level=0;
   private GlossariesSty sty;

   private Vector<String> fields;
}
