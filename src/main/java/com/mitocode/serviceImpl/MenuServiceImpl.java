package com.mitocode.serviceImpl;

import com.mitocode.model.Menu;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IMenuRepo;
import com.mitocode.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends CRUDImpl<Menu, String> implements IMenuService{

	private final IMenuRepo repo;
	
	@Override
	protected IGenericRepo<Menu, String> getRepo() {		
		return repo; 
	}

	@Override
	public Flux<Menu> getMenus(String[] roles) {
		return repo.getMenus(roles);
	}
}