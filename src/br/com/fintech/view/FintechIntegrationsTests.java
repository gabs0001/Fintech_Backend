package br.com.fintech.view;

import br.com.fintech.dao.*;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Categoria;
import br.com.fintech.model.Instituicao;
import br.com.fintech.service.*;

import java.sql.SQLException;

public class FintechIntegrationsTests {
    private static final Long USUARIO_ID_TESTE = 1L;
    private static final Long CATEGORIA_GASTO_ID = 1L;
    private static final Long CATEGORIA_RECEBIMENTO_ID = 1L;

    public static void main(String[] args) {
        // --- DAOs ---
        GastoDAO gastoDAO = null;
        RecebimentoDAO recebimentoDAO = null;
        UsuarioDAO usuarioDAO = null;
        InstituicaoDAO instituicaoDAO = null;
        InvestimentoDAO investimentoDAO = null;

        // --- SERVICES ---
        GastoService gastoService;
        RecebimentoService recebimentoService;
        UsuarioService usuarioService;
        InstituicaoService instituicaoService;
        InvestimentoService investimentoService;
        RelatorioService relatorioService;

        // Entidades criadas no fluxo
        Instituicao instituicaoCriada = null;

        // Simulação das categorias
        Categoria categoriaGasto = new Categoria(CATEGORIA_GASTO_ID, "Gasto de Teste");
        Categoria categoriaRecebimento = new Categoria(CATEGORIA_RECEBIMENTO_ID, "Recebimento de Teste");

        try {
            // --- 1. INICIALIZAÇÃO DE DAOs E SERVICES ---
            gastoDAO = new GastoDAO();
            recebimentoDAO = new RecebimentoDAO();
            usuarioDAO = new UsuarioDAO();
            instituicaoDAO = new InstituicaoDAO();
            investimentoDAO = new InvestimentoDAO();

            gastoService = new GastoService(gastoDAO);
            recebimentoService = new RecebimentoService(recebimentoDAO);
            usuarioService = new UsuarioService(usuarioDAO);
            instituicaoService = new InstituicaoService(instituicaoDAO);
            investimentoService = new InvestimentoService(investimentoDAO);

            relatorioService = new RelatorioService(gastoDAO, recebimentoDAO, investimentoDAO);

            System.out.println("-----------------------------------------------------------------");
            System.out.println("            INICIANDO TESTES DE INTEGRAÇÃO FINTECH");
            System.out.println("-----------------------------------------------------------------");


            // -----------------------------------------------------------------
            // 2. TESTES DE ENTIDADES DE APOIO
            // -----------------------------------------------------------------
            instituicaoCriada = FintechTestRunner.testeInstituicaoInsert(instituicaoService);

            // -----------------------------------------------------------------
            // 3. TESTES DE TRANSAÇÕES (INSERÇÃO PARA O RELATÓRIO)
            // -----------------------------------------------------------------

            // Inserir gastos com datas variadas (para teste de Saldo Período)
            FintechTestRunner.testeGastoInsert(gastoService, USUARIO_ID_TESTE, categoriaGasto);

            // Inserir recebimentos
            FintechTestRunner.testeRecebimentoCrud(recebimentoService, USUARIO_ID_TESTE, categoriaRecebimento);

            // Inserir investimento (para teste de Total Investido)
            FintechTestRunner.testeInvestimentoCrud(investimentoService, USUARIO_ID_TESTE, categoriaGasto, instituicaoCriada);

            // -----------------------------------------------------------------
            // 4. TESTES DE RELATÓRIO (APÓS A INSERÇÃO DOS DADOS)
            // -----------------------------------------------------------------
            FintechTestRunner.testeRelatorioDashboard(relatorioService, USUARIO_ID_TESTE);

            // -----------------------------------------------------------------
            // 5. TESTES DE CONSULTA E UPDATE
            // -----------------------------------------------------------------
            System.out.println("\n\n--- TESTES DE CONSULTA E UPDATE ---");
            FintechTestRunner.testeGastoGetAll(gastoService);
            FintechTestRunner.testeUsuarioUpdate(usuarioService, USUARIO_ID_TESTE);

            // -----------------------------------------------------------------
            // 6. TESTES DE REMOÇÃO E EXCEÇÃO
            // -----------------------------------------------------------------
            System.out.println("\n\n--- TESTES DE REMOÇÃO ---");
            // Nota: O teste GastoRemove usa GastoService, não GastoDAO
            FintechTestRunner.testeGastoRemove(gastoService, USUARIO_ID_TESTE, categoriaGasto);

            // -----------------------------------------------------------------
            // 7. TESTE FINAL DE REMOÇÃO DE USUÁRIO (deve falhar por integridade)
            // -----------------------------------------------------------------
            FintechTestRunner.testeUsuarioRemove(usuarioService, USUARIO_ID_TESTE);


        }
        catch(EntityNotFoundException e) {
            System.out.println("✅ Exceção Capturada (EntityNotFound): " + e.getMessage());
        }
        catch(IllegalArgumentException e) {
            System.err.println("❌ Erro de Validação: " + e.getMessage());
        }
        catch(SQLException e) {
            System.err.println("❌ Erro de SQL (Verifique a conexão e o schema do banco): " + e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e) {
            System.err.println("❌ Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            System.out.println("\n-----------------------------------------------------------------");
            FintechTestRunner.fecharConexoes(gastoDAO, recebimentoDAO, usuarioDAO, instituicaoDAO, investimentoDAO);
            System.out.println("-----------------------------------------------------------------");
        }
    }
}