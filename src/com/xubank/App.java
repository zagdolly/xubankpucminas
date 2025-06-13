package com.xubank;

import com.xubank.model.*;
import com.xubank.service.XuBankService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
    private static XuBankService xuBankService = new XuBankService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Bem-vindo ao XuBank!");
        // Popular com alguns dados para teste
        popularDadosTeste();


        boolean executando = true;
        while (executando) {
            exibirMenuPrincipal();
            int opcao = lerOpcao();
            switch (opcao) {
                case 1:
                    cadastrarNovoCliente();
                    break;
                case 2:
                    acessarCliente();
                    break;
                case 3:
                    menuDiretoria();
                    break;
                case 4:
                    xuBankService.processarViradaDeMes();
                    break;
                case 0:
                    executando = false;
                    System.out.println("Obrigado por usar o XuBank. Até logo!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
            pressioneEnterParaContinuar();
        }
        scanner.close();
    }
    
    private static void popularDadosTeste() {
        Cliente c1 = xuBankService.cadastrarCliente("João Silva", "11122233344", "senha123");
        if (c1 != null) {
            xuBankService.criarContaCorrente(c1, 1000, 500);
            xuBankService.criarContaPoupanca(c1, 2000);
        }

        Cliente c2 = xuBankService.cadastrarCliente("Maria Oliveira", "55566677788", "outrasenha");
        if (c2 != null) {
            xuBankService.criarContaRendaFixa(c2, 5000);
            ContaInvestimento ci = xuBankService.criarContaInvestimento(c2, 10000);
            // Simular um saldo negativo inicial para teste de taxa
            ContaCorrente ccNegativa = xuBankService.criarContaCorrente(c2, 0, 100);
            ccNegativa.sacar(50); // Deixa saldo -50
        }
         System.out.println("\nDados de teste populados.\n");
    }


    private static void exibirMenuPrincipal() {
        System.out.println("\n--- Menu Principal XuBank ---");
        System.out.println("1. Cadastrar Novo Cliente");
        System.out.println("2. Acessar Cliente (Login)");
        System.out.println("3. Funções da Diretoria");
        System.out.println("4. Simular Passagem de Mês (Aplicar Rendimentos/Taxas)");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void cadastrarNovoCliente() {
        System.out.println("\n--- Cadastro de Novo Cliente ---");
        System.out.print("Nome completo: ");
        String nome = scanner.nextLine();
        System.out.print("CPF (somente números): ");
        String cpf = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        xuBankService.cadastrarCliente(nome, cpf, senha);
    }

    private static void acessarCliente() {
        System.out.println("\n--- Acesso Cliente ---");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Cliente cliente = xuBankService.buscarClientePorCpf(cpf);
        if (cliente != null && cliente.autenticar(senha)) {
            System.out.println("Login bem-sucedido! Bem-vindo(a), " + cliente.getNome());
            menuClienteLogado(cliente);
        } else {
            System.out.println("CPF ou Senha inválidos.");
        }
    }

    private static void menuClienteLogado(Cliente cliente) {
        boolean clienteLogado = true;
        while (clienteLogado) {
            System.out.println("\n--- Menu do Cliente: " + cliente.getNome() + " ---");
            System.out.println("1. Criar Nova Conta");
            System.out.println("2. Acessar Conta Existente");
            System.out.println("3. Ver Saldo Total Consolidado");
            System.out.println("0. Voltar ao Menu Principal (Logout)");
            System.out.print("Escolha uma opção: ");

            int opcao = lerOpcao();
            switch (opcao) {
                case 1:
                    criarNovaContaParaCliente(cliente);
                    break;
                case 2:
                    acessarContaExistente(cliente);
                    break;
                case 3:
                    System.out.printf("Saldo total consolidado: R$ %.2f%n", cliente.getSaldoTotalConsolidado());
                    break;
                case 0:
                    clienteLogado = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
             if(clienteLogado) pressioneEnterParaContinuar();
        }
    }

    private static void criarNovaContaParaCliente(Cliente cliente) {
        System.out.println("\n--- Criar Nova Conta para " + cliente.getNome() + " ---");
        System.out.println("Qual tipo de conta deseja criar?");
        System.out.println("1. Conta Corrente");
        System.out.println("2. Conta Poupança");
        System.out.println("3. Conta Renda Fixa");
        System.out.println("4. Conta Investimento");
        System.out.println("0. Cancelar");
        System.out.print("Escolha o tipo: ");
        int tipoConta = lerOpcao();

        System.out.print("Saldo inicial: R$ ");
        double saldoInicial = lerDouble();

        switch (tipoConta) {
            case 1:
                System.out.print("Limite de crédito para Conta Corrente: R$ ");
                double limite = lerDouble();
                xuBankService.criarContaCorrente(cliente, saldoInicial, limite);
                break;
            case 2:
                xuBankService.criarContaPoupanca(cliente, saldoInicial);
                break;
            case 3:
                xuBankService.criarContaRendaFixa(cliente, saldoInicial);
                break;
            case 4:
                xuBankService.criarContaInvestimento(cliente, saldoInicial);
                break;
            case 0:
                System.out.println("Criação de conta cancelada.");
                break;
            default:
                System.out.println("Tipo de conta inválido.");
        }
    }

    private static void acessarContaExistente(Cliente cliente) {
        List<Conta> contasDoCliente = cliente.getContas();
        if (contasDoCliente.isEmpty()) {
            System.out.println("Você ainda não possui contas.");
            return;
        }

        System.out.println("\n--- Suas Contas ---");
        for (int i = 0; i < contasDoCliente.size(); i++) {
            Conta conta = contasDoCliente.get(i);
            System.out.printf("%d. %s - Número: %s, Saldo: R$ %.2f%n",
                    i + 1, conta.getTipoConta(), conta.getNumeroConta(), conta.getSaldo());
        }
        System.out.println("0. Voltar");
        System.out.print("Escolha uma conta para acessar (ou 0 para voltar): ");
        int escolha = lerOpcao();

        if (escolha > 0 && escolha <= contasDoCliente.size()) {
            menuOperacoesConta(contasDoCliente.get(escolha - 1));
        } else if (escolha != 0) {
            System.out.println("Seleção de conta inválida.");
        }
    }

    private static void menuOperacoesConta(Conta conta) {
        boolean operandoConta = true;
        while (operandoConta) {
            System.out.println("\n--- Operações para Conta " + conta.getNumeroConta() + " (" + conta.getTipoConta() + ") ---");
            System.out.println("Saldo Atual: R$ " + String.format("%.2f", conta.getSaldo()));
            if (conta instanceof ContaCorrente) {
                 System.out.println("Limite de Crédito: R$ " + String.format("%.2f", ((ContaCorrente) conta).getLimiteCredito()));
            }
            if (conta instanceof ContaRendaFixa || conta instanceof ContaInvestimento) {
                System.out.println("Rendimento Não Tributado Acumulado: R$ " + String.format("%.2f", conta.getRendimentoNaoTributado()));
            }


            System.out.println("1. Depositar");
            System.out.println("2. Sacar");
            System.out.println("3. Ver Extrato do Último Mês");
            System.out.println("0. Voltar para Contas do Cliente");
            System.out.print("Escolha uma operação: ");

            int opcao = lerOpcao();
            switch (opcao) {
                case 1:
                    System.out.print("Valor para depósito: R$ ");
                    double valorDeposito = lerDouble();
                    xuBankService.realizarDeposito(conta, valorDeposito);
                    break;
                case 2:
                    System.out.print("Valor para saque: R$ ");
                    double valorSaque = lerDouble();
                    xuBankService.realizarSaque(conta, valorSaque);
                    break;
                case 3:
                    exibirExtrato(conta);
                    break;
                case 0:
                    operandoConta = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
            if(operandoConta) pressioneEnterParaContinuar();
        }
    }
    
    private static void exibirExtrato(Conta conta) {
        System.out.println("\n--- Extrato do Último Mês para Conta " + conta.getNumeroConta() + " ---");
        List<Transacao> extrato = conta.emitirExtratoUltimoMes();
        if (extrato.isEmpty()) {
            System.out.println("Nenhuma transação no último mês.");
        } else {
            extrato.forEach(System.out::println);
        }
    }


    private static void menuDiretoria() {
        boolean menuAtivo = true;
        while (menuAtivo) {
            System.out.println("\n--- Menu da Diretoria XuBank ---");
            System.out.println("1. Valor total em custódia por tipo de conta");
            System.out.println("2. Saldo médio das contas");
            System.out.println("3. Cliente com maior saldo total");
            System.out.println("4. Cliente com menor saldo total");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            int opcao = lerOpcao();
            switch (opcao) {
                case 1:
                    Map<String, Double> custodia = xuBankService.getValorCustodiaPorTipoConta();
                    System.out.println("Valor em custódia por tipo de conta:");
                    custodia.forEach((tipo, valor) -> System.out.printf("- %s: R$ %.2f%n", tipo, valor));
                    break;
                case 2:
                    System.out.printf("Saldo médio das contas: R$ %.2f%n", xuBankService.getSaldoMedioContas());
                    break;
                case 3:
                    Cliente clienteMaiorSaldo = xuBankService.getClienteMaiorSaldo();
                    if (clienteMaiorSaldo != null) {
                        System.out.println("Cliente com maior saldo total: " + clienteMaiorSaldo);
                    } else {
                        System.out.println("Nenhum cliente cadastrado.");
                    }
                    break;
                case 4:
                    Cliente clienteMenorSaldo = xuBankService.getClienteMenorSaldo();
                     if (clienteMenorSaldo != null) {
                        System.out.println("Cliente com menor saldo total: " + clienteMenorSaldo);
                    } else {
                        System.out.println("Nenhum cliente cadastrado.");
                    }
                    break;
                case 0:
                    menuAtivo = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
            if(menuAtivo) pressioneEnterParaContinuar();
        }
    }


    private static int lerOpcao() {
        try {
            int opcao = Integer.parseInt(scanner.nextLine());
            return opcao;
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            return -1; // Retorna um valor que geralmente é tratado como inválido
        }
    }

    private static double lerDouble() {
        try {
            double valor = Double.parseDouble(scanner.nextLine());
            return valor;
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, digite um valor numérico.");
            return -1; // Ou lançar exceção / pedir novamente
        }
    }
    
    private static void pressioneEnterParaContinuar() {
        System.out.println("\nPressione Enter para continuar...");
        scanner.nextLine();
    }
}