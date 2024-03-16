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
 * Cite information obtained from <code>\bibcite</code>.
 * Note things can go awry if the writes to the aux file end up out of
 * order. The first <code>\bibcite</code> may end up before the
 * <code>\@writefile</code> for the bibliography header. This is
 * because the default definition of <code>\bibitem</code> uses
 * an immediate write whereas the <code>\@writefile</code> and label
 * use a delayed write. (The immediate write is needed as bibliographies
 * are typically at the end of a document and a delayed write on the final
 * page may be lost.)
 *
 * This may mean that the DivisionInfo is incorrect for the first reference.
 * A workaround is to adjust the division for the first cite if all
 * the other cites belong to another division, but this won't work for
 * a document with multiple bibliographies.
 */

public class CiteInfo implements CrossRefInfo
{
   protected CiteInfo(String label)
   {
      this.label = label;
   }

   public static CiteInfo createCite(AuxData auxData, TeXParser parser)
   {
      String label = auxData.getArg(0).toString(parser);

      CiteInfo info = new CiteInfo(label);

      info.reference = auxData.getArg(1);

      /* If reference consists of a series of groups then it's likely
         that a package such as natbib has been used, and this is 
         a set of parameters.
       */

      if (parser.isStack(info.reference) && !info.reference.isEmpty())
      {
         TeXObjectList list = (TeXObjectList)info.reference;

         boolean hasParams = true;

         for (TeXObject obj : list)
         {
            if (!(obj instanceof Group))
            {
               hasParams = false;
               break;
            }
         }

         if (hasParams)
         {
            info.params = new TeXObjectList[list.size()];

            for (int i = 0; i < info.params.length; i++)
            {
               info.params[i] = ((Group)list.get(i)).toList();
            }
         }
      }

      return info;
   }

   @Override
   public String getLabel()
   {
      return label;
   }

   @Override
   public String getTarget()
   {
      return label;
   }

   @Override
   public TeXObject getReference()
   {
      return reference;
   }

   @Override
   public void setDivisionInfo(DivisionInfo divInfo)
   {
      divisionInfo = divInfo;
   }

   @Override
   public DivisionInfo getDivisionInfo()
   {
      return divisionInfo;
   }

   public boolean hasParameters()
   {
      return params != null;
   }

   public int getParameterCount()
   {
      return params == null ? 0 : params.length;
   }

   public TeXObjectList getParameter(int idx)
   {
      return params[idx];
   }

   protected String label;
   protected TeXObject reference;
   protected DivisionInfo divisionInfo;
   protected TeXObjectList[] params;
}
