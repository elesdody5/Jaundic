package com.example.android.jaundice;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    // to send request to startActivity
    static final int RC_SIGN_IN = 1;
    static final int  RC_PHOTO_PICKER = 2;
    private static final String ANONYOMUS= "Anonymous";

    private static final String TAG = "main";

    private String mUsername;

    /*firebase Instance*/

    // to connect to database
    private FirebaseDatabase mFirebaseDatabase;
    // to refer to child in database
    private DatabaseReference mDatabaseImagesRefernce;
    // to get firebase Storage
    private FirebaseStorage mFirebaseStorage;
    // to refer to jaucdicePhoto folder
    private StorageReference mJaundicePhotoStorageReference;
    // to set Authentication for write or read in database
    private FirebaseAuth mFirebaseAuth;
    // to set a Listener when uer log in or log out
    private FirebaseAuth.AuthStateListener mFirebaseAuthStateListener;

    private boolean isopen= false;
     FloatingActionButton addButton ;
     FloatingActionButton cameraButton ;
     FloatingActionButton uploadButton;

     Animation FabOpen ;
     Animation FabClose ;
     Animation rotateCw ;
     Animation rotateAcw ;
     LinearLayout uploadLayout;
     LinearLayout cameraLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addButton = findViewById(R.id.add_button);
        cameraButton = findViewById(R.id.camera_button);
        uploadButton= findViewById(R.id.upload_button);
        uploadLayout= findViewById(R.id.upload_layout);
        cameraLayout= findViewById(R.id.camera_layout);
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
         rotateCw = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_cw);
         rotateAcw = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotat_anticw);

        /**/
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        // to create child Images in database
        mDatabaseImagesRefernce = mFirebaseDatabase.getReference().child("Images");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mJaundicePhotoStorageReference= mFirebaseStorage.getReference().child("jaundice_photos");
        mFirebaseAuth= FirebaseAuth.getInstance();
        mUsername = ANONYOMUS;

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isopen)
                {
                    closeButtons();
                }
                else
                {
                    openButtons();
                }

            }
        });
        // set listener to open the camera
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TakePictureIntent();

            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallary();

            }
        });

        mFirebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignInInitialize(user.getDisplayName());
                } else {
                    onSignOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()

                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),

                                            new AuthUI.IdpConfig.GoogleBuilder().build()
                                    ))
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };
    }



    private void closeButtons() {
        cameraLayout.startAnimation(FabClose);
        uploadLayout.startAnimation(FabClose);
        addButton.startAnimation(rotateAcw);
        cameraButton.setClickable(false);
        uploadButton.setClickable(false);
        isopen=false;
    }
    private void openButtons(){

        cameraLayout.startAnimation(FabOpen);
        uploadLayout.startAnimation(FabOpen);
        addButton.startAnimation(rotateCw);
        cameraButton.setClickable(true);
        cameraButton.setClickable(true);
        isopen=true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/jaundice_information_menu.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.jaundice_information_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        startActivity(new Intent(this, JaundiceInformation.class));


        return true;
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
       else if (requestCode==RC_PHOTO_PICKER&&resultCode==RESULT_OK)
        {
            Uri selectImageUri = data.getData();

            // to refer to photo in Jaundice_photos folder
            StorageReference mPhotoReference = mJaundicePhotoStorageReference.child(selectImageUri.getLastPathSegment());
            mPhotoReference.putFile(selectImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // to get Photo download Uri
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    // Set the download URL to the message box, so that the user can send it to the database
                    DatabaseImages image = new DatabaseImages(null,mUsername,downloadUri.toString());
                    mDatabaseImagesRefernce.push().setValue(image);

                    Toast.makeText(MainActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();




                }
            }).addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this,"Uploading Photo ...",Toast.LENGTH_LONG).show();
                }
            });


        }

    }
    @Override
    protected void onPause() {

        mFirebaseAuth.removeAuthStateListener(mFirebaseAuthStateListener);

        super.onPause();

    }

    @Override
    protected void onResume() {

        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListener);
        super.onResume();

    }
    private void TakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(takePictureIntent);

    }
    private void openGallary()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);

        startActivityForResult(Intent.createChooser(intent,"Complete Action Using"),RC_PHOTO_PICKER);

    }
    private void onSignInInitialize(String displayName) {
        mUsername=displayName;
    }
    private void onSignOutCleanUp(){
        mUsername=ANONYOMUS;
    }


}
