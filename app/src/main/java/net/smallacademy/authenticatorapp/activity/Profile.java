package net.smallacademy.authenticatorapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smallacademy.authenticatorapp.R;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class Profile extends AppCompatActivity {
    private static final int GALLERY_INTENT_CODE = 1023 ;

    EditText fullName,email;
    TextView verifyMsg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button resendCode,view;
    Button resetPassLocal,changeProfileImage;
    FirebaseUser user;
    ImageView profileImage;
    StorageReference storageReference;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullName       = findViewById(R.id.profileName);
        fullName.setFocusable(false);
        fullName.setEnabled(false);
        fullName.setCursorVisible(false);
        fullName.setKeyListener(null);
        email          = findViewById(R.id.profileEmail);
        email.setFocusable(false);
        email.setEnabled(false);
        email.setCursorVisible(false);
        email.setKeyListener(null);
        view = findViewById(R.id.viewAnal);

        profileImage       = findViewById(R.id.profileImage);
        changeProfileImage = findViewById(R.id.changeProfile);
        fAuth            = FirebaseAuth.getInstance();
        fStore           = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Strands.class));
            }
        });

        StorageReference profileRef = storageReference.child("admin/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        resendCode = findViewById(R.id.resendCode);
        verifyMsg  = findViewById(R.id.verifyMsg);

        userId = fAuth.getCurrentUser().getUid();
        user   = fAuth.getCurrentUser();

        // verifying email

        if(!user.isEmailVerified()){
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        }

        DocumentReference documentReference = fStore.collection("admin").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){

                    fullName.setText(documentSnapshot.getString("Full Name"));
                    email.setText(documentSnapshot.getString("Email"));

                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });

            // changing profile picture

        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                Intent i = new Intent(v.getContext(), EditProfile.class);
                i.putExtra("Full Name",fullName.getText().toString());
                i.putExtra("Email",email.getText().toString());
                startActivity(i);
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigation2);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.profile2:

                        break;
                    case R.id.home2:
                        Intent homeIntent = new Intent(Profile.this, CategoryActivity.class);
                        startActivity(homeIntent);
                        break;
                    case R.id.logout2:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                        builder.setMessage("Are you sure that you want to log out?")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseAuth.getInstance().signOut();

                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    }
                                }).setNegativeButton("NO", null);

                        AlertDialog alert = builder.create();
                        alert.show();
                        break;
                }
                return true;
            }
        });
    }


}
