package com.cooksys.controller;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.dto.CredentialsProfileDto;
import com.cooksys.dto.UserAccountDto;
import com.cooksys.entity.Credentials;
import com.cooksys.entity.UserAccount;
import com.cooksys.service.UserService;

@RestController
@RequestMapping("users")
public class UserController {
	
	private UserService userService;
	
	public UserController(UserService userService)
	{
		this.userService = userService;
	}
	
	@GetMapping
	public Set<UserAccountDto> getUsers()
	{
		return userService.getUsers();
	}
	
	@PostMapping
	public UserAccountDto createUser(@RequestBody CredentialsProfileDto credentialsProfileDto, HttpServletResponse response)
	{
		UserAccountDto uADto = userService.createUser(credentialsProfileDto);
		if (uADto == null)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_OK);
		}
		
		return uADto;
	}
	
	@PatchMapping("@{username}")
	public UserAccountDto updateProfile(@PathVariable String username, @RequestBody CredentialsProfileDto credentialsProfileDto, HttpServletResponse response)
	{
		UserAccountDto uADto = userService.updateProfile(username, credentialsProfileDto);
		
		if (uADto == null)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_OK);
		}
		
		return uADto;
	}
	
	@GetMapping("@{username}")
	public UserAccountDto getUser(@PathVariable String username, HttpServletResponse response)
	{
		UserAccountDto uADto = userService.getUser(username);
		
		if (uADto == null)
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_OK);
		}
		
		return uADto;
	}

	@DeleteMapping("@{username}")
	public UserAccountDto deleteUser(@PathVariable String username, @RequestBody Credentials credentials, HttpServletResponse response)
	{
		UserAccountDto uADto = userService.deleteUser(username, credentials);
		
		if (uADto == null)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_OK);
		}
		
		return uADto;
	}
	
	@PostMapping("@{username}/follow")
	public void followUser(@PathVariable String username, @RequestBody Credentials credentialsOfFollower, HttpServletResponse response)
	{
		response.setStatus(userService.followUser(username, credentialsOfFollower) ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@PostMapping("@{username}/unfollow")
	public void unfollowUser(@PathVariable String username, @RequestBody Credentials credentialsOfFollower, HttpServletResponse response)
	{
		response.setStatus(userService.unfollowUser(username, credentialsOfFollower) ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
	}
}
