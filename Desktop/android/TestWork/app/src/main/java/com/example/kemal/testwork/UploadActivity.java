package com.example.kemal.testwork;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private StorageReference mStoregeRef;
    Uri selectedImage;
    ImageView postImage;
    EditText postCommmanText;
    SharedPreferences shps;
    private ArrayList<String> Liste;
    private ArrayList<String> ListeId;
    private ArrayList<String> ListeUser;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Liste = new ArrayList<String>();
        ListeId = new ArrayList<String>();
        ListeUser = new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        mStoregeRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        postCommmanText = findViewById(R.id.txtComment);
        postImage = findViewById(R.id.postImageView);
        shps = this.getSharedPreferences("com.example.kemal.testwork", Context.MODE_PRIVATE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Uygulamadan çıkmak istiyormusunuz")
                    .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(UploadActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            UploadActivity.this.finish();
                        }
                    }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            }).create().show();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void btnUpload(View view) {
        postCommmanText.setText("Resim yükleniyor");
        UUID uuıd = UUID.randomUUID();
        final String imageName = "images/" + uuıd + ".jpg";
        StorageReference storageReference = mStoregeRef.child(imageName);
        storageReference.putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference newReference = FirebaseStorage.getInstance().getReference(imageName);
                newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) { //strage resim eklendikten sonra kullanıcıya ekleme
                        String downloadUrl = uri.toString();
                        String key = shps.getString("key", "");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(key).child("imageUrl");
                        databaseReference.setValue(downloadUrl);

                        user();
                    }
                });
            }

            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void user() {
        DatabaseReference databaseReference = firebaseDatabase.getReference("user");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean kontrol = false;
                String currentmail = mAuth.getCurrentUser().getEmail().toString();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();

                    String mail = hashMap.get("userEmail").toString();

                    if (mail.equals(currentmail)) {
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
                    startActivity(intent);
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String yasHesapla(String date) {
        int gecmis = Integer.valueOf(date.substring(6, date.length()));
        int gelecek = Calendar.getInstance().get(Calendar.YEAR);
        int sonuc = gelecek - gecmis;
        String Simdiki = Integer.toString(sonuc);
        return Simdiki;
    }

    public void selectImage(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) ;
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                postImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void init() {
        myRef = firebaseDatabase.getReference("user");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
