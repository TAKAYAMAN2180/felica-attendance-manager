package jp.takaman2180.felicaattendacemanager.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import jp.takaman2180.felicaattendacemanager.entity.Member;
import jp.takaman2180.felicaattendacemanager.entity.Name;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

@Service
public class MemberService {
    private static final String COLLECTION_NAME = "name";

    //idmの名前を検索する。idmがなければnullを返送する
    public static String getName(String idm) throws ExecutionException, InterruptedException {
        String returnValue = null;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        Iterable<DocumentReference> iterable = dbFirestore.collection(COLLECTION_NAME).listDocuments();
        Iterator<DocumentReference> iterator = iterable.iterator();


        while (iterator.hasNext()) {
            DocumentReference documentReference = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot documentSnapshot = future.get();


            String getIdm = documentSnapshot.getString("idm");
            if (getIdm.equals(idm)) {
                returnValue = documentSnapshot.getString("name");
            }

        }

        return returnValue;
    }

    public static void registIdm(String getIdm,String getName) {
        Name name = new Name();
        name.setValues(getIdm, getName);

        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> documentFuture = dbFirestore.collection(COLLECTION_NAME).document(getIdm).set(name);
    }

    public static boolean checkRegist(String idm) throws ExecutionException, InterruptedException {
        boolean hasFound = false;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        Iterable<DocumentReference> iterable = dbFirestore.collection(COLLECTION_NAME).listDocuments();
        Iterator<DocumentReference> iterator = iterable.iterator();


        while (iterator.hasNext()) {
            DocumentReference documentReference = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot documentSnapshot = future.get();


            String getIdm = documentSnapshot.getString("idm");
            if (getIdm.equals(idm)) {
                hasFound = true;
            }
        }

        return hasFound;


    }

}
