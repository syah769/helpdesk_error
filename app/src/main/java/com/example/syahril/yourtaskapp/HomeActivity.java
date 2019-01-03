package com.example.syahril.yourtaskapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.syahril.yourtaskapp.Model.Data;
import com.example.syahril.yourtaskapp.Model.Message;
import com.example.syahril.yourtaskapp.adapter.CheckBoxListener;
import com.example.syahril.yourtaskapp.adapter.ClickListener;
import com.example.syahril.yourtaskapp.adapter.RecyclerViewAdapter;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeActivity extends BaseActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabBtn;

    //firebase

    public DatabaseReference mDatabase;
    private DatabaseReference mDatabaseSpinner;
    private FirebaseAuth mAuth;

    //Recycler..

    private RecyclerView recyclerView;

    //Update input field..
    private EditText titleupdate;
    private EditText noteupdate;
    private Spinner spinnerList;
    private Spinner spinnerupdate;
    private LinearLayout root;

    //variable

    private String title;
    private String note;
    private String staff;
    private String post_key;
    private String parentNode;

    private List<String> spinnerListString = new ArrayList<>();
    private ArrayAdapter<String> spinnerListAdapter;
    private List<Data> myObject = new ArrayList<>();

    private RecyclerViewAdapter recyclerViewAdapter;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();
        //get all user uid


        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uId);
        mDatabase.keepSynced(true);

        //firebase spinner data
        mDatabaseSpinner = FirebaseDatabase.getInstance().getReference().child("staff");
        initFCM();
        //Recycler..

        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        root = findViewById(R.id.root);
        fabBtn = findViewById(R.id.fab_btn);
        if (mUser.getUid().equalsIgnoreCase(getString(R.string.uid))) {
            fabBtn.show();

        } else {
            fabBtn.hide();
        }
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupDialogSave();
            }
        });
