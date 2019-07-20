package com.opsalert.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opsalert.dao.UsersDao;
import com.opsalert.domain.Users;

@RestController
public class HomeController {

    @Autowired
    UsersDao objUsersDao;

    @GetMapping("/")
    public String index() {
        return "login";
    }

    @GetMapping("/login")
    public boolean login(@RequestParam(name = "username", required = true) String username,
    		@RequestParam(name = "password", required = true)  String password) {
    	return objUsersDao.login(username, password);
    }
    
    @GetMapping("/getAllUsers")
    public List<Users> getAllUsers() {
    	return objUsersDao.getAllUsers();
    }
    
    @GetMapping("/addUser")
    public boolean addUser(@RequestParam(name = "username", required = true) String username,
    		@RequestParam(name = "firstName", required = true)  String firstName,
    		@RequestParam(name = "lastName", required = true)  String lastName,
    		@RequestParam(name = "password", required = true)  String password) {
    	Users userToAdd = new Users();
    	userToAdd.setFirstName(username);
    	userToAdd.setLastName(firstName);
    	userToAdd.setUsername(lastName);
    	userToAdd.setUserPassword(password);
    	return objUsersDao.addUser(userToAdd);
    }
    
    @GetMapping("/updateUser")
    public boolean updateUser(@RequestParam(name = "username", required = true) String username,
    		@RequestParam(name = "fieldtoupdate", required = true)  String fieldToUpdate, 
    		@RequestParam(name = "updatedValue", required = true)  String updatedValue  		) {
    	return objUsersDao.updateUser(username, fieldToUpdate, updatedValue);
    }
}