package com.example.treeviewexample;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewHolder;

/**
 * 树形控件自定义布局类
 */
public class Tree_ChildViewHolder extends TreeViewHolder {
    private TextView childView;
    private ImageView checkboxImageView;
    private ImageView arrowImageView;
    private LinearLayout rootView;
    private SelectListener selectListener;
    /**
     * 标记当前选中的节点
     */
    private static TreeNode mCurrentSelectedTreeNode;

    public Tree_ChildViewHolder(@NonNull View itemView) {
        super(itemView);
        childView = itemView.findViewById(R.id.tree_node);
        checkboxImageView = itemView.findViewById(R.id.tree_checkbox);
        arrowImageView = itemView.findViewById(R.id.tree_arrow);
        rootView = itemView.findViewById(R.id.tree_root_view);
    }

    public void setSelectListener(SelectListener selectListener) {
        this.selectListener = selectListener;
    }

    @Override
    public void bindTreeNode(TreeNode node) {
        super.bindTreeNode(node);
        if (node.getValue() instanceof Tree_StructureModel) {
            Tree_StructureModel structureModel = (Tree_StructureModel) node.getValue();
            // 选中的节点，背景高亮
            if (null != mCurrentSelectedTreeNode && mCurrentSelectedTreeNode == node) {
                rootView.setBackgroundResource(R.drawable.selected_bg);
            } else {
                rootView.setBackgroundResource(R.drawable.no_selected_bg);
            }
            // 根据节点的状态设置是全选，部分选，还是未选择
            if (structureModel.isParent()) {
                if (node.isExpanded()) {
                    arrowImageView.setImageResource(R.drawable.cad_3d_tree_arrows_open);
                } else {
                    arrowImageView.setImageResource(R.drawable.cad_3d_tree_arrows_close);
                }
                arrowImageView.setVisibility(View.VISIBLE);
                if (structureModel.getType() == 1) {
                    checkboxImageView.setImageResource(R.drawable.cad_3d_tree_checkbox_checked_all);
                } else if (structureModel.getType() == 2) {
                    checkboxImageView.setImageResource(R.drawable.cad_3d_tree_checkbox_checked_part);
                } else {
                    checkboxImageView.setImageResource(R.drawable.cad_3d_tree_checkbox_checked_null);
                }
            } else {
                arrowImageView.setVisibility(View.INVISIBLE);
                if (structureModel.getType() == 1) {
                    checkboxImageView.setImageResource(R.drawable.cad_3d_tree_checkbox_checked_all);
                } else {
                    checkboxImageView.setImageResource(R.drawable.cad_3d_tree_checkbox_checked_null);
                }
            }
            childView.setText(structureModel.getName());
            checkboxImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != selectListener) {
                        if (structureModel.isParent()) {
                            if (structureModel.getType() == 0 || structureModel.getType() == 2) {
                                selectListener.onSelected(node, true);
                            } else {
                                selectListener.onSelected(node, false);
                            }
                        } else {
                            selectListener.onSelected(node, false);
                        }
                    }
                }
            });
            if (arrowImageView.getVisibility() == View.VISIBLE) {
                arrowImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != selectListener) {
                            selectListener.onExpand(node);
                        }
                    }
                });
            } else {
                arrowImageView.setOnClickListener(null);
            }
        }

    }

    /**
     * 设置当前选中的节点
     * @param currentSelectedTreeNode
     */
    public static void setCurrentSelectedTreeNode(TreeNode currentSelectedTreeNode) {
        mCurrentSelectedTreeNode = currentSelectedTreeNode;
    }

    interface SelectListener {
        /**
         * @param treeNode
         * @param isSelectedAll 标记当前节点下的子节点是否全部选中
         */
        void onSelected(TreeNode treeNode, boolean isSelectedAll);

        /**
         * 展开或折叠该节点下的子节点
         *
         * @param treeNode
         */
        void onExpand(TreeNode treeNode);
    }
}
