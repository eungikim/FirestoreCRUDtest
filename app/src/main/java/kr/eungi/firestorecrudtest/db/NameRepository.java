package kr.eungi.firestorecrudtest.db;

import java.util.ArrayList;
import java.util.List;

import kr.eungi.firestorecrudtest.db.domain.Name;

public class NameRepository {

    private List<Name> mNameList;

    private NameRepository() {
        mNameList = new ArrayList<>();
    }

    private static class NameRepositoryHolder {
        static final NameRepository INSTANCE = new NameRepository();
    }

    public static NameRepository getInstance() {
        return NameRepositoryHolder.INSTANCE;
    }

    public List<Name> getNameList() {
        return mNameList;
    }
}
