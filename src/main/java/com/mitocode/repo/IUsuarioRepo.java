package com.mitocode.repo;

import com.mitocode.model.Usuario;
import reactor.core.publisher.Mono;

public interface IUsuarioRepo extends IGenericRepo<Usuario, String>{
	
	//Se usa findOneByUsuario para evitar usar query para obtener el usuario
	Mono<Usuario> findOneByUsuario(String usuario);	
}