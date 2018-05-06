package com.example.android.jaundice;

/**
 * Created by Elesdody on 25-Apr-18.
 */

public class information {
    private String Title;
    private String Part1;
    private String Part2;
    private int image;

    public String getTitle() {
        return Title;
    }

    public String getPart1() {
        return Part1;
    }
public String getPart2(){
        return Part2;
}
    public int getImage() {
        return image;
    }

    public information(String title, String part1,String part2, int image) {

        Title = title;
        Part1= part1;
        Part2= part2;
        this.image = image;
    }
}
