/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;

/**
 * Information about a LaTeX file, package or class.
 */

public class LaTeXFile extends TeXPath
{
   public LaTeXFile(TeXParser parser, String texPath)
     throws IOException
   {
      this(parser, null, texPath, "tex");
   }

   public LaTeXFile(TeXParser parser, String texPath, String defExt)
     throws IOException
   {
      this(parser, null, texPath, defExt);
   }

   public LaTeXFile(TeXParser parser, KeyValList options, String texPath,
     String defExt)
     throws IOException
   {
      this(parser, options, texPath, defExt, false);
   }

   public LaTeXFile(TeXParser parser, KeyValList options, String texPath,
     String defExt, boolean loadParentOptions)
     throws IOException
   {
      super(parser, texPath, defExt);

      String fileName = getFileName().toString();

      int idx = fileName.lastIndexOf(".");

      if (idx > -1)
      {
         this.ext = fileName.substring(idx);
         this.baseName = fileName.substring(0, idx);
      }
      else
      {
         this.ext = defExt;
         this.baseName = fileName;
      }

      this.options = options;

      this.loadParentOptions = loadParentOptions;
      this.listener = (LaTeXParserListener)parser.getListener();
      this.prevSty = listener.getCurrentSty(getExtension());
   }

   public String getName()
   {
      return baseName;
   }

   public String getExtension()
   {
      return ext;
   }

   public KeyValList getOptions()
   {
      return options;
   }

   public void addOptionIfAbsent(String key, TeXObject value)
   {
      if (options == null)
      {
         options = new KeyValList();
         options.put(key, value);
      }
      else
      {
         options.putIfAbsent(key, value);
      }
   }

   protected void addOptions(KeyValList extraOptions)
   {
      if (extraOptions != null)
      {
         if (options == null)
         {
            options = extraOptions;
         }
         else
         {
            options.putAll(extraOptions);
         }
      }
   }

   public LaTeXParserListener getListener()
   {
      return listener;
   }

   public TeXParser getParser()
   {
      return listener.getParser();
   }

   protected void loadParentOptions() throws IOException
   {
      if (loadParentOptions && prevSty != null)
      {
         KeyValList parentOptions = prevSty.getOptions();

         if (parentOptions != null)
         {
            for (Iterator<String> it = parentOptions.keySet().iterator();
                 it.hasNext(); )
            {
               String key = it.next();
               addOptionIfAbsent(key, parentOptions.get(key));
            }

         }
      }

      listener.setCurrentSty(this, getExtension());

      KeyValList passedOptions = listener.getPassedOptions(
        String.format("%s.%s", getName(), getExtension()));

      if (passedOptions != null)
      {
         for (Iterator<String> it = passedOptions.keySet().iterator();
              it.hasNext(); )
         {
            String key = it.next();
            addOptionIfAbsent(key, passedOptions.get(key));
         }

      }
   }

   public void processOptions(TeXObjectList stack) throws IOException
   {
      loadParentOptions();

      KeyValList options = getOptions();

      try
      {
         if (options != null)
         {
            load(options, stack);
         }
         else
         {
            preOptions(stack);
            postOptions(stack);
         }
      }
      finally
      {
         listener.setCurrentSty(prevSty, getExtension());
      }
   }

   public void load(KeyValList options, TeXObjectList stack)
   throws IOException
   {
      TeXObjectList substack = getListener().createStack();
      preOptions(substack);

      if (!substack.isEmpty())
      {
         substack.process(getParser(), stack);
      }

      KeyValList clsOptions = listener.getDocumentClassOptions();

      if (clsOptions != options)
      {
         processOptions(clsOptions);
      }

      processOptions(options);

      postOptions(substack);

      if (!substack.isEmpty())
      {
         substack.process(getParser(), stack);
         stack.push(substack, true);
      }
   }

   protected void preOptions(TeXObjectList stack) throws IOException
   {
   }

   protected void postOptions(TeXObjectList stack) throws IOException
   {
   }

   public void processOption(String option, TeXObject value)
    throws IOException
   {
   }

   public void processOptions(KeyValList options)
   throws IOException
   {
      if (options == null) return;

      if (declaredOptions == null)
      {
         for (Iterator<String> it = options.keySet().iterator(); it.hasNext();)
         {
            String option = it.next();
            TeXObject value = options.get(option);

            processOption(option, value);
         }
      }
      else
      {
         for (String option : declaredOptions)
         {
            if (options.containsKey(option))
            {
               TeXObject value = options.get(option);

               processOption(option, value);
            }
         }

         for (Iterator<String> it = options.keySet().iterator(); it.hasNext();)
         {
            String option = it.next();

            if (!declaredOptions.contains(option))
            {
               TeXObject value = options.get(option);

               processUnknownOption(option, value);
            }
         }
      }
   }

   public void declareUnknownOption(TeXObject code)
   {
      defaultOptionCode = code;
   }

   public void declareOption(String option, TeXObject code)
   {
      if (code == null)
      {
         throw new NullPointerException();
      }

      if (declaredOptions == null)
      {
         declaredOptions = new Vector<String>();
      }

      declaredOptions.add(option);

      if (declaredOptionCode == null)
      {
         declaredOptionCode = new HashMap<String,TeXObject>();
      }

      declaredOptionCode.put(option, code);
   }

   protected void processUnknownOption(String option, TeXObject value)
    throws IOException
   {
      if (defaultOptionCode != null)
      {
         getParser().putControlSequence(true, 
           new GenericCommand("CurrentOption", null, 
              getListener().createString(option)));

         if (value == null)
         {
            defaultOptionCode.process(getParser());
         }
         else
         {
            Group group = getListener().createGroup();
            group.add(value);
            TeXObjectList stack = new TeXObjectList();
            stack.add(group);

            defaultOptionCode.process(getParser(), stack);
         }
      }
   }

   protected void processDeclaredOption(String option, TeXObject value)
    throws IOException
   {
      if (declaredOptions == null || !declaredOptions.contains(option))
      {
         processUnknownOption(option, value);

         return;
      }

      TeXObject code = declaredOptionCode.get(option);

      if (code != null)
      {
         if (value == null)
         {
            code.process(getParser());
         }
         else
         {
            Group group = getListener().createGroup();
            group.add(value);
            TeXObjectList stack = new TeXObjectList();
            stack.add(group);

            code.process(getParser(), stack);
         }
      }
   }

   protected void updateName(String name)
   {
      if (name.equals(baseName))
      {
         return;
      }

      if (knownNames == null)
      {
         knownNames = new Vector<String>();
         knownNames.add(baseName);
      }
      else if (!knownNames.contains(baseName))
      {
         knownNames.add(baseName);
      }

      baseName = name;
   }

   public boolean isName(String name)
   {
      return baseName.equals(name) 
              || (knownNames != null && knownNames.contains(name));
   }

   private String baseName;
   private Vector<String> knownNames;
   private KeyValList options;
   private String ext;

   protected LaTeXParserListener listener;

   private Vector<String> declaredOptions = null;
   private HashMap<String,TeXObject> declaredOptionCode=null;
   private TeXObject defaultOptionCode = null;
   protected boolean loadParentOptions=false;
   private LaTeXFile prevSty=null;
}
