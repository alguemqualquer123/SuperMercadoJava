# 📐 Arquitetura do Sistema - SuperMercado PDV

## 🎯 Visão Geral

O SuperMercado PDV foi desenvolvido seguindo os princípios de **Clean Architecture** e **Separation of Concerns**, 
organizando o código em camadas bem definidas que promovem manutenibilidade, testabilidade e escalabilidade.

---

## 🏗️ Diagrama de Camadas

```
┌─────────────────────────────────────────────────────────────┐
│                        PRESENTATION                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Login.fxml  │  │  Main.fxml   │  │  style.css   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         CONTROLLER                           │
│  ┌──────────────────┐  ┌──────────────────────────────┐    │
│  │ LoginController  │  │    MainController            │    │
│  │  - handleLogin   │  │  - adicionarProduto          │    │
│  │                  │  │  - finalizarVenda            │    │
│  └──────────────────┘  └──────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                          SERVICE                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────┐  │
│  │ ProdutoService  │  │  VendaService   │  │UsuarioSvc  │  │
│  │ - salvar        │  │ - criarVenda    │  │ - login    │  │
│  │ - buscarPorCod  │  │ - adicionarItem │  │ - logout   │  │
│  │ - listarAtivos  │  │ - finalizar     │  │            │  │
│  └─────────────────┘  └─────────────────┘  └────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                           DAO                                │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────┐  │
│  │  ProdutoDAO     │  │   VendaDAO      │  │UsuarioDAO  │  │
│  │ - findById      │  │ - findByPeriodo │  │- findByUsr │  │
│  │ - findByBarcode │  │ - calcTotal     │  │            │  │
│  │ - save/update   │  │ - save/update   │  │            │  │
│  └─────────────────┘  └─────────────────┘  └────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                          MODEL                               │
│  ┌───────────┐  ┌──────────┐  ┌────────┐  ┌─────────────┐ │
│  │  Produto  │  │  Venda   │  │Usuario │  │ Categoria   │ │
│  │  @Entity  │  │ @Entity  │  │@Entity │  │  @Entity    │ │
│  └───────────┘  └──────────┘  └────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     PERSISTENCE (JPA)                        │
│                    ┌──────────────────┐                      │
│                    │ EntityManager    │                      │
│                    │  (Hibernate)     │                      │
│                    └──────────────────┘                      │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       DATABASE                               │
│                  ┌────────────────────┐                      │
│                  │  SQLite Database   │                      │
│                  │ supermercado.db    │                      │
│                  └────────────────────┘                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Detalhamento das Camadas

### 1️⃣ Presentation Layer (View)

**Responsabilidade**: Interface visual do usuário

**Componentes**:
- **FXML Files**: Definição declarativa da UI
  - `Login.fxml`: Tela de autenticação
  - `Main.fxml`: Tela principal com PDV
  
- **CSS**: Estilos visuais
  - `style.css`: Design system com Material Design

**Características**:
- Separação total entre lógica e apresentação
- Bindings declarativos
- Responsiva e moderna

---

### 2️⃣ Controller Layer

**Responsabilidade**: Gerenciar interações do usuário e coordenar com Services

**Componentes**:
- `LoginController`: Autenticação
- `MainController`: PDV e operações de venda
- `PagamentoDialog`: Finalização de venda

**Responsabilidades**:
- Capturar eventos da UI
- Validar entrada do usuário
- Chamar Services apropriados
- Atualizar a View com resultados
- Tratamento de exceções para o usuário

**Exemplo de Fluxo**:
```
User Action → Controller → Service → DAO → Database
                  ↓
            Update View
