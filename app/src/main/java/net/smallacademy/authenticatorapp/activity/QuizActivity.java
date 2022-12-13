package net.smallacademy.authenticatorapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.smallacademy.authenticatorapp.Model.QuestionModel;
import com.smallacademy.authenticatorapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import static net.smallacademy.authenticatorapp.activity.CategoryActivity.catList;
import static net.smallacademy.authenticatorapp.activity.CategoryActivity.selectedCatIndex;

public class QuizActivity extends AppCompatActivity {
    private EditText optionA,optionB,optionC,optionD,question,answer;
    private TextView addMcq,action_bar_text;
    private FirebaseFirestore firestore;
    private int qID;

    private  String action;
    private Dialog isLoading;
    private String stroptionA,stroptionB,stroptionC,stroptionD,strquestion,stringanswer;
    ImageView BackImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        optionA=findViewById(R.id.optionA);
        optionB=findViewById(R.id.optionB);
        optionC=findViewById(R.id.optionC);
        optionD=findViewById(R.id.optionD);
        answer=findViewById(R.id.answer);
        action_bar_text=findViewById(R.id.action_bar_text);
        question=findViewById(R.id.questionDescription);
        addMcq=findViewById(R.id.addMCQbutton);
        isLoading=new Dialog(QuizActivity.this);
        isLoading.setContentView(R.layout.loader);
        isLoading.setCancelable(false);
        isLoading.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
        isLoading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        firestore=FirebaseFirestore.getInstance();


        BackImage = findViewById(R.id.go_imageAddQuest);
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QuestionsActivity.class));
            }
        });

        action = getIntent().getStringExtra("ACTION");

        if(action.compareTo("EDIT") == 0)
        {
            qID = getIntent().getIntExtra("Q_ID",0);
            loadData(qID);
            action_bar_text.setText("Question " + String.valueOf(qID + 1));
            addMcq.setText("UPDATE");
        }
        else
        {
            action_bar_text.setText("Question " + String.valueOf(QuestionsActivity.questionList.size() + 1));
            addMcq.setText("ADD");
        }

        addMcq.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                strquestion=question.getText().toString();
                stroptionA=optionA.getText().toString();
                stroptionB=optionB.getText().toString();
                stroptionC=optionC.getText().toString();
                stroptionD=optionD.getText().toString();
                stringanswer=answer.getText().toString();


                if (strquestion.isEmpty())
                {
                    question.setError("Enter Question Please");
                }
                if (stroptionA.isEmpty())
                {
                    optionA.setError("Enter Option A Please");
                }  if (stroptionB.isEmpty())
                {
                    optionB.setError("Enter Option B Please");
                }  if (stroptionC.isEmpty())
                {
                    optionC.setError("Enter Option C Please");
                }  if (stroptionD.isEmpty())
                {
                    optionD.setError("Enter Option D Please");
                }  if (stringanswer.isEmpty())
                {
                    answer.setError("Enter Answer Please");
                }
                if(action.compareTo("EDIT") == 0)
                {
                    editQuestion();
                }
                else {
                    addnewMcq();
                }


            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addnewMcq() {
        isLoading.show();
        Map<String,Object> quesData=new ArrayMap<>();
        quesData.put("QUESTION",strquestion);
        quesData.put("A",stroptionA);
        quesData.put("B",stroptionB);
        quesData.put("C",stroptionC);
        quesData.put("D",stroptionD);
        quesData.put("ANSWER",stringanswer);

       final String doc_id=firestore.collection("QUIZ").document(catList.get(selectedCatIndex).getId())
                .collection(SetsActivity.setList.get(SetsActivity.seleted_set_index)).document().getId();
        firestore.collection("QUIZ").document(catList.get(selectedCatIndex).getId())
                .collection(SetsActivity.setList.get(SetsActivity.seleted_set_index)).document(doc_id).set(quesData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Map<String,Object> quesDoc=new ArrayMap<>();
                quesDoc.put("Q"+String.valueOf(QuestionsActivity.questionList.size() + 1)+"_ID",doc_id);
                quesDoc.put("COUNT",String.valueOf(QuestionsActivity.questionList.size() +1));
                firestore.collection("QUIZ").document(catList.get(selectedCatIndex).getId())
                        .collection(SetsActivity.setList.get(SetsActivity.seleted_set_index)).document("QUESTIONS_LIST").update(quesDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(QuizActivity.this,"Question Added Successfully",Toast.LENGTH_SHORT).show();
                        QuestionsActivity.questionList.add(new QuestionModel(
                                doc_id,strquestion,
                                stroptionA,
                                  stroptionB,
                                stroptionC,
                                stroptionD,
                                Integer.valueOf(stringanswer)
                        ));
                        isLoading.dismiss();
                        QuizActivity.this.finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuizActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        isLoading.dismiss();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(QuizActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();

            }
        });


    }
    private void loadData(int id)
    {
        question.setText(QuestionsActivity.questionList.get(id).getQuetion());
        optionA.setText(QuestionsActivity.questionList.get(id).getOptionA());
        optionB.setText(QuestionsActivity.questionList.get(id).getOptionB());
        optionC.setText(QuestionsActivity.questionList.get(id).getOptionC());
        optionD.setText(QuestionsActivity.questionList.get(id).getOptionD());
        answer.setText(String.valueOf(QuestionsActivity.questionList.get(id).getCorrectAnswer()));
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void editQuestion()
    {
        isLoading.show();

        Map<String,Object> quesData = new ArrayMap<>();
        quesData.put("QUESTION", strquestion);
        quesData.put("A",stroptionA);
        quesData.put("B",stroptionB);
        quesData.put("C",stroptionC);
        quesData.put("D",stroptionD);
        quesData.put("ANSWER",stringanswer);


        firestore.collection("QUIZ").document(catList.get(selectedCatIndex).getId())
                .collection(SetsActivity.setList.get(SetsActivity.seleted_set_index)).document(QuestionsActivity.questionList.get(qID).getQuetionId())
                .set(quesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(QuizActivity.this,"Question updated successfully",Toast.LENGTH_SHORT).show();

                        QuestionsActivity.questionList.get(qID).setQuetion(strquestion);
                        QuestionsActivity.questionList.get(qID).setOptionA(stroptionA);
                        QuestionsActivity.questionList.get(qID).setOptionB(stroptionB);
                        QuestionsActivity.questionList.get(qID).setOptionC(stroptionC);
                        QuestionsActivity.questionList.get(qID).setOptionD(stroptionD);
                        QuestionsActivity.questionList.get(qID).setCorrectAnswer(Integer.valueOf(stringanswer));

                        isLoading.dismiss();
                        QuizActivity.this.finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuizActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        isLoading.dismiss();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}