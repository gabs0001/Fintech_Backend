package br.com.fintech.main;

import br.com.fintech.dao.GastoDAO;
import br.com.fintech.dao.RecebimentoDAO;
import br.com.fintech.dao.UsuarioDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Gasto;
import br.com.fintech.model.Recebimento;
import br.com.fintech.model.Usuario;
import br.com.fintech.service.GastoService;
import br.com.fintech.service.RecebimentoService;
import br.com.fintech.service.UsuarioService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FintechTestRunner {
    // --- MÉTODOS DE TESTE ---
    public static void testeGastoInsert(GastoDAO gastoDAO, Long userId, Long categoriaId) throws SQLException {
        System.out.println("\n--- INSERINDO 5 NOVOS GASTOS ---");

        List<Gasto> gastos = List.of(
                new Gasto(null, userId, "Aluguel", categoriaId, new BigDecimal("400.00"),
                        LocalDate.of(2025, 7, 24)),
                new Gasto(null, userId, "Mercado", categoriaId, new BigDecimal("350.50"),
                        LocalDate.of(2025, 1, 5)),
                new Gasto(null, userId, "Cinema", categoriaId, new BigDecimal("85.00"),
                        LocalDate.of(2025, 1, 10)),
                new Gasto(null, userId, "Gasolina", categoriaId, new BigDecimal("200.00"),
                        LocalDate.of(2025, 1, 15)),
                new Gasto(null, userId, "Internet", categoriaId, new BigDecimal("100.00"),
                        LocalDate.of(2025, 1, 20))
        );

        for (Gasto g : gastos) {
            gastoDAO.insert(g);
        }
        System.out.println("✅ 5 Gastos inseridos com sucesso.");
        System.out.println("----------------------------------------");
    }

    public static void testeGastoGetAll(GastoDAO gastoDAO) throws SQLException {
        System.out.println("\n--- CONSULTANDO TODOS OS GASTOS ---");
        List<Gasto> todosGastos = gastoDAO.getAll();

        if (todosGastos.isEmpty()) {
            System.out.println("Nenhum gasto encontrado no banco de dados!");
        } else {
            for (Gasto g : todosGastos) {
                System.out.println(g);
            }
        }
        System.out.println("----------------------------------------");
    }

    public static void testeGastoRemove(GastoService gastoService, Long userId, Long categoriaId) throws SQLException, EntityNotFoundException {
        System.out.println("\n--- INICIANDO TESTES DE REMOÇÃO (GASTO) ---");

        Gasto gastoParaRemover = new Gasto(null, userId, "Gasto para Remoção", categoriaId, new BigDecimal("1.00"), LocalDate.now());
        gastoService.insert(gastoParaRemover);
        System.out.println("Gasto temporário inserido. ID do objeto Java: " + gastoParaRemover.getId());

        gastoService.remove(gastoParaRemover.getId(), userId);
        System.out.println("✅ Gasto com ID " + gastoParaRemover.getId() + " removido com sucesso.");

        System.out.println("\n--- TESTE DE EXCEÇÃO: DELETE COM ID INEXISTENTE ---");
        gastoService.remove(9999L, userId);

        System.out.println("-----------------------------------------------------------------");
    }

    public static void testeRecebimentoCrud(RecebimentoService recebimentoService, Long userId, Long categoriaId) throws SQLException, EntityNotFoundException {
        System.out.println("\n--- INSERINDO 3 NOVOS RECEBIMENTOS ---");

        Recebimento r1 = new Recebimento(null, userId, "Salário", categoriaId, new BigDecimal("5000.00"),
                LocalDate.of(2025, 10, 30));
        Recebimento r2 = new Recebimento(null, userId, "Renda Extra", categoriaId, new BigDecimal("800.50"),
                LocalDate.of(2025, 11, 5));
        Recebimento r3 = new Recebimento(null, userId, "Reembolso", categoriaId, new BigDecimal("150.00"),
                LocalDate.of(2025, 11, 10));

        recebimentoService.insert(r1);
        recebimentoService.insert(r2);
        recebimentoService.insert(r3);
        System.out.println("✅ 3 Recebimentos inseridos com sucesso.");
        System.out.println("----------------------------------------");

        System.out.println("\n--- CONSULTANDO TODOS OS RECEBIMENTOS ---");
        List<Recebimento> todosRecebimentos = recebimentoService.getAll();
        if(todosRecebimentos.isEmpty()) {
            System.out.println("Nenhum recebimento encontrado no banco de dados.");
        } else {
            todosRecebimentos.forEach(System.out::println);
        }

        System.out.println("----------------------------------------");

        System.out.println("\n--- ATUALIZANDO UM RECEBIMENTO ---");

        Recebimento recebimentoAtualizado = todosRecebimentos.get(todosRecebimentos.size() - 2);

        recebimentoAtualizado.setDescricao("Renda Extra - Bônus");
        recebimentoAtualizado.setValor(new BigDecimal("900.00"));

        recebimentoService.update(recebimentoAtualizado);

        System.out.println("✅ Recebimento " + recebimentoAtualizado.getId() + " atualizado.");
        System.out.println("----------------------------------------");

        // TESTE DE EXCEÇÃO (UPDATE com ID inexistente)
        System.out.println("\n--- TESTE DE EXCEÇÃO (ID 9999) ---");

        Recebimento recebimentoInexistente = new Recebimento(9999L, userId, "Teste Falha", 1L, new BigDecimal("1.00"),
                LocalDate.now());

        recebimentoService.update(recebimentoInexistente);

        System.out.println("-----------------------------------------------------------------");
    }

    public static void testeUsuarioUpdate(UsuarioService usuarioService, Long userId) throws SQLException, EntityNotFoundException {
        System.out.println("\n--- ATUALIZANDO DADOS DO USUÁRIO ---");

        Usuario usuarioParaAtualizar = usuarioService.getById(userId, userId);

        if (usuarioParaAtualizar != null) {
            String novoNome = "Usuario Teste ATUALIZADO";
            usuarioParaAtualizar.setNome(novoNome);
            usuarioParaAtualizar.setEmail("novoemail.teste@fintech.com.br");

            usuarioService.update(usuarioParaAtualizar);

            System.out.println("✅ Usuário " + userId + " atualizado para: " + novoNome);
        } else {
            System.out.println("Usuário não encontrado. Não foi possível rodar o teste de UPDATE.");
        }

        // TESTE DE EXCEÇÃO (UPDATE com ID inexistente)
        System.out.println("\n--- TESTE DE EXCEÇÃO: UPDATE COM ID 9999 ---");
        Usuario usuarioInexistente = new Usuario(9999L, "Inexistente", LocalDate.now(), "M", "a@b.c", "senha");
        usuarioService.update(usuarioInexistente); // Lança EntityNotFoundException
    }

    public static void testeUsuarioRemove(UsuarioService usuarioService, Long userId) {
        System.out.println("\n--- TESTE DE REMOÇÃO DE USUÁRIO ---");

        try {
            usuarioService.remove(userId);
            System.out.println("✅ Usuário " + userId + " removido com sucesso.");
        }
        catch (SQLException e) {
            System.err.println("✅ Exceção Capturada (Integridade Referencial): Não foi possível remover o Usuário ID " + userId +
                    " pois ele possui gastos/recebimentos vinculados.");
        }
        catch (EntityNotFoundException e) {
            System.out.println("✅ Exceção Capturada (Remoção Inexistente): " + e.getMessage());
        }
    }

    // --- METODO PARA FECHAR CONEXÕES ---
    public static void fecharConexoes(GastoDAO gastoDAO, RecebimentoDAO recebimentoDAO, UsuarioDAO usuarioDAO) {
        if(gastoDAO != null) {
            try {
                gastoDAO.fecharConexao();
                System.out.println("\nConexão de Gasto fechada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão de gasto: " + e.getMessage());
            }
        }

        if(recebimentoDAO != null) {
            try {
                recebimentoDAO.fecharConexao();
                System.out.println("Conexão de Recebimento fechada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão de recebimento: " + e.getMessage());
            }
        }

        if(usuarioDAO != null) {
            try {
                usuarioDAO.fecharConexao();
                System.out.println("Conexão de Usuário fechada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão de usuário: " + e.getMessage());
            }
        }
    }
}