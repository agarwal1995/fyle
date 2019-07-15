package dtos;

import java.util.List;

public class BranchDtoWithToken {
	String token;
	List<BranchDto> list;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public List<BranchDto> getList() {
		return list;
	}
	public void setList(List<BranchDto> list) {
		this.list = list;
	}
	
}
