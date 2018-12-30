package com.example.kemal.testwork;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    EditText txtMail, txtPassword;
    SharedPreferences shps;
    private ArrayList<String> Liste;
    private ArrayList<String> ListeId;
    private ArrayList<String> ListeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Liste = new ArrayList<String>();
        ListeId = new ArrayList<String>();
        ListeUser = new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        txtMail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById((R.id.txtPassword));
        shps = this.getSharedPreferences("com.example.kemal.testwork", Context.MODE_PRIVATE);
    }

    public void goSignIn(View view) {
        String Mail = txtMail.getText().toString();
        String Sifre = txtPassword.getText().toString();
        if (Sifre.length() != 0 && Mail.length() != 0) {
            Liste.clear();
            ListeId.clear();
            mAuth.signInWithEmailAndPassword(txtMail.getText().toString(), txtPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {//giriş yapıldı
                                DatabaseReference databaseReference = firebaseDatabase.getReference("user");
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        boolean kontrol = false;
                                        //  Toast.makeText(SignInActivity.this,"veriler geldi ", Toast.LENGTH_SHORT).show();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();

                                            String mail = hashMap.get("userEmail").toString();
                                            String currentmail = txtMail.getText().toString();

                                            if (mail.equals(currentmail)) {
                                                shps.edit().putString("key", ds.getKey().toString()).apply();//key
                                                //  Toast.makeText(SignInActivity.this, "giriş yapıldııı".toString(), Toast.LENGTH_SHORT).show();
                                                kontrol = true;
                                            } else {
                                                if (hashMap.get("imageUrl") != null) {
                                                    String yas = yasHesapla(hashMap.get("dateTime").toString());
                                                    Liste.add(hashMap.get("imageUrl").toString());
                                                    ListeId.add(ds.getKey().toString());
                                                    ListeUser.add(hashMap.get("userName").toString() + " , " + "   " + yas);
                                                }
                                            }
                                        }
                                        if (kontrol) {
                                            Intent intent = new Intent(getApplicationContext(), tinderActivity.class);
                                            intent.putStringArrayListExtra("Liste", (ArrayList<String>) Liste);
                                            intent.putStringArrayListExtra("ListeId", (ArrayList<String>) ListeId);
                                            intent.putStringArrayListExtra("ListeUser", (ArrayList<String>) ListeUser);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else {

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                            }

                            // ...
                        }

                        public void onFailure(@NonNull Exception e) {                           //hata meydana geldiğinde
                            // Toast.makeText(SignUpMainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            if (Mail.length() == 0) {
                txtMail.setError("Mail Alanı boş bırakılamaz");
            } else {
                txtMail.setError(null);
            }
            if (Sifre.length() == 0) {
                txtPassword.setError("Şifre Alanı boş bırakılamaz");
            } else {
                txtPassword.setError(null);
            }
        }

    }

    public String yasHesapla(String date) {
        int gecmis = Integer.valueOf(date.substring(6, date.length()));
        int gelecek = Calendar.getInstance().get(Calendar.YEAR);
        int sonuc = gelecek - gecmis;
        String Simdiki = Integer.toString(sonuc);
        return Simdiki;
    }

    public void goSignUp(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUpMainActivity.class);
        startActivity(intent);
    }
}
