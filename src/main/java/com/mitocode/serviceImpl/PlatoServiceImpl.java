package com.mitocode.serviceImpl;

import com.mitocode.model.Plato;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IPlatoRepo;
import com.mitocode.service.IPlatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlatoServiceImpl extends CRUDImpl<Plato, String> implements IPlatoService {

	@Autowired
	private IPlatoRepo repo;

	@Override
	protected IGenericRepo<Plato, String> getRepo() {		
		return repo;
	}
}