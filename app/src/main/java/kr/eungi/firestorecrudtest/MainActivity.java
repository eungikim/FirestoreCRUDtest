package kr.eungi.firestorecrudtest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import static kr.eungi.firestorecrudtest.util.Constant.DIALOG_FLAG_ADD;
import static kr.eungi.firestorecrudtest.util.Constant.DIALOG_FLAG_MODIFY;

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
                showDataControlDialog(DIALOG_FLAG_MODIFY, position);
            }
        });
        mNameRecyclerView.setAdapter(mNameAdapter);

        mAddNewButton.setOnClickListener(v -> {
            showDataControlDialog(DIALOG_FLAG_ADD, -1);
        });

        readFirestoreData();

    }

    private void showDataControlDialog(int flag, int selectedPostion) {
        ControlDataDialog dialog = new ControlDataDialog(flag, selectedPostion);
        dialog.setClickListener(mDialogClickListener);
        dialog.show(getSupportFragmentManager(), ControlDataDialog.TAG);
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
                        readFirestoreData();
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
                            NameRepository repo = NameRepository.getInstance();
                            repo.resetNameList();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
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

    private void updateFirestoreData(String name, String documentId) {
        DocumentReference nameDocumentRef =
                mFirestoreDb.collection(DB_COLLECTION_NAME).document(documentId);

        nameDocumentRef
                .update(DB_FIELD_NAME, name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        readFirestoreData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void deleteFirestoreData(String documentId) {
        mFirestoreDb.collection(DB_COLLECTION_NAME).document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        readFirestoreData();
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

    interface DialogClickListener {
        void onAddClickListener(String name);
        void onAddRandomClickListener();
        void onUpdateClickListener(String name, int position);
        void onDeleteClickListener(int position);
    }

    DialogClickListener mDialogClickListener = new DialogClickListener() {
        @Override
        public void onAddClickListener(String name) {
            writeFirestoreData(name);
        }

        @Override
        public void onAddRandomClickListener() {
            writeFirestoreData(generateName());
        }

        @Override
        public void onUpdateClickListener(String name, int position) {
            String documentId = NameRepository.getInstance().getNameList().get(position).getDocumentId();
            updateFirestoreData(name, documentId);
        }

        @Override
        public void onDeleteClickListener(int position) {
            String documentId = NameRepository.getInstance().getNameList().get(position).getDocumentId();
            deleteFirestoreData(documentId);
        }
    };
}
