package com.mitocode.serviceImpl;

import com.mitocode.model.Dish;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IDishRepo;
import com.mitocode.service.IDishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends CRUDImpl<Dish, String> implements IDishService {

	private final IDishRepo repo;

	@Override
	protected IGenericRepo<Dish, String> getRepo() {
		return repo;
	}
}