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
import java.io.File;

import com.dickimawbooks.texparserlib.auxfile.DivisionInfo;

public class DivisionNode implements Comparable<DivisionNode>
{
   public DivisionNode(DivisionInfo data)
   {
     this(0, data, null);
   }

   public DivisionNode(int index, DivisionInfo data, DivisionNode parent)
   {
      if (data == null)
      {
         throw new NullPointerException();
      }

      data.setSpecial(this);

      this.index = index;
      this.data = data;
      this.parent = parent;

      if (parent == null)
      {
         this.level = 0;
      }
      else
      {
         this.level = parent.level+1;
         parent.addChild(this);
      }
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
      if (!(other instanceof DivisionNode)) return false;

      return compareTo((DivisionNode)other) == 0;
   }

   public DivisionNode getParent()
   {
      return parent;
   }

   public int getIndex()
   {
      return index;
   }

   public int getSiblingIndex()
   {
      return siblingIndex;
   }

   public int getLevel()
   {
      return level;
   }

   public void addChild(DivisionNode childNode)
   {
      if (childNode == null)
      {
         throw new NullPointerException();
      }

      if (children == null)
      {
         children = new Vector<DivisionNode>();
      }

      children.add(childNode);

      childNode.siblingIndex = children.size()-1;
   }

   public void addChild(int index, DivisionInfo childData)
   {
      if (childData == null)
      {
         throw new NullPointerException();
      }

      if (children == null)
      {
         children = new Vector<DivisionNode>();
      }

      DivisionNode childNode = new DivisionNode(index, childData, this);

      children.add(childNode);

      childNode.siblingIndex = children.size()-1;
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
      if (parent == null || siblingIndex == parent.getChildCount()-1) return null;

      return parent.children.get(siblingIndex+1);
   }

   public DivisionNode getPreviousSibling()
   {
      if (parent == null || siblingIndex == 0) return null;

      return parent.children.get(siblingIndex-1);
   }

   public boolean isAncestor(DivisionNode other)
   {
      if (parent == null) return false;

      if (parent == other) return true;

      return parent.isAncestor(other);
   }

   public DivisionNode getAncestorAtUnit(String unit)
   {
      if (parent == null) return null;

      if (parent.getUnit().equals(unit)) return parent;

      return parent.getAncestorAtUnit(unit);
   }

   public String getUnit()
   {
      return data.getUnit();
   }

   public DivisionInfo getData()
   {
      return data;
   }

   public void setRef(String ref)
   {
      this.ref = ref;
   }

   public String getRef()
   {
      return ref;
   }

   public void setFile(File file)
   {
      this.file = file;
   }

   public File getFile()
   {
      return (file == null && parent != null) ? parent.getFile() : file;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getTitle()
   {
      return title;
   }

   public void setPrefix(String prefix)
   {
      this.prefix = prefix;
   }

   public String getPrefix()
   {
      return prefix;
   }

   public String getId()
   {
      String label = data.getLabel();
 
      if (label == null)
      {
         String target = data.getTarget();

         if (target == null)
         {
            return "node"+index;
         }
         else
         {
            return target;
         }
      }

      return label;
   }

   public String toString()
   {
      return String.format("%s[label=%s,target=%s,prefix=%s,title=%s,ref=%s,index=%d,level=%d,siblingIndex=%d,childcount=%d]",
        getClass().getSimpleName(),
        data.getLabel(), data.getTarget(),
        prefix, title, ref,
        index, level, siblingIndex, getChildCount());
   }

   protected final int index;
   protected final int level;
   protected final DivisionInfo data;
   protected final DivisionNode parent;

   protected Vector<DivisionNode> children;
   protected int siblingIndex = 0;

   protected String ref, title, prefix;

   protected File file;
}
