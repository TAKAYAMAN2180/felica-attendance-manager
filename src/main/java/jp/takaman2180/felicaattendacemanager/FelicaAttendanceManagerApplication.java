package jp.takaman2180.felicaattendacemanager;


import jp.takaman2180.felicaattendacemanager.entity.Member;
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
            return "error";
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
            return "error";
        }

        return json;
    }

    @PostMapping("/api/rooms/makeRoom")
    public String makeRoom(@RequestParam(value = "roomId", defaultValue = "0") int roomId) {
        if (roomId == 0) {
            return "roomId is null";
        } else {
            boolean result = RoomService.checkRoomExist(roomId);
            if (result) {
                RoomService.makeRoom(roomId);
                return "success";
            } else {
                return roomId + " is already exist";
            }
        }
    }

    @PostMapping("/api/rooms/{roomId}/update")
    public String updateStatus(@PathVariable int roomId, @RequestParam(value = "idm", defaultValue = "") String idm) {
        if (idm.equals("")) {
            return "Please specify idm in RequestParam";
        } else {
            try {
                RoomService.updateStatus(roomId, idm);
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
            return "true";
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
            returnStr = "Error happen";
        }

        if (result) {
            //見つかったときはすでに登録されている旨を返送
            returnStr = "Already exist";
        } else {
            //見つからなかったときは名前を登録する
            MemberService.registIdm(idm, name);
            returnStr = "Regist Name";
        }
        return returnStr;
    }

    @GetMapping("/api/name/get")
    public String getName(@RequestParam(value = "idm", defaultValue = "") String idm) {
        String returnStr;

        if (idm.equals("")) {
            returnStr = "parameter is null";
        } else {
            try {
                returnStr = MemberService.getName(idm);
            } catch (Exception e) {
                e.printStackTrace();
                returnStr = "error";
            }
        }
        return returnStr;
    }

    @GetMapping("/test")
    public String test() {

        boolean result = RoomService.checkRoomExist(114);


        return String.valueOf(result);
    }


}
