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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class FlowFramTkUtilsSty extends LaTeXSty
{
   public FlowFramTkUtilsSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "flowfram", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      registerControlSequence(new AtNumberOfNumber("jdroutline", 3, 3));
      registerControlSequence(new AtFirstOfOne("jdrimagebox"));

      registerControlSequence(new AtNumberOfNumber("flowframtkimgtitlechar", 1, 2));

      registerControlSequence(new StoreDataCs("flowframtkSetTitle",
        "l_flowframtk_image_title_tl", "@title"));
      registerControlSequence(new StoreDataCs("flowframtkSetCreationDate",
        "l_flowframtk_image_creationdate_tl"));

      registerControlSequence(new FlowFramTkImageInfo());
      registerControlSequence(new FlowFramTkNewFrameBorder());

      registerControlSequence(new LaTeXGenericCommand(true,
          "flowframtkUseFrameBorderCsName", "m",
          TeXParserUtils.createStack(listener,
            listener.createString("@flf@border@"),
            listener.getParam(1)
          )
        )
      );

      registerControlSequence(new LaTeXGenericCommand(true,
        "flowframtkUseFrameBorderCs", "m",
          TeXParserUtils.createStack(listener,
             new TeXCsRef("@nameuse"),
             TeXParserUtils.createGroup(listener, 
               listener.createString("@flf@border@"),
               listener.getParam(1)
             )
          )
        )
      );

      registerControlSequence(new FlowFramTkSetOneHeadFoot(this, 
      "flowframtkSetDynamicOddHead", true, true));

      registerControlSequence(new FlowFramTkSetOneHeadFoot(this, 
      "flowframtkSetDynamicEvenHead", false, true));

      registerControlSequence(new FlowFramTkSetOneHeadFoot(this, 
      "flowframtkSetDynamicOddFoot", true, false));

      registerControlSequence(new FlowFramTkSetOneHeadFoot(this, 
      "flowframtkSetDynamicEvenFoot", false, false));

      registerControlSequence(new FlowFramTkSetTwoHeadFoot(this, 
        "flowframtkSetDynamicOddEvenHead", true));

      registerControlSequence(new FlowFramTkSetTwoHeadFoot(this, 
        "flowframtkSetDynamicOddEvenFoot", false));

      registerControlSequence(new AtGobble("flowframtkSetExtraPageStyles", 5));
      registerControlSequence(new AtGobble("flowframtkSetExtraOddEvenHeadings", 4));
      registerControlSequence(new AtGobble("flowframtkSetExtraOddHeadings", 3));
      registerControlSequence(new AtGobble("flowframtkDefFlowframTkPageStyle", 4));

      registerControlSequence(new LaTeXGenericCommand(true,
        "flowframtkSectionMarkPrefix", "m",
          TeXParserUtils.createStack(listener,
            listener.getParam(1),
            new TeXCsRef("quad")
          )
        )
      );

      registerControlSequence(new LaTeXGenericCommand(true,
        "flowframtkChapterMarkPrefix", "mm",
          TeXParserUtils.createStack(listener,
            listener.getParam(1),
            listener.getSpace(),
            listener.getParam(2),
            listener.getOther('.'),
            listener.getSpace()
          )
        )
      );

      registerControlSequence(new LaTeXGenericCommand(true,
        "flowframtkChapterSectionMarkPrefix", "m",
          TeXParserUtils.createStack(listener,
            listener.getParam(1),
            listener.getOther('.'),
            listener.getSpace()
          )
        )
      );

      registerControlSequence(new GenericCommand("flowframtkSetDefaultPageStyle"));
   }

   public void setOddHeadLabel(String label)
   {
      oddHead = label;
   }

   public String getOddHeadLabel()
   {
      return oddHead;
   }

   public void setEvenHeadLabel(String label)
   {
      evenHead = label;
   }

   public String getEvenHeadLabel()
   {
      return evenHead;
   }

   public void setOddFootLabel(String label)
   {
      oddFoot = label;
   }

   public String getOddFootLabel()
   {
      return oddFoot;
   }

   public void setEvenFootLabel(String label)
   {
      evenFoot = label;
   }

   public String getEvenFootLabel()
   {
      return evenFoot;
   }

   String oddHead, evenHead, oddFoot, evenFoot;
}

