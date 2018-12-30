package com.example.kemal.testwork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class tinderActivity extends AppCompatActivity {
    Context context = this;
    private cards cards_data[];
    private arrayAdapter arrayAdapter;
    private int i;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    SharedPreferences shps;
    @BindViews(R.id.frame)
    SwipeFlingAdapterView flingContainer;
    private String currentUId;
    ListView listView;
    private FirebaseAuth mAuth;
    List<cards> rowItems;
    boolean kontrol = false;
    ArrayList<String> test;
    ArrayList<String> test1;
    ArrayList<String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinder);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        shps = this.getSharedPreferences("com.example.kemal.testwork", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        rowItems = new ArrayList<cards>();
        test = getIntent().getStringArrayListExtra("Liste");
        test1 = getIntent().getStringArrayListExtra("ListeId");
        user = getIntent().getStringArrayListExtra("ListeUser");
        for (int i = 0; i < test.size(); i++) {
            cards item = new cards(test.get(i), test1.get(i), user.get(i));//bu text
            rowItems.add(item);
        }
        init();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Uygulamadan çıkmak istiyormusunuz")
                    .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(tinderActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            tinderActivity.this.finish();
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

    public void init() {

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onLeftCardExit(Object dataObject) {
                Toast.makeText(tinderActivity.this, "Dislike", Toast.LENGTH_SHORT).show();
                FirebaseUser user = mAuth.getCurrentUser();
                UUID uuıd = UUID.randomUUID();
                String uuidString = uuıd.toString();
                myRef.child("userLog").child(uuidString).child("event").setValue("Dislike");
                myRef.child("userLog").child(uuidString).child("imageInfo").setValue(dataObject);
                myRef.child("userLog").child(uuidString).child(("likeMail")).setValue(user.getEmail());
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(tinderActivity.this, "Like", Toast.LENGTH_SHORT).show();
                FirebaseUser user = mAuth.getCurrentUser();
                UUID uuıd = UUID.randomUUID();
                String uuidString = uuıd.toString();
                myRef.child("userLog").child(uuidString).child("event").setValue("Like");
                myRef.child("userLog").child(uuidString).child("imageInfo").setValue(dataObject);
                myRef.child("userLog").child(uuidString).child(("likeMail")).setValue(user.getEmail());
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                //  Toast.makeText(tinderActivity.this, "been boşum", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                //  Toast.makeText(tinderActivity.this, "been scrolum", Toast.LENGTH_SHORT).show();
            }
        });
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //   Toast.makeText(tinderActivity.this, "Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnTekrar(View view) {
        Intent intent = new Intent(getApplicationContext(), tinderActivity.class);
        intent.putStringArrayListExtra("Liste", (ArrayList<String>) test);
        intent.putStringArrayListExtra("ListeId", (ArrayList<String>) test);
        intent.putStringArrayListExtra("ListeUser", (ArrayList<String>) user);

        startActivity(intent);
    }

    public void btnLeft(View view) {
        flingContainer.getTopCardListener().selectLeft();
    }

    public void btnRight(View view) {
        flingContainer.getTopCardListener().selectRight();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//menyü bağladığımız yer
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_image, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//menü seçilince
        Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

}