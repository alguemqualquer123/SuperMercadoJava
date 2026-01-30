# Guia de Desenvolvimento - SuperMercado PDV

## 📚 Documentação Técnica

Este documento fornece informações técnicas sobre o projeto para desenvolvedores que desejam contribuir ou entender a arquitetura.

## 🏗️ Arquitetura Detalhada

### Camada Model (Entidades JPA)

As entidades representam as tabelas do banco de dados:

- **Produto**: Itens vendidos no supermercado
- **Categoria**: Agrupamento de produtos
- **Venda**: Transação de venda
- **ItemVenda**: Itens de uma venda específica
- **Usuario**: Usuários do sistema
- **LogAcao**: Auditoria de ações

### Camada Repository (Spring Data JPA)

O sistema utiliza Interfaces Repository que estendem `JpaRepository`, eliminando a necessidade de DAOs manuais e boilerplate de transações.

- `ProdutoRepository`: Busca por código de barras, controle de estoque.
- `VendaRepository`: Consultas financeiras e relatórios por período.
- `UsuarioRepository`: Autenticação e busca por username.
- `CategoriaRepository`: Gerenciamento de categorias.

### Camada Service (Lógica de Negócio)

**ProdutoService**:
- Validações de produto
- Controle de estoque
- Soft delete (desativação)

**VendaService**:
- Criação de vendas
- Adição/remoção de itens
- Aplicação de descontos
- Finalização e baixa de estoque

**UsuarioService**:
- Autenticação (login/logout)
- Gerenciamento de sessão
- Criação de usuários
- Auditoria

### Camada Controller

**LoginController**: Autenticação
**MainController**: PDV principal
**PagamentoDialog**: Diálogo de finalização

### Camada View (FXML)

Arquivos FXML definem a estrutura visual das telas.

## 🔐 Segurança

### Senha
- Criptografia **BCrypt** com cost factor 12
- Senha nunca armazenada em texto plano
- Validação de força de senha (mínimo 6 caracteres)

### Perfis de Acesso
- ADMINISTRADOR: Acesso total
- GERENTE: Vendas, relatórios, produtos
- OPERADOR: Apenas PDV

### Auditoria
- Logs de todas as ações importantes
- Registro de login/logout
- Rastreamento de vendas e alterações

O sistema utiliza o **PostgreSQL** através do Docker para garantir robustez e permitir operação em rede (multi-terminal).

### Schema
O Hibernate cria automaticamente as tabelas na primeira execução (`hbm2ddl.auto=update`).

### Índices
- `produtos.codigo_barras` (UNIQUE)
- `usuarios.username` (UNIQUE)
- `vendas.data_venda`
- `log_acoes.data_hora`

## 🧪 Testes

### Estrutura de Testes (a implementar)

```
src/test/java/
├── dao/
│   ├── ProdutoDAOTest.java
│   └── VendaDAOTest.java
├── service/
│   ├── ProdutoServiceTest.java
│   └── VendaServiceTest.java
└── util/
    └── ValidadorTest.java
```

### Frameworks sugeridos:
- **JUnit 5** para testes unitários
- **Mockito** para mocks
- **Testcontainers** para testes de integração com PostgreSQL

## 🐳 Docker e Containers

O projeto utiliza Docker para facilitar o gerenciamento do banco de dados e ambiente de produção.

### Serviços (docker-compose)
1. **PostgreSQL**: Banco de dados principal (Porta 5432)
2. **pgAdmin**: Interface web para gerenciamento do banco (Porta 5050)
3. **App**: Imagem da aplicação pronta para deployment em servidor

### Comandos Docker úteis:
```bash
# Iniciar apenas o banco de dados
docker-compose up -d postgres

# Iniciar todo o ambiente (Banco + pgAdmin)
docker-compose up -d

# Parar serviços
docker-compose down
```

## 🚀 Build e Deploy Profissional

O sistema agora conta com scripts de automação para facilitar o desenvolvimento e a entrega para o cliente final.

### Requisitos para Build
- **JDK 17** ou superior
- **Maven 3.8+**
- **WiX Toolset v3.11** (Obrigatório para gerar instalador .exe)

### Comandos de Desenvolvimento
```bash
# Rodar via Maven
mvn spring-boot:run     # Modo Spring
mvn javafx:run          # Modo Interface Gráfica
```

### Scripts de Automacao (.bat)
1. **`iniciar.bat`**: Menu interativo para compilar e rodar rapidamente.
2. **`build.bat`**: Script para gerar o instalador do cliente.
   - Gera instalador oficial `.exe` se o WiX estiver no PATH.
   - Gera pasta "portátil" (`app-image`) caso contrário.
   - O resultado é salvo na pasta `dist/`.

## � Banco de Dados (PostgreSQL)

O sistema migrou de SQLite para **PostgreSQL** visando suporte multi-usuário e rede.

### Configuração de Rede (Sistema Interligado)
Para rodar em rede local:
1. Instale o PostgreSQL em um **Servidor**.
2. No firewall do servidor, abra a porta `5432`.
3. Nos clientes, edite o `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://IP_DO_SERVIDOR:5432/supermercado
   ```

- O driver utilizado é o `org.postgresql.Driver`.
- O Hibernate gerencia o schema via `spring.jpa.hibernate.ddl-auto=update`.

## 📦 Dependências Principais

| Dependência | Versão | Uso |
|-------------|--------|-----|
| Spring Boot | 3.2.1 | Base da aplicação e Injeção de Dependências |
| JavaFX | 21.0.1 | Interface gráfica (GUI) |
| Spring Data JPA | 3.2.1 | Persistência e Repositories |
| PostgreSQL | 16.1 | Banco de Dados Relacional |
| iText7 | 8.0.2 | Geração de PDFs (Recibos/Vendas) |
| jBCrypt | 0.4 | Criptografia de senhas (Segurança) |
| Lombok | 1.18.30 | Redução de código Boilerplate |

## 🔮 Roadmap

### Versão 1.5 (Status: Atual)
- [x] Migração para Spring Boot
- [x] Migração para PostgreSQL
- [x] Suporte a Docker e Rede Local
- [x] Script de Build Automatizado (`build.bat`)
- [x] Correção de contraste e temas na interface

### Versão 1.6 (Status: Concluído)
- [x] Relatórios financeiros detalhados (PDF)
- [x] Dashboard gráfico de vendas (JavaFX Charts)
- [x] Sistema de permissões por Nível de Usuário (SessaoService)

### Versão 2.0 (Status: Concluído)
- [x] API REST para integração com App Mobile (Spring Web)
- [x] Integração com periféricos (Impressoras Térmicas ESC/POS e Balanças)
- [x] Módulo de compras e controle de fornecedores (Entidades e Service)

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📞 Suporte

Para dúvidas ou problemas:
- Abra uma **Issue** no repositório
- Consulte a documentação

---

**Desenvolvido com ❤️ em Java**
