package br.com.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.api.model.Cliente;
import br.com.api.service.ClienteService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("clientes")
@Tag(name = "Users Controller", description = "RESTful API for managing customer.")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@GetMapping
	@Operation(summary = "Get all customers", description = "Retrieve a list of all registered customers")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation successful")
	})
	public ResponseEntity<Iterable<Cliente>> buscarTodos() {
		return ResponseEntity.status(HttpStatus.OK).body(clienteService.buscarTodos());
	}

	@GetMapping("/{id}/cliente")
	@Operation(summary = "Get a customer by ID", description = "Retrieve a specific customer based on its ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation successful"),
			@ApiResponse(responseCode = "404", description = "User not found")
	})
	public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(clienteService.buscarPorId(id));
	}

	@PostMapping
	@Operation(summary = "Create a new customer", description = "Create a new user and return the created customers data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Customer created successfully")})
	public ResponseEntity<Cliente> inserir(@RequestBody @Valid Cliente cliente) {
		clienteService.inserir(cliente);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(cliente.getId())
				.toUri();
		return ResponseEntity.created(location).body(cliente);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a customer", description = "Update the data of an existing customer based on its ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Customer updated successfully"),
			@ApiResponse(responseCode = "404", description = "Customer not found"),
			@ApiResponse(responseCode = "422", description = "Invalid customer data provided")
	})
	public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
		clienteService.atualizar(id, cliente);
		return ResponseEntity.ok(cliente);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a customer", description = "Delete an existing customer based on its ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Customer not found")
	})
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		clienteService.deletar(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