```

---

### 3️⃣ Service Layer (Business Logic)

**Responsabilidade**: Lógica de negócio centralizada

**Componentes**:
- `ProdutoService`: Gerenciamento de produtos
- `VendaService`: Operações de venda
- `UsuarioService`: Autenticação e autorização
- `CategoriaService`: Gerenciamento de categorias

**Características**:
- **Transacional**: Garante consistência
- **Validações**: Regras de negócio centralizadas
- **Reutilizável**: Pode ser usado por múltiplos controllers
- **Testável**: Fácil de testar unitariamente

**Exemplo**:
```java
public Venda finalizarVenda(Venda venda, String formaPagamento, BigDecimal valorPago) {
    // 1. Validar venda
    Validador.validarVenda(venda);
    
    // 2. Finalizar
    venda.finalizar(formaPagamento, valorPago);
    
    // 3. Salvar
    Venda vendaSalva = vendaDAO.save(venda);
    
    // 4. Baixar estoque
    for (ItemVenda item : vendaSalva.getItens()) {
        produtoService.atualizarEstoque(item.getProduto().getId(), -item.getQuantidade());
    }
    
    return vendaSalva;
}
```

---

### 4️⃣ DAO Layer (Data Access)

**Responsabilidade**: Acesso e persistência de dados

**Componentes**:
- `GenericDAO<T, ID>`: CRUD genérico
- `ProdutoDAO`: Queries específicas de produto
- `VendaDAO`: Queries de vendas e relatórios
- `UsuarioDAO`: Autenticação
- `JPAUtil`: Gerenciamento do EntityManagerFactory

**Padrões Utilizados**:
- **Repository Pattern**
- **Generic DAO Pattern**
- **Unit of Work** (via JPA)

**Características**:
- Abstração completa do banco de dados
- Queries tipadas (JPQL)
- Gerenciamento automático de transações
- Reutilização de código

---

### 5️⃣ Model Layer (Domain)

**Responsabilidade**: Representação do domínio e regras de entidade

**Entidades**:

| Entidade | Descrição | Relacionamentos |
|----------|-----------|-----------------|
| `Produto` | Itens vendidos | ManyToOne → Categoria |
| `Categoria` | Agrupamento de produtos | OneToMany → Produtos |
| `Venda` | Transação de venda | OneToMany → ItemVenda, ManyToOne → Usuario |
| `ItemVenda` | Item de uma venda | ManyToOne → Venda, ManyToOne → Produto |
| `Usuario` | Usuário do sistema | OneToMany → Vendas |
| `LogAcao` | Auditoria | ManyToOne → Usuario |

**Anotações JPA**:
- `@Entity`: Marca como entidade
- `@Table`: Define nome da tabela
- `@Id`, `@GeneratedValue`: Chave primária
- `@Column`: Configurações de coluna
- `@ManyToOne`, `@OneToMany`: Relacionamentos
- `@PrePersist`, `@PreUpdate`: Callbacks de lifecycle

---

### 6️⃣ Persistence Layer

**Responsabilidade**: Mapeamento objeto-relacional

**Tecnologias**:
- **Hibernate 6.4**: Implementação JPA
- **HikariCP**: Connection pooling
- **SQLite**: Banco de dados

**Configuração**: `persistence.xml`

---

### 7️⃣ Database Layer

**SQLite Database**: `supermercado.db`

**Tabelas Principais**:
- `produtos`
- `categorias`
- `vendas`
- `itens_venda`
- `usuarios`
- `log_acoes`

---

## 🔄 Fluxos Principais

### Fluxo de Venda Completo

```
1. Usuário escaneia código de barras
   └─> MainController.handleAdicionarProduto()
        └─> VendaService.adicionarItem(venda, codigo, qtd)
             ├─> ProdutoService.buscarPorCodigoBarras(codigo)
             │    └─> ProdutoDAO.findByCodigoBarras(codigo)
             │         └─> Database Query
             │
             ├─> Validação de estoque
             ├─> Criação de ItemVenda
             └─> Atualização do carrinho

2. Usuário finaliza a venda (F12)
   └─> MainController.handleFinalizarVenda()
        └─> PagamentoDialog.show()
             └─> Usuário informa forma de pagamento e valor
                  └─> VendaService.finalizarVenda(venda, forma, valor)
                       ├─> Validador.validarVenda()
                       ├─> venda.finalizar()
                       ├─> VendaDAO.save(venda)
                       ├─> Para cada item:
                       │    └─> ProdutoService.atualizarEstoque(..., -quantidade)
                       │         └─> ProdutoDAO.update(produto)
                       │
                       └─> ComprovantePDF.gerarComprovante(venda)
                            └─> iText PDF creation
