# Explorando Apache Kafka

Must Read:
- [Apache Kafka Docs](https://kafka.apache.org/) - Apache Kafka
- [Apache Kafka Source Code](https://github.com/apache/kafka) - Apacha Kafka
- [Quem é Franz Kafka](https://pt.wikipedia.org/wiki/Franz_Kafka) - Wikipédia
- [Apache Kafka: O que é e como funciona?](https://blog.dp6.com.br/apache-kafka-o-que-%C3%A9-e-como-funciona-300a5736e388) - DP6 Team

## Introdução

### O Problema: Arquitetura Síncrona
Em uma arquitetura síncrona, os sistemas dependem de respostas imediatas de outros sistemas para concluir suas tarefas. Por exemplo, em uma operação de pagamento, o sistema A precisa se comunicar com o sistema B, que por sua vez se conecta ao sistema C. Se o sistema C falhar, essa falha se propaga por toda a cadeia, interrompendo a operação.

As principais características de uma arquitetura síncrona incluem:

* **Alto acoplamento**: Os sistemas são fortemente dependentes uns dos outros.
* **Latência em cascata**: Atrasos em um sistema afetam diretamente o desempenho dos outros.
* **Propagação de falhas**: Erros em um ponto do sistema rapidamente afetam toda a aplicação.

### A Solução: Arquitetura Assíncrona
Em uma arquitetura assíncrona, os sistemas podem operar de forma independente, sem a necessidade de esperar respostas imediatas uns dos outros. Um mediador entre eles garante que as tarefas sejam executadas de maneira desacoplada. Por exemplo, o sistema A executa uma operação que gera um evento, que pode ser consumido pelos sistemas B e C de forma independente, sem que A dependa do sucesso imediato de ambos.

As características principais de uma arquitetura assíncrona são:

**Comunicação desacoplada**: Os sistemas se comunicam através de eventos sem precisar de respostas imediatas.
**Responsabilidade única**: Cada sistema realiza apenas sua tarefa específica, seguindo mais ou menos o princípio **S** do **SOLID**.
**Eventos**: A comunicação é feita através de mensagens/eventos.

### Finalmente: Apache Kafka
O Apache Kafka é uma plataforma distribuída e altamente escalável, projetada para o processamento de grandes volumes de dados em tempo real. Ele atua como o mediador central em uma arquitetura assíncrona, transmitindo mensagens ou eventos entre diferentes sistemas, permitindo uma comunicação eficiente e resiliente, independentemente de falhas momentâneas nos serviços.

Doravante entenderemos com mais detalhes o que é o Apache Kafka.

## Apacha Kafka
Pode-se dizer que o Kafka atua como um **broker de mensageria**, mas com uma funcionalidade estendida e mais avançada que brokers tradicionais como o **RabbitMQ** e o **ActiveMQ**, isso porque:
* **Armazenamento Durável**: o Kafka é desenhado para armazenar dados por um período de tempo configurável, ao contrário de muitos sistemas de mensageria que "descartam" a mensagem assim que ela é consumida.
* **Alto Throughput**: é desenhado para lidar com milhões de mensagens por segundo com baixa latência, sendo ideal para trabalhar com o processamento de grandes fluxos de dados em tempo real.
* **Processamento de Eventos**: não é apenas um broker de mesagens, mas uma plataforma completa para streaming de dados. Tem integração com o **Kafka Streams**, permitindo o processamento e transformação de dados em tempo real.

> **Sobre brokers**: Um broker (ou corretor) é um intermediário em sistemas de comunicação que facilita a troca de informações ou dados entre diferentes partes. No contexto de sistemas distribuídos e mensageria, um broker de mensagens é um componente que intermedeia a comunicação entre diferentes aplicações ou serviços, gerenciando o envio, roteamento e armazenamento temporário de mensagens de forma eficiente e segura.

> **Sobre mensageria**: Mensageria é o processo de troca de informações ou dados entre diferentes partes (aplicações, sistemas ou serviços) por meio de mensagens. Ela envolve o envio, o recebimento e o processamento dessas mensagens de maneira estruturada, mais comumente em um ambiente distribuído. A mensageria é bastante difundida em arquiteturas de software para integrar sistemas que precisam se comunicar com eficiência e confiabilidade.

### Características Relevantes:
* Criado em 2011 pelo time do **LinkedIn**
* Escrito em Scala e Java
* Open Source
* Principais autores: Jey Kreps, Neha Narkhede e Jun Rao
* Origem do nome: Franz Kafka

**Abaixo uma ilustração de uso**:
![image](https://hackmd.io/_uploads/Hy168SLTA.png)

### Apache ZooKeeper

O **Apache ZooKeeper** é base fundamental do funcionamento do Apache Kafka, atuando como uma ferramenta de coordenação e gerenciamento dentro de um cluster Kafka. Embora o Kafka esteja evoluindo para eliminar sua dependência do ZooKeeper, muitas versões e implementações do Kafka ainda utilizam o ZooKeeper como um componente fundamental.

#### Por que o Kafka usa o ZooKeeper?
Sabemos que o **Kafka** é um sistema distribuído, com múltiplos brokers trabalhando em conjunto para garantir a entrega e o armazenamento de mensagens.

Para coordenar todas essas interações entre brokers, consumidores e produtores, o Kafka depende do ZooKeeper para garantir que o cluster funcione corretamente e que cada componente esteja ciente de seu papel e estado dentro do sistema. O ZooKeeper mantém as informações críticas sobre:
* **Metadados de tópicos**: Tópicos, partições e o mapeamento entre partições e brokers.
* **Eleição de líderes**: Coordena a eleição de qual broker será o líder de uma partição, responsável por gerenciar as operações de escrita e leitura dessa partição.
* **Estado do cluster**: Mantém informações sobre quais brokers estão ativos, falhos ou desconectados, ajudando na recuperação e reconfiguração do sistema.

>**Sobre partições**: Uma partição é uma subdivisão de um tópico, que permite distribuir mensagens entre múltiplos brokers. Cada partição tem um broker líder responsável por gerenciar leituras e gravações, enquanto réplicas em outros brokers garantem alta disponibilidade e tolerância a falhas. As partições facilitam a escalabilidade, permitindo processamento paralelo de mensagens por consumidores e distribuindo a carga de trabalho entre diferentes nós do cluster Kafka. A ordem das mensagens é garantida dentro de uma partição, mas não entre partições diferentes do mesmo tópico.

#### Funções Do ZooKeeper No Kafka

##### Gerenciamento de Metadados e Partições
O ZooKeeper armazena informações detalhadas sobre os tópicos e suas partições. Cada tópico no Kafka é dividido em partições, e cada partição é atribuída a um broker para gerenciar. O ZooKeeper mantém um registro de qual broker é responsável por qual partição, garantindo que, em caso de falha, essas responsabilidades possam ser transferidas para outro broker.

##### Eleição de Líder de Partição
Uma das responsabilidades mais importantes do ZooKeeper no Kafka é a eleição de líderes para as partições. Cada partição de um tópico tem um broker líder, que é responsável por gerenciar as operações de leitura e escrita para aquela partição. Outros brokers que replicam a partição atuam como replicas. Se o líder de uma partição falhar (por exemplo, se o broker que o gerencia ficar offline), o ZooKeeper coordena uma nova eleição para escolher um novo líder entre as réplicas disponíveis.

Esse mecanismo é essencial para garantir a alta disponibilidade e tolerância a falhas no Kafka. Sempre que um broker falha ou é desconectado, o ZooKeeper assegura que outro broker assuma a função de líder da partição, minimizando o impacto sobre a disponibilidade dos dados.

##### Detecção de Falhas e Reconfiguração
O ZooKeeper monitora o estado dos brokers no cluster Kafka. Quando um broker se junta ao cluster ou se desconecta (por falha ou manutenção), o ZooKeeper notifica o Kafka, que reconfigura as partições e distribui as responsabilidades entre os brokers restantes. Essa capacidade de detecção de falhas é fundamental para garantir que o Kafka continue a operar mesmo em caso de falhas de hardware ou rede.

Se um broker falhar, o ZooKeeper atualiza os metadados do cluster e coordena a reatribuição das partições desse broker para outros brokers. Isso garante que o Kafka mantenha alta disponibilidade, mesmo em cenários de falhas.

##### Gerenciamento de Estados Efêmeros
No Kafka, o ZooKeeper também armazena dados efêmeros que refletem o estado temporário do sistema, como quais consumidores e produtores estão conectados. Isso permite que o Kafka detecte quando um cliente se desconecta inesperadamente e tome as medidas apropriadas, como reequilibrar o grupo de consumidores.

Os **znodes efêmeros** são usados para representar conexões de clientes e sessões temporárias. Quando um cliente (um broker, consumidor ou produtor) se desconecta, os znodes efêmeros associados são removidos automaticamente pelo ZooKeeper, ajudando a Kafka a gerenciar dinamicamente as mudanças no cluster.

#### Arquitetura Do ZooKeeper Dentro Do Kafka
Em um ambiente Kafka, o ZooKeeper atua como um servidor de coordenação separado do Kafka. Ele normalmente é implantado em um cluster ZooKeeper separado, que consiste em múltiplos servidores ZooKeeper (geralmente um número ímpar para evitar empates). O Kafka, por sua vez, consiste em múltiplos brokers que se comunicam com o ZooKeeper para obter informações sobre o estado do cluster e coordenar operações críticas.

##### Servidores ZooKeeper (Ensemble)
Um ensemble ZooKeeper consiste em vários servidores ZooKeeper, e eles são responsáveis por manter o consenso sobre o estado do cluster Kafka. O consenso é garantido pelo protocolo Zab (ZooKeeper Atomic Broadcast), que coordena a replicação de dados entre os servidores ZooKeeper. Isso garante que, mesmo se um servidor ZooKeeper falhar, o restante do ensemble possa continuar operando e fornecendo serviços ao Kafka.

##### Clientes Kafka
Os clientes do ZooKeeper no Kafka são os brokers. Cada broker Kafka se conecta ao ZooKeeper para obter informações sobre o estado do cluster e para se registrar como membro ativo. Quando os brokers precisam de dados sobre o estado de partições, ou precisam coordenar ações como a eleição de líderes, eles consultam o ZooKeeper.

#### Desafios e Limitações do ZooKeeper no Kafka
Embora o ZooKeeper desempenhe um papel importante no Kafka, ele trás consigo alguns probleminhas:

##### Ponto Único de Falha
O ZooKeeper pode ser considerado um ponto único de falha para o Kafka, pois sua indisponibilidade pode comprometer o funcionamento do cluster Kafka. Embora o ZooKeeper seja replicado e tolerante a falhas, se a maioria dos nós ZooKeeper falhar ou ficar indisponível, o Kafka não conseguirá executar corretamente suas operações.

##### Complexidade Operacional
A gestão de um cluster ZooKeeper adiciona complexidade à operação do Kafka, exigindo que os administradores mantenham e monitorem dois sistemas separados.

##### Limitações em Grandes Escalas
O ZooKeeper foi originalmente projetado para coordenação leve, e à medida que clusters Kafka se tornam extremamente grandes, a sobrecarga de operações no ZooKeeper pode aumentar. Isso levou a Apache Kafka a explorar alternativas e arquiteturas que minimizem a dependência do ZooKeeper, e que por ventura possibilitem a eliminação do ZooKeeper da jogada.

#### Kafka sem ZooKeeper (KRaft)
Dado o papel crítico do ZooKeeper, a Apache Kafka começou a desenvolver uma nova arquitetura chamada **KRaft (Kafka Raft)**. O objetivo dessa arquitetura é eliminar a dependência do ZooKeeper, integrando a coordenação e o gerenciamento de metadados diretamente no Kafka. A transição para o KRaft promete simplificar a arquitetura do Kafka e melhorar a escalabilidade e o desempenho, especialmente para grandes clusters.

O KRaft usa o protocolo Raft para coordenar as atividades entre os brokers e gerenciar a replicação e o consenso, funções que anteriormente dependiam do ZooKeeper.

### Produtores, Consumidores, Tópicos E Drivers

#### O Que São Tópicos?
No contexto do Apache Kafka, um tópico é o principal canal de comunicação para onde as mensagens são enviadas e de onde elas são consumidas. Em termos simples, um tópico pode ser comparado a uma fila ou um stream de dados categorizados, em que diferentes aplicações podem escrever ou ler informações.

Pense em tópicos como unidades lógicas, divididos fisicamente em partições, sendo que um tópico pode ter milhares de partições, conforme ilustração abaixo:

![image](https://hackmd.io/_uploads/By_6NiPpA.png)

##### Como Funcionam os Tópicos no Kafka?

###### Estrutura de Publicação e Assinatura
O Kafka segue um modelo publish/subscribe, onde os produtores enviam mensagens para um tópico e os consumidores leem essas mensagens.
Cada tópico armazena uma sequência de eventos que podem ser acessados por múltiplos consumidores. Isso significa que os dados não são removidos imediatamente após serem lidos, permitindo que vários consumidores processem o mesmo conjunto de eventos de maneiras diferentes, sem interferirem uns nos outros.

###### Partições
Um tópico e dividido em partições, podendo ter milhares delas. A divisão de um tópico em partições permite distribuir a carga de trabalho entre vários brokers no cluster Kafka, aumentando a escalabilidade e o desempenho. Quanto mais partições um tópico tiver, maior será o paralelismo de leitura e escrita. Segura aí, já já falaremos mais delas.

###### Persistência de Dados
O armazenamento de mensagens nos tópicos acontece de forma persistente, ou seja, as mensagens não são descartadas após o consumo. Elas são mantidas por um período de tempo configurado ou até que um limite de tamanho seja atingido. Isso permite que sistemas consumidores leiam dados históricos a qualquer momento, facilitando o reprocessamento de eventos.

###### Retenção de Mensagens
Cada tópico tem uma política de retenção configurável, que define por quanto tempo ou até que tamanho as mensagens serão mantidas no armazenamento. Essa política permite que o Kafka atue como uma fonte de verdade para os dados, garantindo que os consumidores possam acessar mensagens antigas para reprocessamento ou auditoria.

###### Tópicos Distribuídos
Em um cluster Kafka, os tópicos e suas partições são distribuídos entre vários brokers. Um broker específico será responsável por uma ou mais partições de um tópico, o que garante que o Kafka possa escalar horizontalmente para processar grandes volumes de dados.
Cada partição tem um broker líder que gerencia as operações de leitura e escrita, e outros brokers podem manter réplicas das partições para garantir alta disponibilidade e tolerância a falhas.


#### O Que São Partições?
As partições são divisões lógicas dentro de um tópico que organizam e armazenam as mensagens de maneira distribuída e paralela. Cada tópico no Kafka pode ter várias partições, e cada uma delas é uma sequência ordenada de eventos (mensagens). As partições são fundamentais para a escalabilidade e paralelismo do Kafka.
![image](https://hackmd.io/_uploads/rku263va0.png)

##### Características das Partições

###### Sequência Ordenada de Mensagens (Princípio FIFO)
Dentro de uma única partição, as mensagens são gravadas em sequência, cada uma com um número único chamado offset. Os consumidores leem as mensagens seguindo essa sequência, garantindo que a primeira mensagem produzida seja a primeira a ser lida.
O FIFO é garantido apenas dentro de uma única partição. Se houver várias partições em um tópico, a ordem entre as partições não é garantida, pois diferentes partições podem ser processadas em paralelo e em ordens diferentes.

###### Distribuição de Dados
Um tópico com várias partições permite distribuir a carga de trabalho entre múltiplos brokers (servidores Kafka), o que melhora a escalabilidade. Cada partição de um tópico pode estar em um broker diferente, permitindo que diferentes sistemas escrevam e leiam dados simultaneamente.

###### Escalabilidade e Paralelismo
Quanto mais partições um tópico tem, maior a capacidade de o Kafka processar eventos em paralelo. Por exemplo, diferentes consumidores podem ler de diferentes partições ao mesmo tempo, aumentando o throughput da aplicação.

###### Replicação e Tolerância a Falhas
Cada partição pode ter réplicas distribuídas em outros brokers, o que garante tolerância a falhas. Se o broker responsável por uma partição falhar, uma das réplicas pode assumir a liderança e continuar processando as mensagens.

###### Processamento Independente
As partições permitem que diferentes consumidores processem diferentes partes do mesmo tópico de forma independente. Por exemplo, se um sistema consome eventos de uma partição e outro sistema consome eventos de outra, ambos podem operar sem interferências.

#### Estrutura Das Mensagens
Em Apache Kafka, as mensagens armazenadas em uma partição têm uma estrutura definida por 5 componentes:

##### Offset
É um identificador único para cada mensagem dentro de uma partição, onde cada partição tem sua sequência de offsets. É um número sequencial que começa em 0 e aumenta conforme novas mensagens são adicionadas. Ele é utilizado para garantir que as mensagens sejam lidas na ordem correta e para possibilitar a recuperação da posição de leitura em caso de falhas.
![image](https://hackmd.io/_uploads/BJM73YyRR.png)


##### Chave (Opcional)
A chave é um valor opcional que pode ser associado a uma mensagem. Ela é usada principalmente para garantir que mensagens com a mesma chave sejam direcionadas para a mesma partição. Além disso, pode ser usada para particionar dados de forma lógica, conforme as necessidades da aplicação.

##### Valor
O valor é o conteúdo principal da mensagem, ou seja, os dados que você deseja enviar. Esse pode ser qualquer tipo de dado, desde texto simples até arquivos binários, dependendo do formato e da codificação utilizada.

##### Cabeçalhos (Opcional)
Kafka permite que você adicione cabeçalhos às mensagens. Cabeçalhos são pares de chave-valor que podem ser utilizados para transportar metadados adicionais com a mensagem. Esses dados adicionais não fazem parte do corpo principal da mensagem e são usados para fornecer contexto ou informações auxiliares.

##### Timestamp
Kafka armazena um timestamp para cada mensagem, que indica o momento em que a mensagem foi produzida. Esse timestamp é útil para diferentes tipos de operações, como expiração de mensagens, ordenação temporal e análise de dados.

##### Particionamento De Mensagens
É interessante mencionar que o Kafka pode utilizar o hash de uma key para determinar em qual partição esta será armazenada. O comportamento exato depende de como a key da mensagem é configurada e das políticas de particionamento definidas no Kafka. Vou explicar essa dinâmica em três cenários comuns:


###### Baseado em key
Se uma mensagem tem uma key definida, Kafka usa essa chave para determinar a partição. O processo é geralmente o seguinte:

* Kafka aplica uma função hash à chave da mensagem.
  * O Kafka usa a estratégia de hash **murmur2** para gerar a chave de hash. Com a chave de hash gerada, ele executa uma função de módulo com o número de partições para o tópico do Kafka. Fica mais ou menos assim:
      ``` 
      murmur2("101") % 4 = partition 0
      murmur2("102") % 4 = partition 1
      murmur2("103") % 4 = partition 2
      murmur2("104") % 4 = partition 3
      ```
* O resultado do hash é dividido pelo número de partições disponíveis para o tópico.
* O restante da divisão (módulo) é o número da partição em que a mensagem será armazenada.

Essa estratégia garante que todas as mensagens com a mesma chave sejam sempre roteadas para a mesma partição. Isso é importante quando você deseja garantir que todas as mensagens relacionadas (por exemplo, todas as transações de um mesmo cliente) fiquem juntas e sejam processadas na ordem correta dentro de uma partição.

Exemplo:

* Tópico com 4 partições.
* Chave: "cliente123".
* Kafka aplica um hash à chave e calcula: `hash("cliente123") % 4`, resultando na partição 2.

###### Particionamento sem key
Se uma mensagem não tiver uma chave definida (ou a chave for nula), Kafka usa uma abordagem de particionamento mais simples. As mensagens são distribuídas **ciclicamente** ou de forma **round-robin** entre as partições disponíveis. Isso significa que Kafka balanceia a carga das mensagens de maneira uniforme entre as partições, para evitar que uma partição fique sobrecarregada.

>Round-robin (RR) é um dos algoritmos empregados por escalonadores de processo e de rede, em computação. Como o termo é geralmente usado, fatias de tempo (também conhecidas como quanta de tempo) são atribuídas a cada processo em partes iguais e em ordem circular, manipulando todos os processos sem prioridade (também conhecido como executivo cíclico).
>O nome do algoritmo vem do princípio round-robin conhecido de outros campos, onde cada pessoa pega um compartilhamento de algo igual por vez.

Neste caso, não há garantia de que mensagens relacionadas fiquem na mesma partição, pois a chave não foi fornecida para determinar esse agrupamento.

Exemplo:

* Tópico com 4 partições.
* Sem chave definida.
* Kafka coloca a primeira mensagem na partição 0, a segunda na partição 1, e assim por diante, até que todas as partições sejam usadas, e o ciclo recomeça.

###### Uso de um Custom Partitioner
Se você quiser personalizar a lógica de particionamento, Kafka também permite a implementação de um Custom Partitioner. Isso significa que, ao invés de usar o hash da chave ou o round-robin, você pode definir uma função específica para determinar para qual partição as mensagens devem ser enviadas, com base em regras próprias.

Exemplo:

* Um Custom Partitioner poderia enviar todas as mensagens relacionadas a usuários VIP para a partição 0, enquanto outras mensagens seriam distribuídas entre as partições restantes.


#### Produtores
No Apache Kafka, produtores (ou producers) são componentes responsáveis por enviar (ou publicar) mensagens para os tópicos. Eles  são a fonte de dados que alimenta o sistema.

##### Envio de Mensagens
Os produtores enviam dados para tópicos específicos dentro do Kafka. Cada mensagem publicada é gravada em um tópico, que pode ser configurado para ter múltiplas partições.

##### Particionamento
Os produtores podem escolher em qual partição de um tópico a mensagem será enviada. Isso pode ser feito de forma manual ou automática, com base em uma chave de particionamento, que garante que todas as mensagens com a mesma chave vão para a mesma partição.

##### Assíncrono e Síncrono
O envio de mensagens pode ser feito de forma assíncrona ou síncrona. O modo assíncrono é mais eficiente e permite que o produtor continue a enviar mensagens sem esperar a confirmação de entrega, enquanto o modo síncrono aguarda a confirmação antes de continuar.

##### Configurações de Desempenho
Os produtores podem ser configurados para otimizar o desempenho e a confiabilidade, ajustando parâmetros como o tamanho do buffer, a quantidade de confirmações necessárias e o tempo de espera para a confirmação.

##### Serialização
Os produtores precisam serializar as mensagens antes de enviá-las para o Kafka. Isso significa converter os dados para um formato que possa ser transmitido e armazenado pelo Kafka, como JSON, Avro, ou uma string.

##### Fault Tolerance
Kafka oferece mecanismos para garantir que as mensagens não sejam perdidas em caso de falhas. Os produtores podem ser configurados para garantir que as mensagens sejam replicadas e confirmadas antes de considerar o envio como bem-sucedido.

##### ACK (Acknowledgment, ou confirmação)
É o mecanismo de resposta que o Kafka usa para notificar o produtor de que a mensagem enviada foi recebida com sucesso. Esse processo de confirmação é uma parte importante da garantia de durabilidade e confiabilidade dos dados no Kafka.

O comportamento de ACK pode ser configurado através da propriedade acks no produtor, e as principais opções são:

###### `acks = 0`
* O produtor não espera por nenhuma confirmação do Kafka.
* Assim que a mensagem é enviada, o produtor considera o envio como bem-sucedido, mesmo que o broker do Kafka não tenha confirmado a recepção.
* Este modo tem o melhor desempenho, mas pode resultar em perda de mensagens se houver falha no broker antes da mensagem ser armazenada.

###### `acks = 1`
* O produtor espera a confirmação de que a mensagem foi recebida e gravada pelo líder da partição (o broker que está gerenciando a partição onde a mensagem será armazenada).
* Uma vez que o líder confirma a gravação, o produtor considera a mensagem como entregue.
* Este modo oferece um bom equilíbrio entre desempenho e segurança, mas, em caso de falha do líder antes que as réplicas tenham copiado a mensagem, existe o risco de perda.

###### `acks = all (ou -1)`
* O produtor espera a confirmação de que a mensagem foi gravada pelo líder e por todas as réplicas sincronizadas (outros brokers que mantêm cópias da partição).
* Isso garante a maior durabilidade possível, já que a mensagem só será considerada entregue após estar replicada de forma segura em todas as réplicas.
* Este modo é o mais seguro, mas também o mais lento, devido à espera pela confirmação de todas as réplicas.

#### Consumidores
Consumidores (ou consumers) são os componentes responsáveis por ler e processar mensagens de tópicos. Eles recebem as mensagens que os produtores publicam.

Aqui vão alguns aspectos importantes sobre os consumidores:

##### Grupos de Consumidores
Consumidores podem ser organizados em grupos (consumer groups), que são conjuntos lógicos que representam as aplicações que consumirão as informações dos tópicos Kafka. Cada consumer group pode possuir 1 ou muitos consumidores, e os consumidores são, na prática, as aplicações.
![image](https://hackmd.io/_uploads/ryUkS_kCA.png)

* Cada partição de um tópico é atribuída a apenas um consumidor dentro do grupo, tal qual cada mensagem de uma partição só será consumida por um único consumidor em cada consumer group. Isso proporciona escalabilidade e paralelismo, pois diferentes consumidores podem processar diferentes partições em paralelo.
* Se o número de consumidores for maior que o número de partições, alguns consumidores ficarão ociosos. Se houver menos consumidores que partições, alguns consumidores processarão mais de uma partição.
* Consumer groups distintos podem receber a mesma mensagem.
* Quando um consumer entra ou sai do grupo ocorre o evento de **Rebalance**.


##### Leitura de Mensagens
Consumidores leem as mensagens de um ou mais tópicos. Eles podem se conectar ao Kafka e consumir dados continuamente, processando as mensagens à medida que são produzidas.

Quando um consumidor se conecta a um tópico pela primeira vez, existe a opção de configurar uma propriedade chamada **Auto Offset Reset**. Essa propriedade tem dois valores, que são e determinam respectivamente:
* **EARLIEST**: Determina que, se um consumidor recém-conectado não tiver um offset commitado (ou seja, ele nunca processou mensagens desse tópico anteriormente), ele irá processar todas as mensagens a partir do menor offset disponível na partição.
* **LATEST**: Determina que, se um consumidor recém-conectado não tiver um offset commitado, ele irá começar a processar somente novas mensagens que forem adicionadas ao tópico a partir do momento da conexão.

Além disso, o Kafka oferece mecanismos para lidar com falhas temporárias ou erros durante o processamento das mensagens.

Um dos mecanismos é o retry, onde consumidores podem tentar processar novamente mensagens que falharam, com a lógica de retry podendo ser implementada manualmente ou por frameworks de processamento.

Outro recurso importante é o seek, que permite ao consumidor se reposicionar em um offset específico dentro de uma partição, possibilitando, por exemplo, reprocessar mensagens desde um determinado ponto ou saltar mensagens que já foram tratadas.

##### Offset
Cada mensagem em uma partição de um tópico possui um número de offset (já mencionado anteriormente), que é uma espécie de identificador exclusivo da posição da mensagem dentro da partição. O offset permite que os consumidores saibam quais mensagens já foram lidas e quais ainda precisam ser processadas. Consumidores podem controlar seus próprios offsets, permitindo retomar a leitura a partir de uma posição específica em caso de falhas.

Os consumidores de um consumer group compartilham o controle dos offsets de cada partição atribuída ao grupo. Isso significa que, para cada partição, há um único offset comprometido, o que evita que múltiplos consumidores dentro do mesmo grupo leiam a mesma mensagem.

O Apache Kafka utiliza um tópico interno para armazenar os commits de offset de cada consumer group, o nome desse tópico é `__consumer_offsets`. Este tópico `__consumer_offsets` é criado automaticamente pelo Kafka e é usado para armazenar os offsets consumidos por cada grupo de consumidores (consumer group). Sempre que um consumidor faz o commit de um offset (ou seja, confirma que processou até certo ponto em uma partição), essa informação é gravada nesse tópico interno.

##### Processamento Paralelo
O Kafka permite que diferentes consumidores processem partições separadas em paralelo. Isso maximiza o throughput de leitura e processamento de dados, escalando o sistema com base no número de partições.

Além disso, vale a pena mencionar que cada thread de um mesmo consumidor pode se inscrever em apenas uma única partição. Além disso, cada thread pode estar em apenas um único consumer group.

##### Polling
Diferente de sistemas que enviam as mensagens diretamente aos consumidores, no Kafka o consumidor faz a leitura das mensagens ativamente, através de um processo chamado **polling**. Isso significa que o consumidor, periodicamente, faz uma requisição ao broker do Kafka para obter novas mensagens. Isso dá mais controle ao consumidor sobre quando e como ele processa as mensagens.

##### Commit de Offsets
Os consumidores têm a responsabilidade de confirmar (ou fazer o commit) dos offsets das mensagens que processaram. Isso informa ao Kafka que o consumidor já leu as mensagens até um determinado ponto, e essa posição é armazenada de forma persistente para garantir que, em caso de falhas ou reinicialização, o consumidor não leia as mesmas mensagens novamente.

Veja um exemplo ilustrado na imagem abaixo:
![image](https://hackmd.io/_uploads/ByHfCYJCC.png)
* **Consumer Group A**:
  * O **consumer A** processou as mensagens da **partição 0** do **tópico A** até o **offset 200**. Isso significa que este consumer group já leu e processou até essa posição específica na partição.
* **Consumer Group B**:
  * O **consumer B** processou a mesma **partição 0** do **tópico A**, mas apenas até o **offset 2**. Este grupo está atrasado em relação ao **consumer A** e ainda precisa processar mensagens a partir do **offset 3**.
* **Independência de offsets**: Cada consumer group possui seu próprio conjunto de offsets, então o progresso do **consumer A** até o **offset 200** não afeta o progresso do **consumer B**, que está no **offset 2**.
* **Mensagens duplicadas?**: Embora ambos os grupos estejam consumindo a mesma partição, eles processam as mensagens de maneira independente. Portanto, a mensagem com **offset 2** foi processada por ambos, mas para diferentes propósitos. Não há problema nisso, pois consumer groups distintos geralmente têm diferentes tarefas a realizar com as mesmas mensagens.

###### Auto Commit
É possível configurar seu consumidor para fazer o auto commit. Dessa forma, toda mensagem que você receber será automaticamente comitada. Mas lembre-se que com esse cenário você poderá perder mensagens, portanto, é uma boa estratégia quando existe a possibilidade de perder mensagens.

###### Commit Manual
Em 99% dos casos, é a estratégia de commit mais adequada. Neste cenário é possível escolher quanto o commit será realizado, o que geralmente é feito após o processamento.

##### Garantias de Entrega e Estratégias de Produção de Mensagem

###### At-least-once
Essa estratégia garante que uma mensagem será entregue ao menos uma vez. Ou seja, mesmo que ocorra uma falha no envio ou no processamento, o Kafka tentará reenviar a mensagem até que ela seja confirmada pelo consumidor.
* Configuração do produtor: `acks=all` (ou `acks=-1`), o que significa que o Kafka espera a confirmação de todos os `min.insync.replicas` antes de considerar a mensagem como entregue.
* Configuração de réplicas: O parâmetro `min.insync.replicas` define quantas réplicas devem estar disponíveis para garantir a entrega.

Isso pode resultar em duplicações, por exemplo, se o consumidor processar a mensagem e falhar antes de confirmar o offset. Por isso, o consumidor deve ser capaz de lidar com duplicidades, geralmente implementando operações idempotentes para garantir que o processamento final não seja afetado por mensagens duplicadas.


###### At-most-once (fire and forget)
Neste modo, o Kafka pode entregar a mensagem no máximo uma vez, sem a garantia de que ela será efetivamente processada, pois a confirmação ocorre antes do processamento.
* Configuração do produtor: `acks=0`, ou seja, o produtor não espera qualquer confirmação de entrega dos brokers.
* No consumidor, se o offset for confirmado (committed) antes do processamento, e houver falha, a mensagem será "perdida", pois o Kafka já a considera entregue.

Isso significa que, neste modo, a mensagem pode não ser processada, mas nunca será processada mais de uma vez. Não há necessidade de idempotência no consumidor, pois ele não precisará lidar com duplicações.

###### Exactly-once
Essa é a garantia mais forte oferecida pelo Kafka, onde cada mensagem é processada exatamente uma vez, evitando tanto duplicações quanto perda de mensagens.
* Configuração do produtor: `enable.idempotence=true`, o que garante que o produtor possa reenviar mensagens sem gerar duplicações.
* Adicionalmente, o Kafka permite o uso de transações (`transactional.id` no produtor), o que garante a entrega e o processamento da mensagem exatamente uma vez em cenários de falhas.

Essa estratégia é a mais robusta e garante que nenhuma mensagem será perdida ou processada mais de uma vez, sem a necessidade de lidar com duplicações diretamente no consumidor. No entanto, exige configurações e maior complexidade de implementação no Kafka.

>Idempotência é um conceito da matemática e da ciência da computação que se refere a uma operação que pode ser aplicada múltiplas vezes sem alterar o resultado além da primeira aplicação. No contexto de processamento de mensagens, uma operação idempotente garante que, mesmo se a mesma mensagem for processada mais de uma vez, o resultado final não será impactado.

##### Sem Estado e Com Estado
Um consumidor pode trabalhar sem estado, onde apenas consome as mensagens e toma ações imediatas sem manter contexto anterior. Alternativamente, um consumidor pode trabalhar com estado, armazenando dados de sessão ou contexto à medida que lê as mensagens e toma decisões com base nas informações acumuladas.

##### Modos de Processamento

###### Consumidores Individuais
* Um único consumidor pode consumir todas as mensagens de todas as partições de um tópico. Este modelo é simples, mas não escala bem com grandes volumes de dados.

###### Grupos de Consumidores
* Este é o modelo mais comum, onde vários consumidores em um grupo de consumidores compartilham o processamento das partições de um tópico. Cada partição é atribuída a um único consumidor, garantindo que cada mensagem seja processada apenas uma vez.

### Rebalance
O Rebalance é o processo de redistribuição das partições de um tópico entre os consumidores de um consumer group. Ele acontece quando um consumidor entra ou sai do grupo, quando um consumidor fica ocioso por muito tempo ou quando novas partições são adicionadas a um tópico. O objetivo é garantir que o workload esteja balanceado entre os consumidores, mas esse processo nem sempre é desejável.

#### Por Que Evitar Rebalance?
O rebalance pode ser um grande vilão quando ocorre com frequência. Alguns dos motivos:

* **Aumenta a latência**: Em clusters grandes, esse processo pode ser lento, demorando de alguns minutos a até horas, dependendo do caso.
* **Causa queda na vazão (throughput)**: Como os consumidores param de processar enquanto redistribuem as partições, a performance geral cai.
* **Consome recursos**: O rebalance exige mais CPU, memória e rede, impactando o desempenho geral da aplicação.
* **Risco de duplicação ou perda de dados**: Durante o rebalance, podem ocorrer reprocessamentos de mensagens ou, no pior cenário, até perda de dados.

#### Como Evitar Rebalance?
Algumas boas práticas para minimizar o rebalance:

* **Evite auto-scaling desenfreado**: Adicionar ou remover instâncias de consumidores automaticamente pode causar rebalances constantes. Se precisar escalar, faça isso em horários de baixa carga.
* **Cuidado ao adicionar partições**: Evitar mexer em tópicos existentes ao adicionar novas partições também ajuda a prevenir rebalances.
* **Reduza o tempo de processamento por mensagem**: Consumidores que demoram muito para processar mensagens podem ficar ociosos e desencadear rebalances.
* **Escolha a estratégia de particionamento correta**: Escolher uma boa estratégia de particionamento pode diminuir o impacto de rebalances inevitáveis.

##### Estratégias de Particionamento
Quando um consumidor entra ou sai de um consumer group, o Kafka precisa redistribuir as partições entre os consumidores restantes. Esse processo causa o chamado *"stop-the-world"*, onde todos os consumidores do grupo param temporariamente de processar mensagens até que a redistribuição esteja completa. Isso acontece porque o Kafka precisa garantir que cada partição seja atribuída a apenas um consumidor dentro do grupo, evitando conflitos de processamento simultâneo.

Embora este fenômeno seja inevitável em muitos casos, podemos minimizar seu impacto ao escolher a estratégia de particionamento adequada. Vejamos algumas fazer isso:

* **Range**: Consumidores e partições são ordenados lexicograficamente (C1, C2, C3 e partições A0, B0, A1, B1). Cada consumidor recebe um conjunto contínuo de partições. Isso é útil quando você quer que determinados dados fiquem no mesmo consumidor (colocation).

* **Round Robin**: As partições são distribuídas de maneira circular entre os consumidores, balanceando a carga igualmente. No entanto, essa abordagem não minimiza o número de movimentos durante o rebalance.

* **Sticky**: Funciona como o Round Robin, mas quando um consumidor sai do grupo, apenas suas partições são redistribuídas, o que reduz o número de movimentos no rebalance, ajudando a manter a estabilidade.

##### Static Group Membership
Uma forma eficiente de reduzir (ou até evitar) rebalances é usar o conceito de **Static Group Membership**. Com essa técnica, cada consumidor tem um ID estático (`group.instance.id`), e as partições são "fixadas" a ele.

Quando o consumidor entra ou sai do grupo, suas partições continuam com ele, evitando o rebalance completo. Contudo, se o consumidor permanecer inativo além do tempo limite de sessão (`session timeout`, por padrão 5 minutos), o rebalance pode ocorrer.

## Schema Registry
No contexto do Apacha Kafka, o **Schema Registry** é um componente externo que gerencia e valida os schemas (estruturas de dados) usados na produção e consumo de mensagens.

É geralmente usado com mensagens serializadas em formatos como **Avro**, **Protobuf** ou **JSON**, que exigem um esquema para definir a estrutura dos dados (como os campos e seus tipos). O **Confluent Schema Registry** é uma implementação bastante comum, integrada ao Kafka.

### Vantagens
**Validação de Dados**: O Schema Registry garante que os dados enviados e recebidos sigam um formato específico (o schema), evitando erros ao consumir dados inválidos ou malformados.

**Evolução de Esquemas**: O Schema Registry permite que os schemas evoluam sem quebrar os consumidores existentes. Ele gerencia versões dos schemas e impõe regras de compatibilidade, como:
* **Backward Compatibility**: O novo schema é compatível com os dados produzidos pelo schema antigo.
* **Forward Compatibility**: Os consumidores antigos podem processar dados produzidos com o novo schema.
* **Full Compatibility**: Tanto os produtores quanto os consumidores podem ler dados, independentemente de usarem o schema antigo ou novo.

**Eficiência**: Ao usar o Schema Registry, o schema não precisa ser enviado junto com cada mensagem. Apenas o ID do schema é anexado à mensagem, economizando espaço.

### Schema Registry na Prática

* **Produção da mensagem**
  * O produtor serializa os dados de acordo com um schema (definido em Avro, Protobuf ou JSON).
  * O schema é registrado no Schema Registry, se ainda não estiver registrado, e um ID único é atribuído a ele.
  * O ID do schema é anexado à mensagem, que é então enviada ao Kafka.

* **Consumo da mensagem**
  * O consumidor lê a mensagem do Kafka.
  * Extrai o ID do schema da mensagem e consulta o Schema Registry para obter o schema associado a esse ID.
  * O consumidor desserializa a mensagem usando o schema recuperado e processa os dados.