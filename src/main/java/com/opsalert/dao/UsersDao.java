package com.opsalert.dao;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Service;

import com.opsalert.domain.Users;
import com.opsalert.utils.UserMapper;

@Service
public class UsersDao {

	protected final static String baseDN = "cn=Manager,ou=Users,dc=opsalerts,dc=com";

	private LdapTemplate objLdapTemplate;

	public void setObjLdapTemplate(LdapTemplate objLdapTemplate) {
		this.objLdapTemplate = objLdapTemplate;
	}
	
	public Hashtable<String, String> getEnvironment() {
		String rootDN = "cn=Manager,ou=Users,dc=opsalerts,dc=com";
		String rootPWD = "password";
		
		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, "ldap://34.242.69.69:389/dc=opsalerts,dc=com");
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.SECURITY_PRINCIPAL, rootDN);
		environment.put(Context.SECURITY_CREDENTIALS, rootPWD);
		
		return environment;
	}
	
	public List<Users> getAllUsers() {
		List<Users> objUsersList = new ArrayList<Users>();
		UserMapper objUserMapper = new UserMapper();
		
		DirContext dirContext = null;
		NamingEnumeration<?> results = null;

		try {
			dirContext = new InitialDirContext(getEnvironment());
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			String filter = "(&(objectclass=inetOrgPerson))";
			results = dirContext.search("", filter, controls);

			while(results.hasMore()) {
				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();
				objUsersList.add(objUserMapper.mapFromAttributes(attributes));
			}
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (dirContext != null) {
				try {
					dirContext.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return objUsersList;
	}
	
	public boolean addUser(Users userToAdd) {
		boolean isUserAdded = true;

		DirContext dirContext = null;

		try {
			dirContext = new InitialDirContext(getEnvironment());
			final Attributes container = new BasicAttributes();

			// Create the objectclass to add
			final Attribute objClasses = new BasicAttribute("objectClass");
			objClasses.add("inetOrgPerson");

			// Assign the username, first name, and last name
			final Attribute firstName = new BasicAttribute("cn", userToAdd.getFirstName());
			final Attribute userName = new BasicAttribute("uid", userToAdd.getUsername());
			final Attribute lastName = new BasicAttribute("sn",userToAdd.getLastName());
			final Attribute userPassword = new BasicAttribute("userPassword", userToAdd.getUserPassword());

			container.put(objClasses);
			container.put(firstName);
			container.put(userName);
			container.put(lastName);
			container.put(userPassword);

			dirContext.createSubcontext(getUserDN(userToAdd.getUsername()), container);
		} catch (NamingException e) {
			isUserAdded = false;
			e.printStackTrace();
		} finally {
			if (dirContext != null) {
				try {
					dirContext.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return isUserAdded;
	}

	public boolean login(String username, String password) {
		boolean isUserAuthentic = false;
		
		DirContext dirContext = null;
		NamingEnumeration<?> results = null;
		
		try {
			dirContext = new InitialDirContext(getEnvironment());
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			
			String filter = "(&(objectclass=inetOrgPerson)(uid=" + username + ")(userPassword=" + password + "))";
			results = dirContext.search("", filter, controls);
			
			if (results.hasMore()) {
				System.out.println("User found");
				isUserAuthentic = true;
			} else {
				System.out.println("User not found");
				isUserAuthentic = false;
			}
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (dirContext != null) {
				try {
					dirContext.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return isUserAuthentic;
	}
	
	public boolean updateUser(String userName, String fieldToUpdate, String updatedValue) {
		boolean isUserUpdated = true;

		DirContext dirContext = null;

		try {
			dirContext = new InitialDirContext(getEnvironment());
			ModificationItem[] mods = new ModificationItem[1];

			Attribute mod0 = new BasicAttribute(fieldToUpdate, updatedValue);
			/* Attribute mod1 = new BasicAttribute("1", "AAA"); */

			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
			/* mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod1); */

			dirContext.modifyAttributes(getUserDN(userName), mods);

		} catch (NamingException e) {
			isUserUpdated = false;
			e.printStackTrace();
		} finally {

		}
		return isUserUpdated;
	}

	public List<Users> findUserByCommonName(String commonName) {
		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass", "inetOrgPerson"));
		andFilter.and(new EqualsFilter("cn", commonName));
		return objLdapTemplate.search("", andFilter.encode(), new UserMapper());
	}
	
	private static String getUserDN(final String userName) {
		String userDN = new StringBuffer().append("ldap://34.242.69.69:389/"+"cn=").append(userName).append(",ou=Users,dc=opsalerts,dc=com").toString();
		System.out.println(userDN);
		return userDN;
	}
}
