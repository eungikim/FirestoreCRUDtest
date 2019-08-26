package kr.eungi.firestorecrudtest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.javafaker.Faker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static kr.eungi.firestorecrudtest.db.Constant.DB_COLLECTION_NAME;
import static kr.eungi.firestorecrudtest.db.Constant.DB_FIELD_NAME;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    FirebaseFirestore mFirestoreDb;

    @BindView(R.id.main_name_recycler_view) RecyclerView mNameRecyclerview;
    @BindView(R.id.main_add_new_floating_button) FloatingActionButton mAddNewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Access a Cloud Firestore instance from your Activity
        mFirestoreDb = FirebaseFirestore.getInstance();
        
        mAddNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeFirestroeData();
            }
        });

    }

    private void writeFirestroeData() {
        // Create a new user with a first and last Name
        Map<String, String> user = new HashMap<>();
        user.put(DB_FIELD_NAME, generateName());

        // Add a new document with a generated ID
        mFirestoreDb.collection(DB_COLLECTION_NAME)
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void readFirestroeData() {
        mFirestoreDb.collection(DB_COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private String generateName() {
//        Locale locale = new Locale("ko");
        Faker generater = new Faker(/*locale*/);
        return generater.name().fullName();
    }
}
