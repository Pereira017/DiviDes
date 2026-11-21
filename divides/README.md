# DiviDes (versão Java / Swing)

## Como rodar no VS Code
1. Instale o JDK 17 ou superior.
2. No VS Code, instale a extensão **Extension Pack for Java**.
3. Abra a pasta `divides` (File > Open Folder).
4. Abra `src/Main.java` e clique em **Run** (acima do `main`).

Sem VS Code, pelo terminal, dentro da pasta `divides`:

    javac -encoding UTF-8 -d out src/*.java
    java -cp out Main

Os dados ficam no arquivo `dados.bin`, e os anexos (fotos/PDF) na pasta `anexos/`, ambos criados na pasta onde o programa roda.

**Atenção:** esta versão mudou a estrutura dos dados (de acordo com o banco DIVDES). Um `dados.bin` salvo pela versão anterior não é lido pela nova, então apague `dados.bin` e a pasta `anexos/` antes de rodar.

## Regras principais
- Quem cria o evento vira administrador dele. Ser administrador de um evento não dá essa permissão em outros.
- Cada despesa gera uma conta. As pessoas dessa conta devem a sua parte (obrigação), que só pode estar paga ou pendente.
- Quem pagou a despesa não deve a si mesmo: a parte dele já nasce paga.
- Dar baixa: o administrador do evento ou quem recebeu (quem pagou a despesa).
- Não dá para editar ou excluir uma despesa que já tenha baixa de outra pessoa. Desfaça a baixa antes.

## Arquivos
- Modelo (tabelas do banco): Usuario (Usuarios), Membro (Membros), Evento (Eventos, com Membros_Eventos), Despesa (Despesas), Conta (Contas), Obrigacao (Obrigacoes), Tipo (Tipos)
- Auxiliares do modelo: Sequencia (gera os códigos)
- Persistência: Banco (salva tudo em dados.bin), Anexos (copia/abre/apaga os arquivos anexados)
- Contas (saldos e totais): Calculadora
- Telas: Janela, PainelLogin, PainelGrupos, PainelGrupo, AbaMembros, AbaGastos, AbaSaldos, AbaResumo
- Auxiliares de tela: Cabecalho, GraficoBarras, Formato, Cores, Main

## Campos fora do banco (decidir se ficam)
- `moeda` do evento e anexos (foto/PDF) da despesa não existem no banco DIVDES. Continuam no app.
