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
package com.dickimawbooks.texparserapp.gui;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.awt.event.*;
import javax.swing.*;

import javax.help.*;

import com.dickimawbooks.texparserapp.*;

/**
 * Main GUI frame
 */
public class TeXParserAppGUI extends JFrame
  implements ActionListener,ErrorListener
{
   public TeXParserAppGUI(TeXParserApp application)
   {
      super(TeXParserApp.appName);
      app = application;
   }

   public void actionPerformed(ActionEvent evt)
   {
   }

   public synchronized void updateAbortItem()
   {
      if (abortItem != null)
      {
         abortItem.setEnabled(app.hasProcessesRunning());
      }
   }

   public void setInfo(String messages)
   {
   }

   public void warning(String message)
   {
      TeXParserAppGuiResources.warning(this, message);
   }

   public void error(String message)
   {
      TeXParserAppGuiResources.error(this, message);
   }

   public void error(Exception e)
   {
      TeXParserAppGuiResources.error(this, e);
   }

   private void initHelp()
     throws HelpSetException,FileNotFoundException
   {
      if (mainHelpBroker == null)
      {
         HelpSet mainHelpSet = null;

         TeXParserAppSettings settings = app.getSettings();

         String helpset = settings.getHelpSetLocation()
            + "-"+settings.getHelpSet()+"/"+settings.RESOURCE+".hs";

         URL hsURL = getClass().getResource(helpset);

         if (hsURL == null)
         {
            if (hsURL == null)
            {
               throw new FileNotFoundException(
                  TeXParserApp.getLabelWithValue("error.resource.not_found",
                    helpset));
            }
         }

         mainHelpSet = new HelpSet(null, hsURL);

         mainHelpBroker = mainHelpSet.createHelpBroker();

         csh = new CSH.DisplayHelpFromSource(mainHelpBroker);
      }
   }

   public void enableHelpOnButton(JComponent comp, String id)
   {
      if (mainHelpBroker != null)
      {
         try
         {
            mainHelpBroker.enableHelpOnButton(comp, id, 
               mainHelpBroker.getHelpSet());
         }
         catch (BadIDException e)
         {
            TeXParserAppGuiResources.error(null, e);
         }
      }
      else
      {
         TeXParserApp.debug("Can't enable help on button (id="+id
           +"): null help broker");
      }
   }

   public JButton createHelpButton(String id)
   {
      JButton button = TeXParserAppGuiResources.createActionButton(
         "button", "help", null, 
         KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

      enableHelpOnButton(button, id);

      return button;
   }

   private TeXParserApp app;

   private JButton abortItem;

   private HelpBroker mainHelpBroker;
   private CSH.DisplayHelpFromSource csh;


}
