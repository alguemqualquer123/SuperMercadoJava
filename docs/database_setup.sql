-- ============================================
-- Script de Dados Iniciais
-- SuperMercado PDV - Sistema de Ponto de Venda
-- ============================================

-- Nota: Este script é para referência.
-- O Hibernate criará as tabelas automaticamente.
-- Os dados iniciais (usuário admin) são criados pelo código Java.

-- ============================================
-- CATEGORIAS DE EXEMPLO
-- ============================================

-- Após criar a interface de cadastro, você pode inserir:

-- INSERT INTO categorias (nome, descricao, ativo, data_criacao, data_atualizacao) 
-- VALUES 
--   ('Alimentos', 'Produtos alimentícios', 1, datetime('now'), datetime('now')),
--   ('Bebidas', 'Bebidas em geral', 1, datetime('now'), datetime('now')),
--   ('Higiene', 'Produtos de higiene pessoal', 1, datetime('now'), datetime('now')),
--   ('Limpeza', 'Produtos de limpeza', 1, datetime('now'), datetime('now')),
--   ('Hortifruti', 'Frutas, legumes e verduras', 1, datetime('now'), datetime('now'));

-- ============================================
-- PRODUTOS DE EXEMPLO
-- ============================================

-- Exemplos de produtos (inserir após criar categorias):

-- INSERT INTO produtos (codigo_barras, nome, categoria_id, preco_custo, preco_venda, 
--                       quantidade_estoque, estoque_minimo, unidade_medida, ativo, 
--                       data_criacao, data_atualizacao) 
-- VALUES 
--   ('7891234567890', 'Arroz Tipo 1 5kg', 1, 15.00, 22.00, 50, 10, 'UN', 1, datetime('now'), datetime('now')),
--   ('7891234567891', 'Feijão Preto 1kg', 1, 5.50, 8.50, 30, 5, 'UN', 1, datetime('now'), datetime('now')),
--   ('7891234567892', 'Macarrão 500g', 1, 2.00, 3.50, 100, 20, 'UN', 1, datetime('now'), datetime('now')),
--   ('7891234567893', 'Refrigerante 2L', 2, 4.00, 7.00, 40, 10, 'UN', 1, datetime('now'), datetime('now')),
--   ('7891234567894', 'Suco de Laranja 1L', 2, 3.50, 6.00, 25, 5, 'UN', 1, datetime('now'), datetime('now')),
--   ('7891234567895', 'Sabonete 90g', 3, 1.00, 2.00, 80, 15, 'UN', 1, datetime('now'), datetime('now')),
--   ('7891234567896', 'Shampoo 400ml', 3, 8.00, 14.00, 20, 5, 'UN', 1, datetime('now'), datetime('now')),
--   ('7891234567897', 'Detergente 500ml', 4, 1.50, 2.50, 60, 10, 'UN', 1, datetime('now'), datetime('now')),
--   ('7891234567898', 'Água Sanitária 1L', 4, 2.00, 3.50, 35, 8, 'UN', 1, datetime('now'), datetime('now')),
--   ('7891234567899', 'Banana Prata', 5, 3.00, 5.00, 0, 0, 'KG', 1, datetime('now'), datetime('now'));

-- ============================================
-- USUARIO ADMINISTRADOR
-- ============================================

-- O usuário admin é criado automaticamente pelo sistema na primeira execução
-- Username: admin
-- Senha: admin123
-- Perfil: ADMINISTRADOR

-- ============================================
-- CONSULTAS ÚTEIS
-- ============================================

-- Ver todas as vendas do dia
-- SELECT * FROM vendas WHERE date(data_venda) = date('now') AND status = 'FINALIZADA';

-- Ver total vendido hoje
-- SELECT SUM(total) FROM vendas WHERE date(data_venda) = date('now') AND status = 'FINALIZADA';

-- Ver produtos com estoque baixo
-- SELECT * FROM produtos WHERE quantidade_estoque <= estoque_minimo AND ativo = 1;

-- Ver produtos mais vendidos
-- SELECT p.nome, SUM(iv.quantidade) as total_vendido
-- FROM itens_venda iv
-- JOIN produtos p ON iv.produto_id = p.id
-- JOIN vendas v ON iv.venda_id = v.id
-- WHERE v.status = 'FINALIZADA'
-- GROUP BY p.id
-- ORDER BY total_vendido DESC
-- LIMIT 10;

-- ============================================
-- FIM DO SCRIPT
-- ============================================
