# Santander Backend 2023
Projeto API RestFul Java para Santander backend 2023.

## Diagrama de classes

```mermaid
classDiagram
  class Pessoa {
    +id: int
    +nome: string
    +sobrenome: string
    +cpf: string
    +email: string
    +telefone: string
    -endereco: Endereco
  }

  class Endereco {
    +cep: string
    +logradouro: string
    +complemento: string
    +bairro: string
    +localidade: string
    +uf: string
  }

  Pessoa "1..*" *-- "1..1" Endereco
```
