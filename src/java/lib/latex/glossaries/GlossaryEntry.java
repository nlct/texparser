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
      ControlSequence cs = null;

      if (key.equals("type"))
      {
         String type;
         Glossary glossary = null;

         if (value instanceof GlsType)
         {
            type = ((GlsType)value).getLabel();
            glossary = ((GlsType)value).getGlossary();
         }
         else
         {
            TeXObject typeVal = (TeXObject)value.clone();

            type = sty.getParser().expandToString(typeVal, stack);
         }

         if (glossary == null)
         {
            glossary = sty.getGlossary(type);
         }

         value = new GlsType("glo@"+getLabel()+"@type", type, glossary);

         cs = (GlsType)value;
      }
      else if (key.equals("category"))
      {
         String categoryLabel;
         Category category = null;

         if (value instanceof GlsCatLabel)
         {
            categoryLabel = ((GlsCatLabel)value).getLabel();
            category = ((GlsCatLabel)value).getCategory();
         }
         else
         {
            TeXObject categoryVal = (TeXObject)value.clone();

            categoryLabel = sty.getParser().expandToString(categoryVal, stack);
         }

         if (category == null)
         {
            category = sty.getCategory(categoryLabel);
         }

         value = new GlsCatLabel("glo@"+getLabel()+"@category",
               categoryLabel, category);

         cs = (GlsCatLabel)value;
      }
      else if (key.equals("parent"))
      {
         if (value == null || value.isEmpty())
         {
            level = 0;
            value = null;
         }
         else
         {
            String parentLabel;
            GlossaryEntry parentEntry = null;

            if (value instanceof GlsLabel)
            {
               parentLabel = ((GlsLabel)value).getLabel();
               parentEntry = ((GlsLabel)value).getEntry();
            }
            else
            {
               TeXObject parentVal = (TeXObject)value.clone();

               parentLabel = sty.getParser().expandToString(parentVal, stack);
            }

            if (parentEntry == null && !parentLabel.isEmpty())
            {
               parentEntry = sty.getEntry(parentLabel);
            }

            if (parentEntry == null)
            {
               level = 0;
               value = null;
            }
            else
            {
               level = parentEntry.getLevel()+1;

               value = new GlsLabel("glo@"+getLabel()+"@parent", parentLabel, parentEntry);
               cs = (GlsLabel)value;
            }
         }
      }

      if (value == null)
      {
         fields.remove(key);

         String csname;

         if (cs == null)
         {
            String internalField = sty.getInternalFieldName(key);

            csname = String.format("glo@%s@%s", getLabel(), internalField);
         }
         else
         {
            csname = cs.getName();
         }

         sty.getParser().removeControlSequence(local, csname);
      }
      else
      {
         if (!fields.contains(key))
         {
            fields.add(key);
         }

         if (cs == null)
         {
            String internalField = sty.getInternalFieldName(key);

            String csname = String.format("glo@%s@%s", getLabel(), internalField);

            cs = new GenericCommand(true, csname, null, value);
         }

         sty.getParser().putControlSequence(local, cs);
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
         return (TeXObject) ((GenericCommand)cs).getDefinition().clone();
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
         val = sty.getParser().getListener().getControlSequence("glsdefaulttype");
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

   public boolean hasParent()
   {
      TeXObject val = get("parent");

      return !(val == null || val.isEmpty());
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

   @Override
   public String toString()
   {
      return String.format("%s[label=%s,level=%d]", getClass().getSimpleName(),
        label, level);
   }

   private String label;
   private int level=0;
   private GlossariesSty sty;

   private Vector<String> fields;
}
