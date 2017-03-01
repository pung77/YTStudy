package common;

import java.io.Serializable;
import java.util.ArrayList;

public class UserList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int type_UPDATE = 1;
	
	private int userListType;
	private ArrayList<String> userList;
	
	public UserList(int type, ArrayList<String> userList)
	{
		this.userListType = type;
		this.userList = userList;
	}
	
	public int getUserListType()
	{
		return userListType;
	}
	
	public ArrayList<String> getUserList()
	{
		return userList;
	}
}
