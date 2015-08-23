package com.danny.camera_ex;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE=1;
    ImageView myImageView;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    private int decision=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myButton=(Button)findViewById(R.id.buttonTakePicture);
        myImageView=(ImageView)findViewById(R.id.myImageView);

        //Disable the button if the user has no camera
        if(!HasCamera()){
            myButton.setEnabled(false);
        }

    }

    private  boolean HasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void LaunchCamera(View view){

        decision=1;
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);

    }

    //Load the image from the device gallery
    public void loadImageFromGallery(View view) {
        decision=2;
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    //If you want to return the image taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (decision){
            case 1:
                if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){

                    Bundle extras=data.getExtras();
                    Bitmap photo=(Bitmap)extras.get("data");
                    myImageView.setImageBitmap(photo);
                    decision=0;
                }
                break;
            case 2:
                super.onActivityResult(requestCode, resultCode, data);
                try {
                    // When an Image is picked
                    if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                            && null != data) {
                        // Get the Image from data

                        Uri selectedImage = data.getData();
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };

                        // Get the cursor
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imgDecodableString = cursor.getString(columnIndex);
                        cursor.close();
                        ImageView imgView = (ImageView) findViewById(R.id.myImageView);
                        // Set the Image in ImageView after decoding the String
                        imgView.setImageBitmap(BitmapFactory
                                .decodeFile(imgDecodableString));

                    } else {
                        Toast.makeText(this, "You haven't picked Image",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                    decision=0;
                }
                break;

        }
        }

    }