//        setupRecyclerViewList(null, true, 0);

        if (mUser.getUid().equalsIgnoreCase(getString(R.string.uid))) {

            mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote");
            operationByAdmin();


        } else {
            //technician
            mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(getString(R.string.uid));
            operationByTechnician();
            //boleh tgok apa admin add je
//            belongsToTechnician();

        }


    }

    private void operationByTechnician(){
        showProgressDialog();
        new Thread(new Runnable() {
            public void run() {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myObject = new ArrayList<>();
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();



                                    Data data = new Data();
                                    data.setParentNode(dataSnapshot1.getKey());
                                    data.setId((String) message.get("id"));
                                    data.setDate((String) message.get("date"));
                                    data.setNote((String) message.get("note"));
                                    data.setStaff((String) message.get("staff"));
                                    data.setTitle((String) message.get("title"));
                                    int status = 0;
                                    if (message.get("status") != null) {
                                        long statusLong = (long) message.get("status");
                                        status = (int) statusLong;
                                    }
                                    data.setStatus(status);
                                    myObject.add(data);


                                final Handler handlers = new Handler();
                                handlers.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (myObject != null && myObject.size() > 0) {
                                            if (recyclerViewAdapter != null) {
                                                recyclerViewAdapter.update(myObject);
                                                recyclerViewAdapter.notifyDataSetChanged();
                                            } else {
                                                recyclerViewAdapter = new RecyclerViewAdapter(mDatabase, getApplicationContext(), myObject, new ClickListener() {
                                                    @Override
                                                    public void btnClick(Data data, int position) {
                                                        parentNode = data.getParentNode();
                                                        post_key = data.getId();
                                                        title = data.getTitle();
                                                        note = data.getNote();
                                                        staff = data.getStaff();
                                                        setupDialogUpdate();
                                                    }
                                                }, new CheckBoxListener() {
                                                    @Override
                                                    public void clickCheckBox(Data data) {
                                                        data.setStatus(1);//success

                                                        //fire listener
                                                        mDatabase.child(data.getParentNode()).child(data.getId()).setValue(data, new DatabaseReference.CompletionListener() {
                                                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                System.out.println("Value was set. Error = " + error);
                                                            }
                                                        });
                                                    }



                                                });
                                            }


                                            recyclerView.setAdapter(recyclerViewAdapter);
                                        }

                                        dismissProgressDialog();
                                    }
                                }, 1000);
                            }
                        } else {
                            dismissProgressDialog();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).start();
    }


    private void operationByAdmin() {
        showProgressDialog();
        new Thread(new Runnable() {
            public void run() {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myObject = new ArrayList<>();
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();



                                for (Map.Entry<String, Object> entry : message.entrySet()) {
                                    Data data = new Data();
                                    Map<String, Object> messages = (Map<String, Object>) entry.getValue();
                                    data.setParentNode(dataSnapshot1.getKey());
                                    data.setId((String) messages.get("id"));
                                    data.setDate((String) messages.get("date"));
                                    data.setNote((String) messages.get("note"));
                                    data.setStaff((String) messages.get("staff"));
                                    data.setTitle((String) messages.get("title"));
                                    int status = 0;
                                    if (messages.get("status") != null) {
                                        long statusLong = (long) messages.get("status");
                                        status = (int) statusLong;
                                    }
                                    data.setStatus(status);
                                    myObject.add(data);

                                }
                                final Handler handlers = new Handler();
                                handlers.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (myObject != null && myObject.size() > 0) {
                                            if (recyclerViewAdapter != null) {
                                                recyclerViewAdapter.update(myObject);
                                                recyclerViewAdapter.notifyDataSetChanged();
                                            } else {
                                                recyclerViewAdapter = new RecyclerViewAdapter(mDatabase, getApplicationContext(), myObject, new ClickListener() {
                                                    @Override
                                                    public void btnClick(Data data, int position) {
                                                        parentNode = data.getParentNode();
                                                        post_key = data.getId();
                                                        title = data.getTitle();
                                                        note = data.getNote();
                                                        staff = data.getStaff();
                                                        setupDialogUpdate();
                                                    }
                                                }, new CheckBoxListener() {
                                                    @Override
                                                    public void clickCheckBox(Data data) {
                                                        data.setStatus(1);//success

                                                        //fire listener
                                                        mDatabase.child(data.getParentNode()).child(data.getId()).setValue(data, new DatabaseReference.CompletionListener() {
                                                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                                System.out.println("Value was set. Error = " + error);
                                                            }
                                                        });
                                                    }



                                                });
                                            }


                                            recyclerView.setAdapter(recyclerViewAdapter);
                                        }

                                        dismissProgressDialog();
                                    }
                                }, 1000);
                            }
                        } else {
                            dismissProgressDialog();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).start();
    }

    private void initFCM() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token", "initFCM: token: " + token);
        sendRegistrationToServer(token);

    }

    private void sendRegistrationToServer(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("messaging_token")
                .setValue(token);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    private void setupSpinnerList(Spinner spinner) {
        spinnerListString.clear();
        spinnerListAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, spinnerListString);
        //spinnerListAdapter.clear();
        spinnerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerListAdapter);
        spinner.setPrompt("Staff");
        mDatabaseSpinner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()
                        ) {
                    spinnerListString.add(data.getValue().toString());
                }
                spinnerListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //TODO
                if (pos <= spinnerListString.size() - 1) {
                    //System.out.println(spinnerListString.get(pos));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //TODO
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_all:
                //show all ticket

                if (mUser.getUid().equalsIgnoreCase(getString(R.string.uid))) {

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote");
                    operationByAdmin();


                } else {
                    //technician
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(getString(R.string.uid));
                    operationByTechnician();
                    //boleh tgok apa admin add je
//            belongsToTechnician();

                }
                break;

            case R.id.action_new:
                //show all ticket
//                queryMDatabase = mDatabase.orderByChild("status").equalTo(0);
                operationSorting(0, false);
                break;

            case R.id.action_success:
                //show all ticket
//                queryMDatabase =  mDatabase.orderByChild("status").equalTo(1);
                operationSorting(1, false);
                break;

            case R.id.action_pending:
                //show all ticket
//                setupRecyclerViewList(false,2);
                operationSorting(-1, true);
                break;

            case R.id.logout:
                mAuth.signOut();
                finish();
//                testPush();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void operationSorting(final int statusSend, final boolean isPending) {
        showProgressDialog();
        new Thread(new Runnable() {
            public void run() {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myObject = new ArrayList<>();
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();

                                for (Map.Entry<String, Object> entry : message.entrySet()) {
                                    Data data = new Data();
                                    Map<String, Object> messages = (Map<String, Object>) entry.getValue();
                                    data.setParentNode(dataSnapshot1.getKey());
                                    data.setId((String) messages.get("id"));
                                    data.setDate((String) messages.get("date"));
                                    data.setNote((String) messages.get("note"));
                                    data.setStaff((String) messages.get("staff"));
                                    data.setTitle((String) messages.get("title"));
                                    int status = 0;
                                    if (messages.get("status") != null) {
                                        long statusLong = (long) messages.get("status");
                                        status = (int) statusLong;
                                    }
                                    data.setStatus(status);
                                    if (statusSend == status) {
                                        myObject.add(data);
                                    } else if (isPending == true) {
                                        String date = convertDateBasedOnDay(data.getDate());
                                        if (!date.equalsIgnoreCase("TODAY")) {
                                            if (data.getStatus() == 0) {
                                                data.setStatus(2);
                                                myObject.add(data);
                                            }
                                        }
                                    }


                                }
                                final Handler handlers = new Handler();
                                handlers.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (recyclerViewAdapter != null) {
                                            recyclerViewAdapter.update(myObject);
                                            recyclerViewAdapter.notifyDataSetChanged();
                                        } else {
                                            recyclerViewAdapter = new RecyclerViewAdapter(mDatabase, getApplicationContext(), myObject, new ClickListener() {
                                                @Override
                                                public void btnClick(Data data, int position) {
                                                    parentNode = data.getParentNode();
                                                    post_key = data.getId();
                                                    title = data.getTitle();
                                                    note = data.getNote();
                                                    staff = data.getStaff();
                                                    setupDialogUpdate();
                                                }
                                            }, new CheckBoxListener() {
                                                @Override
                                                public void clickCheckBox(Data data) {
                                                    data.setStatus(1);//success

                                                    //fire listener
                                                    mDatabase.child(data.getParentNode()).setValue(data, new DatabaseReference.CompletionListener() {
                                                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                            System.out.println("Value was set. Error = " + error);
                                                        }
                                                    });
                                                }
                                            }

                                            );
                                        }


                                        recyclerView.setAdapter(recyclerViewAdapter);
                                        dismissProgressDialog();
                                    }
                                }, 1000);
                            }
                        } else {
                            dismissProgressDialog();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).start();
    }

    private void testPush() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


