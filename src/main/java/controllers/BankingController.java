package controllers;

import javax.inject.Inject;

import dtos.BankDto;
import dtos.BranchDtoWithToken;
import facade.BankFacade;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
public class BankingController {

    @Inject
    BankFacade bankFacade;

    public Result getBankDetail(@PathParam("ifsc") String ifsc){
        BankDto bankDto = bankFacade.getBankDetail(ifsc);
        if(bankDto==null){
        	System.out.println("Null Value");
            return Results.badRequest().json().render("Something bad Has Occured");
        }

        return Results.ok().json().render(bankDto);
    }
    
    
    public Result getBranchesByCity(@Param("name")String name,@Param("city")String city,@Param("page")int page,@Param("pageSize")int pageSize){
    	if(name==null || city==null ){
    		return Results.badRequest().json().render("Bad Request");
    	}
    	BranchDtoWithToken list = bankFacade.getBranches(name,city,page,pageSize);
    	
    	if(list.getList()==null){
    		return Results.badRequest().json().render("Something Bad has Occurred");
    	}
    	return Results.ok().json().render(list);
    }
    
   
}
