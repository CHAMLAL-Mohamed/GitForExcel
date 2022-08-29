package com.twiza.domain;

/**
 * @author Mohamed.Chamlal, 13/01/2021
 */
public class RowKey {
    private int[] normalKeyIndexes;
    private int[] sortedKeyIndexes;

    RowKey(int[] normalKeyIndexes) {
        this(normalKeyIndexes, null);
    }

    RowKey(int[] normalKeyIndexes, int[] sortedKeyIndexes) {
        this.normalKeyIndexes = normalKeyIndexes;
        this.sortedKeyIndexes = sortedKeyIndexes;
    }

    public int[] getNormalKeyIndexes() {
        return normalKeyIndexes;
    }

    public void setNormalKeyIndexes(int[] normalKeyIndexes) {
        this.normalKeyIndexes = normalKeyIndexes;
    }

    public int[] getSortedKeyIndexes() {
        return sortedKeyIndexes;
    }

    public void setSortedKeyIndexes(int[] sortedKeyIndexes) {
        this.sortedKeyIndexes = sortedKeyIndexes;
    }


}
