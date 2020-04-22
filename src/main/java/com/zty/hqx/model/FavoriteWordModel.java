package com.zty.hqx.model;

import java.util.Objects;

/**
 * 喜好词
 * */
public class FavoriteWordModel {
    private String word;//词语
    private double index;//推荐指数

    public FavoriteWordModel() {
    }

    public FavoriteWordModel(String word, double index) {
        this.word = word;
        this.index = index;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public double getIndex() {
        return index;
    }

    public void setIndex(double index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteWordModel that = (FavoriteWordModel) o;
        return Objects.equals(word, that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }

    @Override
    public String toString() {
        return "FavoriteWordModel{" +
                "word='" + word + '\'' +
                ", index=" + index +
                '}';
    }
}
