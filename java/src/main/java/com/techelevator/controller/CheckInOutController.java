package com.techelevator.controller;

import com.techelevator.model.UserCheckInStatus;
import com.techelevator.service.CheckInOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gym-visit")
@CrossOrigin(

        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowedHeaders = "*",
        allowCredentials = "true"
)
public class CheckInOutController {

    private final CheckInOutService checkInOutService;

    @Autowired
    public CheckInOutController(CheckInOutService checkInOutService) {
        this.checkInOutService = checkInOutService;
    }


    @PostMapping("/check-in")
    public ResponseEntity<Void> checkInUser(Principal principal) {
        String username = principal.getName();
        int userId = checkInOutService.getUserIdByUsername(username);
        checkInOutService.checkInUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/check-out")
    public ResponseEntity<Void> checkOutUser(Principal principal) {
        String username = principal.getName();
        int userId = checkInOutService.getUserIdByUsername(username);
        checkInOutService.checkOutUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/average-time")
    public ResponseEntity<String> getAverageTime(Principal principal) {
        String username = principal.getName();
        int userId = checkInOutService.getUserIdByUsername(username);

        String averageTime = checkInOutService.calculateAverageTime(userId);
        return ResponseEntity.ok(averageTime);
    }

    @GetMapping("/total-time")
    public ResponseEntity<String> getTotalTime(Principal principal) {
        String username = principal.getName();
        int userId = checkInOutService.getUserIdByUsername(username);
        String totalTime = checkInOutService.calculateTotalTime(userId);
        return ResponseEntity.ok(totalTime);
    }

    @GetMapping("/check-in-status")
    public ResponseEntity<Map<String, Boolean>> getCheckInStatus(Principal principal) {
        String username = principal.getName();
        int userId = checkInOutService.getUserIdByUsername(username);

        boolean isCheckedIn = checkInOutService.isUserCheckedIn(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("checkedIn", isCheckedIn);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-session-time")
    public ResponseEntity<String> getCurrentSessionTime(Principal principal) {
        String username = principal.getName();
        int userId = checkInOutService.getUserIdByUsername(username);

        LocalDateTime checkInTime = checkInOutService.getOngoingCheckInTime(userId);
        if (checkInTime == null) {
            return ResponseEntity.ok("Not Checked In");
        }

        return ResponseEntity.ok(checkInTime.toString());
    }

    @GetMapping("/check-in-dates")
    public ResponseEntity<List<LocalDate>> getCheckInDates(Principal principal) {
        String username = principal.getName();
        int userId = checkInOutService.getUserIdByUsername(username);

        List<LocalDate> checkInDates = checkInOutService.getCheckInDates(userId);
        return ResponseEntity.ok(checkInDates);
    }

    @GetMapping("/checked-in-users")
    public ResponseEntity<List<UserCheckInStatus>> getCheckedInUsers() {
        List<UserCheckInStatus> users = checkInOutService.getCheckedInUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<Void> checkOutUser(@PathVariable int userId) {
        checkInOutService.checkOutUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/checkin")
    public ResponseEntity<Void> checkInUser(@PathVariable int userId) {
        checkInOutService.checkInUser(userId);
        return ResponseEntity.ok().build();
    }
}
