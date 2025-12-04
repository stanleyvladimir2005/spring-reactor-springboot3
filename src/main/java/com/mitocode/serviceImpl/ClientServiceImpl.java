package com.mitocode.serviceImpl;

import com.mitocode.model.Client;
import com.mitocode.repo.IClientRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl extends CRUDImpl<Client, String> implements IClientService {
	
	private final IClientRepo repo;

    @Override
	protected IGenericRepo<Client, String> getRepo() {
		return repo;
	}
}