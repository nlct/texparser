/*
    Copyright (C) 2013-2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.auxfile;

import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.Charset;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.Primitive;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.Input;

/**
 * Parses aux files
 */

public class AuxParser extends DefaultTeXParserListener
  implements Writeable
{
   public AuxParser(TeXApp texApp)
     throws IOException
   {
      this(texApp, null);
   }

   public AuxParser(TeXApp texApp, Charset charset)
     throws IOException
   {
      this(texApp, charset, null);
   }

   public AuxParser(TeXApp texApp, Charset charset, String labelPrefix)
     throws IOException
   {
      super(null);
      this.texApp = texApp;
      this.charset = charset;
      this.labelPrefix = labelPrefix;

      setWriteable(this);

      auxData = new Vector<AuxData>();
   }

   public TeXApp getTeXApp()
   {
      return texApp;
   }

   public TeXParser parseAuxFile(File auxFile)
     throws IOException
   {
      return parseAuxFile(auxFile, null);
   }

   public TeXParser parseAuxFile(File auxFile, Charset charset)
     throws IOException
   {
      TeXParser parser = new TeXParser(this);

      parseAuxFile(parser, auxFile, charset);

      return parser;
   }

   public void parseAuxFile(TeXParser parser, File auxFile)
     throws IOException
   {
      parseAuxFile(parser, auxFile, null);
   }

   public void parseAuxFile(TeXParser parser, File auxFile, Charset charset)
     throws IOException
   {
      if (charset != null)
      {
         this.charset=charset;
      }

      int code = parser.getCatCode('@');
      parser.setCatCode('@', TeXParser.TYPE_LETTER);
      parser.parse(auxFile);
      parser.setCatCode('@', code);
   }

   public String getLabelPrefix()
   {
      return labelPrefix;
   }

   @Override
   protected void addPredefined()
   {
      super.addPredefined();

      putControlSequence(new Input("@input", Input.NOT_FOUND_ACTION_WARN, false));

      addAuxCommand("newlabel", 2, labelPrefix);
      addAuxCommand("bibstyle", 1);
      addAuxCommand("citation", 1);
      addAuxCommand("bibdata", 1);
      addAuxCommand("bibcite", 2, labelPrefix);

      putControlSequence(new AuxProvideCommand());

      addAuxCommand("@writefile", 2);

      putControlSequence(new AuxIgnoreable("selectlanguage", true, new boolean[]{true}));
   }

   public void addAuxCommand(String name, int numArgs)
   {
      addAuxCommand(name, numArgs, null);
   }

   public void addAuxCommand(String name, int numArgs, String prefix)
   {
      putControlSequence(new AuxCommand(name, numArgs, prefix));
   }

   /*
    * The aux parser is just intended to gather certain information,
    * so most commands should be ignored, but there are a few that
    * need interpreting.
    */ 
   public boolean isAllowedAuxCommand(ControlSequence cs)
   {
      if (cs instanceof CatCodeChanger)
      {
         return allowCatChangers;
      }

      return (cs instanceof Input
              || cs instanceof AuxCommand 
              || cs instanceof AuxIgnoreable 
              || cs instanceof AssignedControlSequence
              || cs instanceof AuxProvideCommand
              || cs instanceof AuxActionCommand
              || cs instanceof Primitive);
   }

   @Override
   public ControlSequence getControlSequence(String name)
   {
      ControlSequence cs = getParser().getControlSequence(name);

      if (cs instanceof CatCodeChanger && !allowCatChangers)
      {
         return ((CatCodeChanger)cs).getNoOpCommand();
      }

      return isAllowedAuxCommand(cs) ? cs : new AuxIgnoreable(name);
   }

   /**
    * Sets whether or not to allow catcode changing commands.
    */ 
   public void setAllowCatCodeChangers(boolean allow)
   {
      allowCatChangers = allow;
   }

   /**
    * Determines whether or not allow catcode setting is on.
    * @return true if known catcode changing commands will be
    * implemented
    */ 
   public boolean isAllowCatCodeChangersOn()
   {
      return allowCatChangers;
   }

   /**
    * Sets whether or not document divisions should be saved.
    * These correspond to the toc content line written to the aux file.
    * (That is, lines starting <code>\@writefile{toc}{\contentsline...}</code>.)
    * Must be set before parsing to have an effect.
    */
   public void enableSaveDivisions(boolean enabled)
   {
      saveDivisions = enabled;
   }

   /**
    * Gets the state of the save divisions setting.
    */ 
   public boolean isSaveDivisionsEnabled()
   {
      return saveDivisions;
   }

   /**
    * Sets whether or not the label information should be saved to a hash map
    * for faster reference. Must be set before parsing. (The information is
    * obtained from <code>\newlabel</code>.)
    */
   public void enableSaveLabels(boolean enabled)
   {
      saveLabels = enabled;
   }

   public boolean isSaveLabelsEnabled()
   {
      return saveLabels;
   }

   /**
    * Sets whether or not the citation information should be saved to a hash map
    * for faster reference. Must be set before parsing. (The information is
    * obtained from <code>\bibcite</code>.)
    */
   public void enableSaveCites(boolean enabled)
   {
      saveCites = enabled;
   }

   public boolean isSaveCitesEnabled()
   {
      return saveCites;
   }

   protected DivisionInfo createDivisionInfo(String unit, TeXObject prefix, TeXObject title, 
     String target, TeXObject location)
   {
      return new DivisionInfo(unit, prefix, title, target, location);
   }

   protected void initDivisionInfo()
   {
      divisionData = new Vector<DivisionInfo>();

      // add document root
      divisionData.add(createDivisionInfo("document", null, null, "Doc-Start", null));
   }

   @Override
   public ControlSequence createUndefinedCs(String name)
   {
      return new AuxIgnoreable(name);
   }

   @Override
   public Writeable getWriteable()
   {
      return this;
   }

   @Override
   public void writeliteral(String text)
     throws IOException
   {
   }

   @Override
   public void writeliteralln(String text)
     throws IOException
   {
   }

   @Override
   public void write(String text)
     throws IOException
   {
   }

   @Override
   public void writeln(String text)
     throws IOException
   {
   }

   @Override
   public void write(char c)
     throws IOException
   {
   }

   @Override
   public void writeCodePoint(int codePoint)
     throws IOException
   {
   }

   @Override
   public void overwithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXObject before, TeXObject after)
    throws IOException
   {
   }

   @Override
   public void abovewithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness, TeXObject before, 
     TeXObject after)
    throws IOException
   {
   }

   @Override
   public void skipping(Ignoreable ignoreable)
      throws IOException
   {
   }

   @Override
   public void href(String url, TeXObject text)
      throws IOException
   {
   }

   @Override
   public void subscript(TeXObject arg)
     throws IOException
   {
   }

   @Override
   public void superscript(TeXObject arg)
     throws IOException
   {
   }

   @Override
   public void endParse(File file)
      throws IOException
   {
   }

   @Override
   public void beginParse(File file, Charset encoding)
      throws IOException
   {
      getParser().message(TeXApp.MESSAGE_READING, file);

      if (encoding != null)
      {
         getParser().message(TeXApp.MESSAGE_ENCODING, encoding);
      }
   }

   public void addAuxData(AuxData data)
   {
      if (getParser().isDebugMode(TeXParser.DEBUG_STY_DATA))
      {
         getParser().logMessage("AuxData: "+data.toString(parser));
      }

      if (data.getName().equals("@writefile"))
      {
         if (saveDivisions)
         {
            String ext = data.getArg(0).toString(getParser());

            if (ext.equals("toc"))
            {
               DivisionInfo divData = getDivisionInfo(data.getArg(1));

               if (divData != null)
               {
                  if (divisionData == null)
                  {
                     initDivisionInfo();
                  }

                  divisionData.add(divData);
               }
            }
         }
      }
      else
      {
         if (data.getName().equals("newlabel"))
         {
            DivisionInfo divData = null;

            if (saveDivisions)
            {
               if (divisionData == null)
               {
                  initDivisionInfo();
               }

               if (!divisionData.isEmpty())
               {
                  divData = divisionData.lastElement();
                  divData.addLabel(data.getArg(0).toString(getParser()));
               }
            }

            if (saveLabels)
            {
               if (labelData == null)
               {
                  labelData = new HashMap<String,LabelInfo>();
               }

               LabelInfo info = LabelInfo.createLabel(data, getParser());

               if (divData != null)
               {
                  info.setDivisionInfo(divData);
               }

               labelData.put(info.getLabel(), info);
            }
         }
         else if (saveCites && data.getName().equals("bibcite"))
         {
            if (citeData == null)
            {
               citeData = new HashMap<String,CiteInfo>();
            }

            if (citeList == null)
            {
               citeList = new Vector<CiteInfo>();
            }

            CiteInfo info = CiteInfo.createCite(data, getParser());

            if (divisionData != null && !divisionData.isEmpty())
            {
               DivisionInfo divData = divisionData.lastElement();

               info.setDivisionInfo(divData);
            }

            citeData.put(info.getLabel(), info);
            citeList.add(info);
         }

         auxData.add(data);
      }
   }

   protected DivisionInfo getDivisionInfo(TeXObject content)
   {
      if (!getParser().isStack(content))
      {
         return null;
      }

      TeXObjectList stack = (TeXObjectList)content;

      if (stack.isEmpty()) return null;

      try
      {
         TeXObject obj = stack.popStack(getParser());

         if (!(obj instanceof ControlSequence)
               || !((ControlSequence)obj).getName().equals("contentsline"))
         {
            return null;
         }

         String unit = TeXParserUtils.popLabelString(getParser(), stack);

         TeXObject title = stack.popArg(getParser());
         TeXObject prefix = null;

         if (getParser().isStack(title))
         {
            obj = ((TeXObjectList)title).peek();

            if (obj instanceof ControlSequence)
            {
               ControlSequence cs = (ControlSequence)obj;

               if (cs.getName().equals("numberline"))
               {
                  ((TeXObjectList)title).pop();

                  prefix = ((TeXObjectList)title).popArg(getParser());
               }
               else if (cs.getName().equals("nonumberline"))
               {
                  ((TeXObjectList)title).pop();
               }
            }
         }

         TeXObject location = stack.popArg(getParser());
         String target = null;

         if (!stack.isEmpty())
         {
            target = TeXParserUtils.popLabelString(getParser(), stack);
         }

         return createDivisionInfo(unit, prefix, title, target, location);
      }
      catch (IOException e)
      {
         if (getParser().isDebugModeOn())
         {
            getTeXApp().error(e);
         }

         return null;
      }
   }

   /**
    * Gets information about the document divisions provided save divisions
    * was enabled before parsing. Returns null if not enabled before parsing
    * or if no information available in the aux file. (That is, there were
    * no lines starting with <code>\@writefile{toc}{\contentsline...}</code>.)
    */ 
   public Vector<DivisionInfo> getDivisionData()
   {
      return divisionData;
   }

   /**
    * Iterates over the division data (if available) and returns the item
    * that has the given target. Returns null if not found or no division data.
    * The target is only available with hyperref and corresponds to the sectional
    * unit's hyper target.
    */ 
   public DivisionInfo getDivisionByTarget(String target)
   {
      if (divisionData == null) return null;

      for (DivisionInfo divData : divisionData)
      {
         if (target.equals(divData.getTarget()))
         {
            return divData;
         }
      }

      return null;
   }

   /**
    * Iterates over the division data (if available) and returns the item
    * that has the given label. Returns null if not found or no division data.
    * The label is the first instance of <code>\newlabel</code> following 
    * a line starting <code>\@writefile{toc}{\contentsline...}</code>.
    */ 
   public DivisionInfo getDivisionByLabel(String label)
   {
      if (divisionData == null) return null;

      for (DivisionInfo divData : divisionData)
      {
         if (label.equals(divData.getLabel()))
         {
            return divData;
         }
      }

      return null;
   }

   /**
    * Gets the division associated with the given label.
    * Returns null if not found or no division data.
    * The label list corresponds to each <code>\newlabel</code> following 
    * a line starting <code>\@writefile{toc}{\contentsline...}</code>.
    */ 
   public DivisionInfo getDivisionContainingLabel(String label)
   {
      if (divisionData == null) return null;

      // First try the label map

      if (labelData != null)
      {
         LabelInfo info = labelData.get(label);

         if (info != null && info.getDivisionInfo() != null)
         {
            return info.getDivisionInfo();
         }

         return null;
      }

      for (DivisionInfo divData : divisionData)
      {
         if (divData.containsLabel(label))
         {
            return divData;
         }
      }

      return null;
   }

   /**
    * Gets all the information obtained identified by the command with the
    * given control sequence name. The aux file needs to be parsed first.
    */ 
   public Vector<AuxData> getAuxData(String name)
   {
      Vector<AuxData> list = new Vector<AuxData>();

      for (AuxData data : auxData)
      {
         if (data.getName().equals(name))
         {
            list.add(data);
         }
      }

      return list;
   }

   /**
    * Gets all the information obtained from parsing the aux file.
    * The list will be empty if the aux file hasn't yet been parsed.
    */ 
   public Vector<AuxData> getAuxData()
   {
      return auxData;
   }

   public HashMap<String,LabelInfo> getLabelData()
   {
      return labelData;
   }

   public HashMap<String,CiteInfo> getCiteData()
   {
      return citeData;
   }

   public Vector<CiteInfo> getCiteList()
   {
      return citeList;
   }

   @Override
   public Charset getCharSet()
   {
      return charset == null ? super.getCharSet() : charset;
   }

   public void setCharSet(Charset charset)
   {
      this.charset = charset;
   }

   // shouldn't be needed in auxFile
   @Override
   public float emToPt(float emValue)
   {
      getParser().warning(
         "Can't convert from em to pt, no font information loaded");

      return 9.5f*emValue;
   }

   // shouldn't be needed in auxFile
   public float exToPt(float exValue)
   {
      getParser().warning(
         "Can't convert from ex to pt, no font information loaded");

      return 4.4f*exValue;
   }

   private Vector<AuxData> auxData;

   private boolean saveDivisions = false;
   protected Vector<DivisionInfo> divisionData;

   private boolean saveLabels = false;
   protected HashMap<String,LabelInfo> labelData;

   private boolean saveCites = false;
   protected HashMap<String,CiteInfo> citeData;
   protected Vector<CiteInfo> citeList;// order of reference

   private TeXApp texApp;

   private Charset charset=null;
   private String labelPrefix = null;
   private boolean allowCatChangers = true;
}
