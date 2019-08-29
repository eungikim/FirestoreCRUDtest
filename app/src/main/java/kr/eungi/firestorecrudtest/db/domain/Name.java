package kr.eungi.firestorecrudtest.db.domain;

public class Name {

    private String mDocumentId;
    private String mName;

    public Name(String documentId, String name) {
        mDocumentId = documentId;
        mName = name;
    }

    @Override
    public String toString() {
        return "Name{" +
                "mDocumentId='" + mDocumentId + '\'' +
                ", mName='" + mName + '\'' +
                '}';
    }

    public String getDocumentId() {
        return mDocumentId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

}
