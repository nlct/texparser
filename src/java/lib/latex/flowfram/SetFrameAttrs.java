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
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class SetFrameAttrs extends ControlSequence
{
   public SetFrameAttrs(String name, FlowFrameType type, FlowFramSty sty)
   {
      super(name);

      this.type = type;
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new SetFrameAttrs(getName(), type, sty);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   protected void applySettings(TeXParser parser,
     TeXObjectList stack,
     FlowFrameData data, KeyValList settings)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();
      String strVal;
      TeXDimension dim;
      TeXNumber num;
      TeXObject html = null;

      for (Iterator<String> it = settings.keySet().iterator();
           it.hasNext(); )
      {
         String key = it.next();
         TeXObject value = settings.get(key);

         if (value instanceof MissingValue)
         {
            value = null;
         }

         try
         {
            if (key.equals("width"))
            {
               if (value instanceof TeXDimension)
               {
                  dim = (TeXDimension)value;
               }
               else
               {
                  dim = TeXParserUtils.toTeXDimension(value, parser, stack);
                  settings.put(key, dim);
               }

               data.setWidth(dim);
            }
            else if (key.equals("height"))
            {
               if (value instanceof TeXDimension)
               {
                  dim = (TeXDimension)value;
               }
               else
               {
                  dim = TeXParserUtils.toTeXDimension(value, parser, stack);
                  settings.put(key, dim);
               }

               data.setHeight(dim);
            }
            else if (key.equals("x"))
            {
               if (value instanceof TeXDimension)
               {
                  dim = (TeXDimension)value;
               }
               else
               {
                  dim = TeXParserUtils.toTeXDimension(value, parser, stack);
                  settings.put(key, dim);
               }

               data.setX(dim);
            }
            else if (key.equals("y"))
            {
               if (value instanceof TeXDimension)
               {
                  dim = (TeXDimension)value;
               }
               else
               {
                  dim = TeXParserUtils.toTeXDimension(value, parser, stack);
                  settings.put(key, dim);
               }

               data.setY(dim);
            }
            else if (key.equals("evenx"))
            {
               if (value instanceof TeXDimension)
               {
                  dim = (TeXDimension)value;
               }
               else
               {
                  dim = TeXParserUtils.toTeXDimension(value, parser, stack);
                  settings.put(key, dim);
               }

               data.setEvenX(dim);
            }
            else if (key.equals("eveny"))
            {
               if (value instanceof TeXDimension)
               {
                  dim = (TeXDimension)value;
               }
               else
               {
                  dim = TeXParserUtils.toTeXDimension(value, parser, stack);
                  settings.put(key, dim);
               }

               data.setEvenY(dim);
            }
            else if (key.equals("oddx"))
            {
               if (value instanceof TeXDimension)
               {
                  dim = (TeXDimension)value;
               }
               else
               {
                  dim = TeXParserUtils.toTeXDimension(value, parser, stack);
                  settings.put(key, dim);
               }

               data.setOddX(dim);
            }
            else if (key.equals("oddy"))
            {
               if (value instanceof TeXDimension)
               {
                  dim = (TeXDimension)value;
               }
               else
               {
                  dim = TeXParserUtils.toTeXDimension(value, parser, stack);
                  settings.put(key, dim);
               }

               data.setOddY(dim);
            }
            else if (key.equals("offset"))
            {
               if (value instanceof TeXDimension)
               {
                  dim = (TeXDimension)value;
               }
               else
               {
                  dim = TeXParserUtils.toTeXDimension(value, parser, stack);
                  settings.put(key, dim);
               }

               data.setOffset(dim);
            }
            else if (key.equals("valign"))
            {
               if (value instanceof DataObjectList)
               {
                  strVal = value.toString(parser);
               }
               else
               {
                  strVal = parser.expandToString(value, stack).trim();
                  settings.put(key, new DataObjectList(listener, strVal));
               }

               if (strVal.length() != 1)
               {
                  throw new LaTeXSyntaxException(parser,
                    LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE, key, strVal);
               }

               data.setVAlign(strVal.charAt(0));
            }
            else if (key.equals("label"))
            {
               if (value instanceof DataObjectList)
               {
                  strVal = value.toString(parser);
               }
               else
               {
                  strVal = parser.expandToString(value, stack).trim();
                  settings.put(key, new DataObjectList(listener, strVal));
               }

               sty.setFrameLabel(data, strVal);
            }
            else if (key.equals("style"))
            {
               if (value instanceof DataObjectList)
               {
                  strVal = value.toString(parser);
               }
               else
               {
                  strVal = parser.expandToString(value, stack).trim();
                  settings.put(key, new DataObjectList(listener, strVal));
               }

               data.setStyle(strVal);
            }
            else if (key.equals("border"))
            {
               if (value instanceof DataObjectList)
               {
                  strVal = value.toString(parser);
               }
               else
               {
                  strVal = parser.expandToString(value, stack).trim();
                  settings.put(key, new DataObjectList(listener, strVal));
               }

               data.setFrameBorderCsName(strVal);
            }
            else if (key.equals("bordercolor"))
            {
               data.setBorderColor(sty.getColor(value));
            }
            else if (key.equals("textcolor"))
            {
               data.setTextColor(sty.getColor(value));
            }
            else if (key.equals("backcolor"))
            {
               data.setBackColor(sty.getColor(value));
            }
            else if (key.equals("pages"))
            {
               if (value instanceof DataObjectList)
               {
                  strVal = value.toString(parser);
               }
               else
               {
                  strVal = parser.expandToString(value, stack).trim();
                  settings.put(key, new DataObjectList(listener, strVal));
               }

               data.setPageList(strVal);
            }
            else if (key.equals("excludepages"))
            {
               if (value instanceof DataObjectList)
               {
                  strVal = value.toString(parser);
               }
               else
               {
                  strVal = parser.expandToString(value, stack).trim();
                  settings.put(key, new DataObjectList(listener, strVal));
               }

               data.setExcludedPageList(strVal);
            }
            else if (key.equals("hide"))
            {
               if (value == null || value.isEmpty())
               {
                  data.setHidden(true);
               }
               else
               {
                  if (value instanceof DataObjectList)
                  {
                     strVal = value.toString(parser);
                  }
                  else
                  {
                     strVal = parser.expandToString(value, stack).trim();
                     settings.put(key, new DataObjectList(listener, strVal));
                  }

                  data.setHidden(Boolean.parseBoolean(strVal));
               }
            }
            else if (key.equals("hidethis"))
            {// can't implement as no output routine
            }
            else if (key.equals("clear"))
            {
               if (value == null || value.isEmpty())
               {
                  data.setClear(true);
               }
               else
               {
                  if (value instanceof DataObjectList)
                  {
                     strVal = value.toString(parser);
                  }
                  else
                  {
                     strVal = parser.expandToString(value, stack).trim();
                     settings.put(key, new DataObjectList(listener, strVal));
                  }

                  data.setClear(Boolean.parseBoolean(strVal));
               }
            }
            else if (key.equals("margin"))
            {
               if (value instanceof DataObjectList)
               {
                  strVal = value.toString(parser);
               }
               else
               {
                  strVal = parser.expandToString(value, stack).trim();
                  settings.put(key, new DataObjectList(listener, strVal));
               }

               data.setMarginSide(strVal);
            }
            else if (key.equals("style"))
            {
               if (value instanceof DataObjectList)
               {
                  strVal = value.toString(parser);
               }
               else
               {
                  strVal = parser.expandToString(value, stack).trim();
                  settings.put(key, new DataObjectList(listener, strVal));
               }

               data.setStyle(strVal);
            }
            else if (key.equals("shape"))
            {
               if (value == null || value.isEmpty()
                   || TeXParserUtils.isControlSequence(value, "relax"))
               {
                  data.setShape(null);
               }
               else
               {
                  data.setShape((TeXObject)value.clone());
               }
            }
            else if (key.equals("angle"))
            {
               if (value instanceof TeXNumber)
               {
                  num = (TeXNumber)value;
               }
               else
               {
                  num = TeXParserUtils.toTeXNumber(false,
                    value, parser, stack);
                  settings.put(key, num);
               }

               data.setAngle(num);
            }
            else if (key.equals("html"))
            {
               html = (TeXObject)value.clone();
            }

         }
         catch (NullPointerException e)
         {
            throw new LaTeXSyntaxException(e, parser,
              LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE, key);
         }
         catch (IllegalArgumentException e)
         {
            if (value == null)
            {
               throw new LaTeXSyntaxException(e, parser,
                 FlowFramSty.INVALID_FRAME_NOVAL_SETTING, key, type);
            }
            else
            {
               throw new LaTeXSyntaxException(e, parser,
                 FlowFramSty.INVALID_FRAME_SETTING, key, 
                   value.toString(parser), type);
            }
         }
      }

      if (html != null)
      {
         sty.incrFrameHtmlOptionsIndex();

         KeyValList htmlOpts = TeXParserUtils.toKeyValList(html, parser);

         Boolean show = htmlOpts.getBoolean("show", parser, stack);

         if (show != null && show.booleanValue())
         {
            data.showContent(parser, stack, htmlOpts);
         }
      }
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean isStar = (popModifier(parser, stack, '*') == '*');
      String[] list = popLabelString(parser, stack).trim().split(" *, *");
      KeyValList settings = TeXParserUtils.popKeyValList(parser, stack);

      FlowFrameData data;

      if (isStar)
      {
         for (String label : list)
         {
            data = sty.getFrame(type, label);
            applySettings(parser, stack, data, settings);
         }
      }
      else
      {
         for (String label : list)
         {
            try
            {
               int id = Integer.parseInt(label);
               data = sty.getFrame(type, id);
               applySettings(parser, stack, data, settings);
            }
            catch (NumberFormatException e)
            {
               throw new TeXSyntaxException(e, parser,
                        TeXSyntaxException.ERROR_NUMBER_EXPECTED, label);
            }
         }
      }
   }

   FlowFramSty sty;
   FlowFrameType type;
}
