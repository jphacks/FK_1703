package com.example.h_sato.recordwav;

/**
 * Created by h_sato on 2017/10/27.
 */

public class Levenstein_distance {
    double LevensteinDistance(String array_left, String array_right) {
        int array_[][] = new int[array_left.length() + 1][array_right.length() + 1];
        int x = 0;
        String[] left = array_left.split("");
        String[] right = array_right.split("");
        int left_point = 0;
        int right_point = 0;
        int left_right = 0;
        int minimum = 0;

        for (int i = 0; i < array_left.length() + 1; i++) {
            array_[i][0] = i;
        }

        for (int j = 0; j < array_right.length() + 1; j++) {
            array_[0][j] = j;
        }

        for (int i = 1; i < array_left.length() + 1; i++) {
            for (int j = 1; j < array_right.length() + 1; j++) {

                left_point = array_[i - 1][j] + 1;
                right_point = array_[i][j - 1] + 1;

                x = (left[i - 1].equals(right[j - 1])) ? 0 : 1;

                left_right = array_[i - 1][j - 1] + x;

                int min1 = Math.min(left_point, right_point);
                minimum = Math.min(min1, left_right);
                array_[i][j] = minimum;
            }
        }
        double max_string_num = Math.max(array_left.length(), array_right.length());
        double normalized_LD = array_[array_left.length()][array_right.length()] / max_string_num;
        return normalized_LD;
    }
}
