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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class StartFrameBox extends AbstractTeXObject
{
   public StartFrameBox(FrameBox fbox)
   {
      this.fbox = fbox;
   }

   @Override
   public Object clone()
   {
      return new StartFrameBox((FrameBox)fbox.clone());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = ((LaTeXParserListener)parser.getListener());

      parser.startGroup();

      if (parser.getDebugLevel() >= TeXParser.DEBUG_PROCESSING)
      {
         parser.logMessage("Applying settings for "+fbox);
      }

      fbox.applyToSettings(parser.getSettings());
      listener.startFrameBox(fbox);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      LaTeXParserListener listener = ((LaTeXParserListener)parser.getListener());

      parser.startGroup();

      if (parser.getDebugLevel() >= TeXParser.DEBUG_PROCESSING)
      {
         parser.logMessage("Applying settings for "+fbox);
      }

      fbox.applyToSettings(parser.getSettings());
      listener.startFrameBox(fbox);
   }

   @Override
   public String toString()
   {
      return String.format("%s[fbox=%s]", getClass().getSimpleName(),
        fbox);
   }

   @Override
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public String format()
   {
      return "";
   }

   @Override
   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return new TeXObjectList();
   }

   @Override
   public String toString(TeXParser parser)
   {
      return "";
   }

   public FrameBox getFrameBox()
   {
      return fbox;
   }

   private FrameBox fbox;
}
