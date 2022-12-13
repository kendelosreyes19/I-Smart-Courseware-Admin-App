package net.smallacademy.authenticatorapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.smallacademy.authenticatorapp.Model.QuestionModel;
import net.smallacademy.authenticatorapp.QuestionAdapter;
import com.smallacademy.authenticatorapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.smallacademy.authenticatorapp.activity.CategoryActivity.catList;
import static net.smallacademy.authenticatorapp.activity.CategoryActivity.selectedCatIndex;
import static net.smallacademy.authenticatorapp.activity.SetsActivity.seleted_set_index;
import static net.smallacademy.authenticatorapp.activity.SetsActivity.setList;

public class QuestionsActivity extends AppCompatActivity {
    RecyclerView question_recycler;
    TextView addquestion;
    private Dialog isLoading;
    private QuestionAdapter adapter;
    private FirebaseFirestore firestore;
    ImageView BackImage;
    public static List<QuestionModel> questionList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        question_recycler=findViewById(R.id.questionRecycler);
        addquestion=findViewById(R.id.addquestionbutton);



        isLoading=new Dialog(QuestionsActivity.this);
        isLoading.setContentView(R.layout.loader);
        isLoading.setCancelable(false);
        isLoading.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
        isLoading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(QuestionsActivity.this, QuizActivity.class);
                intent.putExtra("ACTION","ADD");

                startActivity(intent);

            }
        });

        BackImage = findViewById(R.id.go_imageQuestList);
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SetsActivity.class));
            }
        });

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        question_recycler.setLayoutManager(layoutManager);

        firestore=FirebaseFirestore.getInstance();
        loadData();

    }

    private void loadData() {

        questionList.clear();
        isLoading.show();

        firestore.collection("QUIZ").document(catList.get(selectedCatIndex).getId()).
                collection(setList.get(seleted_set_index)).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String,QueryDocumentSnapshot> docList=new ArrayMap<>();
                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                        {
                            docList.put(doc.getId(),doc);
                        }
                        QueryDocumentSnapshot quesListDoc=  docList.get("QUESTIONS_LIST");

                        String count=quesListDoc.getString("COUNT");
                        for ( int i=0;i< Integer.valueOf(count);i++)
                        {
                            String questionId=quesListDoc.getString("Q"+ String.valueOf(i + 1) +"_ID");
                            QueryDocumentSnapshot quesDoc=docList.get(questionId);
                            questionList.add(new QuestionModel(
                                    questionId,
                                    quesDoc.getString("QUESTION"),
                                    quesDoc.getString("A"),
                                    quesDoc.getString("B"),
                                    quesDoc.getString("C"),
                                    quesDoc.getString("D"),
                                    Integer.valueOf(quesDoc.getString("ANSWER"))
                            ));
                        }
                        adapter=new QuestionAdapter(questionList);
                        question_recycler.setAdapter(adapter);
                        isLoading.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(QuestionsActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();

                isLoading.dismiss();
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }
}