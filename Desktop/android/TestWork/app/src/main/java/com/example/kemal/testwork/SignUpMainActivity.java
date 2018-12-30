package com.example.kemal.testwork;

import android.content.Intent;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.UUID;

public class SignUpMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText txtMail, txtPassword, txtName, txtSurname, txtConfirmPassword;//password gerek yok
    TextView txtDate;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private StorageReference mStoregeRef;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_main);


        txtName = (EditText) findViewById((R.id.txtName));
        txtSurname = (EditText) findViewById((R.id.txtSurname));
        txtMail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById((R.id.txtPassword));
        txtConfirmPassword = (EditText) findViewById((R.id.txtConfirmPassword));
        txtDate = (TextView) findViewById(R.id.txtDate);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        mStoregeRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    boolean tarihKotrol = false;

    public void btnDate(View v) {
        final Calendar takvim = Calendar.getInstance();
        int yil = takvim.get(Calendar.YEAR);
        int ay = takvim.get(Calendar.MONTH);
        int gun = takvim.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        month += 1;
                        if (year <= yil && ay <= month && dayOfMonth <= gun) {
                            txtDate.setText(dayOfMonth + "/" + month + "/" + year);
                            tarihKotrol = true;
                        } else {
                            txtDate.setText("gelecek zaman giremessiniz");
                            tarihKotrol = false;
                        }
                    }
                }, yil, ay, gun);

        dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", dpd);
        dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", dpd);
        dpd.show();
    }

    public void goSignUp(View view) {
        String Ad = txtName.getText().toString();
        String Soyad = txtSurname.getText().toString();
        String Mail = txtMail.getText().toString();
        String Sifre = txtPassword.getText().toString();
        String SifreTekrar = txtConfirmPassword.getText().toString();
        if (Sifre.length() != 0 && Mail.length() != 0 && Ad.length() != 0
                && Soyad.length() != 0 && SifreTekrar.length() != 0 && SifreTekrar.equals(Sifre)) {

            mAuth.createUserWithEmailAndPassword(txtMail.getText().toString(), txtPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) { //tamamlandığın
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String useremail = txtMail.getText().toString();
                                UUID uuıd = UUID.randomUUID();
                                String uuidString = uuıd.toString();
                                myRef.child("user").child(uuidString).child("userEmail").setValue(useremail);
                                myRef.child("user").child(uuidString).child("userName").setValue(txtName.getText().toString());
                                myRef.child("user").child(uuidString).child(("userSurname")).setValue(txtSurname.getText().toString());
                                if (tarihKotrol) {
                                    myRef.child("user").child(uuidString).child(("dateTime")).setValue(txtDate.getText().toString());
                                }
                                Toast.makeText(SignUpMainActivity.this, "giriş yapınız", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                        public void onF(@NonNull Exception e) {                           //hata meydana geldiğinde
                            Toast.makeText(SignUpMainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpMainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            if (Ad.length() == 0) {
                txtName.setError("ad Alanı boş bırakılamaz");
            } else {
                txtName.setError(null);
            }
            if (Soyad.length() == 0) {
                txtSurname.setError("soyad Alanı boş bırakılamaz");
            } else {
                txtSurname.setError(null);
            }
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
            if (SifreTekrar.length() == 0) {
                txtConfirmPassword.setError("Şifre Tekrar Alanı boş bırakılamaz");
            } else {
                txtConfirmPassword.setError(null);
            }
            if (!SifreTekrar.equals(Sifre)) {
                txtPassword.setError("Şifrler aynı değil");
            } else {
                txtPassword.setError(null);
            }
        }
    }

    public void goSignIn(View view) {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }
}
