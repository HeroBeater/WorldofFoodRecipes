package com.example.alex.worldoffoodrecipes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class RecipeShowActivity extends AppCompatActivity {

    private TextView textTitle, textSum, textDesc;
    private TextView videoText, videos_gallery;
    private ImageView imageRecipe;
    private RatingBar ratingBar;
    private String recipe_ID;
    private String user_of_recipe;
    private GridView gridViewImages;
    private ArrayList<Bitmap> images = new ArrayList<>();
    private MyAdapter adapt;
    private RecyclerView recyclerViewReview;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage fs;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_show);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fs = FirebaseStorage.getInstance();

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        recyclerViewReview = findViewById(R.id.recyclerViewReview);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewReview.setLayoutManager(mLayoutManager);

        gridViewImages = findViewById(R.id.grid_view_images);
        adapt = new MyAdapter(this);

        textTitle = findViewById(R.id.titleOfRecipe);
        textSum = findViewById(R.id.keyWordsOfRecipe);
        textDesc = findViewById(R.id.descriptionOfRecipe);
        imageRecipe = findViewById(R.id.mainImageRecipe);
        imageRecipe.setImageResource(R.drawable.ic_launcher_foreground);
        ratingBar = findViewById(R.id.ratingBar);
        videoText = findViewById(R.id.videoText);
        videos_gallery = findViewById(R.id.videos_gallery);

        recipe_ID = getIntent().getStringExtra("recipe_ID");
        user_of_recipe = getIntent().getStringExtra("recipe_user");

        db.collection("All Recipes").document(recipe_ID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final String recipe_ID = document.getString("Recipe_ID");
                        user_of_recipe = document.getString("Author_of_recipe");
                        if(document.getDouble("Number_of_reviews")!=0){
                            double rate = document.getDouble("Total_ratings")/document.getDouble("Number_of_reviews");
                            if((rate%10)<5){
                                db.collection("All Recipes").document(recipe_ID).update("Average_rating", (int)(rate));
                            }else{
                                db.collection("All Recipes").document(recipe_ID).update("Average_rating", (int)(rate+1));
                            }
                        }
                        textTitle.setText(document.getString("Title"));
                        textSum.setText(document.getString("Key_words"));
                        textDesc.setText(document.getString("Description"));
                        ratingBar.setRating(document.getLong("Average_rating"));

                        ArrayList<String> imL = (ArrayList<String>)document.get("Links_Images");

                        if(!imL.isEmpty()){
                            StorageReference httpsReference = fs.getReferenceFromUrl(imL.get(0));
                            httpsReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    imageRecipe.setImageBitmap(bitmap);
                                }
                            });

                            for (int i=0;i<imL.size();i++){
                                httpsReference = fs.getReferenceFromUrl(imL.get(i));
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


                        if (document.getString("Link_Video") == ""){
                            videos_gallery.setVisibility(View.GONE);
                        }else {
                            videoText.setText("Click to see the video!");
                            videoPath = document.getString("Link_Video");
                        }


                        db.collection("All Recipes").document(recipe_ID).collection("Reviews").orderBy("Rating", Query.Direction.DESCENDING)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        ArrayList<Review> review_list = new ArrayList<>();
                                        for (DocumentSnapshot snapshot : documentSnapshots){
                                            review_list.add(new Review(recipe_ID, user_of_recipe,snapshot.getString("Author_of_review"),snapshot.getString("Title"),
                                                    snapshot.getString("Description"), Objects.requireNonNull(snapshot.getDouble("Rating")).floatValue()));
                                        }
                                        mAdapter = new ReviewAdapter(review_list,RecipeShowActivity.this);
                                        recyclerViewReview.setAdapter(mAdapter);
                                    }
                                });
                    } else {
                        Log.d("RecipeShow", "No such document");
                    }
                } else {
                    Log.d("RecipeShow", "get failed with ", task.getException());
                }
            }
        });

        imageRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                    imageRecipe.invalidate();
                    BitmapDrawable drawable = (BitmapDrawable) imageRecipe.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bs);
                    i.putExtra("byteArray", bs.toByteArray());
                    startActivity(i);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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

        videoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeShowActivity.this, FullscreenVideoActivity.class);
                intent.putExtra("what", "yes");
                intent.putExtra("pathURL", videoPath);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.getItem(0).setVisible(false);
        if (!mAuth.getCurrentUser().getUid().equals(user_of_recipe)){
            menu.getItem(2).setVisible(false);
            menu.getItem(4).setVisible(false);
            db.collection("All Recipes").document(getIntent().getStringExtra("recipe_ID"))
                    .collection("Reviews").document(mAuth.getCurrentUser().getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    menu.getItem(3).setVisible(false);
                                } else {
                                    menu.getItem(3).setVisible(true);
                                }
                            } else {
                                Log.d("RecipeShow", "get failed with ", task.getException());
                            }
                        }
                    });
        }else{
            menu.getItem(1).setVisible(false);
            menu.getItem(3).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.favorite:
                final CollectionReference fav = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Fav Recipes");
                fav.document(recipe_ID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    fav.document(recipe_ID).delete()
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                }
                                            })
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getApplicationContext(),"Removed from Favorite",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    db.collection("All Recipes").document(recipe_ID).get()
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                }
                                            })
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    fav.document(recipe_ID).set(task.getResult().getData());
                                                    Toast.makeText(getApplicationContext(),"Added to Favorite",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),"Fail",Toast.LENGTH_SHORT).show();
                            }
                        }
                });
                break;
            case R.id.edit_recipe:
                db.collection("All Recipes").document(recipe_ID).get()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Toast.makeText(getApplicationContext(),"Edit Recipe",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RecipeShowActivity.this, addNewRecipeActivity.class);
                                intent.putExtra("edit", recipe_ID);
                                startActivity(intent);
                                finish();
                            }
                        });
                break;
            case R.id.add_review:
                Toast.makeText(getApplicationContext(),"Creating review",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RecipeShowActivity.this, addReviewActivity.class);
                intent.putExtra("new", "yes");
                intent.putExtra("ID", recipe_ID);
                intent.putExtra("recipe_user", user_of_recipe);
                startActivity(intent);
                finish();
                break;
            case R.id.delete_recipe:
                if (!mAuth.getCurrentUser().getUid().equals(user_of_recipe)){
                    item.setVisible(false);
                }else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Delete Recipe");
                    alert.setMessage("Are you sure you want to delete?");
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            CollectionReference dd = FirebaseFirestore.getInstance().collection("All Recipes");
                            dd.document(recipe_ID).delete()
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Deleting file ", "Error deleting document", e);
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("Deleting file ", "DocumentSnapshot successfully deleted!");
                                            Intent intent = new Intent(RecipeShowActivity.this, myRecipesActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        }
                    });
                    alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
                break;
            default:
                //unknown error
        }

        return super.onOptionsItemSelected(item);
    }

}
