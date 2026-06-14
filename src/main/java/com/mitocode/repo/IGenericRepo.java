package com.mitocode.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean  //Le indicamos a Spring que no tome esta clase como bean 
public interface IGenericRepo<T, ID> extends ReactiveMongoRepository<T, ID>{
}