package facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import dtos.BankDto;
import dtos.BranchDto;
import dtos.BranchDtoWithToken;
import models.Bank;
import models.Branches;
import ninja.utils.NinjaProperties;

public class BankFacadeImpl implements BankFacade{

	@Inject
	Provider<EntityManager> entityManagerProvider;
	@Inject
	NinjaProperties ninjaProperties;
	@Inject
	BankFacade self;
	
	@Override
	@Transactional
	public BankDto getBankDetail(String ifscCode) {
		try{
			
		EntityManager em = entityManagerProvider.get();
		List<Branches> branch = em.createQuery("Select x from Branches x where ifsc = :ifsc",Branches.class)
							.setParameter("ifsc",ifscCode)
							.getResultList();
		
		if(branch==null || branch.size()!=1) {
			return null;
		}
		Branches branchToGet = branch.get(0);
		BankDto bankDto = new BankDto();
		bankDto.setName(branchToGet.getBank().getName());
		bankDto.setBranch(branchToGet.getBranch());
		bankDto.setAddress(branchToGet.getAddress());
		bankDto.setIfsc(branchToGet.getIfsc());
		bankDto.setState(branchToGet.getState());
		bankDto.setCity(branchToGet.getCity());
		System.out.println(bankDto);
		String token = self.createToken(Integer.toString(branchToGet.getId()),1000*60*60*24*5);
		bankDto.setToken(token);
		return bankDto;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


	@Override
	@Transactional
	public BranchDtoWithToken getBranches(String name,String city,int page,int pageSize){
		
		EntityManager em = entityManagerProvider.get();
		try{	
			Bank bank = em.createQuery("Select x from Bank x where x.name=:name",Bank.class)
							.setParameter("name",name)
							.getSingleResult();
			System.out.println(bank.getId());
			List<Branches> branches;
			if(pageSize==0 || page==0){
				branches = em.createQuery("Select b from Branches b where b.city=:city and b.bank.id= :id",Branches.class)
										.setParameter("city", city)
										.setParameter("id",bank.getId())
										.getResultList();
			}else{
				branches = em.createQuery("Select b from Branches b where b.city=:city and b.bank.id= :id order by b.id",Branches.class)
						.setParameter("city", city)
						.setParameter("id",bank.getId())
						.setFirstResult((page-1)*pageSize)
						.setMaxResults(pageSize)
						.getResultList();
			}
		/*List<Object> list = em.createQuery("Select b from Branches b Inner Join b.bank as ba where ba.name= :name",Object.class)
								.setParameter("name", name)
								//.setParameter("city", city)
								.getResultList();*/
		if(branches==null || branches.size()==0){
			return null;
		}
		List<BranchDto> branchesList = new ArrayList<>();
		for(Branches b:branches){
			BranchDto bx = new BranchDto();
			bx.setIfsc(b.getIfsc());
			bx.setCity(b.getCity());
			bx.setAddress(b.getAddress());
			bx.setState(b.getState());
			bx.setBranch(b.getBranch());
			branchesList.add(bx);
		}
		BranchDtoWithToken tokenBranches = new BranchDtoWithToken();
		tokenBranches.setList(branchesList);
		tokenBranches.setToken(self.createToken("1",1000*60*60*24*5));
		return tokenBranches;
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			return null;
		}
	}
	
	 @Override
	 public String createToken(String id,long milliSeconds){
		 try {
			 	String secret = ninjaProperties.get("application.secret");
			 	Date date = new Date();
			 	date.setTime(date.getTime()+milliSeconds);
			    Algorithm algorithm = Algorithm.HMAC256(secret);
			    String token = JWT.create()
			    			.withIssuer("auth0")
			    			.withClaim("id",id)
			    			.withExpiresAt(date)
			    			.sign(algorithm);
			    System.out.println(token);
			    return token;
			} catch (JWTCreationException exception){
				return null;
			}
	    }
}
