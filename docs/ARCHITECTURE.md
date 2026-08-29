# Arquitetura

O Casa em Dia usa uma arquitetura em camadas com fluxo unidirecional de dados.

```text
Views (Activity/Fragments/XML)
          ↓ ações
      ViewModels
          ↓
       Use cases
          ↓
     Repositories
       ↙       ↘
Room (SSOT)   Retrofit + OkHttp + Moshi
          ↓
       WorkManager
```

- **UI:** Fragments apresentam `StateFlow` produzido pelos ViewModels e enviam ações do usuário.
- **Domínio:** casos de uso concentram validação, timestamps, conclusão, exclusão/restauração e agendamento de lembretes.
- **Dados:** Room é a fonte de verdade para tarefas e para o cache do catálogo remoto.
- **Rede:** Retrofit consome um JSON público, OkHttp configura o cliente e Moshi gera os adapters.
- **Trabalho persistente:** WorkManager agenda um trabalho único por tarefa e o substitui após edições.
- **Injeção:** `AppContainer` fornece dependências sem acoplar ViewModels a implementações concretas.

O banco possui schema exportado e migração testada da versão 1 para a versão 2.
