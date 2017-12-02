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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.dickimawbooks.texparserapp.TeXParserApp;

/**
 * A toolbar that scrolls if there are too many buttons to fit
 * available space.
 */
public class ScrollToolBar extends JPanel
   implements ActionListener,ChangeListener
{
   public ScrollToolBar(TeXParserAppGuiResources resources, int orientation)
   {
      super();
      setLayout(new BorderLayout());

      this.resources = resources;

      backComponent = createNavButton("scrollback");

      super.add(backComponent, BorderLayout.WEST);

      toolPanel = new JPanel(null);

      toolPanel.setLayout(new BoxLayout(toolPanel,
         orientation == SwingConstants.HORIZONTAL ?
           BoxLayout.LINE_AXIS : BoxLayout.PAGE_AXIS));

      scrollPane = new JScrollPane(toolPanel,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

      scrollPane.setBorder(BorderFactory.createEmptyBorder());
      scrollPane.getViewport().addChangeListener(this);

      super.add(scrollPane, BorderLayout.CENTER);

      forwardComponent = createNavButton("scrollforward");
      super.add(forwardComponent, BorderLayout.EAST);
   }

   private JButton createNavButton(String action)
   {
      JButton button = resources.createActionButton("button",
        action, this, null,
        resources.getApplication().getLabel("button", action));

      Icon icon = button.getIcon();

      if (icon != null)
      {
         button.setText(null);
         button.setContentAreaFilled(false);
         button.setPreferredSize(new Dimension(icon.getIconWidth()+2,
           icon.getIconHeight()+2));
         button.setBorder(BorderFactory.createEmptyBorder());
      }

      return button;
   }

   public void stateChanged(ChangeEvent e)
   {
      Object source = e.getSource();

      if (source == scrollPane.getViewport())
      {
         JViewport viewport = (JViewport)source;

         Rectangle viewRect = viewport.getViewRect();

         Dimension dim = viewport.getView().getPreferredSize();

         if (dim.width > viewRect.getWidth())
         {
            backComponent.setVisible(viewRect.x > 0);
            forwardComponent.setVisible(viewRect.x+viewRect.width<dim.width);
         }
         else
         {
            backComponent.setVisible(false);
            forwardComponent.setVisible(false);
         }
      }
   }

   public void actionPerformed(ActionEvent event)
   {
      String action = event.getActionCommand();

      if (action == null) return;

      if (action.equals("scrollforward"))
      {
         JViewport viewport = scrollPane.getViewport();

         Rectangle viewRect = viewport.getViewRect();

         viewRect.x += viewRect.width/2;

         Dimension dim = viewport.getView().getPreferredSize();

         double x = viewRect.x+viewRect.width;

         if (x > dim.width)
         {
            viewRect.x = (int)Math.max(0, viewRect.x-x+dim.width);
         }

         toolPanel.scrollRectToVisible(viewRect);
      }
      else if (action.equals("scrollback"))
      {
         JViewport viewport = scrollPane.getViewport();

         Rectangle viewRect = viewport.getViewRect();

         viewRect.x -= viewRect.width/2;

         if (viewRect.x < 0)
         {
            viewRect.x = 0;
         }

         toolPanel.scrollRectToVisible(viewRect);
      }
   }

   public void addButton(JComponent comp)
   {
      toolPanel.add(comp);
   }

   public void addSeparator()
   {
      toolPanel.add(new JToolBar.Separator());
   }

   private JScrollPane scrollPane;
   private JComponent toolPanel;
   private JComponent backComponent, forwardComponent;

   private TeXParserAppGuiResources resources;
}
