package com.example.qrcodegenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ImageView imageView;
    private final String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    private Button btnSave;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.qrCode);
        btnSave = findViewById(R.id.btnSave);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;


        generateQR(smallerDimension);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveImageView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void generateQR(int smallerDim)
    {
        QRGEncoder qrgEncoder = new QRGEncoder("input", null, QRGContents.Type.TEXT, smallerDim);
        //qrgEncoder.setColorBlack(Color.RED);
        //qrgEncoder.setColorWhite(Color.BLUE);

        bitmap = qrgEncoder.getBitmap();
        imageView.setImageBitmap(bitmap);
    }

    private void saveImageView() throws IOException {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = draw.getBitmap();

            FileOutputStream outStream = null;
            File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if(sdCard.exists()){
                Log.wtf(TAG ,"Picture esiste");
            }
            File dir = new File(sdCard.getAbsolutePath() + "/YourFolderName");
            if(!dir.exists()) {
                Log.wtf(TAG, "Creo my folder");
                dir.mkdir();
            }
            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }


    }

    private void saveQRCode(Bitmap bitmap)
    {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try {
                File folder = new File(savePath);
                if(!folder.exists()){
                    folder.mkdirs();
                    Log.wtf(TAG, "Folder created");
                }
                boolean save = new QRGSaver().save(savePath, "input".trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
                Log.wtf(TAG, savePath);


                String result = save ? "Image Saved" : "Image Not Saved";
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                //edtValue.setText(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }
    }
}