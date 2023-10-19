package br.com.api.service;

import br.com.api.model.Cliente;

/**
 * Interface que define o padrão <b>Strategy</b> no domínio de cliente.
 */
public interface ClienteService {

	Iterable<Cliente> buscarTodos();

	Cliente buscarPorId(Long id);

	Cliente inserir(Cliente cliente);

	Cliente atualizar(Long id, Cliente cliente);

	void deletar(Long id);

}
