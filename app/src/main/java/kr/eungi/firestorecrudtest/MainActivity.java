package kr.eungi.firestorecrudtest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.eungi.firestorecrudtest.db.FirebaseDbQuery;
import kr.eungi.firestorecrudtest.db.NameRepository;
import kr.eungi.firestorecrudtest.util.NameGenerator;

import static kr.eungi.firestorecrudtest.util.Constant.DIALOG_FLAG_ADD;
import static kr.eungi.firestorecrudtest.util.Constant.DIALOG_FLAG_MODIFY;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseDbQuery mFirebaseDbQuery;
    private NameAdapter mNameAdapter;

    @BindView(R.id.main_name_recycler_view) RecyclerView mNameRecyclerView;
    @BindView(R.id.main_add_new_floating_button) FloatingActionButton mAddNewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mNameAdapter = new NameAdapter();
        mNameAdapter.setListItemClickListener((view, position) ->
                showDataControlDialog(DIALOG_FLAG_MODIFY, position));
        mNameRecyclerView.setAdapter(mNameAdapter);

        mAddNewButton.setOnClickListener(v -> {
            showDataControlDialog(DIALOG_FLAG_ADD, -1);
        });

        mFirebaseDbQuery = new FirebaseDbQuery(new FirebaseDbQuery.OnReadListener() {
            @Override
            public void onReadListener() {
                updateList();
            }
        });

        mFirebaseDbQuery.readFirestoreData();

    }


    private void showDataControlDialog(int flag, int selectedPosition) {
        ControlDataDialog dialog = new ControlDataDialog(flag);
        dialog.setClickListener(mDialogClickListener);
        dialog.setSelectedPosition(selectedPosition);
        dialog.show(getSupportFragmentManager(), ControlDataDialog.TAG);
    }

    private void updateList() {
        mNameAdapter.notifyDataSetChanged();
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
            mFirebaseDbQuery.writeFirestoreData(name);
        }

        @Override
        public void onAddRandomClickListener() {
            mFirebaseDbQuery.writeFirestoreData(generateName());
        }

        @Override
        public void onUpdateClickListener(String name, int position) {
            String documentId = NameRepository.getInstance().getNameList().get(position).getDocumentId();
            mFirebaseDbQuery.updateFirestoreData(name, documentId);
        }

        @Override
        public void onDeleteClickListener(int position) {
            String documentId = NameRepository.getInstance().getNameList().get(position).getDocumentId();
            mFirebaseDbQuery.deleteFirestoreData(documentId);
        }
    };
}
