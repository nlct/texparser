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

import java.net.URL;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.dickimawbooks.texparserapp.TeXParserApp;

/**
 * Provides a JMenuItem that optionally has an associated button in
 * the toolbar. The button is only created if there is an associated
 * image file.
 */

public class ItemButton extends JMenuItem
{
   public ItemButton(TeXParserAppGuiResources resources, String parentLabel, 
     String actionLabel, ActionListener listener, KeyStroke keyStroke, 
     ScrollToolBar toolBar)
   {
      super(resources.getApplication().getLabelRemoveArgs(parentLabel, actionLabel));
      TeXParserApp app = resources.getApplication();

      setMnemonic(app.getMnemonic(parentLabel, actionLabel));
      setActionCommand(actionLabel);

      button = null;

      if (toolBar != null)
      {
         URL imageURL = resources.getImageUrl(actionLabel);

         if (imageURL != null)
         {
            Icon icon = new ImageIcon(imageURL);
            button = new JButton(icon);
            button.setActionCommand(actionLabel);
            button.putClientProperty("hideActionText", Boolean.TRUE);
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.BOTTOM);
            button.setPreferredSize(
             new Dimension(icon.getIconWidth()+border,
             icon.getIconHeight()+border));
            button.setMaximumSize(button.getPreferredSize());
            button.setContentAreaFilled(false);

            Border border = button.getBorder();
            button.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createEmptyBorder(2,2,2,2), border));

            toolBar.addButton(button);
         } 
      }

      String tooltip = app.getToolTip(parentLabel, actionLabel);

      if (tooltip != null)
      {
         setToolTipText(tooltip);

         if (button != null)
         {
            button.setToolTipText(tooltip);
         }
      }
      else if (button != null)
      {
         button.setToolTipText(getText());
      }

      if (listener != null)
      {
         addActionListener(listener);

         if (button != null)
         {
            button.addActionListener(listener);
         }
      }

      if (keyStroke != null)
      {
         setAccelerator(keyStroke);
      }
   }

   public JButton getButton()
   {
      return button;
   }

   public void setEnabled(boolean enabled)
   {
      super.setEnabled(enabled);

      if (button != null)
      {
         button.setEnabled(enabled);
      }
   }

   private JButton button;

   private int border=10;
}
