package com.example.imagecompressor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView originalImageView,compressImageView;
    TextView txtOriginal,txtCompress,txtQuality;
    EditText txtHeight,txtWidth;
    SeekBar seekBar;
    Button pickButton,compressButton;
    private static String filePath;
    File path=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myCompressor");
    public static final int RESULT_IMAGE=1;
    File originalImage,compressImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermission();
        originalImageView=findViewById(R.id.originalImageView);
        compressImageView=findViewById(R.id.compressImageView);
        txtOriginal=findViewById(R.id.originalTextView);
        txtCompress=findViewById(R.id.compressTextView);
        txtQuality=findViewById(R.id.qualityTextViewId);
        txtHeight=findViewById(R.id.heightEditTextId);
        txtWidth=findViewById(R.id.widthEditTextId);
        seekBar=findViewById(R.id.seekQuality);
        pickButton=findViewById(R.id.pickButton);
        compressButton=findViewById(R.id.compressButton);

        filePath=path.getAbsolutePath();
        if (!path.exists()){
            path.mkdirs();
        }


        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();

            }
        });



    }
        private void openGallery(){
            Intent gallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery,RESULT_IMAGE);

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            compressButton.setVisibility(View.VISIBLE);
            final Uri imageUri=data.getData();

            try {
                final InputStream imageStream=getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage= BitmapFactory.decodeStream(imageStream);
                originalImageView.setImageBitmap(selectedImage);
                originalImage=new File(imageUri.getPath().replace("raw/",""));
                txtOriginal.setText("Size"+ Formatter.formatShortFileSize(this,originalImage.length()));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();

        }
    }

    private void askPermission(){
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }


                    })
                    .check();

        }
}