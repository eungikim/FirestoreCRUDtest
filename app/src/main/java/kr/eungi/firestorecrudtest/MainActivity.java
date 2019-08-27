package kr.eungi.firestorecrudtest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.eungi.firestorecrudtest.db.NameRepository;
import kr.eungi.firestorecrudtest.db.domain.Name;
import kr.eungi.firestorecrudtest.util.NameGenerator;

import static kr.eungi.firestorecrudtest.db.Constant.DB_COLLECTION_NAME;
import static kr.eungi.firestorecrudtest.db.Constant.DB_FIELD_NAME;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseFirestore mFirestoreDb;
    private NameAdapter mNameAdapter;

    @BindView(R.id.main_name_recycler_view) RecyclerView mNameRecyclerView;
    @BindView(R.id.main_add_new_floating_button) FloatingActionButton mAddNewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Access a Cloud Firestore instance from your Activity
        mFirestoreDb = FirebaseFirestore.getInstance();

        mNameAdapter = new NameAdapter();
        mNameAdapter.setListItemClickListener(new NameAdapter.OnListItemClickListener() {
            @Override
            public void onListItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, "Item Click position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mNameRecyclerView.setAdapter(mNameAdapter);
        
        mAddNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControlDataDialog dialog = new ControlDataDialog(0);
                dialog.setClickListener(new DialogClickListener() {
                    @Override
                    public void onAddClickListener(String name) {
                        Toast.makeText(MainActivity.this, "name", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAddRandomClickListener() {

                    }

                    @Override
                    public void onUpdateClickListener(String name) {

                    }

                    @Override
                    public void onDeleteClickListener() {

                    }
                });
                dialog.show(getSupportFragmentManager(), ControlDataDialog.TAG);
            }
        });

        readFirestoreData();

    }

    interface DialogClickListener {
        void onAddClickListener(String name);
        void onAddRandomClickListener();
        void onUpdateClickListener(String name);
        void onDeleteClickListener();
    }

    private void updateList() {
        mNameAdapter.notifyDataSetChanged();
    }

    private void writeFirestoreData(String addedName) {
        // Create a new nameSet with a first and last Name
        Map<String, String> nameSet = new HashMap<>();
        nameSet.put(DB_FIELD_NAME, addedName);

        // Add a new document with a generated ID
        mFirestoreDb.collection(DB_COLLECTION_NAME)
                .add(nameSet)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        NameRepository repo = NameRepository.getInstance();
                        Name addedName = new Name(documentReference.getId(), nameSet.get(DB_FIELD_NAME));
                        repo.addName(addedName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void readFirestoreData() {
        mFirestoreDb.collection(DB_COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                NameRepository repo = NameRepository.getInstance();
                                Name newName = new Name(document.getId(), document.getString(DB_FIELD_NAME));
                                repo.addName(newName);
                                updateList();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void setFirestoreData() {
        Map<String, String> nameSet = new HashMap<>();
        nameSet.put(DB_FIELD_NAME, "Is All Thing Good");

        // If the document does not exist, it will be created.
        // If the document does exist, its contents will be overwritten with the newly provided data.
        // It has option parameter with 'set' method.
        mFirestoreDb.collection(DB_COLLECTION_NAME).document(NameRepository.getInstance().getNameList().get(0).getDocumentId())
                .set(nameSet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void updateFirestoreData(String name) {
        String documentId = NameRepository.getInstance().findDocIdByName(name);
        DocumentReference nameDocumentRef =
                mFirestoreDb.collection(DB_COLLECTION_NAME).document(documentId);

        nameDocumentRef
                .update(DB_FIELD_NAME, name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void deleteFirestoreData() {
        mFirestoreDb.collection(DB_COLLECTION_NAME).document(NameRepository.getInstance().getNameList().get(0).getDocumentId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

    private String generateName() {
        return NameGenerator.generateName();
    }
}