//        if(!mMessage.getText().toString().equals("")){

        //create the new message
//            Message message = new Message();
//            message.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
//            message.setMessage("hai");
//            message.setTimestamp("");
        ///https://codingwithmitch.com/blog/android-firebase-cloud-messages-cloud-function/
        /// https://codingwithmitch.com/blog/android-firebase-cloud-messages-cloud-function/
        //insert the new message

        DatabaseReference databaseRef = reference.child("users");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    Log.d("GetDonorId", "DataSnapshot :" + dataSnapshot1.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//            reference
//                    .child("messages")
//                    .child(mUserId)//get naim userId then
//                    .child(reference.push().getKey())
//                    .setValue(message);
//            Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_SHORT).show();
    }


    public void setupDialogUpdate() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.updateinputfield, null);


        titleupdate = alertLayout.findViewById(R.id.edit_title_update);
        noteupdate = alertLayout.findViewById(R.id.edit_note_update);
        spinnerupdate = alertLayout.findViewById(R.id.spinner_update);
        spinnerupdate.setAdapter(spinnerListAdapter);
        titleupdate.setText(title);
        titleupdate.setSelection(title.length());


        noteupdate.setText(note);
        noteupdate.setSelection(note.length());

        //Spinner

        setupSpinnerList(spinnerupdate);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Update Task");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);

        alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDatabase.child(parentNode).child(post_key).removeValue();
                showSnackBar(root, "Data Removed!");
//                recyclerViewAdapter.notifyDataSetChanged();
            }
        });


        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, int which) {

                showProgressDialog();
                title = titleupdate.getText().toString().trim();
                note = noteupdate.getText().toString().trim();
                staff = spinnerupdate.getSelectedItem().toString().trim();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(title, note, staff, mDate, post_key, 0);
                mDatabase.child(parentNode).child(post_key).setValue(data, new DatabaseReference.CompletionListener() {
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
//                            System.out.println("Value was set. Error = "+error);
                        if (error == null) {
                            showSnackBar(root, "Data Updated!");
                        } else {
                            showSnackBar(root, error.toString());
                        }

                        dismissProgressDialog();
                        dialog.dismiss();
                    }
                });


            }
        });

        final AlertDialog dialog = alert.create();
        dialog.show();


    }

    public void setupDialogSave() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custominputfield, null);

        final EditText title = alertLayout.findViewById(R.id.edit_title);
        final EditText note = alertLayout.findViewById(R.id.edit_note);
        final Spinner staff = alertLayout.findViewById(R.id.spinner);
        spinnerList = alertLayout.findViewById(R.id.spinner);
        //Spinner

        setupSpinnerList(spinnerList);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Task");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, int which) {


                String mTitle = title.getText().toString().trim();
                String mNote = note.getText().toString().trim();
                String mStaff = staff.getSelectedItem().toString().trim();

                if (TextUtils.isEmpty(mTitle)) {
                    title.setError("Required Field");

                } else if (TextUtils.isEmpty(mNote)) {
                    note.setError("Required Field");
                } else {
                    showProgressDialog();
                    String uId = mUser.getUid();
                    String id = mDatabase.child(uId).push().getKey();

                    String date = DateFormat.getDateInstance().format(new Date());

                    //NEW STATUS
                    Data data = new Data(mTitle, mNote, mStaff, date, id, 0);

                    mDatabase.child(uId).child(id).setValue(data, new DatabaseReference.CompletionListener() {
                        public void onComplete(DatabaseError error, DatabaseReference ref) {
//                            System.out.println("Value was set. Error = "+error);
                            if (error == null) {
                                showSnackBar(root, "Data Inserted!");
                            } else {
                                showSnackBar(root, error.toString());
                            }

                            dismissProgressDialog();
                            dialog.dismiss();
                        }
                    });
                }
//


            }
        });

        final AlertDialog dialog = alert.create();
        dialog.show();


    }

}
