package ru.isachenkoff.cnnintegration2;

import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Classifier {
    
    private final Interpreter interpreter;
    private final int INPUT_WIDTH;
    private final int INPUT_HEIGHT;
    private final int CLASSES_COUNT;
    private final Map<Integer, String> classesMap;
    
    Classifier(ByteBuffer modelBuffer, int input_width, int input_height, int classes_count, Map<Integer, String> classesMap) {
        this.INPUT_WIDTH = input_width;
        this.INPUT_HEIGHT = input_height;
        this.CLASSES_COUNT = classes_count;
        this.interpreter = new Interpreter(modelBuffer);
        this.classesMap = classesMap;
    }
    
    private float[][][][] getDataForInput(Bitmap currentImage) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(currentImage, INPUT_WIDTH, INPUT_HEIGHT, false);
        int[][][] ints = ImageUtils.toPixels(resizedBitmap);
        float[][][][] normalizedData = new float[1][INPUT_WIDTH][INPUT_HEIGHT][3];
        for (int i = 0; i < ints.length; i++) {
            for (int j = 0; j < ints[i].length; j++) {
                for (int k = 0; k < ints[i][j].length; k++) {
                    normalizedData[0][i][j][k] = ints[i][j][k] / 255.0f;
                }
            }
        }
        return normalizedData;
    }
    
    public String classify(Bitmap bitmap) {
        float[][] output = new float[1][CLASSES_COUNT];
        interpreter.run(getDataForInput(bitmap), output);
        List<Float> outputValues = new ArrayList<>();
        for (float v : output[0]) {
            outputValues.add(v);
        }
        Float maxValue = Collections.max(outputValues);
        int index = outputValues.indexOf(maxValue);
        return classesMap.get(index);
    }
}
