/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.flowfram;

import java.io.IOException;

import java.util.HashMap;
import java.util.Enumeration;
import java.util.Vector;

import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.ParCs;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.color.ColorSty;
import com.dickimawbooks.texparserlib.html.*;
import com.dickimawbooks.texparserlib.auxfile.*;

/**
 * Since the TeX parser library has no output routine this is mainly
 * for obtaining flowfram.sty data and for providing a basic
 * implementation for L2HConverter.
 */
public class FlowFramSty extends LaTeXSty implements BeginDocumentListener
{
   public FlowFramSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions, ColorSty colorSty)
   throws IOException
   {
      super(options, "flowfram", listener, loadParentOptions);

      this.colorSty = colorSty;

      flowFrames = new Vector<FlowFrameData> ();
      staticFrames = new Vector<FlowFrameData>();
      dynamicFrames = new Vector<FlowFrameData>();

      flowIdMap = new HashMap<String,Integer>();
      staticIdMap = new HashMap<String,Integer>();
      dynamicIdMap = new HashMap<String,Integer>();

      listener.addBeginDocumentListener(this);

      float textWidthPt = TeXParserUtils.toPt(getParser(), "textwidth");

      if (textWidthPt == 0.0f)
      {
         textWidthPt = 430.00462f;
      }

      registerNewLength("typeblockwidth", textWidthPt, TeXUnit.PT);

      float textHeightPt = TeXParserUtils.toPt(getParser(), "textheight");

      if (textHeightPt == 0.0f)
      {
         textHeightPt = 556.47656f;
      }

      registerNewLength("typeblockheight", textHeightPt, TeXUnit.PT);

      listener.addAuxCommand(new AuxCommand("flowfram@preamble@htmlopts", 6));
      listener.addAuxCommand(new AuxCommand("flowfram@doc@htmlopts", 6));
      listener.addAuxCommand(new AuxCommand("@flowfram@saverelativeloc", 4));
   }

   @Override
   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      NewIf.createConditional(true, getParser(), "iflefttorightcolumns", true);
      NewIf.createConditional(true, getParser(), "ifshowtypeblock", false);
      NewIf.createConditional(true, getParser(), "ifshowmargins", false);
      NewIf.createConditional(true, getParser(), "ifshowframebbox", false);

      listener.newcounter("absolutepage");
      listener.newcounter("maxflow");
      listener.newcounter("maxstatic");
      listener.newcounter("maxdynamic");
      listener.newcounter("maxthumbtabs");
      listener.newcounter("thisframe");
      listener.newcounter("displayedframe");
      listener.newcounter("minitoc");

      registerNewLength("fflabelsep", 1, TeXUnit.PT);
      registerNewLength("fftolerance", 2, TeXUnit.PT);
      registerNewLength("sdfparindent", 0, TeXUnit.PT);
      registerNewLength("flowframesep", 3, TeXUnit.PT);
      registerNewLength("flowframerule", 0.4f, TeXUnit.PT);

      registerNewLength("ffareawidth", 0, TeXUnit.PT);
      registerNewLength("ffareaheight", 0, TeXUnit.PT);
      registerNewLength("ffareax", 0, TeXUnit.PT);
      registerNewLength("ffareay", 0, TeXUnit.PT);
      registerNewLength("ffareaevenx", 0, TeXUnit.PT);
      registerNewLength("ffareaeveny", 0, TeXUnit.PT);

      registerNewLength("ffevenoffset", 0, TeXUnit.PT);
      registerNewLength("columnheight", 0, TeXUnit.PT);
      registerNewLength("vcolumnsep", 10, TeXUnit.PT);
      registerNewLength("ffcolumnseprule", 2, TeXUnit.PT);

      registerNewLength("thumbtabwidth", 1, TeXUnit.CM);
      registerNewLength("beforeminitocskip", 0, TeXUnit.PT);
      registerNewLength("afterminitocskip", 0, TeXUnit.PT);

      registerControlSequence(new StaticFloat("staticfigure", "figure"));
      registerControlSequence(new StaticFloat("statictable", "table"));

      registerControlSequence(new NewFlowFrame(this));

      registerControlSequence(new NewFlowFrame("newstaticframe",
        FlowFrameType.STATIC, this));

      registerControlSequence(new NewFlowFrame("newdynamicframe",
        FlowFrameType.DYNAMIC, this));

      registerControlSequence(new NumColumnInArea("onecolumn", 1, false, this));
      registerControlSequence(new NumColumnInArea("onecolumninarea", 1, true, this));
      registerControlSequence(new NumColumnInArea("twocolumn", 2, false, this));
      registerControlSequence(new NumColumnInArea("twocolumninarea", 2, true, this));
      registerControlSequence(new NumColumnInArea("Ncolumn", 0, false, this));
      registerControlSequence(new NumColumnInArea("Ncolumninarea", 0, true, this));

      registerControlSequence(new GetFlowLabel(this));

      registerControlSequence(new GetFlowLabel("getstaticlabel",
        FlowFrameType.STATIC, this));

      registerControlSequence(new GetFlowLabel("getdynamiclabel",
        FlowFrameType.DYNAMIC, this));

      registerControlSequence(new GetFlowId(this));

      registerControlSequence(new GetFlowId("getstaticid",
        FlowFrameType.STATIC, this));

      registerControlSequence(new GetFlowId("getdynamicid",
        FlowFrameType.DYNAMIC, this));

      registerControlSequence(new SetFrameContents("setstaticcontents",
        FlowFrameType.STATIC, this));
      registerControlSequence(new SetFrameContents("setdynamiccontents",
        FlowFrameType.DYNAMIC, this));

      registerControlSequence(new SetFrameContents("appenddynamiccontents",
        FlowFrameType.DYNAMIC, true, this));

      registerControlSequence(new SetFrameContentsEnv("staticcontents",
        FlowFrameType.STATIC, this));
      registerControlSequence(new SetFrameContentsEnv("staticcontents*",
        FlowFrameType.STATIC, this));

      registerControlSequence(new SetFrameContentsEnv("dynamiccontents",
        FlowFrameType.DYNAMIC, this));
      registerControlSequence(new SetFrameContentsEnv("dynamiccontents*",
        FlowFrameType.DYNAMIC, this));

      registerControlSequence(new SetFrameAttrs("setstaticframe",
         FlowFrameType.STATIC, this));

      registerControlSequence(new SetFrameAttrs("setdynamicframe",
         FlowFrameType.DYNAMIC, this));

      registerControlSequence(new SetFrameAttrs("setflowframe",
         FlowFrameType.FLOW, this));

      registerControlSequence(new FlowFramSectionUnit());

      registerControlSequence(new ComputeLeftEdgeOdd());
      registerControlSequence(new ComputeLeftEdgeEven());
      registerControlSequence(new ComputeTopEdge());
      registerControlSequence(new ComputeBottomEdge());
      registerControlSequence(new ComputeRightEdgeOdd());
      registerControlSequence(new ComputeRightEdgeEven());

      registerControlSequence(new RefSavedRelativeLocation(this));
      registerControlSequence(new TextualContentCommand("FFabove", "above"));
      registerControlSequence(new TextualContentCommand("FFaboveleft", "above left"));
      registerControlSequence(new TextualContentCommand("FFaboveright", "above right"));
      registerControlSequence(new TextualContentCommand("FFbelow", "below"));
      registerControlSequence(new TextualContentCommand("FFbelowleft", "below left"));
      registerControlSequence(new TextualContentCommand("FFbelowright", "below right"));
      registerControlSequence(new TextualContentCommand("FFleft", "on the left"));
      registerControlSequence(new TextualContentCommand("FFright", "on the right"));
      registerControlSequence(new TextualContentCommand("FFoverlap", "overlap"));

      registerControlSequence(new TwoTone(this));
      registerControlSequence(new TwoTone("htwotone", false, this));
      registerControlSequence(new TwoToneBottom(this));
      registerControlSequence(new TwoToneBottom("htwotoneleft", false, this));
      registerControlSequence(new TwoToneTop(this));
      registerControlSequence(new TwoToneTop("htwotoneright", false, this));

      registerControlSequence(new MakeDFHeaderFooter(this));

      registerControlSequence(new FlowFramSetup(this));

      // ignore:
      registerControlSequence(new Relax("flowframeshowlayout"));
      registerControlSequence(new Relax("framebreak"));
      registerControlSequence(new Relax("enablethumbtabs"));
      registerControlSequence(new Relax("disablethumbtabs"));

      if (getListener() instanceof L2HConverter)
      {
         registerControlSequence(new ParCs("cleartoevenpage"));
      }

      registerControlSequence(new GenericCommand(true, "ffprechapterhook"));
      registerControlSequence(new GenericCommand(true, "ffruledeclarations"));
      registerControlSequence(new GobbleOpt("continueonframe", 1, 1));
      registerControlSequence(new AtGobble("ffcontinuedtextlayout"));
      registerControlSequence(new AtGobble("ffcontinuedtextfont"));
      registerControlSequence(new GobbleOpt("dfchaphead", 0, 1, '*'));
      registerControlSequence(new AtGobble("DFchapterstyle"));
      registerControlSequence(new AtGobble("DFschapterstyle"));
      registerControlSequence(new GobbleOptMandOpt("makethumbtabs", 1, 1, 1));
      registerControlSequence(new GobbleOpt("enableminitoc", 1, 0));
      registerControlSequence(new AtGobble("setthumbtab", 2));
      registerControlSequence(new GobbleOpt("ffswapoddeven", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("sfswapoddeven", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("dfswapoddeven", 0, 1, '*'));

      registerControlSequence(new GobbleOpt("flowswitchoffnext", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("flowswitchoffnextodd", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("flowswitchoffnextonly", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("flowswitchoffnextoddonly", 0, 1, '*'));

      registerControlSequence(new GobbleOpt("flowswitchonnext", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("flowswitchonnextodd", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("flowswitchonnextonly", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("flowswitchonnextoddonly", 0, 1, '*'));

      registerControlSequence(new GobbleOpt("dynamicswitchonnext", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("dynamicswitchonnextodd", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("dynamicswitchonnextonly", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("dynamicswitchonnextoddonly", 0, 1, '*'));

      registerControlSequence(new GobbleOpt("dynamicswitchoffnext", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("dynamicswitchoffnextodd", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("dynamicswitchoffnextonly", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("dynamicswitchoffnextoddonly", 0, 1, '*'));

      registerControlSequence(new GobbleOpt("staticswitchonnext", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("staticswitchonnextodd", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("staticswitchonnextonly", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("staticswitchonnextoddonly", 0, 1, '*'));

      registerControlSequence(new GobbleOpt("staticswitchoffnext", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("staticswitchoffnextodd", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("staticswitchoffnextonly", 0, 1, '*'));
      registerControlSequence(new GobbleOpt("staticswitchoffnextoddonly", 0, 1, '*'));

      registerControlSequence(new GobbleOpt("SaveRelativeFrameLocation", 0, 5, '*'));
   }

   @Override
   public void processOption(String option, TeXObject value)
    throws IOException
   {
      if (option.equals("pages"))
      {
         String str = getParser().expandToString(value, null).trim();

         pagesRelative = str.equals("relative");
      }
      else if (option.equals("LR"))
      {
         getParser().putControlSequence(true, new IfTrue("iflefttorightcolumns"));
      }
      else if (option.equals("RL"))
      {
         getParser().putControlSequence(true, new IfFalse("iflefttorightcolumns"));
      }
      else if (option.equals("verbose"))
      {
      }
      else if (option.equals("color"))
      {
      }
      else if (option.equals("nocolor"))
      {
      }
      else if (option.equals("rotate"))
      {
      }
      else if (option.equals("thumbtabs"))
      {
      }
      else if (option.equals("ttbtitle"))
      {
      }
      else if (option.equals("ttbnotitle"))
      {
      }
      else if (option.equals("ttbnum"))
      {
      }
      else if (option.equals("ttbnonum"))
      {
      }
   }

   public FlowFrameData newFlowFrame(String label, 
      boolean bordered, TeXDimension width, TeXDimension height,
      TeXDimension posX, TeXDimension posY)
    throws TeXSyntaxException
   {
      int id = flowFrames.size() + 1;

      if (label == null)
      {
         label = ""+id;
      }

      if (flowIdMap.containsKey(label))
      {
         throw new LaTeXSyntaxException(getParser(), 
          ERROR_FLOW_ALREADY_DEFINED, label);
      }

      getParser().putControlSequence(new TextualContentCommand(
        "@col@id@" + RomanNumeral.romannumeral(id), label));

      FlowFrameData data = new FlowFrameData(this, FlowFrameType.FLOW,
        label, id, bordered, width, height,
         posX, posY);

      flowFrames.add(data);
      flowIdMap.put(label, Integer.valueOf(id));

      listener.stepcounter("maxflow");

      if (currentFrame == null)
      {
         currentFrame = data;
      }

      return data;
   }

   public FlowFrameData getFlowFrame(String label)
   {
      Integer id = flowIdMap.get(label);

      if (id == null)
      {
         return null;
      }

      return flowFrames.get(id.intValue());
   }

   public FlowFrameData getFlowFrame(int id)
   {
      return flowFrames.get(id);
   }

   public void setFlowFrameLabel(int id, String newLabel)
    throws TeXSyntaxException
   {
      FlowFrameData data = getFlowFrame(id);

      if (data == null)
      {
         throw new LaTeXSyntaxException(getParser(),
           ERROR_FLOW_ID_NOT_DEFINED, id);
      }

      setFrameLabel(data, newLabel);
   }

   public int getFlowFrameId(String label)
    throws TeXSyntaxException
   {
      Integer id = flowIdMap.get(label);

      if (id == null)
      {
         throw new LaTeXSyntaxException(getParser(),
           ERROR_FLOW_LABEL_NOT_DEFINED, label);
      }

      return id.intValue();
   }

   public FlowFrameData newStaticFrame(String label, 
      boolean bordered, TeXDimension width, TeXDimension height,
      TeXDimension posX, TeXDimension posY)
    throws TeXSyntaxException
   {
      int id = staticFrames.size() + 1;

      if (label == null)
      {
         label = ""+id;
      }

      if (staticIdMap.containsKey(label))
      {
         throw new LaTeXSyntaxException(getParser(), 
          ERROR_STATIC_ALREADY_DEFINED, label);
      }

      getParser().putControlSequence(new TextualContentCommand(
        "@sf@id@" + RomanNumeral.romannumeral(id), label));

      FlowFrameData data = new FlowFrameData(this, FlowFrameType.STATIC,
        label, id, bordered, width, height,
         posX, posY);

      staticFrames.add(data);
      staticIdMap.put(label, Integer.valueOf(id));

      listener.stepcounter("maxstatic");

      return data;
   }

   public FlowFrameData getStaticFrame(String label)
   {
      Integer id = staticIdMap.get(label);

      if (id == null)
      {
         return null;
      }

      return staticFrames.get(id.intValue());
   }

   public FlowFrameData getStaticFrame(int id)
   {
      return staticFrames.get(id);
   }

   public void setStaticFrameLabel(int id, String newLabel)
    throws TeXSyntaxException
   {
      FlowFrameData data = getStaticFrame(id);

      if (data == null)
      {
         throw new LaTeXSyntaxException(getParser(),
           ERROR_STATIC_ID_NOT_DEFINED, id);
      }

      setFrameLabel(data, newLabel);
   }

   public int getStaticFrameId(String label)
    throws TeXSyntaxException
   {
      Integer id = staticIdMap.get(label);

      if (id == null)
      {
         throw new LaTeXSyntaxException(getParser(),
           ERROR_STATIC_LABEL_NOT_DEFINED, label);
      }

      return id.intValue();
   }

   public FlowFrameData newDynamicFrame(String label, 
      boolean bordered, TeXDimension width, TeXDimension height,
      TeXDimension posX, TeXDimension posY)
    throws TeXSyntaxException
   {
      int id = dynamicFrames.size() + 1;

      if (label == null)
      {
         label = ""+id;
      }

      if (dynamicIdMap.containsKey(label))
      {
         throw new LaTeXSyntaxException(getParser(), 
          ERROR_DYNAMIC_ALREADY_DEFINED, label);
      }

      getParser().putControlSequence(new TextualContentCommand(
        "@df@id@" + RomanNumeral.romannumeral(id), label));

      FlowFrameData data = new FlowFrameData(this, FlowFrameType.DYNAMIC,
        label, id, bordered, width, height,
         posX, posY);

      dynamicFrames.add(data);
      dynamicIdMap.put(label, Integer.valueOf(id));

      listener.stepcounter("maxdynamic");

      return data;
   }

   public FlowFrameData getDynamicFrame(String label)
   {
      Integer id = dynamicIdMap.get(label);

      if (id == null)
      {
         return null;
      }

      return dynamicFrames.get(id.intValue());
   }

   public FlowFrameData getDynamicFrame(int id)
   {
      return dynamicFrames.get(id);
   }

   public void setDynamicFrameLabel(int id, String newLabel)
    throws TeXSyntaxException
   {
      FlowFrameData data = getDynamicFrame(id);

      if (data == null)
      {
         throw new LaTeXSyntaxException(getParser(),
           ERROR_DYNAMIC_ID_NOT_DEFINED, id);
      }

      setFrameLabel(data, newLabel);
   }

   public int getDynamicFrameId(String label)
    throws TeXSyntaxException
   {
      Integer id = dynamicIdMap.get(label);

      if (id == null)
      {
         throw new LaTeXSyntaxException(getParser(),
           ERROR_DYNAMIC_LABEL_NOT_DEFINED, label);
      }

      return id.intValue();
   }

   public FlowFrameData getFrame(FlowFrameType type, String label)
    throws TeXSyntaxException
   {
      FlowFrameData data = null;

      switch (type)
      {
         case STATIC:
            data = getStaticFrame(label);
         break;
         case DYNAMIC:
            data = getDynamicFrame(label);
         break;
         case FLOW:
            data = getFlowFrame(label);
         break;
      }

      if (data == null)
      {
         throw new LaTeXSyntaxException(getParser(),
            ERROR_LABEL_NOT_DEFINED, label, type
          );
      }

      return data;
   }

   public FlowFrameData getFrame(FlowFrameType type, int id)
    throws TeXSyntaxException
   {
      FlowFrameData data = null;

      switch (type)
      {
         case STATIC:
            data = getStaticFrame(id);
         break;
         case DYNAMIC:
            data = getDynamicFrame(id);
         break;
         case FLOW:
            data = getFlowFrame(id);
         break;
      }

      if (data == null)
      {
         throw new LaTeXSyntaxException(getParser(),
            ERROR_ID_NOT_DEFINED, id, type
          );
      }

      return data;
   }

   public void setFrameLabel(FlowFrameData data, String newLabel)
    throws TeXSyntaxException
   {
      Integer id;

      switch (data.getType())
      {
         case STATIC:
            id = staticIdMap.remove(data.getLabel());

            if (id == null || id.intValue() != data.getID())
            {
               id = Integer.valueOf(data.getID());
            }

            data.setLabel(newLabel);
            staticIdMap.put(data.getLabel(), id);

            getParser().putControlSequence(new TextualContentCommand(
              "@sf@id@" + RomanNumeral.romannumeral(id), newLabel));
         break;
         case DYNAMIC:
            id = dynamicIdMap.remove(data.getLabel());

            if (id == null || id.intValue() != data.getID())
            {
               id = Integer.valueOf(data.getID());
            }

            data.setLabel(newLabel);
            dynamicIdMap.put(data.getLabel(), id);
            getParser().putControlSequence(new TextualContentCommand(
              "@df@id@" + RomanNumeral.romannumeral(id), newLabel));
         break;
         case FLOW:
            id = flowIdMap.remove(data.getLabel());

            if (id == null || id.intValue() != data.getID())
            {
               id = Integer.valueOf(data.getID());
            }

            data.setLabel(newLabel);
            flowIdMap.put(data.getLabel(), id);
            getParser().putControlSequence(new TextualContentCommand(
              "@col@id@" + RomanNumeral.romannumeral(id), newLabel));
         break;
      }
   }

   public int getMaxFlowFrames()
   {
      return flowFrames.size();
   }

   public int getMaxStaticFrames()
   {
      return staticFrames.size();
   }

   public int getMaxDynamicFrames()
   {
      return dynamicFrames.size();
   }

   public Color getColor(TeXObject obj)
   throws IOException
   {
      TeXParser parser = getParser();

      TeXObjectList list = TeXParserUtils.toList(obj, parser);

      String modelName = TeXParserUtils.popOptLabelString(parser, list);

      if (modelName == null)
      {
         modelName = "named";

         Color col = colorSty.getDefinedColor(list.toString(parser).trim());

         if (col != null)
         {
            return col;
         }
      }
      else
      {
         modelName = modelName.trim();
      }

      String value = TeXParserUtils.popLabelString(parser, list).trim();

      return colorSty.getColor(parser, modelName, value);
   }

   public Enumeration<FlowFrameData> getFlowElements()
   {
      return flowFrames.elements();
   }

   public Enumeration<FlowFrameData> getStaticElements()
   {
      return staticFrames.elements();
   }

   public Enumeration<FlowFrameData> getDynamicElements()
   {
      return dynamicFrames.elements();
   }

   @Override
   public void documentBegun(BeginDocumentEvent evt)
    throws IOException
   {
      if (listener instanceof L2HConverter)
      {
         L2HConverter l2h = (L2HConverter)listener;

         if (!staticFrames.isEmpty() || !dynamicFrames.isEmpty())
         {
            l2h.write("<style>");

            for (FlowFrameData data : staticFrames)
            {
               data.writeCss(l2h);
            }

            for (FlowFrameData data : dynamicFrames)
            {
               data.writeCss(l2h);
            }

            l2h.write("</style>");
         }
      }

      Vector<AuxData> auxData = listener.getAuxData();

      if (auxData != null)
      {
         for (AuxData ad : auxData)
         {
            processAuxData(ad);
         }
      }
   }

   public void processAuxData(AuxData data)
    throws IOException
   {
      TeXParser parser = getParser();
      String strValue;

      if (data.getName().equals("flowfram@doc@htmlopts")
      || data.getName().equals("flowfram@preamble@htmlopts"))
      {
         int idx;

         strValue = data.getArg(0).toString(parser);

         try
         {
            idx = Integer.parseInt(strValue);
         }
         catch (NumberFormatException e)
         {
            throw new TeXSyntaxException(e, parser, 
              TeXSyntaxException.ERROR_NUMBER_EXPECTED, strValue);
         }

         FrameHtmlOptions fho = new FrameHtmlOptions(idx);

         fho.type = data.getArg(1).toString(parser);

         strValue = data.getArg(2).toString(parser);

         try
         {
            fho.frameId = Integer.parseInt(strValue);
         }
         catch (NumberFormatException e)
         {
            throw new TeXSyntaxException(e, parser, 
              TeXSyntaxException.ERROR_NUMBER_EXPECTED, strValue);
         }

         KeyValList options = KeyValList.getList(parser, data.getArg(3));

         fho.width = options.getString("width", parser, null);
         fho.height = options.getString("height", parser, null);

         fho.thepage = data.getArg(4).toString(parser);
         fho.theabsolutepage = data.getArg(5).toString(parser);

         if (frameHtmlOptionsMap == null)
         {
            frameHtmlOptionsMap = new HashMap<Integer,FrameHtmlOptions>();
         }

         frameHtmlOptionsMap.put(Integer.valueOf(idx), fho);
      }
      else if (data.getName().equals("@flowfram@saverelativeloc"))
      {
         RelativeLocationData relData = new RelativeLocationData(
           data.getArg(0).toString(parser));

         strValue = data.getArg(1).toString(parser);

         try
         {
            relData.pageNum = Integer.parseInt(strValue);
         }
         catch (NumberFormatException e)
         {
            throw new TeXSyntaxException(e, parser, 
              TeXSyntaxException.ERROR_NUMBER_EXPECTED, strValue);
         }

         TeXObject obj = data.getArg(2);

         if (parser.isStack(obj))
         {
            TeXObjectList list = (TeXObjectList)obj;

            if (list.size() == 6)
            {
               relData.x1 = TeXParserUtils.popDimensionArg(parser, list);
               relData.y1 = TeXParserUtils.popDimensionArg(parser, list);
               relData.evenX1 = TeXParserUtils.popDimensionArg(parser, list);
               relData.evenY1 = TeXParserUtils.popDimensionArg(parser, list);
               relData.width1 = TeXParserUtils.popDimensionArg(parser, list);
               relData.height1 = TeXParserUtils.popDimensionArg(parser, list);
            }
            else
            {
               throw new LaTeXSyntaxException(parser, INVALID_CMD_ARG, obj.toString(parser));
            }
         }
         else
         {
            throw new LaTeXSyntaxException(parser, 
              INVALID_CMD_ARG, data.getName(), obj.toString(parser));
         }

         obj = data.getArg(3);

         if (parser.isStack(obj))
         {
            TeXObjectList list = (TeXObjectList)obj;

            if (list.size() == 6)
            {
               relData.x2 = TeXParserUtils.popDimensionArg(parser, list);
               relData.y2 = TeXParserUtils.popDimensionArg(parser, list);
               relData.evenX2 = TeXParserUtils.popDimensionArg(parser, list);
               relData.evenY2 = TeXParserUtils.popDimensionArg(parser, list);
               relData.width2 = TeXParserUtils.popDimensionArg(parser, list);
               relData.height2 = TeXParserUtils.popDimensionArg(parser, list);
            }
            else
            {
               throw new LaTeXSyntaxException(parser, INVALID_CMD_ARG, obj.toString(parser));
            }
         }
         else
         {
            throw new LaTeXSyntaxException(parser, 
              INVALID_CMD_ARG, data.getName(), obj.toString(parser));
         }

         relData.calculate();

         if (relLocDataMap == null)
         {
            relLocDataMap = new HashMap<String,RelativeLocationData>();
         }

         relLocDataMap.put(relData.getLabel(), relData);
      }
   }

   public RelativeLocationData getRelativeLocationData(String label)
   {
      return relLocDataMap == null ? null : relLocDataMap.get(label);
   }

   public void incrFrameHtmlOptionsIndex()
   {
      frameHtmlOptionsIndex++;
   }

   public FrameHtmlOptions getFrameHtmlOptions(int index)
   {
      return frameHtmlOptionsMap == null ? null : 
              frameHtmlOptionsMap.get(Integer.valueOf(index));
   }

   public FrameHtmlOptions getCurrentFrameHtmlOptions()
   {
      return getFrameHtmlOptions(frameHtmlOptionsIndex);
   }

   boolean pagesRelative = true;

   FlowFrameData currentFrame;

   Vector<FlowFrameData> flowFrames;
   Vector<FlowFrameData> staticFrames;
   Vector<FlowFrameData> dynamicFrames;

   HashMap<String,Integer> flowIdMap;
   HashMap<String,Integer> staticIdMap;
   HashMap<String,Integer> dynamicIdMap;

   HashMap<Integer,FrameHtmlOptions> frameHtmlOptionsMap;
   private int frameHtmlOptionsIndex = 0;

   HashMap<String,RelativeLocationData> relLocDataMap;

   protected ColorSty colorSty;

   public static final String ERROR_FLOW_ALREADY_DEFINED
     = "flowfram.flow_already_defined";
   public static final String ERROR_FLOW_ID_NOT_DEFINED
     = "flowfram.flow_id_not_defined";
   public static final String ERROR_FLOW_LABEL_NOT_DEFINED
     = "flowfram.flow_label_not_defined";

   public static final String ERROR_STATIC_ALREADY_DEFINED
     = "staticfram.static_already_defined";
   public static final String ERROR_STATIC_ID_NOT_DEFINED
     = "staticfram.static_id_not_defined";
   public static final String ERROR_STATIC_LABEL_NOT_DEFINED
     = "staticfram.static_label_not_defined";

   public static final String ERROR_DYNAMIC_ALREADY_DEFINED
     = "dynamicfram.dynamic_already_defined";
   public static final String ERROR_DYNAMIC_ID_NOT_DEFINED
     = "dynamicfram.dynamic_id_not_defined";
   public static final String ERROR_DYNAMIC_LABEL_NOT_DEFINED
     = "dynamicfram.dynamic_label_not_defined";

   public static final String ERROR_LABEL_NOT_DEFINED
     = "flowfram.label_not_defined";
   public static final String ERROR_ID_NOT_DEFINED
     = "flowfram.id_not_defined";

   public static final String INVALID_FRAME_SETTING
     = "flowfram.invalid_frame_setting";
   public static final String INVALID_FRAME_NOVAL_SETTING
     = "flowfram.invalid_frame_noval_setting";

   public static final String INVALID_CMD_ARG
     = "flowfram.invalid_cmd_arg";

   public static final String LOCDATA_LABEL_NOT_DEFINED
     = "flowfram.locdata_label_not_defined";
}
