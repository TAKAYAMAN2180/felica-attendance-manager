package jp.takaman2180.felicaattendacemanager.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import jp.takaman2180.felicaattendacemanager.entity.Member;
import jp.takaman2180.felicaattendacemanager.entity.Room;
import jp.takaman2180.felicaattendacemanager.entity.ResultStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class RoomService {

    private static final String COLLECTION_NAME = "room";

    public static ArrayList<Integer> getAllRoomId() throws ExecutionException, InterruptedException {
        ArrayList<Integer> returnArray = new ArrayList<>();

        Firestore dbFirestore = FirestoreClient.getFirestore();
        Iterable<DocumentReference> documentReferences = dbFirestore.collection(COLLECTION_NAME).listDocuments();
        Iterator<DocumentReference> iterator = documentReferences.iterator();

        while (iterator.hasNext()) {
            DocumentReference documentReference = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot documentSnapshot = future.get();


            try {
                returnArray.add(((Long) documentSnapshot.get("room_id")).intValue());
            } catch (NullPointerException nullPointerException) {
                nullPointerException.printStackTrace();
                //nothing
            }
        }

        return returnArray;
    }

    public static ArrayList<Member> getMembers(int roomId) throws ExecutionException, InterruptedException {
        try {
            ArrayList<Member> memberArrayList = new ArrayList<>();

            Firestore dbFirestore = FirestoreClient.getFirestore();
            DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(String.valueOf(roomId));

            DocumentSnapshot documentSnapshot = documentReference.get().get();

            if (documentSnapshot.exists()) {
                CollectionReference collectionReference = documentReference.collection("member");
                Iterable<DocumentReference> iterable = collectionReference.listDocuments();
                Iterator<DocumentReference> iterator = iterable.iterator();

                while (iterator.hasNext()) {
                    DocumentReference reference = iterator.next();
                    ApiFuture<DocumentSnapshot> future = reference.get();
                    DocumentSnapshot snapshot = future.get();

                    Member member = snapshot.toObject(Member.class);
                    if (member.getIdm() != null) {
                        memberArrayList.add(member);
                    }
                }

            }
            return memberArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //room???????????????????????????roomId?????????????????????????????????????????????????????????????????????????????????
    public static void makeRoom(int roomId) {
        Room room = new Room();
        room.setRoom_id(roomId);

        Firestore dbFirestore = FirestoreClient.getFirestore();

        //???????????????roomId????????????????????????????????????????????????????????????
        ApiFuture<WriteResult> documentReferences = dbFirestore.collection(COLLECTION_NAME).document(String.valueOf(roomId)).set(room);

        //????????????????????????????????????member???????????????
        DocumentReference membersRef = dbFirestore.collection(COLLECTION_NAME).document(String.valueOf(roomId)).collection("member").document("demo");
        membersRef.set(new HashMap<>());

    }

    public static ResultStatus updateStatus(int roomId, String getIdm) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(String.valueOf(roomId));

        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        boolean isFound = false;


        if (document.exists()) {
            CollectionReference collectionReference = documentReference.collection("member");
            ApiFuture<QuerySnapshot> futureForSubCollection = collectionReference.get();
            List<QueryDocumentSnapshot> documentSnapshots = futureForSubCollection.get().getDocuments();

            for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {
                String tempIdm = documentSnapshot.getString("idm");
                try {
                    if (tempIdm.equals(getIdm)) {
                        isFound = true;
                        break;
                    }
                } catch (NullPointerException nullPointerException) {
                    //nothing
                }
            }

            if (isFound) {
                //????????????idm???????????????status??????????????????1???0???????????????
                DocumentReference documentReferenceForChild = documentReference.collection("member").document(getIdm);
                ApiFuture<DocumentSnapshot> futureForChild = documentReferenceForChild.get();
                DocumentSnapshot documentSnapshotForChild = futureForChild.get();

                boolean isEntry = Boolean.TRUE.equals(documentSnapshotForChild.getBoolean("is_entry"));
                ResultStatus resultStatus;

                if (isEntry) {
                    documentReferenceForChild.update("is_entry", false);
                    resultStatus = ResultStatus.EXIT;
                } else {
                    documentReferenceForChild.update("is_entry", true);
                    resultStatus = ResultStatus.ATTEND;
                }

                return resultStatus;
            } else {
                //??????idm??????????????????????????????????????????&?????????
                //???????????????idm?????????????????????????????????????????????????????????????????????????????????????????????
                Member member = new Member();
                member.setValues(getIdm, true);
                ApiFuture<WriteResult> resultApiFuture = documentReference.collection("member").document(getIdm).set(member);

                return ResultStatus.ATTEND;
            }

        }

        return null;
    }

    //?????????????????????????????????????????????false
    public static boolean checkRoomExist(int roomId) {
        ArrayList<Integer> roomsList;
        try {
            roomsList = getAllRoomId();
            return roomsList.contains(roomId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
