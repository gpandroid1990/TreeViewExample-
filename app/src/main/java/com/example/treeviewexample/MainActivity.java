package com.example.treeviewexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeNodeManager;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolder;
import com.amrdeveloper.treeview.TreeViewHolderFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mTreeRecyclerView;
    private Tree_CustomTreeViewAdapter mTreeViewAdapter;
    private List<TreeNode> mRoots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        TreeNodeManager treeNodeManager = new TreeNodeManager();
        this.mTreeRecyclerView = this.findViewById(R.id.tree_recycler_view);
        TreeViewHolderFactory factory = new TreeViewHolderFactory() {
            @Override
            public TreeViewHolder getTreeViewHolder(View view, int layout) {
                Tree_ChildViewHolder treeChildViewHolder = new Tree_ChildViewHolder(view);
                treeChildViewHolder.setSelectListener(new Tree_ChildViewHolder.SelectListener() {
                    @Override
                    public void onSelected(TreeNode treeNode, boolean isSelectedAll) {
                        TreeNode parent = treeNode.getParent();
                        if (treeNode.getValue() instanceof Tree_StructureModel) {
                            Tree_StructureModel structureModel = (Tree_StructureModel) treeNode.getValue();
                            if (structureModel.isParent()) {
                                // 如果选中的是父节点，则先把子节点都置为选中,然后更新父节点
                                updateChildren(treeNode, isSelectedAll);
                                // 更新父节点
                                if (null != parent) {
                                    updateParent(treeNode);
                                }
                            } else {
                                // 如果不是父节点，则更新子节点状态
                                if (structureModel.getType() == 0) {
                                    structureModel.setType(1);
                                } else {
                                    structureModel.setType(0);
                                }
                                treeNode.setValue(structureModel);
                                // 更新父节点
                                updateParent(treeNode);
                            }
                        }
                        mTreeViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onExpand(TreeNode currentNode) {
                        // 这里处理当前节点下的子节点展开与折叠
                        if (!currentNode.getChildren().isEmpty()) {
                            boolean isNodeExpanded = currentNode.isExpanded();
                            if (isNodeExpanded) treeNodeManager.collapseNode(currentNode);
                            else treeNodeManager.expandNode(currentNode);
                            currentNode.setExpanded(!isNodeExpanded);
                        }
                        mTreeViewAdapter.notifyDataSetChanged();
                    }
                });
                return treeChildViewHolder;
            }
        };
        mTreeViewAdapter = new Tree_CustomTreeViewAdapter(factory, treeNodeManager);
        this.mTreeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.mTreeRecyclerView.setAdapter(mTreeViewAdapter);
        mTreeViewAdapter.setTreeNodeClickListener(new TreeViewAdapter.OnTreeNodeClickListener() {
            @Override
            public void onTreeNodeClick(TreeNode treeNode, View view) {
                // 设置当前选中的节点
                Tree_ChildViewHolder.setCurrentSelectedTreeNode(treeNode);
                mTreeViewAdapter.notifyDataSetChanged();
                if (treeNode.getValue() instanceof Tree_StructureModel) {
                    Tree_StructureModel tree_structureModel = (Tree_StructureModel) treeNode.getValue();
                    String name = tree_structureModel.getName();
                    // 这里可以传递数据给底层
                }
            }
        });
        mTreeViewAdapter.setTreeNodeLongClickListener(new TreeViewAdapter.OnTreeNodeLongClickListener() {
            @Override
            public boolean onTreeNodeLongClick(TreeNode treeNode, View view) {
//                Toast.makeText(MainActivity.this, "" + treeNode.getValue().toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        initData();
    }

    private void initData() {
        Tree_NodeModel[] list = getNodeData1();
        // 解析节点数据，生成数据模型
        for (Tree_NodeModel nodeModel : list) {
            boolean isParent = null != nodeModel.getChildren() && nodeModel.getChildren().length > 0;
            Tree_StructureModel structureModel = new Tree_StructureModel(nodeModel.getName(), 0, isParent);
            TreeNode treeNode = new TreeNode(structureModel, R.layout.list_item_child);
            mRoots.add(treeNode);
            getChildren(treeNode, nodeModel.getChildren());
        }
        mTreeViewAdapter.updateTreeNodes(mRoots);
    }

    /**
     * 遍历子节点数据
     *
     * @param node
     * @param nodeModels
     */
    private void getChildren(TreeNode node, Tree_NodeModel[] nodeModels) {
        if (null == nodeModels || nodeModels.length == 0) {
            return;
        }
        for (Tree_NodeModel nodeModel : nodeModels) {
            boolean isParent = null != nodeModel.getChildren() && nodeModel.getChildren().length > 0;
            Tree_StructureModel structureModel = new Tree_StructureModel(nodeModel.getName(), 0, isParent);
            TreeNode treeNode = new TreeNode(structureModel, R.layout.list_item_child);
            node.addChild(treeNode);
            getChildren(treeNode, nodeModel.getChildren());
        }
    }

    /**
     * 更新父节点选中状态
     *
     * @param child
     */
    private void updateParent(TreeNode child) {
        TreeNode parentNode = child.getParent();
        if (null != parentNode) {
            if (parentNode.getValue() instanceof Tree_StructureModel) {
                Tree_StructureModel structureModel = (Tree_StructureModel) parentNode.getValue();
                if (isAllSelected(parentNode)) {
                    structureModel.setType(1);
                } else {
                    if (isOneSelected(parentNode)) {
                        structureModel.setType(2);
                    } else {
                        structureModel.setType(0);
                    }
                }
                parentNode.setValue(structureModel);
                updateParent(parentNode);
            }
        }
    }

    /**
     * 判断当前节点下的子节点是否都被选中，如果没有子节点，则判断当前节点是否被选中
     *
     * @param treeNode
     * @return
     */
    private boolean isAllSelected(TreeNode treeNode) {
        if (treeNode.getValue() instanceof Tree_StructureModel) {
            Tree_StructureModel structureModel = (Tree_StructureModel) treeNode.getValue();
            if (structureModel.isParent()) {
                boolean isAllSelected = true;
                for (TreeNode child : treeNode.getChildren()) {
                    Tree_StructureModel structure = (Tree_StructureModel) child.getValue();
                    if (structure.getType() != 1) {
                        isAllSelected = false;
                        break;
                    }
                }
                return isAllSelected;
            }
            return structureModel.getType() == 1;
        }
        return false;
    }

    /**
     * 判断当前节点下的子节点是否有一个被选中
     *
     * @param treeNode
     * @return
     */
    private boolean isOneSelected(TreeNode treeNode) {
        if (treeNode.getValue() instanceof Tree_StructureModel) {
            Tree_StructureModel structureModel = (Tree_StructureModel) treeNode.getValue();
            if (structureModel.isParent()) {
                boolean isOneSelected = false;
                for (TreeNode child : treeNode.getChildren()) {
                    Tree_StructureModel structure = (Tree_StructureModel) child.getValue();
                    if (structure.getType() == 1 || structure.getType() == 2) {
                        isOneSelected = true;
                        break;
                    }
                }
                return isOneSelected;
            }
            return false;
        }
        return false;
    }

    /**
     * 更新子节点选中状态
     *
     * @param child
     */
    private void updateChildren(TreeNode child, boolean isSelectedAll) {
        if (child.getValue() instanceof Tree_StructureModel) {
            Tree_StructureModel structureModel = (Tree_StructureModel) child.getValue();
            if (isSelectedAll) {
                structureModel.setType(1);
            } else {
                structureModel.setType(0);
            }
            child.setValue(structureModel);
        }
        if (null == child.getChildren() || child.getChildren().size() == 0) {
            return;
        }
        for (TreeNode treeNode : child.getChildren()) {
            updateChildren(treeNode, isSelectedAll);
        }
    }

    /**
     * 测试节点数据
     * @return
     */
    private Tree_NodeModel[] getNodeData1() {
        Tree_NodeModel[] nodeModels = new Tree_NodeModel[1];

        Tree_NodeModel nodeModel1_0_0 = new Tree_NodeModel();
        nodeModel1_0_0.setName("cengji1_0_0");

        Tree_NodeModel nodeModel1_0_1 = new Tree_NodeModel();
        nodeModel1_0_1.setName("cengji1_0_1");

        Tree_NodeModel nodeModel1_0 = new Tree_NodeModel();
        nodeModel1_0.setName("cengji1_0");
        nodeModel1_0.setChildren(new Tree_NodeModel[]{nodeModel1_0_0, nodeModel1_0_1});


        Tree_NodeModel nodeModel1_1_0 = new Tree_NodeModel();
        nodeModel1_1_0.setName("cengji1_1_0");

        Tree_NodeModel nodeModel1_1_1 = new Tree_NodeModel();
        nodeModel1_1_1.setName("cengji1_1_1");


        Tree_NodeModel nodeModel1_1 = new Tree_NodeModel();
        nodeModel1_1.setName("cengji1_1");
        nodeModel1_1.setChildren(new Tree_NodeModel[]{nodeModel1_1_0, nodeModel1_1_1});


        Tree_NodeModel nodeModel = new Tree_NodeModel();
        nodeModel.setName("cengji1");
        nodeModel.setChildren(new Tree_NodeModel[]{nodeModel1_0, nodeModel1_1});

        nodeModels[0] = nodeModel;
        return nodeModels;
    }

}