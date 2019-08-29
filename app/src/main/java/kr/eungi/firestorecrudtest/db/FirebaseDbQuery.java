package kr.eungi.firestorecrudtest.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import kr.eungi.firestorecrudtest.db.domain.Name;

import static kr.eungi.firestorecrudtest.db.Constant.DB_COLLECTION_NAME;
import static kr.eungi.firestorecrudtest.db.Constant.DB_FIELD_NAME;

public class FirebaseDbQuery {
    private static final String TAG = FirebaseDbQuery.class.getSimpleName();

    private FirebaseFirestore mFirestoreDb = FirebaseFirestore.getInstance();
    private OnReadListener mOnReadListener;

    public FirebaseDbQuery(OnReadListener onReadListener) {
        mOnReadListener = onReadListener;
    }

    public void writeFirestoreData(String addedName) {
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

    public void readFirestoreData() {
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
                                mOnReadListener.onReadListener();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    public void updateFirestoreData(String name, String documentId) {
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

    public void deleteFirestoreData(String documentId) {
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

    public void setFirestoreData() {
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

    public  interface OnReadListener {
        void onReadListener();
    }

}
