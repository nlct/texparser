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
package com.dickimawbooks.texparserlib.html;

import java.util.Vector;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.auxfile.DivisionData;

public class DivisionNode implements Comparable<DivisionNode>
{
   public DivisionNode(DivisionData data)
   {
     this(0, data, null);
   }

   public DivisionNode(int index, DivisionData data, DivisionNode parent)
   {
      if (data == null)
      {
         throw new NullPointerException();
      }

      this.index = index;
      this.data = data;
      this.parent = parent;

      this.level = parent == null ? 0 : parent.level+1;
   }

   @Override
   public int compareTo(DivisionNode other)
   {
      if (index < other.index)
      {
         return -1;
      }
      else if (index == other.index)
      {
         return 0;
      }
      else
      {
         return 1;
      }
   }

   @Override
   public boolean equals(Object other)
   {
      if (this == other) return true;

      if (!(other instanceof DivisionNode)) return false;

      DivisionNode node = (DivisionNode)other;

      if (index != node.index) return false;

      if ((parent == null && node.parent != null)
       || (parent != null && node.parent == null)
       || level != node.level)
      {
         return false;
      }

      return data.equals(node.data);
   }

   public DivisionNode getParent()
   {
      return parent;
   }

   public int getLevel()
   {
      return level;
   }

   public void addChild(int index, DivisionData childData)
   {
      if (children == null)
      {
         children = new Vector<DivisionNode>();
      }

      children.add(new DivisionNode(index, childData, this));
   }

   public int getChildCount()
   {
      return children == null ? 0 : children.size();
   }

   public Iterator<DivisionNode> getChildIterator()
   {
      return children == null ? null : children.iterator();
   }

   public DivisionNode getFirstChild()
   {
      return children == null || children.isEmpty() ? null : children.firstElement();
   }

   public DivisionNode getLastChild()
   {
      return children == null || children.isEmpty() ? null : children.lastElement();
   }

   public DivisionNode getNextSibling()
   {
      if (parent == null || parent.getChildCount() == 0) return null;

      for (int i = 0; i < parent.children.size(); i++)
      {
         DivisionNode node = parent.children.get(i);

         if (node == this)
         {
            i++;

            if (i == parent.children.size()) return null;

            return parent.children.get(i);
         }
      }

      return null;
   }

   public DivisionNode getPreviousSibling()
   {
      if (parent == null || parent.getChildCount() == 0) return null;

      for (int i = parent.children.size() - 1; i >=0 ; i--)
      {
         DivisionNode node = parent.children.get(i);

         if (node == this)
         {
            i--;

            if (i < 0) return null;

            return parent.children.get(i);
         }
      }

      return null;
   }

   public boolean isAncestor(DivisionNode other)
   {
      if (parent == null) return false;

      if (parent == other) return true;

      return parent.isAncestor(other);
   }

   public String getUnit()
   {
      return data.getUnit();
   }

   public DivisionNode getAncestorAtUnit(String unit)
   {
      if (parent == null) return null;

      if (parent.getUnit().equals(unit)) return parent;

      return parent.getAncestorAtUnit(unit);
   }

   public void setRef(String ref)
   {
      this.ref = ref;
   }

   public String getRef()
   {
      return ref;
   }

   protected final int index;
   protected final int level;
   protected final DivisionData data;
   protected final DivisionNode parent;

   protected Vector<DivisionNode> children;

   protected String ref;
}
