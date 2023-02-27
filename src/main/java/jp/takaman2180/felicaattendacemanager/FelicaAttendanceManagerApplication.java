package jp.takaman2180.felicaattendacemanager;


import jp.takaman2180.felicaattendacemanager.entity.Member;
import jp.takaman2180.felicaattendacemanager.entity.RequestStatus;
import jp.takaman2180.felicaattendacemanager.entity.ResultStatus;
import jp.takaman2180.felicaattendacemanager.services.MemberService;
import jp.takaman2180.felicaattendacemanager.services.RoomService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


@SpringBootApplication
@RestController
public class FelicaAttendanceManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FelicaAttendanceManagerApplication.class, args);
    }

    @GetMapping("/api/rooms/{roomId}/members")
    public String showMembers(@PathVariable int roomId) {
        String json;
        //JSON形式でルームidの一覧を返送する
        ArrayList<Member> memberArrayList;
        try {
            memberArrayList = RoomService.getMembers(roomId);

            Member[] array = memberArrayList.toArray(new Member[memberArrayList.size()]);
            ObjectMapper objectMapper = new ObjectMapper();

            json = objectMapper.writeValueAsString(array);
        } catch (Exception e) {
            e.printStackTrace();
            RequestStatus status = RequestStatus.ERROR;
            status.setStatusMsg(e.getMessage());
            return status.getStatusMsg();
        }

        return json;
    }

    @GetMapping("/api/rooms/show")
    public String showRooms() {
        String json = null;
        //JSON形式でルームidの一覧を返送する
        ArrayList<Integer> roomsIdmList = null;
        try {
            roomsIdmList = RoomService.getAllRoomId();

            Integer[] array = roomsIdmList.toArray(new Integer[roomsIdmList.size()]);
            ObjectMapper objectMapper = new ObjectMapper();

            json = objectMapper.writeValueAsString(array);
        } catch (Exception e) {
            e.printStackTrace();
            RequestStatus status = RequestStatus.ERROR;
            status.setStatusMsg(e.getMessage());
            return status.getStatusMsg();
        }

        return json;
    }

    @PostMapping("/api/rooms/makeRoom")
    public String makeRoom(@RequestParam(value = "roomId", defaultValue = "0") int roomId) {
        if (roomId == 0) {
            RequestStatus status = RequestStatus.BAD_REQUEST;
            status.setStatusMsg("roomId is null");
            return status.getStatusMsg();
        } else {
            boolean result = RoomService.checkRoomExist(roomId);
            if (!result) {
                RoomService.makeRoom(roomId);
                return RequestStatus.SUCCESS.getStatusMsg();
            } else {
                RequestStatus status = RequestStatus.ERROR;
                status.setStatusMsg(roomId + " is already exist");
                return status.getStatusMsg();
            }
        }
    }

    @PostMapping("/api/rooms/{roomId}/update")
    public String updateStatus(@PathVariable int roomId, @RequestParam(value = "idm", defaultValue = "") String idm) {
        if (idm.equals("")) {
            RequestStatus status = RequestStatus.BAD_REQUEST;
            status.setStatusMsg("Please specify idm in RequestParam");
            return status.getStatusMsg();
        } else {
            ResultStatus resultStatus;
            try {
                resultStatus = RoomService.updateStatus(roomId, idm);
            } catch (Exception e) {
                e.printStackTrace();
                RequestStatus status = RequestStatus.ERROR;
                status.setStatusMsg(e.getMessage());
                return status.getStatusMsg();
            }
            return resultStatus.getMsg();
        }
    }

    @GetMapping("/api/name/regist")
    public String registName(@RequestParam(value = "idm", defaultValue = "") String idm, @RequestParam(value = "name", defaultValue = "default Name") String name) {
        boolean result = false;
        String returnStr;

        try {
            result = MemberService.checkRegist(idm);
        } catch (Exception e) {
            e.printStackTrace();
            RequestStatus status = RequestStatus.ERROR;
            status.setStatusMsg(e.getMessage());
            return status.getStatusMsg();
        }

        if (result) {
            //見つかったときはすでに登録されている旨を返送
            returnStr = "Already exist";
        } else {
            //見つからなかったときは名前を登録する
            MemberService.registIdm(idm, name);
            returnStr = "Regist Name";
        }
        RequestStatus status = RequestStatus.SUCCESS;
        status.setStatusMsg(returnStr);
        return status.getStatusMsg();
    }

    @GetMapping("/api/name/get")
    public String getName(@RequestParam(value = "idm", defaultValue = "") String idm) {
        RequestStatus status;

        if (idm.equals("")) {
            status = RequestStatus.BAD_REQUEST;
            status.setStatusMsg("parameter is null");

        } else {
            String returnStr;
            try {
                returnStr = MemberService.getName(idm);
                status = RequestStatus.SUCCESS;
                status.setStatusMsg(returnStr);
            } catch (Exception e) {
                e.printStackTrace();
                status = RequestStatus.ERROR;
                status.setStatusMsg(e.getMessage());
            }
        }
        return status.getStatusMsg();
    }
}
