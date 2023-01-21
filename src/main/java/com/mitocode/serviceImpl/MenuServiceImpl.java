package com.mitocode.serviceImpl;

import com.mitocode.model.Menu;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IMenuRepo;
import com.mitocode.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class MenuServiceImpl extends CRUDImpl<Menu, String> implements IMenuService{

	@Autowired
	private IMenuRepo repo;
	
	@Override
	protected IGenericRepo<Menu, String> getRepo() {		
		return repo; 
	}

	@Override
	public Flux<Menu> getMenus(String[] roles) {
		return repo.getMenus(roles);
	}
}