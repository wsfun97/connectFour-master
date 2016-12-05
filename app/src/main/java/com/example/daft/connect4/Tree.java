package com.example.daft.connect4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dixon on 3/12/2016.
 */

public class Tree<E>{
    private List<Tree> children;
    private E value;

    public Tree() {
        children = new ArrayList<>();
    };

    public int size() {
        int size = 1; //count self first
        for(Tree child: children) {
            size += child.size();
        }
        return size; //leaf node return 1
    }

    public void add(Tree child) {
        children.add(child);
    }
    public void add(List<Tree> children) {
        this.children.addAll(children);
    }

    public void removeAll() {
        children = null;
    }
    public void remove(int i) {
        children.remove(i);
    }

    public List<Tree> children() {
        return children;
    }

    public Tree child(int i) {
        return children.get(i);
    }

    public List<E> toList() { //pre-order
        List<E> eList = new ArrayList<>();
        toList(eList, this);
        return eList;
    }
    public void toList(List<E> eList, Tree<E> root) {
        eList.add(root.value);
        for (Tree<E> child: children()) {
            toList(eList, child);
        }
    }

    public int depth;

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }
}
