/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007, 2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.dnd;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.TreeSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;

/**
 * A <code>DragSource</code> implementation for Trees.
 */
public class TreeDragSource extends DragSource {

  protected Tree tree;
  protected TreeSource treeSource = TreeSource.BOTH;
  protected TreeBinder binder;

  /**
   * Creates a new drag source instance. The default code will set the selected
   * TreeItems as the data being dragged.
   * 
   * @param tree the source tree
   */
  public TreeDragSource(Tree tree) {
    super(tree);
    this.tree = tree;
    setStatusText("{0} items selected");
  }

  /**
   * Creates a new drag source instance. The default code will set the selected
   * model instances as the data being dragged.
   * 
   * @param binder the tree binder
   */
  public TreeDragSource(TreeBinder binder) {
    this(binder.getTree());
    this.binder = binder;
  }

  /**
   * Returns the type if items that can be dragged.
   * 
   * @return the tree source type
   */
  public TreeSource getTreeSource() {
    return treeSource;
  }

  /**
   * Sets witch tree items can be dragged (defaults to BOTH).
   * 
   * @param treeSource the tree source type
   */
  public void setTreeSource(TreeSource treeSource) {
    this.treeSource = treeSource;
  }

  @Override
  protected void onDragDrop(DNDEvent event) {
    if (binder != null) {
      List<ModelData> sel = (List) event.data;
      for (ModelData m : sel) {
        ModelData p = binder.getTreeStore().getParent(m);
        if (p != null) {
          binder.getTreeStore().remove(p, m);
        }
      }
    } else {
      List<TreeItem> sel = (List) event.data;
      for (TreeItem item : sel) {
        TreeItem p = item.getParentItem();
        if (p != null) {
          p.remove(item);
        }
      }
    }
  }

  @Override
  protected void onDragStart(DNDEvent e) {
    TreeItem item = tree.findItem(e.getTarget());
    if (item == null) {
      e.doit = false;
      return;
    }

    boolean leaf = treeSource == TreeSource.LEAF || treeSource == TreeSource.BOTH;
    boolean node = treeSource == TreeSource.NODE || treeSource == TreeSource.BOTH;

    List<TreeItem> sel = tree.getSelectionModel().getSelectedItems();
    if (sel.size() > 0) {
      boolean ok = true;
      for (TreeItem ti : sel) {
        if ((leaf && ti.isLeaf()) || (node && !ti.isLeaf())) {
          continue;
        }
        ok = false;
        break;
      }

      if (ok) {
        if (sel.get(0).getModel() != null) {
          List models = new ArrayList();
          for (TreeItem ti : sel) {
            models.add(ti.getModel());
          }
          e.data = models;
        } else {
          e.data = sel;
        }
        e.doit = true;
        e.status.update(Format.substitute(getStatusText(), sel.size()));

      } else {
        e.doit = false;
      }

    } else {
      e.doit = false;
    }
  }

}
