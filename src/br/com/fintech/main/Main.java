package br.com.fintech.main;

import br.com.fintech.dao.*;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.service.*;

import java.sql.SQLException;

public class Main {
    // registros de teste criados no banco
    private static final Long USUARIO_ID_TESTE = 5L;
    private static final Long CATEGORIA_GASTO_ID = 3L;
    private static final Long CATEGORIA_RECEBIMENTO_ID = 2L;

    public static void main(String[] args) throws SQLException {
        GastoDAO gastoDAO = null;
        GastoService gastoService;

        RecebimentoDAO recebimentoDAO = null;
        RecebimentoService recebimentoService;

        UsuarioDAO usuarioDAO = null;
        UsuarioService usuarioService = null;

        try {
            gastoDAO = new GastoDAO();
            gastoService = new GastoService(gastoDAO);

            recebimentoDAO = new RecebimentoDAO();
            recebimentoService = new RecebimentoService(recebimentoDAO);

            usuarioDAO = new UsuarioDAO();
            usuarioService = new UsuarioService(usuarioDAO);

            // -----------------------------------------------------------------
            //                          TESTES DE GASTO
            // -----------------------------------------------------------------

            System.out.println("--- INICIANDO TESTE DE INTEGRAÇÃO GASTO ---");

            // TESTE DE CADASTRO (INSERT)
            FintechTestRunner.testeGastoInsert(gastoDAO, USUARIO_ID_TESTE, CATEGORIA_GASTO_ID);

            // TESTE DE CONSULTA (GET ALL)
            FintechTestRunner.testeGastoGetAll(gastoDAO);

            // TESTE DE REMOÇÃO (REMOVE e Exceção)
            FintechTestRunner.testeGastoRemove(gastoService, USUARIO_ID_TESTE, CATEGORIA_GASTO_ID);

            // -----------------------------------------------------------------
            //                       TESTES DE RECEBIMENTO
            // -----------------------------------------------------------------

            System.out.println("\n\n--- INICIANDO TESTE DE INTEGRAÇÃO RECEBIMENTO ---");

            // TESTE DE CADASTRO, CONSULTA e ATUALIZAÇÃO (INSERT, GET ALL, UPDATE e Exceção)
            FintechTestRunner.testeRecebimentoCrud(recebimentoService, USUARIO_ID_TESTE, CATEGORIA_RECEBIMENTO_ID);

            // -----------------------------------------------------------------
            //                        TESTES DE USUÁRIO
            // -----------------------------------------------------------------

            System.out.println("\n\n--- INICIANDO TESTE DE INTEGRAÇÃO USUÁRIO ---");

            // TESTE DE ATUALIZAÇÃO (UPDATE e Exceção)
            FintechTestRunner.testeUsuarioUpdate(usuarioService, USUARIO_ID_TESTE);
        }
        catch(EntityNotFoundException e) {
            System.out.println("✅ Exceção Capturada (EntityNotFound): " + e.getMessage());
            FintechTestRunner.testeUsuarioRemove(usuarioService, USUARIO_ID_TESTE);
        }
        catch(IllegalArgumentException e) {
            System.err.println("❌ Erro de Validação: " + e.getMessage());
        }
        catch(SQLException e) {
            System.err.println("❌ Erro de SQL: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            FintechTestRunner.fecharConexoes(gastoDAO, recebimentoDAO, usuarioDAO);
        }
    }
}