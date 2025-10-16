package br.com.fintech.main;

import br.com.fintech.dao.*;
import br.com.fintech.dto.DashboardDTO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Categoria;
import br.com.fintech.model.Gasto;
import br.com.fintech.model.Instituicao;
import br.com.fintech.model.Investimento;
import br.com.fintech.model.Recebimento;
import br.com.fintech.model.Usuario;
import br.com.fintech.service.GastoService;
import br.com.fintech.service.InstituicaoService;
import br.com.fintech.service.InvestimentoService;
import br.com.fintech.service.RecebimentoService;
import br.com.fintech.service.RelatorioService;
import br.com.fintech.service.UsuarioService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FintechTestRunner {
    // --- MÉTODOS DE TESTE DE DOMÍNIO ---
    public static Instituicao testeInstituicaoInsert(InstituicaoService instituicaoService) throws SQLException {
        System.out.println("\n--- INSERINDO INSTITUIÇÃO DE TESTE ---");
        Instituicao instituicao = new Instituicao(null, "Banco de Teste S.A. - IT");
        instituicaoService.insert(instituicao);
        System.out.println("✅ Instituição inserida com ID: " + instituicao.getId());
        System.out.println("----------------------------------------");
        return instituicao;
    }

    // --- MÉTODOS DE TESTE DE TRANSAÇÕES ---
    public static void testeGastoInsert(GastoService gastoService, Long userId, Categoria categoria) throws SQLException, IllegalArgumentException {
        System.out.println("\n--- INSERINDO 5 NOVOS GASTOS (via Service) ---");

        List<Gasto> gastos = List.of(
                new Gasto(null, userId, "Aluguel", categoria, new BigDecimal("400.00"),
                        LocalDate.of(2025, 7, 24)),
                new Gasto(null, userId, "Mercado", categoria, new BigDecimal("350.50"),
                        LocalDate.of(2025, 1, 5)),
                new Gasto(null, userId, "Cinema", categoria, new BigDecimal("85.00"),
                        LocalDate.of(2025, 1, 10)),
                new Gasto(null, userId, "Gasolina", categoria, new BigDecimal("200.00"),
                        LocalDate.of(2025, 1, 15)),
                new Gasto(null, userId, "Internet", categoria, new BigDecimal("100.00"),
                        LocalDate.of(2025, 1, 20))
        );

        for (Gasto g : gastos) {
            gastoService.insert(g);
        }
        System.out.println("✅ 5 Gastos inseridos com sucesso.");
        System.out.println("----------------------------------------");
    }

    public static void testeGastoGetAll(GastoService gastoService) throws SQLException {
        System.out.println("\n--- CONSULTANDO TODOS OS GASTOS (via Service) ---");
        List<Gasto> todosGastos = gastoService.getAll();

        if (todosGastos.isEmpty()) {
            System.out.println("Nenhum gasto encontrado no banco de dados!");
        } else {
            for (Gasto g : todosGastos) {
                System.out.println(g);
            }
        }
        System.out.println("----------------------------------------");
    }

    public static void testeGastoRemove(GastoService gastoService, Long userId, Categoria categoria) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        System.out.println("\n--- INICIANDO TESTES DE REMOÇÃO (GASTO) ---");

        Gasto gastoParaRemover = new Gasto(null, userId, "Gasto para Remoção", categoria, new BigDecimal("1.00"), LocalDate.now());
        gastoService.insert(gastoParaRemover);
        System.out.println("Gasto temporário inserido. ID do objeto Java: " + gastoParaRemover.getId());

        gastoService.remove(gastoParaRemover.getId(), userId);
        System.out.println("✅ Gasto com ID " + gastoParaRemover.getId() + " removido com sucesso.");

        System.out.println("\n--- TESTE DE EXCEÇÃO: DELETE COM ID INEXISTENTE ---");
        try {
            gastoService.remove(9999L, userId);
        } catch (EntityNotFoundException e) {
            System.out.println("✅ Exceção Capturada: " + e.getMessage());
        }

        System.out.println("-----------------------------------------------------------------");
    }

    public static void testeRecebimentoCrud(RecebimentoService recebimentoService, Long userId, Categoria categoria) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        System.out.println("\n--- INSERINDO 3 NOVOS RECEBIMENTOS ---");

        // Ajuste nas datas de recebimento para testar o Balanço Período (que é em Jan/2025)
        Recebimento r1 = new Recebimento(null, userId, "Salário (Fora do Período)", categoria, new BigDecimal("5000.00"),
                LocalDate.of(2025, 10, 30));
        Recebimento r2 = new Recebimento(null, userId, "Renda Extra (Fora do Período)", categoria, new BigDecimal("800.50"),
                LocalDate.of(2025, 11, 5));
        Recebimento r3 = new Recebimento(null, userId, "Reembolso (Fora do Período)", categoria, new BigDecimal("150.00"),
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
        System.out.println("\n--- TESTE DE EXCEÇÃO: UPDATE COM ID INEXISTENTE ---");

        Recebimento recebimentoInexistente = new Recebimento(9999L, userId, "Teste Falha", categoria, new BigDecimal("1.00"),
                LocalDate.now());

        try {
            recebimentoService.update(recebimentoInexistente);
        } catch (EntityNotFoundException e) {
            System.out.println("✅ Exceção Capturada: " + e.getMessage());
        }

        System.out.println("-----------------------------------------------------------------");
    }

    public static void testeInvestimentoCrud(InvestimentoService investimentoService, Long userId, Categoria categoria, Instituicao instituicao) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        System.out.println("\n--- INICIANDO TESTES DE INVESTIMENTO ---");

        // 1. INSERT (MANTIDO para o teste de Relatório de Saldo Geral e Investido)
        Investimento i1 = new Investimento(null, userId, "CDB 100% CDI", categoria, new BigDecimal("1000.00"),
                "CDB", LocalDate.now(), LocalDate.now().plusYears(1), instituicao);

        investimentoService.insert(i1);
        System.out.println("✅ Investimento inserido com ID: " + i1.getId());

        // 2. GET ALL
        System.out.println("\n--- CONSULTANDO INVESTIMENTOS ---");
        investimentoService.getAll().forEach(System.out::println);

        // 3. UPDATE
        i1.setNome("CDB 110% CDI (Atualizado)");
        i1.setValor(new BigDecimal("1100.00"));
        investimentoService.update(i1);
        System.out.println("✅ Investimento " + i1.getId() + " atualizado.");

        // 4. REMOVE (COMENTADO para que o Total Investido no Relatório não seja ZERO)
        // investimentoService.remove(i1.getId(), userId);
        // System.out.println("✅ Investimento " + i1.getId() + " removido.");

        System.out.println("-----------------------------------------------------------------");
    }

    // --- MÉTODOS DE TESTE DE RELATÓRIO ---
    public static void testeRelatorioDashboard(RelatorioService relatorioService, Long userId) throws SQLException {
        System.out.println("\n--- INICIANDO TESTE DO RELATORIO (DASHBOARD) ---");

        // Definir o período para teste de saldo (Ex: Mês de Janeiro/2025)
        LocalDate inicioPeriodo = LocalDate.of(2025, 1, 1);
        LocalDate fimPeriodo = LocalDate.of(2025, 1, 31);
        int limite = 3; // Limitar as listas

        System.out.println("Calculando Dashboard para o período: " + inicioPeriodo + " a " + fimPeriodo);

        // CHAMA O METODO ORQUESTRADOR
        DashboardDTO dashboard = relatorioService.getDashboard(userId, limite, inicioPeriodo, fimPeriodo);

        // --- VALIDAÇÃO E EXIBIÇÃO DOS RESULTADOS ---

        // 1. Saldo Geral (acumulado):
        System.out.println("\n✅ Saldo Geral (Total Acumulado): R$ " + dashboard.getSaldoGeral());

        // 2. Saldo por Período (Jan/2025):
        // Esperado: -735.50 (Gastos em Jan: 350.50 + 85.00 + 200.00 + 100.00)
        System.out.println("✅ Saldo Período (" + inicioPeriodo.getYear() + "): R$ " + dashboard.getSaldoPeriodo());

        // 3. Total Investido
        // Esperado: 1100.00 (Valor após o UPDATE do Investimento)
        System.out.println("✅ Total Investido: R$ " + dashboard.getTotalInvestido());

        // 4. Último Gasto (Mais recente): Deve ser o de Julho (400.00)
        System.out.println("\n✅ Último Gasto: " + (dashboard.getUltimoGasto() != null ? dashboard.getUltimoGasto().getDescricao() + " (" + dashboard.getUltimoGasto().getDataGasto() + ")" : "N/A"));

        // 5. Últimos Recebimentos (Lista)
        System.out.println("\n✅ Top " + limite + " Últimos Recebimentos:");
        dashboard.getUltimosRecebimentos().forEach(r -> System.out.println("  - " + r.getDescricao() + " (" + r.getValor() + ")"));

        System.out.println("-----------------------------------------------------------------");
    }

    // --- MÉTODOS DE TESTE DE USUÁRIO ---
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
        try {
            Usuario usuarioInexistente = new Usuario(9999L, "Inexistente", LocalDate.now(), "M", "a@b.c", "senha");
            usuarioService.update(usuarioInexistente); // Lança EntityNotFoundException
        } catch (EntityNotFoundException e) {
            System.out.println("✅ Exceção Capturada: " + e.getMessage());
        }
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

    public static void fecharConexoes(GastoDAO gastoDAO, RecebimentoDAO recebimentoDAO, UsuarioDAO usuarioDAO, InstituicaoDAO instituicaoDAO, InvestimentoDAO investimentoDAO) {
        fecharConexao(gastoDAO);
        fecharConexao(recebimentoDAO);
        fecharConexao(usuarioDAO);
        fecharConexao(instituicaoDAO);
        fecharConexao(investimentoDAO);

        System.out.println("\nTodas as conexões foram fechadas!");
    }

    private static void fecharConexao(AutoCloseable dao) {
        if (dao != null) {
            try {
                dao.close();
                System.out.println("Conexão fechada para: " + dao.getClass().getSimpleName());
            }
            catch (Exception e) {
                System.err.println("Erro ao fechar conexão para " + dao.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}