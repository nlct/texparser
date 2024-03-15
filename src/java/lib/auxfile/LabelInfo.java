/*
    Copyright (C) 2024 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.*;

/**
 * Label information obtained from <code>\newlabel</code>.
 */

public class LabelInfo
{
   protected LabelInfo(String label)
   {
      this.label = label;
   }

   public static LabelInfo createLabel(AuxData auxData, TeXParser parser)
   {
      String label = auxData.getArg(0).toString(parser);

      LabelInfo info = new LabelInfo(label);

      TeXObject arg = auxData.getArg(1);

      if (parser.isStack(arg))
      {
         TeXObjectList params = (TeXObjectList)arg;

         if (params.size() > 0)
         {
            info.reference = params.get(0);

            if (info.reference instanceof Group)
            {
               info.reference = TeXParserUtils.removeGroup((TeXObjectList)info.reference);
            }
         }

         if (params.size() > 1)
         {
            info.page = params.get(1);

            if (info.page instanceof Group)
            {
               info.page = TeXParserUtils.removeGroup((TeXObjectList)info.page);
            }
         }

         if (params.size() > 2)
         {
            info.title = params.get(2);

            if (info.title instanceof Group)
            {
               info.title = TeXParserUtils.removeGroup((TeXObjectList)info.title);
            }
         }

         if (params.size() > 3)
         {
            TeXObject obj = params.get(3);

            if (obj instanceof Group)
            {
               obj = TeXParserUtils.removeGroup((TeXObjectList)obj);
            }

            info.target = obj.toString(parser);
         }

         // Final argument is reserved for future LaTeX kernel use so
         // will currently always be empty.
      }

      return info;
   }

   public String getLabel()
   {
      return label;
   }

   public String getTarget()
   {
      return target;
   }

   public TeXObject getTitle()
   {
      return title;
   }

   public TeXObject getReference()
   {
      return reference;
   }

   public TeXObject getPage()
   {
      return page;
   }

   public void setDivisionInfo(DivisionInfo divData)
   {
      divisionData = divData;
   }

   public DivisionInfo getDivisionInfo()
   {
      return divisionData;
   }

   protected String label, target;
   protected TeXObject title, reference, page;
   protected DivisionInfo divisionData;
}
