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
package com.dickimawbooks.texparserlib.latex.hyperref;

import java.io.IOException;

import com.dickimawbooks.texparserlib.TeXParser;
import com.dickimawbooks.texparserlib.TeXParserUtils;
import com.dickimawbooks.texparserlib.TeXObjectList;
import com.dickimawbooks.texparserlib.TeXObject;
import com.dickimawbooks.texparserlib.CharObject;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.etoolbox.CsDef;

public class HyperrefSty extends LaTeXSty
{
   public HyperrefSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
    throws IOException
   {
      super(options, "hyperref", listener, loadParentOptions);
   }

   @Override
   protected void preOptions(TeXObjectList stack) throws IOException
   {
      getListener().requirepackage(null, "color", false, stack);
   }

   @Override
   protected void postOptions(TeXObjectList stack) throws IOException
   {
      super.postOptions(stack);

      processCollectedValues(stack);
   }

   @Override
   public void processOption(String option, TeXObject value)
    throws IOException
   {
      if (option.equals("pdftitle"))
      {
         pdfTitle = value;
      }
      else if (option.equals("pdfauthor"))
      {
         pdfAuthor = value;
      }
      else if (option.equals("pdfsubject"))
      {
         pdfSubject = value;
      }
      else if (option.equals("pdfcreator"))
      {
         pdfCreator = value;
      }
      else if (option.equals("pdfcreationdate"))
      {
         pdfCreationDate = value;
      }
      else if (option.equals("pdfmoddate"))
      {
         pdfModDate = value;
      }
      else if (option.equals("pdfinfo"))
      {
         KeyValList info = TeXParserUtils.toKeyValList(value, getParser());

         TeXObject infoVal = info.getValue("Title");

         if (infoVal != null)
         {
            pdfTitle = infoVal;
         }

         infoVal = info.getValue("Author");

         if (infoVal != null)
         {
            pdfAuthor = infoVal;
         }

         infoVal = info.getValue("Subject");

         if (infoVal != null)
         {
            pdfSubject = infoVal;
         }

         infoVal = info.getValue("Creator");

         if (infoVal != null)
         {
            pdfCreator = infoVal;
         }

         infoVal = info.getValue("CreationDate");

         if (infoVal != null)
         {
            pdfCreationDate = infoVal;
         }

         infoVal = info.getValue("ModDate");

         if (infoVal != null)
         {
            pdfModDate = infoVal;
         }
      }
      else if (option.equals("baseurl"))
      {
         baseUrlObj = value;
      }
   }

   public void setup(KeyValList options, TeXObjectList stack) throws IOException
   {     
      processOptions(options);
      processCollectedValues(stack);
   }

   protected void processCollectedValues(TeXObjectList stack) throws IOException
   {
      TeXParser parser = getParser();

      if (pdfTitle != null)
      {
         listener.setDocumentProperty(
           "Title", parser.expandToString(pdfTitle, stack));

         pdfTitle = null;
      }

      if (pdfAuthor != null)
      {
         listener.setDocumentProperty(
           "Author", parser.expandToString(pdfAuthor, stack));

         pdfAuthor = null;
      }

      if (pdfSubject != null)
      {
         listener.setDocumentProperty(
           "Subject", parser.expandToString(pdfSubject, stack));

         pdfSubject = null;
      }

      if (pdfCreator != null)
      {
         listener.setDocumentProperty(
           "Creator", parser.expandToString(pdfCreator, stack));

         pdfCreator = null;
      }

      if (pdfKeywords != null)
      {
         listener.setDocumentProperty(
           "Keywords", parser.expandToString(pdfKeywords, stack));

         pdfKeywords = null;
      }

      if (pdfModDate != null)
      {
         listener.setModifiedDate(
           TeXParserUtils.parsePDFDate(pdfModDate, parser, stack));

         pdfModDate = null;
      }

      if (pdfCreationDate != null)
      {
         listener.setCreationDate(
           TeXParserUtils.parsePDFDate(pdfCreationDate, parser, stack));

         pdfCreationDate = null;
      }

      if (baseUrlObj != null)
      {
         setBaseUrl(TeXParserUtils.expandFully(baseUrlObj, parser, stack));

         baseUrlObj = null;
      }
   }

   @Override
   public void addDefinitions()
   {
      registerControlSequence(new HyperTarget());
      registerControlSequence(new HyperLink());
      registerControlSequence(new HyperRef(this));
      registerControlSequence(new Href(this));
      registerControlSequence(new NoLinkUrl());
      registerControlSequence(new Url(this));
      registerControlSequence(new HyperBaseUrl(this));
      registerControlSequence(new HyperSetup(this));
      registerControlSequence(new AtFirstOfTwo("texorpdfstring"));
      // automatically implement unicode package option
      registerControlSequence(new AtFirstOfTwo("ifpdfstringunicode"));
      registerControlSequence(new SymbolCs("unichar"));
      // ignore bookmark commands
      registerControlSequence(new GobbleOpt("pdfbookmark", 1, 2));
      registerControlSequence(new GobbleOpt("currentpdfbookmark", 0, 2));
      registerControlSequence(new GobbleOpt("subpdfbookmark", 0, 2));
      registerControlSequence(new GobbleOpt("belowpdfbookmark", 0, 2));
      registerControlSequence(new AtGobble("thispdfpagelabel"));
      registerControlSequence(new LaTeXGenericEnvironment("HoHyper"));
      // make pdfstringdef simply behave like csdef
      registerControlSequence(new CsDef("pdfstringdef"));
   }

   /**
    * Prepends base URL, if supplied.
    * There's not check to determine if the URL is valid.
    * @param url URL string
    * @return full URL with base prepended
    */ 
   public String toFullUrl(String url)
   {
      if (baseUrl == null)
      {
         return url;
      }

      return baseUrl+url;
   }

   public void setBaseUrl(TeXObject baseObj)
   {
      TeXParser parser = getParser();

      if (baseObj instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)baseObj;

         for (int i = 0; i < list.size(); i++)
         {
            TeXObject obj = list.get(i);

            if (obj instanceof CharObject
                  && ((CharObject)obj).getCharCode()==0x00A0)
            {
               list.set(i, parser.getListener().getOther('~'));
            }
         }
      }

      baseUrl = baseObj.toString(parser);
   }

   public void setBaseUrl(String base)
   {
      baseUrl = base;
   }

   protected String baseUrl = null;

   protected TeXObject pdfTitle, pdfAuthor, pdfSubject, pdfCreator,
    pdfCreationDate, pdfModDate, pdfKeywords, baseUrlObj;
}
