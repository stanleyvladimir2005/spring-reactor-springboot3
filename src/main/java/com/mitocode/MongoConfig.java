package com.mitocode;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

//CLase para quitar el atributo class al ingresar en las colecciones en mongoDB
@Configuration
public class MongoConfig implements InitializingBean{

	@Autowired
	@Lazy
	private MappingMongoConverter mappingMongoConverter;

	@Override
	public void afterPropertiesSet() {
		mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
	}
}