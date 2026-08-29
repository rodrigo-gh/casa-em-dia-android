# Checklist de publicação

- [ ] Criar a upload key e armazená-la fora do repositório.
- [ ] Configurar `CASA_EM_DIA_KEYSTORE_PATH`, `CASA_EM_DIA_KEYSTORE_PASSWORD`, `CASA_EM_DIA_KEY_ALIAS` e `CASA_EM_DIA_KEY_PASSWORD`.
- [ ] Executar `./gradlew testDebugUnitTest lintDebug lintRelease connectedDebugAndroidTest bundleRelease`.
- [ ] Confirmar a assinatura do AAB e ativar Play App Signing.
- [ ] Publicar esta política de privacidade em uma URL pública.
- [ ] Preencher o formulário Segurança dos dados declarando que não há coleta de dados pessoais.
- [ ] Gerar screenshots de telefone, ícone 512 × 512 e feature graphic 1024 × 500.
- [ ] Realizar teste fechado quando exigido para a conta de desenvolvedor.
- [ ] Fazer upload inicial no canal de testes internos e revisar o relatório de pré-lançamento.
