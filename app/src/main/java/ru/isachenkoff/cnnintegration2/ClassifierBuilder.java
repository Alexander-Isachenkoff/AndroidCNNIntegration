package ru.isachenkoff.cnnintegration2;

import android.content.res.AssetFileDescriptor;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassifierBuilder {
    
    public static final String MODEL_TFLITE_FILE = "mushrooms-model.tflite";
    public static final String PARAMS_FILE = "params.cfg";
    public static final String CLASSES_FILE = "classes.txt";
    private final AppCompatActivity activity;
    
    ClassifierBuilder(AppCompatActivity activity) {
        this.activity = activity;
    }
    
    private static String tryGetParam(String name, Map<String, String> params) {
        String value = params.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Param " + name + " does not found");
        }
        return value;
    }
    
    public Classifier create() {
        ByteBuffer model = getModel();
        if (model == null) {
            throw new RuntimeException("Не удалось считать модель");
        }
        
        Map<String, String> params = getLines(PARAMS_FILE)
                .map(s -> s.split(": "))
                .collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]));
        int input_width = Integer.parseInt(tryGetParam("INPUT_WIDTH", params));
        int input_height = Integer.parseInt(tryGetParam("INPUT_HEIGHT", params));
        int classes_count = Integer.parseInt(tryGetParam("CLASSES_COUNT", params));
        
        Map<Integer, String> classesMap = getLines(CLASSES_FILE)
                .map(s -> s.split(": "))
                .collect(Collectors.toMap(strings -> Integer.parseInt(strings[0]), strings -> strings[1]));
        
        return new Classifier(model, input_width, input_height, classes_count, classesMap);
    }
    
    private ByteBuffer getModel() {
        AssetFileDescriptor fileDescriptor;
        try {
            fileDescriptor = activity.getAssets().openFd(MODEL_TFLITE_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        FileInputStream fis = new FileInputStream(fileDescriptor.getFileDescriptor());
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        try {
            return fis.getChannel().map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private Stream<String> getLines(String file) {
        InputStream inputStream;
        try {
            inputStream = activity.getAssets().open(file);
        } catch (IOException e) {
            throw new IllegalArgumentException("Не удалось прочитать файл " + file);
        }
        return new BufferedReader(new InputStreamReader(inputStream)).lines();
    }
    
}
