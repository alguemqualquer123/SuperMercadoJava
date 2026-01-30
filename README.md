# 🛒 SuperMercado PDV - Sistema de Ponto de Venda

Sistema completo de PDV (Ponto de Venda) para supermercados desenvolvido em **Java 17**, **JavaFX**, **Hibernate/JPA** e **SQLite**.

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Como Executar](#como-executar)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Uso do Sistema](#uso-do-sistema)
- [Credenciais Padrão](#credenciais-padrão)

---

## 🎯 Sobre o Projeto

Sistema profissional de PDV para supermercados com as seguintes características:

✅ **Interface gráfica moderna** com JavaFX  
✅ **Persistência de dados** com SQLite e Hibernate/JPA  
✅ **Arquitetura em camadas** (Model-View-Controller + Services + DAO)  
✅ **Leitura de código de barras** via input USB  
✅ **Geração de comprovantes em PDF**  
✅ **Sistema de autenticação** com perfis de usuário  
✅ **Controle de estoque** automático  
✅ **Relatórios** de vendas e produtos  

---

## ⚡ Funcionalidades

### 🔐 Autenticação e Segurança
- Login com usuário e senha (criptografia BCrypt)
- 3 perfis de acesso:
  - **Administrador**: Acesso total
  - **Gerente**: Vendas e relatórios
  - **Operador de Caixa**: PDV apenas
- Registro de auditoria (logs de ações)

### 📦 Cadastro de Produtos
- Código de barras (EAN/UPC)
- Nome, categoria, preços (custo e venda)
- Controle de estoque com alerta de estoque mínimo
- Ativar/desativar produtos
- Cálculo automático de margem de lucro

### 🛒 Sistema de Vendas (PDV)
- Leitura de código de barras via scanner USB
- Busca de produtos por código ou nome
- Adição/remoção de itens no carrinho
- Controle de quantidade
- Aplicação de descontos (percentual)
- Cálculo automático de totais
- Finalização com múltiplas formas de pagamento:
  - Dinheiro
  - Cartão de Débito
  - Cartão de Crédito
  - PIX
- Cálculo de troco
- Baixa automática de estoque

### 🧾 Comprovantes
- Geração de comprovantes em PDF (iText)
- Informações completas: data, itens, valores, totais
- Abertura automática do PDF
- Armazenamento em diretório `/comprovantes`

### 📊 Relatórios *(em desenvolvimento)*
- Vendas do dia
- Vendas do mês
- Produtos mais vendidos
- Exportação em PDF/CSV

### ⚙️ Funcionalidades Técnicas
- Validações centralizadas
- Tratamento de exceções
- Logs detalhados (Logback)
- Pool de conexões (HikariCP)
- Transações JPA

---

## 🛠️ Tecnologias Utilizadas

| Categoria | Tecnologia |
|-----------|------------|
| **Linguagem** | Java 17 |
| **Interface Gráfica** | JavaFX 21 |
| **Persistência** | Hibernate 6.4 / JPA |
| **Banco de Dados** | SQLite |
| **PDF** | iText 8 |
| **Build** | Maven |
| **Logging** | SLF4J + Logback |
| **Utilitários** | Lombok, Apache Commons |
| **Segurança** | BCrypt (jBCrypt) |

---

## 🏗️ Arquitetura

O projeto segue uma **arquitetura em camadas** bem definida:

```
┌─────────────────┐
│  View (FXML)    │ ← Interface gráfica JavaFX
├─────────────────┤
│  Controller     │ ← Lógica de apresentação
├─────────────────┤
│  Service        │ ← Lógica de negócio
├─────────────────┤
│  DAO            │ ← Acesso a dados
├─────────────────┤
│  Model          │ ← Entidades JPA
├─────────────────┤
│  Database       │ ← SQLite
└─────────────────┘
```

### Padrões Utilizados:
- **MVC** (Model-View-Controller)
- **DAO** (Data Access Object)
- **Service Layer**
- **Generic DAO**
- **Singleton** (EntityManagerFactory)

---

## 🚀 Como Executar

### Pré-requisitos

- **Java JDK 17+** instalado ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.8+** instalado ([Download](https://maven.apache.org/download.cgi))

### Passo a Passo

1. **Clone ou baixe o projeto**

2. **Navegue até a pasta do projeto**
   ```bash
   cd SuperMercadoJava
   ```

3. **Compile o projeto**
   ```bash
   mvn clean install
   ```

4. **Execute a aplicação**
   ```bash
   mvn javafx:run
   ```

   **OU** (alternativa)
   ```bash
   mvn clean package
   java --module-path "caminho/para/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -jar target/supermercado-pdv-1.0.0.jar
   ```

---

## 📁 Estrutura do Projeto

```
SuperMercadoJava/
│
├── src/
│   ├── main/
│   │   ├── java/com/supermercado/
│   │   │   ├── model/              # Entidades JPA
│   │   │   │   ├── Produto.java
│   │   │   │   ├── Categoria.java
│   │   │   │   ├── Venda.java
│   │   │   │   ├── ItemVenda.java
│   │   │   │   ├── Usuario.java
│   │   │   │   └── LogAcao.java
│   │   │   │
│   │   │   ├── dao/                # Data Access Objects
│   │   │   │   ├── GenericDAO.java
│   │   │   │   ├── JPAUtil.java
│   │   │   │   ├── ProdutoDAO.java
│   │   │   │   ├── CategoriaDAO.java
│   │   │   │   ├── VendaDAO.java
│   │   │   │   ├── UsuarioDAO.java
│   │   │   │   └── LogAcaoDAO.java
│   │   │   │
│   │   │   ├── service/            # Lógica de Negócio
│   │   │   │   ├── ProdutoService.java
│   │   │   │   ├── VendaService.java
│   │   │   │   └── UsuarioService.java
│   │   │   │
│   │   │   ├── controller/         # Controllers JavaFX
│   │   │   │   ├── LoginController.java
│   │   │   │   ├── MainController.java
│   │   │   │   └── PagamentoDialog.java
│   │   │   │
│   │   │   ├── util/               # Utilitários
│   │   │   │   ├── Validador.java
│   │   │   │   ├── ComprovantePDF.java
│   │   │   │   ├── DialogUtil.java
│   │   │   │   └── FormatadorUtil.java
│   │   │   │
│   │   │   └── SuperMercadoApp.java # Classe principal
│   │   │
│   │   └── resources/
│   │       ├── fxml/               # Arquivos FXML (Views)
│   │       │   ├── Login.fxml
│   │       │   └── Main.fxml
│   │       │
│   │       ├── css/                # Estilos
│   │       │   └── style.css
│   │       │
│   │       ├── META-INF/
│   │       │   └── persistence.xml # Config JPA
│   │       │
│   │       └── logback.xml         # Config Logging
│   │
│   └── test/                       # (Testes futuros)
│
├── database/                       # Banco de dados SQLite (gerado automaticamente)
│   └── supermercado.db
│
├── comprovantes/                   # PDFs gerados (criado automaticamente)
│
├── logs/                           # Logs da aplicação (criado automaticamente)
│
├── pom.xml                         # Configuração Maven
│
└── README.md                       # Este arquivo
```

---

## 💻 Uso do Sistema

### 1. Login

Ao iniciar o sistema, você verá a tela de login:

![Login](https://via.placeholder.com/400x300?text=Tela+de+Login)

- Digite o **usuário** e **senha**
- Clique em **Entrar** ou pressione **Enter**

### 2. Tela Principal (PDV)

![PDV](https://via.placeholder.com/800x600?text=Tela+PDV)

#### Operações básicas:

**Adicionar produto:**
1. Digite ou escaneie o código de barras
2. Ajuste a quantidade (se necessário)
3. Pressione **Enter** ou clique em **Adicionar**

**Remover produto:**
- Selecione o item na tabela
- Clique em **Remover Item Selecionado**

**Aplicar desconto:**
1. Digite o percentual de desconto
2. Clique em **Aplicar**

**Finalizar venda:**
1. Clique em **F12 - FINALIZAR VENDA** (ou pressione F12)
2. Escolha a **forma de pagamento**
3. Digite o **valor pago**
4. Clique em **Finalizar**
5. O sistema calcula o troco automaticamente
6. Gere o comprovante em PDF (opcional)

### 3. Menu Superior

- **Arquivo**
  - Logout: Sair da conta
  - Sair: Fechar o sistema

- **Cadastros** *(em desenvolvimento)*
  - Produtos
  - Categorias
  - Usuários

- **Relatórios** *(em desenvolvimento)*
  - Vendas do Dia
  - Vendas do Mês
  - Produtos Mais Vendidos

### 4. Atalhos de Teclado

| Tecla | Ação |
|-------|------|
| `F12` | Finalizar venda |
| `F2` | Buscar produto |
| `F4` | Limpar venda |
| `Enter` | Adicionar produto (no campo de código) |

---

## 🔑 Credenciais Padrão

Ao executar pela primeira vez, o sistema cria automaticamente um usuário administrador:

- **Usuário:** `admin`
- **Senha:** `admin123`
- **Perfil:** Administrador

> ⚠️ **Importante:** Altere a senha padrão em produção!

---

## 📝 Próximas Implementações

- [ ] Tela de cadastro de produtos (CRUD completo)
- [ ] Tela de cadastro de categorias
- [ ] Tela de cadastro de usuários
- [ ] Tela de busca de produtos
- [ ] Relatórios em PDF
- [ ] Exportação de relatórios em CSV
- [ ] Dashboard com resumo do dia
- [ ] Backup automático do banco
- [ ] Tema claro/escuro
- [ ] Impressão direta de comprovantes (impressora térmica)

---

## 🐛 Solução de Problemas

### Erro ao iniciar: "JavaFX runtime components are missing"

**Solução:** Certifique-se de que o JavaFX está configurado corretamente no `pom.xml` e execute com `mvn javafx:run`.

### Erro de banco de dados

**Solução:** Verifique se o diretório `database/` foi criado. O Hibernate cria o banco automaticamente na primeira execução.

### Comprovante PDF não abre

**Solução:** Verifique se você tem um leitor de PDF instalado (Adobe Reader, etc.).

---

## 📄 Licença

Este projeto é de código aberto para fins educacionais e comerciais.

---

## 👨‍💻 Desenvolvedor

Desenvolvido como projeto de demonstração de sistema PDV profissional em Java.

---

## 🙏 Agradecimentos

- JavaFX pela interface gráfica moderna
- Hibernate pela persistência simplificada
- iText pela geração de PDFs
- SQLite pelo banco de dados leve e eficiente

---

**🎉 Boas vendas! 🛒**
