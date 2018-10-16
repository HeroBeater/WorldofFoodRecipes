package com.example.alex.worldoffoodrecipes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

public class myRecipesActivity extends AppCompatActivity {

    private Button button;
    private EditText editText;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Recipe recipe1;
    private Recipe recipe2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        recyclerView = findViewById(R.id.recyclerView);

        button = findViewById(R.id.btn);
        editText = (EditText) findViewById(R.id.edittext);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        //final FirebaseFirestore db = FirebaseFirestore.getInstance();

        recipe1 = new Recipe("Hunger!!!!!", "BlaBlaBla", "more BlaBlaBla");
        recipe2 = new Recipe("Hunger more !!!!!", "BlaBlaBla", "more BlaBlaBla");

        Recipe[] recipes = {recipe1, recipe2};
        mAdapter = new RecipeAdapter(recipes);
        recyclerView.setAdapter(mAdapter);





        /*//add Data

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("name", editText.getText().toString());
                db.collection("Recipe Name").document().set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Data NOT saved", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // retrieve Data

        db.collection("Recipe Name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                namesList.clear();
                for (DocumentSnapshot snapshot : documentSnapshots){
                    namesList.add(snapshot.getString("name"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_selectable_list_item,namesList);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }
        });*/

    }
}
