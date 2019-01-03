package com.example.syahril.yourtaskapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.syahril.yourtaskapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity {

    private TextView signup;

    private EditText email;
    private EditText pass;
    private Button btnLogin;
    private ConstraintLayout root;

    //firebase..

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

       setupView();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String mEmail=email.getText().toString().trim();
                String mPass=pass.getText().toString().trim();

                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(mPass)){
                    pass.setError("Required Field");
                    return;
                }

                showProgressDialog();
                mAuth.signInWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            //insert some default data
                            //check if user exist,if not,insert.
                            Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("name").equalTo(mEmail);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {

                                        showSnackBar(root,"Login Successful");
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        // 1 or more users exist which have the username property "usernameToCheckIfExists"
                                    }else{
                                        //new
                                        User user = new User();
                                        user.setName(mEmail);
                                        user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
//

                                                        showSnackBar(root,"Login Successful");
                                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                FirebaseAuth.getInstance().signOut();
                                                showSnackBar(root,"Please try again later");
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    FirebaseAuth.getInstance().signOut();
                                    showSnackBar(root,"Please try again later");
                                }
                            });


                        } else {
                            showSnackBar(root,"Please try again later");
                        }
                        dismissProgressDialog();
                    }
                });

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    private void setupView(){
        root=findViewById(R.id.root);
        signup=findViewById(R.id.signup_txt);
        signup.setVisibility(View.GONE);
        email = findViewById(R.id.email_login);
        pass = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.Login_btn);
    }
}
