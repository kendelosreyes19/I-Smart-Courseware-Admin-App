package net.smallacademy.authenticatorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smallacademy.authenticatorapp.R;

import java.util.ArrayList;

public class Strands extends AppCompatActivity {
    FirebaseFirestore db;
    RecyclerView mRecyclerView;
    ArrayList<DownModelStrands> downModelStrandsArrayList = new ArrayList<>();
    MyAdapterStrands myAdapterStrands;
    ImageView BackImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strands);

        setUpRV();
        setUpFB();
        dataFromFirebase();

        BackImage = findViewById(R.id.go_imageStrands);
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CategoryActivity.class));
                finish();
            }
        });
    }

    private void dataFromFirebase() {
        if(downModelStrandsArrayList.size()>0)
            downModelStrandsArrayList.clear();

        //db=FirebaseFirestore.getInstance();

        db.collection("user score")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot: task.getResult()) {

                            DownModelStrands downModelStrands = new DownModelStrands(documentSnapshot.getString("Full Name"),
                                    documentSnapshot.getString("Suggested Strand"),
                                    documentSnapshot.getString("Examination Date"));
                            downModelStrandsArrayList.add(downModelStrands);

                        }

                        myAdapterStrands = new MyAdapterStrands(Strands.this, downModelStrandsArrayList);
                        mRecyclerView.setAdapter(myAdapterStrands);
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Strands.this, "Error ;-.-;", Toast.LENGTH_SHORT).show();
                    }
                })
        ;


    }

    private void setUpFB(){
        db=FirebaseFirestore.getInstance();
    }

    private void setUpRV(){
        mRecyclerView= findViewById(R.id.recycleStrand);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



}
