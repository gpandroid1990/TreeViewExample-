package com.example.treeviewexample;

/**
 * 该类主要标记当前节点是否是父节点，当前节点的选中状态
 */
public class Tree_StructureModel {
    /**
     * 名称
     */
    private String name;
    /**
     * 0 为未选中 1 为全部选中 2 为部分选中
     */
    private int type;
    /**
     * 是否是父节点
     */
    private boolean isParent;


    public Tree_StructureModel(String name, int type, boolean isParent) {
        this.name = name;
        this.type = type;
        this.isParent = isParent;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }
}
