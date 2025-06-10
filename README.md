# Baseball Elimination

Este projeto implementa o clássico problema de **eliminação de times de baseball**, utilizando fluxo em rede (Ford–Fulkerson) para determinar, em cada instante da temporada, quais times já não têm mais possibilidade matemática de alcançar o primeiro lugar.

---

## Estrutura do Projeto

```
/ (raiz do repositório)
├── lib/
│   └── algs4.jar           # Biblioteca de suporte (algoritmos do livro de Sedgewick & Wayne)
├── project/
│   └── baseball/
│       ├── teams4.txt
│       ├── teams8.txt
│       ├── teams12.txt
│       └── ...             # Diversos arquivos de teste com formatos "teamsN.txt"
├── doc/
│   └── Lecture14_Baseball_Elimination.pdf  # Material de referência da aula
├── imgs/                   # Imagens (logo, diagramas etc.)
├── BaseballElimination.java  # Código-fonte principal
└── README.md               # Este arquivo
```

## Pré-requisitos

* **Java 8+** instalado (`java` e `javac` no PATH)
* **algs4.jar** (biblioteca da Princeton) dentro de `lib/`

## Como Compilar

Abra um terminal na pasta raiz do projeto e execute:

### Linux / macOS

```bash
javac -cp ".:lib/algs4.jar" BaseballElimination.java
```

### Windows (PowerShell)

```powershell
javac -cp ".;lib\algs4.jar" BaseballElimination.java
```

Se tudo ocorrer bem, você verá o arquivo `BaseballElimination.class` (e `BaseballElimination$NetworkData.class`) na mesma pasta.

## Como Executar

Após a compilação, rode:

### Linux / macOS

```bash
java -cp ".:lib/algs4.jar" BaseballElimination
```

### Windows (PowerShell)

```powershell
java -cp ".;lib\algs4.jar" BaseballElimination
```

O programa buscará automaticamente os arquivos de teste em `project/baseball/` (por padrão: `teams4.txt`, `teams8.txt`, `teams12.txt`, `teams24.txt`) e imprimirá para cada time:

* Se está **eliminado** (`is eliminated`) e a lista de times que causaram a eliminação (certificado);
* Ou se **não está eliminado** (`is not eliminated`).

### Exemplo de Saída

```
==============================
Testing teams4.txt
Atlanta is not eliminated
Philadelphia is not eliminated
New_York is not eliminated
Montreal is eliminated by Atlanta
...
```

## Personalização e Testes Adicionais

* Para testar outros arquivos, edite o método `main` em `BaseballElimination.java` ou passe o caminho do arquivo como parâmetro.
* Integre essa classe no seu projeto maior movendo-a para o pacote/fonte desejado e ajustando o classpath.

## Sobre o Algoritmo

1. **Eliminação trivial**: verifica se algum outro time já tem mais vitórias do que o máximo possível do time em análise.
2. **Eliminação não-trivial**: constrói uma rede de fluxo

   * Nós de jogo (pares de confrontos) e nós de time;
   * Capacidades de arestas definidas por jogos restantes e margem de vitória;
   * Executa Ford–Fulkerson para decidir se há fluxo suficiente.
3. **Corte mínimo**: identifica quais times formam o certificado de eliminação.

##
