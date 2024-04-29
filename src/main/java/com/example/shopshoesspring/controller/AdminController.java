package com.example.shopshoesspring.controller;


import com.example.shopshoesspring.entity.*;
import com.example.shopshoesspring.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final MrTypeRepository mrTypeRepository;
    private final MrRepository mrRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BannedUserRepository bannedUserRepository;
    private final MrUserRepository mrUserRepository;
    @GetMapping("/home")
    public String homePage() {
        return "admin/ahome";
    }
    @GetMapping("/addType")
    public String addTypePage() {
        return "admin/addType";
    }
    @GetMapping("/mrTypeList")
    public String mrTypeListPage(Model model) {
        model.addAttribute("mrTypeList", mrTypeRepository.findAll());
        return "admin/mrTypeList";
    }
    @PostMapping("/addType")
    public String addType(String mrTypeName) {
        MrType mrType = new MrType();
        mrType.setMrTypeName(mrTypeName);
        mrTypeRepository.save(mrType);
        return "redirect:/admin/mrTypeList";
    }

    @GetMapping("/updateType/{id}")
    public String updateType(@PathVariable Long id, Model model) {
        Optional<MrType> mrType = mrTypeRepository.findById(id);
        if (mrType.isPresent()) {
            model.addAttribute("mrType", mrType.get());
        }
        return "admin/updateMrType";
    }

    @GetMapping("/deleteType/{id}")
    public String deleteType(@PathVariable Long id) {
        mrTypeRepository.deleteById(id);
        return "redirect:/admin/mrTypeList";
    }

    @PostMapping("/updateType/{id}")
    public String updateType(@PathVariable Long id, @ModelAttribute MrType updatedMrType) {
        MrType mrTypeToUpdate = mrTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid mrType Id:" + id));
        mrTypeToUpdate.setMrTypeName(updatedMrType.getMrTypeName());
        mrTypeRepository.save(mrTypeToUpdate);
        return "redirect:/admin/mrTypeList";
    }
    @GetMapping("/addMr")
    public String addMrPage(Model model) {
        model.addAttribute("mrTypeList", mrTypeRepository.findAll());
        return "admin/addMr";
    }
    @PostMapping("/addMr")
    public String addMr(@RequestParam("mrName") String mrName,
                        @RequestParam("mrType") Long mrTypeId,
                        @RequestParam("mrDescription") String mrDescription,
                        @RequestParam("mrDateStart") String mrDateStartString,
                        @RequestParam("mrDateEnd") String mrDateEndString) {
        MrType mrType = mrTypeRepository.findById(mrTypeId).orElseThrow(() -> new IllegalArgumentException("Invalid mrType Id"));

        Instant mrDateStart = LocalDate.parse(mrDateStartString).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant mrDateEnd = LocalDate.parse(mrDateEndString).atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mr mr = new Mr();
        mr.setMrName(mrName);
        mr.setMrType(mrType);
        mr.setMrDescription(mrDescription);
        mr.setMrDateStart(mrDateStart);
        mr.setMrDateEnd(mrDateEnd);
        mrRepository.save(mr);

        return "redirect:/admin/mrList";
    }

    @GetMapping("/mrList")
    public String mrListPage(Model model) {
        model.addAttribute("mrList", mrRepository.findAll());
        return "admin/mrList";
    }

    @GetMapping("/updateMr/{id}")
    public String updateMr(@PathVariable Long id, Model model) {
        Optional<Mr> mr = mrRepository.findById(id);
        if (mr.isPresent()) {
            model.addAttribute("mrTypes", mrTypeRepository.findAll());
            model.addAttribute("mr", mr.get());
        }
        return "admin/mrEdit";
    }
    @GetMapping("/deleteMr/{id}")
    public String deleteMr(@PathVariable Long id) {
        mrRepository.deleteById(id);
        return "redirect:/admin/mrList";
    }

    @PostMapping("/updateMr")
    public String updateMr(@RequestParam("id") Long id,
                           @RequestParam("mrName") String mrName,
                           @RequestParam("mrType.id") Long mrTypeId,
                           @RequestParam("mrDescription") String mrDescription,
                           @RequestParam("mrDateStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate mrDateStart,
                           @RequestParam("mrDateEnd") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate mrDateEnd) {
        Mr existingMr = mrRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + id));

        existingMr.setMrName(mrName);
        existingMr.setMrType(mrTypeRepository.findById(mrTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event type Id:" + mrTypeId)));
        existingMr.setMrDescription(mrDescription);
        existingMr.setMrDateStart(mrDateStart.atStartOfDay().toInstant(ZoneOffset.UTC));
        existingMr.setMrDateEnd(mrDateEnd.atStartOfDay().toInstant(ZoneOffset.UTC));

        mrRepository.save(existingMr);
        return "redirect:/admin/mrList";
    }
    @GetMapping("/addUserPage")
    public String addUserPage() {
        return "admin/addUser";
    }
    @GetMapping("/usersListPage")
    public String usersListPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/userListPage";
    }
    @GetMapping("/bannedList")
    public String bannedList(Model model) {
        model.addAttribute("bannedUsers", bannedUserRepository.findAll());
        return "admin/bannedList";
    }
    @GetMapping("/banUser/{userId}")
    public String banUser(@PathVariable("userId") Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            BannedUser bannedUser = new BannedUser();
            bannedUser.setUser(user);
            bannedUserRepository.save(bannedUser);
        }
        return "redirect:/admin/usersListPage";
    }
    @GetMapping("/unbanUser/{userId}")
    @Transactional
    public String unbanUser(@PathVariable("userId") Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            bannedUserRepository.deleteByUserId(userId);
        }
        return "redirect:/admin/usersListPage";
    }
    @PostMapping("/addUser")
    public String addUser(@RequestParam("userLogin") String userLogin, @RequestParam("userPassword") String userPassword, @RequestParam("userRole") String userRole) {
        UserRole userRoleForCreate = null;
        if (userRole.equals("admin")) {
            userRoleForCreate = new UserRole(1L, "ADMIN");
        } else {
            userRoleForCreate = new UserRole(2L, "CLIENT");
        }
        User newUser = User.builder().userLogin(userLogin).userPassword(passwordEncoder.encode(userPassword)).userBalance(500.5).userRole(userRoleForCreate).build();

        userRepository.save(newUser);

        return "redirect:/admin/usersListPage";
    }
    @GetMapping("/requestsList")
    public String requestsList(Model model) {
        model.addAttribute("mrUser",mrUserRepository.findByRequestAcceptedFalse());
        return "admin/requestsList";
    }
    @GetMapping("/requestsListTrue")
    public String requestsListTrue(Model model) {
        model.addAttribute("mrUser",mrUserRepository.findByRequestAcceptedTrue());
        return "admin/requestsListTrue";
    }
    @PostMapping("/acceptRequest")
    public String acceptRequest(@RequestParam("id") Long id) {
        MrUser mrUser = mrUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid MR User Id:" + id));
        mrUser.setRequestAccepted(true);
        mrUserRepository.save(mrUser);
        return "redirect:/admin/requestsList";
    }
    @PostMapping("/removeRequest")
    public String removeRequest(@RequestParam("id") Long id) {
        MrUser mrUser = mrUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid MR User Id:" + id));
        mrUser.setRequestAccepted(false);
        mrUserRepository.save(mrUser);
        return "redirect:/admin/requestsList";
    }
}

