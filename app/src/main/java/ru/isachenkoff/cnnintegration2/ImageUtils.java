package ru.isachenkoff.cnnintegration2;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageUtils {
    
    public static int[][][] toPixels(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        List<int[]> collect = Arrays.stream(pixels)
                .mapToObj(Integer::toHexString)
                .map(ImageUtils::argbHexToIntTriple)
                .collect(Collectors.toList());
        return splitBy(collect, bitmap.getWidth()).stream()
                .map(ints -> ints.toArray(new int[0][]))
                .toArray(int[][][]::new);
    }
    
    private static <T> List<List<T>> splitBy(List<T> list, int len) {
        List<List<T>> newList = new ArrayList<>();
        for (int pos = 0; pos < list.size(); pos += len) {
            newList.add(list.subList(pos, pos + len));
        }
        return newList;
    }
    
    private static int[] argbHexToIntTriple(String s) {
        return Stream.of(s.substring(2, 4), s.substring(4, 6), s.substring(6, 8))
                .mapToInt(value -> Integer.parseInt(value, 16))
                .toArray();
    }
    
}
