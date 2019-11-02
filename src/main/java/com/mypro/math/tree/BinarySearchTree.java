package com.mypro.math.tree;

/**
 * @author fgzhong
 * @description: 二分搜索树 (O(logn))
 * @since 2019/6/21
 */
public class BinarySearchTree {

    /*
      1、二叉树
      2、节点 > 左子节点；节点 < 左子节点；
      3、前中后序遍历
         前序遍历：自身 -> 左 -> 右
         中序遍历：左 -> 自身 -> 右
         后续遍历：左 -> 右 -> 自身
      4、层序遍历
         深度遍历
         广度遍历
    */
    private static class Tree {
        Node root;
        int count;

        public void insert(int key, int value) {
            root = insert(root, key, value);
        }

        public boolean contain(int key) {
            return contain(root, key);
        }

        public Integer search(int key) {
            return search(root, key);
        }

        /* 前遍历 */
        public void perOrder(Node node) {
            System.out.println(node);
            perOrder(node.left);
            perOrder(node.right);
        }
        /* 中遍历 */
        public void inOrder(Node node) {
            inOrder(node.left);
            System.out.println(node);
            inOrder(node.right);
        }
        /* 后遍历 */
        public void postOrder(Node node) {
            postOrder(node.left);
            postOrder(node.right);
            System.out.println(node);
        }

        /* 层序遍历 */

        private Node insert(Node node, int key, int value) {
            if (node == null) {
                count++;
                return new Node(key, value);
            }
            if (key == node.key) {
                node.value = value;
            } else if (key < node.key) {
                node.left = insert(node.left, key, value);
            } else {
                node.right = insert(node.right, key, value);
            }
            return node;
        }

        private boolean contain(Node node, int key) {
            if (node == null) {
                return false;
            }
            if (key == node.key) {
                return true;
            } else if (key < node.key) {
                return contain(node.left, key);
            } else {
                return contain(node.right, key);
            }
        }

        private Integer search(Node node, int key) {
            if (node == null) {
                return null;
            }
            if (key == node.key) {
                return node.value;
            } else if (key < node.key) {
                return search(node.left, key);
            } else {
                return search(node.right, key);
            }
        }
    }

    private static class Node {
        int key;
        int value;
        Node left;
        Node right;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}
