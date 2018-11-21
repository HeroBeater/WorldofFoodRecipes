package com.example.alex.worldoffoodrecipes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class addNewRecipeActivity extends AppCompatActivity {

    private EditText title, keyWords, description;
    private TextView videoText;
    private Switch switchPublic;
    private GridView gridViewImages;
    private ArrayList<Bitmap> images;
    private MyAdapter adapt;
    private Date date= new Date();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage fs;
    private StorageReference storageReference;
    private Uri filePathVideo;
    private ArrayList<Uri> filesPathImages = new ArrayList<>();
    private ArrayList<String> linksToImages = new ArrayList<>();
    private String linkToVideo = "";
    private final int PICK_IMAGE_REQUEST = 1;
    private final int PICK_VIDEO_REQUEST = 2;
    private Boolean update = false;

    @SuppressLint("SetTextI18n")
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
        keyWords = findViewById(R.id.editTextOfSum);
        description = findViewById(R.id.editTextOfDesc);
        final TextView userField = findViewById(R.id.textViewUser);
        Button addButton = findViewById(R.id.buttonAddNewRecipe);
        Button imageButton = findViewById(R.id.buttonImage);
        Button videoButton = findViewById(R.id.buttonVideo);
        switchPublic = findViewById(R.id.switchPublic);
        videoText = findViewById(R.id.videoText);

        images = new ArrayList<>();
        filesPathImages = new ArrayList<>();
        linksToImages = new ArrayList<>();

        gridViewImages = findViewById(R.id.grid_view_images);
        adapt = new MyAdapter(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fs = FirebaseStorage.getInstance();

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

        if(getIntent().getExtras()!=null){
            update = true;
            final String r_ID = getIntent().getStringExtra("edit");
            addButton.setText("Update recipe");
            db.collection("All Recipes").document(r_ID).get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            title.setText(task.getResult().getString("Title"));
                            keyWords.setText(task.getResult().getString("Key_words"));
                            description.setText(task.getResult().getString("Description"));
                            if (task.getResult().getString("Public").equals("yes")){
                                switchPublic.setChecked(true);
                            }else {
                                switchPublic.setChecked(false);
                            }

                            //IMAGES AND VIDEOS NEEDED ALSO

                            linksToImages = (ArrayList<String>)task.getResult().get("Links_Images");

                            if(!linksToImages.isEmpty()){
                                for (int i=0;i<linksToImages.size();i++){
                                    StorageReference httpsReference = fs.getReferenceFromUrl(linksToImages.get(i));
                                    httpsReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            images.add(bitmap);
                                            adapt.setmItems(images);
                                            gridViewImages.setAdapter(adapt);
                                        }
                                    });
                                }
                            }
                        }
                    });
        }

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

        videoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(addNewRecipeActivity.this, FullscreenVideoActivity.class);
                intent.putExtra("what", "no");
                intent.putExtra("path", filePathVideo.toString());
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(update){
                    DocumentReference recipe = db.collection("All Recipes").document(getIntent().getStringExtra("edit"));
                    String pub = "no";
                    if (switchPublic.isChecked()){
                             pub = "yes";
                    }
                    /*for (int i = 0; i < images.size(); i++) {
                        uploadImage(getIntent().getStringExtra("edit"),i);
                    }*/

                    recipe.update("Title", title.getText().toString(),
                            "Key_words", keyWords.getText().toString(),
                            "Description", description.getText().toString(),
                            "Public",pub,
                            "Links_Images",linksToImages)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"Recipe updated!",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(addNewRecipeActivity.this, myRecipesActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"ERROR update!",Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    long time = date.getTime();
                    final String recipe_ID = title.getText().toString()+String.valueOf(time);
                    Map<String, Object> map = new HashMap<>();
                    map.put("Title", title.getText().toString());
                    map.put("Key_words", keyWords.getText().toString());
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

                    for (int i = 0; i < images.size(); i++) {
                        uploadImage(recipe_ID,i);
                    }

                    uploadVideo(recipe_ID);

                    map.put("Links_Images",linksToImages);
                    map.put("Link_Video",linkToVideo);

                    db.collection("All Recipes").document(recipe_ID).set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
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
            }
        });

        gridViewImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                try {
                    Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    Bitmap selectImage = adapt.getItem(position);
                    selectImage.compress(Bitmap.CompressFormat.JPEG, 20, bs);
                    i.putExtra("byteArray", bs.toByteArray());
                    startActivity(i);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void chooseVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            Uri filePathImage = data.getData();
            try {
                filesPathImages.add(filePathImage);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathImage);
                images.add(bitmap);
                adapt.setmItems(images);
                gridViewImages.setAdapter(adapt);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if(requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            filePathVideo = data.getData();
            try {
                videoText.setText("Video has been selected! (Click to see)");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(final String RecipeID, final int i) {

        final StorageReference ref = storageReference.child("images/"+ RecipeID+String.valueOf(i));

        Bitmap m = images.get(i);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int countBytes = m.getByteCount();
        if(countBytes>60000000){
            m.compress(Bitmap.CompressFormat.JPEG, 15, baos);
        }else if(countBytes>30000000){
            m.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        }else {
            m.compress(Bitmap.CompressFormat.JPEG, 35, baos);
        }
        byte[] data = baos.toByteArray();

        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("Update"+String.valueOf(i),uri.toString());
                                DocumentReference rd = db.collection("All Recipes").document(RecipeID);
                                linksToImages.add(uri.toString());
                                rd.update("Links_Images",linksToImages).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addNewRecipeActivity.this, "Failed to upload!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadVideo(final String RecipeID) {
        if(filePathVideo != null)
        {
            final StorageReference ref = storageReference.child("videos/"+ RecipeID);
            ref.putFile(filePathVideo)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DocumentReference rd = db.collection("All Recipes").document(RecipeID);
                                    linkToVideo = uri.toString();
                                    rd.update("Link_Video",uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(addNewRecipeActivity.this, "Failed to upload!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

}