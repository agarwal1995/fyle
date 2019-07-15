package facade;


import dtos.BankDto;
import dtos.BranchDtoWithToken;

public interface BankFacade {
	BankDto getBankDetail(String ifscCode);
	BranchDtoWithToken getBranches(String bankName,String city,int page,int pageSize);
	String createToken(String id,long time);
}
