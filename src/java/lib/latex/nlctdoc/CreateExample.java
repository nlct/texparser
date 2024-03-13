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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.io.File;
import java.awt.Color;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class CreateExample extends ControlSequence
{
   public CreateExample(UserGuideSty sty)
   {
      this("createexample", sty);
   }

   public CreateExample(String name, UserGuideSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new CreateExample(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      popModifier(parser, stack, '*');

      KeyValList options = TeXParserUtils.popOptKeyValList(parser, stack);
      popArg(parser, stack);
      popArg(parser, stack);

      TeXObject title = null;
      TeXObject description = null;
      String label = null;
      int[] pageList = null;
      String backLink = null;

      if (options != null)
      {
         title = options.get("title");
         description = options.get("description");

         if (description != null && parser.isStack(description))
         {
            ((TeXObjectList)description).add(listener.getOther('.'));
         }

         TeXObject obj = options.get("label");

         if (obj != null)
         {
            label = parser.expandToString(obj, stack);
         }

         obj = options.get("link");

         if (obj != null)
         {
            backLink = parser.expandToString(obj, stack);
         }

         obj = options.get("pages");

         if (obj != null)
         {
            String pageListStr = parser.expandToString(obj, stack).trim();

            if (!pageListStr.isEmpty())
            {
               String[] pages = pageListStr.split(" *, *");

               pageList = new int[pages.length];

               for (int i = 0; i < pages.length; i++)
               {
                  try
                  {
                     pageList[i] = Integer.parseInt(pages[i]);
                  }
                  catch (NumberFormatException e)
                  {
                     throw new TeXSyntaxException(e, parser,
                      TeXSyntaxException.ERROR_NUMBER_EXPECTED, pages[i]);
                  }
               }
            }
         }
      }

      TeXObjectList substack = listener.createStack();

      if (backLink == null && label != null)
      {
         TeXObject ref = listener.getReference(label+"-backref");

         if (ref != null)
         {
            backLink = label+"-backref";
         }
      }

      if (backLink != null)
      {
         substack.add(listener.createLink(backLink, listener.getControlSequence("upsym")));
      }

      substack.add(listener.getControlSequence("refstepcounter"));
      substack.add(listener.createGroup("example"));

      if (label != null)
      {
         substack.add(listener.getControlSequence("label"));
         substack.add(listener.createGroup(label));

      }
      else
      {
         NumericRegister reg = parser.getSettings().getNumericRegister("c@example");

         if (reg == null)
         {
            throw new LaTeXSyntaxException(parser,
               LaTeXSyntaxException.ERROR_UNDEFINED_COUNTER, "example");
         }

         int num = reg.number(parser)+1;

         substack.add(listener.getControlSequence("hypertarget"));
         substack.add(listener.createGroup("example."+num));
         substack.add(listener.createGroup());
      }

      TeXParserUtils.process(substack, parser, stack);

      substack.add(listener.getControlSequence("nlctexampletag"));

      if (title != null)
      {
         substack.add(listener.getOther(':'));
         substack.add(listener.getSpace());
         substack.add(title);
         substack.add(listener.getSpace());
      }

      if (sty.isDraft())
      {
         substack.add(listener.getPar());
         substack.add(listener.createString("[DRAFT MODE ON]"));

         TeXParserUtils.process(substack, parser, stack);
      }
      else
      {
         String dir = parser.expandToString(listener.getControlSequence("examplesdir"),
            stack);

         TeXObject baseObj = listener.getControlSequence("nlctexamplefilebasename");

         baseObj = TeXParserUtils.expandOnce(baseObj, parser, stack);
         String base = parser.expandToString(baseObj, stack);

         ControlSequence hrefCs = listener.getControlSequence("href");

         substack.add(hrefCs);
         substack.add(listener.createGroup(dir+"/"+base+".tex"));
         substack.add(listener.getControlSequence("exampledownloadtexicon"));

         substack.add(listener.getSpace());

         String pdfPath = dir+"/"+base+".pdf";
         String imgPath = pdfPath;
         File pdfFile = new File(dir, base+".pdf");

         substack.add(hrefCs);
         substack.add(listener.createGroup(pdfPath));
         substack.add(listener.getControlSequence("exampledownloadpdficon"));

         File croppedPdfFile = new File(dir, base+"-crop.pdf");

         if (croppedPdfFile.exists())
         {
            pdfFile = croppedPdfFile;
            imgPath = dir+"/"+pdfFile.getName();
         }

         substack.add(listener.getPar());

         TeXParserUtils.process(substack, parser, stack);

         KeyValList imgOptions = new KeyValList();

         if (description != null)
         {
            imgOptions.put("alt", description);
         }

         if (pageList == null)
         {
            try
            {
               File pngFile = new File(dir, base+".png");

               if (!pngFile.exists())
               {
                  listener.getTeXApp().convertimage(-1, null, pdfFile, null, pngFile);
               }

               if (pngFile.exists())
               {
                  imgPath = dir+"/"+base+".png";
               }
            }
            catch (IOException | InterruptedException e)
            {
               parser.logMessage(e);
            }

            listener.includegraphics(stack, imgOptions, imgPath);
         }
         else
         {
            for (int i = 0; i < pageList.length; i++)
            {
               imgPath = null;

               try
               {
                  String basename = String.format("%s-page%d.png", base, pageList[i]);
                  File pngFile = new File(dir, basename);

                  if (!pngFile.exists())
                  {
                     listener.getTeXApp().convertimage(
                        pageList[i], null, pdfFile, null, pngFile);
                  }

                  if (pngFile.exists())
                  {
                     imgPath = dir+"/"+basename;
                  }
               }
               catch (IOException | InterruptedException e)
               {
                  throw new LaTeXSyntaxException(e, parser, 
                    LaTeXSyntaxException.PACKAGE_ERROR, "nlctuserguide", e.getMessage());
               }

               if (imgPath != null)
               {
                  TeXObjectList altList = listener.createString(
                       "Page "+pageList[i]+". ");

                  if (description != null)
                  {
                     altList.add((TeXObject)description.clone(), true);
                  }

                  imgOptions.put("alt", altList);
                  imgOptions.put("class", listener.createString("pageimage"));

                  listener.includegraphics(stack, imgOptions, imgPath);
                  listener.getWriteable().writeln("");
               }
            }
         }
      }
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   private UserGuideSty sty;
}
