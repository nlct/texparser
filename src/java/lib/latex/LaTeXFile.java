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

   public LaTeXParserListener getListener()
   {
      return listener;
   }

   public TeXParser getParser()
   {
      return listener.getParser();
   }

   public void processOptions() throws IOException
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

      KeyValList options = getOptions();

      try
      {
         if (options != null)
         {
            load(options);
         }
         else
         {
            preOptions();
            postOptions();
         }
      }
      finally
      {
         listener.setCurrentSty(prevSty, getExtension());
      }
   }

   public void load(KeyValList options)
   throws IOException
   {
      preOptions();

      KeyValList clsOptions = listener.getDocumentClassOptions();

      if (clsOptions != options)
      {
         processOptions(clsOptions);
      }

      processOptions(options);
      postOptions();
   }

   protected void preOptions() throws IOException
   {
   }

   protected void postOptions() throws IOException
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

   private String baseName;
   private KeyValList options;
   private String ext;

   protected LaTeXParserListener listener;

   private Vector<String> declaredOptions = null;
   private HashMap<String,TeXObject> declaredOptionCode=null;
   private TeXObject defaultOptionCode = null;
   protected boolean loadParentOptions=false;
   private LaTeXFile prevSty=null;
}
