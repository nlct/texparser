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
import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;

import com.dickimawbooks.texparserapp.TeXParserApp;

/**
 * Application GUI resources.
 */
public class TeXParserAppGuiResources
{
    public static synchronized void error(Component parent, String message)
    {
       TeXParserApp.debug("Error: '"+message+"'");

       errorPanel.updateMessage(message);

       JOptionPane.showMessageDialog(parent, errorPanel,
          TeXParserApp.getLabelWithAlt("error.title", "Error"),
          JOptionPane.ERROR_MESSAGE);
    }

    public static synchronized void error(Component parent, Exception e)
    {
       String message = e.getMessage();

       if (message == null)
       {
          message = e.getClass().getName();
       }

       Throwable cause = e.getCause();

       if (cause != null)
       {
          message += "\n"+cause.getMessage();
       }

       error(parent, message, e);
    }

    public static void error(Component parent, String message, Exception e)
    {
       TeXParserApp.debug(e);

       errorPanel.updateMessage(message, e);

       JOptionPane.showMessageDialog(parent, errorPanel,
          TeXParserApp.getLabelWithAlt("error.title", "Error"),
         JOptionPane.ERROR_MESSAGE);
    }

    public static void warning(Component parent, String message)
    {
       TeXParserApp.debug("Warning: '"+message+"'");

       errorPanel.updateMessage(message);

       JOptionPane.showMessageDialog(parent,
          errorPanel,
          TeXParserApp.getLabelWithAlt("error.warning", "Warning"),
          JOptionPane.WARNING_MESSAGE);
    }

