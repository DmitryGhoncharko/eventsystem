package com.example.shopshoesspring.controller;

import com.example.shopshoesspring.entity.Mr;
import com.example.shopshoesspring.entity.MrInfo;
import com.example.shopshoesspring.entity.MrUser;
import com.example.shopshoesspring.entity.User;
import com.example.shopshoesspring.repository.*;
import com.example.shopshoesspring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/client")
public class ClientController {

    private final MrTypeRepository mrTypeRepository;
    private final MrRepository mrRepository;
    private final MrUserRepository mrUserRepository;
    private final UserRepository userRepository;
    private final MrInfoRepository mrInfoRepository;
    @GetMapping("/home")
    public String homePage() {
        return "cleint/сhome";
    }

    @GetMapping("/mrList")
    public String mrListPage(Model model) {
        model.addAttribute("mrType", mrTypeRepository.findAll());
        return "cleint/mrList";
    }
    @GetMapping("/mrListType/{typeId}")
    public String mrListByType(@PathVariable Long typeId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> userOptional = userRepository.findUserByUserLogin(currentUsername);
        User user = userOptional.get();
        List<Mr> mrList =  mrRepository.findByMrTypeId(typeId);
        List<MrUser> mrUserList = mrUserRepository.findAll();
        List<Mr> mrs = new ArrayList<>();

        for(Mr mr : mrList) {
            boolean flag = true;
            for(MrUser mrUser : mrUserList) {
                if(mr.getId().equals(mrUser.getMr().getId()) && mrUser.getUser().getId().equals(user.getId())) {
                    flag = false;
                }
            }
            if(flag) {
                mrs.add(mr);
            }
        }
        model.addAttribute("mrList",mrs);
        return "cleint/mrListByType";
    }
    @PostMapping("/submitRequest")
    public String submitRequest(@RequestParam("mrId") Long mrId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> userOptional = userRepository.findUserByUserLogin(currentUsername);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            MrUser mrUser = new MrUser();
            mrUser.setUser(user);
            Optional<Mr> mrOptional = mrRepository.findById(mrId);
            mrUser.setMr(mrOptional.get());
            mrUser.setRequestAccepted(false);
            mrUserRepository.save(mrUser);
        }
        return "redirect:/client/mrList";
    }
    @GetMapping("/requestList")
    public String requestsList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> userOptional = userRepository.findUserByUserLogin(currentUsername);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("mrUser",mrUserRepository.findByUserId(user.getId()));
        }
        return "cleint/requestsList";
    }
    @GetMapping("/comments/{id}")
    public String showCommentsPage(@PathVariable("id") Long mrId, Model model) {
        model.addAttribute("mrInfo", mrInfoRepository.findByMrId(mrId));
        model.addAttribute("id", mrId);
        return "cleint/comments";
    }
    @PostMapping("/addComment")
    public String addComment(@RequestParam("mrId") Long mrId, @RequestParam("comment") String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> userOptional = userRepository.findUserByUserLogin(currentUsername);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Mr> mrOptional = mrRepository.findById(mrId);
            Mr mr = mrOptional.get();
            MrInfo mrInfo = new MrInfo();
            mrInfo.setMrUser(user);
            mrInfo.setMr(mr);
            mrInfo.setMrReview(comment);
            mrInfoRepository.save(mrInfo);
        }
        return "redirect:/client/comments/" + mrId;
    }
}