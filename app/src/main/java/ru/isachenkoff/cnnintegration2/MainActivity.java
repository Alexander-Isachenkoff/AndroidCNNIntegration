package ru.isachenkoff.cnnintegration2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    
    private final ClassifierBuilder classifierBuilder = new ClassifierBuilder(this);
    private ImageView imageView;
    private Bitmap currentImage;
    private TextView textView;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        pickImage(result.getData());
                    }
                }
            });
    private Classifier classifier;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classifier = classifierBuilder.create();
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
    }
    
    public void onLoad(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        activityResultLauncher.launch(photoPickerIntent);
    }
    
    private void pickImage(Intent imageReturnedIntent) {
        try {
            Uri imageUri = imageReturnedIntent.getData();
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            currentImage = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(currentImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void onClassify(View view) {
        if (currentImage == null) {
            return;
        }
        if (classifier == null) {
            throw new IllegalStateException("classifier has not been initialized");
        }
        String className = classifier.classify(currentImage);
        textView.setText("Я думаю, что это " + className);
        // TODO: 14.06.2022 Нужно, чтобы название класса выводилось в TextView на русском языке.
        // Для этого сделать класс для конвертации названия класса из файла classes.txt в статический объект,
        // у которого могут быть еще какие-то атрибуты, например описание и эталонное изображение для сравнения
    }
    
}