package com.example.treeviewexample;

/**
 * 树形结构数据模型
 */
public class Tree_NodeModel {
    /**
     * 节点名称
     */
    private String name;
    /**
     * 子节点数据
     */
    private Tree_NodeModel[] children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tree_NodeModel[] getChildren() {
        return children;
    }

    public void setChildren(Tree_NodeModel[] children) {
        this.children = children;
    }
}
