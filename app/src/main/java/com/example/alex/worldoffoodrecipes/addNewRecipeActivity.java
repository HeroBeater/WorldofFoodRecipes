package com.example.alex.worldoffoodrecipes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class addNewRecipeActivity extends AppCompatActivity {

    private EditText title, summary, description;
    private Switch switchPublic;
    private GridView gridViewImages;
    private ArrayList<Bitmap> images;
    private MediaController mediaControls;

    private ImageAdapter adapt = new ImageAdapter(this);

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    private Uri filePathImage, filePathVideo;
    private final int PICK_IMAGE_REQUEST = 71;
    private Boolean choosedImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.editTextOfTitleOfRecipe);
        summary = findViewById(R.id.editTextOfSum);
        description = findViewById(R.id.editTextOfDesc);
        final TextView userField = findViewById(R.id.textViewUser);
        Button addButton = findViewById(R.id.buttonAddNewRecipe);
        Button imageButton = findViewById(R.id.buttonImage);
        Button videoButton = findViewById(R.id.buttonVideo);
        switchPublic = findViewById(R.id.switchPublic);

        images = new ArrayList<>();

        gridViewImages = findViewById(R.id.grid_view_images);

        /*if (mediaControls == null) {
            mediaControls = new MediaController(this);
        }*/

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        db.collection("Users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    userField.setText("Created by "+doc.get("Name").toString());
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {}
                });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipe_ID = title.getText().toString()+Calendar.getInstance().getTime().toString();
                Map<String, Object> map = new HashMap<>();
                map.put("Title", title.getText().toString());
                map.put("Summary", summary.getText().toString());
                map.put("Description", description.getText().toString());
                map.put("Author_of_recipe",mAuth.getCurrentUser().getUid());
                map.put("Recipe_ID",recipe_ID);
                map.put("Total_ratings",0);
                map.put("Number_of_reviews",0);
                map.put("Average_rating",0);
                if (switchPublic.isChecked()){
                    map.put("Public","yes");
                }else{
                    map.put("Public","no");
                }

                final String pathName = mAuth.getCurrentUser().getUid()+title.getText().toString();

                db.collection("All Recipes").document(recipe_ID).set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                uploadImage(pathName);
                                uploadVideo(pathName);
                                Toast.makeText(getApplicationContext(), "Recipe saved", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(addNewRecipeActivity.this, myRecipesActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Recipe NOT saved", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        gridViewImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // Sending image id to FullScreenActivity
                Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                // passing array index
                i.putExtra("id", position);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                adapt.getItem(position).compress(Bitmap.CompressFormat.JPEG, 50, bs);
                i.putExtra("byteArray", bs.toByteArray());
                startActivity(i);
            }
        });

    }

    private void chooseImage(){
        choosedImage = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void chooseVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            if(choosedImage){
                filePathImage = data.getData();
                Toast.makeText(getApplicationContext(), "Selected!"+filePathImage, Toast.LENGTH_LONG).show();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathImage);
                    images.add(bitmap);
                    adapt.setmThumbIds(images);
                    gridViewImages.setAdapter(adapt);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }else{
                filePathVideo = data.getData();
                Toast.makeText(getApplicationContext(), "Selected!", Toast.LENGTH_SHORT).show();
                try {
                    //videoRecipe.setVideoURI(filePathVideo);
                    //videoRecipe.start();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadImage(String RecipeID) {

        if(filePathImage != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ RecipeID);
            ref.putFile(filePathImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(addNewRecipeActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(addNewRecipeActivity.this, "Failed to upload!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void uploadVideo(String RecipeID) {
        if(filePathVideo != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("videos/"+ RecipeID);
            ref.putFile(filePathVideo)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(addNewRecipeActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(addNewRecipeActivity.this, "Failed to upload!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

}
