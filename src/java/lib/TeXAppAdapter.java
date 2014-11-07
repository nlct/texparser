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
package com.dickimawbooks.texparserlib;

import java.io.IOException;
import java.io.File;

import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

public class TeXAppAdapter implements TeXApp
{
   public String kpsewhich(String arg)
     throws IOException,InterruptedException
   {
      return null;
   }

   public void epstopdf(File epsFile, File pdfFile)
     throws IOException,InterruptedException
   {
   }

   public void wmftoeps(File wmfFile, File epsFile)
     throws IOException,InterruptedException
   {
   }

   public void substituting(int lineNum, String original, String replacement)
   {
   }

   public void message(int messageType, String arg)
   {
   }

   public void error(Exception e)
   {
      if (e instanceof LaTeXSyntaxException)
      {
         System.err.println(getErrorMessage((LaTeXSyntaxException)e));
      }
      else if (e instanceof TeXSyntaxException)
      {
         System.err.println(getErrorMessage((TeXSyntaxException)e));
      }
      else
      {
         e.printStackTrace();
      }
   }

   public static String getErrorMessage(LaTeXSyntaxException e)
   {
      String message;

      switch (e.getErrorCode())
      {
         case LaTeXSyntaxException.ERROR_MULTI_BEGIN_DOC:
            message = "Only one \\begin{document} permitted";
         break;
         case LaTeXSyntaxException.ERROR_NO_BEGIN_DOC:
            message = "No \\begin{document} found";
         break;
         case LaTeXSyntaxException.ERROR_MULTI_CLS:
            message = "Only one \\documentclass permitted";
         break;
         case LaTeXSyntaxException.ERROR_MISSING_KEY:
            message = "Missing '"+ e.getParam()+"' key";
         break;
         case LaTeXSyntaxException.ERROR_EXTRA_END:
            message = "Extra \\end{" + e.getParam()+"} found";
         break;
         default:
            message = e.getMessage();
      }

      int lineNum = e.getLineNumber();

      return lineNum == -1 ? message : "Line "+lineNum+" : "+message;
   }

   public static String getErrorMessage(TeXSyntaxException e)
   {
      String message;

      switch (e.getErrorCode())
      {
         case TeXSyntaxException.ERROR_BAD_PARAM:
           message = "Parameter digit 1 to 9 expected. Found '"
            + e.getParam()+"'";
         break;
         case TeXSyntaxException.ERROR_NO_EG:
           message = "Missing end group";
         break;
         case TeXSyntaxException.ERROR_PAR_BEFORE_EG:
           message = "Paragraph break found before end group";
         break;
         case TeXSyntaxException.ERROR_UNEXPECTED_EG:
           message = "Unexpected end group found";
         break;
         case TeXSyntaxException.ERROR_MISSING_ENDMATH:
           message = "Missing end math";
         break;
         case TeXSyntaxException.ERROR_DOLLAR2_ENDED_WITH_DOLLAR:
           message = "$$ ended with $";
         break;
         case TeXSyntaxException.ERROR_NOT_FOUND:
           message = "Expected '"+ e.getParam()+"' but not found";
         break;
         case TeXSyntaxException.ERROR_MISSING_PARAM:
           message = "Argument expected but not found";
         break;
         case TeXSyntaxException.ERROR_NOT_MATH_MODE:
           message = e.getParam()+" only permitted in math mode";
         break;
         case TeXSyntaxException.ERROR_INVALID_ACCENT:
           message = e.getParam()+" is not a recognised accent";
         break;
         case TeXSyntaxException.ERROR_AMBIGUOUS_MIDCS:
           message = "Ambiguous use of "+ e.getParam();
         break;
         case TeXSyntaxException.ERROR_MISSING_CLOSING:
           message = "Missing closing "+ e.getParam();
         break;
         case TeXSyntaxException.ERROR_DIMEN_EXPECTED:
           message = "Dimension expected";
         break;
         case TeXSyntaxException.ERROR_MISSING_UNIT:
           message = "Missing unit";
         break;
         case TeXSyntaxException.ERROR_EXPECTED:
           message = "Expected "+ e.getParam();
         break;
         case TeXSyntaxException.ERROR_UNDEFINED:
           message = "Undefined command "+ e.getParam();
         break;
         case TeXSyntaxException.ERROR_CS_EXPECTED:
           message = "Control sequence expected (found " +e.getParam()+")";
         break;
         case TeXSyntaxException.ERROR_NUMBER_EXPECTED:
           message = "Number expected (found "+ e.getParam()+")";
         break;
         case TeXSyntaxException.ERROR_SYNTAX:
           message = "Invalid syntax for "+ e.getParam();
         break;
         case TeXSyntaxException.ERROR_EXTRA:
           message = "Extra "+ e.getParam();
         break;
         default:
           message = e.getMessage();
      }

      int lineNum = e.getLineNumber();

      return lineNum == -1 ? message : "Line "+lineNum+" : "+message;
   }

   public void copyFile(File orgFile, File newFile)
     throws IOException,InterruptedException
   {
   }
}