```

### Fluxo de Autenticação

```
1. Tela de Login
   └─> LoginController.handleLogin()
        └─> UsuarioService.login(username, senha)
             ├─> UsuarioDAO.findByUsername(username)
             ├─> BCrypt.checkpw(senha, hash)
             ├─> usuario.registrarAcesso()
             ├─> LogAcaoDAO.save(log)
             └─> Retorna Usuario

2. Navegação para Main
   └─> SuperMercadoApp.showMain()
        └─> MainController.initialize()
             └─> Carrega dados do usuário logado
```

---

## 🎨 Padrões de Design Utilizados

| Padrão | Onde | Objetivo |
|--------|------|----------|
| **MVC** | Toda aplicação | Separação de responsabilidades |
| **DAO** | Camada de persistência | Abstração de acesso a dados |
| **Service Layer** | Lógica de negócio | Centralização de regras |
| **Singleton** | JPAUtil | Única instância de EMF |
| **Factory** | EntityManagerFactory | Criação de EntityManagers |
| **Strategy** | Formas de pagamento | Comportamentos intercambiáveis |
| **Observer** | JavaFX Properties | Binding reativo |
| **Template Method** | GenericDAO | Reutilização de código CRUD |

---

## 🔐 Segurança

```
┌──────────────┐
│   Usuario    │
└──────────────┘
       │
       ▼
┌──────────────┐      ┌───────────────┐
│  Controller  │─────>│ UsuarioService│
└──────────────┘      └───────────────┘
                             │
                             ▼
                      ┌──────────────┐
                      │  BCrypt Hash │
                      └──────────────┘
                             │
                             ▼
                      ┌──────────────┐
                      │   Database   │
                      └──────────────┘
```

- Senhas com **BCrypt** (cost factor 12)
- Sessão gerenciada por `UsuarioService`
- Logs de auditoria em `LogAcao`

---

## 📊 Diagrama de Entidades (ER)

```
┌─────────────┐         ┌──────────────┐
│  Categoria  │1      ∞ │   Produto    │
│ ─────────── │←────────│ ──────────── │
│ id          │         │ id           │
│ nome        │         │ codigoBarras │
│ descricao   │         │ nome         │
└─────────────┘         │ categoria_id │
                        │ precoVenda   │
                        │ estoque      │
                        └──────────────┘
                               │
                               │∞
                               │
                        ┌──────▼───────┐         ┌─────────────┐
                  ┌────>│  ItemVenda   │∞      1 │    Venda    │
                  │     │ ──────────── │────────>│ ─────────── │
                  │     │ id           │         │ id          │
                  │     │ venda_id     │         │ dataVenda   │
                  │     │ produto_id   │         │ usuario_id  │
                  │     │ quantidade   │         │ total       │
                  │     │ precoUnit    │         │ status      │
                  │     └──────────────┘         └─────────────┘
                  │                                     │1
                  │1                                    │
                  │                                     │∞
           ┌──────┴──────┐                       ┌─────▼──────┐
           │   Produto   │                       │  Usuario   │
           └─────────────┘                       │ ────────── │
                                                 │ id         │
                                                 │ username   │
                                                 │ senha      │
                                                 │ perfil     │
                                                 └────────────┘
```

---

## 🚀 Escalabilidade

### Preparação para Crescimento

1. **Services desacoplados**: Fácil migração para microserviços
2. **Interface DAO**: Trocar SQLite por MySQL/PostgreSQL facilmente
3. **Camada de serviço**: Adicionar cache (Redis) sem afetar controllers
4. **Separação clara**: Adicionar API REST mantendo a lógica

### Migração para MySQL

```xml
<!-- Trocar em persistence.xml -->
<property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
<property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/supermercado"/>
<property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect"/>
```

---

## 📖 Conclusão

A arquitetura do SuperMercado PDV foi desenhada para ser:
- ✅ **Manutenível**: Código organizado e documentado
- ✅ **Testável**: Camadas independentes
- ✅ **Escalável**: Fácil adicionar funcionalidades
- ✅ **Performática**: Connection pool e queries otimizadas
- ✅ **Segura**: Criptografia e auditoria

---

**Última atualização**: 2026-01-30  
**Versão da Arquitetura**: 1.0
