package com.avinash.testapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText edtName;
    EditText edtEmail;
    EditText edtPassword;
    EditText edtCfPassword;
    EditText edtOldPassword;

    private User user;
    private Button btnSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = getIntent().getParcelableExtra("user");

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtCfPassword = findViewById(R.id.edtCfPassword);
        edtOldPassword = findViewById(R.id.edtOldPassword);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if(validate()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", edtEmail.getText().toString());
                    user.put("name", edtName.getText().toString());
                    user.put("password", edtPassword.getText().toString());
                    user.put("createdOn", new Date());
                    user.put("modifiedOn", new Date());

                    DocumentReference reference = db.collection("emailList").document();
                    user.put("documentId", reference.getId());
                    reference.set(user);
                    Snackbar.make(view, "Successfully Saved", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    finish();

                }else{
                    Snackbar.make(view, "invalid values", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        if(user!=null){
            setViews();
        }
    }

    private void setViews() {
        findViewById(R.id.tilOldPassword).setVisibility(View.VISIBLE);
        edtName.setText(user.getName());
        edtEmail.setText(user.getName());
        btnSave.setText("Update");
    }


    private void startListActivity() {
        Intent intent = new Intent(MainActivity.this,ListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //validate the fields
    boolean validate(){
        if(edtName.getText().toString().isEmpty()) {
            edtName.setError("name is required");
            return false;
        }
        if(edtEmail.getText().toString().isEmpty()) {
            edtEmail.setError("email is required");
            return false;
        }

        //if in edit mode check previous password
        if(user!=null){
            if(!edtOldPassword.getText().toString().contentEquals(user.password)) {
                edtOldPassword.setError("invalid old password");
                return false;
            }
        }

        if(edtPassword.getText().toString().isEmpty()) {
            edtPassword.setError("password is required");
            return false;
        }
        if(edtCfPassword.getText().toString().isEmpty()) {
            edtPassword.setError("password is required");
            return false;
        }
        //check if passwords match
        if(!edtPassword.getText().toString().contentEquals(edtCfPassword.getText().toString())){
            edtCfPassword.setError("Password does not match");
            return false;
        }


        return true;
    }
}