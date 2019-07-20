package com.opsalert.utils;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import com.opsalert.domain.Users;

public class UserMapper implements AttributesMapper<Users>{

	@Override
	public Users mapFromAttributes(Attributes attributes) throws NamingException {
		Users users = new Users();
		
		if(attributes.get("uid") != null) {
			users.setUsername(attributes.get("uid").get().toString());
		}
		
		if(attributes.get("cn") != null) {
			users.setFirstName(attributes.get("cn").get().toString());
		}
		
		if(attributes.get("sn") != null) {
			users.setLastName(attributes.get("sn").get().toString());
		}
		
		if(attributes.get("userPassword") != null) {
			users.setUserPassword(attributes.get("userPassword").get().toString());
		}
		
		return users;
	}
	
}