    public static JButton createOkayButton(ActionListener listener)
    {
       return createActionButton("button", "okay", 
          listener, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
    }

    public static JButton createCancelButton(ActionListener listener)
    {
       return createActionButton("button", "cancel", listener,
          KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
    }

    public static JButton createActionButton(String parent, String label, 
      ActionListener listener, KeyStroke keyStroke)
    {
       return createActionButton(parent, label, listener, keyStroke,
         TeXParserApp.getToolTip(parent, label));
    }

    public static JButton createActionButton(String parent, String label, 
      ActionListener listener, KeyStroke keyStroke,
      String tooltipText)
    {
       String buttonLabel = TeXParserApp.getLabel(parent, label);
       int mnemonic = TeXParserApp.getMnemonicInt(parent, label);
       String actionCommand = label;

       // Is there an associated image?

       URL imageURL = getImageUrl(label);

       JButton button;

       if (imageURL == null)
       {
          button = new JButton(buttonLabel);
       }
       else
       {
          button = new JButton(buttonLabel, new ImageIcon(imageURL));

          // Is there an associated rollover image?

          imageURL = getImageUrl(label+"_rollover");

          if (imageURL != null)
          {
             button.setRolloverIcon(new ImageIcon(imageURL));
          }

          // Is there an associated pressed image?

          imageURL = getImageUrl(label+"_pressed");

          if (imageURL != null)
          {
             button.setPressedIcon(new ImageIcon(imageURL));
          }

          // Is there an associated selected image?

          imageURL = getImageUrl(label+"_selected");

          if (imageURL != null)
          {
             button.setSelectedIcon(new ImageIcon(imageURL));
          }

          // Is there an associated disabled image?

          imageURL = getImageUrl(label+"_disabled");

          if (imageURL != null)
          {
             button.setDisabledIcon(new ImageIcon(imageURL));
          }

          // Is there an associated "disabled selected" image?

          imageURL = getImageUrl(label+"_disabled_selected");

          if (imageURL != null)
          {
             button.setDisabledSelectedIcon(new ImageIcon(imageURL));
          }
       }

       if (mnemonic != -1)
       {
          button.setMnemonic(mnemonic);
       }

       if (listener != null)
       {
          button.addActionListener(listener);

          if (actionCommand != null)
          {
             button.setActionCommand(actionCommand);

             if (keyStroke != null)
             {
                button.registerKeyboardAction(listener,
                  actionCommand, keyStroke,
                  JComponent.WHEN_IN_FOCUSED_WINDOW);
             }
          }
       }

       if (tooltipText != null)
       {
          button.setToolTipText(tooltipText);
       }

       return button;
    }

    public static JLabel createJLabel(String label)
    {
       return createJLabel(label, null);
    }

    public static JLabel createJLabel(String label, JComponent comp)
    {
       JLabel jLabel = new JLabel(TeXParserApp.getLabel(label));

       int mnemonic = TeXParserApp.getMnemonicInt(label);

       if (mnemonic != -1)
       {
          jLabel.setDisplayedMnemonic(mnemonic);
       }

       String tooltip = TeXParserApp.getToolTip(label);

       if (tooltip != null)
       {
          jLabel.setToolTipText(tooltip);
       }

       if (comp != null)
       {
         jLabel.setLabelFor(comp);
       }

       return jLabel;
   }

   public static JRadioButton createJRadioButton(String parentLabel,
      String label, ButtonGroup bg, ActionListener listener)
   {
      JRadioButton button = new JRadioButton(
        TeXParserApp.getLabel(parentLabel, label));

      button.setMnemonic(TeXParserApp.getMnemonic(parentLabel, label));

      String tooltip = TeXParserApp.getToolTip(parentLabel, label);

      if (tooltip != null)
      {
         button.setToolTipText(tooltip);
      }

      button.setActionCommand(label);

      if (listener != null)
      {
         button.addActionListener(listener);
      }

      bg.add(button);

      return button;
   }

    public static JCheckBox createJCheckBox(String parentLabel, String label,
       ActionListener listener)
    {
       JCheckBox checkBox = new JCheckBox(
          TeXParserApp.getLabel(parentLabel, label));
       checkBox.setMnemonic(TeXParserApp.getMnemonic(parentLabel, label));
       checkBox.setActionCommand(label);

       if (listener != null)
       {
          checkBox.addActionListener(listener);
       }

       return checkBox;
    }

   public static JTextArea createMessageArea()
   {
      return createMessageArea(8, 30);
   }

   public static JTextArea createMessageArea(String label)
   {
      return createMessageArea(8, 30, label);
   }

   public static JTextArea createMessageArea(int rows, int cols, String label)
   {
      JTextArea area = createMessageArea(rows, cols);

      area.setText(TeXParserApp.getLabel(label));

      return area;
   }

   public static JTextArea createMessageArea(int rows, int cols)
   {
      JTextArea area = new JTextArea(rows, cols);
      area.setWrapStyleWord(true);
      area.setLineWrap(true);
      area.setEditable(false);
      area.setBorder(null);
      area.setOpaque(false);

      return area;
   }

    public static JComponent createOkayCancelHelpPanel(
       ActionListener listener, TeXParserAppGUI gui, String helpId)
    {
      return createOkayCancelHelpPanel(null, listener, gui, helpId);
    }

    public static JComponent createOkayCancelHelpPanel(
       JRootPane rootPane, ActionListener listener, TeXParserAppGUI gui, String helpId)
    {
       JPanel buttonPanel = new JPanel();

       JButton okayButton = createOkayButton(listener);

       buttonPanel.add(okayButton);
       buttonPanel.add(createCancelButton(listener));
       buttonPanel.add(gui.createHelpButton(helpId));

       if (rootPane != null)
       {
          rootPane.setDefaultButton(okayButton);
       }

       return buttonPanel;
    }

    public static JComponent createOkayCancelPanel(
       ActionListener listener)
    {
       return createOkayCancelPanel(null, listener);
    }

    public static JComponent createOkayCancelPanel(
       JRootPane rootPane,
       ActionListener listener)
    {
       JPanel buttonPanel = new JPanel();

       JButton okayButton = createOkayButton(listener);

       buttonPanel.add(okayButton);
       buttonPanel.add(createCancelButton(listener));

       if (rootPane != null)
       {
          rootPane.setDefaultButton(okayButton);
       }

       return buttonPanel;
    }

    public static JMenu createJMenu(String label)
    {
       return createJMenu(null, label);
    }

    public static JMenu createJMenu(String parent, String label)
    {
       JMenu menu = new JMenu(TeXParserApp.getLabel(parent, label));
       menu.setMnemonic(TeXParserApp.getMnemonic(parent, label));

       String tooltip = TeXParserApp.getToolTip(parent, label);

       if (tooltip != null)
       {
          menu.setToolTipText(tooltip);
       }

       return menu;
    }

    public static JMenuItem createJMenuItem(String parent, String label)
    {
       return createJMenuItem(parent, label, null, null, null);
    }

    public static JMenuItem createJMenuItem(String parent, String label,
       ActionListener listener)
    {
       return createJMenuItem(parent, label, listener, null, null);
    }

    public static JMenuItem createJMenuItem(String parent, String label,
       ActionListener listener, ScrollToolBar toolBar)
    {
       return createJMenuItem(parent, label, listener, null, toolBar);
    }

    public static JMenuItem createJMenuItem(String parent, String label,
     ActionListener listener, KeyStroke keyStroke)
    {
       return createJMenuItem(parent, label, listener, keyStroke, null);
    }

    public static JMenuItem createJMenuItem(String parent, String label,
     ActionListener listener, KeyStroke keyStroke, ScrollToolBar toolBar)
    {
       return new ItemButton(parent, label, listener, keyStroke, toolBar);
    }

    // Get the image URL associated with action

    public static URL getImageUrl(String action)
    {
       if (imageMap == null)
       {
          // initialise

          imageMap = new Properties();

          InputStream in = null;
          BufferedReader reader = null;

          try
          {
             try
             {
                in = TeXParserApp.class.getResourceAsStream(
                   "/resources/imagemap.prop");

                if (in == null)
                {
                   throw new FileNotFoundException(
                     "Can't find /resources/imagemap.prop");
                }

                reader = new BufferedReader(
                   new InputStreamReader(in));

                imageMap.load(reader);
             }
             finally
             {
                if (reader != null)
                {
                   reader.close();
                }

                if (in != null)
                {
                   in.close();
                }
             }
          }
          catch (IOException e)
          {
             TeXParserApp.debug(e);
             return null;
          }
       }

       String location = imageMap.getProperty(action);

       if (location == null) return null;

       URL imageURL = TeXParserApp.class.getResource(location);

       if (imageURL == null)
       {
          TeXParserApp.debug("Can't find resource '"+location+"'");
       }

       return imageURL;
    }

    private static ErrorPanel errorPanel = new ErrorPanel();

    private static Properties imageMap = null;
}

