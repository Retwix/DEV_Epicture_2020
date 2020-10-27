package com.epicture.View;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.epicture.Epicture_global;
import com.epicture.R;
import com.epicture.Callbacks.VolleyCallbacks;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class upload_photo extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int IMAGE_PICK_CODE = 1002;
    Uri image_uri;
    ImageView imageView;
    EditText title = null;
    EditText description = null;
    final Context ctx = this;
    public int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        imageView = (ImageView) findViewById(R.id.imageCam);
        Button camBtn = (Button) findViewById(R.id.buttonCam);
        Button galleryBtn = (Button) findViewById(R.id.buttonGallery);
        Button upload = findViewById(R.id.upload);
        final LinearLayout choose = (LinearLayout) findViewById(R.id.choosePicture);
        final LinearLayout dialog = (LinearLayout) findViewById(R.id.dialog);
        ImageView returnDash = (ImageView) findViewById(R.id.returnDash);

        returnDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 0;
                Intent dash = new Intent(upload_photo.this, dashboard.class);
                startActivity(dash);
            }
        });

        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 1;
                checkRight(i);
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 0;
                checkRight(i);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
                String title_upload = null;
                String description_upload = null;
                String upload_send = convertToBase64();

                title = findViewById(R.id.title_upload);
                description = findViewById(R.id.description_upload);
                title_upload = title.getText().toString();
                description_upload = description.getText().toString();


                Epicture_global.uploadImage(ctx, upload_send, title_upload, description_upload, new VolleyCallbacks() {
                    @Override
                    public void onSuccessResponse(JSONObject response) {
                        Intent dashboard = new Intent(upload_photo.this, com.epicture.View.dashboard.class);
                        startActivity(dashboard);

                    }
                });
            }
        });
    }

    public void createDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(upload_photo.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_upload, null);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void checkRight(int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                if (i == 1) {
                    openDialogCam();
                } else {
                    openDialogGallery();
                }
            }
        } else {
            if (i == 1) {
                openDialogCam();
            } else {
                openDialogGallery();
            }
        }
    }

    private void openDialogGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery, IMAGE_PICK_CODE);
    }

    private void openDialogCam() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MediaStore.Images.Media.TITLE, "New picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "From camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cam.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cam, IMAGE_CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (i == 1) {
                        openDialogCam();
                    } else {
                        openDialogGallery();
                    }
                } else {
                    Toast.makeText(this, "Oh gros active ta cam", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LinearLayout choose = (LinearLayout) findViewById(R.id.choosePicture);
        final LinearLayout dialog = (LinearLayout) findViewById(R.id.dialog);

        if (resultCode == RESULT_OK) {
            if (i == 1) {
                imageView.setImageURI(image_uri);
            } else {
                image_uri = data.getData();
                imageView.setImageURI(data.getData());
            }
            dialog.setVisibility(View.GONE);
            choose.setVisibility(View.VISIBLE);
        }
    }

    public String convertToBase64() {
        String upload_send = null;
        try {
            final InputStream imageStream = getContentResolver().openInputStream(image_uri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] b = baos.toByteArray();
            upload_send = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (IOException e){}
        return upload_send;
    }

}
