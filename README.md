# Casa em Dia

Aplicativo Android para organizar tarefas domésticas, construído em Kotlin como projeto de portfólio para demonstrar práticas esperadas de uma pessoa desenvolvedora Android pleno.

## Funcionalidades

- criação, edição, conclusão, reabertura e exclusão com desfazer;
- prazos estruturados com seletor de data e horário;
- lembretes opcionais e conclusão pela notificação;
- categorias, busca e filtros;
- persistência offline com Room;
- sugestões remotas com Retrofit, OkHttp e Moshi e fallback/cache local;
- temas claro e escuro, edge-to-edge e suporte básico a acessibilidade;
- tela de privacidade e versão do aplicativo.

## Stack

Kotlin, Android Views/XML, Fragments, Navigation, ViewModel, StateFlow, Coroutines, Room, WorkManager, Retrofit, OkHttp, Moshi, JUnit, Mockito, Espresso e GitHub Actions.

## Arquitetura

O projeto usa UI, domínio e dados com fluxo unidirecional, casos de uso e repositórios. Consulte [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Executando

1. Abra o diretório no Android Studio.
2. Aguarde o Gradle Sync.
3. Selecione um dispositivo com Android 8.0 (API 26) ou superior.
4. Execute a configuração `app`.

Validação local:

```bash
./gradlew testDebugUnitTest lintDebug lintRelease assembleDebug bundleRelease
```

Os testes instrumentados exigem emulador ou aparelho conectado:

```bash
./gradlew connectedDebugAndroidTest
```

## Release

A assinatura é lida somente por variáveis de ambiente e nenhum keystore é versionado. Veja [docs/PLAY_STORE_CHECKLIST.md](docs/PLAY_STORE_CHECKLIST.md) e [docs/PRIVACY_POLICY.md](docs/PRIVACY_POLICY.md).
