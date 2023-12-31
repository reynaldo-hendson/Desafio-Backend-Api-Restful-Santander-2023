package br.com.api.service.impl;

import java.util.Optional;

import br.com.api.exception.EntidadeNaoEncontradaException;
import br.com.api.exception.NegocioException;
import br.com.api.service.ViaCepService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.api.model.Cliente;
import br.com.api.repository.ClienteRepository;
import br.com.api.model.Endereco;
import br.com.api.repository.EnderecoRepository;
import br.com.api.service.ClienteService;

@Service
@Slf4j

public class ClienteServiceImpl implements ClienteService {

	// Singleton: Injetar os componentes do Spring com @Autowired.
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;

	// Strategy: Implementar os métodos definidos na interface.
	// Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

	@Override
	public Iterable<Cliente> buscarTodos() {
		log.info("Buscar todos os Clientes.");
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		log.info("Buscando Cliente por id.");
		Optional<Cliente> cliente = Optional.ofNullable(clienteRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Cliente não encontrado.")));
		return cliente.get();
	}

	@Override
	public Cliente inserir(Cliente cliente) {
		log.info("Cadastrando cliente.");
		// Verifica se o email já está em uso.
		boolean emailEmUso = clienteRepository.findByEmail(cliente.getEmail())
				.stream()
				.anyMatch(clienteExistente -> !clienteExistente.equals(cliente));
		if(emailEmUso){
			throw new NegocioException("Email já consta na base de dados. Cadastre outro email.");
		}
		// Verifica se o cpf já está em uso.
		boolean cpfEmUso = clienteRepository.findByCpf(cliente.getCpf())
				.stream()
				.anyMatch(clienteExistente -> !clienteExistente.equals(cliente));
		if(cpfEmUso){
			throw new NegocioException("Cpf já consta na base de dados.");
		}
		// Verifica se o telefone já está em uso.
		boolean telefoneEmUso = clienteRepository.findByTelefone(cliente.getTelefone())
				.stream()
				.anyMatch(clienteExistente -> !clienteExistente.equals(cliente));
		if(telefoneEmUso){
			throw new NegocioException("Telefone já consta na base de dados. Cadastre outro email.");
		}
		salvarClienteComCep(cliente);
		return clienteRepository.save(cliente);
	}

	@Override
	public Cliente atualizar(Long id, Cliente cliente) {
		log.info("Buscando Cliente com id: " + id);
		Optional<Cliente> clienteExistente = clienteRepository.findById(id);
		if (clienteExistente.isEmpty()) {
			throw new NegocioException("Cliente não encontrado.");

		}else{
			salvarClienteComCep(cliente);
		}
		cliente.setId(id);
		return clienteRepository.save(cliente);

	}

	@Override
	@Transactional
	public void deletar(Long id) {
		log.info("Deletando cliente com id: " + id);
		this.buscarPorId(id);
		clienteRepository.deleteById(id);
	}

	private void salvarClienteComCep(Cliente cliente) {
		// Verificar se o Endereco do Cliente já existe (pelo CEP).
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e persistir o retorno.
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		clienteRepository.save(cliente);
	}

}
